package de.tum.bgu.msm;
/**
 * 
 */

import java.io.IOException;
import java.util.ResourceBundle;

import com.pb.common.util.ResourceUtil;

import de.tum.bgu.msm.SiloModel.Implementation;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.geoDataMstm;
import de.tum.bgu.msm.data.summarizeData;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.transportModel.MitoTransportModel;
import de.tum.bgu.msm.transportModel.TransportModelI;
import de.tum.bgu.msm.utils.CblcmDiffGenerator;

import static de.tum.bgu.msm.SiloModel.* ;

/**
 * @author kainagel
 *
 */
public class SiloModelCBLCM {
	    private ResourceBundle rbLandUse;
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

	public SiloModelCBLCM(ResourceBundle rb) {
		this.rbLandUse = rb;
		IssueCounter.setUpCounter();   // set up counter for any issues during initial setup
		SiloUtil.modelStopper("initialize");
	}

	void initialize() {
		// initial steps that only need to performed once to set up the model

	        // define years to simulate
	        scalingYears = ResourceUtil.getIntegerArray(rbLandUse, PROPERTIES_SCALING_YEARS);
	        if (scalingYears[0] != -1) summarizeData.readScalingYearControlTotals(rbLandUse);
	        currentYear = SiloUtil.getStartYear();
	        tdmYears = ResourceUtil.getIntegerArray(rbLandUse, PROPERTIES_TRANSPORT_MODEL_YEARS);
	        skimYears = ResourceUtil.getIntegerArray(rbLandUse, PROPERTIES_TRANSPORT_SKIM_YEARS);
	        // Note: only implemented for MSTM:
	        geoData = new geoDataMstm(rbLandUse);
	        // Note: only implemented for MSTM:

	        modelContainer = SiloModelContainer.createSiloModelContainer(rbLandUse, geoData, Implementation.MSTM);
	        // read micro data
	        dataContainer = SiloDataContainer.createSiloDataContainer(rbLandUse, geoData, false);

	        trackTime = ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_TRACK_TIME, false);
	        timeCounter = new long[EventTypes.values().length + 11][SiloUtil.getEndYear() + 1];
	        IssueCounter.logIssues(geoData);           // log any potential issues during initial setup

	        transportModel = new MitoTransportModel(rbLandUse, SiloUtil.baseDirectory, geoData, modelContainer);
	        if (ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_CREATE_PRESTO_SUMMARY_FILE, false))
	            summarizeData.preparePrestoSummary(rbLandUse, geoData);
	        SiloUtil.initializeRandomNumber();
	}
	
	void runYear(double year) {
		// run single simulation period

	        if (year != 1) {
	            logger.error("SILO is not prepared to simulate other interval than 1 year. Invalid interval: " + year);
	            System.exit(1);
	        }
	        if (SiloUtil.containsElement(scalingYears, currentYear))
	            summarizeData.scaleMicroDataToExogenousForecast(rbLandUse, currentYear, dataContainer);
	        logger.info("Simulating changes from year " + currentYear + " to year " + (currentYear + 1));
	        IssueCounter.setUpCounter();    // setup issue counter for this simulation period
	        SiloUtil.trackingFile("Simulating changes from year " + currentYear + " to year " + (currentYear + 1));
	        EventManager em = new EventManager(rbLandUse, dataContainer);
	        long startTime = 0;
	        if (trackTime) startTime = System.currentTimeMillis();
	        modelContainer.getIomig().setupInOutMigration(currentYear);
	        if (trackTime) timeCounter[EventTypes.values().length][currentYear] += System.currentTimeMillis() - startTime;

	        if (trackTime) startTime = System.currentTimeMillis();
	        modelContainer.getCons().planNewDwellingsForThisComingYear(currentYear, modelContainer, dataContainer);
	        if (trackTime) timeCounter[EventTypes.values().length + 1][currentYear] += System.currentTimeMillis() - startTime;

	        if (trackTime) startTime = System.currentTimeMillis();
	        if (currentYear != SiloUtil.getBaseYear()) {
	            modelContainer.getUpdateJobs().updateJobInventoryMultiThreadedThisYear(currentYear, dataContainer);
	            dataContainer.getJobData().identifyVacantJobs();
	        }
	        if (trackTime) timeCounter[EventTypes.values().length + 2][currentYear] += System.currentTimeMillis() - startTime;

	        if (trackTime) startTime = System.currentTimeMillis();
	        dataContainer.getHouseholdData().setUpChangeOfJob(currentYear);   // has to run after updateJobInventoryThisYear, as updateJobInventoryThisYear may remove jobs
	        if (trackTime) timeCounter[EventTypes.values().length + 3][currentYear] += System.currentTimeMillis() - startTime;

	        if (trackTime) startTime = System.currentTimeMillis();
	        int numberOfPlannedCouples = modelContainer.getMardiv().selectCouplesToGetMarriedThisYear();
	        if (trackTime) timeCounter[EventTypes.values().length + 5][currentYear] += System.currentTimeMillis() - startTime;

	        if (trackTime) startTime = System.currentTimeMillis();
	        em.createListOfEvents(numberOfPlannedCouples);
	        if (trackTime) timeCounter[EventTypes.values().length + 4][currentYear] += System.currentTimeMillis() - startTime;

	        if (SiloUtil.containsElement(skimYears, currentYear)) {
	            if (currentYear != SiloUtil.getStartYear() && !SiloUtil.containsElement(tdmYears, currentYear)) {
	                // skims are always read in start year and in every year the transportation model ran. Additional
	                // years to read skims may be provided in skimYears
	                modelContainer.getAcc().readSkim(currentYear);
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
	        if (currentYear != SiloUtil.getBaseYear()) dataContainer.getHouseholdData().adjustIncome();
	        if (trackTime) timeCounter[EventTypes.values().length + 9][currentYear] += System.currentTimeMillis() - startTime;

	        if (trackTime) startTime = System.currentTimeMillis();
	        if (currentYear == SiloUtil.getBaseYear() || currentYear != SiloUtil.getStartYear())
	            SiloUtil.summarizeMicroData(currentYear, modelContainer, dataContainer, geoData, rbLandUse );
	        if (trackTime) timeCounter[EventTypes.values().length + 7][currentYear] += System.currentTimeMillis() - startTime;

	        logger.info("  Simulating events");
	        // walk through all events
	        for (int i = 1; i <= em.getNumberOfEvents(); i++) {
	            //	    if (i%500000==0) logger.info("Processing event " + i);
	            // event[] stores event id in position [0] and person id in position [1]
	            Integer[] event = em.selectNextEvent();
	            if (event[1] == SiloUtil.trackPp || event[1] == SiloUtil.trackHh || event[1] == SiloUtil.trackDd)
	                SiloUtil.trackWriter.println ("Check event " + EventTypes.values()[event[0]] +  " for pp/hh/dd " +
	                        event[1]);
	            if (event[0] == EventTypes.birthday.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getBirth().celebrateBirthday(event[1]);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.checkDeath.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getDeath().chooseDeath(event[1], dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.checkBirth.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getBirth().chooseBirth(event[1]);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.checkLeaveParentHh.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getLph().chooseLeaveParentHh(event[1], modelContainer, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.checkMarriage.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getMardiv().choosePlannedMarriage(event[1], modelContainer, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.checkDivorce.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getMardiv().chooseDivorce(event[1], modelContainer, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.findNewJob.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getChangeEmployment().findNewJob(event[1], modelContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.quitJob.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getChangeEmployment().quitJob(event[1], dataContainer.getJobData());
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.householdMove.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getMove().chooseMove(event[1], modelContainer, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.inmigration.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getIomig().inmigrateHh(event[1], modelContainer, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.outMigration.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getIomig().outMigrateHh(event[1], false, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.ddChangeQual.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getRenov().checkRenovation(event[1]);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.ddDemolition.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getDemol().checkDemolition(event[1], modelContainer, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else if (event[0] == EventTypes.ddConstruction.ordinal()) {
	                if (trackTime) startTime = System.currentTimeMillis();
	                modelContainer.getCons().buildDwelling(event[1], currentYear, modelContainer, dataContainer);
	                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
	            } else {
	                logger.warn("Unknown event type: " + event[0]);
	            }
	        }

	        int nextYearForTransportModel = currentYear + 1;
	        if (SiloUtil.containsElement(tdmYears, nextYearForTransportModel)) {
	            if (ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_RUN_TRAVEL_DEMAND_MODEL, false))
	                transportModel.runTransportModel(nextYearForTransportModel);
	        }

	        if (trackTime) startTime = System.currentTimeMillis();
	        modelContainer.getPrm().updatedRealEstatePrices(currentYear, dataContainer);
	        if (trackTime) timeCounter[EventTypes.values().length + 8][currentYear] += System.currentTimeMillis() - startTime;

	        EventManager.logEvents();
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
	        summarizeData.resultWriterReplicate = true;

	        if (SiloUtil.containsElement(scalingYears, SiloUtil.getEndYear()))
	            summarizeData.scaleMicroDataToExogenousForecast(rbLandUse, SiloUtil.getEndYear(), dataContainer);

	        dataContainer.getHouseholdData().summarizeHouseholdsNearMetroStations(modelContainer);

	        if (SiloUtil.getEndYear() != 2040) {
	            summarizeData.writeOutSyntheticPopulation(rbLandUse, SiloUtil.endYear);
	            geoData.writeOutDevelopmentCapacityFile(dataContainer);
	        }

	        SiloUtil.summarizeMicroData(SiloUtil.getEndYear(), modelContainer, dataContainer, geoData, rbLandUse);
	        SiloUtil.finish(modelContainer);
	        SiloUtil.modelStopper("removeFile");
	        
	        if(ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_CREATE_CBLCM_FILES, false)){
	        	 String directory = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName;
	             SiloUtil.createDirectoryIfNotExistingYet(directory);
	             String outputFile = (directory + "/" + rbLandUse.getString(PROPERTIES_SPATIAL_RESULT_FILE_NAME) + "_" + SiloUtil.getEndYear() + "VS" + rbLandUse.getString(PROPERTIES_CBLCM_BASE_YEAR) + ".csv");
	             String[] inputFiles = new String[2];
	             inputFiles[0] = (directory + "/" + rbLandUse.getString(PROPERTIES_SPATIAL_RESULT_FILE_NAME) + SiloUtil.gregorianIterator + ".csv");
	             inputFiles[1] = (SiloUtil.baseDirectory+ rbLandUse.getString(PROPERTIES_CBLCM_BASE_FILE));

	             try {
					CblcmDiffGenerator.generateCblcmDiff(inputFiles, outputFile, Integer.valueOf(rbLandUse.getString(PROPERTIES_CBLCM_BASE_YEAR)) , SiloUtil.getEndYear(), rbLandUse, PROPERTIES_CBLCM_MULTIPLIER_PREFIX);
				} catch (NumberFormatException e) {
					logger.error(e);
				} catch (IOException e) {
					logger.error("Error while Writing CBLCM Diff File", e);
				}
	        }
	        
	        if (trackTime) SiloUtil.writeOutTimeTracker(timeCounter, rbLandUse );
	        logger.info("Scenario results can be found in the directory scenOutput/" + SiloUtil.scenarioName + ".");
	}
}