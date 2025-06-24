package de.tum.bgu.msm.health;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.MelbourneDwellingTypes;
import de.tum.bgu.msm.data.Zone;
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
import de.tum.bgu.msm.health.io.DefaultSpeedReader;
import de.tum.bgu.msm.health.io.DoseResponseLookupReader;
import de.tum.bgu.msm.io.*;
import de.tum.bgu.msm.io.input.*;
import de.tum.bgu.msm.matsim.MatsimTravelTimesAndCosts;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.*;
import de.tum.bgu.msm.util.RobustCSVReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.network.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static de.tum.bgu.msm.util.CsvWriter.writeTableDataSetToCSV;

public class DataBuilderHealth {

    private DataBuilderHealth() {
    }

    static Logger logger = LogManager.getLogger(DataBuilderHealth.class);

    public static HealthDataContainerImpl getModelDataForMelbourne(Properties properties, Config config) {

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

        new JobType(properties.jobData.jobTypes);

        RealEstateDataManager realEstateDataManager = new RealEstateDataManagerImpl(
                new MelbourneDwellingTypes(), dwellingData, householdData, geoData, new DwellingFactoryMEL(new DwellingFactoryImpl()), properties);

        JobDataManager jobDataManager = new JobDataManagerImpl(
                properties, new JobFactoryMEL(), jobData, geoData, travelTimes, commutingTimeProbability);

        final HouseholdFactoryImpl hhFactory = new HouseholdFactoryImpl();
        HouseholdDataManager householdDataManager = new HouseholdDataManagerImpl(
                householdData, dwellingData, new PersonFactoryMELHealth(),
                hhFactory, properties, realEstateDataManager);

        SchoolData schoolData = new SchoolDataImpl(geoData, dwellingData, properties);

        DataContainerWithSchools delegate = new DataContainerWithSchoolsImpl(geoData, realEstateDataManager, jobDataManager, householdDataManager, travelTimes, accessibility,
                commutingTimeProbability, schoolData, properties);
        return new HealthDataContainerImpl(delegate, properties);
    }

    static public void read(Properties properties, HealthDataContainerImpl dataContainer, Config config) throws IOException {

        int year = properties.main.startYear;

        GeoDataReader reader = new GeoDataReaderMelbourne(dataContainer);
        String pathShp = properties.main.baseDirectory + properties.geo.zoneShapeFile;
        String fileName = properties.main.baseDirectory + properties.geo.zonalDataFile;
        reader.readZoneCsv(fileName);
        reader.readZoneShapefile(pathShp);
        validateDevelopmentData(properties.main.baseDirectory + properties.geo.landUseAndDevelopmentFile,dataContainer.getGeoData());
        String householdFile = properties.main.baseDirectory + properties.householdData.householdFileName;
        householdFile += "_" + year + ".csv";
        HouseholdReader hhReader = new HouseholdReaderMEL(dataContainer.getHouseholdDataManager(), dataContainer.getHouseholdDataManager().getHouseholdFactory());
        hhReader.readData(householdFile);

        String personFile = properties.main.baseDirectory + properties.householdData.personFileName;
        personFile += "_" + year + ".csv";
        PersonReader personReader = new PersonReaderMELHealth(dataContainer.getHouseholdDataManager());
        personReader.readData(personFile);

        DwellingReader ddReader = new DwellingReaderMEL(dataContainer.getRealEstateDataManager(), dataContainer);
        String dwellingsFile = properties.main.baseDirectory + properties.realEstate.dwellingsFileName + "_" + year + ".csv";
        ddReader.readData(dwellingsFile);

        new JobType(properties.jobData.jobTypes);
        JobReader jjReader = new JobReaderMEL(dataContainer.getJobDataManager(), dataContainer);
        String jobsFile = properties.main.baseDirectory + properties.jobData.jobsFileName + "_" + year + ".csv";
        jjReader.readData(jobsFile);

        SchoolReader ssReader = new SchoolReaderMEL(dataContainer);
        String schoolsFile = properties.main.baseDirectory + properties.schoolData.schoolsFileName + "_" + year + ".csv";
        ssReader.readData(schoolsFile);

        new PoiReader(dataContainer).readData(properties.main.baseDirectory + properties.geo.poiFileName);

        Network network = NetworkUtils.readNetwork(config.network().getInputFile());
        Map<Id<Link>, LinkInfo> linkInfoMap = new HashMap<>();
        for (Link link : network.getLinks().values()) {
            linkInfoMap.put(link.getId(), new LinkInfo(link.getId()));
        }
        dataContainer.setLinkInfo(linkInfoMap);

        new PtSkimsReaderMEL(dataContainer).read();

        dataContainer.setAvgSpeeds(new DefaultSpeedReader().readData(properties.main.baseDirectory + properties.healthData.avgSpeedFile));
        dataContainer.setHealthTransitionData(new HealthTransitionTableReaderMEL().readData(
                dataContainer,
                properties.main.baseDirectory + properties.healthData.healthTransitionData)
        );
        DoseResponseLookupReader doseResponseReader = new DoseResponseLookupReader();
        doseResponseReader.readData(properties.main.baseDirectory + properties.healthData.basePath);
        dataContainer.setDoseResponseData(doseResponseReader.getDoseResponseData());

        MicroDataScaler microDataScaler = new MicroDataScaler(dataContainer, properties);
        microDataScaler.scale();
    }


    private static void validateDevelopmentData(String devFilePath, GeoData geoData) {
        File devFile = new File(devFilePath);
        if (devFile.exists()) {
            // Check CSV file contains both header and at least one row of data
            RobustCSVReader csv = new RobustCSVReader(devFilePath);
            int rows = csv.getRowCount();
            if (rows < 2) {
                logger.error("Development data file {} has {} rows (is empty or does not contain a header).", devFilePath, rows);
                mockDevelopmentTableDataSet(devFilePath, geoData);
            } else {
                logger.info("Development data file {} exists and has {} rows.", devFilePath, rows);
            }
        } else {
            mockDevelopmentTableDataSet(devFilePath, geoData);
        }
    }


    private static void mockDevelopmentTableDataSet(String devFilePath, GeoData geoData) {
        Map<Integer, Zone> zones = geoData.getZones();
        // Prepare data arrays for each column
        int n = zones.size();
        int[] zoneIds = new int[n];
        int[] regionIds = new int[n];
        int[] sfd = new int[n];
        int[] sfa = new int[n];
        int[] flat = new int[n];
        int[] mh = new int[n];
        int[] devCapacity = new int[n];
        int[] devLandUse = new int[n];

        int i = 0;
        for (Zone zone : zones.values()) {
            zoneIds[i] = zone.getId();
            regionIds[i] = zone.getRegion().getId();
            sfd[i] = 1;
            sfa[i] = 1;
            flat[i] = 1;
            mh[i] = 1;
            devCapacity[i] = 0;
            devLandUse[i] = 0;
            i++;
        }

        TableDataSet developmentTable = new TableDataSet();
        developmentTable.appendColumn(zoneIds, "Zone");
        developmentTable.appendColumn(regionIds, "Region");
        developmentTable.appendColumn(sfd, "SFD");
        developmentTable.appendColumn(sfa, "SFA");
        developmentTable.appendColumn(flat, "FLAT");
        developmentTable.appendColumn(mh, "MH");
        developmentTable.appendColumn(devCapacity, "DevCapacity");
        developmentTable.appendColumn(devLandUse, "DevLandUse");

        try {
            writeTableDataSetToCSV(developmentTable, devFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("Mock development table created with {} zones at {}", n, devFilePath);
    }

}


