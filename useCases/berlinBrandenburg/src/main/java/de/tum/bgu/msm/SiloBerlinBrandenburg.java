package de.tum.bgu.msm;

import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.output.HouseholdSatisfactionMonitor;
import de.tum.bgu.msm.io.output.ModalSharesResultMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.io.MultiFileResultsMonitorBerlinBrandenburg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ReplanningConfigGroup;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;

import java.util.List;

/**
 * Implements SILO for the Berlin-Brandenburg Metropolitan Area
 *
 * @author Wei-Chieh Huang
 * Created on July 24, 2025 in Munich, Germany
 */
public class SiloBerlinBrandenburg {

    private final static Logger logger = LogManager.getLogger(SiloBerlinBrandenburg.class);


    public static void main(String[] args) {

        // config options
        // siloBer.properties matsimBer.xml
        // note: working directory: /Users/jakob/git/silo-data-berlinBrandenburg
        // this should change...

        // READ SILO PROPERTIES
        Properties properties = SiloUtil.siloInitialization("siloBer.properties");

        // READ MATSIM CONFIG
        Config config = ConfigUtils.loadConfig("/Users/jakob/git/matsim-berlin/input/v6.4/berlin-v6.4.config.xml");
        run(properties, config);
    }

    public static void run(Properties properties, Config config) {

        // MATSim Configuration
        config.plans().setInputFile("https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v6.4/input/berlin-v6.4-0.1pct.plans.xml.gz");
        config.global().setCoordinateSystem("EPSG:25832");
        config.controller().setLastIteration(2);

        Activities.addScoringParams(config, true);

        config.removeModule("simwrapper");

        addReplaningStrategies(config);



        // SILO Infrastrcture
        logger.info("Started SILO land use model for the Berlin-Brandenburg Metropolitan Area");
        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForBerlinBrandenburg(properties, config);
        DataBuilder.read(properties, dataContainer);
        ModelContainer modelContainer = ModelBuilderBerlinBrandenburg.getModelContainer(dataContainer, properties, config);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);

        model.runModel();
        logger.info("Finished SILO.");
    }

    private static void addReplaningStrategies(Config config) {
        for (String subpopulation : List.of("person", "freight", "goodsTraffic", "commercialPersonTraffic", "commercialPersonTraffic_service")) {
            config.replanning().addStrategySettings(
                    new ReplanningConfigGroup.StrategySettings()
                            .setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.ChangeExpBeta)
                            .setWeight(1.0)
                            .setSubpopulation(subpopulation)
            );

            config.replanning().addStrategySettings(
                    new ReplanningConfigGroup.StrategySettings()
                            .setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ReRoute)
                            .setWeight(0.15)
                            .setSubpopulation(subpopulation)
            );
        }

        config.replanning().addStrategySettings(
                new ReplanningConfigGroup.StrategySettings()
                        .setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.TimeAllocationMutator)
                        .setWeight(0.15)
                        .setSubpopulation("person")
        );

        config.replanning().addStrategySettings(
                new ReplanningConfigGroup.StrategySettings()
                        .setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.SubtourModeChoice)
                        .setWeight(0.15)
                        .setSubpopulation("person")
        );
    }

    public void setupModels(){}
}
