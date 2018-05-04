package de.tum.bgu.msm.events;

public class EventImpl implements Event {

    private final EventType type;
    private final int id;
    private final int year;

    public EventImpl(EventType type, int id, int year) {
        this.type = type;
        this.id = id;
        this.year = year;
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public int getId() {
        return id;
    }
}
