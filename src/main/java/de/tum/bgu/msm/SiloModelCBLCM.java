package de.tum.bgu.msm;
/**
 * 
 */

import cern.colt.Timer;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.maryland.GeoDataMstm;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.CblcmDiffGenerator;
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
    private final Timer timer = new Timer();


    private final Table<Integer, String, Integer> timeTrackerTable = HashBasedTable.create();

	private int currentYear;
	private boolean trackTime;
	private SiloModelContainer modelContainer;
	private SiloDataContainer dataContainer;
	public GeoData geoData;


	public SiloModelCBLCM() {
		IssueCounter.setUpCounter();   // set up counter for any issues during initial setup
		SiloUtil.modelStopper("initialize");
	}

	void initialize() {
		// initial steps that only need to performed once to set up the model
	        // define years to simulate
	        scalingYears.addAll(Properties.get().main.scalingYears);
	        if (!scalingYears.isEmpty()) SummarizeData.readScalingYearControlTotals();
	        currentYear = Properties.get().main.startYear;
	        tdmYears.addAll(Properties.get().transportModel.modelYears);
	        skimYears.addAll(Properties.get().transportModel.skimYears);
	        // Note: only implemented for MSTM:
	        geoData = new GeoDataMstm();
	        // Note: only implemented for MSTM:

		// read micro data
		dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
		modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null);

		SkimUtil.updateCarSkim((SkimTravelTimes) modelContainer.getAcc().getTravelTimes(), currentYear, Properties.get());
		SkimUtil.updateTransitSkim((SkimTravelTimes) modelContainer.getAcc().getTravelTimes(), currentYear, Properties.get());
		modelContainer.getAcc().initialize();

	        trackTime = Properties.get().main.trackTime;

	        IssueCounter.logIssues(geoData);           // log any potential issues during initial setup


	        if (Properties.get().main.createPrestoSummary) {
				SummarizeData.preparePrestoSummary(geoData);
			}
	        SiloUtil.initializeRandomNumber(Properties.get().main.randomSeed);
	}
	
	void runYear(double year) {
		// run single simulation period

	        if (year != 1) {
	            logger.error("SILO is not prepared to simulate other interval than 1 year. Invalid interval: " + year);
	            System.exit(1);
	        }
	        if (scalingYears.contains(currentYear))
	            SummarizeData.scaleMicroDataToExogenousForecast(currentYear, dataContainer);
	        logger.info("Simulating changes from year " + currentYear + " to year " + (currentYear + 1));
	        IssueCounter.setUpCounter();    // setup issue counter for this simulation period
	        SiloUtil.trackingFile("Simulating changes from year " + currentYear + " to year " + (currentYear + 1));
	        EventManager em = new EventManager(timeTrackerTable);
            final HouseholdDataManager householdData = dataContainer.getHouseholdData();
			long startTime = 0;


	        if (trackTime) timer.reset();
	        if (currentYear != Properties.get().main.implementation.BASE_YEAR) {
	            modelContainer.getUpdateJobs().updateJobInventoryMultiThreadedThisYear(currentYear);
	            dataContainer.getJobData().identifyVacantJobs();
	        }
            if (trackTime) {
                timeTrackerTable.put((int) year, "updateJobInventory", (int) timer.millis());
            }


	        if (skimYears.contains(currentYear)) {
	            if (currentYear != Properties.get().main.startYear && !tdmYears.contains(currentYear)) {
	                // skims are always read in start year and in every year the transportation model ran. Additional
	                // years to read skims may be provided in skimYears
	                SkimUtil.updateCarSkim((SkimTravelTimes) modelContainer.getAcc().getTravelTimes(),
                            currentYear, Properties.get());
	                SkimUtil.updateTransitSkim((SkimTravelTimes) modelContainer.getAcc().getTravelTimes(),
                            currentYear, Properties.get());
	                modelContainer.getAcc().calculateHansenAccessibilities(currentYear);
	            }
	        }

	        if (trackTime) timer.reset();
	        modelContainer.getDdOverwrite().addDwellings(currentYear);
            if (trackTime) {
                timeTrackerTable.put((int) year, "addOverwriteDwellings", (int) timer.millis());
            }

	        if (trackTime) timer.reset();
	        modelContainer.getMove().calculateRegionalUtilities();
	        modelContainer.getMove().calculateAverageHousingSatisfaction();
            if (trackTime) {
                timeTrackerTable.put((int) year, "calcAveHousingSatisfaction", (int) timer.millis());
            }
	        if (trackTime) timer.reset();
	        if (currentYear != Properties.get().main.implementation.BASE_YEAR) householdData.adjustIncome();
            if (trackTime) {
                timeTrackerTable.put((int) year, "planIncomeChange", (int) timer.millis());
            }

	        if (trackTime) timer.reset();
	        if (currentYear == Properties.get().main.implementation.BASE_YEAR || currentYear != Properties.get().main.startYear)
	            SiloUtil.summarizeMicroData(currentYear, modelContainer, dataContainer);
            if (trackTime) {
                timeTrackerTable.put((int) year, "SummarizeData", (int) timer.millis());
            }

		    em.simulateEvents((int) year);

	        int nextYearForTransportModel = currentYear + 1;
	        if (tdmYears.contains(nextYearForTransportModel)) {
	            if (Properties.get().transportModel.runTravelDemandModel)
	                modelContainer.getTransportModel().runTransportModel(nextYearForTransportModel);
	        }

	        if (trackTime) timer.reset();
	        modelContainer.getPrm().updatedRealEstatePrices();
            if (trackTime) {
                timeTrackerTable.put((int) year, "updateRealEstatePrices", (int) timer.millis());
            }
	        em.finishYear((int) year, new int[] {0,0}, dataContainer);
	        IssueCounter.logIssues(geoData);           // log any issues that arose during this simulation period

	        logger.info("  Finished this simulation period with " + householdData.getPersonCount() +
	                " persons, " + householdData.getHouseholds().size() +" households and "  +
	                dataContainer.getRealEstateData().getDwellings().size() + " dwellings.");
	        currentYear++;
	        if (SiloUtil.modelStopper("check")) finishModel();
	}
	
	protected void finishModel() {
		// close model run

	        //Writes summarize data in 2 files, the normal combined file & a special file with only last year's data
	        SummarizeData.resultWriterReplicate = true;

	        if (scalingYears.contains(Properties.get().main.endYear))
	            SummarizeData.scaleMicroDataToExogenousForecast(Properties.get().main.endYear, dataContainer);

	        dataContainer.getHouseholdData().summarizeHouseholdsNearMetroStations(modelContainer);

	        if (Properties.get().main.endYear != 2040) {
	            SummarizeData.writeOutSyntheticPopulation(Properties.get().main.endYear, dataContainer);
	            geoData.writeOutDevelopmentCapacityFile(dataContainer);
	        }

	        SiloUtil.summarizeMicroData(Properties.get().main.endYear, modelContainer, dataContainer);
	        SiloUtil.finish(modelContainer);
	        SiloUtil.modelStopper("removeFile");
	        
	        if(Properties.get().cblcm.createCblcmFiles){
	        	 String directory = Properties.get().propertiesPath.getParent() + "scenOutput/" + Properties.get().main.scenarioName;
	             SiloUtil.createDirectoryIfNotExistingYet(directory);
	             String outputFile = (directory + "/" + Properties.get().main.spatialResultFileName + "_" + Properties.get().main.endYear + "VS" + Properties.get().cblcm.baseYear + ".csv");
	             String[] inputFiles = new String[2];
	             inputFiles[0] = (directory + "/" + Properties.get().main.spatialResultFileName + Properties.get().main.gregorianIterator + ".csv");
	             inputFiles[1] = (Properties.get().propertiesPath.getParent() + Properties.get().cblcm.baseFile);

	             try {
					CblcmDiffGenerator.generateCblcmDiff(inputFiles, outputFile, Integer.valueOf(Properties.get().cblcm.baseYear) , Properties.get().main.endYear);
				} catch (NumberFormatException e) {
					logger.error(e);
				} catch (IOException e) {
					logger.error("Error while Writing CBLCM Diff File", e);
				}
	        }
	        
	        if (trackTime) SiloUtil.writeOutTimeTracker(timeTrackerTable);
	        logger.info("Scenario results can be found in the directory scenOutput/" + Properties.get().main.scenarioName + ".");
	}
}