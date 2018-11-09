package de.tum.bgu.msm.syntheticPopulationGenerator.perth;


import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.properties.PropertiesUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.SyntheticPopDe;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;

public class SyntheticPopulationGeneratorPerth {

    static Logger logger = Logger.getLogger(SyntheticPopulationGeneratorPerth.class);
    private static DataSetSynPop dataSetSynPop = new DataSetSynPop();

    public static void main (String[] args) {

        SiloUtil.siloInitialization(Implementation.PERTH, args[0]);
//        try {
//            PropertiesSynPop.initializePropertiesSynPop(new PropertyResourceBundle(new FileReader(args[0])), Implementation.PERTH);
            PropertiesUtil.writePropertiesForThisRun(args[0]);
//        } catch (IOException e) {
//            logger.error("File not found: " + args[0]);
//        }
        SyntheticPopI syntheticPop;
        syntheticPop = new SyntheticPopDe(dataSetSynPop);
        syntheticPop.runSP();
    }

}
