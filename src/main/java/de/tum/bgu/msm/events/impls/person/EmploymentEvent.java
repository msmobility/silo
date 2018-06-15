package de.tum.bgu.msm.events.impls.person;

import de.tum.bgu.msm.events.MicroEvent;

public class EmploymentEvent implements MicroEvent {

    public enum Type {FIND, QUIT};

    private final int id;
    private final Type type;

    public EmploymentEvent(int id, Type type) {
        this.id = id;
        this.type = type;
    }

    public int getPersonId() {
        return this.id;
    }

    public Type getType() {
        return this.type;
    }
}
