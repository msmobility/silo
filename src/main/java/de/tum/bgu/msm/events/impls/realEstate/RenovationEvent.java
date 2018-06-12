package de.tum.bgu.msm.events.impls.realEstate;

import de.tum.bgu.msm.events.MicroEvent;

public class RenovationEvent implements MicroEvent {
    private final int id;

    public RenovationEvent(int id) {
        this.id = id;
    }

    public int getDwellingId() {
        return this.id;
    }
}
