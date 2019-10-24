package de.tum.bgu.msm.models.disability;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.person.Disability;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMucDisability;
import de.tum.bgu.msm.events.DisabilityEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class DisabilityImpl extends AbstractModel implements DisabilityModel {

    private final DefaultDisabilityStrategy strategy;

    public DisabilityImpl(DataContainer dataContainer, Properties properties, DefaultDisabilityStrategy strategy, Random rnd) {
        super(dataContainer, properties, rnd);
        this.strategy = strategy;
    }

    @Override
    public void setup() {}

    @Override
    public void prepareYear(int year) {}

    @Override
    public Collection<DisabilityEvent> getEventsForCurrentYear(int year) {
        List<DisabilityEvent> events = new ArrayList<>();
        for(Person person: dataContainer.getHouseholdDataManager().getPersons()) {
            final int id = person.getId();
            //todo. need to add event rule for disability!
            if(properties.eventRules.birth) {
                if (((PersonMucDisability)person).getDisability().equals(Disability.WITHOUT)) {
                    events.add(new DisabilityEvent(id));
                }
            }
        }
        return events;
    }

    @Override
    public boolean handleEvent(DisabilityEvent event) {
        return checkDisability(event);
    }

    @Override
    public void endYear(int year) {

    }

    @Override
    public void endSimulation() {

    }

    public boolean checkDisability(DisabilityEvent event) {
        // check if the person will get a disability on the current year
        Person person = dataContainer.getHouseholdDataManager().getPersonFromId(event.getPersonId());
        if (person != null) {
            if (random.nextDouble() < strategy.calculateDisabilityProbability(person)) {
                if (random.nextDouble() < strategy.calculateDisabilityType(person)) {
                    giveDisability(person, Disability.PHYSICAL);
                    return true;
                } else {
                    giveDisability(person, Disability.MENTAL);
                    return true;
                }
            }
        }
        return false;
    }

    void giveDisability(Person per, Disability disabilityType) {
        ((PersonMucDisability)per).setDisability(disabilityType);
        if (per.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Disability added to person " +
                    per.getId() + ". New age is " + per.getAge() + ".");
        }
    }
}
