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

import com.google.common.collect.*;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.relocation.InOutMigration;
import de.tum.bgu.msm.models.relocation.MovesModelI;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.CreateCarOwnershipModel;
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

public class MarryDivorceModel extends AbstractModel {

    private final static Logger LOGGER = Logger.getLogger(MarryDivorceModel.class);

    private final MovesModelI movesModel;
    private final InOutMigration iomig;
    private final CreateCarOwnershipModel carOwnership;

    private MarryDivorceJSCalculator calculator;

    private float interRacialMarriageShare = Properties.get().demographics.interracialMarriageShare;

    private final static int AGE_OFFSET = 10;
    private final static ContiguousSet<Integer> AGE_DIFF_RANGE
            = ContiguousSet.create(Range.closed(-AGE_OFFSET, AGE_OFFSET), DiscreteDomain.integers());
    private Table<Integer, Integer, Double> ageDiffProbabilityByGender;
    // ageOffset is the range of ages above and below a persons age that are considered for marriage
    // needs to cover -9 to +9 to reach one person type above and one person type below
    // (e.g., for 25-old person consider partners from 20 to 34). ageOffset is 10 and not 9 to
    // capture if potential partner has celebrated BIRTHDAY already (i.e. turned 35). To improve
    // performance, the person type of this person in the marriage market is not updated.

    public MarryDivorceModel(SiloDataContainer dataContainer, MovesModelI movesModel,
                             InOutMigration iomig, CreateCarOwnershipModel carOwnership) {
        super(dataContainer);
        this.movesModel = movesModel;
        this.iomig = iomig;
        this.carOwnership = carOwnership;
        setupModel();
    }

    private void setupModel() {
        // localMarriageAdjuster serves to adjust from national marriage rates to local conditions
        double scale = Properties.get().demographics.localMarriageAdjuster;

        Reader reader;
        if (Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("MarryDivorceCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("MarryDivorceCalcMstm"));
        }
        calculator = new MarryDivorceJSCalculator(reader, scale);
        ageDiffProbabilityByGender = calculateAgeDiffProbabilities();
    }

    public List<Couple> selectCouplesToGetMarriedThisYear(Collection<Person> persons) {
        if (!EventRules.runMarriages()) {
            return Collections.emptyList();
        }
        LOGGER.info("  Selecting couples to get married this year");

        final List<Couple> couplesToMarryThisYear = new ArrayList<>();
        final MarriageMarket market = defineMarriageMarket(persons);

        for (Person person : market.activePartners) {
            final Person partner = findPartner(market, person);
            if (partner != null) {
                couplesToMarryThisYear.add(new Couple(person, partner));
                if (person.getId() == SiloUtil.trackPp || partner.getId() == SiloUtil.trackPp) {
                    SiloUtil.trackWriter.println("Person " + person.getId() + " chose " +
                            "person " + partner + " to marry and they were scheduled as a couple to marry this year.");
                }
            }
        }
        LOGGER.info(couplesToMarryThisYear.size() + " couples created.");
        return couplesToMarryThisYear;
    }

    private MarriageMarket defineMarriageMarket(Collection<Person> persons) {

        LOGGER.info("Defining Marriage Market");

        final List<Person> activePartners = new ArrayList<>();
        final Table<Integer, Integer, List<Person>> partnersByAgeAndGender = ArrayTable.create(
                ContiguousSet.create(Range.closed(16, 100), DiscreteDomain.integers()),
                ContiguousSet.create(Range.closed(1, 2), DiscreteDomain.integers()));

        for (final Person pp : persons) {
            if (EventRules.ruleGetMarried(pp)) {
                final double marryProb = getMarryProb(pp);
                if (SiloUtil.getRandomNumberAsDouble() <= marryProb) {
                    activePartners.add(pp);
                } else if (isQualifiedAsPossiblePartner(pp)) {
                    final List<Person> entry = partnersByAgeAndGender.get(pp.getAge(), pp.getGender());
                    if(entry == null) {
                        partnersByAgeAndGender.put(pp.getAge(), pp.getGender(), Lists.newArrayList(pp));
                    } else {
                        entry.add(pp);
                    }
                }
            }
        }
        LOGGER.info(activePartners.size() + " persons actively looking for partner");
        return new MarriageMarket(activePartners, partnersByAgeAndGender);
    }

    private Person findPartner(MarriageMarket market, Person person) {

        final MarriagePreference preference = defineMarriagePreference(person, market);
        final List<Person> possiblePartners = market.getFittingPartners(preference);

        if (preference == null || possiblePartners.isEmpty()) {
            return null;
        }

        final Map<Person, Float> probabilities = new HashMap<>();

        float sum = 0;
        for (Person p : possiblePartners) {
            float prob;
            if ((preference.sameRace && person.getRace() == p.getRace())
                    || (!preference.sameRace && person.getRace() != p.getRace())) {
                prob = 10000f;
            } else {
                prob = 0.001f;
            }
            sum += prob;
            probabilities.put(p, prob);
        }

        final Person selectedPartner = SiloUtil.select(probabilities, sum);
        possiblePartners.remove(selectedPartner);
        return selectedPartner;
    }

    private MarriagePreference defineMarriagePreference(Person person, MarriageMarket market) {

        final int partnerGender = (person.getGender() == 1 ? 2 : 1);
        final boolean sameRace = SiloUtil.getRandomNumberAsFloat() >= interRacialMarriageShare;

        final Map<Integer, Double> probabilityByAge = new HashMap<>();

        double sum = 0;
        for (int ageDiff : AGE_DIFF_RANGE) {
            final int resultingAge = person.getAge() + ageDiff;
            double probability = ageDiffProbabilityByGender.get(ageDiff, person.getGender());
            probability *= market.getFittingPartners(resultingAge, partnerGender).size();
            sum += probability;
            probabilityByAge.put(resultingAge, probability);
        }

        if (sum == 0) {
            LOGGER.warn("Marriage market ran empty, increase share of persons. Age: " + person.getAge());
            return null;
        }

        final int selectedAge = SiloUtil.select(probabilityByAge, sum);
        return new MarriagePreference(sameRace, selectedAge, partnerGender);

    }

    /**
     * returns marriage probability for a person. Single-person households tend to be more likely to get married,
     * thus, emphasize prop to initialize marriage for people from single-person households.
     */
    private double getMarryProb(Person pp) {
        double marryProb = calculator.calculateMarriageProbability(pp);
        if (pp.getHh().getHhSize() == 1) {
            marryProb *= Properties.get().demographics.onePersonHhMarriageBias;
        }
        return marryProb;
    }

    private boolean isQualifiedAsPossiblePartner(Person person) {
        float share = 0.1f;
        if (person.getHh().getHhSize() == 1) {
            share *= Properties.get().demographics.onePersonHhMarriageBias;
        }
        return SiloUtil.getRandomNumberAsFloat() < share;
    }

    public void marryCouple(int[] couple) {

        final HouseholdDataManager householdData = dataContainer.getHouseholdData();
        final Person partner1 = householdData.getPersonFromId(couple[0]);

        if (!EventRules.ruleGetMarried(partner1)) {
            return;  // Person got already married this simulation period or died or moved away
        }

        final Person partner2 = householdData.getPersonFromId(couple[1]);
        if (!EventRules.ruleGetMarried(partner2)) {
            return;  // Person got already married this simulation period or died or moved away
        }

        final Household hhOfPartner1 = partner1.getHh();
        final Household hhOfPartner2 = partner2.getHh();

        final Household moveTo = chooseRelocationTarget(partner1, partner2, hhOfPartner1, hhOfPartner2);

        final boolean success = moveTogether(partner1, partner2, moveTo);

        if (success) {
            partner1.setRole(PersonRole.MARRIED);
            partner2.setRole(PersonRole.MARRIED);
            EventManager.countEvent(EventTypes.CHECK_MARRIAGE);
            householdData.addHouseholdThatChanged(hhOfPartner1);
            householdData.addHouseholdThatChanged(hhOfPartner2);
        } else {
            if (partner1.getId() == SiloUtil.trackPp
                    || partner2.getId() == SiloUtil.trackPp
                    || moveTo.getId() == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Person " + partner1.getId()
                        + " and person " + partner2.getId()
                        + " of household " + moveTo.getId()
                        + " got married but could not find an appropriate vacant dwelling. "
                        + "Household outmigrated.");
                IssueCounter.countLackOfDwellingFailedMarriage();
            }
        }
    }

    private Household chooseRelocationTarget(Person partner1, Person partner2, Household household1, Household household2) {

        final int hhSize1 = household1.getHhSize();
        final int hhSize2 = household2.getHhSize();
        final PersonRole role1 = partner1.getRole();
        final PersonRole role2 = partner2.getRole();

        Household moveTo = household1;

        if (role1.equals(PersonRole.CHILD) && !role2.equals(PersonRole.CHILD)) {
            moveTo = household2; // if one is not a child, move into that household
        } else if (!role1.equals(PersonRole.CHILD) && role2.equals(PersonRole.CHILD)) {
            moveTo = household1;
        } else if (role1 == role2) {
            // if both are/areNot children, move into smaller hh size
            if (hhSize1 > hhSize2) {
                moveTo = household2;
            } else if (hhSize1 == hhSize2) {
                // if hhSize is identical, move into larger dwelling
                Dwelling dwelling1 = dataContainer.getRealEstateData().getDwelling(household1.getDwellingId());
                Dwelling dwelling2 = dataContainer.getRealEstateData().getDwelling(household2.getDwellingId());
                if (dwelling1.getBedrooms() < dwelling2.getBedrooms()) {
                    moveTo = household2;
                }
            }
        }

        // if household is already crowded, move couple into new household
        if (moveTo.getHhSize() > 3) {
            final int newHhId = dataContainer.getHouseholdData().getNextHouseholdId();
            moveTo = dataContainer.getHouseholdData().createHousehold(newHhId, -1, 0);
        }
        return moveTo;
    }

    private boolean moveTogether(Person person1, Person person2, Household moveTo) {

        movePerson(person1, moveTo);
        movePerson(person2, moveTo);

        if (person1.getId() == SiloUtil.trackPp
                || person2.getId() == SiloUtil.trackPp
                || person1.getHh().getId() == SiloUtil.trackHh
                || person2.getHh().getId() == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("Person " + person1.getId() +
                    " and person " + person2.getId() + " got married and moved into household "
                    + moveTo.getId() + ".");
        }

        if (moveTo.getDwellingId() == -1) {
            final int newDwellingId = movesModel.searchForNewDwelling(ImmutableList.of(person1, person2));
            if (newDwellingId < 0) {
                iomig.outMigrateHh(moveTo.getId(), true);
                return false;
            } else {
                movesModel.moveHousehold(moveTo, -1, newDwellingId);
                if (Properties.get().main.implementation == Implementation.MUNICH) {
                    carOwnership.simulateCarOwnership(moveTo); // set initial car ownership of new household
                }
            }
        }
        return true;
    }

    private void movePerson(Person person1, Household moveTo) {
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        final Household household1 = person1.getHh();
        if (!moveTo.equals(household1)) {
            householdData.removePersonFromHousehold(person1);
            householdData.addPersonToHousehold(person1, moveTo);
            if (household1.checkIfOnlyChildrenRemaining()) {
                moveRemainingChildren(household1, moveTo);
            }
        }
    }

    private void moveRemainingChildren(Household oldHh, Household newHh) {
        List<Person> remainingPersons = new ArrayList<>(oldHh.getPersons());
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        for (Person person : remainingPersons) {
            householdData.removePersonFromHousehold(person);
            householdData.addPersonToHousehold(person, newHh);
            if (person.getId() == SiloUtil.trackPp || oldHh.getId() == SiloUtil.trackHh ||
                    newHh.getId() == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Person " +
                        person.getId() + " was moved from household " + oldHh.getId() + " to household " + newHh.getId() +
                        " as remaining child.");
            }
        }
    }

    private Table<Integer, Integer, Double> calculateAgeDiffProbabilities() {

        final Table<Integer, Integer, Double> probabilitiesByAgeDiffAndGender =
                ArrayTable.create(AGE_DIFF_RANGE, Lists.newArrayList(1, 2));

        for (int ageDiff : AGE_DIFF_RANGE) {
            int ageFactor = ageDiff;
            for (int gender : probabilitiesByAgeDiffAndGender.columnKeySet()) {
                if (gender == 1) {
                    // man searches woman
                    ageFactor += Properties.get().demographics.marryAbsAgeDiff;
                } else {
                    // woman searches man
                    ageFactor -= Properties.get().demographics.marryAbsAgeDiff;
                }
                final double probability =
                        1 / Math.exp(Math.pow(ageFactor, 2) * Properties.get().demographics.marryAgeSpreadFac);
                probabilitiesByAgeDiffAndGender.put(ageDiff, gender, probability);
            }
        }
        return probabilitiesByAgeDiffAndGender;
    }


    public void chooseDivorce(int perId) {
        // select if person gets divorced/leaves joint dwelling

        final HouseholdDataManager householdData = dataContainer.getHouseholdData();
        Person per = householdData.getPersonFromId(perId);
        if (!EventRules.ruleGetDivorced(per)) {
            return;
        }
        double probability = calculator.calculateDivorceProbability(per.getType().ordinal()) / 2;
        if (SiloUtil.getRandomNumberAsDouble() < probability) {
            // check if vacant dwelling is available
            int newDwellingId = movesModel.searchForNewDwelling(Collections.singletonList(per));
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
            householdData.removePersonFromHousehold(per);
            oldHh.determineHouseholdRace();
            oldHh.setType();

            int newHhId = householdData.getNextHouseholdId();
            Household newHh = householdData.createHousehold(newHhId, -1, 0);
            householdData.addPersonToHousehold(per, newHh);
            newHh.setType();
            newHh.determineHouseholdRace();
            // move divorced person into new dwelling
            movesModel.moveHousehold(newHh, -1, newDwellingId);
            if (perId == SiloUtil.trackPp || newHh.getId() == SiloUtil.trackHh ||
                    oldHh.getId() == SiloUtil.trackHh) SiloUtil.trackWriter.println("Person " + perId +
                    " has divorced from household " + oldHh + " and established the new household " +
                    newHhId + ".");
            EventManager.countEvent(EventTypes.CHECK_DIVORCE);
            householdData.addHouseholdThatChanged(oldHh); // consider original household for update in car ownership
            if (Properties.get().main.implementation == Implementation.MUNICH) {
                carOwnership.simulateCarOwnership(newHh); // set initial car ownership of new household
            }
        }
    }

    private final static class MarriagePreference {

        private final boolean sameRace;
        private final int age;
        private final int gender;

        private MarriagePreference(boolean sameRace, int age, int gender) {
            this.sameRace = sameRace;
            this.age = age;
            this.gender = gender;
        }
    }

    private final static class MarriageMarket {
        final List<Person> activePartners;
        final Table<Integer, Integer, List<Person>> partnersByAgeAndGender;

        private MarriageMarket(List<Person> activePartners,
                               Table<Integer, Integer, List<Person>> passivePartnersByAgeAndGender) {
            this.activePartners = activePartners;
            this.partnersByAgeAndGender = passivePartnersByAgeAndGender;
        }

        private List<Person> getFittingPartners(MarriagePreference preference) {
            if(preference != null) {
                return getFittingPartners(preference.age, preference.gender);
            } else {
                return null;
            }
        }

        private List<Person> getFittingPartners(int age, int gender) {
            final List<Person> entry = partnersByAgeAndGender.get(age, gender);
            if(entry == null) {
                return Collections.emptyList();
            }
            return entry;
        }
    }
}