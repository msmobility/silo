package de.tum.bgu.msm.models.relocation;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.data.person.Race;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.events.MicroEventModel;
import de.tum.bgu.msm.events.impls.household.MigrationEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.autoOwnership.munich.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.demography.DriversLicense;
import de.tum.bgu.msm.models.demography.EmploymentModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Adds exogenously given inmigrating and outmigrating households
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 15 January 2010 in Albuquerque
 **/

public class InOutMigration extends AbstractModel implements MicroEventModel<MigrationEvent> {

    private final static Logger LOGGER = Logger.getLogger(InOutMigration.class);

    private final EmploymentModel employment;
    private final MovesModelI movesModel;
    private final CreateCarOwnershipModel carOwnership;
    private final DriversLicense driversLicense;
    private final PersonFactory factory;

    private String populationControlMethod;
    private TableDataSet tblInOutMigration;
    private TableDataSet tblPopulationTarget;
    private Map<Integer, int[]> inmigratingHhData;
    private int outMigrationPPCounter;
    private int inMigrationPPCounter;


    public InOutMigration(SiloDataContainer dataContainer, EmploymentModel employment, MovesModelI movesModel, CreateCarOwnershipModel carOwnership, DriversLicense driversLicense, PersonFactory factory) {
        super(dataContainer);
        this.employment = employment;
        this.movesModel = movesModel;
        this.carOwnership = carOwnership;
        this.driversLicense = driversLicense;
        this.factory = factory;
        populationControlMethod = Properties.get().moves.populationControlTotal;
        if (populationControlMethod.equalsIgnoreCase("population")) {
            String fileName = Properties.get().main.baseDirectory + Properties.get().moves.populationCOntrolTotalFile;
            tblPopulationTarget = SiloUtil.readCSVfile(fileName);
            tblPopulationTarget.buildIndex(tblPopulationTarget.getColumnPosition("Year"));
        } else if (populationControlMethod.equalsIgnoreCase("migration")) {
            String fileName = Properties.get().main.baseDirectory + Properties.get().moves.migrationFile;
            tblInOutMigration = SiloUtil.readCSVfile(fileName);
            tblInOutMigration.buildIndex(tblInOutMigration.getColumnPosition("Year"));
        } else if(populationControlMethod.equalsIgnoreCase("populationGrowthRate")) {
            tblPopulationTarget = new TableDataSet();
            int periodLength = Properties.get().main.endYear - Properties.get().main.startYear + 1;
            int[] years = new int[periodLength];
            int[] populationByYear = new int [periodLength];
            int populationBaseYear = dataContainer.getHouseholdData().getPersons().size();
            for (int i = 0; i < periodLength; i++){
                years[i] = Properties.get().main.startYear + i ;
                populationByYear[i] = (int) Math.round(populationBaseYear * Math.pow(1 + Properties.get().moves.populationGrowthRateInPercentage/100, i));
            }
            tblPopulationTarget.appendColumn(years, "Year");
            tblPopulationTarget.appendColumn(populationByYear, "Population");
            tblPopulationTarget.buildIndex(tblPopulationTarget.getColumnPosition("Year"));


        } else {
                LOGGER.error("Unknown property found for population.control.total, set to population or migration");
                System.exit(0);
        }

    }

    private boolean inmigrateHh(int hhId) {
        // Inmigrate household with hhId from HashMap inmigratingHhData<Integer, int[]>

        int[] imData = inmigratingHhData.get(hhId);
        int hhSize = imData[0];
        int hhInc = 0;
        int k = 0;
        for (int i = 1; i <= hhSize; i++) {
            hhInc += imData[5 + k];
            k += 6;
        }
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        Household hh = householdData.createHousehold(hhId, -1, 0);

        k = 0;
        for (int i = 1; i <= hhSize; i++) {
            Race ppRace = Race.values()[imData[3 + k]];
            Person per = factory.createPerson(householdData.getNextPersonId(), imData[1 + k], Gender.valueOf(imData[2 + k]),
                    ppRace, Occupation.valueOf(imData[4 + k]), -1, imData[5 + k]);
            householdData.addPerson(per);
            householdData.addPersonToHousehold(per, hh);
            k += 6;
        }
        // Searching for employment has to be in a separate loop from setting up all persons, as finding a
        // job will change the household income and household type, which can only be calculated after all
        // persons are set up.
        for (Person per : hh.getPersons()) {
            if (per.getOccupation() == Occupation.EMPLOYED) {
                employment.lookForJob(per.getId());
                if (per.getWorkplace() < 1) {
                    per.setOccupation(Occupation.UNEMPLOYED);
                }
            }
            driversLicense.checkLicenseCreation(per.getId());
        }
        hh.setType();
        hh.determineHouseholdRace();
        HouseholdDataManager.findMarriedCouple(hh);
        HouseholdDataManager.defineUnmarriedPersons(hh);
        int newDdId = movesModel.searchForNewDwelling(hh.getPersons());
        if (newDdId > 0) {
            movesModel.moveHousehold(hh, -1, newDdId);
            if (Properties.get().main.implementation == Implementation.MUNICH) {
                carOwnership.simulateCarOwnership(hh); // set initial car ownership of new household
            }
            inMigrationPPCounter += hh.getHhSize();
            if (hhId == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Household " + hhId + " inmigrated.");
            }
            for (Person pp : hh.getPersons()) {
                if (pp.getId() == SiloUtil.trackPp) {
                    SiloUtil.trackWriter.println(" Person " + pp.getId() + " inmigrated.");
                }
            }
            return true;
        } else {
            IssueCounter.countLackOfDwellingFailedInmigration();
            outMigrateHh(hhId, true);
            return false;
        }
    }


    public boolean outMigrateHh(int hhId, boolean overwriteEventRules) {
        // Household with ID hhId out migrates
        Household hh = dataContainer.getHouseholdData().getHouseholdFromId(hhId);
        if (Properties.get().eventRules.outMigration && !overwriteEventRules || hh == null) {
            return false;
        }
        outMigrationPPCounter += hh.getHhSize();
        if (hhId == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("Household " + hhId + " outmigrated.");
        }
        JobDataManager jobData = dataContainer.getJobData();
        for (Person pp : hh.getPersons()) {
            if (pp.getWorkplace() > 0) {
                jobData.quitJob(true, pp);
            }
            if (pp.getId() == SiloUtil.trackPp) {
                SiloUtil.trackWriter.println(" Person " + pp.getId() + " outmigrated.");
            }
        }
        dataContainer.getHouseholdData().removeHousehold(hhId);
        return true;
    }

    @Override
    public boolean handleEvent(MigrationEvent event) {
        MigrationEvent.Type type = event.getType();
        switch (type) {
            case IN:
                return inmigrateHh(event.getHouseholdId());
            case OUT:
                return outMigrateHh(event.getHouseholdId(), true);
            default:
                return false;
        }
    }

    @Override
    public void finishYear(int year) {
        SummarizeData.resultFile("InmigrantsPP," + inMigrationPPCounter);
        SummarizeData.resultFile("OutmigrantsPP," + outMigrationPPCounter);
    }

    @Override
    public Collection<MigrationEvent> prepareYear(int year) {
        final List<MigrationEvent> events = new ArrayList<>();

        LOGGER.info("  Selecting outmigrants and creating inmigrants for the year " + year);
        final HouseholdDataManager householdData = dataContainer.getHouseholdData();

        // create outmigrants
        int outmigrants = 0;
        if (populationControlMethod.equalsIgnoreCase("migration")) {
            outmigrants = (int) tblInOutMigration.getIndexedValueAt(year, "Outmigration");
        } else {
            int currentPopulation = householdData.getTotalPopulation();
            int target = (int) tblPopulationTarget.getIndexedValueAt(year, "Population");
            if (target < currentPopulation) {
                outmigrants = currentPopulation - target;
            }
        }
        Household[] hhs = householdData.getHouseholds().toArray(new Household[0]);
        int createdOutMigrants = 0;
        if (outmigrants > 0) {
            do {
                int selected = (int) (hhs.length * SiloUtil.getRandomNumberAsDouble());
                events.add(new MigrationEvent(hhs[selected].getId(), MigrationEvent.Type.OUT));
                createdOutMigrants += hhs[selected].getHhSize();
            } while (createdOutMigrants < outmigrants);
        }

        // create inmigrants
        int inmigrants = 0;
        if (populationControlMethod.equalsIgnoreCase("migration")) {
            inmigrants = (int) tblInOutMigration.getIndexedValueAt(year, "Inmigration");
        } else {
            int currentPopulation = householdData.getTotalPopulation();
            int target = (int) tblPopulationTarget.getIndexedValueAt(year, "Population");
            if (target > currentPopulation) {
                inmigrants = target - currentPopulation;
            }
        }
        int createdInmigrants = 0;
        inmigratingHhData = new HashMap<>();
        //TODO Refactoring the DO-WHILE needed??
        if (inmigrants > 0) do {
            int[] inData = new int[31];
            // 0: hhSize, for p1 through p10 (1: age p1, 2: gender p1, 3: race p1, 4: occupation p1, 5: income p1, 6: workplace)
            // if this order in inData[] is changed, adjust method  "public void inmigrateHh (int hhId)" as well
            int selected = (int) (hhs.length * SiloUtil.getRandomNumberAsFloat());
            inData[0] = Math.min(hhs[selected].getHhSize(), 5);
            int k = 0;
            for (Person pp : hhs[selected].getPersons()) {
                if (k + 6 > inData.length) continue;  // for household size 11+, only first ten persons are recorded
                inData[1 + k] = pp.getAge();
                inData[2 + k] = pp.getGender().ordinal()+1;
                inData[3 + k] = pp.getRace().ordinal();
                inData[4 + k] = pp.getOccupation().getCode();
                inData[5 + k] = pp.getIncome();
                // todo: Keep person role in household
                // todo: imData[6+k] is not used, as inmigrant certainly will occupy different workplace. Remove or replace with other person attribute
                inData[6 + k] = pp.getWorkplace();
                k += 6;
            }
            int hhId = householdData.getNextHouseholdId();
            events.add(new MigrationEvent(hhId, MigrationEvent.Type.IN));
            inmigratingHhData.put(hhId, inData);  // create new hhId for inmigrating households and save in HashMap
            createdInmigrants += hhs[selected].getHhSize();
        } while (createdInmigrants < inmigrants);

        // set person counter to 0
        outMigrationPPCounter = 0;
        inMigrationPPCounter = 0;

        return events;
    }
}