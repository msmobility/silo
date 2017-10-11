package de.tum.bgu.msm.autoOwnership;

import com.pb.common.calculator.UtilityExpressionCalculator;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ResourceBundle;

/**
 * Created by matthewokrah on 12/06/2017.
 */
public class UpdateCarOwnershipModel {

    static Logger logger = Logger.getLogger(UpdateCarOwnershipModel.class);
    static Logger traceLogger = Logger.getLogger("trace");
    private ResourceBundle rb;

    protected static final String PROPERTIES_CarOwnership_UEC_FILE               = "CarOwnership.UEC.FileName";
    protected static final String PROPERTIES_CarOwnership_UEC_DATA_SHEET         = "CarOwnership.UEC.DataSheetNumber";
    protected static final String PROPERTIES_CarOwnership_UEC_UPDATE_UTILITY     = "CarOwnership.UEC.Update.Utility";
    protected static final String PROPERTIES_LOG_UTILITY_CALCULATION_CONSTRUCTION = "log.util.carOwnership";

    private String uecFileName;
    private int dataSheetNumber;
    int numAltsCarUpdate;
    private double[][][] carUpdateProbability;   // [previous number of cars][difference in Expected Value][number of alternatives]
    public GeoData geoData;
    public CreateCarOwnershipModel createCarOwnershipModel;


    public UpdateCarOwnershipModel(ResourceBundle rb){
        logger.info(" Setting up probabilities for car update model");
        this.rb = rb;
        uecFileName     = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_CarOwnership_UEC_FILE);
        dataSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_CarOwnership_UEC_DATA_SHEET);
        setupCarUpdateModel();
    }

    private void setupCarUpdateModel() {

        boolean logCalculation = ResourceUtil.getBooleanProperty(rb, PROPERTIES_LOG_UTILITY_CALCULATION_CONSTRUCTION);
        int cuModelSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_CarOwnership_UEC_UPDATE_UTILITY);
        UtilityExpressionCalculator cuModelUtility = new UtilityExpressionCalculator(new File(uecFileName),
                cuModelSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                UpdateCarOwnershipDMU.class);
        UpdateCarOwnershipDMU updateCarOwnershipDMU = new UpdateCarOwnershipDMU();

        // everything is available
        numAltsCarUpdate = cuModelUtility.getNumberOfAlternatives();

        int[] cuAvail = new int[numAltsCarUpdate];
        for (int i = 0; i < cuAvail.length; i++) {
            cuAvail[i] = 1;
        }

        // carUpdateProabability [cars, difEV)
        carUpdateProbability = new double [4][3][numAltsCarUpdate];
        for (int cars = 0; cars < 4; cars++){
            for (int difEV=1; difEV<=3; difEV++) {
                // set DMU attributes
                updateCarOwnershipDMU.setInitialCars(cars);
                if (difEV == 1) updateCarOwnershipDMU.setDifEV(-1);
                else if (difEV == 2) updateCarOwnershipDMU.setDifEV(0);
                else updateCarOwnershipDMU.setDifEV(1);

                double util[] = cuModelUtility.solve(updateCarOwnershipDMU.getDmuIndexValues(),
                        updateCarOwnershipDMU, cuAvail);

                System.arraycopy(util, 0, carUpdateProbability[cars][difEV], 0, numAltsCarUpdate);
                if (logCalculation) {
                    // log UEC values for each
                    cuModelUtility.logAnswersArray(traceLogger, "Car Update Model. OldCars: " + cars +
                            "Difference in Expected Value of HH Cars: " + difEV);
                }
            }
        }
    }
/*
    public void run(boolean flagSkipCreationOfSPforDebugging) {
        // main run method
        //Read geoData
        setStartData();

        geoData = new geoDataMuc(rb);
        geoData.setInitialData();
        createCarOwnershipModel.selectCarOwnership();
        updateCarOwnership();
        summarizeData.summarizeAutoOwnershipByMunicipality(geoData);

        if (flagSkipCreationOfSPforDebugging) {
            logger.info("Finished car ownership model");
            System.exit(0);
        }
    }

    public void setStartData(){
        for (Household hh: Household.getHouseholdArray()) {
            int startCars = Math.min(hh.getAutos(), 3);
            double startEV = hh.getEV;
            hh.
        }
    }

    public void setEndData(){
        for (Household hh: Household.getHouseholdArray()) {
            double endEV = hh.getEV;
            double rawDifEV = endEV - startEV
        }
    }




    public void updateCarOwnership() {
        // simulate number of autos for household hh (Version without the use of SiloDataContainer)
        // Note: This method can only be executed after all households have been generated and allocated to zones,
        // as distance to transit and areaType is dependent on where households are living
        for (Household hh: Household.getHouseholdArray()) {
            int initialCars = Math.min(hh.getAutos(), 3);
            double initialEV = hh.getEV;
            double currentEV = hh.get
            int difEV = hh.getdifEV();
            int logDistanceToTransit = Math.min((int) (Math.log(geoData.getDistanceToTransit(hh.getHomeZone()))), 10);

            double[] prob = new double[4];

            for (int i = 1; i < 4; i++)
                prob[i] =
                        carOwnerShipUtil[i - 1][license][workers][income][logDistanceToTransit][areaType - 1];

            prob[0] = 1 - SiloUtil.getSum(prob);

            double expectedValofCars = prob[1] + 2*prob[2] + 3*prob[3];

            hh.setAutos(SiloUtil.select(prob));

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
*/


}
