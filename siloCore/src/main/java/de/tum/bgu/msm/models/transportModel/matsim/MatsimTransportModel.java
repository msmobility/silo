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
package de.tum.bgu.msm.models.transportModel.matsim;


import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.accessibility.MatsimAccessibility;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.contrib.accessibility.AccessibilityAttributes;
import org.matsim.contrib.accessibility.AccessibilityConfigGroup;
import org.matsim.contrib.accessibility.AccessibilityModule;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.api.internal.MatsimWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.FacilitiesConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.TripRouter;
import org.matsim.core.router.TripRouterFactoryBuilderWithDefaults;
import org.matsim.core.router.costcalculators.OnlyTimeDependentTravelDisutilityFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.trafficmonitoring.TravelTimeCalculator;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.*;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author dziemke
 */
public final class MatsimTransportModel implements TransportModel {
	private static final Logger LOG = Logger.getLogger( MatsimTransportModel.class );
	
	private final Config initialMatsimConfig;
	private final MatsimTravelTimes travelTimes;
	private Properties properties;
	private final DataContainer dataContainer;
	private Network network;
	
	private ActivityFacilities zoneRepresentativeCoords;
	private MatsimAccessibility accessibility;


	public MatsimTransportModel(DataContainer dataContainer, Config matsimConfig,
			Properties properties, MatsimAccessibility accessibility) {
		this.dataContainer = Objects.requireNonNull(dataContainer);
		this.initialMatsimConfig = Objects.requireNonNull(matsimConfig );
		this.travelTimes = (MatsimTravelTimes)
				Objects.requireNonNull((dataContainer.getTravelTimes()));
		this.properties = properties;
		this.accessibility = accessibility;
	}

	@Override
	public void setup() {
        network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(initialMatsimConfig.network().getInputFileURL(initialMatsimConfig.getContext()).getFile());
        travelTimes.initialize(dataContainer.getGeoData().getZones(), network);

        LOG.warn("Finding coordinates that represent a given zone.");
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

        if(properties.transportModel.matsimInitialEventsFile == null) {
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
	    if(properties.transportModel.transportModelYears.contains(year+1)) {
            runTransportModel(year+1);
        }

    }

    @Override
    public void endSimulation() {

    }

    public void runTransportModel(int year) {
		LOG.warn("Running MATSim transport model for year " + year + ".");

		String scenarioName = properties.main.scenarioName;

		boolean writePopulation = false;
		double populationScalingFactor = properties.transportModel.matsimScaleFactor;
		String matsimRunId = scenarioName + "_" + year;
		
		Config config = SiloMatsimUtils.createMatsimConfig(initialMatsimConfig, matsimRunId, populationScalingFactor);
		Population population = SiloMatsimUtils.createMatsimPopulation(config, dataContainer, populationScalingFactor);
		
		if (writePopulation) {
    		new File("./test/scenarios/annapolis_reduced/matsim_output/").mkdirs();
    		MatsimWriter populationWriter = new PopulationWriter(population);
    		populationWriter.write("./test/scenarios/annapolis_reduced/matsim_output/population_" + year + ".xml");
    	}
		
		// Get travel Times from MATSim
		LOG.warn("Using MATSim to compute travel times from zone to zone.");

		MutableScenario scenario = (MutableScenario) ScenarioUtils.loadScenario(config);
		scenario.setPopulation(population);

		ConfigUtils.setVspDefaults(config);
		final Controler controler = new Controler(scenario);

		if(accessibility != null) {

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
			LOG.warn(i + " facilities added as opportunities.");

			SiloMatsimUtils.determineExtentOfFacilities(zoneRepresentativeCoords);

			scenario.getConfig().facilities().setFacilitiesSource(FacilitiesConfigGroup.FacilitiesSource.setInScenario);
			// End opportunities

			// Accessibility settings
			AccessibilityConfigGroup acg = ConfigUtils.addOrGetModule(config, AccessibilityConfigGroup.class);
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
			LOG.warn("Properties.get().accessibility.alphaAuto = " + Properties.get().accessibility.alphaAuto);
			acg.setAccessibilityMeasureType(AccessibilityConfigGroup.AccessibilityMeasureType.rawSum);
			// End accessibility settings
			// Accessibility module

			AccessibilityModule module = new AccessibilityModule();
			module.addFacilityDataExchangeListener(accessibility);
			controler.addOverridingModule(module);
			// End accessibility module
		}

		controler.run();
		LOG.warn("Running MATSim transport model for year " + year + " finished.");

		// Get travel Times from MATSim
		LOG.warn("Using MATSim to compute travel times from zone to zone.");
		TravelTime travelTime = controler.getLinkTravelTimes();
		TravelDisutility travelDisutility = controler.getTravelDisutilityFactory().createTravelDisutility(travelTime);
		updateTravelTimes(controler.getTripRouterProvider().get(), travelTime, travelDisutility);
	}

    /**
     *
     * @param eventsFile
     */
	public void replayFromEvents(String eventsFile) {
        MutableScenario scenario = (MutableScenario) ScenarioUtils.loadScenario(initialMatsimConfig);
	    TravelTimeCalculator ttCalculator = TravelTimeCalculator.create(scenario.getNetwork(), scenario.getConfig().travelTimeCalculator());
        EventsManager events = EventsUtils.createEventsManager();
        events.addHandler(ttCalculator);
        (new MatsimEventsReader(events)).readFile(eventsFile);
        TripRouter tripRouter = TripRouterFactoryBuilderWithDefaults.createDefaultTripRouterFactoryImpl(scenario).get();
        TravelTime travelTime = ttCalculator.getLinkTravelTimes();
        TravelDisutility travelDisutility = new OnlyTimeDependentTravelDisutilityFactory().createTravelDisutility(travelTime);
        updateTravelTimes(tripRouter, travelTime, travelDisutility);
	}

	private void updateTravelTimes(TripRouter tripRouter, TravelTime travelTime, TravelDisutility disutility) {
//		LeastCostPathTree leastCoastPathTree = new LeastCostPathTree(travelTime, disutility);
		/*
		 * Comments:
		 * for now, pt inforamtion from MATSim not required as there are no changes in PT supply (schedule) expected currently;
		 * 
		 * Other idea: provide TripRouter more directly; requires more fundamental change, however
		 * public final TripRouter getTripRouter() {
		 * if(tripRouter == null) {
		 * throw new RuntimeException("MATSim Transport Model needs to run at least once before trips can be queried!");
		 * }
		 * return tripRouter;
		 */
//		travelTimes.update(leastCoastPathTree, zoneFeatureMap, scenario.getNetwork(), controler.getTripRouterProvider().get() );
//		travelTimes.update(tripRouter, leastCoastPathTree);
		travelTimes.update(travelTime, disutility);
//		tripRouter = controler.getTripRouterProvider().get();
	}
}