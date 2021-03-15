package de.tum.bgu.msm.scenarios.excessCommuteMatching;

import blogspot.software_and_algorithms.stern_library.optimization.HungarianAlgorithm;
import cern.colt.map.tint.OpenIntIntHashMap;
import com.google.common.collect.Iterables;
import com.google.common.math.LongMath;
import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.contrib.dvrp.router.DistanceAsTravelDisutility;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.*;
import org.matsim.core.router.costcalculators.FreespeedTravelTimeAndDisutility;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.geometry.CoordUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OneStepMatches {

    private final static Logger logger = Logger.getLogger(OneStepMatches.class);
    private final static double SCALE_FACTER = 0.05;

    public static void main(String[] args) {

        double t0 = System.currentTimeMillis();
        String path = "F:\\models\\muc/siloMuc.properties";
        String matchBaseDirectory = "F:\\models\\msm-papers\\data\\thePerfectMatch";

        Properties properties = SiloUtil.siloInitialization(path);
        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
        DataBuilder.read(properties, dataContainer);

        logger.info("Number of people before sampling: " + dataContainer.getHouseholdDataManager().getPersons().size());
        logger.info("Number of workers before sampling: " + dataContainer.getHouseholdDataManager().getPersons().stream().filter(pp -> pp.getOccupation()== Occupation.EMPLOYED).count());
        logger.info("Number of jobs before sampling: " + dataContainer.getJobDataManager().getJobs().stream().filter(jj -> jj.getWorkerId() > 0).count());
        logger.info("Number of vacant jobs before sampling: " + dataContainer.getJobDataManager().getJobs().stream().filter(jj -> jj.getWorkerId() == -1).count());

        //sampling
        scale(SCALE_FACTER,dataContainer);

        logger.info("Number of people after sampling: " + dataContainer.getHouseholdDataManager().getPersons().size());
        logger.info("Number of workers after sampling: " + dataContainer.getHouseholdDataManager().getPersons().stream().filter(pp -> pp.getOccupation()== Occupation.EMPLOYED).count());
        logger.info("Number of jobs after sampling: " + dataContainer.getJobDataManager().getJobs().stream().filter(jj -> jj.getWorkerId() > 0).count());
        logger.info("Number of vacant jobs after sampling: " + dataContainer.getJobDataManager().getJobs().stream().filter(jj -> jj.getWorkerId() == -1).count());


        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile("F:\\models\\muc/input\\mito\\trafficAssignment/studyNetworkDense.xml");

        //write out origin matches and travel time before match
        String output = matchBaseDirectory + "/originMatches_" + SCALE_FACTER + "_oneStep.csv";
        calculateCurrentTT(output,network,dataContainer);

        final Map<String, List<Job>> jobsBySector = dataContainer.getJobDataManager().getJobs().stream().collect(Collectors.groupingBy(Job::getType));
        final List<Person> workersList = dataContainer.getHouseholdDataManager().getPersons().stream().filter(pp -> pp.getOccupation() == Occupation.EMPLOYED).collect(Collectors.toList());

        //run matching algorithm by sector
        for(String sector : jobsBySector.keySet()){
            final List<Job> jobs = jobsBySector.get(sector);
            final List<Person> workers = workersList.stream().filter(pp -> sector.equals(dataContainer.getJobDataManager().getJobFromId(pp.getJobId()).getType())).collect(Collectors.toList());
            logger.info("Start matches for sector " + sector + " with " + jobs.size() + " jobs and " + workers.size() + " workers");
            final Map<Integer, Tuple<Integer, Double>> jobByPersonCurrent = matchSector(sector, workers, jobs, dataContainer, network);
            logger.info("Writing matches for sector " + sector);
            writeMatches(matchBaseDirectory, sector, jobByPersonCurrent);
        }

        logger.info("Scale Facter: " + SCALE_FACTER + "|Run time: " + (System.currentTimeMillis()-t0));
    }


    private static synchronized void writeMatches(String baseDirectoy, String sector, Map<Integer, Tuple<Integer, Double>> jobByPerson) {
        File file = new File(baseDirectoy + "/finalMatches" + sector + "_" + SCALE_FACTER + "_oneStep.csv");
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

    private static Map<Integer, Tuple<Integer, Double>> matchSector(String sector, List<Person> workers, List<Job> jobs, DataContainerWithSchools dataContainer, Network carNetwork) {
        OpenIntIntHashMap worker2Index = new OpenIntIntHashMap();
        OpenIntIntHashMap jobs2Index = new OpenIntIntHashMap();

        OpenIntIntHashMap index2Worker = new OpenIntIntHashMap();
        OpenIntIntHashMap index2Job = new OpenIntIntHashMap();

        Set<InitialNode> toNodes = new HashSet<>();
        int j = 0;
        for (Job job: jobs) {
            Node node = NetworkUtils.getNearestNode(carNetwork, CoordUtils.createCoord(job.getCoordinate()));
            toNodes.add(new InitialNode(node, 0., 0.));
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

        LeastCostPathCalculatorFactory multiNodeFactory = new FastMultiNodeDijkstraFactory(true);
        FreespeedTravelTimeAndDisutility freespeed = new FreespeedTravelTimeAndDisutility(ConfigUtils.createConfig().planCalcScore());

        final int partitionSize = (int) ((double) workers.size() / (Properties.get().main.numberOfThreads)) + 1;
        Iterable<List<Person>> partitions = Iterables.partition(workers, partitionSize);
        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads);

        AtomicInteger id = new AtomicInteger();

        for (List<Person> partition : partitions) {
            MultiNodePathCalculator calculator = (MultiNodePathCalculator) multiNodeFactory.createPathCalculator(carNetwork, freespeed, freespeed);

            executor.addTaskToQueue(() -> {
                try {
                    int counterr = id.incrementAndGet();
                    int counter = 0;

                    for (Person worker : partition) {
                        if(LongMath.isPowerOfTwo(counter)) {
                            logger.info("Routed " + counter + " workers in partition " + counterr);
                        }

                        int personId = worker.getId();
                        int dwellingId = dataContainer.getHouseholdDataManager().getPersonFromId(personId).getHousehold().getDwellingId();
                        final Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(dwellingId);

                        Node originNode = NetworkUtils.getNearestNode(carNetwork, CoordUtils.createCoord(dwelling.getCoordinate()));
                        calculator.calcLeastCostPath(originNode, aggregatedToNodes, Properties.get().transportModel.peakHour_s, null, null);

                        for(Job job: jobs) {
                            Node destinationNode = NetworkUtils.getNearestNode(carNetwork, CoordUtils.createCoord(job.getCoordinate()));
                            double travelTime = calculator.constructPath(originNode, destinationNode, Properties.get().transportModel.peakHour_s).travelTime;
                            //convert to minutes
                            travelTime /= 60.;
                            costMatrix[worker2Index.get(personId)][jobs2Index.get(job.getId())] = travelTime;
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

    public static void scale(double scaleFactor, DataContainerWithSchools dataContainer) {
        //scale households
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        Collection<Household> households = householdDataManager.getHouseholds();

        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        Collection<Job> jobs = jobDataManager.getJobs();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
        Collection<Dwelling> dwellings = realEstateDataManager.getDwellings();

        Map<String,List<Job>> occupiedJobBySectorBeforeSampling = jobs.stream().filter(jj -> jj.getWorkerId() > 0).collect(Collectors.groupingBy(Job::getType));

        for (Household hh : households) {
            if (SiloUtil.getRandomNumberAsDouble() > scaleFactor) {
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
                if (SiloUtil.getRandomNumberAsDouble() > scaleFactor) {
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

    public static void calculateCurrentTT(String output, Network carNetwork, DataContainerWithSchools dataContainer) {
        List<Person> workers = dataContainer.getHouseholdDataManager().getPersons().stream().filter(pp -> pp.getOccupation() == Occupation.EMPLOYED).collect(Collectors.toList());
        final int partitionSize = (int) ((double) workers.size() / Runtime.getRuntime().availableProcessors()) + 1;
        Iterable<List<Person>> partitions = Iterables.partition(workers, partitionSize);
        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads);
        logger.info("Total " + partitionSize + " partition with " + workers.size() + "workers");

        FreespeedTravelTimeAndDisutility freespeed = new FreespeedTravelTimeAndDisutility(ConfigUtils.createConfig().planCalcScore());

        StringBuilder header = new StringBuilder();
        header.append("personId,workerId,sector,ttOrigin");
        header.append('\n');
        try {
            writeToFile(output,header.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        AtomicInteger id = new AtomicInteger();
        for (List<Person> partition : partitions) {
            LeastCostPathCalculator dijkstra = new FastDijkstraFactory(false)
                    .createPathCalculator(carNetwork, freespeed, freespeed);

            executor.addTaskToQueue(() -> {
                try {
                    int counterr = id.incrementAndGet();
                    int counter = 0;
                    StringBuilder line = new StringBuilder();
                    for (Person worker : partition) {
                        if(LongMath.isPowerOfTwo(counter)) {
                            logger.info("Routed " + counter + " workers in partition " + counterr);
                        }

                        int personId = worker.getId();
                        int dwellingId = dataContainer.getHouseholdDataManager().getPersonFromId(personId).getHousehold().getDwellingId();
                        final Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(dwellingId);
                        final Job job = dataContainer.getJobDataManager().getJobFromId(worker.getJobId());

                        Node originNode = NetworkUtils.getNearestNode(carNetwork, CoordUtils.createCoord(dwelling.getCoordinate()));
                        Node destinationNode = NetworkUtils.getNearestNode(carNetwork, CoordUtils.createCoord(job.getCoordinate()));

                        LeastCostPathCalculator.Path path = dijkstra.calcLeastCostPath(originNode,destinationNode,Properties.get().transportModel.peakHour_s,null,null);

                        line.append(personId);
                        line.append(',');
                        line.append(job.getId());
                        line.append(',');
                        line.append(job.getType());
                        line.append(',');
                        line.append(path.travelTime);
                        line.append('\n');

                    }

                    try {
                        writeToFile(output,line.toString());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    counter++;

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            });
        }
        executor.execute();


    }

    public static synchronized void writeToFile(String path, String building) throws FileNotFoundException {
        PrintWriter bd = new PrintWriter(new FileOutputStream(path, true));
        bd.write(building);
        bd.close();
    }

}
