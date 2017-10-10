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

import java.util.*;

import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.data.*;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import org.apache.log4j.Logger;
import de.tum.bgu.msm.SiloUtil;

/**
 * @author dziemke
 */
public class MatsimTransportModel implements TransportModelI {
	private static final Logger logger = Logger.getLogger( MatsimTransportModel.class );
	
	private static final Random random = MatsimRandom.getLocalInstance(); // Make sure that stream of random variables is reproducible. kai, apr'16

	private static final String PROPERTIES_ZONES_SHAPEFILE	= "matsim.zones.shapefile";
	private static final String PROPERTIES_ZONES_CRS 		= "matsim.zones.crs";

	private HouseholdDataManager householdData;
	private Accessibility acc;
	private ResourceBundle rb;
	private Config matsimConfig;


	public MatsimTransportModel(HouseholdDataManager householdData, Accessibility acc, ResourceBundle rb, Config matsimConfig) {
		Gbl.assertNotNull(householdData);
		this.householdData = householdData;
		this.acc = acc;
		this.rb = rb;
		this.matsimConfig = matsimConfig;
	}



	@Override
	public void setScenarioName(String scenarioName) {

	}

	@Override
	public void runTransportModel(int year) {
		logger.info("Running MATSim transport model for year " + year + ".");

		String scenarioName = rb.getString(SiloUtil.PROPERTIES_SCENARIO_NAME);

		String crs = rb.getString(PROPERTIES_ZONES_CRS);
		matsimConfig.global().setCoordinateSystem(crs);
		String zoneShapeFile = SiloUtil.baseDirectory + "/" + rb.getString(PROPERTIES_ZONES_SHAPEFILE);
		
		// In the current implementation, MATSim is used to reflect the functionality that was previously
		// covered by MSTM. As such, based on the MATSim transport simulation, a travel time matrix (skim)
		// is computed. To do so, random coordinates in each zone are taken to measure the zone-to-zone
		// travel times. <code>numberOfCalcPoints</code> states how many such points in each zone are used;
		// in case multiple points are used; the average of all travel times of a given relation is used.
		int numberOfCalcPoints = 1;
		boolean writePopulation = false;
//		double populationScalingFactor = 0.01;
		double populationScalingFactor = 1.; // For test
		
		// people working at non-peak times (only peak traffic is simulated), and people going by a mode other
		// than car in case a car is still available to them
		double workerScalingFactor = 0.66;
		double flowCapacityFactor = populationScalingFactor;
		matsimConfig.qsim().setFlowCapFactor(flowCapacityFactor);
		
		// According to "NicolaiNagel2013HighResolutionAccessibility (citing Rieser on p.9):
		// Storage_Capacitiy_Factor = Sampling_Rate / ((Sampling_Rate) ^ (1/4))
		double storageCapacityFactor = Math.round((flowCapacityFactor / (Math.pow(flowCapacityFactor, 0.25)) * 100)) / 100.;
		matsimConfig.qsim().setStorageCapFactor(storageCapacityFactor);

		String matsimRunId = scenarioName + "_" + year;
		Collection<SimpleFeature> zoneFeatures = ShapeFileReader.getAllFeatures(zoneShapeFile);

		Map<Integer,SimpleFeature> zoneFeatureMap = new HashMap<>();
		for (SimpleFeature feature: zoneFeatures) {
			int zoneId = Integer.parseInt(feature.getAttribute("SMZRMZ").toString());
			// System.out.println("zoneId = " + zoneId);
			zoneFeatureMap.put(zoneId,feature);
		}

		Population population = MatsimPopulationCreator.createMatsimPopulation(householdData, year, zoneFeatureMap,
				writePopulation, populationScalingFactor * workerScalingFactor, random);

		String outputDirectoryRoot = matsimConfig.controler().getOutputDirectory();

		// Get travel Times from MATSim
		Map<Tuple<Integer, Integer>, Float> travelTimesMap = SiloMatsimController.runMatsimToCreateTravelTimes(numberOfCalcPoints,
				zoneFeatureMap, population, matsimRunId, matsimConfig, outputDirectoryRoot);

		// Update skims in silo from matsim output
		acc.readSkimBasedOnMatsim(year, travelTimesMap);

		// Update accessibilities in silo from matsim output
		acc.calculateAccessibilities(year);
		// TODO calculate accessibility directly from MATSim instead of from skims. Current version is computationally very inefficient
	}

	@Override
	public void writeOutSocioEconomicDataForMstm(int year) {
		// Not doing anything
	}
	@Override
	public void tripGeneration() {
		// Not doing anything
	}

	@Override
	public void feedData(Map<Integer, Zone> zones, Matrix hwySkim, Matrix transitSkim, Map<Integer, MitoHousehold> households) {

	}
}