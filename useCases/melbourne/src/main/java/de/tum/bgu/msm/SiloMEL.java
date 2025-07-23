package de.tum.bgu.msm;

import com.google.common.io.Resources;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.health.DataBuilderHealth;
import de.tum.bgu.msm.health.HealthDataContainerImpl;
import de.tum.bgu.msm.io.output.MultiFileResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Implements SILO for the Great Melbourne region
 *
 * @author Qin Zhang
 * @author Carl Higgs
 * */


public class SiloMEL {

    private final static Logger logger = LogManager.getLogger(SiloMEL.class);

    public static void main(String[] args) throws IOException {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        assert config != null;
        logger.info("Started SILO land use model for the Great Melbourne region");
        checkConfiguredExposureData(properties, properties.main.startYear);
        HealthDataContainerImpl dataContainer = DataBuilderHealth.getModelDataForMelbourne(properties, config);
        DataBuilderHealth.read(properties, dataContainer, config);
        ModelContainer modelContainer = ModelBuilderMEL.getModelContainerForMelbourne(dataContainer, properties, config);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        //model.addResultMonitor(new ResultsMonitorMuc(dataContainer, properties));
        model.addResultMonitor(new MultiFileResultsMonitor(dataContainer, properties));
        //model.addResultMonitor(new HouseholdSatisfactionMonitor(dataContainer, properties, modelContainer));
        //model.addResultMonitor(new ModalSharesResultMonitor(dataContainer, properties));
        model.runModel();
        logger.info("Finished SILO.");
    }

    static void checkConfiguredExposureData(Properties properties, int scenarioYear) {
        String exposureFilePath = Paths.get(properties.main.baseDirectory, properties.healthData.baseExposureFile).toString();
        if (exposureFilePath == null || !Files.exists(Paths.get(exposureFilePath))) {
            logger.error("Exposure data file '{}' not found. Please run health.RunExposureHealthOffline before proceeding.", exposureFilePath);
            throw new RuntimeException("Exposure data file not found: " + exposureFilePath);
        } else {
            try {
                if (Files.size(Paths.get(exposureFilePath)) == 0) {
                    logger.error("Exposure data file '{}' is empty. Please run health.RunExposureHealthOffline before proceeding.", exposureFilePath);
                    throw new RuntimeException("Exposure data file is incomplete: " + exposureFilePath);
                } else {
                    logger.info("Using configured exposure data file: {}", exposureFilePath);
                }
            } catch (IOException e) {
                logger.error("Error checking exposure data file '{}': {}", exposureFilePath, e.getMessage());
                throw new RuntimeException("Error checking exposure data file: " + exposureFilePath, e);
            }
        }
    }
}
