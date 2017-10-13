/*
 * Copyright  2005 PB Consult Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package de.tum.bgu.msm;

import java.io.File;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.matsim.core.config.Config;

import com.pb.common.util.ResourceUtil;

import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.GeoData;
import de.tum.bgu.msm.data.geoDataMstm;
import de.tum.bgu.msm.data.geoDataMuc;
import de.tum.bgu.msm.data.summarizeData;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.transportModel.MatsimTransportModel;
import de.tum.bgu.msm.transportModel.MitoTransportModel;
import de.tum.bgu.msm.transportModel.TransportModelI;

/**
 * @author Greg Erhardt 
 * Created on Dec 2, 2009
 */
public class SiloModel {
	static Logger logger = Logger.getLogger(SiloModel.class);

	public enum Implementation {MUC, MSTM, CAPE_TOWWN, MSP};

	private final ResourceBundle rbLandUse;

	public static final String PROPERTIES_RUN_SILO                          = "run.silo.model";
	protected static final String PROPERTIES_SCALING_YEARS                  = "scaling.years";

	protected static final String PROPERTIES_READ_SMALL_SYNPOP              = "read.small.syn.pop";
	protected static final String PROPERTIES_WRITE_SMALL_SYNPOP             = "write.small.syn.pop";

	protected static final String PROPERTIES_TRANSPORT_MODEL_YEARS          = "transport.model.years";
	protected static final String PROPERTIES_TRANSPORT_SKIM_YEARS           = "skim.years";
	protected static final String PROPERTIES_RUN_TRAVEL_DEMAND_MODEL        = "mito.run.travel.model";
	public static final String PROPERTIES_FILE_DEMAND_MODEL                 = "mito.properties.file";
	public static final String PROPERTIES_RUN_TRAVEL_MODEL_MATSIM           = "matsim.run.travel.model";

	public static final String PROPERTIES_TRACK_TIME                        = "track.time";
	public static final String PROPERTIES_TRACK_TIME_FILE                   = "track.time.file";

	protected static final String PROPERTIES_CREATE_CBLCM_FILES             = "create.cblcm.files";
	protected static final String PROPERTIES_CBLCM_BASE_YEAR				= "cblcm.base.year";
	protected static final String PROPERTIES_CBLCM_BASE_FILE				= "cblcm.base.file";
	protected static final String PROPERTIES_CBLCM_MULTIPLIER_PREFIX		= "cblcm.multiplier";
	protected static final String PROPERTIES_CBLCM_MAND_ZONES_FILE			= "cblcm.mandatory.zonal.base.file";
	protected static final String PROPERTIES_SPATIAL_RESULT_FILE_NAME       = "spatial.result.file.name";
	public static final String PROPERTIES_CREATE_MSTM_OUTPUT_FILES       = "create.mstm.socio.econ.files";

	protected static final String PROPERTIES_CREATE_HOUSING_ENV_IMPACT_FILE = "create.housing.environm.impact.files";
	protected static final String PROPERTIES_CREATE_PRESTO_SUMMARY_FILE     = "create.presto.summary.file";

	private SiloModelContainer modelContainer;
	private SiloDataContainer dataContainer;
	public GeoData geoData;
	private final Config matsimConfig;

	/**
	 * Constructor to set up a SILO model
	 * @param rbLandUse ResourceBundle
	 */
	public SiloModel(ResourceBundle rbLandUse) {
		this( rbLandUse, null ) ;
	}
	
	public SiloModel( ResourceBundle rbLandUse, Config matsimConfig ) {
		this.rbLandUse = rbLandUse;
		IssueCounter.setUpCounter();   // set up counter for any issues during initial setup
		SiloUtil.modelStopper("initialize");
		this.matsimConfig = matsimConfig ;
	}


	public void runModel(Implementation implementation) {
		//Main method to run a SILO model

		if (!ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_RUN_SILO, false)) return;
		logger.info("Start SILO Model (Implementation " + implementation + ")");

		// define years to simulate
		int[] scalingYears = ResourceUtil.getIntegerArray(rbLandUse, PROPERTIES_SCALING_YEARS);
		if (scalingYears[0] != -1) summarizeData.readScalingYearControlTotals(rbLandUse);
		int[] tdmYears = ResourceUtil.getIntegerArray(rbLandUse, PROPERTIES_TRANSPORT_MODEL_YEARS);
		int[] skimYears = ResourceUtil.getIntegerArray(rbLandUse, PROPERTIES_TRANSPORT_SKIM_YEARS);

		// Define geoData object, which stores all geographical data (zones, zonal data, regions, etc.)
		switch (implementation) {
		case MSTM:
			geoData = new geoDataMstm(rbLandUse);
			break;
		case MUC:
			geoData = new geoDataMuc(rbLandUse);
			break;
			//$CASES-OMITTED$
		default:
			logger.error("Invalid implementation. Choose <MSTM> or <Muc>.");
			System.exit(0);
			geoData = new geoDataMuc(rbLandUse);  // superfluous statement (after System.exit()), but model complains without
		}

		geoData.setInitialData();
		IssueCounter.regionSpecificCounters(geoData);

		// create main objects and read synthetic population
		dataContainer = SiloDataContainer.createSiloDataContainer(rbLandUse, geoData,
				ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_READ_SMALL_SYNPOP, false));
		if (ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_WRITE_SMALL_SYNPOP, false))
			dataContainer.getHouseholdData().writeOutSmallSynPop();
		modelContainer = SiloModelContainer.createSiloModelContainer(rbLandUse, geoData, implementation);

		final boolean runMatsim = ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_RUN_TRAVEL_MODEL_MATSIM, false );
		final boolean runTravelDemandModel = ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_RUN_TRAVEL_DEMAND_MODEL, false);
		if ( runMatsim && ( runTravelDemandModel || ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_CREATE_MSTM_OUTPUT_FILES, true) ) ) {
			throw new RuntimeException("trying to run both MATSim and MSTM is inconsistent at this point." ) ;
		}

		TransportModelI transportModel ;

		if ( runMatsim ) {
			logger.info("  MATSim is used as the transport model");
			transportModel = new MatsimTransportModel(dataContainer.getHouseholdData(), modelContainer.getAcc(), rbLandUse, matsimConfig);
		} else {
			logger.info("  MITO is used as the transport model");
			File rbFile = new File(ResourceUtil.getProperty(rbLandUse, PROPERTIES_FILE_DEMAND_MODEL));
			transportModel = new MitoTransportModel(ResourceUtil.getPropertyBundle(rbFile), SiloUtil.baseDirectory, geoData, modelContainer);
		}
		//        setOldLocalModelVariables();
		// yy this is where I found setOldLocalModelVariables().  MATSim fails then, since "householdData" then is a null pointer first time when
		// it is called.  Since I don't know what pulling it up means for MITO, I am putting the command into the if condition.  kai, jan'16

		// Optional method to write out n households with corresponding persons, dwellings and jobs to create smaller
		// synthetic population for testing

		boolean trackTime = ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_TRACK_TIME, false);
		long[][] timeCounter = new long[EventTypes.values().length + 11][SiloUtil.getEndYear() + 1];
		long startTime = 0;
		IssueCounter.logIssues(geoData);           // log any potential issues during initial setup

		if (ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_CREATE_PRESTO_SUMMARY_FILE, false))
			summarizeData.preparePrestoSummary(rbLandUse, geoData);

		for (int year = SiloUtil.getStartYear(); year < SiloUtil.getEndYear(); year += SiloUtil.getSimulationLength()) {
			if (SiloUtil.containsElement(scalingYears, year))
				summarizeData.scaleMicroDataToExogenousForecast(rbLandUse, year, dataContainer);
			logger.info("Simulating changes from year " + year + " to year " + (year + 1));
			IssueCounter.setUpCounter();    // setup issue counter for this simulation period
			SiloUtil.trackingFile("Simulating changes from year " + year + " to year " + (year + 1));
			EventManager em = new EventManager(rbLandUse, dataContainer);

			if (trackTime) startTime = System.currentTimeMillis();
			modelContainer.getIomig().setupInOutMigration(year);
			if (trackTime) timeCounter[EventTypes.values().length][year] += System.currentTimeMillis() - startTime;

			if (trackTime) startTime = System.currentTimeMillis();
			modelContainer.getCons().planNewDwellingsForThisComingYear(year, modelContainer, dataContainer);
			if (trackTime) timeCounter[EventTypes.values().length + 1][year] += System.currentTimeMillis() - startTime;

			if (trackTime) startTime = System.currentTimeMillis();
			if (year != SiloUtil.getBaseYear()) {
				modelContainer.getUpdateJobs().updateJobInventoryMultiThreadedThisYear(year, dataContainer);
				dataContainer.getJobData().identifyVacantJobs();
			}
			if (trackTime) timeCounter[EventTypes.values().length + 2][year] += System.currentTimeMillis() - startTime;

			if (trackTime) startTime = System.currentTimeMillis();
			dataContainer.getHouseholdData().setUpChangeOfJob(year);   // has to run after updateJobInventoryThisYear, as updateJobInventoryThisYear may remove jobs
			if (trackTime) timeCounter[EventTypes.values().length + 3][year] += System.currentTimeMillis() - startTime;

			if (trackTime) startTime = System.currentTimeMillis();
			int numberOfPlannedCouples = modelContainer.getMardiv().selectCouplesToGetMarriedThisYear();
			if (trackTime) timeCounter[EventTypes.values().length + 5][year] += System.currentTimeMillis() - startTime;

			if (trackTime) startTime = System.currentTimeMillis();
			em.createListOfEvents(numberOfPlannedCouples);
			if (trackTime) timeCounter[EventTypes.values().length + 4][year] += System.currentTimeMillis() - startTime;

			if (SiloUtil.containsElement(skimYears, year) && !SiloUtil.containsElement(tdmYears, year) &&
					!ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_RUN_TRAVEL_DEMAND_MODEL, false) &&
					year != SiloUtil.getStartYear()) {
				// skims are always read in start year and in every year the transportation model ran. Additional
				// years to read skims may be provided in skimYears
				modelContainer.getAcc().readSkim(year);
				modelContainer.getAcc().calculateAccessibilities(year);
			}

			if (trackTime) startTime = System.currentTimeMillis();
			modelContainer.getDdOverwrite().addDwellings(year, dataContainer);
			if (trackTime) timeCounter[EventTypes.values().length + 10][year] += System.currentTimeMillis() - startTime;

			if (trackTime) startTime = System.currentTimeMillis();
			modelContainer.getMove().calculateRegionalUtilities(modelContainer);
			modelContainer.getMove().calculateAverageHousingSatisfaction(modelContainer);
			if (trackTime) timeCounter[EventTypes.values().length + 6][year] += System.currentTimeMillis() - startTime;

			if (trackTime) startTime = System.currentTimeMillis();
			if (year != SiloUtil.getBaseYear()) dataContainer.getHouseholdData().adjustIncome();
			if (trackTime) timeCounter[EventTypes.values().length + 9][year] += System.currentTimeMillis() - startTime;

			if (trackTime) startTime = System.currentTimeMillis();
			if (year == SiloUtil.getBaseYear() || year != SiloUtil.getStartYear())
				SiloUtil.summarizeMicroData(year, modelContainer, dataContainer,geoData,rbLandUse);
			if (trackTime) timeCounter[EventTypes.values().length + 7][year] += System.currentTimeMillis() - startTime;

			logger.info("  Simulating events");
			// walk through all events
			for (int i = 1; i <= em.getNumberOfEvents(); i++) {

				//if (i > 5) continue;
				//	    if (i%500000==0) logger.info("Processing event " + i);
				// event[] stores event id in position [0] and person id in position [1]
				Integer[] event = em.selectNextEvent();
				if (event[1] == SiloUtil.trackPp || event[1] == SiloUtil.trackHh || event[1] == SiloUtil.trackDd)
					SiloUtil.trackWriter.println ("Check event " + EventTypes.values()[event[0]] +  " for pp/hh/dd " +
							event[1]);
				if (event[0] == EventTypes.birthday.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getBirth().celebrateBirthday(event[1]);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.checkDeath.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getDeath().chooseDeath(event[1], dataContainer);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.checkBirth.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getBirth().chooseBirth(event[1]);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.checkLeaveParentHh.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getLph().chooseLeaveParentHh(event[1], modelContainer, dataContainer);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.checkMarriage.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getMardiv().choosePlannedMarriage(event[1], modelContainer, dataContainer);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.checkDivorce.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getMardiv().chooseDivorce(event[1], modelContainer, dataContainer);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.checkSchoolUniv.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getChangeSchoolUniv().updateSchoolUniv(event[1], dataContainer);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.checkDriversLicense.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getChangeDriversLicense().updateDriversLicense(event[1], dataContainer);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.findNewJob.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getChangeEmployment().findNewJob(event[1], modelContainer);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.quitJob.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getChangeEmployment().quitJob(event[1], dataContainer.getJobData());
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.householdMove.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getMove().chooseMove(event[1],modelContainer, dataContainer);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.inmigration.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getIomig().inmigrateHh(event[1], modelContainer, dataContainer);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.outMigration.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getIomig().outMigrateHh(event[1], false, dataContainer);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.ddChangeQual.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getRenov().checkRenovation(event[1]);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.ddDemolition.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getDemol().checkDemolition(event[1], modelContainer, dataContainer);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else if (event[0] == EventTypes.ddConstruction.ordinal()) {
					if (trackTime) startTime = System.currentTimeMillis();
					modelContainer.getCons().buildDwelling(event[1], year, modelContainer, dataContainer);
					if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
				} else {
					logger.warn("Unknown event type: " + event[0]);
				}
			}

			if (runMatsim || runTravelDemandModel || ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_CREATE_MSTM_OUTPUT_FILES, true)) {
				if (SiloUtil.containsElement(tdmYears, year + 1)) {
					transportModel.runTransportModel(year + 1);
				}
			}

			if (trackTime) startTime = System.currentTimeMillis();
			modelContainer.getPrm().updatedRealEstatePrices(year, dataContainer);
			if (trackTime) timeCounter[EventTypes.values().length + 8][year] += System.currentTimeMillis() - startTime;

			EventManager.logEvents();
			IssueCounter.logIssues(geoData);           // log any issues that arose during this simulation period

			logger.info("  Finished this simulation period with " + dataContainer.getHouseholdData().getNumberOfPersons() +
					" persons, " + dataContainer.getHouseholdData().getNumberOfHouseholds()+" households and "  +
					Dwelling.getDwellingCount() + " dwellings.");
			if (SiloUtil.modelStopper("check")) break;
		}
		if (SiloUtil.containsElement(scalingYears, SiloUtil.getEndYear()))
			summarizeData.scaleMicroDataToExogenousForecast(rbLandUse, SiloUtil.getEndYear(), dataContainer);

		dataContainer.getHouseholdData().summarizeHouseholdsNearMetroStations(modelContainer);

		if (SiloUtil.getEndYear() != 2040) {
			summarizeData.writeOutSyntheticPopulation(rbLandUse, SiloUtil.endYear);
			geoData.writeOutDevelopmentCapacityFile(dataContainer);
		}

		SiloUtil.summarizeMicroData(SiloUtil.getEndYear(), modelContainer, dataContainer, geoData, rbLandUse );
		SiloUtil.finish(modelContainer);
		SiloUtil.modelStopper("removeFile");
		if (trackTime) SiloUtil.writeOutTimeTracker(timeCounter, rbLandUse);
		logger.info("Scenario results can be found in the directory scenOutput/" + SiloUtil.scenarioName + ".");
	}


	//    private void summarizeRentAndIncome () {
	//        PrintWriter pw = SiloUtil.openFileForSequentialWriting("temp.csv", false);
	//        pw.println("income,rent");
	//        for (Household hh: Household.getHouseholdArray()) {
	//            pw.println(hh.getHhIncome() + "," + Dwelling.getDwellingFromId(hh.getDwellingId()).getPrice());
	//        }
	//        pw.close();
	//    }




	//*************************************************************************
	//*** Special implementation of same SILO Model for integration with    ***
	//*** CBLCM Framework (http://csdms.colorado.edu/wiki/Main_Page). Used  ***
	//*** in Maryland implementation only. Functions:                       ***
	//*** initialize(), runYear (double dt) and finishModel()               ***
	//*************************************************************************

	@Deprecated // use SiloModelCBLCM directly
	private SiloModelCBLCM cblcm ;
	@Deprecated // use SiloModelCBLCM directly
	public void initialize() {
		cblcm = new SiloModelCBLCM(rbLandUse) ;
		cblcm.initialize();
	}
	@Deprecated // use SiloModelCBLCM directly
	public void runYear (double dt) {
		cblcm.runYear(dt);
	}
	@Deprecated // use SiloModelCBLCM directly
	public void finishModel() {
		cblcm.finishModel();
	}




}

