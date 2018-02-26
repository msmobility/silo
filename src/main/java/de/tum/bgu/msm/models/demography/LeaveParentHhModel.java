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
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.properties.Properties;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;

/**
 * Simulates children that leave the parental household
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 30 December 2009 in Cologne
 **/

public class LeaveParentHhModel {

    private double[] lphProbability;

    public LeaveParentHhModel() {
        setupLPHModel();
    }

    private void setupLPHModel() {

        Reader reader;
        if(Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("LeaveParentHhCalcMstm"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("LeaveParentHhCalcMuc"));
        }
        LeaveParentHhJSCalculator calculator = new LeaveParentHhJSCalculator(reader);

        // initialize results for each alternative
        PersonType[] types = PersonType.values();
        lphProbability = new double[types.length];

        //apply the calculator to each alternative
        for (int i = 0; i < types.length; i++) {
            lphProbability[i] = calculator.calculateLeaveParentsProbability(i);
        }
    }


    public void chooseLeaveParentHh(int perId, SiloModelContainer modelContainer, SiloDataContainer dataContainer) {
        // remove person with perId from its household and create new household with this person

        Person per = Person.getPersonFromId(perId);
        if (!EventRules.ruleLeaveParHousehold(per)) return;   // Person got married this simulation period
        if (SiloUtil.getRandomNumberAsDouble() < lphProbability[per.getType().ordinal()]) {

            // search if dwelling is available
            int newDwellingId = modelContainer.getMove().searchForNewDwelling(Collections.singletonList(per));
            if (newDwellingId < 0) {
                if (perId == SiloUtil.trackPp || per.getHh().getId() == SiloUtil.trackHh) SiloUtil.trackWriter.println(
                        "Person " + perId + " wanted to but could not leave parental household " + per.getHh().getId() +
                        " because no appropriate vacant dwelling was found.");
                IssueCounter.countLackOfDwellingFailedLeavingChild();
                return;
            }

            // create new household
            Household hhOfThisPerson = per.getHh();
            hhOfThisPerson.removePerson(per, dataContainer);
            hhOfThisPerson.setType();
            int newHhId = HouseholdDataManager.getNextHouseholdId();
            Household hh = new Household(newHhId, -1,  0);
            hh.addPerson(per);
            hh.setType();
            hh.determineHouseholdRace();
            per.setRole(PersonRole.SINGLE);


            // Move new household
            modelContainer.getMove().moveHousehold(hh, -1, newDwellingId, dataContainer);
            EventManager.countEvent(EventTypes.CHECK_LEAVE_PARENT_HH);
            dataContainer.getHouseholdData().addHouseholdThatChanged(hhOfThisPerson); // consider original household for update in car ownership
            if(Properties.get().main.implementation == Implementation.MUNICH) {
                modelContainer.getCreateCarOwnershipModel().simulateCarOwnership(hh); // set initial car ownership of new household
            }
            if (perId == SiloUtil.trackPp || hhOfThisPerson.getId() == SiloUtil.trackHh ||
                    hh.getId() == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Person " + perId +
                        " has left the parental household " + hhOfThisPerson.getId() +
                        " and established the new household " + newHhId + ".");
            }
        }
    }
}