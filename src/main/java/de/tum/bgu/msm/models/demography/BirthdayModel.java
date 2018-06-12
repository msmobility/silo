package de.tum.bgu.msm.models.demography;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.events.*;
import de.tum.bgu.msm.events.impls.person.BirthDayEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BirthdayModel extends AbstractModel implements MicroEventModel<BirthDayEvent>{

    public BirthdayModel(SiloDataContainer dataContainer) {
        super(dataContainer);
    }

    @Override
    public Collection<BirthDayEvent> prepareYear(int year) {
        List<BirthDayEvent> events = new ArrayList<>();
        for (Person per : dataContainer.getHouseholdData().getPersons()) {
            final int id = per.getId();
            if(Properties.get().eventRules.birthday) {
                events.add(new BirthDayEvent(id));
            }
        }
        return events;
    }

    @Override
    public EventResult handleEvent(BirthDayEvent event) {
        return checkBirthday(event);
    }

    @Override
    public void finishYear(int year) {

    }

    private BirthdayResult checkBirthday(BirthDayEvent event) {
        // increase age of this person by one year
        Person per = dataContainer.getHouseholdData().getPersonFromId(event.getPersonId());
        if (per == null) {
            return null;  // Person has died or moved away
        }
        celebrateBirthday(per);
        return new BirthdayResult(event.getPersonId());
    }

    void celebrateBirthday(Person per) {
        per.birthday();
        if (per.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Celebrated BIRTHDAY of person " +
                    per.getId() + ". New age is " + per.getAge() + ".");
        }
    }

    public static class BirthdayResult implements EventResult {

        @JsonProperty("person")
        public final int personId;

        private BirthdayResult(int personId) {
            this.personId = personId;
        }

        @Override
        public EventType getType() {
            return EventType.BIRTHDAY;
        }
    }
}
