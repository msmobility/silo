package de.tum.bgu.msm.container;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.JobDataManager;
import de.tum.bgu.msm.data.RealEstateDataManager;
import de.tum.bgu.msm.data.maryland.GeoDataMstm;
import de.tum.bgu.msm.data.munich.GeoDataMuc;
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

    /**
     *
     * The contructor is private, with a factory method {link {@link SiloDataContainer#createSiloDataContainer(Implementation)}}
     * being used to encapsulate the object creation.
     *
     *
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
        householdData = new HouseholdDataManager(this);
    }

    public void loadData(Properties properties) {
        geoData.readData();
        householdData.readPopulation(properties);
        realEstateData.readDwellings(properties);
        jobData.readJobs(properties);
    }

    /**
     * This factory method is used to create a fully set up data container with
     * all input data read in defined in the properties.
     * @return A SiloDataContainer, with each data object created within
     */
    public static SiloDataContainer loadSiloDataContainer(Properties properties) {
        LOGGER.info("  Creating Data Objects for SiloDataContainer");
        SiloDataContainer dataContainer = new SiloDataContainer(properties.main.implementation);
        dataContainer.loadData(properties);
        return dataContainer;
    }

    /**
     * This factory method is used to create an empty data container.
     * Barely tested, use with caution!
     * @return A SiloDataContainer, with each data object created within
     */
    public static SiloDataContainer createEmptySiloDataContainer(Implementation implementation) {
        LOGGER.info("  Creating Data Objects for SiloDataContainer");
        SiloDataContainer dataContainer = new SiloDataContainer(implementation);
        return dataContainer;
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
}
