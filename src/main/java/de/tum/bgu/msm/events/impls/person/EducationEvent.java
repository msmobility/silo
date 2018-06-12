package de.tum.bgu.msm.events.impls.person;

import de.tum.bgu.msm.events.MicroEvent;

public class EducationEvent implements MicroEvent {
    private final int id;

    public EducationEvent(int id) {
        this.id = id;
    }

    public int getPersonId() {
        return this.id;
    }
}
