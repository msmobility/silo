package de.tum.bgu.msm.transportModel.matsim;

import ch.sbb.matsim.routing.pt.raptor.*;
import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.*;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacilitiesFactory;
import org.matsim.facilities.ActivityFacilitiesFactoryImpl;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.Facility;
import org.matsim.pt.PtConstants;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import javax.inject.Provider;
import java.util.List;
import java.util.Map;

public class SBBTest {

    @Ignore
    @Test
    public void test() {
        Config config = ConfigUtils.createConfig();

        Scenario scenario = ScenarioUtils.createScenario(config);

        new MatsimNetworkReader(scenario.getNetwork()).readFile("C:\\Users\\Nico\\tum\\fabilut\\transitInput\\network2018.xml");
        new TransitScheduleReader(scenario).readFile("C:\\Users\\Nico\\tum\\fabilut\\transitInput\\schedule2018.xml");

//        Provider<RaptorStopFinder> provider = () ->
//                new DefaultRaptorStopFinder(
//                        null,
//                        new DefaultRaptorIntermodalAccessEgress(),
//                        null);
//
//        Population pop = PopulationUtils.createPopulation(config);
//        final SwissRailRaptorFactory swissRailRaptorFactory = new SwissRailRaptorFactory(
//                scenario.getTransitSchedule(),
//                config,
//                scenario.getNetwork(),
//                new DefaultRaptorParametersForPerson(config),
//                new LeastCostRaptorRouteSelector(),
//                provider,
//                config.plans(),
//                pop,
//                null
//        );
//
//        TripRouter.Builder bd = new TripRouter.Builder(ConfigUtils.createConfig());
//        TeleportationRoutingModule teleportationRoutingModule =
//                new TeleportationRoutingModule(
//                        TransportMode.transit_walk,
//                        PopulationUtils.getFactory(),
//                        1.4,
//                        1.3);
//        RoutingModule routingModule =
//                new SwissRailRaptorRoutingModule(
//                        swissRailRaptorFactory.get(),
//                        scenario.getTransitSchedule(),
//                        scenario.getNetwork(),
//                        teleportationRoutingModule);
//        bd.setRoutingModule(TransportMode.pt, routingModule);
//

        Population population = PopulationUtils.createPopulation(config);
        TripRouter.Builder bd = new TripRouter.Builder(config);

//        TeleportationRoutingModule teleportationRoutingModule = new TeleportationRoutingModule(TransportMode.pt, PopulationUtils.getFactory(), 10, 1.3);
//        bd.setRoutingModule(TransportMode.pt, teleportationRoutingModule);


//        TeleportationRoutingModule teleportationRoutingModule =
//                new TeleportationRoutingModule(
//                        TransportMode.transit_walk,
//                        PopulationUtils.getFactory(),
//                        1.4,
//                        1.3);

        RaptorStaticConfig raptorConfig = RaptorUtils.createStaticConfig(config);
        raptorConfig.setOptimization(RaptorStaticConfig.RaptorOptimization.OneToAllRouting);
        SwissRailRaptorData raptorData = SwissRailRaptorData.create(scenario.getTransitSchedule(),
                null,  raptorConfig, scenario.getNetwork(), null);
        final DefaultRaptorStopFinder stopFinder = new DefaultRaptorStopFinder(
                null,
                new DefaultRaptorIntermodalAccessEgress(),
                null);
        SwissRailRaptor raptor = new SwissRailRaptor(raptorData,

                new DefaultRaptorParametersForPerson(config), null, stopFinder,
                new DefaultRaptorInVehicleCostCalculator(), new DefaultRaptorTransferCostCalculator());

//        RoutingModule routingModule =
//                new SwissRailRaptorRoutingModule(
//                        raptor,
//                        scenario.getTransitSchedule(),
//                        scenario.getNetwork(),
//                        teleportationRoutingModule);
//        bd.setRoutingModule(TransportMode.pt, routingModule);


        final Coord originCoord = new Coord(4436689.657372447, 5368527.815536651);
        ActivityFacilitiesFactoryImpl activityFacilitiesFactory = new ActivityFacilitiesFactoryImpl();
        Facility fromFacility = ((ActivityFacilitiesFactory) activityFacilitiesFactory).createActivityFacility(Id.create(1, ActivityFacility.class), originCoord);

        long time = System.currentTimeMillis();
        final Map<Id<TransitStopFacility>, SwissRailRaptorCore.TravelInfo> idTravelInfoMap = raptor.calcTree(fromFacility, 28800, null, null );

        System.out.println(System.currentTimeMillis() - time);

        TripRouter router = bd.build();

//        final Coord destinationCoord = new Coord(4489369.625538794, 5294502.251605561);
//
//        Facility toFacility = ((ActivityFacilitiesFactory) activityFacilitiesFactory).createActivityFacility(Id.create(2, ActivityFacility.class), destinationCoord);
//
//        long timeQuery = System.currentTimeMillis();
//
//        double time = 0;
//        List<? extends PlanElement> planElements = router.calcRoute(TransportMode.pt, fromFacility, toFacility, 28800, null);
//        System.out.println(System.currentTimeMillis()-timeQuery);
//
//        for (PlanElement e : planElements) {
//            if (e instanceof Leg) {
//                time += ((Leg) e).getTravelTime();
//            } else if (e instanceof Activity) {
//                if (((Activity) e).getType().equalsIgnoreCase(PtConstants.TRANSIT_ACTIVITY_TYPE)) {
//                    time += ((Activity) e).getEndTime() - ((Activity) e).getStartTime();
//                }
//            }
//        }


    }
}
