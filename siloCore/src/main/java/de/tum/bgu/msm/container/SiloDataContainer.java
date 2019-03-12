package de.tum.bgu.msm.container;

import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.JobDataManager;
import de.tum.bgu.msm.data.RealEstateDataManager;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.simulator.AnnualUpdate;

/**
 * //TODO
 */
public interface SiloDataContainer extends AnnualUpdate {

    HouseholdDataManager getHouseholdData();

    RealEstateDataManager getRealEstateData();

    JobDataManager getJobData();

    GeoData getGeoData();

    TravelTimes getTravelTimes();

    Accessibility getAccessibility();

}
