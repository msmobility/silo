/*
 * Copyright  2005 PB Consult Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package de.tum.bgu.msm.models.demography;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.PersonRole;
import de.tum.bgu.msm.events.*;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simulates birth of children
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 28 December 2009 in Bocholt
 **/

public class BirthModel extends AbstractModel implements MicroEventModel {

    private BirthJSCalculator calculator;
    private final static Logger LOGGER = Logger.getLogger(BirthModel.class);


    public BirthModel(SiloDataContainer dataContainer) {
        super(dataContainer);
        setupBirthModel();
    }

    private void setupBirthModel() {
        final Reader reader;
        if (Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("BirthProbabilityCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("BirthProbabilityCalcMstm"));
        }
        float localScaler = Properties.get().demographics.localScaler;
        calculator = new BirthJSCalculator(reader, localScaler);
    }

    @Override
    public EventResult handleEvent(Event event) {
        EventType type = event.getType();
        switch (type) {
            case BIRTHDAY:
                return checkBirthday(event);
            case BIRTH:
                return chooseBirth(event.getId());
        }
        return null;
    }

    @Override
    public void finishYear(int year) {}

    private BirthResult chooseBirth(int perId) {
        final HouseholdDataManager householdData = dataContainer.getHouseholdData();
        final Person person = householdData.getPersonFromId(perId);
        if (person != null && personCanGiveBirth(person)) {
            // todo: distinguish birth probability by neighborhood type (such as urban, suburban, rural)
            double birthProb = calculator.calculateBirthProbability(person.getAge());
            if (person.getRole() == PersonRole.MARRIED) {
                birthProb *= Properties.get().demographics.marriedScaler;
            } else {
                birthProb *= Properties.get().demographics.singleScaler;
            }
            if (SiloUtil.getRandomNumberAsDouble() < birthProb) {
                Person child = giveBirth(person);
                return new BirthResult(perId, child.getId(), child.getGender());
            }
        }
        return null;
    }

    Person giveBirth(Person person) {
        final HouseholdDataManager householdData = dataContainer.getHouseholdData();
        final Household household = householdData.getHouseholdFromId(person.getHh().getId());
        final int id = householdData.getNextPersonId();
        int gender = 1;
        if (SiloUtil.getRandomNumberAsDouble() <= getProbabilityForGirl()) {
            gender = 2;
        }
        final Person child = householdData.createPerson(id, 0, gender, household.getRace(),
                0, 0, 0);
        child.setRole(PersonRole.CHILD);
        householdData.addPersonToHousehold(child, household);
        householdData.addHouseholdThatChanged(household);
        if (id == SiloUtil.trackPp
                || household.getId() == SiloUtil.trackHh
                || person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("For unto us a child was born... " + person.getId() + " gave birth" +
                    "to a child named " + id + ". Added to household " + household.getId() + ".");
        }
        return child;
    }

    void celebrateBirthday(Person per) {
        per.birthday();
        if (per.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Celebrated BIRTHDAY of person " +
                    per.getId() + ". New age is " + per.getAge() + ".");
        }
    }

    private double getProbabilityForGirl() {
        return calculator.getProbabilityForGirl();
    }

    private boolean personCanGiveBirth(Person person) {
        return person.getGender() == 2 && calculator.calculateBirthProbability(person.getAge()) > 0;
    }

    private BirthdayResult checkBirthday(Event event) {
        // increase age of this person by one year
        Person per = dataContainer.getHouseholdData().getPersonFromId(event.getId());
        if (per == null) {
            return null;  // Person has died or moved away
        }
        celebrateBirthday(per);
        return new BirthdayResult(event.getId());
    }

    @Override
    public Collection<Event> prepareYear(int year) {
        final List<Event> events = new ArrayList<>();
        for (Person per : dataContainer.getHouseholdData().getPersons()) {
            final int id = per.getId();
            // Birthday
            if(Properties.get().eventRules.birthday) {
                events.add(new EventImpl(EventType.BIRTHDAY, id, year));
            }
            // Birth
            if (Properties.get().eventRules.birth && personCanGiveBirth(per)) {
                events.add(new EventImpl(EventType.BIRTH, id, year));
            }
        }
        return events;
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

    public static class BirthResult implements EventResult {

        @JsonProperty("id")
        public final int personId;

        @JsonProperty("child")
        public final int childId;

        @JsonProperty("sex")
        public final int gender;

        private BirthResult(int personId, int childId, int gender) {
            this.personId = personId;
            this.childId = childId;
            this.gender = gender;
        }

        @Override
        public EventType getType() {
            return EventType.BIRTH;
        }
    }
}
