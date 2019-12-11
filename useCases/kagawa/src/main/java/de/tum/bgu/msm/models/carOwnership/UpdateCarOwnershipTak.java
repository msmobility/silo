package de.tum.bgu.msm.models.carOwnership;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.Random;

/**
 * Implements car ownership level change (subsequent years) for the Kagawa Area
 * @author nkuehnel
 * Created on 09/10/2019 in Tokyo.
 */
public class UpdateCarOwnershipTak extends AbstractModel implements ModelUpdateListener {

    private static final int REMOVE_ONE_CAR = 2;
    private static final int ADD_ONE_CAR = 1;
    private static final int MAX_NUMBER_OF_CARS = 3;

    private static final double[] intercept = {-3.0888, -5.6650};
    private static final double[] betaPreviousCars = {-0.5201, 1.3526};
    private static final double[] betaHHSizePlus = {2.0179, 0.};
    private static final double[] betaHHSizeMinus = {0., 1.1027};
    private static final double[] betaIncomePlus = {0.4842, 0.};
    private static final double[] betaIncomeMinus = {0., 0.3275};
    private static final double[] betaLicensePlus = {1.8213, 0.};
    private static final double[] betaChangeResidence = {1.1440, 0.9055};

    private static Logger logger = Logger.getLogger(UpdateCarOwnershipTak.class);

    /**
     *  [previousCars][hhSize+][hhSize-][income+][income-][license+][changeRes][three probabilities]
     */
    private final double[][][][][][][][] carUpdateProb = new double[4][2][2][2][2][2][2][3];

    public UpdateCarOwnershipTak(DataContainer dataContainer, Properties properties, Random rnd) {
        super(dataContainer, properties, rnd);
    }

    @Override
    public void setup() {
        for (int prevCar = 0; prevCar < 4; prevCar++){
            for (int sizePlus = 0; sizePlus < 2; sizePlus++){
                for (int sizeMinus = 0; sizeMinus < 2; sizeMinus++){
                    for (int incPlus = 0; incPlus < 2; incPlus++){
                        for (int incMinus = 0; incMinus < 2; incMinus++){
                            for (int licPlus = 0; licPlus < 2; licPlus++){
                                for (int changeRes = 0; changeRes < 2; changeRes++){
                                    carUpdateProb[prevCar][sizePlus][sizeMinus][incPlus][incMinus][licPlus][changeRes] =
                                            calculateCarOwnerShipProbabilities(prevCar, sizePlus, sizeMinus, incPlus, incMinus, licPlus, changeRes);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private double[] calculateCarOwnerShipProbabilities(int prevCar, int sizePlus, int sizeMinus,
                                                        int incPlus, int incMinus, int licPlus,
                                                        int changeRes) {

        double[] results = new double[MAX_NUMBER_OF_CARS];
        double sum = 0;

        for(int i = 0; i < 2; i++) {
            double utility = intercept[i] + (betaPreviousCars[i] * prevCar) + (betaHHSizePlus[i] * sizePlus)
                    + (betaHHSizeMinus[i] * sizeMinus) + (betaIncomePlus[i] * incPlus)
                    + (betaIncomeMinus[i] * incMinus) + (betaLicensePlus[i] * licPlus) + (betaChangeResidence[i] * changeRes);
            double result = Math.exp(utility);
            sum += result;
            results[i+1] = result;
        }

        double probNoChange = 1.0 / (sum + 1.0);

        sum = 0;
        for(int i = 0; i < 2; i++) {
            results[i+1] = results[i+1] * probNoChange;
            sum += results[i+1];
        }

        results[0] = 1 - sum;
        return results;
    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void endYear(int year) {
        updateCarOwnership();
    }

    @Override
    public void endSimulation() {

    }


    private void updateCarOwnership() {

        int[] counter = new int[2];
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        for (Household oldHousehold : householdDataManager.getHouseholdMementos()) {
            Household newHousehold = householdDataManager.getHouseholdFromId(oldHousehold.getId());
            if (newHousehold != null) {
                int previousCars = oldHousehold.getAutos();
                int hhSizePlus = 0;
                int hhSizeMinus = 0;
                int hhIncomePlus = 0;
                int hhIncomeMinus = 0;
                int licensePlus = 0;

                boolean changeResidence = newHousehold.getDwellingId() == oldHousehold.getDwellingId();

                if (newHousehold.getHhSize() > oldHousehold.getHhSize()){
                    hhSizePlus = 1;
                } else if (newHousehold.getHhSize() < oldHousehold.getHhSize()){
                    hhSizeMinus = 1;

                }
                final int newIncome = HouseholdUtil.getAnnualHhIncome(newHousehold);
                final int oldIncome = HouseholdUtil.getAnnualHhIncome(oldHousehold);
                if (newIncome > oldIncome + 6000) {
                    hhIncomePlus = 1;
                } else if (newIncome < oldIncome - 6000) {
                    hhIncomeMinus = 1;
                }

                if (HouseholdUtil.getHHLicenseHolders(newHousehold) > HouseholdUtil.getHHLicenseHolders(oldHousehold)){
                    licensePlus = 1;
                }

                double[] prob = carUpdateProb[previousCars][hhSizePlus][hhSizeMinus][hhIncomePlus][hhIncomeMinus][licensePlus][changeResidence?1:0];

                int action = SiloUtil.select(prob, random);

                if (action == ADD_ONE_CAR){
                    if (newHousehold.getAutos() < MAX_NUMBER_OF_CARS) {
                        newHousehold.setAutos(newHousehold.getAutos() + 1);
                        counter[0]++;
                    }
                } else if (action == REMOVE_ONE_CAR) {
                    if (newHousehold.getAutos() > 0){
                        newHousehold.setAutos(newHousehold.getAutos() - 1);
                        counter[1]++;
                    }
                }
            }
        }
        final double numberOfHh = householdDataManager.getHouseholds().size();
        logger.info("  Simulated household added a car: " + counter[0] + " (" +
                SiloUtil.rounder((100f * counter[0] / numberOfHh), 0) + "% of hh)");

        logger.info("  Simulated household relinquished a car: " + counter[1] + " (" +
                SiloUtil.rounder((100f * counter[1] / numberOfHh), 0) + "% of hh)");
    }
}
