package de.tum.bgu.msm.health;

import de.tum.bgu.msm.health.injury.AccidentsContext;
import de.tum.bgu.msm.health.injury.AnalysisEventHandler;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.*;
import java.util.stream.Collectors;

public class RunAggregateAccidentModel {
    // Define SCALEFACTOR
    private static final double SCALEFACTOR = 10.0; // Adjust as needed

    public static void main(String[] args) {
        // Read in network
        //String networkFile = "/mnt/usb-TOSHIBA_EXTERNAL_USB_20241124015626F-0:0-part1/manchester/scenOutput/base/matsim/2021/sunday/car/2021.output_network.xml.gz";
        String networkFile = "/mnt/usb-TOSHIBA_EXTERNAL_USB_20241124015626F-0:0-part1/manchester/input/mito/trafficAssignment/network(1).xml";

        // Create scenario
        Config config = ConfigUtils.createConfig();
        config.network().setInputFile(networkFile);
        Scenario scenario = ScenarioUtils.loadScenario(config);

        // AccidentContext
        AccidentsContext accidentsContext = new AccidentsContext();

        // Read in carTruck/bikePed traffic volumes
        String eventsFileCarTruck = "/mnt/usb-TOSHIBA_EXTERNAL_USB_20241124015626F-0:0-part1/manchester/scenOutput/base/matsim/2021/sunday/car/2021.output_events.xml.gz";
        String eventsFileBikePed = "/mnt/usb-TOSHIBA_EXTERNAL_USB_20241124015626F-0:0-part1/manchester/scenOutput/base/matsim/2021/sunday/bikePed/2021.output_events.xml.gz";

        AnalysisEventHandler analysisEventHandler = new AnalysisEventHandler();
        analysisEventHandler.setScenario(scenario);
        analysisEventHandler.setAccidentsContext(accidentsContext);
        EventsManagerImpl events = new EventsManagerImpl();
        MatsimEventsReader eventsReader = new MatsimEventsReader(events);

        events.addHandler(analysisEventHandler);
        eventsReader.readFile(eventsFileCarTruck); // carTruck
        eventsReader.readFile(eventsFileBikePed); // bikePed

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
                    computeDemandAttributes(osmLink, analysisEventHandler);
                    return osmLink;
                })
                .collect(Collectors.toList());

        // Output 10 samples for verification
        System.out.println("Processed " + osmLinks.size() + " OsmLinks.");
        if (!osmLinks.isEmpty()) {
            // Select up to 10 OsmLinks
            List<OsmLink> samples = osmLinks.stream().limit(10).collect(Collectors.toList());
            // Choose a specific hour for output (e.g., hour 8)

            for (OsmLink sample : samples) {
                System.out.println("OsmLink ID: " + sample.osmId);
                System.out.println("Road Type: " + sample.roadType);
                System.out.println("Highway: " + sample.highway);
                System.out.println("One Way: " + sample.onwysmm);
                System.out.println("Speed Limit (MPH): " + sample.speedLimitMPH);
                System.out.println("Bike Allowed: " + sample.bikeAllowed);
                System.out.println("Car Allowed: " + sample.carAllowed);
                System.out.println("Walk Allowed: " + sample.walkAllowed);
                System.out.println("Total Length (m): " + sample.lengthSum);
                System.out.println("Width (m): " + sample.width);
                System.out.println("Bike Stress: " + sample.bikeStress);
                System.out.println("Bike Stress Jct: " + sample.bikeStressJct);
                System.out.println("Walk Stress Jct: " + sample.walkStressJct);
                for(int outputHour=0; outputHour<24; outputHour++){
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
            System.out.println("No OsmLinks available to display.");
        }
    }

    private static int getOsmId(Link link) {
        Object osmIdAttr = link.getAttributes().getAttribute("osmID");
        if (osmIdAttr instanceof String) {
            try {
                return Integer.parseInt((String) osmIdAttr);
            } catch (NumberFormatException e) {
                return 0; // Default or handle invalid osmId
            }
        } else if (osmIdAttr instanceof Number) {
            return ((Number) osmIdAttr).intValue();
        }
        return 0; // Default if osmId is missing or invalid
    }

    private static void computeDemandAttributes(OsmLink osmLink, AnalysisEventHandler analyzer) {
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
                double carDemand = analyzer.getDemand(link.getId(), "car", hour);
                double truckDemand = analyzer.getDemand(link.getId(), "truck", hour);
                double pedDemand = analyzer.getDemand(link.getId(), "walk", hour);
                double bikeDemand = analyzer.getDemand(link.getId(), "bike", hour);

                // Handle NaN values
                carSums[hour] += Double.isNaN(carDemand) ? 0.0 : carDemand * SCALEFACTOR;
                truckSums[hour] += Double.isNaN(truckDemand) ? 0.0 : truckDemand * SCALEFACTOR;
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
}