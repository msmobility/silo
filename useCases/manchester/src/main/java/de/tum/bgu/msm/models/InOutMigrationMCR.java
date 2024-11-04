package de.tum.bgu.msm.models;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.events.impls.household.MigrationEvent;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.demography.driversLicense.DriversLicenseModel;
import de.tum.bgu.msm.models.demography.employment.EmploymentModel;
import de.tum.bgu.msm.models.relocation.migration.InOutMigration;
import de.tum.bgu.msm.models.relocation.moves.MovesModel;
import de.tum.bgu.msm.models.relocation.moves.MovesModelImpl;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.modules.MovesProperties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * simple mirgration manchester, only out no in
 **/
public class InOutMigrationMCR extends AbstractModel implements InOutMigration {

    private final static Logger logger = Logger.getLogger(InOutMigrationMCR.class);

    private final EmploymentModel employment;
    private final MovesModel movesModel;
    private final CreateCarOwnershipModel carOwnership;
    private final DriversLicenseModel driversLicense;

    private MovesProperties.PopulationControlTotalMethod populationControlMethod;
    private TableDataSet tblInOutMigration;
    private TableDataSet tblPopulationTarget;
    private int outMigrationPPCounter;
    private int inMigrationPPCounter;
    private int lackOfDwellingFailedInmigration;


    public InOutMigrationMCR(DataContainer dataContainer, EmploymentModel employment,
                             MovesModel movesModel, CreateCarOwnershipModel carOwnership,
                             DriversLicenseModel driversLicense, Properties properties, Random random) {
        super(dataContainer, properties, random);
        this.employment = employment;
        this.movesModel = movesModel;
        this.carOwnership = carOwnership;
        this.driversLicense = driversLicense;
    }


    @Override
    public void setup() {
    }

    @Override
    public void prepareYear(int year) {lackOfDwellingFailedInmigration = 0;}

    @Override
    public Collection<MigrationEvent> getEventsForCurrentYear(int year) {
        final List<MigrationEvent> events = new ArrayList<>();
        //TODO: currently deactivate for manchester impl
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