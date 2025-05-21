package de.tum.bgu.msm.health;

import de.tum.bgu.msm.health.airPollutant.AirPollutantModel;
import de.tum.bgu.msm.health.noise.NoiseModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implements SILO for the Greater Melbourne
 *
 * @author Qin Zhang*/


public class RunExposureHealthOffline {

    private final static Logger logger = LogManager.getLogger(RunExposureHealthOffline.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Greater Melbourne");
        HealthDataContainerImpl dataContainer = DataBuilderHealth.getModelDataForMelbourne(properties, config);
        DataBuilderHealth.read(properties, dataContainer, config);

        AirPollutantModel airPollutantModel = new AirPollutantModel(dataContainer, properties, SiloUtil.provideNewRandom(),config);
        NoiseModel noiseModel = new NoiseModel(dataContainer,properties, SiloUtil.provideNewRandom(),config);
        SportPAModelMEL sportPAModelMCR = new SportPAModelMEL(dataContainer, properties, SiloUtil.provideNewRandom());
        HealthExposureModelMEL exposureModelMCR = new HealthExposureModelMEL(dataContainer, properties, SiloUtil.provideNewRandom(),config);
        DiseaseModelMEL diseaseModelMEL = new DiseaseModelMEL(dataContainer, properties, SiloUtil.provideNewRandom());

        //airPollutantModel.endYear(2021);
        //noiseModel.endYear(2021);
        sportPAModelMCR.endYear(2021);
        exposureModelMCR.endYear(2021);
        diseaseModelMEL.setup();
        diseaseModelMEL.endYear(2021);
        dataContainer.endSimulation();

        logger.info("Finished SILO.");
    }
}
