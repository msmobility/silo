package de.tum.bgu.msm.events.impls.person;

import de.tum.bgu.msm.events.MicroEvent;

public class DivorceEvent implements MicroEvent {
    private final int id;

    public DivorceEvent(int id) {
        this.id = id;
    }

    public int getPersonId() {
        return this.id;
    }
}
