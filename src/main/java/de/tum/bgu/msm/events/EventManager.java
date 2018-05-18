package de.tum.bgu.msm.events;

import cern.colt.Timer;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
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

    private final long[][] timeCounter;
    private final Multiset<Class<? extends Event>> eventCounter = HashMultiset.create();

    private final Map<Class<? extends Event>, MicroEventModel> models = new HashMap<>();

    private final List<Event> events = new ArrayList<>();
    private final Timer timer = new Timer().start();
    private final List<EventResult> results = new ArrayList<>();

    public EventManager(long[][] timeCounter) {
        this.timeCounter = timeCounter;
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
        for(MicroEventModel<? extends Event> model: models.values().stream().distinct().collect(Collectors.toList())) {
            events.addAll(model.prepareYear(year));
        }

        LOGGER.info("  Created " + events.size() + " events to simulate.");
        LOGGER.info("  Shuffling events...");
        Collections.shuffle(events);
        eventCounter.clear();
    }

    private void processEvents() {
        LOGGER.info("  Processing events...");
        for (Event event : events) {
            timer.reset();
            Class<? extends Event> klass= event.getClass();
            final EventResult result = this.models.get(klass).handleEvent(event);

            if(result!= null) {
                eventCounter.add(event.getClass());
                results.add(result);
            }
//            timeCounter[event.getType().ordinal()][event.getYear()] += timer.millis();
        }
    }

    public void finishYear(int year, int[] carChangeCounter, SiloDataContainer dataContainer) {
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
