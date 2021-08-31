package de.tum.bgu.msm.matsim.noise;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.matsim.MatsimData;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.noise.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Injector;
import org.matsim.core.events.EventsManagerModule;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.router.costcalculators.RandomizingTimeDistanceTravelDisutilityFactory;
import org.matsim.core.router.costcalculators.TravelDisutilityFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioByInstanceModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;

public class NoiseModel extends AbstractModel implements ModelUpdateListener {

        private static final Logger logger = Logger.getLogger(NoiseModel.class);

        private final Config initialConfig;

        private final RealEstateDataManager realEstateDataManager;
        private final MatsimData matsimData;
        private int latestMatsimYear = -1;

        private final NoiseReceiverPoints noiseReceiverPoints;


        public NoiseModel(Config initialConfig, DataContainer data, Properties properties, Random random, MatsimData matsimData) {
                super(data, properties, random);
                this.initialConfig = initialConfig;
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

                for (Dwelling dwelling : realEstateDataManager.getDwellings()) {
                        Id<ReceiverPoint> id = Id.create(dwelling.getId(), ReceiverPoint.class);
                        final NoiseReceiverPoint existing = noiseReceiverPoints.remove(id);
                        if (existing == null) {
                                newNoiseReceiverPoints.put(id, new NoiseReceiverPoint(id, CoordUtils.createCoord(dwelling.getCoordinate())));
                        } else {
                                existingNoiseReceiverPoints.put(id, existing);
                        }
                }

                this.noiseReceiverPoints.clear();
                this.noiseReceiverPoints.putAll(newNoiseReceiverPoints);
                this.noiseReceiverPoints.putAll(existingNoiseReceiverPoints);

                //First year
                if(properties.main.startYear == year) {
                        if(properties.transportModel.matsimInitialPlansFile != null
                                           && properties.transportModel.matsimInitialEventsFile != null) {
                                        replayFromEvents(noiseReceiverPoints);
                        } else {
                                //matsim transport model must have run at this stage for the start year
                                latestMatsimYear = year;
                                calculateNoiseOffline(noiseReceiverPoints);
                        }
                } else if (properties.transportModel.transportModelYears.contains(year + 1)) {
                        //matsim either has to run and update all receiver points for the next year...
                        latestMatsimYear = year + 1;
                        calculateNoiseOffline(noiseReceiverPoints);
                } else if(latestMatsimYear == -1 && properties.transportModel.matsimInitialPlansFile != null
                        && properties.transportModel.matsimInitialEventsFile != null) {
                        //...or the initial files are available and still valid for this year. update new receiver points.
                        replayFromEvents(newNoiseReceiverPoints);
                } else {
                        //matsim has run before. update new receiver points.
                        calculateNoiseOffline(newNoiseReceiverPoints);
                }

                int counter65 = 0;
                int counter55 = 0;
                for (Dwelling dwelling : dataContainer.getRealEstateDataManager().getDwellings()) {
                        final Id<ReceiverPoint> id = Id.create(dwelling.getId(), ReceiverPoint.class);
                        if (noiseReceiverPoints.containsKey(id)) {
                                double lden = noiseReceiverPoints.get(id).getLden();
                                ((NoiseDwelling) dwelling).setNoiseImmision(lden);
                                if (lden > 55) {
                                        if (lden > 65) {
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
                                                                   + properties.main.scenarioName + "/matsim/" + latestMatsimYear + "/";

                Config config = ConfigUtils.createConfig(initialConfig.getContext());
                config.controler().setOutputDirectory(outputDirectoryRoot);
                config.controler().setRunId(String.valueOf(latestMatsimYear));
                final MutableScenario scenario = ScenarioUtils.createMutableScenario(config);

//                new PopulationReader(scenario).readFile(populationPath);

                scenario.setNetwork(matsimData.getCarNetwork());

                scenario.addScenarioElement(NoiseReceiverPoints.NOISE_RECEIVER_POINTS, noiseReceiverPoints);

                NoiseConfigGroup noiseParameters = ConfigUtils.addOrGetModule(initialConfig, NoiseConfigGroup.class);
                noiseParameters.setInternalizeNoiseDamages(false);
                noiseParameters.setComputeCausingAgents(false);
                noiseParameters.setComputeNoiseDamages(false);
                noiseParameters.setComputePopulationUnits(false);
                noiseParameters.setComputeAvgNoiseCostPerLinkAndTime(false);
                noiseParameters.setThrowNoiseEventsCaused(false);
                noiseParameters.setThrowNoiseEventsAffected(false);
                noiseParameters.setConsiderNoiseBarriers(true);
                noiseParameters.setNoiseComputationMethod(NoiseConfigGroup.NoiseComputationMethod.RLS19);
                noiseParameters.setWriteOutputIteration(0);
                noiseParameters.setScaleFactor(1./initialConfig.qsim().getFlowCapFactor());
                config.addModule(noiseParameters);

                config.qsim().setEndTime(24 * 60 * 60);

                noiseParameters.setConsiderNoiseBarriers(true);
                noiseParameters.setNoiseBarriersFilePath("D:\\resultStorage\\diss\\noise\\aggBuildingPoly.geojson");
                noiseParameters.setNoiseBarriersSourceCRS("EPSG:31468");
                config.global().setCoordinateSystem("EPSG:31468");

                NoiseOfflineCalculation noiseOfflineCalculation = new NoiseOfflineCalculation(scenario, outputDirectoryRoot);
                noiseOfflineCalculation.run();

        }


        private void replayFromEvents(NoiseReceiverPoints noiseReceiverPoints) {

                final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
                String outputDirectory = outputDirectoryRoot + "/matsim/" + properties.main.startYear +"_warm" + "/";

                try {
                        final Path events = Paths.get(outputDirectory + "/"+ Paths.get(properties.transportModel.matsimInitialEventsFile).getFileName());
                        final Path plans = Paths.get(outputDirectory + "/" + Paths.get(properties.transportModel.matsimInitialPlansFile).getFileName());

                        Files.createDirectories(events.getParent());
                        Files.createDirectories(plans.getParent());

                        Files.copy(Paths.get(properties.main.baseDirectory + properties.transportModel.matsimInitialEventsFile), events);
                        Files.copy(Paths.get(properties.main.baseDirectory + properties.transportModel.matsimInitialPlansFile), plans);

                } catch (Exception e) {
                        logger.error(e);
                }


                String eventsFile = properties.main.baseDirectory + properties.transportModel.matsimInitialEventsFile;
                final Path path = Paths.get(eventsFile);

                String plansFile = properties.main.baseDirectory + properties.transportModel.matsimInitialPlansFile;


                Config config = ConfigUtils.createConfig();
                config.controler().setOutputDirectory(outputDirectory);
                String runId = eventsFile.substring(eventsFile.lastIndexOf("/") + 1, eventsFile.indexOf(".output_events"));
                config.controler().setRunId(runId);
                final MutableScenario scenario = ScenarioUtils.createMutableScenario(config);
                new PopulationReader(scenario).readFile(plansFile);

                scenario.setNetwork(matsimData.getCarNetwork());
                scenario.addScenarioElement(NoiseReceiverPoints.NOISE_RECEIVER_POINTS, noiseReceiverPoints);

                NoiseConfigGroup noiseParameters = ConfigUtils.addOrGetModule(initialConfig, NoiseConfigGroup.class);
                noiseParameters.setInternalizeNoiseDamages(false);
                noiseParameters.setComputeCausingAgents(false);
                noiseParameters.setComputeNoiseDamages(false);
                noiseParameters.setComputePopulationUnits(false);
                noiseParameters.setComputeAvgNoiseCostPerLinkAndTime(false);
                noiseParameters.setThrowNoiseEventsCaused(false);
                noiseParameters.setConsiderNoiseBarriers(true);
                noiseParameters.setNoiseComputationMethod(NoiseConfigGroup.NoiseComputationMethod.RLS19);
                noiseParameters.setThrowNoiseEventsAffected(false);
                noiseParameters.setWriteOutputIteration(0);
                noiseParameters.setScaleFactor(1./initialConfig.qsim().getFlowCapFactor());
                config.addModule(noiseParameters);

                config.qsim().setEndTime(24 * 60 * 60);


                noiseParameters.setConsiderNoiseBarriers(true);
                noiseParameters.setNoiseBarriersFilePath("C:\\Users\\Nico\\tum\\diss\\noise\\aggBuildingPoly.geojson");
                noiseParameters.setNoiseBarriersSourceCRS("EPSG:31468");
                config.global().setCoordinateSystem("EPSG:31468");


                NoiseOfflineCalculation noiseOfflineCalculation = new NoiseOfflineCalculation(scenario, path.getParent().toString());
                noiseOfflineCalculation.run();
        }
}
