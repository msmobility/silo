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
import org.matsim.api.core.v01.IdMap;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.events.handler.EventHandler;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.Vehicles;

import java.util.HashMap;
import java.util.Map;

/**
 * Event handler for tracking hourly traffic volumes by mode (bike, walk, car, truck) in MATSim.
 * Records vehicles entering links and provides demand queries by link, mode, and time interval.
 */
public class AnalysisEventHandler2 implements LinkEnterEventHandler, EventHandler {

    private final Vehicles vehicles;
    private final Scenario scenario;

    private final IdMap<Link, Map<Integer, Integer>> bikeVolumes = new IdMap<>(Link.class);
    private final IdMap<Link, Map<Integer, Integer>> pedVolumes = new IdMap<>(Link.class);
    private final IdMap<Link, Map<Integer, Integer>> carVolumes = new IdMap<>(Link.class);
    private final IdMap<Link, Map<Integer, Integer>> truckVolumes = new IdMap<>(Link.class);

    @Inject
    public AnalysisEventHandler2(Vehicles vehicles, Scenario scenario) {
        this.vehicles = vehicles;
        this.scenario = scenario;
    }

    @Override
    public void reset(int iteration) {
        // Clear all volume maps at the start of each iteration
        bikeVolumes.clear();
        pedVolumes.clear();
        carVolumes.clear();
        truckVolumes.clear();
    }

    @Override
    public void handleEvent(LinkEnterEvent event) {
        if (scenario == null) {
            throw new IllegalStateException("Scenario is not initialized");
        }
        if (vehicles == null) {
            throw new IllegalStateException("Vehicles is not initialized");
        }

        Id<Link> linkId = event.getLinkId();
        Id<Vehicle> vehicleId = event.getVehicleId();

        // Get vehicle mode, default to "car" for unknown modes
        Vehicle vehicle = vehicles.getVehicles().get(vehicleId);
        if (vehicle == null) {
            return; // Skip if vehicle is not found
        }
        String mode = vehicle.getType() != null ? vehicle.getType().getNetworkMode() : null;
        if (mode == null) {
            mode = "car"; // Default to car for unknown modes
        }

        // Calculate time bin based on configuration
        double timeBinSize = scenario.getConfig().travelTimeCalculator().getTraveltimeBinSize();
        int timeBinNr = (int) (event.getTime() / timeBinSize);

        // Update the appropriate volume map
        IdMap<Link, Map<Integer, Integer>> targetMap;
        switch (mode) {
            case "bike":
                targetMap = bikeVolumes;
                break;
            case "walk":
                targetMap = pedVolumes;
                break;
            case "truck":
                targetMap = truckVolumes;
                break;
            default:
                targetMap = carVolumes;
                break;
        }

        targetMap.computeIfAbsent(linkId, k -> new HashMap<>())
                .merge(timeBinNr, 1, Integer::sum);
    }

    /**
     * Returns the number of vehicles entering a link for a specific mode in a given time interval.
     *
     * @param linkId     The ID of the link
     * @param mode       The transport mode (bike, walk, car, truck)
     * @param intervalNr The time bin number
     * @return The number of vehicles
     */
    public double getDemand(Id<Link> linkId, String mode, int intervalNr) {
        IdMap<Link, Map<Integer, Integer>> targetMap;
        switch (mode) {
            case "bike":
                targetMap = bikeVolumes;
                break;
            case "walk":
                targetMap = pedVolumes;
                break;
            case "car":
                targetMap = carVolumes;
                break;
            case "truck":
                targetMap = truckVolumes;
                break;
            default:
                return 0.0; // Return 0 for unknown modes
        }
        return targetMap.getOrDefault(linkId, new HashMap<>()).getOrDefault(intervalNr, 0);
    }

    public IdMap<Link, Map<Integer, Integer>> getBikeVolumes() {
        return bikeVolumes;
    }

    public IdMap<Link, Map<Integer, Integer>> getPedVolumes() {
        return pedVolumes;
    }

    public IdMap<Link, Map<Integer, Integer>> getCarVolumes() {
        return carVolumes;
    }

    public IdMap<Link, Map<Integer, Integer>> getTruckVolumes() {
        return truckVolumes;
    }
}