package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.accessibility.AccessibilityImpl;
import de.tum.bgu.msm.data.accessibility.CommutingTimeProbability;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.geo.DefaultGeoData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.job.*;
import de.tum.bgu.msm.data.person.PersonFactoryMuc;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.*;
import de.tum.bgu.msm.io.input.*;
import de.tum.bgu.msm.matsim.MatsimTravelTimes;
import de.tum.bgu.msm.matsim.noise.NoiseDataManager;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.*;
import org.matsim.core.config.Config;

public class DataBuilderNoise {

    private DataBuilderNoise() {
    }

    public static DataContainer getModelDataForMuc(Properties properties, Config config) {
        DataContainer delegate = DataBuilder.getModelDataForMuc(properties, config);


        NoiseDataManager noiseDataManager = new NoiseDataManager();
        return new NoiseDataContainer(delegate, noiseDataManager);
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
        PersonReader personReader = new PersonReaderMuc(dataContainer.getHouseholdDataManager());
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
