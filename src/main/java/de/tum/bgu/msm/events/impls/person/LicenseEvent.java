package de.tum.bgu.msm.events.impls.person;

import de.tum.bgu.msm.events.Event;

public class LicenseEvent implements Event {

    private final int id;

    public LicenseEvent(int id) {
        this.id = id;
    }

    public int getPersonId() {
        return this.id;
    }
}
