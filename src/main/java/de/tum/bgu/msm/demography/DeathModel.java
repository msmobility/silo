package de.tum.bgu.msm.demography;

import com.pb.common.calculator.UtilityExpressionCalculator;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.EventRules;

import java.io.File;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * @author Greg Erhardt 
 * Created on Dec 2, 2009
 *
 */
public class DeathModel {

//    Logger logger = Logger.getLogger(DeathModel.class);
    private static Logger traceLogger = Logger.getLogger("trace");

    protected static final String PROPERTIES_DEMOGRAPHICS_UEC_FILE              = "Demographics.UEC.FileName";
    protected static final String PROPERTIES_DEMOGRAPHICS_UEC_DATA_SHEET        = "Demographics.UEC.DataSheetNumber";
    protected static final String PROPERTIES_DEMOGRAPHICS_UEC_MODEL_SHEET_DEATH = "Demographics.UEC.ModelSheetNumber.Death";
    protected static final String PROPERTIES_LOG_UTILILITY_CALCULATION_DEATH    = "log.util.death";
    
    // properties
    private String uecFileName;
    private int dataSheetNumber;
    private ResourceBundle rb;
//    private int death2ModelSheetNumber;

    static Logger logger = Logger.getLogger(DeathModel.class);

	private double[] deathProbability; 
	
	public DeathModel(ResourceBundle rb) {
        // constructor
        this.rb = rb;

        // read properties
		uecFileName     = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_DEMOGRAPHICS_UEC_FILE);
		dataSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_DEMOGRAPHICS_UEC_DATA_SHEET);
 
		setupDeathModel();
	}
	

	private void setupDeathModel() {

		// read properties
		int deathModelSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_DEMOGRAPHICS_UEC_MODEL_SHEET_DEATH);
        boolean logCalculation = ResourceUtil.getBooleanProperty(rb, PROPERTIES_LOG_UTILILITY_CALCULATION_DEATH);

		// initialize UEC
        UtilityExpressionCalculator deathModel = new UtilityExpressionCalculator(new File(uecFileName),
        		deathModelSheetNumber,
        		dataSheetNumber, 
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
