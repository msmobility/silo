package model;
/**
 * 
 */

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.events.MicroSimulation;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.CblcmDiffGenerator;
import de.tum.bgu.msm.utils.TimeTracker;
import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.maryland.GeoDataMstm;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.utils.SkimUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kainagel
 *
 */
public class SiloModelCBLCM {

    private final static Logger logger = Logger.getLogger(SiloModelCBLCM.class);

    private final Set<Integer> tdmYears = new HashSet<>();
	private final Set<Integer> skimYears = new HashSet<>();
	private final Set<Integer> scalingYears = new HashSet<>();

	private int currentYear;
	private boolean trackTime;
	private SiloModelContainer modelContainer;
	private SiloDataContainer data;
	public GeoData geoData;
	
	private final Properties properties;


	public SiloModelCBLCM(Properties properties) {
		this.properties = properties;
		IssueCounter.setUpCounter();   // set up counter for any issues during initial setup
		SiloUtil.modelStopper("initialize");
	}

	public void initialize() {
		// initial steps that only need to performed once to set up the model
	        // define years to simulate
	        scalingYears.addAll(properties.main.scalingYears);
	        if (!scalingYears.isEmpty()) SummarizeData.readScalingYearControlTotals();
	        currentYear = properties.main.startYear;
	        tdmYears.addAll(properties.transportModel.modelYears);
	        skimYears.addAll(properties.accessibility.skimYears);
	        // Note: only implemented for MSTM:
	        geoData = new GeoDataMstm();
	        // Note: only implemented for MSTM:

		// read micro data
		data = SiloDataContainer.loadSiloDataContainer(properties);
		modelContainer = SiloModelContainer.createSiloModelContainer(data, null, properties);

		SkimUtil.updateCarSkim((SkimTravelTimes) data.getTravelTimes(), currentYear, properties);
		SkimUtil.updateTransitSkim((SkimTravelTimes) data.getTravelTimes(), currentYear, properties);
		modelContainer.getAcc().initialize();

	        trackTime = properties.main.trackTime;
	        IssueCounter.logIssues(geoData);           // log any potential issues during initial setup


	        if (properties.main.createPrestoSummary) {
				SummarizeData.preparePrestoSummary(geoData);
			}
	        SiloUtil.initializeRandomNumber(properties.main.randomSeed);
	}
	
	public void runYear(double year) {
		// run single simulation period

	        if (year != 1) {
	            logger.error("SILO is not prepared to simulate other interval than 1 year. Invalid interval: " + year);
	            System.exit(1);
	        }
	        if (scalingYears.contains(currentYear))
	            SummarizeData.scaleMicroDataToExogenousForecast(currentYear, data);
	        logger.info("Simulating changes from year " + currentYear + " to year " + (currentYear + 1));
	        IssueCounter.setUpCounter();    // setup issue counter for this simulation period
	        SiloUtil.trackingFile("Simulating changes from year " + currentYear + " to year " + (currentYear + 1));
	        MicroSimulation em = new MicroSimulation(new TimeTracker());
            final HouseholdDataManager householdData = data.getHouseholdData();
			long startTime = 0;


	        if (currentYear != properties.main.implementation.BASE_YEAR) {
	            modelContainer.getUpdateJobs().updateJobInventoryMultiThreadedThisYear(currentYear);
	            data.getJobData().identifyVacantJobs();
	        }


	        if (skimYears.contains(currentYear)) {
	            if (currentYear != properties.main.startYear && !tdmYears.contains(currentYear)) {
	                // skims are always read in start year and in every year the transportation model ran. Additional
	                // years to read skims may be provided in skimYears
	                SkimUtil.updateCarSkim((SkimTravelTimes) data.getTravelTimes(),
                            currentYear, properties);
	                SkimUtil.updateTransitSkim((SkimTravelTimes) data.getTravelTimes(),
                            currentYear, properties);
	                modelContainer.getAcc().calculateHansenAccessibilities(currentYear);
	            }
	        }

	        modelContainer.getDdOverwrite().addDwellings(currentYear);

	        if (trackTime) startTime = System.currentTimeMillis();
	        modelContainer.getMove().calculateRegionalUtilities();
	        modelContainer.getMove().calculateAverageHousingSatisfaction();

	        if (trackTime) startTime = System.currentTimeMillis();
	        if (currentYear != properties.main.implementation.BASE_YEAR) householdData.adjustIncome();

	        if (trackTime) startTime = System.currentTimeMillis();
	        if (currentYear == properties.main.implementation.BASE_YEAR || currentYear != properties.main.startYear)
	            SiloUtil.summarizeMicroData(currentYear, modelContainer, data);

		    em.simulate((int) year);

	        int nextYearForTransportModel = currentYear + 1;
	        if (tdmYears.contains(nextYearForTransportModel)) {
	            if (properties.transportModel.runTravelDemandModel)
	                modelContainer.getTransportModel().runTransportModel(nextYearForTransportModel);
	        }

	        if (trackTime) startTime = System.currentTimeMillis();
	        modelContainer.getPrm().updatedRealEstatePrices();

	        em.finishYear((int) year, new int[] {0,0}, 0, data);
	        IssueCounter.logIssues(geoData);           // log any issues that arose during this simulation period

	        logger.info("  Finished this simulation period with " + householdData.getPersonCount() +
	                " persons, " + householdData.getHouseholds().size() +" households and "  +
	                data.getRealEstateData().getDwellings().size() + " dwellings.");
	        currentYear++;
	        if (SiloUtil.modelStopper("check")) finishModel();
	}
	
	public void finishModel() {
		// close model run

	        //Writes summarize data in 2 files, the normal combined file & a special file with only last year's data
	        SummarizeData.resultWriterReplicate = true;

	        if (scalingYears.contains(properties.main.endYear))
	            SummarizeData.scaleMicroDataToExogenousForecast(properties.main.endYear, data);

	        if (properties.main.endYear != 2040) {
	            SummarizeData.writeOutSyntheticPopulation(properties.main.endYear, data);
	            geoData.writeOutDevelopmentCapacityFile(data);
	        }

	        SiloUtil.summarizeMicroData(properties.main.endYear, modelContainer, data);
	        SiloUtil.finish(modelContainer);
	        SiloUtil.modelStopper("removeFile");
	        
	        if(properties.cblcm.createCblcmFiles){
	        	 String directory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
	             SiloUtil.createDirectoryIfNotExistingYet(directory);
	             String outputFile = (directory + "/" + properties.main.spatialResultFileName + "_" + properties.main.endYear + "VS" + properties.cblcm.baseYear + ".csv");
	             String[] inputFiles = new String[2];
	             inputFiles[0] = (directory + "/" + properties.main.spatialResultFileName + properties.main.gregorianIterator + ".csv");
	             inputFiles[1] = (properties.main.baseDirectory+ properties.cblcm.baseFile);

	             try {
					CblcmDiffGenerator.generateCblcmDiff(inputFiles, outputFile, Integer.valueOf(properties.cblcm.baseYear) , properties.main.endYear);
				} catch (NumberFormatException e) {
					logger.error(e);
				} catch (IOException e) {
					logger.error("Error while Writing CBLCM Diff File", e);
				}
	        }
	        
//	        if (trackTime) SiloUtil.writeOutTimeTracker(timeCounter);
	        logger.info("Scenario results can be found in the directory scenOutput/" + properties.main.scenarioName + ".");
	}
}