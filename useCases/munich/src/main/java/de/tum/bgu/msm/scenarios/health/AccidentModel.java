package de.tum.bgu.msm.scenarios.health;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.resources.Resources;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.accidents.AccidentRateModel;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Random;

public class AccidentModel extends AbstractModel implements ModelUpdateListener {
    private int latestMatsimYear = -1;
    private static final Logger logger = Logger.getLogger(AccidentModel.class);

    public AccidentModel(DataContainer dataContainer, Properties properties, Random random) {
        super(dataContainer, properties, random);
    }

    @Override
    public void setup() {
    }

    @Override
    public void prepareYear(int year) {
    }

    @Override
    public void endYear(int year) {
        logger.warn("Accident model end year:" + year);
        if(properties.main.startYear == year) {
            latestMatsimYear = year;
            runAccidentRateModel(year);
        } else if(properties.transportModel.transportModelYears.contains(year + 1)) {//why year +1
            latestMatsimYear = year + 1;
            runAccidentRateModel(year + 1);
        }
    }




    @Override
    public void endSimulation() {
    }

    private void runAccidentRateModel(int year) {
        for(Day day : Day.values()) {
            logger.info("Updating injury risk for year: " + year + "| day of week: " + day + ".");

            final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                    + properties.main.scenarioName + "/matsim/" + latestMatsimYear + "/" + day + "/";
            Config config = ConfigUtils.createConfig();
            config.controler().setOutputDirectory(outputDirectoryRoot);
            config.controler().setRunId(String.valueOf(latestMatsimYear));
            final MutableScenario scenario = ScenarioUtils.createMutableScenario(config);
            scenario.getConfig().travelTimeCalculator().setTraveltimeBinSize(3600);
            double scalingFactor = properties.main.scaleFactor * Double.parseDouble(Resources.instance.getString(de.tum.bgu.msm.resources.Properties.TRIP_SCALING_FACTOR));
            scenario.addScenarioElement("accidentModelFile",properties.main.baseDirectory+"input/accident/");
            AccidentRateModel model = new AccidentRateModel(scenario, scalingFactor);
            model.runModelOnline();

            for(Id<Link> linkId : model.getAccidentsContext().getLinkId2info().keySet()){
                ((HealthDataContainerImpl)dataContainer).getLinkInfoByDay().get(day).get(linkId).
                        setLightCasualityExposureByAccidentTypeByTime(model.getAccidentsContext().getLinkId2info().get(linkId).getLightCasualityExposureByAccidentTypeByTime());
                ((HealthDataContainerImpl)dataContainer).getLinkInfoByDay().get(day).get(linkId).
                        setSevereFatalCasualityExposureByAccidentTypeByTime(model.getAccidentsContext().getLinkId2info().get(linkId).getSevereFatalCasualityExposureByAccidentTypeByTime());
            }

        }
    }

}
