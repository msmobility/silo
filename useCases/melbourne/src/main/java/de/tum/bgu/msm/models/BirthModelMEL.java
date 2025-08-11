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

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.data.person.PersonRole;
import de.tum.bgu.msm.events.impls.person.BirthEvent;
import de.tum.bgu.msm.health.PersonHealthMEL;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.health.disease.HealthExposures;
import de.tum.bgu.msm.models.demography.birth.BirthModel;
import de.tum.bgu.msm.models.demography.birth.BirthStrategy;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.util.*;

import static de.tum.bgu.msm.data.person.Gender.FEMALE;
import static de.tum.bgu.msm.data.person.Gender.MALE;

/**
 * Simulates birth of children
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 28 December 2009 in Bocholt
 **/

public class BirthModelMEL extends AbstractModel implements BirthModel {

    private final PersonFactory factory;
    private final BirthStrategy strategy;
    private final float localScaler = properties.demographics.localScaler;
    private int currentYear;


    public BirthModelMEL(DataContainer dataContainer, PersonFactory factory,
                         Properties properties, BirthStrategy strategy, Random rnd) {
        super(dataContainer, properties, rnd);
        this.factory = factory;
        this.strategy = strategy;
    }

    @Override
    public void setup() {
    }

    @Override
    public void prepareYear(int year) {}

    @Override
    public Collection<BirthEvent> getEventsForCurrentYear(int year) {
        currentYear = year;
        final List<BirthEvent> events = new ArrayList<>();
        for (Person per : dataContainer.getHouseholdDataManager().getPersons()) {
            final int id = per.getId();
            if (properties.eventRules.birth && personCanGiveBirth(per)) {
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
    public void endYear(int year) {}

    @Override
    public void endSimulation() {}

    private boolean chooseBirth(int perId) {
        final HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        final Person person = householdDataManager.getPersonFromId(perId);
        if (person != null && personCanGiveBirth(person)) {
            // todo: distinguish birth probability by neighborhood type (such as urban, suburban, rural)
            //now it distinguish by number of children at the household
            double birthProb = localScaler * strategy.calculateBirthProbability(person.getAge(), HouseholdUtil.getNumberOfChildren(person.getHousehold()));
            if (person.getRole() == PersonRole.MARRIED) {
                birthProb *= properties.demographics.marriedScaler;
            } else {
                birthProb *= properties.demographics.singleScaler;
            }
            if (random.nextDouble() < birthProb) {
                giveBirth(person);
                return true;
            }
        }
        return false;
    }

    void giveBirth(Person person) {
        final HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        final Household household = person.getHousehold();
        householdDataManager.saveHouseholdMemento(household);
        final int id = householdDataManager.getNextPersonId();
        Gender gender = MALE;
        if (random.nextDouble() <= getProbabilityForGirl()) {
            gender = FEMALE;
        }
        final Person child = factory.giveBirth(person, id, gender);

        //TODO: assume exposure as 0 when born? assume RR as 1.0 when born
        ((PersonHealthMEL)child).updateWeeklyMarginalMetHours(Mode.walk, 0.f);
        ((PersonHealthMEL)child).updateWeeklyMarginalMetHours(Mode.bicycle, 0.f);
        ((PersonHealthMEL)child).setWeeklyMarginalMetHoursSport(0.f);

        Map<String, Float> exposureMap = new HashMap<>();
        exposureMap.put("pm2.5", 0.f);
        exposureMap.put("no2", 0.f);

        ((PersonHealthMEL)child).setWeeklyExposureByPollutantNormalised(exposureMap);
        ((PersonHealthMEL)child).setWeeklyNoiseExposuresNormalised(0.f);
        ((PersonHealthMEL)child).setWeeklyGreenExposuresNormalised(0.f);

        for (HealthExposures exposures : HealthExposures.values()){
            EnumMap<Diseases, Float> rr = new EnumMap<>(Diseases.class);

            for(Diseases diseases : ((DataContainerHealth)dataContainer).getDoseResponseData().get(exposures).keySet()){
                rr.put(diseases, 1.f);
            }
            ((PersonHealthMEL)child).getRelativeRisksByDisease().put(exposures, rr);
        }

        ((PersonHealthMEL) child).getHealthDiseaseTracker().put(currentYear, Arrays.asList("healthy"));

        householdDataManager.addPerson(child);
        householdDataManager.addPersonToHousehold(child, household);
        if (id == SiloUtil.trackPp
                || household.getId() == SiloUtil.trackHh
                || person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("For unto us a child was born... " + person.getId() + " gave birth" +
                    "to a child named " + id + ". Added to household " + household.getId() + ".");
        }
    }


    private double getProbabilityForGirl() {
        return strategy.getProbabilityForGirl();
    }

    private boolean personCanGiveBirth(Person person) {
        //no need to calculate here the exact birth probability, just the possibility of give birth
        return person.getGender() == FEMALE
                && localScaler * strategy.calculateBirthProbability(person.getAge(), 0) > 0;
    }
}
