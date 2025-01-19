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
package de.tum.bgu.msm.health;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.MitoGender;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.Purpose;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.matsim.MatsimData;
import de.tum.bgu.msm.matsim.MatsimScenarioAssembler;
import de.tum.bgu.msm.matsim.MatsimTravelTimesAndCosts;
import de.tum.bgu.msm.matsim.SiloMatsimUtils;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.modules.TransportModelPropertiesModule;
import io.SpeedsReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.contrib.dvrp.trafficmonitoring.TravelTimeUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.*;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.ControlerDefaults;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.MainModeIdentifierImpl;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;
import org.matsim.vehicles.VehiclesFactory;
import routing.BicycleConfigGroup;
import routing.BicycleModule;
import routing.WalkConfigGroup;
import routing.WalkModule;
import routing.components.Gradient;
import routing.components.JctStress;
import routing.components.LinkAmbience;
import routing.components.LinkStress;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import static org.matsim.core.config.groups.ScoringConfigGroup.ModeParams;

/**
 * @author qinzhang
 */
public final class MatsimTransportModelMCRHealth implements TransportModel {

    private static final Logger logger = LogManager.getLogger(MatsimTransportModelMCRHealth.class);

    private final Properties properties;
    private final Config initialMatsimConfig;

    private final MatsimData matsimData;
    private final MatsimTravelTimesAndCosts internalTravelTimes;

    private final DataContainer dataContainer;

    private MatsimScenarioAssembler scenarioAssembler;

    private List<Day> simulatedDays;

    protected final Random random;

    public MatsimTransportModelMCRHealth(DataContainer dataContainer, Config matsimConfig,
                                         Properties properties, MatsimScenarioAssembler scenarioAssembler,
                                         MatsimData matsimData, Random random) {
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
        this.simulatedDays = Arrays.asList(Day.thursday,Day.saturday,Day.sunday);
        this.random = random;
    }

    @Override
    public void setup() {
        internalTravelTimes.initialize(dataContainer.getGeoData(), matsimData);

        if (properties.transportModel.matsimInitialEventsFile == null) {
            //TODO: comment out for longitudinal simulation. need to make it more general
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
        } else if (properties.healthData.exposureModelYears.contains(year)){
            runMitoModel (year);

            logger.warn( "Updating reginal travel times");
            final TravelTimes mainTravelTimes = dataContainer.getTravelTimes();

            if (mainTravelTimes instanceof SkimTravelTimes) {
                ((SkimTravelTimes) mainTravelTimes).updateRegionalTravelTimes(dataContainer.getGeoData().getRegions().values(),
                        dataContainer.getGeoData().getZones().values());
            }
            logger.warn( "finish update reginal travel times");
        }
    }



    @Override
    public void endSimulation() {
    }

    private void runMitoModel(int year) {
        logger.warn("Running MITO model only for year " + year + ".");
        ((MitoMatsimScenarioAssemblerMCR)scenarioAssembler).runMitoStandalone(year);
    }


    private void runTransportModel(int year) {
        logger.warn("Running MATSim transport model for year " + year + ".");
        Map<Day, Scenario> assembledMultiScenario;
        TravelTimes travelTimes = dataContainer.getTravelTimes();
        if (year == properties.main.baseYear &&
                properties.transportModel.transportModelIdentifier == TransportModelPropertiesModule.TransportModelIdentifier.MATSIM){
            //if using the SimpleCommuteModeChoiceScenarioAssembler, we need some intial travel times (this will use an unlodaded network)
            TravelTime myTravelTime = SiloMatsimUtils.getAnEmptyNetworkTravelTime();
            TravelDisutility myTravelDisutility = SiloMatsimUtils.getAnEmptyNetworkTravelDisutility();
            updateTravelTimes(myTravelTime, myTravelDisutility);
        }

        assembledMultiScenario = scenarioAssembler.assembleMultiScenarios(initialMatsimConfig, year, travelTimes);

        //run car truck simulation
        runCarTruckSimulation(year, assembledMultiScenario);

        //run bike ped simulation
        //runBikePedSimulation(year, assembledMultiScenario);
    }

    private void runBikePedSimulation(int year, Map<Day, Scenario> assembledMultiScenario) {
        for (Day day : simulatedDays) {
            Scenario assembledScenario = assembledMultiScenario.get(day);
            MainModeIdentifierImpl mainModeIdentifier = new MainModeIdentifierImpl();

            Population populationBikePed = PopulationUtils.createPopulation(ConfigUtils.createConfig());

            // Add bike, and pedestrian plans from MITO
            //TODO: do we need to scale it down?
            for (Person pp : assembledScenario.getPopulation().getPersons().values()) {
                String mode = mainModeIdentifier.identifyMainMode(TripStructureUtils.getLegs(pp.getSelectedPlan()));
                if("bike".equals(mode) || "walk".equals(mode)){
                    if (random.nextDouble() < properties.healthData.matsim_scale_factor_bikePed){
                        populationBikePed.addPerson(pp);
                    }
                }
            }

            logger.warn("Running MATSim transport model for " + day + " Bike&Ped scenario " + year + ".");
            //initial bike, ped simulation config
            Config bikePedConfig = ConfigUtils.loadConfig(initialMatsimConfig.getContext());
            bikePedConfig.addModule(new BicycleConfigGroup());
            bikePedConfig.addModule(new WalkConfigGroup());
            fillBikePedConfig(bikePedConfig, year, day);

            //initialize scenario
            MutableScenario matsimScenario = (MutableScenario) ScenarioUtils.loadScenario(bikePedConfig);
            matsimScenario.setPopulation(populationBikePed);
            logger.info("total population " + day + " | Bike Walk: " + populationBikePed.getPersons().size());

            // set vehicles
            EnumMap<Mode, EnumMap<MitoGender, Map<Integer,Double>>> allSpeeds = ((DataContainerHealth)dataContainer).getAvgSpeeds();
            VehiclesFactory fac = VehicleUtils.getFactory();
            for(MitoGender gender : MitoGender.values()) {
                for(int age = 0 ; age <= 100 ; age++) {
                    VehicleType walk = fac.createVehicleType(Id.create(TransportMode.walk + gender + age, VehicleType.class));
                    walk.setMaximumVelocity(allSpeeds.get(Mode.walk).get(gender).get(age));
                    walk.setNetworkMode(TransportMode.walk);
                    walk.setPcuEquivalents(0.);
                    matsimScenario.getVehicles().addVehicleType(walk);

                    VehicleType bicycle = fac.createVehicleType(Id.create(TransportMode.bike + gender + age, VehicleType.class));
                    bicycle.setMaximumVelocity(allSpeeds.get(Mode.bicycle).get(gender).get(age));
                    bicycle.setNetworkMode(TransportMode.bike);
                    bicycle.setPcuEquivalents(0.);
                    matsimScenario.getVehicles().addVehicleType(bicycle);
                }
            }

            // Create vehicle for each person (i.e., trip)
            for(Person person : matsimScenario.getPopulation().getPersons().values()) {
                MitoGender gender = (MitoGender) person.getAttributes().getAttribute("sex");
                int age = (int) person.getAttributes().getAttribute("age");
                String mode = (String) person.getAttributes().getAttribute("mode");
                Id<Vehicle> vehicleId = Id.createVehicleId(person.getId().toString());
                VehicleType vehicleType = matsimScenario.getVehicles().getVehicleTypes().get(Id.create(mode + gender + age, VehicleType.class));
                Vehicle veh = fac.createVehicle(vehicleId,vehicleType);
                Map<String,Id<Vehicle>> modeToVehicle = new HashMap<>();
                modeToVehicle.put(mode,vehicleId);
                VehicleUtils.insertVehicleIdsIntoPersonAttributes(person,modeToVehicle);
                matsimScenario.getVehicles().addVehicle(veh);
            }

            matsimScenario.getConfig().qsim().setVehiclesSource(QSimConfigGroup.VehiclesSource.fromVehiclesData);
            matsimScenario.getConfig().qsim().setVehicleBehavior(QSimConfigGroup.VehicleBehavior.teleport);
            matsimScenario.getConfig().qsim().setUsePersonIdForMissingVehicleId(true);


            //set up controler
            final Controler controlerBikePed = new Controler(matsimScenario);
            controlerBikePed.addOverridingModule(new WalkModule());
            controlerBikePed.addOverridingModule(new BicycleModule());


            controlerBikePed.run();
            logger.warn("Running MATSim transport model for " + day + " Bike&Ped scenario " + year + " finished.");
        }
    }

    private void runCarTruckSimulation(int year, Map<Day, Scenario> assembledMultiScenario) {
        for (Day day : simulatedDays) {
            Scenario assembledScenario = assembledMultiScenario.get(day);
            MainModeIdentifierImpl mainModeIdentifier = new MainModeIdentifierImpl();

            Population populationCarTruck = PopulationUtils.createPopulation(ConfigUtils.createConfig());

            // Add truck plans from tfgm (static)
            String truckPlans = properties.main.baseDirectory + properties.healthData.truck_plan_file;
            PopulationUtils.readPopulation(populationCarTruck, truckPlans);

            double truckSample = 0.;
            if(day.equals(Day.thursday)) {
                truckSample = 1.278066;
            } else if (day.equals(Day.saturday)) {
                truckSample = 0.430817;
            } else if (day.equals(Day.sunday)) {
                truckSample = 0.178852;
            } else {
                throw new RuntimeException("Unrecognised day " + day);
            }

            logger.info(day + " truck sample: " + truckSample);
            if(truckSample < 1.) {
                PopulationUtils.sampleDown(populationCarTruck, truckSample);
            }

            logger.warn("MATSim truck population: " + day + "|" + year + "|" + populationCarTruck.getPersons().size());

            //Add through car plans from tfgm (static)
            Population populationThroughTraffic = PopulationUtils.createPopulation(ConfigUtils.createConfig());

            String throughPlans = properties.main.baseDirectory + properties.healthData.throughTraffic_plan_file;
            PopulationUtils.readPopulation(populationThroughTraffic, throughPlans);

            double throughCarSample = 0.;
            if(day.equals(Day.thursday)) {
                throughCarSample = 1.;
            } else if (day.equals(Day.saturday)) {
                throughCarSample = 0.79;
            } else if (day.equals(Day.sunday)) {
                throughCarSample = 0.46;
            } else {
                throw new RuntimeException("Unrecognised day " + day);
            }

            logger.info(day + " through traffic sample: " + throughCarSample);
            if(throughCarSample < 1.) {
                PopulationUtils.sampleDown(populationThroughTraffic, throughCarSample);
            }

            for (Person pp : populationThroughTraffic.getPersons().values()) {
                populationCarTruck.addPerson(pp);
            }

            logger.warn("MATSim truck/through population: " + day + "|" + year + "|" + populationCarTruck.getPersons().size());

            // Add car plans from MITO
            for (Person pp : assembledScenario.getPopulation().getPersons().values()) {
                String mode = mainModeIdentifier.identifyMainMode(TripStructureUtils.getLegs(pp.getSelectedPlan()));
                if("car".equals(mode)){
                        populationCarTruck.addPerson(pp);
                }
            }

            logger.warn("MATSim car/truck/through population: " + day + "|" + year + "|" + populationCarTruck.getPersons().size());


            logger.warn("Running MATSim transport model for " + day + " car scenario " + year + ".");
            //initialize car truck config
            Config carTruckConfig = ConfigUtils.loadConfig(initialMatsimConfig.getContext());
            finalizeCarTruckConfig(carTruckConfig, year, day);

            //initialize scenario
            MutableScenario matsimScenario = (MutableScenario) ScenarioUtils.loadScenario(carTruckConfig);
            matsimScenario.setPopulation(populationCarTruck);

            //set vehicle types
            VehicleType car = VehicleUtils.getFactory().createVehicleType(Id.create(TransportMode.car, VehicleType.class));
            car.setPcuEquivalents(1.);
            car.setLength(7.5);
            car.setNetworkMode(TransportMode.car);
            matsimScenario.getVehicles().addVehicleType(car);

            VehicleType truck = VehicleUtils.getFactory().createVehicleType(Id.create(TransportMode.truck, VehicleType.class));
            truck.setPcuEquivalents(2.5);
            truck.setLength(15.);
            truck.setNetworkMode(TransportMode.truck);
            matsimScenario.getVehicles().addVehicleType(truck);

            matsimScenario.getConfig().qsim().setVehiclesSource(QSimConfigGroup.VehiclesSource.modeVehicleTypesFromVehiclesData);

            //set up controler
            final Controler controlerCar = new Controler(matsimScenario);
            controlerCar.run();
            logger.warn("Running MATSim transport model for " + day + " car scenario " + year + " finished.");

            // Get travel Times from MATSim - weekday
            if(day.equals(Day.thursday)){
                logger.warn("Using MATSim to compute travel times from zone to zone.");
                TravelTime travelTime = controlerCar.getLinkTravelTimes();
                TravelDisutility travelDisutility = controlerCar.getTravelDisutilityFactory().createTravelDisutility(travelTime);
                updateTravelTimes(travelTime, travelDisutility);
            }
        }
    }

    private void fillBikePedConfig(Config bikePedConfig, int year, Day day) {
        // set active mode networks
        bikePedConfig.network().setInputFile(properties.main.baseDirectory + properties.healthData.activeModeNetworkFile);

        // set input file and basic controler settings
        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
        String outputDirectory = outputDirectoryRoot + "/matsim/" + year + "/" + day + "/bikePed/";
        bikePedConfig.controller().setOutputDirectory(outputDirectory);
        bikePedConfig.controller().setLastIteration(1);
        bikePedConfig.controller().setRunId(String.valueOf(year));
        bikePedConfig.controller().setWritePlansInterval(Math.max(bikePedConfig.controller().getLastIteration(), 1));
        bikePedConfig.controller().setWriteEventsInterval(Math.max(bikePedConfig.controller().getLastIteration(), 1));
        bikePedConfig.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

        // set qsim - passingQ
        bikePedConfig.qsim().setFlowCapFactor(properties.main.scaleFactor * properties.healthData.matsim_scale_factor_bikePed);
        bikePedConfig.qsim().setStorageCapFactor(properties.main.scaleFactor * properties.healthData.matsim_scale_factor_bikePed);
        logger.info("Flow Cap Factor for bikePed: " + bikePedConfig.qsim().getFlowCapFactor());
        logger.info("Storage Cap Factor for bikePed: " + bikePedConfig.qsim().getStorageCapFactor());
        bikePedConfig.qsim().setEndTime(24*60*60);
        bikePedConfig.qsim().setLinkDynamics(QSimConfigGroup.LinkDynamics.PassingQ);


        // set routing modes
        List<String> mainModeList = new ArrayList<>();
        mainModeList.add(TransportMode.bike);
        mainModeList.add(TransportMode.walk);
        bikePedConfig.qsim().setMainModes(mainModeList);
        bikePedConfig.routing().setNetworkModes(mainModeList);
        bikePedConfig.routing().removeModeRoutingParams("bike");
        bikePedConfig.routing().removeModeRoutingParams("walk");
        bikePedConfig.routing().removeModeRoutingParams("pt");


        // BIKE ATTRIBUTES
        List<ToDoubleFunction<Link>> bikeAttributes = new ArrayList<>();
        bikeAttributes.add(l -> Math.max(Math.min(Gradient.getGradient(l),0.5),0.));
        bikeAttributes.add(l -> LinkStress.getStress(l,TransportMode.bike));

        // Bike weights
        Function<Person,double[]> bikeWeights = p -> {
            switch((Purpose) p.getAttributes().getAttribute("purpose")) {
                case HBW -> {
                    if(p.getAttributes().getAttribute("sex").equals(MitoGender.FEMALE)) {
                        return new double[] {35.9032908,2.3084587 + 2.7762033};
                    } else {
                        return new double[] {35.9032908,2.3084587};
                    }
                }
                case HBE -> {
                    return new double[] {0,4.3075357};
                }
                case HBS, HBR, HBO -> {
                    if((int) p.getAttributes().getAttribute("age") < 15) {
                        return new double[] {57.0135325,1.2411983 + 6.4243251};
                    } else {
                        return new double[] {57.0135325,1.2411983};
                    }
                }
                default -> {
                    return null;
                }
            }
        };

        // Bicycle config group
        BicycleConfigGroup bicycle = (BicycleConfigGroup) bikePedConfig.getModules().get(BicycleConfigGroup.GROUP_NAME);
        bicycle.setAttributes(bikeAttributes);
        bicycle.setWeights(bikeWeights);

        // WALK ATTRIBUTES
        List<ToDoubleFunction<Link>> walkAttributes = new ArrayList<>();
        walkAttributes.add(l -> Math.max(0.,0.81 - LinkAmbience.getVgviFactor(l)));
        walkAttributes.add(l -> Math.min(1.,l.getFreespeed() / 22.35));
        walkAttributes.add(l -> JctStress.getStressProp(l,TransportMode.walk));

        // Walk weights
        Function<Person,double[]> walkWeights = p -> {
            switch ((Purpose) p.getAttributes().getAttribute("purpose")) {
                case HBW -> {
                    return new double[]{0.3307472, 0, 4.9887390};
                }
                case HBE -> {
                    return new double[]{0, 0, 1.0037846};
                }
                case HBS, HBR, HBO -> {
                    if ((int) p.getAttributes().getAttribute("age") < 15) {
                        return new double[]{0.7789561, 0.4479527 + 2.0418898, 5.8219067};
                    } else if ((int) p.getAttributes().getAttribute("age") >= 65) {
                        return new double[]{0.7789561, 0.4479527 + 0.3715017, 5.8219067};
                    } else {
                        return new double[]{0.7789561, 0.4479527, 5.8219067};
                    }
                }
                case HBA -> {
                    return new double[]{0.6908324, 0, 0};
                }
                case NHBO -> {
                    return new double[]{0, 3.4485883, 0};
                }
                default -> {
                    return null;
                }
            }
        };

        // Walk config group
        WalkConfigGroup walkConfigGroup = (WalkConfigGroup) bikePedConfig.getModules().get(WalkConfigGroup.GROUP_NAME);
        walkConfigGroup.setAttributes(walkAttributes);
        walkConfigGroup.setWeights(walkWeights);

        // set scoring parameters
        ModeParams bicycleParams = new ModeParams(TransportMode.bike);
        bicycleParams.setConstant(0. );
        bicycleParams.setMarginalUtilityOfDistance(-0.0004 );
        bicycleParams.setMarginalUtilityOfTraveling(-6.0 );
        bicycleParams.setMonetaryDistanceRate(0. );
        bikePedConfig.scoring().addModeParams(bicycleParams);

        ModeParams walkParams = new ModeParams(TransportMode.walk);
        walkParams.setConstant(0. );
        walkParams.setMarginalUtilityOfDistance(-0.0004 );
        walkParams.setMarginalUtilityOfTraveling(-6.0 );
        walkParams.setMonetaryDistanceRate(0. );
        bikePedConfig.scoring().addModeParams(walkParams);

        ScoringConfigGroup.ActivityParams homeActivity = new ScoringConfigGroup.ActivityParams("home").setTypicalDuration(12 * 60 * 60);
        bikePedConfig.scoring().addActivityParams(homeActivity);

        ScoringConfigGroup.ActivityParams workActivity = new ScoringConfigGroup.ActivityParams("work").setTypicalDuration(8 * 60 * 60);
        bikePedConfig.scoring().addActivityParams(workActivity);

        ScoringConfigGroup.ActivityParams educationActivity = new ScoringConfigGroup.ActivityParams("education").setTypicalDuration(8 * 60 * 60);
        bikePedConfig.scoring().addActivityParams(educationActivity);

        ScoringConfigGroup.ActivityParams shoppingActivity = new ScoringConfigGroup.ActivityParams("shopping").setTypicalDuration(1 * 60 * 60);
        bikePedConfig.scoring().addActivityParams(shoppingActivity);

        ScoringConfigGroup.ActivityParams recreationActivity = new ScoringConfigGroup.ActivityParams("recreation").setTypicalDuration(1 * 60 * 60);
        bikePedConfig.scoring().addActivityParams(recreationActivity);

        ScoringConfigGroup.ActivityParams otherActivity = new ScoringConfigGroup.ActivityParams("other").setTypicalDuration(1 * 60 * 60);
        bikePedConfig.scoring().addActivityParams(otherActivity);

        ScoringConfigGroup.ActivityParams airportActivity = new ScoringConfigGroup.ActivityParams("airport").setTypicalDuration(1 * 60 * 60);
        bikePedConfig.scoring().addActivityParams(airportActivity);

        //Set strategy
        bikePedConfig.replanning().setMaxAgentPlanMemorySize(5);
        {
            ReplanningConfigGroup.StrategySettings strategySettings = new ReplanningConfigGroup.StrategySettings();
            strategySettings.setStrategyName("ChangeExpBeta");
            strategySettings.setWeight(0.8);
            bikePedConfig.replanning().addStrategySettings(strategySettings);
        }

        {
            ReplanningConfigGroup.StrategySettings strategySettings = new ReplanningConfigGroup.StrategySettings();
            strategySettings.setStrategyName("ReRoute");
            strategySettings.setWeight(0.2);
            bikePedConfig.replanning().addStrategySettings(strategySettings);
        }


        bikePedConfig.transit().setUsingTransitInMobsim(false);
        bikePedConfig.controller().setRoutingAlgorithmType(ControllerConfigGroup.RoutingAlgorithmType.Dijkstra);

    }


    private void finalizeCarTruckConfig(Config config, int year, Day day) {
        // Set basic setting
        config.qsim().setEndTime(24*60*60);
        config.global().setNumberOfThreads(16);
        config.qsim().setNumberOfThreads(16);
        config.transit().setUsingTransitInMobsim(false);
        config.routing().setRoutingRandomness(0.);

        // Set scale factor
        //TODO: check with Mahsa and Corin, if we need scale down again, or it is already incorperated in network
        config.qsim().setFlowCapFactor(properties.main.scaleFactor * properties.healthData.matsim_scale_factor_car);
        config.qsim().setStorageCapFactor(properties.main.scaleFactor * properties.healthData.matsim_scale_factor_car);
        logger.info("Flow Cap Factor for car/truck: " + config.qsim().getFlowCapFactor());
        logger.info("Storage Cap Factor for car/truck: " + config.qsim().getStorageCapFactor());

        // Set output directory
        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
        String outputDirectory = outputDirectoryRoot + "/matsim/" + year + "/" + day + "/car/";
        config.controller().setRunId(String.valueOf(year));
        config.controller().setOutputDirectory(outputDirectory);
        config.controller().setWritePlansInterval(Math.max(config.controller().getLastIteration(), 1));
        config.controller().setWriteEventsInterval(Math.max(config.controller().getLastIteration(), 1));
        config.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);



        //set mode params
        List<String> mainModeList = new ArrayList<>();
        mainModeList.add("car");
        mainModeList.add("truck");
        config.qsim().setMainModes(mainModeList);
        config.routing().setNetworkModes(mainModeList);
        config.routing().setAccessEgressType(RoutingConfigGroup.AccessEgressType.none);

        ModeParams carParams = config.scoring().getOrCreateModeParams(TransportMode.car);
        ModeParams truckParams = new ModeParams(TransportMode.truck);
        truckParams.setConstant(carParams.getConstant());
        truckParams.setDailyMonetaryConstant(carParams.getDailyMonetaryConstant());
        truckParams.setMarginalUtilityOfDistance(carParams.getMarginalUtilityOfDistance());
        truckParams.setDailyUtilityConstant(carParams.getDailyUtilityConstant());
        truckParams.setMonetaryDistanceRate(carParams.getMonetaryDistanceRate());
        config.scoring().addModeParams(truckParams);

    }

    /**
     * @param eventsFile
     */
    private void replayFromEvents(String eventsFile) {
        initialMatsimConfig.routing().setRoutingRandomness(0.);
        Scenario scenario = ScenarioUtils.loadScenario(initialMatsimConfig);
        TravelTime travelTime = TravelTimeUtils.createTravelTimesFromEvents(scenario.getNetwork(),scenario.getConfig(),eventsFile);
        TravelDisutility travelDisutility = ControlerDefaults.createDefaultTravelDisutilityFactory(scenario).createTravelDisutility(travelTime);
        updateTravelTimes(travelTime, travelDisutility);
    }

    private void updateTravelTimes(TravelTime travelTime, TravelDisutility disutility) {
        matsimData.update(disutility, travelTime);
        matsimData.getConfig().routing().setRoutingRandomness(0.);
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