package de.tum.bgu.msm.utils.concurrent;

import com.google.common.collect.Lists;
import com.google.common.math.LongMath;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.FastDijkstraFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ParallelRoutingTest {

    private static Logger logger = Logger.getLogger(ParallelRoutingTest.class);

    @Test
    public void testParallelRouting() {

        Network network = NetworkUtils.createNetwork();
        MatsimNetworkReader reader = new MatsimNetworkReader(network);
        reader.readFile("D:\\muc\\input\\mito\\trafficAssignment/studyNetworkDense.xml");

        AtomicInteger counter = new AtomicInteger(0);
//        final Coord originCoord = new Coord(4436689.657372447, 5368527.815536651);
////        final Coord destinationCoord = new Coord(4489369.625538794, 5294502.251605561);

        final Coord originCoord = new Coord(4468380.32240, 5351028.45416);
        final Coord destinationCoord = new Coord(4468732.136, 5334111.66697);
        final Node nearestNode = NetworkUtils.getNearestNode(network, originCoord);
        final Node destinationNode = NetworkUtils.getNearestNode(network, destinationCoord);

        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(16);
        final int starttime = 8 * 60 * 60;

        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }

        final List<List<Integer>> partition = Lists.partition(list, (list.size() / 16)+1);

        System.out.println(partition.size());




        for (int i = 0; i < 16; i++) {
            executor.addTaskToQueue(() -> {
                LeastCostPathCalculator tree = new FastDijkstraFactory().createPathCalculator(network, new TravelDisutility() {
                    @Override
                    public double getLinkTravelDisutility(Link link, double time, Person person, Vehicle vehicle) {
                        return link.getLength() / link.getFreespeed();
                    }

                    @Override
                    public double getLinkMinimumTravelDisutility(Link link) {
                        return 1;
                    }
                }, new TravelTime() {
                    @Override
                    public double getLinkTravelTime(Link link, double time, Person person, Vehicle vehicle) {
                        return link.getLength() / link.getFreespeed();
                    }
                });

                // 2,000,000 queries divided by 16 threads -> 125000 queries per thread
                IntStream.range(0, 125000).forEach(j -> {
                    double v = tree.calcLeastCostPath(nearestNode, destinationNode, starttime, null, null).links.size();
                    //final double v = leastCoastPathTree.getTree().get(destinationNode.getId()).getTime() - (8 * 60 * 60);
                    final int get = counter.incrementAndGet();
                    if(LongMath.isPowerOfTwo(get)) {
                        logger.info(get + " " + v);
                    }
                });
                return null;
            });
        }
        executor.execute();
    }
}
