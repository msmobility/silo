package run;

import data.OnTheFlyCommutingTimeProbability;
import data.SandboxDwellingTypes;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.DefaultDataContainer;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.AccessibilityImpl;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.job.*;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.data.person.PersonFactoryImpl;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.input.*;
import de.tum.bgu.msm.matsim.MatsimTravelTimesAndCosts;
import de.tum.bgu.msm.properties.Properties;
import org.matsim.core.config.Config;

public class DataBuilderFabiland {

    private DataBuilderFabiland() {
    }

    public static DataContainer buildDataContainer(Properties properties, Config config) {

        GeoData geoData = new DefaultGeoData();

        DwellingData dwellingData = new DwellingDataImpl();
        HouseholdData householdData = new HouseholdDataImpl();
        JobData jobData = new JobDataImpl();

        TravelTimes travelTimes = null;
        Accessibility accessibility = null;

        switch (properties.transportModel.travelTimeImplIdentifier) {
            case SKIM:
                travelTimes = new SkimTravelTimes();
                accessibility = new AccessibilityImpl(geoData, travelTimes, properties, dwellingData, jobData);
                break;
            case MATSIM:
                travelTimes = new MatsimTravelTimesAndCosts(config);
//                accessibility = new MatsimAccessibility(geoData);
                accessibility = new AccessibilityImpl(geoData, travelTimes, properties, dwellingData, jobData);
                break;
            default:
                break;
        }

        CommutingTimeProbability commutingTimeProbability = new OnTheFlyCommutingTimeProbability();

        //TODO: revise this!
        new JobType(properties.jobData.jobTypes);

        RealEstateDataManager realEstateManager = new RealEstateDataManagerImpl(
                new SandboxDwellingTypes(),
                dwellingData,
                householdData, geoData,
                new DwellingFactoryImpl(),
                properties);

        JobDataManager jobManager = new JobDataManagerImpl(
                properties, new JobFactoryImpl(),
                jobData, geoData,
                travelTimes, commutingTimeProbability);

        final HouseholdFactory hhFactory = new HouseholdFactoryImpl();
        final PersonFactory ppFactory = new PersonFactoryImpl();

        HouseholdDataManager householdManager = new HouseholdDataManagerImpl(
                householdData, dwellingData,
                ppFactory, hhFactory,
                properties, realEstateManager);

        DataContainer dataContainer = new DefaultDataContainer(
                geoData, realEstateManager,
                jobManager, householdManager,
                travelTimes, accessibility, commutingTimeProbability, properties);
        return dataContainer;
    }

    public static void readInput(Properties properties, DataContainer dataContainer) {
        final GeoDataReader geoDataReaderMstm = new DefaultGeoDataReader(dataContainer.getGeoData());

        String fileName = properties.main.baseDirectory + properties.geo.zonalDataFile;
        String pathShp = properties.main.baseDirectory + properties.geo.zoneShapeFile;
        geoDataReaderMstm.readZoneCsv(fileName);
        geoDataReaderMstm.readZoneShapefile(pathShp);

        int year = properties.main.startYear;

        readHouseholds(properties, dataContainer.getHouseholdDataManager(),
                dataContainer.getHouseholdDataManager().getHouseholdFactory(), year);
        readPersons(properties, dataContainer.getHouseholdDataManager(), dataContainer.getHouseholdDataManager().getPersonFactory(), year);
        readDwellings(properties, dataContainer.getRealEstateDataManager(), year);

        JobReader jjReader = new DefaultJobReader(dataContainer.getJobDataManager());
        String jobsFile = properties.main.baseDirectory + properties.jobData.jobsFileName + "_" + year + ".csv";
        jjReader.readData(jobsFile);
    }

    private static void readDwellings(Properties properties, RealEstateDataManager realEstateManager, int year) {
        DwellingReader ddReader = new DefaultDwellingReader(realEstateManager);
        String dwellingsFile = properties.main.baseDirectory + properties.realEstate.dwellingsFileName + "_" + year + ".csv";
        ddReader.readData(dwellingsFile);
    }

    private static void readHouseholds(Properties properties, HouseholdDataManager householdData, HouseholdFactory hhFactory, int year) {
        String householdFile = properties.main.baseDirectory + properties.householdData.householdFileName;
        householdFile += "_" + year + ".csv";
        DefaultHouseholdReader hhReader = new DefaultHouseholdReader(householdData, hhFactory);
        hhReader.readData(householdFile);
    }

    private static void readPersons(Properties properties, HouseholdDataManager householdData, PersonFactory ppFactory, int year) {
        String personFile = properties.main.baseDirectory + properties.householdData.personFileName;
        personFile += "_" + year + ".csv";
        PersonReader personReader = new DefaultPersonReader(householdData);
        personReader.readData(personFile);
    }

}
