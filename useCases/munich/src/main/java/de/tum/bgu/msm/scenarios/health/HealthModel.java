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
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.moped.util.concurrent.ConcurrentExecutor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.MitoUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.contrib.accidents.AccidentType;
import org.matsim.contrib.dvrp.trafficmonitoring.TravelTimeUtils;
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

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.matsim.contrib.emissions.Pollutant.*;

public class HealthModel extends AbstractModel implements ModelUpdateListener {
    private int latestMatsimYear = -1;
    private static final Logger logger = Logger.getLogger(HealthModel.class);

    private Map<Integer, Trip> mitoTrips;
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
        String networkFile = properties.main.baseDirectory + "/" + scenario.getConfig().network().getInputFile();
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
            for(Day day : Day.values()){
                for(Mode mode : Mode.values()){
                    switch (mode){
                        case autoDriver:
                        case autoPassenger:
                        case bicycle:
                        case walk:
                            mitoTrips = new TripReaderMucHealth().readData(properties.main.baseDirectory + "scenOutput/"
                                    + properties.main.scenarioName + "/" + latestMatsimYear + "/microData/trips_" + day + "_" + mode + ".csv");
                            healthDataAssembler(latestMatsimYear, day, mode);
                            calculatePersonHealthExposures();
                            break;
                        default:
                            logger.warn("No health model for mode: " + mode);
                    }
                }
            }
        } else if(properties.transportModel.transportModelYears.contains(year + 1)) {//why year +1
            latestMatsimYear = year + 1;
            for(Day day : Day.values()){
                for(Mode mode : Mode.values()){
                    switch (mode){
                        case autoDriver:
                        case autoPassenger:
                        case bicycle:
                        case walk:
                            mitoTrips = new TripReaderMucHealth().readData(properties.main.baseDirectory + "scenOutput/"
                                    + properties.main.scenarioName + "/" + latestMatsimYear + "/microData/trips_" + day + "_" + mode + ".csv");
                            healthDataAssembler(latestMatsimYear, day, mode);
                            calculatePersonHealthExposures();
                            mitoTrips.clear();
                            break;
                        default:
                            logger.warn("No health model for mode: " + mode);
                    }
                }
            }
        }
        calculatePersonHealthExposuresAtHome();
        calculateRelativeRisk();
    }

    @Override
    public void endSimulation() {
    }

    private void healthDataAssembler(int year, Day day, Mode mode) {
        logger.info("Updating health data for year " + year + "|day: " + day + "|mode: " + mode + ".");
        String eventsFile;
        String networkFile;

        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                + properties.main.scenarioName + "/matsim/" + latestMatsimYear;

        switch (mode){
            case autoDriver:
            case autoPassenger:
                scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);
                scenario.getConfig().controler().setOutputDirectory(outputDirectoryRoot);
                eventsFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + latestMatsimYear + ".output_events.xml.gz";
                networkFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + latestMatsimYear + ".output_network.xml.gz";
                calculateTripHealthIndicator(networkFile, eventsFile, new ArrayList<>(mitoTrips.values()), day, mode);
                break;
            case bicycle:
            case walk:
                scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);
                scenario.getConfig().controler().setOutputDirectory(outputDirectoryRoot);
                eventsFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/bikePed/" + latestMatsimYear + ".output_events.xml.gz";
                networkFile = scenario.getConfig().controler().getOutputDirectory() + "/" + day + "/car/" + latestMatsimYear + ".output_network.xml.gz";
                calculateTripHealthIndicator(networkFile, eventsFile, new ArrayList<>(mitoTrips.values()), day, mode);
                break;
            default:
                logger.warn("No health model for mode: " + mode);
        }

        final String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName +"/";
        String filett = outputDirectory
                + "healthIndicators_"
                + year
                + "_" + day
                + "_" + mode
                + ".csv";
        writeMitoTrips(filett);
    }


    public void calculateTripHealthIndicator(String networkFile, String eventsFile, List<Trip> trips, Day day, Mode mode) {
        logger.info("Updating trip health data for mode " + mode + ", day " + day);
        new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);

        final int partitionSize = (int) ((double) trips.size() / Runtime.getRuntime().availableProcessors()) + 1;
        Iterable<List<Trip>> partitions = Iterables.partition(trips, partitionSize);

        TravelTime travelTime = TravelTimeUtils.createTravelTimesFromEvents(scenario, eventsFile);
        TravelDisutility travelDisutility = ControlerDefaults.createDefaultTravelDisutilityFactory(scenario).createTravelDisutility(travelTime);

        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Runtime.getRuntime().availableProcessors());

        AtomicInteger counter = new AtomicInteger();
        logger.info("Partition Size: " + partitionSize);

        AtomicInteger NO_PATH_TRIP = new AtomicInteger();

        for (final List<Trip> partition : partitions) {
            LeastCostPathCalculator pathCalculator = new FastAStarLandmarksFactory(threads).createPathCalculator(scenario.getNetwork(), travelDisutility, travelTime);

            executor.addTaskToQueue(() -> {
                try {

                    int id = counter.incrementAndGet();
                    int counterr = 0;
                    for (Trip trip : partition) {

                        if(LongMath.isPowerOfTwo(counterr)) {
                            logger.info(counterr + " in " + id);
                        }

                        Node originNode = NetworkUtils.getNearestNode(scenario.getNetwork(), trip.getTripOrigin());
                        Node destinationNode = NetworkUtils.getNearestNode(scenario.getNetwork(), trip.getTripDestination());

                        // Calculate exposures for outbound path
                        int outboundDepartureTimeInSeconds = trip.getDepartureTimeInMinutes()*60;
                        LeastCostPathCalculator.Path outboundPath = pathCalculator.calcLeastCostPath(originNode, destinationNode,outboundDepartureTimeInSeconds,null,null);
                        if(outboundPath == null){
                            logger.warn("trip id: " + trip.getId() + ", trip depart time: " + trip.getDepartureTimeInMinutes() +
                                    "origin coord: [" + trip.getTripOrigin().getX() + "," + trip.getTripOrigin().getY() + "], " +
                                    "dest coord: [" + trip.getTripDestination().getX() + "," + trip.getTripDestination().getY() + "], " +
                                    "origin node: " + originNode + ", dest node: " + destinationNode);
                            NO_PATH_TRIP.getAndIncrement();
                        } else {
                            calculatePathExposures(trip,outboundPath,day,outboundDepartureTimeInSeconds,travelTime);
                        }

                        // Calculate exposures for activity & return trip (home-based trips only)
                        if(trip.isHomeBased()) {
                            int returnDepartureTimeInSeconds = trip.getDepartureReturnInMinutes()*60;
                            LeastCostPathCalculator.Path returnPath = pathCalculator.calcLeastCostPath(destinationNode, originNode,returnDepartureTimeInSeconds,null,null);
                            if(returnPath == null){
                                logger.warn("trip id: " + trip.getId() + ", trip depart time: " + trip.getDepartureTimeInMinutes() +
                                        "origin coord: [" + trip.getTripOrigin().getX() + "," + trip.getTripOrigin().getY() + "], " +
                                        "dest coord: [" +  trip.getTripDestination().getX() + "," + trip.getTripDestination().getY() + "], " +
                                        "origin node: " + originNode + ", dest node: " + destinationNode);
                                NO_PATH_TRIP.getAndIncrement();
                            } else {
                                calculateActivityExposures(trip);
                                calculatePathExposures(trip,returnPath,day,returnDepartureTimeInSeconds,travelTime);
                            }
                        }

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

    private void calculatePathExposures(Trip trip,LeastCostPathCalculator.Path path,Day day,int departureTimeInSecond, TravelTime travelTime) {

        Mode mode = trip.getTripMode();
        Double speed = null;
        if(mode.equals(Mode.walk)) {
            speed = getAvgWalkSpeed((PersonMuc) dataContainer.getHouseholdDataManager().getPersonFromId(trip.getPerson()));
        } else if(mode.equals(Mode.bicycle)) {
            speed = getAvgCycleSpeed((PersonMuc) dataContainer.getHouseholdDataManager().getPersonFromId(trip.getPerson()));
        }

        double enterTimeInSecond;
        double pathLength = 0;
        double pathTime = 0;
        double pathMarginalMetHours = 0;
        double pathConcMetersPm25 = 0.;
        double pathConcMetersNo2 = 0.;
        double pathExposurePm25 = 0.;
        double pathExposureNo2 = 0.;
        //double pathLightInjuryRisk = 0;
        double pathSevereInjuryRisk = 0;
        double pathFatalityRisk = 0;

        for(Link link : path.links) {
            enterTimeInSecond = (double) departureTimeInSecond + pathTime;
            double linkLength = link.getLength();
            //double linkLightInjuryRisk = 0.;
            double linkSevereInjuryRisk = 0.;
            double linkFatalityRisk = 0.;
            double linkMarginalMetHours = 0.;
            double linkConcentrationPm25 = 0.;
            double linkConcentrationNo2 = 0.;
            double linkExposurePm25 = 0.;
            double linkExposureNo2 = 0.;

            double linkTime;
            if(mode.equals(Mode.walk) || mode.equals(Mode.bicycle)) {
                linkTime = linkLength / speed;
            } else {
                linkTime = travelTime.getLinkTravelTime(link, enterTimeInSecond, null, null);
            }

            LinkInfo linkInfo = ((HealthDataContainerImpl)dataContainer).getLinkInfoByDay().get(day).get(link.getId());
            if(linkInfo!=null) {
                // INJURY
                //linkLightInjuryRisk = getLinkLightInjuryRisk(mode, (int) (enterTimeInSecond / 3600.), linkInfo);
                double[] severeFatalRisk = getLinkSevereFatalInjuryRisk(mode, (int) (enterTimeInSecond / 3600.), linkInfo);
                linkSevereInjuryRisk = severeFatalRisk[0];
                linkFatalityRisk = severeFatalRisk[1];

                // PHYSICAL ACTIVITY
                double linkMarginalMet = PhysicalActivity.getMMet(mode, linkLength, linkTime);
                linkMarginalMetHours = linkMarginalMet * linkTime / 3600.;

                // AIR POLLUTION
                // Concentration
                int timeBin = (int) (AirPollutantModel.EMISSION_TIME_BIN_SIZE*(Math.floor(Math.abs(enterTimeInSecond/AirPollutantModel.EMISSION_TIME_BIN_SIZE))));
                linkConcentrationPm25 = (linkInfo.getExposure2Pollutant2TimeBin().getOrDefault(PM2_5,new OpenIntFloatHashMap()).get(timeBin) +
                        linkInfo.getExposure2Pollutant2TimeBin().getOrDefault(PM2_5_non_exhaust,new OpenIntFloatHashMap()).get(timeBin)) * 10;
                linkConcentrationNo2 = linkInfo.getExposure2Pollutant2TimeBin().getOrDefault(NO2,new OpenIntFloatHashMap()).get(timeBin) * 10;

                linkExposurePm25 = PollutionExposure.getLinkExposurePm25(mode, linkConcentrationPm25, linkTime, linkMarginalMet);
                linkExposureNo2 = PollutionExposure.getLinkExposureNo2(mode, linkConcentrationPm25, linkTime, linkMarginalMet);
            }

            pathLength += linkLength;
            pathTime += linkTime;
            //pathLightInjuryRisk += linkLightInjuryRisk - (pathLightInjuryRisk * linkLightInjuryRisk);
            pathSevereInjuryRisk += linkSevereInjuryRisk - (pathSevereInjuryRisk * linkSevereInjuryRisk);
            pathFatalityRisk += linkFatalityRisk - (pathFatalityRisk * linkFatalityRisk);
            pathMarginalMetHours += linkMarginalMetHours;
            pathConcMetersPm25 += linkConcentrationPm25 * linkLength;
            pathConcMetersNo2 += linkConcentrationNo2 * linkLength;
            pathExposurePm25 += linkExposurePm25;
            pathExposureNo2 += linkExposureNo2;
        }

        Map<String, Double> accidentRiskMap = new HashMap<>();
        //accidentRiskMap.put("lightInjury", pathLightInjuryRisk);
        accidentRiskMap.put("severeInjury", pathSevereInjuryRisk);
        accidentRiskMap.put("fatality", pathFatalityRisk);

        Map<String, Double> exposureMap = new HashMap<>();
        exposureMap.put("pm2.5", pathExposurePm25);
        exposureMap.put("no2", pathExposureNo2);

        trip.updateMatsimTravelDistance(pathLength);
        trip.updateMatsimTravelTime(pathTime);
        trip.updateMarginalMetHours(pathMarginalMetHours);
        trip.updateTravelRiskMap(accidentRiskMap);
        trip.updateTravelExposureMap(exposureMap);
        trip.updateMatsimLinkCount(path.links.size());
        trip.updateMatsimConcMetersPm25(pathConcMetersPm25);
        trip.updateMatsimConcMetersNo2(pathConcMetersNo2);
    }

    private void calculateActivityExposures(Trip trip) {
        double activityArrivalTime = trip.getDepartureTimeInMinutes() + trip.getMatsimTravelTime()/60.;
        double activityDepartureTime = trip.getDepartureReturnInMinutes();
        double activityDuration = activityDepartureTime - activityArrivalTime;
        if(activityDuration < 0) {
            activityDuration += 1440.;
        }

        // todo: consider location-specific exposures & occupation-specific METs for work activities
        Map<String, Double> exposureMap = new HashMap<>();
        exposureMap.put("pm2.5", PollutionExposure.getActivityExposurePm25(activityDuration));
        exposureMap.put("no2", PollutionExposure.getActivityExposureNo2(activityDuration));

        trip.setActivityDuration(activityDuration);
        trip.setActivityExposureMap(exposureMap);
    }

    private final double FATAL_CAR_DRIVER = 0.077;
    private final double FATAL_BIKECAR_BIKE = 0.024;
    private final double FATAL_BIKEBIKE_BIKE = 0.051;
    private final double FATAL_PED_PED = 0.073;

    private double[] getLinkSevereFatalInjuryRisk(Mode mode, int hour, LinkInfo linkInfo) {
        double severeInjuryRisk;
        double fatalityRisk;
        Map<AccidentType, OpenIntFloatHashMap> exposure = linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime();
        switch (mode){
            case autoDriver:
            case autoPassenger:
                fatalityRisk = exposure.get(AccidentType.CAR).get(hour) * FATAL_CAR_DRIVER;
                severeInjuryRisk = exposure.get(AccidentType.CAR).get(hour) * (1-FATAL_CAR_DRIVER);
                break;
            case bicycle:
                fatalityRisk = exposure.get(AccidentType.BIKECAR).get(hour) * FATAL_BIKECAR_BIKE +
                        exposure.get(AccidentType.BIKEBIKE).get(hour) * FATAL_BIKEBIKE_BIKE;
                severeInjuryRisk = exposure.get(AccidentType.BIKECAR).get(hour) * (1-FATAL_BIKECAR_BIKE) +
                        exposure.get(AccidentType.BIKEBIKE).get(hour) * (1-FATAL_BIKEBIKE_BIKE);
                break;
            case walk:
                fatalityRisk = exposure.get(AccidentType.PED).get(hour) * FATAL_PED_PED;
                severeInjuryRisk = exposure.get(AccidentType.PED).get(hour) * (1-FATAL_PED_PED);
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

    //TODO: concurrent
    private void calculatePersonHealthExposures() {
        int missingPerson = 0;
        for(Trip mitoTrip :  mitoTrips.values()){
            Person siloPerson = dataContainer.getHouseholdDataManager().getPersonFromId(mitoTrip.getPerson());
            if(siloPerson == null){
                missingPerson++;
                continue;
            }

            ((PersonMuc) siloPerson).updateWeeklyAccidentRisks(mitoTrip.getTravelRiskMap());
            ((PersonMuc) siloPerson).updateWeeklyMarginalMetHours(mitoTrip.getTripMode(), mitoTrip.getMarginalMetHours());
            ((PersonMuc) siloPerson).updateWeeklyPollutionExposures(mitoTrip.getTravelExposureMap());
            ((PersonMuc) siloPerson).updateWeeklyTravelSeconds(mitoTrip.getMatsimTravelTime());

            // Activity details (home-based trips only)
            if(mitoTrip.isHomeBased()) {
                ((PersonMuc) siloPerson).updateWeeklyActivityMinutes(mitoTrip.getActivityDuration());
                ((PersonMuc) siloPerson).updateWeeklyPollutionExposures(mitoTrip.getActivityExposureMap());

            }
        }
        logger.warn("total dismatched person: " + missingPerson);
    }

    private void calculatePersonHealthExposuresAtHome() {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            double minutesAtHome = Math.max(0., 10080. - (((PersonMuc) person).getWeeklyTravelSeconds() / 60.) - (((PersonMuc) person).getWeeklyActivityMinutes()));

            Map<String, Double> exposureMap = new HashMap<>();
            exposureMap.put("pm2.5", PollutionExposure.getActivityExposurePm25(minutesAtHome));
            exposureMap.put("no2", PollutionExposure.getActivityExposureNo2(minutesAtHome));

            ((PersonMuc) person).setWeeklyHomeMinutes(minutesAtHome);
            ((PersonMuc) person).updateWeeklyPollutionExposures(exposureMap);
        }
    }

    private void calculateRelativeRisk() {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            PersonMuc personMuc = (PersonMuc)person;
            Map<String, Double> relativeRisks = RelativeRisks.calculate(personMuc);
            personMuc.setRelativeRisks(relativeRisks);
            personMuc.setAllCauseRR(relativeRisks.values().stream().reduce(1., (a, b) -> a*b));
        }
    }

    private double getAvgCycleSpeed(PersonMuc person) {
        return ((HealthDataContainerImpl)dataContainer).getAvgSpeeds().get(Mode.bicycle).
                get(MitoGender.valueOf(person.getGender().name())).get(Math.min(person.getAge(),105)) / 3.6;
    }

    private double getAvgWalkSpeed(PersonMuc person) {
        return ((HealthDataContainerImpl)dataContainer).getAvgSpeeds().get(Mode.walk).
                get(MitoGender.valueOf(person.getGender().name())).get(Math.min(person.getAge(),105)) / 3.6;
    }


    private void writeMitoTrips(String path) {
        logger.info("  Writing trip health indicators file");
        PrintWriter pwh = MitoUtil.openFileForSequentialWriting(path, false);
        pwh.println("t.id,t.mode,t.matsimTravelTime_s,t.matsimTravelDistance_m,t.activityDuration_min," +
                "t.mmetHours,t.lightInjuryRisk,t.severeInjuryRisk,t.fatalityRisk," +
                "t.links,t.avgConcentrationPm25,t.avgConcentrationNo2," +
                "t.exposurePm25,t.exposureNo2,t.activityExposurePm25,t.activityExposureNo2");

        for (Trip trip : mitoTrips.values()) {
            pwh.print(trip.getId());
            pwh.print(",");
            pwh.print(trip.getTripMode().toString());
            pwh.print(",");
            pwh.print(trip.getMatsimTravelTime());
            pwh.print(",");
            pwh.print(trip.getMatsimTravelDistance());
            pwh.print(",");
            pwh.print(trip.getActivityDuration());
            pwh.print(",");
            pwh.print(trip.getMarginalMetHours());
            pwh.print(",");
            pwh.print(trip.getTravelRiskMap().get("lightInjury"));
            pwh.print(",");
            pwh.print(trip.getTravelRiskMap().get("severeInjury"));
            pwh.print(",");
            pwh.print(trip.getTravelRiskMap().get("fatality"));
            pwh.print(",");
            pwh.print(trip.getMatsimLinkCount());
            pwh.print(",");
            pwh.print(trip.getMatsimConcMetersPm25() / trip.getMatsimTravelDistance());
            pwh.print(",");
            pwh.print(trip.getMatsimConcMetersNo2() / trip.getMatsimTravelDistance());
            pwh.print(",");
            pwh.print(trip.getTravelExposureMap().get("pm2.5"));
            pwh.print(",");
            pwh.print(trip.getTravelExposureMap().get("no2"));
            pwh.print(",");
            pwh.print(trip.getActivityExposureMap().get("pm2.5"));
            pwh.print(",");
            pwh.println(trip.getActivityExposureMap().get("no2"));
        }
        pwh.close();
    }

}
