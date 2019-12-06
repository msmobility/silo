package de.tum.bgu.msm.matsim;

import ch.sbb.matsim.routing.pt.raptor.*;
import com.google.common.collect.Iterables;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.*;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.ActivityFacilitiesFactory;
import org.matsim.facilities.ActivityFacilitiesFactoryImpl;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.Facility;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import java.util.*;

public class MatsimSkimCreator {

    private final static Logger logger = Logger.getLogger(MatsimSkimCreator.class);

    private MatsimData matsimData;

    public MatsimSkimCreator(MatsimData provider) {
        this.matsimData = provider;
    }

    public IndexedDoubleMatrix2D createCarSkim(Collection<Zone> zones) {
        final int partitionSize = (int) ((double) zones.size() / (Properties.get().main.numberOfThreads)) + 1;
        Iterable<List<Zone>> partitions = Iterables.partition(zones, partitionSize);

        IndexedDoubleMatrix2D skim = new IndexedDoubleMatrix2D(zones, zones);
        Network carNetwork = matsimData.getCarNetwork();
        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads);
        for (final List<Zone> partition : partitions) {
            executor.addTaskToQueue(() -> {
                try {
                    MultiNodePathCalculator calculator = matsimData.createMultiNodePathCalculator();
                    Set<InitialNode> toNodes = new HashSet<>();
                    for (Zone zone : zones) {
                        for (Coord coord : matsimData.getZoneConnectorManager().getCoordsForZone(zone)) {
                            Node originNode = NetworkUtils.getNearestNode(carNetwork, coord);
                            toNodes.add(new InitialNode(originNode, 0., 0.));
                        }
                    }

                    ImaginaryNode aggregatedToNodes = MultiNodeDijkstra.createImaginaryNode(toNodes);

                    for (Zone origin : partition) {
                        Node originNode = NetworkUtils.getNearestNode(carNetwork, matsimData.getZoneConnectorManager().getCoordsForZone(origin).get(0));
                        calculator.calcLeastCostPath(originNode, aggregatedToNodes, Properties.get().transportModel.peakHour_s, null, null);
                        for (Zone destination : zones) {
                            Node destinationNode = NetworkUtils.getNearestNode(carNetwork, matsimData.getZoneConnectorManager().getCoordsForZone(destination).get(0));
                            double travelTime = calculator.constructPath(originNode, destinationNode, Properties.get().transportModel.peakHour_s).travelTime;

                            //convert to minutes
                            travelTime /= 60.;

                            skim.setIndexed(origin.getZoneId(), destination.getZoneId(), travelTime);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            });
        }
        executor.execute();
        assignIntrazonals(5, Float.MAX_VALUE, 0.66f, skim);
        return skim;
    }

    public IndexedDoubleMatrix2D createPtSkim(Collection<Zone> zones) {
        final int partitionSize = (int) ((double) zones.size() / (Properties.get().main.numberOfThreads)) + 1;
        Iterable<List<Zone>> partitions = Iterables.partition(zones, partitionSize);

        IndexedDoubleMatrix2D skim = new IndexedDoubleMatrix2D(zones, zones);

        //compute closest egress stops per zone
        SwissRailRaptorData raptorData = matsimData.getRaptorData(RaptorStaticConfig.RaptorOptimization.OneToAllRouting);
        RaptorParameters parameters = matsimData.getRaptorParameters();
        double walkSpeed = parameters.getBeelineWalkSpeed();
        Map<Zone, Collection<TransitStopFacility>> stopsPerZone = new LinkedHashMap<>();
        ActivityFacilitiesFactoryImpl activityFacilitiesFactory = new ActivityFacilitiesFactoryImpl();

        for (Zone zone : zones) {
            final Coord coord = matsimData.getZoneConnectorManager().getCoordsForZone(zone).get(0);
            Collection<TransitStopFacility> stops = raptorData.findNearbyStops(coord.getX(), coord.getY(), parameters.getSearchRadius());
            if (stops.isEmpty()) {
                TransitStopFacility nearest = raptorData.findNearestStop(coord.getX(), coord.getY());
                double nearestStopDistance = CoordUtils.calcEuclideanDistance(coord, nearest.getCoord());
                stops = raptorData.findNearbyStops(coord.getX(), coord.getY(), nearestStopDistance + parameters.getExtensionRadius());
            }
            stopsPerZone.put(zone, stops);
        }

        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads);
        for (final List<Zone> partition : partitions) {
            executor.addTaskToQueue(() -> {
                try {
                    SwissRailRaptor raptor = matsimData.createSwissRailRaptor(RaptorStaticConfig.RaptorOptimization.OneToAllRouting);
                    for (Zone origin : partition) {
                        final Coord fromCoord = matsimData.getZoneConnectorManager().getCoordsForZone(origin).get(0);
                        Facility fromFacility = ((ActivityFacilitiesFactory) activityFacilitiesFactory).createActivityFacility(Id.create(1, ActivityFacility.class), fromCoord);

                        //calc tree from origin zone connector. note that it will search for multiple
                        //start stops accessible from the connector
                        final Map<Id<TransitStopFacility>, SwissRailRaptorCore.TravelInfo> idTravelInfoMap
                                = raptor.calcTree(fromFacility, Properties.get().transportModel.peakHour_s, null);
                        for (Zone destination : zones) {
                            if (origin.equals(destination)) {
                                //Intrazonals will be assigned afterwards
                                continue;
                            }

                            //compute direct walk time
                            final Coord toCoord = matsimData.getZoneConnectorManager().getCoordsForZone(destination).get(0);
                            double directDistance = CoordUtils.calcEuclideanDistance(fromCoord, toCoord);
                            double directWalkTime = directDistance / walkSpeed;

                            double travelTime = Double.MAX_VALUE;
                            for (TransitStopFacility stop : stopsPerZone.get(destination)) {
                                final SwissRailRaptorCore.TravelInfo travelInfo = idTravelInfoMap.get(stop.getId());
                                if (travelInfo != null) {
                                    //compute egress to actual zone connector for this stop
                                    double distance = CoordUtils.calcEuclideanDistance(stop.getCoord(), toCoord);
                                    double egressTime = distance / walkSpeed;
                                    //total travel time includes access, egress and waiting times
                                    double time = travelInfo.ptTravelTime + travelInfo.waitingTime + travelInfo.accessTime + egressTime;
                                    //take the most optimistic time up until now
                                    travelTime = Math.min(travelTime, time);
                                }
                            }

                            //check whether direct walk time is faster
                            travelTime = Math.min(travelTime, directWalkTime);

                            //convert to minutes
                            travelTime /= 60.;
                            skim.setIndexed(origin.getZoneId(), destination.getZoneId(), travelTime);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            });
        }
        executor.execute();
        assignIntrazonals(5, Float.MAX_VALUE, 0.66f, skim);
        return skim;
    }

    public IndexedDoubleMatrix2D createTeleportedSkim(Collection<Zone> zones, String mode) {

        final int partitionSize = (int) ((double) zones.size() / (Properties.get().main.numberOfThreads)) + 1;
        Iterable<List<Zone>> partitions = Iterables.partition(zones, partitionSize);

        IndexedDoubleMatrix2D skim = new IndexedDoubleMatrix2D(zones, zones);

        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads);
        for (final List<Zone> partition : partitions) {
            executor.addTaskToQueue(() -> {
                try {
                    final RoutingModule teleportationRouter = matsimData.getTeleportationRouter(mode);
                    for (Zone origin : partition) {
                        for (Zone destination : zones) {
                            Coord originCoord = matsimData.getZoneConnectorManager().getCoordsForZone(origin).get(0);
                            Coord destinationCoord = matsimData.getZoneConnectorManager().getCoordsForZone(destination).get(0);

                            ActivityFacilitiesFactoryImpl activityFacilitiesFactory = new ActivityFacilitiesFactoryImpl();
                            Facility fromFacility = ((ActivityFacilitiesFactory) activityFacilitiesFactory).createActivityFacility(Id.create(1, ActivityFacility.class), originCoord);
                            Facility toFacility = ((ActivityFacilitiesFactory) activityFacilitiesFactory).createActivityFacility(Id.create(2, ActivityFacility.class), destinationCoord);
                            final double peakHour_s = Properties.get().transportModel.peakHour_s;
                            List<? extends PlanElement> planElements = teleportationRouter.calcRoute(fromFacility, toFacility, peakHour_s, null);
                            double arrivalTime = peakHour_s;

                            if (!planElements.isEmpty()) {
                                final Leg lastLeg = (Leg) planElements.get(planElements.size() - 1);
                                arrivalTime = lastLeg.getDepartureTime() + lastLeg.getTravelTime();
                            }

                            double time = arrivalTime - peakHour_s;

                            //convert to minutes
                            time /= 60.;
                            skim.setIndexed(origin.getZoneId(), destination.getZoneId(), time);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            });
        }
        executor.execute();
        assignIntrazonals(5, Float.MAX_VALUE, 0.66f, skim);
        return skim;
    }

    public IndexedDoubleMatrix2D createFreeSpeedFactorSkim(Collection<Zone> zones, double factor) {
        final int partitionSize = (int) ((double) zones.size() / (Properties.get().main.numberOfThreads)) + 1;
        Iterable<List<Zone>> partitions = Iterables.partition(zones, partitionSize);

        IndexedDoubleMatrix2D skim = new IndexedDoubleMatrix2D(zones, zones);
        Network carNetwork = matsimData.getCarNetwork();
        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads);
        for (final List<Zone> partition : partitions) {
            executor.addTaskToQueue(() -> {
                try {
                    MultiNodePathCalculator calculator = matsimData.createFreeSpeedMultiNodePathCalculator();
                    Set<InitialNode> toNodes = new HashSet<>();
                    for (Zone zone : zones) {
                        for (Coord coord : matsimData.getZoneConnectorManager().getCoordsForZone(zone)) {
                            Node originNode = NetworkUtils.getNearestNode(carNetwork, coord);
                            toNodes.add(new InitialNode(originNode, 0., 0.));
                        }
                    }

                    ImaginaryNode aggregatedToNodes = MultiNodeDijkstra.createImaginaryNode(toNodes);

                    for (Zone origin : partition) {
                        Node originNode = NetworkUtils.getNearestNode(carNetwork, matsimData.getZoneConnectorManager().getCoordsForZone(origin).get(0));
                        calculator.calcLeastCostPath(originNode, aggregatedToNodes, Properties.get().transportModel.peakHour_s, null, null);
                        for (Zone destination : zones) {
                            Node destinationNode = NetworkUtils.getNearestNode(carNetwork, matsimData.getZoneConnectorManager().getCoordsForZone(destination).get(0));
                            double travelTime = calculator.constructPath(originNode, destinationNode, Properties.get().transportModel.peakHour_s).travelTime;

                            //adjust by factor
                            travelTime *= factor;

                            //convert to minutes
                            travelTime /= 60.;

                            skim.setIndexed(origin.getZoneId(), destination.getZoneId(), travelTime);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            });
        }
        executor.execute();
        assignIntrazonals(5, Float.MAX_VALUE, 0.66f, skim);
        return skim;
    }

    //TODO: copied from MITO car skim updater...maybe provide a utility function there
    private void assignIntrazonals(int numberOfNeighbours, float maximumMinutes, float proportionOfTime, IndexedDoubleMatrix2D skim) {
        int nonIntrazonalCounter = 0;
        for (int i = 1; i < skim.columns(); i++) {
            int i_id = skim.getIdForInternalColumnIndex(i);
            double[] minTimeValues = new double[numberOfNeighbours];
            for (int k = 0; k < numberOfNeighbours; k++) {
                minTimeValues[k] = maximumMinutes;
            }
            //find the  n closest neighbors - the lower travel time values in the matrix column
            for (int j = 1; j < skim.rows(); j++) {
                int j_id = skim.getIdForInternalRowIndex(j);
                int minimumPosition = 0;
                while (minimumPosition < numberOfNeighbours) {
                    if (minTimeValues[minimumPosition] > skim.getIndexed(i_id, j_id) && skim.getIndexed(i_id, j_id) != 0) {
                        for (int k = numberOfNeighbours - 1; k > minimumPosition; k--) {
                            minTimeValues[k] = minTimeValues[k - 1];
                        }
                        minTimeValues[minimumPosition] = skim.getIndexed(i_id, j_id);
                        break;
                    }
                    minimumPosition++;
                }
            }
            double globalMinTime = 0;
            for (int k = 0; k < numberOfNeighbours; k++) {
                globalMinTime += minTimeValues[k];
            }
            globalMinTime = globalMinTime / numberOfNeighbours * proportionOfTime;

            //fill with the calculated value the cells with zero
            for (int j = 1; j < skim.rows(); j++) {
                int j_id = skim.getIdForInternalColumnIndex(j);
                if (skim.getIndexed(i_id, j_id) == 0) {
                    skim.setIndexed(i_id, j_id, globalMinTime);
                    if (i != j) {
                        nonIntrazonalCounter++;
                    }
                }
            }
        }
        logger.info("Calculated intrazonal times and distances using the " + numberOfNeighbours + " nearest neighbours.");
        logger.info("The calculation of intrazonals has also assigned values for cells with travel time equal to 0, that are not intrazonal: (" +
                nonIntrazonalCounter + " cases).");
    }
}
