package de.tum.bgu.msm.run.models.transport;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.matsim.MatsimScenarioAssembler;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import java.io.File;
import java.util.Objects;

/**
 * This class creates MATSim populations at transport model years, but will not run the transport model. It is prepared to
 * run stand-alone transport simulations with MATSim, without feedback to the land use model
 */

public class MatsimPopulationConeversorAndWriter implements ModelUpdateListener {

    private Logger logger = Logger.getLogger(MatsimPopulationConeversorAndWriter.class);

    private final DataContainer dataContainer;
    private MatsimScenarioAssembler scenarioAssembler;
    private final Properties properties;
    private final Config initialMatsimConfig;

    public MatsimPopulationConeversorAndWriter(DataContainer dataContainer, MatsimScenarioAssembler scenarioAssembler,
                                               Properties properties, Config initialMatsimConfig) {
        this.dataContainer = dataContainer;
        this.scenarioAssembler = scenarioAssembler;
        this.properties = properties;

        this.initialMatsimConfig = Objects.requireNonNull(initialMatsimConfig,
                "No initial matsim config provided to SiloModel class!");
        logger.info("Copying initial config to output folder");
        File file = new File(properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/matsim/initialConfig.xml");
        file.getParentFile().mkdirs();
        ConfigUtils.writeMinimalConfig(initialMatsimConfig, file.getAbsolutePath());

    }

    @Override
    public void setup() {

    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void endYear(int year) {

        if (properties.transportModel.transportModelYears.contains(year)){
            Scenario scenario = scenarioAssembler.assembleScenario(initialMatsimConfig, year, dataContainer.getTravelTimes());
            PopulationWriter populationWriter = new PopulationWriter(scenario.getPopulation());
            populationWriter.write(properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/matsim/plans_" + year + ".xml.gz");
        }


    }

    @Override
    public void endSimulation() {

    }
}
