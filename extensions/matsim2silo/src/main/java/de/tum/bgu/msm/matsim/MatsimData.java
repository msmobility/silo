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
import org.matsim.core.controler.Injector;
import org.matsim.core.controler.PrepareForSim;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.TransportModeNetworkFilter;
import org.matsim.core.router.*;
import org.matsim.core.router.costcalculators.FreespeedTravelTimeAndDisutility;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.vehicles.Vehicles;

import java.util.Collection;
import java.util.Set;

public final class MatsimData {

	private MutableScenario scenario;
	private LeastCostPathCalculatorFactory leastCostPathCalculatorFactory;
    private final LeastCostPathCalculatorFactory multiNodeFactory = new FastMultiNodeDijkstraFactory(true);

    private SwissRailRaptorData raptorData;
    private SwissRailRaptorData raptorDataOneToAll;

    private final int nThreads;

	private Network carNetwork;
    private Network ptNetwork;

	private RaptorParameters raptorParameters;
    private DefaultRaptorParametersForPerson parametersForPerson;
    private LeastCostRaptorRouteSelector routeSelector;
    private DefaultRaptorStopFinder defaultRaptorStopFinder;

    private TravelDisutility travelDisutility;
    private TravelTime travelTime;

    private final ZoneConnectorManager zoneConnectorManager;
    private final static int NUMBER_OF_CALC_POINTS = 1;

	// yyyyyy The TripRouter needs the vehicles container to work properly.  This is provided to the TripRouter via the Scenario.  In consequence, we need to maintain a somewhat
	// consistent scenario.

	public MatsimData( Properties properties, ZoneConnectorManager.ZoneConnectorMethod method, DataContainer dataContainer, MutableScenario scenario ) {
		this( scenario.getConfig(), properties, method, dataContainer, scenario.getNetwork(), scenario.getTransitSchedule() );
		this.scenario = scenario;
		// yyyyyy This now has (in some execution paths) the full matsim scenario.  However, the scenario elements from this class here (such as network, transitSchedule, etc.) are
		// provided separately, and are in most cases not consistent with the contents of the matsim scenario.
	}

	/**
	 * @deprecated -- use {@link MatsimData#MatsimData(Properties, ZoneConnectorManager.ZoneConnectorMethod, DataContainer, MutableScenario)}
	 */
	@Deprecated
    public MatsimData(Config config, Properties properties, ZoneConnectorManagerImpl.ZoneConnectorMethod method, DataContainer dataContainer, Network network, TransitSchedule schedule) {
		this.scenario = ScenarioUtils.createMutableScenario( config );
		this.scenario.setTransitSchedule( schedule );

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
		this.raptorParameters = RaptorUtils.createParameters(config);
        this.nThreads = threads;
		this.zoneConnectorManager = zoneConnectorManager;
        filterNetwork(network);
    }

	/**
	 * @deprecated -- use {@link MatsimData#MatsimData(Properties, ZoneConnectorManager.ZoneConnectorMethod, DataContainer, MutableScenario)}
	 */
	@Deprecated
    public MatsimData(Config config, Properties properties, ZoneConnectorManagerImpl.ZoneConnectorMethod method, DataContainer dataContainer, Network network) {
		this.scenario = ScenarioUtils.createMutableScenario( config );

		// yyyyyy MatsimData needs the scenario, everything else is stupid.  kai

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
		this.nThreads = threads;
		this.zoneConnectorManager = zoneConnectorManager;
        filterNetwork(network);
    }

	/**
	 * @deprecated -- use {@link MatsimData#MatsimData(Properties, ZoneConnectorManager.ZoneConnectorMethod, DataContainer, MutableScenario)}
	 */
	@Deprecated
    public MatsimData(Config config, int threads,Network network, TransitSchedule schedule, ZoneConnectorManager zoneConnectorManager) {
		this.scenario = ScenarioUtils.createMutableScenario( config );
		this.scenario.setTransitSchedule( schedule );

        ConfigUtils.setVspDefaults(config); // Needs to be done before config becomes locked for those changes
		this.raptorParameters = RaptorUtils.createParameters(config);
        this.nThreads = threads;
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

//    Network getPtNetwork() {
//        return ptNetwork;
//    }
	// never used

    public void update(TravelDisutility travelDisutility, TravelTime travelTime) {
        this.travelDisutility = travelDisutility;
        this.travelTime = travelTime;

        this.leastCostPathCalculatorFactory = new AStarLandmarksFactory(nThreads);

        if ( this.scenario.getConfig().transit().isUseTransit() && this.scenario.getTransitSchedule() != null) {
            RaptorStaticConfig raptorConfig = RaptorUtils.createStaticConfig( this.scenario.getConfig() );
            Vehicles ptVehicles = null;
            OccupancyData occupancyData = null;
            raptorData = SwissRailRaptorData.create( this.scenario.getTransitSchedule(), ptVehicles, raptorConfig, ptNetwork, occupancyData );

            RaptorStaticConfig raptorConfigOneToAll = RaptorUtils.createStaticConfig( this.scenario.getConfig() );
            raptorConfigOneToAll.setOptimization(RaptorStaticConfig.RaptorOptimization.OneToAllRouting);
            raptorDataOneToAll = SwissRailRaptorData.create( this.scenario.getTransitSchedule(), ptVehicles, raptorConfig, ptNetwork, occupancyData );

            parametersForPerson = new DefaultRaptorParametersForPerson( this.scenario.getConfig() );
            defaultRaptorStopFinder = new DefaultRaptorStopFinder(
					this.scenario.getConfig(),
                    new DefaultRaptorIntermodalAccessEgress(),
                    null);
            routeSelector = new LeastCostRaptorRouteSelector();
        }
    }

    MultiNodePathCalculator createMultiNodePathCalculator() {
        return (MultiNodePathCalculator) multiNodeFactory.createPathCalculator(carNetwork, travelDisutility, travelTime);
    }

    MultiNodePathCalculator createFreeSpeedMultiNodePathCalculator() {
        FreespeedTravelTimeAndDisutility freespeed = new FreespeedTravelTimeAndDisutility( this.scenario.getConfig().scoring());
        return (MultiNodePathCalculator) multiNodeFactory.createPathCalculator(carNetwork, freespeed, freespeed);
    }

    TripRouter createTripRouter() {
//        Scenario scenario = ScenarioUtils.loadScenario(config);

		com.google.inject.Injector injector = Injector.createMinimalMatsimInjector( this.scenario.getConfig(), scenario );

//		injector.getInstance( PrepareForSim.class ).run();
		// yyyyyy despite this line, it does not find the vehicle.  I think that the person to which the tripRouter is applied is not the same as the one that is in ths scenario. kai
		// yyyy also, I think that the matsim-silo implementation uses a different (older) convention to set the vehicle IDs.

		return injector.getInstance( TripRouter.class );


//        RoutingModule accessEgressToNetworkRouter = DefaultRoutingModules.createTeleportationRouter(TransportMode.walk, scenario, config.routing().getModeRoutingParams().get(TransportMode.walk));
//
//        RoutingModule carRoutingModule;
//        LeastCostPathCalculator routeAlgo = leastCostPathCalculatorFactory.createPathCalculator(carNetwork, travelDisutility, travelTime);
////        if (config.plansCalcRoute().isInsertingAccessEgressWalk()) { // in matsim-12
//        if ( !config.routing().getAccessEgressType().equals(RoutingConfigGroup.AccessEgressType.none) ) { // in matsim-13-w37
//
//			MultimodalLinkChooser linkChooser = RouterUtils.getMultimodalLinkChooserDefault();
//			carRoutingModule = DefaultRoutingModules.createAccessEgressNetworkRouter(TransportMode.car, routeAlgo, scenario, carNetwork, accessEgressToNetworkRouter, TimeInterpretation.create(scenario.getConfig() ),linkChooser );
//			// TODO take access egress type correctly
//			// should use injection!!
//        } else {
//            carRoutingModule = DefaultRoutingModules.createPureNetworkRouter(
//                    TransportMode.car, PopulationUtils.getFactory(), carNetwork, routeAlgo);
//        }
//
//        final RoutingModule ptRoutingModule;
//        if (schedule != null && config.transit().isUseTransit()) {
//            final SwissRailRaptor swissRailRaptor = createSwissRailRaptor(RaptorStaticConfig.RaptorOptimization.OneToOneRouting);
//            ptRoutingModule = new SwissRailRaptorRoutingModule(swissRailRaptor, schedule, ptNetwork, accessEgressToNetworkRouter);
//        } else {
//            ptRoutingModule = DefaultRoutingModules.createPseudoTransitRouter(TransportMode.pt, PopulationUtils.getFactory(), carNetwork,
//                    leastCostPathCalculatorFactory.createPathCalculator(carNetwork, travelDisutility, travelTime), config.routing().getOrCreateModeRoutingParams(TransportMode.pt));
//        }
//
//        TripRouter.Builder bd = new TripRouter.Builder(config);
//        bd.setRoutingModule(TransportMode.car, carRoutingModule);
//        bd.setRoutingModule(TransportMode.pt, ptRoutingModule);
//        return bd.build();
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
        Scenario scenario = ScenarioUtils.loadScenario( this.scenario.getConfig() );
        return DefaultRoutingModules.createTeleportationRouter(
//                mode, PopulationUtils.getFactory(), config.plansCalcRoute().getOrCreateModeRoutingParams(mode));
                mode, scenario, this.scenario.getConfig().routing().getModeRoutingParams().get(mode ) );
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

    public void updateMatsimPopulation(Population matsimPopulation) {
		this.scenario.setPopulation( matsimPopulation );
    }
//	public void updateMatsimVehicles( Vehicles vehicles ) {
//		this.scenario.setVehicles( vehicles );
//	}
	// yyyyyy forgotten method in matsim api :-(

	// === only pure getters below here

	public Config getConfig() {
		return this.scenario.getConfig();
	}
	public Scenario getScenario(){
		return scenario;
	}
	public Population getMatsimPopulation() {
		return this.scenario.getPopulation();
	}
//	public TransitSchedule getSchedule() {
//		// where is this needed?  The router is plugged together in the present class ...
//		return this.scenario.getTransitSchedule();
//	}

}
