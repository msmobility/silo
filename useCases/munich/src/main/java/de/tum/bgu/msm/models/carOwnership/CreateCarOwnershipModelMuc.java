package de.tum.bgu.msm.models.carOwnership;


import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdMuc;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.geo.ZoneMuc;
import de.tum.bgu.msm.data.vehicle.Car;
import de.tum.bgu.msm.data.vehicle.CarType;
import de.tum.bgu.msm.data.vehicle.VehicleUtil;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Implements car ownership of initial synthetic population (base year) for the Munich Metropolitan Area
 *
 * @author Matthew Okrah
 *         Created on 28/04/2017 in Munich, Germany.
 */

public class CreateCarOwnershipModelMuc implements CreateCarOwnershipModel {

    private static Logger logger = Logger.getLogger(CreateCarOwnershipModelMuc.class);
    private final CreateCarOwnershipJSCalculatorMuc calculator;
    private final DataContainer dataContainer;
    private final DefaultGeoData geoData;

    public CreateCarOwnershipModelMuc(DataContainer dataContainer) {
        logger.info(" Setting up probabilities for car ownership model");
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("CreateCarOwnershipCalc"));
        calculator = new CreateCarOwnershipJSCalculatorMuc(reader);
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
        ZoneMuc zone = (ZoneMuc) geoData.getZones().get(dataContainer.getRealEstateDataManager().
                getDwelling(hh.getDwellingId()).getZoneId());
        //int zoneId = (int) ((HouseholdMuc)hh).getAttribute("zone").get();


        double logDistanceToTransit = Math.log(zone.getPTDistance_m() + 1); // add 1 to avoid taking log of 0
        int areaType = zone.getAreaTypeSG().code();

        double[] prob = calculator.calculate(license, workers, income, logDistanceToTransit, areaType);
        final int numberOfAutos = SiloUtil.select(prob);

        hh.getVehicles().clear();

        for (int i =0; i < numberOfAutos; i++){
            hh.getVehicles().add(new Car(VehicleUtil.getHighestVehicleIdInHousehold(hh), CarType.CONVENTIONAL, VehicleUtil.getVehicleAgeInBaseYear()));
        }
    }
}


