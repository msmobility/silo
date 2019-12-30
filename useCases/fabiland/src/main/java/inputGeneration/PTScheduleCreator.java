//package org.matsim.pt;
package inputGeneration;


import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.population.routes.RouteUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.TransitScheduleFactoryImpl;
import org.matsim.pt.transitSchedule.api.Departure;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.transitSchedule.api.TransitScheduleFactory;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.matsim.pt.transitSchedule.api.TransitScheduleWriter;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;
import org.matsim.pt.utils.TransitScheduleValidator;

public class PTScheduleCreator {
	
	private final static Logger LOG = Logger.getLogger(PTScheduleCreator.class);

	private static final double PT_HEADWAY = 15 * 60.;
	private static final double PT_OPERATION_START_TIME = 6 * 3600.;
	private static final double PT_OPERATION_END_TIME = 24 * 3600.;

	private static double DEPARTURE_OFFSET_ONE_LINK = 7 * 60;
	
	public static void main(String[] args) {

        String inputNetwork = "useCases/fabiland/input/base/input/matsim/network.xml";
        String outputSchedule = "useCases/fabiland/input/base/input/matsim/schedule.xml";
//        String inputNetwork = "scenarios/fabiland/network.xml";
//        String outputSchedule = "scenarios/fabiland/schedule.xml";

        createGridSchedule(outputSchedule, inputNetwork);
    }

    private static void createGridSchedule(String outputSchedule, String inputNetwork) {
        Scenario scenario = ScenarioUtils.loadScenario(ConfigUtils.createConfig());

        MatsimNetworkReader matsimNetworkReader = new MatsimNetworkReader(scenario.getNetwork());
        matsimNetworkReader.readFile(inputNetwork);

        TransitScheduleFactory tsf = new TransitScheduleFactoryImpl();
        TransitSchedule ts = tsf.createTransitSchedule();

        Network network = scenario.getNetwork();

        final Node node1 = network.getNodes().get(Id.createNodeId("1"));
        final TransitStopFacility stop0201fac = tsf.createTransitStopFacility(Id.create("stop0201fac", TransitStopFacility.class), node1.getCoord(), false);
        stop0201fac.setLinkId(Id.createLinkId("0201"));
        ts.addStopFacility(stop0201fac);
        final TransitStopFacility stop0601fac = tsf.createTransitStopFacility(Id.create("stop0601fac", TransitStopFacility.class), node1.getCoord(), false);
        stop0601fac.setLinkId(Id.createLinkId("0601"));
        ts.addStopFacility(stop0601fac);
        
        final Node node4 = network.getNodes().get(Id.createNodeId("4"));
        final TransitStopFacility stop0504fac = tsf.createTransitStopFacility(Id.create("stop0504fac", TransitStopFacility.class), node4.getCoord(), false);
        stop0504fac.setLinkId(Id.createLinkId("0504"));
        ts.addStopFacility(stop0504fac);
        final TransitStopFacility stop0904fac = tsf.createTransitStopFacility(Id.create("stop0904fac", TransitStopFacility.class), node4.getCoord(), false);
        stop0904fac.setLinkId(Id.createLinkId("0904"));
        ts.addStopFacility(stop0904fac);
        
        final Node node5 = network.getNodes().get(Id.createNodeId("5"));
        final TransitStopFacility stop1005fac = tsf.createTransitStopFacility(Id.create("stop1005fac", TransitStopFacility.class), node5.getCoord(), false);
        stop1005fac.setLinkId(Id.createLinkId("1005"));
        ts.addStopFacility(stop1005fac);
        final TransitStopFacility stop0405fac = tsf.createTransitStopFacility(Id.create("stop0405fac", TransitStopFacility.class), node5.getCoord(), false);
        stop0405fac.setLinkId(Id.createLinkId("0405"));
        ts.addStopFacility(stop0405fac);
        
        final Node node6 = network.getNodes().get(Id.createNodeId("6"));
        final TransitStopFacility stop0106fac = tsf.createTransitStopFacility(Id.create("stop0106fac", TransitStopFacility.class), node6.getCoord(), false);
        stop0106fac.setLinkId(Id.createLinkId("0106"));
        ts.addStopFacility(stop0106fac);
        final TransitStopFacility stop0706fac = tsf.createTransitStopFacility(Id.create("stop0706fac", TransitStopFacility.class), node6.getCoord(), false);
        stop0706fac.setLinkId(Id.createLinkId("0706"));
        ts.addStopFacility(stop0706fac);
        
        final Node node7 = network.getNodes().get(Id.createNodeId("7"));
        final TransitStopFacility stop0607fac = tsf.createTransitStopFacility(Id.create("stop0607fac", TransitStopFacility.class), node7.getCoord(), false);
        stop0607fac.setLinkId(Id.createLinkId("0607"));
        ts.addStopFacility(stop0607fac);
        final TransitStopFacility stop1207fac = tsf.createTransitStopFacility(Id.create("stop1207fac", TransitStopFacility.class), node7.getCoord(), false);
        stop1207fac.setLinkId(Id.createLinkId("1207"));
        ts.addStopFacility(stop1207fac);
        
        final Node node8 = network.getNodes().get(Id.createNodeId("8"));
        final TransitStopFacility stop0908fac = tsf.createTransitStopFacility(Id.create("stop0908fac", TransitStopFacility.class), node8.getCoord(), false);
        stop0908fac.setLinkId(Id.createLinkId("0908"));
        ts.addStopFacility(stop0908fac);
        final TransitStopFacility stop1308fac = tsf.createTransitStopFacility(Id.create("stop1308fac", TransitStopFacility.class), node8.getCoord(), false);
        stop1308fac.setLinkId(Id.createLinkId("1308"));
        ts.addStopFacility(stop1308fac);
        
        final Node node9 = network.getNodes().get(Id.createNodeId("9"));
        final TransitStopFacility stop0409fac = tsf.createTransitStopFacility(Id.create("stop0409fac", TransitStopFacility.class), node9.getCoord(), false);
        stop0409fac.setLinkId(Id.createLinkId("0409"));
        ts.addStopFacility(stop0409fac);
        final TransitStopFacility stop0809fac = tsf.createTransitStopFacility(Id.create("stop0809fac", TransitStopFacility.class), node9.getCoord(), false);
        stop0809fac.setLinkId(Id.createLinkId("0809"));
        ts.addStopFacility(stop0809fac);
        
        final Node node12 = network.getNodes().get(Id.createNodeId("12"));
        final TransitStopFacility stop0712fac = tsf.createTransitStopFacility(Id.create("stop0712fac", TransitStopFacility.class), node12.getCoord(), false);
        stop0712fac.setLinkId(Id.createLinkId("0712"));
        ts.addStopFacility(stop0712fac);
        final TransitStopFacility stop1312fac = tsf.createTransitStopFacility(Id.create("stop1312fac", TransitStopFacility.class), node12.getCoord(), false);
        stop1312fac.setLinkId(Id.createLinkId("1312"));
        ts.addStopFacility(stop1312fac);
        
        final Node node13 = network.getNodes().get(Id.createNodeId("13"));
        final TransitStopFacility stop1213fac = tsf.createTransitStopFacility(Id.create("stop1213fac", TransitStopFacility.class), node13.getCoord(), false);
        stop1213fac.setLinkId(Id.createLinkId("1213"));
        ts.addStopFacility(stop1213fac);
        final TransitStopFacility stop0813fac = tsf.createTransitStopFacility(Id.create("stop0813fac", TransitStopFacility.class), node13.getCoord(), false);
        stop0813fac.setLinkId(Id.createLinkId("0813"));
        ts.addStopFacility(stop0813fac);
        final TransitStopFacility stop1413fac = tsf.createTransitStopFacility(Id.create("stop1413fac", TransitStopFacility.class), node13.getCoord(), false);
        stop1413fac.setLinkId(Id.createLinkId("1413"));
        ts.addStopFacility(stop1413fac);
        final TransitStopFacility stop1813fac = tsf.createTransitStopFacility(Id.create("stop1813fac", TransitStopFacility.class), node13.getCoord(), false);
        stop1813fac.setLinkId(Id.createLinkId("1813"));
        ts.addStopFacility(stop1813fac);
        
        final Node node14 = network.getNodes().get(Id.createNodeId("14"));
        final TransitStopFacility stop1314fac = tsf.createTransitStopFacility(Id.create("stop1314fac", TransitStopFacility.class), node14.getCoord(), false);
        ts.addStopFacility(stop1314fac);
        stop1314fac.setLinkId(Id.createLinkId("1314"));
        final TransitStopFacility stop1914fac = tsf.createTransitStopFacility(Id.create("stop1914fac", TransitStopFacility.class), node14.getCoord(), false);
        ts.addStopFacility(stop1914fac);
        stop1914fac.setLinkId(Id.createLinkId("1914"));
        
        final Node node17 = network.getNodes().get(Id.createNodeId("17"));
        final TransitStopFacility stop1817fac = tsf.createTransitStopFacility(Id.create("stop1817fac", TransitStopFacility.class), node17.getCoord(), false);
        ts.addStopFacility(stop1817fac);
        stop1817fac.setLinkId(Id.createLinkId("1817"));
        final TransitStopFacility stop2217fac = tsf.createTransitStopFacility(Id.create("stop2217fac", TransitStopFacility.class), node17.getCoord(), false);
        ts.addStopFacility(stop2217fac);
        stop2217fac.setLinkId(Id.createLinkId("2217"));
        
        final Node node18 = network.getNodes().get(Id.createNodeId("18"));
        final TransitStopFacility stop1318fac = tsf.createTransitStopFacility(Id.create("stop1318fac", TransitStopFacility.class), node18.getCoord(), false);
        ts.addStopFacility(stop1318fac);
        stop1318fac.setLinkId(Id.createLinkId("1318"));
        final TransitStopFacility stop1718fac = tsf.createTransitStopFacility(Id.create("stop1718fac", TransitStopFacility.class), node18.getCoord(), false);
        ts.addStopFacility(stop1718fac);
        stop1718fac.setLinkId(Id.createLinkId("1718"));
        
        final Node node19 = network.getNodes().get(Id.createNodeId("19"));
        final TransitStopFacility stop1419fac = tsf.createTransitStopFacility(Id.create("stop1419fac", TransitStopFacility.class), node19.getCoord(), false);
        ts.addStopFacility(stop1419fac);
        stop1419fac.setLinkId(Id.createLinkId("1419"));
        final TransitStopFacility stop2019fac = tsf.createTransitStopFacility(Id.create("stop2019fac", TransitStopFacility.class), node19.getCoord(), false);
        ts.addStopFacility(stop2019fac);
        stop2019fac.setLinkId(Id.createLinkId("2019"));
        
        final Node node20 = network.getNodes().get(Id.createNodeId("20"));
        final TransitStopFacility stop1920fac = tsf.createTransitStopFacility(Id.create("stop1920fac", TransitStopFacility.class), node20.getCoord(), false);
        ts.addStopFacility(stop1920fac);
        stop1920fac.setLinkId(Id.createLinkId("1920"));
        final TransitStopFacility stop2520fac = tsf.createTransitStopFacility(Id.create("stop2520fac", TransitStopFacility.class), node20.getCoord(), false);
        ts.addStopFacility(stop2520fac);
        stop2520fac.setLinkId(Id.createLinkId("2520"));
        
        final Node node21 = network.getNodes().get(Id.createNodeId("21"));
        final TransitStopFacility stop2221fac = tsf.createTransitStopFacility(Id.create("stop2221fac", TransitStopFacility.class), node21.getCoord(), false);
        ts.addStopFacility(stop2221fac);
        stop2221fac.setLinkId(Id.createLinkId("2221"));
        final TransitStopFacility stop1621fac = tsf.createTransitStopFacility(Id.create("stop1621fac", TransitStopFacility.class), node21.getCoord(), false);
        ts.addStopFacility(stop1621fac);
        stop1621fac.setLinkId(Id.createLinkId("1621"));
        
        final Node node22 = network.getNodes().get(Id.createNodeId("22"));
        final TransitStopFacility stop1722fac = tsf.createTransitStopFacility(Id.create("stop1722fac", TransitStopFacility.class), node22.getCoord(), false);
        ts.addStopFacility(stop1722fac);
        stop1722fac.setLinkId(Id.createLinkId("1722"));
        final TransitStopFacility stop2122fac = tsf.createTransitStopFacility(Id.create("stop2122fac", TransitStopFacility.class), node22.getCoord(), false);
        ts.addStopFacility(stop2122fac);
        stop2122fac.setLinkId(Id.createLinkId("2122"));
        
        final Node node25 = network.getNodes().get(Id.createNodeId("25"));
        final TransitStopFacility stop2025fac = tsf.createTransitStopFacility(Id.create("stop2025fac", TransitStopFacility.class), node25.getCoord(), false);
        ts.addStopFacility(stop2025fac);
        stop2025fac.setLinkId(Id.createLinkId("2025"));
        final TransitStopFacility stop2425fac = tsf.createTransitStopFacility(Id.create("stop2425fac", TransitStopFacility.class), node25.getCoord(), false);
        ts.addStopFacility(stop2425fac);
        stop2425fac.setLinkId(Id.createLinkId("2425"));


        // TODO consider DEPARTURE_OFFSET_ONE_LINK if necessary
        TransitRouteStop stop0201 = ts.getFactory().createTransitRouteStop(stop0201fac, 0., 0.);
        TransitRouteStop stop0601 = ts.getFactory().createTransitRouteStop(stop0601fac, 0., 0.);
        TransitRouteStop stop0504 = ts.getFactory().createTransitRouteStop(stop0504fac, 0., 0.);
        TransitRouteStop stop0904 = ts.getFactory().createTransitRouteStop(stop0904fac, 0., 0.);
        TransitRouteStop stop1005 = ts.getFactory().createTransitRouteStop(stop1005fac, 0., 0.);
        TransitRouteStop stop0405 = ts.getFactory().createTransitRouteStop(stop0405fac, 0., 0.);
        TransitRouteStop stop0106 = ts.getFactory().createTransitRouteStop(stop0106fac, 0., 0.);
        TransitRouteStop stop0706 = ts.getFactory().createTransitRouteStop(stop0706fac, 0., 0.);
        TransitRouteStop stop0607 = ts.getFactory().createTransitRouteStop(stop0607fac, 0., 0.);
        TransitRouteStop stop1207 = ts.getFactory().createTransitRouteStop(stop1207fac, 0., 0.);
        TransitRouteStop stop0908 = ts.getFactory().createTransitRouteStop(stop0908fac, 0., 0.);
        TransitRouteStop stop1308 = ts.getFactory().createTransitRouteStop(stop1308fac, 0., 0.);
        TransitRouteStop stop0409 = ts.getFactory().createTransitRouteStop(stop0409fac, 0., 0.);
        TransitRouteStop stop0809 = ts.getFactory().createTransitRouteStop(stop0809fac, 0., 0.);
        TransitRouteStop stop0712 = ts.getFactory().createTransitRouteStop(stop0712fac, 0., 0.);
        TransitRouteStop stop1312 = ts.getFactory().createTransitRouteStop(stop1312fac, 0., 0.);
        TransitRouteStop stop1213 = ts.getFactory().createTransitRouteStop(stop1213fac, 0., 0.);
        TransitRouteStop stop0813 = ts.getFactory().createTransitRouteStop(stop0813fac, 0., 0.);
        TransitRouteStop stop1413 = ts.getFactory().createTransitRouteStop(stop1413fac, 0., 0.);
        TransitRouteStop stop1813 = ts.getFactory().createTransitRouteStop(stop1813fac, 0., 0.);
        TransitRouteStop stop1314 = ts.getFactory().createTransitRouteStop(stop1314fac, 0., 0.);
        TransitRouteStop stop1914 = ts.getFactory().createTransitRouteStop(stop1914fac, 0., 0.);
        TransitRouteStop stop1817 = ts.getFactory().createTransitRouteStop(stop1817fac, 0., 0.);
        TransitRouteStop stop2217 = ts.getFactory().createTransitRouteStop(stop2217fac, 0., 0.);
        TransitRouteStop stop1318 = ts.getFactory().createTransitRouteStop(stop1318fac, 0., 0.);
        TransitRouteStop stop1718 = ts.getFactory().createTransitRouteStop(stop1718fac, 0., 0.);
        TransitRouteStop stop1419 = ts.getFactory().createTransitRouteStop(stop1419fac, 0., 0.);
        TransitRouteStop stop2019 = ts.getFactory().createTransitRouteStop(stop2019fac, 0., 0.);
        TransitRouteStop stop1920 = ts.getFactory().createTransitRouteStop(stop1920fac, 0., 0.);
        TransitRouteStop stop2520 = ts.getFactory().createTransitRouteStop(stop2520fac, 0., 0.);
        TransitRouteStop stop2221 = ts.getFactory().createTransitRouteStop(stop2221fac, 0., 0.);
        TransitRouteStop stop1621 = ts.getFactory().createTransitRouteStop(stop1621fac, 0., 0.);
        TransitRouteStop stop1722 = ts.getFactory().createTransitRouteStop(stop1722fac, 0., 0.);
        TransitRouteStop stop2122 = ts.getFactory().createTransitRouteStop(stop2122fac, 0., 0.);
        TransitRouteStop stop2025 = ts.getFactory().createTransitRouteStop(stop2025fac, 0., 0.);
        TransitRouteStop stop2425 = ts.getFactory().createTransitRouteStop(stop2425fac, 0., 0.);

        {
            TransitLine tl = ts.getFactory().createTransitLine(Id.create("line0125", TransitLine.class));
            NetworkRoute route = RouteUtils.createNetworkRoute(Arrays.asList(Id.createLinkId("0201"), Id.createLinkId("0106"), Id.createLinkId("0607"), Id.createLinkId("0712"),
            		Id.createLinkId("1213"), Id.createLinkId("1314"), Id.createLinkId("1419"), Id.createLinkId("1920"), Id.createLinkId("2025")), network);
            List<TransitRouteStop> stops = Arrays.asList(stop0201, stop0106, stop0607, stop0712, stop1213, stop1314, stop1419, stop1920, stop2025);
            TransitRoute tr = tsf.createTransitRoute(Id.create("line0125", TransitRoute.class), route, stops, TransportMode.pt);
            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
            tl.addRoute(tr);
            ts.addTransitLine(tl);
        } {
        	TransitLine tl = ts.getFactory().createTransitLine(Id.create("line2501", TransitLine.class));
            NetworkRoute route = RouteUtils.createNetworkRoute(Arrays.asList(Id.createLinkId("2425"), Id.createLinkId("2520"), Id.createLinkId("2019"), Id.createLinkId("1914"),
            		Id.createLinkId("1413"), Id.createLinkId("1312"), Id.createLinkId("1207"), Id.createLinkId("0706"), Id.createLinkId("0601")), network);
            List<TransitRouteStop> stops = Arrays.asList(stop2425, stop2520, stop2019, stop1914, stop1413, stop1312, stop1207, stop0706, stop0601);
            TransitRoute tr = tsf.createTransitRoute(Id.create("line2501", TransitRoute.class), route, stops, TransportMode.pt);
            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
            tl.addRoute(tr);
            ts.addTransitLine(tl);
        } {
        	TransitLine tl = ts.getFactory().createTransitLine(Id.create("line0521", TransitLine.class));
            NetworkRoute route = RouteUtils.createNetworkRoute(Arrays.asList(Id.createLinkId("1005"), Id.createLinkId("0504"), Id.createLinkId("0409"), Id.createLinkId("0908"),
            		Id.createLinkId("0813"), Id.createLinkId("1318"), Id.createLinkId("1817"), Id.createLinkId("1722"), Id.createLinkId("2221")), network);
            List<TransitRouteStop> stops = Arrays.asList(stop1005, stop0504, stop0409, stop0908, stop0813, stop1318, stop1817, stop1722, stop2221);
            TransitRoute tr = tsf.createTransitRoute(Id.create("line0521", TransitRoute.class), route, stops, TransportMode.pt);
            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
            tl.addRoute(tr);
            ts.addTransitLine(tl);
        } {
        	TransitLine tl = ts.getFactory().createTransitLine(Id.create("line2105", TransitLine.class));
            NetworkRoute route = RouteUtils.createNetworkRoute(Arrays.asList(Id.createLinkId("1621"), Id.createLinkId("2122"), Id.createLinkId("2217"), Id.createLinkId("1718"),
            		Id.createLinkId("1813"), Id.createLinkId("1308"), Id.createLinkId("0809"), Id.createLinkId("0904"), Id.createLinkId("0405")), network);
            List<TransitRouteStop> stops = Arrays.asList(stop1621, stop2122, stop2217, stop1718, stop1813, stop1308, stop0809, stop0904, stop0405);
            TransitRoute tr = tsf.createTransitRoute(Id.create("line2105", TransitRoute.class), route, stops, TransportMode.pt);
            addDepartures(tsf, tr, PT_OPERATION_START_TIME, PT_OPERATION_END_TIME, PT_HEADWAY);
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
    }
    
    private static void addDepartures(TransitScheduleFactory tsf, TransitRoute tr, double beginning, double end, double interval) {
        for (double time = beginning; time < end; time = time + interval) {
            Departure departue = tsf.createDeparture(Id.create(String.valueOf(time), Departure.class), time);
            tr.addDeparture(departue);
        }
    }
}
