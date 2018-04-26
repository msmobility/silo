package de.tum.bgu.msm.events;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import com.pb.common.util.IndexSort;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.models.realEstate.ConstructionModel;
import de.tum.bgu.msm.models.relocation.InOutMigration;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generates a series of events in random order
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 December 2009 in Santa Fe
 **/

public class EventManager {

    private static Logger logger = Logger.getLogger(EventManager.class);

    private SiloDataContainer dataContainer;
    private List<int[]> events;
    private int randomEventOrder[];
    private int posInArray;
    private static Multiset<EventTypes> eventCounter = EnumMultiset.create(EventTypes.class);

    public EventManager (SiloDataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }


    public void createListOfEvents (List<Couple> plannedMarriages) {
        // create an array list that contains all land use events

        events = new ArrayList<>();
        Collection<Person> persons = dataContainer.getHouseholdData().getPersons();
        int numEvents = 0;

        // create person events
        for (Person per: persons) {
            int id = per.getId();
            // Birthday
            if (EventRules.ruleBirthday(per)) {
                events.add(new int[]{EventTypes.BIRTHDAY.ordinal(), id});
                numEvents ++;
            }
            // Death
            if (EventRules.ruleDeath(per)) {
                events.add(new int[]{EventTypes.CHECK_DEATH.ordinal(), id});
                numEvents ++;
            }
            // Birth
            if (EventRules.ruleGiveBirth(per)) {
                events.add(new int[]{EventTypes.CHECK_BIRTH.ordinal(), id});
                numEvents ++;
            }
            // Leave parental household
            if (EventRules.ruleLeaveParHousehold(per)) {
                events.add(new int[]{EventTypes.CHECK_LEAVE_PARENT_HH.ordinal(), id});
                numEvents ++;
            }
            // Divorce
            if (EventRules.ruleGetDivorced(per)) {
                events.add(new int[]{EventTypes.CHECK_DIVORCE.ordinal(), id});
                numEvents++;
            }
            // Change school
            if (EventRules.ruleChangeSchoolUniversity(per)) {
                events.add(new int[]{EventTypes.CHECK_SCHOOL_UNIV.ordinal(), id});
                numEvents++;
            }
            // Change drivers license
            if (EventRules.ruleChangeDriversLicense(per)) {
                events.add(new int[]{EventTypes.CHECK_DRIVERS_LICENSE.ordinal(), id});
                numEvents++;
            }
        }


        // wedding events
        for (Couple couple: plannedMarriages) {
            events.add(new int[]{EventTypes.CHECK_MARRIAGE.ordinal(),
                    couple.getPartner1().getId(), couple.getPartner2().getId()});
            numEvents++;
        }

        // employment events
        if (EventRules.ruleStartNewJob()) {
            for (int ppId: HouseholdDataManager.getStartNewJobPersonIds()) {
                events.add(new int[]{EventTypes.FIND_NEW_JOB.ordinal(), ppId});
                numEvents++;
            }
        }

        if (EventRules.ruleQuitJob()) {
            for (int ppId: HouseholdDataManager.getQuitJobPersonIds()) {
                events.add(new int[]{EventTypes.QUIT_JOB.ordinal(), ppId});
                numEvents++;
            }
        }


        // create household events
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        for (Household hh: householdData.getHouseholds()) {
            if (EventRules.ruleHouseholdMove(hh)) {
                int id = hh.getId();
                events.add(new int[]{EventTypes.HOUSEHOLD_MOVE.ordinal(), id});
                numEvents++;
            }
        }

        if (EventRules.ruleOutmigrate()) {
            for (int hhId: InOutMigration.outMigratingHhId) {
                if (EventRules.ruleOutmigrate(householdData.getHouseholdFromId(hhId))) {
                    events.add(new int[]{EventTypes.OUT_MIGRATION.ordinal(), hhId});
                    numEvents++;
                }
            }
        }

        if (EventRules.ruleInmigrate()) {
            for (int hhId: InOutMigration.inmigratingHhId) {
                events.add(new int[]{EventTypes.INMIGRATION.ordinal(), hhId});
                numEvents++;
            }
        }

        // update dwelling events
        Collection<Dwelling> dwellings = dataContainer.getRealEstateData().getDwellings();
        for (Dwelling dd: dwellings) {
            int id = dd.getId();
            // renovate dwelling or deteriorate
            if (EventRules.ruleChangeDwellingQuality(dd)) {
                events.add(new int[]{EventTypes.DD_CHANGE_QUAL.ordinal(), id});
                numEvents++;
            }
            // demolish
            if (EventRules.ruleDemolishDwelling(dd)) {
                events.add(new int[]{EventTypes.DD_DEMOLITION.ordinal(), id});
                numEvents++;
            }
        }
        // build new dwellings
        if (EventRules.ruleBuildDwelling()) {
            for (int constructionCase: ConstructionModel.listOfPlannedConstructions) {
                events.add(new int[]{EventTypes.DD_CONSTRUCTION.ordinal(), constructionCase});
                numEvents++;
            }
        }

        logger.info("  Created " + numEvents + " events to simulate");
        logger.info("  Events are randomized");
        int randomNumArray[] = new int[numEvents];
        for (int i = 0; i < numEvents; i++) {
            randomNumArray[i] = (int) (SiloUtil.getRandomNumberAsDouble() * (float) numEvents * 10f);
        }
        randomEventOrder = IndexSort.indexSort(randomNumArray);
        posInArray = 0;

        // reset event counter
        eventCounter.clear();
    }


    public static void countEvent (EventTypes et) {
       eventCounter.add(et);
    }


    public static void countEvent (EventTypes et, int amount) {
        eventCounter.add(et, amount);
    }


    public static void logEvents(int [] carChangeCounter, SiloDataContainer dataContainer) {
        // log number of events to screen and result file

        float pp = dataContainer.getHouseholdData().getPersonCount();
        float hh = dataContainer.getHouseholdData().getHouseholds().size();
        float dd = dataContainer.getRealEstateData().getDwellings().size();
        SummarizeData.resultFile("Count of simulated events");
        int birthday = eventCounter.count(EventTypes.BIRTHDAY);
        logger.info("  Simulated birthdays:         " + birthday + " (" +
                SiloUtil.rounder((100f * birthday / pp), 1) + "% of pp)");
        SummarizeData.resultFile("Birthdays,"+birthday);
        int births = eventCounter.count(EventTypes.CHECK_BIRTH);
        logger.info("  Simulated births:            " + births + " (" +
                SiloUtil.rounder((100f * births / pp), 1) + "% of pp)");
        SummarizeData.resultFile("Births,"+births);
        int deaths = eventCounter.count(EventTypes.CHECK_DEATH);
        logger.info("  Simulated deaths:            " + deaths + " (" +
                SiloUtil.rounder((100f * deaths / pp), 1) + "% of pp)");
        SummarizeData.resultFile("Deaths,"+deaths);
        int marriages = eventCounter.count(EventTypes.CHECK_MARRIAGE);
        logger.info("  Simulated marriages          " + marriages + " (" +
                SiloUtil.rounder((100f * marriages / hh), 1) + "% of hh)");
        SummarizeData.resultFile("Marriages,"+marriages);
        int divorces = eventCounter.count(EventTypes.CHECK_DIVORCE);
        logger.info("  Simulated divorces:          " + divorces + " (" +
                SiloUtil.rounder((100f * divorces / hh), 1) + "% of hh)");
        SummarizeData.resultFile("Divorces,"+divorces);
        int lph = eventCounter.count(EventTypes.CHECK_LEAVE_PARENT_HH);
        logger.info("  Simulated leave parental hh: " + lph + " (" +
                SiloUtil.rounder((100f * lph / hh), 1) + "% of hh)");
        SummarizeData.resultFile("LeaveParentalHH,"+lph);
        int suc = eventCounter.count(EventTypes.CHECK_SCHOOL_UNIV);
        logger.info("  Simulated change of school or university: " + suc + " (" +
                SiloUtil.rounder((100f * suc / pp), 1) + "% of pp)");
        SummarizeData.resultFile("ChangeSchoolUniv,"+suc);
        int dlc = eventCounter.count(EventTypes.CHECK_DRIVERS_LICENSE);
        logger.info("  Simulated change of drivers license: " + dlc + " (" +
                SiloUtil.rounder((100f * dlc / pp), 1) + "% of pp)");
        SummarizeData.resultFile("DriversLicense,"+dlc);
        int sj = eventCounter.count(EventTypes.FIND_NEW_JOB);
        logger.info("  Simulated start new job:     " + sj + " (" +
                SiloUtil.rounder((100f * sj / pp), 1) + "% of pp)");
        SummarizeData.resultFile("StartNewJob,"+sj);
        int qj = eventCounter.count(EventTypes.QUIT_JOB);
        logger.info("  Simulated leaving jobs:      " + qj + " (" +
                SiloUtil.rounder((100f * qj / pp), 1) + "% of pp)");
        SummarizeData.resultFile("QuitJob,"+qj);
        int moves = eventCounter.count(EventTypes.HOUSEHOLD_MOVE);
        logger.info("  Simulated household moves:   " + moves + " (" +
                SiloUtil.rounder((100f * moves / hh), 0) + "% of hh)");
        SummarizeData.resultFile("Moves,"+moves);
        logger.info("  Simulated household added a car" + carChangeCounter[0] + " (" +
                SiloUtil.rounder((100f * carChangeCounter[0] / hh), 0) + "% of hh)");
        SummarizeData.resultFile("AddedCar,"+carChangeCounter[0]);
        logger.info("  Simulated household relinquished a car" + carChangeCounter[1] + " (" +
                SiloUtil.rounder((100f * carChangeCounter[1] / hh), 0) + "% of hh)");
        SummarizeData.resultFile("RelinquishedCar,"+carChangeCounter[1]);
        int inmigration = eventCounter.count(EventTypes.INMIGRATION);
        logger.info("  Simulated inmigrated hh:     " + inmigration + " (" +
                SiloUtil.rounder((100f * inmigration / hh), 0) + "% of hh)");
        SummarizeData.resultFile("InmigrantsHH," + inmigration);
        SummarizeData.resultFile("InmigrantsPP," + InOutMigration.inMigrationPPCounter);
        int outmigration = eventCounter.count(EventTypes.OUT_MIGRATION);
        logger.info("  Simulated outmigrated hh:    " + outmigration + " (" +
                SiloUtil.rounder((100f * outmigration / hh), 0) + "% of hh)");
        SummarizeData.resultFile("OutmigrantsHH," + outmigration);
        SummarizeData.resultFile("OutmigrantsPP," + InOutMigration.outMigrationPPCounter);
        int renovate = eventCounter.count(EventTypes.DD_CHANGE_QUAL);
        logger.info("  Simulated up-/downgrade dd:  " + renovate + " (" +
                SiloUtil.rounder((100f * renovate / dd), 0) + "% of dd)");
        SummarizeData.resultFile("UpDowngradeDD,"+renovate);
        int demolitions = eventCounter.count(EventTypes.DD_DEMOLITION);
        logger.info("  Simulated demolition of dd:  " + demolitions + " (" +
                SiloUtil.rounder((100f * demolitions / dd), 0) + "% of dd)");
        SummarizeData.resultFile("Demolition,"+demolitions);
        int construction = eventCounter.count(EventTypes.DD_CONSTRUCTION);
        logger.info("  Simulated construction of dd:  " + construction + " (" +
                SiloUtil.rounder((100f * construction / dd), 0) + "% of dd)");
        SummarizeData.resultFile("Construction,"+construction);
    }


    public Integer getNumberOfEvents() {
        return events.size();
    }


    public int[] selectNextEvent() {
        // select the next event in order stored in randomEventOrder
        int[] event = events.get(randomEventOrder[posInArray]);
        posInArray++;
        return event;
    }
}
