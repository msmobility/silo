package de.tum.bgu.msm.models.relocation.migration;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.events.impls.household.MigrationEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.demography.driversLicense.DriversLicenseModel;
import de.tum.bgu.msm.models.demography.employment.EmploymentModel;
import de.tum.bgu.msm.models.relocation.moves.MovesModelImpl;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.modules.MovesProperties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Adds exogenously given inmigrating and outmigrating households
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 15 January 2010 in Albuquerque
 **/
public class InOutMigrationImpl extends AbstractModel implements InOutMigration {

    private final static Logger logger = Logger.getLogger(InOutMigrationImpl.class);

    private final EmploymentModel employment;
    private final MovesModelImpl movesModel;
    private final CreateCarOwnershipModel carOwnership;
    private final DriversLicenseModel driversLicense;

    private MovesProperties.PopulationControlTotalMethod populationControlMethod;
    private TableDataSet tblInOutMigration;
    private TableDataSet tblPopulationTarget;
    private int outMigrationPPCounter;
    private int inMigrationPPCounter;
    private int lackOfDwellingFailedInmigration;


    public InOutMigrationImpl(DataContainer dataContainer, EmploymentModel employment,
                              MovesModelImpl movesModel, CreateCarOwnershipModel carOwnership,
                              DriversLicenseModel driversLicense, Properties properties, Random random) {
        super(dataContainer, properties, random);
        this.employment = employment;
        this.movesModel = movesModel;
        this.carOwnership = carOwnership;
        this.driversLicense = driversLicense;
    }


    @Override
    public void setup() {
        populationControlMethod = properties.moves.populationControlTotal;
        double scaleFactor = properties.main.scaleFactor;
        if (populationControlMethod.equals(MovesProperties.PopulationControlTotalMethod.POPULATION)) {
            String fileName = properties.main.baseDirectory + properties.moves.populationCOntrolTotalFile;
            tblPopulationTarget = SiloUtil.readCSVfile(fileName);
            tblPopulationTarget.buildIndex(tblPopulationTarget.getColumnPosition("Year"));
            if (scaleFactor != 1.) {
                int periodLength = properties.main.endYear - properties.main.startYear + 1;
                for (int i = 0; i < periodLength; i++) {
                    int scaledPopulation = (int) (tblPopulationTarget.getValueAt(i + 1, "Population") * scaleFactor);
                    tblPopulationTarget.setValueAt(i + 1, "Population", scaledPopulation);
                }
            }
        } else if (populationControlMethod.equals(MovesProperties.PopulationControlTotalMethod.MIGRATION)) {
            String fileName = properties.main.baseDirectory + properties.moves.migrationFile;
            tblInOutMigration = SiloUtil.readCSVfile(fileName);
            tblInOutMigration.buildIndex(tblInOutMigration.getColumnPosition("Year"));
            if (scaleFactor != 1.) {
                int periodLength = properties.main.endYear - properties.main.startYear + 1;
                for (int i = 0; i < periodLength; i++) {
                    int scaledInmigration = (int) (tblInOutMigration.getValueAt(i + 1, "Inmigration") * scaleFactor);
                    tblInOutMigration.setValueAt(i + 1, "Inmigration", scaledInmigration);
                    int scaledOutmigration = (int) (tblInOutMigration.getValueAt(i + 1, "Outmigration") * scaleFactor);
                    tblInOutMigration.setValueAt(i + 1, "Outmigration", scaledOutmigration);
                }
            }
        } else if (populationControlMethod.equals(MovesProperties.PopulationControlTotalMethod.RATE)) {
            tblPopulationTarget = new TableDataSet();
            int periodLength = properties.main.endYear - properties.main.startYear + 1;
            int[] years = new int[periodLength];
            int[] populationByYear = new int[periodLength];
            int populationBaseYear = dataContainer.getHouseholdDataManager().getPersons().size();
            for (int i = 0; i < periodLength; i++) {
                years[i] = properties.main.startYear + i;
                populationByYear[i] = (int) Math.round(populationBaseYear * Math.pow(1 + properties.moves.populationGrowthRateInPercentage / 100, i));
            }
            tblPopulationTarget.appendColumn(years, "Year");
            tblPopulationTarget.appendColumn(populationByYear, "Population");
            tblPopulationTarget.buildIndex(tblPopulationTarget.getColumnPosition("Year"));


        } else {
            final String message
                    = "Unknown property found for population.control.total, set to population or migration";
            logger.error(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public void prepareYear(int year) {
        lackOfDwellingFailedInmigration = 0;
    }

    private void createInmigrants(int year, List<MigrationEvent> events, Household[] hhs) {
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        int inmigrants = 0;
        if (populationControlMethod.equals(MovesProperties.PopulationControlTotalMethod.MIGRATION)) {
            inmigrants = (int) tblInOutMigration.getIndexedValueAt(year, "Inmigration");
        } else {
            int currentPopulation = householdDataManager.getPersons().size();
            int target = (int) tblPopulationTarget.getIndexedValueAt(year, "Population");
            if (target > currentPopulation) {
                inmigrants = target - currentPopulation;
            }
        }
        int createdInmigrants = 0;
        while (createdInmigrants < inmigrants) {
            Household hh = hhs[(int) (hhs.length * this.random.nextDouble())];
            Household inmigratingHousehold = householdDataManager.duplicateHousehold(hh);
            events.add(new MigrationEvent(inmigratingHousehold, MigrationEvent.Type.IN));
            createdInmigrants += inmigratingHousehold.getHhSize();
        }
    }

    private void createOutmigrants(int year, List<MigrationEvent> events, Household[] hhs) {
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        int outmigrants = 0;
        if (populationControlMethod.equals(MovesProperties.PopulationControlTotalMethod.MIGRATION)) {
            outmigrants = (int) tblInOutMigration.getIndexedValueAt(year, "Outmigration");
        } else {
            int currentPopulation = householdDataManager.getPersons().size();
            int target = (int) tblPopulationTarget.getIndexedValueAt(year, "Population");
            if (target < currentPopulation) {
                outmigrants = currentPopulation - target;
            }
        }

        int createdOutMigrants = 0;
        while (createdOutMigrants < outmigrants) {
            Household selectedHousehold = hhs[(int) (hhs.length * this.random.nextDouble())];
            events.add(new MigrationEvent(selectedHousehold, MigrationEvent.Type.OUT));
            createdOutMigrants += selectedHousehold.getHhSize();

        }
    }

    @Override
    public Collection<MigrationEvent> getEventsForCurrentYear(int year) {
        final List<MigrationEvent> events = new ArrayList<>();

        logger.info("  Selecting outmigrants and creating inmigrants for the year " + year);
        final HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();

        Household[] hhs = householdDataManager.getHouseholds().toArray(new Household[0]);

        createOutmigrants(year, events, hhs);
        createInmigrants(year, events, hhs);

        outMigrationPPCounter = 0;
        inMigrationPPCounter = 0;

        return events;
    }

    @Override
    public boolean handleEvent(MigrationEvent event) {
        MigrationEvent.Type type = event.getType();
        switch (type) {
            case IN:
                return inmigrateHh(event.getHousehold());
            case OUT:
                return outMigrateHh(event.getHousehold().getId(), true);
            default:
                return false;
        }
    }

    private boolean inmigrateHh(Household hh) {
        // Inmigrate household with hhId from HashMap inmigratingHhData<Integer, int[]>


        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();

        // Searching for employment has to be in a separate loop from setting up all persons, as finding a
        // job will change the household income and household type, which can only be calculated after all
        // persons are set up.
        for (Person person : hh.getPersons().values()) {
            if (person.getOccupation() == Occupation.EMPLOYED) {
                employment.lookForJob(person.getId());
                if (person.getJobId() < 1) {
                    person.setOccupation(Occupation.UNEMPLOYED);
                }
            }
            driversLicense.checkLicenseCreation(person.getId());
        }

        int newDdId = movesModel.searchForNewDwelling(hh);
        if (newDdId > 0) {
            movesModel.moveHousehold(hh, -1, newDdId);
            if (carOwnership != null) {
                carOwnership.simulateCarOwnership(hh); // set initial car ownership of new household
            }
            inMigrationPPCounter += hh.getHhSize();
            if (hh.getId() == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Household " + hh.getId() + " inmigrated.");
            }
            for (Integer ppId : hh.getPersons().keySet()) {
                if (ppId == SiloUtil.trackPp) {
                    SiloUtil.trackWriter.println(" Person " + ppId + " inmigrated.");
                }
            }
            householdDataManager.addHousehold(hh);
            for (Person person : hh.getPersons().values()) {
                householdDataManager.addPerson(person);
            }
            return true;
        } else {
            lackOfDwellingFailedInmigration++;
            return false;
        }
    }


    @Override
    public boolean outMigrateHh(int hhId, boolean overwriteEventRules) {
        // Household with ID hhId out migrates
        Household hh = dataContainer.getHouseholdDataManager().getHouseholdFromId(hhId);
        if (properties.eventRules.outMigration && !overwriteEventRules || hh == null) {
            return false;
        }
        outMigrationPPCounter += hh.getHhSize();
        if (hhId == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("Household " + hhId + " outmigrated.");
        }
        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        for (Map.Entry<Integer, ? extends Person> person : hh.getPersons().entrySet()) {
            if (person.getValue().getJobId() > 0) {
                jobDataManager.quitJob(true, person.getValue());
            }
            if (person.getKey() == SiloUtil.trackPp) {
                SiloUtil.trackWriter.println(" Person " + person.getKey() + " outmigrated.");
            }
        }
        dataContainer.getHouseholdDataManager().removeHousehold(hhId);
        return true;
    }

    @Override
    public void endYear(int year) {
        //todo reconsider to print out model results and how to pass them to the ResultsMonitor
        logger.info("Total Inmigrants: " + inMigrationPPCounter);
        logger.info("Total OutmigrantsPP: " + outMigrationPPCounter);
        if (lackOfDwellingFailedInmigration > 0) {
            logger.warn("  Encountered " + lackOfDwellingFailedInmigration + " cases " +
                    "where household wanted to inmigrate but could not find vacant dwelling.");
        }
    }

    @Override
    public void endSimulation() {

    }


}