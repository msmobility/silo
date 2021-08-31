package de.tum.bgu.msm.syntheticPopulationGenerator.munich.synpopTrampa;

import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.PropertiesUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.SyntheticPopDe;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.SyntheticPopulationGeneratorDe;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.MunichPropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;

public class SyntheticPopulationGeneratorDeTrampa {

    static Logger logger = Logger.getLogger(SyntheticPopulationGeneratorDe.class);

    private static DataSetSynPop dataSetSynPop = new DataSetSynPop();

    public static void main (String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);
        try {
            PropertiesSynPop.initializePropertiesSynPop(new MunichPropertiesSynPop(new PropertyResourceBundle(new FileReader(args[0]))));
            PropertiesUtil.writePropertiesForThisRun(args[0]);
        } catch (IOException e) {
            logger.error("File not found: " + args[0]);
        }
        SyntheticPopI syntheticPop = new SyntheticPopDeTrampa(dataSetSynPop, properties);
        syntheticPop.runSP();
    }
}

