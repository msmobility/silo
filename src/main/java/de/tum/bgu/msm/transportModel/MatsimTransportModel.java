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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import common.Logger;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.Accessibility;
import de.tum.bgu.msm.data.HouseholdDataManager;

/**
 * @author dziemke
 */
public class MatsimTransportModel implements TransportModelI {
	private static final Logger logger = Logger.getLogger( MatsimTransportModel.class );

	private static final String PROPERTIES_ZONES_SHAPEFILE				= "matsim.zones.shapefile";
	private static final String PROPERTIES_ZONES_CRS 						= "matsim.zones.crs";
//	private static final String PROPERTIES_MATSIM_WRITE_POPULATION 		= "matsim.write.population";
//	private static final String PROPERTIES_MATSIM_NUMBER_OF_CALC_POINTS 	= "matsim.number.of.calc.points";
//	private static final String PROPERTIES_MATSIM_POPULATION_SCALING_FACOTR = "matsim.population.scaling.factor";
//	private static final String PROPERTIES_MATSIM_WORKER_SCALING_FACTOR 	= "matsim.worker.scaling.factor";
//	private static final String PROPERTIES_SCENARIO_NAME                     = "scenario.name";
	
	private HouseholdDataManager householdData;
	private Accessibility acc;
	private ResourceBundle rb;
	private Config matsimConfig;
	

	public MatsimTransportModel(HouseholdDataManager householdData, Accessibility acc, ResourceBundle rb, Config matsimConfig) {
		this.householdData = householdData;
		this.acc = acc;
		this.rb = rb;
		this.matsimConfig = matsimConfig;
	}
	
	@Override
	public void runTransportModel(int year) {
			logger.info("Running MATSim transport model for year " + year + ".");

			String scenarioName = rb.getString(SiloUtil.PROPERTIES_SCENARIO_NAME);

			String crs = rb.getString(PROPERTIES_ZONES_CRS);
			String zoneShapeFile = SiloUtil.baseDirectory + "/" + rb.getString(PROPERTIES_ZONES_SHAPEFILE);
			
//			int numberOfCalcPoints = ResourceUtil.getIntegerProperty(rb, PROPERTIES_MATSIM_NUMBER_OF_CALC_POINTS);
			int numberOfCalcPoints = 1 ; // what is this?
			
//			boolean writePopulation = ResourceUtil.getBooleanProperty(rb, PROPERTIES_MATSIM_WRITE_POPULATION);
			boolean writePopulation = false ;

//			double populationScalingFactor = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MATSIM_POPULATION_SCALING_FACOTR);
			double populationScalingFactor = 0.01 ;
			
			// people working at non-peak times (only peak traffic is simulated), and people going by
			// a mode other than car in case a car is still available to them
//			double workerScalingFactor = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MATSIM_WORKER_SCALING_FACTOR);
			double workerScalingFactor = 0.66 ;

			double flowCapacityFactor = populationScalingFactor;
			matsimConfig.qsim().setFlowCapFactor(flowCapacityFactor);
			// According to "NicolaiNagel2013HighResolutionAccessibility (citing Rieser on p.9): Storage_Capacitiy_Factor = Sampling_Rate / ((Sampling_Rate) ^ (1/4))
			double storageCapacityFactor = Math.round((flowCapacityFactor / (Math.pow(flowCapacityFactor, 0.25)) * 100)) / 100.;
			matsimConfig.qsim().setStorageCapFactor(storageCapacityFactor);
			
			matsimConfig.global().setCoordinateSystem(crs);
			
			String matsimRunId = scenarioName + "_" + year;
			Collection<SimpleFeature> zoneFeatures = ShapeFileReader.getAllFeatures(zoneShapeFile);

			Map<Integer,SimpleFeature> zoneFeatureMap = new HashMap<>();
			for (SimpleFeature feature: zoneFeatures) {
				int zoneId = Integer.parseInt(feature.getAttribute("SMZRMZ").toString());
				zoneFeatureMap.put(zoneId,feature);
			}

			Population population = MatsimPopulationCreator.createMatsimPopulation(householdData, year, zoneFeatureMap, crs,
					writePopulation, populationScalingFactor * workerScalingFactor);

			String outputDirectoryRoot = matsimConfig.controler().getOutputDirectory();

			// Get travel Times from MATSim
			Map<Tuple<Integer, Integer>, Float> travelTimesMap = SiloMatsimController.runMatsimToCreateTravelTimes(numberOfCalcPoints,
					zoneFeatureMap, population, matsimRunId, matsimConfig, outputDirectoryRoot);

			// Update skims in silo from matsim output:
			acc.readSkimBasedOnMatsim(year, travelTimesMap);

			// Update accessibilities in silo from matsim output:
			acc.calculateAccessibilities(year);
			// TODO calculate accessibility directly from MATSim instead of from skims. Current version is computationally very inefficient
	}

	@Override
	public void writeOutSocioEconomicDataForMstm(int year) {
		// not doing anything. 
	}
}