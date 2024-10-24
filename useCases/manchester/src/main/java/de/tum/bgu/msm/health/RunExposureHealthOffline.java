package de.tum.bgu.msm.health;

import de.tum.bgu.msm.health.airPollutant.AirPollutantModel;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.data.LinkInfo;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.HashMap;
import java.util.Map;

import static de.tum.bgu.msm.utils.SiloUtil.*;

/**
 * Implements SILO for the Greater Manchester
 *
 * @author Qin Zhang*/


public class RunExposureHealthOffline {

    private final static Logger logger = Logger.getLogger(RunExposureHealthOffline.class);

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
        HealthDataContainerImpl dataContainer = DataBuilderHealth.getModelDataForManchester(properties, config);
        DataBuilderHealth.read(properties, dataContainer);

        //Resources.initializeResources(Objects.requireNonNull(properties.main.baseDirectory + properties.transportModel.mitoPropertiesPath));
        HealthExposureModelMCR healthModel = new HealthExposureModelMCR(dataContainer, properties, SiloUtil.provideNewRandom(),config);
        AirPollutantModel airPollutantModel = new AirPollutantModel(dataContainer, properties, SiloUtil.provideNewRandom(),config);

        Scenario scenario = ScenarioUtils.createMutableScenario(config);
        String networkFile = properties.main.baseDirectory + "/" + scenario.getConfig().network().getInputFile();
        new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
        Map<Id<Link>, LinkInfo> linkInfoMap = new HashMap<>();
        for(Link link : scenario.getNetwork().getLinks().values()){
            linkInfoMap.put(link.getId(), new LinkInfo(link.getId()));
        }
        ((DataContainerHealth)dataContainer).setLinkInfo(linkInfoMap);

        airPollutantModel.endYear(2021);
        //healthModel.endYear(2021);
        dataContainer.endSimulation();

        logger.info("Finished SILO.");
    }
}
