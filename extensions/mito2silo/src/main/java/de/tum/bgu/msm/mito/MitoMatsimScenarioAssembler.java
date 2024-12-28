package de.tum.bgu.msm.mito;


import de.tum.bgu.msm.MitoModel;
import de.tum.bgu.msm.MitoModel2;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.DataSet;
import de.tum.bgu.msm.data.DataSetImpl;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.MitoTrip;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.matsim.MatsimScenarioAssembler;
import de.tum.bgu.msm.matsim.MatsimTravelTimesAndCosts;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.resources.Resources;
import de.tum.bgu.msm.scenarios.mito7days.MitoModel7days;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.utils.TravelTimeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.*;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import uk.cam.mrc.phm.MitoModelMCR;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MitoMatsimScenarioAssembler implements MatsimScenarioAssembler {

    private static final Logger logger = LogManager.getLogger(MitoMatsimScenarioAssembler.class);

    private final String propertiesPath;

    private final DataContainer dataContainer;
    private final Properties properties;
    private final MitoDataConverter dataConverter;

    private SkimTravelTimes mitoInputTravelTime;

    public MitoMatsimScenarioAssembler(DataContainer dataContainer,
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
        //TODO: should not always call Manchester model, register mito model for use case?
        MitoModel2 mito = MitoModel2.initializeModelFromSilo(propertiesPath, dataSet, properties.main.scenarioName);
        mito.setRandomNumberGenerator(SiloUtil.getRandomObject());
        mito.run();

        logger.info("  Receiving demand from MITO");
        Population population = mito.getData().getPopulation();

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
        MitoModel7days mito = MitoModel7days.initializeModelFromSilo(propertiesPath, dataSet, properties.main.scenarioName);
        mito.setRandomNumberGenerator(SiloUtil.getRandomObject());
        mito.run();

        logger.info("  Receiving demand from MITO");
        Map<Day, Scenario> scenarios = new HashMap<>();

        Map<Day, Population> populationByDay = new HashMap();

        for(Person person: dataSet.getPopulation().getPersons().values()){
            Day day = Day.valueOf((String)person.getAttributes().getAttribute("day"));
            if (populationByDay.get(day) == null) {
                populationByDay.put(day, PopulationUtils.createPopulation(ConfigUtils.createConfig()));
            }
            populationByDay.get(day).addPerson(person);
        }

        for (Day day : Day.values()){
            Population population = populationByDay.get(day);
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

        ScoringConfigGroup.ActivityParams homeActivity = new ScoringConfigGroup.ActivityParams("home").setTypicalDuration(12 * 60 * 60);
        config.scoring().addActivityParams(homeActivity);

        ScoringConfigGroup.ActivityParams workActivity = new ScoringConfigGroup.ActivityParams("work").setTypicalDuration(8 * 60 * 60);
        config.scoring().addActivityParams(workActivity);

        ScoringConfigGroup.ActivityParams educationActivity = new ScoringConfigGroup.ActivityParams("education").setTypicalDuration(8 * 60 * 60);
        config.scoring().addActivityParams(educationActivity);

        ScoringConfigGroup.ActivityParams shoppingActivity = new ScoringConfigGroup.ActivityParams("shopping").setTypicalDuration(1 * 60 * 60);
        config.scoring().addActivityParams(shoppingActivity);

        ScoringConfigGroup.ActivityParams recreationActivity = new ScoringConfigGroup.ActivityParams("recreation").setTypicalDuration(1 * 60 * 60);
        config.scoring().addActivityParams(recreationActivity);

        ScoringConfigGroup.ActivityParams otherActivity = new ScoringConfigGroup.ActivityParams("other").setTypicalDuration(1 * 60 * 60);
        config.scoring().addActivityParams(otherActivity);

        ScoringConfigGroup.ActivityParams airportActivity = new ScoringConfigGroup.ActivityParams("airport").setTypicalDuration(1 * 60 * 60);
        config.scoring().addActivityParams(airportActivity);
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
