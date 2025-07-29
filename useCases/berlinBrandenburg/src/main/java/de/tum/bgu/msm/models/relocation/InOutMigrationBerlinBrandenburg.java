package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.household.HouseholdBerlinBrandenburg;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonBerlinBrandenburg;
import de.tum.bgu.msm.events.impls.household.MigrationEvent;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.demography.driversLicense.DriversLicenseModel;
import de.tum.bgu.msm.models.demography.employment.EmploymentModel;
import de.tum.bgu.msm.models.relocation.migration.InOutMigration;
import de.tum.bgu.msm.models.relocation.migration.InOutMigrationImpl;
import de.tum.bgu.msm.models.relocation.moves.MovesModelImpl;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.PersonWithSchool;
import de.tum.bgu.msm.schools.School;
import de.tum.bgu.msm.schools.SchoolDataImpl;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class InOutMigrationBerlinBrandenburg implements InOutMigration {

    private static final Logger logger = LogManager.getLogger(InOutMigrationBerlinBrandenburg.class);
    private InOutMigrationImpl delegate;
    private DataContainerWithSchools dataContainerWithSchoolsImpl;

    public InOutMigrationBerlinBrandenburg(DataContainer dataContainer, EmploymentModel employment,
                                           MovesModelImpl movesModel, CreateCarOwnershipModel carOwnership,
                                           DriversLicenseModel driversLicense, Properties properties) {
        delegate = new InOutMigrationImpl(dataContainer, employment, movesModel,
                carOwnership, driversLicense, properties, SiloUtil.provideNewRandom());
        dataContainerWithSchoolsImpl = (DataContainerWithSchools) dataContainer;
    }


    @Override
    public void setup() {
        delegate.setup();
    }

    @Override
    public void prepareYear(int year) {
        delegate.prepareYear(year);
    }

    @Override
    public Collection<MigrationEvent> getEventsForCurrentYear(int year) {
        return delegate.getEventsForCurrentYear(year);
    }

    @Override
    public boolean handleEvent(MigrationEvent event) {
        boolean success = delegate.handleEvent(event);
        if(success) {
            HouseholdBerlinBrandenburg householdMuc = (HouseholdBerlinBrandenburg) event.getHousehold();
            for (Person person : householdMuc.getPersons().values()) {
                if (person.getOccupation().equals(Occupation.STUDENT)) {
                    if (event.getType().equals(MigrationEvent.Type.IN)) {
                        //SchoolType is duplicated from original person
                        int SchoolType = -1;
                        if(((PersonBerlinBrandenburg)person).getSchoolId()> 0){
                            SchoolType = dataContainerWithSchoolsImpl.getSchoolData().getSchoolFromId(((PersonBerlinBrandenburg) person).getSchoolId()).getType();
                        }else if(((PersonBerlinBrandenburg)person).getSchoolId()== -2){
                            SchoolType = SchoolDataImpl.guessSchoolType((PersonWithSchool) person);
                        }
                        School newSchool = dataContainerWithSchoolsImpl.getSchoolData().getClosestSchool(person, SchoolType);
                        ((PersonBerlinBrandenburg) person).setSchoolId(newSchool.getId());
                        newSchool.setOccupancy(newSchool.getOccupancy() - 1);
                    } else if (event.getType().equals(MigrationEvent.Type.OUT)) {
                        if(((PersonBerlinBrandenburg)person).getSchoolId()> 0) {
                            School school = dataContainerWithSchoolsImpl.getSchoolData().getSchoolFromId(((PersonBerlinBrandenburg) person).getSchoolId());
                            school.setOccupancy(school.getOccupancy() + 1);
                        }else{
                            logger.info("person id " + person.getId()+" has school id: " + ((PersonBerlinBrandenburg) person).getSchoolId() + ". Person has a school outside study area or has no school assigned. " +person.getAge()+" Occupation: "+ person.getOccupation().name());
                        }
                    }
                }
            }
        }
        return success;
    }

    @Override
    public boolean outMigrateHh(int hhId, boolean overwriteEventRules) {
        return delegate.outMigrateHh(hhId, overwriteEventRules);
    }

    @Override
    public void endYear(int year) {
        delegate.endYear(year);
    }

    @Override
    public void endSimulation() {
        delegate.endSimulation();
    }
}
