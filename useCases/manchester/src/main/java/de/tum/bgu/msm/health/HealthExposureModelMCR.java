package de.tum.bgu.msm.health;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import com.google.common.collect.Iterables;
import com.google.common.math.LongMath;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.job.JobMCR;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.health.data.*;
import de.tum.bgu.msm.health.injury.AccidentType;
import de.tum.bgu.msm.health.io.LinkInfoReader;
import de.tum.bgu.msm.health.io.ReceiverPointInfoReader;
import de.tum.bgu.msm.health.io.TripExposureWriter;
import de.tum.bgu.msm.health.io.TripReaderHealth;
import de.tum.bgu.msm.health.noise.NoiseMetrics;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.School;
import de.tum.bgu.msm.schools.SchoolImpl;
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
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;
import org.matsim.vehicles.VehiclesFactory;
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
        if(year == properties.main.startYear || properties.healthData.exposureModelYears.contains(year)) {
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

            // assemble travel-activity health exposure data
            for(Day day : simulatedDays){
                logger.warn("Health model setup for " + day);

                replyLinkInfoFromFile(day);
                replyReceiverPointInfoFromFile(day);

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

            // assemble home location health exposure data
            for(Day day : Day.values()){
                replyReceiverPointInfoFromFile(weekdays.contains(day) ? Day.thursday : day);
                calculatePersonHealthExposuresAtHome(day);
                ((DataContainerHealth)dataContainer).reset();
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

        //need to initialize link info and zone exposure map everytime, because to save memory, dataContainer.reset for each day/mode assembler
        Scenario scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);
        //need to use full network (include all car, active mode links) for dispersion
        String networkFile = properties.main.baseDirectory + properties.healthData.network_for_airPollutant_model;
        new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
        Map<Id<Link>, LinkInfo> linkInfoMap = new HashMap<>();
        for(Link link : scenario.getNetwork().getLinks().values()){
            linkInfoMap.computeIfAbsent(link.getId(), id -> new LinkInfo(id))
                    .setNdvi((Double) link.getAttributes().getAttribute("ndvi"));
        }
        ((DataContainerHealth)dataContainer).setLinkInfo(linkInfoMap);

        new LinkInfoReader().readConcentrationData(((DataContainerHealth)dataContainer), outputDirectory, day,"carTruck");

        new LinkInfoReader().readNoiseLevelData(((DataContainerHealth)dataContainer), outputDirectory + "matsim/" + latestMatsimYear, day);

        logger.info("Initialized Link Info for " + ((DataContainerHealth)dataContainer).getLinkInfo().size() + " links ");
    }

    private void replyReceiverPointInfoFromFile(Day day) {
        String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/";

        //currently the receiver points consist of all dwelling location, school location, zone centroid, and poi location
        Map<String, ReceiverPointInfo> receiverPointInfoMap = new HashMap<>();
        for(Dwelling dd : dataContainer.getRealEstateDataManager().getDwellingData().getDwellings()){
            receiverPointInfoMap.put("dd"+dd.getId(),new ReceiverPointInfo(("dd"+dd.getId()), dd.getCoordinate()));
        }

        for(School ss : ((DataContainerWithSchools)dataContainer).getSchoolData().getSchools()){
            receiverPointInfoMap.put("ss"+ss.getId(),new ReceiverPointInfo(("ss"+ss.getId()),((SchoolImpl)ss).getCoordinate()));
        }

        for(Zone zone : dataContainer.getGeoData().getZones().values()){
            receiverPointInfoMap.put("zone"+zone.getId(),new ReceiverPointInfo(("zone"+zone.getId()),zone.getPopCentroidCoord()));
            for (int poi : ((ZoneMCR) zone).getMicroDestinations().keySet()){
                receiverPointInfoMap.put("poi"+poi,new ReceiverPointInfo(("poi"+poi),((ZoneMCR) zone).getMicroDestinations().get(poi)));
            }
        }

        ((DataContainerHealth)dataContainer).setReceiverPointInfo(receiverPointInfoMap);
        logger.info("Initialized Receiver point Info for " + ((DataContainerHealth)dataContainer).getReceiverPointInfo().size() + " points ");

        ReceiverPointInfoReader reader = new ReceiverPointInfoReader();
        reader.readConcentrationData(((DataContainerHealth)dataContainer), outputDirectory, day,"carTruck");
        reader.readNoiseLevelData(((DataContainerHealth)dataContainer), outputDirectory + "matsim/" + latestMatsimYear, day);
        reader.processNdviData(((DataContainerHealth)dataContainer), scenario.getNetwork());

    }

    private void healthDataAssembler(int year, Day day, Mode mode) {
        logger.info("Updating health data for year " + year + "|day: " + day + "|mode: " + mode + ".");

        final String outputDirectoryRoot = properties.main.baseDirectory + "scenOutput/"
                + properties.main.scenarioName + "/matsim/" + latestMatsimYear;

        scenario = ScenarioUtils.createMutableScenario(initialMatsimConfig);
        scenario.getConfig().routing().setRoutingRandomness(0);
        scenario.getConfig().controller().setOutputDirectory(outputDirectoryRoot);

        if(mode.equals(Mode.pt)){
            calculateTripHealthIndicatorNoRoute(new ArrayList<>(mitoTrips.values()), day, mode);
        }else{
            calculateTripHealthIndicator(new ArrayList<>(mitoTrips.values()), day, mode);
        }
    }

    private void calculateTripHealthIndicatorNoRoute(ArrayList<Trip> trips, Day day, Mode mode) {
        //TODO
    }

    private void calculateTripHealthIndicator(List<Trip> trips, Day day, Mode mode) {
        logger.info("Updating trip health data for mode " + mode + ", day " + day);

        final int partitionSize = (int) ((double) trips.size() / Runtime.getRuntime().availableProcessors()) + 1;
        Iterable<List<Trip>> partitions = Iterables.partition(trips, partitionSize);
        System.out.println("current memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

        TravelTime travelTime;
        TravelDisutility travelDisutility;
        String networkFile;
        EnumMap<Mode, EnumMap<MitoGender, Map<Integer,Double>>> allSpeeds = ((DataContainerHealth)dataContainer).getAvgSpeeds();
        VehiclesFactory fac = VehicleUtils.getFactory();

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
                networkFile = scenario.getConfig().controller().getOutputDirectory() + "/" + day + "/bikePed/" + latestMatsimYear + ".output_network.xml.gz";
                new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
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
                        person.getAttributes().putAttribute("purpose",trip.getTripPurpose());
                        Person siloPerson = dataContainer.getHouseholdDataManager().getPersonFromId(trip.getPerson());
                        person.getAttributes().putAttribute("sex",siloPerson.getGender().toString());
                        person.getAttributes().putAttribute("age",siloPerson.getAge());

                        // Create vehicle for each person (i.e., trip) for active traveller
                        Vehicle vehicle = null;
                        if(mode.equals(Mode.walk)||mode.equals(Mode.bicycle)) {
                            MitoGender gender = MitoGender.valueOf((String) person.getAttributes().getAttribute("sex"));
                            int age = (int) person.getAttributes().getAttribute("age");
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
                            calculatePathExposures(trip,outboundPath,outboundDepartureTimeInSeconds,travelTime);
                        }

                        // Calculate exposures for activity & return trip (home-based trips only)
                        // TODO: exposure for activity of non-home-based trips, currently we do not know the activity duration of non-home-based trips
                        if(trip.isHomeBased()) {
                            int returnDepartureTimeInSeconds = trip.getDepartureReturnInMinutes()*60;
                            LeastCostPathCalculator.Path returnPath = pathCalculator.calcLeastCostPath(destinationNode, originNode,returnDepartureTimeInSeconds,person,vehicle);
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

        double pathLength = 0;
        double pathTime = 0;
        double pathMarginalMetHours = 0;
        float[] pathExposurePm25 = new float[24*7];
        float[] pathExposureNo2 = new float[24*7];
        float[] pathExposureNoise = new float[24*7];
        double pathExposureGreen = 0.;
        double pathSevereInjuryRisk = 0;
        double pathFatalityRisk = 0;
        float[] hourOccupied = new float[24*7];

        for(Link link : path.links) {
            double enterTimeInSecond = (double) departureTimeInSecond + pathTime;
            double linkLength = link.getLength();
            double linkTime = travelTime.getLinkTravelTime(link,enterTimeInSecond,null,null);

            double linkSevereInjuryRisk = 0.;
            double linkFatalityRisk = 0.;
            double linkMarginalMetHour = 0.;
            double linkExposureGreen = 0.;

            LinkInfo linkInfo = ((DataContainerHealth)dataContainer).getLinkInfo().get(link.getId());

            // INJURY
            //double[] severeFatalRisk = getLinkSevereFatalInjuryRisk(mode, (int) (enterTimeInSecond / 3600.), linkInfo);
            //linkSevereInjuryRisk = severeFatalRisk[0];
            //linkFatalityRisk = severeFatalRisk[1];

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
                    int exactDayHour = (int) currentDayHour;
                    int exactWeekHour = exactDayHour + 24 * dayCode;

                    if(exactWeekHour > 167){
                        break;
                    }

                    int nextDayHour = exactDayHour + 1;
                    double durationInThisHour = Math.min(endDayHour, nextDayHour) - currentDayHour;

                    hourOccupied[exactWeekHour] = (float) durationInThisHour;

                    // AIR POLLUTION
                    double linkConcentrationPm25 = linkInfo.getExposure2Pollutant2TimeBin().getOrDefault(Pollutant.PM2_5,new OpenIntFloatHashMap()).get(exactDayHour) +
                            linkInfo.getExposure2Pollutant2TimeBin().getOrDefault(Pollutant.PM2_5_non_exhaust,new OpenIntFloatHashMap()).get(exactDayHour);
                    double linkConcentrationNo2 = linkInfo.getExposure2Pollutant2TimeBin().getOrDefault(Pollutant.NO2,new OpenIntFloatHashMap()).get(exactDayHour);

                    pathExposurePm25[exactWeekHour] += PollutionExposure.getLinkExposurePm25(mode, linkConcentrationPm25, durationInThisHour * 3600, linkMarginalMet);
                    pathExposureNo2[exactWeekHour] += PollutionExposure.getLinkExposureNo2(mode, linkConcentrationNo2, durationInThisHour * 3600, linkMarginalMet);

                    //TODO: bike/walk-only link has no noise emission (noise produced on that link). currently we assume 0 noise level while travelling on those links.
                    // Later, we can do geo-spatialling and associate these link to nearest car link? or Instead of using noise emission, we consider each link as noise receivers and do proper noise exposure
                    if(!linkInfo.getNoiseLevel2TimeBin().isEmpty()){
                        pathExposureNoise[exactWeekHour] += linkInfo.getNoiseLevel2TimeBin().get(exactDayHour) * durationInThisHour;
                    }

                    currentDayHour = nextDayHour;
                }

            } else{
                logger.warn("No link info found for link id " + link.getId());
            }

            pathLength += linkLength;
            pathTime += linkTime;
            pathSevereInjuryRisk += linkSevereInjuryRisk - (pathSevereInjuryRisk * linkSevereInjuryRisk);
            pathFatalityRisk += linkFatalityRisk - (pathFatalityRisk * linkFatalityRisk);
            pathMarginalMetHours += linkMarginalMetHour;
            pathExposureGreen += linkExposureGreen;
        }

        trip.updateMatsimTravelDistance(pathLength);
        trip.updateMatsimTravelTime(pathTime);
        trip.updateMatsimLinkCount(path.links.size());

        trip.updateMarginalMetHours(pathMarginalMetHours);
        trip.updateTravelRiskMap(Map.of(
                "severeInjury", (float) pathSevereInjuryRisk,
                "fatality", (float) pathFatalityRisk
        ));
        trip.updateTravelExposureMapByHour(Map.of(
                "pm2.5", pathExposurePm25,
                "no2", pathExposureNo2
        ));
        trip.updateTravelNoiseExposure(pathExposureNoise);
        trip.updateTravelNdviExposure(pathExposureGreen);
        ((PersonHealth)dataContainer.getHouseholdDataManager().getPersonFromId(trip.getPerson())).updateWeeklyTravelActivityHourOccupied(hourOccupied);
    }

    private void calculateActivityExposures(Trip trip) {

        float[] exposurePM25 = new float[24*7];
        float[] exposureNo2 = new float[24*7];
        float[] activityNoiseExposure = new float[24*7];
        double activityGreenExposure = 0.;
        float[] hourOccupied = new float[24*7];


        double activityArrivalTime = trip.getDepartureTimeInMinutes() + trip.getMatsimTravelTime()/60.;
        double activityDepartureTime = trip.getDepartureReturnInMinutes();
        double activityDuration = activityDepartureTime - activityArrivalTime;
        if(activityDuration < 0) {
            activityDuration += 1440.;
        }

        int dayCode = trip.getDepartureDay().getDayCode();
        double startDayHour = activityArrivalTime / 60.;
        double endDayHour = activityDepartureTime/ 60.;



        for(double currentDayHour = startDayHour; currentDayHour < endDayHour;) {
            int exactDayHour = (int) currentDayHour;
            int exactWeekHour = exactDayHour + 24 * dayCode;

            if(exactWeekHour > 167){
                break;
            }

            int nextDayHour = exactDayHour + 1;
            double durationInThisHour = Math.min(endDayHour, nextDayHour) - currentDayHour;

            hourOccupied[exactWeekHour] = (float) durationInThisHour;

            //Air pollutant
            String rpId = ("zone" + trip.getTripDestinationZone());
            ReceiverPointInfo receiverPointInfo = ((DataContainerHealth)dataContainer).getReceiverPointInfo().get(rpId);

            if(receiverPointInfo != null) {

                double zonalIncrementalPM25 = receiverPointInfo.getExposure2Pollutant2TimeBin().get(Pollutant.PM2_5).get(exactDayHour)+
                        receiverPointInfo.getExposure2Pollutant2TimeBin().get(Pollutant.PM2_5_non_exhaust).get(exactDayHour);
                double zonalIncrementalNO2 = receiverPointInfo.getExposure2Pollutant2TimeBin().get(Pollutant.NO2).get(exactDayHour);

                exposurePM25[exactWeekHour] = (float) PollutionExposure.getActivityExposurePm25(durationInThisHour * 60, zonalIncrementalPM25);
                exposureNo2[exactWeekHour] = (float) PollutionExposure.getActivityExposureNo2(durationInThisHour * 60, zonalIncrementalNO2);

            }else{
                logger.warn("No receiver point info found for rpId: " + rpId + " tripId: " + trip.getTripId());
            }


            // Noise and green level
            rpId = getReceiverPointId(trip.getTripDestinationType(), trip.getTripDestinationMicroId());
            receiverPointInfo = ((DataContainerHealth)dataContainer).getReceiverPointInfo().get(rpId);

            if(receiverPointInfo != null) {
                if(!receiverPointInfo.getNoiseLevel2TimeBin().isEmpty()){
                    activityNoiseExposure[exactWeekHour] = (float) (receiverPointInfo.getNoiseLevel2TimeBin().get(exactDayHour) * durationInThisHour);
                }

                //TODO: should we eliminate ndvi exposure in night?
                activityGreenExposure += receiverPointInfo.getNdvi() * durationInThisHour;
            }

            currentDayHour = nextDayHour;
        }

        trip.setActivityDuration(activityDuration);
        trip.setActivityExposureMapByHour(Map.of(
                "pm2.5", exposurePM25,
                "no2", exposureNo2
        ));
        trip.setActivityNoiseExposureByHour(activityNoiseExposure);
        trip.setActivityNdviExposure(activityGreenExposure);
        ((PersonHealth)dataContainer.getHouseholdDataManager().getPersonFromId(trip.getPerson())).updateWeeklyTravelActivityHourOccupied(hourOccupied);
    }

    private void calculatePersonHealthExposures() {
        int missingPerson = 0;
        for (Trip mitoTrip : mitoTrips.values()) {
            Person siloPerson = dataContainer.getHouseholdDataManager().getPersonFromId(mitoTrip.getPerson());
            if (siloPerson == null) {
                missingPerson++;
                continue;
            }

            ((PersonHealth) siloPerson).updateWeeklyTravelSeconds((float) mitoTrip.getMatsimTravelTime());
            ((PersonHealth) siloPerson).updateWeeklyAccidentRisks(mitoTrip.getTravelRiskMap());
            ((PersonHealth) siloPerson).updateWeeklyMarginalMetHours(mitoTrip.getTripMode(), (float) mitoTrip.getMarginalMetHours());
            ((PersonHealth) siloPerson).updateWeeklyPollutionExposuresByHour(mitoTrip.getTravelExposureMapByHour());
            ((PersonHealth) siloPerson).updateWeeklyNoiseExposuresByHour(mitoTrip.getTravelNoiseExposureByHour());
            ((PersonHealth) siloPerson).updateWeeklyGreenExposures((float) mitoTrip.getTravelNdviExposure());

            // Activity details (home-based trips only)
            if (mitoTrip.isHomeBased()) {
                ((PersonHealth) siloPerson).updateWeeklyActivityMinutes((float) mitoTrip.getActivityDuration());
                ((PersonHealth) siloPerson).updateWeeklyPollutionExposuresByHour(mitoTrip.getActivityExposureMapByHour());
                ((PersonHealth) siloPerson).updateWeeklyNoiseExposuresByHour(mitoTrip.getActivityNoiseExposureByHour());
                ((PersonHealth) siloPerson).updateWeeklyGreenExposures((float) mitoTrip.getActivityNdviExposure());
            }
        }
        logger.warn("total dismatched person: " + missingPerson);
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

                //Air pollutant
                int zoneId = dataContainer.getRealEstateDataManager().getDwelling(person.getHousehold().getDwellingId()).getZoneId();
                String rpId = ("zone" + zoneId);
                ReceiverPointInfo receiverPointInfo = ((DataContainerHealth)dataContainer).getReceiverPointInfo().get(rpId);

                if(receiverPointInfo != null) {
                    double zonalIncrementalPM25 = receiverPointInfo.getExposure2Pollutant2TimeBin().get(Pollutant.PM2_5).get(dayHour)+
                            receiverPointInfo.getExposure2Pollutant2TimeBin().get(Pollutant.PM2_5_non_exhaust).get(dayHour);
                    double zonalIncrementalNO2 = receiverPointInfo.getExposure2Pollutant2TimeBin().get(Pollutant.NO2).get(dayHour);

                    exposurePM25[weekHour] = (float) PollutionExposure.getHomeExposurePm25(remainingHour * 60, zonalIncrementalPM25);
                    exposureNo2[weekHour] = (float) PollutionExposure.getHomeExposureNo2(remainingHour * 60, zonalIncrementalNO2);
                }else{
                    logger.warn("No receiver point info found for rpId: " + rpId + " personId: " + person.getId());
                }


                // Noise and green level
                rpId = ("dd" + person.getHousehold().getDwellingId());
                receiverPointInfo = ((DataContainerHealth)dataContainer).getReceiverPointInfo().get(rpId);

                if(receiverPointInfo != null) {
                    if(!receiverPointInfo.getNoiseLevel2TimeBin().isEmpty()){
                        exposureNoise[weekHour] = receiverPointInfo.getNoiseLevel2TimeBin().get(dayHour) * remainingHour;
                    }

                    ndviExposure = receiverPointInfo.getNdvi() * remainingHour;

                }else{
                    logger.warn("No receiver point info found for rpId: " + rpId + " personId: " + person.getId());
                }
            }


            ((PersonHealth) person).setWeeklyHomeMinutes((float) minutesAtHome);
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
            float weightedSumExposurePM25 = 0.f;
            float weightedSumExposureNo2 = 0.f;
            float weightedSumExposureNoise = 0.f;
            float weightedSumExposureNoiseNight = 0.f;
            float weightedSumExposureNdvi = 0.f;

            Map<String, float[]> weeklyPollutionExposures = ((PersonHealth) person).getWeeklyPollutionExposures();
            float[] weeklyNoiseExposureByHour = ((PersonHealth) person).getWeeklyNoiseExposureByHour();
            float[] hourOccupied = ((PersonHealth) person).getWeeklyTravelActivityHourOccupied();


            for (int weekHour = 0;  weekHour < hourOccupied.length; weekHour++) {

                sumHour += Math.max(1, hourOccupied[weekHour]);
                weightedSumExposurePM25 += weeklyPollutionExposures.get("pm2.5")[weekHour]/Math.max(1, hourOccupied[weekHour]);
                weightedSumExposureNo2 += weeklyPollutionExposures.get("no2")[weekHour]/Math.max(1, hourOccupied[weekHour]);

                int dayHour = weekHour % 24;
                float hourlyNoiseLevel = (float) NoiseMetrics.getHourlyNoiseLevel(dayHour, weeklyNoiseExposureByHour[weekHour]/Math.max(1, hourOccupied[weekHour]));
                weightedSumExposureNoise += hourlyNoiseLevel;

                if (dayHour <= 7  || dayHour > 23 ){
                    sumNightHour += Math.max(1, hourOccupied[weekHour]);
                    weightedSumExposureNoiseNight += hourlyNoiseLevel;
                }
            }


            // todo: make not hardcoded...
            // 1.49/3 is the "minimum" weekly ventilation rate (8hr sleep + 16hr rest per day)
            ((PersonHealth) person).setWeeklyExposureByPollutantNormalised(
                    Map.of(
                            "pm2.5", (float) (weightedSumExposurePM25 / (sumHour * (1.49/3.))),
                            "no2", (float) (weightedSumExposureNo2 / (sumHour * (1.49/3.)))
                    )
            );

            float Lden = (float) (10 * Math.log10(weightedSumExposureNoise / sumHour));
            ((PersonHealth) person).setWeeklyNoiseExposuresNormalised (Lden);
            ((PersonHealth) person).setWeeklyGreenExposuresNormalised(weightedSumExposureNdvi / sumHour);

            float Lnight = (float) (10 * Math.log10(weightedSumExposureNoiseNight / sumNightHour));
            ((PersonHealthMCR) person).setNoiseHighAnnoyedPercentage((float) NoiseMetrics.getHighAnnoyedPercentage(Lden));
            ((PersonHealthMCR) person).setNoiseHighSleepDisturbancePercentage((float) NoiseMetrics.getHighSleepDisturbancePercentage(Lnight));

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

    private double[] getLinkSevereFatalInjuryRisk(Mode mode, int hour, LinkInfo linkInfo) {
        double FATAL_CAR_DRIVER = 0.077;
        double FATAL_BIKECAR_BIKE = 0.024;
        double FATAL_BIKEBIKE_BIKE = 0.051;
        double FATAL_PED_PED = 0.073;

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

}
