package de.tum.bgu.msm.syntheticPopulationGenerator.kagawa;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.common.matrix.Matrix;
import de.tum.bgu.msm.common.util.ResourceUtil;
import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobUtils;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.data.person.household.HouseholdFactoryTak;
import de.tum.bgu.msm.io.*;
import de.tum.bgu.msm.io.output.*;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import omx.OmxFile;
import omx.OmxLookup;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.commons.math.stat.Frequency;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class SyntheticPopJP implements SyntheticPopI {

    private ResourceBundle rb;
    private final DataSetSynPop dataSetSynPop;
    //Options to run de synthetic population
    protected static final String PROPERTIES_RUN_IPU                      = "run.ipu.synthetic.pop";
    protected static final String PROPERTIES_RUN_SYNTHETIC_POPULATION     = "run.synth.pop.generator";
    //Routes of the input data
    protected static final String PROPERTIES_MICRODATA_2010_PATH          = "micro.data.2010";
    protected static final String PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX   = "marginals.municipality";
    protected static final String PROPERTIES_JOBS_MUNICIPALITY            = "jobs.municipality";
    protected static final String PROPERTIES_SELECTED_MUNICIPALITIES_LIST = "municipalities.list";
    protected static final String PROPERTIES_RASTER_CELLS                 = "raster.cells.definition";
    protected static final String PROPERTIES_DISTANCE_RASTER_CELLS        = "distanceODmatrix";
    protected static final String PROPERTIES_JOB_DESCRIPTION              = "jobs.dictionary";
    protected static final String PROPERTIES_EDUCATION_DESCRIPTION        = "education.dictionary";
    protected static final String PROPERTIES_SCHOOL_DESCRIPTION           = "school.dictionary";
    protected static final String PROPERTIES_NUMBER_OF_DWELLING_QUALITY_LEVELS = "dwelling.quality.levels.distinguished";
    //Routes of input data (if IPU is not performed)
    protected static final String PROPERTIES_WEIGHTS_MATRIX               = "weights.matrix";
    //Parameters of the synthetic population
    protected static final String PROPERTIES_MAX_ITERATIONS               = "max.iterations.ipu";
    protected static final String PROPERTIES_MAX_ERROR                    = "max.error.ipu";
    protected static final String PROPERTIES_INITIAL_ERROR                = "ini.error.ipu";
    protected static final String PROPERTIES_IMPROVEMENT_ERROR            = "min.improvement.error.ipu";
    protected static final String PROPERTIES_IMPROVEMENT_ITERATIONS       = "iterations.improvement.ipu";
    protected static final String PROPERTIES_INCREASE_ERROR               = "increase.error.ipu";
    protected static final String PROPERTIES_INCOME_GAMMA_PROBABILITY     = "income.probability";
    protected static final String PROPERTIES_INCOME_GAMMA_SHAPE           = "income.gamma.shape";
    protected static final String PROPERTIES_INCOME_GAMMA_RATE            = "income.gamma.rate";

    protected static final String PROPERTIES_SCHOOL_TYPES_DE              = "school.types";
    protected static final String PROPERTIES_JOB_ALPHA                    = "employment.choice.alpha";
    protected static final String PROPERTIES_JOB_GAMMA                    = "employment.choice.gamma";
    protected static final String PROPERTIES_UNIVERSITY_ALPHA             = "employment.choice.alpha";
    protected static final String PROPERTIES_UNIVERSITY_GAMMA             = "employment.choice.gamma";
    //Read the synthetic population
    protected static final String PROPERTIES_HOUSEHOLD_SYN_POP            = "household.file.ascii";
    protected static final String PROPERTIES_PERSON_SYN_POP               = "person.file.ascii";
    protected static final String PROPERTIES_DWELLING_SYN_POP             = "dwelling.file.ascii";
    protected static final String PROPERTIES_JOB_SYN_POP                  = "job.file.ascii";
    protected static final String PROPERTIES_ATRIBUTES_MICRODATA_PP        = "read.attributes.pp";
    protected static final String PROPERTIES_ATRIBUTES_MICRODATA_HH        = "read.attributes.hh";


    protected static final String PROPERTIES_ATRIBUTES_ZONAL_DATA        = "zonal.data.ipu";

    protected TableDataSet microDataHousehold;
    protected TableDataSet microDataPerson;
    protected TableDataSet microDataDwelling;
    protected TableDataSet frequencyMatrix;
    protected TableDataSet marginalsMunicipality;
    protected TableDataSet jobsMunicipality;
    protected TableDataSet cellsMatrix;
    protected TableDataSet regionsforFrequencyMatrix;

    protected int[] cityID;
    protected int[] countyID;
    protected HashMap<Integer, int[]> municipalitiesByCounty;
    protected HashMap<Integer, int[]> cityTAZ;
    protected ArrayList<Integer> municipalitiesWithZeroPopulation;

    protected String[] attributesMunicipality;
    protected int[] ageBracketsPerson;
    protected int[] sizeBracketsDwelling;
    protected int[] yearBracketsDwelling;
    protected String[] typeBracketsDwelling;
    protected int numberofQualityLevels;
    protected TableDataSet counterMunicipality;
    protected TableDataSet errorMunicipality;

    protected TableDataSet weightsTable;
    protected TableDataSet jobsTable;
    protected TableDataSet educationDegreeTable;
    protected TableDataSet schoolLevelTable;
    HashMap<String, Integer> jobIntTypes;
    protected String[] jobStringTypes;
    protected int[] schoolTypes;

    protected HashMap<Integer, HashMap<Integer, Integer>> householdsForFrequencyMatrix;
    protected HashMap<Integer, int[]> idVacantJobsByZoneType;
    protected HashMap<Integer, Integer> numberVacantJobsByType;
    protected HashMap<Integer, int[]> idZonesVacantJobsByType;
    protected HashMap<Integer, Integer> numberVacantJobsByZoneByType;
    protected HashMap<Integer, Integer> numberZonesByType;

    protected HashMap<Integer, Integer> numberVacantSchoolsByZoneByType;
    protected HashMap<Integer, int[]> idZonesVacantSchoolsByType;
    protected HashMap<Integer, Integer> numberZonesWithVacantSchoolsByType;
    protected HashMap<Integer, Integer> schoolCapacityByType;

    protected double alphaJob;
    protected double gammaJob;

    protected Matrix distanceMatrix;
    protected Matrix distanceImpedance;
    protected TableDataSet odMunicipalityFlow;
    protected TableDataSet odCountyFlow;
    private DataContainer dataContainer;

    private HashMap<Person, Integer> jobTypeByWorker= new HashMap<>();

    static Logger logger = Logger.getLogger(String.valueOf(SyntheticPopJP.class));
    private Properties properties;


    public SyntheticPopJP(ResourceBundle rb, DataSetSynPop dataSetSynPop, Properties properties) {
        // Constructor
        this.rb = rb;
        this.dataSetSynPop = dataSetSynPop;
        this.properties = properties;
    }


    public void runSP(){
        //method to create the synthetic population
        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SYNTHETIC_POPULATION, false)) return;
        logger.info("   Starting to create the synthetic population.");
        //TODO: change to cape town implementation
        dataContainer = DataBuilder.getModelDataForMuc(properties, null);
        ExtractMicroDataJP extractMicroData = new ExtractMicroDataJP(rb, dataSetSynPop);
        extractMicroData.run();
        frequencyMatrix = extractMicroData.getFrequencyMatrix();
        microDataDwelling = extractMicroData.getMicroDwellings();
        microDataHousehold = extractMicroData.getMicroHouseholds();
        microDataPerson = extractMicroData.getMicroPersons();
        typeBracketsDwelling = extractMicroData.getTypeBracketsDwelling();
        sizeBracketsDwelling = extractMicroData.getSizeBracketsDwelling();
        yearBracketsDwelling = extractMicroData.getYearBracketsDwelling();
        ageBracketsPerson = extractMicroData.getAgeBracketsPerson();
        jobStringTypes = extractMicroData.getOccupationBrackets();
        attributesMunicipality = extractMicroData.getAttributesMunicipality();
        readInputData();
        createDirectoryForOutput();
        long startTime = System.nanoTime();
        //Run fitting procedure
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_IPU) == true) {
            //runIPUbyCity(); //IPU fitting with one geographical constraint. Each municipality is independent of others
            createWeightsAndErrorsCity();
            new IPUbyCityWithSubsample(dataSetSynPop).run();
            weightsTable = dataSetSynPop.getWeights();
            weightsTable.buildIndex(weightsTable.getColumnPosition("ID"));
        } else {
            readIPU(); //Read the weights to select the household
        }
        generateHouseholdsPersonsDwellings(); //Monte Carlo selection process to generate the synthetic population. The synthetic dwellings will be obtained from the same microdata
        generateJobs(); //Generate the jobs by type. Allocated to TAZ level
        assignJobs(); //Workplace allocation
        assignSchools(); //School allocation
        //SummarizeData.writeOutSyntheticPopulation(2000,dataContainer);
        summarizeData(dataContainer);
        long estimatedTime = System.nanoTime() - startTime;
        logger.info("   Finished creating the synthetic population. Elapsed time: " + estimatedTime);
    }


    private void readInputData() {


        //Read the attributes at the municipality level (household attributes) and the marginals at the municipality level (German: Gemeinden)
        //attributesMunicipality = frequencyMatrix.getColumnLabels();
        marginalsMunicipality = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX));
        marginalsMunicipality.buildIndex(marginalsMunicipality.getColumnPosition("CODE_Z01"));
        jobsMunicipality = SiloUtil.readCSVfile(rb.getString(PROPERTIES_JOBS_MUNICIPALITY));
        jobsMunicipality.buildIndex(jobsMunicipality.getColumnPosition("CODE_Z01"));
        municipalitiesWithZeroPopulation = new ArrayList<>();

        //List of municipalities and counties that are used for IPU and allocation
        TableDataSet selectedMunicipalities = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SELECTED_MUNICIPALITIES_LIST)); //TableDataSet with all municipalities
        ArrayList<Integer> municipalities = new ArrayList<>();
        ArrayList<Integer> counties = new ArrayList<>();
        HashMap<Integer, ArrayList> municipalitiesByCounty;
        municipalitiesByCounty = new HashMap<>();
        for (int row = 1; row <= selectedMunicipalities.getRowCount(); row++){
            if (selectedMunicipalities.getValueAt(row,"Select") == 1f){
                int city = (int) selectedMunicipalities.getValueAt(row,"V1");
                municipalities.add(city);
                int county = (int) selectedMunicipalities.getValueAt(row,"V2");
                if (!SiloUtil.containsElement(counties, county)) {
                    counties.add(county);
                }
                if (municipalitiesByCounty.containsKey(county)) {
                    ArrayList<Integer> citiesInThisCounty = municipalitiesByCounty.get(county);
                    citiesInThisCounty.add(city);
                    municipalitiesByCounty.put(county, citiesInThisCounty);
                } else {
                    ArrayList<Integer> citiesInThisCounty = new ArrayList<>();
                    citiesInThisCounty.add(city);
                    municipalitiesByCounty.put(county, citiesInThisCounty);
                }
                if (selectedMunicipalities.getValueAt(row,"SelectAllocation") == 0){
                    municipalitiesWithZeroPopulation.add((int) selectedMunicipalities.getValueAt(row,"V1"));
                }
            }
        }
        cityID = SiloUtil.convertArrayListToIntArray(municipalities);
        countyID = SiloUtil.convertArrayListToIntArray(counties);
        dataSetSynPop.setCityIDs(cityID);
        dataSetSynPop.setCountyIDs(countyID);
        dataSetSynPop.setMunicipalities(municipalities);
        dataSetSynPop.setCounties(counties);
        dataSetSynPop.setMunicipalitiesByCounty(municipalitiesByCounty);
        dataSetSynPop.setMunicipalitiesWithZeroPopulation(municipalitiesWithZeroPopulation);

        //TAZ attributes
        cellsMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_RASTER_CELLS));
        cellsMatrix.buildIndex(cellsMatrix.getColumnPosition("ID_cell"));
        cityTAZ = new HashMap<>();
        for (int i = 1; i <= cellsMatrix.getRowCount(); i++){
            int city = (int) cellsMatrix.getValueAt(i,"ID_city");
            int taz = (int) cellsMatrix.getValueAt(i,"ID_cell");
            if (cityTAZ.containsKey(city)){
                int[] previousTaz = cityTAZ.get(city);
                previousTaz = SiloUtil.expandArrayByOneElement(previousTaz, taz);
                cityTAZ.put(city, previousTaz);
            } else {
                int[] previousTaz = {taz};
                cityTAZ.put(city,previousTaz);
            }
        }
        dataSetSynPop.setTazByMunicipality(cityTAZ);


        //Read the skim matrix
        logger.info("   Starting to read OMX matrix");
        String omxFileName= ResourceUtil.getProperty(rb,PROPERTIES_DISTANCE_RASTER_CELLS);
        OmxFile travelTimeOmx = new OmxFile(omxFileName);
        travelTimeOmx.openReadOnly();
        distanceMatrix = SiloUtil.convertOmxToMatrix(travelTimeOmx.getMatrix("mat1"));
        OmxLookup omxLookUp = travelTimeOmx.getLookup("lookup1");
        int[] externalNumbers = (int[]) omxLookUp.getLookup();
        distanceMatrix.setExternalNumbersZeroBased(externalNumbers);
        for (int i = 1; i <= distanceMatrix.getRowCount(); i++){
            for (int j = 1; j <= distanceMatrix.getColumnCount(); j++){
                if (i == j) {
                    distanceMatrix.setValueAt(i,j, 50/1000);
                } else {
                    distanceMatrix.setValueAt(i,j, distanceMatrix.getValueAt(i,j)/1000);
                }
            }
        }
        dataSetSynPop.setDistanceTazToTaz(distanceMatrix);
        logger.info("   Read OMX matrix");



        //Zonal data for IPU
        regionsforFrequencyMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_ATRIBUTES_ZONAL_DATA));
        regionsforFrequencyMatrix.buildIndex(regionsforFrequencyMatrix.getColumnPosition("V1"));

        householdsForFrequencyMatrix = new HashMap<>();
        for (int i = 1; i <= microDataDwelling.getRowCount();i++){
            int v2Zone = (int) microDataDwelling.getValueAt(i,"PtResCode");
            int ddID = (int) microDataDwelling.getValueAt(i,"id");
            if (householdsForFrequencyMatrix.containsKey(v2Zone)) {
                householdsForFrequencyMatrix.get(v2Zone).put(ddID, 1);
            } else {
                HashMap<Integer, Integer> map = new HashMap<>();
                map.put(ddID, 1);
                householdsForFrequencyMatrix.put(v2Zone, map);
            }
        }
        dataSetSynPop.setHouseholdsForFrequencyMatrix(householdsForFrequencyMatrix);
        dataSetSynPop.setRegionsforFrequencyMatrix(regionsforFrequencyMatrix);
    }


    private void createDirectoryForOutput() {
        // create output directories
        SiloUtil.createDirectoryIfNotExistingYet("microData");
        SiloUtil.createDirectoryIfNotExistingYet("microData/interimFiles");
    }


    private void runIPUbyCity(){
        //IPU process for independent municipalities (only household attributes)
        logger.info("   Starting to prepare the data for IPU");


        //Read the frequency matrix
        //int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        //int[] nonZeroIds = frequencyMatrix.getColumnAsInt("ID");
        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));

        //Zonal data for IPU
        regionsforFrequencyMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_ATRIBUTES_ZONAL_DATA));
        regionsforFrequencyMatrix.buildIndex(regionsforFrequencyMatrix.getColumnPosition("V1"));

        householdsForFrequencyMatrix = new HashMap<>();
        for (int i = 1; i <= microDataDwelling.getRowCount();i++){
            int v2Zone = (int) microDataDwelling.getValueAt(i,"PtResCode");
            int ddID = (int) microDataDwelling.getValueAt(i,"id");
            if (householdsForFrequencyMatrix.containsKey(v2Zone)) {
                householdsForFrequencyMatrix.get(v2Zone).put(ddID, 1);
            } else {
                HashMap<Integer, Integer> map = new HashMap<>();
                map.put(ddID, 1);
                householdsForFrequencyMatrix.put(v2Zone, map);
            }
        }


        //Create the weights table (for all the municipalities)
        TableDataSet weightsMatrix = new TableDataSet();
        weightsMatrix.appendColumn(frequencyMatrix.getColumnAsInt("id"),"ID");
        weightsMatrix.buildIndex(weightsMatrix.getColumnPosition("ID"));


        //Create the errors table (for all the municipalities, by attribute)
        TableDataSet errorsMatrix = new TableDataSet();
        errorsMatrix.appendColumn(cityID,"ID_city");
        for(int attribute = 0; attribute < attributesMunicipality.length; attribute++) {
            double[] dummy2 = SiloUtil.createArrayWithValue(cityID.length,1.0);
            errorsMatrix.appendColumn(dummy2, attributesMunicipality[attribute]);
        }


        //For each municipality, we perform IPU
        for(int municipality = 0; municipality < cityID.length; municipality++) {

            logger.info("   Municipality " + cityID[municipality] + ". Starting IPU.");

            //-----------***** Data preparation *****-------------------------------------------------------------------
            //Create local variables to avoid accessing to the same variable on the parallel processing
            int municipalityID = cityID[municipality]; //Municipality that is under review.
            String municipalityIDstring = Integer.toString(cityID[municipality]); //Municipality that is under review.

            String[] attributesHouseholdList = new String[attributesMunicipality.length - 1]; //List of attributes.

            int v2zone = (int) regionsforFrequencyMatrix.getIndexedValueAt(municipalityID, "V2");
            if (householdsForFrequencyMatrix.containsKey(v2zone)) {
                TableDataSet microDataMatrix = new TableDataSet(); //Frequency matrix obtained from the micro data.
                HashMap<Integer, Integer> hhs = householdsForFrequencyMatrix.get(v2zone);
                int[] hhIds = hhs.keySet().stream().mapToInt(Integer::intValue).toArray();
                microDataMatrix.appendColumn(hhIds, "id");
                for (int k = 0; k < attributesHouseholdList.length; k++) {
                    attributesHouseholdList[k] = attributesMunicipality[k + 1];
                    microDataMatrix.appendColumn(SiloUtil.createArrayWithValue(hhIds.length, 0), attributesHouseholdList[k]);
                    for (int i = 1; i <= microDataMatrix.getRowCount(); i++) {
                        int ddID = (int) microDataMatrix.getValueAt(i, "id");
                        int value = (int) frequencyMatrix.getIndexedValueAt(ddID, attributesHouseholdList[k]);
                        microDataMatrix.setValueAt(i, attributesHouseholdList[k], value);
                    }
                }
                int[] nonZeroIds = microDataMatrix.getColumnAsInt("id");
                int[] microDataIds = microDataMatrix.getColumnAsInt("id");

                //Create the collapsed matrix (common for all municipalities, because it depends on the microData)
                TableDataSet nonZero = new TableDataSet();
                TableDataSet nonZeroSize = new TableDataSet();
                nonZero.appendColumn(nonZeroIds, "IDZero");
                int[] dummy0 = {0, 0};
                nonZeroSize.appendColumn(dummy0, "IDZero");
                for (int attribute = 0; attribute < attributesMunicipality.length; attribute++) {
                    int[] nonZeroVector = new int[microDataIds.length];
                    int[] sumNonZero = {0, 0};
                    for (int row = 1; row < microDataIds.length + 1; row++) {
                        if (microDataMatrix.getValueAt(row, attributesMunicipality[attribute]) != 0) {
                            nonZeroVector[sumNonZero[0]] = row;
                            sumNonZero[0]++;
                        }
                    }
                    nonZero.appendColumn(nonZeroVector, attributesMunicipality[attribute]);
                    nonZeroSize.appendColumn(sumNonZero, attributesMunicipality[attribute]);
                }
                nonZero.buildIndex(nonZero.getColumnPosition("IDZero"));
                nonZeroSize.buildIndex(nonZeroSize.getColumnPosition("IDZero"));
                TableDataSet collapsedMicroData = new TableDataSet(); //List of values different than zero, per attribute, from microdata
                collapsedMicroData = nonZero;
                TableDataSet lengthMicroData = new TableDataSet(); //Number of values different than zero, per attribute, from microdata
                lengthMicroData = nonZeroSize;


                //weights: TableDataSet with two columns, the ID of the household from microData and the weights for that municipality
                TableDataSet weights = new TableDataSet();
                weights.appendColumn(microDataIds, "ID");
                double[] dummy = SiloUtil.createArrayWithValue(microDataMatrix.getRowCount(), 1.0);
                weights.appendColumn(dummy, municipalityIDstring); //the column label is the municipality cityID
                weights.buildIndex(weights.getColumnPosition("ID"));
                TableDataSet minWeights = new TableDataSet();
                minWeights.appendColumn(microDataIds, "ID");
                double[] dummy1 = SiloUtil.createArrayWithValue(microDataMatrix.getRowCount(), 1.0);
                minWeights.appendColumn(dummy1, municipalityIDstring);
                minWeights.buildIndex(minWeights.getColumnPosition("ID"));


                //marginalsHousehold: TableDataSet that contains in each column the marginal of a household attribute at the municipality level. Only one "real" row
                TableDataSet marginalsHousehold = new TableDataSet();
                int[] dummyw0 = {municipalityID, 0};
                marginalsHousehold.appendColumn(dummyw0, "ID_city");
                for (int attribute = 0; attribute < attributesHouseholdList.length; attribute++) {
                    float[] dummyw1 = {marginalsMunicipality.getValueAt(marginalsMunicipality.getIndexedRowNumber(municipalityID), attributesHouseholdList[attribute]), 0};
                    marginalsHousehold.appendColumn(dummyw1, attributesHouseholdList[attribute]);
                }
                marginalsHousehold.buildIndex(marginalsHousehold.getColumnPosition("ID_city"));


                //weighted sum and errors: TableDataSet that contains in each column the weighted sum (or error) of a household attribute at the municipality level. Only one row
                TableDataSet errorsHousehold = new TableDataSet();
                int[] dummy00 = {municipalityID, 0};
                int[] dummy01 = {municipalityID, 0};
                errorsHousehold.appendColumn(dummy01, "ID_city");
                for (int attribute = 0; attribute < attributesHouseholdList.length; attribute++) {
                    double[] dummyA2 = {0, 0};
                    double[] dummyB2 = {0, 0};
                    errorsHousehold.appendColumn(dummyB2, attributesHouseholdList[attribute]);
                }
                errorsHousehold.buildIndex(errorsHousehold.getColumnPosition("ID_city"));


                //Calculate the first set of weighted sums and errors, using initial weights equal to 1
                for (int attribute = 0; attribute < attributesHouseholdList.length; attribute++) {
                    int positions = (int) lengthMicroData.getValueAt(1, attributesHouseholdList[attribute]);
                    float weighted_sum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalityIDstring),
                            microDataMatrix.getColumnAsFloat(attributesHouseholdList[attribute]),
                            collapsedMicroData.getColumnAsInt(attributesHouseholdList[attribute]), positions);
                    float error = Math.abs((weighted_sum -
                            marginalsHousehold.getIndexedValueAt(municipalityID, attributesHouseholdList[attribute])) /
                            marginalsHousehold.getIndexedValueAt(municipalityID, attributesHouseholdList[attribute]));
                    errorsHousehold.setIndexedValueAt(municipalityID, attributesHouseholdList[attribute], error);
                }


                //Stopping criteria
                int maxIterations = ResourceUtil.getIntegerProperty(rb, PROPERTIES_MAX_ITERATIONS, 1000);
                double maxError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MAX_ERROR, 0.0001);
                double improvementError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_IMPROVEMENT_ERROR, 0.001);
                double iterationError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_IMPROVEMENT_ITERATIONS, 2);
                double increaseError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_INCREASE_ERROR, 1.05);
                double initialError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_INITIAL_ERROR, 1000);


                //-----------***** IPU procedure *****-------------------------------------------------------------------
                int iteration = 0;
                int finish = 0;
                float factor = 0;
                int position = 0;
                float previousWeight = 1;
                float weightedSum = 0;
                float error = 0;
                float averageErrorIteration = 0;
                float minError = 10000;

                while (iteration <= maxIterations && finish == 0) {

                    averageErrorIteration = 0;

                    //Calculate weights for each attribute at the municipality level
                    for (int attribute = 0; attribute < attributesHouseholdList.length; attribute++) {
                        //update the weights according to the weighted sum and constraint of this attribute and the weights from the previous attribute
                        weightedSum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalityIDstring),
                                microDataMatrix.getColumnAsFloat(attributesHouseholdList[attribute]),
                                collapsedMicroData.getColumnAsInt(attributesHouseholdList[attribute]),
                                (int) lengthMicroData.getValueAt(1, attributesHouseholdList[attribute]));
                        factor = marginalsHousehold.getIndexedValueAt(municipalityID, attributesHouseholdList[attribute]) /
                                weightedSum;
                        for (int row = 0; row < lengthMicroData.getValueAt(1, attributesHouseholdList[attribute]); row++) {
                            position = (int) collapsedMicroData.getIndexedValueAt(nonZeroIds[row], attributesHouseholdList[attribute]); // I changed from microdataIds to nonZeroIds because the code stopped.
                            previousWeight = weights.getValueAt(position, municipalityIDstring);
                            weights.setValueAt(position, municipalityIDstring, factor * previousWeight);
                        }
                    }


                    //update the weighted sums and errors of all household attributes, considering the weights after all the attributes
                    for (int attributes = 0; attributes < attributesHouseholdList.length; attributes++) {
                        weightedSum = SiloUtil.getWeightedSum(weights.getColumnAsDouble(municipalityIDstring),
                                microDataMatrix.getColumnAsFloat(attributesHouseholdList[attributes]),
                                collapsedMicroData.getColumnAsInt(attributesHouseholdList[attributes]),
                                (int) lengthMicroData.getValueAt(1, attributesHouseholdList[attributes]));
                        error = Math.abs(weightedSum -
                                marginalsHousehold.getIndexedValueAt(municipalityID, attributesHouseholdList[attributes])) /
                                marginalsHousehold.getIndexedValueAt(municipalityID, attributesHouseholdList[attributes]);
                        //logger.info("   Error of " + error + " at attribute " + attributesHouseholdList[attributes]);
                        errorsHousehold.setIndexedValueAt(municipalityID, attributesHouseholdList[attributes], error);
                        averageErrorIteration += error;
                    }
                    averageErrorIteration = averageErrorIteration / (attributesHouseholdList.length);


                    //Stopping criteria:
                    if (averageErrorIteration < maxError) {
                        finish = 1;
                        iteration = maxIterations + 1;
                        logger.info("   IPU finished for municipality " + municipalityIDstring + " after " + iteration + " iterations.");
                    } else if ((iteration / iterationError) % 1 == 0) {
                        if (Math.abs((initialError - averageErrorIteration) / initialError) < improvementError) {
                            finish = 1;
                            logger.info("   IPU finished after " + iteration + " iterations because the error does not improve. The minimum average error is: " + minError * 100 + " %.");
                        } else if (averageErrorIteration > minError * increaseError) {
                            finish = 1;
                            logger.info("   IPU finished after " + iteration + " iterations because the error starts increasing. The minimum average error is: " + minError * 100 + " %.");
                        } else {
                            initialError = averageErrorIteration;
                            iteration++;
                        }
                    } else if (iteration == maxIterations) {
                        finish = 1;
                        logger.info("   IPU finished after the total number of iterations. The average error is: " + minError * 100 + " %.");
                    } else {
                        iteration++;

                    }


                    //Check if the error is lower than the minimum (at the last iterations fluctuates around the minimum error)
                    if (averageErrorIteration < minError) {
                        minWeights.setColumnAsFloat(minWeights.getColumnPosition(municipalityIDstring),
                                weights.getColumnAsFloat(municipalityIDstring));
                        minError = averageErrorIteration;
                    }
                    logger.info("       Iteration " + iteration + ". The average error is: " + averageErrorIteration * 100 + " %." );

                }

                //Copy the errors per attribute
                for (int attribute = 0; attribute < attributesHouseholdList.length; attribute++) {
                    errorsMatrix.setValueAt(municipality + 1, attributesHouseholdList[attribute], errorsHousehold.getIndexedValueAt(municipalityID, attributesHouseholdList[attribute]));
                }

                //Write the weights after finishing IPU for each municipality (saved each time over the previous version)
                weightsMatrix.appendColumn(SiloUtil.createArrayWithValue(weightsMatrix.getRowCount(),0), municipalityIDstring);
                for (int i = 0; i < microDataIds.length; i++){
                    float value = minWeights.getIndexedValueAt(microDataIds[i], municipalityIDstring);
                    weightsMatrix.setIndexedValueAt(microDataIds[i], municipalityIDstring, value);
                }
                //weightsMatrix.appendColumn(minWeights.getColumnAsFloat(municipalityIDstring),municipalityIDstring);
                SiloUtil.writeTableDataSet(weightsMatrix, rb.getString(PROPERTIES_WEIGHTS_MATRIX));
                String freqFileName2 = ("microData/interimFiles/errorsIPU.csv");
                SiloUtil.writeTableDataSet(errorsMatrix, freqFileName2);

            } else {
                logger.info("   Municipality " + municipalityID + " is not on the code zone");
            }


        }
        //Write the weights final table
        weightsTable = weightsMatrix;
        weightsTable.buildIndex(weightsTable.getColumnPosition("ID"));

        logger.info("   IPU finished");
    }



    private void readIPU(){
        //Read entry data for household selection
        logger.info("   Reading the weights matrix");
        weightsTable = SiloUtil.readCSVfile2(rb.getString(PROPERTIES_WEIGHTS_MATRIX));
        weightsTable.buildIndex(weightsTable.getColumnPosition("ID"));

        logger.info("   Finishing reading the results from the IPU");
    }


    private void generateJobs(){
        //Generate jobs file. The worker ID will be assigned later on the process "assignJobs"

        logger.info("   Starting to generate jobs");
        String[] gender = {"M_", "F_"};
        JobDataManager jobData = dataContainer.getJobDataManager();

        //For each municipality
        for (int municipality = 0; municipality < cityID.length; municipality++) {
            logger.info("   Municipality " + cityID[municipality] + ". Starting to generate jobs.");

            //-----------***** Data preparation *****-------------------------------------------------------------------
            //Create local variables to avoid accessing to the same variable on the parallel processing
            int municipalityID = cityID[municipality];
            TableDataSet rasterCellsMatrix = cellsMatrix;
            rasterCellsMatrix.buildIndex(rasterCellsMatrix.getColumnPosition("ID_cell"));


            //obtain the raster cells of the municipality and their weight within the municipality
            int[] tazInCity = cityTAZ.get(municipalityID);


            //generate jobs
            for (String type : jobStringTypes) {
                for (String gen : gender) {
                    String jobTypeGender = gen + type;
                    int totalJobs = 0;
                    //if (totalJobs > 0.1) {
                    //Obtain the number of jobs of that type in each TAZ of the municipality
                    for (int i = 0; i < tazInCity.length; i++) {
                        int jobsInTaz = (int) rasterCellsMatrix.getIndexedValueAt(tazInCity[i], jobTypeGender);
                        //Create already allocated jobs to TAZs (with replacement)
                        for (int job = 0; job < jobsInTaz; job++) {
                            int id = jobData.getNextJobId();
                            jobData.addJob(JobUtils.getFactory().createJob(id, tazInCity[i], null, -1, type));
                        }
                }
                    //}
                }
            }
        }
    }


    private void assignJobs(){
        //Method to allocate workers at workplaces
        //todo. Things to consider:
        //If there are no more workplaces of the specific job type, the worker is sent outside the area (workplace = -2; distance = 1000 km)
        //Workers that also attend school are considered only as workers (educational place is not selected for them)

        logger.info("   Starting to assign jobs");

        //Calculate distance impedance
        alphaJob = ResourceUtil.getDoubleProperty(rb,PROPERTIES_JOB_ALPHA);
        gammaJob = ResourceUtil.getDoubleProperty(rb,PROPERTIES_JOB_GAMMA);
        distanceImpedance = new Matrix(distanceMatrix.getRowCount(), distanceMatrix.getColumnCount());
        for (int i = 1; i <= distanceMatrix.getRowCount(); i ++){
            for (int j = 1; j <= distanceMatrix.getColumnCount(); j++){
                distanceImpedance.setValueAt(i,j,(float) Math.exp(alphaJob * Math.exp(distanceMatrix.getValueAt(i,j) * gammaJob)));
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
        Collection<Person> persons = dataContainer.getHouseholdDataManager().getPersons();
        ArrayList<Person> workerArrayList = new ArrayList<>();
        for (Person person: persons ){
            if (person.getOccupation() == Occupation.EMPLOYED){
                workerArrayList.add(person);
            }
        }
        //Randomize the order of the worker array list
        Collections.shuffle(workerArrayList);


        //Start the selection of the jobs in random order to avoid geographical bias
        logger.info("   Started assigning workplaces");
        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        JobDataManager jobData = dataContainer.getJobDataManager();
        int assignedJobs = 0;
        for (Person pp : workerArrayList){

            //Select the zones with vacant jobs for that person, given the job type
            int selectedJobType = jobTypeByWorker.get(pp) - 1; //1 Agr, 2 Ind; 3 Srv

            int[] keys = idZonesVacantJobsByType.get(selectedJobType);
            int lengthKeys = numberZonesByType.get(selectedJobType);
            // if there are still TAZ with vacant jobs in the region, select one of them. If not, assign them outside the area
            if (lengthKeys > 0) {

                //Select the workplace location (TAZ) for that person given his/her job type
                Household hh = pp.getHousehold();
                int origin = realEstate.getDwelling(hh.getDwellingId()).getZoneId();
                int[] workplace = selectWorkplace(origin, numberVacantJobsByZoneByType, keys, lengthKeys,
                        distanceImpedance);

                //Assign last vacant jobID from the TAZ
                int jobID = idVacantJobsByZoneType.get(workplace[0])[numberVacantJobsByZoneByType.get(workplace[0]) - 1];

                //Assign values to job and person
                jobData.getJobFromId(jobID).setWorkerID(pp.getId());
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
                        while (w < jobStringTypes.length & selectedJobType > jobIntTypes.get(jobStringTypes[w])) {
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

        int count = 0;

        //Calculate distance impedance for students
        double alphaUniversity = ResourceUtil.getDoubleProperty(rb, PROPERTIES_UNIVERSITY_ALPHA);
        double gammaUniversity = ResourceUtil.getDoubleProperty(rb, PROPERTIES_UNIVERSITY_GAMMA);
        Matrix universityDistanceImpedance = new Matrix(distanceMatrix.getRowCount(), distanceMatrix.getColumnCount());
        Matrix schoolDistanceImpedance = new Matrix(distanceMatrix.getRowCount(), distanceMatrix.getColumnCount());
        for (int i = 1; i <= distanceMatrix.getRowCount(); i++) {
            for (int j = 1; j <= distanceMatrix.getColumnCount(); j++) {
                universityDistanceImpedance.setValueAt(i, j, (float) Math.exp(alphaUniversity * Math.exp(distanceMatrix.getValueAt(i, j) * gammaUniversity)));
                schoolDistanceImpedance.setValueAt(i, j, distanceMatrix.getValueAt(i, j));
            }
        }


        //Identify vacant schools by zone and type
        identifyVacantSchoolsByZoneByType();


        /*//For validation - obtain the trip length distribution
        Frequency travelSecondary = new Frequency();
        Frequency travelUniversity = new Frequency();
        Frequency travelPrimary = new Frequency();
        validationCommutersFlow(); //Generates the validation tabledatasets
        int[] flow = SiloUtil.createArrayWithValue(odMunicipalityFlow.getRowCount(),0);
        odMunicipalityFlow.appendColumn(flow,Integer.toString(count));
*/

        //Produce one array list with students' ID
        Collection<Person> persons = dataContainer.getHouseholdDataManager().getPersons();
        ArrayList<Person> studentArrayList = new ArrayList<>();
        int[] studentsByType2 = new int[schoolTypes.length];
        for (Person person : persons) {
            if (((PersonTak) person).getSchoolType() > 0) { //They are studying
                studentArrayList.add(person);
                studentsByType2[((PersonTak) person).getSchoolType() - 1] = studentsByType2[((PersonTak) person).getSchoolType() - 1] + 1;
            }
        }
        //Randomize the order of the students
        Collections.shuffle(studentArrayList);


        //Start the selection of schools in random order to avoid geographical bias
        logger.info("   Started assigning schools");
        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        int assignedSchools = 0;
        int[] studentsOutside = new int[schoolTypes.length];
        int[] studentsByType = new int[schoolTypes.length];
        for (Person pp : studentArrayList) {

            //Select the zones with vacant schools for that person, given the school type
            int schoolType = ((PersonTak) pp).getSchoolType();
            studentsByType[schoolType - 1] = studentsByType[schoolType - 1] + 1;
            int[] keys = idZonesVacantSchoolsByType.get(schoolType);
            int lengthKeys = numberZonesWithVacantSchoolsByType.get(schoolType);
            if (lengthKeys > 0) {//if there are still TAZ with school capacity in the region, select one of them. If not, assign them outside the area

                //Select the school location (which raster cell) for that person given his/her job type
                int[] schoolPlace = new int[2];
                Household hh = pp.getHousehold();
                int origin = realEstate.getDwelling(hh.getDwellingId()).getZoneId();
                if (schoolType == 3) {
                    schoolPlace = selectWorkplace(origin, numberVacantSchoolsByZoneByType,
                            keys, lengthKeys, universityDistanceImpedance);
                    //travelUniversity.addValue((int) distanceMatrix.getValueAt(origin, schoolPlace[0] / 100));
                } else {
                    schoolPlace = selectClosestSchool(origin, numberVacantSchoolsByZoneByType,
                            keys, lengthKeys, schoolDistanceImpedance);
                    if (schoolType == 1){
                        //travelPrimary.addValue((int) distanceMatrix.getValueAt(origin,schoolPlace[0] / 100));
                    } else if (schoolType == 2){
                        //travelSecondary.addValue((int) distanceMatrix.getValueAt(origin, schoolPlace[0] / 100));
                    }
                }

                //Assign values to job and person
                ((PersonTak) pp).setSchoolPlace(schoolPlace[0] / 100);
                //pp.setTravelTime(distanceMatrix.getValueAt(pp.getZone(), pp.getSchoolPlace()));

               /* //For validation OD TableDataSet
                int homeMun = (int) cellsMatrix.getIndexedValueAt(origin, "smallID");
                int workMun = (int) cellsMatrix.getIndexedValueAt(pp.getSchoolPlace(), "smallID");
                int odPair = homeMun * 1000 + workMun;
                odMunicipalityFlow.setIndexedValueAt(odPair,Integer.toString(count),odMunicipalityFlow.getIndexedValueAt(odPair,Integer.toString(count))+ 1);
*/
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
                ((PersonTak) pp).setSchoolPlace(-2); //they attend one school out of the area
                studentsOutside[schoolType - 1] = studentsOutside[schoolType - 1] + 1;
            }
        }


        //For validation - trip length distribution
        /*checkTripLengthDistribution(travelPrimary, 0, 0, "microData/interimFiles/tripLengthDistributionPrimary.csv", 1);
        checkTripLengthDistribution(travelSecondary, 0, 0, "microData/interimFiles/tripLengthDistributionSecondary.csv", 1); //Trip length frequency distribution
        checkTripLengthDistribution(travelUniversity, alphaJob, gammaJob, "microData/interimFiles/tripLengthDistributionUniversity.csv", 1);*/
        //SiloUtil.writeTableDataSet(odMunicipalityFlow,"microData/interimFiles/odMunicipalityFlow.csv");
        for (int i = 0; i < schoolTypes.length; i++) {
            logger.info("  School type: " + schoolTypes[i] + ". " + studentsOutside[schoolTypes[i] - 1] + " students out of " + studentsByType[schoolTypes[i] - 1] + " study outside the area");
        }
    }


    private void readSyntheticPopulation(){
        //Read the synthetic population

        logger.info("   Starting to read the synthetic population");
        String fileEnding = "_" + Properties.get().main.baseYear + ".csv";
        TableDataSet households = SiloUtil.readCSVfile(rb.getString(PROPERTIES_HOUSEHOLD_SYN_POP) + fileEnding);
        TableDataSet persons = SiloUtil.readCSVfile(rb.getString(PROPERTIES_PERSON_SYN_POP) + fileEnding);
        TableDataSet dwellings = SiloUtil.readCSVfile(rb.getString(PROPERTIES_DWELLING_SYN_POP) + fileEnding);
        TableDataSet jobs = SiloUtil.readCSVfile(rb.getString(PROPERTIES_JOB_SYN_POP) + fileEnding);
        schoolLevelTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SCHOOL_DESCRIPTION));


        //Generate the households, dwellings and persons
        HouseholdDataManager householdData = dataContainer.getHouseholdDataManager();
        //Generate the households, dwellings and persons
        logger.info("   Starting to generate households");
        HouseholdFactoryTak householdFactory = new HouseholdFactoryTak();
        int aux = 1;
        for (int i = 1; i <= households.getRowCount(); i++){
            Household hh = householdFactory.createHousehold((int) households.getValueAt(i, "id"), (int) households.getValueAt(i, "dwelling"),
                    (int) households.getValueAt(i, "autos"));
            householdData.addHousehold(hh);

            for (int j = 1; j <= hh.getHhSize(); j++) {
                PersonFactoryTak factory = new PersonFactoryTak();
                int hhID = (int) persons.getValueAt(aux, "hhid");
                PersonTak pp = factory.createPerson((int) persons.getValueAt(aux, "id"),
                        (int) persons.getValueAt(aux, "age"), Gender.valueOf((int) persons.getValueAt(aux, "gender")),
                        Occupation.valueOf((int) persons.getValueAt(aux, "occupation")), null,  (int) persons.getValueAt(aux, "workplace"),
                        (int) persons.getValueAt(aux, "income"));
                householdData.addPerson(pp);
                householdData.addPersonToHousehold(pp, householdData.getHouseholdFromId(hhID));
                if (persons.getStringValueAt(aux, "relationShip").equals("single")) pp.setRole(PersonRole.SINGLE);
                else if (persons.getStringValueAt(aux, "relationShip").equals("married")) pp.setRole(PersonRole.MARRIED);
                else pp.setRole(PersonRole.CHILD);
                if (persons.getValueAt(aux,"driversLicense") == 1) pp.setDriverLicense(true);
                pp.setSchoolType((int) persons.getValueAt(aux,"school"));
                pp.setWorkplace((int) persons.getValueAt(aux,"workplace"));
                aux++;
            }
        }
        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        for (int i = 1; i <= dwellings.getRowCount(); i++){
            Dwelling dd = DwellingUtils.getFactory().createDwelling((int)dwellings.getValueAt(i,"id"),
                    (int) dwellings.getValueAt(i,"zone"),null,
                    (int)dwellings.getValueAt(i,"hhID"), DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus,(int)dwellings.getValueAt(i,"bedrooms"),
                    (int)dwellings.getValueAt(i,"quality"),(int)dwellings.getValueAt(i,"monthlyCost") ,(int)dwellings.getValueAt(i,"yearBuilt"));
            realEstate.addDwelling(dd);
            realEstate.addDwelling(dd);
            dd.setFloorSpace((int)dwellings.getValueAt(i,"floor"));
            dd.setUsage(DwellingUsage.valueOf((int)dwellings.getValueAt(i,"usage")));
        }
        logger.info("   Generated households, persons and dwellings");


        //Generate the jobs
        JobDataManager jobData = dataContainer.getJobDataManager();
        for (int i = 1; i <= jobs.getRowCount(); i++) {
            int zoneId = (int) jobs.getValueAt(i, "zone");
            jobData.addJob(JobUtils.getFactory().createJob((int) jobs.getValueAt(i, "id"), zoneId, null,
                    (int) jobs.getValueAt(i, "personId"), jobs.getStringValueAt(i, "type")));
        }
        logger.info("   Generated jobs");
    }


    private void validationCommutersFlow(){

        //For checking
        //OD matrix from the commuters data, for validation
        TableDataSet selectedMunicipalities = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SELECTED_MUNICIPALITIES_LIST)); //TableDataSet with all municipalities
        selectedMunicipalities.buildIndex(selectedMunicipalities.getColumnPosition("ID_city"));
        int[] allCounties = selectedMunicipalities.getColumnAsInt("smallCenter");
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

        int[] citySmallID = selectedMunicipalities.getColumnAsInt("smallID");
        for (int i = 0; i < cityID.length; i++){
            ini = (int) selectedMunicipalities.getIndexedValueAt(cityID[i],"smallCenter");
            for (int j = 0; j < cityID.length; j++){
                end = (int) selectedMunicipalities.getIndexedValueAt(cityID[j],"smallCenter");
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
        int previousHouseholds = 0;
        int previousPersons = 0;


        //Define income distribution
        double incomeShape = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCOME_GAMMA_SHAPE);
        double incomeRate = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCOME_GAMMA_RATE);
        double[] incomeProbability = ResourceUtil.getDoubleArray(rb,PROPERTIES_INCOME_GAMMA_PROBABILITY);
        GammaDistributionImpl gammaDist = new GammaDistributionImpl(incomeShape, 1/incomeRate);


        //Create a map to store the household IDs by municipality
        HashMap<Integer, HashMap<Integer, Integer>> householdByMunicipality = new HashMap<>();

        generateCountersForValidation();

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        HouseholdDataManager householdData = dataContainer.getHouseholdDataManager();
        HouseholdFactoryTak householdFactory = new HouseholdFactoryTak();

        regionsforFrequencyMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_ATRIBUTES_ZONAL_DATA));
        regionsforFrequencyMatrix.buildIndex(regionsforFrequencyMatrix.getColumnPosition("V1"));
        householdsForFrequencyMatrix = new HashMap<>();
        for (int i = 1; i <= microDataDwelling.getRowCount();i++){
            int v2Zone = (int) microDataDwelling.getValueAt(i,"PtResCode");
            int ddID = (int) microDataDwelling.getValueAt(i,"id");
            if (householdsForFrequencyMatrix.containsKey(v2Zone)) {
                householdsForFrequencyMatrix.get(v2Zone).put(ddID, 1);
            } else {
                HashMap<Integer, Integer> map = new HashMap<>();
                map.put(ddID, 1);
                householdsForFrequencyMatrix.put(v2Zone, map);
            }
        }

        //Selection of households, persons, jobs and dwellings per municipality
        for (int municipality = 0; municipality < cityID.length; municipality++){
            logger.info("   Municipality " + cityID[municipality] + ". Starting to generate households.");

            //-----------***** Data preparation *****-------------------------------------------------------------------
            //Create local variables to avoid accessing to the same variable on the parallel processing
            int municipalityID = cityID[municipality];
            int v2zone = (int) regionsforFrequencyMatrix.getIndexedValueAt(municipalityID, "V2");
            if (householdsForFrequencyMatrix.containsKey(v2zone)) {
                String[] attributesHouseholdIPU = attributesMunicipality;
                TableDataSet rasterCellsMatrix = cellsMatrix;
                TableDataSet microHouseholds = microDataHousehold;
                TableDataSet microPersons = microDataPerson;
                TableDataSet microDwellings = microDataDwelling;
                microHouseholds.buildIndex(microHouseholds.getColumnPosition("id"));
                microDwellings.buildIndex(microDwellings.getColumnPosition("id"));
                int totalHouseholds = (int) marginalsMunicipality.getIndexedValueAt(municipalityID,"hhTotal");
                int[] agePerson = ageBracketsPerson;
                int[] levelEdu = new int[4];
                double[] probEdu = new double[4];
                for (int i = 0; i < levelEdu.length; i++){
                    probEdu[i] = marginalsMunicipality.getIndexedValueAt(municipalityID,"Ed_" + i);
                }
                //Probability of floor size for vacant dwellings
                double [] sizeDistribution = new double[sizeBracketsDwelling.length];
                for (int row = 0; row < sizeBracketsDwelling.length; row++){
                    String name = "HA_LT_" + sizeBracketsDwelling[row] + "sqm";
                    sizeDistribution[row] = marginalsMunicipality.getIndexedValueAt(municipalityID, name);
                }
                //Probability for year and building size for vacant dwellings
                double[] yearDistribution = new double[yearBracketsDwelling.length];
                for (int row = 0; row < yearBracketsDwelling.length; row++){
                    String name = "HY_" + yearBracketsDwelling[row];
                    yearDistribution[row] = marginalsMunicipality.getIndexedValueAt(municipalityID, name) / totalHouseholds;
                }
                //Average price per sqm of the zone according to building type
                float[] averagePriceDistribution = new float[typeBracketsDwelling.length];
                for (int row = 0; row < typeBracketsDwelling.length; row++){
                    String name = "HPrice_" + typeBracketsDwelling[row];
                    yearDistribution[row] = marginalsMunicipality.getIndexedValueAt(municipalityID, name);
                }

                HashMap<Integer, Integer> hhs = householdsForFrequencyMatrix.get(v2zone);
                int[] hhFromV2 = hhs.keySet().stream().mapToInt(Integer::intValue).toArray();
                HashMap<Integer, Integer> generatedHouseholds = new HashMap<>();


                //obtain the raster cells of the municipality and their weight within the municipality
                int[] tazInCity = cityTAZ.get(municipalityID);
                double[] probTaz = new double[tazInCity.length];
                double tazRemaining = 0;
                for (int i = 0; i < tazInCity.length; i++){
                    probTaz[i] = rasterCellsMatrix.getIndexedValueAt(tazInCity[i],"Population");
                    tazRemaining = tazRemaining + probTaz[i];
                }


                double hhRemaining = 0;
                HashMap<Integer, Double> prob = new HashMap<>();
                for (int row = 0; row < hhFromV2.length; row++){
                    double value = weightsTable.getIndexedValueAt(hhFromV2[row], Integer.toString(municipalityID));
                    hhRemaining = hhRemaining + value;
                    prob.put(hhFromV2[row],value);
                }


                //marginals for the municipality
                int hhPersons = 0;
                int hhTotal = 0;
                int quartersTotal = 0;
                int id = 0;


                //for all the households that are inside the municipality (we will match perfectly the number of households. The total population will vary compared to the marginals.)
                for (int row = 0; row < totalHouseholds; row++) {

                    //select the household to copy from the micro data(with replacement)
                    double[] probability = prob.values().stream().mapToDouble(Double::doubleValue).toArray();
                    int[] hhIds = prob.keySet().stream().mapToInt(Integer::intValue).toArray();
                    int selectedHh = select(probability, hhIds, hhRemaining)[0];
                    if (prob.get(selectedHh) > 1){
                        prob.put(selectedHh, prob.get(selectedHh) - 1);
                        hhRemaining = hhRemaining - 1;
                    } else {
                        hhRemaining = hhRemaining - prob.get(selectedHh);
                        prob.remove(selectedHh);
                    }


                    //Select the taz to allocate the household (without replacement)
                    int[] recordsCell = select(probTaz, tazInCity, tazRemaining);
                    int selectedTAZ = recordsCell[0];


                    //copy the private household characteristics
                    int householdSize = (int) microHouseholds.getIndexedValueAt(selectedHh, "HHsize");
                    int householdCars = Math.min((int) microHouseholds.getIndexedValueAt(selectedHh, "N_Car"),3);
                    id = householdData.getNextHouseholdId();
                    int newDdId = realEstate.getNextDwellingId();
                    Household household = householdFactory.createHousehold(id, newDdId, householdCars); //(int id, int dwellingID, int homeZone, int hhSize, int autos)
                    householdData.addHousehold(household);
                    hhTotal++;




                    //copy the household members characteristics
                    PersonFactoryTak factory = new PersonFactoryTak();
                    for (int rowPerson = 0; rowPerson < householdSize; rowPerson++) {
                        int idPerson = householdData.getNextPersonId();
                        int personCounter = (int) microHouseholds.getIndexedValueAt(selectedHh, "firstPerson") + rowPerson;
                        int age = (int) microPersons.getValueAt(personCounter, "age");
                        Gender gender = Gender.valueOf((int) microDataPerson.getValueAt(personCounter, "gender"));
                        Occupation occupation = Occupation.UNEMPLOYED;
                        int jobType = 1;
                        if ((int) microDataPerson.getValueAt(personCounter, "occupation") == 1) {
                            occupation = Occupation.EMPLOYED;
                            if ((int) microDataPerson.getValueAt(personCounter, "jobType") == 1){
                                jobType = 1;
                            } else if ((int) microDataPerson.getValueAt(personCounter, "jobType") == 2){
                                jobType = 2;
                            } else {
                                jobType = 3;
                            }
                        }
                        int income = 0;
                        int education = 0;
                        if (age > 15){
                            education = SiloUtil.select(probEdu, levelEdu);
                            try {
                                income = (int) translateIncome((int) Math.random()*10,incomeProbability, gammaDist)
                                        * 12;  //convert monthly income to yearly income
                            } catch (MathException e) {
                                e.printStackTrace();
                            }
                        }
                        PersonTak pers = factory.createPerson(idPerson, age, gender, occupation, null, 0, income); //(int id, int hhid, int age, int gender, Race race, int occupation, int workplace, int income)
                        householdData.addPerson(pers);
                        householdData.addPersonToHousehold(pers, household);
                        jobTypeByWorker.put(pers, jobType);
                        PersonRole role = PersonRole.CHILD; //default value = child
                        if ((int)microPersons.getValueAt(personCounter, "personRole") == 1) { //the person is single
                           role = PersonRole.SINGLE;
                        } else if ((int)microPersons.getValueAt(personCounter, "personRole") == 2) { // the person is married
                            role = PersonRole.MARRIED;
                        }
                        pers.setRole(role);
                        boolean license = false;
                        if (microPersons.getValueAt(personCounter, "DrivLicense") == 1){
                            license = true;
                        }
                        pers.setDriverLicense(license);
                        pers.setSchoolType((int) microPersons.getValueAt(personCounter, "school"));
                        hhPersons++;
                        //counterMunicipality = updateCountersPerson(pers, counterMunicipality, municipality,ageBracketsPerson);
                    }
                    //counterMunicipality = updateCountersHousehold(household, counterMunicipality, municipality);

                    //Copy the dwelling of that household
                    int bedRooms = 1; //Not on the micro data
                    int year = select(yearDistribution, yearBracketsDwelling)[0]; //the category
                    int floorSpace = select(sizeDistribution, sizeBracketsDwelling)[0];
                    int usage = (int) microDwellings.getIndexedValueAt(selectedHh, "H_");
                    int buildingSize = (int) microDwellings.getIndexedValueAt(selectedHh, "ddT_");
                    DefaultDwellingTypes.DefaultDwellingTypeImpl ddType = translateDwellingType(buildingSize);
                    int quality = 1; //depend on year built and type of heating
                    year = selectDwellingYear(year); //convert from year class to actual 4-digit year
                    int price = estimatePrice(ddType, floorSpace);
                    Dwelling dwell = DwellingUtils.getFactory().createDwelling(newDdId, selectedTAZ, null, id, ddType , bedRooms, quality, price, year);
                    realEstate.addDwelling(dwell);
                    dwell.setFloorSpace(floorSpace);
                    dwell.setUsage(DwellingUsage.valueOf(usage));
                    generatedHouseholds.put(dwell.getId(), 1);
                }
                int households = householdData.getHighestHouseholdIdInUse() - previousHouseholds;
                int persons = householdData.getHighestPersonIdInUse() - previousPersons;
                previousHouseholds = householdData.getHighestHouseholdIdInUse();
                previousPersons = householdData.getHighestPersonIdInUse();


                //Consider if I need to add also the errors from other attributes. They must be at the marginals file, or one extra file
                //For county level they should be calculated on a next step, outside this loop.
                float averageError = 0f;
                /*for (int attribute = 1; attribute < attributesHouseholdIPU.length; attribute++){
                    float error = Math.abs((counterMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]) -
                            marginalsMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute])) /
                            marginalsMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]));
                    errorMunicipality.setIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute],error);
                    averageError = averageError + error;
                }
                averageError = averageError / (1 + attributesHouseholdIPU.length) * 100;*/
                householdByMunicipality.put(municipalityID, generatedHouseholds);

                logger.info("   Municipality " + municipalityID + ". Generated " + hhPersons + " persons in " + hhTotal + " households. Average error of " + averageError + " %.");

            } else {
                logger.info("   Municipality " + municipalityID + " has no TAZ assigned.");
            }
        }
        int households = householdData.getHighestHouseholdIdInUse();
        int persons = householdData.getHighestPersonIdInUse();
        logger.info("   Finished generating households and persons. A population of " + persons + " persons in " + households + " households was generated.");


        //Vacant dwellings--------------------------------------------
        //They have similar characteristics to the dwellings that are occupied (assume that there is no difference between the occupied and vacant dwellings in terms of quality)
        int vacantCounter = 0;
        for (int municipality = 0; municipality < cityID.length; municipality++) {

            logger.info("   Municipality " + cityID[municipality] + ". Starting to generate vacant dwellings.");
            int municipalityID = cityID[municipality];
            int vacantDwellings = (int) marginalsMunicipality.getIndexedValueAt(cityID[municipality], "dd_Vacant");
            TableDataSet rasterCellsMatrix = cellsMatrix;
            int[] occupiedDwellings = householdByMunicipality.get(municipalityID).keySet().stream().mapToInt(Integer::intValue).toArray();

            //obtain the raster cells of the municipality and their weight within the municipality
            int[] tazInCity = cityTAZ.get(municipalityID);
            double[] probTaz = new double[tazInCity.length];
            double sumProbTaz = 0;
            for (int i = 0; i < tazInCity.length; i++){
                probTaz[i] = rasterCellsMatrix.getIndexedValueAt(tazInCity[i],"Population");
                sumProbTaz = sumProbTaz + probTaz[i];
            }

            //Select the vacant dwelling and copy characteristics
            for (int row = 0; row < vacantDwellings; row++) {

                //Allocation
                int ddTAZ = select(probTaz, tazInCity, sumProbTaz)[0]; // I allocate vacant dwellings using the same proportion as occupied dwellings.
                //Select one occupied dwelling to copy
                int dd = selectEqualProbability(occupiedDwellings)[0];
                //Copy characteristics
                int newDdId = realEstate.getNextDwellingId();
                Dwelling ddToCopy = realEstate.getDwelling(dd);
                int bedRooms = ddToCopy.getBedrooms();
                int price = ddToCopy.getPrice();
                int quality = ddToCopy.getQuality();
                int year = ddToCopy.getYearBuilt();
                int floorSpaceDwelling = ddToCopy.getFloorSpace();
                DwellingType dwellingType = ddToCopy.getType();
                Dwelling dwell = DwellingUtils.getFactory().createDwelling(newDdId, ddTAZ, null, -1, dwellingType, bedRooms, quality, price, year);
                dwell.setUsage(DwellingUsage.VACANT); //vacant dwelling = 3; and hhID is equal to -1
                dwell.setFloorSpace(floorSpaceDwelling);
                vacantCounter++;
            }
            logger.info("   The number of vacant dwellings is: " + vacantCounter);
        }
        //Write the files for all municipalities
        String name = ("microData/interimFiles/totalsSynPop.csv");
        SiloUtil.writeTableDataSet(counterMunicipality,name);
        String name1 = ("microData/interimFiles/errorsSynPop.csv");
        SiloUtil.writeTableDataSet(errorMunicipality,name1);

    }



    private DefaultDwellingTypes.DefaultDwellingTypeImpl translateDwellingType (int pumsDdType) {
        // translate micro census dwelling types into 6 MetCouncil Dwelling Types

        DefaultDwellingTypes.DefaultDwellingTypeImpl type;
        if (pumsDdType == 1) type = DefaultDwellingTypes.DefaultDwellingTypeImpl.SFD; //DETACHED
        else if (pumsDdType == 2) type = DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234; //apartment
        else if (pumsDdType == 3) type = DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus;//multiapartment
        else {
            //logger.error("Unknown dwelling type " + pumsDdType + " found in PUMS data.");
            type = null;
        }
        return type;
    }


    private static double translateIncome (int incomeClass, double[] incomeThresholds, GammaDistributionImpl q) throws MathException, MathException {
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
            Random rnd = new Random();
            double cummulativeProb = rnd.nextDouble()*(high - low) + low;
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
            if (personEducation == educationLevel.getValueAt(row,"fdz_mz_sufCode")) {
                finish =1;
                education = (int) educationLevel.getValueAt(row,"SynPopCode");
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
            if (personEducation == schoolType.getValueAt(row,"fdz_mz_sufCode")) {
                finish =1;
                education = (int) schoolType.getValueAt(row,"SynPopCode");
            }
            else {
                row++;
            }
        }
        //if (education == 0){education = 1;}
        return education;
    }


    private static int obtainDriverLicense (int gender, int age, TableDataSet prob){
        //assign if the person holds a driver license based on the probabilities obtained from MiD data
        int license = 0;
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
            Random rn = new Random();
            if (rn.nextDouble() * 100 < threshold) {
                license = 1;
            }
        } //if they are younger than 18, they don't hold driver license
        return license;
    }


    private static int selectFloorSpace(float[] vacantFloor, int[] sizeBracketsDwelling){
        //provide the size of the building
        int floorSpaceDwelling = 0;
        int floorSpace = SiloUtil.select(vacantFloor);
        Random r = new Random();
        if (floorSpace == 0){
            floorSpaceDwelling = (int) (30 + SiloUtil.getRandomNumberAsFloat() * 20);
        } else if (floorSpace == sizeBracketsDwelling.length - 1) {
            floorSpaceDwelling = (int) (120 + SiloUtil.getRandomNumberAsFloat() * 200);
        } else {
            floorSpaceDwelling = r.nextInt(sizeBracketsDwelling[floorSpace]-sizeBracketsDwelling[floorSpace-1]) +
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


    private static int guessQualityJP(int yearBuilt, int numberofQualityLevels){
        //guess quality of dwelling based on construction year and heating characteristics.
        //kitchen and bathroom quality are not coded on the micro data
        int quality = numberofQualityLevels;
        if (yearBuilt > 0){
            //Ages - 1: before 1919, 2: 1919-1948, 3: 1949-1978, 4: 1979 - 1986; 5: 1987 - 1990; 6: 1991 - 2000; 7: 2001 - 2004; 8: 2005 - 2008, 9: 2009 or later,
            float[] deteriorationProbability = {0.9f, 0.6f, 0.3f, 0.08f, 0.04f, 0.04f};
            float prob = deteriorationProbability[yearBuilt - 1];
            //attempt to drop quality by age two times (to get some spreading of quality levels)
            quality = quality - SiloUtil.select(new double[]{1 - prob ,prob});
            quality = quality - SiloUtil.select(new double[]{1 - prob, prob});
        }
        quality = Math.max(quality, 1);      // ensure that quality never drops below 1
        quality = Math.min(quality, numberofQualityLevels);      // ensure that quality never excess the number of quality levels
        return quality;
    }

    private void identifyVacantJobsByZoneType() {
        // populate HashMap with Jobs by zone and job type
        // adapted from SyntheticPopUS

        logger.info("  Identifying vacant jobs by zone");
        Collection<Job> jobs = dataContainer.getJobDataManager().getJobs();

        idVacantJobsByZoneType = new HashMap<>();
        numberVacantJobsByType = new HashMap<>();
        idZonesVacantJobsByType = new HashMap<>();
        numberZonesByType = new HashMap<>();
        numberVacantJobsByZoneByType = new HashMap<>();
        jobIntTypes = new HashMap<>();
        for (int i = 0; i < jobStringTypes.length; i++) {
            jobIntTypes.put(jobStringTypes[i], i);
        }
        int[] cellsID = cellsMatrix.getColumnAsInt("ID_cell");

        //create the counter hashmaps
        for (int i = 0; i < jobStringTypes.length; i++){
            int type = jobIntTypes.get(jobStringTypes[i]);
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
                int typeZone = type + jj.getZoneId() * 100;
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
        for (int i = 0; i < jobStringTypes.length; i++){
            int type = jobIntTypes.get(jobStringTypes[i]);
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
                int typeZone = jobIntTypes.get(jj.getType()) + jj.getZoneId() * 100;
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
        schoolTypes = ResourceUtil.getIntegerArray(rb, PROPERTIES_SCHOOL_TYPES_DE);
        int[] cellsID = cellsMatrix.getColumnAsInt("ID_cell");


        //create the counter hashmaps
        for (int col = 0; col < schoolTypes.length; col++){
            int i = schoolTypes[col];
            for (int j : cellsID){
                int count = (int) cellsMatrix.getIndexedValueAt(j,"school" + i);
                numberVacantSchoolsByZoneByType.put(i + j * 100, count);
                if (count > 0) {
                    if (idZonesVacantSchoolsByType.containsKey(i)){
                        numberZonesWithVacantSchoolsByType.put(i,numberZonesWithVacantSchoolsByType.get(i) + 1);
                        int[] zones = idZonesVacantSchoolsByType.get(i);
                        zones = SiloUtil.expandArrayByOneElement(zones, i + j * 100);
                        idZonesVacantSchoolsByType.put(i, zones);
                        schoolCapacityByType.put(i, schoolCapacityByType.get(i) + count);
                    } else {
                        numberZonesWithVacantSchoolsByType.put(i, 1);
                        int[] zones = {i + j * 100};
                        idZonesVacantSchoolsByType.put(i, zones);
                        schoolCapacityByType.put(i,count);
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

                return 0;
            }
        }
    }


    public static int[] select (double[] probabilities, int[] id) {
        // select item based on probabilities (for zero-based float array)
        double sumProb = 0;
        int[] results = new int[2];
        for (double val: probabilities) sumProb += val;
        Random rand = new Random();
        double selPos = sumProb * rand.nextDouble();
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
        Random rand = new Random();
        double selPos = sumProb * rand.nextDouble();
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
        Random rand = new Random();
        double selPos = sumProb * rand.nextDouble();
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

    public static int[] selectEqualProbability(int[] id) {
        // select item based on equal probability for all elements

        int[] results = new int[2];
        double step = 1/(id.length);
        Random rand = new Random();
        double sel = rand.nextDouble();
        double prob = 0;
        for (int i = 0; i < id.length; i++){
            prob = prob + step;
            if (prob > sel){
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[id.length - 1];
        results[1] = id.length - 1;

        return results;
    }

    public static int[] removeLastElementFromZeroBasedArray(int[] array) {
        // remove elementIndex'th element from array

        int[] reduced = new int[array.length - 1];
        // remove last element
        System.arraycopy(array, 0, reduced, 0, reduced.length);

        return reduced;
    }

    public static int[] subsetFromZeroBasedArray(int[] array, int newLength) {
        // remove elementIndex'th element from array

        int[] reduced = new int[newLength];
        // remove elements with zero
        System.arraycopy(array, 0, reduced, 0, reduced.length);

        return reduced;
    }

    public static String[] removeLastElementFromZeroBasedArray(String[] array) {
        // remove elementIndex'th element from array

        String[] reduced = new String[array.length - 1];
        // remove last element
        System.arraycopy(array, 0, reduced, 0, reduced.length);

        return reduced;
    }


    public static int[] selectEqualProbability (int[] id, int length) {
        // select item based on equal probability for all elements, given a constraint length (only consider until one row, not the complete array)

        int[] results = new int[2];
        double step = 1/(length);
        Random rand = new Random();
        double sel = rand.nextDouble();
        double prob = 0;
        for (int i = 0; i < length; i++){
            prob = prob + step;
            if (prob > sel){
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[length - 1];
        results[1] = length - 1;

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
        Random rand = new Random();
        double selPos = sumProb * rand.nextDouble();
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
        Random rand = new Random();
        double selPos = sumProb * rand.nextDouble();
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


    public static double[] convertProbability (double[] probabilities){
        //method to return the probability in percentage
        double sum = 0;
        double[] relProb = new double[probabilities.length];
        for (int row = 0; row < probabilities.length; row++){
            sum = sum + probabilities[row];
        }
        for (int row = 0; row < probabilities.length; row++) {
            relProb[row] = probabilities[row]/sum*1000;
        }
        return relProb;
    }

    public static TableDataSet updateCountersHousehold (Household household, TableDataSet attributesCount, int mun){
        /* method to update the counters with the characteristics of the generated private household*/
        if (household.getHhSize() == 1){
            attributesCount.setIndexedValueAt(mun,"nHH_1",attributesCount.getIndexedValueAt(mun,"nHH_1") + 1);
        } else if (household.getHhSize() == 2){
            attributesCount.setIndexedValueAt(mun,"nHH_2",attributesCount.getIndexedValueAt(mun,"nHH_2") + 1);
        } else if (household.getHhSize() == 3){
            attributesCount.setIndexedValueAt(mun,"nHH_3",attributesCount.getIndexedValueAt(mun,"nHH_3") + 1);
        } else if (household.getHhSize() == 4){
            attributesCount.setIndexedValueAt(mun,"nHH_4",attributesCount.getIndexedValueAt(mun,"nHH_4") + 1);
        } else if (household.getHhSize() == 5){
            attributesCount.setIndexedValueAt(mun,"nHH_5",attributesCount.getIndexedValueAt(mun,"nHH_5") + 1);
        } else if (household.getHhSize() > 5){
            attributesCount.setIndexedValueAt(mun,"nHH_5",attributesCount.getIndexedValueAt(mun,"nHH_5") + 1);
        }
        attributesCount.setIndexedValueAt(mun,"hhTotal",attributesCount.getIndexedValueAt(mun,"hhTotal") + 1);
        return attributesCount;
    }

    public static TableDataSet updateCountersPerson (Person person, TableDataSet attributesCount,int mun, int[] ageBracketsPerson) {
        /* method to update the counters with the characteristics of the generated person in a private household*/
        attributesCount.setIndexedValueAt(mun, "population", attributesCount.getIndexedValueAt(mun, "population") + 1);
        if (person.getGender() == Gender.MALE) {
            if (person.getOccupation() == Occupation.EMPLOYED) {
                attributesCount.setIndexedValueAt(mun, "M_Agr", attributesCount.getIndexedValueAt(mun, "M_Agr") + 1);
            }
        } else {
            if (person.getOccupation() == Occupation.EMPLOYED) {
                attributesCount.setIndexedValueAt(mun, "F_Agr", attributesCount.getIndexedValueAt(mun, "F_Agr") + 1);
            }
        }
        int age = person.getAge();
        int row1 = 0;
        while (age > ageBracketsPerson[row1]) {
            row1++;
        }
        if (person.getGender() == Gender.FEMALE) {
            String name = "M_" + ageBracketsPerson[row1];
            attributesCount.setIndexedValueAt(mun, name, attributesCount.getIndexedValueAt(mun, name) + 1);
        } else {
            String name = "F_" + ageBracketsPerson[row1];
            attributesCount.setIndexedValueAt(mun, name, attributesCount.getIndexedValueAt(mun, name) + 1);
        }
        return attributesCount;
    }


    public static TableDataSet updateCountersDwelling (Dwelling dwelling, TableDataSet attributesCount, int mun, int[] yearBrackets, int[] sizeBrackets){
        /* method to update the counters with the characteristics of the generated dwelling*/
        if (dwelling.getUsage() == DwellingUsage.OWNED){
            attributesCount.setIndexedValueAt(mun,"H_Own",attributesCount.getIndexedValueAt(mun,"H_Own") + 1);
        } else {
            attributesCount.setIndexedValueAt(mun,"H_Rent",attributesCount.getIndexedValueAt(mun,"H_Rent") + 1);
        }
        if (dwelling.getType().equals(DefaultDwellingTypes.DefaultDwellingTypeImpl.SFA)){
            attributesCount.setIndexedValueAt(mun,"ddT_Detached",attributesCount.getIndexedValueAt(mun,"ddT_Detached") + 1);
        } else if (dwelling.getType().equals(DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234)){
            attributesCount.setIndexedValueAt(mun,"ddT_Apart",attributesCount.getIndexedValueAt(mun,"ddT_Apart") + 1);
        } else {
        attributesCount.setIndexedValueAt(mun,"ddT_Multi",attributesCount.getIndexedValueAt(mun,"ddT_Multi") + 1);
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

        jobsTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_JOB_DESCRIPTION));
        schoolLevelTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SCHOOL_DESCRIPTION));
        educationDegreeTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_EDUCATION_DESCRIPTION));
        double incomeShape = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCOME_GAMMA_SHAPE);
        double incomeRate = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCOME_GAMMA_RATE);
        double[] incomeProbability = ResourceUtil.getDoubleArray(rb,PROPERTIES_INCOME_GAMMA_PROBABILITY);
        GammaDistributionImpl gammaDist = new GammaDistributionImpl(incomeShape, 1/incomeRate);
        String pumsFileName = Properties.get().main.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_MICRODATA_2010_PATH);
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
            //logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            //logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
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
        TableDataSet ppVariables = SiloUtil.readCSVfile(rb.getString(PROPERTIES_ATRIBUTES_MICRODATA_PP));// variables at the person level
        TableDataSet hhVariables = SiloUtil.readCSVfile(rb.getString(PROPERTIES_ATRIBUTES_MICRODATA_HH)); //variables at the household level
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
                            microPersons.setValueAt(personCount, "ppSchool", translateEducationLevel(school, schoolLevelTable));
                        } else {
                            microPersons.setValueAt(personCount, "ppSchool", 0);
                        }
                        if (microPersons.getValueAt(personCount,"ppOccupation") == 1) { // Only employed persons respond to the sector
                            microPersons.setValueAt(personCount,"WZ08", translateJobType(Math.round((int) microPersons.getValueAt(personCount,"ppSector1")/10), jobsTable)); //First two digits of the WZ08 job classification in Germany. They are converted to 10 job classes (Zensus 2011 - Erwerbstätige nach Wirtschaftszweig Wirtschafts(unter)bereiche)
                        } else {
                            microPersons.setValueAt(personCount,"WZ08",0);
                        }
                        int income = (int) microPersons.getValueAt(personCount,"ppIncome");
                        try{
                            microPersons.setValueAt(personCount,"ppIncome",(int) translateIncome(income, incomeProbability, gammaDist));
                        } catch (MathException e){
                            e.printStackTrace();
                        }
                        int education = (int) microPersons.getValueAt(personCount,"ppEducation");
                        microPersons.setValueAt(personCount,"ppEducation", translateEducationLevel(education, educationDegreeTable));
                }
            }
            logger.info("  Read " + (personCountTotal) + " person records in " +
                    (hhCountTotal) + " private households from file: " + pumsFileName);
        } catch (IOException e) {
            //logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            //logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        SiloUtil.writeTableDataSet(microPersons, "input/summary/microPersonsAna.csv");
        SiloUtil.writeTableDataSet(microHouseholds, "input/summary/microHouseholdsAna.csv");

        logger.info("   Finished reading the micro data");
    }


/*    public int selectJobType(Person person) {
        //given a person, select the job type. It is based on the probabilities

        double[] probabilities = new double[jobStringTypes.length];
        int[] jobTypes = new int[jobStringTypes.length];
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
        for (int job = 0; job < jobStringTypes.length; job++){
            jobTypes[job] = jobIntTypes.get(jobStringTypes[job]);
            probabilities[job] = probabilitiesJob.getStringIndexedValueAt(jobStringTypes[job],name);
        }
        //}
        int selected = new EnumeratedIntegerDistribution(jobTypes, probabilities).sample();
        return selected;

    }*/


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


    private void generateCountersForValidation(){
        //method to obtain the errors from the generated synthetic population
        //Create the errors table (for all the municipalities)
        counterMunicipality = new TableDataSet();
        errorMunicipality = new TableDataSet();
        counterMunicipality.appendColumn(cityID,"ID_city");
        errorMunicipality.appendColumn(cityID,"ID_city");
        for(int attribute = 0; attribute < attributesMunicipality.length; attribute++) {
            double[] dummy2 = SiloUtil.createArrayWithValue(cityID.length,0.0);
            double[] dummy3 = SiloUtil.createArrayWithValue(cityID.length,0.0);
            counterMunicipality.appendColumn(dummy2, attributesMunicipality[attribute]);
            errorMunicipality.appendColumn(dummy3, attributesMunicipality[attribute]);
        }
        double[] dummy4 = SiloUtil.createArrayWithValue(cityID.length,0.0);
        double[] dummy5 = SiloUtil.createArrayWithValue(cityID.length,0.0);
        counterMunicipality.appendColumn(dummy4,"population");
        errorMunicipality.appendColumn(dummy5, "population");
        double[] dummy6 = SiloUtil.createArrayWithValue(cityID.length,0.0);
        double[] dummy7 = SiloUtil.createArrayWithValue(cityID.length,0.0);
        counterMunicipality.appendColumn(dummy6, "hhTotal");
        errorMunicipality.appendColumn(dummy7,"hhTotal");
        counterMunicipality.buildIndex(counterMunicipality.getColumnPosition("ID_city"));
        errorMunicipality.buildIndex(errorMunicipality.getColumnPosition("ID_city"));


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

    private void createWeightsAndErrorsCity(){

        int[] microDataIds = dataSetSynPop.getFrequencyMatrix().getColumnAsInt("ID");
        dataSetSynPop.getFrequencyMatrix().buildIndex(dataSetSynPop.getFrequencyMatrix().getColumnPosition("ID"));
        dataSetSynPop.setWeights(new TableDataSet());
        dataSetSynPop.getWeights().appendColumn(microDataIds, "ID");

        TableDataSet errorsMunicipality = new TableDataSet();
        TableDataSet errorsSummary = new TableDataSet();
        String[] labels = new String[]{"error", "iterations","time"};
        errorsMunicipality =  SiloUtil.initializeTableDataSet(errorsMunicipality,
                PropertiesSynPop.get().main.attributesMunicipality, dataSetSynPop.getCityIDs());
        errorsSummary =  SiloUtil.initializeTableDataSet(errorsSummary, labels, dataSetSynPop.getCityIDs());
        dataSetSynPop.setErrorsMunicipality(errorsMunicipality);
        dataSetSynPop.setErrorsSummary(errorsSummary);
    }

    private int estimatePrice(DefaultDwellingTypes.DefaultDwellingTypeImpl ddType, int floorSpace){
        int averagePricePerSQM = 10;
        if (ddType.equals(DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234)){
            averagePricePerSQM = 5;
        } else if (ddType.equals(DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus)){
            averagePricePerSQM = 5;
        }
        int price = floorSpace * averagePricePerSQM;
        return price;

    }

    private void summarizeData(DataContainer dataContainer){

        String filehh = properties.main.baseDirectory
                + properties.householdData.householdFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        HouseholdWriter hhwriter = new DefaultHouseholdWriter(dataContainer.getHouseholdDataManager().getHouseholds());
        hhwriter.writeHouseholds(filehh);

        String filepp = properties.main.baseDirectory
                + properties.householdData.personFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        PersonWriter ppwriter = new PersonWriterTak(dataContainer.getHouseholdDataManager());
        ppwriter.writePersons(filepp);

        String filedd = properties.main.baseDirectory
                + properties.realEstate.dwellingsFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        DwellingWriter ddwriter = new DwellingWriterTak(dataContainer.getRealEstateDataManager());
        ddwriter.writeDwellings(filedd);

        String filejj = properties.main.baseDirectory
                + properties.jobData.jobsFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        JobWriter jjwriter = new DefaultJobWriter(dataContainer.getJobDataManager().getJobs());
        jjwriter.writeJobs(filejj);


/*        String fileee = properties.main.baseDirectory
                + properties.schoolData.schoolsFileName
                + "_"
                + properties.main.baseYear
                + ".csv";
        SchoolsWriter eewriter = new SchoolsWriter(dataContainer.getSchoolData());
        eewriter.writeSchools(fileee);*/

    }

}
