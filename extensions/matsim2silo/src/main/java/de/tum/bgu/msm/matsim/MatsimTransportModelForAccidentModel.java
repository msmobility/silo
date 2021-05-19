/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.modules.TransportModelPropertiesModule;
import de.tum.bgu.msm.resources.Resources;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.contrib.dvrp.trafficmonitoring.TravelTimeUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.ControlerDefaults;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.MainModeIdentifierImpl;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.matsim.core.config.groups.PlanCalcScoreConfigGroup.*;

/**
 * @author qinzhang
 */
public final class MatsimTransportModelForAccidentModel implements TransportModel {

    private static final Logger logger = Logger.getLogger(MatsimTransportModelForAccidentModel.class);

    private final Properties properties;
    private final Config initialMatsimConfig;

    private final MatsimData matsimData;
    private final MatsimTravelTimesAndCosts internalTravelTimes;

    private final DataContainer dataContainer;

    private MatsimScenarioAssembler scenarioAssembler;

    public MatsimTransportModelForAccidentModel(DataContainer dataContainer, Config matsimConfig,
                                                Properties properties, MatsimScenarioAssembler scenarioAssembler,
                                                MatsimData matsimData) {
        this.dataContainer = Objects.requireNonNull(dataContainer);
        this.initialMatsimConfig = Objects.requireNonNull(matsimConfig,
                "No initial matsim config provided to SiloModel class!");
        logger.info("Copying initial config to output folder");
        File file = new File(properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/matsim/initialConfig.xml");
        file.getParentFile().mkdirs();
        ConfigUtils.writeMinimalConfig(initialMatsimConfig, file.getAbsolutePath());

        final TravelTimes travelTimes = dataContainer.getTravelTimes();
        if (travelTimes instanceof MatsimTravelTimesAndCosts) {
            this.internalTravelTimes = (MatsimTravelTimesAndCosts) travelTimes;
        } else {
            this.internalTravelTimes = new MatsimTravelTimesAndCosts(matsimConfig);
        }
        this.matsimData = matsimData;
        this.scenarioAssembler = scenarioAssembler;
        this.properties = properties;
    }

    @Override
    public void setup() {
        internalTravelTimes.initialize(dataContainer.getGeoData(), matsimData);

        if (properties.transportModel.matsimInitialEventsFile == null) {
            runTransportModel(properties.main.startYear);
        } else {
            String eventsFile = properties.main.baseDirectory + properties.transportModel.matsimInitialEventsFile;
            replayFromEvents(eventsFile);
        }
    }

    @Override
    public void prepareYear(int year) {
    }

    @Override
    public void endYear(int year) {
        if (properties.transportModel.transportModelYears.contains(year + 1)) {
            runTransportModel(year + 1);
        }
    }

    @Override
    public void endSimulation() {
    }

    private void runTransportModel(int year) {
        logger.warn("Running MATSim transport model for year " + year + ".");
        Scenario assembledScenario;
        TravelTimes travelTimes = dataContainer.getTravelTimes();
        if (year == properties.main.baseYear &&
                properties.transportModel.transportModelIdentifier == TransportModelPropertiesModule.TransportModelIdentifier.MATSIM){
            //if using the SimpleCommuteModeChoiceScenarioAssembler, we need some intial travel times (this will use an unlodaded network)
            TravelTime myTravelTime = SiloMatsimUtils.getAnEmptyNetworkTravelTime();
            TravelDisutility myTravelDisutility = SiloMatsimUtils.getAnEmptyNetworkTravelDisutility();
            updateTravelTimes(myTravelTime, myTravelDisutility);
        }

        assembledScenario = scenarioAssembler.assembleScenario(initialMatsimConfig, year, travelTimes);
        MainModeIdentifierImpl mainModeIdentifier = new MainModeIdentifierImpl();

        Population populationCar = PopulationUtils.createPopulation(ConfigUtils.createConfig());
        Population populationBikePed = PopulationUtils.createPopulation(ConfigUtils.createConfig());
        for (Person pp : assembledScenario.getPopulation().getPersons().values()) {
            String mode = mainModeIdentifier.identifyMainMode(TripStructureUtils.getLegs(pp.getSelectedPlan()));
            switch (mode){
                case "car":
                    populationCar.addPerson(pp);
                    break;
                case "bike":
                case "walk":
                    populationBikePed.addPerson(pp);
                    break;
                default:
                    continue;
            }
        }

        logger.warn("Running MATSim transport model for car scenario " + year + ".");
        Config carConfig = ConfigUtils.loadConfig(initialMatsimConfig.getContext());
        MutableScenario scenarioCar = (MutableScenario) ScenarioUtils.loadScenario(carConfig);
        scenarioCar.setPopulation(populationCar);
        finalizeCarConfig(scenarioCar.getConfig(), year);
        final Controler controlerCar = new Controler(scenarioCar);
        controlerCar.run();
        logger.warn("Running MATSim transport model for car scenario " + year + " finished.");

        // Get travel Times from MATSim
        logger.warn("Using MATSim to compute travel times from zone to zone.");
        TravelTime travelTime = controlerCar.getLinkTravelTimes();
        TravelDisutility travelDisutility = controlerCar.getTravelDisutilityFactory().createTravelDisutility(travelTime);
        updateTravelTimes(travelTime, travelDisutility);

        logger.warn("Running MATSim transport model for Bike&Ped scenario " + year + ".");
        Config bikePedConfig = ConfigUtils.loadConfig(initialMatsimConfig.getContext());
        bikePedConfig.network().setInputFile("input/mito/trafficAssignment/studyNetworkDenseBikeWalk.xml");
        MutableScenario scenarioBikePed = (MutableScenario) ScenarioUtils.loadScenario(bikePedConfig);
        scenarioBikePed.setPopulation(populationBikePed);

        VehicleType walk = VehicleUtils.getFactory().createVehicleType(Id.create(TransportMode.walk, VehicleType.class));
        walk.setMaximumVelocity(5/3.6);
        scenarioBikePed.getVehicles().addVehicleType(walk);

        VehicleType bicycle = VehicleUtils.getFactory().createVehicleType(Id.create(TransportMode.bike, VehicleType.class));
        bicycle.setMaximumVelocity(15/3.6);
        scenarioBikePed.getVehicles().addVehicleType(bicycle);

        scenarioBikePed.getConfig().qsim().setVehiclesSource(QSimConfigGroup.VehiclesSource.modeVehicleTypesFromVehiclesData);

        finalizeConfigForBikePedScenario(scenarioBikePed.getConfig(), year);
        final Controler controlerBikePed = new Controler(scenarioBikePed);

        controlerBikePed.addOverridingModule( new AbstractModule(){
            @Override
            public void install(){
                this.addTravelTimeBinding( TransportMode.bike ).toInstance((l,t,p,v) -> l.getLength() / (12.5/3.6));
                this.addTravelTimeBinding( TransportMode.walk ).toInstance((link, time, person, vehicle) -> link.getLength()/(5./3.6));
            }
        } ) ;

        controlerBikePed.run();
        logger.warn("Running MATSim transport model for Bike&Ped scenario " + year + " finished.");


    }

    private void finalizeConfigForBikePedScenario(Config bikePedConfig, int year) {

        bikePedConfig.controler().setLastIteration(1);
        bikePedConfig.qsim().setFlowCapFactor(1.);
        bikePedConfig.qsim().setStorageCapFactor(1.);
        bikePedConfig.qsim().setEndTime(24*60*60);

        ActivityParams homeActivity = new ActivityParams("home").setTypicalDuration(12 * 60 * 60);
        bikePedConfig.planCalcScore().addActivityParams(homeActivity);

        ActivityParams workActivity = new ActivityParams("work").setTypicalDuration(8 * 60 * 60);
        bikePedConfig.planCalcScore().addActivityParams(workActivity);

        ActivityParams educationActivity = new ActivityParams("education").setTypicalDuration(8 * 60 * 60);
        bikePedConfig.planCalcScore().addActivityParams(educationActivity);

        ActivityParams shoppingActivity = new ActivityParams("shopping").setTypicalDuration(1 * 60 * 60);
        bikePedConfig.planCalcScore().addActivityParams(shoppingActivity);

        ActivityParams otherActivity = new ActivityParams("other").setTypicalDuration(1 * 60 * 60);
        bikePedConfig.planCalcScore().addActivityParams(otherActivity);

        ActivityParams airportActivity = new ActivityParams("airport").setTypicalDuration(1 * 60 * 60);
        bikePedConfig.planCalcScore().addActivityParams(airportActivity);

        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
        String outputDirectory = outputDirectoryRoot + "/matsim/" + year + "/bikePed/";
        bikePedConfig.controler().setOutputDirectory(outputDirectory);
        bikePedConfig.controler().setRunId(String.valueOf(year));
        bikePedConfig.controler().setWritePlansInterval(Math.max(bikePedConfig.controler().getLastIteration(), 1));
        bikePedConfig.controler().setWriteEventsInterval(Math.max(bikePedConfig.controler().getLastIteration(), 1));
        bikePedConfig.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        if (properties.transportModel.includeAccessEgress) {
            //config.plansCalcRoute().setAccessEgressType(PlansCalcRouteConfigGroup.AccessEgressType.walkConstantTimeToLink);
        }
        bikePedConfig.transit().setUsingTransitInMobsim(false);


        bikePedConfig.strategy().setMaxAgentPlanMemorySize(5);
        {
            StrategyConfigGroup.StrategySettings strategySettings = new StrategyConfigGroup.StrategySettings();
            strategySettings.setStrategyName("ChangeExpBeta");
            strategySettings.setWeight(0.8);
            bikePedConfig.strategy().addStrategySettings(strategySettings);
        }

        {
            StrategyConfigGroup.StrategySettings strategySettings = new StrategyConfigGroup.StrategySettings();
            strategySettings.setStrategyName("ReRoute");
            strategySettings.setWeight(0.2);
            bikePedConfig.strategy().addStrategySettings(strategySettings);
        }

        List<String> mainModeList = new ArrayList<>();
        mainModeList.add("bike");
        mainModeList.add("walk");
        bikePedConfig.qsim().setMainModes(mainModeList);
        bikePedConfig.plansCalcRoute().setNetworkModes(mainModeList);

        bikePedConfig.plansCalcRoute().removeModeRoutingParams("bike");
        bikePedConfig.plansCalcRoute().removeModeRoutingParams("walk");

        ModeParams bicycleParams = new ModeParams(TransportMode.bike);
        bicycleParams.setConstant(0. );
        bicycleParams.setMarginalUtilityOfDistance(-0.0004 );
        bicycleParams.setMarginalUtilityOfTraveling(-6.0 );
        bicycleParams.setMonetaryDistanceRate(0. );
        bikePedConfig.planCalcScore().addModeParams(bicycleParams);

        ModeParams walkParams = new ModeParams(TransportMode.walk);
        walkParams.setConstant(0. );
        walkParams.setMarginalUtilityOfDistance(-0.0004 );
        walkParams.setMarginalUtilityOfTraveling(-6.0 );
        walkParams.setMonetaryDistanceRate(0. );
        bikePedConfig.planCalcScore().addModeParams(walkParams);
    }

    private void finalizeCarConfig(Config config, int year) {
        config.qsim().setFlowCapFactor(properties.main.scaleFactor * Double.parseDouble(Resources.instance.getString(de.tum.bgu.msm.resources.Properties.TRIP_SCALING_FACTOR)));
        config.qsim().setStorageCapFactor(properties.main.scaleFactor * Double.parseDouble(Resources.instance.getString(de.tum.bgu.msm.resources.Properties.TRIP_SCALING_FACTOR)));

        logger.info("Flow Cap Factor: " + config.qsim().getFlowCapFactor());
        logger.info("Storage Cap Factor: " + config.qsim().getStorageCapFactor());

        ActivityParams homeActivity = new ActivityParams("home").setTypicalDuration(12 * 60 * 60);
        config.planCalcScore().addActivityParams(homeActivity);

        ActivityParams workActivity = new ActivityParams("work").setTypicalDuration(8 * 60 * 60);
        config.planCalcScore().addActivityParams(workActivity);

        ActivityParams educationActivity = new ActivityParams("education").setTypicalDuration(8 * 60 * 60);
        config.planCalcScore().addActivityParams(educationActivity);

        ActivityParams shoppingActivity = new ActivityParams("shopping").setTypicalDuration(1 * 60 * 60);
        config.planCalcScore().addActivityParams(shoppingActivity);

        ActivityParams otherActivity = new ActivityParams("other").setTypicalDuration(1 * 60 * 60);
        config.planCalcScore().addActivityParams(otherActivity);

        ActivityParams airportActivity = new ActivityParams("airport").setTypicalDuration(1 * 60 * 60);
        config.planCalcScore().addActivityParams(airportActivity);

        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
        String outputDirectory = outputDirectoryRoot + "/matsim/" + year + "/car/";
        config.controler().setRunId(String.valueOf(year));
        config.controler().setOutputDirectory(outputDirectory);
        config.controler().setWritePlansInterval(Math.max(config.controler().getLastIteration(), 1));
        config.controler().setWriteEventsInterval(Math.max(config.controler().getLastIteration(), 1));
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        if (properties.transportModel.includeAccessEgress) {
            config.plansCalcRoute().setAccessEgressType(PlansCalcRouteConfigGroup.AccessEgressType.walkConstantTimeToLink);
        }
        config.transit().setUsingTransitInMobsim(false);
        config.qsim().setEndTime(24*60*60);
    }

    /**
     * @param eventsFile
     */
    private void replayFromEvents(String eventsFile) {
        Scenario scenario = ScenarioUtils.loadScenario(initialMatsimConfig);
        TravelTime travelTime = TravelTimeUtils.createTravelTimesFromEvents(scenario, eventsFile);
        TravelDisutility travelDisutility = ControlerDefaults.createDefaultTravelDisutilityFactory(scenario).createTravelDisutility(travelTime);
        updateTravelTimes(travelTime, travelDisutility);
    }

    private void updateTravelTimes(TravelTime travelTime, TravelDisutility disutility) {
        matsimData.update(disutility, travelTime);
        matsimData.getConfig().plansCalcRoute().setRoutingRandomness(0);
        internalTravelTimes.update(matsimData);
        final TravelTimes mainTravelTimes = dataContainer.getTravelTimes();

        if (mainTravelTimes != this.internalTravelTimes && mainTravelTimes instanceof SkimTravelTimes) {
            ((SkimTravelTimes) mainTravelTimes).updateSkimMatrix(internalTravelTimes.getPeakSkim(TransportMode.car), TransportMode.car);
            if ((properties.transportModel.transportModelIdentifier == TransportModelPropertiesModule.TransportModelIdentifier.MATSIM)) {
                ((SkimTravelTimes) mainTravelTimes).updateSkimMatrix(internalTravelTimes.getPeakSkim(TransportMode.pt), TransportMode.pt);
            }
            ((SkimTravelTimes) mainTravelTimes).updateRegionalTravelTimes(dataContainer.getGeoData().getRegions().values(),
                    dataContainer.getGeoData().getZones().values());
        }
    }

}