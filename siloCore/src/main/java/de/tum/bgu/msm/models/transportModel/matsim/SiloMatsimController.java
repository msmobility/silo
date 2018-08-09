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

package de.tum.bgu.msm.models.transportModel.matsim;

import java.util.HashMap;
import java.util.Map;

import org.matsim.api.core.v01.population.Population;
import org.matsim.contrib.accessibility.AccessibilityConfigGroup;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.config.groups.VspExperimentalConfigGroup.VspDefaultsCheckingLevel;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.Tuple;
import org.opengis.feature.simple.SimpleFeature;

/**
 * @author dziemke
 */
/* deliberately package */ class SiloMatsimController {
	
	public static Map<Tuple<Integer, Integer>, Float> runMatsimToCreateTravelTimes(int numberOfCalcPoints, Map<Integer,SimpleFeature> zoneFeatureMap,
			Population population, String runId, Config config, String outputDirectoryRoot) {
		
  		Map<Tuple<Integer, Integer>, Float> travelTimesMap = new HashMap<>();
		
		String outputDirectory = outputDirectoryRoot + "/" + runId + "/";
		config.controler().setRunId(runId);
		config.controler().setOutputDirectory(outputDirectory);
		config.controler().setFirstIteration(0);
		config.controler().setMobsim("qsim");
		config.controler().setWritePlansInterval(config.controler().getLastIteration());
		config.controler().setWriteEventsInterval(config.controler().getLastIteration());
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);

		config.qsim().setTrafficDynamics(TrafficDynamics.withHoles);
		config.vspExperimental().setWritingOutputEvents(true); // writes final events into toplevel directory
		
		{				
			StrategySettings strategySettings = new StrategySettings();
			strategySettings.setStrategyName("ChangeExpBeta");
			strategySettings.setWeight(0.8);
			config.strategy().addStrategySettings(strategySettings);
		}{
			StrategySettings strategySettings = new StrategySettings();
			strategySettings.setStrategyName("ReRoute");
			strategySettings.setWeight(0.2);
			config.strategy().addStrategySettings(strategySettings);
		}
		
		config.strategy().setFractionOfIterationsToDisableInnovation(0.8);
		config.strategy().setMaxAgentPlanMemorySize(4);
		
		ActivityParams homeActivity = new ActivityParams("home");
		homeActivity.setTypicalDuration(12*60*60);
		config.planCalcScore().addActivityParams(homeActivity);
		
		ActivityParams workActivity = new ActivityParams("work");
		workActivity.setTypicalDuration(8*60*60);
		config.planCalcScore().addActivityParams(workActivity);
		
		config.qsim().setNumberOfThreads(1);
		config.global().setNumberOfThreads(1);
		config.parallelEventHandling().setNumberOfThreads(1);
		config.qsim().setUsingThreadpool(false);
		
		AccessibilityConfigGroup accessibilityConfigGroup = ConfigUtils.addOrGetModule(config, AccessibilityConfigGroup.GROUP_NAME, AccessibilityConfigGroup.class);
		double timeOfDay = accessibilityConfigGroup.getTimeOfDay();
		
		config.vspExperimental().setVspDefaultsCheckingLevel(VspDefaultsCheckingLevel.warn);

		MutableScenario scenario = (MutableScenario) ScenarioUtils.loadScenario(config);
		scenario.setPopulation(population);
		
		// Initialize controller
		final Controler controler = new Controler(scenario);

		// Add controller listener
//		Zone2ZoneTravelTimeListener zone2zoneTravelTimeListener = new Zone2ZoneTravelTimeListener(
//				controler, scenario.getNetwork(), config.controler().getLastIteration(),
//				zoneFeatureMap, timeOfDay, numberOfCalcPoints, travelTimesMap);
//		controler.addControlerListener(zone2zoneTravelTimeListener);
		// feedback will not work without the above. kai, apr'16
		
		// Run controller
		controler.run();
		
		// Return collected travel times
		return travelTimesMap;
	}
}
