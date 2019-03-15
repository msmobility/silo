package de.tum.bgu.msm.models;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.events.impls.person.EducationEvent;
import de.tum.bgu.msm.models.demography.education.EducationModel;
import de.tum.bgu.msm.properties.Properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simulates if someone changes school
 * Author: Rolf Moeckel, TUM and Ana Moreno, TUM
 * Created on 13 October 2017 in Cape Town, South Africa
 **/
public class MstmEducationModelImpl extends AbstractModel implements EducationModel {

    public MstmEducationModelImpl(DataContainer dataContainer, Properties properties) {
        super(dataContainer, properties);
    }

    @Override
    public Collection<EducationEvent> getEventsForCurrentYear(int year) {
        final List<EducationEvent> events = new ArrayList<>();
        for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            events.add(new EducationEvent(person.getId()));
        }
        return events;
    }

    @Override
    public boolean handleEvent(EducationEvent event) {
        Person pp = dataContainer.getHouseholdDataManager().getPersonFromId(event.getPersonId());
        if (pp != null) {
            if (pp.getAge() == 19) {
                return updateEducation(pp);
            }
        }
        return false;
    }

    @Override
    public void endYear(int year) {
    }

    @Override
    public void endSimulation() {

    }

    @Override
    public void setup() {

    }

    @Override
    public void prepareYear(int year) {}

    //TODO implement for mstm?
    // todo: Implement logical rules how students change from one school type to another or graduate from school/university
    boolean updateEducation(Person person) {
//        int schoolId = -1;
//        person.setSchoolId(schoolId);
//        // todo if 2 is the right code for someone who graduates from high school
//        //todo also check occupation transition to worker? 'nk
//        if (person.getId() == SiloUtil.trackPp) {
//            SiloUtil.trackWriter.println("Person " + person.getId() +
//                    " changed school. New school place (-1 = left school) " + schoolId);
//        }
        return true;
    }


}
