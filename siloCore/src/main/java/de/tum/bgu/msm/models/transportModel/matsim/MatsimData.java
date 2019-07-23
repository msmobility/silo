package de.tum.bgu.msm.models.transportModel.matsim;

import ch.sbb.matsim.routing.pt.raptor.*;
import com.google.common.collect.Sets;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.properties.Properties;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.TransportModeNetworkFilter;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.*;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import java.util.Collection;
import java.util.Set;

public class MatsimData {

    private LeastCostPathCalculatorFactory leastCostPathCalculatorFactory;
    private LeastCostPathCalculatorFactory multiNodeFactory = new FastMultiNodeDijkstraFactory(true);

    private SwissRailRaptorData raptorData;
    private SwissRailRaptorData raptorDataOneToAll;

    private final Properties properties;
    private Config config;

    private Network carNetwork;
    private Network ptNetwork;

    private TransitSchedule schedule;
    private RaptorParameters raptorParameters;
    private DefaultRaptorParametersForPerson parametersForPerson;
    private LeastCostRaptorRouteSelector routeSelector;
    private DefaultRaptorStopFinder defaultRaptorStopFinder;

    private TravelDisutility travelDisutility;
    private TravelTime travelTime;

    private ZoneConnectorManager zoneConnectorManager;
    private final static int NUMBER_OF_CALC_POINTS = 1;


    public MatsimData(Config config, Properties properties,
                      ZoneConnectorManager.ZoneConnectorMethod method,
                      DataContainer dataContainer) {
        this.config = config;
        this.raptorParameters = RaptorUtils.createParameters(config);
        this.properties = properties;
        final Collection<Zone> zones = dataContainer.getGeoData().getZones().values();
        switch (method) {
            case RANDOM:
                this.zoneConnectorManager = ZoneConnectorManager.createRandomZoneConnectors(zones, NUMBER_OF_CALC_POINTS);
                break;
            case WEIGHTED_BY_POPULATION:
                this.zoneConnectorManager = ZoneConnectorManager.createWeightedZoneConnectors(zones,
                        dataContainer.getRealEstateDataManager(),
                        dataContainer.getHouseholdDataManager());
                break;
            default:
                throw new RuntimeException("No valid zone connector method defined!");
        }
    }

    public ZoneConnectorManager getZoneConnectorManager() {
        return zoneConnectorManager;
    }

    public Network getCarNetwork() {
        return carNetwork;
    }

    public Network getPtNetwork() {
        return ptNetwork;
    }

    public void update(Network network, TransitSchedule schedule,
                       TravelDisutility travelDisutility, TravelTime travelTime) {
        this.travelDisutility = travelDisutility;
        this.travelTime = travelTime;
        this.schedule = schedule;

        TransportModeNetworkFilter filter = new TransportModeNetworkFilter(network);

        Set<String> car = Sets.newHashSet(TransportMode.car);
        Set<String> pt = Sets.newHashSet(TransportMode.pt);

        Network carNetwork = NetworkUtils.createNetwork();
        filter.filter(carNetwork, car);

        Network ptNetwork = NetworkUtils.createNetwork();
        filter.filter(ptNetwork, pt);

        this.carNetwork = carNetwork;
        this.ptNetwork = ptNetwork;

        this.leastCostPathCalculatorFactory = new FastAStarLandmarksFactory(properties.main.numberOfThreads);
        RaptorStaticConfig raptorConfig = RaptorUtils.createStaticConfig(config);
        raptorData = SwissRailRaptorData.create(schedule, raptorConfig, ptNetwork);

        RaptorStaticConfig raptorConfigOneToAll = RaptorUtils.createStaticConfig(config);
        raptorConfigOneToAll.setOptimization(RaptorStaticConfig.RaptorOptimization.OneToAllRouting);
        raptorDataOneToAll = SwissRailRaptorData.create(schedule, raptorConfigOneToAll, ptNetwork);

        parametersForPerson = new DefaultRaptorParametersForPerson(config);
        defaultRaptorStopFinder = new DefaultRaptorStopFinder(
                null,
                new DefaultRaptorIntermodalAccessEgress(),
                null);
        routeSelector = new LeastCostRaptorRouteSelector();
    }

    public MultiNodePathCalculator createMultiNodePathCalculator() {
        return (MultiNodePathCalculator) multiNodeFactory.createPathCalculator(carNetwork, travelDisutility, travelTime);
    }

    public TripRouter createTripRouter() {

        final RoutingModule networkRoutingModule = DefaultRoutingModules.createPureNetworkRouter(
                TransportMode.car, PopulationUtils.getFactory(), carNetwork, leastCostPathCalculatorFactory.createPathCalculator(carNetwork, travelDisutility, travelTime));
        final RoutingModule teleportationRoutingModule = DefaultRoutingModules.createTeleportationRouter(
                TransportMode.walk, PopulationUtils.getFactory(), config.plansCalcRoute().getOrCreateModeRoutingParams(TransportMode.walk));
        final RoutingModule ptRoutingModule;

        if (schedule != null) {
            final SwissRailRaptor swissRailRaptor = createSwissRailRaptor(RaptorStaticConfig.RaptorOptimization.OneToOneRouting);
            ptRoutingModule = new SwissRailRaptorRoutingModule(swissRailRaptor, schedule, ptNetwork, teleportationRoutingModule);
        } else {
            ptRoutingModule = teleportationRoutingModule;
        }

        TripRouter.Builder bd = new TripRouter.Builder(config);
        bd.setRoutingModule(TransportMode.car, networkRoutingModule);
        bd.setRoutingModule(TransportMode.pt, ptRoutingModule);
        return bd.build();
    }

    public SwissRailRaptor createSwissRailRaptor(RaptorStaticConfig.RaptorOptimization optimitzaion) {
        switch (optimitzaion) {
            case OneToAllRouting:
                return new SwissRailRaptor(raptorDataOneToAll, parametersForPerson, routeSelector, defaultRaptorStopFinder);
            case OneToOneRouting:
                return new SwissRailRaptor(raptorData, parametersForPerson, routeSelector, defaultRaptorStopFinder);
            default:
                throw new RuntimeException("Unrecognized raptor optimization!");
        }
    }

    public LeastCostPathCalculator createLeastCostPathCalculator() {
        return leastCostPathCalculatorFactory.createPathCalculator(carNetwork, travelDisutility, travelTime);
    }

    public SwissRailRaptorData getRaptorData(RaptorStaticConfig.RaptorOptimization optimitzaion) {
        switch (optimitzaion) {
            case OneToAllRouting:
                return raptorDataOneToAll;
            case OneToOneRouting:
                return raptorData;
            default:
                throw new RuntimeException("Unrecognized raptor optimization!");
        }
    }

    public RaptorParameters getRaptorParameters() {
        return raptorParameters;
    }

}
