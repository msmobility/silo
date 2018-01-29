package de.tum.bgu.msm.container;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.JobDataManager;
import de.tum.bgu.msm.data.RealEstateDataManager;
import de.tum.bgu.msm.data.maryland.GeoDataMstm;
import de.tum.bgu.msm.data.munich.GeoDataMuc;
import de.tum.bgu.msm.events.IssueCounter;
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
    private static Logger logger = Logger.getLogger(SiloDataContainer.class);

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
     * @return A SiloDataContainer, with each data object created within
     */
    public static SiloDataContainer createSiloDataContainer() {
        GeoData geoData;
        switch (Properties.get().main.implementation) {
            case MARYLAND:
                geoData = new GeoDataMstm();
                break;
            case MUNICH:
                geoData = new GeoDataMuc();
                break;
            default:
                logger.error("Invalid implementation. Choose <MSTM> or <Muc>.");
                throw new RuntimeException("Invalid implementation. Choose <MSTM> or <Muc>.");
        }

        geoData.setInitialData();
        IssueCounter.regionSpecificCounters(geoData);

        // read micro data
        RealEstateDataManager realEstateData = new RealEstateDataManager(geoData);
        HouseholdDataManager householdData = new HouseholdDataManager(realEstateData);
        JobDataManager jobData = new JobDataManager(geoData);
        if (!Properties.get().main.runSynPop) {   // read data only if synth. pop. generator did not run
            int smallSize = 0;
            boolean readSmallSynPop = Properties.get().main.readSmallSynpop;
            if (readSmallSynPop) {
                smallSize = Properties.get().main.smallSynPopSize;
            }
            householdData.readPopulation(readSmallSynPop, smallSize);
            realEstateData.readDwellings(readSmallSynPop, smallSize);
            jobData.readJobs( readSmallSynPop, smallSize);
            householdData.setTypeOfAllHouseholds();
        }

        jobData.calculateEmploymentForecast();
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
