package de.tum.bgu.msm.SyntheticPopulationGenerator;

import com.google.common.primitives.Ints;
import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import com.pb.common.util.ResourceUtil;
import com.sun.org.apache.regexp.internal.RE;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.data.*;
import omx.OmxFile;
import omx.OmxLookup;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.commons.math.stat.Frequency;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.log4j.Logger;

import javax.measure.unit.SI;
import java.io.*;
import java.util.*;

public class SyntheticPopJP {
    private ResourceBundle rb;
    //Options to run de synthetic population
    protected static final String PROPERTIES_CONSTRAINT_BY_CITY_AND_CNTY  = "run.ipu.city.and.county";
    protected static final String PROPERTIES_RUN_IPU                      = "run.ipu.synthetic.pop";
    protected static final String PROPERTIES_RUN_SYNTHETIC_POPULATION     = "run.synth.pop.generator";
    protected static final String PROPERTIES_YEAR_MICRODATA               = "year.micro.data";
    //Routes of the input data
    protected static final String PROPERTIES_MICRODATA_2000_PATH          = "micro.data.2000";
    protected static final String PROPERTIES_MICRODATA_2010_PATH          = "micro.data.2010";
    protected static final String PROPERTIES_MARGINALS_REGIONAL_MATRIX    = "marginals.county";
    protected static final String PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX   = "marginals.municipality";
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
    protected static final String PROPERTIES_REGION_ATTRIBUTES            = "attributes.region";
    protected static final String PROPERTIES_HOUSEHOLD_ATTRIBUTES         = "attributes.household";
    protected static final String PROPERTIES_HOUSEHOLD_SIZES              = "household.sizes";
    protected static final String PROPERTIES_MICRO_DATA_AGES              = "age.brackets";
    protected static final String PROPERTIES_MICRO_DATA_YEAR_DWELLING     = "year.brackets";
    protected static final String PROPERTIES_MICRO_DATA_FLOOR_SPACE_DWELLING = "floor.space.dwelling";
    protected static final String PROPERTIES_MAX_ITERATIONS               = "max.iterations.ipu";
    protected static final String PROPERTIES_MAX_ERROR                    = "max.error.ipu";
    protected static final String PROPERTIES_INITIAL_ERROR                = "ini.error.ipu";
    protected static final String PROPERTIES_IMPROVEMENT_ERROR            = "min.improvement.error.ipu";
    protected static final String PROPERTIES_IMPROVEMENT_ITERATIONS       = "iterations.improvement.ipu";
    protected static final String PROPERTIES_INCREASE_ERROR               = "increase.error.ipu";
    protected static final String PROPERTIES_INCOME_GAMMA_PROBABILITY     = "income.probability";
    protected static final String PROPERTIES_INCOME_GAMMA_SHAPE           = "income.gamma.shape";
    protected static final String PROPERTIES_INCOME_GAMMA_RATE            = "income.gamma.rate";
    protected static final String PROPERTIES_JOB_TYPES                    = "employment.types";
    protected static final String PROPERTIES_SCHOOL_TYPES_DE              = "school.types";
    protected static final String PROPERTIES_JOB_ALPHA                    = "employment.choice.alpha";
    protected static final String PROPERTIES_JOB_GAMMA                    = "employment.choice.gamma";
    protected static final String PROPERTIES_UNIVERSITY_ALPHA             = "university.choice.alpha";
    protected static final String PROPERTIES_UNIVERSITY_GAMMA             = "university.choice.gamma";
    protected static final String PROPERTIES_EMPLOYMENT_BY_GENDER_EDU     = "employment.probability";
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
    protected TableDataSet cellsMatrix;

    protected int[] cityID;
    protected int[] countyID;
    protected HashMap<Integer, int[]> municipalitiesByCounty;
    protected HashMap<Integer, int[]> cityTAZ;

    protected String[] attributesMunicipality;
    protected int[] ageBracketsPerson;
    protected int[] ageBracketsPersonQuarter;
    protected int[] sizeBracketsDwelling;
    protected int[] yearBracketsDwelling;
    protected int[] householdSizes;
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
    protected TableDataSet probabilitiesJob;

    protected Matrix distanceMatrix;
    protected Matrix distanceImpedance;
    protected TableDataSet odMunicipalityFlow;
    protected TableDataSet odCountyFlow;

    static Logger logger = Logger.getLogger(SyntheticPopJP.class);


    public SyntheticPopJP(ResourceBundle rb) {
        // Constructor
        this.rb = rb;
    }


    public void runSP(){
        //method to create the synthetic population
        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SYNTHETIC_POPULATION, false)) return;
        logger.info("   Starting to create the synthetic population.");
        ExtractMicroDataJP extractMicroData = new ExtractMicroDataJP(rb);
        extractMicroData.run();
        frequencyMatrix = extractMicroData.getFrequencyMatrix();
        microDataDwelling = extractMicroData.getMicroDwellings();
        microDataHousehold = extractMicroData.getMicroHouseholds();
        microDataPerson = extractMicroData.getMicroPersons();
        readInputData();
        createDirectoryForOutput();
        long startTime = System.nanoTime();
        //Run fitting procedure
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_IPU) == true) {
            runIPUbyCity(); //IPU fitting with one geographical constraint. Each municipality is independent of others
        } else {
            readIPU(); //Read the weights to select the household
        }
        generateHouseholdsPersonsDwellings(); //Monte Carlo selection process to generate the synthetic population. The synthetic dwellings will be obtained from the same microdata
        /*generateJobs(); //Generate the jobs by type. Allocated to TAZ level
        assignJobs(); //Workplace allocation
        assignSchools(); //School allocation
        addCars(false);
        summarizeData.writeOutSyntheticPopulationDE(rb, SiloUtil.getBaseYear());*/
        long estimatedTime = System.nanoTime() - startTime;
        logger.info("   Finished creating the synthetic population. Elapsed time: " + estimatedTime);
    }


    private void readInputData() {
        //Read the attributes at the municipality level (household attributes) and the marginals at the municipality level (German: Gemeinden)
        attributesMunicipality = frequencyMatrix.getColumnLabels();
        marginalsMunicipality = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX));
        marginalsMunicipality.buildIndex(marginalsMunicipality.getColumnPosition("CODE_Z01"));

        //List of municipalities and counties that are used for IPU and allocation
        TableDataSet selectedMunicipalities = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SELECTED_MUNICIPALITIES_LIST)); //TableDataSet with all municipalities
        ArrayList<Integer> municipalities = new ArrayList<>();
        ArrayList<Integer> counties = new ArrayList<>();
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
                    int[] citiesInThisCounty = municipalitiesByCounty.get(county);
                    int[] expandedCityList = SiloUtil.expandArrayByOneElement(citiesInThisCounty, city);
                    municipalitiesByCounty.put(county, expandedCityList);
                } else {
                    municipalitiesByCounty.put(county, new int[]{city});
                }
            }
        }
        cityID = SiloUtil.convertArrayListToIntArray(municipalities);
        countyID = SiloUtil.convertArrayListToIntArray(counties);


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
        logger.info("   Read OMX matrix");
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
        TableDataSet regionsforFrequencyMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_ATRIBUTES_ZONAL_DATA));
        regionsforFrequencyMatrix.buildIndex(regionsforFrequencyMatrix.getColumnPosition("V1"));

        HashMap<Integer, HashMap<Integer, Integer>> householdsForFrequencyMatrix = new HashMap<>();
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
        weightsTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_WEIGHTS_MATRIX));
        weightsTable.buildIndex(weightsTable.getColumnPosition("ID"));

        logger.info("   Finishing reading the results from the IPU");
    }


    private void generateJobs(){
        //Generate jobs file. The worker ID will be assigned later on the process "assignJobs"

        logger.info("   Starting to generate jobs");

        String[] jobStringType = ResourceUtil.getArray(rb, PROPERTIES_JOB_TYPES);
        int[] rasterCellsIDs = cellsMatrix.getColumnAsInt("ID_cell");

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
            for (int row = 0; row < jobStringType.length; row++) {
                String jobType = jobStringType[row];
                int totalJobs = (int) marginalsMunicipality.getIndexedValueAt(municipalityID, jobType);
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
        probabilitiesJob = SiloUtil.readCSVfile(rb.getString(PROPERTIES_EMPLOYMENT_BY_GENDER_EDU));
        probabilitiesJob.buildStringIndex(1);


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


        //For validation - obtain the trip length distribution
        Frequency travelSecondary = new Frequency();
        Frequency travelUniversity = new Frequency();
        Frequency travelPrimary = new Frequency();
        validationCommutersFlow(); //Generates the validation tabledatasets
        int[] flow = SiloUtil.createArrayWithValue(odMunicipalityFlow.getRowCount(),0);
        odMunicipalityFlow.appendColumn(flow,Integer.toString(count));


        //Produce one array list with students' ID
        Map<Integer, Person> personMap = Person.getPersonMap();
        ArrayList<Person> studentArrayList = new ArrayList<>();
        int[] studentsByType2 = new int[schoolTypes.length];
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
        EmploymentChoice ec = new EmploymentChoice(rb);
        int assignedSchools = 0;
        int[] studentsOutside = new int[schoolTypes.length];
        int[] studentsByType = new int[schoolTypes.length];
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
                    schoolPlace = ec.selectWorkplace2(pp.getZone(), numberVacantSchoolsByZoneByType,
                            keys, lengthKeys, universityDistanceImpedance);
                    travelUniversity.addValue((int) distanceMatrix.getValueAt(pp.getZone(), schoolPlace[0] / 100));
                } else {
                    schoolPlace = ec.selectWorkplace3(pp.getZone(), numberVacantSchoolsByZoneByType,
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

                //For validation OD TableDataSet
                int homeMun = (int) cellsMatrix.getIndexedValueAt(pp.getZone(), "smallID");
                int workMun = (int) cellsMatrix.getIndexedValueAt(pp.getSchoolPlace(), "smallID");
                int odPair = homeMun * 1000 + workMun;
                odMunicipalityFlow.setIndexedValueAt(odPair,Integer.toString(count),odMunicipalityFlow.getIndexedValueAt(odPair,Integer.toString(count))+ 1);

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
        checkTripLengthDistribution(travelPrimary, 0, 0, "microData/interimFiles/tripLengthDistributionPrimary.csv", 1);
        checkTripLengthDistribution(travelSecondary, 0, 0, "microData/interimFiles/tripLengthDistributionSecondary.csv", 1); //Trip length frequency distribution
        checkTripLengthDistribution(travelUniversity, alphaJob, gammaJob, "microData/interimFiles/tripLengthDistributionUniversity.csv", 1);
        SiloUtil.writeTableDataSet(odMunicipalityFlow,"microData/interimFiles/odMunicipalityFlow.csv");
        for (int i = 0; i < schoolTypes.length; i++) {
            logger.info("  School type: " + schoolTypes[i] + ". " + studentsOutside[schoolTypes[i] - 1] + " students out of " + studentsByType[schoolTypes[i] - 1] + " study outside the area");
        }
    }


    private void readSyntheticPopulation(){
        //Read the synthetic population

        logger.info("   Starting to read the synthetic population");
        String fileEnding = "_" + SiloUtil.getBaseYear() + ".csv";
        TableDataSet households = SiloUtil.readCSVfile(rb.getString(PROPERTIES_HOUSEHOLD_SYN_POP) + fileEnding);
        TableDataSet persons = SiloUtil.readCSVfile(rb.getString(PROPERTIES_PERSON_SYN_POP) + fileEnding);
        TableDataSet dwellings = SiloUtil.readCSVfile(rb.getString(PROPERTIES_DWELLING_SYN_POP) + fileEnding);
        TableDataSet jobs = SiloUtil.readCSVfile(rb.getString(PROPERTIES_JOB_SYN_POP) + fileEnding);
        schoolLevelTable = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SCHOOL_DESCRIPTION));


        //Generate the households, dwellings and persons
        int aux = 1;
        for (int i = 1; i <= households.getRowCount(); i++){
            Household hh = new Household((int)households.getValueAt(i,"id"),(int)households.getValueAt(i,"dwelling"),
                    (int)households.getValueAt(i,"zone"),(int)households.getValueAt(i,"hhSize"),
                    (int)households.getValueAt(i,"autos"));
            for (int j = 1; j <= hh.getHhSize(); j++) {
                Person pp = new Person((int) persons.getValueAt(aux, "id"), (int) persons.getValueAt(aux, "hhid"),
                        (int) persons.getValueAt(aux, "age"), (int) persons.getValueAt(aux, "gender"),
                        Race.white, (int) persons.getValueAt(aux, "occupation"), 0,
                        (int) persons.getValueAt(aux, "income"));
                hh.addPersonForInitialSetup(pp);
                pp.setEducationLevel((int) persons.getValueAt(aux, "education"));
                if (persons.getStringValueAt(aux, "relationShip").equals("single")) pp.setRole(PersonRole.single);
                else if (persons.getStringValueAt(aux, "relationShip").equals("married")) pp.setRole(PersonRole.married);
                else pp.setRole(PersonRole.child);
                pp.setDriverLicense((int) persons.getValueAt(aux,"driversLicense"));
                pp.setNationality((int) persons.getValueAt(aux,"nationality"));
                pp.setHhSize(hh.getHhSize());
                pp.setZone(hh.getHomeZone());
                pp.setSchoolType((int) persons.getValueAt(aux,"schoolDE"));
/*                if (pp.getSchoolType() == 1){
                    if (pp.getAge() < 11 & pp.getAge() > 4) {
                    } else {
                        pp.setSchoolType(0);
                    }
                } else if (pp.getSchoolType() < 4){
                    pp.setSchoolType(assignGymnasiumMitteByTAZ(pp));
                }*/
                pp.setWorkplace((int) persons.getValueAt(aux,"workplace"));
                //pp.setSchoolPlace((int) persons.getValueAt(aux, "schoolPlace"));
                aux++;
            }
        }
        for (int i = 1; i <= dwellings.getRowCount(); i++){
            Dwelling dd = new Dwelling((int)dwellings.getValueAt(i,"id"),(int)dwellings.getValueAt(i,"zone"),
                    (int)dwellings.getValueAt(i,"hhID"),DwellingType.MF5plus,(int)dwellings.getValueAt(i,"bedrooms"),
                    (int)dwellings.getValueAt(i,"quality"),(int)dwellings.getValueAt(i,"monthlyCost"),
                    (int)dwellings.getValueAt(i,"restriction"),(int)dwellings.getValueAt(i,"yearBuilt"));
            dd.setFloorSpace((int)dwellings.getValueAt(i,"floor"));
            dd.setBuildingSize((int)dwellings.getValueAt(i,"building"));
            dd.setYearConstructionDE((int)dwellings.getValueAt(i,"year"));
            dd.setUsage((int)dwellings.getValueAt(i,"usage"));
        }
        logger.info("   Generated households, persons and dwellings");


        //Generate the jobs
        for (int i = 1; i <= jobs.getRowCount(); i++) {
            Job jj = new Job((int) jobs.getValueAt(i, "id"), (int) jobs.getValueAt(i, "zone"),
                    (int) jobs.getValueAt(i, "personId"), jobs.getStringValueAt(i, "type"));
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
        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        int previousHouseholds = 0;
        int previousPersons = 0;


        //Define income distribution
        double incomeShape = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCOME_GAMMA_SHAPE);
        double incomeRate = ResourceUtil.getDoubleProperty(rb,PROPERTIES_INCOME_GAMMA_RATE);
        double[] incomeProbability = ResourceUtil.getDoubleArray(rb,PROPERTIES_INCOME_GAMMA_PROBABILITY);
        GammaDistributionImpl gammaDist = new GammaDistributionImpl(incomeShape, 1/incomeRate);


        //Driver license probability
        TableDataSet probabilityDriverLicense = SiloUtil.readCSVfile("input/syntheticPopulation/driverLicenseProb.csv");


        generateCountersForValidation();

        //Selection of households, persons, jobs and dwellings per municipality
        for (int municipality = 0; municipality < cityID.length; municipality++){
            logger.info("   Municipality " + cityID[municipality] + ". Starting to generate households.");

            //-----------***** Data preparation *****-------------------------------------------------------------------
            //Create local variables to avoid accessing to the same variable on the parallel processing
            int municipalityID = cityID[municipality];
            String[] attributesHouseholdIPU = attributesMunicipality;
            TableDataSet rasterCellsMatrix = cellsMatrix;
            TableDataSet microHouseholds = microDataHousehold;
            TableDataSet microPersons = microDataPerson;
            TableDataSet microDwellings = microDataDwelling;
            microHouseholds.buildIndex(microHouseholds.getColumnPosition("id"));
            microDwellings.buildIndex(microDwellings.getColumnPosition("id"));
            int totalHouseholds = (int) marginalsMunicipality.getIndexedValueAt(municipalityID,"hhTotal");
            double[] probability = weightsTable.getColumnAsDouble(Integer.toString(municipalityID));
            int[] agePerson = ageBracketsPerson;
            int[] sizeBuilding = sizeBracketsDwelling;
            int[] yearBuilding = yearBracketsDwelling;
            int[] levelEdu = new int[4];
            double[] probEdu = new double[4];
            for (int i = 0; i < levelEdu.length; i++){
                probEdu[i] = marginalsMunicipality.getIndexedValueAt(municipalityID,"Ed_" + i);
            }


            //Counter and errors for the municipality
/*            TableDataSet counterMunicipality = new TableDataSet();
            TableDataSet errorMunicipality = new TableDataSet();
            TableDataSet marginals = new TableDataSet();
            int[] dummy = SiloUtil.createArrayWithValue(1,municipalityID);
            int[] dummy1 = SiloUtil.createArrayWithValue(1,municipalityID);
            int[] dummy4 = SiloUtil.createArrayWithValue(1,municipalityID);
            counterMunicipality.appendColumn(dummy,"ID_city");
            errorMunicipality.appendColumn(dummy1,"ID_city");
            marginals.appendColumn(dummy4,"ID_city");
            for (int attribute = 0;attribute < attributesHouseholdIPU.length; attribute++) {
                double[] dummy2 = SiloUtil.createArrayWithValue(1,0.0);
                double[] dummy3 = SiloUtil.createArrayWithValue(1,0.0);
                int[] dummy5 = SiloUtil.createArrayWithValue(1,(int) marginalsMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]));
                counterMunicipality.appendColumn(dummy2, attributesHouseholdIPU[attribute]);
                errorMunicipality.appendColumn(dummy3,attributesHouseholdIPU[attribute]);
                marginals.appendColumn(dummy5,attributesHouseholdIPU[attribute]);
            }
            counterMunicipality.buildIndex(counterMunicipality.getColumnPosition("ID_city"));
            errorMunicipality.buildIndex(errorMunicipality.getColumnPosition("ID_city"));
            marginals.buildIndex(marginals.getColumnPosition("ID_city"));*/


            //obtain the raster cells of the municipality and their weight within the municipality
            int[] tazInCity = cityTAZ.get(municipalityID);
            double[] probTaz = new double[tazInCity.length];
            double tazRemaining = 0;
            for (int i = 0; i < tazInCity.length; i++){
                probTaz[i] = rasterCellsMatrix.getIndexedValueAt(tazInCity[i],"Population");
                tazRemaining = tazRemaining + probTaz[i];
            }


            double hhRemaining = 0;
            double[] probabilityPrivate = new double[probability.length]; // Separate private households and group quarters for generation
            for (int row = 0; row < probability.length; row++){
                if ((int) microHouseholds.getValueAt(row + 1,"groupQuarters") == 0){
                    probabilityPrivate[row] = probability[row];
                    hhRemaining = hhRemaining + probability[row];
                }
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
                int householdSize = (int) microHouseholds.getIndexedValueAt(hhIdMD, "HHsize");
                int householdCars = (int) microHouseholds.getIndexedValueAt(hhIdMD, "N_Car");
                id = HouseholdDataManager.getNextHouseholdId();
                int newDdId = RealEstateDataManager.getNextDwellingId();
                Household household = new Household(id, newDdId, tazID, householdSize, householdCars); //(int id, int dwellingID, int homeZone, int hhSize, int autos)
                hhTotal++;
                //counterMunicipality = updateCountersHousehold(household, counterMunicipality, municipalityID);


                //copy the household members characteristics
                for (int rowPerson = 0; rowPerson < householdSize; rowPerson++) {
                    int idPerson = HouseholdDataManager.getNextPersonId();
                    int personCounter = (int) microHouseholds.getIndexedValueAt(hhIdMD, "firstPerson") + rowPerson;
                    int age = (int) microPersons.getValueAt(personCounter, "age");
                    int gender = (int) microPersons.getValueAt(personCounter, "Gender");
                    int occupation = (int) microPersons.getValueAt(personCounter, "occupation");
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

                    Person pers = new Person(idPerson, id, age, gender, Race.white, occupation, 0, income); //(int id, int hhid, int age, int gender, Race race, int occupation, int workplace, int income)
                    household.addPersonForInitialSetup(pers);
                    pers.setEducationLevel(education);
                    PersonRole role = PersonRole.single; //default value = single
                    if (microPersons.getValueAt(personCounter, "personStatus") == 2) {
                        role = PersonRole.married;
                        Person firstPersonInHousehold = household.getPersons()[0];  // get first person in household and make it married (if one person within the household is the partner)
                        firstPersonInHousehold.setRole(PersonRole.married);
                    } else if (microPersons.getValueAt(personCounter, "personStatus") == 3) { //if the person is not the household head nor his spouse
                        if (microPersons.getValueAt(personCounter,"relationshipHousehold") == 3) { //the person is son/daughter of the household head
                            role = PersonRole.child;
                        } else if (microPersons.getValueAt(personCounter,"relationshipHousehold") == 4) { //the person is the grandchild of the household head
                            role = PersonRole.child;
                        }
                    }
                    pers.setRole(role);
                    pers.setNationality(1);
                    pers.setJobType((int) microPersons.getValueAt(personCounter, "jobType"));
                    pers.setDriverLicense((int) microPersons.getValueAt(personCounter, "DrivLicense"));
                    pers.setSchoolType((int) microPersons.getValueAt(personCounter, "school"));
                    pers.setZone(household.getHomeZone());
                    hhPersons++;
                    //counterMunicipality = updateCountersPerson(pers, counterMunicipality, municipalityID,agePerson);
                }


                //Copy the dwelling of that household
                int bedRooms = 1; //Not on the micro data
                int price = 0; //Copied from micro data
                int year = 0; //Not by year. In the data is going to be in classes
                int floorSpace = 0;
                int usage = (int) microDwellings.getIndexedValueAt(hhIdMD, "HousOwn");
                int buildingSize = (int) microDwellings.getIndexedValueAt(hhIdMD, "HousTyp");
                DwellingType ddType = translateDwellingType(buildingSize);
                int quality = 0; //depend on year built and type of heating
                year = selectDwellingYear(year); //convert from year class to actual 4-digit year
                Dwelling dwell = new Dwelling(newDdId, tazID, id, ddType , bedRooms, quality, price, 0, year); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year
                dwell.setFloorSpace(floorSpace);
                dwell.setUsage(usage);
                dwell.setBuildingSize(buildingSize);
                //counterMunicipality = updateCountersDwelling(dwell,counterMunicipality,municipalityID,yearBuilding,sizeBuilding);
            }
            int households = HouseholdDataManager.getHighestHouseholdIdInUse() - previousHouseholds;
            int persons = HouseholdDataManager.getHighestPersonIdInUse() - previousPersons;
            previousHouseholds = HouseholdDataManager.getHighestHouseholdIdInUse();
            previousPersons = HouseholdDataManager.getHighestPersonIdInUse();


            //Calculate the errors from the synthesized population at the attributes of the IPU.
            //Update the tables for all municipalities with the result of this municipality

            //Consider if I need to add also the errors from other attributes. They must be at the marginals file, or one extra file
            //For county level they should be calculated on a next step, outside this loop.
/*            float averageError = 0f;
            for (int attribute = 0; attribute < attributesHouseholdIPU.length; attribute++){
                float error = Math.abs((counterMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]) -
                        marginals.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute])) /
                        marginals.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]));
                errorMunicipality.setIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute],error);
                averageError = averageError + error;
                counterSynPop.setIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute],counterMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]));
                relativeErrorSynPop.setIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute],errorMunicipality.getIndexedValueAt(municipalityID,attributesHouseholdIPU[attribute]));
            }
            averageError = averageError / (1 + attributesHouseholdIPU.length) * 100;*/


            logger.info("   Municipality " + municipalityID + ". Generated " + hhPersons + " persons in " + hhTotal + " households and " + quartersTotal + " persons in group quarters.");
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
            int vacantDwellings = (int) marginalsMunicipality.getIndexedValueAt(cityID[municipality], "totalDwellingsVacant");
            TableDataSet rasterCellsMatrix = cellsMatrix;

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
            float [] vacantFloor = new float[sizeBracketsDwelling.length];
            for (int row = 0; row < sizeBracketsDwelling.length; row++){
                String name = "vacantDwellings" + sizeBracketsDwelling[row];
                vacantFloor[row] = marginalsMunicipality.getIndexedValueAt(municipalityID,name)/vacantDwellings;
            }

            //Probability for year and building size for vacant dwellings
            float[] vacantSize = new float[yearBracketsDwelling.length * 2];
            for (int row = 0; row < yearBracketsDwelling.length; row++){
                String name = "vacantSmallDwellings" + yearBracketsDwelling[row];
                String name1 = "vacantMediumDwellings" + yearBracketsDwelling[row];
                vacantSize[row] = marginalsMunicipality.getIndexedValueAt(municipalityID,name) / vacantDwellings;
                vacantSize[row + yearBracketsDwelling.length] = marginalsMunicipality.getIndexedValueAt(municipalityID,name1) / vacantDwellings;
            }

            //Select the vacant dwelling and copy characteristics
            for (int row = 0; row < vacantDwellings; row++) {

                //Allocation
                int ddCell[] = select(probTaz, tazInCity, sumProbTaz); // I allocate vacant dwellings using the same proportion as occupied dwellings.

                //Copy characteristics
                int newDdId = RealEstateDataManager.getNextDwellingId();
                int bedRooms = 1; //Not on the micro data
                int price = 0; //Monte Carlo
                int[] buildingSizeAndYearBuilt = selectBuildingSizeYear(vacantSize, yearBracketsDwelling);
                int key = municipalityID + buildingSizeAndYearBuilt[1] * 1000;
                int quality = 1; //Based on the distribution of qualities at the municipality for that construction period
                int year = selectVacantDwellingYear(buildingSizeAndYearBuilt[1]);
                int floorSpaceDwelling = selectFloorSpace(vacantFloor, sizeBracketsDwelling);
                Dwelling dwell = new Dwelling(newDdId, ddCell[0], -1, DwellingType.MF234, bedRooms, quality, price, 0, year); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year
                dwell.setUsage(3); //vacant dwelling = 3; and hhID is equal to -1
                dwell.setFloorSpace(floorSpaceDwelling);
                dwell.setBuildingSize(buildingSizeAndYearBuilt[0]);
                vacantCounter++;
            }
            logger.info("   The number of vacant dwellings is: " + vacantCounter);
        }
/*        //Write the files for all municipalities
        String name = ("microData/interimFiles/totalsSynPop.csv");
        SiloUtil.writeTableDataSet(counterSynPop,name);
        String name1 = ("microData/interimFiles/errorsSynPop.csv");
        SiloUtil.writeTableDataSet(relativeErrorSynPop,name1);*/
    }


    private DwellingType translateDwellingType (int pumsDdType) {
        // translate micro census dwelling types into 6 MetCouncil Dwelling Types

        // Available in MICRO CENSUS:
//        V 01 . Building with 1-2 apartments
//        V 02 . Building with 3-6 apartments
//        V 03 . Building with 1-12 apartments
//        V 04 . Building with 13-20 apartments
//        V 05 . Building with 21+ apartments
//        V 09 . Not stated
//        V -1 . Living in group quarter
//        V -5 . Moved in the last 12 months


        DwellingType type;
        if (pumsDdType == 2) type = DwellingType.MF234; //duplexes and buildings 2-4 units
        else if (pumsDdType == 1) type = DwellingType.SFD; //single-family house detached
            //else if (pumsDdType == 3) type = DwellingType.SFA;//single-family house attached or townhouse
            //else if (pumsDdType == 4 || pumsDdType == 5) type = DwellingType.MH; //mobile home
        else if (pumsDdType >= 3 ) type = DwellingType.MF5plus; //multifamily houses with 5+ units. Assumes that not stated are 5+units
        else if (pumsDdType == 0) type = DwellingType.MF5plus; //multifamily houses with 5+ units. Assumes that group quarters are 5+ units
        else if (pumsDdType == -5) type = DwellingType.MH; //mobile home; //mobile home. Assumes that group quarters are 5+ units
        else {
            logger.error("Unknown dwelling type " + pumsDdType + " found in PUMS data.");
            type = null;
        }
        return type;
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

    private void identifyVacantJobsByZoneType() {
        // populate HashMap with Jobs by zone and job type
        // adapted from SyntheticPopUS

        logger.info("  Identifying vacant jobs by zone");
        Job[] jobs = Job.getJobArray();

        idVacantJobsByZoneType = new HashMap<>();
        numberVacantJobsByType = new HashMap<>();
        idZonesVacantJobsByType = new HashMap<>();
        numberZonesByType = new HashMap<>();
        numberVacantJobsByZoneByType = new HashMap<>();
        jobStringTypes = ResourceUtil.getArray(rb, PROPERTIES_JOB_TYPES);
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
        if (person.getNationality() == 8) {
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


    public static TableDataSet updateCountersPersonQuarter (Person person, TableDataSet attributesCount,int mun){
        /* method to update the counters with the characteristics of the generated person living in a group quarter*/
        if (person.getNationality() == 8){
            attributesCount.setIndexedValueAt(mun,"foreigners",attributesCount.getIndexedValueAt(mun,"foreigners") + 1);
        }
        if (person.getGender() == 1){
            if(person.getOccupation() == 1) {
                attributesCount.setIndexedValueAt(mun,"maleWorkers",attributesCount.getIndexedValueAt(mun,"maleWorkers") + 1);
            }
            if (person.getAge() > 64){
                attributesCount.setIndexedValueAt(mun,"maleQuarters99",attributesCount.getIndexedValueAt(mun,"maleQuarters99") + 1);
            } else {
                attributesCount.setIndexedValueAt(mun,"maleQuarters64",attributesCount.getIndexedValueAt(mun,"maleQuarters64") + 1);
            }
        } else {
            if(person.getOccupation() == 1) {
                attributesCount.setIndexedValueAt(mun,"femaleWorkers",attributesCount.getIndexedValueAt(mun,"femaleWorkers") + 1);
            }
            if (person.getAge() > 64){
                attributesCount.setIndexedValueAt(mun,"femaleQuarters99",attributesCount.getIndexedValueAt(mun,"femaleQuarters99") + 1);
            } else {
                attributesCount.setIndexedValueAt(mun,"maleQuarters64",attributesCount.getIndexedValueAt(mun,"maleQuarters64") + 1); //females and males are on the same group!!
            }
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

    public static TableDataSet updateCountersDwellingVacant (Dwelling dwelling, TableDataSet attributesCount,int mun, int[] yearBrackets, int[] sizeBrackets){
        /* method to update the counters with the characteristics of the generated dwelling*/
        int row = 0;
        while (dwelling.getFloorSpace() > sizeBrackets[row]){
            row++;
        }
        String name = "dwellingsVacant" + sizeBrackets[row];
        attributesCount.setIndexedValueAt(mun,name,attributesCount.getIndexedValueAt(mun,name) + 1);
        if (dwelling.getYearConstructionDE() > 0 & dwelling.getYearConstructionDE() < 10) {
            int row1 = 0;
            while (dwelling.getYearConstructionDE() > yearBrackets[row1]){
                row1++;
            }
            if (dwelling.getBuildingSize() == 1){
                String name1 = "smallDwellingsVacant" + yearBrackets[row1];
                attributesCount.setIndexedValueAt(mun,name1,attributesCount.getIndexedValueAt(mun,name1) + 1);
            } else {
                String name1 = "mediumDwellingsVacant" + yearBrackets[row1];
                attributesCount.setIndexedValueAt(mun,name1,attributesCount.getIndexedValueAt(mun,name1) + 1);
            }
        }
        attributesCount.setIndexedValueAt(mun,"vacantDwellings",attributesCount.getIndexedValueAt(mun,"vacantDwellings") + 1);
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
        String pumsFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_MICRODATA_2010_PATH);
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
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        SiloUtil.writeTableDataSet(microPersons, "input/summary/microPersonsAna.csv");
        SiloUtil.writeTableDataSet(microHouseholds, "input/summary/microHouseholdsAna.csv");

        logger.info("   Finished reading the micro data");
    }


    public int selectJobType(Person person) {
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
