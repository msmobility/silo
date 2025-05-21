package de.tum.bgu.msm;

import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implements SILO for the Great Manchester
 *
 * @author Qin Zhang*/


public class Test {

    private final static Logger logger = LogManager.getLogger(Test.class);

    public static void main(String[] args) {
        Properties properties = SiloUtil.siloInitialization(args[0]);

        logger.info(properties);
    }
}
