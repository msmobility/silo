package de.tum.bgu.msm.events;

import java.util.Collection;

public interface EventCreator {

    Collection<Event> createEvents(int year);
}
