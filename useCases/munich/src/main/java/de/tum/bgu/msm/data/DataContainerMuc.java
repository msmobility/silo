package de.tum.bgu.msm.data;

import de.tum.bgu.msm.container.DefaultDataContainer;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.school.SchoolData;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;

/**
 * @author moeckel
 * The Silo Data Container holds all the various Data classes used by the SILO events.
 * Once the DataContainer is created using the resourceBundle, each module can be retrieved
 * using the respective getter.  \n
 * All the data items are constructed within the ModelContainer
 */
public class DataContainerMuc extends DefaultDataContainer {

    private final SchoolData schoolData;

    public DataContainerMuc(
            GeoData geoData, RealEstateDataManager realEstateDataManager,
            JobDataManager jobDataManager, HouseholdDataManager householdDataManager,
            TravelTimes travelTimes, Accessibility accessibility,
            SchoolData schoolData, Properties properties) {
        super(geoData, realEstateDataManager, jobDataManager, householdDataManager, travelTimes, accessibility, properties);
        this.schoolData = schoolData;
    }


//    /**
//     * The contructor is private, with a factory method {@link DataContainerMuc#loadSiloDataContainer(Properties)}
//     * being used to encapsulate the object creation.
//     */
//    private DataContainerMuc() {
//
//        //todo modify when different dwelling types are available
//        List<DwellingType> dwellingTypeList = new ArrayList<>();
//        Collections.addAll(dwellingTypeList, DefaultDwellingTypeImpl.values());
//
//        schoolData = new SchoolDataImpl(this, properties);
//        geoData = new DefaultGeoData();
//        realEstateData = new RealEstateDataManagerImpl(this, dwellingTypeList, new DwellingFactoryImpl());
//        jobDataManager = new JobDataManagerImpl(this, properties, new JobFactoryImpl());
//        householdDataManager = new HouseholdDataManagerImpl(this, new PersonFactoryImpl(), new HouseholdFactoryImpl());
//        travelTimes = new TravelTimesWrapper(new SkimTravelTimes(), properties);
//        accessibility = new Accessibility(this, properties);
//    }


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
        if (year != properties.main.baseYear) {
            super.prepareYear(year);
            schoolData.prepareYear(year);
        }
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
