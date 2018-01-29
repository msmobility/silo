package de.tum.bgu.msm;
/**
 * 
 */

import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.maryland.GeoDataMstm;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.transportModel.MitoTransportModel;
import de.tum.bgu.msm.transportModel.TransportModelI;
import de.tum.bgu.msm.utils.CblcmDiffGenerator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static de.tum.bgu.msm.SiloModel.logger;

/**
 * @author kainagel
 *
 */
public class SiloModelCBLCM {
	private int[] scalingYears;
	private int currentYear;
	private int[] skimYears;
	private int[] tdmYears;
	private boolean trackTime;
	private long[][] timeCounter;
	private SiloModelContainer modelContainer;
	private SiloDataContainer dataContainer;
	public GeoData geoData;
	private TransportModelI transportModel; // ony used for MD implementation


	public SiloModelCBLCM() {
		IssueCounter.setUpCounter();   // set up counter for any issues during initial setup
		SiloUtil.modelStopper("initialize");
	}

	void initialize() {
		// initial steps that only need to performed once to set up the model

	        // define years to simulate
	        scalingYears = Properties.get().main.scalingYears;
	        if (scalingYears[0] != -1) SummarizeData.readScalingYearControlTotals();
	        currentYear = Properties.get().main.startYear;
	        tdmYears = Properties.get().transportModel.modelYears;
	        skimYears = Properties.get().transportModel.skimYears;
	        // Note: only implemented for MSTM:
	        geoData = new GeoDataMstm();
	        // Note: only implemented for MSTM:

		// read micro data
		dataContainer = SiloDataContainer.createSiloDataContainer();
		modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer);
		
		modelContainer.getAcc().readCarSkim(Properties.get().main.startYear);
		modelContainer.getAcc().readPtSkim(Properties.get().main.startYear);
		modelContainer.getAcc().initialize();

	        trackTime = Properties.get().main.trackTime;
	        timeCounter = new long[EventTypes.values().length + 11][Properties.get().main.endYear + 1];
	        IssueCounter.logIssues(geoData);           // log any potential issues during initial setup

	        transportModel = new MitoTransportModel(null, Properties.get().main.baseDirectory, geoData, modelContainer);
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
	        if (SiloUtil.containsElement(scalingYears, currentYear))
	            SummarizeData.scaleMicroDataToExogenousForecast(currentYear, dataContainer);
	        logger.info("Simulating changes from year " + currentYear + " to year " + (currentYear + 1));
	        IssueCounter.setUpCounter();    // setup issue counter for this simulation period
	        SiloUtil.trackingFile("Simulating changes from year " + currentYear + " to year " + (currentYear + 1));
	        EventManager em = new EventManager(dataContainer);
	        long startTime = 0;
	        if (trackTime) startTime = System.currentTimeMillis();
	        modelContainer.getIomig().setupInOutMigration(currentYear);
	        if (trackTime) timeCounter[EventTypes.values().length][currentYear] += System.currentTimeMillis() - startTime;

	        if (trackTime) startTime = System.currentTimeMillis();
	        modelContainer.getCons().planNewDwellingsForThisComingYear(currentYear, modelContainer, dataContainer);
	        if (trackTime) timeCounter[EventTypes.values().length + 1][currentYear] += System.currentTimeMillis() - startTime;

	        if (trackTime) startTime = System.currentTimeMillis();
	        if (currentYear != Properties.get().main.implementation.BASE_YEAR) {
	            modelContainer.getUpdateJobs().updateJobInventoryMultiThreadedThisYear(currentYear, dataContainer);
	            dataContainer.getJobData().identifyVacantJobs();
	        }
	        if (trackTime) timeCounter[EventTypes.values().length + 2][currentYear] += System.currentTimeMillis() - startTime;

	        if (trackTime) startTime = System.currentTimeMillis();
	        dataContainer.getHouseholdData().setUpChangeOfJob(currentYear);   // has to run after updateJobInventoryThisYear, as updateJobInventoryThisYear may remove jobs
	        if (trackTime) timeCounter[EventTypes.values().length + 3][currentYear] += System.currentTimeMillis() - startTime;

	        if (trackTime) startTime = System.currentTimeMillis();
	        List<int[]> plannedCouples = modelContainer.getMardiv().selectCouplesToGetMarriedThisYear();
	        if (trackTime) timeCounter[EventTypes.values().length + 5][currentYear] += System.currentTimeMillis() - startTime;

	        if (trackTime) startTime = System.currentTimeMillis();
	        em.createListOfEvents(plannedCouples);
	        if (trackTime) timeCounter[EventTypes.values().length + 4][currentYear] += System.currentTimeMillis() - startTime;

	        if (SiloUtil.containsElement(skimYears, currentYear)) {
	            if (currentYear != Properties.get().main.startYear && !SiloUtil.containsElement(tdmYears, currentYear)) {
	                // skims are always read in start year and in every year the transportation model ran. Additional
	                // years to read skims may be provided in skimYears
	                modelContainer.getAcc().readCarSkim(currentYear);
	                modelContainer.getAcc().readPtSkim(currentYear);
	                modelContainer.getAcc().calculateAccessibilities(currentYear);
	            }
	        }

	        if (trackTime) startTime = System.currentTimeMillis();
	        modelContainer.getDdOverwrite().addDwellings(currentYear, dataContainer);
	        if (trackTime) timeCounter[EventTypes.values().length + 10][currentYear] += System.currentTimeMillis() - startTime;

	        if (trackTime) startTime = System.currentTimeMillis();
	        modelContainer.getMove().calculateRegionalUtilities(modelContainer);
	        modelContainer.getMove().calculateAverageHousingSatisfaction(modelContainer);
	        if (trackTime) timeCounter[EventTypes.values().length + 6][currentYear] += System.currentTimeMillis() - startTime;

	        if (trackTime) startTime = System.currentTimeMillis();
	        if (currentYear != Properties.get().main.implementation.BASE_YEAR) dataContainer.getHouseholdData().adjustIncome();
	        if (trackTime) timeCounter[EventTypes.values().length + 9][currentYear] += System.currentTimeMillis() - startTime;

	        if (trackTime) startTime = System.currentTimeMillis();
	        if (currentYear == Properties.get().main.implementation.BASE_YEAR || currentYear != Properties.get().main.startYear)
	            SiloUtil.summarizeMicroData(currentYear, modelContainer, dataContainer);
	        if (trackTime) timeCounter[EventTypes.values().length + 7][currentYear] += System.currentTimeMillis() - startTime;

	        logger.info("  Simulating events");
	        // walk through all events
	        for (int i = 1; i <= em.getNumberOfEvents(); i++) {
	            //	    if (i%500000==0) logger.info("Processing event " + i);
	            // event[] stores event id in position [0] and person id in position [1]
	            int[] event = em.selectNextEvent();
	            if (event[1] == SiloUtil.trackPp || event[1] == SiloUtil.trackHh || event[1] == SiloUtil.trackDd)
	                SiloUtil.trackWriter.println ("Check event " + EventTypes.values()[event[0]] +  " for pp/hh/dd " +
	                        event[1]);
	            if (event[0] == EventTypes.BIRTHDAY.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getBirth().celebrateBirthday(event[1]);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.CHECK_DEATH.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getDeath().chooseDeath(event[1], dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.CHECK_BIRTH.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getBirth().chooseBirth(event[1]);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.CHECK_LEAVE_PARENT_HH.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getLph().chooseLeaveParentHh(event[1], modelContainer, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.CHECK_MARRIAGE.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                int[] couple = Arrays.copyOfRange(event, 1,2);
					modelContainer.getMardiv().marryCouple(couple, modelContainer, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.CHECK_DIVORCE.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getMardiv().chooseDivorce(event[1], modelContainer, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.FIND_NEW_JOB.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getChangeEmployment().findNewJob(event[1], modelContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.QUIT_JOB.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getChangeEmployment().quitJob(event[1], dataContainer.getJobData());
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.HOUSEHOLD_MOVE.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getMove().chooseMove(event[1], modelContainer, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.INMIGRATION.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getIomig().inmigrateHh(event[1], modelContainer, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.OUT_MIGRATION.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getIomig().outMigrateHh(event[1], false, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.DD_CHANGE_QUAL.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getRenov().checkRenovation(event[1]);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.DD_DEMOLITION.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getDemol().checkDemolition(event[1], modelContainer, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.DD_CONSTRUCTION.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getCons().buildDwelling(event[1], currentYear, modelContainer, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else {
	                logger.warn("Unknown event type: " + event[0]);
	            }
	        }

	        int nextYearForTransportModel = currentYear + 1;
	        if (SiloUtil.containsElement(tdmYears, nextYearForTransportModel)) {
	            if (Properties.get().transportModel.runTravelDemandModel)
	                transportModel.runTransportModel(nextYearForTransportModel);
	        }

	        if (trackTime) startTime = System.currentTimeMillis();
	        modelContainer.getPrm().updatedRealEstatePrices(currentYear, dataContainer);
	        if (trackTime) timeCounter[EventTypes.values().length + 8][currentYear] += System.currentTimeMillis() - startTime;

	        EventManager.logEvents(new int[] {0,0});
	        IssueCounter.logIssues(geoData);           // log any issues that arose during this simulation period

	        logger.info("  Finished this simulation period with " + dataContainer.getHouseholdData().getNumberOfPersons() +
	                " persons, " + dataContainer.getHouseholdData().getNumberOfHouseholds()+" households and "  +
	                Dwelling.getDwellingCount() + " dwellings.");
	        currentYear++;
	        if (SiloUtil.modelStopper("check")) finishModel();
	}
	
	protected void finishModel() {
		// close model run

	        //Writes summarize data in 2 files, the normal combined file & a special file with only last year's data
	        SummarizeData.resultWriterReplicate = true;

	        if (SiloUtil.containsElement(scalingYears, Properties.get().main.endYear))
	            SummarizeData.scaleMicroDataToExogenousForecast(Properties.get().main.endYear, dataContainer);

	        dataContainer.getHouseholdData().summarizeHouseholdsNearMetroStations(modelContainer);

	        if (Properties.get().main.endYear != 2040) {
	            SummarizeData.writeOutSyntheticPopulation(Properties.get().main.endYear);
	            geoData.writeOutDevelopmentCapacityFile(dataContainer);
	        }

	        SiloUtil.summarizeMicroData(Properties.get().main.endYear, modelContainer, dataContainer);
	        SiloUtil.finish(modelContainer);
	        SiloUtil.modelStopper("removeFile");
	        
	        if(Properties.get().cblcm.createCblcmFiles){
	        	 String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
	             SiloUtil.createDirectoryIfNotExistingYet(directory);
	             String outputFile = (directory + "/" + Properties.get().main.spatialResultFileName + "_" + Properties.get().main.endYear + "VS" + Properties.get().cblcm.baseYear + ".csv");
	             String[] inputFiles = new String[2];
	             inputFiles[0] = (directory + "/" + Properties.get().main.spatialResultFileName + Properties.get().main.gregorianIterator + ".csv");
	             inputFiles[1] = (Properties.get().main.baseDirectory+ Properties.get().cblcm.baseFile);

	             try {
					CblcmDiffGenerator.generateCblcmDiff(inputFiles, outputFile, Integer.valueOf(Properties.get().cblcm.baseYear) , Properties.get().main.endYear);
				} catch (NumberFormatException e) {
					logger.error(e);
				} catch (IOException e) {
					logger.error("Error while Writing CBLCM Diff File", e);
				}
	        }
	        
	        if (trackTime) SiloUtil.writeOutTimeTracker(timeCounter);
	        logger.info("Scenario results can be found in the directory scenOutput/" + Properties.get().main.scenarioName + ".");
	}
}