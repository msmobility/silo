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
import de.tum.bgu.msm.data.RealEstateDataManager;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.Person;
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
import de.tum.bgu.msm.models.transportModel.matsim.MatsimTransportModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.modules.TransportModelPropertiesModule;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.utils.TravelTimeUtil;
import de.tum.bgu.msm.utils.TimeTracker;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static de.tum.bgu.msm.properties.modules.TransportModelPropertiesModule.TransportModelIdentifier.NONE;
import static de.tum.bgu.msm.utils.SiloUtil.initializeRandomNumber;

/**
 * @author Greg Erhardt
 * Created on Dec 2, 2009
 */
public final class SiloModel {

	private final static Logger logger = Logger.getLogger(SiloModel.class);

	private final Set<Integer> tdmYears = new HashSet<>();
	private final Set<Integer> skimYears = new HashSet<>();
    private final Set<Integer> scalingYears = new HashSet<>();
	private final Properties properties;
	private Map<String, Double> parametersMap;
	private Map<Integer, int[]> householdSizeDistribution;
	private final int combinationId;

	private SiloModelContainer modelContainer;
	private SiloDataContainer data;
	private final Config matsimConfig;
    private MicroSimulation microSim;
    private final TimeTracker timeTracker = new TimeTracker();

    private Map<Integer, Household> householdMap;
    private Map<Integer, Person> personMap;

    public SiloModel(Properties properties) {
		this(null, properties, null, 0, null, null) ;
	}

	public SiloModel(Config matsimConfig, Properties properties) {
		IssueCounter.setUpCounter();
		this.properties = properties;
		SiloUtil.modelStopper("initialize");
		this.matsimConfig = matsimConfig ;
		this.combinationId = 0;
	}

	public SiloModel(Config matsimConfig, Properties properties, Map<String, Double> parametersMap, int combinationId,
					 Map<Integer, Household> householdMap, Map<Integer, Person> personMap) {
		IssueCounter.setUpCounter();
		this.properties = properties;
		SiloUtil.modelStopper("initialize");
		this.matsimConfig = matsimConfig ;
		this.parametersMap = parametersMap;
		this.combinationId = combinationId;
		this.householdMap = householdMap;
		this.personMap = personMap;
	}

	public void runModel() {
		//logger.info("Scenario: " + properties.main.scenarioName + ", Simulation start year: " + properties.main.startYear);
		long startTime = System.currentTimeMillis();
		householdSizeDistribution = new HashMap<>();
		try{
			setupModel();
			runYearByYear();
			endSimulation(combinationId);
		} catch (Exception e){
			logger.error("Error running SILO.");
			throw new RuntimeException(e);
		} finally {
			SiloUtil.closeAllFiles(startTime);
		}

	}

	private void setupModel() {
		//logger.info("Setting up SILO Model (Implementation " + properties.main.implementation + ")");
		initializeRandomNumber(parametersMap.get("RandomSeed").intValue());
		setupContainer();
        setupYears();
        //setupTravelTimes();
        //setupAccessibility();
        setupMicroSim();
        IssueCounter.logIssues(data.getGeoData());



		if (properties.main.createPrestoSummary) {
			SummarizeData.preparePrestoSummary(data.getGeoData());
		}
	}



    private void setupContainer() {
        data = SiloDataContainer.loadSiloDataContainer(properties, householdMap, personMap);
		IssueCounter.regionSpecificCounters(data.getGeoData());
		//data.getHouseholdData().calculateInitialSettings();
		//removed for machine learning exercise
		/*data.getJobData().calculateEmploymentForecast();
		data.getJobData().identifyVacantJobs();
		data.getJobData().calculateJobDensityByZone();
		data.getRealEstateData().fillQualityDistribution();
		data.getRealEstateData().setHighestVariablesAndCalculateRentShareByIncome();*/
		//data.getRealEstateData().identifyVacantDwellings();
        modelContainer = SiloModelContainer.createSiloModelContainer(data, matsimConfig, properties, parametersMap);
    }

    private void setupTravelTimes() {
		if(properties.transportModel.transportModelIdentifier == TransportModelPropertiesModule.TransportModelIdentifier.MATSIM) {
		    if(properties.transportModel.matsimInitialEventsFile == null) {
                modelContainer.getTransportModel().runTransportModel(properties.main.startYear);
            } else {
                String eventsFile = properties.main.baseDirectory + properties.transportModel.matsimInitialEventsFile;
                ((MatsimTransportModel) modelContainer.getTransportModel()).replayFromEvents(eventsFile);
            }
		} else {
			updateSkims(properties.main.startYear);
		}
	}

	private void updateSkims(int year) {
		TravelTimeUtil.updateCarSkim((SkimTravelTimes) data.getTravelTimes(),
				year, properties);
		TravelTimeUtil.updateTransitSkim((SkimTravelTimes) data.getTravelTimes(),
				year, properties);
	}

    private void setupAccessibility() {
        modelContainer.getAcc().initialize();
        modelContainer.getAcc().calculateHansenAccessibilities(properties.main.startYear);
    }

	private void setupYears() {
		scalingYears.addAll(properties.main.scalingYears);
		if (!scalingYears.isEmpty()) {
			SummarizeData.readScalingYearControlTotals();
		}
		tdmYears.addAll(properties.transportModel.transportModelYears);
		skimYears.addAll(properties.accessibility.skimYears);
	}

	private void setupMicroSim() {
        microSim = new MicroSimulation(timeTracker);

		if(properties.eventRules.allDemography) {
			if (properties.eventRules.birthday ) {
				microSim.registerModel(BirthDayEvent.class, modelContainer.getBirthday());
            }
            if(properties.eventRules.birth) {
				microSim.registerModel(BirthEvent.class, modelContainer.getBirth());
			}
			if (properties.eventRules.death) {
				microSim.registerModel(DeathEvent.class, modelContainer.getDeath());
			}
			if (properties.eventRules.leaveParentHh) {
				microSim.registerModel(LeaveParentsEvent.class, modelContainer.getLph());
			}
			if (properties.eventRules.divorce) {
				microSim.registerModel(MarriageEvent.class, modelContainer.getMarriage());
			}
			if(properties.eventRules.marriage) {
				microSim.registerModel(DivorceEvent.class, modelContainer.getDivorce());
			}
			if (properties.eventRules.schoolUniversity) {
				microSim.registerModel(EducationEvent.class, modelContainer.getChangeSchoolUniv());
			}
			if (properties.eventRules.driversLicense) {
				microSim.registerModel(LicenseEvent.class, modelContainer.getDriversLicense());
			}
			if (properties.eventRules.quitJob || properties.eventRules.startNewJob) {
				microSim.registerModel(EmploymentEvent.class, modelContainer.getEmployment());
            }
			if(properties.eventRules.outMigration || properties.eventRules.inmigration) {
				microSim.registerModel(MigrationEvent.class, modelContainer.getIomig());
			}
		}
        if(properties.eventRules.allHhMoves) {
            microSim.registerModel(MoveEvent.class, modelContainer.getMove());
            if(properties.eventRules.outMigration || properties.eventRules.inmigration) {
                microSim.registerModel(MigrationEvent.class, modelContainer.getIomig());
            }
        }
        if(properties.eventRules.allDwellingDevelopments) {
            if(properties.eventRules.dwellingChangeQuality) {
                microSim.registerModel(RenovationEvent.class, modelContainer.getRenov());
            }
            if(properties.eventRules.dwellingDemolition) {
                microSim.registerModel(DemolitionEvent.class, modelContainer.getDemol());
            }
            if(properties.eventRules.dwellingConstruction) {
                microSim.registerModel(ConstructionEvent.class, modelContainer.getCons());
            }
        }
    }

	private void runYearByYear() {

        final HouseholdDataManager householdData = data.getHouseholdData();

        for (int year = properties.main.startYear; year < properties.main.endYear; year ++) {

            logger.info("Simulating changes from year " + year + " to year " + (year + 1));
            IssueCounter.setUpCounter();    // setup issue counter for this simulation period
            SiloUtil.trackingFile("Simulating changes from year " + year + " to year " + (year + 1));
            //timeTracker.setCurrentYear(year);

            //timeTracker.reset();

            //removed for machine learning exercise
/*
            if (scalingYears.contains(year)) {
                SummarizeData.scaleMicroDataToExogenousForecast(year, data);
            }
            timeTracker.recordAndReset("scaleDataToForecast");

            if (year != properties.main.implementation.BASE_YEAR) {
				modelContainer.getUpdateJobs().updateJobInventoryMultiThreadedThisYear(year);
				data.getJobData().identifyVacantJobs();
			}
			timeTracker.recordAndReset("setupJobChange");



			modelContainer.getDdOverwrite().addDwellings(year);
            timeTracker.recordAndReset("addOverwriteDwellings");

            modelContainer.getMove().calculateRegionalUtilities();
			modelContainer.getMove().calculateAverageHousingSatisfaction();
			timeTracker.recordAndReset("calcAveHousingSatisfaction");

			if (year != properties.main.implementation.BASE_YEAR) {
			    householdData.adjustIncome();
            }
			timeTracker.record("planIncomeChange");*/

/*			if (year == properties.main.implementation.BASE_YEAR || year != properties.main.startYear) {
                SiloUtil.summarizeMicroData(year, modelContainer, data, combinationId);
            }*/

            microSim.simulate(year);

			//removed for machine learning exercise
/*			timeTracker.reset();
			int[] carChangeCounter = modelContainer.getUpdateCarOwnershipModel().updateCarOwnership(householdData.getUpdatedHouseholds());
			householdData.clearUpdatedHouseholds();
			timeTracker.recordAndReset("updateCarOwnership");


			int avSwitchCounter = 0;
			if (properties.main.implementation == Implementation.MUNICH){
				avSwitchCounter = modelContainer.getSwitchToAutonomousVehicleModel().switchToAV(householdData.getConventionalCarsHouseholds(), year);
				householdData.clearConventionalCarsHouseholds();
				timeTracker.recordAndReset("switchToAV");
			}

			if (skimYears.contains(year) && year != properties.main.startYear) {
				updateSkims(year);
				timeTracker.recordAndReset("Skim update");
			}

			if ( properties.transportModel.transportModelIdentifier != NONE && tdmYears.contains(year + 1)) {
                    modelContainer.getTransportModel().runTransportModel(year + 1);
					timeTracker.recordAndReset("transportModel");
            }

			modelContainer.getAcc().calculateHansenAccessibilities(year+1);
			timeTracker.recordAndReset("calcAccessibilities");

			modelContainer.getPrm().updatedRealEstatePrices();
			timeTracker.record("updateRealEstatePrices");*/

			microSim.finishYear(year, data);
			IssueCounter.logIssues(data.getGeoData());           // log any issues that arose during this simulation period

			householdSizeDistribution.put(year, householdData.getHouseholdSizeDistribution());
/*			logger.info("  Finished this simulation period with " + householdData.getPersonCount() +
					" persons, " + householdData.getHouseholds().size() + " households and "  +
					data.getRealEstateData().getDwellings().size() + " dwellings.");*/

			if (SiloUtil.modelStopper("check")) {
			    break;
            }
            //timeTracker.endYear();
		}
	}

	private void endSimulation(int combinationId) {
    	//removed for machine learning exercise
/*		if (scalingYears.contains(properties.main.endYear)) {
            SummarizeData.scaleMicroDataToExogenousForecast(properties.main.endYear, data);
        }

		if (properties.main.endYear != 2040) {
			SummarizeData.writeOutSyntheticPopulation(properties.main.endYear, data);
			data.getGeoData().writeOutDevelopmentCapacityFile(data);
		}*/
		SiloUtil.summarizeHouseholdSizeDistribution(householdSizeDistribution, combinationId);
		//SiloUtil.summarizeMicroData(properties.main.endYear, modelContainer, data, combinationId);
		//SummarizeData.writeOutSyntheticPopulation(properties.main.endYear, data);
		SiloUtil.finish(modelContainer);
		SiloUtil.modelStopper("removeFile");
        //SiloUtil.writeOutTimeTracker(timeTracker, combinationId);
		logger.info("Scenario results can be found in the directory scenOutput/" + properties.main.scenarioName + combinationId + ".");
	}
}
