package de.tum.bgu.msm;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.DataContainerMuc;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.AccessibilityImpl;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.job.*;
import de.tum.bgu.msm.data.person.PersonFactoryMuc;
import de.tum.bgu.msm.data.school.SchoolData;
import de.tum.bgu.msm.data.school.SchoolDataImpl;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.*;
import de.tum.bgu.msm.io.input.*;
import de.tum.bgu.msm.models.transportModel.matsim.MatsimTravelTimes;
import de.tum.bgu.msm.properties.Properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.tum.bgu.msm.properties.modules.TransportModelPropertiesModule.TransportModelIdentifier.MATSIM;

public class DataBuilder {

    private DataBuilder() {
    }

    public static DataContainerMuc getModelDataForMuc(Properties properties) {

        HouseholdData householdData = new HouseholdDataImpl();
        JobData jobData = new JobDataImpl();
        DwellingData dwellingData = new DwellingDataImpl();

        GeoData geoData = new DefaultGeoData(properties);


        TravelTimes travelTimes;
        if (properties.transportModel.transportModelIdentifier == MATSIM) {
            travelTimes = new MatsimTravelTimes();
        } else {
            travelTimes = new SkimTravelTimes();
        }

        Accessibility accessibility = new AccessibilityImpl(geoData, travelTimes, properties, dwellingData, householdData);

        //TODO: revise this!
        new JobType(properties.jobData.jobTypes);


        JobFactoryMuc jobFactory = new JobFactoryMuc();
        jobFactory.readWorkingTimeDistributions(properties);

        List<DwellingType> dwellingTypeList = new ArrayList<>();
        Collections.addAll(dwellingTypeList, DefaultDwellingTypeImpl.values());

        RealEstateDataManager realEstateDataManager = new RealEstateDataManagerImpl(
                dwellingTypeList, dwellingData, householdData, geoData, new DwellingFactoryImpl(), properties);

        JobDataManager jobDataManager = new JobDataManagerImpl(
                properties, jobFactory, jobData, geoData, travelTimes, accessibility);

        final HouseholdFactoryMuc hhFactory = new HouseholdFactoryMuc();
        HouseholdDataManager householdDataManager = new HouseholdDataManagerImpl(
                householdData, dwellingData, geoData, new PersonFactoryMuc(),
                hhFactory, properties, realEstateDataManager);

        SchoolData schoolData = new SchoolDataImpl(geoData, dwellingData, properties);

        return new DataContainerMuc(geoData, realEstateDataManager, jobDataManager, householdDataManager, travelTimes, accessibility,
                schoolData, properties);
    }

    static public void read(Properties properties, DataContainerMuc dataContainer){

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
        PersonReader personReader = new PersonReaderMuc(dataContainer.getHouseholdDataManager());
        personReader.readData(personFile);

        DwellingReader ddReader = new DwellingReaderMuc(dataContainer.getRealEstateDataManager());
        String dwellingsFile = properties.main.baseDirectory + properties.realEstate.dwellingsFileName + "_" + year + ".csv";
        ddReader.readData(dwellingsFile);

        new JobType(properties.jobData.jobTypes);
        JobReader jjReader = new JobReaderMuc(dataContainer.getJobDataManager(), (JobFactoryMuc) dataContainer.getJobDataManager().getFactory());
        String jobsFile = properties.main.baseDirectory + properties.jobData.jobsFileName + "_" + year + ".csv";
        jjReader.readData(jobsFile);

        SchoolReader ssReader = new SchoolReaderMuc(dataContainer.getSchoolData());
        String schoolsFile = properties.main.baseDirectory + properties.schoolData.schoolsFileName + "_" + year + ".csv";
        ssReader.readData(schoolsFile);
    }
}