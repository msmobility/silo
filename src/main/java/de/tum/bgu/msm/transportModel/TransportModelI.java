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

import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.data.MitoHousehold;

/**
 * @author nagel
 *
 */
public interface TransportModelI {

	void feedData(int[] zones, Matrix autoTravelTimes, Matrix transitTravelTimes, MitoHousehold[] mitoHouseholds,
				  int[] retailEmplByZone, int[] officeEmplByZone, int[] otherEmplByZone, int[] totalEmplByZone,
				  float[] sizeOfZonesInAcre);

	void setScenarioName(String name);

	void runTransportModel(int year);
	
	void writeOutSocioEconomicDataForMstm(int year) ;

	void tripGeneration();

}
