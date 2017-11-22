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

import java.io.File;
import java.util.ResourceBundle;

import com.pb.common.calculator.UtilityExpressionCalculator;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

/**
 * Simulates birth of children
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 28 December 2009 in Bocholt
 **/

public class BirthModel {

    static Logger traceLogger = Logger.getLogger("trace");

	private static double[] birthProbability;
    private static float propGirl;
    private final HouseholdDataManager householdDataManager;

    public BirthModel(HouseholdDataManager householdDataManager) {
        propGirl        = Properties.get().demographics.propabilityForGirl;
        this.householdDataManager = householdDataManager;
        setupBirthModel();
	}


	private void setupBirthModel() {

		// read properties
		int birthModelSheetNumber =
                Properties.get().demographics.birthModelSheet;
        String uecFileName = Properties.get().main.baseDirectory + Properties.get().demographics.uecFileName;
        int dataSheetNumber = Properties.get().demographics.dataSheet;
        boolean logCalculation = Properties.get().demographics.logBirthCalculation;
        float localScaler = Properties.get().demographics.localScaler;

        // initialize UEC
        UtilityExpressionCalculator birthModel = new UtilityExpressionCalculator(new File(uecFileName),
        		birthModelSheetNumber,
        		dataSheetNumber,
        		SiloUtil.getRbHashMap(),
        		BirthDMU.class);
 		BirthDMU birthDmu = new BirthDMU();

		// everything is available
		int numAlts = birthModel.getNumberOfAlternatives();
		int[] birthAvail = new int[numAlts+1];
        for (int i=1; i < birthAvail.length; i++)  birthAvail[i] = 1;

        PersonType[] types = PersonType.values();
        birthProbability = new double[types.length];
        for (int i=0; i<types.length; i++) {
        	// set DMU attributes
        	birthDmu.setType(types[i]);
            // There is only one alternative, and the utility is really the probability of giving birth
    		double util[] = birthModel.solve(birthDmu.getDmuIndexValues(), birthDmu, birthAvail);
            birthProbability[i] = util[0] / 1000d * localScaler;  // birth probability is given as "per 1000 women"

            if (logCalculation) {
                // log UEC values for each person type
                birthModel.logAnswersArray(traceLogger, "Birth Married Model for Person Type " + types[i].toString());
            }
        }
    }


    public void chooseBirth(int perId) {

        Person per = Person.getPersonFromId(perId);
        if (!EventRules.ruleGiveBirth(per)) return;  // Person has died or moved away
        // todo: distinguish birth probability by neighborhood type (such as urban, suburban, rural)
        double birthProb;
        if (per.getRole() == PersonRole.married) birthProb = birthProbability[per.getType().ordinal()] * Properties.get().demographics.marriedScaler;
        else birthProb = birthProbability[per.getType().ordinal()] * Properties.get().demographics.singleScaler;
        if (SiloUtil.getRandomNumberAsDouble() < birthProb) {
            Household hhOfThisWoman = Household.getHouseholdFromId(per.getHhId());
            hhOfThisWoman.addNewbornPerson(hhOfThisWoman.getRace());
            EventManager.countEvent(EventTypes.checkBirth);
            householdDataManager.addHouseholdThatChanged(hhOfThisWoman);
            if (perId == SiloUtil.trackPp) {
                SiloUtil.trackWriter.println("Person " + perId + " gave birth to a child.");
            }
        }
    }


    public static float getProbabilityForGirl () {
        return propGirl;
    }


    public static boolean personCanGiveBirth(PersonType pt) {
        return (birthProbability[pt.ordinal()] > 0);
    }

    //TODO AGE UPDATION IS INT. FOR SIMULATIONS LESS THAN 1 YEAR, AGE CAN BE NON-INT VALUE
    public void celebrateBirthday (int personId) {
        // increase age of this person by number of years in simulation period
        Person per = Person.getPersonFromId(personId);
        if (!EventRules.ruleBirthday(per)) return;  // Person has died or moved away
        int age = per.getAge() + Properties.get().demographics.simulationPeriodLength;
        per.setAge(age);
        per.setType(age, per.getGender());
        EventManager.countEvent(EventTypes.birthday);
        if (personId == SiloUtil.trackPp) SiloUtil.trackWriter.println("Celebrated birthday of person " +
                personId + ". New age is " + age + ".");
    }
}
