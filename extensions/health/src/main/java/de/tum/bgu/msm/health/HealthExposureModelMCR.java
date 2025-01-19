package de.tum.bgu.msm.health;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import com.google.common.collect.Iterables;
import com.google.common.math.LongMath;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.health.airPollutant.AirPollutantModel;
import de.tum.bgu.msm.health.data.*;
import de.tum.bgu.msm.health.injury.AccidentType;
import de.tum.bgu.msm.health.io.LinkInfoReader;
import de.tum.bgu.msm.health.io.TripExposureWriter;
import de.tum.bgu.msm.health.io.TripReaderMucHealth;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.contrib.dvrp.trafficmonitoring.TravelTimeUtils;
import org.matsim.contrib.emissions.Pollutant;
import org.matsim.core.config.Config;
import org.matsim.core.controler.ControlerDefaults;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.speedy.SpeedyALTFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import routing.BicycleConfigGroup;
import routing.WalkConfigGroup;
import routing.components.Gradient;
import routing.components.JctStress;
import routing.components.LinkAmbience;
import routing.components.LinkStress;
import routing.travelDisutility.ActiveDisutilityPrecalc;
import routing.travelTime.BicycleLinkSpeedCalculatorImpl;
import routing.travelTime.BicycleTravelTime;
import routing.travelTime.WalkLinkSpeedCalculatorImpl;
import routing.travelTime.WalkTravelTime;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class HealthExposureModelMCR extends AbstractModel implements ModelUpdateListener {
    private int latestMatsimYear = -1;
    private int latestMITOYear = -1;
    private static final Logger logger = LogManager.getLogger(HealthExposureModelMCR.class);
    private Map<Integer, Trip> mitoTrips = new HashMap<>();
    private final Config initialMatsimConfig;
    private MutableScenario scenario;
    private List<Day> simulatedDays;
    private List<Day> weekdays = Arrays.asList(Day.monday,Day.tuesday,Day.wednesday,Day.thursday,Day.friday);

    private final double FATAL_CAR_DRIVER = 0.077;
    private final double FATAL_BIKECAR_BIKE = 0.024;
    private final double FATAL_BIKEBIKE_BIKE = 0.051;
    private final double FATAL_PED_PED = 0.073;


    public HealthExposureModelMCR(DataContainer dataContainer, Properties properties, Random random, Config config) {
        super(dataContainer, properties, random);
        this.initialMatsimConfig = config;
        simulatedDays = Arrays.asList(Day.thursday,Day.saturday,Day.sunday);
    }

    @Override
    public void setup() { }

    @Override
    public void prepareYear(int year) {}

    @Override
    public void endYear(int year) {
        if(properties.healthData.exposureModelYears.contains(year)) {
            logger.warn("Health model end year:" + year);
            TreeSet<Integer> sortedYears = new TreeSet<>(properties.transportModel.transportModelYears);
            latestMatsimYear = sortedYears.floor(year);
            latestMITOYear = year;

            Map<Integer, Trip> mitoTripsAll = new TripReaderMucHealth().readData(properties.main.baseDirectory + "scenOutput/"
                    + properties.main.scenarioName + "/" + latestMITOYear + "/microData/trips.csv");

            //clear the health data from last exposure model year
            for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
                ((PersonHealth) person).resetHealthData();
            }

            for(Day day : simulatedDays){
                logger.warn("Health model setup for " + day);

                replyLinkInfoFromFile(day);

                logger.warn("Run health exposure model for " + day);

                for(Mode mode : Mode.values()){
                    switch (mode){
                        case autoDriver:
                        case autoPassenger:
                        case bicycle:
                        case walk:
                            if(Day.thursday.equals(day)){
                                mitoTrips = mitoTripsAll.values().stream().
                                        filter(trip -> trip.getTripMode().equals(mode) & weekdays.contains(trip.getDepartureDay())).
                                        collect(Collectors.toMap(Trip::getId,trip -> trip));
                            }else {
                                mitoTrips = mitoTripsAll.values().stream().
                                        filter(trip -> trip.getTripMode().equals(mode) & trip.getDepartureDay().equals(day)).
                                        collect(Collectors.toMap(Trip::getId,trip -> trip));
                            }

                            healthDataAssembler(latestMatsimYear, day, mode);
                            calculatePersonHealthExposures();
                            final String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName +"/" + year+"/" ;
                            String filett = outputDirectory
                                    + "healthIndicators"
                                    + "_" + day
                                    + "_" + mode
                                    + ".csv";
                            new TripExposureWriter().writeMitoTrips(mitoTrips,filett);
                            break;
                        default:
                            logger.warn("No exposure model for mode: " + mode);
                    }
                    mitoTrips.clear();
                    System.gc();
                }
                ((DataContainerHealth)dataContainer).reset();
                System.gc();
            }
            //TODO: read zonal concentration info from CSV, use Thursday exposure as average?
            replyLinkInfoFromFile(Day.thursday);
            calculatePersonHealthExposuresAtHome();
        }

    }

    @Override
    public void endSimulation() {
    }

    private void replyLinkInfoFromFile(Day day) {
        String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/";

        //need to initialize link info and zone exposure map everytime, because to save memory, dataContainer.reset for each day/mode assembler
        Scenario scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);
        //need to use full network (include all car, active mode links) for dispersion
        String networkFile = properties.main.baseDirectory + properties.healthData.network_for_airPollutant_model;
        new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
        Map<Id<Link>, LinkInfo> linkInfoMap = new HashMap<>();
        for(Link link : scenario.getNetwork().getLinks().values()){
            linkInfoMap.put(link.getId(), new LinkInfo(link.getId()));
        }
        ((DataContainerHealth)dataContainer).setLinkInfo(linkInfoMap);
        logger.info("Initialized Link Info for " + ((DataContainerHealth)dataContainer).getLinkInfo().size() + " links ");

        for(Zone zone : dataContainer.getGeoData().getZones().values()){
            Map<Pollutant, OpenIntFloatHashMap> pollutantMap = new HashMap<>();
            ((DataContainerHealth)dataContainer).getZoneExposure2Pollutant2TimeBin().put(zone, pollutantMap);
        }

        new LinkInfoReader().readData( ((DataContainerHealth)dataContainer), outputDirectory, day);
    }

    private void healthDataAssembler(int year, Day day, Mode mode) {
        logger.info("Updating health data for year " + year + "|day: " + day + "|mode: " + mode + ".");

        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                + properties.main.scenarioName + "/matsim/" + latestMatsimYear;

        scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);
        scenario.getConfig().routing().setRoutingRandomness(0);
        scenario.getConfig().controller().setOutputDirectory(outputDirectoryRoot);

        calculateTripHealthIndicator(new ArrayList<>(mitoTrips.values()), day, mode);
    }

    private void calculateTripHealthIndicator(List<Trip> trips, Day day, Mode mode) {
        logger.info("Updating trip health data for mode " + mode + ", day " + day);

        final int partitionSize = (int) ((double) trips.size() / Runtime.getRuntime().availableProcessors()) + 1;
        Iterable<List<Trip>> partitions = Iterables.partition(trips, partitionSize);
        System.out.println("current memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

        TravelTime travelTime;
        TravelDisutility travelDisutility;
        String networkFile;

        String[] purposes = new String[]{"commute","discretionary","HBA","NHBO","NHBW"};
        switch (mode){
            case autoDriver:
            case autoPassenger:
                String eventsFile = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + latestMatsimYear + ".output_events.xml.gz";
                networkFile = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + latestMatsimYear + ".output_network.xml.gz";
                new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
                travelTime = TravelTimeUtils.createTravelTimesFromEvents(scenario.getNetwork(),scenario.getConfig(), eventsFile);
                travelDisutility = ControlerDefaults.createDefaultTravelDisutilityFactory(scenario).createTravelDisutility(travelTime);
                break;
            case walk:
                networkFile = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/bikePed/" + latestMatsimYear + ".output_network.xml.gz";
                new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
                WalkConfigGroup walkConfigGroup = new WalkConfigGroup();
                fillConfigWithWalkStandardValue(walkConfigGroup);
                scenario.getConfig().addModule(walkConfigGroup);
                travelTime = new WalkTravelTime(new WalkLinkSpeedCalculatorImpl(scenario.getConfig()));
                travelDisutility = new ActiveDisutilityPrecalc(scenario.getNetwork(),walkConfigGroup,travelTime);
                break;
            case bicycle:
                networkFile = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/bikePed/" + latestMatsimYear + ".output_network.xml.gz";
                new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
                BicycleConfigGroup bicycleConfigGroup = new BicycleConfigGroup();
                fillConfigWithBikeStandardValue(bicycleConfigGroup);
                scenario.getConfig().addModule(bicycleConfigGroup);
                travelTime = new BicycleTravelTime(new BicycleLinkSpeedCalculatorImpl(scenario.getConfig()));
                travelDisutility = new ActiveDisutilityPrecalc(scenario.getNetwork(),bicycleConfigGroup,travelTime);
                break;
            default:
                travelTime = null;
                travelDisutility = null;
                logger.error("No travel time/disutility for mode: " + mode);
        }

        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Runtime.getRuntime().availableProcessors());

        AtomicInteger counter = new AtomicInteger();
        logger.info("Partition Size: " + partitionSize);

        AtomicInteger NO_PATH_TRIP = new AtomicInteger();
        System.out.println("current memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

        for (final List<Trip> partition : partitions) {
            LeastCostPathCalculator pathCalculator = new SpeedyALTFactory().createPathCalculator(scenario.getNetwork(),travelDisutility,travelTime);
            PopulationFactory factory = PopulationUtils.getFactory();
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
                        org.matsim.api.core.v01.population.Person person = factory.createPerson(Id.createPersonId(trip.getId()));
                        person.getAttributes().putAttribute("purpose",convertTripPurpose(trip));

                        LeastCostPathCalculator.Path outboundPath = pathCalculator.calcLeastCostPath(originNode, destinationNode,outboundDepartureTimeInSeconds,person,null);
                        if(outboundPath == null){
                            logger.warn("trip id: " + trip.getId() + ", trip depart time: " + trip.getDepartureTimeInMinutes() +
                                    "origin coord: [" + trip.getTripOrigin().getX() + "," + trip.getTripOrigin().getY() + "], " +
                                    "dest coord: [" + trip.getTripDestination().getX() + "," + trip.getTripDestination().getY() + "], " +
                                    "origin node: " + originNode + ", dest node: " + destinationNode);
                            NO_PATH_TRIP.getAndIncrement();
                        } else {
                            calculatePathExposures(trip,outboundPath,outboundDepartureTimeInSeconds,travelTime);
                        }

                        // Calculate exposures for activity & return trip (home-based trips only)
                        if(trip.isHomeBased()) {
                            int returnDepartureTimeInSeconds = trip.getDepartureReturnInMinutes()*60;
                            LeastCostPathCalculator.Path returnPath = pathCalculator.calcLeastCostPath(destinationNode, originNode,returnDepartureTimeInSeconds,person,null);
                            if(returnPath == null){
                                logger.warn("trip id: " + trip.getId() + ", trip depart time: " + trip.getDepartureTimeInMinutes() +
                                        "origin coord: [" + trip.getTripOrigin().getX() + "," + trip.getTripOrigin().getY() + "], " +
                                        "dest coord: [" +  trip.getTripDestination().getX() + "," + trip.getTripDestination().getY() + "], " +
                                        "origin node: " + originNode + ", dest node: " + destinationNode);
                                NO_PATH_TRIP.getAndIncrement();
                            } else {
                                calculateActivityExposures(trip);
                                calculatePathExposures(trip,returnPath,returnDepartureTimeInSeconds,travelTime);
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

    private void calculatePathExposures(Trip trip,LeastCostPathCalculator.Path path,int departureTimeInSecond, TravelTime travelTime) {

        Mode mode = trip.getTripMode();

        double enterTimeInSecond;
        double pathLength = 0;
        double pathTime = 0;
        double pathMarginalMetHours = 0;
        double pathConcMetersPm25 = 0.;
        double pathConcMetersNo2 = 0.;
        double pathExposurePm25 = 0.;
        double pathExposureNo2 = 0.;
        double pathSevereInjuryRisk = 0;
        double pathFatalityRisk = 0;

        for(Link link : path.links) {
            enterTimeInSecond = (double) departureTimeInSecond + pathTime;
            double linkLength = link.getLength();
            double linkSevereInjuryRisk = 0.;
            double linkFatalityRisk = 0.;
            double linkMarginalMetHours = 0.;
            double linkConcentrationPm25 = 0.;
            double linkConcentrationNo2 = 0.;
            double linkExposurePm25 = 0.;
            double linkExposureNo2 = 0.;

            double linkTime = travelTime.getLinkTravelTime(link,enterTimeInSecond,null,null);

            LinkInfo linkInfo = ((DataContainerHealth)dataContainer).getLinkInfo().get(link.getId());
            if(linkInfo!=null) {
                // INJURY
                //double[] severeFatalRisk = getLinkSevereFatalInjuryRisk(mode, (int) (enterTimeInSecond / 3600.), linkInfo);
                //linkSevereInjuryRisk = severeFatalRisk[0];
                //linkFatalityRisk = severeFatalRisk[1];

                // PHYSICAL ACTIVITY
                double linkMarginalMet = PhysicalActivity.getMMet(mode, linkLength, linkTime, link);
                linkMarginalMetHours = linkMarginalMet * linkTime / 3600.;

                // AIR POLLUTION Concentration
                int timeBin = (int) (AirPollutantModel.EMISSION_TIME_BIN_SIZE*(Math.floor(Math.abs(enterTimeInSecond/AirPollutantModel.EMISSION_TIME_BIN_SIZE))));
                linkConcentrationPm25 = linkInfo.getExposure2Pollutant2TimeBin().getOrDefault(Pollutant.PM2_5,new OpenIntFloatHashMap()).get(timeBin) +
                        linkInfo.getExposure2Pollutant2TimeBin().getOrDefault(Pollutant.PM2_5_non_exhaust,new OpenIntFloatHashMap()).get(timeBin);
                linkConcentrationNo2 = linkInfo.getExposure2Pollutant2TimeBin().getOrDefault(Pollutant.NO2,new OpenIntFloatHashMap()).get(timeBin);

                linkExposurePm25 = PollutionExposure.getLinkExposurePm25(mode, linkConcentrationPm25, linkTime, linkMarginalMet);
                linkExposureNo2 = PollutionExposure.getLinkExposureNo2(mode, linkConcentrationPm25, linkTime, linkMarginalMet);
            }

            pathLength += linkLength;
            pathTime += linkTime;
            pathSevereInjuryRisk += linkSevereInjuryRisk - (pathSevereInjuryRisk * linkSevereInjuryRisk);
            pathFatalityRisk += linkFatalityRisk - (pathFatalityRisk * linkFatalityRisk);
            pathMarginalMetHours += linkMarginalMetHours;
            pathConcMetersPm25 += linkConcentrationPm25 * linkLength;
            pathConcMetersNo2 += linkConcentrationNo2 * linkLength;
            pathExposurePm25 += linkExposurePm25;
            pathExposureNo2 += linkExposureNo2;
        }

        Map<String, Float> accidentRiskMap = new HashMap<>();
        accidentRiskMap.put("severeInjury", (float) pathSevereInjuryRisk);
        accidentRiskMap.put("fatality", (float) pathFatalityRisk);

        Map<String, Float> exposureMap = new HashMap<>();
        exposureMap.put("pm2.5", (float) pathExposurePm25);
        exposureMap.put("no2", (float) pathExposureNo2);

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

        int timeBin = (int) (AirPollutantModel.EMISSION_TIME_BIN_SIZE*(Math.floor(Math.abs(activityArrivalTime/AirPollutantModel.EMISSION_TIME_BIN_SIZE))));

        Zone zone = dataContainer.getGeoData().getZones().get(trip.getTripDestinationZone());
        double zonalIncrementalPM25 = ((DataContainerHealth)dataContainer).getZoneExposure2Pollutant2TimeBin().get(zone).get(Pollutant.PM2_5).get(timeBin)+
                ((DataContainerHealth)dataContainer).getZoneExposure2Pollutant2TimeBin().get(zone).get(Pollutant.PM2_5_non_exhaust).get(timeBin);
        double zonalIncrementalNO2 = ((DataContainerHealth)dataContainer).getZoneExposure2Pollutant2TimeBin().get(zone).get(Pollutant.NO2).get(timeBin);

        // todo: consider location-specific exposures & occupation-specific METs for work activities
        Map<String, Float> exposureMap = new HashMap<>();
        exposureMap.put("pm2.5", (float) PollutionExposure.getActivityExposurePm25(activityDuration, zonalIncrementalPM25));
        exposureMap.put("no2", (float) PollutionExposure.getActivityExposureNo2(activityDuration, zonalIncrementalNO2));

        trip.setActivityDuration(activityDuration);
        trip.setActivityExposureMap(exposureMap);
    }

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

    //TODO: concurrent
    private void calculatePersonHealthExposures() {
        int missingPerson = 0;
        for(Trip mitoTrip :  mitoTrips.values()){
            Person siloPerson = dataContainer.getHouseholdDataManager().getPersonFromId(mitoTrip.getPerson());
            if(siloPerson == null){
                missingPerson++;
                continue;
            }

            ((PersonHealth) siloPerson).updateWeeklyAccidentRisks(mitoTrip.getTravelRiskMap());
            ((PersonHealth) siloPerson).updateWeeklyMarginalMetHours(mitoTrip.getTripMode(), (float) mitoTrip.getMarginalMetHours());
            ((PersonHealth) siloPerson).updateWeeklyPollutionExposures(mitoTrip.getTravelExposureMap());
            ((PersonHealth) siloPerson).updateWeeklyTravelSeconds((float) mitoTrip.getMatsimTravelTime());

            // Activity details (home-based trips only)
            if(mitoTrip.isHomeBased()) {
                ((PersonHealth) siloPerson).updateWeeklyActivityMinutes((float) mitoTrip.getActivityDuration());
                ((PersonHealth) siloPerson).updateWeeklyPollutionExposures(mitoTrip.getActivityExposureMap());

            }
        }
        logger.warn("total dismatched person: " + missingPerson);
    }

    private void calculatePersonHealthExposuresAtHome() {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            double minutesAtHome = Math.max(0., 10080. - (((PersonHealth) person).getWeeklyTravelSeconds() / 60.) - (((PersonHealth) person).getWeeklyActivityMinutes()));

            //TODO: check, assume people usually at home at night (6 pm?)
            int timeBin = (int) (AirPollutantModel.EMISSION_TIME_BIN_SIZE*(Math.floor(Math.abs(3600*18/AirPollutantModel.EMISSION_TIME_BIN_SIZE))));

            int zoneId = dataContainer.getRealEstateDataManager().getDwelling(person.getHousehold().getDwellingId()).getZoneId();
            Zone zone = dataContainer.getGeoData().getZones().get(zoneId);
            double zonalIncrementalPM25 = ((DataContainerHealth)dataContainer).getZoneExposure2Pollutant2TimeBin().get(zone).get(Pollutant.PM2_5).get(timeBin)+
                    ((DataContainerHealth)dataContainer).getZoneExposure2Pollutant2TimeBin().get(zone).get(Pollutant.PM2_5_non_exhaust).get(timeBin);
            double zonalIncrementalNO2 = ((DataContainerHealth)dataContainer).getZoneExposure2Pollutant2TimeBin().get(zone).get(Pollutant.NO2).get(timeBin);


            Map<String, Float> exposureMap = new HashMap<>();
            exposureMap.put("pm2.5", (float) PollutionExposure.getHomeExposurePm25(minutesAtHome, zonalIncrementalPM25));
            exposureMap.put("no2", (float) PollutionExposure.getHomeExposureNo2(minutesAtHome, zonalIncrementalNO2));

            ((PersonHealth) person).setWeeklyHomeMinutes((float) minutesAtHome);
            ((PersonHealth) person).updateWeeklyPollutionExposures(exposureMap);
        }
    }

    private double getAvgCycleSpeed(PersonHealth person) {
        return ((DataContainerHealth)dataContainer).getAvgSpeeds().get(Mode.bicycle).
                get(MitoGender.valueOf(person.getGender().name())).get(Math.min(person.getAge(),105)) / 3.6;
    }

    private double getAvgWalkSpeed(PersonHealth person) {
        return ((DataContainerHealth)dataContainer).getAvgSpeeds().get(Mode.walk).
                get(MitoGender.valueOf(person.getGender().name())).get(Math.min(person.getAge(),105)) / 3.6;
    }

    private String convertTripPurpose(Trip trip) {
        Purpose purpose = trip.getTripPurpose();
        if (purpose.equals(Purpose.HBW)){
            return "commute";
        } else if (purpose.equals(Purpose.HBE)){
            return "commute";
        } else if (purpose.equals(Purpose.HBS)|purpose.equals(Purpose.HBO)|purpose.equals(Purpose.HBR)|purpose.equals(Purpose.RRT)){
            return "discretionary";
        } else {
            return purpose.name();
        }
    }
    private void fillConfigWithWalkStandardValue(WalkConfigGroup walkConfigGroup) {
        // WALK ATTRIBUTES
        List<ToDoubleFunction<Link>> walkAttributes = new ArrayList<>();
        walkAttributes.add(l -> Math.max(0.,0.81 - LinkAmbience.getVgviFactor(l)));
        walkAttributes.add(l -> Math.min(1.,l.getFreespeed() / 22.35));
        walkAttributes.add(l -> JctStress.getStressProp(l,TransportMode.walk));

        // Walk weights
        Function<org.matsim.api.core.v01.population.Person,double[]> walkWeights = p -> {
            switch ((Purpose) p.getAttributes().getAttribute("purpose")) {
                case HBW -> {
                    return new double[]{0.3307472, 0, 4.9887390};
                }
                case HBE -> {
                    return new double[]{0, 0, 1.0037846};
                }
                case HBS, HBR, HBO -> {
                    if ((int) p.getAttributes().getAttribute("age") < 15) {
                        return new double[]{0.7789561, 0.4479527 + 2.0418898, 5.8219067};
                    } else if ((int) p.getAttributes().getAttribute("age") >= 65) {
                        return new double[]{0.7789561, 0.4479527 + 0.3715017, 5.8219067};
                    } else {
                        return new double[]{0.7789561, 0.4479527, 5.8219067};
                    }
                }
                case HBA -> {
                    return new double[]{0.6908324, 0, 0};
                }
                case NHBO -> {
                    return new double[]{0, 3.4485883, 0};
                }
                default -> {
                    return null;
                }
            }
        };

        // Walk config group
        walkConfigGroup.setAttributes(walkAttributes);
        walkConfigGroup.setWeights(walkWeights);

    }

    private void fillConfigWithBikeStandardValue(BicycleConfigGroup bicycleConfigGroup) {
        // BIKE ATTRIBUTES
        List<ToDoubleFunction<Link>> bikeAttributes = new ArrayList<>();
        bikeAttributes.add(l -> Math.max(Math.min(Gradient.getGradient(l),0.5),0.));
        bikeAttributes.add(l -> LinkStress.getStress(l, TransportMode.bike));

        // Bike weights
        Function<org.matsim.api.core.v01.population.Person,double[]> bikeWeights = p -> {
            switch((Purpose) p.getAttributes().getAttribute("purpose")) {
                case HBW -> {
                    if(p.getAttributes().getAttribute("sex").equals(MitoGender.FEMALE)) {
                        return new double[] {35.9032908,2.3084587 + 2.7762033};
                    } else {
                        return new double[] {35.9032908,2.3084587};
                    }
                }
                case HBE -> {
                    return new double[] {0,4.3075357};
                }
                case HBS, HBR, HBO -> {
                    if((int) p.getAttributes().getAttribute("age") < 15) {
                        return new double[] {57.0135325,1.2411983 + 6.4243251};
                    } else {
                        return new double[] {57.0135325,1.2411983};
                    }
                }
                default -> {
                    return null;
                }
            }
        };

        // Bicycle config group
        bicycleConfigGroup.setAttributes(bikeAttributes);
        bicycleConfigGroup.setWeights(bikeWeights);

    }

}
