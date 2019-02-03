package de.tum.bgu.msm.syntheticPopulationGenerator.austin;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.properties.PropertiesUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;

public class RunSyntheticPopUs {

    static Logger logger = Logger.getLogger(RunSyntheticPopUs.class);

    public static void main (String[] args) {

        SiloUtil.siloInitialization(Implementation.AUSTIN, args[0]);
        try {
            PropertyResourceBundle bundle = new PropertyResourceBundle(new FileReader(args[0]));
            PropertiesSynPop.initializePropertiesSynPop(bundle, Implementation.AUSTIN);
            PropertiesUtil.writePropertiesForThisRun(args[0]);
            SyntheticPopI syntheticPop;
            syntheticPop = new SyntheticPopUs(bundle);
            syntheticPop.runSP();
        } catch (IOException e) {
            logger.error("File not found: " + args[0]);
        }
    }
}
