package de.tum.bgu.msm.models.relocation;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.JobDataManager;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.events.MicroEventModel;
import de.tum.bgu.msm.events.impls.household.MigrationEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.autoOwnership.munich.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.demography.DriversLicense;
import de.tum.bgu.msm.models.demography.EmploymentModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    private final HouseholdFactory hhFactory;

    private String populationControlMethod;
    private TableDataSet tblInOutMigration;
    private TableDataSet tblPopulationTarget;
    private Map<Integer, int[]> inmigratingHhData;
    private int outMigrationPPCounter;
    private int inMigrationPPCounter;


    public InOutMigration(SiloDataContainer dataContainer, EmploymentModel employment, MovesModelI movesModel,
                          CreateCarOwnershipModel carOwnership, DriversLicense driversLicense,
                          PersonFactory factory, HouseholdFactory hhFactory) {
        super(dataContainer);
        this.employment = employment;
        this.movesModel = movesModel;
        this.carOwnership = carOwnership;
        this.driversLicense = driversLicense;
        this.factory = factory;
        this.hhFactory = hhFactory;
        populationControlMethod = Properties.get().moves.populationControlTotal;
        if (populationControlMethod.equalsIgnoreCase("population")) {
            String fileName = Properties.get().main.baseDirectory + Properties.get().moves.populationCOntrolTotalFile;
            tblPopulationTarget = SiloUtil.readCSVfile(fileName);
            tblPopulationTarget.buildIndex(tblPopulationTarget.getColumnPosition("Year"));
        } else if (populationControlMethod.equalsIgnoreCase("migration")) {
            String fileName = Properties.get().main.baseDirectory + Properties.get().moves.migrationFile;
            tblInOutMigration = SiloUtil.readCSVfile(fileName);
            tblInOutMigration.buildIndex(tblInOutMigration.getColumnPosition("Year"));
        } else if (populationControlMethod.equalsIgnoreCase("populationGrowthRate")) {
            tblPopulationTarget = new TableDataSet();
            int periodLength = Properties.get().main.endYear - Properties.get().main.startYear + 1;
            int[] years = new int[periodLength];
            int[] populationByYear = new int[periodLength];
            int populationBaseYear = dataContainer.getHouseholdData().getPersons().size();
            for (int i = 0; i < periodLength; i++) {
                years[i] = Properties.get().main.startYear + i;
                populationByYear[i] = (int) Math.round(populationBaseYear * Math.pow(1 + Properties.get().moves.populationGrowthRateInPercentage / 100, i));
            }
            tblPopulationTarget.appendColumn(years, "Year");
            tblPopulationTarget.appendColumn(populationByYear, "Population");
            tblPopulationTarget.buildIndex(tblPopulationTarget.getColumnPosition("Year"));


        } else {
            LOGGER.error("Unknown property found for population.control.total, set to population or migration");
            System.exit(0);
        }

    }


    @Override
    public Collection<MigrationEvent> prepareYear(int year) {
        final List<MigrationEvent> events = new ArrayList<>();

        LOGGER.info("  Selecting outmigrants and creating inmigrants for the year " + year);
        final HouseholdDataManager householdData = dataContainer.getHouseholdData();

        Household[] hhs = householdData.getHouseholds().toArray(new Household[0]);

        createOutmigrants(year, events, hhs);
        createInmigrants(year, events, hhs);

        outMigrationPPCounter = 0;
        inMigrationPPCounter = 0;

        return events;
    }

    private void createInmigrants(int year, List<MigrationEvent> events, Household[] hhs) {
        HouseholdDataManager householdData = dataContainer.getHouseholdData() ;
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
        while (createdInmigrants < inmigrants) {
            Household hh = hhs[(int) (hhs.length * SiloUtil.getRandomNumberAsFloat())];
            Household inmigratingHousehold = householdData.duplicateHousehold(hh);
            events.add(new MigrationEvent(inmigratingHousehold, MigrationEvent.Type.IN));
            createdInmigrants += inmigratingHousehold.getHhSize();
        }
    }

    private void createOutmigrants(int year, List<MigrationEvent> events, Household[] hhs) {
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
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

        int createdOutMigrants = 0;
        while (createdOutMigrants < outmigrants) {
            Household selectedHousehold = hhs[(int) (hhs.length * SiloUtil.getRandomNumberAsFloat())];
            events.add(new MigrationEvent(selectedHousehold, MigrationEvent.Type.OUT));
            createdOutMigrants += selectedHousehold.getHhSize();

        }
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


        HouseholdDataManager householdData = dataContainer.getHouseholdData();

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
            if (Properties.get().main.implementation == Implementation.MUNICH) {
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
            householdData.addHousehold(hh);
            for(Person person: hh.getPersons().values()) {
                householdData.addPerson(person);
            }
            return true;
        } else {
            IssueCounter.countLackOfDwellingFailedInmigration();
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
        for (Map.Entry<Integer, ? extends Person> person : hh.getPersons().entrySet()) {
            if (person.getValue().getJobId() > 0) {
                jobData.quitJob(true, person.getValue());
            }
            if (person.getKey() == SiloUtil.trackPp) {
                SiloUtil.trackWriter.println(" Person " + person.getKey() + " outmigrated.");
            }
        }
        dataContainer.getHouseholdData().removeHousehold(hhId);
        return true;
    }

    @Override
    public void finishYear(int year) {
        SummarizeData.resultFile("InmigrantsPP," + inMigrationPPCounter);
        SummarizeData.resultFile("OutmigrantsPP," + outMigrationPPCounter);
    }


}