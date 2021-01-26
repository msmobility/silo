package sdg;

import de.tum.bgu.msm.container.DefaultDataContainer;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.job.*;
import de.tum.bgu.msm.data.person.PersonFactoryImpl;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.*;
import de.tum.bgu.msm.io.input.*;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.SchoolData;
import de.tum.bgu.msm.schools.SchoolDataImpl;
import de.tum.bgu.msm.schools.SchoolReader;
import de.tum.bgu.msm.schools.SchoolReaderImpl;
import org.matsim.core.config.Config;
import sdg.data.DataContainerSdg;

public class DataBuilderSdg extends DefaultDataContainer {

    public DataBuilderSdg(GeoData geoData, RealEstateDataManager realEstateDataManager, JobDataManager jobDataManager, HouseholdDataManager householdDataManager, TravelTimes travelTimes, Accessibility accessibility, CommutingTimeProbability commutingTimeProbability, Properties properties) {
        super(geoData, realEstateDataManager, jobDataManager, householdDataManager, travelTimes, accessibility, commutingTimeProbability, properties);
    }

    public static DataContainerSdg getModelData(Properties properties, Config config) {

        HouseholdData householdData = new HouseholdDataImpl();
        DwellingData dwellingData = new DwellingDataImpl();
        GeoData geoData = new DefaultGeoData();
        RealEstateDataManager realEstateDataManager = new RealEstateDataManagerImpl(new DefaultDwellingTypes(), dwellingData, householdData, geoData, new DwellingFactoryImpl(), properties);
        SchoolData schoolData = new SchoolDataImpl(geoData, dwellingData, properties);
        JobFactory jobFactory = new JobFactoryImpl();
        JobData jobData = new JobDataImpl();
        JobDataManager jobDataManager = new JobDataManagerImpl(properties, jobFactory, jobData, geoData, null, null);
        final HouseholdFactory hhFactory = new HouseholdFactoryImpl();
        HouseholdDataManager householdDataManager = new HouseholdDataManagerImpl(
                householdData, dwellingData, new PersonFactoryImpl(),
                hhFactory, properties, realEstateDataManager);

        return new DataContainerSdg(geoData, realEstateDataManager, householdDataManager, schoolData, properties,jobDataManager);

    }

    static public void read(Properties properties, DataContainerSdg dataContainer, int year){

        GeoDataReader reader = new GeoDataReaderMuc(dataContainer.getGeoData());
        String fileName = properties.main.baseDirectory + properties.geo.zonalDataFile;
        String pathShp = properties.main.baseDirectory + properties.geo.zoneShapeFile;
        reader.readZoneCsv(fileName);
        reader.readZoneShapefile(pathShp);

        String householdFile;
        String personFile;
        String dwellingsFile;
        String jobsFile;
        String schoolsFile;

        if(Properties.get().main.startYear == year){

            householdFile = properties.main.baseDirectory + properties.householdData.householdFileName + "_" + year + ".csv";

            personFile = properties.main.baseDirectory + properties.householdData.personFileName + "_" + year + ".csv";

            dwellingsFile = properties.main.baseDirectory + properties.realEstate.dwellingsFileName + "_" + year + ".csv";

            jobsFile = properties.main.baseDirectory + properties.jobData.jobsFileName + "_" + year + ".csv";

            schoolsFile = properties.main.baseDirectory + properties.schoolData.schoolsFileName + "_" + year + ".csv";

        }else {

            householdFile = properties.main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName + "/microData/hh_" + Properties.get().main.endYear + ".csv";
            personFile = properties.main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName + "/microData/pp_" + Properties.get().main.endYear + ".csv";
            dwellingsFile = properties.main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName + "/microData/dd_" + Properties.get().main.endYear + ".csv";
            jobsFile = properties.main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName + "/microData/jj_" + Properties.get().main.endYear + ".csv";
            schoolsFile = properties.main.baseDirectory + properties.schoolData.schoolsFinalFileName + "_" + year + ".csv";

        }

        HouseholdReader hhReader = new DefaultHouseholdReader(dataContainer.getHouseholdDataManager(), dataContainer.getHouseholdDataManager().getHouseholdFactory());
        hhReader.readData(householdFile);

        PersonReader personReader = new DefaultPersonReader(dataContainer.getHouseholdDataManager());
        personReader.readData(personFile);

        DwellingReader ddReader = new DwellingReaderTak(dataContainer.getRealEstateDataManager().getDwellingData());
        ddReader.readData(dwellingsFile);

        new JobType(properties.jobData.jobTypes);
        JobReader jjReader = new DefaultJobReader(dataContainer.getJobDataManager());
        jjReader.readData(jobsFile);

        SchoolReader eeReader = new SchoolReaderImpl(dataContainer.getSchoolData());
        eeReader.readData(schoolsFile);
    }
}