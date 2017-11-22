package de.tum.bgu.msm.demography;

import com.pb.common.calculator.UtilityExpressionCalculator;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.EventRules;

import java.io.File;

import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

/**
 * @author Greg Erhardt 
 * Created on Dec 2, 2009
 *
 */
public class DeathModel {

    private static Logger traceLogger = Logger.getLogger("trace");

    private final HouseholdDataManager householdDataManager;
	private double[] deathProbability;

    public DeathModel(HouseholdDataManager householdDataManager) {
        this.householdDataManager = householdDataManager;
		setupDeathModel();
	}

	private void setupDeathModel() {

		// read properties
		int deathModelSheetNumber = Properties.get().demographics.deathModelSheet;
        boolean logCalculation = Properties.get().demographics.logDeathCalculation;

		// initialize UEC
        UtilityExpressionCalculator deathModel = new UtilityExpressionCalculator(new File(Properties.get().main.baseDirectory + Properties.get().demographics.uecFileName),
        		deathModelSheetNumber,
                Properties.get().demographics.dataSheet,
        		SiloUtil.getRbHashMap(),
        		DeathDMU.class);
        
		DeathDMU deathDmu = new DeathDMU();

		// everything is available	
		int numAlts = deathModel.getNumberOfAlternatives();
		int[] death2Avail = new int[numAlts+1]; 
        for (int i=1; i < death2Avail.length; i++) {
            death2Avail[i] = 1;
        }
        
        PersonType[] types = PersonType.values();
        deathProbability = new double[types.length];
        for (int i=0; i<types.length; i++) {

        	// set DMU attributes
        	deathDmu.setType(types[i]);
        	
            // There is only one alternative, and the utility is 
    		// really the probability of dying
    		double util[] = deathModel.solve(deathDmu.getDmuIndexValues(), deathDmu, death2Avail);
    		deathProbability[i] = util[0];
            
            if (logCalculation) {
                // log UEC values for each person type
                deathModel.logAnswersArray(traceLogger, "Death Model for Person Type " + types[i].toString());
            }
        }
	}

	public void chooseDeath(int perId, SiloDataContainer dataContainer) {
        // simulate if person with ID perId dies in this simulation period

        Person per = Person.getPersonFromId(perId);
        if (!EventRules.ruleDeath(per)) return;  // Person has moved away
        if (SiloUtil.getRandomNumberAsDouble() < deathProbability[per.getType().ordinal()]) {
            if (per.getWorkplace() > 0) per.quitJob(true, dataContainer.getJobData());
            Household hhOfPersonToDie = Household.getHouseholdFromId(per.getHhId());
            int hhId = hhOfPersonToDie.getId();
            if (per.getRole() == PersonRole.married) {
                int widowId = HouseholdDataManager.findMostLikelyPartner(per, hhOfPersonToDie);
                Person widow = Person.getPersonFromId(widowId);
                widow.setRole(PersonRole.single);
            }
            hhOfPersonToDie.removePerson(per, dataContainer);
            boolean onlyChildrenLeft = checkIfOnlyChildrenRemainInHousehold(hhOfPersonToDie, per);
            if (onlyChildrenLeft) {
                for (Person pp: hhOfPersonToDie.getPersons()) {
                    if (pp != per)
                    Person.removePerson(pp.getId());
                    if (pp.getId() == SiloUtil.trackPp || hhId == SiloUtil.trackHh)
                        SiloUtil.trackWriter.println("Child " + pp.getId() + " was moved from household " + hhId +
                                " to foster care as remaining child just before head of household (ID " +
                                per.getId() + ") passed away.");
                }
                dataContainer.getHouseholdData().removeHousehold(hhId);
            }
            Person.removePerson(per.getId());
            EventManager.countEvent(EventTypes.checkDeath);
            householdDataManager.addHouseholdThatChanged(hhOfPersonToDie);
            if (perId == SiloUtil.trackPp || hhId == SiloUtil.trackHh)
                SiloUtil.trackWriter.println("We regret to inform that person " + perId + " from household " + hhId +
                        " has passed away.");
        }
    }


    private boolean checkIfOnlyChildrenRemainInHousehold(Household hh, Person personToDie) {
        // if hh has only children left, send children to foster care (foster care is outside of study area, children
        // are dropped from this simulation)

        if (hh.getHhSize() == 1) return false;
        boolean onlyChildren = true;
        for (Person per: hh.getPersons()) if (per.getId() != personToDie.getId() && per.getAge() >= 16) onlyChildren = false;
        return onlyChildren;
    }

}
