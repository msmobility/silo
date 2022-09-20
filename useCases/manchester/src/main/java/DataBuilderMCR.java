import de.tum.bgu.msm.container.DefaultDataContainer;
import de.tum.bgu.msm.data.ManchesterDwellingTypes;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.AccessibilityImpl;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbabilityExponential;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.job.*;
import de.tum.bgu.msm.data.person.PersonFactoryImpl;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.MicroDataScaler;
import de.tum.bgu.msm.io.input.*;
import de.tum.bgu.msm.matsim.MatsimTravelTimesAndCosts;
import de.tum.bgu.msm.models.modeChoice.SimpleCommuteModeChoice;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import io.GeoDataReaderManchester;
import org.matsim.core.config.Config;

public class DataBuilderMCR {

    private DataBuilderMCR() {
    }

    public static DefaultDataContainer getModelDataForManchester(Properties properties, Config config) {

        HouseholdData householdData = new HouseholdDataImpl();
        JobData jobData = new JobDataImpl();
        DwellingData dwellingData = new DwellingDataImpl();

        GeoData geoData = new DefaultGeoData();

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
                throw new RuntimeException("Travel time not recognized! Please set property \"travel.time\" accordingly!");
        }

        CommutingTimeProbability commutingTimeProbability =
                new CommutingTimeProbabilityExponential(properties.accessibility.betaTimeCarExponentialCommutingTime,
                        properties.accessibility.betaTimePtExponentialCommutingTime);

        //TODO: revise this!
        new JobType(properties.jobData.jobTypes);


        JobFactory jobFactory = new JobFactoryImpl();

        RealEstateDataManager realEstateDataManager = new RealEstateDataManagerImpl(
                new ManchesterDwellingTypes(), dwellingData, householdData, geoData, new DwellingFactoryImpl(), properties);

        SimpleCommuteModeChoice commuteModeChoice = new SimpleCommuteModeChoice(commutingTimeProbability, travelTimes, geoData,
                properties, SiloUtil.provideNewRandom());
        JobDataManager jobDataManager = new JobDataManagerWithCommuteModeChoice(
                properties, jobFactory, jobData, geoData, travelTimes, commutingTimeProbability, commuteModeChoice);


        final HouseholdFactory hhFactory = new HouseholdFactoryImpl();
        HouseholdDataManager householdDataManager = new HouseholdDataManagerImpl(
                householdData, dwellingData, new PersonFactoryImpl(),
                hhFactory, properties, realEstateDataManager);


        return new DefaultDataContainer(geoData, realEstateDataManager, jobDataManager, householdDataManager, travelTimes, accessibility,
                commutingTimeProbability, properties);
    }

    static public void read(Properties properties, DefaultDataContainer dataContainer){

        GeoDataReader reader = new GeoDataReaderManchester(dataContainer.getGeoData());
        String pathShp = properties.main.baseDirectory + properties.geo.zoneShapeFile;
        String fileName = properties.main.baseDirectory + properties.geo.zonalDataFile;
        reader.readZoneCsv(fileName);
        reader.readZoneShapefile(pathShp);

        int year = properties.main.startYear;
        String householdFile = properties.main.baseDirectory + properties.householdData.householdFileName;
        householdFile += "_" + year + ".csv";
        HouseholdReader hhReader = new DefaultHouseholdReader(dataContainer.getHouseholdDataManager(), dataContainer.getHouseholdDataManager().getHouseholdFactory());
        hhReader.readData(householdFile);

        String personFile = properties.main.baseDirectory + properties.householdData.personFileName;
        personFile += "_" + year + ".csv";
        PersonReader personReader = new DefaultPersonReader(dataContainer.getHouseholdDataManager());
        personReader.readData(personFile);

        DwellingReader ddReader = new DefaultDwellingReader(dataContainer.getRealEstateDataManager());
        String dwellingsFile = properties.main.baseDirectory + properties.realEstate.dwellingsFileName + "_" + year + ".csv";
        ddReader.readData(dwellingsFile);

        new JobType(properties.jobData.jobTypes);
        JobReader jjReader = new DefaultJobReader(dataContainer.getJobDataManager());
        String jobsFile = properties.main.baseDirectory + properties.jobData.jobsFileName + "_" + year + ".csv";
        jjReader.readData(jobsFile);

        MicroDataScaler microDataScaler = new MicroDataScaler(dataContainer, properties);
        microDataScaler.scale();
    }
}