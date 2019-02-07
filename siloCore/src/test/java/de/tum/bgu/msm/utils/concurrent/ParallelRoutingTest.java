package de.tum.bgu.msm.utils.concurrent;

import com.google.common.math.LongMath;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.FastAStarLandmarksFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.vehicles.Vehicle;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ParallelRoutingTest {

    private static Logger logger = Logger.getLogger(ParallelRoutingTest.class);

    @Test
    @Ignore
    public void testParallelRouting() {

        Network network = NetworkUtils.createNetwork();
        MatsimNetworkReader reader = new MatsimNetworkReader(network);
        reader.readFile("D:\\muc\\input\\mito\\trafficAssignment/studyNetworkLight.xml");

        AtomicInteger counter = new AtomicInteger(0);
        final Coord originCoord = new Coord(4436689.657372447, 5368527.815536651);
        final Coord destinationCoord = new Coord(4489369.625538794, 5294502.251605561);
        final Node nearestNode = NetworkUtils.getNearestNode(network, originCoord);
        final Node destinationNode = NetworkUtils.getNearestNode(network, destinationCoord);

        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(16);
        final int starttime = 8 * 60 * 60;

        for (int i = 0; i < 16; i++) {
            executor.addTaskToQueue(() -> {
                LeastCostPathCalculator tree = new FastAStarLandmarksFactory(16).createPathCalculator(network, new TravelDisutility() {
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
                    double v = tree.calcLeastCostPath(nearestNode, destinationNode, starttime, null, null).travelTime;
                    //final double v = leastCoastPathTree.getTree().get(destinationNode.getId()).getTime() - (8 * 60 * 60);
                    final int get = counter.incrementAndGet();
                    if(LongMath.isPowerOfTwo(get)) {
                        logger.info(get);
                    }
                });
                return null;
            });
        }
        executor.execute();
    }
}
