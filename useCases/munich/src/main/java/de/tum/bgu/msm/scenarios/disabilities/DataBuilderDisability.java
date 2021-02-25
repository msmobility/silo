package de.tum.bgu.msm.scenarios.disabilities;

import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.AccessibilityImpl;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbabilityImpl;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.job.*;
import de.tum.bgu.msm.data.person.PersonFactoryMucDisability;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.*;
import de.tum.bgu.msm.io.input.*;
import de.tum.bgu.msm.matsim.MatsimTravelTimesAndCosts;
import de.tum.bgu.msm.models.disability.DefaultDisabilityStrategy;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.*;
import org.matsim.core.config.Config;

public class DataBuilderDisability {

    private DataBuilderDisability() {
    }

    public static DataContainerWithSchoolsImpl getModelDataForMuc(Properties properties, Config config) {

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


        JobFactoryMuc jobFactory = new JobFactoryMuc();
        jobFactory.readWorkingTimeDistributions(properties);

        RealEstateDataManager realEstateDataManager = new RealEstateDataManagerImpl(
                new DefaultDwellingTypes(), dwellingData, householdData, geoData, new DwellingFactoryImpl(), properties);

        JobDataManager jobDataManager = new JobDataManagerImpl(
                properties, jobFactory, jobData, geoData, travelTimes, commutingTimeProbability);

        final HouseholdFactoryMuc hhFactory = new HouseholdFactoryMuc();
        HouseholdDataManager householdDataManager = new HouseholdDataManagerImpl(
                householdData, dwellingData, new PersonFactoryMucDisability(),
                hhFactory, properties, realEstateDataManager);

        SchoolData schoolData = new SchoolDataImpl(geoData, dwellingData, properties);

        return new DataContainerWithSchoolsImpl(geoData, realEstateDataManager, jobDataManager, householdDataManager, travelTimes, accessibility,
        		commutingTimeProbability, schoolData, properties);
    }

    static public void read(Properties properties, DataContainerWithSchoolsImpl dataContainer){

        GeoDataReader reader = new GeoDataReaderMuc(dataContainer.getGeoData());
        String pathShp = properties.main.baseDirectory + properties.geo.zoneShapeFile;
        String fileName = properties.main.baseDirectory + properties.geo.zonalDataFile;
        reader.readZoneCsv(fileName);
        reader.readZoneShapefile(pathShp);

        int year = properties.main.startYear;
        String householdFile = properties.main.baseDirectory + properties.householdData.householdFileName;
        householdFile += "_" + year + ".csv";
        HouseholdReader hhReader = new HouseholdReaderMuc(dataContainer.getHouseholdDataManager(), (HouseholdFactoryMuc) dataContainer.getHouseholdDataManager().getHouseholdFactory());
        hhReader.readData(householdFile);

        String personFile = properties.main.baseDirectory + properties.householdData.personFileName;
        personFile += "_" + year + ".csv";
        PersonReader personReader = new PersonReaderMucDisability(dataContainer.getHouseholdDataManager(), new DefaultDisabilityStrategy());
        personReader.readData(personFile);

        DwellingReader ddReader = new DwellingReaderMuc(dataContainer.getRealEstateDataManager());
        String dwellingsFile = properties.main.baseDirectory + properties.realEstate.dwellingsFileName + "_" + year + ".csv";
        ddReader.readData(dwellingsFile);

        new JobType(properties.jobData.jobTypes);
        JobReader jjReader = new JobReaderMuc(dataContainer.getJobDataManager(), (JobFactoryMuc) dataContainer.getJobDataManager().getFactory());
        String jobsFile = properties.main.baseDirectory + properties.jobData.jobsFileName + "_" + year + ".csv";
        jjReader.readData(jobsFile);

        SchoolReader ssReader = new SchoolReaderImpl(dataContainer.getSchoolData());
        String schoolsFile = properties.main.baseDirectory + properties.schoolData.schoolsFileName + "_" + year + ".csv";
        ssReader.readData(schoolsFile);
    }
}