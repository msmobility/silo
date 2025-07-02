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

import com.google.inject.Inject;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.events.handler.EventHandler;
import org.matsim.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author ikaddoura
*/

public class AnalysisEventHandler implements EventHandler, LinkLeaveEventHandler, PersonEntersVehicleEventHandler, PersonDepartureEventHandler {

	private final Map<Id<Link>, Map<Integer, Integer>> linkId2time2leavingAgents = new HashMap<>();
	private final Map<Id<Link>, Map<Integer, List<Id<Person>>>> linkId2time2personIds = new HashMap<>();
	private final Map<Id<Vehicle>, Id<Person>> vehicleId2personId = new HashMap<>();
	private final Map<Id<Link>, Map<String, Map<Integer, Integer>>> linkId2mode2time2leavingAgents = new HashMap<>();
	private final Map<Id<Person>, String> personId2legMode = new HashMap<>();


	@Inject
	public AnalysisEventHandler(){}
	
	@Inject
	private Scenario scenario;

	@Inject
	private AccidentsContext accidentsContext;
	
	@Override
	public void reset(int arg0) {
		// reset temporary information at the beginning of each iteration
		
		linkId2time2leavingAgents.clear();
		linkId2time2personIds.clear();
		vehicleId2personId.clear();
		linkId2mode2time2leavingAgents.clear();
		personId2legMode.clear();
	}

	@Override
	public void handleEvent(LinkLeaveEvent event) {
		//Why scenario is null
		double timeBinSize = this.scenario.getConfig().travelTimeCalculator().getTraveltimeBinSize();
		int timeBinNr = (int) (event.getTime() / timeBinSize);

		if(timeBinNr < 24){
			Id<Link> linkId = event.getLinkId();

			String legMode = personId2legMode.get(vehicleId2personId.get(event.getVehicleId()));
			if (linkId2mode2time2leavingAgents.get(linkId) != null) {
				if (linkId2mode2time2leavingAgents.get(linkId).get(legMode) != null) {
					if(linkId2mode2time2leavingAgents.get(linkId).get(legMode).get(timeBinNr) != null){
						int leavingAgents = linkId2mode2time2leavingAgents.get(linkId).get(legMode).get(timeBinNr) + 1;
						linkId2mode2time2leavingAgents.get(linkId).get(legMode).put(timeBinNr, leavingAgents);
					}else {
						linkId2mode2time2leavingAgents.get(linkId).get(legMode).put(timeBinNr, 1);
					}
				} else {
					Map<Integer,Integer> time2leavingAgents = new HashMap<>();
					time2leavingAgents.put(timeBinNr,1);
					linkId2mode2time2leavingAgents.get(linkId).put(legMode, time2leavingAgents);
				}
			} else {
				Map<String, Map<Integer,Integer>> mode2time2leavingAgents = new HashMap<>();
				Map<Integer,Integer> time2leavingAgents = new HashMap<>();
				time2leavingAgents.put(timeBinNr,1);
				mode2time2leavingAgents.put(legMode,time2leavingAgents);
				linkId2mode2time2leavingAgents.put(linkId, mode2time2leavingAgents);
			}

			AccidentAgentInfo personInfo = accidentsContext.getPersonId2info().get(getDriverId(event.getVehicleId()));
			if(personInfo==null){
				personInfo = new AccidentAgentInfo(getDriverId(event.getVehicleId()));
				accidentsContext.getPersonId2info().put(getDriverId(event.getVehicleId()),personInfo);
			}
			int hour = (int) (event.getTime()/3600);
			if(personInfo.getLinkId2time2mode().get(linkId)!=null){
				personInfo.getLinkId2time2mode().get(linkId).put(hour, legMode);
			}else{
				Map<Integer, String> time2Mode = new HashMap<>();
				time2Mode.put(hour, legMode);
				personInfo.getLinkId2time2mode().put(linkId, time2Mode);
			}

		}
	}

	private Id<Person> getDriverId(Id<Vehicle> vehicleId) {
		return this.vehicleId2personId.get(vehicleId);
	}

	public double getDemand(Id<Link> linkId, int intervalNr) {
		double demand = 0.;
		if (this.linkId2time2leavingAgents.get(linkId) != null && this.linkId2time2leavingAgents.get(linkId).get(intervalNr) != null) {
			demand = this.linkId2time2leavingAgents.get(linkId).get(intervalNr);
		}
		return demand;
	}

	public double getDemand(Id<Link> linkId, String mode) {
		double demand = 0.;
		if (this.linkId2mode2time2leavingAgents.get(linkId) != null) {
			if(this.linkId2mode2time2leavingAgents.get(linkId).get(mode) != null){
				for(int i : this.linkId2mode2time2leavingAgents.get(linkId).get(mode).keySet()){
					demand += this.linkId2mode2time2leavingAgents.get(linkId).get(mode).get(i);
				}
			}
		}
		return demand;
	}

	public double getDemand(Id<Link> linkId, String mode, int intervalNr) {
		double demand = 0.;
		if (this.linkId2mode2time2leavingAgents.get(linkId) != null) {
			if(this.linkId2mode2time2leavingAgents.get(linkId).get(mode) != null){
				if(this.linkId2mode2time2leavingAgents.get(linkId).get(mode).get(intervalNr) != null){
					demand = this.linkId2mode2time2leavingAgents.get(linkId).get(mode).get(intervalNr);
				}
			}
		}
		return demand;
	}

	@Override
	public void handleEvent(PersonEntersVehicleEvent event) {
		vehicleId2personId.put(event.getVehicleId(), event.getPersonId());
	}

	@Override
	public void handleEvent(PersonDepartureEvent event) {
		personId2legMode.put(event.getPersonId(), event.getLegMode());
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public void setAccidentsContext(AccidentsContext accidentsContext) {
		this.accidentsContext = accidentsContext;
	}
}

