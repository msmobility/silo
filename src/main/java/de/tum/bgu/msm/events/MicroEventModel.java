package de.tum.bgu.msm.events;

import java.util.Collection;

public interface MicroEventModel<T extends MicroEvent> {

    /**
     * Perform model internal preparations for the current year and return
     * planned events to be handled.
     * @param year  the current starting year for which events shall be created
     * @return  a Collection of events that this model will handle in the current year.
     */
    Collection<T> prepareYear(int year);

    /**
     * Handles the given event and returns whether the event succeeded, i.e.
     * resulted in a change.
     * @param event the event to be handled. Event here describes more an opportunity
     *              that something happens which can fail or succeed.
     * @return  true if the event is triggered, i.e. a change in data occurred; false otherwise
     */
    boolean handleEvent(T event);

    /**
     * Entry point for post-year actions, e.g. cleaning up or summarizing temporary data
     * @param year  the current ending year
     */
    void finishYear(int year);
}
