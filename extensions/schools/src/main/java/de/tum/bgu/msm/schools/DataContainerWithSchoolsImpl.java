package de.tum.bgu.msm.schools;

import de.tum.bgu.msm.container.DefaultDataContainer;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;

/**
 * @author moeckel
 * The Silo Data Container holds all the various Data classes used by the SILO events.
 * Once the DataContainer is created using the resourceBundle, each module can be retrieved
 * using the respective getter.
 * All the data items are constructed within the ModelContainer
 */
public class DataContainerWithSchoolsImpl extends DefaultDataContainer implements DataContainerWithSchools {

    private final SchoolData schoolData;

    public DataContainerWithSchoolsImpl(
            GeoData geoData, RealEstateDataManager realEstateDataManager,
            JobDataManager jobDataManager, HouseholdDataManager householdDataManager,
            TravelTimes travelTimes, Accessibility accessibility,
            CommutingTimeProbability commutingTimeProbability,
            SchoolData schoolData, Properties properties) {
        super(geoData, realEstateDataManager, jobDataManager, householdDataManager,
        		travelTimes, accessibility, commutingTimeProbability, properties);
        this.schoolData = schoolData;
    }

    @Override
    public SchoolData getSchoolData() {
        return schoolData;
    }

    @Override
    public void setup() {
        super.setup();
        schoolData.setup();
    }

    @Override
    public void prepareYear(int year) {
    	super.prepareYear(year);
    	schoolData.prepareYear(year);
    }

    @Override
    public void endYear(int year) {
        super.endYear(year);
        schoolData.endYear(year);
    }

    @Override
    public void endSimulation() {
        super.endSimulation();
        schoolData.endSimulation();
    }
}
