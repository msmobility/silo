package de.tum.bgu.msm.events;

import cern.colt.Timer;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.models.realEstate.ConstructionModel;
import de.tum.bgu.msm.models.relocation.InOutMigration;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Generates a series of events in random order
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 December 2009 in Santa Fe
 **/

public class EventManager {

    private final static Logger LOGGER = Logger.getLogger(EventManager.class);

    private final SiloDataContainer dataContainer;
    private final SiloModelContainer modelContainer;
    private final long[][] timeCounter;
    private static Multiset<EventType> eventCounter = EnumMultiset.create(EventType.class);
    private final List<EventHandler> handlers = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();
    private final Timer timer = new Timer().start();

    public EventManager(SiloDataContainer dataContainer, SiloModelContainer modelContainer, long[][] timeCounter) {
        this.dataContainer = dataContainer;
        this.modelContainer = modelContainer;
        this.timeCounter = timeCounter;
    }

    public void registerEventHandler(EventHandler handler) {
        this.handlers.add(handler);
    }

    public void simulateEvents(int year) {
        createListOfEvents(year);
        processEvents();
    }

    private void createListOfEvents(int year) {
        LOGGER.info("  Simulating events");
        events.clear();
        final Collection<Person> persons = dataContainer.getHouseholdData().getPersons();

        // create person events
        for (Person per : persons) {
            int id = per.getId();
            // Birthday
            if (EventRules.ruleBirthday(per)) {
                events.add(new EventImpl(EventType.BIRTHDAY, id, year));
            }
            // Death
            if (EventRules.ruleDeath(per)) {
                events.add(new EventImpl(EventType.CHECK_DEATH, id, year));
            }
            // Birth
            if (EventRules.ruleGiveBirth(per)) {
                events.add(new EventImpl(EventType.CHECK_BIRTH, id, year));
            }
            // Leave parental household
            if (EventRules.ruleLeaveParHousehold(per)) {
                events.add(new EventImpl(EventType.CHECK_LEAVE_PARENT_HH, id, year));
            }
            // Divorce
            if (EventRules.ruleGetDivorced(per)) {
                events.add(new EventImpl(EventType.CHECK_DIVORCE, id, year));
            }
            // Change school
            if (EventRules.ruleChangeSchoolUniversity(per)) {
                events.add(new EventImpl(EventType.CHECK_SCHOOL_UNIV, id, year));
            }
            // Change drivers license
            if (EventRules.ruleChangeDriversLicense(per)) {
                events.add(new EventImpl(EventType.CHECK_DRIVERS_LICENSE, id, year));
            }
        }

        // wedding events
        timer.reset();
        final List<Event> marriageEvents = modelContainer.getMardiv().selectCouplesToGetMarriedThisYear(persons, year);
        timeCounter[EventType.values().length + 5][year] += timer.millis();
        events.addAll(marriageEvents);

        // employment events
        if (EventRules.ruleStartNewJob()) {
            for (int ppId : HouseholdDataManager.getStartNewJobPersonIds()) {
                events.add(new EventImpl(EventType.FIND_NEW_JOB, ppId, year));
            }
        }

        if (EventRules.ruleQuitJob()) {
            for (int ppId : HouseholdDataManager.getQuitJobPersonIds()) {
                events.add(new EventImpl(EventType.QUIT_JOB, ppId, year));
            }
        }

        // create household events
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        for (Household hh : householdData.getHouseholds()) {
            if (EventRules.ruleHouseholdMove(hh)) {
                int id = hh.getId();
                events.add(new EventImpl(EventType.HOUSEHOLD_MOVE, id, year));
            }
        }

        if (EventRules.ruleOutmigrate()) {
            for (int hhId : InOutMigration.outMigratingHhId) {
                if (EventRules.ruleOutmigrate(householdData.getHouseholdFromId(hhId))) {
                    events.add(new EventImpl(EventType.OUT_MIGRATION, hhId, year));
                }
            }
        }

        if (EventRules.ruleInmigrate()) {
            for (int hhId : InOutMigration.inmigratingHhId) {
                events.add(new EventImpl(EventType.INMIGRATION, hhId, year));
            }
        }

        // update dwelling events
        Collection<Dwelling> dwellings = dataContainer.getRealEstateData().getDwellings();
        for (Dwelling dd : dwellings) {
            int id = dd.getId();
            // renovate dwelling or deteriorate
            if (EventRules.ruleChangeDwellingQuality(dd)) {
                events.add(new EventImpl(EventType.DD_CHANGE_QUAL, id, year));
            }
            // demolish
            if (EventRules.ruleDemolishDwelling(dd)) {
                events.add(new EventImpl(EventType.DD_DEMOLITION, id, year));
            }
        }
        // build new dwellings
        if (EventRules.ruleBuildDwelling()) {
            for (int constructionCase : ConstructionModel.listOfPlannedConstructions) {
                events.add(new EventImpl(EventType.DD_CONSTRUCTION, constructionCase, year));
            }
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
//            if (event[1] == SiloUtil.trackPp || event[1] == SiloUtil.trackHh || event[1] == SiloUtil.trackDd)
//                SiloUtil.trackWriter.println("Check event " + EventType.values()[event[0]] + " for pp/hh/dd " +
//                        event[1]);

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
