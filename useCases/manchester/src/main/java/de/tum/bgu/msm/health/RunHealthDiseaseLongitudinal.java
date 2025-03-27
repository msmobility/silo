package de.tum.bgu.msm.health;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.health.diseaseModelOffline.HealthExposuresReader;
import de.tum.bgu.msm.health.diseaseModelOffline.ModelBuilderMCR;
import de.tum.bgu.msm.io.output.MultiFileResultsMonitor;
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


public class RunHealthDiseaseLongitudinal {

    private final static Logger logger = LogManager.getLogger(RunHealthDiseaseLongitudinal.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Greater Manchester");
        HealthDataContainerImpl dataContainer = DataBuilderHealth.getModelDataForManchester(properties, config);
        DataBuilderHealth.read(properties, dataContainer, config);

        ModelContainer modelContainer = ModelBuilderLongitudinalMCR.getModelContainerForManchester(dataContainer, properties, config);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);

        model.addResultMonitor(new MultiFileResultsMonitor(dataContainer, properties));

        model.runModel();

        logger.info("Finished SILO.");
    }
}
