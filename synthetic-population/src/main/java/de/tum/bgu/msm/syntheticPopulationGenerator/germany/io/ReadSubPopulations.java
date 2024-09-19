package de.tum.bgu.msm.syntheticPopulationGenerator.germany.io;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdFactoryMuc;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobFactoryMuc;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.*;
import java.util.*;

public class ReadSubPopulations {

    private static final Logger logger = Logger.getLogger(ReadSubPopulations.class);
    private final DataContainer dataContainer;
    private Map<String, Map<String, Integer>> countsPreviousState;
    private Map<String, Map<String, Integer>> countsState;
    private boolean generateSyntheticObjects;
    private int subPopulation;

    public ReadSubPopulations(DataContainer dataContainer, boolean generateSyntheticObjects, int subPopulation){
        this.dataContainer = dataContainer;
        this.generateSyntheticObjects = generateSyntheticObjects;
        this.subPopulation = subPopulation;
    }

    public void run(){
        logger.info("   Running module: read subpopulation");
        startCounters();
        readPopulationAndFillCounters();
        /*if (!PropertiesSynPop.get().main.runBySubpopulation) {
            printCounters();
        }*/
    }

    private void readPopulationAndFillCounters(){
        if (PropertiesSynPop.get().main.readMergeAndSplit) {
            for (String state : PropertiesSynPop.get().main.states) {
                int finalHhIdPreviousState = countsPreviousState.get("all").get("hh");
                int finalPpIdPreviousState = countsPreviousState.get("all").get("pp");
                int finalDdIdPreviousState = countsPreviousState.get("all").get("dd");
                int householdsInState = readHouseholdDataAndReassignIds(Properties.get().main.startYear, state, finalHhIdPreviousState, generateSyntheticObjects);
                int dwellingsInState = readDwellingDataAndReassignIds(Properties.get().main.startYear, state, finalDdIdPreviousState, finalHhIdPreviousState, generateSyntheticObjects);
                int personsInState = readPersonDataAndReassignIds(Properties.get().main.startYear, state, finalPpIdPreviousState, finalHhIdPreviousState, generateSyntheticObjects);
                countsPreviousState.get(state).put("hh", finalHhIdPreviousState);
                countsPreviousState.get(state).put("pp", finalPpIdPreviousState);
                countsPreviousState.get(state).put("dd", finalDdIdPreviousState);
                countsPreviousState.get("all").put("hh", finalHhIdPreviousState + householdsInState);
                countsPreviousState.get("all").put("pp", finalPpIdPreviousState + personsInState);
                countsPreviousState.get("all").put("dd", finalDdIdPreviousState + dwellingsInState);
                countsState.get(state).put("hh", householdsInState);
                countsState.get(state).put("pp", personsInState);
                countsState.get(state).put("dd", dwellingsInState);

            }
        } else {
            boolean hasWorkZone = true;
            if (PropertiesSynPop.get().main.runJobAllocation){
                hasWorkZone = false;
            }
            readHouseholdData(Properties.get().main.startYear, false, hasWorkZone);
            readDwellingData(Properties.get().main.startYear, false, hasWorkZone);
            readPersonData(Properties.get().main.startYear, false,hasWorkZone);
            readJobData(Properties.get().main.startYear);

        }
    }


    private void printCounters (){
            try {
                String fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                        + "/subPopulations/"
                        + "countersByState.csv";
                PrintWriter pw = new PrintWriter(new FileWriter(fileName, true));
                pw.println("state,hhInState,ppInState,ddInState,hhPrevious,ppPrevious,ddPrevious");
                for (String state : PropertiesSynPop.get().main.states) {
                    pw.print(state);
                    pw.print(",");
                    pw.print(countsState.get(state).get("hh"));
                    pw.print(",");
                    pw.print(countsState.get(state).get("pp"));
                    pw.print(",");
                    pw.print(countsState.get(state).get("dd"));
                    pw.print(",");
                    pw.print(countsPreviousState.get(state).get("hh"));
                    pw.print(",");
                    pw.print(countsPreviousState.get(state).get("pp"));
                    pw.print(",");
                    pw.println(countsPreviousState.get(state).get("dd"));
                }
                pw.print("all");
                pw.print(",");
                pw.print(0);
                pw.print(",");
                pw.print(0);
                pw.print(",");
                pw.print(0);
                pw.print(",");
                pw.print(countsPreviousState.get("all").get("hh"));
                pw.print(",");
                pw.print(countsPreviousState.get("all").get("pp"));
                pw.print(",");
                pw.println(countsPreviousState.get("all").get("dd"));
                pw.flush();
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }


    private int readHouseholdDataAndReassignIds(int year, String state, int finalHhIdPreviousState, boolean generate) {
        logger.info("Reading household micro data from ascii file from state " + state);

        HouseholdDataManager householdData = dataContainer.getHouseholdDataManager();
        HouseholdFactory householdFactory = householdData.getHouseholdFactory();
        String fileName = "";
        if (PropertiesSynPop.get().main.runBySubpopulation){
            fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                    + "/" + "subPopulations" + "/"
                    + PropertiesSynPop.get().main.householdsFileName
                    + subPopulation
                    + "_" + year + ".csv";
        } else {
            fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                    + "/" + state + "/"
                    + PropertiesSynPop.get().main.householdsFileName + "_" + year + ".csv";
        }
        HouseholdReaderMucMito hhReader = new HouseholdReaderMucMito(householdData, (HouseholdFactoryMuc) householdFactory);
        int totalHouseholds = hhReader.readDataWithStateAndReassignIds(fileName, state, finalHhIdPreviousState, generate);
        return totalHouseholds;
    }


    private int readPersonDataAndReassignIds(int year, String state, int finalPpIdPreviousState, int finalHhIdPreviousState, boolean generate) {
        logger.info("Reading person micro data from ascii file from state " + state);

        HouseholdDataManager householdData = dataContainer.getHouseholdDataManager();
        String fileName = "";
        if (PropertiesSynPop.get().main.runBySubpopulation){
            fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                    + "/" + "subPopulations" + "/"
                    + PropertiesSynPop.get().main.personsFileName
                    + subPopulation
                    + "_" + year + ".csv";
        } else {
            fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                    + "/" + state + "/"
                    + PropertiesSynPop.get().main.personsFileName + "_" + year + ".csv";
        }
        PersonReaderMucMito ppReader = new PersonReaderMucMito(householdData);
        int totalPersons = ppReader.readDataWithStateAndReassignIds(fileName, finalPpIdPreviousState, finalHhIdPreviousState, generate);
        return totalPersons;
    }


    private int readDwellingDataAndReassignIds(int year, String state, int finalDdIdPreviousState, int finalHhIdPreviousState, boolean generate) {
        logger.info("Reading dwelling micro data from ascii file from state " + state);

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        String fileName = "";
        if (PropertiesSynPop.get().main.runBySubpopulation){
            fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                    + "/" + "subPopulations" + "/"
                    + PropertiesSynPop.get().main.dwellingsFileName
                    + subPopulation
                    + "_" + year + ".csv";
        } else {
            fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                    + "/" + state + "/"
                    + PropertiesSynPop.get().main.dwellingsFileName + "_" + year + ".csv";
        }
        DwellingReaderMucMito ddReader = new DwellingReaderMucMito(realEstate);
        int dwellingsInState = ddReader.readDataWithStateAndReassignIds(fileName, finalDdIdPreviousState, finalHhIdPreviousState, generate, state);
        //int dwellingsInState = ddReader.readDataWithStateAndReassignIds(fileName, finalDdIdPreviousState, finalHhIdPreviousState, generate,
                //PropertiesSynPop.get().main.cellsMicrolocations);
        return dwellingsInState;
    }


    private int readHouseholdData(int year, boolean haveState, boolean hasWorkZone) {
        logger.info("Reading household micro data from ascii file from state " );

        HouseholdDataManager householdData = dataContainer.getHouseholdDataManager();
        HouseholdFactory householdFactory = householdData.getHouseholdFactory();
        String fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                + "/" + "subPopulations" + "/"
                + PropertiesSynPop.get().main.householdsFileName
                + subPopulation
                + "_" + year + ".csv";
        if (hasWorkZone){
            if (subPopulation != -1) {
                fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                        + "/" + "subPopulationsWithJobs" + "/"
                        + PropertiesSynPop.get().main.householdsFileName
                        + subPopulation
                        + "_" + year + ".csv";
            } else {
                fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                        + "/" + "subPopulationsWithJobs" + "/"
                        + PropertiesSynPop.get().main.householdsFileName
                        + "_" + year + ".csv";
            }
        }
        HouseholdReaderMucMito hhReader = new HouseholdReaderMucMito(householdData, (HouseholdFactoryMuc) householdFactory);
        int totalHouseholds = hhReader.readDataWithState(fileName, haveState);
        return totalHouseholds;
    }


    private int readPersonData(int year, boolean haveState, boolean haveWorkZone) {
        logger.info("Reading person micro data from ascii file from state " );

        HouseholdDataManager householdData = dataContainer.getHouseholdDataManager();
        String fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                    + "/" + "subPopulations" + "/"
                    + PropertiesSynPop.get().main.personsFileName
                    + subPopulation
                    + "_" + year + ".csv";
        if (haveWorkZone){
            if (subPopulation != -1) {
                fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                        + "/" + "subPopulationsWithJobs" + "/"
                        + PropertiesSynPop.get().main.personsFileName
                        + subPopulation
                        + "_" + year + ".csv";
            } else {
                fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                        + "/" + "subPopulationsWithJobs" + "/"
                        + PropertiesSynPop.get().main.personsFileName
                        + "_" + year + ".csv";
            }
        }
        PersonReaderMucMito ppReader = new PersonReaderMucMito(householdData);
        int totalPersons = ppReader.readDataWithState(fileName, haveState, haveWorkZone);
        return totalPersons;
    }

    private int readDwellingData(int year, boolean hasState, boolean haveWorkZone) {
        logger.info("Reading dwelling micro data from ascii file from state " );

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        String fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                    + "/" + "subPopulations" + "/"
                    + PropertiesSynPop.get().main.dwellingsFileName
                    + subPopulation
                    + "_" + year + ".csv";
        if (haveWorkZone){
            if (subPopulation != -1) {
                fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                        + "/" + "subPopulationsWithJobs" + "/"
                        + PropertiesSynPop.get().main.dwellingsFileName
                        + subPopulation
                        + "_" + year + ".csv";
            } else {
                fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                        + "/" + "subPopulationsWithJobs" + "/"
                        + PropertiesSynPop.get().main.dwellingsFileName
                        + "_" + year + ".csv";
            }
        }
        DwellingReaderMucMito ddReader = new DwellingReaderMucMito(realEstate);
        int dwellingsInState = ddReader.readDataWithState(fileName, hasState);
        return dwellingsInState;
    }

    private void readJobData(int year) {
        logger.info("Reading job micro data from ascii file from state " );

        String fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                + "/" + "subPopulationsWithJobs" + "/"
                + PropertiesSynPop.get().main.jobsFileName
                + subPopulation
                + "_" + year + ".csv";

        if (subPopulation == -1){
            fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                    + "/" + "subPopulationsWithJobs" + "/"
                    + PropertiesSynPop.get().main.jobsFileName
                    + "_" + year + ".csv";
        }

        JobReaderMucMito ddReader = new JobReaderMucMito(dataContainer.getJobDataManager(), (JobFactoryMuc) dataContainer.getJobDataManager().getFactory());
        ddReader.readData(fileName);
    }


    private void readJobData(int year, String state) {
        logger.info("Reading job micro data from ascii file");

        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        JobFactoryMuc jobFactory = (JobFactoryMuc) dataContainer.getJobDataManager().getFactory();
        String fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                + PropertiesSynPop.get().main.jobsFileName + "_" + year + ".csv";

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posZone = SiloUtil.findPositionInArray("zone",header);
            int posWorker = SiloUtil.findPositionInArray("personId",header);
            int posType = SiloUtil.findPositionInArray("type",header);
            int posCoordX = SiloUtil.findPositionInArray("CoordX", header);
            int posCoordY = SiloUtil.findPositionInArray("CoordY", header);
            int posStartTime = SiloUtil.findPositionInArray("startTime", header);
            int posDuration = SiloUtil.findPositionInArray("duration", header);


            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id      = Integer.parseInt(lineElements[posId]);
                int zoneId    = Integer.parseInt(lineElements[posZone]);
                int worker  = Integer.parseInt(lineElements[posWorker]);
                String type = lineElements[posType].replace("\"", "");
                Coordinate coordinate = null;
                if (posCoordX >= 0 && posCoordY >= 0) {
                    try {
                        coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                    } catch (Exception e) {
                    }
                }
                JobMuc jj = jobFactory.createJob(id, zoneId, coordinate, worker, type);
                int startTime = Integer.parseInt(lineElements[posStartTime]);
                int duration = Integer.parseInt(lineElements[posDuration]);
                jj.setJobWorkingTime(startTime, duration);
                jobDataManager.addJob(jj);
                if (id == SiloUtil.trackJj) {
                    SiloUtil.trackWriter.println("Read job with following attributes from " + fileName);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop job file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " jobs.");
    }

    private void writeMultipleFilesForHouseholdsAndPersons(DataContainerWithSchools dataContainer){

        Map<Integer, PrintWriter> householdWriter = new HashMap<>();
        Map<Integer, PrintWriter> personWriter = new HashMap<>();
        Map<Integer, PrintWriter> dwellingWriter = new HashMap<>();

        String outputFolder = Properties.get().main.baseDirectory  + PropertiesSynPop.get().main.pathSyntheticPopulationFiles
                + "/subPopulations/" + PropertiesSynPop.get().main.state + "/";
        SiloUtil.createDirectoryIfNotExistingYet(outputFolder);

        ArrayList<Household> householdArrayList = new ArrayList<>();
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()){
            householdArrayList.add(hh);
        }
        Collections.shuffle(householdArrayList);

        for (int part = 0; part < PropertiesSynPop.get().main.numberOfSubpopulations; part++) {
            String filehh = outputFolder
                    + PropertiesSynPop.get().main.householdsFileName + part + "_"
                    + Properties.get().main.baseYear
                    + ".csv";
            String filepp = outputFolder
                    + PropertiesSynPop.get().main.personsFileName + part + "_"
                    + Properties.get().main.baseYear
                    + ".csv";
            String filedd = outputFolder
                    + PropertiesSynPop.get().main.dwellingsFileName + part + "_"
                    + Properties.get().main.baseYear
                    + ".csv";
            PrintWriter pwHousehold0 = SiloUtil.openFileForSequentialWriting(filehh, false);
            pwHousehold0.println("id,dwelling,zone,hhSize,autos,state,originalId");
            PrintWriter pwp = SiloUtil.openFileForSequentialWriting(filepp, false);
            pwp.print("id,hhid,age,gender,occupation,driversLicense,workplace,income,state,originalId");
            PrintWriter pwDwelling0 = SiloUtil.openFileForSequentialWriting(filedd, false);
            pwDwelling0.println("id,hhId,zone,coordX,coordY,state,originalId");
            householdWriter.put(part, pwHousehold0);
            personWriter.put(part, pwp);
            dwellingWriter.put(part, pwDwelling0);
        }

        int hhCount = 1;
        int partCount = 0;

        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();

        int numberOfHhSubpopulation = (int) (householdArrayList.size() / PropertiesSynPop.get().main.numberOfSubpopulations);
        int startingHouseholdId = 100000000;
        int startingPersonId = 100000000;
        for (Household hh : householdArrayList) {
            Dwelling dd = realEstateDataManager.getDwelling(hh.getDwellingId());
            if (hhCount <= numberOfHhSubpopulation) {
                PrintWriter pwh = householdWriter.get(partCount);
                pwh.print(hh.getId() + startingHouseholdId);
                pwh.print(",");
                pwh.print(hh.getDwellingId() + startingHouseholdId);
                pwh.print(",");
                pwh.print(dd.getZoneId());
                pwh.print(",");
                pwh.print(hh.getHhSize());
                pwh.print(",");
                pwh.print(hh.getAutos());
                pwh.print(",");
                pwh.print(hh.getAttribute("state").get().toString());
                pwh.print(",");
                pwh.println(hh.getAttribute("originalId").get().toString());
                householdWriter.put(partCount, pwh);
                for (Person pp : hh.getPersons().values()){
                    PrintWriter pwp = personWriter.get(partCount);
                    pwp.print(pp.getId() + startingPersonId);
                    pwp.print(",");
                    pwp.print(pp.getHousehold().getId() + startingHouseholdId);
                    pwp.print(",");
                    pwp.print(pp.getAge());
                    pwp.print(",");
                    pwp.print(pp.getGender().getCode());
                    pwp.print(",");
                    pwp.print(pp.getOccupation().getCode());
                    pwp.print(",");
                    pwp.print(pp.hasDriverLicense());
                    pwp.print(",");
                    pwp.print(pp.getJobId());
                    pwp.print(",");
                    pwp.print(pp.getAnnualIncome());
                    pwp.print(",");
                    pwp.print(pp.getAttribute("jobType").get().toString());
                    pwp.print(",");
                    pwp.print(pp.getAttribute("disability").get().toString());
                    pwp.print(",");
                    pwp.print(0);
                    pwp.print(",");
                    pwp.print(pp.getAttribute("schoolType").get().toString());
                    pwp.print(",");
                    pwp.print(((PersonMuc)pp).getSchoolPlace());
                    pwp.print(",");
                    pwp.print(hh.getAttribute("state").get().toString());
                    pwp.print(",");
                    pwp.print(pp.getAttribute("originalId").get().toString());
                    pwp.println();
                    personWriter.put(partCount, pwp);
                }
                PrintWriter pwd = dwellingWriter.get(partCount);
                pwd.print(dd.getId() + startingHouseholdId);
                pwd.print(",");
                pwd.print(dd.getResidentId() + startingPersonId);
                pwd.print(",");
                pwd.print(dd.getZoneId());
                pwd.print(",");
                pwd.print(dd.getCoordinate().x);
                pwd.print(",");
                pwd.print(dd.getCoordinate().y);
                pwd.print(",");
                pwd.print(hh.getAttribute("state").get().toString());
                pwd.print(",");
                pwd.println(dd.getAttribute("originalId").get().toString());
                householdDataManager.removeHousehold(hh.getId());
                realEstateDataManager.removeDwelling(dd.getId());
            } else {
                hhCount = 1;
                partCount++;
                if (partCount > PropertiesSynPop.get().main.numberOfSubpopulations - 1){
                    partCount = PropertiesSynPop.get().main.numberOfSubpopulations - 1;
                }
            }
            hhCount++;
        }
        for (int part = 0; part < PropertiesSynPop.get().main.numberOfSubpopulations; part++) {
            householdWriter.get(part).close();
            personWriter.get(part).close();
            dwellingWriter.get(part).close();
        }

    }

    private void startCounters(){
        countsPreviousState = new HashMap<>();
        countsPreviousState = new HashMap<>();
        countsPreviousState.putIfAbsent("all", new HashMap<>());
        countsPreviousState.get("all").putIfAbsent("hh", 0);
        countsPreviousState.get("all").put("pp", 0);
        countsPreviousState.get("all").put("dd", 0);
        countsState = new HashMap<>();
        countsState = new HashMap<>();
        for (String state : PropertiesSynPop.get().main.states) {
            countsPreviousState.put(state, new HashMap<>());
            countsPreviousState.get(state).putIfAbsent("hh", 0);
            countsPreviousState.get(state).put("pp", 0);
            countsPreviousState.get(state).put("dd", 0);
            countsState.put(state, new HashMap<>());
            countsState.get(state).putIfAbsent("hh", 0);
            countsState.get(state).put("pp", 0);
            countsState.get(state).put("dd", 0);
        }
    }

}
