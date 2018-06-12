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

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.PersonRole;
import de.tum.bgu.msm.events.MicroEventModel;
import de.tum.bgu.msm.events.impls.person.BirthEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;

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

public class BirthModel extends AbstractModel implements MicroEventModel<BirthEvent> {

    private BirthJSCalculator calculator;

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
    public Collection<BirthEvent> prepareYear(int year) {
        final List<BirthEvent> events = new ArrayList<>();
        for (Person per : dataContainer.getHouseholdData().getPersons()) {
            final int id = per.getId();
            if (Properties.get().eventRules.birth && personCanGiveBirth(per)) {
                events.add(new BirthEvent(id));
            }
        }
        return events;
    }

    @Override
    public boolean handleEvent(BirthEvent event) {
        return chooseBirth(event.getPersonId());
    }

    @Override
    public void finishYear(int year) {
    }

    private boolean chooseBirth(int perId) {
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
                giveBirth(person);
                return true;
            }
        }
        return false;
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


    private double getProbabilityForGirl() {
        return calculator.getProbabilityForGirl();
    }

    private boolean personCanGiveBirth(Person person) {
        return person.getGender() == 2 && calculator.calculateBirthProbability(person.getAge()) > 0;
    }
}
