package edu.umd.ncsg.realEstate;

import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.events.EventRules;
import edu.umd.ncsg.events.EventManager;
import edu.umd.ncsg.events.EventTypes;
import edu.umd.ncsg.data.Dwelling;
import edu.umd.ncsg.data.RealEstateDataManager;
import com.pb.common.util.ResourceUtil;
import com.pb.common.calculator.UtilityExpressionCalculator;

import java.util.ResourceBundle;
import java.io.File;

import org.apache.log4j.Logger;

/**
 * Simulates renovation and deterioration of dwellings
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 7 January 2010 in Rhede
 **/

public class RenovationModel {
    static Logger logger = Logger.getLogger(RenovationModel.class);
    static Logger traceLogger = Logger.getLogger("trace");

    protected static final String PROPERTIES_RealEstate_UEC_FILE                   = "RealEstate.UEC.FileName";
    protected static final String PROPERTIES_RealEstate_UEC_DATA_SHEET             = "RealEstate.UEC.DataSheetNumber";
    protected static final String PROPERTIES_RealEstate_UEC_MODEL_SHEET_RENOVATION = "RealEstate.UEC.ModelSheetNumber.Renovation";
    protected static final String PROPERTIES_LOG_UTILILITY_CALCULATION_RENOVATION  = "log.util.ddChangeQual";

    // properties
    private String uecFileName;
    private int dataSheetNumber;
	private double[][] renovationProbability;
    private ResourceBundle rb;

    public RenovationModel(ResourceBundle rb) {
        // constructor

        this.rb = rb;
        // read properties
		uecFileName     = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_RealEstate_UEC_FILE);
		dataSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_RealEstate_UEC_DATA_SHEET);

        setupRenovationModel();
	}


	private void setupRenovationModel() {

		// read properties
		int renovationModelSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_RealEstate_UEC_MODEL_SHEET_RENOVATION);
        boolean logCalculation = ResourceUtil.getBooleanProperty(rb, PROPERTIES_LOG_UTILILITY_CALCULATION_RENOVATION);

		// initialize UEC
        UtilityExpressionCalculator renovationModel = new UtilityExpressionCalculator(new File(uecFileName),
        		renovationModelSheetNumber,
        		dataSheetNumber,
        		SiloUtil.getRbHashMap(),
        		RenovationDMU.class);
		RenovationDMU renovationDmu = new RenovationDMU();

		// everything is available
		int numAlts = renovationModel.getNumberOfAlternatives();
		int[] renovationAvail = new int[numAlts+1];
        for (int i=1; i < renovationAvail.length; i++) renovationAvail[i] = 1;

        renovationProbability = new double[SiloUtil.numberOfQualityLevels][numAlts];
        for (int oldQual = 0; oldQual < SiloUtil.numberOfQualityLevels; oldQual++) {

        	// set DMU attributes
        	renovationDmu.setQuality(oldQual + 1);
            // There is only one alternative, and the utility is really the probability of giving birth
    		double util[] = renovationModel.solve(renovationDmu.getDmuIndexValues(), renovationDmu, renovationAvail);
            System.arraycopy(util, 0, renovationProbability[oldQual], 0, numAlts);
            if (logCalculation) {
                // log UEC values for each person type
                renovationModel.logAnswersArray(traceLogger, "Renovation Model for Dwelling Quality " + oldQual);
            }
        }
	}


    public void checkRenovation(int dwellingId) {
        // check if dwelling is renovated or deteriorates
        Dwelling dd = Dwelling.getDwellingFromId(dwellingId);
        if (!EventRules.ruleChangeDwellingQuality(dd)) return;  // Dwelling not available for renovation
        int currentQuality = dd.getQuality();
        int selected = SiloUtil.select(getProbabilities(currentQuality));

        if (selected != 2) {
            EventManager.countEvent(EventTypes.ddChangeQual);
            RealEstateDataManager.dwellingsByQuality[currentQuality - 1] -= 1;
        }
        switch (selected) {
            case (0): {
                RealEstateDataManager.dwellingsByQuality[currentQuality - 1 - 2] += 1;
                dd.setQuality(currentQuality - 2);
                break;
            }
            case (1): {
                RealEstateDataManager.dwellingsByQuality[currentQuality - 1 - 1] += 1;
                dd.setQuality(currentQuality - 1);
                break;
            }
            case (3): {
                RealEstateDataManager.dwellingsByQuality[currentQuality - 1 + 1] += 1;
                dd.setQuality(currentQuality + 1);
                break;
            }
            case (4): {
                RealEstateDataManager.dwellingsByQuality[currentQuality - 1 + 2] += 1;
                dd.setQuality(currentQuality + 2);
                break;
            }
        }
    }


    private double[] getProbabilities (int currentQual) {
        // return probabilities to upgrade or deteriorate based on current quality of dwelling and average
        // quality of all dwellings
        double[] currentShare = RealEstateDataManager.getCurrentQualShares();
        // if share of certain quality level is currently 0, set it to very small number to ensure model keeps working
        for (int i = 0; i < currentShare.length; i++) if (currentShare[i] == 0) currentShare[i] = 0.01d;
        double[] initialShare = RealEstateDataManager.getInitialQualShares();
        for (int i = 0; i < initialShare.length; i++) if (initialShare[i] == 0) initialShare[i] = 0.01d;
        double[] probs = new double[5];
        for (int i = 0; i < probs.length; i++) {
            int potentialNewQual = currentQual + i - 2;  // translate into new quality level this alternative would generate
            double ratio;
            if (potentialNewQual >= 1 && potentialNewQual <= 4) ratio = initialShare[potentialNewQual - 1] / currentShare[potentialNewQual - 1];
            else ratio = 0.;
            if (i <= 1) {
                probs[i] = renovationProbability[currentQual - 1][i] * ratio;
            } else if (i == 2) {
                probs[i] = renovationProbability[currentQual - 1][i];
            } else if (i >= 3) probs[i] = renovationProbability[currentQual - 1][i] * ratio;
        }
        return probs;
    }
}

