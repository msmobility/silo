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
    private SiloDataContainer() {

        switch (Properties.get().main.implementation) {
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

    private void setupDataContainer() {
        geoData.setInitialData();
        IssueCounter.regionSpecificCounters(getGeoData());

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
    }

    /**
     * This factory method is used to create all the data objects needed for SILO
     * Each data object is created sequentially
     * @return A SiloDataContainer, with each data object created within
     */
    public static SiloDataContainer createSiloDataContainer() {
        LOGGER.info("  Creating Data Objects for SiloDataContainer");
        SiloDataContainer dataContainer = new SiloDataContainer();
        dataContainer.setupDataContainer();
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
