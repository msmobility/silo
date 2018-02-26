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
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.PersonRole;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.properties.Properties;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Simulates birth of children
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 28 December 2009 in Bocholt
 **/

public class BirthModel {

    private final HouseholdDataManager householdDataManager;
    private static BirthJSCalculator calculator;

    public BirthModel(HouseholdDataManager householdDataManager) {
        this.householdDataManager = householdDataManager;
        setupBirthModel();
	}


    private void setupBirthModel() {
        Reader reader;
        if(Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("BirthProbabilityCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("BirthProbabilityCalcMstm"));
        }
        float localScaler = Properties.get().demographics.localScaler;
        calculator = new BirthJSCalculator(reader, localScaler);
    }


    public void chooseBirth(int perId) {

        Person per = Person.getPersonFromId(perId);
        if (!EventRules.ruleGiveBirth(per)) return;  // Person has died or moved away
        if (per.getGender() == 1) return;            // Exclude males, model should never get here
        // todo: distinguish birth probability by neighborhood type (such as urban, suburban, rural)
        double birthProb = calculator.calculateBirthProbability(per.getAge());
        if (per.getRole() == PersonRole.MARRIED) birthProb *= Properties.get().demographics.marriedScaler;
        else birthProb *= Properties.get().demographics.singleScaler;
        if (SiloUtil.getRandomNumberAsDouble() < birthProb) {
            // For, unto us a child is born
            Household hhOfThisWoman = Household.getHouseholdFromId(per.getHh().getId());
            hhOfThisWoman.addNewbornPerson(hhOfThisWoman.getRace());
            EventManager.countEvent(EventTypes.CHECK_BIRTH);
            householdDataManager.addHouseholdThatChanged(hhOfThisWoman);
            if (perId == SiloUtil.trackPp) {
                SiloUtil.trackWriter.println("Person " + perId + " gave birth to a child.");
            }
        }
    }


    public static double getProbabilityForGirl () {
        return calculator.getProbabilityForGirl();
    }


    public static boolean personCanGiveBirth(int age) {
        return (calculator.calculateBirthProbability(age) > 0);
    }


    public void celebrateBirthday (int personId) {
        // increase age of this person by number of years in simulation period
        Person per = Person.getPersonFromId(personId);
        if (!EventRules.ruleBirthday(per)) return;  // Person has died or moved away
        int age = per.getAge() + Properties.get().demographics.simulationPeriodLength;
        per.setAge(age);
        per.setType(age, per.getGender());
        EventManager.countEvent(EventTypes.BIRTHDAY);
        if (personId == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Celebrated BIRTHDAY of person " +
                    personId + ". New age is " + age + ".");
        }
    }
}
