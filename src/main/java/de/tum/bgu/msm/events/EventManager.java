package de.tum.bgu.msm.events;

import cern.colt.Timer;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.models.relocation.InOutMigration;
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
    private static Multiset<EventType> eventCounter = EnumMultiset.create(EventType.class);

    private final List<EventHandler> handlers = new ArrayList<>();
    private final List<EventCreator> creators = new ArrayList<>();

    private final List<Event> events = new ArrayList<>();
    private final Timer timer = new Timer().start();

    public EventManager(long[][] timeCounter) {
        this.timeCounter = timeCounter;
    }

    public void registerEventHandler(EventHandler handler) {
        this.handlers.add(handler);
    }

    public void registerEventCreator(EventCreator creator) {
        this.creators.add(creator);
    }

    public void simulateEvents(int year) {
        createEvents(year);
        processEvents();
    }

    private void createEvents(int year) {
        LOGGER.info("  Simulating events");
        events.clear();

        for(EventCreator creator: creators) {
            events.addAll(creator.createEvents(year));
        }

        LOGGER.info("  Created " + events.size() + " events to simulate");

        LOGGER.info("  Events are randomized");
        Collections.shuffle(events);

        // reset event counter
        eventCounter.clear();
    }

    private void processEvents() {
        for (Event event : events) {
            timer.reset();
            for(EventHandler handler: handlers) {
                handler.handleEvent(event);
            }
            timeCounter[event.getType().ordinal()][event.getYear()] += timer.millis();
        }
    }

    public static void countEvent(EventType et) {
        eventCounter.add(et);
    }


    public static void countEvent(EventType et, int amount) {
        eventCounter.add(et, amount);
    }


    public static void logEvents(int[] carChangeCounter, SiloDataContainer dataContainer) {
        // log number of events to screen and result file

        float pp = dataContainer.getHouseholdData().getPersonCount();
        float hh = dataContainer.getHouseholdData().getHouseholds().size();
        float dd = dataContainer.getRealEstateData().getDwellings().size();
        SummarizeData.resultFile("Count of simulated events");
        int birthday = eventCounter.count(EventType.BIRTHDAY);
        LOGGER.info("  Simulated birthdays:         " + birthday + " (" +
                SiloUtil.rounder((100f * birthday / pp), 1) + "% of pp)");
        SummarizeData.resultFile("Birthdays," + birthday);
        int births = eventCounter.count(EventType.CHECK_BIRTH);
        LOGGER.info("  Simulated births:            " + births + " (" +
                SiloUtil.rounder((100f * births / pp), 1) + "% of pp)");
        SummarizeData.resultFile("Births," + births);
        int deaths = eventCounter.count(EventType.CHECK_DEATH);
        LOGGER.info("  Simulated deaths:            " + deaths + " (" +
                SiloUtil.rounder((100f * deaths / pp), 1) + "% of pp)");
        SummarizeData.resultFile("Deaths," + deaths);
        int marriages = eventCounter.count(EventType.CHECK_MARRIAGE);
        LOGGER.info("  Simulated marriages          " + marriages + " (" +
                SiloUtil.rounder((100f * marriages / hh), 1) + "% of hh)");
        SummarizeData.resultFile("Marriages," + marriages);
        int divorces = eventCounter.count(EventType.CHECK_DIVORCE);
        LOGGER.info("  Simulated divorces:          " + divorces + " (" +
                SiloUtil.rounder((100f * divorces / hh), 1) + "% of hh)");
        SummarizeData.resultFile("Divorces," + divorces);
        int lph = eventCounter.count(EventType.CHECK_LEAVE_PARENT_HH);
        LOGGER.info("  Simulated leave parental hh: " + lph + " (" +
                SiloUtil.rounder((100f * lph / hh), 1) + "% of hh)");
        SummarizeData.resultFile("LeaveParentalHH," + lph);
        int suc = eventCounter.count(EventType.CHECK_SCHOOL_UNIV);
        LOGGER.info("  Simulated change of school or university: " + suc + " (" +
                SiloUtil.rounder((100f * suc / pp), 1) + "% of pp)");
        SummarizeData.resultFile("ChangeSchoolUniv," + suc);
        int dlc = eventCounter.count(EventType.CHECK_DRIVERS_LICENSE);
        LOGGER.info("  Simulated change of drivers license: " + dlc + " (" +
                SiloUtil.rounder((100f * dlc / pp), 1) + "% of pp)");
        SummarizeData.resultFile("DriversLicense," + dlc);
        int sj = eventCounter.count(EventType.FIND_NEW_JOB);
        LOGGER.info("  Simulated start new job:     " + sj + " (" +
                SiloUtil.rounder((100f * sj / pp), 1) + "% of pp)");
        SummarizeData.resultFile("StartNewJob," + sj);
        int qj = eventCounter.count(EventType.QUIT_JOB);
        LOGGER.info("  Simulated leaving jobs:      " + qj + " (" +
                SiloUtil.rounder((100f * qj / pp), 1) + "% of pp)");
        SummarizeData.resultFile("QuitJob," + qj);
        int moves = eventCounter.count(EventType.HOUSEHOLD_MOVE);
        LOGGER.info("  Simulated household moves:   " + moves + " (" +
                SiloUtil.rounder((100f * moves / hh), 0) + "% of hh)");
        SummarizeData.resultFile("Moves," + moves);
        LOGGER.info("  Simulated household added a car" + carChangeCounter[0] + " (" +
                SiloUtil.rounder((100f * carChangeCounter[0] / hh), 0) + "% of hh)");
        SummarizeData.resultFile("AddedCar," + carChangeCounter[0]);
        LOGGER.info("  Simulated household relinquished a car" + carChangeCounter[1] + " (" +
                SiloUtil.rounder((100f * carChangeCounter[1] / hh), 0) + "% of hh)");
        SummarizeData.resultFile("RelinquishedCar," + carChangeCounter[1]);
        int inmigration = eventCounter.count(EventType.INMIGRATION);
        LOGGER.info("  Simulated inmigrated hh:     " + inmigration + " (" +
                SiloUtil.rounder((100f * inmigration / hh), 0) + "% of hh)");
        SummarizeData.resultFile("InmigrantsHH," + inmigration);
        SummarizeData.resultFile("InmigrantsPP," + InOutMigration.inMigrationPPCounter);
        int outmigration = eventCounter.count(EventType.OUT_MIGRATION);
        LOGGER.info("  Simulated outmigrated hh:    " + outmigration + " (" +
                SiloUtil.rounder((100f * outmigration / hh), 0) + "% of hh)");
        SummarizeData.resultFile("OutmigrantsHH," + outmigration);
        SummarizeData.resultFile("OutmigrantsPP," + InOutMigration.outMigrationPPCounter);
        int renovate = eventCounter.count(EventType.DD_CHANGE_QUAL);
        LOGGER.info("  Simulated up-/downgrade dd:  " + renovate + " (" +
                SiloUtil.rounder((100f * renovate / dd), 0) + "% of dd)");
        SummarizeData.resultFile("UpDowngradeDD," + renovate);
        int demolitions = eventCounter.count(EventType.DD_DEMOLITION);
        LOGGER.info("  Simulated demolition of dd:  " + demolitions + " (" +
                SiloUtil.rounder((100f * demolitions / dd), 0) + "% of dd)");
        SummarizeData.resultFile("Demolition," + demolitions);
        int construction = eventCounter.count(EventType.DD_CONSTRUCTION);
        LOGGER.info("  Simulated construction of dd:  " + construction + " (" +
                SiloUtil.rounder((100f * construction / dd), 0) + "% of dd)");
        SummarizeData.resultFile("Construction," + construction);
    }
}
