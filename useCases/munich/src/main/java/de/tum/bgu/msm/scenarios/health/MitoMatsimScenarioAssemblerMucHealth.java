package de.tum.bgu.msm.scenarios.health;


import de.tum.bgu.msm.MitoModel2017;
import de.tum.bgu.msm.MitoModel2017withMoped;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.DataSet;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.MitoTrip;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.matsim.MatsimScenarioAssembler;
import de.tum.bgu.msm.matsim.MatsimTravelTimesAndCosts;
import de.tum.bgu.msm.mito.MitoDataConverter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.resources.Resources;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.utils.TravelTimeUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MitoMatsimScenarioAssemblerMucHealth implements MatsimScenarioAssembler {

    private static final Logger logger = Logger.getLogger(MitoMatsimScenarioAssemblerMucHealth.class);

    private final String propertiesPath;

    private final DataContainer dataContainer;
    private final Properties properties;
    private final MitoDataConverter dataConverter;

    private SkimTravelTimes mitoInputTravelTime;



    public MitoMatsimScenarioAssemblerMucHealth(DataContainer dataContainer,
                                                Properties properties,
                                                MitoDataConverter dataConverter) {
        this.dataContainer = dataContainer;
        this.properties = properties;
        this.dataConverter = dataConverter;
        this.propertiesPath = Objects.requireNonNull(properties.main.baseDirectory + properties.transportModel.mitoPropertiesPath);
    }

    @Override
    public Scenario assembleScenario(Config initialMatsimConfig, int year, TravelTimes travelTimes) {

        logger.info("  Running travel demand model MITO for the year " + year);

        DataSet dataSet = convertData(year);

        logger.info("  SILO data being sent to MITO");
        MitoModel2017 mito = MitoModel2017.initializeModelFromSilo(propertiesPath, dataSet, properties.main.scenarioName);
        mito.setRandomNumberGenerator(SiloUtil.getRandomObject());
        mito.run();

        logger.info("  Receiving demand from MITO");
        Population population = mito.getData().getPopulation(Day.weekday);

        Config config = ConfigUtils.loadConfig(initialMatsimConfig.getContext());
        setDemandSpecificConfigSettings(config);
        MutableScenario scenario = (MutableScenario) ScenarioUtils.loadScenario(config);
        scenario.setPopulation(population);
        return scenario;
    }

    @Override
    public Map<Day, Scenario> assembleMultiScenarios(Config initialMatsimConfig, int year, TravelTimes travelTimes) {

        logger.info("  Running travel demand model MITO for the year " + year);

        DataSet dataSet = convertData(year);

        logger.info("  SILO data being sent to MITO");
        MitoModel2017withMoped mito = MitoModel2017withMoped.initializeModelFromSilo(propertiesPath, dataSet, properties.main.scenarioName);
        mito.setRandomNumberGenerator(SiloUtil.getRandomObject());
        mito.run();

        System.exit(1);

        logger.info("  Receiving demand from MITO");
        Map<Day, Scenario> scenarios = new HashMap<>();

        for (Day day : Day.values()){
            Population population = mito.getData().getPopulation(day);
            Config config = ConfigUtils.loadConfig(initialMatsimConfig.getContext());
            setDemandSpecificConfigSettings(config);
            MutableScenario scenario = (MutableScenario) ScenarioUtils.loadScenario(config);
            scenario.setPopulation(population);
            scenarios.put(day, scenario);
        }

        return scenarios;
    }


    public void setDemandSpecificConfigSettings(Config config) {
        config.qsim().setFlowCapFactor(properties.main.scaleFactor * Double.parseDouble(Resources.instance.getString(de.tum.bgu.msm.resources.Properties.TRIP_SCALING_FACTOR)));
        config.qsim().setStorageCapFactor(properties.main.scaleFactor * Double.parseDouble(Resources.instance.getString(de.tum.bgu.msm.resources.Properties.TRIP_SCALING_FACTOR)));

        logger.info("Flow Cap Factor: " + config.qsim().getFlowCapFactor());
        logger.info("Storage Cap Factor: " + config.qsim().getStorageCapFactor());

        PlanCalcScoreConfigGroup.ActivityParams homeActivity = new PlanCalcScoreConfigGroup.ActivityParams("home").setTypicalDuration(12 * 60 * 60);
        config.planCalcScore().addActivityParams(homeActivity);

        PlanCalcScoreConfigGroup.ActivityParams workActivity = new PlanCalcScoreConfigGroup.ActivityParams("work").setTypicalDuration(8 * 60 * 60);
        config.planCalcScore().addActivityParams(workActivity);

        PlanCalcScoreConfigGroup.ActivityParams educationActivity = new PlanCalcScoreConfigGroup.ActivityParams("education").setTypicalDuration(8 * 60 * 60);
        config.planCalcScore().addActivityParams(educationActivity);

        PlanCalcScoreConfigGroup.ActivityParams shoppingActivity = new PlanCalcScoreConfigGroup.ActivityParams("shopping").setTypicalDuration(1 * 60 * 60);
        config.planCalcScore().addActivityParams(shoppingActivity);

        PlanCalcScoreConfigGroup.ActivityParams recreationActivity = new PlanCalcScoreConfigGroup.ActivityParams("recreation").setTypicalDuration(1 * 60 * 60);
        config.planCalcScore().addActivityParams(recreationActivity);

        PlanCalcScoreConfigGroup.ActivityParams otherActivity = new PlanCalcScoreConfigGroup.ActivityParams("other").setTypicalDuration(1 * 60 * 60);
        config.planCalcScore().addActivityParams(otherActivity);

        PlanCalcScoreConfigGroup.ActivityParams airportActivity = new PlanCalcScoreConfigGroup.ActivityParams("airport").setTypicalDuration(1 * 60 * 60);
        config.planCalcScore().addActivityParams(airportActivity);
    }

    private DataSet convertData(int year) {
        DataSet dataSet = dataConverter.convertData(dataContainer);
        dataSet.setTravelTimes(mitoInputTravelTime);

        final TravelTimes travelTimes = dataContainer.getTravelTimes();
        if(mitoInputTravelTime == null ) {
            //Transport model runs for the first time.
            if(travelTimes instanceof SkimTravelTimes) {
                mitoInputTravelTime = (SkimTravelTimes) travelTimes;
            } else if(travelTimes instanceof MatsimTravelTimesAndCosts) {
                mitoInputTravelTime = new SkimTravelTimes();
                //MATSim did not run yet
                if(properties.transportModel.matsimInitialEventsFile == null) {
                    //read initial skim for the first year
                    logger.info("Reading car skim for initial Mito input");
                    TravelTimeUtil.updateCarSkim(mitoInputTravelTime, properties.main.startYear, properties);
                } else {
                    //MATSim travel times are initialized from initial events file. Create skim from there.
                    mitoInputTravelTime.updateSkimMatrix(travelTimes.getPeakSkim(TransportMode.car), TransportMode.car);
                }
            }
        } else {
            //Transport model has run at least once. Update input travel times from skim or matsim.
            mitoInputTravelTime.updateSkimMatrix(travelTimes.getPeakSkim(TransportMode.car), TransportMode.car);
        }
        dataSet.setTravelTimes(mitoInputTravelTime);
        dataSet.setYear(year);
        return dataSet;
    }

}
