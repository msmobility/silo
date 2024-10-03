package de.tum.bgu.msm.health.airPollutant;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.health.airPollutant.dispersion.Grid;
import de.tum.bgu.msm.health.airPollutant.emission.CreateVehicles;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.airPollutant.dispersion.EmissionGridAnalyzerMSM;
import de.tum.bgu.msm.health.data.LinkInfo;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.resources.Resources;
import de.tum.bgu.msm.util.MitoUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.emissions.EmissionModule;
import org.matsim.contrib.emissions.Pollutant;
import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.EventWriterXML;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.Tuple;

import java.io.PrintWriter;
import java.util.*;

import static org.matsim.core.controler.Injector.createInjector;

public class AirPollutantModel extends AbstractModel implements ModelUpdateListener {
    public static final int EMISSION_TIME_BIN_SIZE = 3600*4;
    private static final double EMISSION_GRID_SIZE = 20;
    private static final double EMISSION_SMOOTH_RADIUS = 100;
    private int latestMatsimYear = -1;
    private static final Logger logger = Logger.getLogger(AirPollutantModel.class);
    private Scenario scenario;
    private final Config initialMatsimConfig;
    private final Set<Pollutant> pollutantSet = new HashSet<>();
    private List<Day> simulatedDays;

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

    public void endYear(int year, Day day) {
        logger.warn("Air pollutant exposure model end year:" + year);
        if(properties.main.startYear == year) {
            latestMatsimYear = year;
            final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                    + properties.main.scenarioName + "/matsim/" + latestMatsimYear;
                scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);
                scenario.getConfig().controler().setOutputDirectory(outputDirectoryRoot);
                prepareConfig();
                String eventFileWithoutEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events.xml.gz";
                String eventFileWithEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events_emission.xml.gz";
                String vehicleFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_vehicles.xml.gz";
                String vehicleFileWithEmissionType = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".vehicles_emission.xml.gz";
                //CreateVehicles createVehicles = new CreateVehicles(scenario);
                //createVehicles.runVehicleType();
                //createVehicles.runVehicle(vehicleFile, vehicleFileWithEmissionType);
                updateConfig(day, vehicleFileWithEmissionType);
                scenario = ScenarioUtils.loadScenario(scenario.getConfig());
                //createEmissionEventsOffline(eventFileWithoutEmissions,eventFileWithEmissions);
                runEmissionGridAnalyzer(year,day, eventFileWithEmissions);
        } else if(properties.transportModel.transportModelYears.contains(year + 1)) {//why year +1
            latestMatsimYear = year + 1;
            final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                    + properties.main.scenarioName + "/matsim/" + latestMatsimYear;
            scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);
            scenario.getConfig().controler().setOutputDirectory(outputDirectoryRoot);
            prepareConfig();
            String eventFileWithoutEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events.xml.gz";
            String eventFileWithEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events_emission.xml.gz";
            String vehicleFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_vehicles.xml.gz";
            String vehicleFileWithEmissionType = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".vehicles_emission.xml.gz";
            //CreateVehicles createVehicles = new CreateVehicles(scenario);
            //createVehicles.runVehicleType();
            //createVehicles.runVehicle(vehicleFile, vehicleFileWithEmissionType);
            updateConfig(day, vehicleFileWithEmissionType);
            scenario = ScenarioUtils.loadScenario(scenario.getConfig());
            //createEmissionEventsOffline(eventFileWithoutEmissions,eventFileWithEmissions);
            runEmissionGridAnalyzer(year,day, eventFileWithEmissions);
        }

        final String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/";
        String filett = outputDirectory
                + "linkExposure_"
                + day
                + ".csv";
        writeLinkExposure(filett);

        System.gc();
    }


    @Override
    public void endSimulation() {

    }

    private void writeLinkExposure(String path) {
        logger.info("  Writing link exposure health indicators file");
        PrintWriter pwh = MitoUtil.openFileForSequentialWriting(path, false);
        pwh.println("linkId,pollutant,timebin,value");
        //order of Set is not fixed
            for (LinkInfo linkInfo : ((DataContainerHealth) dataContainer).getLinkInfo().values()) {
                for (Pollutant pollutant : linkInfo.getExposure2Pollutant2TimeBin().keySet()) {
                    for (int timebin : linkInfo.getExposure2Pollutant2TimeBin().get(pollutant).keys().elements()) {
                        pwh.print(linkInfo.getLinkId());
                        pwh.print(",");
                        pwh.print(pollutant.name());
                        pwh.print(",");
                        pwh.print(timebin);
                        pwh.print(",");
                        pwh.print(linkInfo.getExposure2Pollutant2TimeBin().get(pollutant).get(timebin));
                        pwh.println();
                    }
                }
            }

        pwh.close();
    }
    
    private void createEmissionEventsOffline(String eventsFileWithoutEmissions, String eventsFileWithEmission) {
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

    public Config prepareConfig() {
        if(scenario.getConfig().getModules().containsKey("emissions")){
            scenario.getConfig().removeModule("emissions");
        }
        scenario.getConfig().travelTimeCalculator().setTraveltimeBinSize(3600);
        EmissionsConfigGroup emissionsConfig= new EmissionsConfigGroup();
        emissionsConfig.setDetailedVsAverageLookupBehavior(EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
        emissionsConfig.setAverageColdEmissionFactorsFile(Properties.get().main.baseDirectory+"input/mito/trafficAssignment/EFA_ColdStart_Vehcat_healthModelWithTruck.txt");
        emissionsConfig.setAverageWarmEmissionFactorsFile(Properties.get().main.baseDirectory+"input/mito/trafficAssignment/EFA_HOT_Vehcat_healthModelWithTruck.txt");
        emissionsConfig.setNonScenarioVehicles(EmissionsConfigGroup.NonScenarioVehicles.ignore);
        emissionsConfig.setHbefaRoadTypeSource(EmissionsConfigGroup.HbefaRoadTypeSource.fromLinkAttributes);
        emissionsConfig.setHbefaVehicleDescriptionSource(EmissionsConfigGroup.HbefaVehicleDescriptionSource.fromVehicleTypeDescription);
        scenario.getConfig().addModule(emissionsConfig);
        return scenario.getConfig();
    }

    public Config updateConfig(Day day, String vehicleFile) {
        String populationFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + latestMatsimYear + ".output_plans.xml.gz";

        scenario.getConfig().vehicles().setVehiclesFile(vehicleFile);
        scenario.getConfig().plans().setInputFile(populationFile);

        return scenario.getConfig();
    }


    private void runEmissionGridAnalyzer(int year, Day day, String eventsFileWithEmission) {
        logger.info("Creating grid cell air pollutant exposure for year " + year + ", day " + day);

        //double scalingFactor = properties.main.scaleFactor * Double.parseDouble(Resources.instance.getString(de.tum.bgu.msm.resources.Properties.TRIP_SCALING_FACTOR));
        double scalingFactor = 0.1;
        System.out.println("current memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

        EmissionGridAnalyzerMSM gridAnalyzer =	new	EmissionGridAnalyzerMSM.Builder()
                .withNetwork(scenario.getNetwork())
                .withTimeBinSize(EMISSION_TIME_BIN_SIZE)
                .withGridSize(EMISSION_GRID_SIZE)
                .withSmoothingRadius(EMISSION_SMOOTH_RADIUS)
                .withCountScaleFactor(1./scalingFactor)
                .withGridType(EmissionGridAnalyzerMSM.GridType.Square)
                .build();
        System.out.println("current memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

        gridAnalyzer.processTimeBinsWithEmissions(eventsFileWithEmission);
        System.out.println("current memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

        while(gridAnalyzer.hasNextTimeBin()){
            assembleLinkExposure(year, day, gridAnalyzer.processNextTimeBin());
        }
        System.out.println("current memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

    }

    private void assembleLinkExposure(int year, Day day, Tuple<Double, Grid<Map<Pollutant, Float>>>  gridEmissionMap) {
        logger.warn("Updating link air pollutant exposure for year: " + year + "| day of week: " + day + "| time of day: " + gridEmissionMap.getFirst() + ".");
        int startTime = (int) Math.floor(gridEmissionMap.getFirst());
        System.out.println("current memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

        Grid<Map<Pollutant,	Float>> grid =	gridEmissionMap.getSecond();
        System.out.println("current memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

        for(Link link : scenario.getNetwork().getLinks().values()){
            Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin =  ((DataContainerHealth)dataContainer).getLinkInfo().get(link.getId()).getExposure2Pollutant2TimeBin();
            Grid.Cell<Map<Pollutant,Float>> toNodeCell = grid.getCell(new Coordinate(link.getToNode().getCoord().getX(),link.getToNode().getCoord().getY()));
            Grid.Cell<Map<Pollutant,Float>> fromNodeCell = grid.getCell(new Coordinate(link.getFromNode().getCoord().getX(),link.getFromNode().getCoord().getY()));
            Grid.Cell<Map<Pollutant,Float>> middleNodeCell = grid.getCell(new Coordinate(link.getCoord().getX(),link.getCoord().getY()));
            for(Pollutant pollutant : pollutantSet){
                //TODO: use avg of link from node, to node and middle point as link pollutant concentration?
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
                double avg = (toNodeCellExposure + fromNodeCellExposure + middleNodeCellExposure)/3;
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

        System.out.println("current memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

        logger.warn("Updating link air pollutant exposure for year: " + year + "| day of week: " + day + " finished.");
    }

    public void runOffineWithEmission(int year, boolean createEmissionEvents) {
        logger.warn("Air pollutant exposure model end year:" + year);
        if(properties.main.startYear == year) {
            latestMatsimYear = year;
            final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                    + properties.main.scenarioName + "/matsim/" + latestMatsimYear;
            for(Day day : simulatedDays){
                scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);
                scenario.getConfig().controler().setOutputDirectory(outputDirectoryRoot);
                prepareConfig();
                String eventFileWithoutEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events.xml.gz";
                String eventFileWithEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events_emission.xml.gz";
                String vehicleFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_vehicles.xml.gz";
                String vehicleFileWithEmissionType = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".vehicles_emission.xml.gz";

                if(createEmissionEvents){
                    CreateVehicles createVehicles = new CreateVehicles(scenario);
                    createVehicles.runVehicleType();
                    createVehicles.runVehicle(vehicleFile, vehicleFileWithEmissionType);
                    updateConfig(day, vehicleFileWithEmissionType);
                    scenario = ScenarioUtils.loadScenario(scenario.getConfig());
                    createEmissionEventsOffline(eventFileWithoutEmissions,eventFileWithEmissions);
                    runEmissionGridAnalyzer(year,day, eventFileWithEmissions);
                }else{
                    updateConfig(day, vehicleFileWithEmissionType);
                    scenario = ScenarioUtils.loadScenario(scenario.getConfig());
                    runEmissionGridAnalyzer(year,day, eventFileWithEmissions);
                }

            }
        } else if(properties.transportModel.transportModelYears.contains(year + 1)) {//why year +1
            latestMatsimYear = year + 1;
            final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                    + properties.main.scenarioName + "/matsim/" + latestMatsimYear;
            for(Day day : simulatedDays){

                scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);
                scenario.getConfig().controler().setOutputDirectory(outputDirectoryRoot);
                prepareConfig();
                String eventFileWithoutEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events.xml.gz";
                String eventFileWithEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events_emission.xml.gz";
                String vehicleFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_vehicles.xml.gz";
                String vehicleFileWithEmissionType = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".vehicles_emission.xml.gz";

                if(createEmissionEvents){
                    CreateVehicles createVehicles = new CreateVehicles(scenario);
                    createVehicles.runVehicleType();
                    createVehicles.runVehicle(vehicleFile, vehicleFileWithEmissionType);
                    updateConfig(day, vehicleFileWithEmissionType);
                    scenario = ScenarioUtils.loadScenario(scenario.getConfig());
                    createEmissionEventsOffline(eventFileWithoutEmissions,eventFileWithEmissions);
                    runEmissionGridAnalyzer(year,day, eventFileWithEmissions);
                }else{
                    updateConfig(day, vehicleFileWithEmissionType);
                    scenario = ScenarioUtils.loadScenario(scenario.getConfig());
                    runEmissionGridAnalyzer(year,day, eventFileWithEmissions);
                }
            }
        }

        final String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/";
        String filett = outputDirectory
                + "linkExposure"
                + ".csv";
        writeLinkExposure(filett);
    }
}
