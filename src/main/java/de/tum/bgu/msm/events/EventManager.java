package de.tum.bgu.msm.events;

import cern.colt.Timer;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.SummarizeData;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates a series of events in random order
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 December 2009 in Santa Fe
 **/
public class EventManager {

    private final static Logger LOGGER = Logger.getLogger(EventManager.class);

    private final Table<Integer, String, Integer> timeTracker;
    private final Multiset<Class<? extends Event>> timeAggregator = HashMultiset.create();
    private final Timer timer = new Timer().start();

    private final Multiset<Class<? extends Event>> eventCounter = HashMultiset.create();
    private final List<Event> events = new ArrayList<>();

    private final Map<Class<? extends Event>, MicroEventModel> models = new HashMap<>();

    private final List<EventResult> results = new ArrayList<>();

    public EventManager(Table<Integer, String, Integer> timeTracker) {
        this.timeTracker = timeTracker;
    }

    public <T extends Event> void registerEventHandler(Class<T> klass, MicroEventModel<T> model) {
        this.models.put(klass, model);
    }

    public void simulateEvents(int year) {
        createEvents(year);
        processEvents();
    }

    private void createEvents(int year) {
        LOGGER.info("  Simulating events");

        for(@SuppressWarnings("unchecked") MicroEventModel<? extends Event> model: models.values()) {
            events.addAll(model.prepareYear(year));
        }
        LOGGER.info("  Created " + events.size() + " events to simulate.");
        LOGGER.info("  Shuffling events...");
        Collections.shuffle(events);
        eventCounter.clear();
        timeAggregator.clear();
    }

    private void processEvents() {
        LOGGER.info("  Processing events...");
        for (Event event : events) {
            timer.reset();
            Class<? extends Event> klass= event.getClass();

            //unchecked is justified here, as
            //<T extends Event> void registerEventHandler(Class<T> klass, MicroEventModel<T> model)
            // checks for the right type of model handlers. nico, May'18
            @SuppressWarnings("unchecked")
            final EventResult result = this.models.get(klass).handleEvent(event);
            if(result!= null) {
                eventCounter.add(event.getClass());
                results.add(result);
            }
            timeAggregator.add(klass, (int) timer.millis());
        }
    }

    public void finishYear(int year, int[] carChangeCounter, SiloDataContainer dataContainer) {

        for(Class<? extends Event> klass: timeAggregator.elementSet()) {
            timeTracker.put(year, klass.getSimpleName(), (int) timer.millis());
        }


        for(MicroEventModel model: models.values()) {
            model.finishYear(year);
        }
        LOGGER.info("Writing out events...");
        EventWriter.writeEvents(results, year);
        SummarizeData.resultFile("Count of simulated events");
        LOGGER.info("Simulated " + results.size() + " events in total.");
        for(Class<? extends Event> event: eventCounter.elementSet()) {
            final int count = eventCounter.count(event);
            SummarizeData.resultFile(event.getName() + "," + count);
            LOGGER.info("Simulated " + event.getName() + ": " + count);
        }
        float hh = dataContainer.getHouseholdData().getHouseholds().size();
        LOGGER.info("  Simulated household added a car" + carChangeCounter[0] + " (" +
                SiloUtil.rounder((100f * carChangeCounter[0] / hh), 0) + "% of hh)");
        SummarizeData.resultFile("AddedCar," + carChangeCounter[0]);
        LOGGER.info("  Simulated household relinquished a car" + carChangeCounter[1] + " (" +
                SiloUtil.rounder((100f * carChangeCounter[1] / hh), 0) + "% of hh)");
        SummarizeData.resultFile("RelinquishedCar," + carChangeCounter[1]);

        events.clear();
        results.clear();
    }
}
