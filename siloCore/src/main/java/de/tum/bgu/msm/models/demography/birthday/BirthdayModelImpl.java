package de.tum.bgu.msm.models.demography.birthday;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.events.impls.person.BirthDayEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class BirthdayModelImpl extends AbstractModel implements BirthdayModel {

    public BirthdayModelImpl(DataContainer dataContainer, Properties properties, Random random) {
        super(dataContainer, properties, random);
    }

    @Override
    public void setup() {}

    @Override
    public void prepareYear(int year) {}

    @Override
    public Collection<BirthDayEvent> getEventsForCurrentYear(int year) {
        List<BirthDayEvent> events = new ArrayList<>();
        for (Person per : dataContainer.getHouseholdDataManager().getPersons()) {
            final int id = per.getId();
            if(properties.eventRules.birthday) {
                events.add(new BirthDayEvent(id));
            }
        }
        return events;
    }

    @Override
    public boolean handleEvent(BirthDayEvent event) {
        return checkBirthday(event);
    }

    @Override
    public void endYear(int year) {

    }

    @Override
    public void endSimulation() {

    }

    private boolean checkBirthday(BirthDayEvent event) {
        // increase age of this person by one year
        Person per = dataContainer.getHouseholdDataManager().getPersonFromId(event.getPersonId());
        if (per == null) {
            return false;  // Person has died or moved away
        }
        celebrateBirthday(per);
        return true;
    }

    void celebrateBirthday(Person per) {
        per.birthday();
        if (per.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Celebrated BIRTHDAY of person " +
                    per.getId() + ". New age is " + per.getAge() + ".");
        }
    }
}
