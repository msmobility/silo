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
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.events.MicroEventModel;
import de.tum.bgu.msm.events.impls.person.BirthEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static de.tum.bgu.msm.data.person.Gender.FEMALE;
import static de.tum.bgu.msm.data.person.Gender.MALE;

/**
 * Simulates birth of children
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 28 December 2009 in Bocholt
 **/

public class BirthModel extends AbstractModel implements MicroEventModel<BirthEvent> {

    private final PersonFactory factory;
    private BirthJSCalculator calculator;
    private HashMap<PersonRole,HashMap<Integer, double[]>> birthProbabilities;

    public BirthModel(SiloDataContainer dataContainer, PersonFactory factory) {
        super(dataContainer);
        this.factory = factory;
        //setupBirthModel();
        birthProbabilities = setupBirthModelDistribution();
    }

    private void setupBirthModel() {
        final Reader reader;
        if (Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("BirthProbabilityCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("BirthProbabilityCalcMstm"));
        }
        float localScaler = Properties.get().demographics.localScaler;
        calculator = new BirthJSCalculator(reader, localScaler);
    }


    private HashMap<PersonRole,HashMap<Integer, double[]>> setupBirthModelDistribution(){
        NormalDistribution d0 = new NormalDistributionImpl(29.73, 5.40);
        NormalDistribution d1 = new NormalDistributionImpl(32.23, 5.04);
        NormalDistribution d2 = new NormalDistributionImpl(33.43, 5.21);
        NormalDistribution d3 = new NormalDistributionImpl(34.41, 5.32);
        double scale0 = 1654.92f * 2.243;
        double scale1 = 1213.75f * 2.243;
        double scale2 = 422.01f * 2.243;
        double scale3 = 206.29f * 2.243;
        double scale0single = 1654.92f * 0.1;
        double scale1single = 1213.75f * 0.1;
        double scale2single = 422.01f * 0.1;
        double scale3single = 206.29f * 0.1;
        double localScaler = 0.87f;
        double[] prob0married = new double[100];
        double[] prob1married = new double[100];
        double[] prob2married = new double[100];
        double[] prob3married = new double[100];
        double[] prob0single = new double[100];
        double[] prob1single = new double[100];
        double[] prob2single = new double[100];
        double[] prob3single = new double[100];
        for (int age = 15; age < 50; age++){
            prob0married[age] = d0.density((double) age) * scale0 * localScaler;
            prob1married[age] = d1.density((double) age) * scale1 * localScaler;
            prob2married[age] = d2.density((double) age) * scale2 * localScaler;
            prob3married[age] = d3.density((double) age) * scale3 * localScaler;
            prob0single[age] = prob0married[age] * scale0single;
            prob1single[age] = prob0married[age] * scale1single;
            prob2single[age] = prob0married[age] * scale2single;
            prob3single[age] = prob0married[age] * scale3single;
        }
        HashMap<PersonRole,HashMap<Integer, double[]>> probabilities = new HashMap<>();
        HashMap<Integer, double[]> married = new HashMap<>();
        married.put(0,prob0married);
        married.put(1,prob1married);
        married.put(2,prob2married);
        married.put(3,prob3married);
        HashMap<Integer, double[]> single = new HashMap<>();
        single.put(0,prob0single);
        single.put(1,prob1single);
        single.put(2,prob2single);
        single.put(3,prob3single);
        probabilities.put(PersonRole.MARRIED, married);
        probabilities.put(PersonRole.SINGLE, single);
        return probabilities;
    }

    @Override
    public Collection<BirthEvent> prepareYear(int year) {
        final List<BirthEvent> events = new ArrayList<>();
        for (Person per : dataContainer.getHouseholdData().getPersons()) {
            final int id = per.getId();
            if (Properties.get().eventRules.birth && personCanGiveBirth(per)) {
                events.add(new BirthEvent(id));
            }
        }
        return events;
    }

    @Override
    public boolean handleEvent(BirthEvent event) {
        return chooseBirthDistribution(event.getPersonId());
    }

    @Override
    public void finishYear(int year) {
    }

    private boolean chooseBirth(int perId) {
        final HouseholdDataManager householdData = dataContainer.getHouseholdData();
        final Person person = householdData.getPersonFromId(perId);
        if (person != null && personCanGiveBirth(person)) {
            // todo: distinguish birth probability by neighborhood type (such as urban, suburban, rural)
            //now it distinguish by number of children at the household
            double birthProb = calculator.calculateBirthProbability(person.getAge(), HouseholdUtil.getNumberOfChildren(person.getHousehold()));
            if (person.getRole() == PersonRole.MARRIED) {
                birthProb *= Properties.get().demographics.marriedScaler;
            } else {
                birthProb *= Properties.get().demographics.singleScaler;
            }
            if (SiloUtil.getRandomNumberAsDouble() < birthProb) {
                giveBirth(person);
                return true;
            }
        }
        return false;
    }


    private boolean chooseBirthDistribution(int perId) {
        final HouseholdDataManager householdData = dataContainer.getHouseholdData();
        final Person person = householdData.getPersonFromId(perId);
        if (person != null && personCanGiveBirth(person)) {
            double birthProb = birthProbabilities.get(person.getRole()).get(HouseholdUtil.getNumberOfChildren(person.getHousehold()))[person.getAge()];
            if (SiloUtil.getRandomNumberAsDouble() < birthProb) {
                giveBirth(person);
                return true;
            }
        }
        return false;
    }

    void giveBirth(Person person) {
        final HouseholdDataManager householdData = dataContainer.getHouseholdData();
        final Household household = person.getHousehold();
        final int id = householdData.getNextPersonId();
        Gender gender = MALE;
        if (SiloUtil.getRandomNumberAsDouble() <= getProbabilityForGirl()) {
            gender = FEMALE;
        }
        final Person child = factory.createPerson(id, 0, gender, household.getRace(),
                Occupation.TODDLER, 0, 0);
        householdData.addPerson(child);
        child.setRole(PersonRole.CHILD);
        householdData.addPersonToHousehold(child, household);
        householdData.addHouseholdThatChanged(household);
        if (id == SiloUtil.trackPp
                || household.getId() == SiloUtil.trackHh
                || person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("For unto us a child was born... " + person.getId() + " gave birth" +
                    "to a child named " + id + ". Added to household " + household.getId() + ".");
        }
    }


    private double getProbabilityForGirl() {
        //return calculator.getProbabilityForGirl();
        return 0.4867;
    }

    private boolean personCanGiveBirth(Person person) {
        return person.getGender() == FEMALE && calculator.calculateBirthProbability(person.getAge(), HouseholdUtil.getNumberOfChildren(person.getHousehold())) > 0;
    }
}
