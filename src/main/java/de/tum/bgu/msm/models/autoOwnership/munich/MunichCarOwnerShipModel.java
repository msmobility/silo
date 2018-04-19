package de.tum.bgu.msm.models.autoOwnership.munich;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.data.*;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.*;

/**
 * Implements car ownership level change (subsequent years) for the Munich Metropolitan Area
 * @author Matthew Okrah
 * Created on 28/08/2017 in Munich, Germany.
 */
public class MunichCarOwnerShipModel extends AbstractModel implements CreateCarOwnershipModel {

    static Logger logger = Logger.getLogger(MunichCarOwnerShipModel.class);

    private double[][][][][][][][] carUpdateProb; // [previousCars][hhSize+][hhSize-][income+][income-][license+][changeRes][three probabilities]

    public MunichCarOwnerShipModel(SiloDataContainer dataContainer) {
        super(dataContainer);
    }

    public void summarizeCarUpdate() {
        // This function summarizes household car ownership update and quits
        PrintWriter pwa = SiloUtil.openFileForSequentialWriting("microData/interimFiles/carUpdate.csv", false);
        pwa.println("id, dwelling, zone, license, income, size, autos");
        for (Household hh: dataContainer.getHouseholdData().getHouseholds()) {
            Dwelling dwelling = dataContainer.getRealEstateData().getDwelling(hh.getDwellingId());
            int homeZone = -1;
            if(dwelling != null) {
                homeZone = dwelling.getZone();
            }
            pwa.println(hh.getId() + "," + hh.getDwellingId() + "," + homeZone + "," + hh.getHHLicenseHolders()+ "," +  hh.getHhIncome() + "," + hh.getHhSize() + "," + hh.getAutos());
        }
        pwa.close();

        logger.info("Summarized car update and quit.");
        System.exit(0);
    }

    @Override
    public void initialize() {
        // Setting up probabilities for car update model

        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("UpdateCarOwnershipCalc"));
        MunichCarOwnershipJSCalculator calculator = new MunichCarOwnershipJSCalculator(reader);
        //set car update probabilities
        carUpdateProb = new double[4][2][2][2][2][2][2][3];
        for (int prevCar = 0; prevCar < 4; prevCar++){
            for (int sizePlus = 0; sizePlus < 2; sizePlus++){
                for (int sizeMinus = 0; sizeMinus < 2; sizeMinus++){
                    for (int incPlus = 0; incPlus < 2; incPlus++){
                        for (int incMinus = 0; incMinus < 2; incMinus++){
                            for (int licPlus = 0; licPlus < 2; licPlus++){
                                for (int changeRes = 0; changeRes < 2; changeRes++){
                                    carUpdateProb[prevCar][sizePlus][sizeMinus][incPlus][incMinus][licPlus][changeRes] = calculator.calculateCarOwnerShipProbabilities(prevCar, sizePlus, sizeMinus, incPlus, incMinus, licPlus, changeRes);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int[] updateCarOwnership(Map<Integer, int[]> updatedHouseholds) {

        int[] counter = new int[2];
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        for (Map.Entry<Integer, int[]> pair : updatedHouseholds.entrySet()) {
            Household hh = householdData.getHouseholdFromId(pair.getKey());
            if (hh != null) {
                int[] previousAttributes = pair.getValue();
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
                        counter[0]++;
                    }
                } else if (action == 2) { //remove one car
                    if (hh.getAutos() > 0){ //cannot have less than zero cars
                        hh.setAutos(hh.getAutos() - 1);
                        counter[1]++;
                    }
                }
            }
        }
        return counter;
    }
}
