package de.tum.bgu.msm.health;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.health.airPollutant.emission.MCRHbefaRoadTypeMapping;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.injury.AccidentRateModel;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.resources.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AccidentModelMCR extends AbstractModel implements ModelUpdateListener {
    private int latestMatsimYear = -1;
    private static final Logger logger = LogManager.getLogger(AccidentModelMCR.class);
    private List<Day> simulatedDays;

    public AccidentModelMCR(DataContainer dataContainer, Properties properties, Random random) {
        super(dataContainer, properties, random);
        simulatedDays = Arrays.asList(Day.saturday,Day.sunday);
        //simulatedDays = Arrays.asList(Day.thursday, Day.saturday, Day.sunday);
    }

    @Override
    public void setup() {
    }

    @Override
    public void prepareYear(int year) {
    }

    public void endYear(int year) {
        logger.warn("Accident model end year:" + year);

        int targetYear = -1;

        // no baseExposure file when simulation starts
        if (properties.main.startYear == year && properties.healthData.baseExposureFile == null) {
            targetYear = year;
        }

        // Accident model outputs will be needed for future exposure updates
        if (properties.main.startYear == year &&
                properties.healthData.baseExposureFile != null &&
                !properties.healthData.exposureModelYears.isEmpty() &&
                !properties.healthData.exposureModelYears.contains(-1)) {
            targetYear = year;
        }

        // Accident model needs to be updated whenever traffic flows are updated
        if(properties.transportModel.transportModelYears.contains(year) && year != properties.main.startYear) {
            targetYear = year;
        }

        if (targetYear != -1) {
            // todo: lastestMatsimYear to be generalized ...
            latestMatsimYear = targetYear;
            runAccidentRateModel(latestMatsimYear);
            System.gc();
        }
    }

    @Override
    public void endSimulation() {
    }

    private void runAccidentRateModel(int year) {
        //generate link injury risks for each simulated day
        for (Day day : simulatedDays) {
            logger.info("Updating injury risk for year: " + year + "| day of week: " + day + ".");

            final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                    + properties.main.scenarioName + "/matsim/" + latestMatsimYear + "/" + day + "/";

            Config config = ConfigUtils.createConfig();
            config.controller().setOutputDirectory(outputDirectoryRoot);
            config.controller().setRunId(String.valueOf(latestMatsimYear));

            final MutableScenario scenario = ScenarioUtils.createMutableScenario(config);
            scenario.getConfig().travelTimeCalculator().setTraveltimeBinSize(3600);

            //float scalingFactor = (float) (properties.main.scaleFactor * Double.parseDouble(Resources.instance.getString(de.tum.bgu.msm.resources.Properties.TRIP_SCALING_FACTOR)));
            float scalingFactor = 0.1f; // todo: temporary fix
            scenario.addScenarioElement("accidentModelFile", properties.main.baseDirectory + "input/accident/");

            // Injury model
            AccidentRateModelMCR model = new AccidentRateModelMCR(scenario, 1.f / scalingFactor, day);

            model.runCasualtyRateMCR(); // number of casualties per link (max 1 per link, otherwise 0)
            //model.computeLinkLevelInjuryRisk(); // R=1/v where v is the traffic volume
            //model.computePersonLevelInjuryRiskOffline();


            for (Id<Link> linkId : model.getAccidentsContext().getLinkId2info().keySet()) {
                //((de.tum.bgu.msm.scenarios.health.HealthDataContainerImpl)dataContainer).getLinkInfoByDay().get(day).get(linkId).setLightCasualityExposureByAccidentTypeByTime(model.getAccidentsContext().getLinkId2info().get(linkId).getLightCasualityExposureByAccidentTypeByTime());
                //((DataContainerHealth) dataContainer).getLinkInfo().get(linkId).setSevereFatalCasualityExposureByAccidentTypeByTime(model.getAccidentsContext().getLinkId2info().get(linkId).getSevereFatalCasualityExposureByAccidentTypeByTime());
                ((HealthDataContainerImpl) dataContainer).getLinkInfoByDay(day).get(linkId).setSevereFatalCasualityExposureByAccidentTypeByTime(model.getAccidentsContext().getLinkId2info().get(linkId).getSevereFatalCasualityExposureByAccidentTypeByTime());
            }
            model.getAccidentsContext().reset();

            //
        }
    }
}
