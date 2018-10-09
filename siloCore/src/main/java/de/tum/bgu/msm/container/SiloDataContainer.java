package de.tum.bgu.msm.container;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.JobDataManager;
import de.tum.bgu.msm.data.RealEstateDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.JobFactoryImpl;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.data.job.JobUtils;
import de.tum.bgu.msm.data.maryland.GeoDataMstm;
import de.tum.bgu.msm.data.munich.GeoDataMuc;
import de.tum.bgu.msm.data.person.PersonUtils;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.io.*;
import de.tum.bgu.msm.models.transportModel.matsim.MatsimTravelTimes;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

/**
 * @author moeckel
 * The Silo Data Container holds all the various Data classes used by the SILO events.
 * Once the SiloDataContainer is created using the resourceBundle, each module can be retrieved
 * using the repsective getter.  \n
 * All the data items are constructed within the SiloModelContainer
 */
public class SiloDataContainer {
    private final static Logger LOGGER = Logger.getLogger(SiloDataContainer.class);

    private final HouseholdDataManager householdData;
    private final RealEstateDataManager realEstateData;
    private final JobDataManager jobData;
    private final GeoData geoData;
    private final TravelTimes travelTimes;

    /**
     * The contructor is private, with a factory method {link {@link SiloDataContainer#createSiloDataContainer(Implementation)}}
     * being used to encapsulate the object creation.
     */
    private SiloDataContainer(Implementation implementation) {

        switch (implementation) {
            case MARYLAND:
                geoData = new GeoDataMstm();
                break;
            case MUNICH:
                geoData = new GeoDataMuc();
                break;
            default:
                LOGGER.error("Invalid implementation. Choose <MSTM> or <Muc>.");
                throw new RuntimeException("Invalid implementation. Choose <MSTM> or <Muc>.");
        }

        realEstateData = new RealEstateDataManager(this);
        jobData = new JobDataManager(this);
        householdData = new HouseholdDataManager(this, PersonUtils.getFactory(), HouseholdUtil.getFactory());
        travelTimes = new SkimTravelTimes();
    }

    /**
     * The contructor is private, with a factory method {link {@link SiloDataContainer#createSiloDataContainer(Implementation)}}
     * being used to encapsulate the object creation.
     */
    private SiloDataContainer(Implementation implementation, Properties properties) {

        switch (implementation) {
            case MARYLAND:
                geoData = new GeoDataMstm();
                break;
            case MUNICH:
                geoData = new GeoDataMuc();
                break;
            default:
                LOGGER.error("Invalid implementation. Choose <MSTM> or <Muc>.");
                throw new RuntimeException("Invalid implementation. Choose <MSTM> or <Muc>.");
        }

        realEstateData = new RealEstateDataManager(this);
        jobData = new JobDataManager(this);
        householdData = new HouseholdDataManager(this, PersonUtils.getFactory(), HouseholdUtil.getFactory());
        geoData.readData();

        int year = properties.main.startYear;
        String householdFile = properties.main.baseDirectory + properties.householdData.householdFileName;
        householdFile += "_" + year + ".csv";
        HouseholdReader hhReader = new DefaultHouseholdReader(householdData);
        hhReader.readData(householdFile);

        String personFile = properties.main.baseDirectory + properties.householdData.personFileName;
        personFile += "_" + year + ".csv";
        PersonReader personReader = new DefaultPersonReader(householdData);
        personReader.readData(personFile);

        householdData.setHighestHouseholdAndPersonId();

        DwellingReader ddReader = new DefaultDwellingReader(realEstateData);
        String dwellingsFile = properties.main.baseDirectory + properties.realEstate.dwellingsFileName + "_" + year + ".csv";
        ddReader.readData(dwellingsFile);
        realEstateData.readAcresNeededByDwellingType();

        new JobType(properties.jobData.jobTypes);

        if (Properties.get().main.implementation.equals(Implementation.MUNICH)) {
            ((JobFactoryImpl) JobUtils.getFactory()).readWorkingTimeDistributions(properties);
        }
        JobReader jjReader = new DefaultJobReader(jobData);
        String jobsFile = properties.main.baseDirectory + properties.jobData.jobsFileName + "_" + year + ".csv";
        jjReader.readData(jobsFile);

        jobData.setHighestJobId();

        if (properties.transportModel.runMatsim) {
            travelTimes = new MatsimTravelTimes();
        } else {
            travelTimes = new SkimTravelTimes();
        }
    }

    /**
     * This factory method is used to create a fully set up data container with
     * all input data read in defined in the properties.
     *
     * @return A SiloDataContainer, with each data object created within
     */
    public static SiloDataContainer loadSiloDataContainer(Properties properties) {
        LOGGER.info("  Creating Data Objects for SiloDataContainer");
        return new SiloDataContainer(properties.main.implementation, properties);
    }

    /**
     * This factory method is used to create an empty data container.
     * Barely tested, use with caution! Uses Skim Travel times
     *
     * @return A SiloDataContainer, with each data object created within
     */
    public static SiloDataContainer createEmptySiloDataContainer(Implementation implementation) {
        LOGGER.info("  Creating Data Objects for SiloDataContainer");
        return new SiloDataContainer(implementation);
    }

    public HouseholdDataManager getHouseholdData() {
        return householdData;
    }

    public RealEstateDataManager getRealEstateData() {
        return realEstateData;
    }

    public JobDataManager getJobData() {
        return jobData;
    }

    public GeoData getGeoData() {
        return geoData;
    }

    public TravelTimes getTravelTimes() {
        return travelTimes;
    }
}
