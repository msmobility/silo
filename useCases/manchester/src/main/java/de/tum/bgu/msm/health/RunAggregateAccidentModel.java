package de.tum.bgu.msm.health;

import de.tum.bgu.msm.health.injury.AccidentSeverity;
import de.tum.bgu.msm.health.injury.AccidentType;
import de.tum.bgu.msm.health.injury.AccidentsContext;
import de.tum.bgu.msm.health.injury.AnalysisEventHandler2;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.MatsimVehicleReader;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleUtils;
import org.matsim.vehicles.Vehicles;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RunAggregateAccidentModel {
    private static final double SCALEFACTOR = 10.0; // Adjust as needed

    public static void main(String[] args) {
        // Read in network
        String networkFile = "/mnt/usb-TOSHIBA_EXTERNAL_USB_20241124015626F-0:0-part1/manchester/input/mito/trafficAssignment/network(1).xml";

        // Create scenario
        Config config = ConfigUtils.createConfig();
        config.network().setInputFile(networkFile);
        Scenario scenario = ScenarioUtils.loadScenario(config);

        // Vehicles
        String vehicleFileCar = "/mnt/usb-TOSHIBA_EXTERNAL_USB_20241124015626F-0:0-part1/manchester/scenOutput/base/matsim/2021/sunday/car/2021.output_vehicles.xml.gz";
        String vehicleFileBikePed = "/mnt/usb-TOSHIBA_EXTERNAL_USB_20241124015626F-0:0-part1/manchester/scenOutput/base/matsim/2021/sunday/bikePed/2021.output_vehicles.xml.gz";

        // Create separate vehicle containers
        Vehicles vehiclesMotorized = VehicleUtils.createVehiclesContainer();
        Vehicles vehiclesNonMotorized = VehicleUtils.createVehiclesContainer();

        try {
            // Read car/truck vehicles
            new MatsimVehicleReader(vehiclesMotorized).readFile(vehicleFileCar);
            System.out.println("Motorized vehicles loaded: " + vehiclesMotorized.getVehicles().size());

            // Read bike/ped vehicles
            new MatsimVehicleReader(vehiclesNonMotorized).readFile(vehicleFileBikePed);
            System.out.println("Non-motorized vehicles loaded: " + vehiclesNonMotorized.getVehicles().size());

            // Validate vehicle IDs for each container
            validateVehicleIds(vehiclesMotorized, "Motorized");
            validateVehicleIds(vehiclesNonMotorized, "Non-motorized");

        } catch (Exception e) {
            System.err.println("Error reading vehicle files: " + e.getMessage());
            e.printStackTrace();
        }

        // AccidentContext
        //AccidentsContext accidentsContext = new AccidentsContext();

        // Read in car/truck and bike/ped traffic volumes
        String eventsFileCarTruck = "/mnt/usb-TOSHIBA_EXTERNAL_USB_20241124015626F-0:0-part1/manchester/scenOutput/base/matsim/2021/sunday/car/2021.output_events.xml.gz";
        String eventsFileBikePed = "/mnt/usb-TOSHIBA_EXTERNAL_USB_20241124015626F-0:0-part1/manchester/scenOutput/base/matsim/2021/sunday/bikePed/2021.output_events.xml.gz";

        // Create separate event handlers
        AnalysisEventHandlerMotorized motorizedHandler = new AnalysisEventHandlerMotorized(vehiclesMotorized, scenario);
        AnalysisEventHandlerNonMotorized nonMotorizedHandler = new AnalysisEventHandlerNonMotorized(vehiclesNonMotorized, scenario);

        // Set AccidentsContext
        // motorizedHandler.setAccidentsContext(accidentsContext);
        // nonMotorizedHandler.setAccidentsContext(accidentsContext);

        // Process events
        EventsManagerImpl events = new EventsManagerImpl();
        MatsimEventsReader eventsReader = new MatsimEventsReader(events);

        events.addHandler(motorizedHandler);
        events.addHandler(nonMotorizedHandler);

        // Read event files separately
        System.out.println("Processing motorized events...");
        eventsReader.readFile(eventsFileCarTruck); // car/truck
        System.out.println("Processing non-motorized events...");
        eventsReader.readFile(eventsFileBikePed); // bike/ped

        // Process network
        Map<Integer, Set<Link>> linksByOsmId = scenario.getNetwork().getLinks().values().stream()
                .collect(Collectors.groupingBy(
                        link -> getOsmId(link),
                        Collectors.toSet()
                ));

        // Create OsmLink instances and compute attributes
        List<OsmLink> osmLinks = linksByOsmId.entrySet().stream()
                .map(entry -> {
                    OsmLink osmLink = new OsmLink(entry.getKey(), entry.getValue());
                    osmLink.computeAttributes();
                    computeDemandAttributes(osmLink, motorizedHandler, nonMotorizedHandler);
                    return osmLink;
                })
                .collect(Collectors.toList());

        // TODO: check probability of casualty by OSMlink here
        String basePath = "/mnt/usb-TOSHIBA_EXTERNAL_USB_20241124015626F-0:0-part1/manchester/input/accident/";
        CasualtyRateCalculationOsmMCR casualtyRateCalculationOsmMCR = new CasualtyRateCalculationOsmMCR(
                new AccidentsContext(),
                AccidentType.PED,
                AccidentSeverity.SEVEREFATAL,
                basePath);

        // loop over OSM links from list osmLinks
        // casualtyRateCalculationOsmMCR.calculateProbability(osmLink, 6);

        // method that takes list of osm links witj
        // osmLinks.get(0).networkLinks



        // Define the set of target osmIDs
        Set<Integer> targetOsmIds = Set.of(1111480, 2219688, 2246322, 2246324, 2270660, 3662033, 3663335, 3697915, 3697942, 3988635, 3991625, 3991626, 3991667, 3991668, 3991962, 3994226, 3994227, 3994228, 4013498, 4018897, 4018898);

        // Output results to CSV
        generateOsmLinksCsv(osmLinks, targetOsmIds, "osmLinks_output.csv");

        // Print results for verification
        System.out.println("The aggregated network has " + osmLinks.size() + " osmLinks.");
        if (!osmLinks.isEmpty()) {
            List<OsmLink> samples = osmLinks.stream()
                    .filter(link -> targetOsmIds.contains(link.osmId))
                    .collect(Collectors.toList());

            if (!samples.isEmpty()) {
                for (OsmLink sample : samples) {
                    System.out.println("OsmLink ID: " + sample.osmId);
                    System.out.println("Road Type: " + sample.roadType);
                    System.out.println("One Way ?: " + sample.onwysmm);
                    System.out.println("Speed Limit (MPH): " + sample.speedLimitMPH);
                    System.out.println("Bike Allowed: " + sample.bikeAllowed);
                    System.out.println("Car Allowed: " + sample.carAllowed);
                    System.out.println("Walk Allowed: " + sample.walkAllowed);
                    System.out.println("Total Length (m): " + sample.lengthSum);
                    System.out.println("Width (m): " + sample.width);
                    System.out.println("Bike Stress: " + sample.bikeStress);
                    System.out.println("Bike Stress Jct: " + sample.bikeStressJct);
                    System.out.println("Walk Stress Jct: " + sample.walkStressJct);

                    for (int outputHour = 0; outputHour < 24; outputHour++) {
                        System.out.println("Car Hourly Demand (Hour " + outputHour + "): " + sample.carHourlyDemand[outputHour]);
                        System.out.println("Truck Hourly Demand (Hour " + outputHour + "): " + sample.truckHourlyDemand[outputHour]);
                        System.out.println("Ped Hourly Demand (Hour " + outputHour + "): " + sample.pedHourlyDemand[outputHour]);
                        System.out.println("Bike Hourly Demand (Hour " + outputHour + "): " + sample.bikeHourlyDemand[outputHour]);
                        System.out.println("Motor Hourly Demand (Hour " + outputHour + "): " + sample.motorHourlyDemand[outputHour]);
                        System.out.println("*******");
                    }
                    System.out.println("------------------------");
                }
            } else {
                System.out.println("No OsmLinks found for the specified osmIDs.");
            }
        } else {
            System.out.println("No OsmLinks available to display.");
        }
    }

    // Subclass for motorized events
    static class AnalysisEventHandlerMotorized extends AnalysisEventHandler2 {
        public AnalysisEventHandlerMotorized(Vehicles vehicles, Scenario scenario) {
            super(vehicles, scenario);
        }

        @Override
        public void handleEvent(LinkEnterEvent event) {
            Id<Vehicle> vehicleId = event.getVehicleId();
            Vehicle vehicle = vehicles.getVehicles().get(vehicleId);
            if (vehicle != null) {
                String mode = vehicle.getType() != null ? vehicle.getType().getNetworkMode() : "car";
                if (mode.equals("car") || mode.equals("truck")) {
                    super.handleEvent(event);
                }
            }
        }
    }

    // Subclass for non-motorized events
    static class AnalysisEventHandlerNonMotorized extends AnalysisEventHandler2 {
        public AnalysisEventHandlerNonMotorized(Vehicles vehicles, Scenario scenario) {
            super(vehicles, scenario);
        }

        @Override
        public void handleEvent(LinkEnterEvent event) {
            Id<Vehicle> vehicleId = event.getVehicleId();
            Vehicle vehicle = vehicles.getVehicles().get(vehicleId);
            if (vehicle != null) {
                String mode = vehicle.getType() != null ? vehicle.getType().getNetworkMode() : null;
                if (mode != null && (mode.equals("bike") || mode.equals("walk"))) {
                    super.handleEvent(event);
                }
            }
        }
    }

    private static void validateVehicleIds(Vehicles vehicles, String type) {
        vehicles.getVehicles().keySet().stream()
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .forEach(entry -> System.err.println("Duplicate " + type + " vehicle ID found: " + entry.getKey()));
        System.out.println(type + " vehicle IDs validated. Total: " + vehicles.getVehicles().size());
    }

    private static int getOsmId(Link link) {
        Object osmIdAttr = link.getAttributes().getAttribute("osmID");
        if (osmIdAttr instanceof String) {
            try {
                return Integer.parseInt((String) osmIdAttr);
            } catch (NumberFormatException e) {
                return 0;
            }
        } else if (osmIdAttr instanceof Number) {
            return ((Number) osmIdAttr).intValue();
        }
        return 0;
    }

    private static void computeDemandAttributes(OsmLink osmLink, AnalysisEventHandler2 motorizedHandler, AnalysisEventHandler2 nonMotorizedHandler) {
        Set<Link> links = osmLink.getNetworkLinks();
        if (links.isEmpty()) {
            Arrays.fill(osmLink.carHourlyDemand, 0.0);
            Arrays.fill(osmLink.truckHourlyDemand, 0.0);
            Arrays.fill(osmLink.pedHourlyDemand, 0.0);
            Arrays.fill(osmLink.bikeHourlyDemand, 0.0);
            Arrays.fill(osmLink.motorHourlyDemand, 0.0);
            return;
        }

        int count = links.size();
        double[] carSums = new double[24];
        double[] truckSums = new double[24];
        double[] pedSums = new double[24];
        double[] bikeSums = new double[24];

        for (Link link : links) {
            for (int hour = 0; hour < 24; hour++) {
                double carDemand = motorizedHandler.getDemand(link.getId(), "car", hour) * SCALEFACTOR;
                double truckDemand = motorizedHandler.getDemand(link.getId(), "truck", hour) * SCALEFACTOR;
                double pedDemand = nonMotorizedHandler.getDemand(link.getId(), "walk", hour);
                double bikeDemand = nonMotorizedHandler.getDemand(link.getId(), "bike", hour);

                carSums[hour] += Double.isNaN(carDemand) ? 0.0 : carDemand;
                truckSums[hour] += Double.isNaN(truckDemand) ? 0.0 : truckDemand;
                pedSums[hour] += Double.isNaN(pedDemand) ? 0.0 : pedDemand;
                bikeSums[hour] += Double.isNaN(bikeDemand) ? 0.0 : bikeDemand;
            }
        }

        for (int hour = 0; hour < 24; hour++) {
            osmLink.carHourlyDemand[hour] = carSums[hour] / count;
            osmLink.truckHourlyDemand[hour] = truckSums[hour] / count;
            osmLink.pedHourlyDemand[hour] = pedSums[hour] / count;
            osmLink.bikeHourlyDemand[hour] = bikeSums[hour] / count;
            osmLink.motorHourlyDemand[hour] = osmLink.carHourlyDemand[hour] + osmLink.truckHourlyDemand[hour];
        }
    }

    private static double getDoubleAttribute(Link link, String key, double defaultVal) {
        Object attr = link.getAttributes().getAttribute(key);
        return attr instanceof Number ? ((Number) attr).doubleValue() : defaultVal;
    }

    public static void generateOsmLinksCsv(List<OsmLink> osmLinks, Set<Integer> targetOsmIds, String filePath) {
        System.out.println("The aggregated network has " + osmLinks.size() + " osmLinks.");
        if (!osmLinks.isEmpty()) {
            List<OsmLink> samples = osmLinks.stream()
                    .filter(link -> targetOsmIds.contains(link.osmId))
                    .collect(Collectors.toList());

            if (!samples.isEmpty()) {
                try (FileWriter writer = new FileWriter(filePath)) {
                    StringBuilder header = new StringBuilder();
                    header.append("osmId,roadType,onwysmm,speedLimitMPH,bikeAllowed,carAllowed,walkAllowed,lengthSum,width,bikeStress,bikeStressJct,walkStressJct");
                    for (int hour = 0; hour < 24; hour++) {
                        header.append(",carHourlyDemand_").append(hour)
                                .append(",truckHourlyDemand_").append(hour)
                                .append(",pedHourlyDemand_").append(hour)
                                .append(",bikeHourlyDemand_").append(hour)
                                .append(",motorHourlyDemand_").append(hour);
                    }
                    header.append("\n");
                    writer.write(header.toString());

                    for (OsmLink sample : samples) {
                        StringBuilder row = new StringBuilder();
                        row.append(sample.osmId).append(",")
                                .append(escapeCsv(sample.roadType)).append(",")
                                .append(sample.onwysmm).append(",")
                                .append(sample.speedLimitMPH).append(",")
                                .append(sample.bikeAllowed).append(",")
                                .append(sample.carAllowed).append(",")
                                .append(sample.walkAllowed).append(",")
                                .append(sample.lengthSum).append(",")
                                .append(sample.width).append(",")
                                .append(sample.bikeStress).append(",")
                                .append(sample.bikeStressJct).append(",")
                                .append(sample.walkStressJct);

                        for (int hour = 0; hour < 24; hour++) {
                            row.append(",").append(sample.carHourlyDemand[hour])
                                    .append(",").append(sample.truckHourlyDemand[hour])
                                    .append(",").append(sample.pedHourlyDemand[hour])
                                    .append(",").append(sample.bikeHourlyDemand[hour])
                                    .append(",").append(sample.motorHourlyDemand[hour]);
                        }
                        row.append("\n");
                        writer.write(row.toString());
                    }
                    System.out.println("CSV file '" + filePath + "' generated successfully.");
                } catch (IOException e) {
                    System.err.println("Error writing CSV file: " + e.getMessage());
                }
            } else {
                System.out.println("No OsmLinks found for the specified osmIDs.");
            }
        } else {
            System.out.println("No OsmLinks available to display.");
        }
    }

    public static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}