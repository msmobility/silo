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
package de.tum.bgu.msm.demography;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.IssueCounter;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ResourceBundle;

/**
 * Simulates children that leave the parental household
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 30 December 2009 in Cologne
 **/

public class LeaveParentHhModel {

    private double[] lphProbability;

    public LeaveParentHhModel() {
        // constructor
        setupLPHModel();
    }

    private void setupLPHModel() {

        // read properties
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("LeaveParentHhCalc"));
        LeaveParentHhJSCalculator calculator = new LeaveParentHhJSCalculator(reader, false);

        // initialize results for each alternative
        PersonType[] types = PersonType.values();
        lphProbability = new double[types.length];

        //apply the calculator to each alternative
        for (int i = 0; i < types.length; i++) {
            // set calculator bindings
            calculator.setPersonType(i);
            //calculate
            try {
                lphProbability[i] = calculator.calculate();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
    }


    public void chooseLeaveParentHh(int perId, SiloModelContainer modelContainer, SiloDataContainer dataContainer) {
        // remove person with perId from its household and create new household with this person

        Person per = Person.getPersonFromId(perId);
        if (!EventRules.ruleLeaveParHousehold(per)) return;   // Person got married this simulation period
        if (SiloUtil.getRandomNumberAsDouble() < lphProbability[per.getType().ordinal()]) {

            // search if dwelling is available
            int newDwellingId = modelContainer.getMove().searchForNewDwelling(new Person[]{per}, modelContainer);
            if (newDwellingId < 0) {
                if (perId == SiloUtil.trackPp || per.getHhId() == SiloUtil.trackHh) SiloUtil.trackWriter.println(
                        "Person " + perId + " wanted to but could not leave parental household " + per.getHhId() +
                        " because no appropriate vacant dwelling was found.");
                IssueCounter.countLackOfDwellingFailedLeavingChild();
                return;
            }

            // create new household
            Household hhOfThisPerson = Household.getHouseholdFromId(per.getHhId());
            hhOfThisPerson.removePerson(per, dataContainer);
            hhOfThisPerson.setType();
            int newHhId = HouseholdDataManager.getNextHouseholdId();
            Household hh = new Household(newHhId, -1, -1, 1, 0);
            hh.addPersonForInitialSetup(per);
            hh.setType();
            hh.setHouseholdRace();
            per.setRole(PersonRole.single);

            // Move new household
            modelContainer.getMove().moveHousehold(hh, -1, newDwellingId, dataContainer);
            EventManager.countEvent(EventTypes.checkLeaveParentHh);
            dataContainer.getHouseholdData().addHouseholdThatChanged(hhOfThisPerson);
            if (perId == SiloUtil.trackPp || hhOfThisPerson.getId() == SiloUtil.trackHh ||
                    hh.getId() == SiloUtil.trackHh) SiloUtil.trackWriter.println("Person " + perId +
                    " has left the parental household " + hhOfThisPerson.getId() +
                    " and established the new household " + newHhId + ".");
        }
    }
}