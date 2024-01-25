package de.tum.bgu.msm.models.bikeOwnership;

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
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Implements bike ownership level change (subsequent years) for the Munich Metropolitan Area
 *  * Repurposed from CarOwnership by Kieran on 25/01/2024.
 */
public class UpdateBikeOwnershipModelMuc extends AbstractModel implements ModelUpdateListener {

    private static Logger logger = Logger.getLogger(UpdateBikeOwnershipModelMuc.class);

    private double[][][][][][][][] bikeUpdateProb; // [previousBikes][hhSize+][hhSize-][income+][income-][license+][changeRes][three probabilities]

    private final Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("UpdateBikeOwnershipCalc"));

    public UpdateBikeOwnershipModelMuc(DataContainer dataContainer, Properties properties, Random rnd) {
        super(dataContainer, properties, rnd);
    }

    @Override
    public void setup() {
//        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("UpdateBikeOwnershipCalc"));
        BikeOwnershipJSCalculatorMuc calculator = new BikeOwnershipJSCalculatorMuc(reader);
        //set bike update probabilities
        bikeUpdateProb = new double[4][2][2][2][2][2][2][3];
        for (int prevBike = 0; prevBike < 4; prevBike++){
            for (int sizePlus = 0; sizePlus < 2; sizePlus++){
                for (int sizeMinus = 0; sizeMinus < 2; sizeMinus++){
                    for (int incPlus = 0; incPlus < 2; incPlus++){
                        for (int incMinus = 0; incMinus < 2; incMinus++){
                            for (int licPlus = 0; licPlus < 2; licPlus++){
                                for (int changeRes = 0; changeRes < 2; changeRes++){
                                    bikeUpdateProb[prevBike][sizePlus][sizeMinus][incPlus][incMinus][licPlus][changeRes] =
                                            calculator.calculateBikeOwnerShipProbabilities(prevBike, sizePlus, sizeMinus, incPlus, incMinus, licPlus, changeRes);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void endYear(int year) {
        updateBikeOwnership();
    }

    @Override
    public void endSimulation() {

    }

    public void summarizeBikeUpdate() {
        // This function summarizes household bike ownership update and quits
        PrintWriter pwa = SiloUtil.openFileForSequentialWriting("microData/interimFiles/bikeUpdate.csv", false);
        pwa.println("id, dwelling, zone, license, income, size, autos");
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        for (Household hh: householdDataManager.getHouseholds()) {
            Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId());
            int homeZone = -1;
            if(dwelling != null) {
                homeZone = dwelling.getZoneId();
            }
            pwa.println(hh.getId() + "," + hh.getDwellingId() + "," + homeZone + "," +
                    HouseholdUtil.getHHLicenseHolders(hh)+ "," +  HouseholdUtil.getAnnualHhIncome(hh) + "," + hh.getHhSize() + "," + hh.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.BIKE)).count());
        }
        pwa.close();

        logger.info("Summarized bike update and quit.");
        System.exit(0);
    }

    private void updateBikeOwnership() {

        int[] counter = new int[2];
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        for (Household oldHousehold : householdDataManager.getHouseholdMementos()) {
            Household newHousehold = householdDataManager.getHouseholdFromId(oldHousehold.getId());
            if (newHousehold != null) {
                int previousBikes = (int) oldHousehold.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.BIKE)).count();
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

                double[] prob = bikeUpdateProb[previousBikes][hhSizePlus][hhSizeMinus][hhIncomePlus][hhIncomeMinus][licensePlus][changeResidence?1:0];

                int action = SiloUtil.select(prob, random);

                final long autos = newHousehold.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.BIKE)).count();
                if (action == 1){ //add one bike
                    if (autos < 3) { //maximum number of bikes is equal to 3
                        newHousehold.getVehicles().add(new Bike(VehicleUtil.getHighestVehicleIdInHousehold(newHousehold), BikeType.CONVENTIONAL, VehicleUtil.getVehicleAgeInBaseYear()));
                        counter[0]++;
                    }
                } else if (action == 2) { //remove one bike
                    if (autos > 0){ //cannot have less than zero bikes

                        Vehicle vehicleToRemove = newHousehold.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.BIKE)).findAny().orElse(null);
                        if (vehicleToRemove != null){
                            for (Vehicle vehicle : newHousehold.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.BIKE)).collect(Collectors.toSet())) {
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
                        // update number of AVs if necessary after household relinquishes a bike
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
        logger.info("  Simulated household added a bike: " + counter[0] + " (" +
                SiloUtil.rounder((100f * counter[0] / numberOfHh), 0) + "% of hh)");

        logger.info("  Simulated household relinquished a bike: " + counter[1] + " (" +
                SiloUtil.rounder((100f * counter[1] / numberOfHh), 0) + "% of hh)");

    }
}
