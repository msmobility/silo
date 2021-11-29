package de.tum.bgu.msm.matsim;

import ch.sbb.matsim.routing.pt.raptor.*;
import com.google.common.collect.Sets;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.properties.Properties;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.mobsim.qsim.pt.TransitVehicle;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.TransportModeNetworkFilter;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.*;
import org.matsim.core.router.costcalculators.FreespeedTravelTimeAndDisutility;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.timing.TimeInterpretation;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.vehicles.Vehicles;

import java.util.Collection;
import java.util.Set;

public final class MatsimData {

    private LeastCostPathCalculatorFactory leastCostPathCalculatorFactory;
    private LeastCostPathCalculatorFactory multiNodeFactory = new FastMultiNodeDijkstraFactory(true);

    private SwissRailRaptorData raptorData;
    private SwissRailRaptorData raptorDataOneToAll;

    private final int nThreads;
    private Config config;

    private Network carNetwork;
    private Network ptNetwork;
    private final TransitSchedule schedule;

    private RaptorParameters raptorParameters;
    private DefaultRaptorParametersForPerson parametersForPerson;
    private LeastCostRaptorRouteSelector routeSelector;
    private DefaultRaptorStopFinder defaultRaptorStopFinder;

    private TravelDisutility travelDisutility;
    private TravelTime travelTime;
    private Population matsimPopulation;

    private ZoneConnectorManager zoneConnectorManager;
    private final static int NUMBER_OF_CALC_POINTS = 1;

    public MatsimData(Config config, Properties properties,
                      ZoneConnectorManagerImpl.ZoneConnectorMethod method,
                      DataContainer dataContainer, Network network, TransitSchedule schedule) {
        int threads = properties.main.numberOfThreads;
        final Collection<Zone> zones = dataContainer.getGeoData().getZones().values();
        ZoneConnectorManager zoneConnectorManager;
        switch (method) {
            case RANDOM:
                zoneConnectorManager = ZoneConnectorManagerImpl.createRandomZoneConnectors(zones, NUMBER_OF_CALC_POINTS);
                break;
            case WEIGHTED_BY_POPULATION:
                zoneConnectorManager = ZoneConnectorManagerImpl.createWeightedZoneConnectors(zones,
                        dataContainer.getRealEstateDataManager(),
                        dataContainer.getHouseholdDataManager());
                break;
            default:
                throw new RuntimeException("No valid zone connector method defined!");
        }

        ConfigUtils.setVspDefaults(config); // Needs to be done before config becomes locked for those changes
        this.config = config;
        this.raptorParameters = RaptorUtils.createParameters(config);
        this.nThreads = threads;
        this.schedule = schedule;
        this.zoneConnectorManager = zoneConnectorManager;
        filterNetwork(network);
    }

    public MatsimData(Config config, int threads,Network network,
                      TransitSchedule schedule, ZoneConnectorManager zoneConnectorManager) {
        ConfigUtils.setVspDefaults(config); // Needs to be done before config becomes locked for those changes
        this.config = config;
        this.raptorParameters = RaptorUtils.createParameters(config);
        this.nThreads = threads;
        this.schedule = schedule;
        this.zoneConnectorManager = zoneConnectorManager;
        filterNetwork(network);
    }

    public void filterNetwork(Network network) {
        TransportModeNetworkFilter filter = new TransportModeNetworkFilter(network);

        Set<String> car = Sets.newHashSet(TransportMode.car);
        Set<String> pt = Sets.newHashSet(TransportMode.pt, TransportMode.train, "bus",
                "artificial", "subway", "tram", "rail");

        Network carNetwork = NetworkUtils.createNetwork();
        filter.filter(carNetwork, car);

        Network ptNetwork = NetworkUtils.createNetwork();
        filter.filter(ptNetwork, pt);

        this.carNetwork = carNetwork;
        this.ptNetwork = ptNetwork;
    }

    ZoneConnectorManager getZoneConnectorManager() {
        return zoneConnectorManager;
    }

    public Network getCarNetwork() {
        return carNetwork;
    }

    Network getPtNetwork() {
        return ptNetwork;
    }

    public void update(TravelDisutility travelDisutility, TravelTime travelTime) {
        this.travelDisutility = travelDisutility;
        this.travelTime = travelTime;

        this.leastCostPathCalculatorFactory = new FastAStarLandmarksFactory(nThreads);

        if (config.transit().isUseTransit() && schedule != null) {
            RaptorStaticConfig raptorConfig = RaptorUtils.createStaticConfig(config);
            Vehicles ptVehicles = null;
            OccupancyData occupancyData = null;
            raptorData = SwissRailRaptorData.create(schedule, ptVehicles, raptorConfig, ptNetwork, occupancyData);

            RaptorStaticConfig raptorConfigOneToAll = RaptorUtils.createStaticConfig(config);
            raptorConfigOneToAll.setOptimization(RaptorStaticConfig.RaptorOptimization.OneToAllRouting);
            raptorDataOneToAll = SwissRailRaptorData.create(schedule, ptVehicles, raptorConfig, ptNetwork, occupancyData);

            parametersForPerson = new DefaultRaptorParametersForPerson(config);
            defaultRaptorStopFinder = new DefaultRaptorStopFinder(
                    config,
                    new DefaultRaptorIntermodalAccessEgress(),
                    null);
            routeSelector = new LeastCostRaptorRouteSelector();
        }
    }

    MultiNodePathCalculator createMultiNodePathCalculator() {
        return (MultiNodePathCalculator) multiNodeFactory.createPathCalculator(carNetwork, travelDisutility, travelTime);
    }

    MultiNodePathCalculator createFreeSpeedMultiNodePathCalculator() {
        FreespeedTravelTimeAndDisutility freespeed = new FreespeedTravelTimeAndDisutility(config.planCalcScore());
        return (MultiNodePathCalculator) multiNodeFactory.createPathCalculator(carNetwork, freespeed, freespeed);
    }

    TripRouter createTripRouter() {
        Scenario scenario = ScenarioUtils.loadScenario(config);
        RoutingModule accessEgressToNetworkRouter = DefaultRoutingModules.createTeleportationRouter(TransportMode.walk, scenario, config.plansCalcRoute().getModeRoutingParams().get(TransportMode.walk));

        RoutingModule carRoutingModule;
        LeastCostPathCalculator routeAlgo = leastCostPathCalculatorFactory.createPathCalculator(carNetwork, travelDisutility, travelTime);
//        if (config.plansCalcRoute().isInsertingAccessEgressWalk()) { // in matsim-12
        if ( !config.plansCalcRoute().getAccessEgressType().equals(PlansCalcRouteConfigGroup.AccessEgressType.none) ) { // in matsim-13-w37
            carRoutingModule = DefaultRoutingModules.createAccessEgressNetworkRouter(
                    TransportMode.car, routeAlgo, scenario, carNetwork, accessEgressToNetworkRouter,
                    accessEgressToNetworkRouter, TimeInterpretation.create(scenario.getConfig())); // TODO take access egress type correctly
        } else {
            carRoutingModule = DefaultRoutingModules.createPureNetworkRouter(
                    TransportMode.car, PopulationUtils.getFactory(), carNetwork, routeAlgo);
        }

        final RoutingModule ptRoutingModule;
        if (schedule != null && config.transit().isUseTransit()) {
            final SwissRailRaptor swissRailRaptor = createSwissRailRaptor(RaptorStaticConfig.RaptorOptimization.OneToOneRouting);
            ptRoutingModule = new SwissRailRaptorRoutingModule(swissRailRaptor, schedule, ptNetwork, accessEgressToNetworkRouter);
        } else {
            ptRoutingModule = DefaultRoutingModules.createPseudoTransitRouter(TransportMode.pt, PopulationUtils.getFactory(), carNetwork,
                    leastCostPathCalculatorFactory.createPathCalculator(carNetwork, travelDisutility, travelTime), config.plansCalcRoute().getOrCreateModeRoutingParams(TransportMode.pt));
        }

        TripRouter.Builder bd = new TripRouter.Builder(config);
        bd.setRoutingModule(TransportMode.car, carRoutingModule);
        bd.setRoutingModule(TransportMode.pt, ptRoutingModule);
        return bd.build();
    }

    SwissRailRaptor createSwissRailRaptor(RaptorStaticConfig.RaptorOptimization optimitzaion) {


        switch (optimitzaion) {
            case OneToAllRouting:
                return new SwissRailRaptor(raptorDataOneToAll, parametersForPerson, routeSelector, defaultRaptorStopFinder,
                        new DefaultRaptorInVehicleCostCalculator(), new DefaultRaptorTransferCostCalculator());
            case OneToOneRouting:
                return new SwissRailRaptor(raptorData, parametersForPerson, routeSelector, defaultRaptorStopFinder,
                        new DefaultRaptorInVehicleCostCalculator(), new DefaultRaptorTransferCostCalculator());
            default:
                throw new RuntimeException("Unrecognized raptor optimization!");
        }
    }

    LeastCostPathCalculator createLeastCostPathCalculator() {
        return leastCostPathCalculatorFactory.createPathCalculator(carNetwork, travelDisutility, travelTime);
    }

    RoutingModule getTeleportationRouter(String mode) {
        Scenario scenario = ScenarioUtils.loadScenario(config);
        return DefaultRoutingModules.createTeleportationRouter(
//                mode, PopulationUtils.getFactory(), config.plansCalcRoute().getOrCreateModeRoutingParams(mode));
                mode, scenario, config.plansCalcRoute().getModeRoutingParams().get(mode));
    }

    SwissRailRaptorData getRaptorData(RaptorStaticConfig.RaptorOptimization optimization) {
        switch (optimization) {
            case OneToAllRouting:
                return raptorDataOneToAll;
            case OneToOneRouting:
                return raptorData;
            default:
                throw new RuntimeException("Unrecognized raptor optimization!");
        }
    }

    RaptorParameters getRaptorParameters() {
        return raptorParameters;
    }

    public TransitSchedule getSchedule() {
        return schedule;
    }

    public Population getMatsimPopulation() {return matsimPopulation; }

    public void updateMatsimPopulation(Population matsimPopulation) {
        this.matsimPopulation = matsimPopulation;
    }
}
