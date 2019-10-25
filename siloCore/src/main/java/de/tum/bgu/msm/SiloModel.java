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

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.events.MicroEvent;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.models.EventModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.simulator.Simulator;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.utils.TimeTracker;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Greg Erhardt
 * Created on Dec 2, 2009
 */
public final class SiloModel {

	private final static Logger logger = Logger.getLogger(SiloModel.class);

    private final Set<Integer> scalingYears = new HashSet<>();
	private final Properties properties;

	private ModelContainer modelContainer;
	private DataContainer dataContainer;

    private Simulator simulator;
    private final TimeTracker timeTracker = new TimeTracker();
	private Set<ResultsMonitor> resultsMonitors = new HashSet<>();

	/**
     * @param properties
     * @param dataContainer
     * @param modelContainer
     */
  	public SiloModel(Properties properties,
                     DataContainer dataContainer, ModelContainer modelContainer) {
        this.modelContainer = modelContainer;
        this.dataContainer = dataContainer;
		this.properties = properties;

		SiloUtil.modelStopper("initialize");
	}

	public void addResultMonitor(ResultsMonitor resultsMonitor){
  		resultsMonitors.add(resultsMonitor);
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

		logger.info("Setting up SILO Model");

		simulator = new Simulator(timeTracker);
		for(Map.Entry<Class<? extends MicroEvent>, EventModel> eventModel: modelContainer.getEventModels().entrySet()) {
			if(eventModel.getValue() != null) {
				simulator.registerEventModel(eventModel.getKey(), eventModel.getValue());
			}
		}
		for(ModelUpdateListener modelUpdateListener : modelContainer.getModelUpdateListeners()) {
			if(modelUpdateListener != null) {
				simulator.registerAnnualModel(modelUpdateListener);
			}
		}

		for (ResultsMonitor resultsMonitor : resultsMonitors){
			simulator.registerResultsMonitor(resultsMonitor);

		}

        setupScalingYears();

        dataContainer.setup();
        simulator.setup();
	}

	private void setupScalingYears() {
		scalingYears.addAll(properties.main.scalingYears);
		if (!scalingYears.isEmpty()) {
			SummarizeData.readScalingYearControlTotals();
		}
	}

	private void runYearByYear() {

        final HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        for (int year = properties.main.startYear; year < properties.main.endYear; year++) {

            logger.info("Simulating changes from year " + year + " to year " + (year + 1));
            long time = System.currentTimeMillis();
            SiloUtil.trackingFile("Simulating changes from year " + year + " to year " + (year + 1));
            timeTracker.setCurrentYear(year);

            timeTracker.reset();
            if (scalingYears.contains(year)) {
                SummarizeData.scaleMicroDataToExogenousForecast(year, dataContainer);
            }
            timeTracker.recordAndReset("scaleDataToForecast");

            dataContainer.prepareYear(year);
            if (year == properties.main.baseYear || year != properties.main.startYear) {
                SiloUtil.summarizeMicroData(year, modelContainer, dataContainer);
            }
            simulator.simulate(year);
			dataContainer.endYear(year);

			logger.info("  Finished this simulation period with " + householdDataManager.getPersons().size() +
					" persons, " + householdDataManager.getHouseholds().size() + " households and "  +
					dataContainer.getRealEstateDataManager().getDwellings().size() + " dwellings in " +
                    (System.currentTimeMillis() - time) / 1000 + " seconds.");

			if (SiloUtil.modelStopper("check")) {
			    break;
            }
            timeTracker.endYear();
		}
	}

	private void endSimulation() {
  	    simulator.endSimulation();
  	    dataContainer.endSimulation();

  	    if (scalingYears.contains(properties.main.endYear)) {
            SummarizeData.scaleMicroDataToExogenousForecast(properties.main.endYear, dataContainer);
        }

		if (properties.main.printOutFinalSyntheticPopulation) {
			SummarizeData.writeOutDevelopmentFile(dataContainer);
		}
		SiloUtil.summarizeMicroData(properties.main.endYear, modelContainer, dataContainer);
		SiloUtil.finish();
		SiloUtil.modelStopper("removeFile");
        SiloUtil.writeOutTimeTracker(timeTracker);
		logger.info("Scenario results can be found in the directory scenOutput/" + properties.main.scenarioName + ".");
	}
}
