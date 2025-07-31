package de.tum.bgu.msm.health.analysis;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.VehicleEntersTrafficEvent;
import org.matsim.api.core.v01.events.handler.VehicleEntersTrafficEventHandler;
import org.matsim.vehicles.Vehicle;

import java.util.List;

public class FindAgentsFromEventsHandler implements VehicleEntersTrafficEventHandler {

    private List<Id<Vehicle>> listOfIds;

    public FindAgentsFromEventsHandler(List<Id<Vehicle>> listOfIds) {
        this.listOfIds = listOfIds;
    }

    @Override
    public void handleEvent(VehicleEntersTrafficEvent event) {

        Id<Vehicle> id = event.getVehicleId();
        if(!listOfIds.contains(id)){
            listOfIds.add(id);
        }
    }

    @Override
    public void reset(int iteration) {

    }
}
