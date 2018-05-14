package de.tum.bgu.msm.events;

import java.util.Collection;

public interface MicroEventModel {

    Collection<Event> prepareYear(int year);
    EventResult handleEvent(Event event);
    void finishYear(int year);

}
