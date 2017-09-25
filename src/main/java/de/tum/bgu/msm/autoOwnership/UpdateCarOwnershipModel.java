package de.tum.bgu.msm.autoOwnership;

import com.pb.common.calculator.UtilityExpressionCalculator;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Implements car ownership level change (subsequent years) for the Munich Metropolitan Area
 * @author Matthew Okrah
 * Created on 28/08/2017 in Munich, Germany.
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
    private double[][][][][][][][] carUpdateUtil; // [four probabilities][previousCars][hhSize+][hhSize-][income+][income-][license+][changeRes]
    protected static HashMap<Integer, int[]> householdsChanged;


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

        int[] cuAvail = new int[numAltsCarUpdate + 1];
        for (int i = 0; i < cuAvail.length; i++) {
            cuAvail[i] = 1;
        }

        // carUpdateProabability [cars, difEV)
        carUpdateUtil = new double [2][4][2][2][2][2][2][2];
        for (int prevCar = 0; prevCar < 4; prevCar++){
            for (int sizePlus = 0; sizePlus < 2; sizePlus++){
                for (int sizeMinus = 0; sizeMinus < 2; sizeMinus++){
                    for (int incPlus = 0; incPlus < 2; incPlus++){
                        for (int incMinus = 0; incMinus < 2; incMinus++){
                            for (int licPlus = 0; licPlus < 2; licPlus++){
                                for (int changeRes = 0; changeRes < 2; changeRes++){
                                    //set DMU attributes
                                    updateCarOwnershipDMU.setPreviousCars(prevCar);
                                    updateCarOwnershipDMU.setHhSizePlus(sizePlus);
                                    updateCarOwnershipDMU.setHhSizeMinus(sizeMinus);
                                    updateCarOwnershipDMU.setHhIncomePlus(incPlus);
                                    updateCarOwnershipDMU.setHhIncomeMinus(incMinus);
                                    updateCarOwnershipDMU.setLicensePlus(licPlus);
                                    updateCarOwnershipDMU.setChangeResidence(changeRes);

                                    double util[]= cuModelUtility.solve(updateCarOwnershipDMU.getDmuIndexValues(),
                                            updateCarOwnershipDMU, cuAvail);

                                    for (int i = 1; i < cuAvail.length; i++){
                                        util[i-1] = Math.exp(util[i-1]);
                                    }

                                    double prob0change = 1d / (SiloUtil.getSum(util) + 1d);

                                    for (int i = 1; i < cuAvail.length; i++){
                                        carUpdateUtil[i-1][prevCar][sizePlus][sizeMinus][incPlus][incMinus][licPlus][changeRes]
                                                = util[i-1] * prob0change;
                                    }

                                    if (logCalculation){
                                        // log UEC values for each hh type
                                        cuModelUtility.logAnswersArray(traceLogger, " Car Update Model. PrevCar: " + prevCar +
                                                ", sizePlus: " + sizePlus + ", sizeMinus: " + sizeMinus + ", incPlus: " + incPlus +
                                                ", incMinus: " + incMinus + ", licPlus: " + licPlus + ", changeRes " + changeRes);

                                        logger.info(prevCar + "," + sizePlus + "," + sizeMinus + "," + incPlus + "," +
                                                incMinus + "," + licPlus + "," + changeRes + "," + prob0change + "," +
                                                carUpdateUtil[0][prevCar][sizePlus][sizeMinus][incPlus][incMinus][licPlus][changeRes] + "," +
                                                carUpdateUtil[1][prevCar][sizePlus][sizeMinus][incPlus][incMinus][licPlus][changeRes]
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    //public void run (boolean flagSkipCreationOfSPforDebugging
    public void run (){
        //main run method

         for (Map.Entry<Integer, int[]> pair : householdsChanged.entrySet()) {
            Household hh = Household.getHouseholdFromId(pair.getKey());
            int[] previousAttributes = pair.getValue();
            updateHouseholdCars(hh, previousAttributes);
            householdsChanged.remove(hh.getId());
        }

    }

    public void updateHouseholdCars(Household hh, int[] previousAttributes){
        // update cars owned by household hh
        int previousCars = hh.getAutos();
        int hhSizePlus = 0;
        int hhSizeMinus = 0;
        int hhIncomePlus = 0;
        int hhIncomeMinus = 0;
        int licensePlus = 0;
        int changeResidence = previousAttributes[3];

        if (hh.getHhSize() > previousAttributes[0]){
            hhSizePlus = 1;
        } else if (hh.getHhSize() < previousAttributes[0]){
            hhSizeMinus = 1;
        }
        if (hh.getHhIncome() > previousAttributes[1] + 500) {
            hhIncomePlus = 1;
        } else if (hh.getHhIncome() < previousAttributes[1] - 500) {
            hhIncomeMinus = 1;
        }
        if (hh.getHHLicenseHolders() > previousAttributes[2]){
            licensePlus = 1;
        }
        double[] prob = new double[3];
        for (int i = 1; i < 3; i++){
            prob[i] = carUpdateUtil[i-1][previousCars][hhSizePlus][hhSizeMinus][hhIncomePlus][hhIncomeMinus][licensePlus][changeResidence];
        }
        prob[0] = 1 - SiloUtil.getSum(prob); //maintain the number of cars. If it is selected, don´t do anything
        int action = SiloUtil.select(prob);
        if (action == 1){ //add one car
            if (hh.getAutos() < 3) { //maximum number of cars is equal to 3
                hh.setAutos(hh.getAutos() + 1);
            }
        } else if (action == 2) { //remove one car
            if (hh.getAutos() > 0){ //cannot have less than zero cars
                hh.setAutos(hh.getAutos() - 1);
            }
        }
    }

    public static void initializeHouseholdsChanged (){
        householdsChanged = new HashMap<>();
    }

    public static void addHouseholdThatChanged (Household hh){
        //Add one household that probably had changed their attributes for the car updating model

        if (householdsChanged.containsKey(hh.getId())) {

        } else {
            int[] currentHouseholdAttributes = new int[4];
            currentHouseholdAttributes[0] = hh.getHhSize();
            currentHouseholdAttributes[1] = hh.getHhIncome();
            currentHouseholdAttributes[2] = hh.getHHLicenseHolders();
            currentHouseholdAttributes[3] = 0;
            householdsChanged.put(hh.getId(), currentHouseholdAttributes);
        }
    }

    public static void addHouseholdThatMoved (Household hh){
        //Add one household that moved out for the car updating model

        if (householdsChanged.containsKey(hh.getId())) {
            int[] currentHouseholdAttributes = householdsChanged.get(hh.getId());
            currentHouseholdAttributes [3] = 1;
            householdsChanged.put(hh.getId(), currentHouseholdAttributes);
        } else {
            int[] currentHouseholdAttributes = new int[4];
            currentHouseholdAttributes[0] = hh.getHhSize();
            currentHouseholdAttributes[1] = hh.getHhIncome();
            currentHouseholdAttributes[2] = hh.getHHLicenseHolders();
            currentHouseholdAttributes[3] = 1;
            householdsChanged.put(hh.getId(), currentHouseholdAttributes);
        }
    }




}
