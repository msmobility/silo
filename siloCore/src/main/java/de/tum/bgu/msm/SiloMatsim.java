package de.tum.bgu.msm;

import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author dziemke
 */

public final class SiloMatsim {
    static Logger logger = Logger.getLogger(SiloMatsim.class);

    private final Properties properties;
    private final Config matsimConfig;// = ConfigUtils.createConfig(); // SILO-MATSim integration-specific

    /**
     * Option to set the matsim config directly, at this point meant for tests.
     */
    public SiloMatsim(String args, Config config, Implementation implementation) {
        properties = SiloUtil.siloInitialization(args, implementation);
        matsimConfig = config;
    }

    public final void run() {
        logger.info("Starting SILO program for MATSim");
        SiloModel model = new SiloModel(matsimConfig, Properties.get());
        model.runModel();
        logger.info("Finished SILO.");

    }
}