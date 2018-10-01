package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.events.MicroEventModel;
import de.tum.bgu.msm.events.impls.person.EducationEvent;
import de.tum.bgu.msm.models.AbstractModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simulates if someone changes school
 * Author: Rolf Moeckel, TUM and Ana Moreno, TUM
 * Created on 13 October 2017 in Cape Town, South Africa
 **/
public class ChangeSchoolUnivModel extends AbstractModel implements MicroEventModel<EducationEvent> {

    public ChangeSchoolUnivModel(SiloDataContainer dataContainer) {
        super(dataContainer);
    }

    @Override
    public boolean handleEvent(EducationEvent event) {
        Person pp = dataContainer.getHouseholdData().getPersonFromId(event.getPersonId());
        if (pp != null) {
            if (pp.getAge() == 19) {
                return updateEducation(pp);
            }
        }
        return false;
    }

    @Override
    public void finishYear(int year) {}

    @Override
    public Collection<EducationEvent> prepareYear(int year) {
        final List<EducationEvent> events = new ArrayList<>();
        for(Person person: dataContainer.getHouseholdData().getPersons()) {
            events.add(new EducationEvent(person.getId()));
        }
        return events;
    }

    // todo: Implement logical rules how students change from one school type to another or graduate from school/university
    boolean updateEducation(Person person) {
        int schoolId = 0;
        person.setSchoolPlace(schoolId);
        // todo if 2 is the right code for someone who graduates from high school
        //todo also check occupation transition to worker? 'nk
        person.setEducationLevel(2);
        if (person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + person.getId() +
                    " changed school. New school place (0 = left school) " + schoolId);
        }
        return true;
    }
}
