package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.events.*;
import de.tum.bgu.msm.models.AbstractModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simulates if someone changes school
 * Author: Rolf Moeckel, TUM and Ana Moreno, TUM
 * Created on 13 October 2017 in Cape Town, South Africa
 **/
public class ChangeSchoolUnivModel extends AbstractModel implements EventHandler, EventCreator{

    public ChangeSchoolUnivModel(SiloDataContainer dataContainer) {
        super(dataContainer);
    }

    @Override
    public void handleEvent(Event event) {
        if(event.getType() == EventType.CHECK_SCHOOL_UNIV) {
            Person pp = dataContainer.getHouseholdData().getPersonFromId(event.getId());
            if (pp == null) {
                return;  // person has died or moved away
            }

            if (pp.getAge() == 19) {
                updateEducation(pp);
            }
        }
    }

    @Override
    public Collection<Event> createEvents(int year) {
        final List<Event> events = new ArrayList<>();
        for(Person person: dataContainer.getHouseholdData().getPersons()) {
            events.add(new EventImpl(EventType.CHECK_SCHOOL_UNIV, person.getId(), year));
        }
        return events;
    }

    // todo: Implement logical rules how students change from one school type to another or graduate from school/university
    void updateEducation(Person person) {
        int schoolId = 0;
        person.setSchoolPlace(schoolId);
        // todo if 2 is the right code for someone who graduates from high school
        //todo also check occupation transition to worker? 'nk
        person.setEducationLevel(2);
        EventManager.countEvent(EventType.CHECK_SCHOOL_UNIV);
        if (person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + person.getId() +
                    " changed school. New school place (0 = left school) " + schoolId);
        }
    }
}
