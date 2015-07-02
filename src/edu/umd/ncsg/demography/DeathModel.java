package edu.umd.ncsg.demography;

import com.pb.common.calculator.UtilityExpressionCalculator;
import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloModel;
import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.events.EventTypes;
import edu.umd.ncsg.events.EventRules;
import edu.umd.ncsg.events.EventManager;
import edu.umd.ncsg.data.*;

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
    static Logger traceLogger = Logger.getLogger("trace");

    protected static final String PROPERTIES_DEMOGRAPHICS_UEC_FILE              = "Demographics.UEC.FileName";
    protected static final String PROPERTIES_DEMOGRAPHICS_UEC_DATA_SHEET        = "Demographics.UEC.DataSheetNumber";
    protected static final String PROPERTIES_DEMOGRAPHICS_UEC_MODEL_SHEET_DEATH = "Demographics.UEC.ModelSheetNumber.Death";
    protected static final String PROPERTIES_LOG_UTILILITY_CALCULATION_DEATH    = "log.util.death";
    
    // properties
    private String uecFileName;
    private int dataSheetNumber;
    private ResourceBundle rb;
//    private int death2ModelSheetNumber;

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

	public void chooseDeath(int perId) {
        // simulate if person with ID perId dies in this simulation period

        Person per = Person.getPersonFromId(perId);
        if (!EventRules.ruleDeath(per)) return;  // Person has moved away
        double rnum = SiloModel.rand.nextDouble();
        if (rnum < deathProbability[per.getType().ordinal()]) {
            Household hhOfThisPerson = Household.getHouseholdFromId(per.getHhId());
            hhOfThisPerson.removePerson(per);
            if (per.getWorkplace() > 0) per.quitJob(true);
            if (per.getRole() == PersonRole.married) {
                int widowId = HouseholdDataManager.findMostLikelyPartner(per, hhOfThisPerson); 
                Person widow = Person.getPersonFromId(widowId);
                widow.setRole(PersonRole.single);
            }

            Person.removePerson(perId);
            EventManager.countEvent(EventTypes.checkDeath);
            if (perId == SiloUtil.trackPp) SiloUtil.trackWriter.println("We regret to inform that person " +
                    perId + " passed away.");
        }
    }
}
