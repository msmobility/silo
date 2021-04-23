package de.tum.bgu.msm.scenarios.excessCommuteMatching;

import blogspot.software_and_algorithms.stern_library.optimization.HungarianAlgorithm;
import cern.colt.map.tint.OpenIntIntHashMap;
import cern.colt.map.tobject.OpenIntObjectHashMap;
import ch.sbb.matsim.routing.pt.raptor.*;
import com.google.common.collect.Iterables;
import com.google.common.math.LongMath;
import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.matsim.MatsimData;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.*;
import org.matsim.core.router.costcalculators.FreespeedTravelTimeAndDisutility;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.ActivityFacilitiesFactoryImpl;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.Facility;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OneStepMatches_carPt {

    private final static Logger logger = Logger.getLogger(OneStepMatches_carPt.class);
    //Parameters and file paths setting
    private final static double SCALE_FACTOR = 1;
    private final static String scenarioName = "carPt_min_"; //part of the output folder name. e.g. car_min, carPt_min, car_max, carPt_max, twoStep...
    private final static int startRandomSeed = 0;
    private final static int endRandomSeed = 1;

    private final static String siloPropertiesPath = "F:\\models\\muc/siloMuc.properties";
    private final static String outputBaseDirectory = "F:\\models\\msm-papers\\data\\thePerfectMatch_"+scenarioName;
    private final static String carPtNetwork = "F:\\models\\muc\\input\\mito\\trafficAssignment\\pt_2020\\network_pt_road.xml.gz";
    private final static String ptSchedule ="F:\\models\\muc\\input\\mito\\trafficAssignment\\pt_2020\\schedule.xml";

    public static void main(String[] args) {
        Properties properties = SiloUtil.siloInitialization(siloPropertiesPath);

        for (int i = startRandomSeed; i < endRandomSeed; i++) {

            double t0 = System.currentTimeMillis();
            String output = outputBaseDirectory + i;
            new File(output).mkdirs();

            logger.info("Read silo micro data...");
            DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
            DataBuilder.read(properties, dataContainer);

            logger.info("Number of people before sampling: " + dataContainer.getHouseholdDataManager().getPersons().size());
            logger.info("Number of workers before sampling: " + dataContainer.getHouseholdDataManager().getPersons().stream().filter(pp -> pp.getOccupation() == Occupation.EMPLOYED).count());
            logger.info("Number of jobs before sampling: " + dataContainer.getJobDataManager().getJobs().stream().filter(jj -> jj.getWorkerId() > 0).count());
            logger.info("Number of vacant jobs before sampling: " + dataContainer.getJobDataManager().getJobs().stream().filter(jj -> jj.getWorkerId() == -1).count());

            logger.info("Sampling silo population with scale factor " + SCALE_FACTOR + "...");
            scale(SCALE_FACTOR, dataContainer, i);

            logger.info("Number of people after sampling: " + dataContainer.getHouseholdDataManager().getPersons().size());
            logger.info("Number of workers after sampling: " + dataContainer.getHouseholdDataManager().getPersons().stream().filter(pp -> pp.getOccupation() == Occupation.EMPLOYED).count());
            logger.info("Number of jobs after sampling: " + dataContainer.getJobDataManager().getJobs().stream().filter(jj -> jj.getWorkerId() > 0).count());
            logger.info("Number of vacant jobs after sampling: " + dataContainer.getJobDataManager().getJobs().stream().filter(jj -> jj.getWorkerId() == -1).count());

            logger.info("Preparing matsim config...");
            Config config = ConfigUtils.createConfig();
            config.transit().setUseTransit(true);

            logger.info("Reading network...");
            Network network = NetworkUtils.createNetwork();
            new MatsimNetworkReader(network).readFile(carPtNetwork);

            Scenario scenario = ScenarioUtils.createScenario(config);
            logger.info("Reading schedule...");
            new TransitScheduleReader(scenario).readFile(ptSchedule);
            TransitSchedule schedule = scenario.getTransitSchedule();

            logger.info("Preparing MATSim data...");
            MatsimData matsimData = new MatsimData(config, 8, network, schedule, null);
            FreespeedTravelTimeAndDisutility freespeed = new FreespeedTravelTimeAndDisutility(config.planCalcScore());
            matsimData.update(freespeed, freespeed);

            logger.info("Write out travel time of origin matches...");
            String outputOrigin = output + "/originMatches_" + SCALE_FACTOR + "_oneStep.csv";
            calculateOriginTravelTime(outputOrigin, matsimData, dataContainer);


            final Map<String, List<Job>> jobsBySector = dataContainer.getJobDataManager().getJobs().stream().collect(Collectors.groupingBy(Job::getType));
            final List<Person> workersList = dataContainer.getHouseholdDataManager().getPersons().stream().filter(pp -> pp.getOccupation() == Occupation.EMPLOYED).collect(Collectors.toList());

            logger.info("Start matching algorithm by sector...");
            for (String sector : jobsBySector.keySet()) {
                final List<Job> jobs = jobsBySector.get(sector);
                final List<Person> workers = workersList.stream().filter(pp -> sector.equals(dataContainer.getJobDataManager().getJobFromId(pp.getJobId()).getType())).collect(Collectors.toList());
                logger.info("Start matches for sector " + sector + " with " + jobs.size() + " jobs and " + workers.size() + " workers");
                final Map<Integer, Tuple<Integer, Double>> jobByPersonCurrent = matchSector(sector, workers, jobs, dataContainer, matsimData);
                logger.info("Writing matches for sector " + sector);
                writeMatches(output, sector, jobByPersonCurrent);
            }

            logger.info("Scale factor: " + SCALE_FACTOR + "|Run time: " + (System.currentTimeMillis() - t0));
        }
    }


    public static void scale(double scaleFactor, DataContainerWithSchools dataContainer, long seed) {
        Random random = new Random(seed);
        //scale households
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        Collection<Household> households = householdDataManager.getHouseholds();

        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        Collection<Job> jobs = jobDataManager.getJobs();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
        Collection<Dwelling> dwellings = realEstateDataManager.getDwellings();

        Map<String,List<Job>> occupiedJobBySectorBeforeSampling = jobs.stream().filter(jj -> jj.getWorkerId() > 0).collect(Collectors.groupingBy(Job::getType));

        for (Household hh : households) {
            if (random.nextDouble() > scaleFactor) {
                int dwellingId = hh.getDwellingId();
                for (Person person : hh.getPersons().values()) {
                    person.setHousehold(null);
                    householdDataManager.removePersonFromHousehold(person);
                    householdDataManager.removePerson(person.getId());
                    Job job = jobDataManager.getJobFromId(person.getJobId());
                    if (job != null){
                        jobDataManager.removeJob(job.getId());
                    }
                }
                householdDataManager.removeHousehold(hh.getId());
                realEstateDataManager.removeDwellingFromVacancyList(dwellingId);
                realEstateDataManager.removeDwelling(dwellingId);

            }
        }

        logger.warn("The population was scaled to " + householdDataManager.getHouseholds().size() + " households with " + householdDataManager.getPersons().size() + " persons");

        /*Map<String,List<Job>> occupiedJobBySector = jobs.stream().filter(jj -> jj.getWorkerId() > 0).collect(Collectors.groupingBy(Job::getType));

        Map<String, Double> shareBySector = new HashMap<>();

        for (String sector : occupiedJobBySector.keySet()){
            double share = occupiedJobBySector.get(sector).size()/(double)occupiedJobBySectorBeforeSampling.get(sector).size();
            shareBySector.put(sector,share);
            logger.info("Sector: " + sector + " | Occupied job share: " + share);
        }


        Map<String,List<Job>> vacantJobBySectorBeforeSampling = jobs.stream().filter(jj -> jj.getWorkerId() == -1).collect(Collectors.groupingBy(Job::getType));
*/
        for (Job jj : jobs) {
            if (jj.getWorkerId() == -1) {
                if (random.nextDouble() > scaleFactor) {
                    jobDataManager.removeJob(jj.getId());
                }
            }
        }

        /*Map<String,List<Job>> vacantJobBySector = jobs.stream().filter(jj -> jj.getWorkerId() == -1).collect(Collectors.groupingBy(Job::getType));


        for (String sector : vacantJobBySector.keySet()){
            logger.info("Sector: " + sector + " | Vacant job share: " + vacantJobBySector.get(sector).size()/(double)vacantJobBySectorBeforeSampling.get(sector).size());
        }*/

        logger.warn("The population was scaled to " + realEstateDataManager.getDwellings().size() + " dwellings and " + jobDataManager.getJobs().size() + " jobs");

    }

    public static void calculateOriginTravelTime(String output, MatsimData matsimData, DataContainerWithSchools dataContainer) {
        Network carNetwork = matsimData.getCarNetwork();

        List<Person> workers = dataContainer.getHouseholdDataManager().getPersons().stream().filter(pp -> pp.getOccupation() == Occupation.EMPLOYED).collect(Collectors.toList());
        final int partitionSize = (int) ((double) workers.size() / Runtime.getRuntime().availableProcessors()) + 1;
        Iterable<List<Person>> partitions = Iterables.partition(workers, partitionSize);
        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads);
        logger.info("Total " + partitionSize + " partition with " + workers.size() + "workers");


        StringBuilder header = new StringBuilder();
        header.append("personId,workerId,sector,ttOrigin_car,ttOrigin_PT,ttOrigin_walk,ttOrigin");
        header.append('\n');
        try {
            writeToFile(output,header.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //prepare for pt travel time calculation
        SwissRailRaptorData raptorData = matsimData.getRaptorData(RaptorStaticConfig.RaptorOptimization.OneToAllRouting);
        RaptorParameters parameters = matsimData.getRaptorParameters();
        //double walkSpeed = parameters.getBeelineWalkSpeed();
        double walkSpeed = 5/3.6;
        logger.info("walk speed: " + walkSpeed);
        ActivityFacilitiesFactoryImpl activityFacilitiesFactory = new ActivityFacilitiesFactoryImpl();

        logger.info("Start computing travel time for origin matches...");
        AtomicInteger id = new AtomicInteger(0);
        for (List<Person> partition : partitions) {
            SwissRailRaptor raptor = matsimData.createSwissRailRaptor(RaptorStaticConfig.RaptorOptimization.OneToAllRouting);

            FreespeedTravelTimeAndDisutility freeSpeed = new FreespeedTravelTimeAndDisutility(ConfigUtils.createConfig().planCalcScore());
            LeastCostPathCalculator dijkstra = new FastDijkstraFactory(false)
                    .createPathCalculator(carNetwork, freeSpeed, freeSpeed);

            executor.addTaskToQueue(() -> {
                try {
                    int partitionId = id.incrementAndGet();
                    int counter = 0;
                    StringBuilder line = new StringBuilder();
                    for (Person worker : partition) {
                        if(LongMath.isPowerOfTwo(counter)) {
                            logger.info("Computed " + counter + " workers in partition " + partitionId);
                        }

                        int personId = worker.getId();
                        int dwellingId = dataContainer.getHouseholdDataManager().getPersonFromId(personId).getHousehold().getDwellingId();
                        final Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(dwellingId);
                        final Job job = dataContainer.getJobDataManager().getJobFromId(worker.getJobId());

                        Coord fromCoord = CoordUtils.createCoord(dwelling.getCoordinate());
                        Coord toCoord = CoordUtils.createCoord(job.getCoordinate());

                        //Compute car travel time
                        Node originNode = NetworkUtils.getNearestNode(carNetwork, fromCoord);
                        Node destinationNode = NetworkUtils.getNearestNode(carNetwork, toCoord);
                        LeastCostPathCalculator.Path path = dijkstra.calcLeastCostPath(originNode,destinationNode,28800,null,null);
                        double carTravelTime = path.travelTime;

                        //compute direct walk time
                        double directDistance = CoordUtils.calcEuclideanDistance(fromCoord, toCoord);
                        double directWalkTime = directDistance / walkSpeed;

                        //Compute pt travel time
                        Facility fromFacility = activityFacilitiesFactory.createActivityFacility(Id.create(1, ActivityFacility.class), fromCoord);
                        final Map<Id<TransitStopFacility>, SwissRailRaptorCore.TravelInfo> idTravelInfoMap
                                = raptor.calcTree(fromFacility, 28800, null);

                        //compute closest egress stops per job - for pt travel time router
                        Collection<TransitStopFacility> stops = raptorData.findNearbyStops(toCoord.getX(), toCoord.getY(), parameters.getSearchRadius());
                        if (stops.isEmpty()) {
                            TransitStopFacility nearest = raptorData.findNearestStop(toCoord.getX(), toCoord.getY());
                            double nearestStopDistance = CoordUtils.calcEuclideanDistance(toCoord, nearest.getCoord());
                            stops = raptorData.findNearbyStops(toCoord.getX(), toCoord.getY(), nearestStopDistance + parameters.getExtensionRadius());
                        }

                        double ptTravelTime = Double.MAX_VALUE;
                        for (TransitStopFacility stop : stops) {
                            final SwissRailRaptorCore.TravelInfo travelInfo = idTravelInfoMap.get(stop.getId());
                            if (travelInfo != null) {
                                //compute egress to actual zone connector for this stop
                                double distance = CoordUtils.calcEuclideanDistance(stop.getCoord(), toCoord);
                                double egressTime = distance / walkSpeed;
                                //total travel time includes access, egress and waiting times
                                double time = travelInfo.ptTravelTime + travelInfo.waitingTime + travelInfo.accessTime + egressTime;
                                //take the most optimistic time up until now
                                ptTravelTime = Math.min(ptTravelTime, time);
                            }
                        }

                        //check the fastest among direct walk time, car travel time and pt travel time
                        double travelTime = Math.min(carTravelTime, Math.min(ptTravelTime, directWalkTime));
                        travelTime /= 60.;


                        line.append(personId);
                        line.append(',');
                        line.append(job.getId());
                        line.append(',');
                        line.append(job.getType());
                        line.append(',');
                        line.append(carTravelTime/60.);
                        line.append(',');
                        line.append(ptTravelTime/60.);
                        line.append(',');
                        line.append(directWalkTime/60.);
                        line.append(',');
                        line.append(travelTime);
                        line.append('\n');
                        counter++;
                    }

                    try {
                        writeToFile(output,line.toString());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            });
        }
        executor.execute();


    }

    private static Map<Integer, Tuple<Integer, Double>> matchSector(String sector, List<Person> workers, List<Job> jobs, DataContainerWithSchools dataContainer, MatsimData matsimData) {
        OpenIntIntHashMap worker2Index = new OpenIntIntHashMap();
        OpenIntIntHashMap jobs2Index = new OpenIntIntHashMap();

        OpenIntIntHashMap index2Worker = new OpenIntIntHashMap();
        OpenIntIntHashMap index2Job = new OpenIntIntHashMap();

        OpenIntObjectHashMap jobs2Node = new OpenIntObjectHashMap();
        Set<InitialNode> toNodes = new HashSet<>();
        Map<Job, Collection<TransitStopFacility>> stopsPerJob = new LinkedHashMap<>();

        //prepare for pt travel time calculation
        SwissRailRaptorData raptorData = matsimData.getRaptorData(RaptorStaticConfig.RaptorOptimization.OneToAllRouting);
        RaptorParameters parameters = matsimData.getRaptorParameters();
        //double walkSpeed = parameters.getBeelineWalkSpeed();
        double walkSpeed = 5/3.6;
        Network carNetwork = matsimData.getCarNetwork();
        ActivityFacilitiesFactoryImpl activityFacilitiesFactory = new ActivityFacilitiesFactoryImpl();

        logger.info("Compute closest road network node per job...");
        logger.info("Compute closest egress stops per job...");
        int j = 0;
        for (Job job: jobs) {
            //compute closest road network node per job - for car travel time router
            Node node = NetworkUtils.getNearestNode(carNetwork, CoordUtils.createCoord(job.getCoordinate()));
            jobs2Node.put(job.getId(), node);
            toNodes.add(new InitialNode(node, 0., 0.));

            //compute closest egress stops per job - for pt travel time router
            final Coord coord = CoordUtils.createCoord(job.getCoordinate());
            Collection<TransitStopFacility> stops = raptorData.findNearbyStops(coord.getX(), coord.getY(), parameters.getSearchRadius());
            if (stops.isEmpty()) {
                TransitStopFacility nearest = raptorData.findNearestStop(coord.getX(), coord.getY());
                double nearestStopDistance = CoordUtils.calcEuclideanDistance(coord, nearest.getCoord());
                stops = raptorData.findNearbyStops(coord.getX(), coord.getY(), nearestStopDistance + parameters.getExtensionRadius());
            }
            stopsPerJob.put(job, stops);

            //mapping job to internal index
            jobs2Index.put(job.getId(), j);
            index2Job.put(j, job.getId());
            j++;
        }
        ImaginaryNode aggregatedToNodes = MultiNodeDijkstra.createImaginaryNode(toNodes);

        int i = 0;
        for (Person worker : workers){
            worker2Index.put(worker.getId(), i);
            index2Worker.put(i, worker.getId());
            i++;
        }

        double[][] costMatrix = new double[workers.size()][jobs.size()];


        final int partitionSize = (int) ((double) workers.size() / (Properties.get().main.numberOfThreads)) + 1;
        Iterable<List<Person>> partitions = Iterables.partition(workers, partitionSize);
        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads);

        AtomicInteger id = new AtomicInteger(0);
        AtomicInteger counterCar = new AtomicInteger(0);
        AtomicInteger counterPt = new AtomicInteger(0);

        logger.info("Start computing Car travel time, PT travel time and direct walk time...");
        for (List<Person> partition : partitions) {

            SwissRailRaptor raptor = matsimData.createSwissRailRaptor(RaptorStaticConfig.RaptorOptimization.OneToAllRouting);
            MultiNodePathCalculator calculator = matsimData.createFreeSpeedMultiNodePathCalculator();

            executor.addTaskToQueue(() -> {
                try {
                    int partitionId = id.incrementAndGet();
                    int counter = 0;

                    for (Person worker : partition) {
                        if(LongMath.isPowerOfTwo(counter)) {
                            logger.info("Computed " + counter + " workers in partition " + partitionId);
                        }

                        int personId = worker.getId();
                        int dwellingId = dataContainer.getHouseholdDataManager().getPersonFromId(personId).getHousehold().getDwellingId();
                        final Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(dwellingId);
                        Coord fromCoord = CoordUtils.createCoord(dwelling.getCoordinate());


                        Node originNode = NetworkUtils.getNearestNode(carNetwork, fromCoord);
                        calculator.calcLeastCostPath(originNode, aggregatedToNodes, Properties.get().transportModel.peakHour_s, null, null);

                        Facility fromFacility = activityFacilitiesFactory.createActivityFacility(Id.create(1, ActivityFacility.class), fromCoord);
                        final Map<Id<TransitStopFacility>, SwissRailRaptorCore.TravelInfo> idTravelInfoMap
                                = raptor.calcTree(fromFacility, 28800, null);

                        for(Job job: jobs) {
                            //compute car travel time
                            Node destinationNode = (Node) jobs2Node.get(job.getId());
                            double carTravelTime = calculator.constructPath(originNode, destinationNode, Properties.get().transportModel.peakHour_s).travelTime;

                            //compute direct walk time
                            final Coord toCoord = CoordUtils.createCoord(job.getCoordinate());
                            double directDistance = CoordUtils.calcEuclideanDistance(fromCoord, toCoord);
                            double directWalkTime = directDistance / walkSpeed;

                            //compute pt travel time
                            double ptTravelTime = Double.MAX_VALUE;
                            for (TransitStopFacility stop : stopsPerJob.get(job)) {
                                final SwissRailRaptorCore.TravelInfo travelInfo = idTravelInfoMap.get(stop.getId());
                                if (travelInfo != null) {
                                    //compute egress to actual zone connector for this stop
                                    double distance = CoordUtils.calcEuclideanDistance(stop.getCoord(), toCoord);
                                    double egressTime = distance / walkSpeed;
                                    //total travel time includes access, egress and waiting times
                                    double time = travelInfo.ptTravelTime + travelInfo.waitingTime + travelInfo.accessTime + egressTime;
                                    //take the most optimistic time up until now
                                    ptTravelTime = Math.min(ptTravelTime, time);
                                }
                            }

                            //check the fastest among direct walk time, car travel time and pt travel time
                            if(carTravelTime<ptTravelTime){
                                counterCar.incrementAndGet();
                            }else {
                                counterPt.incrementAndGet();
                            }

                            double travelTime = Math.min(carTravelTime, Math.min(ptTravelTime, directWalkTime));
                            travelTime /= 60.;
                            costMatrix[worker2Index.get(personId)][jobs2Index.get(job.getId())] =  (int) travelTime;
                        }

                        counter++;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            });
        }
        executor.execute();

        logger.info("Car travel time < Pt travel time: " + counterCar.get());
        logger.info("Car travel time > Pt travel time: " + counterPt.get());

        logger.info("Start Hungarian Algorithm for sector " + sector);
        final HungarianAlgorithm withinSectorMatching = new HungarianAlgorithm(costMatrix);
        final int[] results = withinSectorMatching.execute();
        logger.info("finish Hungarian Algorithm for sector " + sector);
        Map<Integer, Tuple<Integer,Double>> jobByPerson = new LinkedHashMap<>();

        for (int k = 0; k < results.length; k++) {
            int personId = index2Worker.get(k);
            int jobId = index2Job.get(results[k]);
            jobByPerson.put(personId, new Tuple<>(jobId, costMatrix[k][results[k]]));
        }
        return jobByPerson;
    }

    private static synchronized void writeMatches(String baseDirectoy, String sector, Map<Integer, Tuple<Integer, Double>> jobByPerson) {
        File file = new File(baseDirectoy + "/finalMatches" + sector + "_" + SCALE_FACTOR + "_oneStep.csv");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("personId,jobId,tt");
            writer.newLine();
            for(Map.Entry<Integer, Tuple<Integer,Double>> entry : jobByPerson.entrySet()) {
                writer.write(entry.getKey()+","+entry.getValue().getFirst()+","+entry.getValue().getSecond());
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static synchronized void writeToFile(String path, String building) throws FileNotFoundException {
        PrintWriter bd = new PrintWriter(new FileOutputStream(new File(path), true));
        bd.write(building);
        bd.close();
    }
}
