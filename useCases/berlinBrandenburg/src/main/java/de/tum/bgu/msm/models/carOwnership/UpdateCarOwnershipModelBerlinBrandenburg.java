package de.tum.bgu.msm.models.carOwnership;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.vehicle.*;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Implements car ownership level change (subsequent years) for the Munich Metropolitan Area
 * @author Matthew Okrah
 * Created on 28/08/2017 in Munich, Germany.
 */
public class UpdateCarOwnershipModelBerlinBrandenburg extends AbstractModel implements ModelUpdateListener {

    private static Logger logger = LogManager.getLogger(UpdateCarOwnershipModelBerlinBrandenburg.class);

    private double[][][][][][][][] carUpdateProb; // [previousCars][hhSize+][hhSize-][income+][income-][license+][changeRes][three probabilities]

    public UpdateCarOwnershipModelBerlinBrandenburg(DataContainer dataContainer, Properties properties, Random rnd) {
        super(dataContainer, properties, rnd);
    }

    @Override
    public void setup() {
        //set car update probabilities
        carUpdateProb = new double[4][2][2][2][2][2][2][3];
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

    private double[] calculateCarOwnerShipProbabilities(int previousCars, int hhSizePlus, int hhSizeMinus,
                                                        int hhIncomePlus, int hhIncomeMinus, int licensePlus, int changeResidence) {

        double[] intercept = {-3.0888, -5.6650};
        double[] betaPreviousCars = {-0.5201, 1.3526};
        double[] betaHHSizePlus = {2.0179, 0.0};
        double[] betaHHSizeMinus = {0.0, 1.1027};
        double[] betaIncomePlus = {0.4842, 0.0};
        double[] betaIncomeMinus = {0.0, 0.3275};
        double[] betaLicensePlus = {1.8213, 0.0};
        double[] betaChangeResidence = {1.1440, 0.9055};

        double[] results = new double[3];
        double sum = 0.0;

        for (int i = 0; i < 2; i++) {
            double utility = intercept[i]
                    + betaPreviousCars[i] * previousCars
                    + betaHHSizePlus[i] * hhSizePlus
                    + betaHHSizeMinus[i] * hhSizeMinus
                    + betaIncomePlus[i] * hhIncomePlus
                    + betaIncomeMinus[i] * hhIncomeMinus
                    + betaLicensePlus[i] * licensePlus
                    + betaChangeResidence[i] * changeResidence;

            results[i + 1] = Math.exp(utility);
            sum += results[i + 1];
        }

        double probNoChange = 1.0 / (sum + 1.0);
        double sumProb = 0.0;

        for (int i = 0; i < 2; i++) {
            results[i + 1] *= probNoChange;
            sumProb += results[i + 1];
        }

        results[0] = 1.0 - sumProb;

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

    public void summarizeCarUpdate() {
        // This function summarizes household car ownership update and quits
        PrintWriter pwa = SiloUtil.openFileForSequentialWriting("microData/interimFiles/carUpdate.csv", false);
        pwa.println("id, dwelling, zone, license, income, size, autos");
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        for (Household hh: householdDataManager.getHouseholds()) {
            Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId());
            int homeZone = -1;
            if(dwelling != null) {
                homeZone = dwelling.getZoneId();
            }
            pwa.println(hh.getId() + "," + hh.getDwellingId() + "," + homeZone + "," +
                    HouseholdUtil.getHHLicenseHolders(hh)+ "," +  HouseholdUtil.getAnnualHhIncome(hh) + "," + hh.getHhSize() + "," + hh.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.CAR)).count());
        }
        pwa.close();

        logger.info("Summarized car update and quit.");
        System.exit(0);
    }

    private void updateCarOwnership() {

        int[] counter = new int[2];
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        for (Household oldHousehold : householdDataManager.getHouseholdMementos()) {
            Household newHousehold = householdDataManager.getHouseholdFromId(oldHousehold.getId());
            if (newHousehold != null) {
                int previousCars = (int) oldHousehold.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.CAR)).count();
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

                final long autos = newHousehold.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.CAR)).count();
                if (action == 1){ //add one car
                    if (autos < 3) { //maximum number of cars is equal to 3
                        newHousehold.getVehicles().add(new Car(VehicleUtil.getHighestVehicleIdInHousehold(newHousehold), CarType.CONVENTIONAL, VehicleUtil.getVehicleAgeInBaseYear()));
                        counter[0]++;
                    }
                } else if (action == 2) { //remove one car
                    if (autos > 0){ //cannot have less than zero cars

                        Vehicle vehicleToRemove = newHousehold.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.CAR)).findAny().orElse(null);
                        if (vehicleToRemove != null){
                            for (Vehicle vehicle : newHousehold.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.CAR)).collect(Collectors.toSet())) {
                                if (vehicle.getAge() > vehicleToRemove.getAge()){
                                    vehicleToRemove = vehicle;
                                }
                            }
                        }

                        boolean ok = newHousehold.getVehicles().remove(vehicleToRemove);

                        if (!ok){
                            throw new RuntimeException();
                        }

                        counter[1]++;
                        // update number of AVs if necessary after household relinquishes a car
//                        if (newHousehold.getAutonomous() > autos) { // no. of AVs cannot exceed total no. of autos
//                            newHousehold.setAutonomous(autos);
//                        }
//                        if ((int) newHousehold.getAttribute("EV").orElse(0) > autos) { // no. of AVs cannot exceed total no. of autos
//                            newHousehold.setAttribute("EV", autos);
//                        }

                    }
                }
            }
        }
        final double numberOfHh = householdDataManager.getHouseholds().size();
        //todo reconsider to print out model results and how to pass them to the ResultsMonitor
        logger.info("  Simulated household added a car: " + counter[0] + " (" +
                SiloUtil.rounder((100f * counter[0] / numberOfHh), 0) + "% of hh)");

        logger.info("  Simulated household relinquished a car: " + counter[1] + " (" +
                SiloUtil.rounder((100f * counter[1] / numberOfHh), 0) + "% of hh)");

    }
}
