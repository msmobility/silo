package de.tum.bgu.msm.syntheticPopulationGenerator.austin;

import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.PropertiesUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;

public class RunSyntheticPopUs {

    static Logger logger = LogManager.getLogger(RunSyntheticPopUs.class);

    public static void main (String[] args) {

        final Properties properties = SiloUtil.siloInitialization(args[0]);
        try {
            PropertyResourceBundle bundle = new PropertyResourceBundle(new FileReader(args[0]));
            PropertiesUtil.writePropertiesForThisRun(args[0]);
            SyntheticPopI syntheticPop;
            syntheticPop = new SyntheticPopUs(bundle, properties);
            syntheticPop.runSP();
        } catch (IOException e) {
            logger.error("File not found: " + args[0]);
        }
    }
}
