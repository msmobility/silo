package de.tum.bgu.msm.events.impls;

import de.tum.bgu.msm.events.Event;

public class MarriageEvent implements Event{

    private final int firstId;
    private final int secondId;

    public MarriageEvent(int firstId, int secondId) {
        this.firstId = firstId;
        this.secondId = secondId;
    }

    public int getFirstId() {
        return this.firstId;
    }
    public int getSecondId() {
        return this.secondId;
    }

}
