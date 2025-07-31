package de.tum.bgu.msm.events;

public class DisabilityEvent implements MicroEvent {

    private final int id;

    public DisabilityEvent(int id) {
        this.id = id;
    }

    public int getPersonId() {
        return this.id;
    }
}
