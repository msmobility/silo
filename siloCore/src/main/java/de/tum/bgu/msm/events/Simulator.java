package de.tum.bgu.msm.events;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.models.AnnualModel;
import de.tum.bgu.msm.models.EventModel;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.utils.TimeTracker;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Generates a series of events in random order
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 December 2009 in Santa Fe
 **/
public final class Simulator {

    private final static Logger LOGGER = Logger.getLogger(Simulator.class);

    private final Multiset<Class<? extends MicroEvent>> eventCounter = HashMultiset.create();

    private final Map<Class<? extends MicroEvent>, EventModel> models = new LinkedHashMap<>();
    private final List<AnnualModel> annualModels = new ArrayList<>();

    private final List<MicroEvent> events = new ArrayList<>();
    private final TimeTracker timeTracker;

    public Simulator(TimeTracker timeTracker) {
        this.timeTracker = timeTracker;
    }

    public <T extends MicroEvent> void registerEventModel(Class<T> klass, EventModel<T> model) {
        this.models.put(klass, model);
        LOGGER.info("Registered " + model.getClass().getSimpleName() + " for: " + klass.getSimpleName());
    }

    public void registerAnnualModel(AnnualModel model) {
        this.annualModels.add(model);
        LOGGER.info("Registered annual model " + model.getClass().getSimpleName());
    }

    public void simulate(int year) {
        prepareYear(year);
        processEvents();
//        finishYear(year);
    }

    private void prepareYear(int year) {
        LOGGER.info("  Creating events");
        for(@SuppressWarnings("unchecked") EventModel<? extends MicroEvent> model: models.values()) {
            timeTracker.reset();
            events.addAll(model.prepareYear(year));
            timeTracker.record("PreparationFor" + model.getClass().getSimpleName());
        }
        LOGGER.info("  Created " + events.size() + " events to simulate.");
        LOGGER.info("  Shuffling events...");
        Collections.shuffle(events, SiloUtil.getRandomObject());
        eventCounter.clear();
    }

    private void processEvents() {
        LOGGER.info("  Processing events...");
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

    public void finishYear(int year, int[] carChangeCounter, int avSwitchCounter, SiloDataContainer dataContainer) {
        for(AnnualModel annualModel: annualModels) {
            annualModel.finishYear(year);
        }
        for(EventModel model: models.values()) {
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
