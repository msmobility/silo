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

import de.tum.bgu.msm.container.SiloDataContainerImpl;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.models.AnnualModel;
import de.tum.bgu.msm.models.EventModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.simulator.Simulator;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.utils.TimeTracker;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Greg Erhardt
 * Created on Dec 2, 2009
 */
public final class SiloModel {

	private final static Logger logger = Logger.getLogger(SiloModel.class);

	private final Set<Integer> skimYears = new HashSet<>();
    private final Set<Integer> scalingYears = new HashSet<>();
	private final Properties properties;

	private SiloModelContainer modelContainer;
	private SiloDataContainerImpl dataContainer;


	private final Config matsimConfig;
    private Simulator simulator;
    private final TimeTracker timeTracker = new TimeTracker();

    public SiloModel(Properties properties) {
		this(null, properties) ;
	}

	public SiloModel(Config matsimConfig, Properties properties) {
		IssueCounter.setUpCounter();
		this.properties = properties;
		SiloUtil.modelStopper("initialize");
		this.matsimConfig = matsimConfig ;
	}

	public void runModel() {
		logger.info("Scenario: " + properties.main.scenarioName + ", Simulation start year: " + properties.main.startYear);
		long startTime = System.currentTimeMillis();
		try{
			setupModel();
			runYearByYear();
			endSimulation();
		} catch (Exception e){
			logger.error("Error running SILO.");
			throw new RuntimeException(e);
		} finally {
			SiloUtil.closeAllFiles(startTime, timeTracker);
		}

	}

	private void setupModel() {

		logger.info("Setting up SILO Model (Implementation " + properties.main.implementation + ")");

		simulator = new Simulator(timeTracker);
		for(Map.Entry<Class<? extends MicroEvent>, EventModel> eventModel: modelContainer.getEventModels().entrySet()) {
			simulator.registerEventModel(eventModel.getKey(), eventModel.getValue());
		}
		for(AnnualModel annualModel: modelContainer.getAnnualModels()) {
			simulator.registerAnnualModel(annualModel);
		}

        setupScalingYears();

        dataContainer.setup();

        IssueCounter.logIssues(dataContainer.getGeoData());
        IssueCounter.regionSpecificCounters(dataContainer.getGeoData());

        simulator.setup();

		if (properties.main.createPrestoSummary) {
			SummarizeData.preparePrestoSummary(dataContainer.getGeoData());
		}
	}

//    private void setupContainer() {
//        dataContainer = SiloDataContainerImpl.loadSiloDataContainer(properties);
//        modelContainer = SiloModelContainerImpl.createSiloModelContainer(dataContainer, matsimConfig, properties);
//    }


	private void setupScalingYears() {
		scalingYears.addAll(properties.main.scalingYears);
		if (!scalingYears.isEmpty()) {
			SummarizeData.readScalingYearControlTotals();
		}
	}


	private void runYearByYear() {

        final HouseholdDataManager householdData = dataContainer.getHouseholdData();
        for (int year = properties.main.startYear; year < properties.main.endYear; year++) {

            logger.info("Simulating changes from year " + year + " to year " + (year + 1));
            IssueCounter.setUpCounter();    // setup issue counter for this simulation period
            SiloUtil.trackingFile("Simulating changes from year " + year + " to year " + (year + 1));
            timeTracker.setCurrentYear(year);

            timeTracker.reset();
            if (scalingYears.contains(year)) {
                SummarizeData.scaleMicroDataToExogenousForecast(year, dataContainer);
            }
            timeTracker.recordAndReset("scaleDataToForecast");

			if (year == properties.main.implementation.BASE_YEAR || year != properties.main.startYear) {
                SiloUtil.summarizeMicroData(year, modelContainer, dataContainer);
            }

            simulator.simulate(year);

			simulator.finishYear(year, dataContainer);
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
		if (scalingYears.contains(properties.main.endYear)) {
            SummarizeData.scaleMicroDataToExogenousForecast(properties.main.endYear, dataContainer);
        }

		if (properties.main.printOutFinalSyntheticPopulation) {
			SummarizeData.writeOutSyntheticPopulation(properties.main.endYear, dataContainer);
			SummarizeData.writeOutDevelopmentFile(dataContainer);
		}


		SiloUtil.summarizeMicroData(properties.main.endYear, modelContainer, dataContainer);
		SiloUtil.finish(modelContainer);
		SiloUtil.modelStopper("removeFile");
        SiloUtil.writeOutTimeTracker(timeTracker);
		logger.info("Scenario results can be found in the directory scenOutput/" + properties.main.scenarioName + ".");
	}
}
