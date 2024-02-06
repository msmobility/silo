import ch.sbb.matsim.routing.pt.raptor.*;
import com.google.common.math.LongMath;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.utils.SiloUtil;
import isuh.IsuhWorker;
import isuh.NetworkUtils2;
import isuh.TravelAttribute;
import isuh.calculate.NetworkIndicatorCalculator;
import isuh.calculate.PtIndicatorCalculator;
import isuh.routing.disutility.JibeDisutility;
import isuh.routing.disutility.components.JctStress;
import isuh.routing.disutility.components.LinkAttractiveness;
import isuh.routing.disutility.components.LinkStress;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.bicycle.BicycleConfigGroup;
import isuh.routing.travelTime.BicycleTravelTime;
import isuh.routing.travelTime.WalkTravelTime;
import isuh.routing.travelTime.speed.BicycleLinkSpeedCalculatorDefaultImpl;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.FastDijkstraFactory;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.router.TeleportationRoutingModule;
import org.matsim.core.router.costcalculators.FreespeedTravelTimeAndDisutility;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.misc.Counter;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.log4j.Logger;
import org.matsim.facilities.ActivityFacilitiesFactoryImpl;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;

public class IsuhCalculator {

    private final static Logger logger = Logger.getLogger(IsuhCalculator.class);

    private final Network network;

    private final Map<String, Network> networkByMode = new HashMap<>();

    private final static double MAX_BIKE_SPEED = 16 / 3.6;

    private final static String SEP = ",";
    private final static String TRANSIT_SCHEDULE = "C:\\Users\\Corin Staves\\Documents\\manchester\\pt\\scheduleMapped.xml";
    private final static String TRANSIT_NETWORK = "C:\\Users\\Corin Staves\\Documents\\manchester\\pt\\ptNetwork.xml";

    private final int numberOfThreads;
    private final DataContainer dataContainer;

    private final Set<IsuhWorker> persons = new HashSet<>();

    private final Map<String, List<String>> allAttributeNames = new LinkedHashMap<>();

    public IsuhCalculator(Network network, DataContainer dataContainer, int numberOfThreads) {
        this.network = network;
        this.numberOfThreads = numberOfThreads;
        this.dataContainer = dataContainer;
    }

    public void extractWorkers(double populationScalingFactor) {
        int negJobIDs = 0;
        for (Household household : dataContainer.getHouseholdDataManager().getHouseholds()) {
            if (SiloUtil.getRandomNumberAsDouble() > populationScalingFactor) {
                continue;
            }
            for (Person person : household.getPersons().values()) {
                if(person.getOccupation().equals(Occupation.EMPLOYED)) {
                    if(person.getJobId() <= 0) {
                        negJobIDs++;
                    } else {
                        Coordinate dwellingCoordinate = dataContainer.getRealEstateDataManager().getDwelling(household.getDwellingId()).getCoordinate();
                        Coordinate jobCoordinate = dataContainer.getJobDataManager().getJobFromId(person.getJobId()).getCoordinate();
                        Coord dwellingCoord = CoordUtils.createCoord(dwellingCoordinate);
                        Coord jobCoord = CoordUtils.createCoord(jobCoordinate);
                        persons.add(new IsuhWorker(person, dwellingCoord, jobCoord));
                    }
                }
            }
        }
        logger.info(negJobIDs + " workers with negative job IDs");
    }

    public void writeWorkers() {

        PrintWriter out = openFileForSequentialWriting(new File("C:\\Users\\Corin Staves\\Documents\\manchester\\synthpop.csv"));

        out.println("IDNumber,PersonNumber,TripNumber,HomeEasting,HomeNorthing,EndEasting,EndNorthing,agegroup,sex,licence,carsno");

        int counter = 0;
        for(IsuhWorker person : persons) {
            counter++;
            if(LongMath.isPowerOfTwo(counter)) {
                logger.info(counter + " records written");
            }
            out.print(person.getPerson().getHousehold().getId());
            out.print(",");
            out.print(person.getPerson().getId());
            out.print(",1,");
            out.print(person.getHomeCoord().getX());
            out.print(",");
            out.print(person.getHomeCoord().getY());
            out.print(",");
            out.print(person.getWorkCoord().getX());
            out.print(",");
            out.print(person.getWorkCoord().getY());
            out.print(",");
            out.print(getAgeGroup(person.getPerson().getAge()));
            out.print(",");
            out.print(person.getPerson().getGender().equals(Gender.FEMALE) ? 1 : 0);
            out.print(",");
            out.print(person.getPerson().hasDriverLicense() ? 1 : 0);
            out.print(",");
            out.println(Math.min(3,person.getPerson().getHousehold().getVehicles().stream().filter(vv -> vv.getType().equals(de.tum.bgu.msm.data.vehicle.VehicleType.CAR)).count()));
        }

        out.close();

        logger.info("Wrote " + counter + " records.");
    }

    private int getAgeGroup(int age) {
        if (age <= 14) {
            return 1;
        } else if (age <= 24) {
            return 2;
        } else if (age <= 34) {
            return 3;
        } else if (age <= 44) {
            return 4;
        } else if (age <= 54) {
            return 5;
        } else if (age <= 64) {
            return 6;
        } else {
            return 7;
        }
    }

    private static String createHeader(Map<String,List<String>> attributes) {
        StringBuilder builder = new StringBuilder();

        // Trip identifiers
        builder.append("IDNumber,PersonNumber,TripNumber,HomeEasting,HomeNorthing,EndEasting,EndNorthing,agegroup,sex,licence,cars");


        // Route attributes
        for (Map.Entry<String, List<String>> e1 : attributes.entrySet()) {
            String route = e1.getKey();
            for (String attributeName : e1.getValue()) {
                builder.append(SEP).append(route);
                if(!attributeName.equals("")) {
                    builder.append("_").append(attributeName);
                }
            }
        }
        return builder.toString();
    }

    private static PrintWriter openFileForSequentialWriting(File outputFile) {
        if (outputFile.getParent() != null) {
            File parent = outputFile.getParentFile();
            parent.mkdirs();
        }

        try {
            FileWriter fw = new FileWriter(outputFile, false);
            BufferedWriter bw = new BufferedWriter(fw);
            return new PrintWriter(bw);
        } catch (IOException var5) {
            logger.info("Could not open file <" + outputFile.getName() + ">.");
            return null;
        }
    }



    public void calculateIndicators() {

        // Set up scenario and config
        logger.info("Preparing Matsim config and scenario...");
        Config config = ConfigUtils.createConfig();
        BicycleConfigGroup bicycleConfigGroup = new BicycleConfigGroup();
        bicycleConfigGroup.setBicycleMode("bike");
        config.addModule(bicycleConfigGroup);

        // CREATE BICYCLE VEHICLE
        VehicleType type = VehicleUtils.createVehicleType(Id.create("bicycle", VehicleType.class));
        type.setMaximumVelocity(MAX_BIKE_SPEED);
        Vehicle bike = VehicleUtils.createVehicle(Id.createVehicleId(1), type);

        // Create mode-specific networks
        logger.info("Creating mode-specific networks...");
        Network networkCar = NetworkUtils2.extractModeSpecificNetwork(network, TransportMode.car);
        Network carXy2l = NetworkUtils2.extractXy2LinksNetwork(networkCar, l -> !((boolean) l.getAttributes().getAttribute("motorway")));
        Network networkBike = NetworkUtils2.extractModeSpecificNetwork(network, TransportMode.bike);
        Network networkWalk = NetworkUtils2.extractModeSpecificNetwork(network, TransportMode.walk);

        // Travel time
        FreespeedTravelTimeAndDisutility freeSpeed = new FreespeedTravelTimeAndDisutility(config.planCalcScore());
        BicycleLinkSpeedCalculatorDefaultImpl linkSpeedCalculator = new BicycleLinkSpeedCalculatorDefaultImpl((BicycleConfigGroup) config.getModules().get(BicycleConfigGroup.GROUP_NAME));
        TravelTime ttBike = new BicycleTravelTime(linkSpeedCalculator);
        TravelTime ttWalk = new WalkTravelTime();

        // calculate
        network("car", null, networkCar, carXy2l, freeSpeed, freeSpeed, null);
        network("bike", bike, networkBike, null, new JibeDisutility(TransportMode.bike,ttBike), ttBike, activeAttributes(TransportMode.bike));
        network("walk", null, networkWalk, null, new JibeDisutility(TransportMode.walk,ttWalk), ttWalk, activeAttributes(TransportMode.walk));

        pt("pt",config,TRANSIT_SCHEDULE,TRANSIT_NETWORK);
    }

    private static LinkedHashMap<String,TravelAttribute> activeAttributes(String mode) {
        LinkedHashMap<String,TravelAttribute> attributes = new LinkedHashMap<>();
        attributes.put("attractiveness", (l,td) -> LinkAttractiveness.getDayAttractiveness(l) * l.getLength());
        attributes.put("stressLink",(l,td) -> LinkStress.getStress(l,mode) * l.getLength());
        attributes.put("stressJct",(l,td) -> JctStress.getJunctionStress(l,mode));
        return attributes;
    }

    private void network(String mode, Vehicle vehicle, Network network, Network xy2lNetwork,
                         TravelDisutility travelDisutility, TravelTime travelTime,
                         LinkedHashMap<String, TravelAttribute> additionalAttributes) {

        logger.info("Calculating network indicators for mode " + mode);

        // Specify attribute names
        List<String> attributeNames = new ArrayList<>(List.of("time","dist"));
        if(additionalAttributes != null) {
            attributeNames.addAll(additionalAttributes.keySet());
        }
        allAttributeNames.put(mode, attributeNames);

        // Do calculation
        ConcurrentLinkedQueue<IsuhWorker> personsQueue = new ConcurrentLinkedQueue<>(persons);
        Counter counter = new Counter("worker "," of " + persons.size());
        Thread[] threads = new Thread[numberOfThreads];

        for(int i = 0 ; i < numberOfThreads ; i++) {
            LeastCostPathCalculator dijkstra = new FastDijkstraFactory(false)
                    .createPathCalculator(network, travelDisutility, travelTime);
            NetworkIndicatorCalculator worker = new NetworkIndicatorCalculator(personsQueue, counter, mode,vehicle,
                    network, xy2lNetwork, dijkstra, travelDisutility, additionalAttributes);
            threads[i] = new Thread(worker, "NetworkCalculator-" + mode + "-" + i);
            threads[i].start();
        }

        // wait until all threads have finished
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void pt(String mode, Config config, String scheduleFilePath, String networkFilePath) {

        config.transit().setUseTransit(true);
        Scenario scenario = ScenarioUtils.createScenario(config);

        // Specify attribute names
        List<String> attributeNames = List.of("totalTravelTime");
        allAttributeNames.put(mode, attributeNames);

        logger.info("loading schedule from " + scheduleFilePath);
        new TransitScheduleReader(scenario).readFile(scheduleFilePath);
        new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFilePath);

        logger.info("preparing PT route calculation");
        RaptorStaticConfig raptorConfig = RaptorUtils.createStaticConfig(config);
        raptorConfig.setOptimization(RaptorStaticConfig.RaptorOptimization.OneToOneRouting);
        SwissRailRaptorData raptorData = SwissRailRaptorData.create(scenario.getTransitSchedule(), scenario.getTransitVehicles(), raptorConfig, scenario.getNetwork(), null);

        ConcurrentLinkedQueue<IsuhWorker> personsQueue = new ConcurrentLinkedQueue<>(persons);
        Counter counter = new Counter("Routing PT person ", " / " + persons.size());
        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            SwissRailRaptor.Builder builder = new SwissRailRaptor.Builder(raptorData, config);
            Map<String, RoutingModule> accessRoutingModules = new HashMap<>();
            accessRoutingModules.put("walk",new TeleportationRoutingModule("walk",scenario,5.3 / 3.6,1.));
            builder.with(new DefaultRaptorStopFinder(new DefaultRaptorIntermodalAccessEgress(),accessRoutingModules));
            SwissRailRaptor raptor = builder.build();
            ActivityFacilitiesFactoryImpl activityFacilitiesFactory = new ActivityFacilitiesFactoryImpl();

            PtIndicatorCalculator worker = new PtIndicatorCalculator(personsQueue, counter, mode, scenario, raptor, activityFacilitiesFactory);
            threads[i] = new Thread(worker, "PublicTransportCalculator-" + i);
            threads[i].start();
        }

        // wait until all threads have finished
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void estimateModes() {

    }







}
