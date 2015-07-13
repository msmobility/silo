package edu.umd.ncsg.relocation;

import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.autoOwnership.AutoOwnershipModel;
import edu.umd.ncsg.data.*;
import edu.umd.ncsg.demography.ChangeEmploymentModel;
import edu.umd.ncsg.events.IssueCounter;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import edu.umd.ncsg.SiloModel;
import edu.umd.ncsg.events.EventRules;
import edu.umd.ncsg.events.EventTypes;
import edu.umd.ncsg.events.EventManager;
import com.pb.common.util.ResourceUtil;
import com.pb.common.datafile.TableDataSet;

/**
 * Adds exogenously given inmigrating and outmigrating households
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 15 January 2010 in Albuquerque
 **/

public class InOutMigration {

    static Logger logger = Logger.getLogger(InOutMigration.class);
    protected static final String PROPERTIES_POPULATION_CONTROL_METHOD     = "population.control.total";
    protected static final String PROPERTIES_POPULATION_CONTROL_TOTAL_FILE = "total.population.control.total.file";
    protected static final String PROPERTIES_INMIGRATION_OUTMIGRATION_FILE = "inmigration.outmigration.file";
    private String populationControlMethod;
    private TableDataSet tblInOutMigration;
    private TableDataSet tblPopulationTarget;
    private HashMap<Integer, int[]> inmigratingHhData;
    public static int[] outMigratingHhId;
    public static int[] inmigratingHhId;
    public static int outMigrationPPCounter;
    public static int inMigrationPPCounter;


    public InOutMigration(ResourceBundle rb) {
        // Constructor

        populationControlMethod = rb.getString(PROPERTIES_POPULATION_CONTROL_METHOD);
        if (populationControlMethod.equalsIgnoreCase("population")) {
            String fileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_POPULATION_CONTROL_TOTAL_FILE);
            tblPopulationTarget = SiloUtil.readCSVfile(fileName);
            tblPopulationTarget.buildIndex(tblPopulationTarget.getColumnPosition("Year"));
        } else if (populationControlMethod.equalsIgnoreCase("migration")) {
            String fileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_INMIGRATION_OUTMIGRATION_FILE);
            tblInOutMigration = SiloUtil.readCSVfile(fileName);
            tblInOutMigration.buildIndex(tblInOutMigration.getColumnPosition("Year"));
        } else {
            logger.error("Unknown property found for population.control.total, set to population or migration");
            System.exit(0);
        }
    }


    public void setupInOutMigration(int year) {
        // prepare data for inmigration and outmigration

        if (!EventRules.ruleInmigrate() && !EventRules.ruleOutmigrate()) return;
        logger.info("  Selecting outmigrants and creating inmigrants for the year " + year);

        // create outmigrants
        int outmigrants = 0;
        if (populationControlMethod.equalsIgnoreCase("migration")) {
            outmigrants = (int) tblInOutMigration.getIndexedValueAt(year, "Outmigration");
        } else {
            int currentPopulation = Household.getTotalPopulation();
            int target = (int) tblPopulationTarget.getIndexedValueAt(year, "Population");
            if (target < currentPopulation) {
                outmigrants = currentPopulation - target;
            }
        }
        Household[] hhs = Household.getHouseholdArray();
        ArrayList<Integer> selectedOutmigrationHh = new ArrayList<>();
        int createdOutMigrants = 0;
        if (outmigrants > 0) do {
            int selected = (int) (hhs.length * SiloModel.rand.nextDouble());
            selectedOutmigrationHh.add(hhs[selected].getId());
            createdOutMigrants += hhs[selected].getHhSize();
        } while (createdOutMigrants < outmigrants);
        outMigratingHhId = SiloUtil.convertIntegerArrayListToArray(selectedOutmigrationHh);

        // create inmigrants
        int inmigrants = 0;
        if (populationControlMethod.equalsIgnoreCase("migration")) {
            inmigrants = (int) tblInOutMigration.getIndexedValueAt(year, "Inmigration");
        } else {
            int currentPopulation = Household.getTotalPopulation();
            int target = (int) tblPopulationTarget.getIndexedValueAt(year, "Population");
            if (target > currentPopulation) {
                inmigrants = target - currentPopulation;
            }
        }
        int createdInmigrants = 0;
        inmigratingHhData = new HashMap<>();
        ArrayList<Integer> inHhIdArray = new ArrayList<>();
        if (inmigrants > 0) do {
            int[] inData = new int[31];
            // 0: hhSize, for p1 through p10 (1: age p1, 2: gender p1, 3: race p1, 4: occupation p1, 5: income p1, 6: workplace)
            // if this order in inData[] is changed, adjust method  "public void inmigrateHh (int hhId)" as well
            int selected = (int) (hhs.length * SiloModel.rand.nextDouble());
            inData[0] = Math.min(hhs[selected].getHhSize(), 5);
            int k = 0;
            for (Person pp: hhs[selected].getPersons()) {
                if (k + 6 > inData.length) continue;  // for household size 11+, only first ten persons are recorded
                inData[1 + k] = pp.getAge();
                inData[2 + k] = pp.getGender();
                inData[3 + k] = pp.getRace().ordinal();
                inData[4 + k] = pp.getOccupation();
                inData[5 + k] = pp.getIncome();
                // todo: Keep person role in household
                // todo: imData[6+k] is not used, as inmigrant certainly will occupy different workplace. Remove or replace with other person attribute
                inData[6 + k] = pp.getWorkplace();
                k += 6;
            }
            int hhId = HouseholdDataManager.getNextHouseholdId();
            inHhIdArray.add(hhId);
            inmigratingHhData.put(hhId, inData);  // create new hhId for inmigrating households and save in HashMap
            createdInmigrants += hhs[selected].getHhSize();
        } while (createdInmigrants < inmigrants);
        inmigratingHhId = SiloUtil.convertIntegerArrayListToArray(inHhIdArray);

        // set person counter to 0
        outMigrationPPCounter = 0;
        inMigrationPPCounter = 0;
    }


    public void inmigrateHh(int hhId, MovesModel move, ChangeEmploymentModel changeEmployment, AutoOwnershipModel aoModel) {
        // Inmigrate household with hhId from HashMap inmigratingHhData<Integer, int[]>

        if (!EventRules.ruleInmigrate()) return;
        int[] imData = inmigratingHhData.get(hhId);
        int hhSize = imData[0];
        int hhInc = 0;
        int k = 0;
        for (int i = 1; i <= hhSize; i++) {
            hhInc += imData[5 + k];
            k += 6;
        }

        Household hh = new Household(hhId, -1, -1, hhSize, 0);
        k = 0;
        for (int i = 1; i <= hhSize; i++) {
            Race ppRace = Race.values()[imData[3 + k]];
            Person per = new Person(HouseholdDataManager.getNextPersonId(), hhId, imData[1 + k], imData[2 + k],
                    ppRace, imData[4 + k], -1, imData[5 + k]);
            k += 6;
            hh.addPersonForInitialSetup(per);
        }
        // Searching for employment has to be in a separate loop from setting up all persons, as finding a job will change the household income and household type, which can only be calculated after all persons are set up.
        for (Person per: hh.getPersons()) {
            if (per.getOccupation() == 1) {
                boolean success = changeEmployment.findNewJob(per.getId());
                if (!success) per.setOccupation(2);
            }
        }
        hh.setType();
        hh.setHouseholdRace();
        HouseholdDataManager.findMarriedCouple(hh);
        HouseholdDataManager.defineUnmarriedPersons(hh);
        int newDdId = move.searchForNewDwelling(hh.getPersons());
        if (newDdId > 0) {
            move.moveHousehold(hh, -1, newDdId);
        } else {
            IssueCounter.countLackOfDwellingFailedInmigration();
            outMigrateHh(hhId, true);
            return;
        }
        aoModel.simulateAutoOwnership(hh);
        EventManager.countEvent(EventTypes.inmigration);
        inMigrationPPCounter += hh.getHhSize();
        if (hhId == SiloUtil.trackHh) SiloUtil.trackWriter.println("Household " + hhId + " inmigrated.");
        for (Person pp: Household.getHouseholdFromId(hhId).getPersons())
            if (pp.getId() == SiloUtil.trackPp) SiloUtil.trackWriter.println(" Person " + pp.getId() + " inmigrated.");
    }


    public void outMigrateHh (int hhId, boolean overwriteEventRules) {
        // Household with ID hhId out migrates
        Household hh = Household.getHouseholdFromId(hhId);
        if (!EventRules.ruleOutmigrate(hh) && !overwriteEventRules) return;
        EventManager.countEvent(EventTypes.outMigration);
        outMigrationPPCounter += hh.getHhSize();
        if (hhId == SiloUtil.trackHh) SiloUtil.trackWriter.println("Household " + hhId + " outmigrated.");
        for (Person pp: hh.getPersons()) {
            if (pp.getWorkplace() > 0) pp.quitJob(true);
            Person.removePerson(pp.getId());
            if (pp.getId() == SiloUtil.trackPp) SiloUtil.trackWriter.println(" Person " + pp.getId() + " outmigrated.");
        }
        HouseholdDataManager.removeHousehold(hhId);
    }

}
