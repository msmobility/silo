package de.tum.bgu.msm.syntheticPopulationGenerator.lisbon;


import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.PropertiesUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.LisbonPropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.ManchesterPropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;

public class SyntheticPopulationGeneratorLIS {

    static Logger logger = Logger.getLogger(SyntheticPopulationGeneratorLIS.class);
    private static DataSetSynPopLIS dataSetSynPop = new DataSetSynPopLIS();

    public static void main (String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);
        try {
            PropertiesSynPop.initializePropertiesSynPop(new LisbonPropertiesSynPop(new PropertyResourceBundle(new FileReader(args[0]))));
            PropertiesUtil.writePropertiesForThisRun(args[0]);
        } catch (IOException e) {
            logger.error("File not found: " + args[0]);
        }
        SyntheticPopI syntheticPop;
        syntheticPop = new SyntheticPopLIS(dataSetSynPop, properties);
        syntheticPop.runSP();
    }

}
