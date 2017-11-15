package de.tum.bgu.msm.container;

import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.events.IssueCounter;
import org.apache.log4j.Logger;

import java.util.ResourceBundle;

/**
 * @author moeckel
 * The Silo Data Container holds all the various Data classes used by the SILO events.
 * Once the SiloDataContainer is created using the resourceBundle, each module can be retrieved
 * using the repsective getter.  \n
 * All the data items are constructed within the SiloModelContainer
 */
public class SiloDataContainer {
    private static Logger logger = Logger.getLogger(SiloDataContainer.class);
    protected static final String PROPERTIES_SIZE_SMALL_SYNPOP              = "size.small.syn.pop";
    private final HouseholdDataManager householdData;
    private final RealEstateDataManager realEstateData;
    private final JobDataManager jobData;
    private final GeoData geoData;

    /**
     *
     * The contructor is private, with a factory method {link {@link SiloDataContainer#createSiloDataContainer(ResourceBundle, boolean, de.tum.bgu.msm.SiloModel.Implementation)}}
     * being used to encapsulate the object creation.
     *
     *
     * @param householdData
     * @param realEstateData
     * @param jobData
     * @param geoData
     */
    private SiloDataContainer(HouseholdDataManager householdData, RealEstateDataManager realEstateData,
                               JobDataManager jobData, GeoData geoData) {
        this.householdData = householdData;
        this.realEstateData = realEstateData;
        this.jobData = jobData;
        this.geoData = geoData;
    }

    /**
     * This factory method is used to create all the data objects needed for SILO from the Configuration file, loaded as a ResourceBundle
     * Each data object is created sequentially, before being passed as parameters to the private constructor.
     * @param rbLandUse The configuration file, as a @see {@link ResourceBundle}
     * @return A SiloDataContainer, with each data object created within
     */
    public static SiloDataContainer createSiloDataContainer(ResourceBundle rbLandUse,
                                                            boolean readSmallSynPop, SiloModel.Implementation implementation) {
        GeoData geoData;
        switch (implementation) {
            case MSTM:
                geoData = new GeoDataMstm(rbLandUse);
                break;
            case MUC:
                geoData = new GeoDataMuc(rbLandUse);
                break;
            default:
                logger.error("Invalid implementation. Choose <MSTM> or <Muc>.");
                throw new RuntimeException("Invalid implementation. Choose <MSTM> or <Muc>.");
        }

        geoData.setInitialData();
        IssueCounter.regionSpecificCounters(geoData);


        // read micro data
        RealEstateDataManager realEstateData = new RealEstateDataManager(rbLandUse, geoData);
        HouseholdDataManager householdData = new HouseholdDataManager(rbLandUse, realEstateData);
        JobDataManager jobData = new JobDataManager(rbLandUse, geoData);
        if (!ResourceUtil.getBooleanProperty(rbLandUse, "run.synth.pop.generator")) {   // read data only if synth. pop. generator did not run
            int smallSize = 0;
            if (readSmallSynPop) smallSize = ResourceUtil.getIntegerProperty(rbLandUse, PROPERTIES_SIZE_SMALL_SYNPOP);
            householdData.readPopulation(readSmallSynPop, smallSize);
            realEstateData.readDwellings(readSmallSynPop, smallSize);
            jobData.readJobs( readSmallSynPop, smallSize);
            householdData.connectPersonsToHouseholds();
            householdData.setTypeOfAllHouseholds();
        }

        jobData.updateEmploymentForecast();
        jobData.identifyVacantJobs();
        jobData.calculateJobDensityByZone();
        realEstateData.fillQualityDistribution();
        realEstateData.setHighestVariables();
        realEstateData.identifyVacantDwellings();
        householdData.setHighestHouseholdAndPersonId();
        householdData.calculateInitialSettings();

        logger.info("  Creating Data Objects for SiloDataContainer");

        return new SiloDataContainer(householdData, realEstateData, jobData, geoData);
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
