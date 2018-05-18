package de.tum.bgu.msm.events.impls.realEstate;

import de.tum.bgu.msm.events.Event;

public class ConstructionEvent implements Event {
    private final int id;

    public ConstructionEvent(int id) {
        this.id = id;
    }

    public int getDwellingId() {
        return this.id;
    }
}
