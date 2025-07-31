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
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;

import java.util.HashMap;
import java.util.Map;

/**
* @author ikaddoura
*/

public final class AccidentsContext {
	// class is public so it can be bound from outside package

	@Inject AccidentsContext(){}
	// injected constructor is package-private so that nobody can instantiate this class directly
	
	private Map<Id<Link>, AccidentLinkInfo> linkId2info = new HashMap<>();

	private Map<Id<Person>, AccidentAgentInfo> personId2info = new HashMap<>();

	public Map<Id<Link>, AccidentLinkInfo> getLinkId2info() {
		return linkId2info;
	}

	public Map<Id<Person>, AccidentAgentInfo> getPersonId2info() {
		return personId2info;
	}

	public void reset() {
		linkId2info.clear();
		personId2info.clear();
	}
}

