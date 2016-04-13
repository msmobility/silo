/* *********************************************************************** *
 * project: org.matsim.*
 * CadytsController.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
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

package edu.umd.ncsg.transportModel;

import java.util.Map;

import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.Tuple;
import org.opengis.feature.simple.SimpleFeature;

/**
 * @author dziemke
 */
public class SiloMatsimController {
	
	public static Map<Tuple<Integer, Integer>, Float> runMatsimToCreateTravelTimes(Map<Tuple<Integer, Integer>, Float> travelTimesMap,
			int timeOfDay, int numberOfCalcPoints, Map<Integer,SimpleFeature> zoneFeatureMap, //CoordinateTransformation ct, 
			String inputNetworkFile,
			Population population, int year, 
			String crs, int numberOfIterations, String siloRunId, String outputDirectoryRoot,
			double flowCapacityFactor, double storageCapacityFactor) {
//			String populationFile, int year, String crs, int numberOfIterations) {
		final Config config = ConfigUtils.createConfig();

		// Global
		config.global().setCoordinateSystem(crs);
		
		// Network
		config.network().setInputFile(inputNetworkFile);
		
		// Plans
//		config.plans().setInputFile(inputPlansFile);
		
		// Simulation
//		config.qsim().setFlowCapFactor(0.01);
		config.qsim().setFlowCapFactor(flowCapacityFactor);
//		config.qsim().setStorageCapFactor(0.018);
		config.qsim().setStorageCapFactor(storageCapacityFactor);
		config.qsim().setRemoveStuckVehicles(false);
		
		config.qsim().setTrafficDynamics( TrafficDynamics.withHoles ); // this normally works better. kai, feb'16

		// Controller
//		String siloRunId = "run_09";
		String runId = siloRunId + "_" + year;
		String outputDirectory = outputDirectoryRoot + "/" + runId + "/";
		config.controler().setRunId(runId);
		config.controler().setOutputDirectory(outputDirectory);
		config.controler().setFirstIteration(0);
		config.controler().setLastIteration(numberOfIterations);
		config.controler().setMobsim("qsim");
		config.controler().setWritePlansInterval(numberOfIterations);
		config.controler().setWriteEventsInterval(numberOfIterations);
		
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);

		// QSim and other
		config.qsim().setTrafficDynamics(TrafficDynamics.withHoles);
		config.vspExperimental().setWritingOutputEvents(true); // writes final events into toplevel directory
				
		// Strategy
		StrategySettings strategySettings1 = new StrategySettings();
		strategySettings1.setStrategyName("ChangeExpBeta");
		strategySettings1.setWeight(0.8);
		config.strategy().addStrategySettings(strategySettings1);
		
		StrategySettings strategySettings2 = new StrategySettings();
		strategySettings2.setStrategyName("ReRoute");
		strategySettings2.setWeight(0.2);
		strategySettings2.setDisableAfter((int) (numberOfIterations * 0.7));
		config.strategy().addStrategySettings(strategySettings2);
		
		config.strategy().setMaxAgentPlanMemorySize(4);
		
		// Plan Scoring (planCalcScore)
		ActivityParams homeActivity = new ActivityParams("home");
		homeActivity.setTypicalDuration(12*60*60);
		config.planCalcScore().addActivityParams(homeActivity);
		
		ActivityParams workActivity = new ActivityParams("work");
		workActivity.setTypicalDuration(8*60*60);
		config.planCalcScore().addActivityParams(workActivity);
		
		// Scenario
		MutableScenario scenario = (MutableScenario) ScenarioUtils.loadScenario(config);
//		PopulationReader populationReader = new PopulationReaderMatsimV5(scenario);
//		populationReader.readFile(populationFile);
		scenario.setPopulation(population);
		
		// Initialize controller
		final Controler controler = new Controler(scenario);

		// Add controller listener
//		Zone2ZoneTravelTimeListener zone2zoneTravelTimeListener = new Zone2ZoneTravelTimeListener(
//				controler, scenario.getNetwork(), config.controler().getLastIteration(),
//				zoneFeatureMap, timeOfDay, numberOfCalcPoints, //ct, 
//				travelTimesMap);
//		controler.addControlerListener(zone2zoneTravelTimeListener);
		
		// Run controller
		controler.run();
		
		// Return collected travel times
		return travelTimesMap;
	}
}