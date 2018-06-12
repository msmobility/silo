package de.tum.bgu.msm.events.impls.household;

import de.tum.bgu.msm.events.MicroEvent;

public class MigrationEvent implements MicroEvent {

    public enum Type {IN, OUT}

    private final int id;
    private final Type type;

    public MigrationEvent(int id, Type type) {
        this.id = id;
        this.type = type;
    }

    public int getHouseholdId() {
        return this.id;
    }

    public Type getType() {
        return type;
    }
}
