package de.tum.bgu.msm.models;

import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.simulator.UpdateListener;

import java.util.Collection;

public interface EventModel<T extends MicroEvent> extends UpdateListener {

    /**
     * Perform model internal preparations for the current year and return
     * planned events to be handled.
     * @param year  the current starting year for which events shall be created
     * @return  a Collection of events that this model will handle in the current year.
     */
    Collection<T> getEventsForCurrentYear(int year);

    /**
     * Handles the given event and returns whether the event succeeded, i.e.
     * resulted in a change.
     * @param event the event to be handled. Event here describes more an opportunity
     *              that something happens which can fail or succeed.
     * @return  true if the event is triggered, i.e. a change in data occurred; false otherwise
     */
    boolean handleEvent(T event);

}
