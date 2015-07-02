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
import edu.umd.ncsg.autoOwnership.AutoOwnershipModel;
import edu.umd.ncsg.events.EventTypes;
import edu.umd.ncsg.events.EventRules;
import edu.umd.ncsg.events.EventManager;
import edu.umd.ncsg.data.*;
import edu.umd.ncsg.events.IssueCounter;
import edu.umd.ncsg.relocation.MovesModel;
import org.apache.log4j.Logger;

/**
 * Simulates children that leave the parental household
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 30 December 2009 in Cologne
 **/

public class LeaveParentHhModel {

//    static Logger logger = Logger.getLogger(LeaveParentHhModel.class);
    static Logger traceLogger = Logger.getLogger("trace");

    protected static final String PROPERTIES_DEMOGRAPHICS_UEC_FILE            = "Demographics.UEC.FileName";
    protected static final String PROPERTIES_DEMOGRAPHICS_UEC_DATA_SHEET      = "Demographics.UEC.DataSheetNumber";
    protected static final String PROPERTIES_DEMOGRAPHICS_UEC_MODEL_SHEET_LPH = "Demographics.UEC.ModelSheetNumber.LPH";
    protected static final String PROPERTIES_LOG_UTILILITY_CALCULATION_LPH    = "log.util.leaveParentHh";

    // properties
    private String uecFileName;
    private int dataSheetNumber;

    private double[] lphProbability;

    public LeaveParentHhModel(ResourceBundle rb) {
        // constructor

        // read properties
        uecFileName     = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_DEMOGRAPHICS_UEC_FILE);
        dataSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_DEMOGRAPHICS_UEC_DATA_SHEET);

        setupLPHModel(rb);
    }


    private void setupLPHModel(ResourceBundle rb) {

        // read properties
        int lphModelSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_DEMOGRAPHICS_UEC_MODEL_SHEET_LPH);
        boolean logCalculation = ResourceUtil.getBooleanProperty(rb, PROPERTIES_LOG_UTILILITY_CALCULATION_LPH);

        // initialize UEC

        UtilityExpressionCalculator lphModel = new UtilityExpressionCalculator(new File(uecFileName),
                lphModelSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                LeaveParentHhDMU.class);

        LeaveParentHhDMU lphDmu = new LeaveParentHhDMU();

        // everything is available
        int numAlts = lphModel.getNumberOfAlternatives();
        int[] lphAvail = new int[numAlts+1];
        for (int i=1; i < lphAvail.length; i++) {
            lphAvail[i] = 1;
        }

        PersonType[] types = PersonType.values();
        lphProbability = new double[types.length];
        for (int i=0; i<types.length; i++) {

            // set DMU attributes
            lphDmu.setType(types[i]);

            // There is only one alternative, and the utility is really the probability of giving birth
            double util[] = lphModel.solve(lphDmu.getDmuIndexValues(), lphDmu, lphAvail);
            lphProbability[i] = util[0];
            if (logCalculation) {
                // log UEC values for each person type
                lphModel.logAnswersArray(traceLogger, "Leave-Parental-Household Model for Person Type " + types[i].toString());
            }
        }
    }


    public void chooseLeaveParentHh(int perId, MovesModel moveM, AutoOwnershipModel aoModel) {
        // remove person with perId from its household and create new household with this person

        Person per = Person.getPersonFromId(perId);
        if (!EventRules.ruleLeaveParHousehold(per)) return;   // Person got married this simulation period
        double rnum = SiloModel.rand.nextDouble();
        if (rnum < lphProbability[per.getType().ordinal()]) {

            // search if dwelling is available
            int newDwellingId = moveM.searchForNewDwelling(new Person[]{per});
            if (newDwellingId < 0) {
                if (perId == SiloUtil.trackPp || per.getHhId() == SiloUtil.trackHh) SiloUtil.trackWriter.println(
                        "Person " + perId + " wanted to but could not leave parental household " + per.getHhId() +
                        " because no appropriate vacant dwelling was found.");
                IssueCounter.countLackOfDwellingFailedLeavingChild();
                return;
            }

            // create new household
            Household hhOfThisPerson = Household.getHouseholdFromId(per.getHhId());
            hhOfThisPerson.removePerson(per);
            hhOfThisPerson.setType();
            int newHhId = HouseholdDataManager.getNextHouseholdId();
            Household hh = new Household(newHhId, -1, -1, 1, 0);
            hh.addPersonForInitialSetup(per);
            hh.setType();
            hh.setHouseholdRace();
            per.setRole(PersonRole.single);

            // Move new household
            moveM.moveHousehold(hh, -1, newDwellingId);
            aoModel.simulateAutoOwnership(hh);
            EventManager.countEvent(EventTypes.checkLeaveParentHh);
            if (perId == SiloUtil.trackPp || hhOfThisPerson.getId() == SiloUtil.trackHh ||
                    hh.getId() == SiloUtil.trackHh) SiloUtil.trackWriter.println("Person " + perId +
                    " has left the parental household " + hhOfThisPerson.getId() +
                    " and established the new household " + newHhId + ".");
        }
    }
}