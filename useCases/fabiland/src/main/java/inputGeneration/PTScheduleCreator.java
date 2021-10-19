package inputGeneration;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.*;
import org.matsim.pt.utils.CreatePseudoNetwork;
import org.matsim.pt.utils.CreateVehiclesForSchedule;
import org.matsim.pt.utils.TransitScheduleValidator;
import org.matsim.vehicles.VehicleWriterV1;

import java.util.Arrays;
import java.util.List;

public class PTScheduleCreator {
	
	private final static Logger LOG = Logger.getLogger(PTScheduleCreator.class);

	private static final double PT_HEADWAY = 10 * 60.;
	private static final double PT_OPERATION_START_TIME = 6 * 3600.;
	private static final double PT_OPERATION_END_TIME = 24 * 3600.;

	public static void main(String[] args) {
	    String scenarioName = "_2-l_x_lh";
        //String scenarioName = "_2-l_u";
        //String scenarioName = "_1-l_lower-u";
        //String scenarioName = "_1-l_upper-u";
        //String scenarioName = "_1-l_ne";
        //String scenarioName = "_1-l_nes";
        //String scenarioName = "_1-l_ring";

        String inputNetwork = "useCases/fabiland/scenario/matsimInput/nw_cap240.xml";
        String outputNetwork = "useCases/fabiland/scenario/matsimInput/nw_cap240" + scenarioName + ".xml";
        String outputPTSchedule = "useCases/fabiland/scenario/matsimInput/ts" + scenarioName + ".xml";
        String outputPTVehicles = "useCases/fabiland/scenario/matsimInput/tv" + scenarioName + ".xml";

        createSchedule(inputNetwork, outputNetwork, outputPTSchedule, outputPTVehicles);
    }

    private static void createSchedule(String inputNetwork, String outputNetwork, String outputPTSchedule, String outputPTVehicles) {
        Scenario scenario = ScenarioUtils.loadScenario(ConfigUtils.createConfig());

        MatsimNetworkReader matsimNetworkReader = new MatsimNetworkReader(scenario.getNetwork());
        matsimNetworkReader.readFile(inputNetwork);

        TransitSchedule ts = scenario.getTransitSchedule();
        TransitScheduleFactory tsf = ts.getFactory();

        Network network = scenario.getNetwork();

        final Node node1 = network.getNodes().get(Id.createNodeId("1"));
        final TransitStopFacility stopFac1 = tsf.createTransitStopFacility(Id.create(node1.getId(), TransitStopFacility.class), node1.getCoord(), false);

        final Node node2 = network.getNodes().get(Id.createNodeId("2"));
        final TransitStopFacility stopFac2 = tsf.createTransitStopFacility(Id.create(node2.getId(), TransitStopFacility.class), node2.getCoord(), false);

        final Node node3 = network.getNodes().get(Id.createNodeId("3"));
        final TransitStopFacility stopFac3 = tsf.createTransitStopFacility(Id.create(node3.getId(), TransitStopFacility.class), node3.getCoord(), false);

        final Node node4 = network.getNodes().get(Id.createNodeId("4"));
        final TransitStopFacility stopFac4 = tsf.createTransitStopFacility(Id.create(node4.getId(), TransitStopFacility.class), node4.getCoord(), false);

        final Node node5 = network.getNodes().get(Id.createNodeId("5"));
        final TransitStopFacility stopFac5 = tsf.createTransitStopFacility(Id.create(node5.getId(), TransitStopFacility.class), node5.getCoord(), false);

        final Node node6 = network.getNodes().get(Id.createNodeId("6"));
        final TransitStopFacility stopFac6 = tsf.createTransitStopFacility(Id.create(node6.getId(), TransitStopFacility.class), node6.getCoord(), false);

        final Node node7 = network.getNodes().get(Id.createNodeId("7"));
        final TransitStopFacility stopFac7 = tsf.createTransitStopFacility(Id.create(node7.getId(), TransitStopFacility.class), node7.getCoord(), false);

        final Node node9 = network.getNodes().get(Id.createNodeId("9"));
        final TransitStopFacility stopFac9 = tsf.createTransitStopFacility(Id.create(node9.getId(), TransitStopFacility.class), node9.getCoord(), false);

        final Node node10 = network.getNodes().get(Id.createNodeId("10"));
        final TransitStopFacility stopFac10 = tsf.createTransitStopFacility(Id.create(node10.getId(), TransitStopFacility.class), node10.getCoord(), false);

        final Node node11 = network.getNodes().get(Id.createNodeId("11"));
        final TransitStopFacility stopFac11 = tsf.createTransitStopFacility(Id.create(node11.getId(), TransitStopFacility.class), node11.getCoord(), false);

        final Node node12 = network.getNodes().get(Id.createNodeId("12"));
        final TransitStopFacility stopFac12 = tsf.createTransitStopFacility(Id.create(node12.getId(), TransitStopFacility.class), node12.getCoord(), false);

        final Node node13 = network.getNodes().get(Id.createNodeId("13"));
        final TransitStopFacility stopFac13 = tsf.createTransitStopFacility(Id.create(node13.getId(), TransitStopFacility.class), node13.getCoord(), false);

        final Node node14 = network.getNodes().get(Id.createNodeId("14"));
        final TransitStopFacility stopFac14 = tsf.createTransitStopFacility(Id.create(node14.getId(), TransitStopFacility.class), node14.getCoord(), false);

        final Node node15 = network.getNodes().get(Id.createNodeId("15"));
        final TransitStopFacility stopFac15 = tsf.createTransitStopFacility(Id.create(node15.getId(), TransitStopFacility.class), node15.getCoord(), false);

        final Node node16 = network.getNodes().get(Id.createNodeId("16"));
        final TransitStopFacility stopFac16 = tsf.createTransitStopFacility(Id.create(node16.getId(), TransitStopFacility.class), node16.getCoord(), false);

        final Node node17 = network.getNodes().get(Id.createNodeId("17"));
        final TransitStopFacility stopFac17 = tsf.createTransitStopFacility(Id.create(node17.getId(), TransitStopFacility.class), node17.getCoord(), false);

        final Node node19 = network.getNodes().get(Id.createNodeId("19"));
        final TransitStopFacility stopFac19 = tsf.createTransitStopFacility(Id.create(node19.getId(), TransitStopFacility.class), node19.getCoord(), false);

        final Node node20 = network.getNodes().get(Id.createNodeId("20"));
        final TransitStopFacility stopFac20 = tsf.createTransitStopFacility(Id.create(node20.getId(), TransitStopFacility.class), node20.getCoord(), false);

        final Node node21 = network.getNodes().get(Id.createNodeId("21"));
        final TransitStopFacility stopFac21 = tsf.createTransitStopFacility(Id.create(node21.getId(), TransitStopFacility.class), node21.getCoord(), false);

        final Node node22 = network.getNodes().get(Id.createNodeId("22"));
        final TransitStopFacility stopFac22 = tsf.createTransitStopFacility(Id.create(node22.getId(), TransitStopFacility.class), node22.getCoord(), false);

        final Node node23 = network.getNodes().get(Id.createNodeId("23"));
        final TransitStopFacility stopFac23 = tsf.createTransitStopFacility(Id.create(node23.getId(), TransitStopFacility.class), node23.getCoord(), false);

        final Node node24 = network.getNodes().get(Id.createNodeId("24"));
        final TransitStopFacility stopFac24 = tsf.createTransitStopFacility(Id.create(node24.getId(), TransitStopFacility.class), node24.getCoord(), false);

        final Node node25 = network.getNodes().get(Id.createNodeId("25"));
        final TransitStopFacility stopFac25 = tsf.createTransitStopFacility(Id.create(node25.getId(), TransitStopFacility.class), node25.getCoord(), false);

        ts.addStopFacility(stopFac1);
//        ts.addStopFacility(stopFac2); // only ring
//        ts.addStopFacility(stopFac3); // only ring
//        ts.addStopFacility(stopFac4); // only ring
        ts.addStopFacility(stopFac5);
        ts.addStopFacility(stopFac6); // not in X
//        ts.addStopFacility(stopFac7); // not in U
//        ts.addStopFacility(stopFac9); // not in U
        ts.addStopFacility(stopFac10); // not in X
        ts.addStopFacility(stopFac11); // not in X
        ts.addStopFacility(stopFac12); // not in X
        ts.addStopFacility(stopFac13);
        ts.addStopFacility(stopFac14); // not in X
        ts.addStopFacility(stopFac15); // not in X
//        ts.addStopFacility(stopFac16); // not in X
//        ts.addStopFacility(stopFac17); // not in U
//        ts.addStopFacility(stopFac19); // not in U
//        ts.addStopFacility(stopFac20); // not in X
//        ts.addStopFacility(stopFac21);
//        ts.addStopFacility(stopFac22); // only ring
//        ts.addStopFacility(stopFac23); // only ring
//        ts.addStopFacility(stopFac24); // only ring
//        ts.addStopFacility(stopFac25);

        double tt = 360.;
        double tt2 = 500.;
        // double tt = 0.;
        // double tt2 = 0.;

        // Lower U
//        TransitLine tlLowerU = ts.getFactory().createTransitLine(Id.create("lower-u", TransitLine.class));
//        {
//            TransitRouteStop stop21 = ts.getFactory().createTransitRouteStop(stopFac21, 0 * tt, 0 * tt);
//            TransitRouteStop stop16 = ts.getFactory().createTransitRouteStop(stopFac16, 1 * tt, 1 * tt);
//            TransitRouteStop stop11 = ts.getFactory().createTransitRouteStop(stopFac11, 2 * tt, 2 * tt);
//            TransitRouteStop stop12 = ts.getFactory().createTransitRouteStop(stopFac12, 3 * tt, 3 * tt);
//            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 4 * tt, 4 * tt);
//            TransitRouteStop stop14 = ts.getFactory().createTransitRouteStop(stopFac14, 5 * tt, 5 * tt);
//            TransitRouteStop stop15 = ts.getFactory().createTransitRouteStop(stopFac15, 6 * tt, 6 * tt);
//            TransitRouteStop stop20 = ts.getFactory().createTransitRouteStop(stopFac20, 7 * tt, 7 * tt);
//            TransitRouteStop stop25 = ts.getFactory().createTransitRouteStop(stopFac25, 8 * tt, 8 * tt);
//
//            List<TransitRouteStop> stops = Arrays.asList(stop21, stop16, stop11, stop12, stop13, stop14, stop15, stop20, stop25);
//            TransitRoute tr = tsf.createTransitRoute(Id.create("lower-u-1", TransitRoute.class), null, stops, TransportMode.pt);
//            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
//            tlLowerU.addRoute(tr);
//        }{
//            TransitRouteStop stop25 = ts.getFactory().createTransitRouteStop(stopFac25, 0 * tt, 0 * tt);
//            TransitRouteStop stop20 = ts.getFactory().createTransitRouteStop(stopFac20, 1 * tt, 1 * tt);
//            TransitRouteStop stop15 = ts.getFactory().createTransitRouteStop(stopFac15, 2 * tt, 2 * tt);
//            TransitRouteStop stop14 = ts.getFactory().createTransitRouteStop(stopFac14, 3 * tt, 3 * tt);
//            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 4 * tt, 4 * tt);
//            TransitRouteStop stop12 = ts.getFactory().createTransitRouteStop(stopFac12, 5 * tt, 5 * tt);
//            TransitRouteStop stop11 = ts.getFactory().createTransitRouteStop(stopFac11, 6 * tt, 6 * tt);
//            TransitRouteStop stop16 = ts.getFactory().createTransitRouteStop(stopFac16, 7 * tt, 7 * tt);
//            TransitRouteStop stop21 = ts.getFactory().createTransitRouteStop(stopFac21, 8 * tt, 8 * tt);
//
//            List<TransitRouteStop> stops = Arrays.asList(stop25, stop20, stop15, stop14, stop13, stop12, stop11, stop16, stop21);
//            TransitRoute tr = tsf.createTransitRoute(Id.create("lower-u-2", TransitRoute.class), null, stops, TransportMode.pt);
//            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
//            tlLowerU.addRoute(tr);
//        }
//        ts.addTransitLine(tlLowerU);

        // Upper U
        TransitLine tlUpperU = ts.getFactory().createTransitLine(Id.create("upper-u", TransitLine.class));
        {
            TransitRouteStop stop1 = ts.getFactory().createTransitRouteStop(stopFac1, 0 * tt, 0 * tt);
            TransitRouteStop stop6 = ts.getFactory().createTransitRouteStop(stopFac6, 1 * tt, 1 * tt);
            TransitRouteStop stop11 = ts.getFactory().createTransitRouteStop(stopFac11, 2 * tt, 2 * tt);
            TransitRouteStop stop12 = ts.getFactory().createTransitRouteStop(stopFac12, 3 * tt, 3 * tt);
            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 4 * tt, 4 * tt);
            TransitRouteStop stop14 = ts.getFactory().createTransitRouteStop(stopFac14, 5 * tt, 5 * tt);
            TransitRouteStop stop15 = ts.getFactory().createTransitRouteStop(stopFac15, 6 * tt, 6 * tt);
            TransitRouteStop stop10 = ts.getFactory().createTransitRouteStop(stopFac10, 7 * tt, 7 * tt);
            TransitRouteStop stop5 = ts.getFactory().createTransitRouteStop(stopFac5, 8 * tt, 8 * tt);

            List<TransitRouteStop> stops = Arrays.asList(stop1, stop6, stop11, stop12, stop13, stop14, stop15, stop10, stop5);
            TransitRoute tr = tsf.createTransitRoute(Id.create("upper-u-1", TransitRoute.class), null, stops, TransportMode.pt);
            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
            tlUpperU.addRoute(tr);
        }{
            TransitRouteStop stop5 = ts.getFactory().createTransitRouteStop(stopFac5, 0 * tt, 0 * tt);
            TransitRouteStop stop10 = ts.getFactory().createTransitRouteStop(stopFac10, 1 * tt, 1 * tt);
            TransitRouteStop stop15 = ts.getFactory().createTransitRouteStop(stopFac15, 2 * tt, 2 * tt);
            TransitRouteStop stop14 = ts.getFactory().createTransitRouteStop(stopFac14, 3 * tt, 3 * tt);
            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 4 * tt, 4 * tt);
            TransitRouteStop stop12 = ts.getFactory().createTransitRouteStop(stopFac12, 5 * tt, 5 * tt);
            TransitRouteStop stop11 = ts.getFactory().createTransitRouteStop(stopFac11, 6 * tt, 6 * tt);
            TransitRouteStop stop6 = ts.getFactory().createTransitRouteStop(stopFac6, 7 * tt, 7 * tt);
            TransitRouteStop stop1 = ts.getFactory().createTransitRouteStop(stopFac1, 8 * tt, 8 * tt);

            List<TransitRouteStop> stops = Arrays.asList(stop5, stop10, stop15, stop14, stop13, stop12, stop11, stop6, stop1);
            TransitRoute tr = tsf.createTransitRoute(Id.create("upper-u-2", TransitRoute.class), null, stops, TransportMode.pt);
            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
            tlUpperU.addRoute(tr);
        }
        ts.addTransitLine(tlUpperU);

        // X starting in north west
//        TransitLine tlXNW = ts.getFactory().createTransitLine(Id.create("x-nw", TransitLine.class));
//        {
//            TransitRouteStop stop1 = ts.getFactory().createTransitRouteStop(stopFac1, 0 * tt2, 0 * tt2);
//            TransitRouteStop stop7 = ts.getFactory().createTransitRouteStop(stopFac7, 1 * tt2, 1 * tt2);
//            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 2 * tt2, 2 * tt2);
//            TransitRouteStop stop19 = ts.getFactory().createTransitRouteStop(stopFac19, 3 * tt2, 3 * tt2);
//            TransitRouteStop stop25 = ts.getFactory().createTransitRouteStop(stopFac25, 4 * tt2, 4 * tt2);
//
//            List<TransitRouteStop> stops = Arrays.asList(stop1, stop7, stop13, stop19, stop25);
//            TransitRoute tr = tsf.createTransitRoute(Id.create("x-nw-1", TransitRoute.class), null, stops, TransportMode.pt);
//            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
//            tlXNW.addRoute(tr);
//        }{
//            TransitRouteStop stop25 = ts.getFactory().createTransitRouteStop(stopFac25, 0 * tt2, 0 * tt2);
//            TransitRouteStop stop19 = ts.getFactory().createTransitRouteStop(stopFac19, 1 * tt2, 1 * tt2);
//            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 2 * tt2, 2 * tt2);
//            TransitRouteStop stop7 = ts.getFactory().createTransitRouteStop(stopFac7, 3 * tt2, 3 * tt2);
//            TransitRouteStop stop1 = ts.getFactory().createTransitRouteStop(stopFac1, 4 * tt2, 4 * tt2);
//
//            List<TransitRouteStop> stops = Arrays.asList(stop25, stop19, stop13, stop7, stop1);
//            TransitRoute tr = tsf.createTransitRoute(Id.create("x-nw-2", TransitRoute.class), null, stops, TransportMode.pt);
//            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
//            tlXNW.addRoute(tr);
//        }
//        ts.addTransitLine(tlXNW);

        // X starting in north east
//        TransitLine tlXNE = ts.getFactory().createTransitLine(Id.create("x-ne", TransitLine.class));
//        {
//            TransitRouteStop stop5 = ts.getFactory().createTransitRouteStop(stopFac5, 0 * tt2, 0 * tt2);
//            TransitRouteStop stop9 = ts.getFactory().createTransitRouteStop(stopFac9, 1 * tt2, 1 * tt2);
//            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 2 * tt2, 2 * tt2);
//            TransitRouteStop stop17 = ts.getFactory().createTransitRouteStop(stopFac17, 3 * tt2, 3 * tt2);
//            TransitRouteStop stop21 = ts.getFactory().createTransitRouteStop(stopFac21, 4 * tt2, 4 * tt2);
//
//            List<TransitRouteStop> stops = Arrays.asList(stop5, stop9, stop13, stop17, stop21);
//            TransitRoute tr = tsf.createTransitRoute(Id.create("x-ne-1", TransitRoute.class), null, stops, TransportMode.pt);
//            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
//            tlXNE.addRoute(tr);
//        }{
//            TransitRouteStop stop21 = ts.getFactory().createTransitRouteStop(stopFac21, 0 * tt2, 0 * tt2);
//            TransitRouteStop stop17 = ts.getFactory().createTransitRouteStop(stopFac17, 1 * tt2, 1 * tt2);
//            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 2 * tt2, 2 * tt2);
//            TransitRouteStop stop9 = ts.getFactory().createTransitRouteStop(stopFac9, 3 * tt2, 3 * tt2);
//            TransitRouteStop stop5 = ts.getFactory().createTransitRouteStop(stopFac5, 4 * tt2, 4 * tt2);
//
//            List<TransitRouteStop> stops = Arrays.asList(stop21, stop17, stop13, stop9, stop5);
//            TransitRoute tr = tsf.createTransitRoute(Id.create("x-ne-2", TransitRoute.class), null, stops, TransportMode.pt);
//            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
//            tlXNE.addRoute(tr);
//        }
//        ts.addTransitLine(tlXNE);

        // X starting in north east short
//        TransitLine tlXNES = ts.getFactory().createTransitLine(Id.create("x-nes", TransitLine.class));
//        {
//            TransitRouteStop stop5 = ts.getFactory().createTransitRouteStop(stopFac5, 0 * tt2, 0 * tt2);
//            TransitRouteStop stop9 = ts.getFactory().createTransitRouteStop(stopFac9, 1 * tt2, 1 * tt2);
//            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 2 * tt2, 2 * tt2);
//
//            List<TransitRouteStop> stops = Arrays.asList(stop5, stop9, stop13);
//            TransitRoute tr = tsf.createTransitRoute(Id.create("x-nes-1", TransitRoute.class), null, stops, TransportMode.pt);
//            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
//            tlXNES.addRoute(tr);
//        }{
//            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 0 * tt2, 0 * tt2);
//            TransitRouteStop stop9 = ts.getFactory().createTransitRouteStop(stopFac9, 1 * tt2, 1 * tt2);
//            TransitRouteStop stop5 = ts.getFactory().createTransitRouteStop(stopFac5, 2 * tt2, 2 * tt2);
//
//            List<TransitRouteStop> stops = Arrays.asList(stop13, stop9, stop5);
//            TransitRoute tr = tsf.createTransitRoute(Id.create("x-nes-2", TransitRoute.class), null, stops, TransportMode.pt);
//            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
//            tlXNES.addRoute(tr);
//        }
//        ts.addTransitLine(tlXNES);

        // Ring
//        TransitLine tlRing = ts.getFactory().createTransitLine(Id.create("ring", TransitLine.class));
//        {
//            TransitRouteStop stop21 = ts.getFactory().createTransitRouteStop(stopFac21, 0 * tt, 0 * tt);
//            TransitRouteStop stop22 = ts.getFactory().createTransitRouteStop(stopFac22, 1 * tt, 1 * tt);
//            TransitRouteStop stop23 = ts.getFactory().createTransitRouteStop(stopFac23, 2 * tt, 2 * tt);
//            TransitRouteStop stop24 = ts.getFactory().createTransitRouteStop(stopFac24, 3 * tt, 3 * tt);
//            TransitRouteStop stop25 = ts.getFactory().createTransitRouteStop(stopFac25, 4 * tt, 4 * tt);
//            TransitRouteStop stop20 = ts.getFactory().createTransitRouteStop(stopFac20, 5 * tt, 5 * tt);
//            TransitRouteStop stop15 = ts.getFactory().createTransitRouteStop(stopFac15, 6 * tt, 6 * tt);
//            TransitRouteStop stop10 = ts.getFactory().createTransitRouteStop(stopFac10, 7 * tt, 7 * tt);
//            TransitRouteStop stop5 = ts.getFactory().createTransitRouteStop(stopFac5, 8 * tt, 8 * tt);
//            TransitRouteStop stop4 = ts.getFactory().createTransitRouteStop(stopFac4, 9 * tt, 9 * tt);
//            TransitRouteStop stop3 = ts.getFactory().createTransitRouteStop(stopFac3, 10 * tt, 10 * tt);
//            TransitRouteStop stop2 = ts.getFactory().createTransitRouteStop(stopFac2, 11 * tt, 11 * tt);
//            TransitRouteStop stop1 = ts.getFactory().createTransitRouteStop(stopFac1, 12 * tt, 12 * tt);
//            TransitRouteStop stop6 = ts.getFactory().createTransitRouteStop(stopFac6, 13 * tt, 13 * tt);
//            TransitRouteStop stop11 = ts.getFactory().createTransitRouteStop(stopFac11, 14 * tt, 14 * tt);
//            TransitRouteStop stop16 = ts.getFactory().createTransitRouteStop(stopFac16, 15 * tt, 15 * tt);
//
//            List<TransitRouteStop> stops = Arrays.asList(stop21, stop22, stop23, stop24, stop25, stop20, stop15, stop10, stop5, stop4, stop3, stop2, stop1, stop6, stop11, stop16, stop21);
//            TransitRoute tr = tsf.createTransitRoute(Id.create("ring-counter", TransitRoute.class), null, stops, TransportMode.pt);
//            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
//            tlRing.addRoute(tr);
//        }{
//            TransitRouteStop stop21 = ts.getFactory().createTransitRouteStop(stopFac21, 0 * tt, 0 * tt);
//            TransitRouteStop stop16 = ts.getFactory().createTransitRouteStop(stopFac16, 15 * tt, 15 * tt);
//            TransitRouteStop stop11 = ts.getFactory().createTransitRouteStop(stopFac11, 14 * tt, 14 * tt);
//            TransitRouteStop stop6 = ts.getFactory().createTransitRouteStop(stopFac6, 13 * tt, 13 * tt);
//            TransitRouteStop stop1 = ts.getFactory().createTransitRouteStop(stopFac1, 12 * tt, 12 * tt);
//            TransitRouteStop stop2 = ts.getFactory().createTransitRouteStop(stopFac2, 11 * tt, 11 * tt);
//            TransitRouteStop stop3 = ts.getFactory().createTransitRouteStop(stopFac3, 10 * tt, 10 * tt);
//            TransitRouteStop stop4 = ts.getFactory().createTransitRouteStop(stopFac4, 9 * tt, 9 * tt);
//            TransitRouteStop stop5 = ts.getFactory().createTransitRouteStop(stopFac5, 8 * tt, 8 * tt);
//            TransitRouteStop stop10 = ts.getFactory().createTransitRouteStop(stopFac10, 7 * tt, 7 * tt);
//            TransitRouteStop stop15 = ts.getFactory().createTransitRouteStop(stopFac15, 6 * tt, 6 * tt);
//            TransitRouteStop stop20 = ts.getFactory().createTransitRouteStop(stopFac20, 5 * tt, 5 * tt);
//            TransitRouteStop stop25 = ts.getFactory().createTransitRouteStop(stopFac25, 4 * tt, 4 * tt);
//            TransitRouteStop stop24 = ts.getFactory().createTransitRouteStop(stopFac24, 3 * tt, 3 * tt);
//            TransitRouteStop stop23 = ts.getFactory().createTransitRouteStop(stopFac23, 2 * tt, 2 * tt);
//            TransitRouteStop stop22 = ts.getFactory().createTransitRouteStop(stopFac22, 1 * tt, 1 * tt);
//
//            List<TransitRouteStop> stops = Arrays.asList(stop21, stop16, stop11, stop6, stop1, stop2, stop3, stop4, stop5, stop10, stop15, stop20, stop25, stop24, stop23, stop22, stop21);
//            TransitRoute tr = tsf.createTransitRoute(Id.create("ring-clock", TransitRoute.class), null, stops, TransportMode.pt);
//            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
//            tlRing.addRoute(tr);
//        }
//        ts.addTransitLine(tlRing);

        LOG.info("After alteration, transit schedule has " + ts.getTransitLines().size() + " lines.");

        new CreatePseudoNetwork(ts, network, "pt-").createNetwork();

        // Validate -------------------------------------------------------------------------------
        TransitScheduleValidator.ValidationResult validationResult = TransitScheduleValidator.validateAll(ts, scenario.getNetwork());
        for (String error : validationResult.getErrors()) {
            LOG.warn(error);
        }

        for (String warning : validationResult.getWarnings()) {
            LOG.warn(warning);
        }

        for (TransitScheduleValidator.ValidationResult.ValidationIssue issue : validationResult.getIssues()) {
            LOG.warn(issue.getMessage());
        }

        NetworkWriter writer = new NetworkWriter(network);
        writer.write(outputNetwork);

        TransitScheduleWriter transitScheduleWriter = new TransitScheduleWriter(ts);
        transitScheduleWriter.writeFile(outputPTSchedule);

        new CreateVehiclesForSchedule(ts, scenario.getTransitVehicles()).run();
        new VehicleWriterV1(scenario.getTransitVehicles()).writeFile(outputPTVehicles);
    }

//    static void setLinkSpeedsToMax(Scenario scenario) {
//        Map<Id<Link>, Double> linkMaxSpeed = new HashMap<>();
//
//        for (Link link : scenario.getNetwork().getLinks().values()) {
//            linkMaxSpeed.put(link.getId(), 0.);
//        }
//
//        for (TransitLine line : scenario.getTransitSchedule().getTransitLines().values()) {
//            for (TransitRoute transitRoute : line.getRoutes().values()) {
//                double arrivalTime = 0;
//                double departureTime = 0;
//                for (int ii = 0; ii < transitRoute.getStops().size(); ii++) {
//
//                    arrivalTime = transitRoute.getStops().get(ii).getArrivalOffset();
//                    Id<Link> linkId = null;
//                    if (ii == 0) {
//                        linkId = transitRoute.getRoute().getStartLinkId();
//                        linkMaxSpeed.replace(linkId, 50.);
//                    }
//
//                    else {
//
//                        if (ii == transitRoute.getStops().size()-1) {
//                            linkId = transitRoute.getRoute().getEndLinkId();
//                        }
//
//                        else {
//                            linkId = transitRoute.getRoute().getLinkIds().get(ii-1);
//                        }
//
//                        Double prevSpeed = linkMaxSpeed.get(linkId);
//                        double newSpeed = 50.;
//                        if (arrivalTime - departureTime != 0) {
//                            newSpeed = scenario.getNetwork().getLinks().get(linkId).getLength() / (- 1 + arrivalTime - departureTime);
//                        }
//
//                        if(newSpeed > prevSpeed) {
//                            linkMaxSpeed.replace(linkId, newSpeed);
//                        }
//                    }
//                    departureTime = transitRoute.getStops().get(ii).getDepartureOffset();
//                }
//            }
//        }
//
//        for (Link link : scenario.getNetwork().getLinks().values()) {
//            double speed = linkMaxSpeed.get(link.getId());
//            link.setFreespeed(speed);
//            if (speed>300./3.6) {
//                LOG.warn("Link speed is higher than 300 km/h on link " + link.getId()+ " - Speed is " + Math.round(speed*3.6) + " km/h");
//            }
//            if (speed<1./3.6) {
//                LOG.warn("Link speed is lower than 1 km/h on link " + link.getId()+ " - Speed is " + Math.round(speed*3.6) + " km/h");
//            }
//        }
//
//    }
    
    private static void addDepartures(TransitScheduleFactory tsf, TransitRoute tr, double beginning, double end, double interval) {
        for (double time = beginning; time < end; time = time + interval) {
            Departure departue = tsf.createDeparture(Id.create(String.valueOf(time), Departure.class), time);
            tr.addDeparture(departue);
        }
    }
}