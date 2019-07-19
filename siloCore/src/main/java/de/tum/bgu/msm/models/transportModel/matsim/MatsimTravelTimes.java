package de.tum.bgu.msm.models.transportModel.matsim;

import ch.sbb.matsim.routing.pt.raptor.*;
import com.google.common.collect.Iterables;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Location;
import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix2D;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.Config;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.*;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.ActivityFacilitiesFactory;
import org.matsim.facilities.ActivityFacilitiesFactoryImpl;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.Facility;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import java.util.*;

/**
 * @author dziemke, nkuehnel
 */
public final class MatsimTravelTimes implements TravelTimes {
    private final static Logger logger = Logger.getLogger(MatsimTravelTimes.class);

    private final static int NUMBER_OF_CALC_POINTS = 1;

    public enum ZoneConnectorMethod {RANDOM, WEIGHTED_BY_POPULATION}

    private MatsimRoutingProvider routingProvider;

    private final Map<String, IndexedDoubleMatrix2D> skimsByMode = new HashMap<>();
    private Map<Integer, Zone> zones;

    private TripRouter tripRouter;

    private ZoneConnectorManager zoneConnectorManager;

    private IndexedDoubleMatrix2D travelTimeFromRegion;
    private IndexedDoubleMatrix2D travelTimeToRegion;

    private final ZoneConnectorMethod zoneConnectorMethod;
    private final Config config;

    public MatsimTravelTimes(ZoneConnectorMethod method, Config config) {
        this.zoneConnectorMethod = method;
        this.config = config;
    }

    public void initialize(DataContainer dataContainer, MatsimRoutingProvider matsimRoutingProvider) {
        final GeoData geoData = dataContainer.getGeoData();
        this.zones = geoData.getZones();
        this.routingProvider = matsimRoutingProvider;
        this.travelTimeFromRegion = new IndexedDoubleMatrix2D(geoData.getRegions().values(), geoData.getZones().values());
        this.travelTimeFromRegion.assign(-1);
        this.travelTimeToRegion = new IndexedDoubleMatrix2D(geoData.getZones().values(), geoData.getRegions().values());
        this.travelTimeToRegion.assign(-1);

        switch (zoneConnectorMethod) {
            case RANDOM:
                this.zoneConnectorManager = ZoneConnectorManager.createRandomZoneConnectors(zones.values(), NUMBER_OF_CALC_POINTS);
                break;
            case WEIGHTED_BY_POPULATION:
                this.zoneConnectorManager = ZoneConnectorManager.createWeightedZoneConnectors(zones.values(),
                        dataContainer.getRealEstateDataManager(),
                        dataContainer.getHouseholdDataManager());
                break;
            default:
                throw new RuntimeException("No valid zone connector method defined!");
        }
    }

    public void update() {
        this.tripRouter = routingProvider.createTripRouter();
        this.skimsByMode.clear();
        if (this.travelTimeFromRegion != null) {
            this.travelTimeFromRegion.assign(-1);
        }
        if (this.travelTimeToRegion != null) {
            this.travelTimeToRegion.assign(-1);
        }
    }

    // TODO Use travel costs?
    @Override
    public double getTravelTime(Location origin, Location destination, double timeOfDay_s, String mode) {
        Coord originCoord;
        Coord destinationCoord;
        if (origin instanceof MicroLocation && destination instanceof MicroLocation) {
            // Microlocations case
            originCoord = CoordUtils.createCoord(((MicroLocation) origin).getCoordinate());
            destinationCoord = CoordUtils.createCoord(((MicroLocation) destination).getCoordinate());
        } else if (origin instanceof Zone && destination instanceof Zone) {
            // Non-microlocations case
            originCoord = zoneConnectorManager.getCoordsForZone((Zone) origin).get(0);
            destinationCoord = zoneConnectorManager.getCoordsForZone((Zone) destination).get(0);
        } else {
            throw new IllegalArgumentException("Origin and destination have to be consistent in location type!");
        }

        ActivityFacilitiesFactoryImpl activityFacilitiesFactory = new ActivityFacilitiesFactoryImpl();
        Facility fromFacility = ((ActivityFacilitiesFactory) activityFacilitiesFactory).createActivityFacility(Id.create(1, ActivityFacility.class), originCoord);
        Facility toFacility = ((ActivityFacilitiesFactory) activityFacilitiesFactory).createActivityFacility(Id.create(2, ActivityFacility.class), destinationCoord);
        List<? extends PlanElement> planElements = tripRouter.calcRoute(mode, fromFacility, toFacility, timeOfDay_s, null);
        double arrivalTime = timeOfDay_s;

        if (!planElements.isEmpty()) {
            final Leg lastLeg = (Leg) planElements.get(planElements.size() - 1);
            arrivalTime = lastLeg.getDepartureTime() + lastLeg.getTravelTime();
        }

        double time = arrivalTime - timeOfDay_s;

        //convert to minutes
        time /= 60.;
        return time;
    }

    @Override
    public double getTravelTimeFromRegion(Region origin, Zone destination, double timeOfDay_s, String mode) {

        int destinationZone = destination.getZoneId();
        if (travelTimeFromRegion.getIndexed(origin.getId(), destinationZone) > 0) {
            return travelTimeFromRegion.getIndexed(origin.getId(), destinationZone);
        }
        double min = Double.MAX_VALUE;
        for (Zone zoneInRegion : origin.getZones()) {
            double travelTime = getPeakSkim(mode).getIndexed(zoneInRegion.getZoneId(), destinationZone);
            if (travelTime < min) {
                min = travelTime;
            }
        }
        travelTimeFromRegion.setIndexed(origin.getId(), destinationZone, min);
        return min;

    }

    @Override
    public double getTravelTimeToRegion(Zone origin, Region destination, double timeOfDay_s, String mode) {

        if (travelTimeToRegion.getIndexed(origin.getId(), destination.getId()) > 0) {
            return travelTimeFromRegion.getIndexed(origin.getId(), destination.getId());
        }
        double min = Double.MAX_VALUE;
        for (Zone zoneInRegion : destination.getZones()) {
            double travelTime = getPeakSkim(mode).getIndexed(origin.getZoneId(), zoneInRegion.getZoneId());
            if (travelTime < min) {
                min = travelTime;
            }
        }
        travelTimeFromRegion.setIndexed(origin.getId(), destination.getId(), min);
        return min;
    }

    @Override
    public IndexedDoubleMatrix2D getPeakSkim(String mode) {
        if (skimsByMode.containsKey(mode)) {
            return skimsByMode.get(mode);
        } else {
            logger.info("Calculating skim matrix for mode " + mode +
                    " using " + Properties.get().main.numberOfThreads + " threads.");
            final int partitionSize = (int) ((double) zones.size() / (Properties.get().main.numberOfThreads)) + 1;
            Iterable<List<Zone>> partitions = Iterables.partition(zones.values(), partitionSize);

            IndexedDoubleMatrix2D skim = new IndexedDoubleMatrix2D(zones.values(), zones.values());
            switch (mode) {
                case TransportMode.car:
                    createCarSkim(skim, partitions);
                    break;
                case TransportMode.pt:
                    if (config.transit().isUseTransit()) {
                        createPtSkim(skim, partitions);
                        break;
                    } else {
                        logger.warn("No schedule/ network provided for pt.");
                    }
                default:
                    logger.warn("Defaulting to teleportation.");
                    createTeleportedSkim(skim, partitions, mode);
            }
            assignIntrazonals(5, 10, 0.66f, skim);
            skimsByMode.put(mode, skim);
            logger.info("Finished skim for mode " + mode);
            return skim;
        }
    }

    private void createTeleportedSkim(IndexedDoubleMatrix2D skim, Iterable<List<Zone>> partitions, String mode) {
        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads);
        for (final List<Zone> partition : partitions) {
            executor.addTaskToQueue(() -> {
                try {
                    TravelTimes copy = duplicate();
                    for (Zone origin : partition) {
                        for (Zone destination : zones.values()) {
                            double travelTime = copy.getTravelTime(origin, destination, Properties.get().transportModel.peakHour_s, mode);

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
    }

    private void createPtSkim(IndexedDoubleMatrix2D skim, Iterable<List<Zone>> partitions) {

        //compute closest egress stops per zone
        SwissRailRaptorData raptorData = routingProvider.getRaptorData(RaptorStaticConfig.RaptorOptimization.OneToAllRouting);
        RaptorParameters parameters = routingProvider.getRaptorParameters();
        double walkSpeed = parameters.getBeelineWalkSpeed();
        Map<Zone, Collection<TransitStopFacility>> stopsPerZone = new LinkedHashMap<>();
        ActivityFacilitiesFactoryImpl activityFacilitiesFactory = new ActivityFacilitiesFactoryImpl();

        for (Zone zone : zones.values()) {
            final Coord coord = zoneConnectorManager.getCoordsForZone(zone).get(0);
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
                    SwissRailRaptor raptor = routingProvider.createSwissRailRaptor(RaptorStaticConfig.RaptorOptimization.OneToAllRouting);
                    for (Zone origin : partition) {
                        final Coord fromCoord = zoneConnectorManager.getCoordsForZone(origin).get(0);
                        Facility fromFacility = ((ActivityFacilitiesFactory) activityFacilitiesFactory).createActivityFacility(Id.create(1, ActivityFacility.class), fromCoord);

                        //calc tree from origin zone connector. note that it will search for multiple
                        //start stops accessible from the connector
                        final Map<Id<TransitStopFacility>, SwissRailRaptorCore.TravelInfo> idTravelInfoMap
                                = raptor.calcTree(fromFacility, Properties.get().transportModel.peakHour_s, null);
                        for (Zone destination : zones.values()) {
                            if (origin.equals(destination)) {
                                //Intrazonals will be assigned afterwards
                                continue;
                            }

                            //compute direct walk time
                            final Coord toCoord = zoneConnectorManager.getCoordsForZone(destination).get(0);
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
    }


    private void createCarSkim(IndexedDoubleMatrix2D skim, Iterable<List<Zone>> partitions) {
        Network carNetwork = routingProvider.getCarNetwork();
        ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads);
        for (final List<Zone> partition : partitions) {
            executor.addTaskToQueue(() -> {
                try {
                    MultiNodePathCalculator calculator = routingProvider.createMultiNodePathCalculator();
                    Set<InitialNode> toNodes = new HashSet<>();
                    for (Zone zone : zones.values()) {
                        for (Coord coord : zoneConnectorManager.getCoordsForZone(zone)) {
                            Node originNode = NetworkUtils.getNearestNode(carNetwork, coord);
                            toNodes.add(new InitialNode(originNode, 0., 0.));
                        }
                    }

                    ImaginaryNode aggregatedToNodes = MultiNodeDijkstra.createImaginaryNode(toNodes);

                    for (Zone origin : partition) {
                        Node originNode = NetworkUtils.getNearestNode(carNetwork, zoneConnectorManager.getCoordsForZone(origin).get(0));
                        calculator.calcLeastCostPath(originNode, aggregatedToNodes, Properties.get().transportModel.peakHour_s, null, null);
                        for (Zone destination : zones.values()) {
                            Node destinationNode = NetworkUtils.getNearestNode(carNetwork, zoneConnectorManager.getCoordsForZone(destination).get(0));
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
    }

    @Override
    public TravelTimes duplicate() {
        logger.warn("Creating another TravelTimes object.");
        MatsimTravelTimes matsimTravelTimes = new MatsimTravelTimes(zoneConnectorMethod, config);
        matsimTravelTimes.zones = this.zones;
        matsimTravelTimes.zoneConnectorManager = this.zoneConnectorManager;
        matsimTravelTimes.routingProvider = routingProvider;
        matsimTravelTimes.update();
        matsimTravelTimes.travelTimeFromRegion = this.travelTimeFromRegion.copy();
        matsimTravelTimes.travelTimeToRegion = this.travelTimeToRegion.copy();
        matsimTravelTimes.skimsByMode.putAll(this.skimsByMode);
        return matsimTravelTimes;
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