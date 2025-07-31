package de.tum.bgu.msm.health;

import de.tum.bgu.msm.health.airPollutant.AirPollutantModel;
import de.tum.bgu.msm.health.noise.NoiseModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

/**
 * Implements SILO for the Greater Manchester
 *
 * @author Qin Zhang*/


public class RunHomeBasedExposureOffline {

    private final static Logger logger = LogManager.getLogger(RunHomeBasedExposureOffline.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Greater Manchester");
        HealthDataContainerImpl dataContainer = DataBuilderHealth.getModelDataForManchester(properties, config);
        DataBuilderHealth.read(properties, dataContainer, config);

        HealthExposureModelMCR exposureModelMCR = new HealthExposureModelMCR(dataContainer, properties, SiloUtil.provideNewRandom(),config);


        exposureModelMCR.calculateHomeBasedExposureOnly(2021);
        dataContainer.writePersonHomeBasedExposureData(2021);

        logger.info("Finished SILO.");
    }
}
