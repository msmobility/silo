package de.tum.bgu.msm.events;

import com.pb.common.util.IndexSort;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.relocation.InOutMigration;
import de.tum.bgu.msm.realEstate.ConstructionModel;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * Generates a series of events in random order
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 December 2009 in Santa Fe
 **/

public class EventManager {

    private static Logger logger = Logger.getLogger(EventManager.class);

    private SiloDataContainer dataContainer;
    private ArrayList<Integer[]> events;
    private int randomEventOrder[];
    private int posInArray;
    private static HashMap<EventTypes, Integer> eventCounter;

    public EventManager (ResourceBundle rb, SiloDataContainer dataContainer) {
        // Constructor of EventManager
        this.dataContainer = dataContainer;
        EventRules.setUpEventRules(rb);
    }


    public void createListOfEvents (int numberOfPlannedMarriages) {
        // create an array list that contains all land use events

        events = new ArrayList<>();
        Collection<Person> persons = dataContainer.getHouseholdData().getPersons();
        int numEvents = 0;

        // create person events
        for (Person per: persons) {
            int id = per.getId();
            // Birthday
            if (EventRules.ruleBirthday(per)) {
                events.add(new Integer[]{EventTypes.birthday.ordinal(), id});
                numEvents ++;
            }
            // Death
            if (EventRules.ruleDeath(per)) {
                events.add(new Integer[]{EventTypes.checkDeath.ordinal(), id});
                numEvents ++;
            }
            // Birth
            if (EventRules.ruleGiveBirth(per)) {
                events.add(new Integer[]{EventTypes.checkBirth.ordinal(), id});
                numEvents ++;
            }
            // Leave parental household
            if (EventRules.ruleLeaveParHousehold(per)) {
                events.add(new Integer[]{EventTypes.checkLeaveParentHh.ordinal(), id});
                numEvents ++;
            }
            // Divorce
            if (EventRules.ruleGetDivorced(per)) {
                events.add(new Integer[]{EventTypes.checkDivorce.ordinal(), id});
                numEvents++;
            }
            // Change school
            if (EventRules.ruleChangeSchoolUniversity(per)) {
                events.add(new Integer[]{EventTypes.checkSchoolUniv.ordinal(), id});
                numEvents++;
            }
            // Change drivers license
            if (EventRules.ruleChangeDriversLicense(per)) {
                events.add(new Integer[]{EventTypes.checkDriversLicense.ordinal(), id});
                numEvents++;
            }
        }


        // wedding events
        for (int i = 0; i < numberOfPlannedMarriages; i++) {
            events.add(new Integer[]{EventTypes.checkMarriage.ordinal(), i});
            numEvents++;
        }


        // employment events
        if (EventRules.ruleStartNewJob()) {
            for (int ppId: HouseholdDataManager.getStartNewJobPersonIds()) {
                events.add(new Integer[]{EventTypes.findNewJob.ordinal(), ppId});
                numEvents++;
            }
        }

        if (EventRules.ruleQuitJob()) {
            for (int ppId: HouseholdDataManager.getQuitJobPersonIds()) {
                events.add(new Integer[]{EventTypes.quitJob.ordinal(), ppId});
                numEvents++;
            }
        }


        // create household events
        for (Household hh: Household.getHouseholdArray()) {
            if (EventRules.ruleHouseholdMove(hh)) {
                int id = hh.getId();
                events.add(new Integer[]{EventTypes.householdMove.ordinal(), id});
                numEvents++;
            }
        }

        if (EventRules.ruleOutmigrate()) {
            for (int hhId: InOutMigration.outMigratingHhId) {
                if (EventRules.ruleOutmigrate(Household.getHouseholdFromId(hhId))) {
                    events.add(new Integer[]{EventTypes.outMigration.ordinal(), hhId});
                    numEvents++;
                }
            }
        }

        if (EventRules.ruleInmigrate()) {
            for (int hhId: InOutMigration.inmigratingHhId) {
                events.add(new Integer[]{EventTypes.inmigration.ordinal(), hhId});
                numEvents++;
            }
        }

        // update dwelling events
        Collection<Dwelling> dwellings = dataContainer.getRealEstateData().getDwellings();
        for (Dwelling dd: dwellings) {
            int id = dd.getId();
            // renovate dwelling or deteriorate
            if (EventRules.ruleChangeDwellingQuality(dd)) {
                events.add(new Integer[]{EventTypes.ddChangeQual.ordinal(), id});
                numEvents++;
            }
            // demolish
            if (EventRules.ruleDemolishDwelling(dd)) {
                events.add(new Integer[]{EventTypes.ddDemolition.ordinal(), id});
                numEvents++;
            }
        }
        // build new dwellings
        if (EventRules.ruleBuildDwelling()) {
            for (int constructionCase: ConstructionModel.listOfPlannedConstructions) {
                events.add(new Integer[]{EventTypes.ddConstruction.ordinal(), constructionCase});
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

        // initialize event counter
        eventCounter = new HashMap<>();
        for (EventTypes et: EventTypes.values()) eventCounter.put(et, 0);
    }


    public static void countEvent (EventTypes et) {
        // add 1 to counter for EventTypes et
        int counter = eventCounter.get(et) + 1;
        eventCounter.put(et, counter);
    }


    public static void countEvent (EventTypes et, int amount) {
        // add <amount> to counter for EventTypes et
        int counter = eventCounter.get(et) + amount;
        eventCounter.put(et, counter);
    }


    public static void logEvents() {
        // log number of events to screen and result file

        float pp = Person.getPersonCount();
        float hh = Household.getHouseholdCount();
        float dd = Dwelling.getDwellingCount();
        summarizeData.resultFile("Count of simulated events");
        int birthday = eventCounter.get(EventTypes.birthday);
        logger.info("  Simulated birthdays:         " + birthday + " (" +
                SiloUtil.rounder((100f * birthday / pp), 1) + "% of pp)");
        summarizeData.resultFile("Birthdays,"+birthday);
        int births = eventCounter.get(EventTypes.checkBirth);
        logger.info("  Simulated births:            " + births + " (" +
                SiloUtil.rounder((100f * births / pp), 1) + "% of pp)");
        summarizeData.resultFile("Births,"+births);
        int deaths = eventCounter.get(EventTypes.checkDeath);
        logger.info("  Simulated deaths:            " + deaths + " (" +
                SiloUtil.rounder((100f * deaths / pp), 1) + "% of pp)");
        summarizeData.resultFile("Deaths,"+deaths);
        int marriages = eventCounter.get(EventTypes.checkMarriage);
        logger.info("  Simulated marriages          " + marriages + " (" +
                SiloUtil.rounder((100f * marriages / hh), 1) + "% of hh)");
        summarizeData.resultFile("Marriages,"+marriages);
        int divorces = eventCounter.get(EventTypes.checkDivorce);
        logger.info("  Simulated divorces:          " + divorces + " (" +
                SiloUtil.rounder((100f * divorces / hh), 1) + "% of hh)");
        summarizeData.resultFile("Divorces,"+divorces);
        int lph = eventCounter.get(EventTypes.checkLeaveParentHh);
        logger.info("  Simulated leave parental hh: " + lph + " (" +
                SiloUtil.rounder((100f * lph / hh), 1) + "% of hh)");
        summarizeData.resultFile("LeaveParentalHH,"+lph);
        int suc = eventCounter.get(EventTypes.checkSchoolUniv);
        logger.info("  Simulated change of school or university: " + suc + " (" +
                SiloUtil.rounder((100f * suc / pp), 1) + "% of pp)");
        summarizeData.resultFile("ChangeSchoolUniv,"+suc);
        int dlc = eventCounter.get(EventTypes.checkDriversLicense);
        logger.info("  Simulated change of drivers license: " + dlc + " (" +
                SiloUtil.rounder((100f * dlc / pp), 1) + "% of pp)");
        summarizeData.resultFile("ChangeDriversLicense,"+dlc);
        int sj = eventCounter.get(EventTypes.findNewJob);
        logger.info("  Simulated start new job:     " + sj + " (" +
                SiloUtil.rounder((100f * sj / pp), 1) + "% of pp)");
        summarizeData.resultFile("StartNewJob,"+sj);
        int qj = eventCounter.get(EventTypes.quitJob);
        logger.info("  Simulated leaving jobs:      " + qj + " (" +
                SiloUtil.rounder((100f * qj / pp), 1) + "% of pp)");
        summarizeData.resultFile("QuitJob,"+qj);
        int moves = eventCounter.get(EventTypes.householdMove);
        logger.info("  Simulated household moves:   " + moves + " (" +
                SiloUtil.rounder((100f * moves / hh), 0) + "% of hh)");
        summarizeData.resultFile("Moves,"+moves);
        int inmigration = eventCounter.get(EventTypes.inmigration);
        logger.info("  Simulated inmigrated hh:     " + inmigration + " (" +
                SiloUtil.rounder((100f * inmigration / hh), 0) + "% of hh)");
        summarizeData.resultFile("InmigrantsHH," + inmigration);
        summarizeData.resultFile("InmigrantsPP," + InOutMigration.inMigrationPPCounter);
        int outmigration = eventCounter.get(EventTypes.outMigration);
        logger.info("  Simulated outmigrated hh:    " + outmigration + " (" +
                SiloUtil.rounder((100f * outmigration / hh), 0) + "% of hh)");
        summarizeData.resultFile("OutmigrantsHH," + outmigration);
        summarizeData.resultFile("OutmigrantsPP," + InOutMigration.outMigrationPPCounter);
        int renovate = eventCounter.get(EventTypes.ddChangeQual);
        logger.info("  Simulated up-/downgrade dd:  " + renovate + " (" +
                SiloUtil.rounder((100f * renovate / dd), 0) + "% of dd)");
        summarizeData.resultFile("UpDowngradeDD,"+renovate);
        int demolitions = eventCounter.get(EventTypes.ddDemolition);
        logger.info("  Simulated demolition of dd:  " + demolitions + " (" +
                SiloUtil.rounder((100f * demolitions / dd), 0) + "% of dd)");
        summarizeData.resultFile("Demolition,"+demolitions);
        int construction = eventCounter.get(EventTypes.ddConstruction);
        logger.info("  Simulated construction of dd:  " + construction + " (" +
                SiloUtil.rounder((100f * construction / dd), 0) + "% of dd)");
        summarizeData.resultFile("Construction,"+construction);
    }


    public Integer getNumberOfEvents() {
        // returns size of event array
        return events.size();
    }


    public Integer[] selectNextEvent() {
        // select the next event in order stored in randomEventOrder
        Integer[] event = events.get(randomEventOrder[posInArray]);
        posInArray++;
        return event;
    }
}
