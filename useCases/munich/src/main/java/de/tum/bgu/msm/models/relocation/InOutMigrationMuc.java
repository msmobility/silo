package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.DataContainerMuc;
import de.tum.bgu.msm.data.household.HouseholdImpl;
import de.tum.bgu.msm.data.household.HouseholdMuc;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.data.school.School;
import de.tum.bgu.msm.data.school.SchoolImpl;
import de.tum.bgu.msm.events.impls.household.MigrationEvent;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.demography.driversLicense.DriversLicenseModel;
import de.tum.bgu.msm.models.demography.employment.EmploymentModel;
import de.tum.bgu.msm.models.relocation.migration.InOutMigration;
import de.tum.bgu.msm.models.relocation.migration.InOutMigrationImpl;
import de.tum.bgu.msm.models.relocation.moves.MovesModelImpl;
import de.tum.bgu.msm.properties.Properties;

import java.util.Collection;

public class InOutMigrationMuc implements InOutMigration {

    private InOutMigrationImpl delegate;
    private DataContainerMuc dataContainerMuc;

    public InOutMigrationMuc(DataContainer dataContainer, EmploymentModel employment,
                              MovesModelImpl movesModel, CreateCarOwnershipModel carOwnership,
                              DriversLicenseModel driversLicense, Properties properties) {
        delegate = new InOutMigrationImpl(dataContainer,employment,movesModel,carOwnership,driversLicense,properties);
        dataContainerMuc = (DataContainerMuc) dataContainer;
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
        HouseholdMuc householdMuc = (HouseholdMuc) event.getHousehold();
        for(Person person : householdMuc.getPersons().values()){
            if (person.getOccupation().equals(Occupation.STUDENT)){
                if (event.getType().equals(MigrationEvent.Type.IN)){
                    //oldSchool is duplicated from original person
                    School oldSchool = dataContainerMuc.getSchoolData().getSchoolFromId(((PersonMuc)person).getSchoolId());
                    School newSchool = dataContainerMuc.getSchoolData().getClosestSchool(person,oldSchool.getType());
                    ((PersonMuc)person).setSchoolId(newSchool.getId());
                    newSchool.setOccupancy(newSchool.getOccupancy()-1);
                }else if (event.getType().equals(MigrationEvent.Type.OUT)){
                    School school = dataContainerMuc.getSchoolData().getSchoolFromId(((PersonMuc)person).getSchoolId());
                    school.setOccupancy(school.getOccupancy()+1);
                }
            }
        }

        return success;
    }

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
