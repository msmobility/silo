package de.tum.bgu.msm.syntheticPopulationGenerator.shodoshima;

import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.PropertiesUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.ShodoshimaPropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class SyntheticPopulationGeneratorShodoshima {

    static Logger logger = Logger.getLogger(SyntheticPopulationGeneratorShodoshima.class);
    private static DataSetSynPop dataSetSynPop = new DataSetSynPop();
    private static ResourceBundle rb;

    public static void main (String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);
        try {
            rb = new PropertyResourceBundle(new FileReader(args[0]));
            PropertiesSynPop.initializePropertiesSynPop(new ShodoshimaPropertiesSynPop(new PropertyResourceBundle(new FileReader(args[0]))));
            PropertiesUtil.writePropertiesForThisRun(args[0]);
        } catch (IOException e) {
            logger.error("File not found: " + args[0]);
        }
        SyntheticPopI syntheticPop;
        syntheticPop = new SyntheticPopSH(rb, dataSetSynPop,properties);
        syntheticPop.runSP();
    }
}
