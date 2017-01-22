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
package de.tum.bgu.msm.transportModel;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.api.internal.MatsimWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.scenario.ScenarioUtils;
import org.opengis.feature.simple.SimpleFeature;

import de.tum.bgu.msm.SiloMatsimTest;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.Job;
import de.tum.bgu.msm.data.Person;

/**
 * @author dziemke
 */
public class MatsimPopulationCreator {
	private static final Logger LOG = Logger.getLogger(SiloMatsimTest.class);
	
	public static Population createMatsimPopulation(HouseholdDataManager householdDataManager, int year,
			Map<Integer,SimpleFeature> zoneFeatureMap, boolean writePopulation, double scalingFactor) {
		LOG.info("Starting creation of population.");
    	Collection<Person> siloPersons = householdDataManager.getPersons();
    	
    	Config matsimConfig = ConfigUtils.createConfig();
    	Scenario matsimScenario = ScenarioUtils.createScenario(matsimConfig);
    	Network matsimNetwork = matsimScenario.getNetwork();
    	Population matsimPopulation = matsimScenario.getPopulation();   
    	PopulationFactory matsimPopulationFactory = matsimPopulation.getFactory();
    	
    	Random random = MatsimRandom.getLocalInstance(); // Make sure that stream of random variables is reproducible. kai, apr'16
    	
    	for (Person siloPerson : siloPersons) {
    		if (random.nextDouble() > scalingFactor) {
    			// e.g. if scalingFactor = 0.01, there will be a 1% chance that the loop is not
    			// continued in the next step, i.e. that the person is added to the population
    			continue;
    		}

    		if (siloPerson.getOccupation() != 1) { // i.e. person does not work
    			continue;
    		}

    		int siloWorkplaceId = siloPerson.getWorkplace();
    		if (siloWorkplaceId == -2) { // i.e. person has workplace outside study area
    			continue;
    		}

    		int householdId = siloPerson.getHhId();
    		Household household = Household.getHouseholdFromId(householdId);
    		int numberOfWorkers = household.getNumberOfWorkers();
    		int numberOfAutos = household.getAutos();
    		if (numberOfWorkers == 0) {
    			throw new RuntimeException("If there are no workers in the household, the loop must already"
    					+ " have been continued by finding that the given person is not employed!");
    		}
    		if ((double) numberOfAutos/numberOfWorkers < 1.) {
    			if (random.nextDouble() > (double) numberOfAutos/numberOfWorkers) {
    				continue;
    			}
    		}

    		int siloPersonId = siloPerson.getId();
    		int siloHomeTazId = siloPerson.getHomeTaz();
    		Job job = Job.getJobFromId(siloWorkplaceId);
    		int workZoneId = job.getZone();

    		// Note: Do not confuse the SILO Person class with the MATSim Person class here
    		org.matsim.api.core.v01.population.Person matsimPerson = 
    				matsimPopulationFactory.createPerson(Id.create(siloPersonId, org.matsim.api.core.v01.population.Person.class));
    		matsimPopulation.addPerson(matsimPerson);

    		Plan matsimPlan = matsimPopulationFactory.createPlan();
    		matsimPerson.addPlan(matsimPlan);

    		SimpleFeature homeFeature = zoneFeatureMap.get(siloHomeTazId);
    		Coord homeCoordinates = SiloMatsimUtils.getRandomCoordinateInGeometry(homeFeature);
    		Activity activity1 = matsimPopulationFactory.createActivityFromCoord("home", homeCoordinates);
    		activity1.setEndTime(6 * 3600 + 3 * random.nextDouble() * 3600); // TODO Potentially change later
    		matsimPlan.addActivity(activity1);
    		matsimPlan.addLeg(matsimPopulationFactory.createLeg(TransportMode.car)); // TODO Potentially change later

    		SimpleFeature workFeature = zoneFeatureMap.get(workZoneId);
    		Coord workCoordinates = SiloMatsimUtils.getRandomCoordinateInGeometry(workFeature);
    		Activity activity2 = matsimPopulationFactory.createActivityFromCoord("work", workCoordinates);
    		activity2.setEndTime(15 * 3600 + 3 * random.nextDouble() * 3600); // TODO Potentially change later
    		matsimPlan.addActivity(activity2);
    		matsimPlan.addLeg(matsimPopulationFactory.createLeg(TransportMode.car)); // TODO Potentially change later

    		Activity activity3 = matsimPopulationFactory.createActivityFromCoord("home", homeCoordinates);
    		matsimPlan.addActivity(activity3);
    	}
    	
    	
    	if (writePopulation == true) {
    		new File("./test/scenarios/annapolis_reduced/matsim_output/").mkdir();
    		MatsimWriter popWriter = new PopulationWriter(matsimPopulation, matsimNetwork);
    		popWriter.write("./test/scenarios/annapolis_reduced/matsim_output/population_" + year + ".xml");
    	}
    	
    	return matsimPopulation;
    }
}