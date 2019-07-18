package de.tum.bgu.msm.models.transportModel.matsim;

import ch.sbb.matsim.routing.pt.raptor.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.TransportModeNetworkFilter;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.*;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.facilities.ActivityFacilitiesFactory;
import org.matsim.facilities.ActivityFacilitiesFactoryImpl;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.Facility;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import javax.inject.Provider;
import java.util.*;

/**
 * @author dziemke, nkuehnel
 */
public final class MatsimTravelTimes implements TravelTimes {
    private final static Logger logger = Logger.getLogger(MatsimTravelTimes.class);

    private final static int NUMBER_OF_CALC_POINTS = 1;

    public enum ZoneConnectorMethod {RANDOM, WEIGHTED_BY_POPULATION}

    private Network carNetwork;
    private Network ptNetwork;

    private TransitSchedule schedule;

    private final Map<String, IndexedDoubleMatrix2D> skimsByMode = new HashMap<>();
    private Map<Integer, Zone> zones;

    private TripRouter tripRouter;

    private ZoneConnectorManager zoneConnectorManager;


    private IndexedDoubleMatrix2D travelTimeFromRegion;
    private IndexedDoubleMatrix2D travelTimeToRegion;

    private Provider<TripRouter> routerProvider;
    private TravelTime travelTime;
    private TravelDisutility travelDisutility;

    private final ZoneConnectorMethod zoneConnectorMethod;

    public MatsimTravelTimes(ZoneConnectorMethod method) {
        this.zoneConnectorMethod = method;
    }

    public void initialize(DataContainer dataContainer, Network network, TransitSchedule schedule) {

        TransportModeNetworkFilter filter = new TransportModeNetworkFilter(network);
        Set<String> car = Sets.newHashSet(TransportMode.car);
        Set<String> pt = Sets.newHashSet(TransportMode.pt);

        Network carNetwork = NetworkUtils.createNetwork();
        filter.filter(carNetwork, car);

        Network ptNetwork = NetworkUtils.createNetwork();
        filter.filter(ptNetwork, pt);

        this.carNetwork = carNetwork;
        this.ptNetwork = ptNetwork;

        this.schedule = schedule;
        final GeoData geoData = dataContainer.getGeoData();
        this.zones = geoData.getZones();
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

    public void update(Provider<TripRouter> routerProvider, TravelTime travelTime, TravelDisutility disutility) {
        this.travelTime = travelTime;
        this.travelDisutility = disutility;
        final Config config = ConfigUtils.createConfig();
        this.routerProvider = routerProvider;

        TripRouter.Builder bd = new TripRouter.Builder(config);
        bd.setRoutingModule(TransportMode.car, this.routerProvider.get().getRoutingModule(TransportMode.car));

        TeleportationRoutingModule teleportationRoutingModule =
                new TeleportationRoutingModule(
                        TransportMode.transit_walk,
                        PopulationUtils.getFactory(),
                        1.4,
                        1.3);

        if (schedule != null) {
            RaptorStaticConfig raptorConfig = RaptorUtils.createStaticConfig(config);
            raptorConfig.setOptimization(RaptorStaticConfig.RaptorOptimization.OneToAllRouting);
            SwissRailRaptorData raptorData = SwissRailRaptorData.create(schedule, raptorConfig, ptNetwork);
            SwissRailRaptor raptor = new SwissRailRaptor(
                    raptorData,
                    new DefaultRaptorParametersForPerson(config), null,
                    new DefaultRaptorStopFinder(null, new DefaultRaptorIntermodalAccessEgress(), null)
            );
            RoutingModule raptorRoutingModule =
                    new SwissRailRaptorRoutingModule(
                            raptor,
                            schedule,
                            ptNetwork,
                            teleportationRoutingModule
                    );
            bd.setRoutingModule(TransportMode.pt, raptorRoutingModule);
        } else {
            bd.setRoutingModule(TransportMode.pt, teleportationRoutingModule);
        }

        tripRouter = bd.build();
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
            originCoord = zoneConnectorManager.getCordsForZone((Zone) origin).get(0);
            destinationCoord = zoneConnectorManager.getCordsForZone((Zone) destination).get(0);
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
            IndexedDoubleMatrix2D skim = new IndexedDoubleMatrix2D(zones.values(), zones.values());
            logger.info("Calculating skim matrix for mode " + mode + " using " + Properties.get().main.numberOfThreads + " threads.");
            final int partitionSize = (int) ((double) zones.size() / (Properties.get().main.numberOfThreads)) + 1;
            logger.info("Intended size of all of partitions = " + partitionSize);
            Iterable<List<Zone>> partitions = Iterables.partition(zones.values(), partitionSize);
            ConcurrentExecutor<Void> executor = ConcurrentExecutor.fixedPoolService(Properties.get().main.numberOfThreads);

            for (final List<Zone> partition : partitions) {
                if (mode.equalsIgnoreCase(TransportMode.car)) {
                    executor.addTaskToQueue(() -> {
                        try {
                            MultiNodePathCalculator calculator
                                    = (MultiNodePathCalculator) new FastMultiNodeDijkstraFactory(true).createPathCalculator(carNetwork, travelDisutility, travelTime);

                            Set<InitialNode> toNodes = new HashSet<>();
                            for (Zone zone : zones.values()) {
                                for (Coord coord : zoneConnectorManager.getCordsForZone(zone)) {
                                    Node originNode = NetworkUtils.getNearestNode(carNetwork, coord);
                                    toNodes.add(new InitialNode(originNode, 0., 0.));
                                }
                            }

                            ImaginaryNode aggregatedToNodes = MultiNodeDijkstra.createImaginaryNode(toNodes);

                            for (Zone origin : partition) {
                                Node originNode = NetworkUtils.getNearestNode(carNetwork, zoneConnectorManager.getCordsForZone(origin).get(0));
                                calculator.calcLeastCostPath(originNode, aggregatedToNodes, Properties.get().transportModel.peakHour_s, null, null);
                                for (Zone destination : zones.values()) {
                                    Node destinationNode = NetworkUtils.getNearestNode(carNetwork, zoneConnectorManager.getCordsForZone(destination).get(0));
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
                } else if (mode.equalsIgnoreCase(TransportMode.pt) && schedule != null) {
                    Config config = ConfigUtils.createConfig();
                    RaptorStaticConfig raptorConfig = RaptorUtils.createStaticConfig(config);
                    raptorConfig.setOptimization(RaptorStaticConfig.RaptorOptimization.OneToAllRouting);
                    SwissRailRaptorData raptorData = SwissRailRaptorData.create(schedule, raptorConfig, ptNetwork);
                    final RaptorParameters parameters = RaptorUtils.createParameters(config);

                    Map<Zone, List<TransitStopFacility>> stopsPerZone = new LinkedHashMap<>();
                    for (Zone zone : zones.values()) {
                        final Coord coordinate = zoneConnectorManager.getCordsForZone(zone).get(0);
                        final Collection<TransitStopFacility> nearbyStops = raptorData.findNearbyStops(coordinate.getX(), coordinate.getY(), 1000);
                        if (!nearbyStops.isEmpty()) {
                            stopsPerZone.put(zone, new ArrayList<>(nearbyStops));
                        } else {
                            final TransitStopFacility nearestStop = raptorData.findNearestStop(coordinate.getX(), coordinate.getY());
                            stopsPerZone.put(zone, Lists.newArrayList(nearestStop));
                        }
                    }

                    executor.addTaskToQueue(() -> {
                        try {
                            SwissRailRaptor raptor = new SwissRailRaptor(raptorData, new DefaultRaptorParametersForPerson(config), null, new DefaultRaptorStopFinder(
                                    null,
                                    new DefaultRaptorIntermodalAccessEgress(),
                                    null));
                            for (Zone origin : partition) {
                                List<TransitStopFacility> nearbyStops = stopsPerZone.get(origin);
                                final Map<Id<TransitStopFacility>, SwissRailRaptorCore.TravelInfo> idTravelInfoMap = raptor.calcTree(nearbyStops, Properties.get().transportModel.peakHour_s, parameters);
                                for (Zone destination : zones.values()) {
                                    double travelTime = Double.MAX_VALUE;
                                    for (TransitStopFacility stop : stopsPerZone.get(destination)) {
                                        final SwissRailRaptorCore.TravelInfo travelInfo = idTravelInfoMap.get(stop.getId());
                                        if (travelInfo != null) {
                                            double time = travelInfo.accessTime + travelInfo.ptTravelTime + travelInfo.waitingTime;
                                            travelTime = Math.min(travelTime, time);
                                        }
                                    }
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
                } else {
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
            }
            executor.execute();
            assignIntrazonals(5, 10, 0.33f, skim);
            skimsByMode.put(mode, skim);
            logger.info("Finished skim for mode " + mode);
            return skim;
        }
    }

    @Override
    public TravelTimes duplicate() {
        logger.warn("Creating another TravelTimes object.");
        MatsimTravelTimes matsimTravelTimes = new MatsimTravelTimes(zoneConnectorMethod);
        matsimTravelTimes.carNetwork = this.carNetwork;
        matsimTravelTimes.ptNetwork = this.ptNetwork;
        matsimTravelTimes.zones = this.zones;
        matsimTravelTimes.schedule = this.schedule;
        matsimTravelTimes.zoneConnectorManager = this.zoneConnectorManager;
        matsimTravelTimes.update(routerProvider, travelTime, travelDisutility);
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