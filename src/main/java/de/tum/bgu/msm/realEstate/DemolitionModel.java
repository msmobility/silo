package de.tum.bgu.msm.realEstate;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.relocation.InOutMigration;
import de.tum.bgu.msm.relocation.MovesModel;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventManager;
import com.pb.common.util.ResourceUtil;
import com.pb.common.calculator.UtilityExpressionCalculator;

import java.util.ResourceBundle;
import java.io.File;

import de.tum.bgu.msm.events.IssueCounter;
import org.apache.log4j.Logger;

/**
 * Simulates demolition of dwellings
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 8 January 2010 in Rhede
 **/

public class DemolitionModel {

    static Logger traceLogger = Logger.getLogger("trace");

    protected static final String PROPERTIES_RealEstate_UEC_FILE                   = "RealEstate.UEC.FileName";
    protected static final String PROPERTIES_RealEstate_UEC_DATA_SHEET             = "RealEstate.UEC.DataSheetNumber";
    protected static final String PROPERTIES_RealEstate_UEC_MODEL_SHEET_DEMOLITION = "RealEstate.UEC.ModelSheetNumber.Demolition";
    protected static final String PROPERTIES_LOG_UTILILITY_CALCULATION_DEMOLITION  = "log.util.ddDemolition";

    // properties
    private String uecFileName;
    private int dataSheetNumber;
    private ResourceBundle rb;

    private double[][] demolitionProbability;


    public DemolitionModel(ResourceBundle rb) {
        // constructor

        this.rb = rb;
        // read properties
        uecFileName     = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_RealEstate_UEC_FILE);
        dataSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_RealEstate_UEC_DATA_SHEET);

        setupDemolitionModel();
    }


    private void setupDemolitionModel() {

        // read properties
        int demolitionModelSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_RealEstate_UEC_MODEL_SHEET_DEMOLITION);
        boolean logCalculation = ResourceUtil.getBooleanProperty(rb, PROPERTIES_LOG_UTILILITY_CALCULATION_DEMOLITION);

        // initialize UEC
        UtilityExpressionCalculator demolitionModel = new UtilityExpressionCalculator(new File(uecFileName),
                demolitionModelSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                DemolitionDMU.class);
        DemolitionDMU demolitionDmu = new DemolitionDMU();

        // everything is available
        int numAlts = demolitionModel.getNumberOfAlternatives();
        int[] demolitionAvail = new int[numAlts+1];
        for (int i=1; i < demolitionAvail.length; i++) {
            demolitionAvail[i] = 1;
        }

        // demolitionProbability["quality-1","vacant/occupied"]
        demolitionProbability = new double[4][2];
        for (int i = 1; i <= 4; i++) {
            for (int j = 0; j <= 1; j++) {
                // set DMU attributes
                demolitionDmu.setQuality(i);
                if (j == 0) demolitionDmu.setResidentId(-1);
                else demolitionDmu.setResidentId(1);
                // There is only one alternative, and the utility is really the probability of being demolished
                double util[] = demolitionModel.solve(demolitionDmu.getDmuIndexValues(), demolitionDmu, demolitionAvail);
                demolitionProbability[i-1][j] = util[0];
                if (logCalculation) {
                    // log UEC values for each dwelling type
                    if (j == 0) {
                        demolitionModel.logAnswersArray(traceLogger, "Demolition Model for quality " + i + " (vacant)");
                    } else {
                        demolitionModel.logAnswersArray(traceLogger, "Demolition Model for quality " + i + " (occupied)");
                    }
                }
            }
        }
    }


    public void checkDemolition (int dwellingId, MovesModel move, InOutMigration iomig,
                                 RealEstateDataManager realEstateData, Accessibility accessibility,
                                 HouseholdDataManager householdData, JobDataManager jobData) {
        // check if is demolished

        Dwelling dd = Dwelling.getDwellingFromId(dwellingId);
        if (!EventRules.ruleDemolishDwelling(dd)) return;  // Dwelling not available for demolition
        int quality = dd.getQuality();
        int residentId = dd.getResidentId();
        int occupied;
        if (residentId > 0) occupied = 1;
        else occupied = 0;
        if (SiloModel.rand.nextDouble() < demolitionProbability[quality - 1][occupied]) {
            // demolish dwelling
            if (occupied == 1) {
                // dwelling is currently occupied, force household to move out
                Household hh = Household.getHouseholdFromId(residentId);
                int idNewDD = move.searchForNewDwelling(hh.getPersons(), accessibility);
                if (idNewDD > 0) {
                    move.moveHousehold(hh, -1, idNewDD, realEstateData);  // set old dwelling ID to -1 to avoid it from being added to the vacancy list
                } else {
                    iomig.outMigrateHh(residentId, true, householdData, jobData);
                    realEstateData.removeDwellingFromVacancyList(dwellingId);
                    IssueCounter.countLackOfDwellingForcedOutmigration();
                }
            } else {
                realEstateData.removeDwellingFromVacancyList(dwellingId);
            }
            Dwelling.removeDwelling(dwellingId);
            EventManager.countEvent(EventTypes.ddDemolition);
            if (dwellingId == SiloUtil.trackDd) SiloUtil.trackWriter.println("Dwelling " +
                    dwellingId + " was demolished.");
        }
    }
}

