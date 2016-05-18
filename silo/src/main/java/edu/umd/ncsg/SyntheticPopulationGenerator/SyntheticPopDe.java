package edu.umd.ncsg.SyntheticPopulationGenerator;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloMuc;
import edu.umd.ncsg.SiloUtil;
import org.apache.log4j.Logger;

import java.util.ResourceBundle;

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
    protected static final String PROPERTIES_MARGINALS_DATA               = "marginals.data.households";


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
        readMarginalsSynPop(); //Read the marginal sums
        defineHouseholdPersonTypes();//Define the different type of household and person
        logger.info("IPU started");
        generateFrequencyMatrix(/*microData*/);//Obtain the n-dimensional frequency matrix according to the defined types
        performIPU(); //IPU fitting to obtain the expansion factor for each entry of the micro data
        logger.info("IPU finished");
        logger.info("Starting to select the synthetic population.");
        generateSynPopMonteCarlo(); //Monte Carlo selection process to generate the synthetic population
        logger.info("Finished creating the synthetic population.");

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


    private void readMarginalsSynPop(){
        //method to read the marginals from the synthetic population

        TableDataSet marginalsData = SiloUtil.readCSVfile(rb.getString(PROPERTIES_MARGINALS_DATA));
        //NEED TO ADD THIS VALUE TO THE SILOMUC PROPERTIES FILE

        //Do I need to return this table to be used on the next methods?

    }


    private void defineHouseholdPersonTypes(){
        //method to generate the different types of household and person

    }


    private void generateFrequencyMatrix(/*TableDataSet microData*/){//NOT QUITE SURE if the entry data needs to be called there
        //method to generate the frequency matrix given the types of household and person

        //For each entry record, identify what type of household is and how many person has of each person type
        /*
        for (int row = 1; row < microData.getRowCount();row++){

        }
        */

        //Add the weight column to the n-dimensional frequency matrix

        //Calculate weighted sum and error of each attribute. Calculate the average error


    }


    private void performIPU(){
        //method to obtain the expansion factor (weight) of each entry of the micro data, using the marginal sums as constraints
        //For each iteration
            //For each attribute of region level
                //Update the weight multiplying the previous weight by the ratio between constraint and weighted sum

                //Update the weighted sum of all the frequency matrix

                //Update the error of each attribute and the average error

            //For each region
                //For each attribute of household and person levels
                    //Update the weight multiplying the previous weight by the ratio between constraint and weighted sum

                    //Update the weighted sum of all the frequency matrix

                    //Update the error of each attribute and the average error

        //At the end of the iteration, compare the average error with the error threshold
    }


    private void generateSynPopMonteCarlo(){
        //method to select the households of the synthetic population according to the expansion factor
        //While fin == 0 (household counter smaller than household population)
            //draw one random number and compare with the cumulative distribution of households to select the household

            //check if the person count is lower than the marginal sum.
                // If so, copy the characteristics of the household

                //If not, withdraw this household for consideration and recalculate the household weight.


    }

}
