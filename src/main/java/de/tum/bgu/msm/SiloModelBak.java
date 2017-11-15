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
 *//*

package de.tum.bgu.msm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.ResourceBundle;

import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.jobmography.UpdateJobs;
import de.tum.bgu.msm.realEstate.RenovationModel;
import de.tum.bgu.msm.relocation.InOutMigration;
import de.tum.bgu.msm.relocation.MovesModelMstm;
import de.tum.bgu.msm.transportModel.MatsimTransportModel;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;

import de.tum.bgu.msm.autoOwnership.MaryLandCarOwnershipModel;
import de.tum.bgu.msm.demography.BirthModel;
import de.tum.bgu.msm.demography.ChangeEmploymentModel;
import de.tum.bgu.msm.demography.DeathModel;
import de.tum.bgu.msm.demography.LeaveParentHhModel;
import de.tum.bgu.msm.demography.MarryDivorceModel;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.realEstate.ConstructionModel;
import de.tum.bgu.msm.realEstate.ConstructionOverwrite;
import de.tum.bgu.msm.realEstate.DemolitionModel;
import de.tum.bgu.msm.realEstate.PricingModel;
import de.tum.bgu.msm.transportModel.TransportModelI;
import de.tum.bgu.msm.transportModel.MstmTransportModel;

*/
/**
 * @author Greg Erhardt 
 * Created on Dec 2, 2009
 *
 *//*

public class SiloModelBak {
    static Logger logger = Logger.getLogger(SiloModelBak.class);

    public ResourceBundle rb;
    public static Random rand;

    protected static final String RUN_SILO                       = "run.silo.model";
    protected static final String PROPERTIES_SCALING_YEARS                  = "scaling.years";
    protected static final String PROPERTIES_TRANSPORT_MODEL_YEARS          = "transport.model.years";
    protected static final String PROPERTIES_TRANSPORT_SKIM_YEARS           = "skim.years";
    public static final String TRACK_TIME                        = "track.time";
    public static final String PROPERTIES_TRACK_TIME_FILE                   = "track.time.file";
    protected static final String PROPERTIES_CREATE_CBLCM_FILES             = "create.cblcm.files";
    protected static final String PROPERTIES_CREATE_HOUSING_ENV_IMPACT_FILE = "create.housing.environm.impact.files";
    protected static final String PROPERTIES_CREATE_PRESTO_SUMMARY_FILE     = "create.presto.summary.file";

    protected static final String PROPERTIES_TRANSPORT_MODEL	           = "transport.model";
    
    private Config matsimConfig = null;
    
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
    private MovesModelMstm move;
    private ChangeEmploymentModel changeEmployment;
    private Accessibility accessibility;
    private MaryLandCarOwnershipModel aoModel;
    private MstmTransportModel TransportModel;
    private UpdateJobs updateJobs;
    private int[] skimYears;
    private int[] tdmYears;
    private boolean trackTime;
    private long[][] timeCounter;
    private geoDataI geoData;

    */
/**
     * Constructor to set up a SILO model
     * @param rb ResourceBundle
     *//*

    public SiloModelBak(ResourceBundle rb) {
        this.rb = rb;
        summarizeData.openResultFile(rb);
        summarizeData.resultFileSpatial(rb, "open");
        IssueCounter.setUpCounter();   // set up counter for any issues during initial setup
        modelStopper("initialize");
    }


    public void runModel() {
        //Main method to run a SILO model

        if (!ResourceUtil.getBooleanProperty(rb, RUN_SILO)) return;

        logger.info("Start SILO Model");

        // define years to simulate
        int[] scalingYears = ResourceUtil.getIntegerArray(rb, PROPERTIES_SCALING_YEARS);
        if (scalingYears[0] != -1) summarizeData.readScalingYearControlTotals(rb);
        int[] tdmYears = ResourceUtil.getIntegerArray(rb, PROPERTIES_TRANSPORT_MODEL_YEARS);
        int[] skimYears = ResourceUtil.getIntegerArray(rb, PROPERTIES_TRANSPORT_SKIM_YEARS);

        // read micro data
        RealEstateDataManager realEstateData = new RealEstateDataManager(rb, geoData);
        HouseholdDataManager householdData = new HouseholdDataManager(rb, realEstateData);
        JobDataManager jobData = new JobDataManager(rb, geoData);
        if (!ResourceUtil.getBooleanProperty(rb, "run.synth.pop.generator")) {   // read data only if synth. pop. generator did not run
            householdData.readPopulation();
            realEstateData.readDwellings();
            jobData.readJobs();
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

        logger.info("Creating UEC Models");
        DeathModel death = new DeathModel(rb);
        BirthModel birth = new BirthModel(rb);
        LeaveParentHhModel lph = new LeaveParentHhModel(rb);
        MarryDivorceModel mardiv = new MarryDivorceModel(rb);
        ChangeEmploymentModel changeEmployment = new ChangeEmploymentModel(geoData);
        Accessibility accessibility = new Accessibility(rb, SiloUtil.getStartYear(), geoData);
        //summarizeData.summarizeAutoOwnershipByCounty(accessibility, jobData);

        MovesModelMstm move = new MovesModelMstm(rb, geoData);
        InOutMigration iomig = new InOutMigration(rb);
        ConstructionModel cons = new ConstructionModel(rb, geoData);
        RenovationModel renov = new RenovationModel(rb);
        DemolitionModel demol = new DemolitionModel(rb);
        PricingModel prm = new PricingModel(rb);
        UpdateJobs updateJobs = new UpdateJobs(rb);
        MaryLandCarOwnershipModel aoModel = new MaryLandCarOwnershipModel(rb);
        ConstructionOverwrite ddOverwrite = new ConstructionOverwrite(rb);

        boolean trackTime = ResourceUtil.getBooleanProperty(rb, TRACK_TIME, false);
        long[][] timeCounter = new long[EventTypes.values().length + 11][SiloUtil.getEndYear() + 1];
        long startTime = 0;
        IssueCounter.logIssues(geoData);           // log any potential issues during initial setup

        TransportModelI TransportModel;
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_CREATE_PRESTO_SUMMARY_FILE, false))
            summarizeData.preparePrestoSummary(rb, geoData);

        for (int year = SiloUtil.getStartYear(); year < SiloUtil.getEndYear(); year += SiloUtil.getSimulationLength()) {
            if (SiloUtil.containsElement(scalingYears, year))
                summarizeData.scaleMicroDataToExogenousForecast(rb, year, householdData, geoData);
            logger.info("Simulating changes from year " + year + " to year " + (year + 1));
            IssueCounter.setUpCounter();    // setup issue counter for this simulation period
            SiloUtil.trackingFile("Simulating changes from year " + year + " to year " + (year + 1));
            EventManager em = new EventManager(rb, householdData, realEstateData);

            if (trackTime) startTime = System.currentTimeMillis();
            iomig.setupInOutMigration(year);
            if (trackTime) timeCounter[EventTypes.values().length][year] += System.currentTimeMillis() - startTime;

            if (trackTime) startTime = System.currentTimeMillis();
            cons.planNewDwellingsForThisComingYear(year, realEstateData, accessibility);
            if (trackTime) timeCounter[EventTypes.values().length + 1][year] += System.currentTimeMillis() - startTime;

            if (trackTime) startTime = System.currentTimeMillis();
            if (year != SiloUtil.getBaseYear()) {
                updateJobs.updateJobInventoryMultiThreadedThisYear(year, jobData, geoData);
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
                    accessibility.readSkim(year);
                    accessibility.calculateAccessibilities(year);
                }
            }

            if (trackTime) startTime = System.currentTimeMillis();
            ddOverwrite.addDwellings(year, realEstateData);
            if (trackTime) timeCounter[EventTypes.values().length + 10][year] += System.currentTimeMillis() - startTime;

            if (trackTime) startTime = System.currentTimeMillis();
            move.calculateRegionalUtilities(year);
            move.calculateAverageHousingSatisfaction(accessibility);
            if (trackTime) timeCounter[EventTypes.values().length + 6][year] += System.currentTimeMillis() - startTime;

            if (trackTime) startTime = System.currentTimeMillis();
            if (year != SiloUtil.getBaseYear()) householdData.adjustIncome();
            if (trackTime) timeCounter[EventTypes.values().length + 9][year] += System.currentTimeMillis() - startTime;

            if (trackTime) startTime = System.currentTimeMillis();
            if (year == SiloUtil.getBaseYear() || year != SiloUtil.getStartYear()) summarizeMicroData(year, move, realEstateData, jobData);
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
                    death.chooseDeath(event[1], jobData, householdData);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.checkBirth.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    birth.chooseBirth(event[1]);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.checkLeaveParentHh.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    lph.chooseLeaveParentHh(event[1], move, aoModel, realEstateData, accessibility, jobData, householdData);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.checkMarriage.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    mardiv.choosePlannedMarriage(event[1], move, iomig, aoModel, realEstateData, accessibility, householdData, jobData);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.checkDivorce.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    mardiv.chooseDivorce(event[1], move, aoModel, realEstateData, accessibility, jobData, householdData);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.findNewJob.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    changeEmployment.findNewJob(event[1]);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.quitJob.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    changeEmployment.quitJob(event[1], jobData);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.householdMove.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    move.chooseMove(event[1], realEstateData, accessibility);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.inmigration.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    iomig.inmigrateHh(event[1], move, changeEmployment, aoModel, realEstateData, householdData,
                            accessibility, jobData);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.outMigration.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    iomig.outMigrateHh(event[1], false, householdData, jobData);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.ddChangeQual.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    renov.checkRenovation(event[1]);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.ddDemolition.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    demol.checkDemolition(event[1], move, iomig, realEstateData, accessibility, householdData,
                            jobData);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else if (event[0] == EventTypes.ddConstruction.ordinal()) {
                    if (trackTime) startTime = System.currentTimeMillis();
                    cons.buildDwelling(event[1], move, year, realEstateData, accessibility);
                    if (trackTime) timeCounter[event[0]][year] += System.currentTimeMillis() - startTime;
                } else {
                    logger.warn("Unknown event type: " + event[0]);
                }
            }
            
            if (true) {
            	TransportModel = new MatsimTransportModel(householdData, accessibility, rb, matsimConfig);
            }
//            if (ResourceUtil.getProperty(rb, PROPERTIES_TRANSPORT_MODEL) == "MATSim") {
//            	TransportModel = new MatsimTransportModel(householdData, accessibility, rb, matsimConfig);
//            } else if (ResourceUtil.getProperty(rb, PROPERTIES_TRANSPORT_MODEL) == "MSTM") {
//            	TransportModel = new transportModel(rb);
//            } else {
//            	throw new IllegalArgumentException("Not implemented for transport models other than MSTM or MATSim.");
//            }
            
            int nextYearForTransportModel = year + 1;
            if (SiloUtil.containsElement(tdmYears, nextYearForTransportModel)) {
                TransportModel.runTransportModel(nextYearForTransportModel);
            }

            if (trackTime) startTime = System.currentTimeMillis();
            prm.updatedRealEstatePrices(year, realEstateData, geoData);
            if (trackTime) timeCounter[EventTypes.values().length + 8][year] += System.currentTimeMillis() - startTime;

            EventManager.logEvents();
            IssueCounter.logIssues(geoData);           // log any issues that arose during this simulation period

            logger.info("  Finished this simulation period with " + householdData.getNumberOfPersons() +
                    " persons, " + householdData.getNumberOfHouseholds()+" households and "  +
                    Dwelling.getDwellingCount() + " dwellings.");
            if (modelStopper("check")) break;
        }
        if (SiloUtil.containsElement(scalingYears, SiloUtil.getEndYear()))
            summarizeData.scaleMicroDataToExogenousForecast(rb, SiloUtil.getEndYear(), householdData, geoData);

        householdData.summarizeHouseholdsNearMetroStations();

        if (SiloUtil.getEndYear() != 2040) summarizeData.writeOutSyntheticPopulation(rb, SiloUtil.getEndYear());

        summarizeMicroData(SiloUtil.getEndYear(), move, realEstateData, jobData);
        SiloUtil.finish(ddOverwrite);
        modelStopper("removeFile");
        if (trackTime) writeOutTimeTracker(timeCounter);
        logger.info("Scenario results can be found in the directory scenOutput/" + SiloUtil.scenarioName + ".");
    }


    public void initialize() {
        // initial steps that only need to performed once to set up the model

        // define years to simulate
        scalingYears = ResourceUtil.getIntegerArray(rb, PROPERTIES_SCALING_YEARS);
        if (scalingYears[0] != -1) summarizeData.readScalingYearControlTotals(rb);
        currentYear = SiloUtil.getStartYear();
        tdmYears = ResourceUtil.getIntegerArray(rb, PROPERTIES_TRANSPORT_MODEL_YEARS);
        skimYears = ResourceUtil.getIntegerArray(rb, PROPERTIES_TRANSPORT_SKIM_YEARS);

        // read micro data
        realEstateData = new RealEstateDataManager(rb, geoData);
        householdData = new HouseholdDataManager(rb, realEstateData);
        jobData = new JobDataManager(rb, geoData);
        if (!ResourceUtil.getBooleanProperty(rb, "run.synth.pop.generator")) {   // read data only if synth. pop. generator did not run
            householdData.readPopulation();
            realEstateData.readDwellings();
            jobData.readJobs();
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

        logger.info("Creating UEC Models");
        death = new DeathModel(rb);
        birth = new BirthModel(rb);
        lph = new LeaveParentHhModel(rb);
        mardiv = new MarryDivorceModel(rb);
        changeEmployment = new ChangeEmploymentModel(geoData);
        accessibility = new Accessibility(rb, SiloUtil.getStartYear(), geoData);
        //summarizeData.summarizeAutoOwnershipByCounty(accessibility, jobData);

        move = new MovesModelMstm(rb, geoData);
        iomig = new InOutMigration(rb);
        cons = new ConstructionModel(rb, geoData);
        renov = new RenovationModel(rb);
        demol = new DemolitionModel(rb);
        prm = new PricingModel(rb);
        updateJobs = new UpdateJobs(rb);
        aoModel = new MaryLandCarOwnershipModel(rb);
        ddOverwrite = new ConstructionOverwrite(rb);

        trackTime = ResourceUtil.getBooleanProperty(rb, TRACK_TIME, false);
        timeCounter = new long[EventTypes.values().length + 11][SiloUtil.getEndYear() + 1];
        IssueCounter.logIssues(geoData);           // log any potential issues during initial setup

        TransportModel = new MstmTransportModel(rb, geoData);
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_CREATE_PRESTO_SUMMARY_FILE, false))
            summarizeData.preparePrestoSummary(rb, geoData);

    }


    public void runYear (double dt) {
        // run single simulation period

        if (dt != 1) {
            logger.error("SILO is not prepared to simulate other interval than 1 year. Invalid interval: " + dt);
            System.exit(1);
        }
        if (SiloUtil.containsElement(scalingYears, currentYear))
            summarizeData.scaleMicroDataToExogenousForecast(rb, currentYear, householdData, geoData);
        logger.info("Simulating changes from year " + currentYear + " to year " + (currentYear + 1));
        IssueCounter.setUpCounter();    // setup issue counter for this simulation period
        SiloUtil.trackingFile("Simulating changes from year " + currentYear + " to year " + (currentYear + 1));
        EventManager em = new EventManager(rb, householdData, realEstateData);
        long startTime = 0;
        if (trackTime) startTime = System.currentTimeMillis();
        iomig.setupInOutMigration(currentYear);
        if (trackTime) timeCounter[EventTypes.values().length][currentYear] += System.currentTimeMillis() - startTime;

        if (trackTime) startTime = System.currentTimeMillis();
        cons.planNewDwellingsForThisComingYear(currentYear, realEstateData, accessibility);
        if (trackTime) timeCounter[EventTypes.values().length + 1][currentYear] += System.currentTimeMillis() - startTime;

        if (trackTime) startTime = System.currentTimeMillis();
        if (currentYear != SiloUtil.getBaseYear()) {
            updateJobs.updateJobInventoryMultiThreadedThisYear(currentYear, jobData, geoData);
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
                accessibility.readSkim(currentYear);
                accessibility.calculateAccessibilities(currentYear);
            }
        }

        if (trackTime) startTime = System.currentTimeMillis();
        ddOverwrite.addDwellings(currentYear, realEstateData);
        if (trackTime) timeCounter[EventTypes.values().length + 10][currentYear] += System.currentTimeMillis() - startTime;

        if (trackTime) startTime = System.currentTimeMillis();
        move.calculateRegionalUtilities(currentYear);
        move.calculateAverageHousingSatisfaction(accessibility);
        if (trackTime) timeCounter[EventTypes.values().length + 6][currentYear] += System.currentTimeMillis() - startTime;

        if (trackTime) startTime = System.currentTimeMillis();
        if (currentYear != SiloUtil.getBaseYear()) householdData.adjustIncome();
        if (trackTime) timeCounter[EventTypes.values().length + 9][currentYear] += System.currentTimeMillis() - startTime;

        if (trackTime) startTime = System.currentTimeMillis();
        if (currentYear == SiloUtil.getBaseYear() || currentYear != SiloUtil.getStartYear())
            summarizeMicroData(currentYear, move, realEstateData, jobData);
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
                death.chooseDeath(event[1], jobData, householdData);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.checkBirth.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                birth.chooseBirth(event[1]);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.checkLeaveParentHh.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                lph.chooseLeaveParentHh(event[1], move, aoModel, realEstateData, accessibility, jobData, householdData);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.checkMarriage.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                mardiv.choosePlannedMarriage(event[1], move, iomig, aoModel,realEstateData, accessibility,
                        householdData, jobData);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.checkDivorce.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                mardiv.chooseDivorce(event[1], move, aoModel, realEstateData, accessibility, jobData, householdData);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.findNewJob.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                changeEmployment.findNewJob(event[1]);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.quitJob.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                changeEmployment.quitJob(event[1], jobData);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.householdMove.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                move.chooseMove(event[1], realEstateData, accessibility);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.inmigration.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                iomig.inmigrateHh(event[1], move, changeEmployment, aoModel, realEstateData, householdData,
                        accessibility, jobData);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.outMigration.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                iomig.outMigrateHh(event[1], false, householdData, jobData);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.ddChangeQual.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                renov.checkRenovation(event[1]);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.ddDemolition.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                demol.checkDemolition(event[1], move, iomig, realEstateData, accessibility, householdData,
                        jobData);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else if (event[0] == EventTypes.ddConstruction.ordinal()) {
                if (trackTime) startTime = System.currentTimeMillis();
                cons.buildDwelling(event[1], move, currentYear, realEstateData, accessibility);
                if (trackTime) timeCounter[event[0]][currentYear] += System.currentTimeMillis() - startTime;
            } else {
                logger.warn("Unknown event type: " + event[0]);
            }
        }

        int nextYearForTransportModel = currentYear + 1;
        if (SiloUtil.containsElement(tdmYears, nextYearForTransportModel)) {
            TransportModel.runTransportModel(nextYearForTransportModel);
        }

        if (trackTime) startTime = System.currentTimeMillis();
        prm.updatedRealEstatePrices(currentYear, realEstateData, geoData);
        if (trackTime) timeCounter[EventTypes.values().length + 8][currentYear] += System.currentTimeMillis() - startTime;

        EventManager.logEvents();
        IssueCounter.logIssues(geoData);           // log any issues that arose during this simulation period

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
            summarizeData.scaleMicroDataToExogenousForecast(rb, SiloUtil.getEndYear(), householdData, geoData);

        householdData.summarizeHouseholdsNearMetroStations();

        if (SiloUtil.getEndYear() != 2040) summarizeData.writeOutSyntheticPopulation(rb, SiloUtil.getEndYear());

        summarizeMicroData(SiloUtil.getEndYear(), move, realEstateData, jobData);
        SiloUtil.finish(ddOverwrite);
        modelStopper("removeFile");
        if (trackTime) writeOutTimeTracker(timeCounter);
        logger.info("Scenario results can be found in the directory scenOutput/" + SiloUtil.scenarioName + ".");
    }


    public void closeAllFiles (long startTime) {
        // run this method whenever SILO closes, regardless of whether SILO completed successfully or SILO crashed
        SiloUtil.trackingFile("close");
        summarizeData.resultFile("close");
        summarizeData.resultFileSpatial(rb, "close");
        float endTime = SiloUtil.rounder(((System.currentTimeMillis() - startTime) / 60000), 1);
        int hours = (int) (endTime / 60);
        int min = (int) (endTime - 60 * hours);
        logger.info("Runtime: " + hours + " hours and " + min + " minutes.");
        if (ResourceUtil.getBooleanProperty(rb, SiloModel.TRACK_TIME, false)) {
            String fileName = rb.getString(SiloModel.PROPERTIES_TRACK_TIME_FILE);
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


    public void summarizeMicroData (int year, MovesModelMstm move, RealEstateDataManager realEstateData,
                                    JobDataManager jobData) {
        // aggregate micro data

        if (SiloUtil.trackHh != -1 || SiloUtil.trackPp != -1 || SiloUtil.trackDd != -1)
            SiloUtil.trackWriter.println("Started simulation for year " + year);
        logger.info("  Summarizing micro data for year " + year);


        summarizeData.resultFile("Year " + year, false);
        HouseholdDataManager.summarizePopulation(geoData);
        realEstateData.summarizeDwellings();
        jobData.summarizeJobs(geoData.getRegionList());

        summarizeData.resultFileSpatial(rb, "Year " + year, false);
        summarizeData.summarizeSpatially(year, move, realEstateData, geoData, accessibility);
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_CREATE_CBLCM_FILES, false))
            summarizeDataCblcm.createCblcmSummaries(rb, year, geoData, accessibility);
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_CREATE_HOUSING_ENV_IMPACT_FILE, false))
            summarizeData.summarizeHousing(rb, year);
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_CREATE_PRESTO_SUMMARY_FILE, false)) {
            summarizeData.summarizePrestoRegion(rb, year);
        }

    }


    private void writeOutTimeTracker (long[][] timeCounter) {
        // write file summarizing run times

        int startYear = SiloUtil.getStartYear();
        PrintWriter pw = SiloUtil.openFileForSequentialWriting(rb.getString(PROPERTIES_TRACK_TIME_FILE), startYear != SiloUtil.getBaseYear());
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

//    private void summarizeRentAndIncome () {
//        PrintWriter pw = SiloUtil.openFileForSequentialWriting("temp.csv", false);
//        pw.println("income,rent");
//        for (Household hh: Household.getHouseholdArray()) {
//            pw.println(hh.getHhIncome() + "," + Dwelling.getDwellingFromId(hh.getDwellingId()).getPrice());
//        }
//        pw.close();
//    }
    
    public void setMatsimConfig (Config matsimConfig) {
	    this.matsimConfig = matsimConfig;
    }
}*/
