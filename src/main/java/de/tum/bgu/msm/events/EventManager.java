package de.tum.bgu.msm.events;

import cern.colt.Timer;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.SummarizeData;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates a series of events in random order
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 December 2009 in Santa Fe
 **/
public class EventManager {

    private final static Logger LOGGER = Logger.getLogger(EventManager.class);

    private final long[][] timeCounter;
    private final Multiset<EventType> eventCounter = EnumMultiset.create(EventType.class);

    private final List<MicroEventModel> models = new ArrayList<>();

    private final List<Event> events = new ArrayList<>();
    private final Timer timer = new Timer().start();
    private final List<EventResult> results = new ArrayList<>();

    public EventManager(long[][] timeCounter) {
        this.timeCounter = timeCounter;
    }

    public void registerEventHandler(MicroEventModel model) {
        this.models.add(model);
    }

    public void simulateEvents(int year) {
        createEvents(year);
        processEvents();
    }

    private void createEvents(int year) {
        LOGGER.info("  Simulating events");
        for(MicroEventModel model: models) {
            events.addAll(model.prepareYear(year));
        }

        LOGGER.info("  Created " + events.size() + " events to simulate");
        LOGGER.info("  Shuffling events.");
        Collections.shuffle(events);
        eventCounter.clear();
    }

    private void processEvents() {
        for (Event event : events) {
            timer.reset();
            for(MicroEventModel model: models) {
                final EventResult result = model.handleEvent(event);
                if(result!= null) {
                    eventCounter.add(event.getType());
                    results.add(result);
                }
            }
            timeCounter[event.getType().ordinal()][event.getYear()] += timer.millis();
        }
    }

    public void finishYear(int year, int[] carChangeCounter, SiloDataContainer dataContainer) {
        for(MicroEventModel model: models) {
            model.finishYear(year);
        }
        EventWriter.writeEvents(results, year);
        SummarizeData.resultFile("Count of simulated events");
        LOGGER.info("Simulated " + results.size() + " events in total.");
        for(EventType type: eventCounter.elementSet()) {
            final int count = eventCounter.count(type);
            SummarizeData.resultFile(type.name() + "," + count);
            LOGGER.info("Simulated " + type.name() + ": " + count);
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
