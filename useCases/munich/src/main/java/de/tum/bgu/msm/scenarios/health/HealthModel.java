package de.tum.bgu.msm.scenarios.health;

import com.google.common.collect.Iterables;
import com.google.common.math.LongMath;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.matsim.ZoneConnectorManager;
import de.tum.bgu.msm.matsim.ZoneConnectorManagerImpl;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.moped.util.concurrent.ConcurrentExecutor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.MitoUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.contrib.accidents.AccidentLinkInfo;
import org.matsim.contrib.accidents.AccidentType;
import org.matsim.contrib.dvrp.trafficmonitoring.TravelTimeUtils;
import org.matsim.contrib.emissions.Pollutant;
import org.matsim.core.config.Config;
import org.matsim.core.controler.ControlerDefaults;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.FastAStarLandmarksFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class HealthModel extends AbstractModel implements ModelUpdateListener {
    private int latestMatsimYear = -1;
    private static final Logger logger = Logger.getLogger(HealthModel.class);
    private Map<Integer, MitoTrip> mitoTrips;
    private final Config initialMatsimConfig;

    private final int threads;
    private final ZoneConnectorManager zoneConnectorManager;


    public HealthModel(DataContainer dataContainer, Properties properties, Random random, Config config) {
        super(dataContainer, properties, random);
        this.mitoTrips = ((HealthDataContainerImpl)dataContainer).getMitoTrips();
        this.initialMatsimConfig = config;
        threads = properties.main.numberOfThreads;
        final Collection<Zone> zones = dataContainer.getGeoData().getZones().values();
        zoneConnectorManager = ZoneConnectorManagerImpl.createWeightedZoneConnectors(zones,
                        dataContainer.getRealEstateDataManager(),
                        dataContainer.getHouseholdDataManager());
    }

    @Override
    public void setup() {
        logger.warn("Health model setup: ");
        MutableScenario scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);

        String networkFile;
        if (scenario.getConfig().controler().getRunId() == null || scenario.getConfig().controler().getRunId().equals("")) {
            networkFile = scenario.getConfig().controler().getOutputDirectory() + "/" + Day.weekday + "/car/" + "output_network.xml.gz";
        } else {
            networkFile = scenario.getConfig().controler().getOutputDirectory() + "/" + Day.weekday + "/car/" + scenario.getConfig().controler().getRunId() + ".output_network.xml.gz";
        }

        new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);

        for(Day day : Day.values()){
            Map<Id<Link>, LinkInfo> linkInfoMap = new HashMap<>();
            for(Link link : scenario.getNetwork().getLinks().values()){
                linkInfoMap.put(link.getId(), new LinkInfo(link.getId()));
            }
            ((HealthDataContainerImpl)dataContainer).getLinkInfoByDay().put(day, linkInfoMap);
        }
    }

    @Override
    public void prepareYear(int year) {
    }

    @Override
    public void endYear(int year) {
        logger.warn("Health model end year:" + year);
        if(properties.main.startYear == year) {
            latestMatsimYear = year;
            healthDataAssembler(year);
            calculateIndividualHealthIndicator();
            calculateRelativeRisk();
        } else if(properties.transportModel.transportModelYears.contains(year + 1)) {//why year +1
            latestMatsimYear = year + 1;
            healthDataAssembler(year + 1);
            calculateIndividualHealthIndicator();
            calculateRelativeRisk();
        }
    }

    @Override
    public void endSimulation() {
    }

    private void healthDataAssembler(int year) {
        logger.info("Updating health data for year " + year + ".");
        MutableScenario scenario;
        String eventsFile;
        String networkFile;
        List<MitoTrip> trips;

        for(Day day : Day.values()){
            for(Mode mode : Mode.values()){
                switch (mode){
                    case autoDriver:
                    case autoPassenger:
                        scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);

                        if (scenario.getConfig().controler().getRunId() == null || scenario.getConfig().controler().getRunId().equals("")) {
                            eventsFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "car/" + "output_events.xml.gz";
                            networkFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "car/" + "output_network.xml.gz";
                        } else {
                            eventsFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "car/" + scenario.getConfig().controler().getRunId() + ".output_events.xml.gz";
                            networkFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "car/" + scenario.getConfig().controler().getRunId() + ".output_network.xml.gz";
                        }

                        trips = mitoTrips.values().stream().filter(tt -> tt.getDepartureDay().equals(day) & tt.getTripMode().equals(mode)).collect(Collectors.toList());

                        calculateHealthIndicator(scenario, networkFile, eventsFile, trips, day, mode);
                        break;
                    case bicycle:
                    case walk:
                        scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);

                        if (scenario.getConfig().controler().getRunId() == null || scenario.getConfig().controler().getRunId().equals("")) {
                            eventsFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "bikePed/" + "output_events.xml.gz";
                            networkFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "bikePed/" + "output_network.xml.gz";
                        } else {
                            eventsFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "bikePed/" + scenario.getConfig().controler().getRunId() + ".output_events.xml.gz";
                            networkFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "bikePed/" + scenario.getConfig().controler().getRunId() + ".output_network.xml.gz";
                        }

                        trips  = mitoTrips.values().stream().filter(tt -> tt.getDepartureDay().equals(day) & tt.getTripMode().equals(mode)).collect(Collectors.toList());

                        calculateHealthIndicator(scenario, networkFile, eventsFile, trips, day, mode);
                        break;
                    default:
                        logger.warn("No health model for mode: " + mode);
                }
            }
        }

        //TODO:
        calculateHealthIndicatorAtHome();

        final String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName +"/";
        String filett = outputDirectory
                + "healthIndicators_"
                + year
                + ".csv";
        writeMitoTrips(filett);
    }


    public void calculateHealthIndicator (Scenario scenario, String networkFile, String eventsFile, List<MitoTrip> trips, Day day, Mode mode) {

        final int partitionSize = (int) ((double) trips.size() / Runtime.getRuntime().availableProcessors()) + 1;
        Iterable<List<MitoTrip>> partitions = Iterables.partition(trips, partitionSize);
        logger.info("Partition Size: " + partitionSize);

        TravelTime travelTime = TravelTimeUtils.createTravelTimesFromEvents(scenario, eventsFile);
        TravelDisutility travelDisutility = ControlerDefaults.createDefaultTravelDisutilityFactory(scenario).createTravelDisutility(travelTime);

        new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);

        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Runtime.getRuntime().availableProcessors());

        AtomicInteger counter = new AtomicInteger();

        for (final List<MitoTrip> partition : partitions) {
            LeastCostPathCalculator pathCalculator = new FastAStarLandmarksFactory(threads).createPathCalculator(scenario.getNetwork(), travelDisutility, travelTime);

            executor.addTaskToQueue(() -> {
                try {
                    int id = counter.incrementAndGet();
                    int counterr = 0;
                    for (MitoTrip trip : partition) {

                        if(LongMath.isPowerOfTwo(counterr)) {
                            logger.info(counterr + " in " + id);
                        };

                        Coord originCoord;
                        Coord destinationCoord;
                        Location origin =trip.getTripOrigin();
                        Location destination =trip.getTripDestination();

                        if (origin instanceof MicroLocation && destination instanceof MicroLocation) {
                            // Microlocations case
                            originCoord = CoordUtils.createCoord(((MicroLocation) origin).getCoordinate());
                            destinationCoord = CoordUtils.createCoord(((MicroLocation) destination).getCoordinate());
                        } else if (origin instanceof Zone && destination instanceof Zone) {
                            // Non-microlocations case
                            originCoord = zoneConnectorManager.getCoordsForZone(origin.getZoneId()).get(0);
                            destinationCoord = zoneConnectorManager.getCoordsForZone(destination.getZoneId()).get(0);
                        } else {
                            throw new IllegalArgumentException("Origin and destination have to be consistent in location type!");
                        }

                        Node originNode = NetworkUtils.getNearestNode(scenario.getNetwork(), originCoord);
                        Node destinationNode = NetworkUtils.getNearestNode(scenario.getNetwork(), destinationCoord);

                        LeastCostPathCalculator.Path path = pathCalculator.calcLeastCostPath(originNode, destinationNode,trip.getDepartureTimeInMinutes()*60,null,null);

                        double enterTimeInSecond = trip.getDepartureTimeInMinutes()*60;
                        double tt = 0;
                        double lightInjuryRisk = 0;
                        double severeInjuryRisk = 0;
                        Map<String, Double> exposureMap = new HashMap<>();

                        for(Link link : path.links) {
                            enterTimeInSecond = enterTimeInSecond + tt;
                            LinkInfo linkInfo = ((HealthDataContainerImpl) dataContainer).getLinkInfoByDay().get(day).get(link.getId());
                            if (linkInfo != null) {
                                lightInjuryRisk += getLinkLightInjuryRisk(mode, (int) (enterTimeInSecond / 3600.), linkInfo);
                                severeInjuryRisk += getLinkSevereInjuryRisk(mode, (int) (enterTimeInSecond / 3600.), linkInfo);

                                for (Pollutant pollutant : ((HealthDataContainerImpl) dataContainer).getPollutantSet()) {
                                    double exposure = linkInfo.getExposure2Pollutant2TimeBin().get(pollutant).get((int) (enterTimeInSecond / 3600.));
                                    if (exposureMap.get(pollutant) == null) {
                                        exposureMap.put(pollutant.name(), exposure);
                                    } else {
                                        exposureMap.put(pollutant.name(), exposureMap.get(pollutant) + exposure);
                                    }
                                }

                            }
                            tt = path.travelTime;
                        }
                        trip.setLightInjuryRisk(lightInjuryRisk);
                        trip.setSevereInjuryRisk(severeInjuryRisk);
                        trip.setExposureMap(exposureMap);
                        trip.setPhysicalActivityMmetHours(PhysicalActivity.calculate(
                                trip.getTripMode(),
                                path.links.stream().mapToDouble(x -> x.getLength()).sum(),
                                path.travelTime));

                        counterr++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn(e.getLocalizedMessage());
                    throw new RuntimeException(e);
                }
                return null;
            });
        }
        executor.execute();
    }


    private double getLinkSevereInjuryRisk(Mode mode, int hour, LinkInfo linkInfo) {
        double severeInjuryRisk = 0.;
        switch (mode){
            case autoDriver:
            case autoPassenger:
                severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.CAR).get(hour);
                break;
            case bicycle:
                severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKECAR).get(hour);
                severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKEBIKE).get(hour);
                break;
            case walk:
                severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.PED).get(hour);
                break;
            default:
                throw new RuntimeException("Undefined mode " + mode);
        }


        return severeInjuryRisk;
    }

    private double getLinkLightInjuryRisk(Mode mode, int hour, LinkInfo linkInfo) {
        double lightInjuryRisk = 0.;
        switch (mode){
            case autoDriver:
            case autoPassenger:
                lightInjuryRisk += linkInfo.getLightCasualityExposureByAccidentTypeByTime().get(AccidentType.CAR).get(hour);
                break;
            case bicycle:
                lightInjuryRisk += linkInfo.getLightCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKECAR).get(hour);
                lightInjuryRisk += linkInfo.getLightCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKEBIKE).get(hour);
                break;
            case walk:
                lightInjuryRisk += linkInfo.getLightCasualityExposureByAccidentTypeByTime().get(AccidentType.PED).get(hour);
                break;
            default:
                throw new RuntimeException("Undefined mode " + mode);
        }


        return lightInjuryRisk;
    }

    //TODO: noise and air pollutant exposure at home
    private void calculateHealthIndicatorAtHome() {

    }

    //TODO: Qin
    private void calculateIndividualHealthIndicator() {
        for(MitoTrip mitoTrip :  mitoTrips.values()){
            PersonMuc siloPerson = (PersonMuc) dataContainer.getHouseholdDataManager().getPersonFromId(mitoTrip.getPerson().getId());
            siloPerson.setWeeklyLightInjuryRisk(siloPerson.getWeeklyLightInjuryRisk()+mitoTrip.getLightInjuryRisk());
            siloPerson.setWeeklySevereInjuryRisk(siloPerson.getWeeklySevereInjuryRisk()+mitoTrip.getSevereInjuryRisk());
            siloPerson.addWeeklyPhysicalActivityMmetHours(mitoTrip.getTripMode(),mitoTrip.getPhysicalActivityMmetHours());

            for(Pollutant pollutant : ((HealthDataContainerImpl)dataContainer).getPollutantSet()){
                double exposure = mitoTrip.getExposureMap().get(pollutant.name());
                if(siloPerson.getWeeklyExposureByPollutant().get(pollutant)==null){
                    siloPerson.getWeeklyExposureByPollutant().put(pollutant,exposure);
                }else {
                    double previousExposure = siloPerson.getWeeklyExposureByPollutant().get(pollutant);
                    siloPerson.getWeeklyExposureByPollutant().put(pollutant,previousExposure+exposure);
                }
            }
        }
    }

    private void calculateRelativeRisk() {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            PersonMuc personMuc = (PersonMuc)person;

            personMuc.setAllCauseRR(
                    RelativeRisks.walk(personMuc.getWeeklyPhysicalActivityMmetHours(Mode.walk)) *
                    RelativeRisks.bike(personMuc.getWeeklyPhysicalActivityMmetHours(Mode.bicycle)) *
                    RelativeRisks.no2(personMuc.getWeeklyExposureByPollutant().get(Pollutant.NO2)*10e2) *
                    RelativeRisks.pm25(personMuc.getWeeklyExposureByPollutant().get(Pollutant.PM2_5)*10e2) *
                    RelativeRisks.accident(personMuc.getWeeklySevereInjuryRisk()));
        }
    }


    private void writeMitoTrips(String path) {
        logger.info("  Writing trips file");
        PrintWriter pwh = MitoUtil.openFileForSequentialWriting(path, false);
        pwh.println("t.id,t.mode,t.mmetHours,t.lightInjuryRisk,t.severeInjuryRisk,t.pmExposure,t.no2Exposure");
        for (MitoTrip trip : mitoTrips.values()) {
            pwh.print(trip.getId());
            pwh.print(",");
            pwh.print(trip.getTripMode().toString());
            pwh.print(",");
            pwh.print(trip.getPhysicalActivityMmetHours());
            pwh.print(",");
            pwh.print(trip.getLightInjuryRisk());
            pwh.print(",");
            pwh.print(trip.getSevereInjuryRisk());
            for(Pollutant pollutant : ((HealthDataContainerImpl) dataContainer).getPollutantSet()){
                pwh.print(",");
                pwh.print(trip.getExposureMap().get(pollutant.name()));
            }
            pwh.println();
        }
        pwh.close();
    }

}
