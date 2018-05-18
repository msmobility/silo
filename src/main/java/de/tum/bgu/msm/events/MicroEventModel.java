package de.tum.bgu.msm.events;

import java.util.Collection;

public interface MicroEventModel<T extends Event> {
    Collection<T> prepareYear(int year);
    EventResult handleEvent(T event);
    void finishYear(int year);
}
