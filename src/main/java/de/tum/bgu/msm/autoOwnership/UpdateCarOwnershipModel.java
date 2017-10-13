package de.tum.bgu.msm.autoOwnership;

import com.pb.common.calculator.UtilityExpressionCalculator;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.*;
import org.apache.log4j.Logger;

import javax.script.ScriptException;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.*;

/**
 * Implements car ownership level change (subsequent years) for the Munich Metropolitan Area
 * @author Matthew Okrah
 * Created on 28/08/2017 in Munich, Germany.
 */
public class UpdateCarOwnershipModel {

    static Logger logger = Logger.getLogger(UpdateCarOwnershipModel.class);
    static Logger traceLogger = Logger.getLogger("trace");
    private ResourceBundle rb;

    private double[][][][][][][][] carUpdateProb; // [previousCars][hhSize+][hhSize-][income+][income-][license+][changeRes][three probabilities]
    protected static HashMap<Integer, int[]> householdsChanged;

    private Reader reader;
    private UpdateCarOwnershipJSCalculator calculator;


    public UpdateCarOwnershipModel(ResourceBundle rb){
        logger.info(" Setting up probabilities for car update model");
        this.rb = rb;
        setupCarUpdateModel();
    }

    private void setupCarUpdateModel() {

        // read properties
        reader = new InputStreamReader(this.getClass().getResourceAsStream("UpdateCarOwnershipCalc"));
        calculator = new UpdateCarOwnershipJSCalculator(reader, false);

        //set car update probabilities
        carUpdateProb = new double[4][2][2][2][2][2][2][3];
        for (int prevCar = 0; prevCar < 4; prevCar++){
            for (int sizePlus = 0; sizePlus < 2; sizePlus++){
                for (int sizeMinus = 0; sizeMinus < 2; sizeMinus++){
                    for (int incPlus = 0; incPlus < 2; incPlus++){
                        for (int incMinus = 0; incMinus < 2; incMinus++){
                            for (int licPlus = 0; licPlus < 2; licPlus++){
                                for (int changeRes = 0; changeRes < 2; changeRes++){
                                    calculator.setPreviousCars(prevCar);
                                    calculator.setHHSizePlus(sizePlus);
                                    calculator.setHHSizeMinus(sizeMinus);
                                    calculator.setHHIncomePlus(incPlus);
                                    calculator.setHHIncomeMinus(incMinus);
                                    calculator.setLicensePlus(licPlus);
                                    calculator.setChangeResidence(changeRes);

                                    try {
                                        carUpdateProb[prevCar][sizePlus][sizeMinus][incPlus][incMinus][licPlus][changeRes] = calculator.calculate();

                                    } catch (ScriptException e) {
                                        e.printStackTrace();
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
             if (hh != null) {
                 int[] previousAttributes = pair.getValue();
                 updateHouseholdCars(hh, previousAttributes);
             }
        }
        householdsChanged.clear();
        //summarizeCarUpdate();
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
        if (hh.getHhIncome() > previousAttributes[1] + 6000) {
            hhIncomePlus = 1;
        } else if (hh.getHhIncome() < previousAttributes[1] - 6000) {
            hhIncomeMinus = 1;
        }
        if (hh.getHHLicenseHolders() > previousAttributes[2]){
            licensePlus = 1;
        }

        double[] prob = carUpdateProb[previousCars][hhSizePlus][hhSizeMinus][hhIncomePlus][hhIncomeMinus][licensePlus][changeResidence];

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

        if (!householdsChanged.containsKey(hh.getId())) {
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

    public static void summarizeCarUpdate() {
        // This function summarizes household car ownership update and quits
        PrintWriter pwa = SiloUtil.openFileForSequentialWriting("microData/interimFiles/carUpdate.csv", false);
        pwa.println("id, dwelling, zone, license, income, size, autos");
        for (Household hh: Household.getHouseholdArray()) {
            pwa.println(hh.getId() + "," + hh.getDwellingId() + "," + hh.getHomeZone() + "," + hh.getHHLicenseHolders()+ "," +  hh.getHhIncome() + "," + hh.getHhSize() + "," + hh.getAutos());
        }
        pwa.close();

        logger.info("Summarized car update and quit.");
        System.exit(0);

    }

}
