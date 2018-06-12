package de.tum.bgu.msm.events.impls.person;

import de.tum.bgu.msm.events.MicroEvent;

public class BirthEvent implements MicroEvent {
    private final int id;

    public BirthEvent(int id) {
        this.id = id;
    }

    public int getPersonId() {
        return this.id;
    }
}
