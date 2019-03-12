package de.tum.bgu.msm.container;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.accessibility.Accessibility;
import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.JobFactoryImpl;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.data.job.JobUtils;
import de.tum.bgu.msm.data.maryland.GeoDataMstm;
import de.tum.bgu.msm.data.person.PersonUtils;
import de.tum.bgu.msm.data.perth.GeoDataPerth;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.*;
import de.tum.bgu.msm.models.transportModel.matsim.MatsimTravelTimes;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.tum.bgu.msm.properties.modules.TransportModelPropertiesModule.TransportModelIdentifier.*;

/**
 * @author moeckel
 * The Silo Data Container holds all the various Data classes used by the SILO events.
 * Once the SiloDataContainer is created using the resourceBundle, each module can be retrieved
 * using the respective getter.  \n
 * All the data items are constructed within the SiloModelContainer
 */
public class SiloDataContainerImpl implements SiloDataContainer {
    private final static Logger LOGGER = Logger.getLogger(SiloDataContainerImpl.class);

    private final HouseholdDataManager householdData;
    private final RealEstateDataManager realEstateData;
    private final JobDataManager jobData;
    private final GeoData geoData;
    private final TravelTimes travelTimes;
    private final Accessibility accessibility;
    private final SchoolDataManager schoolData;
    private Properties properties;

    /**
     * The contructor is private, with a factory method {@link SiloDataContainerImpl#loadSiloDataContainer(Properties)}
     * being used to encapsulate the object creation.
     */
    private SiloDataContainerImpl(Implementation implementation) {

        //todo modify when different dwelling types are available
        List<DwellingType> dwellingTypeList = new ArrayList<>();
        Collections.addAll(dwellingTypeList, DefaultDwellingTypeImpl.values());

        switch (implementation) {
            case MARYLAND:
                geoData = new GeoDataMstm();
                schoolData = null;
                break;
            case MUNICH:
                schoolData = new SchoolDataManager(this);
                geoData = new DefaultGeoData();
                break;
            case PERTH:
                geoData = new GeoDataPerth();
                schoolData = null;
                break;
            case AUSTIN:
                // to do: this might need to be replaced by GeoDataAustin
                geoData = new GeoDataMstm();
                schoolData = null;
                break;
            default:
                LOGGER.error(implementation + " is an invalid implementation. Choose <MSTM> or <Muc>.");
                throw new RuntimeException("Invalid implementation. Choose <MSTM> or <Muc>.");
        }

        realEstateData = new RealEstateDataManager(this, dwellingTypeList);
        jobData = new JobDataManager(this);
        householdData = new HouseholdDataManager(this, PersonUtils.getFactory(), HouseholdUtil.getFactory());
        travelTimes = new SkimTravelTimes();
        accessibility = new Accessibility(this, properties);

    }

    /**
     * The contructor is private, with a factory method {@link SiloDataContainerImpl#loadSiloDataContainer(Properties)}
     * being used to encapsulate the object creation.
     */
    private SiloDataContainerImpl(Implementation implementation, Properties properties) {
        
        this.properties = properties;

        //todo modify when different dwelling types are available
        List<DwellingType> dwellingTypeList = new ArrayList<>();
        Collections.addAll(dwellingTypeList, DefaultDwellingTypeImpl.values());

        switch (implementation) {
            case MARYLAND:
                geoData = new GeoDataMstm();
                schoolData = null;
                break;
            case MUNICH:
                geoData = new DefaultGeoData();
                schoolData = new SchoolDataManager(this);
                break;
            case KAGAWA:
                geoData = new DefaultGeoData();
                schoolData = new SchoolDataManager(this);
                break;
            case AUSTIN:
                // to do: this might need to be replaced by GeoDataAustin
                geoData = new GeoDataMstm();
                schoolData = null;
                break;
            case PERTH:
                geoData = new GeoDataPerth();
                schoolData = null;
                break;
            default:
                LOGGER.error("Invalid implementation. Choose <KAGAWA>, <MARYLAND>, <MUNICH> or <PERTH>.");
                throw new RuntimeException("Invalid implementation. Choose <KAGAWA>, <MARYLAND>, <MUNICH> or <PERTH>.");
        }

        //todo read the geodata around here
        String pathShp = Properties.get().geo.zoneShapeFile;
        String fileName = Properties.get().main.baseDirectory + Properties.get().geo.zonalDataFile;

        realEstateData = new RealEstateDataManager(this, dwellingTypeList);
        jobData = new JobDataManager(this);
        householdData = new HouseholdDataManager(this, PersonUtils.getFactory(), HouseholdUtil.getFactory());
        accessibility = new Accessibility(this, properties);

        int year = properties.main.startYear;
        String householdFile = properties.main.baseDirectory + properties.householdData.householdFileName;
        householdFile += "_" + year + ".csv";
        HouseholdReader hhReader = new DefaultHouseholdReader(householdData);
        hhReader.readData(householdFile);

        String personFile = properties.main.baseDirectory + properties.householdData.personFileName;
        personFile += "_" + year + ".csv";
        PersonReader personReader = new DefaultPersonReader(householdData);
        personReader.readData(personFile);

        householdData.identifyHighestHouseholdAndPersonId();

        DwellingReader ddReader = new DefaultDwellingReader(realEstateData);
        String dwellingsFile = properties.main.baseDirectory + properties.realEstate.dwellingsFileName + "_" + year + ".csv";
        ddReader.readData(dwellingsFile);
        realEstateData.calculateRegionWidePriceAndVacancyByDwellingType();



        new JobType(properties.jobData.jobTypes);

        if (properties.main.implementation.equals(Implementation.MUNICH)) {
            ((JobFactoryImpl) JobUtils.getFactory()).readWorkingTimeDistributions(properties);
            schoolData.setSchoolSearchTree(properties);
            SchoolReader ssReader = new DefaultSchoolReader(schoolData);
            String schoolsFile = properties.main.baseDirectory + properties.schoolData.schoolsFileName + "_" + year + ".csv";
            ssReader.readData(schoolsFile);
        }
        JobReader jjReader = new DefaultJobReader(jobData);
        String jobsFile = properties.main.baseDirectory + properties.jobData.jobsFileName + "_" + year + ".csv";
        jjReader.readData(jobsFile);

        jobData.setHighestJobId();


        if (properties.transportModel.transportModelIdentifier == MATSIM) {
            travelTimes = new MatsimTravelTimes();
        } else {
            travelTimes = new SkimTravelTimes();
        }

    }

    @Override
    public HouseholdDataManager getHouseholdData() {
        return householdData;
    }

    @Override
    public RealEstateDataManager getRealEstateData() {
        return realEstateData;
    }

    @Override
    public JobDataManager getJobData() {
        return jobData;
    }

    @Override
    public GeoData getGeoData() {
        return geoData;
    }

    @Override
    public TravelTimes getTravelTimes() {
        return travelTimes;
    }

    @Override
    public Accessibility getAccessibility() {
        return null;
    }

    public SchoolDataManager getSchoolData() {
        return schoolData;
    }

    @Override
    public void setup() {
        geoData.setup();

        realEstateData.readDevelopmentData();
        householdData.calculateInitialSettings();
        jobData.calculateEmploymentForecast();
        jobData.identifyVacantJobs();
        jobData.calculateJobDensityByZone();
        realEstateData.fillQualityDistribution();
        realEstateData.setHighestVariablesAndCalculateRentShareByIncome();
        realEstateData.identifyVacantDwellings();

    }

    @Override
    public void prepareYear(int year) {
        if (year != properties.main.implementation.BASE_YEAR) {
            jobData.identifyVacantJobs();
            householdData.adjustIncome();
        }
    }

    @Override
    public void finishYear(int year) {

    }

    /**
     * This factory method is used to create a fully set up data container with
     * all input data read in defined in the properties.
     *
     * @return A SiloDataContainer, with each data object created within
     */
    public static SiloDataContainerImpl loadSiloDataContainer(Properties properties) {
        LOGGER.info("  Creating Data Objects for SiloDataContainer");
        return new SiloDataContainerImpl(properties.main.implementation, properties);
    }

    /**
     * This factory method is used to create an empty data container.
     * Barely tested, use with caution! Uses Skim Travel times
     *
     * @return A SiloDataContainer, with each data object created within
     */
    public static SiloDataContainerImpl createEmptySiloDataContainer(Implementation implementation) {
        LOGGER.info("  Creating Data Objects for SiloDataContainer");
        return new SiloDataContainerImpl(implementation);
    }


}
