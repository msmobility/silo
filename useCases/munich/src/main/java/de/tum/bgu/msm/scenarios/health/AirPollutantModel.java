package de.tum.bgu.msm.scenarios.health;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.resources.Resources;
import de.tum.bgu.msm.scenarios.health.emission.CreateVehicles;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.analysis.spatial.Grid;
import org.matsim.contrib.analysis.time.TimeBinMap;
import org.matsim.contrib.emissions.EmissionModule;
import org.matsim.contrib.emissions.Pollutant;
import org.matsim.contrib.emissions.analysis.EmissionGridAnalyzerMSM;
import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Injector;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.EventWriterXML;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.Tuple;

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

    public AirPollutantModel(DataContainer dataContainer, Properties properties, Random random, Config config) {
        super(dataContainer, properties, random);
        this.initialMatsimConfig = config;
        scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);

        Pollutant[] pollutants = new Pollutant[]{Pollutant.NO2,Pollutant.PM,Pollutant.PM_non_exhaust,Pollutant.PM2_5, Pollutant.PM2_5_non_exhaust};
        for(Pollutant pollutant : pollutants){
            this.pollutantSet.add(pollutant);
        }
        ((HealthDataContainerImpl)dataContainer).setPollutantSet(pollutantSet);
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
            scenario.getConfig().controler().setOutputDirectory(outputDirectoryRoot);
            prepareConfig();
            CreateVehicles createVehicles = new CreateVehicles(scenario);
            createVehicles.runVehicleType();

            for(Day day : Day.values()){
                String eventFileWithoutEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events.xml.gz";
                String eventFileWithEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events_emission.xml.gz";
                String individualVehicleFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".vehicles_emission.xml.gz";

                updateConfig(day, individualVehicleFile);

                createVehicles.runVehicle(eventFileWithoutEmissions, individualVehicleFile);
                scenario = ScenarioUtils.loadScenario(scenario.getConfig());
                createEmissionEventsOffline(eventFileWithoutEmissions,eventFileWithEmissions);
                runEmissionGridAnalyzer(year,day, eventFileWithEmissions);
            }

        } else if(properties.transportModel.transportModelYears.contains(year + 1)) {//why year +1
            latestMatsimYear = year + 1;
            final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                    + properties.main.scenarioName + "/matsim/" + latestMatsimYear;
            scenario.getConfig().controler().setOutputDirectory(outputDirectoryRoot);
            prepareConfig();
            CreateVehicles createVehicles = new CreateVehicles(scenario);
            createVehicles.runVehicleType();

            for(Day day : Day.values()){
                String eventFileWithoutEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events.xml.gz";
                String eventFileWithEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events_emission.xml.gz";
                String individualVehicleFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".vehicles_emission.xml.gz";
                updateConfig(day, individualVehicleFile);

                createVehicles.runVehicle(eventFileWithoutEmissions, individualVehicleFile);

                scenario = ScenarioUtils.loadScenario(scenario.getConfig());
                createEmissionEventsOffline(eventFileWithoutEmissions,eventFileWithEmissions);
                runEmissionGridAnalyzer(year,day, eventFileWithEmissions);
            }
        }
    }


    @Override
    public void endSimulation() {
    }
    
    private void createEmissionEventsOffline(String eventsFileWithoutEmissions, String eventsFileWithEmission) {
        EventsManager eventsManager = EventsUtils.createEventsManager();

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
        scenario.getConfig().travelTimeCalculator().setTraveltimeBinSize(3600);
        EmissionsConfigGroup emissionsConfig= new EmissionsConfigGroup();
        emissionsConfig.setDetailedVsAverageLookupBehavior(EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
        emissionsConfig.setAverageColdEmissionFactorsFile(Properties.get().main.baseDirectory+"input/mito/trafficAssignment/EFA_ColdStart_Vehcat_healthModel.txt");
        emissionsConfig.setAverageWarmEmissionFactorsFile(Properties.get().main.baseDirectory+"input/mito/trafficAssignment/EFA_HOT_Vehcat_healthModel.txt");
        emissionsConfig.setNonScenarioVehicles(EmissionsConfigGroup.NonScenarioVehicles.ignore);
        emissionsConfig.setHbefaRoadTypeSource(EmissionsConfigGroup.HbefaRoadTypeSource.fromLinkAttributes);
        emissionsConfig.setHbefaVehicleDescriptionSource(EmissionsConfigGroup.HbefaVehicleDescriptionSource.fromVehicleTypeDescription);
        scenario.getConfig().addModule(emissionsConfig);

        return scenario.getConfig();
    }

    public Config updateConfig(Day day, String vehicleFile) {
        String networkFile = Properties.get().main.baseDirectory+"input/mito/trafficAssignment/studyNetworkDenseCarHealth_Hbefa.xml.gz";
        String populationFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + latestMatsimYear + ".output_plans.xml.gz";


        scenario.getConfig().vehicles().setVehiclesFile(vehicleFile);
        scenario.getConfig().network().setInputFile(networkFile);
        scenario.getConfig().plans().setInputFile(populationFile);

        return scenario.getConfig();
    }


    private void runEmissionGridAnalyzer(int year, Day day, String eventsFileWithEmission) {
        logger.info("Creating grid cell air pollutant exposure for year " + year + ", day " + day);

        //new MatsimNetworkReader(scenario.getNetwork()).readFile(scenario.getConfig().network().getInputFile());
        double scalingFactor = properties.main.scaleFactor * Double.parseDouble(Resources.instance.getString(de.tum.bgu.msm.resources.Properties.TRIP_SCALING_FACTOR));

        EmissionGridAnalyzerMSM gridAnalyzer =	new	EmissionGridAnalyzerMSM.Builder()
                .withNetwork(scenario.getNetwork())
                .withTimeBinSize(EMISSION_TIME_BIN_SIZE)
                .withGridSize(EMISSION_GRID_SIZE)
                .withSmoothingRadius(EMISSION_SMOOTH_RADIUS)
                .withCountScaleFactor(1./scalingFactor)
                .withGridType(EmissionGridAnalyzerMSM.GridType.Square)
                .build();

        gridAnalyzer.processTimeBinsWithEmissions(eventsFileWithEmission);

        while(gridAnalyzer.hasNextTimeBin()){
            assembleLinkExposure(year, day, gridAnalyzer.processNextTimeBin());
        }
    }

    private void assembleLinkExposure(int year, Day day, Tuple<Double, Grid<Map<Pollutant, Float>>>  gridEmissionMap) {
        logger.warn("Updating link air pollutant exposure for year: " + year + "| day of week: " + day + "| time of day: " + gridEmissionMap.getFirst() + ".");

        int startTime = (int) Math.floor(gridEmissionMap.getFirst());
        Grid<Map<Pollutant,	Float>> grid =	gridEmissionMap.getSecond();

        for(Link link : scenario.getNetwork().getLinks().values()){
            Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin =  new HashMap<>();

            Grid.Cell<Map<Pollutant,Float>> toNodeCell = grid.getCell(new Coordinate(link.getToNode().getCoord().getX(),link.getToNode().getCoord().getY()));
            Grid.Cell<Map<Pollutant,Float>> fromNodeCell = grid.getCell(new Coordinate(link.getFromNode().getCoord().getX(),link.getFromNode().getCoord().getY()));


            for(Pollutant pollutant : pollutantSet){
                //TODO: use avg as link exposure?
                double toNodeCellExposure = 0;
                double fromNodeCellExposure = 0;
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

                double avg = (toNodeCellExposure + fromNodeCellExposure)/2;
                if(exposure2Pollutant2TimeBin.get(pollutant)==null){
                    OpenIntFloatHashMap exposureByTimeBin = new OpenIntFloatHashMap();
                    exposureByTimeBin.put(startTime, (float) avg);
                    exposure2Pollutant2TimeBin.put(pollutant, exposureByTimeBin);
                }else {
                    exposure2Pollutant2TimeBin.get(pollutant).put(startTime, (float) avg);
                }
            }

            ((HealthDataContainerImpl)dataContainer).getLinkInfoByDay().get(day).get(link.getId()).setExposure2Pollutant2TimeBin(exposure2Pollutant2TimeBin);
        }

        logger.warn("Updating link air pollutant exposure for year: " + year + "| day of week: " + day + " finished.");



    }


    private void assembleLinkExposure(int year, Day day, TimeBinMap<Grid<Map<Pollutant, Float>>> gridTimeBinMap) {
        logger.warn("Updating link air pollutant exposure for year: " + year + "| day of week: " + day + ".");

        for	(TimeBinMap.TimeBin<Grid<Map<Pollutant,	Float>>> timebin :	gridTimeBinMap.getTimeBins()){
            int	startTime = (int) timebin.getStartTime();
            Grid<Map<Pollutant,	Float>> grid =	timebin.getValue();
            for(Link link : scenario.getNetwork().getLinks().values()){
                Map<Pollutant, OpenIntFloatHashMap> exposure2Pollutant2TimeBin =  new HashMap<>();

                Grid.Cell<Map<Pollutant,Float>> toNodeCell = grid.getCell(new Coordinate(link.getToNode().getCoord().getX(),link.getToNode().getCoord().getY()));
                Grid.Cell<Map<Pollutant,Float>> fromNodeCell = grid.getCell(new Coordinate(link.getFromNode().getCoord().getX(),link.getToNode().getCoord().getY()));

                for(Pollutant pollutant : pollutantSet){
                    //TODO: use avg as link exposure?
                    double avg = (toNodeCell.getValue().get(pollutant) + fromNodeCell.getValue().get(pollutant))/2;
                    if(exposure2Pollutant2TimeBin.get(pollutant)==null){
                        OpenIntFloatHashMap exposureByTimeBin = new OpenIntFloatHashMap();
                        exposureByTimeBin.put(startTime, (float) avg);
                        exposure2Pollutant2TimeBin.put(pollutant, exposureByTimeBin);
                    }else {
                        exposure2Pollutant2TimeBin.get(pollutant).put(startTime, (float) avg);
                    }
                }

                ((HealthDataContainerImpl)dataContainer).getLinkInfoByDay().get(day).get(link.getId()).setExposure2Pollutant2TimeBin(exposure2Pollutant2TimeBin);
            }
        }
        logger.warn("Updating link air pollutant exposure for year: " + year + "| day of week: " + day + " finished.");



    }
}
