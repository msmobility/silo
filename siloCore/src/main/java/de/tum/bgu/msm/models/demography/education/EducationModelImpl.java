package de.tum.bgu.msm.models.demography.education;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.events.impls.person.EducationEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Simulates if someone changes school
 * Author: Rolf Moeckel, TUM and Ana Moreno, TUM
 * Created on 13 October 2017 in Cape Town, South Africa
 **/
public class EducationModelImpl extends AbstractModel implements EducationModel {

    public EducationModelImpl(DataContainer dataContainer, Properties properties, Random rnd) {
        super(dataContainer, properties, rnd);
    }

    @Override
    public Collection<EducationEvent> getEventsForCurrentYear(int year) {
        final List<EducationEvent> events = new ArrayList<>();
        for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            if (person.getAge() >= 19 && person.getOccupation().equals(Occupation.STUDENT)) {
                events.add(new EducationEvent(person.getId()));
            }
        }
        return events;
    }

    @Override
    public boolean handleEvent(EducationEvent event) {
        Person pp = dataContainer.getHouseholdDataManager().getPersonFromId(event.getPersonId());
        if (pp != null) {
            return updateEducation(pp);
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
        person.setOccupation(Occupation.UNEMPLOYED);
        person.setWorkplace(-1);
        // also check occupation transition to worker? 'nk
        // rm: don't think so, student is added to the labor market but does not necessarily find a job right away.
        if (person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " + person.getId() +
                    " changed school.");
        }
        return true;
    }


}
