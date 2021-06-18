package de.tum.bgu.msm.scenarios.health;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import com.google.common.collect.Iterables;
import com.google.common.math.LongMath;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.matsim.ZoneConnectorManager;
import de.tum.bgu.msm.matsim.ZoneConnectorManagerImpl;
import de.tum.bgu.msm.mito.MitoMatsimScenarioAssembler;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.moped.util.concurrent.ConcurrentExecutor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.MitoUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
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
    private MutableScenario scenario;


    public HealthModel(DataContainer dataContainer, Properties properties, Random random, Config config) {
        super(dataContainer, properties, random);
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
        scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);
        String networkFile = scenario.getConfig().network().getInputFile();
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
        this.mitoTrips = MitoMatsimScenarioAssembler.getMitoTrips();
        if(properties.main.startYear == year) {
            latestMatsimYear = year;
            healthDataAssembler(year);
            calculatePersonHealthIndicator();
            calculateRelativeRisk();
        } else if(properties.transportModel.transportModelYears.contains(year + 1)) {//why year +1
            latestMatsimYear = year + 1;
            healthDataAssembler(year + 1);
            calculatePersonHealthIndicator();
            calculateRelativeRisk();
        }
    }

    @Override
    public void endSimulation() {
    }

    private void healthDataAssembler(int year) {
        logger.info("Updating health data for year " + year + ".");
        String eventsFile;
        String networkFile;
        List<MitoTrip> trips;

        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                + properties.main.scenarioName + "/matsim/" + latestMatsimYear;
        scenario.getConfig().controler().setOutputDirectory(outputDirectoryRoot);

        for(Day day : Day.values()){
            for(Mode mode : Mode.values()){
                switch (mode){
                    case autoDriver:
                    case autoPassenger:
                        eventsFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + latestMatsimYear + ".output_events.xml.gz";
                        networkFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + latestMatsimYear + ".output_network.xml.gz";

                        trips = mitoTrips.values().stream().filter(tt -> tt.getDepartureDay().equals(day) & tt.getTripMode().equals(mode)).collect(Collectors.toList());
                        calculateTripHealthIndicator(eventsFile, trips, day, mode);
                        break;
                    case bicycle:
                    case walk:
                        eventsFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/bikePed/" + latestMatsimYear + ".output_events.xml.gz";
                        networkFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/bikePed/" + latestMatsimYear + ".output_network.xml.gz";

                        trips  = mitoTrips.values().stream().filter(tt -> tt.getDepartureDay().equals(day) & tt.getTripMode().equals(mode)).collect(Collectors.toList());
                        calculateTripHealthIndicator(eventsFile, trips, day, mode);
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


    public void calculateTripHealthIndicator(String eventsFile, List<MitoTrip> trips, Day day, Mode mode) {
        logger.info("Updating trip health data for mode " + mode + ", day " + day);
        final int partitionSize = (int) ((double) trips.size() / Runtime.getRuntime().availableProcessors()) + 1;
        Iterable<List<MitoTrip>> partitions = Iterables.partition(trips, partitionSize);

        TravelTime travelTime = TravelTimeUtils.createTravelTimesFromEvents(scenario, eventsFile);
        TravelDisutility travelDisutility = ControlerDefaults.createDefaultTravelDisutilityFactory(scenario).createTravelDisutility(travelTime);

        Network network = scenario.getNetwork();
        //new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);

        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Runtime.getRuntime().availableProcessors());

        AtomicInteger counter = new AtomicInteger();
        logger.info("Partition Size: " + partitionSize);

        AtomicInteger NO_PATH_TRIP = new AtomicInteger();

        for (final List<MitoTrip> partition : partitions) {
            LeastCostPathCalculator pathCalculator = new FastAStarLandmarksFactory(threads).createPathCalculator(network, travelDisutility, travelTime);
            //LeastCostPathCalculator pathCalculator = new FastDijkstraFactory(false).createPathCalculator(network, travelDisutility, travelTime);

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

                        //TODO: should not be random for walk trips
                        if(trip.getOriginMopedZoneCoord()!=null){
                            originCoord = trip.getOriginMopedZoneCoord();
                        }else if(origin instanceof MicroLocation) {
                            originCoord = CoordUtils.createCoord(((MicroLocation) origin).getCoordinate());
                        } else {
                            originCoord = CoordUtils.createCoord(((MitoZone) origin).getRandomCoord(MitoUtil.getRandomObject()));
                        }

                        if(trip.getDestinationMopedZoneCoord()!=null){
                            destinationCoord = trip.getDestinationMopedZoneCoord();
                        }else if(destination instanceof MicroLocation) {
                            destinationCoord = CoordUtils.createCoord(((MicroLocation) destination).getCoordinate());
                        } else {
                            destinationCoord = CoordUtils.createCoord(((MitoZone) destination).getRandomCoord(MitoUtil.getRandomObject()));
                        }

                        Node originNode = NetworkUtils.getNearestNode(network, originCoord);
                        Node destinationNode = NetworkUtils.getNearestNode(network, destinationCoord);

                        LeastCostPathCalculator.Path path = pathCalculator.calcLeastCostPath(originNode, destinationNode,trip.getDepartureTimeInMinutes()*60,null,null);

                        if(path == null){
                            logger.warn("trip id: " + trip.getId() + ", trip depart time: " + trip.getDepartureTimeInMinutes() +
                                    "origin coord: [" + originCoord.getX() + "," + originCoord.getY() + "], " +
                                    "dest coord: [" + destinationCoord.getX() + "," + destinationCoord.getY() + "], " +
                                    "origin node: " + originNode + ", dest node: " + destinationNode);
                            NO_PATH_TRIP.getAndIncrement();
                        }

                        double enterTimeInSecond = 0;
                        double tt = 0;
                        //double lightInjuryRisk = 0;
                        double severeInjuryRisk = 0;
                        double fatalityRisk = 0;
                        Map<String, Double> exposureMap = new HashMap<>();

                        for(Link link : path.links) {
                            enterTimeInSecond = trip.getDepartureTimeInMinutes()*60 + tt;
                            LinkInfo linkInfo = ((HealthDataContainerImpl)dataContainer).getLinkInfoByDay().get(day).get(link.getId());
                            if(linkInfo!=null) {
                                //lightInjuryRisk += getLinkLightInjuryRisk(mode, (int) (enterTimeInSecond / 3600.), linkInfo);
                                double[] severeFatalRisk = getLinkSevereFatalInjuryRisk(mode, (int) (enterTimeInSecond / 3600.), linkInfo);
                                severeInjuryRisk += severeFatalRisk[0];
                                fatalityRisk += severeFatalRisk[1];

                                for (Pollutant pollutant : ((HealthDataContainerImpl) dataContainer).getPollutantSet()){
                                    int timeBin = (int) (AirPollutantModel.EMISSION_TIME_BIN_SIZE*(Math.floor(Math.abs(enterTimeInSecond/AirPollutantModel.EMISSION_TIME_BIN_SIZE))));
                                    double exposure =linkInfo.getExposure2Pollutant2TimeBin().getOrDefault(pollutant,new OpenIntFloatHashMap()).get(timeBin);
                                    exposureMap.put(pollutant.name(),exposureMap.getOrDefault(pollutant,0.) + exposure);
                                }

                            }

                            if(mode.equals(Mode.walk)){
                                tt += link.getLength()/getAvgWalkSpeed(trip.getPerson());
                            }else if(mode.equals(Mode.bicycle)){
                                tt += link.getLength()/getAvgCycleSpeed(trip.getPerson());
                            } else{
                                tt += travelTime.getLinkTravelTime(link, enterTimeInSecond, null, null);
                            }
                        }
                        //trip.setLightInjuryRisk(lightInjuryRisk);
                        trip.setSevereInjuryRisk(severeInjuryRisk);
                        trip.setFatalityRisk(fatalityRisk);
                        trip.setExposureMap(exposureMap);
                        //Note: Here cannot directly set the travel time to path.travelTime,
                        // path.travelTime is wrong for walk and bike, the travel time is calculated based on default car speed
                        trip.setMatsimTravelTime(tt);
                        trip.setMatsimTravelDistance(path.links.stream().mapToDouble(x -> x.getLength()).sum());
                        //TODO: matsimTravelTime needs to be converted from second to hour?
                        trip.setPhysicalActivityMmetHours(PhysicalActivity.calculate(
                                trip.getTripMode(),
                                trip.getMatsimTravelDistance(),
                                trip.getMatsimTravelTime()/3600.));
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

        logger.info("No path trips for mode " + mode + " : " + NO_PATH_TRIP.get());

    }

    private final double FATAL_CAR_DRIVER = 0.077;
    private final double FATAL_BIKECAR_BIKE = 0.024;
    private final double FATAL_BIKEBIKE_BIKE = 0.051;
    private final double FATAL_PED_PED = 0.073;

    private double[] getLinkSevereFatalInjuryRisk(Mode mode, int hour, LinkInfo linkInfo) {
        double severeInjuryRisk = 0.;
        double fatalityRisk = 0.;
        switch (mode){
            case autoDriver:
            case autoPassenger:
                fatalityRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.CAR).get(hour) * FATAL_CAR_DRIVER;
                severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.CAR).get(hour) * (1-FATAL_CAR_DRIVER);
                break;
            case bicycle:
                fatalityRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKECAR).get(hour) * FATAL_BIKECAR_BIKE;
                fatalityRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKEBIKE).get(hour) * FATAL_BIKEBIKE_BIKE;
                severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKECAR).get(hour) * (1-FATAL_BIKECAR_BIKE);
                severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKEBIKE).get(hour) * (1-FATAL_BIKEBIKE_BIKE);
                break;
            case walk:
                fatalityRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.PED).get(hour) * FATAL_PED_PED;
                severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.PED).get(hour) * (1-FATAL_PED_PED);
                break;
            default:
                throw new RuntimeException("Undefined mode " + mode);
        }


        return new double[]{severeInjuryRisk,fatalityRisk};
    }

    /*private double getLinkLightInjuryRisk(Mode mode, int hour, LinkInfo linkInfo) {
        double lightInjuryRisk = 0.;
        switch (mode){
            case autoDriver:
            case autoPassenger:
                lightInjuryRisk += linkInfo.getLightCasualityExposureByAccidentTypeByTime().get(AccidentType.CAR).getOrDefault(hour,0.);
                break;
            case bicycle:
                lightInjuryRisk += linkInfo.getLightCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKECAR).getOrDefault(hour,0.);
                lightInjuryRisk += linkInfo.getLightCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKEBIKE).getOrDefault(hour,0.);
                break;
            case walk:
                lightInjuryRisk += linkInfo.getLightCasualityExposureByAccidentTypeByTime().get(AccidentType.PED).getOrDefault(hour,0.);
                break;
            default:
                throw new RuntimeException("Undefined mode " + mode);
        }


        return lightInjuryRisk;
    }
*/
    //TODO: noise and air pollutant exposure at home
    private void calculateHealthIndicatorAtHome() {

    }

    //TODO: concurrent
    private void calculatePersonHealthIndicator() {
        int missingPerson = 0;
        for(MitoTrip mitoTrip :  mitoTrips.values()){
            Person siloPerson = dataContainer.getHouseholdDataManager().getPersonFromId(mitoTrip.getPerson().getId());
            if(siloPerson == null){
                missingPerson++;
                continue;
            }

            ((PersonMuc) siloPerson).setWeeklyLightInjuryRisk(((PersonMuc) siloPerson).getWeeklyLightInjuryRisk()+mitoTrip.getLightInjuryRisk());
            ((PersonMuc) siloPerson).setWeeklySevereInjuryRisk(((PersonMuc) siloPerson).getWeeklySevereInjuryRisk()+mitoTrip.getSevereInjuryRisk());
            ((PersonMuc) siloPerson).setWeeklyFatalityInjuryRisk(((PersonMuc) siloPerson).getWeeklyFatalityInjuryRisk()+mitoTrip.getFatalityRisk());
            ((PersonMuc) siloPerson).addWeeklyPhysicalActivityMmetHours(mitoTrip.getTripMode(),mitoTrip.getPhysicalActivityMmetHours());

            for(Pollutant pollutant : ((HealthDataContainerImpl)dataContainer).getPollutantSet()){
                double exposure = mitoTrip.getExposureMap().getOrDefault(pollutant.name(),0.);
                double previousExposure = ((PersonMuc) siloPerson).getWeeklyExposureByPollutant().getOrDefault(pollutant,0.);
                ((PersonMuc) siloPerson).getWeeklyExposureByPollutant().put(pollutant,previousExposure+exposure);
            }
        }
        logger.warn("total dismatched person: " + missingPerson);
    }

    private void calculateRelativeRisk() {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            PersonMuc personMuc = (PersonMuc)person;

            personMuc.setAllCauseRR(
                    RelativeRisks.walk(personMuc.getWeeklyPhysicalActivityMmetHours(Mode.walk)) *
                    RelativeRisks.bike(personMuc.getWeeklyPhysicalActivityMmetHours(Mode.bicycle)) *
                    RelativeRisks.no2(personMuc.getWeeklyExposureByPollutant().getOrDefault(Pollutant.NO2,0.)*10e2) *
                    RelativeRisks.pm25(personMuc.getWeeklyExposureByPollutant().getOrDefault(Pollutant.PM2_5,0.)*10e2) *
                    RelativeRisks.accident(personMuc.getWeeklyFatalityInjuryRisk()));
        }
    }

    private double getAvgCycleSpeed(MitoPerson person) {
        return ((HealthDataContainerImpl)dataContainer).getAvgSpeeds().get(Mode.bicycle).get(person.getMitoGender()).get(Math.min(person.getAge(),105)) / 3.6;
    }

    private double getAvgWalkSpeed(MitoPerson person) {
        return ((HealthDataContainerImpl)dataContainer).getAvgSpeeds().get(Mode.walk).get(person.getMitoGender()).get(Math.min(person.getAge(),105)) / 3.6;
    }


    private void writeMitoTrips(String path) {
        logger.info("  Writing trip health indicators file");
        PrintWriter pwh = MitoUtil.openFileForSequentialWriting(path, false);
        pwh.print("t.id,t.mode,t.matsimTravelTime_s,t.matsimTravelDistance_m,t.mmetHours,t.lightInjuryRisk,t.severeInjuryRisk,t.fatalityRisk");
        //order of Set is not fixed
        List<Pollutant> fixedPollutantList = new ArrayList<>();
        for(Pollutant pollutant : ((HealthDataContainerImpl) dataContainer).getPollutantSet()){
            fixedPollutantList.add(pollutant);
            pwh.print(",");
            pwh.print(pollutant.name());
        }
        pwh.println();

        for (MitoTrip trip : mitoTrips.values()) {
            pwh.print(trip.getId());
            pwh.print(",");
            pwh.print(trip.getTripMode().toString());
            pwh.print(",");
            pwh.print(trip.getMatsimTravelTime());
            pwh.print(",");
            pwh.print(trip.getMatsimTravelDistance());
            pwh.print(",");
            pwh.print(trip.getPhysicalActivityMmetHours());
            pwh.print(",");
            pwh.print(trip.getLightInjuryRisk());
            pwh.print(",");
            pwh.print(trip.getSevereInjuryRisk());
            pwh.print(",");
            pwh.print(trip.getFatalityRisk());
            for(Pollutant pollutant : fixedPollutantList){
                pwh.print(",");
                pwh.print(trip.getExposureMap().get(pollutant.name()));
            }
            pwh.println();
        }
        pwh.close();
    }

}
