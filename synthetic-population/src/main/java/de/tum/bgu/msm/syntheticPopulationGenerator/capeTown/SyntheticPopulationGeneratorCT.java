package de.tum.bgu.msm.syntheticPopulationGenerator.capeTown;


import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class SyntheticPopulationGeneratorCT {

    static Logger logger = Logger.getLogger(SyntheticPopulationGeneratorCT.class);
    private static DataSetSynPop dataSetSynPop = new DataSetSynPop();
    private static ResourceBundle rb;

    public static void main (String[] args) {

        Properties properties =SiloUtil.siloInitialization(args[0]);
        try {
            //PropertiesSynPop.initializePropertiesSynPop(new PropertyResourceBundle(new FileReader(args[0])), Implementation.CAPE_TOWN);
            rb = new PropertyResourceBundle(new FileReader(args[0]));
            //PropertiesUtil.writePropertiesForThisRun(args[0]);
        } catch (IOException e) {
            logger.error("File not found: " + args[0]);
        }
        SyntheticPopI syntheticPop;
        syntheticPop = new SyntheticPopCTrace(rb, properties);
        syntheticPop.runSP();
    }

}
