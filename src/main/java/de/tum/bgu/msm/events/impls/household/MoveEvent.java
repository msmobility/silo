package de.tum.bgu.msm.events.impls.household;

import de.tum.bgu.msm.events.MicroEvent;

public class MoveEvent implements MicroEvent {
    private final int id;

    public MoveEvent(int id) {
        this.id = id;
    }

    public int getHouseholdId() {
        return this.id;
    }
}
