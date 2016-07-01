package edu.umd.ncsg.SyntheticPopulationGenerator;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloMuc;
import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.data.*;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.*;

/**
 * Generates a simple synthetic population for a study area in Germany
 * @author Ana Moreno (TUM)
 * Created on May 12, 2016 in Munich
 *
 */
public class SyntheticPopDe {
    private ResourceBundle rb;
    protected static final String PROPERTIES_MICRO_DATA_ALL               = "micro.data.bayern";
    protected static final String PROPERTIES_MICRO_DATA_HOUSEHOLD         = "micro.data.households";
    protected static final String PROPERTIES_MICRO_DATA_PERSON            = "micro.data.persons";
    protected static final String PROPERTIES_MICRO_DATA_AGES              = "age.brackets";

    protected static final String PROPERTIES_MAX_RECORDS_MICRO_DATA       = "micro.data.max.records";
    protected static final String PROPERTIES_RUN_SYNTHETIC_POPULATION     = "run.synth.pop.generator";
    protected static final String PROPERTIES_FREQUENCY_MATRIX             = "frequency.matrix.households";
    protected static final String PROPERTIES_MARGINALS_REGIONAL_MATRIX    = "marginals.region.matrix";
    protected static final String PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX   = "marginals.household.matrix";
    protected static final String PROPERTIES_MAX_ITERATIONS               = "max.iterations.ipu";
    protected static final String PROPERTIES_MAX_ERROR                    = "max.error.ipu";
    protected static final String PROPERTIES_INITIAL_ERROR                = "ini.error.ipu";
    protected static final String PROPERTIES_IMPROVEMENT_ERROR            = "min.improvement.error.ipu";
    protected static final String PROPERTIES_IMPROVEMENT_ITERATIONS       = "iterations.improvement.ipu";
    protected static final String PROPERTIES_REGION_ATTRIBUTES            = "attributes.region";
    protected static final String PROPERTIES_HOUSEHOLD_ATTRIBUTES         = "attributes.household";


    protected static final String PROPERTIES_MICRO_DATA_DWELLINGS         = "micro.data.dwellings";

    protected static final String PROPERTIES_CONTROL_ATTRIBUTES_MC        = "control.attributes.MC";

    protected TableDataSet microDataAll;
    protected TableDataSet microDataHousehold;
    protected TableDataSet microDataPerson;
    protected TableDataSet frequencyMatrix;
    protected TableDataSet marginalsRegionMatrix;
    protected TableDataSet marginalsHouseholdMatrix;
    protected TableDataSet microDataDwelling;

    String[] cityIDs;

    protected String[] attributesRegion;
    protected String[] attributesHousehold;
    protected int[] ageBracketsPerson;

    protected TableDataSet weight;
    protected TableDataSet nonZeroFrequency;
    protected TableDataSet nonZeroNumber;
    protected TableDataSet weightedSumHousehold;
    protected TableDataSet weightedSumRegion;
    protected TableDataSet errorHousehold;
    protected TableDataSet errorRegion;

    protected int maxIterations;
    protected double maxError;
    protected double initialError;
    protected double improvementError;
    protected double iterationError;
    protected String maxErrorAttribute;

    protected String[] controlAttributes;

    static Logger logger = Logger.getLogger(SyntheticPopDe.class);


    public SyntheticPopDe(ResourceBundle rb) {
        // Constructor
        this.rb = rb;
    }


    public void runSP(){
        //method to create the synthetic population
        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SYNTHETIC_POPULATION, false)) return;
        logger.info("   Starting to create the synthetic population.");
        long startTime = System.nanoTime();
        readDataSynPop(); //Read the micro data and marginals
        prepareFrequencyMatrix();//Obtain the n-dimensional frequency matrix according to the defined types
        performIPU(); //IPU fitting to obtain the expansion factor for each entry of the micro data
        selectHouseholds(); //Monte Carlo selection process to generate the synthetic population
        //readDwellings();
        //selectDwelling();
        summarizeData.writeOutSyntheticPopulation(rb, SiloUtil.getBaseYear());
        long estimatedTime = System.nanoTime() - startTime;
        logger.info("   Finished creating the synthetic population. Elapsed time: " + estimatedTime);
    }


    private void readDataSynPop(){
        //method to read the synthetic population initial data
        logger.info("   Starting to read the micro data");

        //Scanning the file to obtain the number of households and persons in Bavaria
        String pumsFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_MICRO_DATA_ALL);
        String recString = "";
        int recCount = 0;
        int hhCountTotal = 0;
        int personCountTotal = 0;
        int hhOutCountTotal = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                String recLander = recString.substring(0,2);
                switch (recLander) {
                    case "09": //Record from Bavaria
                        int householdNumber = convertToInteger(recString.substring(7,9));
                        if (householdNumber != previousHouseholdNumber) {
                            hhCountTotal++;
                            personCountTotal++;
                            previousHouseholdNumber = householdNumber; // Update the household number
                        } else if (householdNumber == previousHouseholdNumber) {
                            personCountTotal++;
                        }

                    default:
                        hhOutCountTotal++;
                        break;
                }
            }
            logger.info("  Read " + personCountTotal + " person records " + hhCountTotal + " in households in Bavaria from file: " + pumsFileName);
            //logger.info("  Read " + personCountTotal + " person records in Bavaria from file: " + pumsFileName);
            //logger.info("  Read " + hhOutCountTotal + " household records outside of Bavaria from file: " + pumsFileName);
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }


        //Obtain the household, person and frequency matrix from the records from Bavaria (once the totals are known)
        //Person variables
        int age[] = new int[personCountTotal];
        int gender[] = new int[personCountTotal];
        int occupation[] = new int[personCountTotal];
        int personId[] = new int[personCountTotal];
        int personHH[] = new int[personCountTotal];
        int personCount = 0;
        int personHHCount = 0;
        int hhCount = -1;
        //Household variables
        ageBracketsPerson = ResourceUtil.getIntegerArray(rb, PROPERTIES_MICRO_DATA_AGES);
        int hhTotal [] = new int[hhCountTotal];
        int hhSingle [] = new int [hhCountTotal];
        int hhMaleWorkers[] = new int[hhCountTotal];
        int hhFemaleWorkers[] = new int[hhCountTotal];
        int hhWorkers[] = new int[hhCountTotal];
        int hhMaleAge[][] = new int[hhCountTotal][ageBracketsPerson.length];
        int hhFemaleAge[][] = new int[hhCountTotal][ageBracketsPerson.length];
        int hhSize[] = new int[hhCountTotal];
        int hhSizeCount[] = new int[hhCountTotal];
        int hhIncome [] = new int[hhCountTotal];
        int hhId [] = new int[hhCountTotal];


        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                String recLander = recString.substring(0,2);
                switch (recLander) {
                    case "09": //Record from Bavaria //Record from Bavaria
                        int householdNumber = convertToInteger(recString.substring(7,9));
                        if (householdNumber != previousHouseholdNumber) {
                            hhCount++;
                            //logger.info("Household Count: "+ hhCount);
                            //logger.info("Number of females working: " + hhFemaleWorkers[hhCount]);
                            int householdPrivate = convertToInteger(recString.substring(313,314));
                            if (householdPrivate == 1) {hhTotal[hhCount] = 1;} //we only match private households
                            hhSize[hhCount] = convertToInteger(recString.substring(323, 324));
                            if (hhSize[hhCount] == 1) {hhSingle[hhCount] = 1;}
                            hhIncome[hhCount] = convertToInteger(recString.substring(297,299));
                            hhId[hhCount] = convertToInteger(recString.substring(2, 9));
                            previousHouseholdNumber = householdNumber; // Update the household number
                            if (hhCount > 1) {hhSizeCount[hhCount-1] = personHHCount;} else {hhSizeCount[hhCount] = hhSize[hhCount];}
                            personHHCount = 0;
                        }
                        age[personCount] = convertToInteger(recString.substring(25, 27));
                        gender[personCount] = convertToInteger(recString.substring(28, 29)); // 1: male; 2: female
                        //logger.info("Gender " + " of person " + hhPersonCounter + " on household " + hhCount + " is: " + gender[personCount] );
                        occupation[personCount] = convertToInteger(recString.substring(74, 75)); // 1: employed, 8: unemployed, empty: NA
                        personId[personCount] = convertToInteger(recString.substring(2,11));
                        personHH[personCount] = convertToInteger(recString.substring(2, 9));
                        int row = 0;
                        while (age[personCount] > ageBracketsPerson[row]){
                            row++;
                        }
                        if (gender[personCount] == 1) {
                            if (occupation[personCount] == 1){
                                hhMaleWorkers[hhCount]++;
                                hhWorkers[hhCount]++;
                            }
                            hhMaleAge[hhCount][row]++;
                        } else if (gender[personCount] == 2) {
                            if (occupation[personCount] == 1){
                                hhFemaleWorkers[hhCount]++;
                            }
                            hhFemaleAge[hhCount][row]++;
                            hhWorkers[hhCount]++;
                        }
                        personCount++;
                        personHHCount++;
                }
            }
            /*for (int col = 0; col < hhCountTotal; col++){
                for (int row = 0; row < ageBracketsPerson.length; row++){
                    //logger.info("Number of male workers on household " + col + " is " + hhMaleWorkers[col]);
                    //logger.info("Number of female workers on household " + col + " is " + hhFemaleWorkers[col]);
                    logger.info("Number of males of age " + ageBracketsPerson[row] + " on household " + col + " is " + hhMaleAge[col][row]);
                    logger.info("Number of females of age " + ageBracketsPerson[row] + " on household " + col + " is " + hhFemaleAge[col][row]);
                }
            }*/
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        //Copy attributes to the person micro data
        TableDataSet microPersons = new TableDataSet();
        microPersons.appendColumn(personHH,"ID");
        microPersons.appendColumn(age,"age");
        microPersons.appendColumn(gender,"gender");
        microPersons.appendColumn(occupation,"occupation");
        microPersons.appendColumn(personId,"personID");
        microDataPerson = microPersons;
        microDataPerson.buildIndex(microDataPerson.getColumnPosition("personID"));


        //Copy attributes to the household micro data
        TableDataSet microRecords = new TableDataSet();
        microRecords.appendColumn(hhId,"ID");
        microRecords.appendColumn(hhWorkers,"workers");
        //microRecords.appendColumn(hhFemaleWorkers,"femaleWorkers");
        //microRecords.appendColumn(hhMaleWorkers,"maleWorkers");
        for (int row = 0; row < ageBracketsPerson.length; row++){
            int[] ageMale = SiloUtil.obtainColumnFromArray(hhMaleAge,hhCountTotal,row);
            int[] ageFemale = SiloUtil.obtainColumnFromArray(hhFemaleAge,hhCountTotal,row);
            String nameMale = "male" + ageBracketsPerson[row];
            String nameFemale = "female" + ageBracketsPerson[row];
            microRecords.appendColumn(ageMale,nameMale);
            microRecords.appendColumn(ageFemale,nameFemale);
        }
        microRecords.appendColumn(hhIncome,"hhIncome");
        microRecords.appendColumn(hhSize,"hhSizeDeclared");
        microRecords.appendColumn(hhSizeCount,"hhSize");
        microDataHousehold = microRecords;
        microDataHousehold.buildIndex(microDataHousehold.getColumnPosition("ID"));


        //Copy attributes to the frequency matrix (IPU)
        TableDataSet microRecords1 = new TableDataSet();
        microRecords1.appendColumn(hhId,"ID");
        microRecords1.appendColumn(hhWorkers,"workers");
        microRecords1.appendColumn(hhMaleWorkers,"maleWorkers");
        microRecords1.appendColumn(hhFemaleWorkers,"femaleWorkers");
        for (int row = 0; row < ageBracketsPerson.length; row++){
            int[] ageMale = SiloUtil.obtainColumnFromArray(hhMaleAge,hhCountTotal,row);
            int[] ageFemale = SiloUtil.obtainColumnFromArray(hhFemaleAge,hhCountTotal,row);
            String nameMale = "male" + ageBracketsPerson[row];
            String nameFemale = "female" + ageBracketsPerson[row];
            microRecords1.appendColumn(ageMale,nameMale);
            microRecords1.appendColumn(ageFemale,nameFemale);
        }
        microRecords1.appendColumn(hhTotal,"hhTotal");
        microRecords1.appendColumn(hhSingle,"hhSingle");
        frequencyMatrix = microRecords1;

        String hhFileName = ("input/syntheticPopulation/DataPrepMicroHouseholds.csv");
        SiloUtil.writeTableDataSet(microDataHousehold, hhFileName);
        String popFileName = ("input/syntheticPopulation/DataPrepMicroPersons.csv");
        SiloUtil.writeTableDataSet(microDataPerson, popFileName);
        String freqFileName = ("input/syntheticPopulation/frequencyMatrix.csv");
        SiloUtil.writeTableDataSet(frequencyMatrix, freqFileName);

        logger.info("   Finished reading the micro data");
    }


    private void prepareFrequencyMatrix(){
        //Generate the frequency matrix given the types of household and person and calculate the initial set of weights, weighted sums and errors

        logger.info("   Starting to prepare the data for IPU");
        //Read the attributes at the area level (region attributes) and at the municipality level (household attributes). Read stopping criteria (max iterations and error threshold)
        attributesRegion = ResourceUtil.getArray(rb, PROPERTIES_REGION_ATTRIBUTES); //{"Region_1", "Region_2", "Region_3"};
        attributesHousehold = ResourceUtil.getArray(rb, PROPERTIES_HOUSEHOLD_ATTRIBUTES); //{"HH_1","HH_2","Person_1","Person_2","Person_3"};


        //Create the frequency matrix and marginals matrix at the area level (region matrix) and municipality level (household matrix)
            //For each microData record, identify what type of household is and how many persons of each person type has
            //This section will be substituted by the method when the microData from the census is available
            //Read the frequency matrix, marginals at area level and household level, max number of iterations and max error
        //frequencyMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_FREQUENCY_MATRIX));
        marginalsRegionMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_REGIONAL_MATRIX));
        marginalsHouseholdMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX));

        //Build indexes for the TableDataSet
        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        int[] cityID = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        cityIDs = new String[cityID.length];
        for (int row = 0; row < cityID.length; row++){cityIDs[row] = Integer.toString(cityID[row]);}
        int[] areaID = marginalsRegionMatrix.getColumnAsInt("ID_area");
        String[] areaIDs = new String[areaID.length];
        for (int row = 0; row < areaID.length; row++){areaIDs[row] = Integer.toString(areaID[row]);}
        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));
        marginalsHouseholdMatrix.buildIndex(marginalsHouseholdMatrix.getColumnPosition("ID_city"));
        marginalsRegionMatrix.buildIndex(marginalsRegionMatrix.getColumnPosition("ID_area"));


        //Create the collapsed matrix to optimize the calculation of the weighted sums
            //nonZero: TableDataSet that contains in each column the positions different than zero for each attribute (column label is the name of the attribute). It is unique across municipalities
            //nonZeroSize: TableDataSet that contains on the first row the number of cells different than zero for each attribute (column label is the name of the attribute). It is unique across municipalities
        TableDataSet nonZero = new TableDataSet();
        TableDataSet nonZeroSize = new TableDataSet();
        nonZero.appendColumn(microDataIds,"ID");
        int[] dummy0 = {0,0};
        nonZeroSize.appendColumn(dummy0,"ID");
        for(int attribute = 0; attribute < attributesRegion.length; attribute++){
            int[] nonZeroVector = new int[microDataIds.length];
            int[] sumNonZero = {0,0};
            for(int row = 1; row < microDataIds.length+1; row++){
                if (frequencyMatrix.getValueAt(row,attributesRegion[attribute])!=0){
                    nonZeroVector[sumNonZero[0]] = row;
                    sumNonZero[0] = sumNonZero[0] + 1;
                }
            }
            nonZero.appendColumn(nonZeroVector,attributesRegion[attribute]);
            nonZeroSize.appendColumn(sumNonZero,attributesRegion[attribute]);
        }
        for(int attribute = 0; attribute < attributesHousehold.length; attribute++) {
            int[] nonZeroVector = new int[microDataIds.length];
            int[] sumNonZero = {0, 0};
            for (int row = 1; row < microDataIds.length + 1; row++) {
                if (frequencyMatrix.getValueAt(row, attributesHousehold[attribute]) != 0) {
                    nonZeroVector[sumNonZero[0]] = row;
                    sumNonZero[0] = sumNonZero[0] + 1;
                }
            }
            nonZero.appendColumn(nonZeroVector, attributesHousehold[attribute]);
            nonZeroSize.appendColumn(sumNonZero, attributesHousehold[attribute]);
        }
        nonZero.buildIndex(nonZero.getColumnPosition("ID"));
        nonZeroSize.buildIndex(nonZeroSize.getColumnPosition("ID"));


        //create the weights table automatically and fill it with ones
            //weights: TableDataSet that contains in each column the weights for that area (column label is the municipality ID). The number of rows is equal to the number of microData records
        TableDataSet weights = new TableDataSet();
        weights.appendColumn(microDataIds,"ID");
        for(int area = 0; area < cityID.length; area++){
            float[] dummy = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(),1f);
            weights.appendColumn(dummy, cityIDs[area]);
        }
        weights.buildIndex(weights.getColumnPosition("ID"));


        //create the weighted sums and errors tables automatically, for area level
            //weightedSumRegion: TableDataSet that contains in each column the weighted sum at the areas level (column label is the region attribute name). The number of rows is equal to the number of rows of the marginals Region
            //errorsRegion: TableDataSet that contains in each column the error between the weighted sum and constrain (marginal) at the areas level (column label is the region attribute name).
        TableDataSet weightedSumsRegion = new TableDataSet();
        TableDataSet errorsRegion = new TableDataSet();
        weightedSumsRegion.appendColumn(areaID,"ID_area");
        errorsRegion.appendColumn(areaID,"ID_area");
        for(int attribute = 0; attribute < attributesRegion.length; attribute++){
            float[] dummyA1 = SiloUtil.createArrayWithValue(marginalsRegionMatrix.getRowCount(),0f);
            float[] dummyB1 = SiloUtil.createArrayWithValue(marginalsRegionMatrix.getRowCount(),0f);
            weightedSumsRegion.appendColumn(dummyA1,attributesRegion[attribute]);
            errorsRegion.appendColumn(dummyB1,attributesRegion[attribute]);
        }
        weightedSumsRegion.buildIndex(weightedSumsRegion.getColumnPosition("ID_area"));
        errorsRegion.buildIndex(errorsRegion.getColumnPosition("ID_area"));
        //Calculate the first set of weighted sums and errors, using initial weights equal to 1
        for(int attribute = 0; attribute < attributesRegion.length; attribute++){
            float weighted_sum = 0f;
            for(int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++){
                int positions = (int) nonZeroSize.getValueAt(1,attributesRegion[attribute]);
                weighted_sum = weighted_sum + SiloUtil.getWeightedSum(weights.getColumnAsFloat(cityIDs[area]),
                        frequencyMatrix.getColumnAsFloat(attributesRegion[attribute]),
                        nonZero.getColumnAsInt(attributesRegion[attribute]),positions);
            }
            weightedSumsRegion.setIndexedValueAt(1,attributesRegion[attribute],weighted_sum);
            float error = Math.abs((weightedSumsRegion.getValueAt(1,attributesRegion[attribute]) -
                    marginalsRegionMatrix.getValueAt(1,attributesRegion[attribute])) /
                    marginalsRegionMatrix.getValueAt(1,attributesRegion[attribute]));
            errorsRegion.setIndexedValueAt(1,attributesRegion[attribute],error);
        }


        //create the weighted sums and errors tables automatically, for household level
            //weightedSumHousehold: TableDataSet that contains in each column the weighted sum at each municipality (column label is the household-person attribute name). The number of rows is equal to the number of rows of the marginals Household (# municipalities)
            //errorsHousehold: TableDataSet that contains in each column the error between the weighted sum and constrain (marginal) at each municipality (column label is the household-person attribute name).
        TableDataSet weightedSumsHousehold = new TableDataSet();
        TableDataSet errorsHousehold = new TableDataSet();
        weightedSumsHousehold.appendColumn(cityID,"ID_city");
        errorsHousehold.appendColumn(cityID,"ID_city");
        for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
            float[] dummyA2 = SiloUtil.createArrayWithValue(marginalsHouseholdMatrix.getRowCount(),0f);
            float[] dummyB2 = SiloUtil.createArrayWithValue(marginalsHouseholdMatrix.getRowCount(),0f);
            weightedSumsHousehold.appendColumn(dummyA2,attributesHousehold[attribute]);
            errorsHousehold.appendColumn(dummyB2,attributesHousehold[attribute]);
        }
        weightedSumsHousehold.buildIndex(weightedSumsHousehold.getColumnPosition("ID_city"));
        errorsHousehold.buildIndex(errorsHousehold.getColumnPosition("ID_city"));
        //Calculate the first set of weighted sums and errors, using initial weights equal to 1
        for(int attribute = 0; attribute < attributesHousehold.length; attribute++) {
            for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                int positions = (int) nonZeroSize.getValueAt(1, attributesHousehold[attribute]);
                float weighted_sum = SiloUtil.getWeightedSum(weights.getColumnAsFloat(cityIDs[area]),
                        frequencyMatrix.getColumnAsFloat(attributesHousehold[attribute]),
                        nonZero.getColumnAsInt(attributesHousehold[attribute]),positions);
                weightedSumsHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attribute],weighted_sum);
                float error = Math.abs((weighted_sum -
                        marginalsHouseholdMatrix.getIndexedValueAt(cityID[area], attributesHousehold[attribute])) /
                        marginalsHouseholdMatrix.getIndexedValueAt(cityID[area], attributesHousehold[attribute]));
                errorsHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attribute],error);
            }
        }


        //Assign the tables that will be used on the next method (performIPU)
            //This may be changed using refractor and only having one name for each table, but I encountered problems if the tables are new
        weight = weights; //contains the weights, for each municipality
        nonZeroFrequency = nonZero; //contains the positions different than zero, for each attribute
        nonZeroNumber = nonZeroSize; //contains the number of positions different than zero, for each attribute
        weightedSumHousehold = weightedSumsHousehold; //contains the weighted sum per household-person attribute, for each municipality
        weightedSumRegion = weightedSumsRegion; //contains the weighted sum per region attribute, for each area (now equal to 1)
        errorHousehold = errorsHousehold; //contains the error per household-person attribute, for each municipality
        errorRegion = errorsRegion; //contains the error per region attribute, for each area (now equal to 1)

        logger.info("   Finished preparing the data for IPU");
    }


    private void performIPU(){
        //obtain the expansion factor (weight) of each entry of the micro data, using the marginal sums as constraints
        logger.info("   IPU started");

        //Get the labels for the indexed TableDataSet
        //int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        //int[] cityID = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        //int[] areaID = marginalsRegionMatrix.getColumnAsInt("ID_area");

        maxIterations= ResourceUtil.getIntegerProperty(rb, PROPERTIES_MAX_ITERATIONS,1000);
        maxError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MAX_ERROR, 0.0001);
        initialError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_INITIAL_ERROR, 1000);
        improvementError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_IMPROVEMENT_ERROR, 0.001);
        iterationError = ResourceUtil.getDoubleProperty(rb,PROPERTIES_IMPROVEMENT_ITERATIONS,2);


        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        int[] cityID = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        cityIDs = new String[cityID.length];
        for (int row = 0; row < cityID.length; row++){cityIDs[row] = Integer.toString(cityID[row]);}
        weight.buildIndex(weight.getColumnPosition("ID"));


        //Iterative loop. For each iteration, the IPU is performed for all attributes at the area level (region attributes) and at the municipalities level (household attributes)
        int iteration = 0;
        int finish = 0;
        while(iteration <= maxIterations && finish == 0){

            //Area level (region attributes)
            //update the weights, weighted sum and error for region attributes (they do not depend on each other because they are exclusive[if region 1 = 1, the other regions are = 0])
            for (int attribute = 0; attribute < attributesRegion.length; attribute++) {
                logger.info("Iteration: "+ iteration + ". Starting to calculate attribute " + attribute + " on the region");
                float factor = marginalsRegionMatrix.getIndexedValueAt(1, attributesRegion[attribute]) /
                        weightedSumRegion.getIndexedValueAt(1, attributesRegion[attribute]);
                float weighted_sum = 0f;
                for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                    for (int row = 0; row < nonZeroNumber.getValueAt(1, attributesRegion[attribute]); row++) {
                        int position = (int) nonZeroFrequency.getIndexedValueAt(microDataIds[row],attributesRegion[attribute]);
                        //logger.info(position + " area " + cityIDs[area]);
                        //logger.info(weight.getValueAt(position,cityIDs[area]));
                        float previous_weight = weight.getValueAt(position,cityIDs[area]);
                        weight.setValueAt(position,cityIDs[area],factor*previous_weight);
                   }
                    weighted_sum = weighted_sum + SiloUtil.getWeightedSum(weight.getColumnAsFloat(cityIDs[area]),
                            frequencyMatrix.getColumnAsFloat(attributesRegion[attribute]),
                            nonZeroFrequency.getColumnAsInt(attributesRegion[attribute]),
                            (int) nonZeroFrequency.getValueAt(1,attributesRegion[attribute]));
                }
                logger.info("Iteration: "+ iteration + ". Updating weighted sums of " + attribute + " on the region");
                weightedSumRegion.setIndexedValueAt(1,attributesRegion[attribute],weighted_sum);
                float error = Math.abs((weightedSumRegion.getValueAt(1,attributesRegion[attribute]) -
                        marginalsRegionMatrix.getValueAt(1,attributesRegion[attribute])) /
                        marginalsRegionMatrix.getValueAt(1,attributesRegion[attribute]));
                errorRegion.setIndexedValueAt(1,attributesRegion[attribute],error);
            }


            //Update the weighted sums and errors for household attributes, given the new weights for the area level
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
                logger.info("Iteration: "+ iteration + ". Updating weighted sums of " + attribute + " at the household level");
                for(int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++){
                    int positions = (int) nonZeroNumber.getValueAt(1,attributesHousehold[attribute]);
                    float weighted_sum = SiloUtil.getWeightedSum(weight.getColumnAsFloat(cityIDs[area]),
                            frequencyMatrix.getColumnAsFloat(attributesHousehold[attribute]),
                            nonZeroFrequency.getColumnAsInt(attributesHousehold[attribute]),positions);
                    weightedSumHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attribute],weighted_sum);
                    float error = Math.abs((weighted_sum -
                            marginalsHouseholdMatrix.getIndexedValueAt(cityID[area], attributesHousehold[attribute])) /
                            marginalsHouseholdMatrix.getIndexedValueAt(cityID[area], attributesHousehold[attribute]));;
                    errorHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attribute],error);
                }
            }


            //Municipalities level (household attributes)
            //For each attribute at the municipality level
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
                logger.info("       Iteration: "+ iteration + ". Starting to calculate weight of the attribute " + attribute + " at the household level.");
                //update the weights according to the weighted sum and constraint of the household attribute
                for(int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                    float factor = marginalsHouseholdMatrix.getIndexedValueAt(cityID[area],attributesHousehold[attribute]) /
                            weightedSumHousehold.getIndexedValueAt(cityID[area],attributesHousehold[attribute]);
                    for(int row = 0; row < nonZeroNumber.getValueAt(1, attributesHousehold[attribute]); row++){
                        int position = (int) nonZeroFrequency.getIndexedValueAt(microDataIds[row],attributesHousehold[attribute]);
                        float previous_weight = weight.getValueAt(position,cityIDs[area]);
                        weight.setValueAt(position,cityIDs[area],factor*previous_weight);
                    }
                }
                //update the weighted sums and errors of the region attributes, given the new weights
                for(int attributes = 0; attributes < attributesRegion.length; attributes++){

                    float weighted_sum = 0f;
                    for(int area = 0; area <  marginalsHouseholdMatrix.getRowCount(); area++){
                         weighted_sum = weighted_sum + SiloUtil.getWeightedSum(weight.getColumnAsFloat(cityIDs[area]),
                                 frequencyMatrix.getColumnAsFloat(attributesRegion[attributes]),
                                 nonZeroFrequency.getColumnAsInt(attributesRegion[attributes]),
                                 (int) nonZeroNumber.getValueAt(1,attributesRegion[attributes]));
                    }
                    weightedSumRegion.setIndexedValueAt(1,attributesRegion[attributes],weighted_sum);
                    float error = Math.abs((weightedSumRegion.getValueAt(1,attributesRegion[attributes]) -
                            marginalsRegionMatrix.getValueAt(1,attributesRegion[attributes])) /
                            marginalsRegionMatrix.getValueAt(1,attributesRegion[attributes]));
                    errorRegion.setIndexedValueAt(1,attributesRegion[attributes],error);
                }

                //update the weighted sums and errors of the household attributes, given the new weights
                for (int attributes = 0; attributes < attributesHousehold.length; attributes++){
                    logger.info("   Iteration: "+ iteration + ". Updating weighted sums of " + attributes + " at the household level");
                    for(int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++){
                        float weighted_sum = SiloUtil.getWeightedSum(weight.getColumnAsFloat(cityIDs[area]),
                                frequencyMatrix.getColumnAsFloat(attributesHousehold[attributes]),
                                nonZeroFrequency.getColumnAsInt(attributesHousehold[attributes]),
                                (int)nonZeroNumber.getValueAt(1,attributesHousehold[attributes]));
                        weightedSumHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attributes],weighted_sum);
                        float error = Math.abs((weighted_sum -
                                marginalsHouseholdMatrix.getIndexedValueAt(cityID[area], attributesHousehold[attributes]))/
                                marginalsHouseholdMatrix.getIndexedValueAt(cityID[area], attributesHousehold[attributes]));
                        errorHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attributes],error);
                    }
                }
            }


            //Calculate the maximum error among all the attributes (area and municipalities level). This will serve as one stopping criteria
            float maxErrorIteration = errorRegion.getIndexedValueAt(1,attributesRegion[0]);
            for(int attribute = 1; attribute < attributesRegion.length; attribute++){
                if(errorRegion.getIndexedValueAt(1,attributesRegion[attribute]) > maxErrorIteration){
                    maxErrorIteration = errorRegion.getIndexedValueAt(1,attributesRegion[attribute]);
                    maxErrorAttribute = "region_" + Integer.toString(attribute);
                }
            }
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
                for(int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                    if (errorHousehold.getIndexedValueAt(cityID[area], attributesHousehold[attribute]) > maxErrorIteration) {
                        maxErrorIteration = errorHousehold.getIndexedValueAt(cityID[area], attributesHousehold[attribute]);
                        maxErrorAttribute = "municipality_" + Integer.toString(cityID[area])+ "_household_" + Integer.toString(attribute);
                    }
                }
            }

            logger.info("   Iteration " + iteration + " completed. The maximum error is " + maxErrorIteration + " on the attribute: " + maxErrorAttribute);

            String freqFileName = ("input/syntheticPopulation/weigthsMatrix.csv");
            SiloUtil.writeTableDataSet(weight, freqFileName);

            //Stopping criteria: exceeds the maximum number of iterations or the maximum error is lower than the threshold
            if (maxErrorIteration < maxError){
                finish = 1;
                logger.info("   IPU finished after :" + iteration + " iterations with a maximum error of " + maxErrorIteration);
                iteration = maxIterations + 1;
            }
            else if ((iteration/iterationError) % 1 == 0){
                if (Math.abs((initialError-maxErrorIteration)/initialError) < improvementError) {
                    finish = 1;
                    logger.info("   IPU finished after :" + iteration + " iterations because the error does not improve. The maximum error is: " + maxErrorIteration);
                }
                else {
                    initialError = maxErrorIteration;
                    iteration = iteration + 1;
                }
            }
            else if (iteration == maxIterations) {
                finish = 1;
                logger.info("   IPU finished after the total number of iterations. The maximum error is: " + maxErrorIteration);
            }
            else{
                iteration = iteration + 1;
            }
        }


        //Print the results of IPU on the screen (weights, weighted sums and errors)
        /*
        for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
            for (int row = 0; row < microDataIds.length; row++){
                logger.info("Area: " + cityIDs[area] + ", micro data: " + microDataIds[row] + ", weight: " + weight.getIndexedValueAt(microDataIds[row],cityIDs[area]));
            }
        }
        for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++){
            for(int attribute = 0; attribute < attributesRegion.length; attribute++){
                logger.info("Area: " + cityIDs[area] + ", attribute: " + attributesRegion[attribute] + ", weighted sum: " + weightedSumRegion.getIndexedValueAt(1,attributesRegion[attribute]));
            }
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
                logger.info("Area: " + cityIDs[area] + ", attribute: " + attributesHousehold[attribute] + ", weighted sum: " + weightedSumHousehold.getIndexedValueAt(cityID[area],attributesHousehold[attribute]));
            }
        }
        for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++){
            for(int attribute = 0; attribute < attributesRegion.length; attribute++){
                logger.info("Area: " + cityIDs[area] + ", attribute: " + attributesRegion[attribute] + ", error: " + errorRegion.getIndexedValueAt(1,attributesRegion[attribute]));
            }
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
                logger.info("Area: " + cityIDs[area] + ", attribute: " + attributesHousehold[attribute] + ", error: " + errorHousehold.getIndexedValueAt(cityID[area],attributesHousehold[attribute]));
            }
        }
        */

        String freqFileName = ("input/syntheticPopulation/weigthsMatrix.csv");
        SiloUtil.writeTableDataSet(weight, freqFileName);

        logger.info("   IPU finished");
    }


    private void selectHouseholds(){
        //Generate the synthetic population using Monte Carlo (select the households according to the weight)
        //Once the household is selected, all the characteristics of the household will be copied (including the household members)
        logger.info("   Starting to generate households and persons.");


        int[] microDataIds = microDataHousehold.getColumnAsInt("ID");
        int[] cityID = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        int[] personCount = new int[microDataIds.length];
        personCount[0] = 1;
        for(int count = 1; count < microDataIds.length;count++){
            personCount[count] = personCount[count-1] + (int)microDataHousehold.getIndexedValueAt(microDataIds[count-1],"hhSize");
        }
        microDataHousehold.appendColumn(personCount,"personCount");
        String freqFileName = ("input/syntheticPopulation/newHouseholdFile.csv");
        SiloUtil.writeTableDataSet(microDataHousehold, freqFileName);


        float[] probability = {0.2f, 0.05f, 0.5f, 0.1f, 0.15f}; //this is the individual probability of each category of income (NOT CUMULATIVE)


        //scroll through the table with the micro data
        for (int count = 0; count < microDataIds.length; count++){
            for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                //multiply the number of households entry
                int expansionFactor = Math.round(weight.getIndexedValueAt(microDataIds[count],cityIDs[area]));
                int householdSize = (int)microDataHousehold.getIndexedValueAt(microDataIds[count],"hhSize");
                int householdType = (int)microDataHousehold.getIndexedValueAt(microDataIds[count],"hhIncome");
                int householdID = (int)microDataHousehold.getIndexedValueAt(microDataIds[count],"ID");
                if (expansionFactor > 0){
                    for(int row = 0; row < expansionFactor; row++){
                        int id = HouseholdDataManager.getNextHouseholdId();
                        new Household(id,householdID,cityID[area],householdSize,householdType); //(int id, int dwellingID, int homeZone, int hhSize, int autos)
                        for(int rowPerson = 0; rowPerson < householdSize; rowPerson++){
                            int idPerson = HouseholdDataManager.getNextPersonId();
                            int personCounter = (int) microDataHousehold.getIndexedValueAt(microDataIds[count],"personCount")+rowPerson;
                            int age = (int) microDataPerson.getValueAt(personCounter,"age");
                            int gender = (int) microDataPerson.getValueAt(personCounter,"gender");
                            int occupation = (int) microDataPerson.getValueAt(personCounter,"occupation");
                            int income = SiloUtil.select(probability); //income is selected according to the probabilities that are input
                            new Person(idPerson,id,age,gender,Race.white,occupation,cityID[area],income); //(int id, int hhid, int age, int gender, Race race, int occupation, int workplace, int income)
                        }
                    }
                }
            }
        }
        int households = HouseholdDataManager.getHighestHouseholdIdInUse();
        int persons = HouseholdDataManager.getHighestPersonIdInUse();
        logger.info("   Finished generating households and persons. A population of " + persons + " persons in " + households + " households was generated.");
    }


    private void readDwellings(){
        //Read the entry data from the dwellings of the region of Bavaria.
        logger.info(    "Reading the dwellings data file.");

        //Read the file from Corinna
        microDataDwelling = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MICRO_DATA_DWELLINGS));
        microDataDwelling.buildIndex(microDataDwelling.getColumnPosition("ID"));
    }


    private void selectDwelling(){
        //Based on the entry data, generate the dwellings. Assign the households to the dwellings.
        logger.info("   Starting to generate dwellings.");

        //Check for the different types of dwelling and assign the attributes that are not defined on the microData.

        for(int row = 0; row < microDataDwelling.getRowCount();row++){
            int id = RealEstateDataManager.getNextDwellingId();
            //note: for household Id should be picked from the households randomly (and then removed from the sample) or it is some relationship to keep?

            new Dwelling(id,0,0,DwellingType.MH,0,0,0,0,2000); //(int id, int zone, int hhId, DwellingType type, int bedrooms, int quality, int price, float restriction,int year)
        }
        logger.info("   Finished generating dwellings.");
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


}
