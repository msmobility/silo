package de.tum.bgu.msm.models.autoOwnership.munich;


import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Household;
import de.tum.bgu.msm.data.munich.GeoDataMuc;
import de.tum.bgu.msm.data.munich.MunichZone;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Implements car ownership of initial synthetic population (base year) for the Munich Metropolitan Area
 *
 * @author Matthew Okrah
 *         Created on 28/04/2017 in Munich, Germany.
 */

public class CreateCarOwnershipModel {

    static Logger logger = Logger.getLogger(CreateCarOwnershipModel.class);
    private final CreateCarOwnershipJSCalculator calculator;
    private final SiloDataContainer dataContainer;
    private final GeoDataMuc geoDataMuc;

    public CreateCarOwnershipModel(SiloDataContainer dataContainer, GeoDataMuc geoDataMuc) {
        logger.info(" Setting up probabilities for car ownership model");
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("CreateCarOwnershipCalc"));
        calculator = new CreateCarOwnershipJSCalculator(reader);
        this.dataContainer = dataContainer;
        this.geoDataMuc = geoDataMuc;
    }

    /**
     * Simulate car ownership for all households
     */
    public void run() {
        for (Household hh : dataContainer.getHouseholdData().getHouseholds()) {
            simulateCarOwnership(hh);
        }
        //SummarizeData.summarizeCarOwnershipByMunicipality(zonalData);
    }

    /**
     * Simulates the number of cars for a given household. This method can only be executed after all households have
     * been generated and allocated to zones, as distance to transit and areaType is dependent on where households live
     * @param hh the household for which number of cars have to be simulated
     */
    public void simulateCarOwnership(Household hh) {
        int license = hh.getHHLicenseHolders();
        int workers = hh.getNumberOfWorkers();
        int income = hh.getHhIncome()/12;  // convert yearly into monthly income
        MunichZone zone = (MunichZone) geoDataMuc.getZones().get(dataContainer.getRealEstateData().
                getDwelling(hh.getDwellingId()).getZoneId());

        double logDistanceToTransit = Math.log(zone.getPTDistance() + 1); // add 1 to avoid taking log of 0
        int areaType = zone.getAreaType().code();

        double[] prob = calculator.calculate(license, workers, income, logDistanceToTransit, areaType);
        hh.setAutos(SiloUtil.select(prob));
    }
}


