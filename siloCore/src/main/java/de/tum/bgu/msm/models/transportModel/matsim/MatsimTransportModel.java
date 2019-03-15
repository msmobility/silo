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
import de.tum.bgu.msm.data.TravelTimesWrapper;
import de.tum.bgu.msm.models.transportModel.TransportModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.api.internal.MatsimWriter;
import org.matsim.core.config.Config;
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
import org.matsim.utils.leastcostpathtree.LeastCostPathTree;

import java.io.File;
import java.util.Objects;

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


	public MatsimTransportModel(DataContainer dataContainer, Config matsimConfig, Properties properties) {
		this.dataContainer = Objects.requireNonNull(dataContainer);
		this.initialMatsimConfig = Objects.requireNonNull(matsimConfig );
		this.travelTimes = (MatsimTravelTimes)
				Objects.requireNonNull(((TravelTimesWrapper)dataContainer.getTravelTimes()).getDelegate());
		this.properties = properties;

	}

	@Override
	public void setup() {
        network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(initialMatsimConfig.network().getInputFileURL(initialMatsimConfig.getContext()).getFile());
        travelTimes.initialize(dataContainer.getGeoData().getZones().values(), network);

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

		// In the current implementation, MATSim is used to reflect the functionality that was previously
		// covered by MSTM. As such, based on the MATSim transport simulation, a travel time matrix (skim)
		// is computed. To do so, random coordinates in each zone are taken to measure the zone-to-zone
		// travel times. <code>numberOfCalcPoints</code> states how many such points in each zone are used;
		// in case multiple points are used; the average of all travel times of a given relation is used.
//		int numberOfCalcPoints = 1;
		boolean writePopulation = false;

		double populationScalingFactor = properties.transportModel.matsimScaleFactor;
		

		double workerScalingFactor = properties.transportModel.matsimWorkersShare;

		
		String matsimRunId = scenarioName + "_" + year;
		
		Config config = SiloMatsimUtils.createMatsimConfig(initialMatsimConfig, matsimRunId, populationScalingFactor, workerScalingFactor);
//		Population population = SiloMatsimUtils.createMatsimPopulation(config, dataContainer, zoneFeatureMap, populationScalingFactor * workerScalingFactor);
		Population population = SiloMatsimUtils.createMatsimPopulation(config, dataContainer, populationScalingFactor * workerScalingFactor);
		
		if (writePopulation) {
    		new File("./test/scenarios/annapolis_reduced/matsim_output/").mkdirs();
    		MatsimWriter populationWriter = new PopulationWriter(population);
    		populationWriter.write("./test/scenarios/annapolis_reduced/matsim_output/population_" + year + ".xml");
    	}


//		config.plansCalcRoute().setInsertingAccessEgressWalk(true);
		MutableScenario scenario = (MutableScenario) ScenarioUtils.loadScenario(config);
		scenario.setPopulation(population);
		
		final Controler controler = new Controler(scenario);
		
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
//        initialMatsimConfig.plansCalcRoute().setInsertingAccessEgressWalk(true);
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
		LeastCostPathTree leastCoastPathTree = new LeastCostPathTree(travelTime, disutility);
//
////		travelTimes.update(leastCoastPathTree, zoneFeatureMap, scenario.getNetwork(), controler.getTripRouterProvider().get() );
//		// for now, pt inforamtion from MATSim not required as there are no changes in PT supply (schedule) expected currently;
//		// potentially revise this later; nk/dz, nov'17
//		//TODO: Optimize pt travel time query
////		MatsimPtTravelTimes matsimPtTravelTimes = new MatsimPtTravelTimes(controler.getTripRouterProvider().get(), zoneFeatureMap, scenario.getNetwork());
////		acc.addTravelTimeForMode(TransportMode.pt, matsimTravelTimes); // use car times for now also, as pt travel times are too slow to compute, Nico Oct 17
//
//		if (config.transit().isUseTransit() && Properties.get().main.implementation == Implementation.MUNICH) {
//			MatsimPTDistances matsimPTDistances = new MatsimPTDistances(config, scenario, (GeoDataMuc) dataContainer.getGeoData());
//		}
		travelTimes.update(tripRouter, leastCoastPathTree);
//		tripRouter = controler.getTripRouterProvider().get();
	}




	// Other idea; provide TripRouter more directly; requires more fundamental change, however
//	public final TripRouter getTripRouter() {
//		if(tripRouter == null) {
//			throw new RuntimeException("MATSim Transport Model needs to run at least once before trips can be queried!");
//		}
//		return tripRouter;
//	}
}