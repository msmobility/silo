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

import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.events.MicroSimulation;
import de.tum.bgu.msm.events.impls.MarriageEvent;
import de.tum.bgu.msm.events.impls.household.MigrationEvent;
import de.tum.bgu.msm.events.impls.household.MoveEvent;
import de.tum.bgu.msm.events.impls.person.*;
import de.tum.bgu.msm.events.impls.realEstate.ConstructionEvent;
import de.tum.bgu.msm.events.impls.realEstate.DemolitionEvent;
import de.tum.bgu.msm.events.impls.realEstate.RenovationEvent;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SkimUtil;
import de.tum.bgu.msm.utils.TimeTracker;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Greg Erhardt
 * Created on Dec 2, 2009
 */
public final class SiloModel {

	private final static Logger logger = Logger.getLogger(SiloModel.class);

	private final Set<Integer> tdmYears = new HashSet<>();
	private final Set<Integer> skimYears = new HashSet<>();
    private final Set<Integer> scalingYears = new HashSet<>();

	private SiloModelContainer modelContainer;
	private SiloDataContainer dataContainer;
	private final Config matsimConfig;
    private MicroSimulation microSim;
    private TimeTracker timeTracker = new TimeTracker();

    public SiloModel() {
		this(null) ;
	}

	public SiloModel(Config matsimConfig) {
		IssueCounter.setUpCounter();
		SiloUtil.modelStopper("initialize");
		this.matsimConfig = matsimConfig ;
	}

	public void runModel() {
		if (!Properties.get().main.runSilo) {
			return;
		}
		setupModel();
		runYearByYear();
		endSimulation();
	}

	private void setupModel() {
		logger.info("Setting up SILO Model (Implementation " + Properties.get().main.implementation + ")");
        setupContainer();
        setupYears();
        setupAccessibility();
        setupMicroSim();
        IssueCounter.logIssues(dataContainer.getGeoData());

        if (Properties.get().main.writeSmallSynpop) {
            dataContainer.getHouseholdData().writeOutSmallSynPop();
        }
		if (Properties.get().main.createPrestoSummary) {
			SummarizeData.preparePrestoSummary(dataContainer.getGeoData());
		}
	}

    private void setupContainer() {
        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
		IssueCounter.regionSpecificCounters(dataContainer.getGeoData());
		dataContainer.getHouseholdData().setTypeOfAllHouseholds();
		dataContainer.getHouseholdData().setHighestHouseholdAndPersonId();
		dataContainer.getHouseholdData().calculateInitialSettings();
		dataContainer.getJobData().calculateEmploymentForecast();
		dataContainer.getJobData().identifyVacantJobs();
		dataContainer.getJobData().calculateJobDensityByZone();
		dataContainer.getRealEstateData().fillQualityDistribution();
		dataContainer.getRealEstateData().setHighestVariablesAndCalculateRentShareByIncome();
		dataContainer.getRealEstateData().identifyVacantDwellings();

        modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, matsimConfig);
    }

    private void setupAccessibility() {

	    if(Properties.get().transportModel.runMatsim) {
	        modelContainer.getTransportModel().runTransportModel(Properties.get().main.startYear);
        } else {
            SkimUtil.updateCarSkim((SkimTravelTimes) modelContainer.getAcc().getTravelTimes(),
                    Properties.get().main.startYear, Properties.get());
			SkimUtil.updateTransitSkim((SkimTravelTimes) modelContainer.getAcc().getTravelTimes(),
					Properties.get().main.startYear, Properties.get());
			// updateTransitSkim was outside the bracket before, but central code should not decide how matsim updates
			// travel times.  If necessary, should be done inside the matsim adapter class. kai, may'18
        }
        modelContainer.getAcc().initialize();
        modelContainer.getAcc().calculateHansenAccessibilities(Properties.get().main.startYear);
    }

	private void setupYears() {
		scalingYears.addAll(Properties.get().main.scalingYears);
		if (!scalingYears.isEmpty()) {
			SummarizeData.readScalingYearControlTotals();
		}
		tdmYears.addAll(Properties.get().transportModel.modelYears);
		skimYears.addAll(Properties.get().transportModel.skimYears);
	}

	private void setupMicroSim() {
        microSim = new MicroSimulation(timeTracker);

		if(Properties.get().eventRules.allDemography) {
			if (Properties.get().eventRules.birthday ) {
				microSim.registerModel(BirthDayEvent.class, modelContainer.getBirthday());
            }
            if(Properties.get().eventRules.birth) {
				microSim.registerModel(BirthEvent.class, modelContainer.getBirth());
			}
			if (Properties.get().eventRules.death) {
				microSim.registerModel(DeathEvent.class, modelContainer.getDeath());
			}
			if (Properties.get().eventRules.leaveParentHh) {
				microSim.registerModel(LeaveParentsEvent.class, modelContainer.getLph());
			}
			if (Properties.get().eventRules.divorce) {
				microSim.registerModel(MarriageEvent.class, modelContainer.getMarriage());
			}
			if(Properties.get().eventRules.marriage) {
				microSim.registerModel(DivorceEvent.class, modelContainer.getDivorce());
			}
			if (Properties.get().eventRules.schoolUniversity) {
				microSim.registerModel(EducationEvent.class, modelContainer.getChangeSchoolUniv());
			}
			if (Properties.get().eventRules.driversLicense) {
				microSim.registerModel(LicenseEvent.class, modelContainer.getDriversLicense());
			}
			if (Properties.get().eventRules.quitJob || Properties.get().eventRules.startNewJob) {
				microSim.registerModel(EmploymentEvent.class, modelContainer.getEmployment());
            }
		}
        if(Properties.get().eventRules.allHhMoves) {
            microSim.registerModel(MoveEvent.class, modelContainer.getMove());
            if(Properties.get().eventRules.outMigration || Properties.get().eventRules.inmigration) {
                microSim.registerModel(MigrationEvent.class, modelContainer.getIomig());
            }
        }
        if(Properties.get().eventRules.allDwellingDevelopments) {
            if(Properties.get().eventRules.dwellingChangeQuality) {
                microSim.registerModel(RenovationEvent.class, modelContainer.getRenov());
            }
            if(Properties.get().eventRules.dwellingDemolition) {
                microSim.registerModel(DemolitionEvent.class, modelContainer.getDemol());
            }
            if(Properties.get().eventRules.dwellingConstruction) {
                microSim.registerModel(ConstructionEvent.class, modelContainer.getCons());
            }
        }
    }

	private void runYearByYear() {

        final HouseholdDataManager householdData = dataContainer.getHouseholdData();

        for (int year = Properties.get().main.startYear; year < Properties.get().main.endYear; year ++) {

            logger.info("Simulating changes from year " + year + " to year " + (year + 1));
            IssueCounter.setUpCounter();    // setup issue counter for this simulation period
            SiloUtil.trackingFile("Simulating changes from year " + year + " to year " + (year + 1));
            timeTracker.setCurrentYear(year);

            timeTracker.reset();
            if (scalingYears.contains(year)) {
                SummarizeData.scaleMicroDataToExogenousForecast(year, dataContainer);
            }
            timeTracker.record("scaleDataToForecast");

			timeTracker.reset();
			if (year != Properties.get().main.implementation.BASE_YEAR) {
				modelContainer.getUpdateJobs().updateJobInventoryMultiThreadedThisYear(year);
				dataContainer.getJobData().identifyVacantJobs();
			}
			timeTracker.record("setupJobChange");

			timeTracker.reset();
			if (skimYears.contains(year) && !tdmYears.contains(year) &&
					!Properties.get().transportModel.runTravelDemandModel &&
					year != Properties.get().main.startYear) {
				// skims are (in non-Matsim case) always read in start year and in every year the transportation model ran. Additional
				// years to read skims may be provided in skimYears
				if (!Properties.get().transportModel.runMatsim) {
                    SkimUtil.updateCarSkim((SkimTravelTimes) modelContainer.getAcc().getTravelTimes(),
                            Properties.get().main.startYear, Properties.get());
				}
                SkimUtil.updateTransitSkim((SkimTravelTimes) modelContainer.getAcc().getTravelTimes(),
                        Properties.get().main.startYear, Properties.get());
                modelContainer.getAcc().calculateHansenAccessibilities(year);
            }
			timeTracker.record("calcAccessibilities");

            timeTracker.reset();
			modelContainer.getDdOverwrite().addDwellings(year);
            timeTracker.record("addOverwriteDwellings");

            timeTracker.reset();
            modelContainer.getMove().calculateRegionalUtilities();
			modelContainer.getMove().calculateAverageHousingSatisfaction();
			timeTracker.record("calcAveHousingSatisfaction");

            timeTracker.reset();
			if (year != Properties.get().main.implementation.BASE_YEAR) {
			    householdData.adjustIncome();
            }
			timeTracker.record("planIncomeChange");

			if (year == Properties.get().main.implementation.BASE_YEAR || year != Properties.get().main.startYear) {
                SiloUtil.summarizeMicroData(year, modelContainer, dataContainer);
            }

            microSim.simulate(year);

			timeTracker.reset();
			int[] carChangeCounter = modelContainer.getCarOwnershipModel().updateCarOwnership(householdData.getUpdatedHouseholds());
			householdData.clearUpdatedHouseholds();
			timeTracker.record("updateCarOwnership");

			if ( Properties.get().transportModel.runMatsim || Properties.get().transportModel.runTravelDemandModel
                    || Properties.get().main.createMstmOutput) {
                if (tdmYears.contains(year + 1)) {
					timeTracker.reset();
                    modelContainer.getTransportModel().runTransportModel(year + 1);
					timeTracker.record("transportModel");
					timeTracker.reset();
                    modelContainer.getAcc().calculateHansenAccessibilities(year + 1);
					timeTracker.record("calcAccessibilities");
                }
            }

			timeTracker.reset();
			modelContainer.getPrm().updatedRealEstatePrices();
			timeTracker.record("updateRealEstatePrices");

			microSim.finishYear(year, carChangeCounter, dataContainer);
			IssueCounter.logIssues(dataContainer.getGeoData());           // log any issues that arose during this simulation period

			logger.info("  Finished this simulation period with " + householdData.getPersonCount() +
					" persons, " + householdData.getHouseholds().size() + " households and "  +
					dataContainer.getRealEstateData().getDwellings().size() + " dwellings.");

			if (SiloUtil.modelStopper("check")) {
			    break;
            }
            timeTracker.endYear();
		}
	}

	private void endSimulation() {
		if (scalingYears.contains(Properties.get().main.endYear)) {
            SummarizeData.scaleMicroDataToExogenousForecast(Properties.get().main.endYear, dataContainer);
        }

		if (Properties.get().main.endYear != 2040) {
			SummarizeData.writeOutSyntheticPopulation(Properties.get().main.endYear, dataContainer);
			dataContainer.getGeoData().writeOutDevelopmentCapacityFile(dataContainer);
		}

		SiloUtil.summarizeMicroData(Properties.get().main.endYear, modelContainer, dataContainer);
		SiloUtil.finish(modelContainer);
		SiloUtil.modelStopper("removeFile");
        SiloUtil.writeOutTimeTracker(timeTracker);
		logger.info("Scenario results can be found in the directory scenOutput/" + Properties.get().main.scenarioName + ".");
	}

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
		cblcm = new SiloModelCBLCM() ;
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
