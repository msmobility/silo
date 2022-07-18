package de.tum.bgu.msm.events.impls.person;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.vehicle.Vehicle;
import de.tum.bgu.msm.events.MicroEvent;

public class VehicleBirthdayEvent implements MicroEvent {

    private final Household hh;
    private final Vehicle vehicle;

    public VehicleBirthdayEvent(Household hh, Vehicle vehicle) {
        this.hh = hh;
        this.vehicle = vehicle;
    }

    public Household getHousehold() {
        return this.hh;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}
