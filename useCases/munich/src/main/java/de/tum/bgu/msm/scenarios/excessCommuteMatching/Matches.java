package de.tum.bgu.msm.scenarios.excessCommuteMatching;

import blogspot.software_and_algorithms.stern_library.optimization.HungarianAlgorithm;
import cern.colt.map.tint.OpenIntIntHashMap;
import com.google.common.collect.Iterables;
import com.google.common.math.LongMath;
import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.*;
import org.matsim.core.router.costcalculators.FreespeedTravelTimeAndDisutility;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.geometry.CoordUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Matches {

    private final static Logger logger = Logger.getLogger(Matches.class);


    public static void main(String[] args) {


        String path = "C:\\Users\\Nico\\tum\\fabilut\\gitproject\\muc/siloMuc.properties";
        String matchBaseDirectory = "C:\\Users\\Nico\\tum\\msm-papers\\data\\thePerfectMatch";

        Properties properties = SiloUtil.siloInitialization(path);
        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, null);
        DataBuilder.read(properties, dataContainer);

        final Map<String, Map<Integer, List<Job>>> jobsByZoneBySector = dataContainer.getJobDataManager().getJobs().stream().collect(Collectors.groupingBy(Job::getType, Collectors.groupingBy(Job::getZoneId)));
        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile("C:\\Users\\Nico\\tum\\fabilut\\gitproject\\muc/input\\mito\\trafficAssignment/studyNetworkDense.xml");

        LeastCostPathCalculatorFactory multiNodeFactory = new FastMultiNodeDijkstraFactory(true);
        FreespeedTravelTimeAndDisutility freespeed = new FreespeedTravelTimeAndDisutility(ConfigUtils.createConfig().planCalcScore());



        for(String sector: JobType.getJobTypes()) {
            logger.warn("Starting matching of sector " + sector);
            Map<Integer, Tuple<Integer, Double>> jobByPerson = new ConcurrentHashMap<>();

            final List<Match> matches = readMatches(matchBaseDirectory, sector);
            final Map<Integer, List<Match>> collect = matches.stream().collect(Collectors.groupingBy(match -> match.zoneId));
            logger.info("read " + matches.size() + " matches.");
            AtomicInteger counter = new AtomicInteger();
            final Set<Map.Entry<Integer, List<Match>>> entries = collect.entrySet();

            final int partitionSize = (int) ((double) entries.size() / (Properties.get().main.numberOfThreads)) + 1;
            Iterable<List<Map.Entry<Integer, List<Match>>>> partitions = Iterables.partition(entries, partitionSize);
            ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads);

            for (List<Map.Entry<Integer, List<Match>>> partition : partitions) {
                executor.addTaskToQueue(() -> {
                    try {
                        MultiNodePathCalculator calculator = (MultiNodePathCalculator) multiNodeFactory.createPathCalculator(network, freespeed, freespeed);

                        for (Map.Entry<Integer, List<Match>> entry : partition) {
                            if(LongMath.isPowerOfTwo(counter.get())) {
                                logger.info("Matched " + counter + " zones.");
                            }
                            final List<Job> jobs = jobsByZoneBySector.get(sector).get(entry.getKey());
                            final Map<Integer, Tuple<Integer, Double>> jobByPersonCurrent = matchZone(entry.getValue(), jobs, dataContainer, network, calculator);
                            jobByPerson.putAll(jobByPersonCurrent);
                            counter.incrementAndGet();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                });
            }
            executor.execute();



            logger.info("Writing matches for sector " + sector);
            writeMatches(matchBaseDirectory, sector, jobByPerson);
        }




    }

    private static void writeMatches(String baseDirectoy, String sector, Map<Integer, Tuple<Integer, Double>> jobByPerson) {
        File file = new File(baseDirectoy+"/finalMatches"+sector+".csv");
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

    private static Map<Integer, Tuple<Integer, Double>> matchZone(List<Match> matches, List<Job> jobs, DataContainerWithSchools dataContainer, Network carNetwork, MultiNodePathCalculator calculator) {


        Set<InitialNode> toNodes = new HashSet<>();
        for (Job job: jobs) {
            Node node = NetworkUtils.getNearestNode(carNetwork, CoordUtils.createCoord(job.getCoordinate()));
            toNodes.add(new InitialNode(node, 0., 0.));
        }
        ImaginaryNode aggregatedToNodes = MultiNodeDijkstra.createImaginaryNode(toNodes);

        OpenIntIntHashMap worker2Index = new OpenIntIntHashMap();
        OpenIntIntHashMap jobs2Index = new OpenIntIntHashMap();

        OpenIntIntHashMap index2Worker = new OpenIntIntHashMap();
        OpenIntIntHashMap index2Job = new OpenIntIntHashMap();

        double[][] costMatrix = new double[matches.size()][jobs.size()];

        int i = 0;
        for(Match match: matches) {
            int j= 0;

            int personId = match.workerId;
            int dwellingId = dataContainer.getHouseholdDataManager().getPersonFromId(personId).getHousehold().getDwellingId();
            final Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(dwellingId);

            Node originNode = NetworkUtils.getNearestNode(carNetwork, CoordUtils.createCoord(dwelling.getCoordinate()));
            calculator.calcLeastCostPath(originNode, aggregatedToNodes, Properties.get().transportModel.peakHour_s, null, null);

            for(Job job: jobs) {
                Node destinationNode = NetworkUtils.getNearestNode(carNetwork, CoordUtils.createCoord(job.getCoordinate()));
                double travelTime = calculator.constructPath(originNode, destinationNode, Properties.get().transportModel.peakHour_s).travelTime;

                //convert to minutes
                travelTime /= 60.;
                costMatrix[i][j] = travelTime;

                jobs2Index.put(job.getId(),j);
                index2Job.put(j, job.getId());
                j++;
            }
            worker2Index.put(personId, i);
            index2Worker.put(i, personId);
            i++;
        }


        final HungarianAlgorithm withinZoneMatching = new HungarianAlgorithm(costMatrix);
        final int[] results = withinZoneMatching.execute();

        Map<Integer, Tuple<Integer,Double>> jobByPerson = new LinkedHashMap<>();

        for (int k = 0; k < results.length; k++) {
            int personId = index2Worker.get(k);
            int jobId = index2Job.get(results[k]);
            jobByPerson.put(personId, new Tuple<>(jobId, costMatrix[k][results[k]]));
        }
        return jobByPerson;
    }


    private static List<Match> readMatches(String base, String sector) {
        List<Match> matches = new ArrayList<>();
        File file = new File(base +"/"+ sector+"matches.csv");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            final String header = reader.readLine();
            String record = reader.readLine();
            while (record!= null) {
                final String[] split = record.split(",");
                int workerId = Integer.parseInt(split[0]);
                int zoneId = Integer.parseInt(split[1]);
                matches.add(new Match(workerId, zoneId));
                record = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matches;
    }

    public static class Match {
        private final int workerId;
        private final int zoneId;

        public Match(int workerId, int zoneId) {
            this.workerId = workerId;
            this.zoneId = zoneId;
        }
    }
}
