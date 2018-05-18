package de.tum.bgu.msm.events.impls.person;

import de.tum.bgu.msm.events.Event;

public class EducationEvent implements Event {
    private final int id;

    public EducationEvent(int id) {
        this.id = id;
    }

    public int getPersonId() {
        return this.id;
    }
}
