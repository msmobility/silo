package de.tum.bgu.msm.models.carOwnership;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.AreaTypes;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.models.autoOwnership.CreateCarOwnershipModel;
import de.tum.bgu.msm.utils.SampleException;
import de.tum.bgu.msm.utils.Sampler;
import org.apache.log4j.Logger;

public class CreateCarOwnershipTak implements CreateCarOwnershipModel {

    private static Logger logger = Logger.getLogger(CreateCarOwnershipTak.class);

    private final DataContainer dataContainer;
    private final DefaultGeoData geoData;
    private final CreateCarOwnershipStrategy strategy;

    public CreateCarOwnershipTak(DataContainer dataContainer, CreateCarOwnershipStrategy strategy) {
        this.strategy = strategy;
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
        // convert yearly into monthly income
        int income = HouseholdUtil.getAnnualHhIncome(hh)/12;
        Zone zone = geoData.getZones().get(dataContainer.getRealEstateDataManager().
                getDwelling(hh.getDwellingId()).getZoneId());

        //TODO: Reconsider static distance to transit for Kagawa zones in CarownershipModel
        // add 1 to avoid taking log of 0
        double logDistanceToTransit = Math.log(1000 + 1);

        //TODO: Reconsider static area type for Kagawa zones in CarownershipModel
        int areaType = AreaTypes.SGType.MEDIUM_SIZED_CITY.code();

        Sampler<Integer> sampler = strategy.getSampler(license, workers, income, logDistanceToTransit, areaType);
        try {
            hh.setAutos(sampler.sampleObject());
        } catch (SampleException e) {
            throw new RuntimeException(e);
        }
    }
}
