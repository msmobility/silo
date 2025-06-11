package de.tum.bgu.msm.health;

import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.ManchesterDwellingTypes;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.AccessibilityImpl;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbabilityImpl;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.job.*;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.health.data.LinkInfo;
import de.tum.bgu.msm.health.diseaseModelOffline.HealthExposuresReader;
import de.tum.bgu.msm.health.io.*;
import de.tum.bgu.msm.io.*;
import de.tum.bgu.msm.io.input.*;
import de.tum.bgu.msm.matsim.MatsimTravelTimesAndCosts;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.*;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.HashMap;
import java.util.Map;

public class DataBuilderHealth {

    private DataBuilderHealth() {
    }

    public static HealthDataContainerImpl getModelDataForManchester(Properties properties, Config config) {

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
                break;
        }

        CommutingTimeProbability commutingTimeProbability = new CommutingTimeProbabilityImpl(properties);

        //TODO: revise this!
        new JobType(properties.jobData.jobTypes);

        //TODO: MCR
        //JobFactoryMuc jobFactory = new JobFactoryMuc();
        //jobFactory.readWorkingTimeDistributions(properties);

        RealEstateDataManager realEstateDataManager = new RealEstateDataManagerImpl(
                new ManchesterDwellingTypes(), dwellingData, householdData, geoData, new DwellingFactoryMCR(new DwellingFactoryImpl()), properties);

        JobDataManager jobDataManager = new JobDataManagerImpl(
                properties, new JobFactoryMCR(), jobData, geoData, travelTimes, commutingTimeProbability);

        final HouseholdFactoryImpl hhFactory = new HouseholdFactoryImpl();
        HouseholdDataManager householdDataManager = new HouseholdDataManagerImpl(
                householdData, dwellingData, new PersonFactoryMCRHealth(),
                hhFactory, properties, realEstateDataManager);

        SchoolData schoolData = new SchoolDataImpl(geoData, dwellingData, properties);

        DataContainerWithSchools delegate = new DataContainerWithSchoolsImpl(geoData, realEstateDataManager, jobDataManager, householdDataManager, travelTimes, accessibility,
                commutingTimeProbability, schoolData, properties);
        return new HealthDataContainerImpl(delegate, properties);
    }

    static public void read(Properties properties, HealthDataContainerImpl dataContainer, Config config){

        GeoDataReader reader = new GeoDataReaderManchester(dataContainer);
        String pathShp = properties.main.baseDirectory + properties.geo.zoneShapeFile;
        String fileName = properties.main.baseDirectory + properties.geo.zonalDataFile;
        reader.readZoneCsv(fileName);
        reader.readZoneShapefile(pathShp);

        int year = properties.main.startYear;
        String householdFile = properties.main.baseDirectory + properties.householdData.householdFileName;
        householdFile += "_" + year + ".csv";
        HouseholdReader hhReader = new DefaultHouseholdReader(dataContainer.getHouseholdDataManager(), (HouseholdFactoryImpl) dataContainer.getHouseholdDataManager().getHouseholdFactory());
        hhReader.readData(householdFile);

        String personFile = properties.main.baseDirectory + properties.householdData.personFileName;
        personFile += "_" + year + ".csv";
        PersonReader personReader = new PersonReaderMCRHealth(dataContainer.getHouseholdDataManager());
        personReader.readData(personFile);

        DwellingReader ddReader = new DwellingReaderMCR(dataContainer.getRealEstateDataManager(), dataContainer);
        String dwellingsFile = properties.main.baseDirectory + properties.realEstate.dwellingsFileName + "_" + year + ".csv";
        ddReader.readData(dwellingsFile);

        new JobType(properties.jobData.jobTypes);
        JobReader jjReader = new JobReaderMCR(dataContainer.getJobDataManager(), dataContainer);
        String jobsFile = properties.main.baseDirectory + properties.jobData.jobsFileName + "_" + year + ".csv";
        jjReader.readData(jobsFile);

        SchoolReader ssReader = new SchoolReaderMCR(dataContainer);
        String schoolsFile = properties.main.baseDirectory + properties.schoolData.schoolsFileName + "_" + year + ".csv";
        ssReader.readData(schoolsFile);

        new PoiReader(dataContainer).readData(properties.main.baseDirectory + properties.geo.poiFileName);

        Network network = NetworkUtils.readNetwork(config.network().getInputFile());
        Map<Id<Link>, LinkInfo> linkInfoMap = new HashMap<>();
        for(Link link : network.getLinks().values()){
            linkInfoMap.put(link.getId(), new LinkInfo(link.getId()));
        }
        dataContainer.setLinkInfo(linkInfoMap);

        // Initialize here for map per day
        dataContainer.setLinkInfoByDay(linkInfoMap, Day.thursday);
        dataContainer.setLinkInfoByDay(linkInfoMap, Day.saturday);
        dataContainer.setLinkInfoByDay(linkInfoMap, Day.sunday);

        new PtSkimsReaderMCR(dataContainer).read();

        dataContainer.setAvgSpeeds(new DefaultSpeedReader().readData(properties.main.baseDirectory + properties.healthData.avgSpeedFile));
        dataContainer.setHealthTransitionData(new HealthTransitionTableReader().readData(dataContainer,properties.main.baseDirectory + properties.healthData.healthTransitionData));
        DoseResponseLookupReader doseResponseReader = new DoseResponseLookupReader();
        doseResponseReader.readData(properties.main.baseDirectory + properties.healthData.basePath);
        dataContainer.setDoseResponseData(doseResponseReader.getDoseResponseData());
        dataContainer.setHealthPrevalenceData(new PrevalenceDataReader().readData(properties.main.baseDirectory + properties.healthData.prevalenceDataFile));
        dataContainer.setHealthInjuryRRdata(new InjuryRRTableReader().readData(properties.main.baseDirectory + properties.healthData.healthInjuryRRDataFile));

        MicroDataScaler microDataScaler = new MicroDataScaler(dataContainer, properties);
        microDataScaler.scale();
    }
}
