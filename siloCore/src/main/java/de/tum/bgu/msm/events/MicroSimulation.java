package de.tum.bgu.msm.events;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.utils.TimeTracker;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Generates a series of events in random order
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 December 2009 in Santa Fe
 **/
public final class MicroSimulation {

    private final static Logger LOGGER = Logger.getLogger(MicroSimulation.class);

    private final Multiset<Class<? extends MicroEvent>> eventCounter = HashMultiset.create();

    private final Map<Class<? extends MicroEvent>, MicroEventModel> models = new LinkedHashMap<>();

    private final List<MicroEvent> events = new ArrayList<>();
    private final TimeTracker timeTracker;

    public MicroSimulation(TimeTracker timeTracker) {
        this.timeTracker = timeTracker;
    }

    public <T extends MicroEvent> void registerModel(Class<T> klass, MicroEventModel<T> model) {
        this.models.put(klass, model);
        LOGGER.info("Registered " + model.getClass().getSimpleName() + " for: " + klass.getSimpleName());
    }

    public void simulate(int year) {
        createEvents(year);
        processEvents();
    }

    private void createEvents(int year) {
        LOGGER.info("  Creating events");
        timeTracker.reset();
        for(@SuppressWarnings("unchecked") MicroEventModel<? extends MicroEvent> model: models.values()) {
            events.addAll(model.prepareYear(year));
        }
        LOGGER.info("  Created " + events.size() + " events to simulate.");
        LOGGER.info("  Shuffling events...");
        Collections.shuffle(events, SiloUtil.getRandomObject());
        timeTracker.record("EventCreation");
        eventCounter.clear();
    }

    private void processEvents() {
        LOGGER.info("  Processing events...");
        for (MicroEvent e: events) {
            timeTracker.reset();
            Class<? extends MicroEvent> klass= e.getClass();
            //unchecked is justified here, as
            //<T extends Event> void registerModel(Class<T> klass, MicroEventModel<T> model)
            // checks for the right type of model handlers
            @SuppressWarnings("unchecked")
            boolean success = this.models.get(klass).handleEvent(e);
            if(success) {
                eventCounter.add(e.getClass());
            }
            timeTracker.record(klass.getSimpleName());
        }
    }

    public void finishYear(int year, int[] carChangeCounter, int avSwitchCounter, SiloDataContainer dataContainer) {
        for(MicroEventModel model: models.values()) {
            model.finishYear(year);
        }
        SummarizeData.resultFile("Count of simulated events");
        LOGGER.info("Simulated " + eventCounter.size() + " successful events in total.");
        for(Class<? extends MicroEvent> event: eventCounter.elementSet()) {
            final int count = eventCounter.count(event);
            SummarizeData.resultFile(event.getSimpleName() + "," + count);
            LOGGER.info("Simulated " + event.getSimpleName() + ": " + count);
        }
        float hh = dataContainer.getHouseholdData().getHouseholds().size();
        LOGGER.info("  Simulated household added a car" + carChangeCounter[0] + " (" +
                SiloUtil.rounder((100f * carChangeCounter[0] / hh), 0) + "% of hh)");
        SummarizeData.resultFile("AddedCar," + carChangeCounter[0]);
        LOGGER.info("  Simulated household relinquished a car" + carChangeCounter[1] + " (" +
                SiloUtil.rounder((100f * carChangeCounter[1] / hh), 0) + "% of hh)");
        SummarizeData.resultFile("RelinquishedCar," + carChangeCounter[1]);
        LOGGER.info(" Simulated household switched to AV" + avSwitchCounter + " (" +
                SiloUtil.rounder((100f * avSwitchCounter / hh), 0) + "% of hh)");
        SummarizeData.resultFile("SwitchedToAV," + avSwitchCounter);

        events.clear();
    }
}
