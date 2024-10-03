package health;

import de.tum.bgu.msm.health.HealthModelMCR;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import static de.tum.bgu.msm.utils.SiloUtil.*;

/**
 * Implements SILO for the Greater Manchester
 *
 * @author Qin Zhang*/


public class RunSiloMCRHealthOffline {

    private final static Logger logger = Logger.getLogger(RunSiloMCRHealthOffline.class);

    public static void main(String[] args) {

        Properties properties = Properties.initializeProperties(args[0]);
        final String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
        createDirectoryIfNotExistingYet(outputDirectory);
        initializeRandomNumber(properties.main.randomSeed);
        trackingFile("open");
        loadHdf5Lib();

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Greater Manchester");
        HealthDataContainerImpl dataContainer = DataBuilderHealth.getModelDataForMuc(properties, config);
        DataBuilderHealth.read(properties, dataContainer);

        //Resources.initializeResources(Objects.requireNonNull(properties.main.baseDirectory + properties.transportModel.mitoPropertiesPath));
        HealthModelMCR healthModel = new HealthModelMCR(dataContainer, properties, SiloUtil.provideNewRandom(),config);
        healthModel.endYear(2021);
        dataContainer.endSimulation();

        logger.info("Finished SILO.");
    }
}
