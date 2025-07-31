package de.tum.bgu.msm.models.carOwnership;


import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.ZoneBerlinBrandenburg;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.vehicle.Car;
import de.tum.bgu.msm.data.vehicle.CarType;
import de.tum.bgu.msm.data.vehicle.VehicleUtil;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Implements car ownership of initial synthetic population (base year) for the Munich Metropolitan Area
 *
 * @author Matthew Okrah
 *         Created on 28/04/2017 in Munich, Germany.
 */

public class CreateCarOwnershipModelBerlinBrandenburg implements CreateCarOwnershipModel {

    private static Logger logger = LogManager.getLogger(CreateCarOwnershipModelBerlinBrandenburg.class);
    private final DataContainer dataContainer;
    private final DefaultGeoData geoData;

    public CreateCarOwnershipModelBerlinBrandenburg(DataContainer dataContainer) {
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
        //SummarizeData.summarizeCarOwnershipByMunicipality(zonalData);
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
        ZoneBerlinBrandenburg zone = (ZoneBerlinBrandenburg) geoData.getZones().get(dataContainer.getRealEstateDataManager().
                getDwelling(hh.getDwellingId()).getZoneId());
        //int zoneId = (int) ((HouseholdMuc)hh).getAttribute("zone").get();


        double logDistanceToTransit = Math.log(zone.getPTDistance_m() + 1); // add 1 to avoid taking log of 0
        int areaType = zone.getAreaTypeSG().code();

        double areaTypeCoefficient_areaType20_utility1 = 0.88210;
        double areaTypeCoefficient_areaType30_utility1 = 0.99270;
        double areaTypeCoefficient_areaType40_utility1 = 1.34420;

        double areaTypeCoefficient_areaType20_utility2 = 1.43410;
        double areaTypeCoefficient_areaType30_utility2 = 1.60730;
        double areaTypeCoefficient_areaType40_utility2 = 2.25490;

        double areaTypeCoefficient_areaType20_utility3 = 1.69830;
        double areaTypeCoefficient_areaType30_utility3 = 1.91330;
        double areaTypeCoefficient_areaType40_utility3 = 2.93080;

        double areaTypeCoefficient_utility1 = 0.0;
        double areaTypeCoefficient_utility2 = 0.0;
        double areaTypeCoefficient_utility3 = 0.0;

        switch (areaType){
            case 10:
                break;
            case 20:
                areaTypeCoefficient_utility1 = areaTypeCoefficient_areaType20_utility1;
                areaTypeCoefficient_utility2 = areaTypeCoefficient_areaType20_utility2;
                areaTypeCoefficient_utility3 = areaTypeCoefficient_areaType20_utility3;
                break;
            case 30:
                areaTypeCoefficient_utility1 = areaTypeCoefficient_areaType30_utility1;
                areaTypeCoefficient_utility2 = areaTypeCoefficient_areaType30_utility2;
                areaTypeCoefficient_utility3 = areaTypeCoefficient_areaType30_utility3;
                break;
            case 40:
                areaTypeCoefficient_utility1 = areaTypeCoefficient_areaType40_utility1;
                areaTypeCoefficient_utility2 = areaTypeCoefficient_areaType40_utility2;
                areaTypeCoefficient_utility3 = areaTypeCoefficient_areaType40_utility3;
                break;
        }

        //Todo the calculation is transfered from the Java script is not nicely coded now, consider polishing
        double utility0 = 0;
        double utility1 = -4.69730 +
                3.11410 * license +
                0.22840 * workers +
                0.00070 * income +
                0.15230 * logDistanceToTransit +
                areaTypeCoefficient_utility1;
        double utility2 = -10.98800 +
                4.65460 * license +
                0.76420 * workers +
                0.00100 * income +
                0.25180 * logDistanceToTransit +
                areaTypeCoefficient_utility2;
        double utility3 = -17.00200+
                5.71850 * license +
                1.13260 * workers +
                0.00120 * income +
                0.25180 * logDistanceToTransit +
                areaTypeCoefficient_utility3;

        double probability0 = Math.exp(utility0)/(Math.exp(utility0)+Math.exp(utility1)+Math.exp(utility2)+Math.exp(utility3));
        double probability1 = Math.exp(utility1)/(Math.exp(utility0)+Math.exp(utility1)+Math.exp(utility2)+Math.exp(utility3));
        double probability2 = Math.exp(utility2)/(Math.exp(utility0)+Math.exp(utility1)+Math.exp(utility2)+Math.exp(utility3));
        double probability3 = Math.exp(utility3)/(Math.exp(utility0)+Math.exp(utility1)+Math.exp(utility2)+Math.exp(utility3));

        double[] prob = new double[]{probability0, probability1, probability2, probability3};
        final int numberOfAutos = SiloUtil.select(prob);

        hh.getVehicles().clear();

        for (int i =0; i < numberOfAutos; i++){
            hh.getVehicles().add(new Car(VehicleUtil.getHighestVehicleIdInHousehold(hh), CarType.CONVENTIONAL, VehicleUtil.getVehicleAgeInBaseYear()));
        }
    }
}


