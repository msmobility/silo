package de.tum.bgu.msm.health;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.health.injury.*;
import de.tum.bgu.msm.properties.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.accidents.AccidentsModule;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Injector;
import org.matsim.core.events.EventsManagerModule;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioByInstanceModule;
import org.matsim.vehicles.MatsimVehicleReader;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleUtils;
import org.matsim.vehicles.Vehicles;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class AccidentRateModelOsmMCR {
    private static final Logger log = LogManager.getLogger(AccidentRateModelOsmMCR.class);
    private final Scenario scenario;
    private final float scaleFactor;
    private final Day day;
    private final AccidentsContext accidentsContext = new AccidentsContext();
    private AnalysisEventHandlerMotorized motorizedHandler;
    private AnalysisEventHandlerNonMotorized nonMotorizedHandler;
    private int counterCar;
    private int counterBikePed;
    private List<OsmLink> osmLinks;
    private Properties properties;

    private static final Set<AccidentType> ACCIDENT_TYPES_EXCLUDED = Set.of(AccidentType.CAR, AccidentType.BIKECAR, AccidentType.BIKEBIKE);
    private static final Set<AccidentSeverity> ACCIDENT_SEVERITIES_EXCLUDED = Set.of(AccidentSeverity.LIGHT);
    public static final Set<String> MAJOR = Set.of(
            "primary", "primary_link", "secondary", "secondary_link",
            "tertiary", "tertiary_link", "trunk", "trunk_link", "bus_guideway", "cycleway","motorway","motorway_link"
    );
    public static final Set<String> MINOR = Set.of(
            "unclassified", "residential", "living_street", "service",
            "pedestrian", "track", "footway", "bridleway", "steps",
            "path", "road"
    );

    // TODO: adjust
    private static final double VEHICLE_SCALE_FACTOR = 10.0;

    public AccidentRateModelOsmMCR(Properties properties, Scenario scenario, float scaleFactor, Day day) {
        this.properties = properties;
        this.scenario = scenario;
        this.scaleFactor = scaleFactor;
        this.day = day;
    }

    public void runCasualtyRateMCR() {
        // Initialize injector
        com.google.inject.Injector injector = Injector.createInjector(scenario.getConfig(), new AbstractModule() {
            @Override
            public void install() {
                install(new ScenarioByInstanceModule(scenario));
                install(new AccidentsModule());
                install(new EventsManagerModule());
            }
        });

        // Read network
        //networkFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + this.scenario.getConfig().controller().getRunId() + ".output_network.xml.gz";
        log.info("Reading network file...");
        /*
        String networkFile = scenario.getConfig().network().getInputFile();
        if (networkFile == null) {
            networkFile = "/mnt/usb-TOSHIBA_EXTERNAL_USB_20241124015626F-0:0-part1/manchester/input/mito/trafficAssignment/network(1).xml";
        }

         */
        // TODO: generalize for other networks
        String networkFile = properties.main.baseDirectory + "input/mito/trafficAssignment/" + "network(1).xml";
        new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
        log.info("Reading network file... Done.");

        // Set accidentContext
        // links
        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            AccidentLinkInfo info = new AccidentLinkInfo(link.getId());
            this.accidentsContext.getLinkId2info().put(link.getId(), info);
        }
        log.info("Initializing all link-specific information... Done.");

        // Read vehicles
        Vehicles vehiclesMotorized = VehicleUtils.createVehiclesContainer();
        Vehicles vehiclesNonMotorized = VehicleUtils.createVehiclesContainer();
        readVehicles(vehiclesMotorized, vehiclesNonMotorized);

        // Initialize event handlers and read events
        setupEventHandlers(vehiclesMotorized, vehiclesNonMotorized);
        readEvents(injector, "car", "output_events.xml.gz");
        readEvents(injector, "bikePed", "output_events.xml.gz");

        // Aggregate network by OSM ID and compute attributes
        initializeOsmLinks();

        // Calculate casualty frequency at OSM level
        calculateCasualtyFrequency();

        // Compute link-level injury risk
        // computeLinkLevelInjuryRisk();

        // Clean up
        motorizedHandler.reset(0);
        nonMotorizedHandler.reset(0);
        System.gc();
    }

    private void readVehicles(Vehicles vehiclesMotorized, Vehicles vehiclesNonMotorized) {
        log.info("Reading vehicle files...");
        String vehicleFileCar = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + this.scenario.getConfig().controller().getRunId() + ".output_vehicles.xml.gz";
        String vehicleFileBikePed = this.scenario.getConfig().controller().getOutputDirectory() + "bikePed/" + this.scenario.getConfig().controller().getRunId() + ".output_vehicles.xml.gz";

        //String vehicleFileCar = "/mnt/usb-TOSHIBA_EXTERNAL_USB_20241124015626F-0:0-part1/manchester/scenOutput/base/matsim/2021/sunday/car/2021.output_vehicles.xml.gz";
        //String vehicleFileBikePed = "/mnt/usb-TOSHIBA_EXTERNAL_USB_20241124015626F-0:0-part1/manchester/scenOutput/base/matsim/2021/sunday/bikePed/2021.output_vehicles.xml.gz";
        try {
            new MatsimVehicleReader(vehiclesMotorized).readFile(vehicleFileCar);
            new MatsimVehicleReader(vehiclesNonMotorized).readFile(vehicleFileBikePed);
            validateVehicleIds(vehiclesMotorized, "Motorized");
            validateVehicleIds(vehiclesNonMotorized, "Non-motorized");
        } catch (Exception e) {
            log.error("Error reading vehicle files", e);
        }
        log.info("Vehicle files loaded.");
    }

    private void validateVehicleIds(Vehicles vehicles, String type) {
        vehicles.getVehicles().keySet().stream()
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .forEach(entry -> log.warn("Duplicate {} vehicle ID found: {}", type, entry.getKey()));
    }

    private void setupEventHandlers(Vehicles vehiclesMotorized, Vehicles vehiclesNonMotorized) {
        log.info("Setting up event handlers...");
        motorizedHandler = new AnalysisEventHandlerMotorized(vehiclesMotorized, scenario);
        nonMotorizedHandler = new AnalysisEventHandlerNonMotorized(vehiclesNonMotorized, scenario);
        //motorizedHandler.setAccidentsContext(accidentsContext);
        //nonMotorizedHandler.setAccidentsContext(accidentsContext);
        log.info("Event handlers set up.");
    }

    private String getOutputFilePath(String mode, String fileName) {
        String runId = scenario.getConfig().controller().getRunId();
        String basePath = scenario.getConfig().controller().getOutputDirectory() + mode + "/";
        return runId == null || runId.isEmpty() ? basePath + fileName : basePath + runId + "." + fileName;
    }

    private void readEvents(com.google.inject.Injector injector, String mode, String fileName) {
        log.info("Reading {} events file...", mode);
        EventsManager events = injector.getInstance(EventsManager.class);
        events.addHandler(motorizedHandler);
        events.addHandler(nonMotorizedHandler);
        new MatsimEventsReader(events).readFile(getOutputFilePath(mode, fileName));
        log.info("Reading {} events file... Done.", mode);
    }

    private void initializeOsmLinks() {
        log.info("Aggregating network by OSM ID...");
        Map<Integer, Set<Link>> linksByOsmId = scenario.getNetwork().getLinks().values().stream()
                .collect(Collectors.groupingBy(
                        link -> getOsmId(link),
                        Collectors.toSet()
                ));

        osmLinks = linksByOsmId.entrySet().stream()
                .map(entry -> {
                    OsmLink osmLink = new OsmLink(entry.getKey(), entry.getValue());
                    osmLink.computeAttributes();
                    computeDemandAttributes(osmLink);
                    //initializeLinkInfo(osmLink);
                    return osmLink;
                })
                .collect(Collectors.toList());
        log.info("Network aggregated. {} OsmLinks created.", osmLinks.size());
    }

    private void initializeLinkInfo(OsmLink osmLink) {
        for (Link link : osmLink.getNetworkLinks()) {
            accidentsContext.getLinkId2info().put(link.getId(), new AccidentLinkInfo(link.getId()));
        }
    }

    private int getOsmId(Link link) {
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

    private void computeDemandAttributes(OsmLink osmLink) {
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
                double carDemand = motorizedHandler.getDemand(link.getId(), "car", hour) * VEHICLE_SCALE_FACTOR;
                double truckDemand = motorizedHandler.getDemand(link.getId(), "truck", hour) * VEHICLE_SCALE_FACTOR;
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

    private void calculateCasualtyFrequency() {
        log.info("Link casualty frequency calculation (by type by time of day) start.");
        Random random = new Random();
        double calibrationFactor = day == Day.thursday || day == Day.saturday || day == Day.sunday ? 2.19 : 2.19;
        String basePath = scenario.getScenarioElement("accidentModelFile").toString();

        for (AccidentType accidentType : AccidentType.values()) {
            if (ACCIDENT_TYPES_EXCLUDED.contains(accidentType)) continue;
            for (AccidentSeverity accidentSeverity : AccidentSeverity.values()) {
                if (ACCIDENT_SEVERITIES_EXCLUDED.contains(accidentSeverity)) continue;
                CasualtyRateCalculationOsmMCR calculator = new CasualtyRateCalculationOsmMCR(
                        accidentsContext,
                        accidentType,
                        accidentSeverity,
                        basePath);
                List<OsmLink> relevantOsmLinks = extractOsmLinksSpecific(osmLinks, accidentType);
                calculator.run(relevantOsmLinks, random);
                log.info("Calculating {} {} crash rate done.", accidentType, accidentSeverity);
            }
        }
        log.info("Link casualty frequency calculation completed.");

        try {
            writeOutCasualtyRate();
        } catch (FileNotFoundException e) {
            log.error("Error writing casualty rates", e);
        }
    }

    private List<OsmLink> extractOsmLinksSpecific(List<OsmLink> osmLinks, AccidentType accidentType) {
        return osmLinks.stream().filter(osmLink -> {
            switch (accidentType) {
                case PED:
                    return osmLink.walkAllowed;
                case CAR_ONEWAY:
                    return osmLink.onwysmm && osmLink.carAllowed;
                case CAR_TWOWAY:
                    return !osmLink.onwysmm && osmLink.carAllowed;
                case BIKE_MINOR:
                    return MINOR.contains(osmLink.roadType) && osmLink.bikeAllowed;
                case BIKE_MAJOR:
                    return MAJOR.contains(osmLink.roadType) && osmLink.bikeAllowed;
                default:
                    return false;
            }
        }).collect(Collectors.toList());
    }

    private void computeLinkLevelInjuryRisk() {
        log.info("Link casualty exposure calculation start.");
        for (OsmLink osmLink : osmLinks) {
            computeLinkCasualtyExposureMCR(osmLink);
        }
        log.info("{} car links have no hourly traffic volume", counterCar);
        log.info("{} bike/ped links have no hourly traffic volume", counterBikePed);
        log.info("Link casualty exposure calculation completed.");

        try {
            writeOutExposure();
        } catch (FileNotFoundException e) {
            log.error("Error writing exposure data", e);
        }
    }

    private void computeLinkCasualtyExposureMCR(OsmLink osmLink) {
        for (AccidentType accidentType : AccidentType.values()) {
            String mode = getModeForAccidentType(accidentType);
            if ("null".equals(mode)) continue;

            for (Link link : osmLink.getNetworkLinks()) {
                OpenIntFloatHashMap severeCasualtyExposureByTime = new OpenIntFloatHashMap();
                for (int hour = 0; hour < 24; hour++) {
                    float severeCasualty = getSevereCasualty(link.getId(), accidentType, hour);
                    float exposure = calculateExposure(osmLink, mode, hour, severeCasualty);
                    severeCasualtyExposureByTime.put(hour, exposure);
                }
                accidentsContext.getLinkId2info().get(link.getId())
                        .getSevereFatalCasualityExposureByAccidentTypeByTime()
                        .put(accidentType, severeCasualtyExposureByTime);
            }
        }
    }

    private String getModeForAccidentType(AccidentType accidentType) {
        switch (accidentType) {
            case CAR_TWOWAY:
            case CAR_ONEWAY:
                return "car";
            case PED:
                return "walk";
            case BIKE_MAJOR:
            case BIKE_MINOR:
                return "bike";
            default:
                return "null";
        }
    }

    private float getSevereCasualty(Id<Link> linkId, AccidentType accidentType, int hour) {
        OpenIntFloatHashMap timeMap = accidentsContext.getLinkId2info()
                .get(linkId)
                .getSevereFatalCasualityExposureByAccidentTypeByTime()
                .get(accidentType);
        return timeMap != null ? timeMap.get(hour) : 0.0f;
    }

    private float calculateExposure(OsmLink osmLink, String mode, int hour, float severeCasualty) {
        double demand = mode.equals("car") ? osmLink.carHourlyDemand[hour] :
                mode.equals("walk") ? osmLink.pedHourlyDemand[hour] :
                        osmLink.bikeHourlyDemand[hour];
        if (demand == 0) {
            if (severeCasualty == 1) {
                log.warn("A casualty was predicted in a link with no {} flows: OSM ID {}", mode, osmLink.osmId);
            }
            if (mode.equals("car")) counterCar++;
            else counterBikePed++;
            return 0.0f;
        }
        return mode.equals("car") ?
                (float) (severeCasualty / (demand * scaleFactor)) :
                (float) (severeCasualty / demand);
    }

    public void writeOutCasualtyRate() throws FileNotFoundException {
        String outputPath = scenario.getConfig().controller().getOutputDirectory() + "casualtyRatesW.csv";
        StringBuilder data = new StringBuilder("osmId,linkId,accidentType,casualty\n");

        for (OsmLink osmLink : osmLinks) {
            for (Link link : osmLink.getNetworkLinks()) {
                for (AccidentType accidentType : AccidentType.values()) {
                    if (ACCIDENT_TYPES_EXCLUDED.contains(accidentType)) continue;
                    for (AccidentSeverity accidentSeverity : AccidentSeverity.values()) {
                        if (ACCIDENT_SEVERITIES_EXCLUDED.contains(accidentSeverity)) continue;

                    /*
                    double totalCasualty = osmLink.getNetworkLinks().stream()
                            .mapToDouble(link -> calculateTotalCasualty(link.getId(), accidentType))
                            .sum();

                     */

                        if (accidentsContext.getLinkId2info().get(link.getId()).getSevereFatalCasualityExposureByAccidentTypeByTime().get(accidentType) != null) {
                            double totalCasualty = 0;
                            for (int hour = 0; hour < 24; hour++) {

                                totalCasualty += accidentsContext.getLinkId2info().get(link.getId()).getSevereFatalCasualityExposureByAccidentTypeByTime().get(accidentType).get(hour);
                            }
                                if (totalCasualty > 0) {
                                    data.append(String.format("%d,%s,%s,%.2f\n", osmLink.osmId, link.getId().toString(), accidentType.name(), totalCasualty));
                                }

                        }
                    }
                }
            }
        }
        writeToFile(outputPath, data.toString());
    }

    private double calculateTotalCasualty(Id<Link> linkId, AccidentType accidentType) {
        OpenIntFloatHashMap timeMap = accidentsContext.getLinkId2info()
                .get(linkId)
                .getSevereFatalCasualityExposureByAccidentTypeByTime()
                .get(accidentType);
        if (timeMap == null) return 0.0;
        double total = 0.0;
        for (int hour = 0; hour < 24; hour++) {
            total += timeMap.get(hour);
        }
        return total;
    }

    public void writeOutExposure() throws FileNotFoundException {
        String outputPath = scenario.getConfig().controller().getOutputDirectory() + "linkExposure.csv";
        StringBuilder data = new StringBuilder("osmId,accidentType,exposure\n");

        for (OsmLink osmLink : osmLinks) {
            for (AccidentType accidentType : AccidentType.values()) {
                if (ACCIDENT_TYPES_EXCLUDED.contains(accidentType)) continue;
                for (AccidentSeverity accidentSeverity : AccidentSeverity.values()) {
                    if (ACCIDENT_SEVERITIES_EXCLUDED.contains(accidentSeverity)) continue;
                    double totalExposure = osmLink.getNetworkLinks().stream()
                            .mapToDouble(link -> calculateTotalCasualty(link.getId(), accidentType))
                            .sum();
                    if (totalExposure > 0) {
                        data.append(String.format("%d,%s,%.2f\n", osmLink.osmId, accidentType.name(), totalExposure));
                    }
                }
            }
        }
        writeToFile(outputPath, data.toString());
    }

    public static void writeToFile(String path, String data) throws FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(path, false))) {
            writer.print(data);
        }
    }

    public AccidentsContext getAccidentsContext() {
        return accidentsContext;
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
}