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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
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
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

/**
 * Simulates marriage and divorce
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 31 December 2009 in Cologne
 * Revised on 5 March 2015 in Wheaton, MD
 **/

public class MarryDivorceModel {

    private static Logger logger = Logger.getLogger(MarryDivorceModel.class);

    private MarryDivorceJSCalculator calculator;

    private final static int AGE_OFFSET = 10;
    // ageOffset is the range of ages above and below a persons age that are considered for marriage
    // needs to cover -9 to +9 to reach one person type above and one person type below
    // (e.g., for 25-old person consider partners from 20 to 34). ageOffset is 10 and not 9 to
    // capture if potential partner has celebrated BIRTHDAY already (i.e. turned 35). To improve
    // performance, the person type of this person in the marriage market is not updated.

    public MarryDivorceModel() {
        setupModel();
    }

    private void setupModel() {

        // localMarriageAdjuster serves to adjust from national marriage rates to local conditions
        double scale = Properties.get().demographics.localMarriageAdjuster;

        Reader reader;
        if(Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("MarryDivorceCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("MarryDivorceCalcMstm"));
        }
        calculator = new MarryDivorceJSCalculator(reader, scale);
    }

    public List<int[]> selectCouplesToGetMarriedThisYear() {
        // select singles that will get married during this coming year
        if (!EventRules.runMarriages()) {
            return Collections.emptyList();
        }
        logger.info("  Selecting couples to get married this year");
        List<int[]> couplesToMarryThisYear = new ArrayList<>();

        // create HashMap with men and women by age
        Map<String, List<Integer>> ppByAgeAndGender = new HashMap<>();

        for (Person pp : Person.getPersons()) {
            if (EventRules.ruleGetMarried(pp) && pp.getAge() < 100) {
                int size = pp.getHh().getHhSize();
                // put only every fifth person into marriage market, emphasize single-person households
                if (size == 1 && SiloUtil.getRandomNumberAsFloat() > 0.1 * Properties.get().demographics.onePersonHhMarriageBias) {
                    continue;
                }
                if (size != 1 && SiloUtil.getRandomNumberAsFloat() > 0.1) {
                    continue;
                }
                // Store persons by age and gender
                String token = pp.getAge() + "_" + pp.getGender();
                if (ppByAgeAndGender.containsKey(token)) {
                    List<Integer> al = ppByAgeAndGender.get(token);
                    al.add(pp.getId());
                } else {
                    ppByAgeAndGender.put(token, Lists.newArrayList(pp.getId()));
                }
            }
        }

        // create couples
        int highestId = HouseholdDataManager.getHighestPersonIdInUse();
        boolean[] personSelectedForMarriage = SiloUtil.createArrayWithValue(highestId + 1, false);
        float interRacialMarriageShare = Properties.get().demographics.interracialMarriageShare;
        for (Person pp : Person.getPersons()) {
            if (EventRules.ruleGetMarried(pp) && pp.getAge() < 100 && !personSelectedForMarriage[pp.getId()]) {
                double marryProb = calculator.calculateMarriageProbability(pp);   // raw marriage probability for this age/gender group
                // to keep things simple, emphasize prop to initialize marriage for people from single-person households.
                // Single-person household has no influence on whether someone is selected by the marriage initializer
                if (pp.getHh().getHhSize() == 1) {
                    marryProb *= Properties.get().demographics.onePersonHhMarriageBias;
                }
                if (SiloUtil.getRandomNumberAsDouble() >= marryProb){
                    continue;
                }
                // person was selected to find a partner
                personSelectedForMarriage[pp.getId()] = true;

                // First, select interracial or monoracial marriage
                boolean sameRace = true;
                if (SiloUtil.getRandomNumberAsFloat() <= interRacialMarriageShare) {
                    sameRace = false;
                }

                // Second, select age of new partner
                double[] ageProb = new double[AGE_OFFSET * 2 + 1];
                for (int ageDiff = -AGE_OFFSET; ageDiff <= AGE_OFFSET; ageDiff++) {
                    ageProb[ageDiff + AGE_OFFSET] = getAgeDependentProbabilities(pp.getGender(), ageDiff);
                    int thisAge = pp.getAge() + ageDiff;
                    if (pp.getGender() == 1) {
                        if (ppByAgeAndGender.containsKey(thisAge + "_" + 2)) {    // man looking for women
                            ageProb[ageDiff + AGE_OFFSET] *= ppByAgeAndGender.get(thisAge + "_" + 2).size();
                        } else {
                            ageProb[ageDiff + AGE_OFFSET] = 0;
                        }
                    } else {                                                     // woman looking for men
                        if (ppByAgeAndGender.containsKey(thisAge + "_" + 1)) {
                            ageProb[ageDiff + AGE_OFFSET] *= ppByAgeAndGender.get(thisAge + "_" + 1).size();
                        } else {
                            ageProb[ageDiff + AGE_OFFSET] = 0;
                        }
                    }
                }
                if (SiloUtil.getSum(ageProb) == 0) {
                    logger.warn("Marriage market ran empty, increase share of persons. Age: " + pp.getAge());
                    break;
                }
                int selectedAge = SiloUtil.select(ageProb) - AGE_OFFSET + pp.getAge();

                // Third, select partner
                List<Integer> possiblePartners;
                if (pp.getGender() == 1) {   // Look for woman
                    possiblePartners = ppByAgeAndGender.get(selectedAge + "_" + 2);
                } else {                     // Look for man
                    possiblePartners = ppByAgeAndGender.get(selectedAge + "_" + 1);
                }
                float[] partnerProb = SiloUtil.createArrayWithValue(possiblePartners.size(), 0f);
                for (int per = 0; per < possiblePartners.size(); per++) {
                    int personId = possiblePartners.get(per);
                    if (personSelectedForMarriage[personId])
                        continue;  // this person was already selected to get married
                    Race personRace = Person.getPersonFromId(personId).getRace();
                    if ((sameRace && pp.getRace() == personRace) || (!sameRace && pp.getRace() != personRace)) {
                        partnerProb[per] = 10000f;
                    } else {
                        partnerProb[per] = 0.001f;  // set probability to small non-zero value to ensure that model works when marriage market runs almost empty
                    }
                }
                int selectedPartner = possiblePartners.get(SiloUtil.select(partnerProb));
                personSelectedForMarriage[selectedPartner] = true;
                couplesToMarryThisYear.add(new int[]{pp.getId(), selectedPartner});
                if (pp.getId() == SiloUtil.trackPp) SiloUtil.trackWriter.println("Person " + pp.getId() + " chose " +
                        "person " + selectedPartner + " to marry and they were scheduled as a couple to marry this year.");
                if (selectedPartner == SiloUtil.trackPp)
                    SiloUtil.trackWriter.println("Person " + selectedPartner + " was chosen " +
                            "by person " + pp.getId() + " to get married and they were scheduled as a couple to marry this year.");
            }
        }
        return couplesToMarryThisYear;
    }

    private double getAgeDependentProbabilities(int gender, int ageDiff) {
        double marryAbsAgeDiff = Properties.get().demographics.marryAbsAgeDiff;
        double marryAgeSpreadFac = Properties.get().demographics.marryAgeSpreadFac;
        if(gender == 1) {
            return 1 / Math.exp(Math.pow(ageDiff + marryAbsAgeDiff, 2) * marryAgeSpreadFac);  // man searches woman
        } else if(gender ==2) {
            return 1 / Math.exp(Math.pow(ageDiff - marryAbsAgeDiff, 2) * marryAgeSpreadFac);  // woman searches man
        } else {
            throw new IllegalArgumentException("Unknwon gender " + gender);
        }
    }


    public void marryCouple(int[] couple, SiloModelContainer modelContainer, SiloDataContainer dataContainer) {
        Person partner1 = Person.getPersonFromId(couple[0]);
        if (!EventRules.ruleGetMarried(partner1)) {
            return;  // Person got already married this simulation period or died or moved away
        }
        Person partner2 = Person.getPersonFromId(couple[1]);
        if (!EventRules.ruleGetMarried(partner2)) {
            return;  // Person got already married this simulation period or died or moved away
        }

        Household hhOfPartner1 = partner1.getHh();
        Household hhOfPartner2 = partner2.getHh();
        int moveTo = 1;
        if (partner1.getRole().equals(PersonRole.CHILD) && !partner2.getRole().equals(PersonRole.CHILD))
            moveTo = 2; // if one is not a child, move into that household
        if (!partner1.getRole().equals(PersonRole.CHILD) && partner2.getRole().equals(PersonRole.CHILD)) moveTo = 1;
        if (!partner1.getRole().equals(PersonRole.CHILD) && !partner2.getRole().equals(PersonRole.CHILD) ||
                partner1.getRole().equals(PersonRole.CHILD) && partner2.getRole().equals(PersonRole.CHILD)) {
            int hhSizeA = hhOfPartner1.getHhSize();                                                                  // if both are/areNot children, move into smaller hh size
            int hhSizeB = hhOfPartner2.getHhSize();
            if (hhSizeA > hhSizeB) {
                moveTo = 2;
            } else if (hhSizeA == hhSizeB) {                                                                         // if hhSize is identical, move into larger dwelling
                Dwelling a = Dwelling.getDwellingFromId(hhOfPartner1.getDwellingId());
                Dwelling b = Dwelling.getDwellingFromId(hhOfPartner2.getDwellingId());
                if (b.getBedrooms() > a.getBedrooms()) moveTo = 2;
            }
        }
        // if household is already crowded, move couple into new household
        if ((moveTo == 1 && hhOfPartner1.getHhSize() > 3) || (moveTo == 2 && hhOfPartner2.getHhSize() > 3)) {
            moveTo = 3;
        }
        if (moveTo == 1) {
            moveMarriedPersons(hhOfPartner2, hhOfPartner1, partner2, partner1, dataContainer);
        } else if (moveTo == 2) {
            moveMarriedPersons(hhOfPartner1, hhOfPartner2, partner1, partner2, dataContainer);
        } else {
            // create new household for newly-wed couple
            int newHhId = HouseholdDataManager.getNextHouseholdId();
            Household newHh = new Household(newHhId, -1, 0);
            newHh.addPerson(partner1);
            newHh.addPerson(partner2);
            hhOfPartner1.removePerson(partner1, dataContainer);
            if (hhOfPartner1.checkIfOnlyChildrenRemaining()) {
                moveRemainingChildren(hhOfPartner1, newHh, dataContainer);
            }
            hhOfPartner2.removePerson(partner2, dataContainer);
            if (hhOfPartner2.checkIfOnlyChildrenRemaining()) {
                moveRemainingChildren(hhOfPartner2, newHh, dataContainer);
            }
            int newDwellingId = modelContainer.getMove().searchForNewDwelling(ImmutableList.of(partner1, partner2));
            if (newDwellingId < 0) {
                modelContainer.getIomig().outMigrateHh(newHhId, true, dataContainer);
                if (partner1.getId() == SiloUtil.trackPp || partner2.getId() == SiloUtil.trackPp || newHhId == SiloUtil.trackHh)
                    SiloUtil.trackWriter.println("Person " + partner1.getId() + " and person " + partner2.getId() +
                            " of household " + newHhId + " got married but could not find an appropriate vacant dwelling. " +
                            "Household outmigrated.");
                IssueCounter.countLackOfDwellingFailedMarriage();
                return;
            }
            modelContainer.getMove().moveHousehold(newHh, -1, newDwellingId, dataContainer);
            if (Properties.get().main.implementation == Implementation.MUNICH) {
                modelContainer.getCreateCarOwnershipModel().simulateCarOwnership(newHh); // set initial car ownership of new household
            }
        }
        partner1.setRole(PersonRole.MARRIED);
        partner2.setRole(PersonRole.MARRIED);
        EventManager.countEvent(EventTypes.CHECK_MARRIAGE);
        dataContainer.getHouseholdData().addHouseholdThatChanged(hhOfPartner1);
        dataContainer.getHouseholdData().addHouseholdThatChanged(hhOfPartner2);
    }

    private void moveMarriedPersons(Household hhOfMovingPerson, Household hhOfStayingPerson, Person movingPerson, Person stayingPerson, SiloDataContainer dataContainer) {
        hhOfMovingPerson.removePerson(movingPerson, dataContainer);
        hhOfStayingPerson.addPerson(movingPerson);
        if (hhOfMovingPerson.checkIfOnlyChildrenRemaining()) {
            moveRemainingChildren(hhOfMovingPerson, hhOfStayingPerson, dataContainer);
        }
        if (movingPerson.getId() == SiloUtil.trackPp || stayingPerson.getId() == SiloUtil.trackPp || hhOfMovingPerson.getId() == SiloUtil.trackHh ||
                hhOfStayingPerson.getId() == SiloUtil.trackHh)
            SiloUtil.trackWriter.println("Person " + stayingPerson.getId() +
                    " and person " + movingPerson.getId() + " got married and moved into household " + stayingPerson.getId() + ".");
    }


    private void moveRemainingChildren(Household oldHh, Household newHh, SiloDataContainer dataContainer) {
        List<Person> remainingPersons = new ArrayList<>(oldHh.getPersons());
        for (Person person : remainingPersons) {
            oldHh.removePerson(person, dataContainer);
            newHh.addPerson(person);
            if (person.getId() == SiloUtil.trackPp || oldHh.getId() == SiloUtil.trackHh ||
                    newHh.getId() == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Person " +
                        person.getId() + " was moved from household " + oldHh.getId() + " to household " + newHh.getId() +
                        " as remaining child.");
            }
        }
    }


    public void chooseDivorce(int perId, SiloModelContainer modelContainer, SiloDataContainer dataContainer) {
        // select if person gets divorced/leaves joint dwelling

        Person per = Person.getPersonFromId(perId);
        if (!EventRules.ruleGetDivorced(per)) {
            return;
        }
        double probability = calculator.calculateDivorceProbability(per.getType().ordinal()) / 2;
        if (SiloUtil.getRandomNumberAsDouble() < probability) {
            // check if vacant dwelling is available
            int newDwellingId = modelContainer.getMove().searchForNewDwelling(Collections.singletonList(per));
            if (newDwellingId < 0) {
                if (perId == SiloUtil.trackPp || per.getHh().getId() == SiloUtil.trackHh) {
                    SiloUtil.trackWriter.println(
                            "Person " + perId + " wanted to but could not divorce from household " + per.getHh().getId() +
                                    " because no appropriate vacant dwelling was found.");
                }
                IssueCounter.countLackOfDwellingFailedDivorce();
                return;
            }

            // divorce
            Household oldHh = per.getHh();
            Person divorcedPerson = HouseholdDataManager.findMostLikelyPartner(per, oldHh);
            divorcedPerson.setRole(PersonRole.SINGLE);
            per.setRole(PersonRole.SINGLE);
            oldHh.removePerson(per, dataContainer);
            oldHh.determineHouseholdRace();
            oldHh.setType();

            int newHhId = HouseholdDataManager.getNextHouseholdId();
            Household newHh = new Household(newHhId, -1, 0);
            newHh.addPerson(per);
            newHh.setType();
            newHh.determineHouseholdRace();
            // move divorced person into new dwelling
            modelContainer.getMove().moveHousehold(newHh, -1, newDwellingId, dataContainer);
            if (perId == SiloUtil.trackPp || newHh.getId() == SiloUtil.trackHh ||
                    oldHh.getId() == SiloUtil.trackHh) SiloUtil.trackWriter.println("Person " + perId +
                    " has divorced from household " + oldHh + " and established the new household " +
                    newHhId + ".");
            EventManager.countEvent(EventTypes.CHECK_DIVORCE);
            dataContainer.getHouseholdData().addHouseholdThatChanged(oldHh); // consider original household for update in car ownership
            if (Properties.get().main.implementation == Implementation.MUNICH) {
                modelContainer.getCreateCarOwnershipModel().simulateCarOwnership(newHh); // set initial car ownership of new household
            }
        }
    }
}