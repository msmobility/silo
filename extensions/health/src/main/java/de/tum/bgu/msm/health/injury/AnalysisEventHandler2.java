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

import java.util.HashMap;
import java.util.Map;

/**
 * Event handler for analyzing traffic flows and accident data in MATSim.
 * Tracks agents leaving links by time bin and mode, and stores per-person accident data.
 *
 * @author ikaddoura
 */
public class AnalysisEventHandler2 implements EventHandler, LinkLeaveEventHandler, PersonEntersVehicleEventHandler, PersonDepartureEventHandler {

    private final Map<Id<Link>, Map<Integer, Integer>> linkId2time2leavingAgents = new HashMap<>();
    private final Map<Id<Vehicle>, Id<Person>> vehicleId2personId = new HashMap<>();
    private final Map<Id<Link>, Map<String, Map<Integer, Integer>>> linkId2mode2time2leavingAgents = new HashMap<>();
    private final Map<Id<Person>, String> personId2legMode = new HashMap<>();

    @Inject
    private Scenario scenario;

    @Inject
    private AccidentsContext accidentsContext;

    @Inject
    public AnalysisEventHandler2() {
    }

    @Override
    public void reset(int iteration) {
        // Clear all maps at the start of each iteration
        linkId2time2leavingAgents.clear();
        vehicleId2personId.clear();
        linkId2mode2time2leavingAgents.clear();
        personId2legMode.clear();
    }

    @Override
    public void handleEvent(LinkLeaveEvent event) {
        // Validate dependencies
        if (scenario == null) {
            throw new IllegalStateException("Scenario is not initialized");
        }
        if (accidentsContext == null) {
            throw new IllegalStateException("AccidentsContext is not initialized");
        }

        // Calculate time bin based on configuration
        double timeBinSize = scenario.getConfig().travelTimeCalculator().getTraveltimeBinSize();
        int timeBinNr = (int) (event.getTime() / timeBinSize);
        Id<Link> linkId = event.getLinkId();

        // Get the driver's mode
        Id<Person> driverId = getDriverId(event.getVehicleId());
        String legMode = personId2legMode.get(driverId);
        if (legMode == null) {
            return; // Skip if mode is unknown (e.g., event processed out of order)
        }

        // Update total agents leaving the link in this time bin
        linkId2time2leavingAgents.computeIfAbsent(linkId, k -> new HashMap<>())
                .merge(timeBinNr, 1, Integer::sum);

        // Update mode-specific agents leaving the link in this time bin
        linkId2mode2time2leavingAgents.computeIfAbsent(linkId, k -> new HashMap<>())
                .computeIfAbsent(legMode, k -> new HashMap<>())
                .merge(timeBinNr, 1, Integer::sum);

        // Update accident data for the driver
        AccidentAgentInfo personInfo = accidentsContext.getPersonId2info().computeIfAbsent(driverId, k -> new AccidentAgentInfo(driverId));
        personInfo.getLinkId2time2mode().computeIfAbsent(linkId, k -> new HashMap<>())
                .put(timeBinNr, legMode);
    }

    @Override
    public void handleEvent(PersonEntersVehicleEvent event) {
        // Only map the person to the vehicle if they are the driver (e.g., car or bike mode)
        String mode = personId2legMode.get(event.getPersonId());
        if (mode != null && (mode.equals("car") || mode.equals("bike"))) { // Adjust modes as needed
            vehicleId2personId.put(event.getVehicleId(), event.getPersonId());
        }
    }

    @Override
    public void handleEvent(PersonDepartureEvent event) {
        // Store the transport mode for the person's current leg
        personId2legMode.put(event.getPersonId(), event.getLegMode());
    }

    /**
     * Returns the total number of agents leaving a link in a specific time bin.
     *
     * @param linkId    The ID of the link
     * @param intervalNr The time bin number
     * @return The number of agents
     */
    public double getDemand(Id<Link> linkId, int intervalNr) {
        return linkId2time2leavingAgents.getOrDefault(linkId, new HashMap<>())
                .getOrDefault(intervalNr, 0);
    }

    /**
     * Returns the total number of agents leaving a link for a specific mode across all time bins.
     *
     * @param linkId The ID of the link
     * @param mode   The transport mode
     * @return The total number of agents
     */
    public double getDemand(Id<Link> linkId, String mode) {
        Map<String, Map<Integer, Integer>> mode2time2count = linkId2mode2time2leavingAgents.get(linkId);
        if (mode2time2count == null) {
            return 0.0;
        }
        Map<Integer, Integer> time2count = mode2time2count.get(mode);
        if (time2count == null) {
            return 0.0;
        }
        return time2count.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Returns the number of agents leaving a link for a specific mode in a specific time bin.
     *
     * @param linkId    The ID of the link
     * @param mode      The transport mode
     * @param intervalNr The time bin number
     * @return The number of agents
     */
    public double getDemand(Id<Link> linkId, String mode, int intervalNr) {
        return linkId2mode2time2leavingAgents.getOrDefault(linkId, new HashMap<>())
                .getOrDefault(mode, new HashMap<>())
                .getOrDefault(intervalNr, 0);
    }

    private Id<Person> getDriverId(Id<Vehicle> vehicleId) {
        return vehicleId2personId.getOrDefault(vehicleId, null);
    }

    // Setter for scenario (for manual initialization if needed)
    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    // Setter for accidents context (for manual initialization if needed)
    public void setAccidentsContext(AccidentsContext accidentsContext) {
        this.accidentsContext = accidentsContext;
    }
}