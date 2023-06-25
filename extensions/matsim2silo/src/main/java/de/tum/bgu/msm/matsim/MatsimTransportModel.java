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
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.modules.TransportModelPropertiesModule;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.contrib.dvrp.trafficmonitoring.TravelTimeUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.ControlerDefaults;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;
import org.matsim.vehicles.Vehicles;

import java.io.File;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.tum.bgu.msm.properties.modules.TransportModelPropertiesModule.*;

/**
 * @author dziemke, nkuehnel
 */
public final class MatsimTransportModel implements TransportModel {

    private static final Logger logger = Logger.getLogger(MatsimTransportModel.class);

    private final Properties properties;
    private final Config initialMatsimConfig;

    private final MatsimData matsimData;
    private final MatsimTravelTimesAndCosts internalTravelTimes;

    private final DataContainer dataContainer;

    private MatsimScenarioAssembler scenarioAssembler;

    public MatsimTransportModel(DataContainer dataContainer, Config matsimConfig,
                                Properties properties, MatsimScenarioAssembler scenarioAssembler,
                                MatsimData matsimData) {
        this.dataContainer = Objects.requireNonNull(dataContainer);
        this.initialMatsimConfig = Objects.requireNonNull(matsimConfig, "No initial matsim config provided to SiloModel class!");
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
        Scenario assembledScenario = ScenarioUtils.createScenario(initialMatsimConfig);
        TravelTimes travelTimes = dataContainer.getTravelTimes();

        if (year == properties.main.baseYear && properties.transportModel.transportModelIdentifier == TransportModelIdentifier.MATSIM){
            //if using the SimpleCommuteModeChoiceScenarioAssembler, we need some initial travel times (this will use an unlodaded network)
            TravelTime myTravelTime = SiloMatsimUtils.getAnEmptyNetworkTravelTime();
            TravelDisutility myTravelDisutility = SiloMatsimUtils.getAnEmptyNetworkTravelDisutility();
            updateTravelTimes(myTravelTime, myTravelDisutility);
        }

        for (Household household: dataContainer.getHouseholdDataManager().getHouseholds()) {
            for (Person pp : household.getPersons().values()) {
                PopulationFactory pf = assembledScenario.getPopulation().getFactory();

                final int noHHAUtos = (int) household.getVehicles().stream().filter( vv -> vv.getType().equals( de.tum.bgu.msm.data.vehicle.VehicleType.CAR ) ).count();
                assembledScenario.getPopulation().addPerson( SiloMatsimUtils.createMatsimAlterEgo(pf, pp, noHHAUtos ) );
            }
        }

        matsimData.updateMatsimPopulation(assembledScenario.getPopulation());

        // create a dummy vehicle type
        VehicleType dummyVehType = assembledScenario.getVehicles().getFactory().createVehicleType(Id.create("defaultVehicleType", VehicleType.class));
        assembledScenario.getVehicles().addVehicleType(dummyVehType);

        for (org.matsim.api.core.v01.population.Person person : assembledScenario.getPopulation().getPersons().values()) {
            Id<Vehicle> vehicleId = Id.createVehicleId(person.getId());
            assembledScenario.getVehicles().addVehicle(assembledScenario.getVehicles().getFactory().createVehicle(vehicleId, dummyVehType));
            VehicleUtils.insertVehicleIdsIntoAttributes(person, Collections.singletonMap( TransportMode.car, vehicleId) );
        }

        // TODO remove config argument as it is duplicate (cf. above)
        assembledScenario = scenarioAssembler.assembleScenario(initialMatsimConfig, year, travelTimes);

        finalizeConfig(assembledScenario.getConfig(), year);

        final Controler controler = new Controler(assembledScenario);

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
        TravelTime travelTime = TravelTimeUtils.createTravelTimesFromEvents(scenario.getNetwork(), scenario.getConfig(), eventsFile);
        TravelDisutility travelDisutility = ControlerDefaults.createDefaultTravelDisutilityFactory(scenario).createTravelDisutility(travelTime);
        updateTravelTimes(travelTime, travelDisutility);
    }

    private void updateTravelTimes(TravelTime travelTime, TravelDisutility disutility) {
        matsimData.update(disutility, travelTime);
        internalTravelTimes.update(matsimData);
        final TravelTimes mainTravelTimes = dataContainer.getTravelTimes();

        if (mainTravelTimes != this.internalTravelTimes && mainTravelTimes instanceof SkimTravelTimes) {
            ((SkimTravelTimes) mainTravelTimes).updateSkimMatrix(internalTravelTimes.getPeakSkim(TransportMode.car), TransportMode.car);
            if ((properties.transportModel.transportModelIdentifier == TransportModelIdentifier.MATSIM)) {
                ((SkimTravelTimes) mainTravelTimes).updateSkimMatrix(internalTravelTimes.getPeakSkim(TransportMode.pt), TransportMode.pt);
            }
            ((SkimTravelTimes) mainTravelTimes).updateRegionalTravelTimes(dataContainer.getGeoData().getRegions().values(),
                    dataContainer.getGeoData().getZones().values());
    }
    }
}
