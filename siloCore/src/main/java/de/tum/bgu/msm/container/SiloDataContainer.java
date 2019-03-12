package de.tum.bgu.msm.container;

import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.JobDataManager;
import de.tum.bgu.msm.data.RealEstateDataManager;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;

/**
 * //TODO
 */
public interface SiloDataContainer {
    HouseholdDataManager getHouseholdData();

    RealEstateDataManager getRealEstateData();

    JobDataManager getJobData();

    GeoData getGeoData();

    TravelTimes getTravelTimes();
}
