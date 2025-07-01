package de.tum.bgu.msm.health;

import de.tum.bgu.msm.health.injury.AccidentsContext;
import de.tum.bgu.msm.health.injury.AnalysisEventHandler;
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
    public static void main(String[] args) {
        // Read in network
        String networkFile = "/mnt/usb-TOSHIBA_EXTERNAL_USB_20241124015626F-0:0-part1/manchester/scenOutput/base/matsim/2021/sunday/car/2021.output_network.xml.gz";

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
                    osmLink.computeAttributes(); // Compute existing attributes
                    computeDemandAttributes(osmLink); // Compute demand-related attributes
                    return osmLink;
                })
                .collect(Collectors.toList());

        // Optional: Output or further process osmLinks
        System.out.println("Processed " + osmLinks.size() + " OsmLinks.");
        // Example: Print attributes of first OsmLink for verification
        for(int k=0; k<10; k++){
            if (!osmLinks.isEmpty()) {
                OsmLink sample = osmLinks.get(0);
                System.out.println("Sample OsmLink ID: " + sample.osmId);
                System.out.println("Bike Allowed: " + sample.bikeAllowed);
                System.out.println("Car Allowed: " + sample.carAllowed);
                System.out.println("Walk Allowed: " + sample.walkAllowed);
                System.out.println("Total Length: " + sample.lengthSum);
                System.out.println("Width: " + sample.width);
                System.out.println("Bike Stress: " + sample.bikeStress);
                System.out.println("Bike Stress Jct: " + sample.bikeStressJct);
                System.out.println("Walk Stress Jct: " + sample.walkStressJct);
                System.out.println("Car Hourly Demand: " + sample.carHourlyDemand);
                System.out.println("Truck Hourly Demand: " + sample.truckHourlyDemand);
                System.out.println("Ped Hourly Demand: " + sample.pedHourlyDemand);
                System.out.println("Bike Hourly Demand: " + sample.bikeHourlyDemand);
                System.out.println("Motor Hourly Demand: " + sample.motorHourlyDemand);
            }
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

    private static void computeDemandAttributes(OsmLink osmLink) {
        Set<Link> links = osmLink.getNetworkLinks();
        if (links.isEmpty()) {
            osmLink.carHourlyDemand = 0.0;
            osmLink.truckHourlyDemand = 0.0;
            osmLink.pedHourlyDemand = 0.0;
            osmLink.bikeHourlyDemand = 0.0;
            osmLink.motorHourlyDemand = 0.0;
            return;
        }

        double carSum = 0.0;
        double truckSum = 0.0;
        double pedSum = 0.0;
        double bikeSum = 0.0;
        int count = links.size();

        for (Link link : links) {
            carSum += getDoubleAttribute(link, "carFlow", 0.0);
            truckSum += getDoubleAttribute(link, "truckFlow", 0.0);
            pedSum += getDoubleAttribute(link, "pedFlow", 0.0);
            bikeSum += getDoubleAttribute(link, "bikeFlow", 0.0);
        }

        osmLink.carHourlyDemand = carSum / count;
        osmLink.truckHourlyDemand = truckSum / count;
        osmLink.pedHourlyDemand = pedSum / count;
        osmLink.bikeHourlyDemand = bikeSum / count;
        osmLink.motorHourlyDemand = osmLink.carHourlyDemand + osmLink.truckHourlyDemand;
    }

    private static double getDoubleAttribute(Link link, String key, double defaultVal) {
        Object attr = link.getAttributes().getAttribute(key);
        return attr instanceof Number ? ((Number) attr).doubleValue() : defaultVal;
    }
}