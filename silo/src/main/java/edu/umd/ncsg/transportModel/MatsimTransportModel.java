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
package edu.umd.ncsg.transportModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.jfree.util.Log;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import com.pb.common.util.ResourceUtil;

import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.data.Accessibility;
import edu.umd.ncsg.data.HouseholdDataManager;

/**
 * @author dziemke
 */
public class MatsimTransportModel implements TransportModelI {
	private HouseholdDataManager householdData;
	private Accessibility acc;
	private ResourceBundle rb;
	private Config matsimConfig;
	private String runId;
	
//  protected static final String PROPERTIES_MATSIM_NETWORK_FILE      		= "matsim.network";
  protected static final String PROPERTIES_ZONES_SHAPEFILE				= "zones.shapefile";
////  protected static final String ZONES_SHAPEFILE							= "../../../maryland/siloMatsim/shp/SMZ_RMZ_02152011inMSTM_EPSG26918.shp";
////  protected static final String PROPERTIES_ZONES_SHAPEFILE			= "additional_input/shp/SMZ_RMZ_02152011inMSTM_EPSG26918.shp";
  protected static final String PROPERTIES_ZONES_CRS 						= "zones.crs";
//  protected static final String PROPERTIES_MATSIM_RUN_ID 					= "matsim.run.id";
//  protected static final String PROPERTIES_MATSIM_WRITE_POPULATION 		= "matsim.write.population";
//  protected static final String PROPERTIES_MATSIM_TIME_OF_DAY_FOR_IMPEDANCE_MATRIX = "matsim.time.of.day.for.impedance.matrix";
//  protected static final String PROPERTIES_MATSIM_NUMBER_OF_CALC_POINTS 	= "matsim.number.of.calc.points";
//  protected static final String PROPERTIES_MATSIM_NUMBER_OF_ITERATIONS 	= "matsim.number.of.iterations";
  protected static final String PROPERTIES_MATSIM_POPULATION_SCALING_FACOTR = "matsim.population.scaling.factor";
  protected static final String PROPERTIES_MATSIM_WORKER_SCALING_FACTOR 	= "matsim.worker.scaling.factor";
//  protected static final String PROPERTIES_MATSIM_OUTPUT_DIRETORY_ROOT	= "matsim.output.directory.root";
//  protected static final String PROPERTIES_MATSIM_FLOW_CAPACITIY_FACTOR	= "matsim.flow.capacity.factor";
//  protected static final String PROPERTIES_MATSIM_STORAGE_CAPACITIY_FACTOR	= "matsim.storage.capacity.factor";

  protected static final String PROPERTIES_SCENARIO_NAME                     = "scenario.name";
  
//  private Config matsimConfig = null;
	
	
	public MatsimTransportModel(HouseholdDataManager householdData, Accessibility acc, ResourceBundle rb, Config matsimConfig) {
		this.householdData = householdData;
		this.acc = acc;
		this.rb = rb;
		this.matsimConfig = matsimConfig;
	}
	
	@Override
	public void runMstm(int year) {
			Log.info("Running MATSim transport model for year " + year + ".");

			String zoneShapeFile = SiloUtil.baseDirectory + "/" + rb.getString(PROPERTIES_ZONES_SHAPEFILE);
			String crs = rb.getString(PROPERTIES_ZONES_CRS);
			
			String scenarioName = rb.getString(PROPERTIES_SCENARIO_NAME);

			boolean writePopulation = false; // TODO remove hardcoded
			int timeOfDayForImpedanceMatrix = 8*60*60; // TODO remove hardcoded
			int numberOfCalcPoints = 1; // usual value is 3 // TODO remove hardcoded

			double populationScalingFactor = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MATSIM_POPULATION_SCALING_FACOTR);
			// people working at non-peak times (only peak traffic is simulated), and people going by
			// a mode other than car in case a car is still available to them
			double workerScalingFactor = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MATSIM_WORKER_SCALING_FACTOR);

			// set it equal to population scaling factor ???
			//      		matsimConfig.qsim().setFlowCapFactor(populationScalingFactor);

			/* According to "Nicolai and Nagel 2013 High Resolution Accessibility ... citing Rieser ... p.9
			 * Storage_Capacitiy_Factor = Sampling_Rate / ((Sampling_Rate) ^ (1/4)) */
			//        		double storageCapacityFactor = flowCapacityFactor / (Math.pow(flowCapacityFactor, (1/4)));
			//        		double storageCapacityFactor = ResourceUtil.getDoubleProperty(rb, PROPERTIES_MATSIM_STORAGE_CAPACITIY_FACTOR);
			//      		matsimConfig.qsim().setStorageCapFactor(storageCapacityFactor);

			Collection<SimpleFeature> zoneFeatures = ShapeFileReader.getAllFeatures(zoneShapeFile);

			Map<Integer,SimpleFeature> zoneFeatureMap = new HashMap<>();
			for (SimpleFeature feature: zoneFeatures) {
				int zoneId = Integer.parseInt(feature.getAttribute("SMZRMZ").toString());
				zoneFeatureMap.put(zoneId,feature);
			}

			Population population = MatsimPopulationCreator.createMatsimPopulation(
					householdData, year, zoneFeatureMap, crs,
					writePopulation, populationScalingFactor * workerScalingFactor);

			String outputDirectoryRoot = matsimConfig.controler().getOutputDirectory();

			matsimConfig.global().setCoordinateSystem(crs);

			// Get travel Times from MATSim
			Map<Tuple<Integer, Integer>, Float> travelTimesMap = 
					SiloMatsimController.runMatsimToCreateTravelTimes(timeOfDayForImpedanceMatrix, numberOfCalcPoints,
							zoneFeatureMap, population, scenarioName, matsimConfig, outputDirectoryRoot);

			// update skims in silo from matsim output:
			acc.readSkimBasedOnMatsim(year, travelTimesMap);

			// update accessibilities in silo from matsim output:
			acc.calculateAccessibilities(year);
			// TODO calculate accessibility directly from MATSim instead of from skims
			// this is computationally very inefficient
	}
}