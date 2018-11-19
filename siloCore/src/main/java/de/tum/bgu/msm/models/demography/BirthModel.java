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
import java.util.*;

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
    private Map<String, Double> parametersMap;

    public BirthModel(SiloDataContainer dataContainer, PersonFactory factory, Map<String, Double> parametersMap) {
        super(dataContainer);
        this.factory = factory;
        this.parametersMap = parametersMap;
        //setupBirthModel();
        setupBirthModelDistribution();
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


    private void setupBirthModelDistribution(){

        NormalDistribution d0 = new NormalDistributionImpl(parametersMap.get("BirthFirstChildMean"), parametersMap.get("BirthFirstChildDeviation"));
        double scaleFirstChild = parametersMap.get("BirthFirstChildScale");
        NormalDistribution d1 = new NormalDistributionImpl(parametersMap.get("BirthSecondChildMean"), parametersMap.get("BirthSecondChildDeviation"));
        double scaleSecondChild = parametersMap.get("BirthSecondChildScale");
        NormalDistribution d2 = new NormalDistributionImpl(parametersMap.get("BirthThirdChildMean"), parametersMap.get("BirthThirdChildDeviation"));
        double scaleThirdChild = parametersMap.get("BirthThirdChildScale");
        NormalDistribution d3 = new NormalDistributionImpl(parametersMap.get("BirthFourthChildMean"), parametersMap.get("BirthFourthChildDeviation"));
        double scaleFourthChild = parametersMap.get("BirthFourthChildScale");

        double scaleSingle = parametersMap.get("BirthSingleScaler");
        double marriedProportion = parametersMap.get("BirthProportionMarried");
        double scaleMarried = (1 - scaleSingle + scaleSingle * marriedProportion) / marriedProportion;

        double localScaler = parametersMap.get("BirthLocalScaler");

        double[] prob0married = new double[101];
        double[] prob1married = new double[101];
        double[] prob2married = new double[101];
        double[] prob3married = new double[101];
        double[] prob0single = new double[101];
        double[] prob1single = new double[101];
        double[] prob2single = new double[101];
        double[] prob3single = new double[101];
        for (int age = 15; age < 50; age++){
            prob0married[age] = d0.density((double) age) * scaleFirstChild * localScaler * scaleMarried;
            prob1married[age] = d1.density((double) age) * scaleSecondChild * localScaler * scaleMarried;
            prob2married[age] = d2.density((double) age) * scaleThirdChild * localScaler * scaleMarried;
            prob3married[age] = d3.density((double) age) * scaleFourthChild * localScaler * scaleMarried;
            prob0single[age] = d0.density((double) age) * scaleFirstChild * localScaler * scaleSingle;
            prob1single[age] = d1.density((double) age) * scaleSecondChild * localScaler * scaleSingle;
            prob2single[age] = d2.density((double) age) * scaleThirdChild * localScaler * scaleSingle;
            prob3single[age] = d3.density((double) age) * scaleFourthChild * localScaler * scaleSingle;
        }
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
        birthProbabilities = new HashMap<>();
        birthProbabilities.put(PersonRole.MARRIED, married);
        birthProbabilities.put(PersonRole.SINGLE, single);
        birthProbabilities.put(PersonRole.CHILD, single);
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
            int numberOfChildren = Math.min(HouseholdUtil.getNumberOfChildren(person.getHousehold()), 3);
            double birthProb = birthProbabilities.get(person.getRole()).get(numberOfChildren)[person.getAge()];
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
        int numberOfChildren = Math.min(HouseholdUtil.getNumberOfChildren(person.getHousehold()), 3);
        return person.getGender() == FEMALE && birthProbabilities.get(person.getRole()).get(numberOfChildren)[person.getAge()] > 0;
                //calculator.calculateBirthProbability(person.getAge(), HouseholdUtil.getNumberOfChildren(person.getHousehold())) > 0;
    }
}
