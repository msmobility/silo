package de.tum.bgu.msm.scenarios.health;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.scenarios.health.emission.CreateVehicles;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.analysis.spatial.Grid;
import org.matsim.contrib.analysis.time.TimeBinMap;
import org.matsim.contrib.emissions.EmissionModule;
import org.matsim.contrib.emissions.Pollutant;
import org.matsim.contrib.emissions.analysis.EmissionGridAnalyzer;
import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Injector;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.EventWriterXML;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.VehicleType;

import java.util.*;

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

        Pollutant[] pollutants = new Pollutant[]{Pollutant.NO2,Pollutant.PM2_5,Pollutant.PM2_5_non_exhaust};
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

            for(Day day : Day.values()){
                String eventFileWithoutEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events.xml.gz";
                String eventFileWithEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events_emission.xml.gz";
                String individualVehicleFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".vehicles_emission.xml.gz";

                Config config = prepareConfig(day, individualVehicleFile);

                CreateVehicles createVehicles = new CreateVehicles(scenario);
                createVehicles.run(eventFileWithoutEmissions, individualVehicleFile);
                scenario = ScenarioUtils.loadScenario(config);
                createEmissionEventsOffline(eventFileWithoutEmissions,eventFileWithEmissions);
                assembleLinkExposure(year, day, runEmissionGridAnalyzer(year,eventFileWithEmissions));
            }

        } else if(properties.transportModel.transportModelYears.contains(year + 1)) {//why year +1
            latestMatsimYear = year + 1;
            final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                    + properties.main.scenarioName + "/matsim/" + latestMatsimYear;
            scenario.getConfig().controler().setOutputDirectory(outputDirectoryRoot);

            for(Day day : Day.values()){
                String eventFileWithoutEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events.xml.gz";
                String eventFileWithEmissions = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".output_events_emission.xml.gz";
                String individualVehicleFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + year + ".vehicles_emission.xml.gz";
                Config config = prepareConfig(day, individualVehicleFile);

                CreateVehicles createVehicles = new CreateVehicles(scenario);
                createVehicles.run(eventFileWithoutEmissions, individualVehicleFile);
                scenario = ScenarioUtils.loadScenario(config);
                createEmissionEventsOffline(eventFileWithoutEmissions,eventFileWithEmissions);
                assembleLinkExposure(year, day, runEmissionGridAnalyzer(year,eventFileWithEmissions));
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


        com.google.inject.Injector injector = Injector.createInjector(scenario.getConfig(), module);

        EmissionModule emissionModule = injector.getInstance(EmissionModule.class);

        EventWriterXML emissionEventWriter = new EventWriterXML(eventsFileWithEmission);
        emissionModule.getEmissionEventsManager().addHandler(emissionEventWriter);

        for(VehicleType type : scenario.getVehicles().getVehicleTypes().values()){
            logger.warn("Vehicle type: " + type.getId().toString());
        }

        logger.warn("Number of vehicles: " + scenario.getVehicles().getVehicles().size());

        MatsimEventsReader matsimEventsReader = new MatsimEventsReader(eventsManager);
        matsimEventsReader.readFile(eventsFileWithoutEmissions);

        emissionEventWriter.closeFile();
        logger.warn("Created Emission events file.");
    }

    public Config prepareConfig(Day day, String vehicleFile) {
        scenario.getConfig().travelTimeCalculator().setTraveltimeBinSize(3600);

        EmissionsConfigGroup emissionsConfig= new EmissionsConfigGroup();
        emissionsConfig.setDetailedVsAverageLookupBehavior(EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
        emissionsConfig.setAverageColdEmissionFactorsFile(Properties.get().main.baseDirectory+"input/mito/trafficAssignment/EFA_ColdStart_Vehcat_EFA_COLD_VehCat_2.txt");
        emissionsConfig.setAverageWarmEmissionFactorsFile(Properties.get().main.baseDirectory+"input/mito/trafficAssignment/EFA_HOT_Vehcat_EFA_HOT_VehCat_2.txt");
        emissionsConfig.setNonScenarioVehicles(EmissionsConfigGroup.NonScenarioVehicles.ignore);
        emissionsConfig.setHbefaRoadTypeSource(EmissionsConfigGroup.HbefaRoadTypeSource.fromLinkAttributes);
        emissionsConfig.setHbefaVehicleDescriptionSource(EmissionsConfigGroup.HbefaVehicleDescriptionSource.fromVehicleTypeDescription);
        scenario.getConfig().addModule(emissionsConfig);
        logger.warn(((EmissionsConfigGroup)scenario.getConfig().getModules().get(EmissionsConfigGroup.GROUP_NAME)).getAverageColdEmissionFactorsFile());
        logger.warn(((EmissionsConfigGroup)scenario.getConfig().getModules().get(EmissionsConfigGroup.GROUP_NAME)).getAverageWarmEmissionFactorsFile());

        String networkFile = Properties.get().main.baseDirectory+"input/mito/trafficAssignment/studyNetworkDenseCarHealth_Hbefa.xml.gz";
        String populationFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + latestMatsimYear + ".output_plans.xml.gz";


        scenario.getConfig().vehicles().setVehiclesFile(vehicleFile);
        scenario.getConfig().network().setInputFile(networkFile);
        scenario.getConfig().plans().setInputFile(populationFile);

        return scenario.getConfig();
    }


    private TimeBinMap<Grid<Map<Pollutant, Double>>> runEmissionGridAnalyzer(int year, String eventsFileWithEmission) {
        logger.info("Creating grid cell air pollutant exposure for year " + year + ".");

        //new MatsimNetworkReader(scenario.getNetwork()).readFile(scenario.getConfig().network().getInputFile());

        EmissionGridAnalyzer gridAnalyzer =	new	EmissionGridAnalyzer.Builder()
                .withNetwork(scenario.getNetwork())
                .withTimeBinSize(EMISSION_TIME_BIN_SIZE)
                .withGridSize(EMISSION_GRID_SIZE)
                .withSmoothingRadius(EMISSION_SMOOTH_RADIUS)
                .withGridType(EmissionGridAnalyzer.GridType.Square)
                .build();

        return gridAnalyzer.process(eventsFileWithEmission);
    }

    private void assembleLinkExposure(int year, Day day, TimeBinMap<Grid<Map<Pollutant, Double>>> gridTimeBinMap) {
        logger.warn("Updating link air pollutant exposure for year: " + year + "| day of week: " + day + ".");

        for	(TimeBinMap.TimeBin<Grid<Map<Pollutant,	Double>>> timebin :	gridTimeBinMap.getTimeBins()){
            int	startTime = (int) timebin.getStartTime();
            Grid<Map<Pollutant,	Double>> grid =	timebin.getValue();
            for(Link link : scenario.getNetwork().getLinks().values()){
                Map<Pollutant, Map<Integer, Double>> exposure2Pollutant2TimeBin =  new HashMap<>();

                Grid.Cell<Map<Pollutant,Double>> toNodeCell = grid.getCell(new Coordinate(link.getToNode().getCoord().getX(),link.getToNode().getCoord().getY()));
                Grid.Cell<Map<Pollutant,Double>> fromNodeCell = grid.getCell(new Coordinate(link.getFromNode().getCoord().getX(),link.getToNode().getCoord().getY()));

                for(Pollutant pollutant : pollutantSet){
                    //TODO: use avg as link exposure?
                    double avg = (toNodeCell.getValue().get(pollutant) + fromNodeCell.getValue().get(pollutant))/2;
                    if(exposure2Pollutant2TimeBin.get(pollutant)==null){
                        Map<Integer, Double> exposureByTimeBin = new HashMap<>();
                        exposureByTimeBin.put(startTime, avg);
                        exposure2Pollutant2TimeBin.put(pollutant, exposureByTimeBin);
                    }else {
                        exposure2Pollutant2TimeBin.get(pollutant).put(startTime, avg);
                    }
                }

                ((HealthDataContainerImpl)dataContainer).getLinkInfoByDay().get(day).get(link.getId()).setExposure2Pollutant2TimeBin(exposure2Pollutant2TimeBin);
            }
        }
        logger.warn("Updating link air pollutant exposure for year: " + year + "| day of week: " + day + " finished.");



    }
}
