package de.tum.bgu.msm.events.impls.household;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.events.MicroEvent;

public class MigrationEvent implements MicroEvent {

    public enum Type {IN, OUT}

    private final Household hh;
    private final Type type;

    public MigrationEvent(Household hh, Type type) {
        this.hh = hh;
        this.type = type;
    }

    public Household getHousehold() {
        return this.hh;
    }

    public Type getType() {
        return type;
    }
}
