package health.diseaseModelOffline;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.health.HealthModelMCR;
import de.tum.bgu.msm.io.output.MultiFileResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import health.DataBuilderHealth;
import health.HealthDataContainerImpl;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import static de.tum.bgu.msm.utils.SiloUtil.*;

/**
 * Implements SILO for the Greater Manchester
 *
 * @author Qin Zhang*/


public class RunHealthDiseaseOffline {

    private final static Logger logger = Logger.getLogger(RunHealthDiseaseOffline.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Greater Manchester");
        HealthDataContainerImpl dataContainer = DataBuilderHealth.getModelDataForMuc(properties, config);
        DataBuilderHealth.read(properties, dataContainer);

        ModelContainer modelContainer = ModelBuilderMCR.getModelContainerForManchester(dataContainer, properties, config);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);

        //Read in person microdata with exposures
        HealthExposuresReader healthExposuresReader = new HealthExposuresReader();
        healthExposuresReader.readData(dataContainer,properties.main.baseDirectory + "input/health/pp_health_2021_base.csv");

        model.runModel();

        logger.info("Finished SILO.");
    }
}
