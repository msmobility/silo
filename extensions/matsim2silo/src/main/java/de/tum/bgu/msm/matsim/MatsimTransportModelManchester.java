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

import ch.sbb.matsim.config.SBBTransitConfigGroup;
import ch.sbb.matsim.config.SwissRailRaptorConfigGroup;
import ch.sbb.matsim.mobsim.qsim.SBBTransitModule;
import ch.sbb.matsim.mobsim.qsim.pt.SBBTransitEngineQSimModule;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.Person;
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
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.contrib.dvrp.trafficmonitoring.TravelTimeUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.ControlerDefaults;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;

import java.io.File;
import java.util.*;

/**
 * @author dziemke, nkuehnel
 */
public final class MatsimTransportModelManchester implements TransportModel {

    private static final Logger logger = Logger.getLogger(MatsimTransportModelManchester.class);

    private final Properties properties;
    private final Config initialMatsimConfig;

    private final MatsimData matsimData;
    private final MatsimTravelTimesAndCosts internalTravelTimes;

    private final DataContainer dataContainer;

    private MatsimScenarioAssembler scenarioAssembler;

    private boolean useSSB = false;
    private static boolean deterministic = false;
    private static double maxSearchRadius = 1000;
    private static double betaTransfer = 300;


    public MatsimTransportModelManchester(DataContainer dataContainer, Config matsimConfig,
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
        Scenario assembledScenario = ScenarioUtils.createScenario(configureMatsim(year));
        TravelTimes travelTimes = dataContainer.getTravelTimes();

        if (year == properties.main.baseYear &&
                properties.transportModel.transportModelIdentifier == TransportModelPropertiesModule.TransportModelIdentifier.MATSIM){
            //if using the SimpleCommuteModeChoiceScenarioAssembler, we need some initial travel times (this will use an unlodaded network)
            TravelTime myTravelTime = SiloMatsimUtils.getAnEmptyNetworkTravelTime();
            TravelDisutility myTravelDisutility = SiloMatsimUtils.getAnEmptyNetworkTravelDisutility();
            updateTravelTimes(myTravelTime, myTravelDisutility);
        }

        /*for (Household household: dataContainer.getHouseholdDataManager().getHouseholds()) {
            for (Person pp : household.getPersons().values()) {
                PopulationFactory populationFactory = assembledScenario.getPopulation().getFactory();

                org.matsim.api.core.v01.population.Person matsimAlterEgo = SiloMatsimUtils.createMatsimAlterEgo(populationFactory, pp, household.getAutos());
                assembledScenario.getPopulation().addPerson(matsimAlterEgo);
            }
        }

        matsimData.updateMatsimPopulation(assembledScenario.getPopulation());
*/
        // create a dummy vehicle type
        VehicleType dummyVehType = assembledScenario.getVehicles().getFactory().createVehicleType(Id.create("defaultVehicleType", VehicleType.class));
        assembledScenario.getVehicles().addVehicleType(dummyVehType);

        for (org.matsim.api.core.v01.population.Person person : assembledScenario.getPopulation().getPersons().values()) {
            Id<Vehicle> vehicleId = Id.createVehicleId(person.getId());
            assembledScenario.getVehicles().addVehicle(assembledScenario.getVehicles().getFactory().createVehicle(vehicleId, dummyVehType));
            Map<String, Id<Vehicle>> modeToVehMap = new HashMap<>();
            modeToVehMap.put(TransportMode.car, vehicleId);
            VehicleUtils.insertVehicleIdsIntoAttributes(person, modeToVehMap);
        }

        assembledScenario = scenarioAssembler.assembleScenario(assembledScenario.getConfig(), year, travelTimes);

        //finalizeConfig(assembledScenario.getConfig(), year);
        logger.warn("Population: " + assembledScenario.getPopulation().getPersons().size());
        logger.warn("Iteration: " + assembledScenario.getConfig().controler().getLastIteration());

        final Controler controler = new Controler(assembledScenario);

        if(useSSB){

            // To use the deterministic pt simulation (Part 1 of 2):
            controler.addOverridingModule(new SBBTransitModule());

            // To use the fast pt router (Part 1 of 1)
            controler.addOverridingModule(new SwissRailRaptorModule());

            // To use the deterministic pt simulation (Part 2 of 2):
            controler.configureQSimComponents(components -> {
                SBBTransitEngineQSimModule.configure(components);

                // if you have other extensions that provide QSim components, call their configure-method here
            });

            setSBBConfig(controler.getConfig(),deterministic, maxSearchRadius, betaTransfer);
        }

        controler.run();
        logger.warn("Running MATSim transport model for year " + year + " finished.");

        // Get travel Times from MATSim
        logger.warn("Using MATSim to compute travel times from zone to zone.");
        TravelTime travelTime = controler.getLinkTravelTimes();
        TravelDisutility travelDisutility = controler.getTravelDisutilityFactory().createTravelDisutility(travelTime);
        updateTravelTimes(travelTime, travelDisutility);
    }

    private void finalizeConfig(Config config, int year) {
        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
        String outputDirectory = outputDirectoryRoot + "/matsim/" + year + "/";
        config.controler().setRunId(String.valueOf(year));
        config.controler().setOutputDirectory(outputDirectory);
        config.controler().setWritePlansInterval(Math.max(config.controler().getLastIteration(), 1));
        config.controler().setWriteEventsInterval(Math.max(config.controler().getLastIteration(), 1));
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.plansCalcRoute().setRoutingRandomness(0.);
    }

    /**
     * @param eventsFile
     */
    private void replayFromEvents(String eventsFile) {
        Scenario scenario = ScenarioUtils.loadScenario(initialMatsimConfig);
        TravelTime travelTime = TravelTimeUtils.createTravelTimesFromEvents(scenario.getNetwork(),scenario.getConfig(), eventsFile);
        TravelDisutility travelDisutility = ControlerDefaults.createDefaultTravelDisutilityFactory(scenario).createTravelDisutility(travelTime);
        updateTravelTimes(travelTime, travelDisutility);
    }

    private void updateTravelTimes(TravelTime travelTime, TravelDisutility disutility) {
        matsimData.update(disutility, travelTime);
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

    public Config configureMatsim(int year) {
        Config config = ConfigUtils.createConfig();
        config.controler().setFirstIteration(0);
        config.controler().setMobsim("qsim");
        config.controler().setWritePlansInterval(config.controler().getLastIteration());
        config.controler().setWriteEventsInterval(config.controler().getLastIteration());
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
        String outputDirectory = outputDirectoryRoot + "/matsim/" + year + "/";
        config.controler().setOutputDirectory(outputDirectory);

        config.qsim().setEndTime(26 * 3600);
        config.qsim().setTrafficDynamics(QSimConfigGroup.TrafficDynamics.withHoles);
        config.vspExperimental().setWritingOutputEvents(true); // writes final events into toplevel directory

        //strategy config
        {
            StrategyConfigGroup.StrategySettings strat = new StrategyConfigGroup.StrategySettings();
            strat.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ReRoute);
            strat.setWeight(0.2);
            config.strategy().addStrategySettings(strat);
        }


        {
            StrategyConfigGroup.StrategySettings strat = new StrategyConfigGroup.StrategySettings();
            strat.setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.ChangeExpBeta);
            strat.setWeight(0.4);
            config.strategy().addStrategySettings(strat);
        }

        {
            StrategyConfigGroup.StrategySettings strat = new StrategyConfigGroup.StrategySettings();
            strat.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ChangeTripMode);
            strat.setWeight(0.4);
            config.strategy().addStrategySettings(strat);
        }

        config.strategy().setFractionOfIterationsToDisableInnovation(0.8);
        config.strategy().setMaxAgentPlanMemorySize(4);

        PlanCalcScoreConfigGroup.ActivityParams homeActivity = new PlanCalcScoreConfigGroup.ActivityParams("home");
        homeActivity.setTypicalDuration(12 * 60 * 60);
        config.planCalcScore().addActivityParams(homeActivity);

        PlanCalcScoreConfigGroup.ActivityParams workActivity = new PlanCalcScoreConfigGroup.ActivityParams("work");
        workActivity.setTypicalDuration(8 * 60 * 60);
        config.planCalcScore().addActivityParams(workActivity);

        PlansCalcRouteConfigGroup.ModeRoutingParams ptParams = new PlansCalcRouteConfigGroup.ModeRoutingParams("pt");
        ptParams.setBeelineDistanceFactor(1.5);
        ptParams.setTeleportedModeSpeed(50 / 3.6);
        config.plansCalcRoute().addModeRoutingParams(ptParams);

        PlansCalcRouteConfigGroup.ModeRoutingParams bicycleParams = new PlansCalcRouteConfigGroup.ModeRoutingParams("bike");
        bicycleParams.setBeelineDistanceFactor(1.3);
        bicycleParams.setTeleportedModeSpeed(15 / 3.6);
        config.plansCalcRoute().addModeRoutingParams(bicycleParams);

        PlansCalcRouteConfigGroup.ModeRoutingParams walkParams = new PlansCalcRouteConfigGroup.ModeRoutingParams("walk");
        walkParams.setBeelineDistanceFactor(1.3);
        walkParams.setTeleportedModeSpeed(5 / 3.6);
        config.plansCalcRoute().addModeRoutingParams(walkParams);

        String runId = "silo_matsim";
        config.controler().setRunId(runId);
        config.network().setInputFile("input/transport/network2dCar.xml");

        config.qsim().setNumberOfThreads(16);
        config.global().setNumberOfThreads(16);
        config.parallelEventHandling().setNumberOfThreads(16);

        config.controler().setLastIteration(properties.transportModel.matsimIteration);
        config.controler().setWritePlansInterval(config.controler().getLastIteration());
        config.controler().setWriteEventsInterval(config.controler().getLastIteration());

        config.qsim().setStuckTime(10);
        config.qsim().setFlowCapFactor(properties.transportModel.matsimScaleFactor);
        config.qsim().setStorageCapFactor(properties.transportModel.matsimScaleFactor);

        String[] networkModes = properties.transportModel.matsimNetworkModes;
        Set<String> networkModesSet = new HashSet<>();

        for (String mode : networkModes) {
            String matsimMode = Mode.getMatsimMode(Mode.valueOf(mode));
            if (!networkModesSet.contains(matsimMode)) {
                networkModesSet.add(matsimMode);
            }
        }

        config.plansCalcRoute().setNetworkModes(networkModesSet);

        return config;
    }

    public static void setSBBConfig(Config config, boolean deterministic, double maxSearchRadius, double betaTransfer){
        SBBTransitConfigGroup sbbTransitConfigGroup = new SBBTransitConfigGroup();


        if (deterministic) {
            Set<String> deterministicMode = new HashSet<>();
            deterministicMode.add("subway");
            deterministicMode.add("rail");
            sbbTransitConfigGroup.setDeterministicServiceModes(deterministicMode);
            sbbTransitConfigGroup.setCreateLinkEventsInterval(config.controler().getLastIteration());
        }

        SwissRailRaptorConfigGroup swissRailRaptorConfigGroup = new SwissRailRaptorConfigGroup();

        swissRailRaptorConfigGroup.setUseIntermodalAccessEgress(true);
        SwissRailRaptorConfigGroup.IntermodalAccessEgressParameterSet walkAccessEgress =  new SwissRailRaptorConfigGroup.IntermodalAccessEgressParameterSet();
        walkAccessEgress.setMode(TransportMode.walk);
        walkAccessEgress.setInitialSearchRadius(maxSearchRadius);
        walkAccessEgress.setMaxRadius(maxSearchRadius);
        walkAccessEgress.setSearchExtensionRadius(100);
        swissRailRaptorConfigGroup.addIntermodalAccessEgress(walkAccessEgress);

        swissRailRaptorConfigGroup.setUseRangeQuery(true);
        SwissRailRaptorConfigGroup.RangeQuerySettingsParameterSet rangeQuerySettings = new SwissRailRaptorConfigGroup.RangeQuerySettingsParameterSet();
        rangeQuerySettings.setMaxEarlierDeparture(600);
        rangeQuerySettings.setMaxLaterDeparture(900);
        swissRailRaptorConfigGroup.addRangeQuerySettings(rangeQuerySettings);
        //swissRailRaptorConfigGroup.setTransferPenaltyCostPerTravelTimeHour(betaTransfer/3600.);
        //swissRailRaptorConfigGroup.setTransferPenaltyBaseCost(betaTransfer/3600.);

        SwissRailRaptorConfigGroup.RouteSelectorParameterSet routeSelector = new SwissRailRaptorConfigGroup.RouteSelectorParameterSet();
        routeSelector.setBetaTravelTime(1);
        routeSelector.setBetaDepartureTime(1);
        routeSelector.setBetaTransfers(betaTransfer);
        swissRailRaptorConfigGroup.addRouteSelector(routeSelector);

        config.addModule(sbbTransitConfigGroup);
        config.addModule(swissRailRaptorConfigGroup);
    }
}