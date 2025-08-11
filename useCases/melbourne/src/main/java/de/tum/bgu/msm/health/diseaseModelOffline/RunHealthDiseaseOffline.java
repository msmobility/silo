package de.tum.bgu.msm.health.diseaseModelOffline;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.health.DataBuilderHealth;
import de.tum.bgu.msm.health.HealthDataContainerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import java.io.IOException;

/**
 * Implements SILO for the Greater Melbourne
 *
 * @author Qin Zhang*/


public class RunHealthDiseaseOffline {

    private final static Logger logger = LogManager.getLogger(RunHealthDiseaseOffline.class);

    public static void main(String[] args) throws IOException {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Greater Melbourne");
        HealthDataContainerImpl dataContainer = DataBuilderHealth.getModelDataForMelbourne(properties, config);
        assert config != null;
        DataBuilderHealth.read(properties, dataContainer, config);

        ModelContainer modelContainer = ModelBuilderMEL.getModelContainerForMelbourne(dataContainer, properties, config);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);

        //Read in person microdata with exposures
        HealthExposuresReader healthExposuresReader = new HealthExposuresReader();
        healthExposuresReader.readData(dataContainer,properties.healthData.baseExposureFile);

        model.runModel();

        logger.info("Finished SILO.");
    }
}
