package de.tum.bgu.msm.health;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.health.airPollutant.emission.MCRHbefaRoadTypeMapping;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.data.LinkInfo;
import de.tum.bgu.msm.health.injury.AccidentRateModel;
import de.tum.bgu.msm.health.injury.AccidentType;
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

import java.util.*;

public class AccidentModelMEL extends AbstractModel implements ModelUpdateListener {
    private int latestMatsimYear = -1;
    private static final Logger logger = LogManager.getLogger(AccidentModelMEL.class);
    private List<Day> simulatedDays;

    public AccidentModelMEL(DataContainer dataContainer, Properties properties, Random random) {
        super(dataContainer, properties, random);
        //simulatedDays = Arrays.asList(Day.sunday);
        simulatedDays = Arrays.asList(Day.sunday, Day.saturday, Day.thursday);
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

            // float scalingFactor = (float) (properties.main.scaleFactor * properties.transportModel.matsimScaleFactor);
            float scalingFactor = 0.1f; // todo: temporary fix
            scenario.addScenarioElement("accidentModelFile", properties.main.baseDirectory + "input/accident/");
            //System.out.println(scenario.getScenarioElement("accidentModelFile").toString());
            //System.exit(0);

            // injury model (old version)
            //AccidentRateModelMCR model = new AccidentRateModelMCR(scenario, 1.f / scalingFactor, day);
            //model.runCasualtyRateMCR(); // number of casualties per link (max 1 per link, otherwise 0)
            // model.computeLinkLevelInjuryRisk(); // R=1/v where v is the traffic volume
            // model.computePersonLevelInjuryRiskOffline();

            // osm-based injury model (new version)
            AccidentRateModelOsmMCR model = new AccidentRateModelOsmMCR(properties, scenario, 1.f / scalingFactor, day);
            model.runCasualtyRateMCR();

            for (Id<Link> linkId : model.getAccidentsContext().getLinkId2info().keySet()) {
                //((de.tum.bgu.msm.scenarios.health.HealthDataContainerImpl)dataContainer).getLinkInfoByDay().get(day).get(linkId).setLightCasualityExposureByAccidentTypeByTime(model.getAccidentsContext().getLinkId2info().get(linkId).getLightCasualityExposureByAccidentTypeByTime());
                //((DataContainerHealth) dataContainer).getLinkInfo().get(linkId).setSevereFatalCasualityExposureByAccidentTypeByTime(model.getAccidentsContext().getLinkId2info().get(linkId).getSevereFatalCasualityExposureByAccidentTypeByTime());
                ((HealthDataContainerImpl) dataContainer).getLinkInfoByDay(day).get(linkId).setSevereFatalCasualityExposureByAccidentTypeByTime(model.getAccidentsContext().getLinkId2info().get(linkId).getSevereFatalCasualityExposureByAccidentTypeByTime());
            }

            logger.info("====================");
            logger.info("Accident model stats for day: {}", day);
            logger.info("====================");

            // Initialize a map to store total risk per AccidentType
            Map<AccidentType, Double> totalRiskByAccidentType = new EnumMap<>(AccidentType.class);
            for (AccidentType accidentType : AccidentType.values()) {
                totalRiskByAccidentType.put(accidentType, 0.0);
            }

            // Iterate over each LinkId in the linkInfoByDay map for the given day
            Map<Id<Link>, LinkInfo> linkInfoMap = ((HealthDataContainerImpl) dataContainer).getLinkInfoByDay(day);
            if (linkInfoMap == null) {
                logger.warn("No link info available for day: {}", day);
                return;
            }

            for (Id<Link> linkId : linkInfoMap.keySet()) {
                // Get LinkInfo for the current linkId, defaulting to a new LinkInfo if absent
                LinkInfo linkInfo = linkInfoMap.getOrDefault(linkId, new LinkInfo(linkId));

                // Iterate over each AccidentType
                for (AccidentType accidentType : AccidentType.values()) {
                    double totalRisk = 0.0;

                    // Sum risk across all 24 hours for the current AccidentType
                    for (int hour = 0; hour < 24; hour++) {
                        // Retrieve the hourly risk map for the accidentType, defaulting to an empty OpenIntFloatHashMap
                        OpenIntFloatHashMap hourlyRiskMap = linkInfo
                                .getSevereFatalCasualityExposureByAccidentTypeByTime()
                                .getOrDefault(accidentType, new OpenIntFloatHashMap());

                        // Add the risk for the current hour, defaulting to 0.0 if not present
                        totalRisk += hourlyRiskMap.get(hour);
                    }

                    // Aggregate the risk into the total for this AccidentType
                    totalRiskByAccidentType.merge(accidentType, totalRisk, Double::sum);
                }
            }

            // Log the aggregated risk for each AccidentType
            for (AccidentType accidentType : AccidentType.values()) {
                logger.info("Accident Type: {}, Total Risk: {}", accidentType, totalRiskByAccidentType.get(accidentType));
            }



            // check
            /*
            for (Id<Link> linkId : model.getAccidentsContext().getLinkId2info().keySet()) {
                //((de.tum.bgu.msm.scenarios.health.HealthDataContainerImpl)dataContainer).getLinkInfoByDay().get(day).get(linkId).setLightCasualityExposureByAccidentTypeByTime(model.getAccidentsContext().getLinkId2info().get(linkId).getLightCasualityExposureByAccidentTypeByTime());
                //((DataContainerHealth) dataContainer).getLinkInfo().get(linkId).setSevereFatalCasualityExposureByAccidentTypeByTime(model.getAccidentsContext().getLinkId2info().get(linkId).getSevereFatalCasualityExposureByAccidentTypeByTime());

                for(int hour =0; hour<24; hour++){
                    double risk= ((HealthDataContainerImpl) dataContainer).getLinkInfoByDay(day).get(linkId).getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.PED).get(hour);
                    logger.warn("risk = " + risk);
                }

            }

             */

            model.getAccidentsContext().reset();
        }
    }
}
