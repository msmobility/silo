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
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.matsim.MatsimData;
import de.tum.bgu.msm.matsim.MatsimScenarioAssembler;
import de.tum.bgu.msm.matsim.MatsimTravelTimesAndCosts;
import de.tum.bgu.msm.matsim.SiloMatsimUtils;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.modules.TransportModelPropertiesModule;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.contrib.dvrp.trafficmonitoring.TravelTimeUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.QSimConfigGroup;
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
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;
import routing.BicycleConfigGroup;
import routing.BicycleModule;
import routing.WalkConfigGroup;
import routing.WalkModule;

import java.io.File;
import java.util.*;

import static org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ModeParams;

/**
 * @author qinzhang
 */
public final class MatsimTransportModelMCRHealth implements TransportModel {

    private static final Logger logger = Logger.getLogger(MatsimTransportModelMCRHealth.class);

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
            //runTransportModel(properties.main.startYear);
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
            finalizeBikePedConfig(bikePedConfig, year, day);

            //initialize scenario
            MutableScenario matsimScenario = (MutableScenario) ScenarioUtils.loadScenario(bikePedConfig);
            matsimScenario.setPopulation(populationBikePed);
            logger.info("total population " + day + " | Bike Walk: " + populationBikePed.getPersons().size());

            //set vehicle types
            VehicleType walk = VehicleUtils.getFactory().createVehicleType(Id.create(TransportMode.walk, VehicleType.class));
            walk.setMaximumVelocity(properties.healthData.MAX_WALKSPEED / 3.6);
            walk.setPcuEquivalents(0.);
            matsimScenario.getVehicles().addVehicleType(walk);

            VehicleType bicycle = VehicleUtils.getFactory().createVehicleType(Id.create(TransportMode.bike, VehicleType.class));
            bicycle.setMaximumVelocity(properties.healthData.MAX_CYCLESPEED / 3.6);
            bicycle.setPcuEquivalents(0.);
            matsimScenario.getVehicles().addVehicleType(bicycle);

            matsimScenario.getConfig().qsim().setVehiclesSource(QSimConfigGroup.VehiclesSource.modeVehicleTypesFromVehiclesData);

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
            matsimScenario.getVehicles().addVehicleType(car);

            VehicleType truck = VehicleUtils.getFactory().createVehicleType(Id.create(TransportMode.truck, VehicleType.class));
            truck.setPcuEquivalents(2.5);
            truck.setLength(15.);
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


    private void finalizeBikePedConfig(Config bikePedConfig, int year, Day day) {
        // set active mode networks
        bikePedConfig.network().setInputFile(properties.main.baseDirectory + properties.healthData.activeModeNetworkFile);

        // set basic controler settings
        bikePedConfig.controler().setLastIteration(1);
        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
        String outputDirectory = outputDirectoryRoot + "/matsim/" + year + "/" + day + "/bikePed/";
        bikePedConfig.controler().setOutputDirectory(outputDirectory);
        bikePedConfig.controler().setRunId(String.valueOf(year));
        bikePedConfig.controler().setWritePlansInterval(Math.max(bikePedConfig.controler().getLastIteration(), 1));
        bikePedConfig.controler().setWriteEventsInterval(Math.max(bikePedConfig.controler().getLastIteration(), 1));
        bikePedConfig.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

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
        bikePedConfig.plansCalcRoute().setNetworkModes(mainModeList);

        // set walk/bike routing parameters
        BicycleConfigGroup bicycleConfigGroup = (BicycleConfigGroup) bikePedConfig.getModules().get(BicycleConfigGroup.GROUP_NAME);
        bicycleConfigGroup.getMarginalCostGradient().put("commute",66.8);
        bicycleConfigGroup.getMarginalCostVgvi().put("commute",0.);
        bicycleConfigGroup.getMarginalCostLinkStress().put("commute",6.3);
        bicycleConfigGroup.getMarginalCostJctStress().put("commute",0.);
        bicycleConfigGroup.getMarginalCostGradient().put("nonCommute",63.45);
        bicycleConfigGroup.getMarginalCostVgvi().put("nonCommute",0.);
        bicycleConfigGroup.getMarginalCostLinkStress().put("nonCommute",1.59);
        bicycleConfigGroup.getMarginalCostJctStress().put("nonCommute",0.);


        WalkConfigGroup walkConfigGroup = (WalkConfigGroup) bikePedConfig.getModules().get(WalkConfigGroup.GROUP_NAME);
        walkConfigGroup.getMarginalCostGradient().put("commute",0.);
        walkConfigGroup.getMarginalCostVgvi().put("commute",0.);
        walkConfigGroup.getMarginalCostLinkStress().put("commute",0.);
        walkConfigGroup.getMarginalCostJctStress().put("commute",4.27);
        walkConfigGroup.getMarginalCostGradient().put("nonCommute",0.);
        walkConfigGroup.getMarginalCostVgvi().put("nonCommute",0.62);
        walkConfigGroup.getMarginalCostLinkStress().put("nonCommute",0.);
        walkConfigGroup.getMarginalCostJctStress().put("nonCommute",14.34);

        // set scoring parameters
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

        bikePedConfig.transit().setUsingTransitInMobsim(false);
    }

    private void finalizeCarTruckConfig(Config config, int year, Day day) {
        // Set basic setting
        config.qsim().setEndTime(24*60*60);
        config.global().setNumberOfThreads(16);
        config.qsim().setNumberOfThreads(16);
        config.transit().setUsingTransitInMobsim(false);
        config.plansCalcRoute().setRoutingRandomness(0.);

        // Set scale factor
        //TODO: check with Mahsa and Corin, if we need scale down again, or it is already incorperated in network
        config.qsim().setFlowCapFactor(properties.main.scaleFactor * properties.healthData.matsim_scale_factor_car);
        config.qsim().setStorageCapFactor(properties.main.scaleFactor * properties.healthData.matsim_scale_factor_car);
        logger.info("Flow Cap Factor for car/truck: " + config.qsim().getFlowCapFactor());
        logger.info("Storage Cap Factor for car/truck: " + config.qsim().getStorageCapFactor());

        // Set output directory
        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
        String outputDirectory = outputDirectoryRoot + "/matsim/" + year + "/" + day + "/car/";
        config.controler().setRunId(String.valueOf(year));
        config.controler().setOutputDirectory(outputDirectory);
        config.controler().setWritePlansInterval(Math.max(config.controler().getLastIteration(), 1));
        config.controler().setWriteEventsInterval(Math.max(config.controler().getLastIteration(), 1));
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);



        //set mode params
        List<String> mainModeList = new ArrayList<>();
        mainModeList.add("car");
        mainModeList.add("truck");
        config.qsim().setMainModes(mainModeList);
        config.plansCalcRoute().setNetworkModes(mainModeList);

        ModeParams carParams = config.planCalcScore().getOrCreateModeParams(TransportMode.car);
        ModeParams truckParams = new ModeParams(TransportMode.truck);
        truckParams.setConstant(carParams.getConstant());
        truckParams.setDailyMonetaryConstant(carParams.getDailyMonetaryConstant());
        truckParams.setMarginalUtilityOfDistance(carParams.getMarginalUtilityOfDistance());
        truckParams.setDailyUtilityConstant(carParams.getDailyUtilityConstant());
        truckParams.setMonetaryDistanceRate(carParams.getMonetaryDistanceRate());
        config.planCalcScore().addModeParams(truckParams);

    }

    /**
     * @param eventsFile
     */
    private void replayFromEvents(String eventsFile) {
        initialMatsimConfig.plansCalcRoute().setRoutingRandomness(0.);
        Scenario scenario = ScenarioUtils.loadScenario(initialMatsimConfig);
        TravelTime travelTime = TravelTimeUtils.createTravelTimesFromEvents(scenario.getNetwork(),scenario.getConfig(),eventsFile);
        TravelDisutility travelDisutility = ControlerDefaults.createDefaultTravelDisutilityFactory(scenario).createTravelDisutility(travelTime);
        updateTravelTimes(travelTime, travelDisutility);
    }

    private void updateTravelTimes(TravelTime travelTime, TravelDisutility disutility) {
        matsimData.update(disutility, travelTime);
        matsimData.getConfig().plansCalcRoute().setRoutingRandomness(0.);
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