package de.tum.bgu.msm.events.impls.realEstate;

import de.tum.bgu.msm.events.MicroEvent;

public class DemolitionEvent implements MicroEvent {
    private final int id;

    public DemolitionEvent(int id) {
        this.id = id;
    }

    public int getDwellingId() {
        return this.id;
    }
}
