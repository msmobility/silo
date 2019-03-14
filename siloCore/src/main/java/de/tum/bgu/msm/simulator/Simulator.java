package de.tum.bgu.msm.simulator;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.models.EventModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.utils.TimeTracker;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Generates a series of events in random order
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 December 2009 in Santa Fe
 **/
public final class Simulator {

    private final static Logger logger = Logger.getLogger(Simulator.class);

    private final Multiset<Class<? extends MicroEvent>> eventCounter = HashMultiset.create();

    private final Map<Class<? extends MicroEvent>, EventModel> models = new LinkedHashMap<>();
    private final List<ModelUpdateListener> modelUpdateListeners = new ArrayList<>();

    private final List<MicroEvent> events = new ArrayList<>();
    private final TimeTracker timeTracker;

    public Simulator(TimeTracker timeTracker) {
        this.timeTracker = timeTracker;
    }

    public <T extends MicroEvent> void registerEventModel(Class<T> klass, EventModel<T> model) {
        this.models.put(klass, model);
        logger.info("Registered " + model.getClass().getSimpleName() + " for: " + klass.getSimpleName());
    }

    public void registerAnnualModel(ModelUpdateListener model) {
        this.modelUpdateListeners.add(model);
        logger.info("Registered annual model " + model.getClass().getSimpleName());
    }

    public void setup() {
        logger.info("  Setting up annual models");
        timeTracker.reset();
        for(ModelUpdateListener modelUpdateListener : modelUpdateListeners) {
            modelUpdateListener.setup();
            timeTracker.recordAndReset("SetupOf" + modelUpdateListener.getClass().getSimpleName());
        }
        logger.info("  Setting up event models");
        for(@SuppressWarnings("unchecked") EventModel<? extends MicroEvent> model: models.values()) {
            model.setup();
            timeTracker.recordAndReset("SetupOf" + model.getClass().getSimpleName());
        }
    }

    public void simulate(int year) {
        prepareYear(year);
        processEvents();
        finishYear(year);
    }

    private void prepareYear(int year) {
        timeTracker.reset();
        logger.info("  Running annual models");
        for(ModelUpdateListener modelUpdateListener : modelUpdateListeners) {
            modelUpdateListener.prepareYear(year);
            timeTracker.recordAndReset("PreparationFor" + modelUpdateListener.getClass().getSimpleName());
        }
        logger.info("  Preparing and creating events");
        for(@SuppressWarnings("unchecked") EventModel<? extends MicroEvent> model: models.values()) {
            model.prepareYear(year);
            events.addAll(model.getEventsForCurrentYear(year));
            timeTracker.recordAndReset("PreparationFor" + model.getClass().getSimpleName());
        }
        logger.info("  Created " + events.size() + " events to simulate.");
        logger.info("  Shuffling events...");
        Collections.shuffle(events, SiloUtil.getRandomObject());
        eventCounter.clear();
    }

    private void processEvents() {
        logger.info("  Processing events...");
        for (MicroEvent e: events) {
            timeTracker.reset();
            Class<? extends MicroEvent> klass= e.getClass();
            //unchecked is justified here, as
            //<T extends Event> void registerEventModel(Class<T> klass, EventModel<T> model)
            // checks for the right type of model handlers
            @SuppressWarnings("unchecked")
            boolean success = this.models.get(klass).handleEvent(e);
            if(success) {
                eventCounter.add(klass);
            }
            timeTracker.record(klass.getSimpleName());
        }
    }

    private void finishYear(int year) {
        for(ModelUpdateListener modelUpdateListener : modelUpdateListeners) {
            modelUpdateListener.endYear(year);
        }
        for(EventModel model: models.values()) {
            model.endYear(year);
        }
        SummarizeData.resultFile("Count of simulated events");
        logger.info("Simulated " + eventCounter.size() + " successful events in total.");
        for(Class<? extends MicroEvent> event: eventCounter.elementSet()) {
            final int count = eventCounter.count(event);
            SummarizeData.resultFile(event.getSimpleName() + "," + count);
            logger.info("Simulated " + event.getSimpleName() + ": " + count);
        }
        events.clear();
    }

    public void endSimulation() {
        for(ModelUpdateListener modelUpdateListener : modelUpdateListeners) {
            modelUpdateListener.endSimulation();
        }
        for(EventModel model: models.values()) {
            model.endSimulation();
        }
    }
}
