package edu.umd.ncsg.SyntheticPopulationGenerator;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloMuc;
import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.data.*;
import org.apache.log4j.Logger;

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
    protected static final String PROPERTIES_MICRO_DATA                   = "micro.data.households";
    protected static final String PROPERTIES_MICRO_DATA_PERSON            = "micro.data.persons";
    protected static final String PROPERTIES_RUN_SYNTHETIC_POPULATION     = "run.synth.pop.generator";

    protected static final String PROPERTIES_FREQUENCY_MATRIX             = "frequency.matrix.households";
    protected static final String PROPERTIES_MARGINALS_REGIONAL_MATRIX    = "marginals.region.matrix";
    protected static final String PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX   = "marginals.household.matrix";
    protected static final String PROPERTIES_MAX_ITERATIONS               = "max.iterations.ipu";
    protected static final String PROPERTIES_MAX_ERROR                    = "max.error.ipu";

    //To keep flexible the attributes that are used for the IPU
    protected static final String PROPERTIES_REGION_ATTRIBUTES            = "attributes.region";
    protected static final String PROPERTIES_HOUSEHOLD_ATTRIBUTES         = "attributes.household";
    protected static final String PROPERTIES_CONTROL_ATTRIBUTES_MC        = "control.attributes.MC";

    protected TableDataSet microDataHousehold;
    protected TableDataSet microDataPerson;
    protected TableDataSet frequencyMatrix;
    protected TableDataSet marginalsRegionMatrix;
    protected TableDataSet marginalsHouseholdMatrix;
    String[] cityIDs;

    protected String[] attributesRegion;
    protected String[] attributesHousehold;

    protected TableDataSet weight;
    protected TableDataSet nonZeroFrequency;
    protected TableDataSet nonZeroNumber;
    protected TableDataSet weightedSumHousehold;
    protected TableDataSet weightedSumRegion;
    protected TableDataSet errorHousehold;
    protected TableDataSet errorRegion;

    protected int maxIterations;
    protected double maxError;

    protected String[] controlAttributes;

    static Logger logger = Logger.getLogger(SyntheticPopDe.class);


    public SyntheticPopDe(ResourceBundle rb) {
        // Constructor
        this.rb = rb;
    }


    public void runSP(){
        //method to create the synthetic population
        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SYNTHETIC_POPULATION, false)) return;
        logger.info("Starting to create the synthetic population.");

        readDataSynPop(); //Read the micro data
        //readMarginalsSynPop(); //Read the marginal sums
        //defineHouseholdPersonTypes();//Define the different type of household and person
        prepareFrequencyMatrix();//Obtain the n-dimensional frequency matrix according to the defined types
        performIPU(); //IPU fitting to obtain the expansion factor for each entry of the micro data
        selectHouseholds(); //Monte Carlo selection process to generate the synthetic population
        summarizeData.writeOutSyntheticPopulation(rb, SiloUtil.getBaseYear());
    }


    private void readDataSynPop(){
        //method to read the synthetic population initial data
        microDataHousehold = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MICRO_DATA));
        microDataPerson = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MICRO_DATA_PERSON));
        microDataHousehold.buildIndex(microDataHousehold.getColumnPosition("ID"));
        microDataPerson.buildIndex(microDataPerson.getColumnPosition("personID"));
    }


    private void readMarginalsSynPop(){
        //method to read the marginals from the synthetic population
        //TableDataSet marginalsData = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_DATA));
    }


    private void defineHouseholdPersonTypes(){
        //method to generate the different types of household and person
    }


    private void prepareFrequencyMatrix(){
        //Generate the frequency matrix given the types of household and person and calculate the initial set of weights, weighted sums and errors

        //Read the attributes at the area level (region attributes) and at the municipality level (household attributes). Read stopping criteria (max iterations and error threshold)
        attributesRegion = ResourceUtil.getArray(rb, PROPERTIES_REGION_ATTRIBUTES); //{"Region_1", "Region_2", "Region_3"};
        attributesHousehold = ResourceUtil.getArray(rb, PROPERTIES_HOUSEHOLD_ATTRIBUTES); //{"HH_1","HH_2","Person_1","Person_2","Person_3"};
        maxIterations= ResourceUtil.getIntegerProperty(rb, PROPERTIES_MAX_ITERATIONS,1000);
        maxError = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MAX_ERROR, 0.0001);


        //Create the frequency matrix and marginals matrix at the area level (region matrix) and municipality level (household matrix)
            //For each microData record, identify what type of household is and how many persons of each person type has
            //This section will be substituted by the method when the microData from the census is available
            //Read the frequency matrix, marginals at area level and household level, max number of iterations and max error
        frequencyMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_FREQUENCY_MATRIX));
        marginalsRegionMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_REGIONAL_MATRIX));
        marginalsHouseholdMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX));

        //Build indexes for the TableDataSet
        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        int[] cityID = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        cityIDs = new String[cityID.length];
        for (int row = 0; row < cityID.length; row++){cityIDs[row] = Integer.toString(cityID[row]);}
        int[] areaID = marginalsRegionMatrix.getColumnAsInt("ID_area");
        String[] areaIDs = new String[areaID.length];
        for (int row=0;row<areaID.length;row++){areaIDs[row] = Integer.toString(areaID[row]);}
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
    }


    private void performIPU(){
        //obtain the expansion factor (weight) of each entry of the micro data, using the marginal sums as constraints
        logger.info("IPU started");

        //Get the labels for the indexed TableDataSet
        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        int[] cityID = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        int[] areaID = marginalsRegionMatrix.getColumnAsInt("ID_area");


        //Iterative loop. For each iteration, the IPU is performed for all attributes at the area level (region attributes) and at the municipalities level (household attributes)
        int iteration = 0;
        int finish = 0;
        while(iteration <= maxIterations && finish == 0){

            //Area level (region attributes)
            //update the weights, weighted sum and error for region attributes (they do not depend on each other because they are exclusive[if region 1 = 1, the other regions are = 0])
            for (int attribute = 0; attribute < attributesRegion.length; attribute++) {
                float factor = marginalsRegionMatrix.getIndexedValueAt(1, attributesRegion[attribute]) /
                        weightedSumRegion.getIndexedValueAt(1, attributesRegion[attribute]);
                float weighted_sum = 0f;
                for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                    for (int row = 0; row < nonZeroNumber.getValueAt(1, attributesRegion[attribute]); row++) {
                        int position = (int) nonZeroFrequency.getIndexedValueAt(microDataIds[row],attributesRegion[attribute]);
                        float previous_weight = weight.getIndexedValueAt(position,cityIDs[area]);
                        weight.setIndexedValueAt(position,cityIDs[area],factor*previous_weight);
                   }
                    weighted_sum = weighted_sum + SiloUtil.getWeightedSum(weight.getColumnAsFloat(cityIDs[area]),
                            frequencyMatrix.getColumnAsFloat(attributesRegion[attribute]),
                            nonZeroFrequency.getColumnAsInt(attributesRegion[attribute]),
                            (int) nonZeroFrequency.getValueAt(1,attributesRegion[attribute]));
                }
                weightedSumRegion.setIndexedValueAt(1,attributesRegion[attribute],weighted_sum);
                float error = Math.abs((weightedSumRegion.getValueAt(1,attributesRegion[attribute]) -
                        marginalsRegionMatrix.getValueAt(1,attributesRegion[attribute])) /
                        marginalsRegionMatrix.getValueAt(1,attributesRegion[attribute]));
                errorRegion.setIndexedValueAt(1,attributesRegion[attribute],error);
            }
            //Update the weighted sums and errors for household attributes, given the new weights for the area level
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
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
                //update the weights according to the weighted sum and constraint of the household attribute
                for(int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                    float factor = marginalsHouseholdMatrix.getIndexedValueAt(cityID[area],attributesHousehold[attribute]) /
                            weightedSumHousehold.getIndexedValueAt(cityID[area],attributesHousehold[attribute]);
                    for(int row = 0; row < nonZeroNumber.getValueAt(1, attributesHousehold[attribute]); row++){
                        int position = (int) nonZeroFrequency.getIndexedValueAt(microDataIds[row],attributesHousehold[attribute]);
                        float previous_weight = weight.getIndexedValueAt(position,cityIDs[area]);
                        weight.setIndexedValueAt(position,cityIDs[area],factor*previous_weight);
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
                }
            }
            for(int attribute = 0; attribute < attributesHousehold.length; attribute++){
                for(int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                    if (errorHousehold.getIndexedValueAt(cityID[area], attributesHousehold[attribute]) > maxErrorIteration) {
                        maxErrorIteration = errorHousehold.getIndexedValueAt(cityID[area], attributesHousehold[attribute]);
                    }
                }
            }


            //Stopping criteria: exceeds the maximum number of iterations or the maximum error is lower than the threshold
            if (maxErrorIteration < maxError){
                finish = 1;
                logger.info("IPU finished after :" + iteration + " iterations with a maximum error of " + maxErrorIteration);
                iteration = maxIterations+1;
            }
            else if (iteration == maxIterations) {
                finish = 1;
                logger.info("IPU finished after the total number of iterations. The maximum error is: " + maxErrorIteration);
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
        logger.info("IPU finished");
    }


    private void selectHouseholds(){
        //Generate the synthetic population using Monte Carlo (select the households according to the weight)
        //Once the household is selected, all the characteristics of the household will be copied (including the household members)
        logger.info("Generating base year households.");

        int[] microDataIds = microDataHousehold.getColumnAsInt("ID");
        int[] cityID = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        int[] personCount = new int[microDataIds.length];
        personCount[0] = 1;
        for(int count = 1; count < microDataIds.length;count++){
            personCount[count] = personCount[count-1] + (int)microDataHousehold.getIndexedValueAt(microDataIds[count-1],"HH_size");
        }
        microDataHousehold.appendColumn(personCount,"personCount");


        float[] probability = {0.2f, 0.05f, 0.5f, 0.1f, 0.15f}; //this is the individual probability of each category of income (NOT CUMULATIVE)


        //scroll through the table with the micro data
        for (int count = 0; count < microDataIds.length; count++){
            for (int area = 0; area < marginalsHouseholdMatrix.getRowCount(); area++) {
                //multiply the number of households entry
                int expansionFactor = Math.round(weight.getIndexedValueAt(microDataIds[count],cityIDs[area]));
                int householdSize = (int)microDataHousehold.getIndexedValueAt(microDataIds[count],"HH_size");
                int householdType = (int)microDataHousehold.getIndexedValueAt(microDataIds[count],"HH_type");
                int householdRegion = (int)microDataHousehold.getIndexedValueAt(microDataIds[count],"Region");
                if (expansionFactor > 0){
                    for(int row = 0; row < expansionFactor; row++){
                        int id = HouseholdDataManager.getNextHouseholdId();
                        new Household(id,householdRegion,cityID[area],householdSize,householdType); //(int id, int dwellingID, int homeZone, int hhSize, int autos)
                        for(int rowPerson = 0; rowPerson < householdSize; rowPerson++){
                            int idPerson = HouseholdDataManager.getNextPersonId();
                            int personCounter = (int) microDataHousehold.getIndexedValueAt(microDataIds[count],"personCount")+rowPerson;
                            int age = (int) microDataPerson.getIndexedValueAt(personCounter,"Age");
                            int income = SiloUtil.select(probability); //income is selected according to the probabilities that are input
                            new Person(idPerson,id,age,cityID[area],Race.white,0,0,income); //(int id, int hhid, int age, int gender, Race race, int occupation, int workplace, int income)
                        }
                    }
                }
            }
        }
        logger.info("Finished creating the synthetic population.");
    }


}
