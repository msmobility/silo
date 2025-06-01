package de.tum.bgu.msm.health;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import com.google.common.collect.Iterables;
import com.google.common.math.LongMath;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.job.JobMCR;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.SkimTravelTimes;
import de.tum.bgu.msm.health.data.*;
import de.tum.bgu.msm.health.diseaseModelOffline.HealthExposuresReader;
import de.tum.bgu.msm.health.injury.AccidentType;
import de.tum.bgu.msm.health.io.LinkInfoReader;
import de.tum.bgu.msm.health.io.ActivityLocationInfoReader;
import de.tum.bgu.msm.health.io.TripExposureWriter;
import de.tum.bgu.msm.health.io.TripReaderHealth;
import de.tum.bgu.msm.health.noise.NoiseMetrics;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.contrib.dvrp.trafficmonitoring.TravelTimeUtils;
import org.matsim.contrib.emissions.Pollutant;
import org.matsim.core.config.Config;
import org.matsim.core.controler.ControlerDefaults;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.speedy.SpeedyALTFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;
import org.matsim.vehicles.VehiclesFactory;
import routing.BicycleConfigGroup;
import routing.TransportModeNetworkFilter;
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

    public HealthExposureModelMCR(DataContainer dataContainer, Properties properties, Random random, Config config) {
        super(dataContainer, properties, random);
        this.initialMatsimConfig = config;
        simulatedDays = Arrays.asList(Day.thursday,Day.saturday,Day.sunday);
    }

    @Override
    public void setup() {
        if (properties.healthData.baseExposureFile != null) {
            new HealthExposuresReader().readData((HealthDataContainerImpl) dataContainer,properties.healthData.baseExposureFile);
        }
    }

    @Override
    public void prepareYear(int year) {}

    @Override
    public void endYear(int year) {
        //TODO: clean up the code to be compatible for different simulation setting
        if((properties.healthData.baseExposureFile ==null && year == properties.main.startYear) || properties.healthData.exposureModelYears.contains(year)) {
            logger.warn("Health model end year:" + year);
            TreeSet<Integer> sortedYears = new TreeSet<>(properties.transportModel.transportModelYears);
            latestMatsimYear = sortedYears.floor(year);
            latestMITOYear = year;

            Map<Integer, Trip> mitoTripsAll = new TripReaderHealth().readData(properties.main.baseDirectory + "scenOutput/"
                    + properties.main.scenarioName + "/" + latestMITOYear + "/microData/trips.csv");

            //clear the health data from last exposure model year
            for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
                ((PersonHealth) person).resetHealthData();
            }

            // process ndvi data
            processNdviData(NetworkUtils.readNetwork(initialMatsimConfig.network().getInputFile()));

            // assemble travel-activity health exposure data
            for(Day day : simulatedDays){
                logger.warn("Health model setup for " + day);

                replyLinkInfoFromFile(day);
                replyActivityLocationInfoFromFile(day);
                System.gc();

                logger.warn("Run health exposure model for " + day);

                for(Mode mode : Mode.values()){
                    switch (mode){
                        case autoDriver:
                        case autoPassenger:
                        case bicycle:
                        case walk:
                        case pt:
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
                ((DataContainerHealth)dataContainer).getLinkInfo().values().forEach(linkInfo -> {linkInfo.reset();});
                ((DataContainerHealth)dataContainer).getActivityLocations().values().forEach(activityLocation -> {activityLocation.reset();});
                System.gc();
            }

            // assemble home location health exposure data
            for(Day day : Day.values()){
                replyActivityLocationInfoFromFile(weekdays.contains(day) ? Day.thursday : day);
                calculatePersonHealthExposuresAtHome(day);
                ((DataContainerHealth)dataContainer).getActivityLocations().values().forEach(activityLocation -> {activityLocation.reset();});
                System.gc();
            }

            // normalize person-level home-travel-activity exposure
            calculatePersonHealthExposureMetrics();
        }

    }

    @Override
    public void endSimulation() {
    }

    private void replyLinkInfoFromFile(Day day) {
        String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/";

        new LinkInfoReader().readConcentrationData(((DataContainerHealth)dataContainer), outputDirectory + "linkConcentration_" + day + ".csv");

        //we produced concentration from bus vehicle source at link level, currently it is static over days and scenarios.
        //So add this as an additional concentration to link
        new LinkInfoReader().readConcentrationData(((DataContainerHealth)dataContainer), properties.healthData.busLinkConcentration);

        new LinkInfoReader().readNoiseLevelData(((DataContainerHealth)dataContainer), outputDirectory + "matsim/" + latestMatsimYear, day);

        logger.info("Initialized Link Info for " + ((DataContainerHealth)dataContainer).getLinkInfo().size() + " links ");
        // todo: I need to initialize the link info for link-based injury risks by accidentType
    }

    private void replyActivityLocationInfoFromFile(Day day) {
        String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/";

        ActivityLocationInfoReader reader = new ActivityLocationInfoReader();
        reader.readConcentrationData(((DataContainerHealth)dataContainer), outputDirectory + "locationConcentration_" + day + ".csv");

        //we produced concentration from bus vehicle source at location level, currently it is static over days and scenarios.
        //So add this as an additional concentration to activity location
        reader.readConcentrationData(((DataContainerHealth)dataContainer), properties.healthData.busLocationConcentration);

        reader.readNoiseLevelData(((DataContainerHealth)dataContainer), outputDirectory + "matsim/" + latestMatsimYear + "/" + day +  "/car/noise-analysis/immissions/");
    }

    private void healthDataAssembler(int year, Day day, Mode mode) {
        logger.info("Updating health data for year " + year + "|day: " + day + "|mode: " + mode + ".");

        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                + properties.main.scenarioName + "/matsim/" + latestMatsimYear;

        scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);
        ScenarioUtils.loadScenario(scenario);

        if (mode.equals(Mode.walk) || mode.equals(Mode.bicycle)) {
            Network activeNetwork = extractModeSpecificNetwork(scenario.getNetwork(),new HashSet<>(Arrays.asList(TransportMode.bike, TransportMode.walk)));
            scenario.setNetwork(activeNetwork);
        }

        scenario.getConfig().routing().setRoutingRandomness(0);
        scenario.getConfig().controller().setOutputDirectory(outputDirectoryRoot);

        //TODO: currently we don't have decent pt simulation. so pt trips has no actual routes.
        // Simple approach is used to roughly calculate exposures while pt (access, egress and bus-part) exposure. rail part is ignored
        if(mode.equals(Mode.pt)){
            calculateTripHealthIndicatorPt(new ArrayList<>(mitoTrips.values()), day, mode);
        }else{
            calculateTripHealthIndicator(new ArrayList<>(mitoTrips.values()), day, mode);
        }
    }

    private void calculateTripHealthIndicatorPt(ArrayList<Trip> trips, Day day, Mode mode) {
        logger.info("Updating trip health data for mode " + mode + ", day " + day);

        final int partitionSize = (int) ((double) trips.size() / Runtime.getRuntime().availableProcessors()) + 1;
        Iterable<List<Trip>> partitions = Iterables.partition(trips, partitionSize);

        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Runtime.getRuntime().availableProcessors());

        AtomicInteger counter = new AtomicInteger();
        logger.info("Partition Size: " + partitionSize);

        AtomicInteger NO_PATH_TRIP = new AtomicInteger();

        for (final List<Trip> partition : partitions) {
            executor.addTaskToQueue(() -> {
                try {
                    int id = counter.incrementAndGet();
                    int counterr = 0;
                    for (Trip trip : partition) {

                        if(LongMath.isPowerOfTwo(counterr)) {
                            logger.info(counterr + " in " + id);
                        }

                        Person siloPerson = dataContainer.getHouseholdDataManager().getPersonFromId(trip.getPerson());
                        MitoGender gender = MitoGender.valueOf(siloPerson.getGender().toString());
                        int age = Math.min(siloPerson.getAge(),100);
                        double walkSpeed = ((DataContainerHealth)dataContainer).getAvgSpeeds().get(Mode.walk).get(gender).get(age);

                        Zone originZone = dataContainer.getGeoData().getZones().get(trip.getTripOriginZone());
                        Zone destinationZone = dataContainer.getGeoData().getZones().get(trip.getTripDestinationZone());

                        double accessTime_s = dataContainer.getTravelTimes().getTravelTime(originZone,destinationZone,3600 * 8, "ptAccess");
                        double egressTime_s = dataContainer.getTravelTimes().getTravelTime(originZone,destinationZone,3600 * 8, "ptEgress");
                        double totalTravelTime_s = dataContainer.getTravelTimes().getTravelTime(originZone,destinationZone,3600 * 8, "ptTotalTravelTime");
                        double totalInVehicleTime_s = (totalTravelTime_s - accessTime_s - egressTime_s);
                        double busInVehicleTime_s =  totalInVehicleTime_s * dataContainer.getTravelTimes().getTravelTime(originZone,destinationZone,3600 * 8, "ptBusTimeShare");

                        if(Double.isInfinite(accessTime_s) || Double.isInfinite(egressTime_s)||Double.isInfinite(totalTravelTime_s)){
                            NO_PATH_TRIP.incrementAndGet();
                            continue;
                        }
                        // update access egress time based on person's walk speed;
                        // default beeline walk speed in MATSim is 3.0 km/h.
                        // it returns beeline distance. apply detour factor 1.2
                        accessTime_s = (accessTime_s * (3.0 / 3.6) * 1.2) / walkSpeed;
                        egressTime_s = (egressTime_s * (3.0 / 3.6) * 1.2) / walkSpeed;

                        int departureTimeInSeconds = trip.getDepartureTimeInMinutes() * 60;
                        processPtLegExposures(trip, Mode.walk, accessTime_s * walkSpeed, accessTime_s, departureTimeInSeconds);
                        if (busInVehicleTime_s > 0) {
                            processPtLegExposures(trip, Mode.bus,  -1, busInVehicleTime_s, departureTimeInSeconds + accessTime_s);
                        }

                        // rail/train part currently no exposure processed, but need to add up travel time
                        trip.updateMatsimTravelTime(totalInVehicleTime_s-busInVehicleTime_s);
                        ((PersonHealth) siloPerson).updateWeeklyTravelSeconds((float) (totalInVehicleTime_s-busInVehicleTime_s));

                        processPtLegExposures(trip, Mode.walk, egressTime_s * walkSpeed, egressTime_s, departureTimeInSeconds + accessTime_s + totalInVehicleTime_s);

                        if(trip.isHomeBased()) {
                            calculateActivityExposures(trip);
                            int returnDepartureTimeInSeconds = trip.getDepartureReturnInMinutes()*60;

                            accessTime_s = dataContainer.getTravelTimes().getTravelTime(destinationZone,originZone,3600 * 8, "ptAccess");
                            egressTime_s = dataContainer.getTravelTimes().getTravelTime(destinationZone,originZone,3600 * 8, "ptEgress");
                            totalTravelTime_s = dataContainer.getTravelTimes().getTravelTime(destinationZone,originZone,3600 * 8, "ptTotalTravelTime");
                            totalInVehicleTime_s = (totalTravelTime_s - accessTime_s - egressTime_s);
                            busInVehicleTime_s =  totalInVehicleTime_s * dataContainer.getTravelTimes().getTravelTime(destinationZone,originZone,3600 * 8, "ptBusTimeShare");

                            if(Double.isInfinite(totalTravelTime_s)||Double.isInfinite(accessTime_s) || Double.isInfinite(egressTime_s)){
                                NO_PATH_TRIP.incrementAndGet();
                                continue;
                            }
                            // update access egress time based on person's walk speed;
                            // default beeline walk speed in MATSim is 3.0 km/h.
                            // it returns beeline distance. apply detour factor 1.2
                            accessTime_s = (accessTime_s * (3.0 / 3.6) * 1.2) / walkSpeed;
                            egressTime_s = (egressTime_s * (3.0 / 3.6) * 1.2) / walkSpeed;

                            processPtLegExposures(trip, Mode.walk, accessTime_s * walkSpeed, accessTime_s, returnDepartureTimeInSeconds);
                            if (busInVehicleTime_s > 0) {
                                processPtLegExposures(trip, Mode.bus,  -1, busInVehicleTime_s, returnDepartureTimeInSeconds + accessTime_s);
                            }

                            // rail/train part currently no exposure processed, but need to add up travel time
                            trip.updateMatsimTravelTime(totalInVehicleTime_s-busInVehicleTime_s);
                            ((PersonHealth) siloPerson).updateWeeklyTravelSeconds((float) (totalInVehicleTime_s-busInVehicleTime_s));

                            processPtLegExposures(trip, Mode.walk, egressTime_s * walkSpeed, egressTime_s, returnDepartureTimeInSeconds + accessTime_s + totalInVehicleTime_s);

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

    private void processPtLegExposures(Trip trip, Mode legMode, double legDist_m, double legTime_s, double startTimeInSecond) {

        double legMarginalMetHours = 0.;

        float[] legExposurePm25ByHour = new float[24*7];
        float[] legExposureNo2ByHour = new float[24*7];
        double legExposurePm25 = 0.;
        double legExposureNo2 = 0.;

        float[] hourOccupied = new float[24*7];

        //Physical activity (assume access and egress walk)
        double legMarginalMet = PhysicalActivity.getMMet(legMode, legDist_m, legTime_s, null);
        legMarginalMetHours = legMarginalMet * legTime_s / 3600.;


        //Air Pollutant
        int dayCode = trip.getDepartureDay().getDayCode();

        double startDayHour = startTimeInSecond / 3600.;
        double endDayHour = (startTimeInSecond + legTime_s)/ 3600.;

        for(double currentDayHour = startDayHour; currentDayHour < endDayHour;) {
            if(currentDayHour >= 24){
                dayCode++;
                currentDayHour = currentDayHour - 24;
                endDayHour = endDayHour - 24;
            }

            int exactDayHour = (int) currentDayHour;
            int nextDayHour = exactDayHour + 1;
            double durationInThisHour = Math.min(endDayHour, nextDayHour) - currentDayHour;

            int exactWeekHour = exactDayHour + 24 * dayCode;

            if(exactWeekHour > 167){
                break;
            }

            hourOccupied[exactWeekHour] += (float) durationInThisHour;

            double legPartExposurePm25 = PollutionExposure.getLinkExposurePm25(legMode, properties.get().healthData.DEFAULT_ROAD_TRAFFIC_INCREMENTAL_PM25, legTime_s, legMarginalMet);
            double legPartExposureNo2 = PollutionExposure.getLinkExposureNo2(legMode, properties.get().healthData.DEFAULT_ROAD_TRAFFIC_INCREMENTAL_NO2,legTime_s, legMarginalMet);

            legExposurePm25ByHour[exactWeekHour] += legPartExposurePm25;
            legExposureNo2ByHour[exactWeekHour] += legPartExposureNo2;

            legExposurePm25 += legPartExposurePm25;
            legExposureNo2 += legPartExposureNo2;

            currentDayHour = nextDayHour;
        }

        trip.updateMatsimTravelTime(legTime_s);
        trip.updateMarginalMetHours(legMarginalMetHours);
        trip.updateTravelExposureMap(Map.of(
                "pm2.5", (float) legExposurePm25,
                "no2", (float) legExposureNo2
        ));

        PersonHealth siloPerson = ((PersonHealth)dataContainer.getHouseholdDataManager().getPersonFromId(trip.getPerson()));
        siloPerson.updateWeeklyTravelSeconds((float) legTime_s);
        siloPerson.updateWeeklyMarginalMetHours(legMode, (float) legMarginalMetHours);
        siloPerson.updateWeeklyPollutionExposuresByHour(Map.of(
                "pm2.5", legExposurePm25ByHour,
                "no2", legExposureNo2ByHour
        ));
        siloPerson.updateWeeklyTravelActivityHourOccupied(hourOccupied);
    }

    private void calculateTripHealthIndicator(List<Trip> trips, Day day, Mode mode) {
        logger.info("Updating trip health data for mode " + mode + ", day " + day);

        final int partitionSize = (int) ((double) trips.size() / Runtime.getRuntime().availableProcessors()) + 1;
        Iterable<List<Trip>> partitions = Iterables.partition(trips, partitionSize);

        TravelTime travelTime;
        TravelDisutility travelDisutility;

        EnumMap<Mode, EnumMap<MitoGender, Map<Integer,Double>>> allSpeeds = ((DataContainerHealth)dataContainer).getAvgSpeeds();
        VehiclesFactory fac = VehicleUtils.getFactory();

        switch (mode){
            case autoDriver:
            case autoPassenger:
                String eventsFile = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/car/" + latestMatsimYear + ".output_events.xml.gz";
                travelTime = TravelTimeUtils.createTravelTimesFromEvents(scenario.getNetwork(),scenario.getConfig(), eventsFile);
                travelDisutility = ControlerDefaults.createDefaultTravelDisutilityFactory(scenario).createTravelDisutility(travelTime);
                break;
            case walk:
                WalkConfigGroup walkConfigGroup = new WalkConfigGroup();
                fillConfigWithWalkStandardValue(walkConfigGroup);
                //without remove, throw run time exception "Module xx exists already".
                scenario.getConfig().removeModule("walk");
                scenario.getConfig().addModule(walkConfigGroup);
                // set vehicles
                for(MitoGender gender : MitoGender.values()) {
                    for(int age = 0 ; age <= 100 ; age++) {
                        VehicleType walk = fac.createVehicleType(Id.create(TransportMode.walk + gender + age, VehicleType.class));
                        walk.setMaximumVelocity(allSpeeds.get(Mode.walk).get(gender).get(age));
                        walk.setNetworkMode(TransportMode.walk);
                        walk.setPcuEquivalents(0.);
                        scenario.getVehicles().addVehicleType(walk);
                    }
                }
                travelTime = new WalkTravelTime(new WalkLinkSpeedCalculatorImpl(scenario.getConfig()));
                travelDisutility = new ActiveDisutilityPrecalc(scenario.getNetwork(),walkConfigGroup,travelTime);
                break;
            case bicycle:
                BicycleConfigGroup bicycleConfigGroup = new BicycleConfigGroup();
                fillConfigWithBikeStandardValue(bicycleConfigGroup);
                scenario.getConfig().removeModule("bike");
                scenario.getConfig().addModule(bicycleConfigGroup);
                // set vehicles
                for(MitoGender gender : MitoGender.values()) {
                    for(int age = 0 ; age <= 100 ; age++) {
                        VehicleType bicycle = fac.createVehicleType(Id.create(TransportMode.bike + gender + age, VehicleType.class));
                        bicycle.setMaximumVelocity(allSpeeds.get(Mode.bicycle).get(gender).get(age));
                        bicycle.setNetworkMode(TransportMode.bike);
                        bicycle.setPcuEquivalents(0.);
                        scenario.getVehicles().addVehicleType(bicycle);
                    }
                }
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

                        // Create person and vehicle for each person (i.e., trip) for active traveller
                        Vehicle vehicle = null;
                        org.matsim.api.core.v01.population.Person person = null;
                        if(mode.equals(Mode.walk)||mode.equals(Mode.bicycle)) {
                            Person siloPerson = dataContainer.getHouseholdDataManager().getPersonFromId(trip.getPerson());
                            MitoGender gender = MitoGender.valueOf(siloPerson.getGender().toString());
                            int age = siloPerson.getAge();

                            person = factory.createPerson(Id.createPersonId(trip.getId()));
                            person.getAttributes().putAttribute("purpose",trip.getTripPurpose());
                            person.getAttributes().putAttribute("sex",gender.toString());
                            person.getAttributes().putAttribute("age",age);


                            Id<Vehicle> vehicleId = Id.createVehicleId(person.getId().toString());
                            String key = (mode.equals(Mode.walk)? TransportMode.walk : TransportMode.bike) + gender + age;
                            VehicleType vehicleType = scenario.getVehicles().getVehicleTypes().get(Id.create(key, VehicleType.class));
                            vehicle = fac.createVehicle(vehicleId,vehicleType);
                        }

                        LeastCostPathCalculator.Path outboundPath = pathCalculator.calcLeastCostPath(originNode, destinationNode,outboundDepartureTimeInSeconds,person,vehicle);
                        if(outboundPath == null){
                            logger.warn("trip id: " + trip.getId() + ", trip depart time: " + trip.getDepartureTimeInMinutes() +
                                    "origin coord: [" + trip.getTripOrigin().getX() + "," + trip.getTripOrigin().getY() + "], " +
                                    "dest coord: [" + trip.getTripDestination().getX() + "," + trip.getTripDestination().getY() + "], " +
                                    "origin node: " + originNode + ", dest node: " + destinationNode);
                            NO_PATH_TRIP.getAndIncrement();
                        } else {
                            calculatePathExposures(trip,outboundPath,outboundDepartureTimeInSeconds,travelTime, vehicle);
                        }

                        // Calculate exposures for activity & return trip (home-based trips only)
                        // TODO: exposure for activity of non-home-based trips and RRT, currently we do not know their activity duration, so it is not calculated
                        if(trip.isHomeBased()) {
                            calculateActivityExposures(trip);
                            int returnDepartureTimeInSeconds = trip.getDepartureReturnInMinutes()*60;
                            LeastCostPathCalculator.Path returnPath = pathCalculator.calcLeastCostPath(destinationNode, originNode,returnDepartureTimeInSeconds,person,vehicle);
                            if(returnPath == null){
                                logger.warn("trip id: " + trip.getId() + ", trip depart time: " + trip.getDepartureTimeInMinutes() +
                                        "origin coord: [" + trip.getTripOrigin().getX() + "," + trip.getTripOrigin().getY() + "], " +
                                        "dest coord: [" +  trip.getTripDestination().getX() + "," + trip.getTripDestination().getY() + "], " +
                                        "origin node: " + originNode + ", dest node: " + destinationNode);
                                NO_PATH_TRIP.getAndIncrement();
                            } else {
                                calculatePathExposures(trip,returnPath,returnDepartureTimeInSeconds,travelTime, vehicle);
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

    private void calculatePathExposures(Trip trip, LeastCostPathCalculator.Path path, int departureTimeInSecond, TravelTime travelTime, Vehicle vehicle) {

        Mode mode = trip.getTripMode();

        double pathLength = 0;
        double pathTime = 0;
        double pathMarginalMetHours = 0;

        float[] pathExposurePm25ByHour = new float[24*7];
        float[] pathExposureNo2ByHour = new float[24*7];
        float[] pathExposureNoiseByHour = new float[24*7];
        double pathExposurePm25 = 0.;
        double pathExposureNo2 = 0.;
        double pathExposureNoise = 0.;

        double pathExposureGreen = 0.;

        // Injury variables
        // Munich
        double pathSevereInjuryRisk = 0;
        double pathFatalityRisk = 0;

        // Manchester
        double pathInjuryRisk = 1.;

        float[] hourOccupied = new float[24*7];

        for(Link link : path.links) {
            double enterTimeInSecond = (double) departureTimeInSecond + pathTime;
            double linkLength = link.getLength();
            double linkTime = travelTime.getLinkTravelTime(link,enterTimeInSecond,null,vehicle);

            // Munich
            double linkSevereInjuryRisk = 0.;
            double linkFatalityRisk = 0.;

            // Manchester
            double linkInjuryRisk = 0.;

            double linkMarginalMetHour = 0.;
            double linkExposureGreen = 0.;
            double linkExposurePm25 = 0.;
            double linkExposureNo2 = 0.;
            double linkExposureNoise = 0.;

            LinkInfo linkInfo = ((DataContainerHealth)dataContainer).getLinkInfo().get(link.getId());

            // INJURY
            //double[] severeFatalRisk = getLinkSevereFatalInjuryRisk(mode, (int) (enterTimeInSecond / 3600.), linkInfo);
            //linkSevereInjuryRisk = severeFatalRisk[0];
            //linkFatalityRisk = severeFatalRisk[1];
            linkInjuryRisk = getLinkInjuryRisk(mode, (int) enterTimeInSecond, linkInfo);





            // PHYSICAL ACTIVITY
            double linkMarginalMet = PhysicalActivity.getMMet(mode, linkLength, linkTime, link);
            linkMarginalMetHour = linkMarginalMet * linkTime / 3600.;

            // NDVI
            linkExposureGreen = linkInfo.getNdvi() * linkTime / 3600.;

            if(linkInfo!=null) {

                int dayCode = trip.getDepartureDay().getDayCode();
                double startDayHour = enterTimeInSecond / 3600.;
                double endDayHour = (enterTimeInSecond + linkTime)/ 3600.;

                for(double currentDayHour = startDayHour; currentDayHour < endDayHour;) {
                    //check if start hour is already next day, it could be that trip starts at 23:30, after travelling (e.g. 40 mins), activity start time is next day
                    //the limitation is already we move it to next day, the AP and noise data is still retrived from the current day. because to save memory, we handle trips day by day and only retrive AP noise data of the corresponding day
                    //so it might slightly overestimate the personal exposure, if we use weekday for (next day) saturday
                    if(currentDayHour >= 24){
                        dayCode++;
                        currentDayHour = currentDayHour - 24;
                        endDayHour = endDayHour - 24;
                    }

                    int exactDayHour = (int) currentDayHour;
                    int nextDayHour = exactDayHour + 1;
                    double durationInThisHour = Math.min(endDayHour, nextDayHour) - currentDayHour;

                    int exactWeekHour = exactDayHour + 24 * dayCode;

                    if(exactWeekHour > 167){
                        break;
                    }

                    hourOccupied[exactWeekHour] += (float) durationInThisHour;

                    // AIR POLLUTION
                    double linkConcentrationPm25 = linkInfo.getExposure2Pollutant2TimeBin().getOrDefault(Pollutant.PM2_5,new OpenIntFloatHashMap()).get(exactDayHour) +
                            linkInfo.getExposure2Pollutant2TimeBin().getOrDefault(Pollutant.PM2_5_non_exhaust,new OpenIntFloatHashMap()).get(exactDayHour);
                    double linkConcentrationNo2 = linkInfo.getExposure2Pollutant2TimeBin().getOrDefault(Pollutant.NO2,new OpenIntFloatHashMap()).get(exactDayHour);

                    linkExposurePm25 = PollutionExposure.getLinkExposurePm25(mode, linkConcentrationPm25, durationInThisHour * 3600, linkMarginalMet);
                    linkExposureNo2 =PollutionExposure.getLinkExposureNo2(mode, linkConcentrationNo2, durationInThisHour * 3600, linkMarginalMet);

                    pathExposurePm25ByHour[exactWeekHour] += linkExposurePm25;
                    pathExposureNo2ByHour[exactWeekHour] += linkExposureNo2;

                    //TODO: bike/walk-only link has no noise emission (noise produced on that link). currently we assume 0 noise level while travelling on those links.
                    // Later, we can do geo-spatialling and associate these link to nearest car link? or Instead of using noise emission, we consider each link as noise receivers and do proper noise exposure
                    if(!linkInfo.getNoiseLevel2TimeBin().isEmpty()){
                        linkExposureNoise = linkInfo.getNoiseLevel2TimeBin().get(exactDayHour) * durationInThisHour;
                        pathExposureNoiseByHour[exactWeekHour] += linkExposureNoise;
                    }

                    pathExposurePm25 += linkExposurePm25;
                    pathExposureNo2 += linkExposureNo2;
                    pathExposureNoise += linkExposureNoise;

                    currentDayHour = nextDayHour;
                }

            } else{
                logger.warn("No link info found for link id " + link.getId());
            }

            pathLength += linkLength;
            pathTime += linkTime;

            // pathSevereInjuryRisk += linkSevereInjuryRisk - (pathSevereInjuryRisk * linkSevereInjuryRisk);
            // pathFatalityRisk += linkFatalityRisk - (pathFatalityRisk * linkFatalityRisk);
            pathInjuryRisk *= (1 - linkInjuryRisk);


            pathMarginalMetHours += linkMarginalMetHour;
            pathExposureGreen += linkExposureGreen;
        }

        trip.updateMatsimTravelDistance(pathLength);
        trip.updateMatsimTravelTime(pathTime);
        trip.updateMatsimLinkCount(path.links.size());

        trip.updateMarginalMetHours(pathMarginalMetHours);

        /*
        trip.updateTravelRiskMap(Map.of(
                "severeInjury", (float) pathSevereInjuryRisk,
                "fatality", (float) pathFatalityRisk
        ));

         */

        // Manchester
        pathInjuryRisk = 1- pathInjuryRisk;
        trip.updateTravelRiskMap(Map.of("severeFatalInjury", (float) pathInjuryRisk));

        trip.updateTravelExposureMap(Map.of(
                "pm2.5", (float) pathExposurePm25,
                "no2", (float) pathExposureNo2
        ));
        trip.updateTravelNoiseExposure(pathExposureNoise);
        trip.updateTravelNdviExposure(pathExposureGreen);

        PersonHealth siloPerson = ((PersonHealth)dataContainer.getHouseholdDataManager().getPersonFromId(trip.getPerson()));
        siloPerson.updateWeeklyTravelSeconds((float) pathTime);

        /*
        siloPerson.updateWeeklyAccidentRisks(Map.of(
                "severeInjury", (float) pathSevereInjuryRisk,
                "fatality", (float) pathFatalityRisk));

         */

        // todo: by mode ??
        // siloPerson.updateWeeklyAccidentRisks(Map.of("severeFatalInjury", (float) pathInjuryRisk));

        //
        switch(mode){
            case autoDriver:
            case autoPassenger:
                siloPerson.updateWeeklyAccidentRisks(Map.of("severeFatalInjuryCar", (float) pathInjuryRisk));
                break;
            case bicycle:
                siloPerson.updateWeeklyAccidentRisks(Map.of("severeFatalInjuryWalk", (float) pathInjuryRisk));
                break;
            case walk:
                siloPerson.updateWeeklyAccidentRisks(Map.of("severeFatalInjuryBike", (float) pathInjuryRisk));
                break;
            default:
                throw new RuntimeException("Undefined mode " + mode);
        }

        siloPerson.updateWeeklyMarginalMetHours(trip.getTripMode(), (float) pathMarginalMetHours);

        siloPerson.updateWeeklyPollutionExposuresByHour(Map.of(
                "pm2.5", pathExposurePm25ByHour,
                "no2", pathExposureNo2ByHour
        ));
        siloPerson.updateWeeklyNoiseExposuresByHour(pathExposureNoiseByHour);
        siloPerson.updateWeeklyGreenExposures((float) pathExposureGreen);
        siloPerson.updateWeeklyTravelActivityHourOccupied(hourOccupied);
    }

    private void calculateActivityExposures(Trip trip) {
        float[] hourOccupied = new float[24*7];
        float[] activityExposurePM25ByHour = new float[24*7];
        float[] activityExposureNo2ByHour = new float[24*7];
        float[] activityNoiseExposureByHour = new float[24*7];
        double activityGreenExposure = 0.;
        double activityExposurePM25 = 0.;
        double activityExposureNo2 = 0.;
        double activityNoiseExposure = 0.;

        double activityDurationInMinutes = trip.getActivityDuration();

        int dayCode = trip.getDepartureDay().getDayCode();
        double startDayHour = (trip.getDepartureTimeInMinutes() + trip.getMatsimTravelTime()/60.) / 60.;
        double endDayHour = (startDayHour + activityDurationInMinutes/60.);


        for(double currentDayHour = startDayHour; currentDayHour < endDayHour;) {
            //check if start hour is already next day, it could be that trip starts at 23:30, after travelling (e.g. 40 mins), activity start time is next day
            if(currentDayHour >= 24){
                dayCode++;
                currentDayHour = currentDayHour - 24;
                endDayHour = endDayHour - 24;
            }
            int exactDayHour = (int) currentDayHour;
            int nextDayHour = exactDayHour + 1;
            double durationInThisHour = Math.min(endDayHour, nextDayHour) - currentDayHour;

            int exactWeekHour = exactDayHour + 24 * dayCode;

            if(exactWeekHour > 167){
                break;
            }
            hourOccupied[exactWeekHour] = (float) durationInThisHour;

            String rpId = getReceiverPointId(trip.getTripDestinationType(), trip.getTripDestinationMicroId());
            ActivityLocation activityLocation = ((DataContainerHealth)dataContainer).getActivityLocations().get(rpId);

            // todo: I found that new dwellings have no receiver points when we run simulations longitudinally, and given that the AP model is not run everytime
            // take the nearest receiver point and assign it ??

            /*double distMeasured = 1000000000.0;
            double currentMeasured = 0.0;

            for(Map.Entry<String, ActivityLocation> entry : ((DataContainerHealth) dataContainer).getActivityLocations().entrySet()){
                Coord coordDest = trip.getTripDestination();
                Coordinate coordRP = entry.getValue().getCoordinate();
                currentMeasured = NetworkUtils.getEuclideanDistance(coordDest.getX(), coordDest.getY(), coordRP.getX(), coordRP.getY());
                if(currentMeasured < distMeasured) {
                    distMeasured = currentMeasured;
                    rpId = entry.getValue().getLocationId();
                }
                // todo: compare coordinate, and take the nearest
            }*/

            if(activityLocation != null) {

                //Air pollutant
                double locationIncrementalPM25 = activityLocation.getExposure2Pollutant2TimeBin().get(Pollutant.PM2_5).get(exactDayHour)+
                        activityLocation.getExposure2Pollutant2TimeBin().get(Pollutant.PM2_5_non_exhaust).get(exactDayHour);
                double locationIncrementalNO2 = activityLocation.getExposure2Pollutant2TimeBin().get(Pollutant.NO2).get(exactDayHour);

                double exposurePM25 = PollutionExposure.getActivityExposurePm25(durationInThisHour * 60, locationIncrementalPM25);
                double exposureNo2 = PollutionExposure.getActivityExposureNo2(durationInThisHour * 60, locationIncrementalNO2);
                activityExposurePM25ByHour[exactWeekHour] = (float) exposurePM25;
                activityExposureNo2ByHour[exactWeekHour] = (float) exposureNo2;

                activityExposurePM25 += exposurePM25;
                activityExposureNo2 += exposureNo2;

                // Noise level
                if(!activityLocation.getNoiseLevel2TimeBin().isEmpty()){
                    double noiseExposure = activityLocation.getNoiseLevel2TimeBin().get(exactDayHour) * durationInThisHour;
                    activityNoiseExposureByHour[exactWeekHour] = (float) noiseExposure;
                    activityNoiseExposure += noiseExposure;
                }

                //Green ndvi
                //TODO: should we eliminate ndvi exposure in night?
                activityGreenExposure += activityLocation.getNdvi() * durationInThisHour;

            }else{
                logger.warn("No receiver point info found for rpId: " + rpId + " tripId: " + trip.getTripId());
            }

            currentDayHour = nextDayHour;
        }

        trip.updateDepartureReturnInMinutes((int)(endDayHour*60));
        trip.setActivityExposureMap(Map.of(
                "pm2.5", (float) activityExposurePM25,
                "no2", (float) activityExposureNo2
        ));
        trip.setActivityNoiseExposure(activityNoiseExposure);
        trip.setActivityNdviExposure(activityGreenExposure);

        PersonHealth siloPerson =  ((PersonHealth)dataContainer.getHouseholdDataManager().getPersonFromId(trip.getPerson()));

        siloPerson.updateWeeklyActivityMinutes((float) activityDurationInMinutes);
        siloPerson.updateWeeklyTravelActivityHourOccupied(hourOccupied);
        siloPerson.updateWeeklyPollutionExposuresByHour(Map.of(
                "pm2.5", activityExposurePM25ByHour,
                "no2", activityExposureNo2ByHour
        ));
        siloPerson.updateWeeklyNoiseExposuresByHour(activityNoiseExposureByHour);
        siloPerson.updateWeeklyGreenExposures((float) activityGreenExposure);

    }

    private void calculatePersonHealthExposuresAtHome(Day day) {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {

            double minutesAtHome = 0.;
            float[] exposurePM25 = new float[24*7];
            float[] exposureNo2 = new float[24*7];
            float[] exposureNoise = new float[24*7];
            double ndviExposure = 0.;

            float[] hourOccupied = ((PersonHealth) person).getWeeklyTravelActivityHourOccupied();

            for(int dayHour = 0; dayHour < 24; dayHour++) {
                int weekHour = dayHour + 24 * day.getDayCode();
                float remainingHour = 1.f - hourOccupied[weekHour];
                minutesAtHome += remainingHour * 60;

                if (remainingHour <= 0.) {
                    continue;
                }


                String rpId = ("dd" + person.getHousehold().getDwellingId());
                ActivityLocation activityLocation = ((DataContainerHealth)dataContainer).getActivityLocations().get(rpId);


                if(activityLocation != null) {
                    //Air pollutant
                    double locationIncrementalPM25 = activityLocation.getExposure2Pollutant2TimeBin().get(Pollutant.PM2_5).get(dayHour)+
                            activityLocation.getExposure2Pollutant2TimeBin().get(Pollutant.PM2_5_non_exhaust).get(dayHour);
                    double locationIncrementalNO2 = activityLocation.getExposure2Pollutant2TimeBin().get(Pollutant.NO2).get(dayHour);

                    exposurePM25[weekHour] = (float) PollutionExposure.getHomeExposurePm25(remainingHour * 60, dayHour, locationIncrementalPM25);
                    exposureNo2[weekHour] = (float) PollutionExposure.getHomeExposureNo2(remainingHour * 60, dayHour, locationIncrementalNO2);

                    // Noise level
                    if(!activityLocation.getNoiseLevel2TimeBin().isEmpty()){
                        exposureNoise[weekHour] = activityLocation.getNoiseLevel2TimeBin().get(dayHour) * remainingHour;
                    }

                    // Green ndvi
                    ndviExposure += activityLocation.getNdvi() * remainingHour;
                }else{
                    logger.warn("No receiver point info found for rpId: " + rpId + " personId: " + person.getId());
                }
            }


            ((PersonHealth) person).updateWeeklyHomeMinutes((float) minutesAtHome);
            ((PersonHealth) person).updateWeeklyPollutionExposuresByHour(Map.of(
                    "pm2.5", exposurePM25,
                    "no2", exposureNo2
            ));
            ((PersonHealth) person).updateWeeklyNoiseExposuresByHour(exposureNoise);
            ((PersonHealth) person).updateWeeklyGreenExposures((float) ndviExposure);

        }
    }

    private void calculatePersonHealthExposureMetrics() {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            float sumHour = 0.f;
            float sumNightHour = 0.f;
            float sumExposurePM25_normalized = 0.f;
            float sumExposureNo2_normalized = 0.f;
            float sumExposureNoise = 0.f;
            float sumExposureNoiseNight = 0.f;

            Map<String, float[]> weeklyPollutionExposures = ((PersonHealth) person).getWeeklyPollutionExposures();
            float[] weeklyNoiseExposureByHour = ((PersonHealth) person).getWeeklyNoiseExposureByHour();
            float[] hourOccupied = ((PersonHealth) person).getWeeklyTravelActivityHourOccupied();


            for (int weekHour = 0;  weekHour < hourOccupied.length; weekHour++) {
                int dayHour = weekHour % 24;

                sumHour += Math.max(1, hourOccupied[weekHour]);

                double min_ventilation_rate = 0.;
                if (dayHour <= 7  || dayHour > 23 ){
                    //"minimum"  ventilation rate = 0.27 (v_sleep)
                    min_ventilation_rate = 0.27;
                } else {
                    //"minimum"  ventilation rate = 0.61 (v_rest)
                    min_ventilation_rate = 0.61;
                }

                sumExposurePM25_normalized += weeklyPollutionExposures.get("pm2.5")[weekHour]/Math.max(1, hourOccupied[weekHour])/min_ventilation_rate;
                sumExposureNo2_normalized += weeklyPollutionExposures.get("no2")[weekHour]/Math.max(1, hourOccupied[weekHour])/min_ventilation_rate;


                float hourlyNoiseLevel = (float) NoiseMetrics.getHourlyNoiseLevel(dayHour, (weeklyNoiseExposureByHour[weekHour]/Math.max(1, hourOccupied[weekHour])));
                sumExposureNoise += hourlyNoiseLevel;

                if (dayHour <= 7  || dayHour > 23 ){
                    sumNightHour += Math.max(1, hourOccupied[weekHour]);
                    sumExposureNoiseNight += hourlyNoiseLevel;
                }
            }



            ((PersonHealth) person).setWeeklyExposureByPollutantNormalised(
                    Map.of(
                            "pm2.5", (float) (sumExposurePM25_normalized / 168.),
                            "no2", (float) (sumExposureNo2_normalized / 168.)
                    )
            );

            float Lden = (float) (10 * Math.log10(sumExposureNoise / sumHour));
            float Lnight = (float) (10 * Math.log10(sumExposureNoiseNight / sumNightHour));
            ((PersonHealth) person).setWeeklyNoiseExposuresNormalised (Lden);
            ((PersonHealthMCR) person).setNoiseHighAnnoyedPercentage((float) NoiseMetrics.getHighAnnoyedPercentage(Lden));
            ((PersonHealthMCR) person).setNoiseHighSleepDisturbancePercentage((float) NoiseMetrics.getHighSleepDisturbancePercentage(Lnight));

            ((PersonHealth) person).setWeeklyGreenExposuresNormalised(((PersonHealthMCR) person).getWeeklyNdviExposure() / sumHour);
        }
    }

    private String getReceiverPointId(String tripDestinationType, int tripDestinationMicroId) {
        if("household".equals(tripDestinationType)){
            int ddId = dataContainer.getHouseholdDataManager().getHouseholdFromId(tripDestinationMicroId).getDwellingId();
            return "dd" + ddId;
        }else if ("vacantDwelling".equals(tripDestinationType)){
            return "dd" + tripDestinationMicroId;
        }else if ("job".equals(tripDestinationType)){
            JobMCR job = (JobMCR)dataContainer.getJobDataManager().getJobFromId(tripDestinationMicroId);
            if(job != null) {
                if("poi".equals(job.getMicrolocationType())) {
                    return "poi" + job.getMicroBuildingId();
                }else if("zoneCentroid".equals(job.getMicrolocationType())) {
                    return "zone" + job.getMicroBuildingId();
                }
            }
        }else if ("poi".equals(tripDestinationType)){
            return "poi" + tripDestinationMicroId;
        }else if ("zoneCentroid".equals(tripDestinationType)){
            return "zone" + tripDestinationMicroId;
        }else if ("school".equals(tripDestinationType)){
            return "ss" + tripDestinationMicroId;
        }else {
            logger.warn("Unknown receiver point type: " + tripDestinationType);
        }

        return null;
    }

    private double getLinkInjuryRisk(Mode mode, int time, LinkInfo linkInfo){
        double linkInjuryRisk = 0.;
        switch (mode){
            case autoDriver:
            case autoPassenger:
                linkInjuryRisk = linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.CAR_ONEWAY).get((int) (time / 3600.))
                        + linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.CAR_TWOWAY).get((int) (time / 3600.));
                break;
            case bicycle:
                linkInjuryRisk = linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKE_MAJOR).get((int) (time / 3600.))
                        + linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKE_MINOR).get((int) (time / 3600.));
                break;
            case walk:
                linkInjuryRisk = linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.PED).get((int) (time / 3600.))
                        + linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.PED).get((int) (time / 3600.));
                break;
            default:
                throw new RuntimeException("Undefined mode " + mode);
        }
        return linkInjuryRisk;
    }

    private double[] getLinkSevereFatalInjuryRisk(Mode mode, int hour, LinkInfo linkInfo) {
        // Munich
        double FATAL_CAR_DRIVER = 0.077;
        double FATAL_BIKECAR_BIKE = 0.024;
        double FATAL_BIKEBIKE_BIKE = 0.051;
        double FATAL_PED_PED = 0.073;

        double severeInjuryRisk;
        double fatalityRisk;
        Map<AccidentType, OpenIntFloatHashMap> exposure = linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime();

        switch (mode){
            case autoPassenger:
            case autoDriver:
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

    private void fillConfigWithWalkStandardValue(WalkConfigGroup walkConfigGroup) {
        // WALK ATTRIBUTES
        List<ToDoubleFunction<Link>> walkAttributes = new ArrayList<>();
        walkAttributes.add(l -> Math.max(0.,0.81 - LinkAmbience.getVgviFactor(l)));
        walkAttributes.add(l -> Math.min(1.,((double) l.getAttributes().getAttribute("speedLimitMPH")) / 50.));
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
                    if(p.getAttributes().getAttribute("sex").equals(Gender.FEMALE)) {
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

    private Network extractModeSpecificNetwork(Network fullNetwork, Set<String> transportModes) {

        Network modeSpecificNetwork = NetworkUtils.createNetwork();

        new TransportModeNetworkFilter(fullNetwork).filter(modeSpecificNetwork, transportModes);
        NetworkUtils.runNetworkCleaner(modeSpecificNetwork);
        return modeSpecificNetwork;
    }

    public void processNdviData(Network network) {

        for(Link link : network.getLinks().values()){
            double ndvi = 0.;
            if(link.getAttributes().getAttribute("ndvi")!=null){
                ndvi = (double) link.getAttributes().getAttribute("ndvi");
            }
            ((DataContainerHealth)dataContainer).getLinkInfo().get(link.getId()).setNdvi(ndvi);
        }


        for (ActivityLocation locationInfo :  ((DataContainerHealth)dataContainer).getActivityLocations().values()){
            Link link = NetworkUtils.getNearestLink(network, CoordUtils.createCoord(locationInfo.getCoordinate()));

            if (link!=null){
                locationInfo.setNdvi((Double) link.getAttributes().getAttribute("ndvi"));
            }
        }
    }

    public void calculateHomeBasedExposureOnly(int year){
        latestMatsimYear = year;
        processNdviData(NetworkUtils.readNetwork(initialMatsimConfig.network().getInputFile()));

        //assemble person home exposure by day by hour
        for(Day day : Day.values()) {
            replyActivityLocationInfoFromFile(weekdays.contains(day) ? Day.thursday : day);
            for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {

                double minutesAtHome = 0.;
                float[] exposurePM25 = new float[24 * 7];
                float[] exposureNo2 = new float[24 * 7];
                float[] exposureNoise = new float[24 * 7];
                double ndviExposure = 0.;

                for (int dayHour = 0; dayHour < 24; dayHour++) {
                    int weekHour = dayHour + 24 * day.getDayCode();
                    minutesAtHome += 60;

                    String rpId = ("dd" + person.getHousehold().getDwellingId());
                    ActivityLocation activityLocation = ((DataContainerHealth) dataContainer).getActivityLocations().get(rpId);


                    if (activityLocation != null) {
                        //Air pollutant
                        double locationIncrementalPM25 = activityLocation.getExposure2Pollutant2TimeBin().get(Pollutant.PM2_5).get(dayHour) +
                                activityLocation.getExposure2Pollutant2TimeBin().get(Pollutant.PM2_5_non_exhaust).get(dayHour);
                        double locationIncrementalNO2 = activityLocation.getExposure2Pollutant2TimeBin().get(Pollutant.NO2).get(dayHour);

                        exposurePM25[weekHour] = (float) PollutionExposure.getHomeExposurePm25(60, dayHour, locationIncrementalPM25);
                        exposureNo2[weekHour] = (float) PollutionExposure.getHomeExposureNo2(60, dayHour, locationIncrementalNO2);

                        // Noise level
                        if (!activityLocation.getNoiseLevel2TimeBin().isEmpty()) {
                            exposureNoise[weekHour] = activityLocation.getNoiseLevel2TimeBin().get(dayHour);
                        }

                        // Green ndvi
                        ndviExposure += activityLocation.getNdvi();
                    } else {
                        logger.warn("No receiver point info found for rpId: " + rpId + " personId: " + person.getId());
                    }
                }


                ((PersonHealth) person).updateWeeklyHomeMinutes((float) minutesAtHome);
                ((PersonHealth) person).updateWeeklyPollutionExposuresByHour(Map.of(
                        "pm2.5", exposurePM25,
                        "no2", exposureNo2
                ));
                ((PersonHealth) person).updateWeeklyNoiseExposuresByHour(exposureNoise);
                ((PersonHealth) person).updateWeeklyGreenExposures((float) ndviExposure);

            }

            ((DataContainerHealth)dataContainer).getActivityLocations().values().forEach(activityLocation -> {activityLocation.reset();});
            System.gc();
        }


        //normalized person's home exposure over a week
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            float sumHour = 168.f;
            float sumNightHour = 56.f;
            float sumExposurePM25_normalized = 0.f;
            float sumExposureNo2_normalized = 0.f;
            float sumExposureNoise = 0.f;
            float sumExposureNoiseNight = 0.f;

            Map<String, float[]> weeklyPollutionExposures = ((PersonHealth) person).getWeeklyPollutionExposures();
            float[] weeklyNoiseExposureByHour = ((PersonHealth) person).getWeeklyNoiseExposureByHour();

            for (int weekHour = 0;  weekHour < 168; weekHour++) {
                int dayHour = weekHour % 24;

                double min_ventilation_rate = 0.;
                if (dayHour <= 7  || dayHour > 23 ){
                    //"minimum"  ventilation rate = 0.27 (v_sleep)
                    min_ventilation_rate = 0.27;
                } else {
                    //"minimum"  ventilation rate = 0.61 (v_rest)
                    min_ventilation_rate = 0.61;
                }

                sumExposurePM25_normalized += weeklyPollutionExposures.get("pm2.5")[weekHour]/min_ventilation_rate;
                sumExposureNo2_normalized += weeklyPollutionExposures.get("no2")[weekHour]/min_ventilation_rate;


                float hourlyNoiseLevel = (float) NoiseMetrics.getHourlyNoiseLevel(dayHour, (weeklyNoiseExposureByHour[weekHour]));
                sumExposureNoise += hourlyNoiseLevel;

                if (dayHour <= 7  || dayHour > 23 ){
                    sumExposureNoiseNight += hourlyNoiseLevel;
                }
            }



            ((PersonHealth) person).setWeeklyExposureByPollutantNormalised(
                    Map.of(
                            "pm2.5", (float) (sumExposurePM25_normalized / sumHour),
                            "no2", (float) (sumExposureNo2_normalized / sumHour)
                    )
            );

            float Lden = (float) (10 * Math.log10(sumExposureNoise / sumHour));
            float Lnight = (float) (10 * Math.log10(sumExposureNoiseNight / sumNightHour));
            ((PersonHealth) person).setWeeklyNoiseExposuresNormalised (Lden);
            ((PersonHealthMCR) person).setNoiseHighAnnoyedPercentage((float) NoiseMetrics.getHighAnnoyedPercentage(Lden));
            ((PersonHealthMCR) person).setNoiseHighSleepDisturbancePercentage((float) NoiseMetrics.getHighSleepDisturbancePercentage(Lnight));

            ((PersonHealth) person).setWeeklyGreenExposuresNormalised(((PersonHealthMCR) person).getWeeklyNdviExposure() / sumHour);
        }
    }


}
