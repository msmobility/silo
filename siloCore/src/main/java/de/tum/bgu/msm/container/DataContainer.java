package de.tum.bgu.msm.container;

import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.simulator.UpdateListener;

/**
 * //TODO
 */
public interface DataContainer extends UpdateListener {

    HouseholdDataManager getHouseholdDataManager();

    RealEstateDataManager getRealEstateDataManager();

    JobDataManager getJobDataManager();

    GeoData getGeoData();

    TravelTimes getTravelTimes();

    Accessibility getAccessibility();
    
    CommutingTimeProbability getCommutingTimeProbability();
}
