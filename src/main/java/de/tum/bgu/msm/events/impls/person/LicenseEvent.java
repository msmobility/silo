package de.tum.bgu.msm.events.impls.person;

import de.tum.bgu.msm.events.MicroEvent;

public class LicenseEvent implements MicroEvent {

    private final int id;

    public LicenseEvent(int id) {
        this.id = id;
    }

    public int getPersonId() {
        return this.id;
    }
}
