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
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.accessibility.MatsimAccessibility;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.modules.TransportModelPropertiesModule;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.contrib.accessibility.AccessibilityAttributes;
import org.matsim.contrib.accessibility.AccessibilityConfigGroup;
import org.matsim.contrib.accessibility.AccessibilityModule;
import org.matsim.contrib.dvrp.trafficmonitoring.TravelTimeUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.FacilitiesConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.ControlerDefaults;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.FastAStarLandmarksFactory;
import org.matsim.core.router.TripRouterFactoryBuilderWithDefaults;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.*;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.opengis.feature.simple.SimpleFeature;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author dziemke, nkuehnel
 */
public final class MatsimTransportModel implements TransportModel {

    private static final Logger logger = Logger.getLogger(MatsimTransportModel.class);

    private final Properties properties;
    private final Config initialMatsimConfig;

    private final MatsimData matsimData;
    private final MatsimTravelTimes internalTravelTimes;
    private Scenario scenario;

    private final DataContainer dataContainer;

    private ActivityFacilities zoneRepresentativeCoords;
    private MatsimAccessibility accessibility;

    private MatsimScenarioAssembler scenarioAssembler;

    public MatsimTransportModel(DataContainer dataContainer, Config matsimConfig,
                                Properties properties, MatsimAccessibility accessibility,
                                ZoneConnectorManager.ZoneConnectorMethod method, MatsimScenarioAssembler scenarioAssembler) {
        this.dataContainer = Objects.requireNonNull(dataContainer);
        this.initialMatsimConfig = Objects.requireNonNull(matsimConfig,
                "No initial matsim config provided to SiloModel class!");

        final TravelTimes travelTimes = dataContainer.getTravelTimes();
        if (travelTimes instanceof MatsimTravelTimes) {
            this.internalTravelTimes = (MatsimTravelTimes) travelTimes;
        } else {
            this.internalTravelTimes = new MatsimTravelTimes(matsimConfig);
        }
        this.matsimData = new MatsimData(matsimConfig, properties, method, dataContainer);
        this.scenarioAssembler = scenarioAssembler;

        this.properties = properties;
        this.accessibility = accessibility;
    }

    @Override
    public void setup() {
        scenario = ScenarioUtils.loadScenario(initialMatsimConfig);
        Network network = scenario.getNetwork();
        internalTravelTimes.initialize(dataContainer.getGeoData(), matsimData);

        logger.warn("Finding coordinates that represent a given zone.");
        zoneRepresentativeCoords = FacilitiesUtils.createActivityFacilities();
        ActivityFacilitiesFactory aff = new ActivityFacilitiesFactoryImpl();
        Map<Integer, Zone> zoneMap = dataContainer.getGeoData().getZones();
        for (int zoneId : zoneMap.keySet()) {
            Geometry geometry = (Geometry) zoneMap.get(zoneId).getZoneFeature().getDefaultGeometry();
            Coord centroid = CoordUtils.createCoord(geometry.getCentroid().getX(), geometry.getCentroid().getY());
            Node nearestNode = NetworkUtils.getNearestNode(network, centroid); // TODO choose road of certain category
            Coord coord = CoordUtils.createCoord(nearestNode.getCoord().getX(), nearestNode.getCoord().getY());
            ActivityFacility activityFacility = aff.createActivityFacility(Id.create(zoneId, ActivityFacility.class), coord);
            zoneRepresentativeCoords.addActivityFacility(activityFacility);
        }

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

        ConfigUtils.setVspDefaults(assembledScenario.getConfig());
        finalizeConfig(assembledScenario.getConfig(), year);

        final Controler controler = new Controler(assembledScenario);

        if (accessibility != null) {
            setupAccessibility(assembledScenario, controler);
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
        config.transit().setUsingTransitInMobsim(false);

        config.vspExperimental().setWritingOutputEvents(true);
    }

    private void setupAccessibility(Scenario scenario, Controler controler) {
        // Opportunities
        Map<Integer, Integer> populationMap = HouseholdUtil.getPopulationByZoneAsMap(dataContainer);
        Map<Id<ActivityFacility>, Integer> zonePopulationMap = new TreeMap<>();
        for (int zoneId : populationMap.keySet()) {
            zonePopulationMap.put(Id.create(zoneId, ActivityFacility.class), populationMap.get(zoneId));
        }
        final ActivityFacilities opportunities = scenario.getActivityFacilities();
        int i = 0;
        for (ActivityFacility activityFacility : zoneRepresentativeCoords.getFacilities().values()) {
            activityFacility.getAttributes().putAttribute(AccessibilityAttributes.WEIGHT, zonePopulationMap.get(activityFacility.getId()));
            opportunities.addActivityFacility(activityFacility);
            i++;
        }
        logger.warn(i + " facilities added as opportunities.");

        SiloMatsimUtils.determineExtentOfFacilities(zoneRepresentativeCoords);

        scenario.getConfig().facilities().setFacilitiesSource(FacilitiesConfigGroup.FacilitiesSource.setInScenario);
        // End opportunities

        // Accessibility settings
        AccessibilityConfigGroup acg = ConfigUtils.addOrGetModule(scenario.getConfig(), AccessibilityConfigGroup.class);
        acg.setMeasuringPointsFacilities(zoneRepresentativeCoords);
        //
        Map<Id<ActivityFacility>, Geometry> measurePointGeometryMap = new TreeMap<>();
        Map<Integer, SimpleFeature> zoneFeatureMap = new HashMap<>();
        for (Zone zone : dataContainer.getGeoData().getZones().values()) {
            zoneFeatureMap.put(zone.getId(), zone.getZoneFeature());
        }

        for (Integer zoneId : zoneFeatureMap.keySet()) {
            SimpleFeature feature = zoneFeatureMap.get(zoneId);
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            measurePointGeometryMap.put(Id.create(zoneId, ActivityFacility.class), geometry);
        }
        acg.setMeasurePointGeometryProvision(AccessibilityConfigGroup.MeasurePointGeometryProvision.fromShapeFile);
        acg.setMeasurePointGeometryMap(measurePointGeometryMap);

        acg.setTileSize_m(1000); // TODO This is only a dummy value here
        //
        acg.setAreaOfAccessibilityComputation(AccessibilityConfigGroup.AreaOfAccesssibilityComputation.fromFacilitiesObject);
        acg.setUseOpportunityWeights(true);
        acg.setWeightExponent(Properties.get().accessibility.alphaAuto); // TODO Need differentiation for different modes
        logger.warn("Properties.get().accessibility.alphaAuto = " + Properties.get().accessibility.alphaAuto);
        acg.setAccessibilityMeasureType(AccessibilityConfigGroup.AccessibilityMeasureType.rawSum);
        // End accessibility settings
        // Accessibility module

        AccessibilityModule module = new AccessibilityModule();
        module.addFacilityDataExchangeListener(accessibility);
        controler.addOverridingModule(module);
        // End accessibility module
    }

    /**
     * @param eventsFile
     */
    private void replayFromEvents(String eventsFile) {
        Scenario scenario = ScenarioUtils.loadScenario(initialMatsimConfig);
        TravelTime travelTime = TravelTimeUtils.createTravelTimesFromEvents(scenario, eventsFile);
        TravelDisutility travelDisutility = ControlerDefaults.createDefaultTravelDisutilityFactory(scenario).createTravelDisutility(travelTime);
        TripRouterFactoryBuilderWithDefaults builder = new TripRouterFactoryBuilderWithDefaults();
        builder.setLeastCostPathCalculatorFactory(new FastAStarLandmarksFactory(properties.main.numberOfThreads));
        builder.setTravelTime(travelTime);
        builder.setTravelDisutility(travelDisutility);
        updateTravelTimes(travelTime, travelDisutility);
    }


    private void updateTravelTimes(TravelTime travelTime, TravelDisutility disutility) {
        Network network = scenario.getNetwork();
        TransitSchedule schedule = null;
        if (scenario.getConfig().transit().isUseTransit()) {
            schedule = scenario.getTransitSchedule();
        }
        matsimData.update(network, schedule, disutility, travelTime);
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
