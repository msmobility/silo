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


    static Logger logger = Logger.getLogger(SyntheticPopDe.class);

    public SyntheticPopDe(ResourceBundle rb) {
        // Constructor
        this.rb = rb;

    }


    public void runSP(){
        //method to create the synthetic population
        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_RUN_SYNTHETIC_POPULATION, false)) return;
        logger.info("Starting to create the synthetic population");

        readDataSynPop();

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

}
