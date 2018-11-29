package de.tum.bgu.msm.events.impls.realEstate;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.events.MicroEvent;

public class ConstructionEvent implements MicroEvent {
    private final Dwelling dd;

    public ConstructionEvent(Dwelling dd) {
        this.dd = dd;
    }

    public Dwelling getDwelling() {
        return this.dd;
    }
}
