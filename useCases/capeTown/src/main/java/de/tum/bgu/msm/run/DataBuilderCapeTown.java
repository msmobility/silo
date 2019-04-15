package de.tum.bgu.msm.run;

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
import de.tum.bgu.msm.data.person.PersonFactoryCapeTown;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.GeoDataReaderCapeTown;
import de.tum.bgu.msm.io.PersonReaderCapeTown;
import de.tum.bgu.msm.io.input.*;
import de.tum.bgu.msm.models.transportModel.matsim.MatsimTravelTimes;
import de.tum.bgu.msm.properties.Properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.tum.bgu.msm.properties.modules.TransportModelPropertiesModule.TransportModelIdentifier.MATSIM;

public class DataBuilderCapeTown {

    private DataBuilderCapeTown(){}

    public static DataContainer getModelDataForCapeTown(Properties properties) {

        HouseholdData householdData = new HouseholdDataImpl();
        JobData jobData = new JobDataImpl();
        DwellingData dwellingData = new DwellingDataImpl();

        GeoData geoData = new DefaultGeoData();

        TravelTimes travelTimes = null;
        Accessibility accessibility = null;

        switch (properties.transportModel.travelTimeImplIdentifier) {
            case SKIM:
                travelTimes = new SkimTravelTimes();
                accessibility = new AccessibilityImpl(geoData, travelTimes, properties, dwellingData, householdData);
                break;
            case MATSIM:
                travelTimes = new MatsimTravelTimes();
//                accessibility = new MatsimAccessibility(geoData);
                accessibility = new AccessibilityImpl(geoData, travelTimes, properties, dwellingData, householdData);
                break;
            default:
                break;
        }
        CommutingTimeProbability commutingTimeProbability = new CommutingTimeProbability(properties);

        //TODO: revise this!
        new JobType(properties.jobData.jobTypes);

        JobFactory jobFactory = new JobFactoryImpl();

        List<DwellingType> dwellingTypeList = new ArrayList<>();
        Collections.addAll(dwellingTypeList, DefaultDwellingTypeImpl.values());

        RealEstateDataManager realEstateDataManager = new RealEstateDataManagerImpl(
                dwellingTypeList, dwellingData, householdData, geoData, new DwellingFactoryImpl(), properties);

        JobDataManager jobDataManager = new JobDataManagerImpl(
                properties, jobFactory, jobData, geoData, travelTimes, commutingTimeProbability);

        HouseholdFactory hhFactory = new HouseholdFactoryCapeTown();
        PersonFactory ppFactory = new PersonFactoryCapeTown();
        HouseholdDataManager householdDataManager = new HouseholdDataManagerImpl(
                householdData, dwellingData, ppFactory,
                hhFactory, properties, realEstateDataManager);

        //for the time being not required
//        SchoolData schoolData = new SchoolDataImpl(geoData, dwellingData, properties);

        return new DefaultDataContainer(geoData, realEstateDataManager, jobDataManager,
                householdDataManager, travelTimes, accessibility, commutingTimeProbability, properties);
    }

    static public void read(Properties properties, DataContainer dataContainer){

        GeoDataReader reader = new GeoDataReaderCapeTown(dataContainer.getGeoData());
        String fileName = properties.main.baseDirectory + properties.geo.zonalDataFile;
        String pathShp = properties.main.baseDirectory + properties.geo.zoneShapeFile;
        reader.readZoneCsv(fileName);
        reader.readZoneShapefile(pathShp);

        int year = properties.main.startYear;
        String householdFile = properties.main.baseDirectory + properties.householdData.householdFileName;
        householdFile += "_" + year + ".csv";
        HouseholdReader hhReader = new DefaultHouseholdReader(dataContainer.getHouseholdDataManager(),
                dataContainer.getHouseholdDataManager().getHouseholdFactory());
        hhReader.readData(householdFile);

        String personFile = properties.main.baseDirectory + properties.householdData.personFileName;
        personFile += "_" + year + ".csv";
        PersonReader personReader = new PersonReaderCapeTown(dataContainer.getHouseholdDataManager(), new PersonFactoryCapeTown());
        personReader.readData(personFile);

        DwellingReader ddReader = new DefaultDwellingReader(dataContainer.getRealEstateDataManager());
        String dwellingsFile = properties.main.baseDirectory + properties.realEstate.dwellingsFileName + "_" + year + ".csv";
        ddReader.readData(dwellingsFile);

        new JobType(properties.jobData.jobTypes);
        JobReader jjReader = new DefaultJobReader(dataContainer.getJobDataManager());
        String jobsFile = properties.main.baseDirectory + properties.jobData.jobsFileName + "_" + year + ".csv";
        jjReader.readData(jobsFile);

        //might be added later
//        SchoolReader ssReader = new SchoolReaderMuc(dataContainer.getSchoolData());
//        String schoolsFile = properties.main.baseDirectory + properties.schoolData.schoolsFileName + "_" + year + ".csv";
//        ssReader.readData(schoolsFile);
    }
}
