package inputGeneration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.*;
import org.matsim.pt.utils.CreatePseudoNetwork;
import org.matsim.pt.utils.CreateVehiclesForSchedule;
import org.matsim.pt.utils.TransitScheduleValidator;
import org.matsim.vehicles.MatsimVehicleWriter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Generates both rings and diagonals, including a combined road/PT network and vehicles. */
public class PTScheduleCreatorRingsAndDiagonals {
    private static final Logger LOG = LogManager.getLogger(PTScheduleCreatorRingsAndDiagonals.class);
    private static final Path DEFAULT_NETWORK = Path.of(
            "useCases/fabiland/scenario/matsimInput/nw_cap30.xml");
    private static final double PT_HEADWAY = 600;
    private static final double PT_OPERATION_START_TIME = 6 * 3600;
    private static final double PT_OPERATION_END_TIME = 24 * 3600;
    private static final double RING_SEGMENT_TIME = 360;
    private static final double DIAGONAL_SEGMENT_TIME = 500;

    /** Run from the repository root, or supply the road network path as the only argument. */
    public static void main(String[] args) {
        if (args.length > 1) {
            throw new IllegalArgumentException("Usage: PTScheduleCreatorRingsAndDiagonals [road-network.xml]");
        }
        Path inputNetwork = (args.length == 0 ? DEFAULT_NETWORK : Path.of(args[0])).toAbsolutePath();
        Path outputDirectory = inputNetwork.getParent();
        Scenario scenario = createScenario(inputNetwork);

        new NetworkWriter(scenario.getNetwork()).write(
                outputDirectory.resolve("nw_cap30_rings_x.xml").toString());
        // Vehicle generation must precede schedule writing to persist departure vehicle IDs.
        new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile(
                outputDirectory.resolve("ts_rings_x.xml").toString());
        new MatsimVehicleWriter(scenario.getTransitVehicles()).writeFile(
                outputDirectory.resolve("tv_rings_x.xml").toString());
        LOG.info("Wrote rings and diagonals network, schedule and vehicles to {}", outputDirectory);
    }

    public static Scenario createScenario(Path inputNetwork) {
        Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        new MatsimNetworkReader(scenario.getNetwork()).readFile(inputNetwork.toString());
        if (scenario.getNetwork().getNodes().keySet().stream().anyMatch(id -> id.toString().startsWith("pt-"))
                || scenario.getNetwork().getLinks().keySet().stream().anyMatch(id -> id.toString().startsWith("pt-"))) {
            throw new IllegalArgumentException("Input must be a road network without the reserved pt- prefix");
        }
        TransitSchedule schedule = scenario.getTransitSchedule();
        TransitScheduleFactory factory = schedule.getFactory();
        for (int number = 1; number <= 25; number++) {
            Node node = scenario.getNetwork().getNodes().get(Id.createNodeId(number));
            if (node == null) {
                throw new IllegalArgumentException("Road network is missing grid node " + number);
            }
            TransitStopFacility stop = factory.createTransitStopFacility(
                    Id.create(number, TransitStopFacility.class), node.getCoord(), false);
            stop.setStopAreaId(Id.create(number, TransitStopArea.class));
            schedule.addStopFacility(stop);
        }

        addLine(schedule, "outer-ring", RING_SEGMENT_TIME,
                21, 22, 23, 24, 25, 20, 15, 10, 5, 4, 3, 2, 1, 6, 11, 16, 21);
        addLine(schedule, "inner-ring", RING_SEGMENT_TIME,
                7, 8, 9, 14, 19, 18, 17, 12, 7);
        addLine(schedule, "x-ne", DIAGONAL_SEGMENT_TIME, 5, 9, 13, 17, 21);
        addLine(schedule, "x-nw", DIAGONAL_SEGMENT_TIME, 1, 7, 13, 19, 25);

        new CreatePseudoNetwork(schedule, scenario.getNetwork(), "pt-").createNetwork();
        new CreateVehiclesForSchedule(schedule, scenario.getTransitVehicles()).run(TransportMode.pt);

        TransitScheduleValidator.ValidationResult validation =
                TransitScheduleValidator.validateAll(schedule, scenario.getNetwork());
        validation.getWarnings().forEach(LOG::warn);
        if (!validation.getErrors().isEmpty()) {
            throw new IllegalStateException("Invalid transit schedule: " + validation.getErrors());
        }
        return scenario;
    }

    private static void addLine(TransitSchedule schedule, String lineId, double segmentTime, int... nodes) {
        TransitScheduleFactory factory = schedule.getFactory();
        TransitLine line = factory.createTransitLine(Id.create(lineId, TransitLine.class));
        for (int direction = 0; direction < 2; direction++) {
            List<TransitRouteStop> stops = new ArrayList<>();
            for (int index = 0; index < nodes.length; index++) {
                int node = nodes[direction == 0 ? index : nodes.length - 1 - index];
                TransitStopFacility facility = schedule.getFacilities().get(Id.create(node, TransitStopFacility.class));
                if (index == 0 && nodes[0] == nodes[nodes.length - 1]) {
                    // A ring revisits its origin. Sharing its departure platform with another
                    // line makes Raptor discard transfers there as an earlier stop on the ring.
                    TransitStopFacility departurePlatform = factory.createTransitStopFacility(
                            Id.create(lineId + "-" + (direction + 1) + "-start", TransitStopFacility.class),
                            facility.getCoord(), false);
                    departurePlatform.setStopAreaId(facility.getStopAreaId());
                    schedule.addStopFacility(departurePlatform);
                    facility = departurePlatform;
                }
                double offset = index * segmentTime;
                TransitRouteStop stop = factory.createTransitRouteStop(facility, offset, offset);
                stop.setAwaitDepartureTime(false);
                stops.add(stop);
            }
            TransitRoute route = factory.createTransitRoute(
                    Id.create(lineId + "-" + (direction + 1), TransitRoute.class), null, stops, TransportMode.pt);
            for (double time = PT_OPERATION_START_TIME; time < PT_OPERATION_END_TIME; time += PT_HEADWAY) {
                route.addDeparture(factory.createDeparture(Id.create(String.valueOf(time), Departure.class), time));
            }
            line.addRoute(route);
        }
        schedule.addTransitLine(line);
    }
}
