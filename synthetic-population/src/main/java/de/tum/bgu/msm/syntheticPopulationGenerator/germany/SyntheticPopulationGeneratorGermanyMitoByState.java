package de.tum.bgu.msm.syntheticPopulationGenerator.germany;


import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.PropertiesUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.GermanyPropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;

public class SyntheticPopulationGeneratorGermanyMitoByState {

    static Logger logger = Logger.getLogger(SyntheticPopulationGeneratorGermanyMitoByState.class);
    private static DataSetSynPop dataSetSynPop = new DataSetSynPop();

    public static void main (String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);
        try {
            PropertiesSynPop.initializePropertiesSynPop(new GermanyPropertiesSynPop(new PropertyResourceBundle(new FileReader(args[0]))));
            PropertiesUtil.writePropertiesForThisRun(args[0]);
        } catch (IOException e) {
            logger.error("File not found: " + args[0]);
        }
        SyntheticPopI syntheticPop;
        syntheticPop = new SyntheticPopGermanyMitoByState(dataSetSynPop, properties);
        syntheticPop.runSP();
    }

}
