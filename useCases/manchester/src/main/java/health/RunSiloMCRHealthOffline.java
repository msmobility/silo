package health;

import de.tum.bgu.msm.health.airPollutant.AirPollutantModel;
import de.tum.bgu.msm.health.HealthModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.resources.Resources;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import java.util.Objects;

/**
 * Implements SILO for the Greater Manchester
 *
 * @author Qin Zhang*/


public class RunSiloMCRHealthOffline {

    private final static Logger logger = Logger.getLogger(RunSiloMCRHealthOffline.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Greater Manchester");
        HealthDataContainerImpl dataContainer = DataBuilderHealth.getModelDataForMuc(properties, config);
        DataBuilderHealth.read(properties, dataContainer);

        Resources.initializeResources(Objects.requireNonNull(properties.main.baseDirectory + properties.transportModel.mitoPropertiesPath));
        AirPollutantModel airPollutantModel = new AirPollutantModel(dataContainer,properties, SiloUtil.provideNewRandom(),config);
        //AccidentModel accidentModel = new AccidentModel(dataContainer,properties,SiloUtil.provideNewRandom());
        HealthModel healthModel = new HealthModel(dataContainer, properties, SiloUtil.provideNewRandom(),config);
        healthModel.setup();
        //accidentModel.endYear(2011);
        airPollutantModel.runOffineWithEmission(2011,false);
        healthModel.endYear(2011);
        dataContainer.endSimulation();

        logger.info("Finished SILO.");
    }
}
