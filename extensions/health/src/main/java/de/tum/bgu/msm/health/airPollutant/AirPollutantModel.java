package de.tum.bgu.msm.health.airPollutant;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.health.airPollutant.dispersion.Grid;
import de.tum.bgu.msm.health.airPollutant.emission.CreateVehicles;
import de.tum.bgu.msm.health.airPollutant.emission.MCRHbefaRoadTypeMapping;
import de.tum.bgu.msm.health.data.ActivityLocation;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.airPollutant.dispersion.EmissionSpatialDispersion;
import de.tum.bgu.msm.health.io.PollutantConcentrationWriter;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.contrib.emissions.EmissionModule;
import org.matsim.contrib.emissions.Pollutant;
import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.EventWriterXML;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.Tuple;

import java.util.*;
import java.util.stream.Collectors;

import static org.matsim.core.controler.Injector.createInjector;

public class AirPollutantModel extends AbstractModel implements ModelUpdateListener {
    public static final int EMISSION_TIME_BIN_SIZE = 3600;
    private static final double EMISSION_GRID_SIZE = 20;
    private static final double EMISSION_SMOOTH_RADIUS = 100;
    private int latestMatsimYear = -1;
    private static final Logger logger = LogManager.getLogger(AirPollutantModel.class);
    private MutableScenario scenario;
    private final Config initialMatsimConfig;
    private final Set<Pollutant> pollutantSet = new HashSet<>();
    private List<Day> simulatedDays;
    private List<Coordinate> receiverPoints = new ArrayList<>();

    public AirPollutantModel(DataContainer dataContainer, Properties properties, Random random, Config config) {
        super(dataContainer, properties, random);
        this.initialMatsimConfig = config;
        Pollutant[] pollutants = new Pollutant[]{Pollutant.NO2,Pollutant.PM2_5, Pollutant.PM2_5_non_exhaust};
        simulatedDays = Arrays.asList(Day.thursday,Day.saturday,Day.sunday);
        for(Pollutant pollutant : pollutants){
            this.pollutantSet.add(pollutant);
        }
        ((DataContainerHealth)dataContainer).setPollutantSet(pollutantSet);
    }

    @Override
    public void setup() {
    }

    @Override
    public void prepareYear(int year) {
    }

    @Override
    public void endYear(int year) {
        logger.warn("Air pollutant exposure model end year:" + year);
        if(properties.main.startYear == year) {
            latestMatsimYear = year;
            final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                    + properties.main.scenarioName + "/matsim/" + latestMatsimYear;

            // add Hbefa road type to network
            Network network = NetworkUtils.readNetwork(initialMatsimConfig.network().getInputFile());
            new MCRHbefaRoadTypeMapping().addHbefaMappings(network);

            //create pollutant receiver points
            receiverPoints = assembleReceiverPoints(network);

            //generate car truck emission and concentration for each simulated day
            for(Day day : simulatedDays){
                Config config = ConfigUtils.loadConfig(initialMatsimConfig.getContext());
                scenario = ScenarioUtils.createMutableScenario(config);
                scenario.getConfig().controller().setOutputDirectory(outputDirectoryRoot);

                // create vehicle with emission type
                String vehicleFile = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + year + ".output_allVehicles.xml.gz";
                String vehicleFileWithEmissionType = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + year + ".vehicles_emission.xml.gz";
                CreateVehicles createVehicles = new CreateVehicles(scenario.getConfig());
                createVehicles.runVehicleType();
                createVehicles.runVehicle(vehicleFile, vehicleFileWithEmissionType);

                String eventFileWithoutEmissions = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events.xml.gz";
                String eventFileWithEmissions = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events_emission.xml.gz";

                addEmissionConfig(day, vehicleFileWithEmissionType);

                ScenarioUtils.loadScenario(scenario);
                scenario.setNetwork(network);

                // run MATSim emission offline based on events file
                createEmissionEventsOffline(eventFileWithoutEmissions,eventFileWithEmissions);

                // run simple dispersion model based on Gaussian spatial dispersion
                double scalingFactor = properties.main.scaleFactor * properties.healthData.matsim_scale_factor_car;
                runEmissionGridAnalyzer(year, day, eventFileWithEmissions, scalingFactor);

                // write out link and activity location concentration
                String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/";
                new PollutantConcentrationWriter().writeData((DataContainerHealth) dataContainer, outputDirectory, day);
                System.gc();
            }
        }
    }

    @Override
    public void endSimulation() {

    }

    private List<Coordinate> assembleReceiverPoints(Network network) {
        List<Coordinate> receiverPoints = new ArrayList<>();
        // for link concentration, from node, to node and middle points are used
        for(Node node : network.getNodes().values()){
            receiverPoints.add(new Coordinate(node.getCoord().getX(), node.getCoord().getY()));
        }

        for(Link link : network.getLinks().values()){
            receiverPoints.add(new Coordinate(link.getCoord().getX(), link.getCoord().getY()));
        }

        // all activity locations are considered as pollutant receiver points
        receiverPoints.addAll(((DataContainerHealth) dataContainer).getActivityLocations().values().stream().map(ActivityLocation::getCoordinate).collect(Collectors.toList()));
        logger.info("{} receiver points created for air pollutant concentration, having bounds: {}.", receiverPoints.size(),
                receiverPoints.stream()
                        .map(Envelope::new)
                        .reduce((a, b) -> {
                            a.expandToInclude(b);
                            return a;
                        }).orElse(null));
        return receiverPoints;
    }

    public Config addEmissionConfig(Day day, String vehicleFile) {
        if(scenario.getConfig().getModules().containsKey("emissions")){
            scenario.getConfig().removeModule("emissions");
        }
        scenario.getConfig().travelTimeCalculator().setTraveltimeBinSize(3600);
        EmissionsConfigGroup emissionsConfig= new EmissionsConfigGroup();
        emissionsConfig.setDetailedVsAverageLookupBehavior(EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
        emissionsConfig.setAverageColdEmissionFactorsFile(Properties.get().main.baseDirectory+ Properties.get().healthData.COLD_EMISSION_FILE);
        emissionsConfig.setAverageWarmEmissionFactorsFile(Properties.get().main.baseDirectory+ Properties.get().healthData.HOT_EMISSION_FILE);
        emissionsConfig.setNonScenarioVehicles(EmissionsConfigGroup.NonScenarioVehicles.ignore);
        emissionsConfig.setHbefaVehicleDescriptionSource(EmissionsConfigGroup.HbefaVehicleDescriptionSource.fromVehicleTypeDescription);
        scenario.getConfig().addModule(emissionsConfig);

        String populationFile = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + latestMatsimYear + ".output_plans.xml.gz";
        scenario.getConfig().vehicles().setVehiclesFile(vehicleFile);
        scenario.getConfig().plans().setInputFile(populationFile);

        return scenario.getConfig();
    }

    private void createEmissionEventsOffline(String eventsFileWithoutEmissions, String eventsFileWithEmission) {
        logger.warn("Start create Emission events offline...");
        EventsManager eventsManager = new EventsManagerImpl();

        AbstractModule module = new AbstractModule(){
            @Override
            public void install(){
                bind( Scenario.class ).toInstance( scenario );
                bind( EventsManager.class ).toInstance( eventsManager );
                bind( EmissionModule.class ) ;
            }
        };


        com.google.inject.Injector injector = createInjector(scenario.getConfig(), module);

        EmissionModule emissionModule = injector.getInstance(EmissionModule.class);

        EventWriterXML emissionEventWriter = new EventWriterXML(eventsFileWithEmission);
        emissionModule.getEmissionEventsManager().addHandler(emissionEventWriter);

        MatsimEventsReader matsimEventsReader = new MatsimEventsReader(eventsManager);
        matsimEventsReader.readFile(eventsFileWithoutEmissions);

        emissionEventWriter.closeFile();
        logger.warn("Created Emission events file.");
    }

    private void runEmissionGridAnalyzer(int year, Day day, String eventsFileWithEmission, double scalingFactor) {
        logger.info("Creating grid cell air pollutant exposure for year " + year + ", day " + day);

        logger.info("Apply scale factor: " + scalingFactor + " to emission grid analyzer.");

        EmissionSpatialDispersion gridAnalyzer =	new	EmissionSpatialDispersion.Builder()
                .withNetwork(scenario.getNetwork())
                .withTimeBinSize(EMISSION_TIME_BIN_SIZE)
                .withGridSize(EMISSION_GRID_SIZE)
                .withSmoothingRadius(EMISSION_SMOOTH_RADIUS)
                .withCountScaleFactor(1./scalingFactor)
                .withGridType(EmissionSpatialDispersion.GridType.Square)
                .build();

        gridAnalyzer.processTimeBinsWithEmissions(eventsFileWithEmission);

        while(gridAnalyzer.hasNextTimeBin()){
            assembleRpConcentration(year, day, gridAnalyzer.processNextTimeBin(receiverPoints));
        }

    }

    private void assembleRpConcentration(int year, Day day, Tuple<Double, Grid<Map<Pollutant, Float>>>  gridEmissionMap) {
        logger.warn("Updating link air pollutant concentration for year: " + year + "| day of week: " + day + "| time of day: " + gridEmissionMap.getFirst() + ".");
        int startTime = (int) Math.floor(gridEmissionMap.getFirst());

        Grid<Map<Pollutant,	Float>> grid =	gridEmissionMap.getSecond();

        for(Link link : scenario.getNetwork().getLinks().values()){
            Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin =  ((DataContainerHealth)dataContainer).getLinkInfo().get(link.getId()).getExposure2Pollutant2TimeBin();
            Grid.Cell<Map<Pollutant,Float>> toNodeCell = grid.getCell(new Coordinate(link.getToNode().getCoord().getX(),link.getToNode().getCoord().getY()));
            Grid.Cell<Map<Pollutant,Float>> fromNodeCell = grid.getCell(new Coordinate(link.getFromNode().getCoord().getX(),link.getFromNode().getCoord().getY()));
            Grid.Cell<Map<Pollutant,Float>> middleNodeCell = grid.getCell(new Coordinate(link.getCoord().getX(),link.getCoord().getY()));
            for(Pollutant pollutant : pollutantSet){
                //use avg of link from node, to node and middle point as link pollutant concentration?
                double toNodeCellExposure = 0;
                double fromNodeCellExposure = 0;
                double middleNodeCellExposure = 0;
                if(toNodeCell!=null){
                    if(toNodeCell.getValue().containsKey(pollutant)) {
                        toNodeCellExposure = toNodeCell.getValue().get(pollutant);
                    }
                }
                if(fromNodeCell!=null){
                    if(fromNodeCell.getValue().containsKey(pollutant)){
                        fromNodeCellExposure = fromNodeCell.getValue().get(pollutant);
                    }
                }
                if(middleNodeCell!=null){
                    if(middleNodeCell.getValue().containsKey(pollutant)){
                        middleNodeCellExposure = middleNodeCell.getValue().get(pollutant);
                    }
                }
                double avg = (toNodeCellExposure + fromNodeCellExposure + middleNodeCellExposure * 2)/4;
                if(exposure2Pollutant2TimeBin.get(pollutant)==null){
                    OpenIntFloatHashMap exposureByTimeBin = new OpenIntFloatHashMap();
                    exposureByTimeBin.put(startTime, (float) avg);
                    exposure2Pollutant2TimeBin.put(pollutant, exposureByTimeBin);
                }else {
                    exposure2Pollutant2TimeBin.get(pollutant).put(startTime, (float) avg);
                }
            }
            ((DataContainerHealth)dataContainer).getLinkInfo().get(link.getId()).setExposure2Pollutant2TimeBin(exposure2Pollutant2TimeBin);
        }

        logger.warn("Update link air pollutant concentration for year: " + year + "| day of week: " + day + " finished.");

        logger.warn("Updating activity location air pollutant concentration for year: " + year + "| day of week: " + day + "| time of day: " + gridEmissionMap.getFirst() + ".");

        for(ActivityLocation activityLocation : ((DataContainerHealth)dataContainer).getActivityLocations().values()){

            Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin =  ((DataContainerHealth)dataContainer).getActivityLocations().get(activityLocation.getLocationId()).getExposure2Pollutant2TimeBin();

            Coordinate coordinate = activityLocation.getCoordinate();

            Grid.Cell<Map<Pollutant,Float>> locationCell = grid.getCell(coordinate);

            if(locationCell == null) {
                logger.warn("No grid cell found for activity location coordinate: {}. Skipping concentration update for this location.", coordinate);
                continue;
            }

            for(Pollutant pollutant : pollutantSet){
                if(exposure2Pollutant2TimeBin.get(pollutant)==null){
                    OpenIntFloatHashMap exposureByTimeBin = new OpenIntFloatHashMap();
                    exposureByTimeBin.put(startTime, locationCell.getValue().getOrDefault(pollutant, 0.f));
                    exposure2Pollutant2TimeBin.put(pollutant, exposureByTimeBin);
                }else {
                    exposure2Pollutant2TimeBin.get(pollutant).put(startTime, locationCell.getValue().getOrDefault(pollutant,0.f));
                }
            }
        }

        logger.warn("Update activity location air pollutant concentration for year: " + year + "| day of week: " + day + "| time of day: " + gridEmissionMap.getFirst() + "finished.");

    }

    @Deprecated
    public void endYear(int year, Day day) {
        logger.warn("Air pollutant exposure model end year:" + year);
        if(properties.main.startYear == year) {
            latestMatsimYear = year;
            final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                    + properties.main.scenarioName + "/matsim/" + latestMatsimYear;
            scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);
            scenario.getConfig().controller().setOutputDirectory(outputDirectoryRoot);
            prepareConfig();
            String eventFileWithoutEmissions = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events.xml.gz";
            String eventFileWithEmissions = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events_emission.xml.gz";
            String vehicleFile = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + year + ".output_vehicles.xml.gz";
            String vehicleFileWithEmissionType = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + year + ".vehicles_emission.xml.gz";
            CreateVehicles createVehicles = new CreateVehicles(scenario.getConfig());
            createVehicles.runVehicleType();
            createVehicles.runVehicle(vehicleFile, vehicleFileWithEmissionType);
            updateConfig(day, vehicleFileWithEmissionType);
            ScenarioUtils.loadScenario(scenario);
            createEmissionEventsOffline(eventFileWithoutEmissions,eventFileWithEmissions);
            double scalingFactor = properties.main.scaleFactor * properties.healthData.matsim_scale_factor_car;
            runEmissionGridAnalyzer(year,day, eventFileWithEmissions, scalingFactor);
        } else if(properties.transportModel.transportModelYears.contains(year + 1)) {//why year +1
            latestMatsimYear = year + 1;
            final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                    + properties.main.scenarioName + "/matsim/" + latestMatsimYear;
            scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);
            scenario.getConfig().controller().setOutputDirectory(outputDirectoryRoot);
            prepareConfig();
            String eventFileWithoutEmissions = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events.xml.gz";
            String eventFileWithEmissions = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events_emission.xml.gz";
            String vehicleFile = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + year + ".output_vehicles.xml.gz";
            String vehicleFileWithEmissionType = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + year + ".vehicles_emission.xml.gz";
            CreateVehicles createVehicles = new CreateVehicles(scenario.getConfig());
            createVehicles.runVehicleType();
            createVehicles.runVehicle(vehicleFile, vehicleFileWithEmissionType);
            updateConfig(day, vehicleFileWithEmissionType);
            ScenarioUtils.loadScenario(scenario);
            createEmissionEventsOffline(eventFileWithoutEmissions,eventFileWithEmissions);
            double scalingFactor = properties.main.scaleFactor * properties.healthData.matsim_scale_factor_car;
            runEmissionGridAnalyzer(year,day, eventFileWithEmissions, scalingFactor);
        }

        final String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/";
        new PollutantConcentrationWriter().writeData((DataContainerHealth) dataContainer, outputDirectory, day);

        System.gc();
    }

    @Deprecated
    public Config prepareConfig() {
        if(scenario.getConfig().getModules().containsKey("emissions")){
            scenario.getConfig().removeModule("emissions");
        }
        scenario.getConfig().travelTimeCalculator().setTraveltimeBinSize(3600);
        EmissionsConfigGroup emissionsConfig= new EmissionsConfigGroup();
        emissionsConfig.setDetailedVsAverageLookupBehavior(EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
        emissionsConfig.setAverageColdEmissionFactorsFile(Properties.get().main.baseDirectory+ Properties.get().healthData.COLD_EMISSION_FILE);
        emissionsConfig.setAverageWarmEmissionFactorsFile(Properties.get().main.baseDirectory+ Properties.get().healthData.HOT_EMISSION_FILE);
        emissionsConfig.setNonScenarioVehicles(EmissionsConfigGroup.NonScenarioVehicles.ignore);
        emissionsConfig.setHbefaVehicleDescriptionSource(EmissionsConfigGroup.HbefaVehicleDescriptionSource.fromVehicleTypeDescription);
        scenario.getConfig().addModule(emissionsConfig);
        return scenario.getConfig();
    }

    @Deprecated
    public Config updateConfig(Day day, String vehicleFile) {

        String populationFile = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + latestMatsimYear + ".output_plans.xml.gz";
        scenario.getConfig().vehicles().setVehiclesFile(vehicleFile);
        scenario.getConfig().plans().setInputFile(populationFile);

        return scenario.getConfig();
    }
}
