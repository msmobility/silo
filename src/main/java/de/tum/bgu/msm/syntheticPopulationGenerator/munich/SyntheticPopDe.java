package de.tum.bgu.msm.syntheticPopulationGenerator.munich;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.CreateCarOwnershipModel;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation.Allocation;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.optimization.Optimization;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.Preparation;
import omx.OmxFile;
import omx.OmxLookup;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.commons.math.stat.Frequency;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;


/**
 * Generates a simple synthetic population for a study area in Germany
 * @author Ana Moreno (TUM)
 * Created on May 12, 2016 in Munich
 *
 */
public class SyntheticPopDe implements SyntheticPopI {

    protected TableDataSet microDataHousehold;
    protected TableDataSet microDataPerson;
    protected TableDataSet microDataDwelling;
    protected TableDataSet frequencyMatrix;

    protected int[] cityID;
    protected int[] countyID;
    protected HashMap<Integer, ArrayList> municipalitiesByCounty;
    protected HashMap<Integer, int[]> cityTAZ;

    protected TableDataSet counterMunicipality;
    protected TableDataSet errorMunicipality;

    protected TableDataSet weightsTable;


    HashMap<String, Integer> jobIntTypes;

    protected HashMap<Integer, int[]> idVacantJobsByZoneType;
    protected HashMap<Integer, Integer> numberVacantJobsByType;
    protected HashMap<Integer, int[]> idZonesVacantJobsByType;
    protected HashMap<Integer, Integer> numberVacantJobsByZoneByType;
    protected HashMap<Integer, Integer> numberZonesByType;

    protected HashMap<Integer, Integer> numberVacantSchoolsByZoneByType;
    protected HashMap<Integer, int[]> idZonesVacantSchoolsByType;
    protected HashMap<Integer, Integer> numberZonesWithVacantSchoolsByType;
    protected HashMap<Integer, Integer> schoolCapacityByType;

    protected Matrix distanceMatrix;
    protected Matrix distanceImpedance;
    protected TableDataSet odMunicipalityFlow;
    protected TableDataSet odCountyFlow;

    public static final Logger logger = Logger.getLogger(SyntheticPopDe.class);
    private final DataSetSynPop dataSetSynPop;

    private ResourceBundle rb;

    public SyntheticPopDe(DataSetSynPop dataSetSynPop) {
        this.rb = rb;
        this.dataSetSynPop = dataSetSynPop;
    }


    public void runSP(){
        //method to create the synthetic population
        if (!PropertiesSynPop.get().main.runSyntheticPopulation) return;
        logger.info("   Starting to create the synthetic population.");
        createDirectoryForOutput();
        long startTime = System.nanoTime();

        logger.info("Running Module: Reading inputs");
        Preparation preparation = new Preparation(dataSetSynPop);
        preparation.run();

        logger.info("Running Module: Optimization IPU");
        Optimization optimization = new Optimization(dataSetSynPop);
        optimization.run();

        logger.info("Running Module: Allocation");
        Allocation allocation = new Allocation(dataSetSynPop);
        allocation.run();

        //generateHouseholdsPersonsDwellings(); //Monte Carlo selection process to generate the synthetic population. The synthetic dwellings will be obtained from the same microdata
        //generateJobs(); //Generate the jobs by type. Allocated to TAZ level
        assignJobs(); //Workplace allocation
        assignSchools(); //School allocation
        addCars(false);
        SummarizeData.writeOutSyntheticPopulationDE(SiloUtil.getBaseYear());

        long estimatedTime = System.nanoTime() - startTime;
        logger.info("   Finished creating the synthetic population. Elapsed time: " + estimatedTime);
    }


    private void createDirectoryForOutput() {
        // create output directories
        SiloUtil.createDirectoryIfNotExistingYet("microData");
        SiloUtil.createDirectoryIfNotExistingYet("microData/interimFiles");
    }


    private void generateJobs(){
        //Generate jobs file. The worker ID will be assigned later on the process "assignJobs"

        logger.info("   Starting to generate jobs");

        int[] rasterCellsIDs = PropertiesSynPop.get().main.cellsMatrix.getColumnAsInt("ID_cell");

        //For each municipality
        for (int municipality = 0; municipality < cityID.length; municipality++) {
            //logger.info("   Municipality " + cityID[municipality] + ". Starting to generate jobs.");

            //-----------***** Data preparation *****-------------------------------------------------------------------
            //Create local variables to avoid accessing to the same variable on the parallel processing
            int municipalityID = cityID[municipality];
            TableDataSet rasterCellsMatrix = PropertiesSynPop.get().main.cellsMatrix;
            rasterCellsMatrix.buildIndex(rasterCellsMatrix.getColumnPosition("ID_cell"));


            //obtain the raster cells of the municipality and their weight within the municipality
            int[] tazInCity = cityTAZ.get(municipalityID);


            //generate jobs
            for (String jobType : PropertiesSynPop.get().main.jobStringType) {
                int totalJobs = (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipalityID, jobType);
                if (totalJobs > 0.1) {
                    //Obtain the number of jobs of that type in each TAZ of the municipality
                    double[] jobsInTaz = new double[tazInCity.length];
                    for (int i = 0; i < tazInCity.length; i++) {
                        jobsInTaz[i] = rasterCellsMatrix.getIndexedValueAt(tazInCity[i], jobType);
                    }
                    //Create and allocate jobs to TAZs (with replacement)
                    for (int job = 0; job < totalJobs; job++) {
                        int[] records = select(jobsInTaz, tazInCity);
                        jobsInTaz[records[1]] = jobsInTaz[records[1]] - 1;
                        int id = JobDataManager.getNextJobId();
                        Job jj = new Job(id, records[0], -1, jobType); //(int id, int zone, int workerId, String type)
                    }
                }
            }
        }
    }


    private void assignJobs(){
        //Method to allocate workers at workplaces
        //todo. Things to consider:
        //If there are no more workplaces of the specific job type, the worker is sent outside the area (workplace = -2; distance = 1000 km)
        //Workers that also attend school are considered only as workers (educational place is not selected for them)

        //Calculate distance impedance
        distanceImpedance = new Matrix(distanceMatrix.getRowCount(), distanceMatrix.getColumnCount());
        for (int i = 1; i <= distanceMatrix.getRowCount(); i ++){
            for (int j = 1; j <= distanceMatrix.getColumnCount(); j++){
                distanceImpedance.setValueAt(i,j,(float) Math.exp(PropertiesSynPop.get().main.alphaJob *
                        Math.exp(distanceMatrix.getValueAt(i,j) * PropertiesSynPop.get().main.gammaJob)));
            }
        }


        //Identify vacant jobs and schools by zone and type
        identifyVacantJobsByZoneType();


        //For validation - obtain the trip length distribution
/*        Frequency commuteDistance = new Frequency();
        validationCommutersFlow(); //Generates the validation tabledatasets
        int[] flow = SiloUtil.createArrayWithValue(odMunicipalityFlow.getRowCount(),0);
        int[] flowR = SiloUtil.createArrayWithValue(odCountyFlow.getRowCount(),0);
        int count = 0;
        odMunicipalityFlow.appendColumn(flow,Integer.toString(count));
        odCountyFlow.appendColumn(flowR,Integer.toString(count));*/


        //Produce one array list with workers' ID
        Map<Integer, Person> personMap = Person.getPersonMap();
        ArrayList<Person> workerArrayList = new ArrayList<>();
        for (Map.Entry<Integer,Person> pair : personMap.entrySet() ){
            if (pair.getValue().getOccupation() == 1){
                workerArrayList.add(pair.getValue());
            }
        }
        //Randomize the order of the worker array list
        Collections.shuffle(workerArrayList);


        //Job type probabilities
        String[] jobStringTypes = PropertiesSynPop.get().main.jobStringType;

        //Start the selection of the jobs in random order to avoid geographical bias
        logger.info("   Started assigning workplaces");
        int assignedJobs = 0;
        for (Person pp : workerArrayList){

            //Select the zones with vacant jobs for that person, given the job type
            int selectedJobType = selectJobType(pp);

            int[] keys = idZonesVacantJobsByType.get(selectedJobType);
            int lengthKeys = numberZonesByType.get(selectedJobType);
            // if there are still TAZ with vacant jobs in the region, select one of them. If not, assign them outside the area
            if (lengthKeys > 0) {

                //Select the workplace location (TAZ) for that person given his/her job type
                int[] workplace = selectWorkplace(pp.getZone(), numberVacantJobsByZoneByType, keys, lengthKeys,
                        distanceImpedance);

                //Assign last vacant jobID from the TAZ
                int jobID = idVacantJobsByZoneType.get(workplace[0])[numberVacantJobsByZoneByType.get(workplace[0]) - 1];

                //Assign values to job and person
                Job.getJobFromId(jobID).setWorkerID(pp.getId());
                pp.setJobTAZ(Job.getJobFromId(jobID).getZone());
                pp.setWorkplace(jobID);
                //pp.setTravelTime(distanceMatrix.getValueAt(pp.getZone(), Job.getJobFromId(jobID).getZone()));

                //For validation OD TableDataSet
/*
                commuteDistance.addValue((int) distanceMatrix.getValueAt(pp.getZone(), Job.getJobFromId(jobID).getZone()));
                int homeMun = (int) cellsMatrix.getIndexedValueAt(pp.getZone(), "smallID");
                int workMun = (int) cellsMatrix.getIndexedValueAt(pp.getWorkplace(), "smallID");
                int odPair = homeMun * 1000 + workMun;
                odMunicipalityFlow.setIndexedValueAt(odPair,Integer.toString(count),odMunicipalityFlow.getIndexedValueAt(odPair,Integer.toString(count))+ 1);
                homeMun = (int) cellsMatrix.getIndexedValueAt(pp.getZone(), "smallCenter");
                workMun = (int) cellsMatrix.getIndexedValueAt(pp.getWorkplace(), "smallCenter");
                odPair = homeMun * 1000 + workMun;
                odCountyFlow.setIndexedValueAt(odPair,Integer.toString(count),odCountyFlow.getIndexedValueAt(odPair,Integer.toString(count))+ 1);
*/

                //Update counts of vacant jobs
                numberVacantJobsByZoneByType.put(workplace[0], numberVacantJobsByZoneByType.get(workplace[0]) - 1);
                numberVacantJobsByType.put(selectedJobType, numberVacantJobsByType.get(selectedJobType) - 1);
                if (numberVacantJobsByZoneByType.get(workplace[0]) < 1) {
                    keys[workplace[1]] = keys[numberZonesByType.get(selectedJobType) - 1];
                    idZonesVacantJobsByType.put(selectedJobType, keys);
                    numberZonesByType.put(selectedJobType, numberZonesByType.get(selectedJobType) - 1);
                    if (numberZonesByType.get(selectedJobType) < 1) {
                        int w = 0;
                        while (w < PropertiesSynPop.get().main.jobStringType.length & selectedJobType > jobIntTypes.get(jobStringTypes[w])) {
                            w++;
                        }
                        jobIntTypes.remove(jobStringTypes[w]);
                        jobStringTypes[w] = jobStringTypes[jobStringTypes.length - 1];
                        jobStringTypes = SiloUtil.removeOneElementFromZeroBasedArray(jobStringTypes, jobStringTypes.length - 1);

                    }
                }
                //logger.info("   Job " + assignedJobs + " assigned at " + workplace[0]);
                assignedJobs++;

            } else { //No more vacant jobs in the study area. This person will work outside the study area
                pp.setWorkplace(-2);
                //pp.setTravelTime(1000);
                logger.info("   No more jobs available of " + selectedJobType + " class. Person " + pp.getId() + " has workplace outside the study area.");
            }
        }


        //For validation - trip length distribution
        //checkTripLengthDistribution(commuteDistance, alphaJob, gammaJob, "microData/interimFiles/tripLengthDistributionWork.csv", 1); //Trip length frequency distribution
        //checkodMatrix(odMunicipalityFlow, alphaJob, gammaJob, count,"microData/interimFiles/odMunicipalityDifference.csv");
        //SiloUtil.writeTableDataSet(odMunicipalityFlow,"microData/interimFiles/odMunicipalityFlow.csv");
        //SiloUtil.writeTableDataSet(odCountyFlow,"microData/interimFiles/odRegionFlow.csv");
        //count++;

    }


        private void assignSchools(){
        //method to assign the school location for students. They should be registered on the microdata as students

        //todo. Things to consider:
        //The location of the school is stored under "schoolplace location"
        //Students from Berufschule are considered to be working full-time and therefore they don't attend class
        //If there are no more school places for the student, they are sent outside the area (schoolplace = -2)
        //For the following years, we school transition should be accomplished

            logger.info("   Started assigning schools");
        int count = 0;

        //Calculate distance impedance for students
        Matrix universityDistanceImpedance = new Matrix(distanceMatrix.getRowCount(), distanceMatrix.getColumnCount());
        Matrix schoolDistanceImpedance = new Matrix(distanceMatrix.getRowCount(), distanceMatrix.getColumnCount());
        for (int i = 1; i <= distanceMatrix.getRowCount(); i++) {
            for (int j = 1; j <= distanceMatrix.getColumnCount(); j++) {
                universityDistanceImpedance.setValueAt(i, j, (float) Math.exp(PropertiesSynPop.get().main.alphaUniversity *
                        Math.exp(distanceMatrix.getValueAt(i, j) * PropertiesSynPop.get().main.gammaUniversity)));
                schoolDistanceImpedance.setValueAt(i, j, distanceMatrix.getValueAt(i, j));
            }
        }


        //Identify vacant schools by zone and type
        identifyVacantSchoolsByZoneByType();


        //For validation - obtain the trip length distribution
        Frequency travelSecondary = new Frequency();
        Frequency travelUniversity = new Frequency();
        Frequency travelPrimary = new Frequency();
        //validationCommutersFlow(); //Generates the validation tabledatasets
/*        int[] flow = SiloUtil.createArrayWithValue(odMunicipalityFlow.getRowCount(),0);
        odMunicipalityFlow.appendColumn(flow,Integer.toString(count));*/


        //Produce one array list with students' ID
        Map<Integer, Person> personMap = Person.getPersonMap();
        ArrayList<Person> studentArrayList = new ArrayList<>();
        int[] studentsByType2 = new int[PropertiesSynPop.get().main.schoolTypes.length];
        for (Map.Entry<Integer, Person> pair : personMap.entrySet()) {
            int school = pair.getValue().getSchoolType();
            if (school > 0) { //They are studying
                studentArrayList.add(pair.getValue());
                studentsByType2[school - 1] = studentsByType2[school - 1] + 1;
            }
        }
        //Randomize the order of the students
        Collections.shuffle(studentArrayList);


        //Start the selection of schools in random order to avoid geographical bias
        logger.info("   Started assigning schools");
        int assignedSchools = 0;
        int[] studentsOutside = new int[PropertiesSynPop.get().main.schoolTypes.length];
        int[] studentsByType = new int[PropertiesSynPop.get().main.schoolTypes.length];
        for (Person pp : studentArrayList) {

            //Select the zones with vacant schools for that person, given the school type
            int schoolType = pp.getSchoolType();
            studentsByType[schoolType - 1] = studentsByType[schoolType - 1] + 1;
            int[] keys = idZonesVacantSchoolsByType.get(schoolType);
            int lengthKeys = numberZonesWithVacantSchoolsByType.get(schoolType);
            if (lengthKeys > 0) {//if there are still TAZ with school capacity in the region, select one of them. If not, assign them outside the area

                //Select the school location (which raster cell) for that person given his/her job type
                int[] schoolPlace = new int[2];
                if (schoolType == 3) {
                    schoolPlace = selectWorkplace(pp.getZone(), numberVacantSchoolsByZoneByType,
                            keys, lengthKeys, universityDistanceImpedance);
                    travelUniversity.addValue((int) distanceMatrix.getValueAt(pp.getZone(), schoolPlace[0] / 100));
                } else {
                    schoolPlace = selectClosestSchool(pp.getZone(), numberVacantSchoolsByZoneByType,
                            keys, lengthKeys, schoolDistanceImpedance);
                    if (schoolType == 1){
                        travelPrimary.addValue((int) distanceMatrix.getValueAt(pp.getZone(),schoolPlace[0] / 100));
                    } else if (schoolType == 2){
                        travelSecondary.addValue((int) distanceMatrix.getValueAt(pp.getZone(), schoolPlace[0] / 100));
                    }
                }

                //Assign values to job and person
                pp.setSchoolPlace(schoolPlace[0] / 100);
                //pp.setTravelTime(distanceMatrix.getValueAt(pp.getZone(), pp.getSchoolPlace()));

/*                //For validation OD TableDataSet
                int homeMun = (int) PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(pp.getZone(), "smallID");
                int workMun = (int) PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(pp.getSchoolPlace(), "smallID");
                int odPair = homeMun * 1000 + workMun;
                odMunicipalityFlow.setIndexedValueAt(odPair,Integer.toString(count),odMunicipalityFlow.getIndexedValueAt(odPair,Integer.toString(count))+ 1);*/

                //Update counts of vacant school places
                numberVacantSchoolsByZoneByType.put(schoolPlace[0], numberVacantSchoolsByZoneByType.get(schoolPlace[0]) - 1);
                if (numberVacantSchoolsByZoneByType.get(schoolPlace[0]) < 1) {
                    numberVacantSchoolsByZoneByType.put(schoolPlace[0], 0);
                    keys[schoolPlace[1]] = keys[numberZonesWithVacantSchoolsByType.get(schoolType) - 1];
                    idZonesVacantSchoolsByType.put(schoolType, keys);
                    numberZonesWithVacantSchoolsByType.put(schoolType, numberZonesWithVacantSchoolsByType.get(schoolType) - 1);
                    if (numberZonesWithVacantSchoolsByType.get(schoolType) < 1) {
                        numberZonesWithVacantSchoolsByType.put(schoolType, 0);
                    }
                }
                assignedSchools++;
            } else {//No more school capacity in the study area. This person will study outside the area
                pp.setSchoolPlace(-2); //they attend one school out of the area
                studentsOutside[schoolType - 1] = studentsOutside[schoolType - 1] + 1;
            }
        }


        //For validation - trip length distribution
        /*checkTripLengthDistribution(travelPrimary, 0, 0, "microData/interimFiles/tripLengthDistributionPrimary.csv", 1);
        checkTripLengthDistribution(travelSecondary, 0, 0, "microData/interimFiles/tripLengthDistributionSecondary.csv", 1); //Trip length frequency distribution
        checkTripLengthDistribution(travelUniversity, PropertiesSynPop.get().main.alphaJob, PropertiesSynPop.get().main.gammaJob, "microData/interimFiles/tripLengthDistributionUniversity.csv", 1);
        SiloUtil.writeTableDataSet(odMunicipalityFlow,"microData/interimFiles/odMunicipalityFlow.csv");*/
        for (int schoolType : PropertiesSynPop.get().main.schoolTypes) {
            logger.info("  School type: " + schoolType + ". " + studentsOutside[schoolType - 1] + " students out of " + studentsByType[schoolType - 1] + " study outside the area");
        }
    }


    private void readSyntheticPopulation(){
        //Read the synthetic population

        logger.info("   Starting to read the synthetic population");
        String fileEnding = "_" + SiloUtil.getBaseYear() + ".csv";
        TableDataSet households = SiloUtil.readCSVfile2(PropertiesSynPop.get().main.householdsFileName + fileEnding);
        TableDataSet persons = SiloUtil.readCSVfile2(PropertiesSynPop.get().main.personsFileName + fileEnding);
        TableDataSet dwellings = SiloUtil.readCSVfile2(PropertiesSynPop.get().main.dwellingsFileName + fileEnding);
        TableDataSet jobs = SiloUtil.readCSVfile2(PropertiesSynPop.get().main.jobsFileName + fileEnding);
        logger.info("   Read input data");

        TableDataSet prices = SiloUtil.readCSVfile2("microData/interimFiles/zoneAttributes_landPrice.csv");
        prices.buildIndex(prices.getColumnPosition("ID_cell"));
        //Generate the households, dwellings and persons
        logger.info("   Starting to generate households");
        for (int i = 1; i <= households.getRowCount(); i++) {
            Household hh = new Household((int) households.getValueAt(i, "id"), (int) households.getValueAt(i, "dwelling"),
                    (int) households.getValueAt(i, "zone"), (int) households.getValueAt(i, "hhSize"),
                    (int) households.getValueAt(i, "autos"));
        }

        logger.info("   Starting to generate persons");
        for (int i = 1; i <= persons.getRowCount(); i++) {
            Race race = Race.white;
            if ((int) persons.getValueAt(i,"nationality") > 1){race = Race.black;}
            int hhID = (int) persons.getValueAt(i, "hhid");
            Person pp = new Person((int) persons.getValueAt(i, "id"),hhID ,
                    (int) persons.getValueAt(i, "age"), (int) persons.getValueAt(i, "gender"),
                    race, (int) persons.getValueAt(i, "occupation"), (int) persons.getValueAt(i, "workplace"),
                    (int) persons.getValueAt(i, "income"));
            Household.getHouseholdFromId(hhID).addPersonForInitialSetup(pp);
            pp.setEducationLevel((int) persons.getValueAt(i, "education"));
            if (persons.getStringValueAt(i, "relationShip").equals("single")) pp.setRole(PersonRole.single);
            else if (persons.getStringValueAt(i, "relationShip").equals("married")) pp.setRole(PersonRole.married);
            else pp.setRole(PersonRole.child);
            if (persons.getValueAt(i,"driversLicense") == 1) pp.setDriverLicense(true);
            int nationality = (int) persons.getValueAt(i,"nationality");
            if (nationality == 1) {
                pp.setNationality(Nationality.german);
            } else {
                pp.setNationality(Nationality.other);
            }
            pp.setHhSize(Household.getHouseholdFromId(hhID).getHhSize());
            pp.setZone(Household.getHouseholdFromId(hhID).getHomeZone());
            pp.setSchoolType((int) persons.getValueAt(i,"schoolDE"));
            pp.setWorkplace((int) persons.getValueAt(i,"workplace"));
        }

        logger.info("   Starting to generate dwellings");
        for (int i = 1; i <= dwellings.getRowCount(); i++){
            int buildingSize = (int) dwellings.getValueAt(i,"building");
            int zone = (int) dwellings.getValueAt(i,"zone");
            int municipality = (int) PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(zone,"ID_city");
/*            float ddType1Prob = marginalsMunicipality.getIndexedValueAt(municipality, "dwelling12");
            float ddType3Prob = marginalsMunicipality.getIndexedValueAt(municipality, "dwelling37");
            DwellingType type = guessDwellingType(buildingSize, ddType1Prob, ddType3Prob);*/
            String ddtype = dwellings.getStringValueAt(i,"type");
            DwellingType type = guessDwellingType(ddtype);
            int size = (int) dwellings.getValueAt(i,"floor");
            int bedrooms = guessBedrooms(size);
            int quality = (int)dwellings.getValueAt(i,"quality");
            float brw = prices.getIndexedValueAt(zone, ddtype);
            float price = guessPrice(brw, quality, size);
            Dwelling dd = new Dwelling((int)dwellings.getValueAt(i,"id"),(int)dwellings.getValueAt(i,"zone"),
                    (int)dwellings.getValueAt(i,"hhID"),type,bedrooms,
                    (int)dwellings.getValueAt(i,"quality"),(int) price,
                    (int)dwellings.getValueAt(i,"restriction"),(int)dwellings.getValueAt(i,"yearBuilt"));
            dd.setFloorSpace((int)dwellings.getValueAt(i,"floor"));
            dd.setBuildingSize((int)dwellings.getValueAt(i,"building"));
            dd.setYearConstructionDE((int)dwellings.getValueAt(i,"year"));
            dd.setUsage((int)dwellings.getValueAt(i,"usage"));
        }
        logger.info("   Generated households, persons and dwellings");


        //Generate the jobs
        //Starting to generate jobs
        logger.info("   Starting to generate jobs");
        for (int i = 1; i <= jobs.getRowCount(); i++) {
            Job jj = new Job((int) jobs.getValueAt(i, "id"), (int) jobs.getValueAt(i, "zone"),
                    (int) jobs.getValueAt(i, "personId"), jobs.getStringValueAt(i, "type"));
        }
        logger.info("   Generated jobs");
    }

    private float guessPrice(float brw, int quality, int size) {

        float coef = 1;
        if (quality == 1){
            coef = 0.7f;
        } else if (quality == 2){
            coef = 0.9f;
        } else if (quality == 4){
            coef = 1.1f;
        }
        float convertToMonth = 0.0057f;
        return brw * size * coef * convertToMonth + 150;
    }

    private int guessBedrooms(int size) {
        int bedrooms = 0;
        if (size < 40){
            bedrooms = 0;
        } else if (size < 60){
            bedrooms = 1;
        } else if (size < 80){
            bedrooms = 2;
        } else if (size < 100){
            bedrooms = 3;
        } else if (size < 120){
            bedrooms = 4;
        } else {
            bedrooms = 5;
        }

        return bedrooms;
    }


    private void validationCommutersFlow(){

        //For checking
        //OD matrix from the commuters data, for validation


        int[] allCounties = PropertiesSynPop.get().main.selectedMunicipalities.getColumnAsInt("smallCenter");
        TableDataSet observedODFlow = SiloUtil.readCSVfile("input/syntheticPopulation/odMatrixCommuters.csv");
        observedODFlow.buildIndex(observedODFlow.getColumnPosition("ID_city"));
        //OD matrix for the core cities, obtained from the commuters data
        TableDataSet observedCoreODFlow = new TableDataSet();
        int [] selectedCounties = SiloUtil.idendifyUniqueValues(allCounties);
        observedCoreODFlow.appendColumn(selectedCounties,"smallCenter");
        for (int i = 0; i < selectedCounties.length; i++){
            int[] dummy = SiloUtil.createArrayWithValue(selectedCounties.length,0);
            observedCoreODFlow.appendColumn(dummy,Integer.toString(selectedCounties[i]));
        }
        observedCoreODFlow.buildIndex(observedCoreODFlow.getColumnPosition("smallCenter"));
        int ini = 0;
        int end = 0;
        // We decided to read this file here again, as this method is likely to be removed later, which is why we did not
        // want to create a global variable for TableDataSet selectedMunicipalities (Ana and Rolf, 29 Mar 2017)

        int[] citySmallID = PropertiesSynPop.get().main.selectedMunicipalities.getColumnAsInt("smallID");
        for (int i = 0; i < cityID.length; i++){
            ini = (int) PropertiesSynPop.get().main.selectedMunicipalities.getIndexedValueAt(cityID[i],"smallCenter");
            for (int j = 0; j < cityID.length; j++){
                end = (int) PropertiesSynPop.get().main.selectedMunicipalities.getIndexedValueAt(cityID[j],"smallCenter");
                observedCoreODFlow.setIndexedValueAt(ini,Integer.toString(end),
                        observedCoreODFlow.getIndexedValueAt(ini,Integer.toString(end)) + observedODFlow.getIndexedValueAt(cityID[i],Integer.toString(cityID[j])));
            }
        }
        //OD flows at the municipality level in one TableDataSet, to facilitate visualization of the deviation between the observed data and the estimated data
        odMunicipalityFlow = new TableDataSet();
        int[] cityKeys = new int[citySmallID.length * citySmallID.length];
        int[] odData = new int[citySmallID.length * citySmallID.length];
        int k = 0;
        for (int row = 0; row < citySmallID.length; row++){
            for (int col = 0; col < citySmallID.length; col++){
                cityKeys[k] = citySmallID[row] * 1000 + citySmallID[col];
                odData[k] = (int) observedODFlow.getIndexedValueAt(cityID[row],Integer.toString(cityID[col]));
                k++;
            }
        }
        int[] initial = SiloUtil.createArrayWithValue(cityKeys.length, 0);
        odMunicipalityFlow.appendColumn(cityKeys,"ID_od");
        odMunicipalityFlow.appendColumn(odData,"ObservedFlow");
        odMunicipalityFlow.appendColumn(initial,"SimulatedFlow");
        odMunicipalityFlow.buildIndex(odMunicipalityFlow.getColumnPosition("ID_od"));

        //OD flows at the regional level (5 core cities)
        odCountyFlow = new TableDataSet();
        int[] regionKeys = new int[selectedCounties.length * selectedCounties.length];
        int[] regionalFlows = new int[selectedCounties.length * selectedCounties.length];
        k = 0;
        for (int row = 0; row < selectedCounties.length; row++){
            for (int col = 0; col < selectedCounties.length; col++){
                regionKeys[k] = selectedCounties[row] * 1000 + selectedCounties[col];
                regionalFlows[k] = (int) observedCoreODFlow.getIndexedValueAt(selectedCounties[row],Integer.toString(selectedCounties[col]));
                k++;
            }
        }
        int[] initialFlow = SiloUtil.createArrayWithValue(regionKeys.length, 0);
        odCountyFlow.appendColumn(regionKeys,"ID_od");
        odCountyFlow.appendColumn(regionalFlows,"ObservedFlow");
        odCountyFlow.appendColumn(initialFlow,"SimulatedFlow");
        odCountyFlow.buildIndex(odCountyFlow.getColumnPosition("ID_od"));
    }


    private void generateHouseholdsPersonsDwellings(){
        //Generate the synthetic population using Monte Carlo (select the households according to the weight)
        //Once the household is selected, all the characteristics of the household will be copied (including the household members)
        logger.info("   Starting to generate households and persons.");


        //List of households of the micro data
        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        int previousHouseholds = 0;
        int previousPersons = 0;


        //Define income distribution

        GammaDistributionImpl gammaDist = new GammaDistributionImpl(PropertiesSynPop.get().main.incomeShape,
                1/PropertiesSynPop.get().main.incomeRate);


        //Driver license probability
        TableDataSet probabilityDriverLicense = SiloUtil.readCSVfile("input/syntheticPopulation/driverLicenseProb.csv");

        generateCountersForValidation();

        //Create hashmaps to store quality of occupied dwellings
        HashMap<Integer, int[]> ddQuality = new HashMap<>();
        for (int municipality = 0; municipality < cityID.length; municipality++){
            for (int year : PropertiesSynPop.get().main.yearBracketsDwelling){
                int[] probability = SiloUtil.createArrayWithValue(PropertiesSynPop.get().main.numberofQualityLevels, 0);
                int key = year * 1000 + cityID[municipality];
                ddQuality.put(key, probability);
            }
        }


        //Selection of households, persons, jobs and dwellings per municipality
        for (int municipality = 0; municipality < cityID.length; municipality++){
            logger.info("   Municipality " + cityID[municipality] + ". Starting to generate households.");

            //-----------***** Data preparation *****-------------------------------------------------------------------
            //Create local variables to avoid accessing to the same variable on the parallel processing
            int municipalityID = cityID[municipality];
            String[] attributesHouseholdIPU = PropertiesSynPop.get().main.attributesMunicipality;
            TableDataSet rasterCellsMatrix = PropertiesSynPop.get().main.cellsMatrix;
            TableDataSet microHouseholds = microDataHousehold;
            TableDataSet microPersons = microDataPerson;
            TableDataSet microDwellings = microDataDwelling;
            microHouseholds.buildIndex(microHouseholds.getColumnPosition("ID"));
            microDwellings.buildIndex(microDwellings.getColumnPosition("dwellingID"));
            int totalHouseholds = (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipalityID,"hhTotal");
            int totalQuarters = (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipalityID,"privateQuarters");
            double[] probability = dataSetSynPop.getWeights().getColumnAsDouble(Integer.toString(municipalityID));
            int[] agePerson = PropertiesSynPop.get().main.ageBracketsPerson;
            int[] sizeBuilding = PropertiesSynPop.get().main.sizeBracketsDwelling;
            int[] yearBuilding = PropertiesSynPop.get().main.yearBracketsDwelling;
            float ddType1Prob = PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipalityID,"ddProbSFAorSFD");
            float ddType3Prob = PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipalityID,"ddProbMF234orMF5plus");


            //obtain the raster cells of the municipality and their weight within the municipality
            int[] tazInCity = cityTAZ.get(municipalityID);
            double[] probTaz = new double[tazInCity.length];
            double tazRemaining = 0;
            for (int i = 0; i < tazInCity.length; i++){
                probTaz[i] = rasterCellsMatrix.getIndexedValueAt(tazInCity[i],"Population");
                tazRemaining = tazRemaining + probTaz[i];
            }


            double hhRemaining = 0;
            //logger.info("   " + probability[0]);
            double[] probabilityPrivate = new double[probability.length]; // Separate private households and group quarters for generation
            for (int row = 0; row < probability.length; row++){
                //if ((int) microHouseholds.getValueAt(row + 1,"groupQuarters") == 0){
                    probabilityPrivate[row] = probability[row];
                    hhRemaining = hhRemaining + probability[row];
                //}
            }

            //marginals for the municipality
            int hhPersons = 0;
            int hhTotal = 0;
            int quartersTotal = 0;
            int id = 0;


            //for all the households that are inside the municipality (we will match perfectly the number of households. The total population will vary compared to the marginals.)
            for (int row = 0; row < totalHouseholds; row++) {

                //select the household to copy from the micro data(with replacement)
                int[] records = select(probabilityPrivate, microDataIds, hhRemaining);
                int hhIdMD = records[0];
                int hhRowMD = records[1];
                if (probabilityPrivate[hhRowMD] > 1.0) {
                    probabilityPrivate[hhRowMD] = probabilityPrivate[hhRowMD] - 1;
                    hhRemaining = hhRemaining - 1;
                } else {
                    hhRemaining = hhRemaining - probabilityPrivate[hhRowMD];
                    probabilityPrivate[hhRowMD] = 0;
                }


                //Select the taz to allocate the household (without replacement)
                int[] recordsCell = select(probTaz, tazInCity, tazRemaining);
                int tazID = recordsCell[0];


                //copy the private household characteristics
                int householdSize = (int) microHouseholds.getIndexedValueAt(hhIdMD, "hhSize");
                id = HouseholdDataManager.getNextHouseholdId();
                int newDdId = RealEstateDataManager.getNextDwellingId();
                Household household = new Household(id, newDdId, tazID, householdSize, 0); //(int id, int dwellingID, int homeZone, int hhSize, int autos)
                hhTotal++;
                counterMunicipality = updateCountersHousehold(household, counterMunicipality, municipalityID);


                //copy the household members characteristics
                int[] roleCounter = new int[3];
                for (int rowPerson = 0; rowPerson < householdSize; rowPerson++) {
                    int idPerson = HouseholdDataManager.getNextPersonId();
                    int personCounter = (int) microHouseholds.getIndexedValueAt(hhIdMD, "personCount") + rowPerson;
                    int age = (int) microPersons.getValueAt(personCounter, "age");
                    int gender = (int) microPersons.getValueAt(personCounter, "gender");
                    int occupation = (int) microPersons.getValueAt(personCounter, "occupation");
                    int income = (int) microPersons.getValueAt(personCounter, "income");
                    try {
                        income = (int) translateIncome((int) microPersons.getValueAt(personCounter, "income"),PropertiesSynPop.get().main.incomeProbability, gammaDist)
                                * 12;  //convert monthly income to yearly income
                    } catch (MathException e) {
                        e.printStackTrace();
                    }
                    Person pers = new Person(idPerson, id, age, gender, Race.white, occupation, 0, income); //(int id, int hhid, int age, int gender, Race race, int occupation, int workplace, int income)
                    household.addPersonForInitialSetup(pers);
                    pers.setEducationLevel((int) microPersons.getValueAt(personCounter, "educationLevel"));
                    PersonRole role = PersonRole.single; //default value = single
                    if (microPersons.getValueAt(personCounter, "personRole") == 2) { //is married
                        role = PersonRole.married;
                    } else if (microPersons.getValueAt(personCounter, "personRole") == 3) { //is children
                        role = PersonRole.child;
                    }
                    pers.setRole(role);
                    int nationality = (int) microPersons.getValueAt(personCounter,"nationality");
                    if (nationality == 1) {
                        pers.setNationality(Nationality.german);
                    } else {
                        pers.setNationality(Nationality.other);
                    }
                    pers.setTelework((int) microPersons.getValueAt(personCounter, "telework"));
                    //int selectedJobType = ec.selectJobType(pers, probabilitiesJob, jobTypes);
                    //pers.setJobTypeDE(selectedJobType);
                    pers.setDriverLicense(obtainDriverLicense(pers.getGender(), pers.getAge(),probabilityDriverLicense));
                    pers.setSchoolType((int) microPersons.getValueAt(personCounter, "schoolType"));
                    pers.setZone(household.getHomeZone());
                    hhPersons++;
                    counterMunicipality = updateCountersPerson(pers, counterMunicipality, municipalityID,agePerson);
                }


                //Copy the dwelling of that household
                int bedRooms = 1; //Not on the micro data
                int price = Math.max((int) microDwellings.getIndexedValueAt(hhIdMD, "dwellingRentPrice"), 0); //Copied from micro data
                int year = (int) microDwellings.getIndexedValueAt(hhIdMD, "dwellingYear"); //Not by year. In the data is going to be in classes
                int floorSpace = (int) microDwellings.getIndexedValueAt(hhIdMD, "dwellingFloorSpace");
                int usage = (int) microDwellings.getIndexedValueAt(hhIdMD, "dwellingUsage");
                int buildingSize = (int) microDwellings.getIndexedValueAt(hhIdMD, "dwellingType");
                int heatingType = (int) microDwellings.getIndexedValueAt(hhIdMD, "dwellingHeatingType");
                int heatingEnergy = (int) microDwellings.getIndexedValueAt(hhIdMD, "dwellingHeatingEnergy");
                int heatingAdditional = (int) microDwellings.getIndexedValueAt(hhIdMD, "dwellingAdHeating");
                int quality = guessQualityDE(heatingType, heatingEnergy, heatingAdditional, year, PropertiesSynPop.get().main.numberofQualityLevels); //depend on year built and type of heating
                DwellingType type = guessDwellingType(buildingSize, ddType1Prob, ddType3Prob);
                int yearVacant = 0;
                while (year > PropertiesSynPop.get().main.yearBracketsDwelling[yearVacant]) {yearVacant++;}
                int key = municipalityID + PropertiesSynPop.get().main.yearBracketsDwelling[yearVacant] * 1000;
                int[] qualityCounts = ddQuality.get(key);
                qualityCounts[quality - 1]++;
                ddQuality.put(key, qualityCounts);
                year = selectDwellingYear(year); //convert from year class to actual 4-digit year
                Dwelling dwell = new Dwelling(newDdId, tazID, id, type , bedRooms, quality, price, 0, year); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year
                dwell.setFloorSpace(floorSpace);
                dwell.setUsage(usage);
                dwell.setBuildingSize(buildingSize);
                counterMunicipality = updateCountersDwelling(dwell,counterMunicipality,municipalityID,yearBuilding,sizeBuilding);
            }
            int households = HouseholdDataManager.getHighestHouseholdIdInUse() - previousHouseholds;
            int persons = HouseholdDataManager.getHighestPersonIdInUse() - previousPersons;
            previousHouseholds = HouseholdDataManager.getHighestHouseholdIdInUse();
            previousPersons = HouseholdDataManager.getHighestPersonIdInUse();


            //Calculate the errors from the synthesized population at the attributes of the IPU.
            //Update the tables for all municipalities with the result of this municipality

            //Consider if I need to add also the errors from other attributes. They must be at the marginals file, or one extra file
            //For county level they should be calculated on a next step, outside this loop.
            float averageError = 0f;
            for (int attribute = 0; attribute < attributesHouseholdIPU.length; attribute++){
                float error = Math.abs((counterMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]) -
                        PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute])) /
                        PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]));
                errorMunicipality.setIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute],error);
                averageError = averageError + error;
                //counterSynPop.setIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute],counterMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]));
                //relativeErrorSynPop.setIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute],errorMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]));
            }
            averageError = averageError / (1 + attributesHouseholdIPU.length) * 100;


            logger.info("   Municipality " + municipalityID + ". Generated " + hhPersons + " persons in " + hhTotal + " households. The error is " + averageError + " %.");
            //SiloUtil.writeTableDataSet(counterMunicipality,"microData/interimFiles/counterMun.csv");
            //SiloUtil.writeTableDataSet(errorMunicipality,"microData/interimFiles/errorMun.csv");
        }
        int households = HouseholdDataManager.getHighestHouseholdIdInUse();
        int persons = HouseholdDataManager.getHighestPersonIdInUse();
        logger.info("   Finished generating households and persons. A population of " + persons + " persons in " + households + " households was generated.");


        //Vacant dwellings--------------------------------------------
        //They have similar characteristics to the dwellings that are occupied (assume that there is no difference between the occupied and vacant dwellings in terms of quality)
        int vacantCounter = 0;
        for (int municipality = 0; municipality < cityID.length; municipality++) {

            logger.info("   Municipality " + cityID[municipality] + ". Starting to generate vacant dwellings.");
            int municipalityID = cityID[municipality];
            int vacantDwellings = (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(cityID[municipality], "totalDwellingsVacant");
            TableDataSet rasterCellsMatrix = PropertiesSynPop.get().main.cellsMatrix;

            //obtain the raster cells of the municipality and their weight within the municipality
            int[] tazInCity = cityTAZ.get(municipalityID);
            double[] probTaz = new double[tazInCity.length];
            double sumProbTaz = 0;
            for (int i = 0; i < tazInCity.length; i++){
                probTaz[i] = rasterCellsMatrix.getIndexedValueAt(tazInCity[i],"Population");
                sumProbTaz = sumProbTaz + probTaz[i];
            }
            int rasterCount = 0;
            for (int row = 1; row <= rasterCellsMatrix.getRowCount(); row++) {
                if ((int) rasterCellsMatrix.getValueAt(row, "ID_city") == municipalityID) {
                    rasterCount++;
                }
            }


            //Probability of floor size for vacant dwellings
            float [] vacantFloor = new float[PropertiesSynPop.get().main.sizeBracketsDwelling.length];
            for (int row = 0; row < PropertiesSynPop.get().main.sizeBracketsDwelling.length; row++){
                String name = "vacantDwellings" + PropertiesSynPop.get().main.sizeBracketsDwelling[row];
                vacantFloor[row] = PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipalityID,name)/vacantDwellings;
            }

            //Probability for year and building size for vacant dwellings
            float[] vacantSize = new float[PropertiesSynPop.get().main.yearBracketsDwelling.length * 2];
            for (int row = 0; row < PropertiesSynPop.get().main.yearBracketsDwelling.length; row++){
                String name = "vacantSmallDwellings" + PropertiesSynPop.get().main.yearBracketsDwelling[row];
                String name1 = "vacantMediumDwellings" + PropertiesSynPop.get().main.yearBracketsDwelling[row];
                vacantSize[row] = PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipalityID,name) / vacantDwellings;
                vacantSize[row + PropertiesSynPop.get().main.yearBracketsDwelling.length] = PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipalityID,name1) / vacantDwellings;
            }

            //Select the vacant dwelling and copy characteristics
            for (int row = 0; row < vacantDwellings; row++) {

                //Allocation
                int ddCell[] = select(probTaz, tazInCity, sumProbTaz); // I allocate vacant dwellings using the same proportion as occupied dwellings.

                //Copy characteristics
                int newDdId = RealEstateDataManager.getNextDwellingId();
                int bedRooms = 1; //Not on the micro data
                int price = 0; //Monte Carlo
                int[] buildingSizeAndYearBuilt = selectBuildingSizeYear(vacantSize, PropertiesSynPop.get().main.yearBracketsDwelling);
                int key = municipalityID + buildingSizeAndYearBuilt[1] * 1000;
                int quality = select(ddQuality.get(key)) + 1; //Based on the distribution of qualities at the municipality for that construction period
                int year = selectVacantDwellingYear(buildingSizeAndYearBuilt[1]);
                int floorSpaceDwelling = selectFloorSpace(vacantFloor, PropertiesSynPop.get().main.sizeBracketsDwelling);
                Dwelling dwell = new Dwelling(newDdId, ddCell[0], -1, DwellingType.MF234, bedRooms, quality, price, 0, year); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year
                dwell.setUsage(3); //vacant dwelling = 3; and hhID is equal to -1
                dwell.setFloorSpace(floorSpaceDwelling);
                dwell.setBuildingSize(buildingSizeAndYearBuilt[0]);
                vacantCounter++;
            }
            logger.info("   The number of vacant dwellings is: " + vacantCounter);
        }
    }


    private static double translateIncome (int incomeClass, double[] incomeThresholds, GammaDistributionImpl q) throws MathException {
        //provide the income value for each person give the income class.
        //income follows a gamma distribution that was calibrated using the microdata. Income thresholds are calculated for the stiches
        double income;
        int finish = 0;
        double low = 0;
        double high = 1;
        if (incomeClass == 90) {
            income = 0;  // kein Einkommen
/*        } else if (incomeClass == 50) {
            income = 0; // Selbständige/r Landwirt/in in der Haupttätigkeit
        } else if (incomeClass == 99) {
            income = -1; //keine Angabe*/
        } else {
            if (incomeClass == 1) {
                low = 0;
                high = incomeThresholds[0];
            } else if (incomeClass == 50){ // Selbständige/r Landwirt/in in der Haupttätigkeit
                low = 0; //give them a random income following the distribution
                high = 1;
            } else if (incomeClass == 99){ //keine Angabe
                low = 0; //give them a random income following the distribution
                high = 1;
            } else if (incomeClass == incomeThresholds.length + 1) {
                low = incomeThresholds[incomeThresholds.length-1];
                high = 1;
            } else {
                int i = 2;
                while (finish == 0){
                    if (incomeClass > i){
                        i++;
                    } else {
                        finish = 1;
                        low = incomeThresholds[i-2];
                        high = incomeThresholds[i-1];
                    }
                }
            }
            double cummulativeProb = SiloUtil.getRandomNumberAsDouble()*(high - low) + low;
            income = q.inverseCumulativeProbability(cummulativeProb);
        }
        return income;
    }


    private static int translateJobType (int personJob, TableDataSet jobs){
        //translate 100 job descriptions to 4 job types
        //jobs is one TableDataSet that is read from a csv file containing the description, ID and types of jobs.
        int job = 0;
        int finish = 0;
        int row = 1;
        while (finish == 0 & row < jobs.getRowCount()){
            if (personJob == jobs.getValueAt(row,"WZ08Code")) {
                finish =1;
                job = (int) jobs.getValueAt(row,"MarginalsCode");
            }
            else {
                row++;
            }
        }
        return job;
    }


    private static int translateEducationLevel (int personEducation, TableDataSet educationLevel){
        //translate 12 education levels to 4
        //jobs is one TableDataSet that is read from a csv file containing the description, ID and types of jobs.
        int education = 0;
        int finish = 0;
        int row = 1;
        while (finish == 0 & row < educationLevel.getRowCount()){
            if (personEducation == educationLevel.getValueAt(row,"microDataLabel")) {
                finish =1;
                education = (int) educationLevel.getValueAt(row,"controlTotalLabel");
            }
            else {
                row++;
            }
        }
        if (education == 0){education = 1;}
        return education;
    }


    private static int translateSchoolType (int personEducation, TableDataSet schoolType){
        //translate 12 education levels to 4
        //jobs is one TableDataSet that is read from a csv file containing the description, ID and types of jobs.
        int education = 0;
        int finish = 0;
        int row = 1;
        while (finish == 0 & row < schoolType.getRowCount()){
            if (personEducation == schoolType.getValueAt(row,"microDataLabel")) {
                finish =1;
                education = (int) schoolType.getValueAt(row,"controlTotalLabel");
            }
            else {
                row++;
            }
        }
        //if (education == 0){education = 1;}
        return education;
    }


    private static boolean obtainDriverLicense (int gender, int age, TableDataSet prob){
        //assign if the person holds a driver license based on the probabilities obtained from MiD data
        boolean license = false;
        int finish = 0;
        int row = 1;
        int threshold = 0;
        if (age > 18) {
            while (finish == 0 & row < prob.getRowCount()) {
                if (age > prob.getValueAt(row, "ageLimit")) {
                    row++;
                } else {
                    finish = 1;
                }
            }
            if (finish == 0) {
                row = prob.getRowCount();
            }
            if (gender == 0) {
                threshold = (int) prob.getValueAt(row, "male");
            } else {
                threshold = (int) prob.getValueAt(row, "female");
            }
            if (SiloUtil.getRandomNumberAsDouble() * 100 < threshold) {
                license = true;
            }
        } //if they are younger than 18, they don't hold driver license
        return license;
    }


    private static int selectFloorSpace(float[] vacantFloor, int[] sizeBracketsDwelling){
        //provide the size of the building
        int floorSpaceDwelling = 0;
        int floorSpace = SiloUtil.select(vacantFloor);
        if (floorSpace == 0){
            floorSpaceDwelling = (int) (30 + SiloUtil.getRandomNumberAsFloat() * 20);
        } else if (floorSpace == sizeBracketsDwelling.length - 1) {
            floorSpaceDwelling = (int) (120 + SiloUtil.getRandomNumberAsFloat() * 200);
        } else {
            floorSpaceDwelling = (int) SiloUtil.getRandomNumberAsDouble()*(sizeBracketsDwelling[floorSpace]-sizeBracketsDwelling[floorSpace-1]) +
                    sizeBracketsDwelling[floorSpace - 1];
        }
        return floorSpaceDwelling;
    }


    private static int[] selectBuildingSizeYear(float[] vacantSize, int[] yearBracketsDwelling){
        //provide the size of the building
        int[] buildingSizeAndYear = new int[2];
        int yearSize = SiloUtil.select(vacantSize);
        if (yearSize < yearBracketsDwelling.length){
            buildingSizeAndYear[0] = 1; //small-size building
            buildingSizeAndYear[1] = yearBracketsDwelling[yearSize];
        } else {
            buildingSizeAndYear[0] = 2; //medium-size building
            buildingSizeAndYear[1] = yearBracketsDwelling[yearSize - yearBracketsDwelling.length];
        }
        return buildingSizeAndYear;
    }


    private static int selectDwellingYear(int yearBuilt){
        //assign randomly one construction year to the dwelling within the year brackets of the microdata
        //Ages - 1: before 1919, 2: 1919-1948, 3: 1949-1978, 4: 1979 - 1986; 5: 1987 - 1990; 6: 1991 - 2000; 7: 2001 - 2004; 8: 2005 - 2008, 9: 2009 or later,
        int selectedYear = 1;
        float rnd = SiloUtil.getRandomNumberAsFloat();
        switch (yearBuilt){
            case 1: selectedYear = 1919;
                break;
            case 2: selectedYear = (int) (1919 + rnd * 39);
                break;
            case 3: selectedYear = (int) (1949 + rnd * 29);
                break;
            case 4: selectedYear = (int) (1979 + rnd * 7);
                break;
            case 5: selectedYear = (int) (1987 + rnd * 3);
                break;
            case 6: selectedYear = (int) (1991 + rnd * 9);
                break;
            case 7: selectedYear = (int) (2001 + rnd * 3);
                break;
            case 8: selectedYear = (int) (2005 + rnd * 3);
                break;
            case 9: selectedYear = (int) (2009 + rnd * 2);
                break;
        }
        return selectedYear;
    }


    private static int selectVacantDwellingYear(int yearBuilt){
        //assign randomly one construction year to the dwelling within the year brackets of the microdata -
        //Ages - 2: Before 1948, 5: 1949 - 1990; 6: 1991 - 2000; 9: 2001 or later
        int selectedYear = 1;
        float rnd = SiloUtil.getRandomNumberAsFloat();
        switch (yearBuilt){
            case 2: selectedYear = (int) (1919 + rnd * 39);
                break;
            case 5: selectedYear = (int) (1949 + rnd * 41);
                break;
            case 6: selectedYear = (int) (1991 + rnd * 9);
                break;
            case 9: selectedYear = (int) (2001 + rnd * 10);
                break;
        }
        return selectedYear;
    }

    private static int guessQualityDE(int heatingType, int heatingEnergy, int additionalHeating, int yearBuilt, int numberofQualityLevels){
        //guess quality of dwelling based on construction year and heating characteristics.
        //kitchen and bathroom quality are not coded on the micro data
        int quality = numberofQualityLevels;
        if (heatingType > 2) quality--; //reduce quality if not central or district heating
        if (heatingEnergy > 4) quality--; //reduce quality if energy is not gas, electricity or heating oil (i.e. coal, wood, biomass, solar energy)
        if (additionalHeating == 0) quality++; //increase quality if there is additional heating in the house (regardless the used energy)
        if (yearBuilt > 0){
            //Ages - 1: before 1919, 2: 1919-1948, 3: 1949-1978, 4: 1979 - 1986; 5: 1987 - 1990; 6: 1991 - 2000; 7: 2001 - 2004; 8: 2005 - 2008, 9: 2009 or later,
            float[] deteriorationProbability = {0.9f, 0.8f, 0.6f, 0.3f, 0.12f, 0.08f, 0.05f, 0.04f, 0.04f};
            float prob = deteriorationProbability[yearBuilt - 1];
            //attempt to drop quality by age two times (to get some spreading of quality levels)
            quality = quality - SiloUtil.select(new double[]{1 - prob ,prob});
            quality = quality - SiloUtil.select(new double[]{1 - prob, prob});
        }
        quality = Math.max(quality, 1);      // ensure that quality never drops below 1
        quality = Math.min(quality, numberofQualityLevels);      // ensure that quality never excess the number of quality levels
        return quality;
    }

    private static DwellingType guessDwellingType(int buildingSize, float ddType1Prob, float ddType3Prob){
        //Guess dwelling type based on the number of dwellings in the building from micro data (buildingSize, from micro data)
        //and the probability of having 1 dwelling out of having 1 or 2 (distribution in the municipality, from census)
        //and the probability of having 3-6 dwellings out of having 3-3+ (distribution in the municipality, from census)
        DwellingType type = DwellingType.MF234;
        if (buildingSize < 3){
            if (SiloUtil.getRandomNumberAsFloat() < ddType1Prob){
                type = DwellingType.SFD;
            } else {
                type = DwellingType.SFA;
            }
        } else {
            if (SiloUtil.getRandomNumberAsFloat() < ddType3Prob){
                type = DwellingType.MF5plus;
            }
        }

        return type;
    }


    private static DwellingType guessDwellingType(String ddType){
        //Guess dwelling type based on the number of dwellings in the building from micro data (buildingSize, from micro data)
        //and the probability of having 1 dwelling out of having 1 or 2 (distribution in the municipality, from census)
        //and the probability of having 3-6 dwellings out of having 3-3+ (distribution in the municipality, from census)
        DwellingType type = DwellingType.MF234;
        if (ddType == "MF234"){

        } else if (ddType.equals("MF5plus")){
            type = DwellingType.MF5plus;
        } else if (ddType.equals("SFD")) {
            type = DwellingType.SFD;
        } else if (ddType.equals("SFA")) {
            type = DwellingType.SFA;
        }
        return type;
    }

    private void identifyVacantJobsByZoneType() {
        // populate HashMap with Jobs by zone and job type
        // adapted from SyntheticPopUS

        logger.info("  Identifying vacant jobs by zone");
        Collection<Job> jobs = Job.getJobs();

        idVacantJobsByZoneType = new HashMap<>();
        numberVacantJobsByType = new HashMap<>();
        idZonesVacantJobsByType = new HashMap<>();
        numberZonesByType = new HashMap<>();
        numberVacantJobsByZoneByType = new HashMap<>();
        jobIntTypes = new HashMap<>();
        for (int i = 0; i < PropertiesSynPop.get().main.jobStringType.length; i++) {
            jobIntTypes.put(PropertiesSynPop.get().main.jobStringType[i], i);
        }
        int[] cellsID = PropertiesSynPop.get().main.cellsMatrix.getColumnAsInt("ID_cell");

        //create the counter hashmaps
        for (int i = 0; i < PropertiesSynPop.get().main.jobStringType.length; i++){
            int type = jobIntTypes.get(PropertiesSynPop.get().main.jobStringType[i]);
            numberZonesByType.put(type,0);
            numberVacantJobsByType.put(type,0);
            for (int j = 0; j < cellsID.length; j++){
                numberVacantJobsByZoneByType.put(type + cellsID[j] * 100, 0);
            }
        }
        //get the totals
        for (Job jj: jobs) {
            if (jj.getWorkerId() == -1) {
                int type = jobIntTypes.get(jj.getType());
                int typeZone = type + jj.getZone() * 100;
                //update the set of zones that have ID
                if (numberVacantJobsByZoneByType.get(typeZone) == 0){
                    numberZonesByType.put(type, numberZonesByType.get(type) + 1);
                }
                //update the number of vacant jobs per job type
                numberVacantJobsByType.put(type, numberVacantJobsByType.get(type) + 1);
                numberVacantJobsByZoneByType.put(typeZone, numberVacantJobsByZoneByType.get(typeZone) + 1);
            }
        }
        //create the IDs Hashmaps and reset the counters
        for (String jobType : PropertiesSynPop.get().main.jobStringType){
            int type = jobIntTypes.get(jobType);
            int[] dummy = SiloUtil.createArrayWithValue(numberZonesByType.get(type),0);
            idZonesVacantJobsByType.put(type,dummy);
            numberZonesByType.put(type,0);
            for (int j = 0; j < cellsID.length; j++){
                int typeZone = type + cellsID[j] * 100;
                int[] dummy2 = SiloUtil.createArrayWithValue(numberVacantJobsByZoneByType.get(typeZone), 0);
                idVacantJobsByZoneType.put(typeZone, dummy2);
                numberVacantJobsByZoneByType.put(typeZone, 0);
            }
        }
        //fill the Hashmaps with IDs
        for (Job jj: jobs) {
            if (jj.getWorkerId() == -1) {
                int type = jobIntTypes.get(jj.getType());
                int typeZone = jobIntTypes.get(jj.getType()) + jj.getZone() * 100;
                //update the list of job IDs per zone and job type
                int [] previousJobIDs = idVacantJobsByZoneType.get(typeZone);
                previousJobIDs[numberVacantJobsByZoneByType.get(typeZone)] = jj.getId();
                idVacantJobsByZoneType.put(typeZone,previousJobIDs);
                //update the set of zones that have ID
                if (numberVacantJobsByZoneByType.get(typeZone) == 0){
                    int[] previousZones = idZonesVacantJobsByType.get(type);
                    previousZones[numberZonesByType.get(type)] = typeZone;
                    idZonesVacantJobsByType.put(type,previousZones);
                    numberZonesByType.put(type, numberZonesByType.get(type) + 1);
                }
                //update the number of vacant jobs per job type
                numberVacantJobsByZoneByType.put(typeZone, numberVacantJobsByZoneByType.get(typeZone) + 1);
            }
        }
    }


    private void identifyVacantSchoolsByZoneByType(){
        logger.info("   Create vacant schools");

        numberVacantSchoolsByZoneByType = new HashMap<>();
        numberZonesWithVacantSchoolsByType = new HashMap<>();
        idZonesVacantSchoolsByType = new HashMap<>();
        schoolCapacityByType = new HashMap<>();
        int[] cellsID = PropertiesSynPop.get().main.cellsMatrix.getColumnAsInt("ID_cell");


        //create the counter hashmaps
        for (int schoolType : PropertiesSynPop.get().main.schoolTypes){
            for (int taz : cellsID){
                int count = (int) PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(taz,"school" + schoolType);
                numberVacantSchoolsByZoneByType.put(schoolType + taz * 100, count);
                if (count > 0) {
                    if (idZonesVacantSchoolsByType.containsKey(schoolType)){
                        numberZonesWithVacantSchoolsByType.put(schoolType,numberZonesWithVacantSchoolsByType.get(schoolType) + 1);
                        int[] zones = idZonesVacantSchoolsByType.get(schoolType);
                        zones = SiloUtil.expandArrayByOneElement(zones, schoolType + taz * 100);
                        idZonesVacantSchoolsByType.put(schoolType, zones);
                        schoolCapacityByType.put(schoolType, schoolCapacityByType.get(schoolType) + count);
                    } else {
                        numberZonesWithVacantSchoolsByType.put(schoolType, 1);
                        int[] zones = {schoolType + taz * 100};
                        idZonesVacantSchoolsByType.put(schoolType, zones);
                        schoolCapacityByType.put(schoolType,count);
                    }
                }
            }
        }



    }


    public static String[] expandArrayByOneElement (String[] existing, String addElement) {
        // create new array that has length of existing.length + 1 and copy values into new array
        String[] expanded = new String[existing.length + 1];
        System.arraycopy(existing, 0, expanded, 0, existing.length);
        expanded[expanded.length - 1] = addElement;
        return expanded;
    }

    public static int[] expandArrayByOneElement (int[] existing, int addElement) {
        // create new array that has length of existing.length + 1 and copy values into new array
        int[] expanded = new int[existing.length + 1];
        System.arraycopy(existing, 0, expanded, 0, existing.length);
        expanded[expanded.length - 1] = addElement;
        return expanded;
    }

    private int convertToInteger(String s) {
        // converts s to an integer value, one or two leading spaces are allowed

        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            boolean spacesOnly = true;
            for (int pos = 0; pos < s.length(); pos++) {
                if (!s.substring(pos, pos+1).equals(" ")) spacesOnly = false;
            }
            if (spacesOnly) return -999;
            else {
                logger.fatal("String " + s + " cannot be converted into an integer.");
                return 0;
            }
        }
    }


    public static int[] select (double[] probabilities, int[] id) {
        // select item based on probabilities (for zero-based float array)
        double sumProb = 0;
        int[] results = new int[2];
        for (double val: probabilities) sumProb += val;
        double selPos = sumProb * SiloUtil.getRandomNumberAsFloat();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[probabilities.length - 1];
        results[1] = probabilities.length - 1;
        return results;
    }


    public static int select (int[] probabilities) {
        // select item based on probabilities (for zero-based float array)
        double selPos = SiloUtil.getSum(probabilities) * SiloUtil.getRandomNumberAsDouble();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                return i;
            }
        }
        return probabilities.length - 1;
    }


    public static int[] select (double[] probabilities, int[] id, int sumProb) {
        // select item based on probabilities (for zero-based float array)
        int[] results = new int[2];
        double selPos = sumProb * SiloUtil.getRandomNumberAsFloat();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[probabilities.length - 1];
        results[1] = probabilities.length - 1;
        return results;
    }

    public static int[] select (double[] probabilities, int[] id, double sumProb) {
        // select item based on probabilities (for zero-based float array)
        int[] results = new int[2];
        double selPos = sumProb * SiloUtil.getRandomNumberAsDouble();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[probabilities.length - 1];
        results[1] = probabilities.length - 1;
        return results;
    }


    public static int[] select (double[] probabilities, int length, int[] id){
        //select item based on probabilities and return the name
        //probabilities and name have more items than the required (max number of required items is set on "length")
        double sumProb = 0;
        int[] results = new int[2];
        for (int i = 0; i < length; i++) {
            sumProb += probabilities[i];
        }
        double selPos = sumProb * SiloUtil.getRandomNumberAsDouble();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[probabilities.length - 1];
        results[1] = probabilities.length - 1;
        return results;
    }


    public static int[] select (double[] probabilities, int length, int[] id, double sumProb){
        //select item based on probabilities and return the name
        //probabilities and name have more items than the required (max number of required items is set on "length")

        int[] results = new int[2];
        double selPos = sumProb * SiloUtil.getRandomNumberAsDouble();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[probabilities.length - 1];
        results[1] = probabilities.length - 1;
        return results;
    }


    public static TableDataSet updateCountersHousehold (Household household, TableDataSet attributesCount, int mun){
        /* method to update the counters with the characteristics of the generated private household*/
        if (household.getHhSize() == 1){
            attributesCount.setIndexedValueAt(mun,"hhSize1",attributesCount.getIndexedValueAt(mun,"hhSize1") + 1);
        } else if (household.getHhSize() == 2){
            attributesCount.setIndexedValueAt(mun,"hhSize2",attributesCount.getIndexedValueAt(mun,"hhSize2") + 1);
        } else if (household.getHhSize() == 3){
            attributesCount.setIndexedValueAt(mun,"hhSize3",attributesCount.getIndexedValueAt(mun,"hhSize3") + 1);
        } else if (household.getHhSize() == 4){
            attributesCount.setIndexedValueAt(mun,"hhSize4",attributesCount.getIndexedValueAt(mun,"hhSize4") + 1);
        } else if (household.getHhSize() == 5){
            attributesCount.setIndexedValueAt(mun,"hhSize5",attributesCount.getIndexedValueAt(mun,"hhSize5") + 1);
        } else if (household.getHhSize() > 5){
            attributesCount.setIndexedValueAt(mun,"hhSize5",attributesCount.getIndexedValueAt(mun,"hhSize5") + 1);
        }
        attributesCount.setIndexedValueAt(mun,"hhTotal",attributesCount.getIndexedValueAt(mun,"hhTotal") + 1);
        return attributesCount;
    }

    public static TableDataSet updateCountersPerson (Person person, TableDataSet attributesCount,int mun, int[] ageBracketsPerson) {
        /* method to update the counters with the characteristics of the generated person in a private household*/
        attributesCount.setIndexedValueAt(mun, "population", attributesCount.getIndexedValueAt(mun, "population") + 1);
        if (person.getNationality() == Nationality.other) {
            attributesCount.setIndexedValueAt(mun, "foreigners", attributesCount.getIndexedValueAt(mun, "foreigners") + 1);
        }
        if (person.getGender() == 1) {
            if (person.getOccupation() == 1) {
                attributesCount.setIndexedValueAt(mun, "maleWorkers", attributesCount.getIndexedValueAt(mun, "maleWorkers") + 1);
            }
        } else {
            if (person.getOccupation() == 1) {
                attributesCount.setIndexedValueAt(mun, "femaleWorkers", attributesCount.getIndexedValueAt(mun, "femaleWorkers") + 1);
            }
        }
        int age = person.getAge();
        int row1 = 0;
        while (age > ageBracketsPerson[row1]) {
            row1++;
        }
        if (person.getGender() == 1) {
            String name = "male" + ageBracketsPerson[row1];
            attributesCount.setIndexedValueAt(mun, name, attributesCount.getIndexedValueAt(mun, name) + 1);
        } else {
            String name = "female" + ageBracketsPerson[row1];
            attributesCount.setIndexedValueAt(mun, name, attributesCount.getIndexedValueAt(mun, name) + 1);
        }
        return attributesCount;
    }


    public static TableDataSet updateCountersDwelling (Dwelling dwelling, TableDataSet attributesCount,int mun, int[] yearBrackets, int[] sizeBrackets){
        /* method to update the counters with the characteristics of the generated dwelling*/
        if (dwelling.getUsage() == 1){
            attributesCount.setIndexedValueAt(mun,"ownDwellings",attributesCount.getIndexedValueAt(mun,"ownDwellings") + 1);
        } else {
            attributesCount.setIndexedValueAt(mun,"rentedDwellings",attributesCount.getIndexedValueAt(mun,"rentedDwellings") + 1);
        }
        if (dwelling.getBuildingSize() == 1){
            attributesCount.setIndexedValueAt(mun,"smallDwellings",attributesCount.getIndexedValueAt(mun,"smallDwellings") + 1);
        } else {
            attributesCount.setIndexedValueAt(mun,"mediumDwellings",attributesCount.getIndexedValueAt(mun,"mediumDwellings") + 1);
        }
        return attributesCount;
    }


    public void writeVectorToCSV(int[] thresholds, double[] frequencies, String outputFile, double a, double g){
        try {

            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, true));
            pw.println("alpha,gamma,threshold,frequency,iteration");

            for (int i = 0; i< thresholds.length; i++) {
                pw.println(a + "," + g + "," + thresholds[i] + "," + frequencies[i]);
            }
            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMatrixToCSV(String outputFile, TableDataSet matrix, Double alpha, Double gamma, int count){
        try {

            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, true));

            for (int i = 1; i<= matrix.getRowCount(); i++) {
                String line = Integer.toString((int) matrix.getValueAt(i,1));
                for  (int j = 2; j <= matrix.getColumnCount(); j++){
                    line = line + "," + Integer.toString((int) matrix.getValueAt(i,j));
                }
                line = line + "," + alpha + "," + gamma + "," + count;
                pw.println(line);
            }
            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void checkTripLengthDistribution (Frequency travelTimes, double alpha, double gamma, String fileName, double step){
        //to obtain the trip length distribution
        int[] timeThresholds1 = new int[79];
        double[] frequencyTT1 = new double[79];
        for (int row = 0; row < timeThresholds1.length; row++) {
            timeThresholds1[row] = row + 1;
            frequencyTT1[row] = travelTimes.getCumPct(timeThresholds1[row]);
            //logger.info("Time: " + timeThresholds1[row] + ", cummulated frequency:  " + frequencyTT1[row]);
        }
        writeVectorToCSV(timeThresholds1, frequencyTT1, fileName, alpha, gamma);

    }

    public void checkodMatrix (TableDataSet odMatrix, double a, double g, int it, String fileName){
        //to obtain the square difference between the observed and estimated OD flows
        double dif = 0;
        double ind = 0;
        int count = 0;
        for (int row = 1; row <= odMatrix.getRowCount(); row++){
            ind = odMatrix.getValueAt(row,Integer.toString(it)) - odMatrix.getValueAt(row,"ObservedFlow");
            dif = dif + ind * ind;
            count++;
        }
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(fileName, true));
            pw.println(a + "," + g + "," + dif + "," + dif / count + "," + it);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double[] createArrayDoubles(double initial, double end, int length){
        //Create one array with specified start, end and number of elements

        double[] array = new double[length + 1];
        double step = (end - initial) / length;
        for (int i = 0; i < array.length; i++){
            array[i] = initial + i * step;
        }
        return array;

    }


    private void readAndStoreMicroData(){
        //method to read the synthetic population initial data
        logger.info("   Starting to read the micro data");

        //Scanning the file to obtain the number of households and persons in Bavaria
        GammaDistributionImpl gammaDist = new GammaDistributionImpl(PropertiesSynPop.get().main.incomeShape,
                1/PropertiesSynPop.get().main.incomeRate);
        String pumsFileName = de.tum.bgu.msm.properties.Properties.get().main.baseDirectory + PropertiesSynPop.get().main.microDataFile;
        String recString = "";
        int recCount = 0;
        int hhCountTotal = 0;
        int personCountTotal = 0;
        try {

            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                int householdNumber = 0;
                int recLander = convertToInteger(recString.substring(0,2));
                switch (recLander){
                    case 9:
                    householdNumber = convertToInteger(recString.substring(2,8)) * 1000 + convertToInteger(recString.substring(8,11));
                    if (householdNumber != previousHouseholdNumber) {
                        hhCountTotal++;
                        personCountTotal++;
                        previousHouseholdNumber = householdNumber; // Update the household number

                    } else if (householdNumber == previousHouseholdNumber) {
                        personCountTotal++;
                    }
                }
            }
            logger.info("  Read " + (personCountTotal) + " person records in " +
                    (hhCountTotal) + " private households from file: " + pumsFileName);
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }


        TableDataSet microHouseholds = new TableDataSet();
        TableDataSet microPersons = new TableDataSet();
        int[] dummy = SiloUtil.createArrayWithValue(hhCountTotal,0);
        int[] dummy1 = SiloUtil.createArrayWithValue(personCountTotal,0);
        int[] dummy4 = SiloUtil.createArrayWithValue(personCountTotal,0);
        int[] dummy5 = SiloUtil.createArrayWithValue(personCountTotal,0);
        microHouseholds.appendColumn(dummy,"IDhh");
        microPersons.appendColumn(dummy1,"IDpp");
        microPersons.appendColumn(dummy4,"IDhh");
        microPersons.appendColumn(dummy5,"WZ08");


        //Obtain person and household variables and add one column to the microData
        TableDataSet ppVariables = SiloUtil.readCSVfile2(PropertiesSynPop.get().main.attributesPersonFileName);// variables at the person level
        TableDataSet hhVariables = SiloUtil.readCSVfile2(PropertiesSynPop.get().main.attributesHouseholdFileName); //variables at the household level
        for (int i = 1; i <= ppVariables.getRowCount(); i++){
            int[] dummy2 = SiloUtil.createArrayWithValue(personCountTotal,0);
            microPersons.appendColumn(dummy2,ppVariables.getStringValueAt(i,"EF"));
        }
        for (int i = 1; i <= hhVariables.getRowCount(); i++){
            int[] dummy2 = SiloUtil.createArrayWithValue(hhCountTotal,0);
            microHouseholds.appendColumn(dummy2,hhVariables.getStringValueAt(i,"EF"));
            int[] dummy3 = SiloUtil.createArrayWithValue(personCountTotal,0);
            microPersons.appendColumn(dummy3,hhVariables.getStringValueAt(i,"EF"));
        }


        //read the micro data and assign the characteristics
        int hhCount = 0;
        int personCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                int recLander = convertToInteger(recString.substring(0,2));
                switch (recLander){
                    case 9:
                        int householdNumber = convertToInteger(recString.substring(2,8)) * 1000 + convertToInteger(recString.substring(8,11));
                        //Household attributes (only if the number of household differs)
                        if (householdNumber != previousHouseholdNumber) {
                            hhCount++;
                            microHouseholds.setValueAt(hhCount,"IDhh",hhCount);
                            for (int i = 1; i <= hhVariables.getRowCount(); i++){
                                int start = (int) hhVariables.getValueAt(i,"initial");
                                int finish = (int) hhVariables.getValueAt(i,"end");
                                microHouseholds.setValueAt(hhCount,hhVariables.getStringValueAt(i,"EF"),convertToInteger(recString.substring(start,finish)));
                            }
                            previousHouseholdNumber = householdNumber; // Update the household number
                        }
                        //Person attributes
                        personCount++;
                        microPersons.setValueAt(personCount,"IDpp",personCount);
                        microPersons.setValueAt(personCount,"IDhh",hhCount);
                        for (int i = 1; i <= ppVariables.getRowCount(); i++){
                            int start = (int) ppVariables.getValueAt(i,"initial");
                            int finish = (int) ppVariables.getValueAt(i,"end");
                            microPersons.setValueAt(personCount,ppVariables.getStringValueAt(i,"EF"),convertToInteger(recString.substring(start,finish)));
                        }
                        for (int i = 1; i <= hhVariables.getRowCount(); i++){
                            int start = (int) hhVariables.getValueAt(i,"initial");
                            int finish = (int) hhVariables.getValueAt(i,"end");
                            microPersons.setValueAt(personCount,hhVariables.getStringValueAt(i,"EF"),convertToInteger(recString.substring(start,finish)));
                        }
                        //translate the person categories to our categories
                        int school = (int) microPersons.getValueAt(personCount,"ppSchool");
                        if (school > 0) {
                            microPersons.setValueAt(personCount, "ppSchool", translateEducationLevel(school, PropertiesSynPop.get().main.schoolLevelTable));
                        } else {
                            microPersons.setValueAt(personCount, "ppSchool", 0);
                        }
                        if (microPersons.getValueAt(personCount,"ppOccupation") == 1) { // Only employed persons respond to the sector
                            //microPersons.setValueAt(personCount,"WZ08", translateJobType(Math.round((int) microPersons.getValueAt(personCount,"ppSector1")/10), PropertiesSynPop.get().main.jobsTable)); //First two digits of the WZ08 job classification in Germany. They are converted to 10 job classes (Zensus 2011 - Erwerbstätige nach Wirtschaftszweig Wirtschafts(unter)bereiche)
                        } else {
                            microPersons.setValueAt(personCount,"WZ08",0);
                        }
                        int income = (int) microPersons.getValueAt(personCount,"ppIncome");
                        try{
                            microPersons.setValueAt(personCount,"ppIncome",(int) translateIncome(income, PropertiesSynPop.get().main.incomeProbability, gammaDist));
                        } catch (MathException e){
                            e.printStackTrace();
                        }
                        int education = (int) microPersons.getValueAt(personCount,"ppEducation");
                        microPersons.setValueAt(personCount,"ppEducation", translateEducationLevel(education, PropertiesSynPop.get().main.educationDegreeTable));
                    }
            }
            logger.info("  Read " + (personCountTotal) + " person records in " +
                    (hhCountTotal) + " private households from file: " + pumsFileName);
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        SiloUtil.writeTableDataSet(microPersons, "input/summary/microPersonsAna.csv");
        SiloUtil.writeTableDataSet(microHouseholds, "input/summary/microHouseholdsAna.csv");

        logger.info("   Finished reading the micro data");
    }


    public int selectJobType(Person person) {
        //given a person, select the job type. It is based on the probabilities

        double[] probabilities = new double[PropertiesSynPop.get().main.jobStringType.length];
        int[] jobTypes = new int[PropertiesSynPop.get().main.jobStringType.length];
        //Person and job type values
        String name = "";
        if (person.getGender() == 1) {
            name = "maleEducation";
        } else {
            name = "femaleEducation";
        }
        name = name + person.getEducationLevel();

        //if (jobStringTypes.length == probabilitiesJob.getRowCount()) {
        //    probabilities = probabilitiesJob.getColumnAsDouble(name);
        //} else {
            for (int job = 0; job < PropertiesSynPop.get().main.jobStringType.length; job++){
                jobTypes[job] = jobIntTypes.get(PropertiesSynPop.get().main.jobStringType[job]);
                probabilities[job] = PropertiesSynPop.get().main.probabilitiesJob.getStringIndexedValueAt(PropertiesSynPop.get().main.jobStringType[job],name);
            }
        //}
        return new EnumeratedIntegerDistribution(jobTypes, probabilities).sample();

    }


    private HashMap<String,HashMap<Integer,Integer>> updateInnerMap(HashMap<String, HashMap<Integer, Integer>> outer, int gender, int age, int row) {

        String key = "male";
        if (gender == 2) {
            key = "female";
        }
        HashMap<Integer, Integer> inner = outer.get(key);
        if (inner == null){
            inner = new HashMap<Integer, Integer>();
            outer.put(key, inner);
        }
        inner.put(row, age);
        return outer;
    }


    private void setRoles(HashMap<String, HashMap<Integer, Integer>> singles, HashMap<String, HashMap<Integer, Integer>> married,
                          HashMap<Integer, Integer> childrenInHousehold, HashMap<String, HashMap<Integer, Integer>> noClass) {

        //set children in the household
        if (childrenInHousehold != null){
            for (Map.Entry<Integer, Integer> pair : childrenInHousehold.entrySet()){
                int row = pair.getKey();
                microDataPerson.setValueAt(row, "personRole", 3);
            }
        }
        //set singles and married in the household
        String[] keys = {"male", "female"};
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            HashMap<Integer, Integer> inner = singles.get(key);
            if (inner != null) {
                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
                    int row = pair.getKey();
                    microDataPerson.setValueAt(row, "personRole", 1);
                }
            }
            inner = married.get(key);
            if (inner != null) {
                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
                    int row = pair.getKey();
                    microDataPerson.setValueAt(row, "personRole", 2);
                }
            }
            inner = noClass.get(key);
            if (inner != null) {
                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
                    int row = pair.getKey();
                    microDataPerson.setValueAt(row, "rearrangedRole", 1);
                }
            }
        }
    }


    public int[] selectWorkplace(int home, HashMap<Integer, Integer> vacantJobsByZoneByType,
                                  int[] zoneJobKeys, int lengthZoneKeys, Matrix impedanceMatrix) {
        //given a person and job type, select the workplace location (raster cell)
        //it is based on the utility of that job type and each location, multiplied by the number of jobs that remain vacant
        //it can be directly used for schools, since the utility only checks the distance between the person home and destination

        double[] probabilities = new double[lengthZoneKeys];
        for (int j = 0; j < lengthZoneKeys; j++){
            probabilities[j] = impedanceMatrix.getValueAt(home, zoneJobKeys[j] / 100) * vacantJobsByZoneByType.get(zoneJobKeys[j]);
            //probability = impedance * number of vacant jobs. Impedance is calculated in advance as exp(utility)
        }
        int[] work = select(probabilities,zoneJobKeys);
        return work;
    }

    public int[] selectClosestSchool(int home, HashMap<Integer, Integer> vacantJobsByZoneByType,
                                  int[] zoneJobKeys, int lengthZoneKeys, Matrix impedanceMatrix) {
        //given a person and job type, select the workplace location (raster cell)
        //it is based on the utility of that job type and each location, multiplied by the number of jobs that remain vacant
        //it can be directly used for schools, since the utility only checks the distance between the person home and destination


        int[] min = new int[2];
        min[0] = zoneJobKeys[0];
        min[1] = 0;
        double minDist = impedanceMatrix.getValueAt(home, zoneJobKeys[0] / 100);
        for (int j = 1; j < lengthZoneKeys; j++) {
            if (impedanceMatrix.getValueAt(home, zoneJobKeys[j] / 100) < minDist) {
                min[0] = zoneJobKeys[j];
                min[1] = j;
                minDist = impedanceMatrix.getValueAt(home, zoneJobKeys[j] / 100);
            }
        }
        return min;
    }

    private void generateCountersForValidation(){
        //method to obtain the errors from the generated synthetic population
        //Create the errors table (for all the municipalities)
        counterMunicipality = new TableDataSet();
        errorMunicipality = new TableDataSet();
        counterMunicipality.appendColumn(cityID,"ID_city");
        errorMunicipality.appendColumn(cityID,"ID_city");
        for(String attribute : PropertiesSynPop.get().main.attributesMunicipality) {
            double[] dummy2 = SiloUtil.createArrayWithValue(cityID.length,0.0);
            double[] dummy3 = SiloUtil.createArrayWithValue(cityID.length,0.0);
            counterMunicipality.appendColumn(dummy2, attribute);
            errorMunicipality.appendColumn(dummy3, attribute);
        }
        counterMunicipality.buildIndex(counterMunicipality.getColumnPosition("ID_city"));
        errorMunicipality.buildIndex(errorMunicipality.getColumnPosition("ID_city"));


    }

    private void addCars(boolean flagSkipCreationOfSPforDebugging) {
        //method to estimate the number of cars per household
        //it must be run after generating the population
        CreateCarOwnershipModel createCarOwnershipModel = new CreateCarOwnershipModel(rb);
        createCarOwnershipModel.run(flagSkipCreationOfSPforDebugging);
    }


}
