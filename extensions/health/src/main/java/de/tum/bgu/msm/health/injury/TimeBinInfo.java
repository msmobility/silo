/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2017 by the members listed in the COPYING,        *
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

package de.tum.bgu.msm.health.injury;

/**
* @author ikaddoura
*/

 class TimeBinInfo {
	
	private final int intervalNr;
	private double accidentCosts;
		
	public int getIntervalNr() {
		return intervalNr;
	}

	public TimeBinInfo(int intervalNr) {
		this.intervalNr = intervalNr;
	}

	public double getAccidentCosts() {
		return accidentCosts;
	}

	public void setAccidentCosts(double accidentCosts) {
		this.accidentCosts = accidentCosts;
	}

}

