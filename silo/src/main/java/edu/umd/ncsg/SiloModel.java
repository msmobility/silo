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
package edu.umd.ncsg;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.matsim.core.config.Config;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;

import edu.umd.ncsg.autoOwnership.AutoOwnershipModel;
import edu.umd.ncsg.data.Accessibility;
import edu.umd.ncsg.data.Dwelling;
import edu.umd.ncsg.data.HouseholdDataManager;
import edu.umd.ncsg.data.JobDataManager;
import edu.umd.ncsg.data.RealEstateDataManager;
import edu.umd.ncsg.data.summarizeData;
import edu.umd.ncsg.data.summarizeDataCblcm;
import edu.umd.ncsg.demography.BirthModel;
import edu.umd.ncsg.demography.ChangeEmploymentModel;
import edu.umd.ncsg.demography.DeathModel;
import edu.umd.ncsg.demography.LeaveParentHhModel;
import edu.umd.ncsg.demography.MarryDivorceModel;
import edu.umd.ncsg.events.EventManager;
import edu.umd.ncsg.events.EventTypes;
import edu.umd.ncsg.events.IssueCounter;
import edu.umd.ncsg.jobmography.UpdateJobs;
import edu.umd.ncsg.realEstate.ConstructionModel;
import edu.umd.ncsg.realEstate.ConstructionOverwrite;
import edu.umd.ncsg.realEstate.DemolitionModel;
import edu.umd.ncsg.realEstate.PricingModel;
import edu.umd.ncsg.realEstate.RenovationModel;
import edu.umd.ncsg.relocation.InOutMigration;
import edu.umd.ncsg.relocation.MovesModel;
import edu.umd.ncsg.transportModel.MatsimTransportModel;
import edu.umd.ncsg.transportModel.TransportModelI;
import edu.umd.ncsg.transportModel.TravelDemandModel;
import edu.umd.ncsg.utils.CblcmDiffGenerator;

/**
 * @author Greg Erhardt 
 * Created on Dec 2, 2009
 *
 */
public class SiloModel {
    static Logger logger = Logger.getLogger(SiloModel.class);

    private ResourceBundle rbLandUse;
    public static Random rand;

    protected static final String PROPERTIES_SCALING_YEARS                  = "scaling.years";

    protected static final String PROPERTIES_RUN_TRAVEL_DEMAND_MODEL        = "run.travel.demand.model";
    protected static final String PROPERTIES_TRANSPORT_MODEL_YEARS          = "transport.model.years";
    protected static final String PROPERTIES_TRANSPORT_SKIM_YEARS           = "skim.years";
    public static final String PROPERTIES_RUN_TRANSPORT_DEMAND_MODEL = "run.travel.demand.model";
    public static final String PROPERTIES_RUN_TRAVEL_MODEL_MATSIM = "matsim.run.travel.model";

    public static final String PROPERTIES_TRACK_TIME                        = "track.time";
    public static final String PROPERTIES_TRACK_TIME_FILE                   = "track.time.file";
    
    protected static final String PROPERTIES_CREATE_CBLCM_FILES             = "create.cblcm.files";
    protected static final String PROPERTIES_CBLCM_BASE_YEAR				= "cblcm.base.year";
    protected static final String PROPERTIES_CBLCM_BASE_FILE				= "cblcm.base.file";
    protected static final String PROPERTIES_CBLCM_MULTIPLIER_PREFIX				= "cblcm.multiplier";
    protected static final String PROPERTIES_CBLCM_MAND_ZONES_FILE			= "cblcm.mandatory.zonal.base.file";
    protected static final String PROPERTIES_SPATIAL_RESULT_FILE_NAME       = "spatial.result.file.name";
    protected static final String PROPERTIES_CREATE_MSTM_OUTPUT_FILES       = "create.mstm.socio.econ.files";

    protected static final String PROPERTIES_CREATE_HOUSING_ENV_IMPACT_FILE = "create.housing.environm.impact.files";
    protected static final String PROPERTIES_CREATE_PRESTO_SUMMARY_FILE     = "create.presto.summary.file";

    private int[] scalingYears;
    private int currentYear;
    private HouseholdDataManager householdData;
    private RealEstateDataManager realEstateData;
    private JobDataManager jobData;
    private InOutMigration iomig;
    private ConstructionModel cons;
    private ConstructionOverwrite ddOverwrite;
    private RenovationModel renov;
    private DemolitionModel demol;
    private PricingModel prm;
    private BirthModel birth;
    private DeathModel death;
    private MarryDivorceModel mardiv;
    private LeaveParentHhModel lph;
    private MovesModel move;
    private ChangeEmploymentModel changeEmployment;
    private Accessibility acc;
    private AutoOwnershipModel aoModel;
    private TravelDemandModel TransportModel;
    private UpdateJobs updateJobs;
    private int[] skimYears;
    private int[] tdmYears;
    private boolean trackTime;
    private long[][] timeCounter;
    private SiloModelContainer modelContainer;

private Config matsimConfig;

    /**
     * Constructor to set up a SILO model
     * @param rbLandUse ResourceBundle
     */
    public SiloModel(ResourceBundle rbLandUse) {
        this.rbLandUse = rbLandUse;
        summarizeData.openResultFile(rbLandUse);
        summarizeData.resultFileSpatial(rbLandUse, "open");
        IssueCounter.setUpCounter();   // set up counter for any issues during initial setup
        modelStopper("initialize");
    }


    public void runModel() {
        //Main method to run a SILO model

        logger.info("Start SILO Model");

        // define years to simulate
        int[] scalingYears = ResourceUtil.getIntegerArray(rbLandUse, PROPERTIES_SCALING_YEARS);
        if (scalingYears[0] != -1) summarizeData.readScalingYearControlTotals(rbLandUse);
        int[] tdmYears = ResourceUtil.getIntegerArray(rbLandUse, PROPERTIES_TRANSPORT_MODEL_YEARS);
        int[] skimYears = ResourceUtil.getIntegerArray(rbLandUse, PROPERTIES_TRANSPORT_SKIM_YEARS);

        modelContainer = SiloModelContainer.createSiloModelContainer(rbLandUse);
        setOldLocalModelVariables();



        boolean trackTime = ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_TRACK_TIME, false);
        long[][] timeCounter = new long[EventTypes.values().length + 11][SiloUtil.getEndYear() + 1];
        long startTime = 0;
        IssueCounter.logIssues();           // log any potential issues during initial setup

        if (ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_CREATE_PRESTO_SUMMARY_FILE, false))
            summarizeData.preparePrestoSummary(rbLandUse);

        for (int year = SiloUtil.getStartYear(); year < SiloUtil.getEndYear(); year += SiloUtil.getSimulationLength()) {
            if (SiloUtil.containsElement(scalingYears, year))
                summarizeData.scaleMicroDataToExogenousForecast(rbLandUse, year, householdData);
            logger.info("Simulating changes from year " + year + " to year " + (year + 1));
            IssueCounter.setUpCounter();    // setup issue counter for this simulation period
            SiloUtil.trackingFile("Simulating changes from year " + year + " to year " + (year + 1));
            EventManager em = new EventManager(rbLandUse, householdData, realEstateData);

            if (trackTime) startTime = System.currentTimeMillis();
            iomig.setupInOutMigration(year);
            if (trackTime) timeCounter[EventTypes.values().length][year] += System.currentTimeMillis() - startTime;

            if (trackTime) startTime = System.currentTimeMillis();
            cons.planNewDwellingsForThisComingYear(year, realEstateData);
            if (trackTime) timeCounter[EventTypes.values().length + 1][year] += System.currentTimeMillis() - startTime;

            if (trackTime) startTime = System.currentTimeMillis();
            if (year != SiloUtil.getBaseYear()) {
                updateJobs.updateJobInventoryMultiThreadedThisYear(year);
                jobData.identifyVacantJobs();
            }
            if (trackTime) timeCounter[EventTypes.values().length + 2][year] += System.currentTimeMillis() - startTime;

            if (trackTime) startTime = System.currentTimeMillis();
            householdData.setUpChangeOfJob(year);   // has to run after updateJobInventoryThisYear, as updateJobInventoryThisYear may remove jobs
            if (trackTime) timeCounter[EventTypes.values().length + 3][year] += System.currentTimeMillis() - startTime;

            if (trackTime) startTime = System.currentTimeMillis();
            int numberOfPlannedCouples = mardiv.selectCouplesToGetMarriedThisYear();
            if (trackTime) timeCounter[EventTypes.values().length + 5][year] += System.currentTimeMillis() - startTime;

            if (trackTime) startTime = System.currentTimeMillis();
            em.createListOfEvents(numberOfPlannedCouples);
            if (trackTime) timeCounter[EventTypes.values().length + 4][year] += System.currentTimeMillis() - startTime;

            if (SiloUtil.containsElement(skimYears, year)) {
                if (year != SiloUtil.getStartYear() && !SiloUtil.containsElement(tdmYears, year)) {
                    // skims are always read in start year and in every year the transportation model ran. Additional
                    // years to read skims may be provided in skimYears
                    acc.readSkim(year);
                    acc.calculateAccessibilities(year);
                }
            }

            if (trackTime) startTime = System.currentTimeMillis();
            ddOverwrite.addDwellings(year);
            if (trackTime) timeCounter[EventTypes.values().length + 10][year] += System.currentTimeMillis() - startTime;

            if (trackTime) startTime = System.currentTimeMillis();
            move.calculateRegionalUtilities(year);
            move.calculateAverageHousingSatisfaction();
            if (trackTime) timeCounter[EventTypes.values().length + 6][year] += System.currentTimeMillis() - startTime;

            if (trackTime) startTime = System.currentTimeMillis();
            if (year != SiloUtil.getBaseYear()) householdData.adjustIncome();
            if (trackTime) timeCounter[EventTypes.values().length + 9][year] += System.currentTimeMillis() - startTime;

            if (trackTime) startTime = System.currentTimeMillis();
            if (year == SiloUtil.getBaseYear() || year != SiloUtil.getStartYear()) summarizeMicroData(year, move, realEstateData);
            if (trackTime) timeCounter[EventTypes.values().length + 7][year] += System.currentTimeMillis() - startTime;

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
                    birth.celebrateBirthday(event[1]);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.checkDeath.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    death.chooseDeath(event[1]);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.checkBirth.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    birth.chooseBirth(event[1]);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.checkLeaveParentHh.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    lph.chooseLeaveParentHh(event[1], move, aoModel);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.checkMarriage.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    mardiv.choosePlannedMarriage(event[1], move, iomig, aoModel);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.checkDivorce.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    mardiv.chooseDivorce(event[1], move, aoModel);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.findNewJob.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    changeEmployment.findNewJob(event[1]);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.quitJob.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    changeEmployment.quitJob(event[1]);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.householdMove.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    move.chooseMove(event[1]);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.inmigration.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    iomig.inmigrateHh(event[1], move, changeEmployment, aoModel);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.outMigration.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    iomig.outMigrateHh(event[1], false);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.ddChangeQual.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    renov.checkRenovation(event[1]);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.ddDemolition.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    demol.checkDemolition(event[1], move, iomig);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.ddConstruction.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    cons.buildDwelling(event[1], move, year);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else {
                    logger.warn("Unknown event type: " + event[0]);
                }
            }

            {
                  TransportModelI TransportModel ;
                  // this shadows a global definition, not sure if that is intended ... kai, aug'16

            	final boolean runMatsim = ResourceUtil.getBooleanProperty(rbLandUse,  PROPERTIES_RUN_TRAVEL_MODEL_MATSIM, false );
            	final boolean runTravelDemandModel = ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_RUN_TRAVEL_DEMAND_MODEL, false);
            	final boolean createMstmOutputFiles = ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_CREATE_MSTM_OUTPUT_FILES, true);
            	
            	if ( runMatsim && ( runTravelDemandModel || createMstmOutputFiles ) ) {
            		throw new RuntimeException("trying to run both MATSim and MSTM is inconsistent" ) ;
            	}

            	if ( runMatsim ) {
            		logger.info("using MATSim as transport model") ;
            		TransportModel = new MatsimTransportModel(householdData, acc, rbLandUse, matsimConfig);
            		int nextYearForTransportModel = year + 1;
            		if (SiloUtil.containsElement(tdmYears, nextYearForTransportModel)) {
            			TransportModel.runTransportModel(nextYearForTransportModel);
            		}
            	} else if ( runTravelDemandModel || createMstmOutputFiles ) {
            		TransportModel = new TravelDemandModel(rbLandUse);
            		int nextYearForTransportModel = year + 1;
            		if (SiloUtil.containsElement(tdmYears, nextYearForTransportModel)) {
            			if (runTravelDemandModel)
            				TransportModel.runTransportModel(nextYearForTransportModel);
            			if (createMstmOutputFiles)
            				TransportModel.writeOutSocioEconomicDataForMstm(nextYearForTransportModel);
            			// yyyyyy what is this method good for?  The name of the method tells me something, but then why is it run
            			// _AFTER_ the transport model?  kai, aug'16 -it is just for cube model in maryland - rolf
            		}
            	} 

            }

            if (trackTime) startTime = System.currentTimeMillis();
            prm.updatedRealEstatePrices(year, realEstateData);
            if (trackTime) timeCounter[EventTypes.values().length + 8][year] += System.currentTimeMillis() - startTime;

            EventManager.logEvents();
            IssueCounter.logIssues();           // log any issues that arose during this simulation period

            logger.info("  Finished this simulation period with " + householdData.getNumberOfPersons() +
                    " persons, " + householdData.getNumberOfHouseholds()+" households and "  +
                    Dwelling.getDwellingCount() + " dwellings.");
            if (modelStopper("check")) break;
        }
        if (SiloUtil.containsElement(scalingYears, SiloUtil.getEndYear()))
            summarizeData.scaleMicroDataToExogenousForecast(rbLandUse, SiloUtil.getEndYear(), householdData);

        householdData.summarizeHouseholdsNearMetroStations();

        if (SiloUtil.getEndYear() != 2040) {
            summarizeData.writeOutSyntheticPopulation(rbLandUse, SiloUtil.endYear);
            summarizeData.writeOutDevelopmentCapacityFile(rbLandUse, realEstateData);
        }

        summarizeMicroData(SiloUtil.getEndYear(), move, realEstateData);
        SiloUtil.finish(ddOverwrite);
        modelStopper("removeFile");
        if (trackTime) writeOutTimeTracker(timeCounter);
        logger.info("Scenario results can be found in the directory scenOutput/" + SiloUtil.scenarioName + ".");
    }

    private void setOldLocalModelVariables() {
        // read micro data
        realEstateData = modelContainer.getRealEstateData();
        householdData = modelContainer.getHouseholdData();
        jobData = modelContainer.getJobData();

        death = modelContainer.getDeath();
        birth = modelContainer.getBirth();
        lph = modelContainer.getLph();
        mardiv = modelContainer.getMardiv();
        changeEmployment = modelContainer.getChangeEmployment();
        acc = modelContainer.getAcc();
//        summarizeData.summarizeAutoOwnershipByCounty();

        move = modelContainer.getMove();
        iomig = modelContainer.getIomig();
        cons = modelContainer.getCons();
        renov = modelContainer.getRenov();
        demol = modelContainer.getDemol();
        prm = modelContainer.getPrm();
        updateJobs = modelContainer.getUpdateJobs();
        aoModel = modelContainer.getAoModel();
        ddOverwrite = modelContainer.getDdOverwrite();
    }


    public void initialize() {
        // initial steps that only need to performed once to set up the model

        // define years to simulate
        scalingYears = ResourceUtil.getIntegerArray(rbLandUse, PROPERTIES_SCALING_YEARS);
        if (scalingYears[0] != -1) summarizeData.readScalingYearControlTotals(rbLandUse);
        currentYear = SiloUtil.getStartYear();
        tdmYears = ResourceUtil.getIntegerArray(rbLandUse, PROPERTIES_TRANSPORT_MODEL_YEARS);
        skimYears = ResourceUtil.getIntegerArray(rbLandUse, PROPERTIES_TRANSPORT_SKIM_YEARS);

        // read micro data
        modelContainer = SiloModelContainer.createSiloModelContainer(rbLandUse);
        setOldLocalModelVariables();

        trackTime = ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_TRACK_TIME, false);
        timeCounter = new long[EventTypes.values().length + 11][SiloUtil.getEndYear() + 1];
        IssueCounter.logIssues();           // log any potential issues during initial setup

        TransportModel = new TravelDemandModel(rbLandUse);
        if (ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_CREATE_PRESTO_SUMMARY_FILE, false))
            summarizeData.preparePrestoSummary(rbLandUse);

    }


    public void runYear (double dt) {
        // run single simulation period

        if (dt != 1) {
            logger.error("SILO is not prepared to simulate other interval than 1 year. Invalid interval: " + dt);
            System.exit(1);
        }
        if (SiloUtil.containsElement(scalingYears, currentYear))
            summarizeData.scaleMicroDataToExogenousForecast(rbLandUse, currentYear, householdData);
        logger.info("Simulating changes from year " + currentYear + " to year " + (currentYear + 1));
        IssueCounter.setUpCounter();    // setup issue counter for this simulation period
        SiloUtil.trackingFile("Simulating changes from year " + currentYear + " to year " + (currentYear + 1));
        EventManager em = new EventManager(rbLandUse, householdData, realEstateData);
        long startTime = 0;
        if (trackTime) startTime = System.currentTimeMillis();
        iomig.setupInOutMigration(currentYear);
        if (trackTime) timeCounter[EventTypes.values().length][currentYear] += System.currentTimeMillis() - startTime;

        if (trackTime) startTime = System.currentTimeMillis();
        cons.planNewDwellingsForThisComingYear(currentYear, realEstateData);
        if (trackTime) timeCounter[EventTypes.values().length + 1][currentYear] += System.currentTimeMillis() - startTime;

        if (trackTime) startTime = System.currentTimeMillis();
        if (currentYear != SiloUtil.getBaseYear()) {
            updateJobs.updateJobInventoryMultiThreadedThisYear(currentYear);
            jobData.identifyVacantJobs();
        }
        if (trackTime) timeCounter[EventTypes.values().length + 2][currentYear] += System.currentTimeMillis() - startTime;

        if (trackTime) startTime = System.currentTimeMillis();
        householdData.setUpChangeOfJob(currentYear);   // has to run after updateJobInventoryThisYear, as updateJobInventoryThisYear may remove jobs
        if (trackTime) timeCounter[EventTypes.values().length + 3][currentYear] += System.currentTimeMillis() - startTime;

        if (trackTime) startTime = System.currentTimeMillis();
        int numberOfPlannedCouples = mardiv.selectCouplesToGetMarriedThisYear();
        if (trackTime) timeCounter[EventTypes.values().length + 5][currentYear] += System.currentTimeMillis() - startTime;

        if (trackTime) startTime = System.currentTimeMillis();
        em.createListOfEvents(numberOfPlannedCouples);
        if (trackTime) timeCounter[EventTypes.values().length + 4][currentYear] += System.currentTimeMillis() - startTime;

        if (SiloUtil.containsElement(skimYears, currentYear)) {
            if (currentYear != SiloUtil.getStartYear() && !SiloUtil.containsElement(tdmYears, currentYear)) {
                // skims are always read in start year and in every year the transportation model ran. Additional
                // years to read skims may be provided in skimYears
                acc.readSkim(currentYear);
                acc.calculateAccessibilities(currentYear);
            }
        }

        if (trackTime) startTime = System.currentTimeMillis();
        ddOverwrite.addDwellings(currentYear);
        if (trackTime) timeCounter[EventTypes.values().length + 10][currentYear] += System.currentTimeMillis() - startTime;

        if (trackTime) startTime = System.currentTimeMillis();
        move.calculateRegionalUtilities(currentYear);
        move.calculateAverageHousingSatisfaction();
        if (trackTime) timeCounter[EventTypes.values().length + 6][currentYear] += System.currentTimeMillis() - startTime;

        if (trackTime) startTime = System.currentTimeMillis();
        if (currentYear != SiloUtil.getBaseYear()) householdData.adjustIncome();
        if (trackTime) timeCounter[EventTypes.values().length + 9][currentYear] += System.currentTimeMillis() - startTime;

        if (trackTime) startTime = System.currentTimeMillis();
        if (currentYear == SiloUtil.getBaseYear() || currentYear != SiloUtil.getStartYear())
            summarizeMicroData(currentYear, move, realEstateData);
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
                birth.celebrateBirthday(event[1]);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.checkDeath.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                death.chooseDeath(event[1]);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.checkBirth.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                birth.chooseBirth(event[1]);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.checkLeaveParentHh.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                lph.chooseLeaveParentHh(event[1], move, aoModel);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.checkMarriage.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                mardiv.choosePlannedMarriage(event[1], move, iomig, aoModel);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.checkDivorce.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                mardiv.chooseDivorce(event[1], move, aoModel);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.findNewJob.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                changeEmployment.findNewJob(event[1]);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.quitJob.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                changeEmployment.quitJob(event[1]);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.householdMove.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                move.chooseMove(event[1]);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.inmigration.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                iomig.inmigrateHh(event[1], move, changeEmployment, aoModel);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.outMigration.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                iomig.outMigrateHh(event[1], false);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.ddChangeQual.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                renov.checkRenovation(event[1]);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.ddDemolition.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                demol.checkDemolition(event[1], move, iomig);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.ddConstruction.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                cons.buildDwelling(event[1], move, currentYear);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else {
                logger.warn("Unknown event type: " + event[0]);
            }
        }

        int nextYearForTransportModel = currentYear + 1;
        if (SiloUtil.containsElement(tdmYears, nextYearForTransportModel)) {
            if (ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_RUN_TRAVEL_DEMAND_MODEL, false))
                TransportModel.runTransportModel(nextYearForTransportModel);
            if (ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_CREATE_MSTM_OUTPUT_FILES, true))
                TransportModel.writeOutSocioEconomicDataForMstm(nextYearForTransportModel);
        }

        if (trackTime) startTime = System.currentTimeMillis();
        prm.updatedRealEstatePrices(currentYear, realEstateData);
        if (trackTime) timeCounter[EventTypes.values().length + 8][currentYear] += System.currentTimeMillis() - startTime;

        EventManager.logEvents();
        IssueCounter.logIssues();           // log any issues that arose during this simulation period

        logger.info("  Finished this simulation period with " + householdData.getNumberOfPersons() +
                " persons, " + householdData.getNumberOfHouseholds()+" households and "  +
                Dwelling.getDwellingCount() + " dwellings.");
        currentYear++;
        if (modelStopper("check")) finishModel();
    }


    public void finishModel () {
        // close model run

        //Writes summarize data in 2 files, the normal combined file & a special file with only last year's data
        summarizeData.resultWriterReplicate = true;

        if (SiloUtil.containsElement(scalingYears, SiloUtil.getEndYear()))
            summarizeData.scaleMicroDataToExogenousForecast(rbLandUse, SiloUtil.getEndYear(), householdData);

        householdData.summarizeHouseholdsNearMetroStations();

        if (SiloUtil.getEndYear() != 2040) {
            summarizeData.writeOutSyntheticPopulation(rbLandUse, SiloUtil.endYear);
            summarizeData.writeOutDevelopmentCapacityFile(rbLandUse, realEstateData);
        }

        summarizeMicroData(SiloUtil.getEndYear(), move, realEstateData);
        SiloUtil.finish(ddOverwrite);
        modelStopper("removeFile");
        
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
        
        if (trackTime) writeOutTimeTracker(timeCounter);
        logger.info("Scenario results can be found in the directory scenOutput/" + SiloUtil.scenarioName + ".");
    }


    public void closeAllFiles (long startTime) {
        // run this method whenever SILO closes, regardless of whether SILO completed successfully or SILO crashed
        SiloUtil.trackingFile("close");
        summarizeData.resultFile("close");
        summarizeData.resultFileSpatial(rbLandUse, "close");
        float endTime = SiloUtil.rounder(((System.currentTimeMillis() - startTime) / 60000), 1);
        int hours = (int) (endTime / 60);
        int min = (int) (endTime - 60 * hours);
        logger.info("Runtime: " + hours + " hours and " + min + " minutes.");
        if (ResourceUtil.getBooleanProperty(rbLandUse, SiloModel.PROPERTIES_TRACK_TIME, false)) {
            String fileName = rbLandUse.getString(SiloModel.PROPERTIES_TRACK_TIME_FILE);
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
                out.println("Runtime: " + hours + " hours and " + min + " minutes.");
                out.close();
            } catch (IOException e) {
                logger.warn("Could not add run-time statement to time-tracking file.");
            }
        }
    }


    private boolean modelStopper (String action) {
        // provide option for a clean model stop after every simulation period is completed
        String fileName = SiloUtil.baseDirectory + "status.csv";
        if (action.equalsIgnoreCase("initialize")) {
            PrintWriter pw = SiloUtil.openFileForSequentialWriting(fileName, false);
            pw.println("Status");
            pw.println("continue");
            pw.close();
        } else if (action.equalsIgnoreCase("removeFile")) {
            SiloUtil.deleteFile (fileName);
        } else {
            TableDataSet status = SiloUtil.readCSVfile(fileName);
            if (!status.getStringValueAt(1, "Status").equalsIgnoreCase("continue")) return true;
        }
        return false;
    }


    public void summarizeMicroData (int year, MovesModel move, RealEstateDataManager realEstateData) {
        // aggregate micro data

        if (SiloUtil.trackHh != -1 || SiloUtil.trackPp != -1 || SiloUtil.trackDd != -1)
            SiloUtil.trackWriter.println("Started simulation for year " + year);
        logger.info("  Summarizing micro data for year " + year);


        summarizeData.resultFile("Year " + year, false);
        HouseholdDataManager.summarizePopulation();
        RealEstateDataManager.summarizeDwellings(realEstateData);
        JobDataManager.summarizeJobs();

        summarizeData.resultFileSpatial(rbLandUse, "Year " + year, false);
        summarizeData.summarizeSpatially(year, move, realEstateData);
        if (ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_CREATE_CBLCM_FILES, false))
            summarizeDataCblcm.createCblcmSummaries(rbLandUse, year);
        if (ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_CREATE_HOUSING_ENV_IMPACT_FILE, false))
            summarizeData.summarizeHousing(rbLandUse, year);
        if (ResourceUtil.getBooleanProperty(rbLandUse, PROPERTIES_CREATE_PRESTO_SUMMARY_FILE, false)) {
            summarizeData.summarizePrestoRegion(rbLandUse, year);
        }

    }

    private void writeOutTimeTracker (long[][] timeCounter) {
        // write file summarizing run times

        int startYear = SiloUtil.getStartYear();
        PrintWriter pw = SiloUtil.openFileForSequentialWriting(rbLandUse.getString(PROPERTIES_TRACK_TIME_FILE), startYear != SiloUtil.getBaseYear());
        if (startYear == SiloUtil.getBaseYear()) {
            pw.print("Year");
            for (EventTypes et : EventTypes.values()) pw.print("," + et.toString());
            pw.print(",setupInOutMigration,setupConstructionOfNewDwellings,updateJobInventory,setupJobChange," +
                    "setupListOfEvents,fillMarriageMarket,calcAveHousingSatisfaction,summarizeData,updateRealEstatePrices," +
                    "planIncomeChange,addOverwriteDwellings");
            pw.println();
        }
        for (int year = startYear; year < SiloUtil.getEndYear(); year += SiloUtil.getSimulationLength()) {
            pw.print(year);
            for (EventTypes et: EventTypes.values()) {
                float timeInMinutes = timeCounter[et.ordinal()][year] / 60000f;
                pw.print("," + timeInMinutes);
            }
            pw.print("," + timeCounter[EventTypes.values().length][year] / 60000f);       // setup inmigration/outmigration
            pw.print("," + timeCounter[EventTypes.values().length + 1][year] / 60000f);   // setup construction of new dwellings
            pw.print("," + timeCounter[EventTypes.values().length + 2][year] / 60000f);   // update job inventory
            pw.print("," + timeCounter[EventTypes.values().length + 3][year] / 60000f);   // setup job change model
            pw.print("," + timeCounter[EventTypes.values().length + 4][year] / 60000f);   // setup list of events
            pw.print("," + timeCounter[EventTypes.values().length + 5][year] / 60000f);   // fill marriage market
            pw.print("," + timeCounter[EventTypes.values().length + 6][year] / 60000f);   // calculate average housing satisfaction
            pw.print("," + timeCounter[EventTypes.values().length + 7][year] / 60000f);   // summarize data
            pw.print("," + timeCounter[EventTypes.values().length + 8][year] / 60000f);   // update real estate prices
            pw.print("," + timeCounter[EventTypes.values().length + 9][year] / 60000f);   // plan income change
            pw.print("," + timeCounter[EventTypes.values().length + 10][year] / 60000f);  // add dwellings from overwrite
            pw.println();
        }
        pw.close();
    }


public void setMatsimConfig(Config matsimConfig) {
	this.matsimConfig=matsimConfig ;
}


//    private void summarizeRentAndIncome () {
//        PrintWriter pw = SiloUtil.openFileForSequentialWriting("temp.csv", false);
//        pw.println("income,rent");
//        for (Household hh: Household.getHouseholdArray()) {
//            pw.println(hh.getHhIncome() + "," + Dwelling.getDwellingFromId(hh.getDwellingId()).getPrice());
//        }
//        pw.close();
//    }
}

