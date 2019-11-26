package de.tum.bgu.msm.matsim.noise;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.matsim.MatsimData;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.contrib.noise.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;

import java.util.Random;

public class NoiseModel extends AbstractModel implements ModelUpdateListener {

    private static final Logger logger = Logger.getLogger(NoiseModel.class);
    private final RealEstateDataManager realEstateDataManager;
    private final MatsimData matsimData;
    private int latestMatsimYear = -1;

    private final NoiseReceiverPoints noiseReceiverPoints;


    public NoiseModel(DataContainer data, Properties properties, Random random, MatsimData matsimData) {
        super(data, properties, random);
        this.realEstateDataManager = data.getRealEstateDataManager();
        this.matsimData = matsimData;
        this.noiseReceiverPoints = new NoiseReceiverPoints();
    }

    @Override
    public void setup() {
        updateImmissions(properties.main.baseYear);
    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void endYear(int year) {
        updateImmissions(year);
    }

    @Override
    public void endSimulation() {
    }

    private void updateImmissions(int year) {
        logger.info("Updating noise immisisons for year " + year + ".");

        NoiseReceiverPoints newNoiseReceiverPoints = new NoiseReceiverPoints();
        NoiseReceiverPoints existingNoiseReceiverPoints = new NoiseReceiverPoints();

        for(Dwelling dwelling: realEstateDataManager.getDwellings()) {
            Id<ReceiverPoint> id = Id.create(dwelling.getId(), ReceiverPoint.class);
            final NoiseReceiverPoint existing = noiseReceiverPoints.remove(id);
            if(existing == null) {
                newNoiseReceiverPoints.put(id, new NoiseReceiverPoint(id, CoordUtils.createCoord(dwelling.getCoordinate())));
            } else {
                existingNoiseReceiverPoints.put(id, existing);
            }
        }

        this.noiseReceiverPoints.clear();
        this.noiseReceiverPoints.putAll(newNoiseReceiverPoints);
        this.noiseReceiverPoints.putAll(existingNoiseReceiverPoints);

        if (properties.transportModel.transportModelYears.contains(year + 1) || properties.main.startYear == year) {
            if (properties.transportModel.transportModelYears.contains(year + 1)) {
                latestMatsimYear = year + 1;
            } else {
                latestMatsimYear = year;
            }
            calculateNoiseOffline(noiseReceiverPoints);
        } else if(latestMatsimYear > 0) {
            calculateNoiseOffline(newNoiseReceiverPoints);
            this.noiseReceiverPoints.putAll(newNoiseReceiverPoints);
        }

        int counter65 = 0;
        int counter55 = 0;
        for (Dwelling dwelling: dataContainer.getRealEstateDataManager().getDwellings()) {
            final Id<ReceiverPoint> id = Id.create(dwelling.getId(), ReceiverPoint.class);
            if(noiseReceiverPoints.containsKey(id)) {
                double lden = noiseReceiverPoints.get(id).getLden();
                ((NoiseDwelling) dwelling).setNoiseImmision(lden);
                if(lden > 55) {
                    if(lden > 65) {
                        counter65++;
                    } else {
                        counter55++;
                    }
                }
            }
        }
        int total = dataContainer.getRealEstateDataManager().getDwellings().size();
        int quiet = total - counter55 - counter65;
        logger.info("Dwellings <55dB(A) : " + quiet + " (" + ((double) quiet) / total + "%)");
        logger.info("Dwellings 55dB(A)-65dB(A) : " + counter55 + " (" + ((double) counter55) / total + "%)");
        logger.info("Dwellings >65dB(A) : " + counter65 + " (" + ((double) counter65) / total + "%)");
    }


    private void calculateNoiseOffline(NoiseReceiverPoints noiseReceiverPoints) {
        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                + properties.main.scenarioName + "/matsim/" + latestMatsimYear +"/";

        String populationPath = outputDirectoryRoot + latestMatsimYear +".output_plans.xml.gz";

        Config config = ConfigUtils.createConfig();
        config.controler().setOutputDirectory(outputDirectoryRoot);
        config.controler().setRunId(String.valueOf(latestMatsimYear));
        final MutableScenario scenario = ScenarioUtils.createMutableScenario(config);

        new PopulationReader(scenario).readFile(populationPath);

        scenario.setNetwork(matsimData.getCarNetwork());

        scenario.addScenarioElement(NoiseReceiverPoints.NOISE_RECEIVER_POINTS, noiseReceiverPoints);

        NoiseConfigGroup noiseParameters = ConfigUtils.addOrGetModule(scenario.getConfig(), NoiseConfigGroup.class);
        noiseParameters.setInternalizeNoiseDamages(false);
        noiseParameters.setComputeCausingAgents(false);
        noiseParameters.setComputeNoiseDamages(false);
        noiseParameters.setComputePopulationUnits(false);
        noiseParameters.setComputeAvgNoiseCostPerLinkAndTime(false);
        noiseParameters.setThrowNoiseEventsCaused(false);
        noiseParameters.setThrowNoiseEventsAffected(false);
        noiseParameters.setWriteOutputIteration(0);
        noiseParameters.setScaleFactor(20);
        config.qsim().setEndTime(24*60*60);

        NoiseOfflineCalculation noiseOfflineCalculation = new NoiseOfflineCalculation(scenario, outputDirectoryRoot);
        noiseOfflineCalculation.run();
    }
}
