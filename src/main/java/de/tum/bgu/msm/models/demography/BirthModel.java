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
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Simulates birth of children
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 28 December 2009 in Bocholt
 **/

public class BirthModel extends AbstractModel {

    private static BirthJSCalculator calculator;

    public BirthModel(SiloDataContainer dataContainer) {
        super(dataContainer);
        setupBirthModel();
    }

    private void setupBirthModel() {
        Reader reader;
        if (Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("BirthProbabilityCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("BirthProbabilityCalcMstm"));
        }
        float localScaler = Properties.get().demographics.localScaler;
        calculator = new BirthJSCalculator(reader, localScaler);
    }


    public void chooseBirth(int perId) {
        final HouseholdDataManager householdData = dataContainer.getHouseholdData();
        final Person person = householdData.getPersonFromId(perId);
        if (!EventRules.ruleGiveBirth(person)) {
            return;  // Person has died or moved away
        }
        if (person.getGender() == 1) {
            return;            // Exclude males, model should never get here
        }
        // todo: distinguish birth probability by neighborhood type (such as urban, suburban, rural)
        double birthProb = calculator.calculateBirthProbability(person.getAge());
        if (person.getRole() == PersonRole.MARRIED) {
            birthProb *= Properties.get().demographics.marriedScaler;
        } else {
            birthProb *= Properties.get().demographics.singleScaler;
        }
        if (SiloUtil.getRandomNumberAsDouble() < birthProb) {
            giveBirth(person);
        }

    }

    void giveBirth(Person person) {

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
        EventManager.countEvent(EventTypes.CHECK_BIRTH);
    }


    public void checkBirthday(int personId) {
        // increase age of this person by one year
        Person per = dataContainer.getHouseholdData().getPersonFromId(personId);

        if (!EventRules.ruleBirthday(per)) {
            return;  // Person has died or moved away
        }
        celebrateBirthday(per);
    }

    void celebrateBirthday(Person per) {
        per.birthday();
        EventManager.countEvent(EventTypes.BIRTHDAY);
        if (per.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Celebrated BIRTHDAY of person " +
                    per.getId() + ". New age is " + per.getAge() + ".");
        }
    }

    public static double getProbabilityForGirl() {
        return calculator.getProbabilityForGirl();
    }

    public static boolean personCanGiveBirth(int age) {
        return (calculator.calculateBirthProbability(age) > 0);
    }

}
