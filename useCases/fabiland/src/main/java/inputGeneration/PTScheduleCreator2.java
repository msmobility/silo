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

public class PTScheduleCreator2 {
	
	private final static Logger LOG = Logger.getLogger(PTScheduleCreator2.class);

	private static final double PT_HEADWAY = 10 * 60.;
	private static final double PT_OPERATION_START_TIME = 6 * 3600.;
	private static final double PT_OPERATION_END_TIME = 24 * 3600.;

	public static void main(String[] args) {
	    //String scenarioName = "_4-l_ux";
        //String scenarioName = "_1-l_lower-u";
        String scenarioName = "_2-l_x";

        String inputNetwork = "useCases/fabiland/scenario/matsimInput/network_cap75.xml";
        String outputNetwork = "useCases/fabiland/scenario/matsimInput/network_cap75" + scenarioName + ".xml";
        String outputPTSchedule = "useCases/fabiland/scenario/matsimInput/schedule" + scenarioName + ".xml";
        String outputPTVehicles = "useCases/fabiland/scenario/matsimInput/transitvehicles" + scenarioName + ".xml";

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
        ts.addStopFacility(stopFac1);

        final Node node5 = network.getNodes().get(Id.createNodeId("5"));
        final TransitStopFacility stopFac5 = tsf.createTransitStopFacility(Id.create(node5.getId(), TransitStopFacility.class), node5.getCoord(), false);
        ts.addStopFacility(stopFac5);

        final Node node6 = network.getNodes().get(Id.createNodeId("6"));
        final TransitStopFacility stopFac6 = tsf.createTransitStopFacility(Id.create(node6.getId(), TransitStopFacility.class), node6.getCoord(), false);
        ts.addStopFacility(stopFac6);

        final Node node7 = network.getNodes().get(Id.createNodeId("7"));
        final TransitStopFacility stopFac7 = tsf.createTransitStopFacility(Id.create(node7.getId(), TransitStopFacility.class), node7.getCoord(), false);
        ts.addStopFacility(stopFac7);

        final Node node9 = network.getNodes().get(Id.createNodeId("9"));
        final TransitStopFacility stopFac9 = tsf.createTransitStopFacility(Id.create(node9.getId(), TransitStopFacility.class), node9.getCoord(), false);
        ts.addStopFacility(stopFac9);

        final Node node10 = network.getNodes().get(Id.createNodeId("10"));
        final TransitStopFacility stopFac10 = tsf.createTransitStopFacility(Id.create(node10.getId(), TransitStopFacility.class), node10.getCoord(), false);
        ts.addStopFacility(stopFac10);

        final Node node11 = network.getNodes().get(Id.createNodeId("11"));
        final TransitStopFacility stopFac11 = tsf.createTransitStopFacility(Id.create(node11.getId(), TransitStopFacility.class), node11.getCoord(), false);
        ts.addStopFacility(stopFac11);

        final Node node12 = network.getNodes().get(Id.createNodeId("12"));
        final TransitStopFacility stopFac12 = tsf.createTransitStopFacility(Id.create(node12.getId(), TransitStopFacility.class), node12.getCoord(), false);
        ts.addStopFacility(stopFac12);

        final Node node13 = network.getNodes().get(Id.createNodeId("13"));
        final TransitStopFacility stopFac13 = tsf.createTransitStopFacility(Id.create(node13.getId(), TransitStopFacility.class), node13.getCoord(), false);
        ts.addStopFacility(stopFac13);

        final Node node14 = network.getNodes().get(Id.createNodeId("14"));
        final TransitStopFacility stopFac14 = tsf.createTransitStopFacility(Id.create(node14.getId(), TransitStopFacility.class), node14.getCoord(), false);
        ts.addStopFacility(stopFac14);

        final Node node15 = network.getNodes().get(Id.createNodeId("15"));
        final TransitStopFacility stopFac15 = tsf.createTransitStopFacility(Id.create(node15.getId(), TransitStopFacility.class), node15.getCoord(), false);
        ts.addStopFacility(stopFac15);

        final Node node16 = network.getNodes().get(Id.createNodeId("16"));
        final TransitStopFacility stopFac16 = tsf.createTransitStopFacility(Id.create(node16.getId(), TransitStopFacility.class), node16.getCoord(), false);
        ts.addStopFacility(stopFac16);

        final Node node17 = network.getNodes().get(Id.createNodeId("17"));
        final TransitStopFacility stopFac17 = tsf.createTransitStopFacility(Id.create(node17.getId(), TransitStopFacility.class), node17.getCoord(), false);
        ts.addStopFacility(stopFac17);

        final Node node19 = network.getNodes().get(Id.createNodeId("19"));
        final TransitStopFacility stopFac19 = tsf.createTransitStopFacility(Id.create(node19.getId(), TransitStopFacility.class), node19.getCoord(), false);
        ts.addStopFacility(stopFac19);

        final Node node20 = network.getNodes().get(Id.createNodeId("20"));
        final TransitStopFacility stopFac20 = tsf.createTransitStopFacility(Id.create(node20.getId(), TransitStopFacility.class), node20.getCoord(), false);
        ts.addStopFacility(stopFac20);

        final Node node21 = network.getNodes().get(Id.createNodeId("21"));
        final TransitStopFacility stopFac21 = tsf.createTransitStopFacility(Id.create(node21.getId(), TransitStopFacility.class), node21.getCoord(), false);
        ts.addStopFacility(stopFac21);

        final Node node25 = network.getNodes().get(Id.createNodeId("25"));
        final TransitStopFacility stopFac25 = tsf.createTransitStopFacility(Id.create(node25.getId(), TransitStopFacility.class), node25.getCoord(), false);
        ts.addStopFacility(stopFac25);

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
//        TransitLine tlUpperU = ts.getFactory().createTransitLine(Id.create("upper-u", TransitLine.class));
//        {
//            TransitRouteStop stop1 = ts.getFactory().createTransitRouteStop(stopFac1, 0 * tt, 0 * tt);
//            TransitRouteStop stop6 = ts.getFactory().createTransitRouteStop(stopFac6, 1 * tt, 1 * tt);
//            TransitRouteStop stop11 = ts.getFactory().createTransitRouteStop(stopFac11, 2 * tt, 2 * tt);
//            TransitRouteStop stop12 = ts.getFactory().createTransitRouteStop(stopFac12, 3 * tt, 3 * tt);
//            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 4 * tt, 4 * tt);
//            TransitRouteStop stop14 = ts.getFactory().createTransitRouteStop(stopFac14, 5 * tt, 5 * tt);
//            TransitRouteStop stop15 = ts.getFactory().createTransitRouteStop(stopFac15, 6 * tt, 6 * tt);
//            TransitRouteStop stop10 = ts.getFactory().createTransitRouteStop(stopFac10, 7 * tt, 7 * tt);
//            TransitRouteStop stop5 = ts.getFactory().createTransitRouteStop(stopFac5, 8 * tt, 8 * tt);
//
//            List<TransitRouteStop> stops = Arrays.asList(stop1, stop6, stop11, stop12, stop13, stop14, stop15, stop10, stop5);
//            TransitRoute tr = tsf.createTransitRoute(Id.create("upper-u-1", TransitRoute.class), null, stops, TransportMode.pt);
//            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
//            tlUpperU.addRoute(tr);
//        }{
//            TransitRouteStop stop5 = ts.getFactory().createTransitRouteStop(stopFac5, 0 * tt, 0 * tt);
//            TransitRouteStop stop10 = ts.getFactory().createTransitRouteStop(stopFac10, 1 * tt, 1 * tt);
//            TransitRouteStop stop15 = ts.getFactory().createTransitRouteStop(stopFac15, 2 * tt, 2 * tt);
//            TransitRouteStop stop14 = ts.getFactory().createTransitRouteStop(stopFac14, 3 * tt, 3 * tt);
//            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 4 * tt, 4 * tt);
//            TransitRouteStop stop12 = ts.getFactory().createTransitRouteStop(stopFac12, 5 * tt, 5 * tt);
//            TransitRouteStop stop11 = ts.getFactory().createTransitRouteStop(stopFac11, 6 * tt, 6 * tt);
//            TransitRouteStop stop6 = ts.getFactory().createTransitRouteStop(stopFac6, 7 * tt, 7 * tt);
//            TransitRouteStop stop1 = ts.getFactory().createTransitRouteStop(stopFac1, 8 * tt, 8 * tt);
//
//            List<TransitRouteStop> stops = Arrays.asList(stop5, stop10, stop15, stop14, stop13, stop12, stop11, stop6, stop1);
//            TransitRoute tr = tsf.createTransitRoute(Id.create("upper-u-2", TransitRoute.class), null, stops, TransportMode.pt);
//            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
//            tlUpperU.addRoute(tr);
//        }
//        ts.addTransitLine(tlUpperU);
//
//        // X starting in north west
        TransitLine tlXNW = ts.getFactory().createTransitLine(Id.create("x-nw", TransitLine.class));
        {
            TransitRouteStop stop1 = ts.getFactory().createTransitRouteStop(stopFac1, 0 * tt2, 0 * tt2);
            TransitRouteStop stop7 = ts.getFactory().createTransitRouteStop(stopFac7, 1 * tt2, 1 * tt2);
            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 2 * tt2, 2 * tt2);
            TransitRouteStop stop19 = ts.getFactory().createTransitRouteStop(stopFac19, 3 * tt2, 3 * tt2);
            TransitRouteStop stop25 = ts.getFactory().createTransitRouteStop(stopFac25, 4 * tt2, 4 * tt2);

            List<TransitRouteStop> stops = Arrays.asList(stop1, stop7, stop13, stop19, stop25);
            TransitRoute tr = tsf.createTransitRoute(Id.create("x-nw-1", TransitRoute.class), null, stops, TransportMode.pt);
            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
            tlXNW.addRoute(tr);
        }{
            TransitRouteStop stop25 = ts.getFactory().createTransitRouteStop(stopFac25, 0 * tt2, 0 * tt2);
            TransitRouteStop stop19 = ts.getFactory().createTransitRouteStop(stopFac19, 1 * tt2, 1 * tt2);
            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 2 * tt2, 2 * tt2);
            TransitRouteStop stop7 = ts.getFactory().createTransitRouteStop(stopFac7, 3 * tt2, 3 * tt2);
            TransitRouteStop stop1 = ts.getFactory().createTransitRouteStop(stopFac1, 4 * tt2, 4 * tt2);

            List<TransitRouteStop> stops = Arrays.asList(stop25, stop19, stop13, stop7, stop1);
            TransitRoute tr = tsf.createTransitRoute(Id.create("x-nw-2", TransitRoute.class), null, stops, TransportMode.pt);
            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
            tlXNW.addRoute(tr);
        }
        ts.addTransitLine(tlXNW);

        // X starting in north east
        TransitLine tlXNE = ts.getFactory().createTransitLine(Id.create("x-ne", TransitLine.class));
        {
            TransitRouteStop stop5 = ts.getFactory().createTransitRouteStop(stopFac5, 0 * tt2, 0 * tt2);
            TransitRouteStop stop9 = ts.getFactory().createTransitRouteStop(stopFac9, 1 * tt2, 1 * tt2);
            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 2 * tt2, 2 * tt2);
            TransitRouteStop stop17 = ts.getFactory().createTransitRouteStop(stopFac17, 3 * tt2, 3 * tt2);
            TransitRouteStop stop21 = ts.getFactory().createTransitRouteStop(stopFac21, 4 * tt2, 4 * tt2);

            List<TransitRouteStop> stops = Arrays.asList(stop5, stop9, stop13, stop17, stop21);
            TransitRoute tr = tsf.createTransitRoute(Id.create("x-ne-1", TransitRoute.class), null, stops, TransportMode.pt);
            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
            tlXNE.addRoute(tr);
        }{
            TransitRouteStop stop21 = ts.getFactory().createTransitRouteStop(stopFac21, 0 * tt2, 0 * tt2);
            TransitRouteStop stop17 = ts.getFactory().createTransitRouteStop(stopFac17, 1 * tt2, 1 * tt2);
            TransitRouteStop stop13 = ts.getFactory().createTransitRouteStop(stopFac13, 2 * tt2, 2 * tt2);
            TransitRouteStop stop9 = ts.getFactory().createTransitRouteStop(stopFac9, 3 * tt2, 3 * tt2);
            TransitRouteStop stop5 = ts.getFactory().createTransitRouteStop(stopFac5, 4 * tt2, 4 * tt2);

            List<TransitRouteStop> stops = Arrays.asList(stop21, stop17, stop13, stop9, stop5);
            TransitRoute tr = tsf.createTransitRoute(Id.create("x-ne-2", TransitRoute.class), null, stops, TransportMode.pt);
            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
            tlXNE.addRoute(tr);
        }
        ts.addTransitLine(tlXNE);

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