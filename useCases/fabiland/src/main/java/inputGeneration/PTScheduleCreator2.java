//package org.matsim.pt;
package inputGeneration;


import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.population.routes.RouteUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.*;
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

	private static double DEPARTURE_OFFSET_ONE_LINK = 7 * 60;
	
	public static void main(String[] args) {

        String inputNetwork = "useCases/fabiland/scenarios/base/matsimInput/network_v2_cap150.xml";
        String outputSchedule = "useCases/fabiland/scenarios/base/matsimInput/schedule_v2.xml";
        String outputTransitVehicles = "useCases/fabiland/scenarios/base/matsimInput/transitvehicles_v2.xml";
//        String inputNetwork = "scenarios/fabiland/network.xml";
//        String outputSchedule = "scenarios/fabiland/schedule.xml";
//        String outputTransitVehicles = "scenarios/fabiland/transitvehicles.xml";

        createGridSchedule(outputSchedule, inputNetwork, outputTransitVehicles);
    }

    private static void createGridSchedule(String outputSchedule, String inputNetwork, String outputTransitVehicles) {
        Scenario scenario = ScenarioUtils.loadScenario(ConfigUtils.createConfig());

        MatsimNetworkReader matsimNetworkReader = new MatsimNetworkReader(scenario.getNetwork());
        matsimNetworkReader.readFile(inputNetwork);

        TransitSchedule ts = scenario.getTransitSchedule();
        TransitScheduleFactory tsf = ts.getFactory();

        Network network = scenario.getNetwork();

        // Common part
        final Node node11 = network.getNodes().get(Id.createNodeId("11"));
        final TransitStopFacility stop1211fac = tsf.createTransitStopFacility(Id.create("stop1211fac", TransitStopFacility.class), node11.getCoord(), false);
        stop1211fac.setLinkId(Id.createLinkId("1211"));
        ts.addStopFacility(stop1211fac);

        final Node node12 = network.getNodes().get(Id.createNodeId("12"));
        final TransitStopFacility stop1112fac = tsf.createTransitStopFacility(Id.create("stop1112fac", TransitStopFacility.class), node12.getCoord(), false);
        stop1112fac.setLinkId(Id.createLinkId("1112"));
        ts.addStopFacility(stop1112fac);
        final TransitStopFacility stop1312fac = tsf.createTransitStopFacility(Id.create("stop1312fac", TransitStopFacility.class), node12.getCoord(), false);
        stop1312fac.setLinkId(Id.createLinkId("1312"));
        ts.addStopFacility(stop1312fac);

        final Node node13 = network.getNodes().get(Id.createNodeId("13"));
        final TransitStopFacility stop1213fac = tsf.createTransitStopFacility(Id.create("stop1213fac", TransitStopFacility.class), node13.getCoord(), false);
        stop1213fac.setLinkId(Id.createLinkId("1213"));
        ts.addStopFacility(stop1213fac);
        final TransitStopFacility stop1413fac = tsf.createTransitStopFacility(Id.create("stop1413fac", TransitStopFacility.class), node13.getCoord(), false);
        stop1413fac.setLinkId(Id.createLinkId("1413"));
        ts.addStopFacility(stop1413fac);

        final Node node14 = network.getNodes().get(Id.createNodeId("14"));
        final TransitStopFacility stop1314fac = tsf.createTransitStopFacility(Id.create("stop1314fac", TransitStopFacility.class), node14.getCoord(), false);
        stop1314fac.setLinkId(Id.createLinkId("1314"));
        ts.addStopFacility(stop1314fac);
        final TransitStopFacility stop1514fac = tsf.createTransitStopFacility(Id.create("stop1514fac", TransitStopFacility.class), node14.getCoord(), false);
        stop1514fac.setLinkId(Id.createLinkId("1514"));
        ts.addStopFacility(stop1514fac);

        final Node node15 = network.getNodes().get(Id.createNodeId("15"));
        final TransitStopFacility stop1415fac = tsf.createTransitStopFacility(Id.create("stop1415fac", TransitStopFacility.class), node15.getCoord(), false);
        stop1415fac.setLinkId(Id.createLinkId("1415"));
        ts.addStopFacility(stop1415fac);

        // Line 2125 / 2521
        final Node node21 = network.getNodes().get(Id.createNodeId("21"));
        final TransitStopFacility stop2221fac = tsf.createTransitStopFacility(Id.create("stop2221fac", TransitStopFacility.class), node21.getCoord(), false);
        stop2221fac.setLinkId(Id.createLinkId("2221"));
        ts.addStopFacility(stop2221fac);
        final TransitStopFacility stop1621fac = tsf.createTransitStopFacility(Id.create("stop1621fac", TransitStopFacility.class), node21.getCoord(), false);
        stop1621fac.setLinkId(Id.createLinkId("1621"));
        ts.addStopFacility(stop1621fac);

        final Node node16 = network.getNodes().get(Id.createNodeId("16"));
        final TransitStopFacility stop2116fac = tsf.createTransitStopFacility(Id.create("stop2116fac", TransitStopFacility.class), node16.getCoord(), false);
        stop2116fac.setLinkId(Id.createLinkId("2116"));
        ts.addStopFacility(stop2116fac);
        final TransitStopFacility stop1116fac = tsf.createTransitStopFacility(Id.create("stop1116fac", TransitStopFacility.class), node16.getCoord(), false);
        stop1116fac.setLinkId(Id.createLinkId("1116"));
        ts.addStopFacility(stop1116fac);

        final TransitStopFacility stop1611fac = tsf.createTransitStopFacility(Id.create("stop1611fac", TransitStopFacility.class), node11.getCoord(), false);
        stop1611fac.setLinkId(Id.createLinkId("1611"));
        ts.addStopFacility(stop1611fac);

        final TransitStopFacility stop2015fac = tsf.createTransitStopFacility(Id.create("stop2015fac", TransitStopFacility.class), node15.getCoord(), false);
        stop2015fac.setLinkId(Id.createLinkId("2015"));
        ts.addStopFacility(stop2015fac);

        final Node node20 = network.getNodes().get(Id.createNodeId("20"));
        final TransitStopFacility stop1520fac = tsf.createTransitStopFacility(Id.create("stop1520fac", TransitStopFacility.class), node20.getCoord(), false);
        stop1520fac.setLinkId(Id.createLinkId("1520"));
        ts.addStopFacility(stop1520fac);
        final TransitStopFacility stop2520fac = tsf.createTransitStopFacility(Id.create("stop2520fac", TransitStopFacility.class), node20.getCoord(), false);
        stop2520fac.setLinkId(Id.createLinkId("2520"));
        ts.addStopFacility(stop2520fac);

        final Node node25 = network.getNodes().get(Id.createNodeId("25"));
        final TransitStopFacility stop2025fac = tsf.createTransitStopFacility(Id.create("stop2025fac", TransitStopFacility.class), node25.getCoord(), false);
        stop2025fac.setLinkId(Id.createLinkId("2025"));
        ts.addStopFacility(stop2025fac);
        final TransitStopFacility stop2425fac = tsf.createTransitStopFacility(Id.create("stop2425fac", TransitStopFacility.class), node25.getCoord(), false);
        stop2425fac.setLinkId(Id.createLinkId("2425"));
        ts.addStopFacility(stop2425fac);

        // Line 0105 / 0501
        final Node node1 = network.getNodes().get(Id.createNodeId("1"));
        final TransitStopFacility stop0201fac = tsf.createTransitStopFacility(Id.create("stop0201fac", TransitStopFacility.class), node1.getCoord(), false);
        stop0201fac.setLinkId(Id.createLinkId("0201"));
        ts.addStopFacility(stop0201fac);
        final TransitStopFacility stop0601fac = tsf.createTransitStopFacility(Id.create("stop0601fac", TransitStopFacility.class), node1.getCoord(), false);
        stop0601fac.setLinkId(Id.createLinkId("0601"));
        ts.addStopFacility(stop0601fac);

        final Node node6 = network.getNodes().get(Id.createNodeId("6"));
        final TransitStopFacility stop0106fac = tsf.createTransitStopFacility(Id.create("stop0106fac", TransitStopFacility.class), node6.getCoord(), false);
        stop0106fac.setLinkId(Id.createLinkId("0201"));
        ts.addStopFacility(stop0106fac);
        final TransitStopFacility stop1106fac = tsf.createTransitStopFacility(Id.create("stop1106fac", TransitStopFacility.class), node6.getCoord(), false);
        stop1106fac.setLinkId(Id.createLinkId("1106"));
        ts.addStopFacility(stop1106fac);

        final TransitStopFacility stop0611fac = tsf.createTransitStopFacility(Id.create("stop0611fac", TransitStopFacility.class), node11.getCoord(), false);
        stop0611fac.setLinkId(Id.createLinkId("0611"));
        ts.addStopFacility(stop0611fac);

        final TransitStopFacility stop1015fac = tsf.createTransitStopFacility(Id.create("stop1015fac", TransitStopFacility.class), node15.getCoord(), false);
        stop1015fac.setLinkId(Id.createLinkId("1015"));
        ts.addStopFacility(stop1015fac);

        final Node node10 = network.getNodes().get(Id.createNodeId("10"));
        final TransitStopFacility stop1510fac = tsf.createTransitStopFacility(Id.create("stop1510fac", TransitStopFacility.class), node10.getCoord(), false);
        stop1510fac.setLinkId(Id.createLinkId("1510"));
        ts.addStopFacility(stop1510fac);
        final TransitStopFacility stop0510fac = tsf.createTransitStopFacility(Id.create("stop0510fac", TransitStopFacility.class), node10.getCoord(), false);
        stop0510fac.setLinkId(Id.createLinkId("0510"));
        ts.addStopFacility(stop0510fac);

        final Node node5 = network.getNodes().get(Id.createNodeId("5"));
        final TransitStopFacility stop1005fac = tsf.createTransitStopFacility(Id.create("stop1005fac", TransitStopFacility.class), node5.getCoord(), false);
        stop1005fac.setLinkId(Id.createLinkId("1005"));
        ts.addStopFacility(stop1005fac);
        final TransitStopFacility stop0405fac = tsf.createTransitStopFacility(Id.create("stop0405fac", TransitStopFacility.class), node5.getCoord(), false);
        stop0405fac.setLinkId(Id.createLinkId("0405"));
        ts.addStopFacility(stop0405fac);


        // TODO consider DEPARTURE_OFFSET_ONE_LINK if necessary
        // Common part
        TransitRouteStop stop1211 = ts.getFactory().createTransitRouteStop(stop1211fac, 0., 0.);
        TransitRouteStop stop1112 = ts.getFactory().createTransitRouteStop(stop1112fac, 0., 0.);
        TransitRouteStop stop1312 = ts.getFactory().createTransitRouteStop(stop1312fac, 0., 0.);
        TransitRouteStop stop1213 = ts.getFactory().createTransitRouteStop(stop1213fac, 0., 0.);
        TransitRouteStop stop1413 = ts.getFactory().createTransitRouteStop(stop1413fac, 0., 0.);
        TransitRouteStop stop1314 = ts.getFactory().createTransitRouteStop(stop1314fac, 0., 0.);
        TransitRouteStop stop1514 = ts.getFactory().createTransitRouteStop(stop1514fac, 0., 0.);
        TransitRouteStop stop1415 = ts.getFactory().createTransitRouteStop(stop1415fac, 0., 0.);

        // Line 2125 / 2521
        TransitRouteStop stop2221 = ts.getFactory().createTransitRouteStop(stop2221fac, 0., 0.);
        TransitRouteStop stop1621 = ts.getFactory().createTransitRouteStop(stop1621fac, 0., 0.);
        TransitRouteStop stop2116 = ts.getFactory().createTransitRouteStop(stop2116fac, 0., 0.);
        TransitRouteStop stop1116 = ts.getFactory().createTransitRouteStop(stop1116fac, 0., 0.);
        TransitRouteStop stop1611 = ts.getFactory().createTransitRouteStop(stop1611fac, 0., 0.);

        TransitRouteStop stop2015 = ts.getFactory().createTransitRouteStop(stop2015fac, 0., 0.);
        TransitRouteStop stop1520 = ts.getFactory().createTransitRouteStop(stop1520fac, 0., 0.);
        TransitRouteStop stop2520 = ts.getFactory().createTransitRouteStop(stop2520fac, 0., 0.);
        TransitRouteStop stop2025 = ts.getFactory().createTransitRouteStop(stop2025fac, 0., 0.);
        TransitRouteStop stop2425 = ts.getFactory().createTransitRouteStop(stop2425fac, 0., 0.);

        // Line 0105 / 0501
        TransitRouteStop stop0201 = ts.getFactory().createTransitRouteStop(stop0201fac, 0., 0.);
        TransitRouteStop stop0601 = ts.getFactory().createTransitRouteStop(stop0601fac, 0., 0.);
        TransitRouteStop stop0106 = ts.getFactory().createTransitRouteStop(stop0106fac, 0., 0.);
        TransitRouteStop stop1106 = ts.getFactory().createTransitRouteStop(stop1106fac, 0., 0.);
        TransitRouteStop stop0611 = ts.getFactory().createTransitRouteStop(stop0611fac, 0., 0.);

        TransitRouteStop stop1015 = ts.getFactory().createTransitRouteStop(stop1015fac, 0., 0.);
        TransitRouteStop stop1510 = ts.getFactory().createTransitRouteStop(stop1510fac, 0., 0.);
        TransitRouteStop stop0510 = ts.getFactory().createTransitRouteStop(stop0510fac, 0., 0.);
        TransitRouteStop stop1005 = ts.getFactory().createTransitRouteStop(stop1005fac, 0., 0.);
        TransitRouteStop stop0405 = ts.getFactory().createTransitRouteStop(stop0405fac, 0., 0.);


        // Line 2125 / 2521
        {
            TransitLine tl = ts.getFactory().createTransitLine(Id.create("line2125", TransitLine.class));
            NetworkRoute route = RouteUtils.createNetworkRoute(Arrays.asList(Id.createLinkId("2221"), Id.createLinkId("2116"), Id.createLinkId("1611"), Id.createLinkId("1112"),
                    Id.createLinkId("1213"), Id.createLinkId("1314"), Id.createLinkId("1415"), Id.createLinkId("1520"), Id.createLinkId("2025")), network);
            List<TransitRouteStop> stops = Arrays.asList(stop2221, stop2116, stop1611, stop1112, stop1213, stop1314, stop1415, stop1520, stop2025);
            TransitRoute tr = tsf.createTransitRoute(Id.create("line2125", TransitRoute.class), route, stops, TransportMode.pt);
            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
            tl.addRoute(tr);
            ts.addTransitLine(tl);
        } {
            TransitLine tl = ts.getFactory().createTransitLine(Id.create("line2521", TransitLine.class));
            NetworkRoute route = RouteUtils.createNetworkRoute(Arrays.asList(Id.createLinkId("2425"), Id.createLinkId("2520"), Id.createLinkId("2015"), Id.createLinkId("1514"),
                    Id.createLinkId("1413"), Id.createLinkId("1312"), Id.createLinkId("1211"), Id.createLinkId("1116"), Id.createLinkId("1621")), network);
            List<TransitRouteStop> stops = Arrays.asList(stop2425, stop2520, stop2015, stop1514, stop1413, stop1312, stop1211, stop1116, stop1621);
            TransitRoute tr = tsf.createTransitRoute(Id.create("line2521", TransitRoute.class), route, stops, TransportMode.pt);
            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
            tl.addRoute(tr);
            ts.addTransitLine(tl);
        }
        // Line 0105 / 0501
        {
            TransitLine tl = ts.getFactory().createTransitLine(Id.create("line0105", TransitLine.class));
            NetworkRoute route = RouteUtils.createNetworkRoute(Arrays.asList(Id.createLinkId("0201"), Id.createLinkId("0106"), Id.createLinkId("0611"), Id.createLinkId("1112"),
                    Id.createLinkId("1213"), Id.createLinkId("1314"), Id.createLinkId("1415"), Id.createLinkId("1510"), Id.createLinkId("1005")), network);
            List<TransitRouteStop> stops = Arrays.asList(stop0201, stop0106, stop0611, stop1112, stop1213, stop1314, stop1415, stop1510, stop1005);
            TransitRoute tr = tsf.createTransitRoute(Id.create("line0105", TransitRoute.class), route, stops, TransportMode.pt);
            addDepartures(tsf, tr, PT_OPERATION_START_TIME + PT_HEADWAY/2, PT_OPERATION_END_TIME, PT_HEADWAY);
            tl.addRoute(tr);
            ts.addTransitLine(tl);
        } {
            TransitLine tl = ts.getFactory().createTransitLine(Id.create("line0501", TransitLine.class));
            NetworkRoute route = RouteUtils.createNetworkRoute(Arrays.asList(Id.createLinkId("0405"), Id.createLinkId("0510"), Id.createLinkId("1015"), Id.createLinkId("1514"),
                    Id.createLinkId("1413"), Id.createLinkId("1312"), Id.createLinkId("1211"), Id.createLinkId("1106"), Id.createLinkId("0601")), network);
            List<TransitRouteStop> stops = Arrays.asList(stop0405, stop0510, stop1015, stop1514, stop1413, stop1312, stop1211, stop1106, stop0601);
            TransitRoute tr = tsf.createTransitRoute(Id.create("line0501", TransitRoute.class), route, stops, TransportMode.pt);
            addDepartures(tsf, tr, PT_OPERATION_START_TIME + PT_HEADWAY/2, PT_OPERATION_END_TIME, PT_HEADWAY);
            tl.addRoute(tr);
            ts.addTransitLine(tl);
        }

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

        LOG.info("After alteration, transit schedule has " + ts.getTransitLines().size() + " lines.");

        TransitScheduleWriter transitScheduleWriter = new TransitScheduleWriter(ts);
        transitScheduleWriter.writeFile(outputSchedule);
        
        
        // create transit vehicles
        new CreateVehiclesForSchedule(ts, scenario.getTransitVehicles()).run();
        new VehicleWriterV1(scenario.getTransitVehicles()).writeFile(outputTransitVehicles);
    }
    
    private static void addDepartures(TransitScheduleFactory tsf, TransitRoute tr, double beginning, double end, double interval) {
        for (double time = beginning; time < end; time = time + interval) {
            Departure departue = tsf.createDeparture(Id.create(String.valueOf(time), Departure.class), time);
            tr.addDeparture(departue);
        }
    }
}