package de.tum.bgu.msm.scenarios.oneCarPolicy;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.ZoneMuc;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.vehicle.Car;
import de.tum.bgu.msm.data.vehicle.CarType;
import de.tum.bgu.msm.data.vehicle.VehicleUtil;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.carOwnership.CreateCarOwnershipJSCalculatorMuc;
import de.tum.bgu.msm.models.carOwnership.CreateCarOwnershipModelMuc;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;

public class OneCarCreateCarOwnershipModelMuc implements CreateCarOwnershipModel {

    private static Logger logger = Logger.getLogger(CreateCarOwnershipModelMuc.class);
    private final DataContainer dataContainer;
    private final DefaultGeoData geoData;

    public OneCarCreateCarOwnershipModelMuc(DataContainer dataContainer) {
        logger.info(" Setting up probabilities for car ownership model");
        this.dataContainer = dataContainer;
        this.geoData = (DefaultGeoData) dataContainer.getGeoData();
    }

    /**
     * Simulate car ownership for all households
     */
    @Override
    public void run() {
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            simulateCarOwnership(hh);
        }
    }

    /**
     * Simulates the number of cars for a given household. This method can only be executed after all households have
     * been generated and allocated to zones, as distance to transit and areaType is dependent on where households live
     * @param hh the household for which number of cars have to be simulated
     */
    @Override
    public void simulateCarOwnership(Household hh) {
        int license = HouseholdUtil.getHHLicenseHolders(hh);
        int workers = HouseholdUtil.getNumberOfWorkers(hh);
        int income = HouseholdUtil.getAnnualHhIncome(hh)/12;  // convert yearly into monthly income
        ZoneMuc zone = (ZoneMuc) geoData.getZones().get(dataContainer.getRealEstateDataManager().
                getDwelling(hh.getDwellingId()).getZoneId());

        double logDistanceToTransit = Math.log(zone.getPTDistance_m() + 1); // add 1 to avoid taking log of 0
        int areaType = zone.getAreaTypeSG().code();

        double[] prob = Calculator.getProbabilities(license, workers, income, logDistanceToTransit, areaType);
        final int numberOfCars = Math.min(1, SiloUtil.select(prob));
        for (int i = 0; i< numberOfCars; i++){
            hh.getVehicles().add(new Car(VehicleUtil.getHighestVehicleIdInHousehold(hh), CarType.CONVENTIONAL, VehicleUtil.getVehicleAgeInBaseYear()));
        }
    }



    private static class Calculator {

        static final double[] intercept = {-4.69730, -10.98800, -17.00200};
        static final double[] betaLicense = {3.11410, 4.65460, 5.71850};
        static final double[] betaWorkers = {0.22840, 0.76420, 1.13260};
        static final double[] betaIncome = {0.00070, 0.00100, 0.00120};
        static final double[] betaDistance = {0.15230, 0.25180, 0.25930};
        static final double[][] betaAreaType = {
                {0., 0.88210, 0.99270, 1.34420},
                {0., 1.43410, 1.60730, 2.25490},
                {0., 1.69830, 1.91330, 2.93080}
        };

        private static double[] getProbabilities(int license, int workers, int income, double logDistanceToTransit, int areaType) {

            double[] results = new double[4];
            double sum = 0;
            for (int i = 0; i < 3; i++) {
                double utility = intercept[i] + (betaLicense[i] * license) + (betaWorkers[i] * workers) + (betaIncome[i] * income) + (betaDistance[i] * logDistanceToTransit) + betaAreaType[i][areaType / 10 - 1];
                double result = Math.exp(utility);
                sum += result;
                results[i + 1] = result;
            }

            double prob0cars = 1.0 / (sum + 1.0);

            sum = 0;
            for (int i = 0; i < 3; i++) {
                results[i + 1] = results[i + 1] * prob0cars;
                sum += results[i + 1];
            }

            results[0] = 1 - sum;
            return results;
        }
    }
}
