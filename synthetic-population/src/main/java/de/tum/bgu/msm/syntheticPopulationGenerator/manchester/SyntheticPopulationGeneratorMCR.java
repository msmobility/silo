package de.tum.bgu.msm.syntheticPopulationGenerator.manchester;


import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.PropertiesUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.BangkokPropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.ManchesterPropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;

public class SyntheticPopulationGeneratorMCR {

    static Logger logger = LogManager.getLogger(SyntheticPopulationGeneratorMCR.class);
    private static DataSetSynPopMCR dataSetSynPop = new DataSetSynPopMCR();

    public static void main (String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);
        try {
            PropertiesSynPop.initializePropertiesSynPop(new ManchesterPropertiesSynPop(new PropertyResourceBundle(new FileReader(args[0]))));
            PropertiesUtil.writePropertiesForThisRun(args[0]);
        } catch (IOException e) {
            logger.error("File not found: " + args[0]);
        }
        SyntheticPopI syntheticPop;
        syntheticPop = new SyntheticPopMCR(dataSetSynPop, properties);
        syntheticPop.runSP();
    }

}
