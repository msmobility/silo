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
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.relocation.MovesModelI;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.CreateCarOwnershipModel;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;

/**
 * Simulates children that leave the parental household
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 30 December 2009 in Cologne
 **/
public class LeaveParentHhModel extends AbstractModel {

    private LeaveParentHhJSCalculator calculator;
    private final CreateCarOwnershipModel createCarOwnershipModel;
    private final MovesModelI movesModel;

    public LeaveParentHhModel(SiloDataContainer dataContainer, MovesModelI move, CreateCarOwnershipModel createCarOwnershipModel) {
        super(dataContainer);
        this.movesModel = move;
        this.createCarOwnershipModel = createCarOwnershipModel;
        setupLPHModel();
    }

    private void setupLPHModel() {
        Reader reader;
        if(Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("LeaveParentHhCalcMstm"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("LeaveParentHhCalcMuc"));
        }
        calculator = new LeaveParentHhJSCalculator(reader);
    }

    public void chooseLeaveParentHh(int perId) {
        final HouseholdDataManager householdData = dataContainer.getHouseholdData();
        final Person per = householdData.getPersonFromId(perId);
        if (!EventRules.ruleLeaveParHousehold(per)){
            return;
        }
        final double prob = calculator.calculateLeaveParentsProbability(per.getType());
        if (SiloUtil.getRandomNumberAsDouble() < prob) {
            leaveHousehold(per);
        }
    }

    void leaveHousehold(Person per) {
        // search if dwelling is available
        final int newDwellingId = movesModel.searchForNewDwelling(Collections.singletonList(per));
        if (newDwellingId < 0) {
            if (per.getId() == SiloUtil.trackPp || (per.getHh() != null && per.getHh().getId() == SiloUtil.trackHh)) {
                SiloUtil.trackWriter.println(
                        "Person " + per.getId() + " wanted to but could not leave parental Household " + per.getHh().getId() +
                                " because no appropriate vacant dwelling was found.");
            }
            IssueCounter.countLackOfDwellingFailedLeavingChild();
            return;
        }

        final HouseholdDataManager households = dataContainer.getHouseholdData();
        final Household hhOfThisPerson = per.getHh();
        households.removePersonFromHousehold(per);

        final int newHhId = households.getNextHouseholdId();
        final Household newHousehold = households.createHousehold(newHhId, -1,  0);
        households.addPersonToHousehold(per, newHousehold);
        per.setRole(PersonRole.SINGLE);
        dataContainer.getHouseholdData().addHouseholdThatChanged(hhOfThisPerson); // consider original newHousehold for update in car ownership

        movesModel.moveHousehold(newHousehold, -1, newDwellingId);
        EventManager.countEvent(EventTypes.CHECK_LEAVE_PARENT_HH);
        if(Properties.get().main.implementation == Implementation.MUNICH) {
            createCarOwnershipModel.simulateCarOwnership(newHousehold); // set initial car ownership of new newHousehold
        }

        if (per.getId() == SiloUtil.trackPp || hhOfThisPerson.getId() == SiloUtil.trackHh ||
                newHousehold.getId() == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("Person " + per.getId() +
                    " has left the parental newHousehold " + hhOfThisPerson.getId() +
                    " and established the new newHousehold " + newHhId + ".");
        }
    }
}