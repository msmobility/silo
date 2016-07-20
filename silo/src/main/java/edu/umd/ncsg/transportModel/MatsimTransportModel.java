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

import org.jfree.util.Log;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import com.pb.common.util.ResourceUtil;

import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.data.Accessibility;
import edu.umd.ncsg.data.HouseholdDataManager;

/**
 * @author nagel
 *
 */
public class MatsimTransportModel implements TransportModelI {
	private HouseholdDataManager householdData;
	private Accessibility acc;
	public MatsimTransportModel( HouseholdDataManager householdData, Accessibility acc ) {
		this.householdData = householdData;
		this.acc = acc;
		
	}
	@Override
	public void runMstm(int year ) {
		if (year == 2000 || year == 2007) {
			Log.info("Running MATSim transport model for year " + (year+1) + "."); 
			// yyyyyy a problem with "next year"

			String zoneShapeFile = SiloUtil.baseDirectory + "/" + rb.getString(PROPERTIES_ZONES_SHAPEFILE);
			String crs = rb.getString(PROPERTIES_ZONES_CRS);

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
					householdData, nextYearForTransportModel, zoneFeatureMap, crs,
					writePopulation, populationScalingFactor * workerScalingFactor);

			String outputDirectoryRoot = matsimConfig.controler().getOutputDirectory();

			matsimConfig.global().setCoordinateSystem(crs);

			String siloRunId = "siloRunId" ;

			// Get travel Times from MATSim
			Map<Tuple<Integer, Integer>, Float> travelTimesMap = 
					SiloMatsimController.runMatsimToCreateTravelTimes(timeOfDayForImpedanceMatrix, numberOfCalcPoints,
							zoneFeatureMap, population, nextYearForTransportModel, siloRunId, matsimConfig, outputDirectoryRoot);

			// update skims in silo from matsim output:
			acc.readSkimBasedOnMatsim(nextYearForTransportModel, travelTimesMap);

			// update accessibilities in silo from matsim output:
			acc.calculateAccessibilities(nextYearForTransportModel);
			// TODO calculate accessibility directly from MATSim instead of from skims
			// this is computationally very inefficient
		}
	}

}
