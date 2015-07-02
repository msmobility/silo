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
package edu.umd.ncsg.demography;

import java.io.File;
import java.util.ResourceBundle;

import com.pb.common.calculator.UtilityExpressionCalculator;
import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloModel;
import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.events.EventTypes;
import edu.umd.ncsg.events.EventRules;
import edu.umd.ncsg.events.EventManager;
import edu.umd.ncsg.data.Person;
import edu.umd.ncsg.data.PersonType;
import edu.umd.ncsg.data.Household;
import edu.umd.ncsg.data.PersonRole;
import org.apache.log4j.Logger;

/**
 * Simulates birth of children
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 28 December 2009 in Bocholt
 **/

public class BirthModel {
//    static Logger logger = Logger.getLogger(BirthModel.class);
    static Logger traceLogger = Logger.getLogger("trace");

    protected static final String PROPERTIES_DEMOGRAPHICS_UEC_FILE                 = "Demographics.UEC.FileName";
    protected static final String PROPERTIES_DEMOGRAPHICS_UEC_DATA_SHEET           = "Demographics.UEC.DataSheetNumber";
    protected static final String PROPERTIES_DEMOGRAPHICS_UEC_MODEL_SHEET_BIRTH    = "Demographics.UEC.ModelSheetNumber.Birth";
    protected static final String PROPERTIES_DEMOGRAPHICS_PROP_GIRL                = "demographics.proability.girl";
    protected static final String PROPERTIES_DEMOGRAPHICS_BIRTH_SCALER_MARRIED     = "demographics.birth.scaler.married";
    protected static final String PROPERTIES_DEMOGRAPHICS_BIRTH_SCALER_SINGLE      = "demographics.birth.scaler.single";
    protected static final String PROPERTIES_DEMOGRAPHICS_BIRTH_LOCAL_SCALER       = "demographics.local.birth.rate.adjuster";
    protected static final String PROPERTIES_DEMOGRAPHICS_SIMULATION_PERIOD_LENGTH = "simulation.period.length";
    protected static final String PROPERTIES_LOG_UTILILITY_CALCULATION_BIRTH       = "log.util.birth";

    // properties
	private static double[] birthProbability;
    private static float propGirl;
    private int simPeriodLength;
    private float marriedScaler;
    private float singleScaler;
    private ResourceBundle rb;


    public BirthModel(ResourceBundle rb) {
        // constructor

        this.rb = rb;
        // read properties
        propGirl        = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_DEMOGRAPHICS_PROP_GIRL);
        marriedScaler   = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_DEMOGRAPHICS_BIRTH_SCALER_MARRIED);
        singleScaler    = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_DEMOGRAPHICS_BIRTH_SCALER_SINGLE);
        simPeriodLength = ResourceUtil.getIntegerProperty(rb, PROPERTIES_DEMOGRAPHICS_SIMULATION_PERIOD_LENGTH);
        setupBirthModel();
	}


	private void setupBirthModel() {

		// read properties
		int birthModelSheetNumber =
                ResourceUtil.getIntegerProperty(rb, PROPERTIES_DEMOGRAPHICS_UEC_MODEL_SHEET_BIRTH);
        String uecFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_DEMOGRAPHICS_UEC_FILE);
        int dataSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_DEMOGRAPHICS_UEC_DATA_SHEET);
        boolean logCalculation = ResourceUtil.getBooleanProperty(rb, PROPERTIES_LOG_UTILILITY_CALCULATION_BIRTH);
        float localScaler = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_DEMOGRAPHICS_BIRTH_LOCAL_SCALER);

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
        double rnum = SiloModel.rand.nextDouble();
        // todo: distinguish birth probability by neighborhood type (such as urban, suburban, rural)
        double birthProb;
        if (per.getRole() == PersonRole.married) birthProb = birthProbability[per.getType().ordinal()] * marriedScaler;
        else birthProb = birthProbability[per.getType().ordinal()] * singleScaler;
        if (rnum < birthProb) {
            Household hhOfThisWoman = Household.getHouseholdFromId(per.getHhId());
            hhOfThisWoman.addNewbornPerson(hhOfThisWoman.getRace());
            EventManager.countEvent(EventTypes.checkBirth);
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


    public void celebrateBirthday (int personId) {
        // increase age of this person by number of years in simulation period
        Person per = Person.getPersonFromId(personId);
        if (!EventRules.ruleBirthday(per)) return;  // Person has died or moved away
        int age = per.getAge() + simPeriodLength;
        per.setAge(age);
        per.setType(age, per.getGender());
        EventManager.countEvent(EventTypes.birthday);
        if (personId == SiloUtil.trackPp) SiloUtil.trackWriter.println("Celebrated birthday of person " +
                personId + ". New age is " + age + ".");
    }
}
