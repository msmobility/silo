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
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonRole;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.events.MicroEventModel;
import de.tum.bgu.msm.events.impls.person.LeaveParentsEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.autoOwnership.munich.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.relocation.MovesModelI;
import de.tum.bgu.msm.properties.Properties;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simulates children that leave the parental household
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 30 December 2009 in Cologne
 **/
public class LeaveParentHhModel extends AbstractModel implements MicroEventModel<LeaveParentsEvent> {

    private LeaveParentHhJSCalculator calculator;
    private final CreateCarOwnershipModel createCarOwnershipModel;
    private final HouseholdFactory hhFactory;
    private final MovesModelI movesModel;
    private HouseholdDataManager householdData;

    public LeaveParentHhModel(SiloDataContainer dataContainer, MovesModelI move,
                              CreateCarOwnershipModel createCarOwnershipModel, HouseholdFactory hhFactory) {
        super(dataContainer);
        this.movesModel = move;
        this.createCarOwnershipModel = createCarOwnershipModel;
        this.hhFactory = hhFactory;
        this.householdData = dataContainer.getHouseholdData();

        setupLPHModel();
    }

    private void setupLPHModel() {
        Reader reader;
        if (Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("LeaveParentHhCalcMstm"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("LeaveParentHhCalcMuc"));
        }
        calculator = new LeaveParentHhJSCalculator(reader);
    }

    @Override
    public Collection<LeaveParentsEvent> prepareYear(int year) {
        final List<LeaveParentsEvent> events = new ArrayList<>();
        for (Person person : dataContainer.getHouseholdData().getPersons()) {
            if (qualifiesForParentalHHLeave(person)) {
                events.add(new LeaveParentsEvent(person.getId()));
            }
        }
        return events;
    }

    @Override
    public boolean handleEvent(LeaveParentsEvent event) {
        final Person per = householdData.getPersonFromId(event.getPersonId());
        if (per != null && qualifiesForParentalHHLeave(per)) {
            final double prob = calculator.calculateLeaveParentsProbability(per.getType());
            if (SiloUtil.getRandomNumberAsDouble() < prob) {
                return leaveHousehold(per);
            }
        }
        return false;
    }

    @Override
    public void finishYear(int year) {

    }

    boolean leaveHousehold(Person per) {
        // search if dwelling is available
        Household fakeHypotheticalHousehold = hhFactory.createHousehold(-1, -1, 0);
        fakeHypotheticalHousehold.addPerson(per);
        final int newDwellingId = movesModel.searchForNewDwelling(fakeHypotheticalHousehold);
        if (newDwellingId < 0) {
            if (per.getId() == SiloUtil.trackPp || per.getHousehold().getId() == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println(
                        "Person " + per.getId() + " wanted to but could not leave parental Household "
                                + per.getHousehold().getId() + " because no appropriate vacant dwelling was found.");
            }
            IssueCounter.countLackOfDwellingFailedLeavingChild();
            return false;
        }

        final HouseholdDataManager households = dataContainer.getHouseholdData();
        final Household hhOfThisPerson = households.getHouseholdFromId(per.getHousehold().getId());
        households.removePersonFromHousehold(per);

        final int newHhId = households.getNextHouseholdId();
        final Household newHousehold = hhFactory.createHousehold(newHhId, -1, 0);
        dataContainer.getHouseholdData().addHousehold(newHousehold);
        households.addPersonToHousehold(per, newHousehold);
        per.setRole(PersonRole.SINGLE);
        dataContainer.getHouseholdData().addHouseholdThatChanged(hhOfThisPerson); // consider original newHousehold for update in car ownership

        movesModel.moveHousehold(newHousehold, -1, newDwellingId);
        if (Properties.get().main.implementation == Implementation.MUNICH) {
            createCarOwnershipModel.simulateCarOwnership(newHousehold); // set initial car ownership of new newHousehold
        }

        if (per.getId() == SiloUtil.trackPp || hhOfThisPerson.getId() == SiloUtil.trackHh ||
                newHousehold.getId() == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("Person " + per.getId() +
                    " has left the parental newHousehold " + hhOfThisPerson.getId() +
                    " and established the new newHousehold " + newHhId + ".");
        }
        return true;
    }

    private boolean qualifiesForParentalHHLeave(Person person) {
        return (householdData.getHouseholdFromId(person.getHousehold().getId()).getHhSize() >= 2 && person.getRole() == PersonRole.CHILD);
    }
}