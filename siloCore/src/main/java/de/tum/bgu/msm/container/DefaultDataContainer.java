package de.tum.bgu.msm.container;

import de.tum.bgu.msm.data.TravelTimesWrapper;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

/**
 * @author moeckel
 * The Silo Data Container holds all the various Data classes used by the SILO events.
 * Once the DataContainer is created using the resourceBundle, each module can be retrieved
 * using the respective getter.  \n
 * All the data items are constructed within the ModelContainer
 */
public class DefaultDataContainer implements DataContainer {

    private final static Logger logger = Logger.getLogger(DefaultDataContainer.class);

    private final HouseholdDataManager householdDataManager;
    private final RealEstateDataManager realEstateDataManager;
    private final JobDataManager jobDataManager;
    private final GeoData geoData;
    private final TravelTimesWrapper travelTimes;
    private final Accessibility accessibility;
    private final CommutingTimeProbability commutingTimeProbability;
    protected Properties properties;

    public DefaultDataContainer(
            GeoData geoData, RealEstateDataManager realEstateDataManager,
            JobDataManager jobDataManager, HouseholdDataManager householdDataManager,
            TravelTimes travelTimes, Accessibility accessibility,
            CommutingTimeProbability commutingTimeProbability, Properties properties) {
        this.geoData = geoData;
        this.realEstateDataManager = realEstateDataManager;
        this.jobDataManager = jobDataManager;
        this.householdDataManager = householdDataManager;
        this.travelTimes = new TravelTimesWrapper(travelTimes, properties, geoData);
        this.accessibility = accessibility;
        this.commutingTimeProbability = commutingTimeProbability;
        this.properties = properties;
    }

    @Override
    public HouseholdDataManager getHouseholdDataManager() {
        return householdDataManager;
    }

    @Override
    public RealEstateDataManager getRealEstateDataManager() {
        return realEstateDataManager;
    }

    @Override
    public JobDataManager getJobDataManager() {
        return jobDataManager;
    }

    @Override
    public GeoData getGeoData() {
        return geoData;
    }

    @Override
    public TravelTimes getTravelTimes() {
        return travelTimes.getDelegate();
    }

    @Override
    public Accessibility getAccessibility() {
        return accessibility;
    }
    
    @Override
    public CommutingTimeProbability getCommutingTimeProbability() {
    	return commutingTimeProbability;
    }

    @Override
    public void setup() {
        geoData.setup();
        householdDataManager.setup();
        jobDataManager.setup();
        realEstateDataManager.setup();
        travelTimes.setup();
        accessibility.setup();
        commutingTimeProbability.setup();
    }

    @Override
    public void prepareYear(int year) {
    	geoData.prepareYear(year);
    	householdDataManager.prepareYear(year);
    	jobDataManager.prepareYear(year);
    	realEstateDataManager.prepareYear(year);
    	travelTimes.prepareYear(year);
    	accessibility.prepareYear(year);
    	commutingTimeProbability.prepareYear(year);
    }

    @Override
    public void endYear(int year) {
        geoData.endYear(year);
        householdDataManager.endYear(year);
        jobDataManager.endYear(year);
        realEstateDataManager.endYear(year);
        travelTimes.endYear(year);
        accessibility.endYear(year);
        commutingTimeProbability.endYear(year);
    }

    @Override
    public void endSimulation() {
        geoData.endSimulation();
        householdDataManager.endSimulation();
        jobDataManager.endSimulation();
        realEstateDataManager.endSimulation();
        travelTimes.endSimulation();
        accessibility.endSimulation();
        commutingTimeProbability.endSimulation();
    }
}
