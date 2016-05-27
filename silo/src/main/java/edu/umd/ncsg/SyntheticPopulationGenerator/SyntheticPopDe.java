package edu.umd.ncsg.SyntheticPopulationGenerator;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloMuc;
import edu.umd.ncsg.SiloUtil;
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
    protected static final String PROPERTIES_RUN_SYNTHETIC_POPULATION     = "run.synth.pop.generator";

    protected static final String PROPERTIES_FREQUENCY_MATRIX             = "frequency.matrix.households";
    protected static final String PROPERTIES_MARGINALS_REGIONAL_MATRIX    = "marginals.region.matrix";
    protected static final String PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX   = "marginals.household.matrix";
    protected static final String PROPERTIES_WEIGHTS                      = "weights.matrix";
    protected static final String PROPERTIES_WEIGHTED_SUM_HOUSEHOLD        = "weighted.sum.household.matrix";
    protected static final String PROPERTIES_WEIGHTED_SUM_REGION           = "weighted.sum.region.matrix";
    protected static final String PROPERTIES_ERROR_HOUSEHOLD              = "error.household.matrix";
    protected static final String PROPERTIES_ERROR_REGION                 = "error.region.matrix";

    //To keep flexible the attributes that are used for the IPU
    protected static final String PROPERTIES_REGION_ATTRIBUTES            = "attributes.region";
    protected static final String PROPERTIES_HOUSEHOLD_ATTRIBUTES         = "attributes.household";

    protected TableDataSet frequencyMatrix;
    protected TableDataSet marginalsRegionMatrix;
    protected TableDataSet marginalsHouseholdMatrix;

    protected String[] attributesRegion;
    protected String[] attributesHousehold;

    protected TableDataSet weight;
    protected TableDataSet nonZeroFrequency;
    protected TableDataSet nonZeroNumber;
    protected TableDataSet weightedSumHousehold;
    protected TableDataSet weightedSumRegion;
    protected TableDataSet errorHousehold;
    protected TableDataSet errorRegion;

    protected int maxIterations=1000;
    protected int maxAttributesRegion;
    protected int maxAttributesHousehold;
    protected int maxHouseholdRecord;
    protected int maxRegionRecord;


    static Logger logger = Logger.getLogger(SyntheticPopDe.class);

    public SyntheticPopDe(ResourceBundle rb) {
        // Constructor
        this.rb = rb;
    }


    public void runSP(){
        //method to create the synthetic population
        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SYNTHETIC_POPULATION, false)) return;
        logger.info("Starting to create the synthetic population.");

        //readDataSynPop(); //Read the micro data
        //readMarginalsSynPop(); //Read the marginal sums
        //defineHouseholdPersonTypes();//Define the different type of household and person
        prepareFrequencyMatrix();//Obtain the n-dimensional frequency matrix according to the defined types
        performIPU(); //IPU fitting to obtain the expansion factor for each entry of the micro data
        //generateSynPopMonteCarlo(); //Monte Carlo selection process to generate the synthetic population
    }


    private void readDataSynPop(){
        //method to read the synthetic population initial data

        TableDataSet microData = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MICRO_DATA));
        logger.info(microData.getColumnLabel(2));

        // Simple scrolling through a table
        for (int row = 1; row < microData.getRowCount(); row++) {
            logger.info("Found ID: " + microData.getValueAt(row, "ID"));
        }

        // or build an index for direct access of selected records
        microData.buildIndex(microData.getColumnPosition("ID"));
        int[] indices = microData.getColumnAsInt("ID");
        for (int thisId: indices) {
            logger.info("Read ID: " + microData.getIndexedValueAt(thisId, "ID"));
        }

    }


    /*private void readMarginalsSynPop(){
        //method to read the marginals from the synthetic population
        TableDataSet marginalsData = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_DATA));
    }
    */

    private void defineHouseholdPersonTypes(){
        //method to generate the different types of household and person

    }


    private void prepareFrequencyMatrix(){
        //generate the frequency matrix given the types of household and person and calculate the initial set of weights and weighted sums

        //For each entry record, identify what type of household is and how many person has of each person type


        //Read the frequency matrix, marginals, initial weights, weighted sums and errors
        frequencyMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_FREQUENCY_MATRIX));
        marginalsRegionMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_REGIONAL_MATRIX));
        marginalsHouseholdMatrix = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_HOUSEHOLD_MATRIX));

        attributesRegion = ResourceUtil.getArray(rb, PROPERTIES_REGION_ATTRIBUTES); //{"Region_1", "Region_2", "Region_3"};
        attributesHousehold = ResourceUtil.getArray(rb, PROPERTIES_HOUSEHOLD_ATTRIBUTES); //{"HH_1","HH_2","Person_1","Person_2","Person_3"};
        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");

        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));
        marginalsHouseholdMatrix.buildIndex(marginalsHouseholdMatrix.getColumnPosition("ID_city"));
        marginalsRegionMatrix.buildIndex(marginalsRegionMatrix.getColumnPosition("ID_area"));


        //Obtain the number of records, attribute names and number of attributes
        maxHouseholdRecord = frequencyMatrix.getRowCount();
        maxRegionRecord = marginalsHouseholdMatrix.getRowCount();
        maxAttributesRegion = marginalsRegionMatrix.getColumnCount()-2;
        maxAttributesHousehold = marginalsHouseholdMatrix.getColumnCount()-2;

        int[] cityID = marginalsHouseholdMatrix.getColumnAsInt("ID_city");
        String[] cityIDs = new String[cityID.length];
        for (int row = 0; row < cityID.length; row++){cityIDs[row] = Integer.toString(cityID[row]);}
        int[] areaID = marginalsRegionMatrix.getColumnAsInt("ID_area");
        String[] areaIDs = new String[areaID.length];
        for (int row=0;row<areaID.length;row++){areaIDs[row] = Integer.toString(areaID[row]);}

        /*for(int row = 0; row < marginalsHouseholdMatrix.getRowCount();row++){
            for(int att = 0; att < marginalsHouseholdMatrix.getColumnCount(); att++){
                logger.info(marginalsHouseholdMatrix.getIndexedValueAt(cityID[row], attributesHousehold[att]));
            }
        }*/


        //Collapsed matrix with the label of the columns equal to the name of the attribute
        //Table with the number of positions different than zero for each attribute (by name)
        TableDataSet nonZero = new TableDataSet();
        TableDataSet nonZeroSize = new TableDataSet();
        nonZero.appendColumn(microDataIds,"ID");
        int[] dummy0 = {0,0};
        nonZeroSize.appendColumn(dummy0,"ID");
        for(int attribute = 0; attribute < maxAttributesRegion; attribute++){
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
        for(int attribute = 0; attribute < maxAttributesHousehold; attribute++) {
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
        //To check that it makes it right
        /*for(int attribute = 0; attribute < maxAttributesRegion; attribute++){
            for(int row=1; row<microDataIds.length+1;row++){
                logger.info("Row: " + row + ", Attribute: " + nonZero.getColumnLabel(attribute+2) + ", Position: " + nonZero.getValueAt(row,attributesRegion[attribute]));
            }
            logger.info("Attribute: " + nonZeroSize.getColumnLabel(attribute+2) + ", Number non Zero: " + nonZeroSize.getValueAt(1,attributesRegion[attribute]));
        }
        for(int attribute = 0; attribute < maxAttributesHousehold; attribute++){
            for(int row=1; row<microDataIds.length+1;row++){
                logger.info("Row: " + row + ", Attribute: " + nonZero.getColumnLabel(attribute+2+maxAttributesRegion) + ", Position: " + nonZero.getValueAt(row,attributesHousehold[attribute]));
            }
            logger.info("Attribute: " + nonZeroSize.getColumnLabel(attribute+2+maxAttributesRegion) + ", Number non Zero: " + nonZeroSize.getValueAt(1,attributesHousehold[attribute]));
        }*/


        //create the weights table automatically and fill it with ones
        TableDataSet weights = new TableDataSet();
        weights.appendColumn(microDataIds,"ID");
        float[] dummy = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(),1f);
        for(int area = 0; area < cityID.length; area++){
            weights.appendColumn(dummy, cityIDs[area]);
        }
        weights.buildIndex(weights.getColumnPosition("ID"));
        /* To check that it works
        for(int row = 1; row < weights.getRowCount()+1;row++){
            for(int col=0; col < cityIDs.length;col++){
                logger.info("ID: " + row + ", Weight: " + weights.getValueAt(row,cityIDs[col]));
            }
        }*/


        //create the weighted sums and errors tables automatically, for area level
        TableDataSet weightedSumsRegion = new TableDataSet();
        TableDataSet errorsRegion = new TableDataSet();
        weightedSumsRegion.appendColumn(areaID,"ID_area");
        errorsRegion.appendColumn(areaID,"ID_area");
        float[] dummyA1 = SiloUtil.createArrayWithValue(marginalsRegionMatrix.getRowCount(),0f);
        float[] dummyB1 = SiloUtil.createArrayWithValue(marginalsRegionMatrix.getRowCount(),0f);
        for(int attribute = 0; attribute < maxAttributesRegion; attribute++){
            weightedSumsRegion.appendColumn(dummyA1,attributesRegion[attribute]);
            errorsRegion.appendColumn(dummyB1,attributesRegion[attribute]);
        }
        weightedSumsRegion.buildIndex(weightedSumsRegion.getColumnPosition("ID_area"));
        errorsRegion.buildIndex(errorsRegion.getColumnPosition("ID_area"));
        //Calculate the first set of weighted sums and errors, using initial weights equal to 1
        for(int attribute = 0; attribute < maxAttributesRegion; attribute++){
            float sum = 0f;
            float error = 0f;
            for(int area = 0; area < maxRegionRecord; area++){
                int positions = (int) nonZeroSize.getValueAt(1,attributesRegion[attribute]);
                sum=sum+SiloUtil.getWeightedSum(weights.getColumnAsFloat(cityIDs[area]),frequencyMatrix.getColumnAsFloat(attributesRegion[attribute]),nonZero.getColumnAsInt(attributesRegion[attribute]),positions);
            }
            weightedSumsRegion.setIndexedValueAt(1,attributesRegion[attribute],sum);
            error = Math.abs((weightedSumsRegion.getValueAt(1,attributesRegion[attribute])-marginalsRegionMatrix.getValueAt(1,attributesRegion[attribute]))/marginalsRegionMatrix.getValueAt(1,attributesRegion[attribute]));
            errorsRegion.setIndexedValueAt(1,attributesRegion[attribute],error);
            //logger.info("Sum calculated:" + sum);
            //logger.info("Error calculated: "+ error);
        }
        //To check that it works
        /*for(int attribute=0; attribute < maxAttributesRegion; attribute++){
            logger.info("Weighted sum assigned (1):" + weightedSumsRegion.getIndexedValueAt(1,attributesRegion[attribute]));
            logger.info("Error assigned (1)" + errorsRegion.getIndexedValueAt(1,attributesRegion[attribute]));
        }*/


        //create the weighted sums and errors tables automatically, for household level
        TableDataSet weightedSumsHousehold = new TableDataSet();
        TableDataSet errorsHousehold = new TableDataSet();
        weightedSumsHousehold.appendColumn(cityID,"ID_city");
        errorsHousehold.appendColumn(cityID,"ID_city");
        float[] dummyA2 = SiloUtil.createArrayWithValue(marginalsHouseholdMatrix.getRowCount(),0f);
        float[] dummyB2 = SiloUtil.createArrayWithValue(marginalsHouseholdMatrix.getRowCount(),0f);
        for(int attribute = 0; attribute < maxAttributesHousehold; attribute++){
            weightedSumsHousehold.appendColumn(dummyA2,attributesHousehold[attribute]);
            errorsHousehold.appendColumn(dummyB2,attributesHousehold[attribute]);
        }
        weightedSumsHousehold.buildIndex(weightedSumsHousehold.getColumnPosition("ID_city"));
        errorsHousehold.buildIndex(errorsHousehold.getColumnPosition("ID_city"));
        //Calculate the first set of weighted sums and errors, using initial weights equal to 1
        for(int attribute = 0; attribute < maxAttributesHousehold; attribute++) {
            for (int area = 0; area < maxRegionRecord; area++) {
                float sum = 0f;
                float error = 0f;
                int positions = (int) nonZeroSize.getValueAt(1, attributesHousehold[attribute]);
                sum = SiloUtil.getWeightedSum(weights.getColumnAsFloat(cityIDs[area]),frequencyMatrix.getColumnAsFloat(attributesHousehold[attribute]),nonZero.getColumnAsInt(attributesHousehold[attribute]),positions);
                //logger.info(marginalsHouseholdMatrix.getIndexedValueAt(0, attributesHousehold[attribute]));
                weightedSumsHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attribute],sum);
                error = Math.abs((sum - marginalsHouseholdMatrix.getIndexedValueAt(cityID[area], attributesHousehold[attribute])) / marginalsHouseholdMatrix.getIndexedValueAt(cityID[area], attributesHousehold[attribute]));
                errorsHousehold.setIndexedValueAt(cityID[area],attributesHousehold[attribute],error);
                //logger.info("Attribute: " + attribute + ", Area: " + area + ", Weighted sum:" + sum);
                //logger.info("Attribute: " + attribute + ", Area: " + area + ", Error:" + error);
                //logger.info("Weighted sum (1):" + weightedSumsHousehold.getIndexedValueAt(cityID[area], attributesHousehold[attribute]));
                //logger.info("Error (1)" + errorsHousehold.getIndexedValueAt(cityID[area], attributesHousehold[attribute]));
            }
        }
        /*
        -------------PREVIOUS VERSION----------------------------
        //Collapse the matrix to optimize the IPU process.
        //We obtain the rows that are different than zero for each attribute. It is common for all areas.
        nonZeroMatrix = new int[maxAttributesRegion+maxAttributesHousehold+1][];
        for (int attribute = 1; attribute < maxAttributesRegion+maxAttributesHousehold+1;attribute++){
            int[] nonZeroVector = new int [maxHouseholdRecord];
            int sumNonZero = 0;
            for(int row = 1; row< maxHouseholdRecord+1; row++){
                if(frequencyMatrix.getValueAt(row,attribute+1)!=0){
                    nonZeroVector[sumNonZero] = row;
                    sumNonZero =sumNonZero+1;
                }
            }
            nonZeroMatrix[attribute-1] = new int[sumNonZero-1];
            nonZeroMatrix[attribute-1] = Arrays.copyOfRange(nonZeroVector,0,sumNonZero);
        }
        logger.info("Frequency matrix prepared");

        //Calculate initial weighted sums and errors for household attributes
        for(int attribute=1; attribute<maxAttributesHousehold+1;attribute++) {
            for(int area=1; area<maxRegionRecord+1; area++) {
                weightedSumHousehold.setValueAt(area, attribute, SiloUtil.getWeightedSum(weightsMatrix.getColumnAsFloat(1 + area), frequencyMatrix.getColumnAsFloat(1 +maxAttributesRegion +attribute), nonZeroMatrix[-1 + maxAttributesRegion+attribute]));
                errorHousehold.setValueAt(area,attribute,Math.abs(weightedSumHousehold.getValueAt(area,attribute)-marginalsHouseholdMatrix.getValueAt(area,attribute))/marginalsHouseholdMatrix.getValueAt(area,attribute));
                //logger.info("Area: " + area + " , Attribute: " + attribute + ", weighted sum is: " + weightedSumHousehold.getValueAt(area,attribute));
                //logger.info("Area: " + area + " , Attribute: " + attribute + ", error is: " + errorHousehold.getValueAt(area,attribute));
            }
        }

        //Calculate initial weighted sums and errors for region attributes
        for(int attribute=1; attribute<maxAttributesRegion+1;attribute++){
            weightedSumRegion.setValueAt(1,attribute,0);
            for(int area=1; area<maxRegionRecord+1;area++) {
                weightedSumRegion.setValueAt(1, attribute, weightedSumRegion.getValueAt(1, attribute)+SiloUtil.getWeightedSum(weightsMatrix.getColumnAsFloat(1 + area), frequencyMatrix.getColumnAsFloat(1 + attribute), nonZeroMatrix[-1 + attribute]));
            }
            errorRegion.setValueAt(1, attribute, Math.abs(weightedSumRegion.getValueAt(1, attribute) - marginalsRegionMatrix.getValueAt(1, attribute)) / marginalsRegionMatrix.getValueAt(1, attribute));
            //logger.info("Attribute: " + attribute + " , weighted sum: " + weightedSumRegion.getValueAt(1, attribute));
            //logger.info("Attribute: " + attribute + ", error is: " + errorRegion.getValueAt(1, attribute));
        }
        */

        //Assign the tables that will be used on the next method
        weight = weights;
        nonZeroFrequency = nonZero;
        nonZeroNumber = nonZeroSize;
        weightedSumHousehold = weightedSumsHousehold;
        weightedSumRegion = weightedSumsRegion;
        errorHousehold = errorsHousehold;
        errorRegion = errorsRegion;

    }


    private void performIPU(){
        //obtain the expansion factor (weight) of each entry of the micro data, using the marginal sums as constraints
        logger.info("IPU started");
        for(int iteration = 0; iteration < maxIterations; iteration++){
            //Region attributes
            for(int attribute = 0; attribute < maxAttributesRegion; attribute++){
                //update the weight
                float factor = marginalsRegionMatrix.getIndexedValueAt(1,attributesRegion[attribute])/weightedSumRegion.getIndexedValueAt(1,attributesRegion[attribute]);
                for (int area = 0; area < maxRegionRecord; area++){
                    for(int row = 0; row < nonZeroNumber.getValueAt(1,attributesRegion[attribute]); row++){
                        //Check the call to the previous weight
                        //weight.setIndexedValueAt(nonZeroFrequency.getIndexedValueAt(1,attributesRegion[attribute],area+1,factor*weight.getIndexedValueAt(nonZeroFrequency.getIndexedValueAt(attributesRegion[attribute]))));
                    }
                }




            }



            /*---------- PREVIOUS VERSION---------------
            //Region attributes
            for(int attribute=1; attribute<maxAttributesRegion+1;attribute++){
                //update the weight
                float factor=marginalsRegionMatrix.getValueAt(1,attribute)/weightedSumRegion.getValueAt(1,attribute);
                //logger.info(factor);
                for(int area=1; area<maxRegionRecord+1; area++){
                    for(int row=0; row<nonZeroMatrix[attribute-1].length;row++){
                        weightsMatrix.setValueAt(nonZeroMatrix[attribute-1][row],1+area,factor * weightsMatrix.getValueAt(nonZeroMatrix[attribute-1][row],1+area));
                    }
                }


                //recalculate weighted sums and errors for household attributes
                for(int attributes=1; attributes<maxAttributesHousehold+1;attributes++) {
                    for(int area=1; area<maxRegionRecord+1; area++) {
                        weightedSumHousehold.setValueAt(area, attributes, SiloUtil.getWeightedSum(weightsMatrix.getColumnAsFloat(1 + area), frequencyMatrix.getColumnAsFloat(1 +maxAttributesRegion +attributes), nonZeroMatrix[-1 + maxAttributesRegion+attributes]));
                        errorHousehold.setValueAt(area,attributes,Math.abs(weightedSumHousehold.getValueAt(area,attributes)-marginalsHouseholdMatrix.getValueAt(area,attributes))/marginalsHouseholdMatrix.getValueAt(area,attributes));
                    }
                }


                //recalculate weighted sums and errors for region attributes
                for(int attributes=1; attributes<maxAttributesRegion+1;attributes++){
                    weightedSumRegion.setValueAt(1,attributes,0);
                    for(int area=1; area<maxRegionRecord+1;area++) {
                        weightedSumRegion.setValueAt(1, attributes, weightedSumRegion.getValueAt(1, attributes)+SiloUtil.getWeightedSum(weightsMatrix.getColumnAsFloat(1 + area), frequencyMatrix.getColumnAsFloat(1 + attributes), nonZeroMatrix[-1 + attributes]));
                    }
                    errorRegion.setValueAt(1, attributes, Math.abs(weightedSumRegion.getValueAt(1, attributes) - marginalsRegionMatrix.getValueAt(1, attributes)) / marginalsRegionMatrix.getValueAt(1, attributes));
                }
            }


            //Household attributes
            for(int attribute=1; attribute<maxAttributesHousehold+1;attribute++) {
                //update the weight by area
                for (int area = 1; area < maxRegionRecord + 1; area++) {
                    float factor = marginalsHouseholdMatrix.getValueAt(area, attribute) / weightedSumHousehold.getValueAt(area, attribute);
                    //logger.info(factor);
                    for (int row = 0; row < nonZeroMatrix[attribute - 1+maxAttributesRegion].length; row++) {
                        weightsMatrix.setValueAt(nonZeroMatrix[attribute - 1+maxAttributesRegion][row], 1 + area, factor * weightsMatrix.getValueAt(nonZeroMatrix[attribute - 1+maxAttributesRegion][row], 1 + area));
                    }
                }

                //recalculate weighted sums and errors for household attributes
                for (int attributes = 1; attributes < maxAttributesHousehold + 1; attributes++) {
                    for (int area = 1; area < maxRegionRecord + 1; area++) {
                        weightedSumHousehold.setValueAt(area, attributes, SiloUtil.getWeightedSum(weightsMatrix.getColumnAsFloat(1 + area), frequencyMatrix.getColumnAsFloat(1 + maxAttributesRegion + attributes), nonZeroMatrix[-1 + maxAttributesRegion + attributes]));
                        errorHousehold.setValueAt(area, attributes, Math.abs(weightedSumHousehold.getValueAt(area, attributes) - marginalsHouseholdMatrix.getValueAt(area, attributes)) / marginalsHouseholdMatrix.getValueAt(area, attributes));
                    }
                }


                //recalculate weighted sums and errors for region attributes
                for (int attributes = 1; attributes < maxAttributesRegion + 1; attributes++) {
                    weightedSumRegion.setValueAt(1, attributes, 0);
                    for (int area = 1; area < maxRegionRecord + 1; area++) {
                        weightedSumRegion.setValueAt(1, attributes, weightedSumRegion.getValueAt(1, attributes) + SiloUtil.getWeightedSum(weightsMatrix.getColumnAsFloat(1 + area), frequencyMatrix.getColumnAsFloat(1 + attributes), nonZeroMatrix[-1 + attributes]));
                    }
                    errorRegion.setValueAt(1, attributes, Math.abs(weightedSumRegion.getValueAt(1, attributes) - marginalsRegionMatrix.getValueAt(1, attributes)) / marginalsRegionMatrix.getValueAt(1, attributes));
                }
            }*/

        }
        //At the end of the iteration, compare the average error with the error threshold


        //Just to check that the IPU was performed correctly
        /*for(int area=1;area<maxRegionRecord+1;area++) {
            for (int row = 1; row < maxHouseholdRecord + 1; row++) {
                logger.info("Area: "+area+" Household: " + row + ", weight: " + weightsMatrix.getValueAt(row,1+area));
            }
        }
        for(int area=1;area<maxRegionRecord+1;area++) {
            for (int row = 1; row < maxAttributesHousehold + 1; row++) {
                logger.info("Area: "+area+" Attribute: " + row + ", weighted sum: " + weightedSumHousehold.getValueAt(area,row));
            }
        }*/
        logger.info("IPU finished");
    }


    private void generateSynPopMonteCarlo(){
        //method to select the households of the synthetic population according to the expansion factor
        logger.info("Starting to select the synthetic population.");
        //While fin == 0 (household counter smaller than household population)
        //draw one random number and compare with the cumulative distribution of households to select the household

        //check if the person count is lower than the marginal sum.
        // If so, copy the characteristics of the household

        //If not, withdraw this household for consideration and recalculate the household weight.

        logger.info("Finished creating the synthetic population.");
    }

}
