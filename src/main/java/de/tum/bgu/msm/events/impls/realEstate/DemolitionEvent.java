package de.tum.bgu.msm.events.impls.realEstate;

import de.tum.bgu.msm.events.Event;

public class DemolitionEvent implements Event {
    private final int id;

    public DemolitionEvent(int id) {
        this.id = id;
    }

    public int getDwellingId() {
        return this.id;
    }
}
