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
package de.tum.bgu.msm.models;

import com.google.common.collect.*;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.person.PersonMstm;
import de.tum.bgu.msm.data.person.Race;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonRole;
import de.tum.bgu.msm.events.impls.person.MarriageEvent;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.demography.marriage.MarriageModel;
import de.tum.bgu.msm.models.demography.marriage.MarriageStrategy;
import de.tum.bgu.msm.models.relocation.migration.InOutMigrationImpl;
import de.tum.bgu.msm.models.relocation.moves.MovesModelImpl;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Simulates marriage and divorce
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 31 December 2009 in Cologne
 * Revised on 5 March 2015 in Wheaton, MD
 **/
public class MarriageModelMstm extends AbstractModel implements MarriageModel {

    private final static Logger logger = Logger.getLogger(MarriageModelMstm.class);

    private final InOutMigrationImpl iomig;
    private final MovesModelImpl movesModel;
    private final CreateCarOwnershipModel carOwnership;
    private final HouseholdFactory hhFactory;
    private final MarriageStrategy strategy;

    private float interRacialMarriageShare = properties.demographics.interracialMarriageShare;

    private final static int AGE_OFFSET = 10;
    private final static ContiguousSet<Integer> AGE_DIFF_RANGE
            = ContiguousSet.create(Range.closed(-AGE_OFFSET, AGE_OFFSET), DiscreteDomain.integers());
    private Table<Integer, Gender, Double> ageDiffProbabilityByGender;

    /**
     * localMarriageAdjuster serves to adjust from national marriage rates to local conditions
     */
    private double scale = properties.demographics.localMarriageAdjuster;
    private int lackOfDwellingFailedMarriage;

    // ageOffset is the range of ages above and below a persons age that are considered for marriage
    // needs to cover -9 to +9 to reach one person type above and one person type below
    // (e.g., for 25-old person consider partners from 20 to 34). ageOffset is 10 and not 9 to
    // capture if potential partner has celebrated BIRTHDAY already (i.e. turned 35). To improve
    // performance, the person type of this person in the marriage market is not updated.

    public MarriageModelMstm(DataContainer dataContainer, MovesModelImpl movesModel,
                             InOutMigrationImpl iomig, CreateCarOwnershipModel carOwnership,
                             HouseholdFactory hhFactory, Properties properties, MarriageStrategy strategy, Random rnd) {
        super(dataContainer, properties, rnd);
        this.movesModel = movesModel;
        this.iomig = iomig;
        this.carOwnership = carOwnership;
        this.hhFactory = hhFactory;
        this.strategy = strategy;
    }

    @Override
    public void setup() {
        ageDiffProbabilityByGender = calculateAgeDiffProbabilities();
    }

    @Override
    public void prepareYear(int year) {
        lackOfDwellingFailedMarriage = 0;
    }

    @Override
    public Collection<MarriageEvent> getEventsForCurrentYear(int year) {
        final List<MarriageEvent> events = new ArrayList<>();
        if (properties.eventRules.marriage) {
            events.addAll(selectCouplesToGetMarriedThisYear(dataContainer.getHouseholdDataManager().getPersons()));
        }
        return events;
    }

    @Override
    public boolean handleEvent(MarriageEvent event) {
        int id1 = event.getFirstId();
        int id2 = event.getSecondId();
        return marryCouple(id1, id2);
    }

    @Override
    public void endYear(int year) {
        if (lackOfDwellingFailedMarriage > 0) {
            logger.warn("  Encountered " + lackOfDwellingFailedMarriage + " cases " +
                    "where a couple wanted to marry (cohabitate) but could not find vacant dwelling.");
        }
    }

    @Override
    public void endSimulation() {

    }

    private List<MarriageEvent> selectCouplesToGetMarriedThisYear(Collection<Person> persons) {
        logger.info("  Selecting couples to get married this year");

        final List<MarriageEvent> couplesToMarryThisYear = new ArrayList<>();
        final MarriageMarket market = defineMarriageMarket(persons);

        for (Person person : market.activePartners) {
            final Person partner = findPartner(market, person);
            if (partner != null) {
                couplesToMarryThisYear.add(new MarriageEvent(person.getId(), partner.getId()));
                if (person.getId() == SiloUtil.trackPp || partner.getId() == SiloUtil.trackPp) {
                    SiloUtil.trackWriter.println("Person " + person.getId() + " chose " +
                            "person " + partner + " to marry and they were scheduled as a couple to marry this year.");
                }
            }
        }
        logger.info(couplesToMarryThisYear.size() + " couples created.");
        return couplesToMarryThisYear;
    }

    private MarriageMarket defineMarriageMarket(Collection<Person> persons) {

        logger.info("Defining Marriage Market");

        final List<Person> activePartners = new ArrayList<>();
        final Table<Integer, Gender, List<Person>> partnersByAgeAndGender = ArrayTable.create(
                ContiguousSet.create(Range.closed(16, 100), DiscreteDomain.integers()),
                Arrays.asList(Gender.values()));

        for (final Person pp : persons) {
            if (ruleGetMarried(pp)) {
                final double marryProb = getMarryProb(pp);
                if (random.nextDouble() <= marryProb) {
                    activePartners.add(pp);
                } else if (isQualifiedAsPossiblePartner(pp)) {
                    final List<Person> entry = partnersByAgeAndGender.get(pp.getAge(), pp.getGender());
                    if (entry == null) {
                        partnersByAgeAndGender.put(pp.getAge(), pp.getGender(), Lists.newArrayList(pp));
                    } else {
                        entry.add(pp);
                    }
                }
            }
        }
        logger.info(activePartners.size() + " persons actively looking for partner");
        return new MarriageMarket(activePartners, partnersByAgeAndGender);
    }

    private Person findPartner(MarriageMarket market, Person person) {

        final MarriagePreference preference = defineMarriagePreference(person, market);
        final List<Person> possiblePartners = market.getFittingPartners(preference);

        if (preference == null || possiblePartners.isEmpty()) {
            return null;
        }

        final Map<Person, Float> probabilities = new HashMap<>();

        Race personRace = ((PersonMstm) person).getRace();
        float sum = 0;
        for (Person pp : possiblePartners) {
            float prob;
            PersonMstm p = ((PersonMstm) pp);
            if ((preference.sameRace && personRace == p.getRace())
                    || (!preference.sameRace && personRace != p.getRace())) {
                prob = 10000f;
            } else {
                prob = 0.001f;
            }
            sum += prob;
            probabilities.put(p, prob);
        }

        final Person selectedPartner = SiloUtil.select(probabilities, sum, random);
        possiblePartners.remove(selectedPartner);
        return selectedPartner;
    }

    private MarriagePreference defineMarriagePreference(Person person, MarriageMarket market) {

        final Gender partnerGender = person.getGender().opposite();
        final boolean sameRace = random.nextDouble() >= interRacialMarriageShare;

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
            logger.warn("Marriage market ran empty, increase share of persons. Age: " + person.getAge());
            return null;
        }

        final int selectedAge = SiloUtil.select(probabilityByAge, sum, random);
        return new MarriagePreference(sameRace, selectedAge, partnerGender);

    }

    /**
     * returns marriage probability for a person. Single-person households tend to be more likely to get married,
     * thus, emphasize prop to initialize marriage for people from single-person households.
     */
    private double getMarryProb(Person pp) {
        double marryProb = strategy.calculateMarriageProbability(pp) * scale;
        Household hh = pp.getHousehold();
        if (hh.getHhSize() == 1) {
            marryProb *= properties.demographics.onePersonHhMarriageBias;
        }
        return marryProb;
    }

    private boolean isQualifiedAsPossiblePartner(Person person) {
        float share = 0.1f;
        Household hh = person.getHousehold();
        if (hh.getHhSize() == 1) {
            share *= properties.demographics.onePersonHhMarriageBias;
        }
        return random.nextDouble() < share;
    }

    private boolean marryCouple(int id1, int id2) {

        final HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        final Person partner1 = householdDataManager.getPersonFromId(id1);

        if (!ruleGetMarried(partner1)) {
            return false;  // Person got already married this simulation period or died or moved away
        }

        final Person partner2 = householdDataManager.getPersonFromId(id2);
        if (!ruleGetMarried(partner2)) {
            return false;  // Person got already married this simulation period or died or moved away
        }

        final Household hhOfPartner1 = partner1.getHousehold();
        final Household hhOfPartner2 = partner2.getHousehold();

        final Household moveTo = chooseRelocationTarget(partner1, partner2, hhOfPartner1, hhOfPartner2);

        householdDataManager.saveHouseholdMemento(hhOfPartner1);
        householdDataManager.saveHouseholdMemento(hhOfPartner2);
        final boolean success = moveTogether(partner1, partner2, moveTo);

        if (success) {
            partner1.setRole(PersonRole.MARRIED);
            partner2.setRole(PersonRole.MARRIED);
            return true;
        } else {
            if (partner1.getId() == SiloUtil.trackPp
                    || partner2.getId() == SiloUtil.trackPp
                    || moveTo.getId() == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Person " + partner1.getId()
                        + " and person " + partner2.getId()
                        + " of household " + moveTo.getId()
                        + " got married but could not find an appropriate vacant dwelling. "
                        + "Household outmigrated.");
            }
            lackOfDwellingFailedMarriage++;
            return false;
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
                Dwelling dwelling1 = dataContainer.getRealEstateDataManager().getDwelling(household1.getDwellingId());
                Dwelling dwelling2 = dataContainer.getRealEstateDataManager().getDwelling(household2.getDwellingId());
                if (dwelling1.getBedrooms() < dwelling2.getBedrooms()) {
                    moveTo = household2;
                }
            }
        }

        // if household is already crowded, move couple into new household
        if (moveTo.getHhSize() > 3) {
            final int newHhId = dataContainer.getHouseholdDataManager().getNextHouseholdId();
            moveTo = hhFactory.createHousehold(newHhId, -1, 0);
            dataContainer.getHouseholdDataManager().addHousehold(moveTo);
        }
        return moveTo;
    }

    private boolean moveTogether(Person person1, Person person2, Household moveTo) {

        movePerson(person1, moveTo);
        movePerson(person2, moveTo);

        if (person1.getId() == SiloUtil.trackPp
                || person2.getId() == SiloUtil.trackPp
                || person1.getHousehold().getId() == SiloUtil.trackHh
                || person2.getHousehold().getId() == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("Person " + person1.getId() +
                    " and person " + person2.getId() + " got married and moved into household "
                    + moveTo.getId() + ".");
        }

        if (moveTo.getDwellingId() == -1) {
            final int newDwellingId = movesModel.searchForNewDwelling(moveTo);
            if (newDwellingId < 0) {
                iomig.outMigrateHh(moveTo.getId(), true);
                return false;
            } else {
                movesModel.moveHousehold(moveTo, -1, newDwellingId);
                if (carOwnership != null) {
                    carOwnership.simulateCarOwnership(moveTo); // set initial car ownership of new household
                }
            }
        }
        return true;
    }

    private void movePerson(Person person1, Household moveTo) {
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        final Household household1 = person1.getHousehold();
        if (!moveTo.equals(household1)) {
            householdDataManager.removePersonFromHousehold(person1);
            householdDataManager.addPersonToHousehold(person1, moveTo);
            if (HouseholdUtil.checkIfNoAdultsPresent(household1)) {
                moveRemainingChildren(household1, moveTo);
            }
        }
    }

    private void moveRemainingChildren(Household oldHh, Household newHh) {
        List<Person> remainingPersons = new ArrayList<>(oldHh.getPersons().values());
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        for (Person person : remainingPersons) {
            householdDataManager.removePersonFromHousehold(person);
            householdDataManager.addPersonToHousehold(person, newHh);
            if (person.getId() == SiloUtil.trackPp || oldHh.getId() == SiloUtil.trackHh ||
                    newHh.getId() == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Person " +
                        person.getId() + " was moved from household " + oldHh.getId() + " to household " + newHh.getId() +
                        " as remaining child.");
            }
        }
    }

    private Table<Integer, Gender, Double> calculateAgeDiffProbabilities() {

        final Table<Integer, Gender, Double> probabilitiesByAgeDiffAndGender =
                ArrayTable.create(AGE_DIFF_RANGE, Lists.newArrayList(Gender.values()));

        for (int ageDiff : AGE_DIFF_RANGE) {
            int ageFactor = ageDiff;
            for (Gender gender : probabilitiesByAgeDiffAndGender.columnKeySet()) {
                if (gender == Gender.MALE) {
                    // man searches woman
                    ageFactor += properties.demographics.marryAbsAgeDiff;
                } else {
                    // woman searches man
                    ageFactor -= properties.demographics.marryAbsAgeDiff;
                }
                final double probability =
                        1 / Math.exp(Math.pow(ageFactor, 2) * properties.demographics.marryAgeSpreadFac);
                probabilitiesByAgeDiffAndGender.put(ageDiff, gender, probability);
            }
        }
        return probabilitiesByAgeDiffAndGender;
    }


    private boolean ruleGetMarried(Person per) {
        if (per == null) {
            return false;
        }
        PersonRole role = per.getRole();
        return (role == PersonRole.SINGLE || role == PersonRole.CHILD)
                && per.getAge() >= properties.demographics.minMarryAge
                && per.getAge() < 100;
    }

    private final static class MarriagePreference {

        private final boolean sameRace;
        private final int age;
        private final Gender gender;

        private MarriagePreference(boolean sameRace, int age, Gender gender) {
            this.sameRace = sameRace;
            this.age = age;
            this.gender = gender;
        }
    }

    private final static class MarriageMarket {
        final List<Person> activePartners;
        final Table<Integer, Gender, List<Person>> partnersByAgeAndGender;

        private MarriageMarket(List<Person> activePartners,
                               Table<Integer, Gender, List<Person>> passivePartnersByAgeAndGender) {
            this.activePartners = activePartners;
            this.partnersByAgeAndGender = passivePartnersByAgeAndGender;
        }

        private List<Person> getFittingPartners(MarriagePreference preference) {
            if (preference != null) {
                return getFittingPartners(preference.age, preference.gender);
            } else {
                return null;
            }
        }

        private List<Person> getFittingPartners(int age, Gender gender) {
            final List<Person> entry = partnersByAgeAndGender.get(age, gender);
            if (entry == null) {
                return Collections.emptyList();
            }
            return entry;
        }
    }
}