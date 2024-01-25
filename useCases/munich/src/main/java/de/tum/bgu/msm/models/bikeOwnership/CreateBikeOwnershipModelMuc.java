package de.tum.bgu.msm.models.bikeOwnership;


import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.vehicle.Vehicle;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.ZoneMuc;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.models.bikeOwnership.CreateBikeOwnershipModel;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Implements bike ownership of initial synthetic population (base year) for the Munich Metropolitan Area
 *
 *  * Repurposed from CarOwnership by Kieran on 25/01/2024.
 */

public class CreateBikeOwnershipModelMuc implements CreateBikeOwnershipModel {

    private static Logger logger = Logger.getLogger(CreateBikeOwnershipModelMuc.class);
    private final CreateBikeOwnershipJSCalculatorMuc calculator;
    private final DataContainer dataContainer;
    private final DefaultGeoData geoData;

    public CreateBikeOwnershipModelMuc(DataContainer dataContainer) {
        logger.info(" Setting up probabilities for bike ownership model");
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("CreateBikeOwnershipCalc"));
        calculator = new CreateBikeOwnershipJSCalculatorMuc(reader);
        this.dataContainer = dataContainer;
        this.geoData = (DefaultGeoData) dataContainer.getGeoData();
    }

    /**
     * Simulate bike ownership for all households
     */
    @Override
    public void run() {
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            simulateBikeOwnership(hh);
        }
        //SummarizeData.summarizeBikeOwnershipByMunicipality(zonalData);
    }

    /**
     * Simulates the number of bikes for a given household. This method can only be executed after all households have
     * been generated and allocated to zones, as distance to transit and areaType is dependent on where households live
     * @param hh the household for which number of bikes have to be simulated
     */
    @Override
    public void simulateBikeOwnership(Household hh) {
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

        for (int i =0; i < numberOfBikes; i++){
            hh.getVehicles().add(new Bike();
        }
    }
}



