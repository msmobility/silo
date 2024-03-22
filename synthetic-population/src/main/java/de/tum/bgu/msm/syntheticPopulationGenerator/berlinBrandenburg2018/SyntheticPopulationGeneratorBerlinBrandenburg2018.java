package de.tum.bgu.msm.syntheticPopulationGenerator.berlinBrandenburg2018;


import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.PropertiesUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.MunichPropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;

public class SyntheticPopulationGeneratorBerlinBrandenburg2018 {

    static Logger logger = Logger.getLogger(SyntheticPopulationGeneratorBerlinBrandenburg2018.class);
    private static DataSetSynPop dataSetSynPop = new DataSetSynPop();

    public static void main (String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);
        try {
            PropertiesSynPop.initializePropertiesSynPop(new MunichPropertiesSynPop(new PropertyResourceBundle(new FileReader(args[0]))));
            PropertiesUtil.writePropertiesForThisRun(args[0]);
        } catch (IOException e) {
            logger.error("File not found: " + args[0]);
        }
        SyntheticPopI syntheticPop;
        syntheticPop = new SyntheticPopBerlinBrandenburg2018(dataSetSynPop, properties);
        syntheticPop.runSP();
    }

}
