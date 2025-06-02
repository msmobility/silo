package de.tum.bgu.msm.health;

import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.health.airPollutant.AirPollutantModel;
import de.tum.bgu.msm.health.data.LinkInfo;
import de.tum.bgu.msm.health.data.ActivityLocation;
import de.tum.bgu.msm.health.noise.NoiseModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.resources.Resources;
import de.tum.bgu.msm.utils.SiloUtil;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.tum.bgu.msm.health.injury.AccidentModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implements SILO for the Greater Manchester
 *
 * @author Qin Zhang*/


public class RunExposureHealthOffline {

    private final static Logger logger = LogManager.getLogger(RunExposureHealthOffline.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        // todo: check if that is good practice/ necessary to run the accident model, but need to make sure there are no implications elsewhere
        Resources.initializeResources(properties.transportModel.mitoPropertiesPath);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Greater Manchester");
        HealthDataContainerImpl dataContainer = DataBuilderHealth.getModelDataForManchester(properties, config);
        DataBuilderHealth.read(properties, dataContainer, config);

        /*
        // all
        AirPollutantModel airPollutantModel = new AirPollutantModel(dataContainer, properties, SiloUtil.provideNewRandom(),config);
        NoiseModel noiseModel = new NoiseModel(dataContainer,properties, SiloUtil.provideNewRandom(),config);
        SportPAModelMCR sportPAModelMCR = new SportPAModelMCR(dataContainer, properties, SiloUtil.provideNewRandom());
        HealthExposureModelMCR exposureModelMCR = new HealthExposureModelMCR(dataContainer, properties, SiloUtil.provideNewRandom(),config);
        DiseaseModelMCR diseaseModelMCR = new DiseaseModelMCR(dataContainer, properties, SiloUtil.provideNewRandom());
        */

        // setup
        AccidentModelMCR accidentModel = new AccidentModelMCR(dataContainer, properties, SiloUtil.provideNewRandom());
        //DiseaseModelMCR diseaseModelMCR = new DiseaseModelMCR(dataContainer, properties, SiloUtil.provideNewRandom());
        HealthExposureModelMCR exposureModelMCR = new HealthExposureModelMCR(dataContainer, properties, SiloUtil.provideNewRandom(),config);

        //
        for(Day day : Set.of(Day.thursday, Day.saturday, Day.sunday)) {
            accidentModel.endYear(2021, day);
        }



        //exposureModelMCR.endYear(2021);
        //diseaseModelMCR.setup();
        //diseaseModelMCR.endYear(2021);

        /*
        airPollutantModel.endYear(2021);
        noiseModel.endYear(2021);
        sportPAModelMCR.endYear(2021);
        exposureModelMCR.endYear(2021);
        diseaseModelMCR.setup();
        diseaseModelMCR.endYear(2021);
        dataContainer.endSimulation();
         */

        logger.info("Finished SILO.");
    }
}
