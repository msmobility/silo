package de.tum.bgu.msm.health;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import de.tum.bgu.msm.health.injury.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.accidents.AccidentsModule;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Injector;
import org.matsim.core.events.EventsManagerModule;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioByInstanceModule;
import org.matsim.utils.objectattributes.attributable.Attributes;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AccidentRateModelMCR {
    private static final Logger log = LogManager.getLogger( AccidentRateModelMCR.class );
    private Scenario scenario;
    private final float SCALEFACTOR;
    private AccidentsContext accidentsContext = new AccidentsContext();
    private AnalysisEventHandler analysisEventHandler;

    private static final float CAR_LIGHT_LIGHT = 1.23f;
    private static final float CAR_SEVERE_LIGHT = 0.29f;
    private static final float CAR_SEVERE_SEVERE = 1.01f;
    private static final float BIKECAR_LIGHT_LIGHT = 0.99f;
    private static final float BIKECAR_SEVERE_LIGHT = 0.01f;
    private static final float BIKECAR_SEVERE_SEVERE = 0.99f;
    private static final float BIKEBIKE_LIGHT_LIGHT = 1.05f;
    private static final float BIKEBIKE_SEVERE_LIGHT = 0.00f;
    private static final float BIKEBIKE_SEVERE_SEVERE = 1.00f;
    private static final float PED_LIGHT_LIGHT = 1.03f;
    private static final float PED_SEVERE_LIGHT = 0.05f;
    private static final float PED_SEVERE_SEVERE = 1.00f;
    private int count;
    private int counterCar;
    private int counterBikePed;

    //
    private static final Set<AccidentType> ACCIDENT_TYPE_MCR = Set.of(AccidentType.CAR, AccidentType.BIKECAR, AccidentType.BIKEBIKE);
    private static final Set<AccidentSeverity> ACCIDENT_SEVERITY_MCR = Set.of(AccidentSeverity.LIGHT);

    //
    public static final Set<String> MAJOR = Set.of(
            "primary", "primary_link", "secondary", "secondary_link",
            "tertiary", "tertiary_link", "trunk", "trunk_link",
            "motorway", "motorway_link", "bus_guideway", "cycleway"
    );

    public static final Set<String> MINOR = Set.of(
            "unclassified", "residential", "living_street", "service",
            "pedestrian", "track", "footway", "bridleway", "steps",
            "path", "road"
    );

    public AccidentRateModelMCR(Scenario scenario, float scalefactor) {
        this.scenario = scenario;
        SCALEFACTOR = scalefactor;
    }

    public void runModelOnline() {
        com.google.inject.Injector injector = Injector.createInjector( scenario.getConfig() , new AbstractModule(){
            @Override public void install(){
                install( new ScenarioByInstanceModule( scenario ) );
                install( new AccidentsModule()) ;
                install( new EventsManagerModule());
            }
        }) ;

        log.info("Reading network file...");
        String networkFile;
        if (this.scenario.getConfig().controller().getRunId() == null || this.scenario.getConfig().controller().getRunId().equals("")) {
            networkFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + "output_network.xml.gz";
        } else {
            networkFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + this.scenario.getConfig().controller().getRunId() + ".output_network.xml.gz";
        }
        new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
        log.info("Reading network file... Done.");

        analysisEventHandler = new AnalysisEventHandlerOnline();
        analysisEventHandler.setScenario(scenario);
        analysisEventHandler.setAccidentsContext(accidentsContext);

        log.info("Reading car events file...");
        EventsManager events = injector.getInstance( EventsManager.class ) ;
        MatsimEventsReader eventsReader = new MatsimEventsReader(events);
        String eventsFile;
        if (this.scenario.getConfig().controller().getRunId() == null || this.scenario.getConfig().controller().getRunId().equals("")) {
            eventsFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + "output_events.xml.gz";
        } else {
            eventsFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + this.scenario.getConfig().controller().getRunId() + ".output_events.xml.gz";
        }
        events.addHandler(analysisEventHandler);
        eventsReader.readFile(eventsFile); //car AADT are calculated by eventHandler
        log.info("Reading car events file... Done.");

        log.info("Reading bike&ped events file...");
        String eventsFileBikePed;
        if (this.scenario.getConfig().controller().getRunId() == null || this.scenario.getConfig().controller().getRunId().equals("")) {
            eventsFileBikePed = this.scenario.getConfig().controller().getOutputDirectory() + "bikePed/" + "output_events.xml.gz";
        } else {
            eventsFileBikePed = this.scenario.getConfig().controller().getOutputDirectory() + "bikePed/" + this.scenario.getConfig().controller().getRunId() + ".output_events.xml.gz";
        }

        // todo: this is a temporary fix link to pedBike volumes, just to test.
        eventsFileBikePed = "C:/Users/saadi/Documents/Cambridge/manchester/scenOutput/base/matsim/2021/saturday/bikePed/2021.output_events_bikePed_saturday.xml.gz";
        eventsReader.readFile(eventsFileBikePed); //car, bike, ped AADT are calculated by eventHandler
        log.info("Reading bike&ped events file... Done.");


        //Preparation
        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            AccidentLinkInfo info = new AccidentLinkInfo(link.getId());
            this.accidentsContext.getLinkId2info().put(link.getId(), info);
        }
        log.info("Initializing all link-specific information... Done.");


        log.info("Link accident frequency calculation (by type by time of day) start.");
        for (AccidentType accidentType : AccidentType.values()){
            for (AccidentSeverity accidentSeverity : AccidentSeverity.values()){
                String basePath = scenario.getScenarioElement("accidentModelFile").toString();
                AccidentRateCalculationMCR calculator = new AccidentRateCalculationMCR(SCALEFACTOR, accidentsContext, analysisEventHandler, accidentType, accidentSeverity, basePath);

                // todo: add condition here to check if link is major/minor - 1way/2way based on accidentType
                // todo: if CAR_ONEWAY, CAR_TWOWAY, BIKE_MINOR, BIKE_MAJOR -> send oneway, twoway, minor, major links only otherwise send all in run()

                calculator.run(this.scenario.getNetwork().getLinks().values());
                log.info("Calculating " + accidentType + "_" + accidentSeverity + " crash rate done.");
            }
        }
        log.info("Link accident frequency calculation completed.");

        try {
            writeOutCrashFrequency();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        log.info("Link casualty frequency conversion (by type by time of day) start.");
        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            casualtyRateCalculation(link);
        }
        log.info("Link casualty frequency conversion completed.");

        try {
            writeOutCasualtyRate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        log.info("Link casualty exposure calculation start.");
        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            computeLinkCasualtyExposure(link);
        }

        try {
            writeOutExposure();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        log.info(counterCar + "car links have no hourly traffic volume");
        log.info(counterBikePed + "bikeped links have no hourly traffic volume");
        log.info("Link casualty exposure calculation completed.");

        analysisEventHandler.reset(0);
        System.gc();
        System.out.println("System gc is reset. Current free memory usage: " + Runtime.getRuntime().freeMemory());
    }

    public void runAgentInjuryRiskOffline() {
        com.google.inject.Injector injector = Injector.createInjector( scenario.getConfig() , new AbstractModule(){
            @Override public void install(){
                install( new ScenarioByInstanceModule( scenario ) );
                install( new AccidentsModule()) ;
                install( new EventsManagerModule());
            }
        }) ;

        log.info("Reading network file...");
        String networkFile;
        if (this.scenario.getConfig().controller().getRunId() == null || this.scenario.getConfig().controller().getRunId().equals("")) {
            networkFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + "output_network.xml.gz";
        } else {
            networkFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + this.scenario.getConfig().controller().getRunId() + ".output_network.xml.gz";
        }
        new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
        log.info("Reading network file... Done.");

        log.info("Reading car plans file...");
        PopulationReader popReader = new PopulationReader(scenario);
        String plansFile;
        if (this.scenario.getConfig().controller().getRunId() == null || this.scenario.getConfig().controller().getRunId().equals("")) {
            plansFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + "output_plans.xml.gz";
        } else {
            plansFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + this.scenario.getConfig().controller().getRunId() + ".output_plans.xml.gz";
        }
        popReader.readFile(plansFile);
        log.info("Reading car plans file... Done.");

        log.warn("Total population:" + scenario.getPopulation().getPersons().size());

        log.info("Reading bikePed plans file...");
        String plansFileBikePed;
        if (this.scenario.getConfig().controller().getRunId() == null || this.scenario.getConfig().controller().getRunId().equals("")) {
            plansFileBikePed = this.scenario.getConfig().controller().getOutputDirectory() + "bikePed/" + "output_plans.xml.gz";
        } else {
            plansFileBikePed = this.scenario.getConfig().controller().getOutputDirectory() + "bikePed/" + this.scenario.getConfig().controller().getRunId() + ".output_plans.xml.gz";
        }
        popReader.readFile(plansFileBikePed);
        log.info("Reading bikePed plans file... Done.");
        log.warn("Total population:" + scenario.getPopulation().getPersons().size());


        analysisEventHandler = new AnalysisEventHandler();
        analysisEventHandler.setScenario(scenario);
        analysisEventHandler.setAccidentsContext(accidentsContext);
        log.info("Reading car events file...");
        EventsManager events = injector.getInstance( EventsManager.class ) ;
        MatsimEventsReader eventsReader = new MatsimEventsReader(events);
        String eventsFile;
        if (this.scenario.getConfig().controller().getRunId() == null || this.scenario.getConfig().controller().getRunId().equals("")) {
            eventsFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + "output_events.xml.gz";
        } else {
            eventsFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + this.scenario.getConfig().controller().getRunId() + ".output_events.xml.gz";
        }
        events.addHandler(analysisEventHandler);
        eventsReader.readFile(eventsFile); //car AADT are calculated by eventHandler
        log.info("Reading car events file... Done.");

        log.info("Reading bike&ped events file...");
        String eventsFileBikePed;
        if (this.scenario.getConfig().controller().getRunId() == null || this.scenario.getConfig().controller().getRunId().equals("")) {
            eventsFileBikePed = this.scenario.getConfig().controller().getOutputDirectory() + "bikePed/" + "output_events.xml.gz";
        } else {
            eventsFileBikePed = this.scenario.getConfig().controller().getOutputDirectory() + "bikePed/" + this.scenario.getConfig().controller().getRunId() + ".output_events.xml.gz";
        }
        eventsReader.readFile(eventsFileBikePed); //car, bike, ped AADT are calculated by eventHandler
        log.info("Reading bike&ped events file... Done.");


        //Preparation
        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            AccidentLinkInfo info = new AccidentLinkInfo(link.getId());
            this.accidentsContext.getLinkId2info().put(link.getId(), info);
        }
        log.info("Initializing all link-specific information... Done.");

/*        for (Person person : this.scenario.getPopulation().getPersons().values()) {
            AccidentAgentInfo info = new AccidentAgentInfo(person.getId());
            this.accidentsContext.getPersonId2info().put(person.getId(), info);
        }
        log.info("Initializing all agent-specific information... Done.");*/


        log.info("Link accident frequency calculation (by type by time of day) start.");
        for (AccidentType accidentType : AccidentType.values()){
            for (AccidentSeverity accidentSeverity : AccidentSeverity.values()){
                String basePath = scenario.getScenarioElement("accidentModelFile").toString();
                AccidentRateCalculationMCR calculator = new AccidentRateCalculationMCR(SCALEFACTOR, accidentsContext, analysisEventHandler, accidentType, accidentSeverity,basePath);
                calculator.run(this.scenario.getNetwork().getLinks().values());
                log.info("Calculating " + accidentType + "_" + accidentSeverity + " crash rate done.");
            }
        }
        log.info("Link accident frequency calculation completed.");


        try {
            writeOutCrashFrequency();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        log.info("Link casualty frequency conversion (by type by time of day) start.");
        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            casualtyRateCalculation(link);
        }
        log.info("Link casualty frequency conversion completed.");


        try {
            writeOutCasualtyRate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        log.info("Link casualty exposure calculation start.");
        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            computeLinkCasualtyExposure(link);
        }
        log.info(counterCar + "car links have no hourly traffic volume");
        log.info(counterBikePed + "bikeped links have no hourly traffic volume");
        log.info("Link casualty exposure calculation completed.");


        try {
            writeOutExposure();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //only for offline
        log.info("Agent crash risk calculation start.");
        for (Person pp : this.scenario.getPopulation().getPersons().values()){
            AccidentAgentInfo personInfo = this.accidentsContext.getPersonId2info().get(pp.getId());
            if(personInfo==null){
                //log.warn("Person Id: " + pp.getId() + "is not analyzed in the handler!");
                count++;
                continue;
            }
            computeAgentCrashRisk(personInfo);
        }
        log.info(count + " agents are not analyzed in the handler!");
        log.info("Agent crash risk calculation completed.");

        try {
            writeOut();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        analysisEventHandler.reset(0);
        System.gc();
    }

    public void runCasualtyRateOffline() {
        com.google.inject.Injector injector = Injector.createInjector( scenario.getConfig() , new AbstractModule(){
            @Override public void install(){
                install( new ScenarioByInstanceModule( scenario ) );
                install( new AccidentsModule()) ;
                install( new EventsManagerModule());
            }
        }) ;

        log.info("Reading network file...");
        String networkFile;
        if (this.scenario.getConfig().controller().getRunId() == null || this.scenario.getConfig().controller().getRunId().equals("")) {
            networkFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + "output_network.xml.gz";
        } else {
            networkFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + this.scenario.getConfig().controller().getRunId() + ".output_network.xml.gz";
        }

        networkFile = "/home/admin/ismail/manchester/main/scenOutput/base/matsim/2021/thursday/car/2021.output_network.xml.gz";
        new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
        log.info("Reading network file... Done.");

        analysisEventHandler = new AnalysisEventHandler();
        analysisEventHandler.setScenario(scenario);
        analysisEventHandler.setAccidentsContext(accidentsContext);
        log.info("Reading car events file...");
        EventsManager events = injector.getInstance( EventsManager.class ) ;
        MatsimEventsReader eventsReader = new MatsimEventsReader(events);
        String eventsFile;
        if (this.scenario.getConfig().controller().getRunId() == null || this.scenario.getConfig().controller().getRunId().equals("")) {
            eventsFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + "output_events.xml.gz";
        } else {
            eventsFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + this.scenario.getConfig().controller().getRunId() + ".output_events.xml.gz";
        }
        eventsFile = "/home/admin/ismail/manchester/main/scenOutput/base/matsim/2021/thursday/car/AccidentTest/2021.output_events.xml.gz";
        events.addHandler(analysisEventHandler);
        eventsReader.readFile(eventsFile); //car AADT are calculated by eventHandler
        log.info("Reading car events file... Done.");

        log.info("Reading bike&ped events file...");
        String eventsFileBikePed;
        if (this.scenario.getConfig().controller().getRunId() == null || this.scenario.getConfig().controller().getRunId().equals("")) {
            eventsFileBikePed = this.scenario.getConfig().controller().getOutputDirectory() + "bikePed/" + "output_events.xml.gz";
        } else {
            eventsFileBikePed = this.scenario.getConfig().controller().getOutputDirectory() + "bikePed/" + this.scenario.getConfig().controller().getRunId() + ".output_events.xml.gz";
        }
        // todo: this is a temporary fix link to pedBike volumes, just to test.
        eventsFileBikePed = "/home/admin/ismail/manchester/main/scenOutput/base/matsim/2021/thursday/bikePed/AccidentTest/2021.output_events_bikePed_saturday.xml.gz";
        eventsReader.readFile(eventsFileBikePed); //car, bike, ped AADT are calculated by eventHandler
        log.info("Reading bike&ped events file... Done.");


        //Preparation
        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            AccidentLinkInfo info = new AccidentLinkInfo(link.getId());
            this.accidentsContext.getLinkId2info().put(link.getId(), info);
        }
        log.info("Initializing all link-specific information... Done.");

        /*
        for (Person person : this.scenario.getPopulation().getPersons().values()) {
            AccidentAgentInfo info = new AccidentAgentInfo(person.getId());
            this.accidentsContext.getPersonId2info().put(person.getId(), info);
        }
        log.info("Initializing all agent-specific information... Done.");
         */

        Random random = new Random();
        log.info("Link casualty frequency calculation (by type by time of day) start.");
        for (AccidentType accidentType : AccidentType.values()){
            if (ACCIDENT_TYPE_MCR.contains(accidentType)){
                continue;
            }
            for (AccidentSeverity accidentSeverity : AccidentSeverity.values()){
                if(ACCIDENT_SEVERITY_MCR.contains(accidentSeverity)){
                    continue;
                }

                String basePath = scenario.getScenarioElement("accidentModelFile").toString();
                CasualtyRateCalculationMCR calculator = new CasualtyRateCalculationMCR(SCALEFACTOR, accidentsContext, analysisEventHandler, accidentType, accidentSeverity, basePath);

                Map<Id<Link>, Link> placeholderMap = new HashMap<>();
                placeholderMap.putAll(extractLinkSpecific((Map<Id<Link>, Link>) this.scenario.getNetwork().getLinks(), accidentType));
                calculator.run(placeholderMap.values(), random);

                log.info("Calculating " + accidentType + "_" + accidentSeverity + " crash rate done.");
            }
        }
        log.info("Link casualty frequency calculation completed.");

        try {
            writeOutCasualtyRate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        /*
        log.info("Link casualty frequency conversion (by type by time of day) start.");
        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            casualtyRateCalculation(link);
        }
        log.info("Link casualty frequency conversion completed.");

        try {
            writeOutCasualtyRate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
         */

        log.info("Link casualty exposure calculation start.");
        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            computeLinkCasualtyExposureMCR(link);
        }

        try {
            writeOutExposure();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        log.info(counterCar + "car links have no hourly traffic volume");
        log.info(counterBikePed + "bikeped links have no hourly traffic volume");
        log.info("Link casualty exposure calculation completed.");


        //only for offline todo: read matsim plans
        log.info("Agent injury risk calculation start.");
        for (Person pp : this.scenario.getPopulation().getPersons().values()){
            AccidentAgentInfo personInfo = this.accidentsContext.getPersonId2info().get(pp.getId());
            if(personInfo == null){
                //log.warn("Person Id: " + pp.getId() + "is not analyzed in the handler!");
                count++;
                continue;
            }
            computeAgentCrashRiskMCR(personInfo);
        }
        log.info(count + " agents are not analyzed in the handler!");
        log.info("Agent injury risk calculation completed.");

        try {
            writeOut();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        // todo: why ??
        analysisEventHandler.reset(0);
        System.gc();
    }

    private Map<Id<Link>,Link> extractLinkSpecific(Map<Id<Link>, Link> links, AccidentType accidentType) {
        Map<Id<Link>, Link> placeholderMap = new HashMap<>();

        // send only relevant links for which we want to predict casualties
        switch(accidentType){
            case PED:
                for(Link link : links.values()){
                    if(link.getAllowedModes().contains(TransportMode.walk)){
                        placeholderMap.put(link.getId(), link);
                    }
                }
                break;
            case CAR_ONEWAY:
                for(Link link : links.values()){
                    if(!isTwoWayRoad(this.scenario.getNetwork(), link, TransportMode.car)){
                        placeholderMap.put(link.getId(), link);
                    }
                }
                break;
            case CAR_TWOWAY:
                for(Link link : links.values()){
                    if(isTwoWayRoad(this.scenario.getNetwork(), link, TransportMode.car)){
                        placeholderMap.put(link.getId(), link);
                    }
                }
                break;
            case BIKE_MINOR:
                for(Link link : links.values()){
                    if(MINOR.contains(getStringAttribute(link.getAttributes(), "type", "residential")) && link.getAllowedModes().contains(TransportMode.bike)){
                        placeholderMap.put(link.getId(), link);
                    }
                }
                break;
            case BIKE_MAJOR:
                for(Link link : links.values()){
                    if(MAJOR.contains(getStringAttribute(link.getAttributes(), "type", "residential")) && link.getAllowedModes().contains(TransportMode.bike)){
                        placeholderMap.put(link.getId(), link);
                    }
                }
                break;
            default:
                // todo: better to raise error here ??
                break;

        }
        return placeholderMap;
    }

    public String getStringAttribute(Attributes attributes, String key, String defaultValue) {
        Object value = attributes.getAttribute(key);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * Checks if a link is part of a two-way road for a specific transport mode.
     * @param network The MATSim network
     * @param link The link to check
     * @param mode The transport mode to verify
     * @return true if there exists a reverse link that also allows the specified mode
     */

    public boolean isTwoWayRoad(Network network, Link link, String mode) {
        // First check if the original link allows the specified mode
        if (!link.getAllowedModes().contains(mode)) {
            return false;
        }

        Node fromNode = link.getFromNode();
        Node toNode = link.getToNode();

        // Check all outgoing links from the 'to' node (more efficient than scanning all links)
        for (Link potentialReverse : toNode.getOutLinks().values()) {
            if (potentialReverse.getToNode().equals(fromNode) &&
                    potentialReverse.getAllowedModes().contains(mode)) {
                return true;
            }
        }
        return false;
    }

    public void runAccidentRateOffline() {
        com.google.inject.Injector injector = Injector.createInjector( scenario.getConfig() , new AbstractModule(){
            @Override public void install(){
                install( new ScenarioByInstanceModule( scenario ) );
                install( new AccidentsModule()) ;
                install( new EventsManagerModule());
            }
        }) ;

        log.info("Reading network file...");
        String networkFile;
        if (this.scenario.getConfig().controller().getRunId() == null || this.scenario.getConfig().controller().getRunId().equals("")) {
            networkFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + "output_network.xml.gz";
        } else {
            networkFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + this.scenario.getConfig().controller().getRunId() + ".output_network.xml.gz";
        }
        new MatsimNetworkReader(scenario.getNetwork()).readFile(networkFile);
        log.info("Reading network file... Done.");

        analysisEventHandler = new AnalysisEventHandler();
        analysisEventHandler.setScenario(scenario);
        analysisEventHandler.setAccidentsContext(accidentsContext);
        log.info("Reading car events file...");
        EventsManager events = injector.getInstance( EventsManager.class ) ;
        MatsimEventsReader eventsReader = new MatsimEventsReader(events);
        String eventsFile;
        if (this.scenario.getConfig().controller().getRunId() == null || this.scenario.getConfig().controller().getRunId().equals("")) {
            eventsFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + "output_events.xml.gz";
        } else {
            eventsFile = this.scenario.getConfig().controller().getOutputDirectory() + "car/" + this.scenario.getConfig().controller().getRunId() + ".output_events.xml.gz";
        }
        events.addHandler(analysisEventHandler);
        eventsReader.readFile(eventsFile); //car AADT are calculated by eventHandler
        log.info("Reading car events file... Done.");

        log.info("Reading bike&ped events file...");
        String eventsFileBikePed;
        if (this.scenario.getConfig().controller().getRunId() == null || this.scenario.getConfig().controller().getRunId().equals("")) {
            eventsFileBikePed = this.scenario.getConfig().controller().getOutputDirectory() + "bikePed/" + "output_events.xml.gz";
        } else {
            eventsFileBikePed = this.scenario.getConfig().controller().getOutputDirectory() + "bikePed/" + this.scenario.getConfig().controller().getRunId() + ".output_events.xml.gz";
        }
        // todo: this is a temporary fix link to pedBike volumes, just to test.
        eventsFileBikePed = "C:/Users/saadi/Documents/Cambridge/manchester/scenOutput/base/matsim/2021/saturday/bikePed/2021.output_events_bikePed_saturday.xml.gz";
        eventsReader.readFile(eventsFileBikePed); //car, bike, ped AADT are calculated by eventHandler
        log.info("Reading bike&ped events file... Done.");


        //Preparation
        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            AccidentLinkInfo info = new AccidentLinkInfo(link.getId());
            this.accidentsContext.getLinkId2info().put(link.getId(), info);
        }
        log.info("Initializing all link-specific information... Done.");

        for (Person person : this.scenario.getPopulation().getPersons().values()) {
            AccidentAgentInfo info = new AccidentAgentInfo(person.getId());
            this.accidentsContext.getPersonId2info().put(person.getId(), info);
        }
        log.info("Initializing all agent-specific information... Done.");


        log.info("Link accident frequency calculation (by type by time of day) start.");
        for (AccidentType accidentType : AccidentType.values()){
            for (AccidentSeverity accidentSeverity : AccidentSeverity.values()){
                String basePath = scenario.getScenarioElement("accidentModelFile").toString();
                AccidentRateCalculationMCR calculator = new AccidentRateCalculationMCR(SCALEFACTOR, accidentsContext, analysisEventHandler, accidentType, accidentSeverity,basePath);
                calculator.run(this.scenario.getNetwork().getLinks().values());
                log.info("Calculating " + accidentType + "_" + accidentSeverity + " crash rate done.");
            }
        }
        log.info("Link accident frequency calculation completed.");

        try {
            writeOutCrashFrequency();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        log.info("Link casualty frequency conversion (by type by time of day) start.");
        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            casualtyRateCalculation(link);
        }
        log.info("Link casualty frequency conversion completed.");

        try {
            writeOutCasualtyRate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void computeAgentCrashRiskMCR(AccidentAgentInfo personInfo) {

        //double lightInjuryRisk = .0;
        double severeInjuryRisk = .0;
        Map<String, Double> PersonInjuryRiskByMode = new HashMap<>();

        // Initialize modes
        PersonInjuryRiskByMode.put("car", 0.0);
        PersonInjuryRiskByMode.put("bike", 0.0);
        PersonInjuryRiskByMode.put("walk", 0.0);

        for(Id<Link> linkId : personInfo.getLinkId2time2mode().keySet()){
            AccidentLinkInfo linkInfo = this.accidentsContext.getLinkId2info().get(linkId);
            if(linkInfo == null){
                log.warn(linkId + " has no Link Info.");
                continue;
            }

            for(int hour : personInfo.getLinkId2time2mode().get(linkId).keySet()){
                String mode = personInfo.getLinkId2time2mode().get(linkId).get(hour);
                switch (mode){
                    case "car":
                        severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.CAR_ONEWAY).get(hour);
                        severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.CAR_TWOWAY).get(hour);
                        PersonInjuryRiskByMode.put("car", PersonInjuryRiskByMode.get("car") + severeInjuryRisk);
                        severeInjuryRisk = .0;
                        break;
                    case "bike":
                        severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKE_MAJOR).get(hour);
                        severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKE_MINOR).get(hour);
                        PersonInjuryRiskByMode.put("bike", PersonInjuryRiskByMode.get("bike") + severeInjuryRisk);
                        severeInjuryRisk = .0;
                        break;
                    case "walk":
                        severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.PED).get(hour);
                        PersonInjuryRiskByMode.put("walk", PersonInjuryRiskByMode.get("walk") + severeInjuryRisk);
                        severeInjuryRisk = .0;
                        break;
                    default:
                        throw new RuntimeException("Undefined mode " + mode);
                }
            }
        }
        //personInfo.setSevereInjuryRisk(severeInjuryRisk);
        personInfo.setSevereInjuryRiskByMode(PersonInjuryRiskByMode);
    }

    private void computeAgentCrashRisk(AccidentAgentInfo personInfo) {

        double lightInjuryRisk = .0;
        double severeInjuryRisk = .0;
        for(Id<Link> linkId : personInfo.getLinkId2time2mode().keySet()){
            AccidentLinkInfo linkInfo = this.accidentsContext.getLinkId2info().get(linkId);
            if(linkInfo==null){
                log.warn(linkId + " has no Link Info.");
                continue;
            }
            for(int hour : personInfo.getLinkId2time2mode().get(linkId).keySet()){
                String mode = personInfo.getLinkId2time2mode().get(linkId).get(hour);
                switch (mode){
                    case "car":
                        lightInjuryRisk += linkInfo.getLightCasualityExposureByAccidentTypeByTime().get(AccidentType.CAR).get(hour);
                        severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.CAR).get(hour);
                        break;
                    case "bike":
                        lightInjuryRisk += linkInfo.getLightCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKECAR).get(hour);
                        severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKECAR).get(hour);
                        lightInjuryRisk += linkInfo.getLightCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKEBIKE).get(hour);
                        severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.BIKEBIKE).get(hour);
                        break;
                    case "walk":
                        lightInjuryRisk += linkInfo.getLightCasualityExposureByAccidentTypeByTime().get(AccidentType.PED).get(hour);
                        severeInjuryRisk += linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(AccidentType.PED).get(hour);
                        break;
                    default:
                        throw new RuntimeException("Undefined mode " + mode);
                }
            }
        }
        personInfo.setLightInjuryRisk(lightInjuryRisk);
        personInfo.setSevereInjuryRisk(severeInjuryRisk);
    }

    private void casualtyRateCalculation(Link link) {
        AccidentLinkInfo linkInfo = this.accidentsContext.getLinkId2info().get(link.getId());
        for (AccidentType accidentType : AccidentType.values()){
            OpenIntFloatHashMap lightCasualtyByTime = new OpenIntFloatHashMap();
            OpenIntFloatHashMap severeCasualtyByTime = new OpenIntFloatHashMap();
            for(int hour = 0; hour<=24; hour++) {
                float lightCasualty = 0.0f;
                float severeCasualty = 0.0f;
                float lightCrash = 0.0f;
                float severeCrash = 0.0f;

                if(linkInfo.getLightCasualityExposureByAccidentTypeByTime().get(accidentType)!=null){
                    lightCrash = linkInfo.getLightCasualityExposureByAccidentTypeByTime().get(accidentType).get(hour);
                }

                if(linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(accidentType)!=null){
                    severeCrash = linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().get(accidentType).get(hour);
                }

                 switch (accidentType){
                    case CAR:
                        lightCasualty += lightCrash * CAR_LIGHT_LIGHT;
                        lightCasualty += severeCrash * CAR_SEVERE_LIGHT;
                        severeCasualty += severeCrash * CAR_SEVERE_SEVERE;
                        break;
                    case PED:
                        lightCasualty += lightCrash * PED_LIGHT_LIGHT;
                        lightCasualty += severeCrash * PED_SEVERE_LIGHT;
                        severeCasualty += severeCrash * PED_SEVERE_SEVERE;
                        break;
                    case BIKECAR:
                        lightCasualty += lightCrash * BIKECAR_LIGHT_LIGHT;
                        lightCasualty += severeCrash * BIKECAR_SEVERE_LIGHT;
                        severeCasualty += severeCrash * BIKECAR_SEVERE_SEVERE;
                        break;
                    case BIKEBIKE:
                        lightCasualty += lightCrash * BIKEBIKE_LIGHT_LIGHT;
                        lightCasualty += severeCrash * BIKEBIKE_SEVERE_LIGHT;
                        severeCasualty += severeCrash * BIKEBIKE_SEVERE_SEVERE;
                        break;
                    default:
                        throw new RuntimeException("Undefined accident type " + accidentType);
                }
                lightCasualtyByTime.put(hour,lightCasualty);
                severeCasualtyByTime.put(hour,severeCasualty);
            }
            linkInfo.getLightCasualityExposureByAccidentTypeByTime().put(accidentType,lightCasualtyByTime);
            linkInfo.getSevereFatalCasualityExposureByAccidentTypeByTime().put(accidentType,severeCasualtyByTime);
        }
    }

    private void computeLinkCasualtyExposureMCR(Link link) {
        for (AccidentType accidentType : AccidentType.values()){
            String mode;
            switch (accidentType){
                case CAR_TWOWAY:
                    mode = "car";
                    break;
                case CAR_ONEWAY:
                    mode = "car";
                    break;
                case PED:
                    mode = "walk";
                    break;
                case BIKE_MAJOR:
                    mode = "bike";
                    break;
                case BIKE_MINOR:
                    mode = "bike";
                    break;
                default:
                    mode = "null";
            }

            if("null".equals(mode)){
                // throw new RuntimeException("Undefined accident type " + accidentType);
                // todo: adjust this error message
                continue;
            }

            //OpenIntFloatHashMap lightCasualtyExposureByTime = new OpenIntFloatHashMap();
            OpenIntFloatHashMap severeCasualtyExposureByTime = new OpenIntFloatHashMap(); // this includes severeFatal

            for(int hour = 0; hour < 24; hour++) {
                //float lightCasualty = this.accidentsContext.getLinkId2info().get(link.getId()).getLightCasualityExposureByAccidentTypeByTime().get(accidentType).get(hour);
                float severeCasualty = this.accidentsContext.getLinkId2info().get(link.getId()).getSevereFatalCasualityExposureByAccidentTypeByTime().get(accidentType).get(hour);
                //float lightCasualtyExposure =0.f;
                float severeCasualtyExposure = 0.f;
                if(mode.equals("car")){
                    if(analysisEventHandler.getDemand(link.getId(), mode, hour) != 0){
                        //lightCasualtyExposure = (float) (lightCasualty/((analysisEventHandler.getDemand(link.getId(),mode,hour))*SCALEFACTOR*1.5));
                        severeCasualtyExposure = (float) (severeCasualty/(analysisEventHandler.getDemand(link.getId(), mode, hour) * SCALEFACTOR)); // todo: why 1.5 in Munich ?? check if SCALEFACTOR should be applied or not ?
                    }else{
                        //log.warn(link.getId()+mode+hour);
                        counterCar++;
                    }
                }else{
                    if(analysisEventHandler.getDemand(link.getId(), mode, hour) != 0){
                        //lightCasualtyExposure = (float) (lightCasualty/(analysisEventHandler.getDemand(link.getId(),mode,hour)*SCALEFACTOR));
                        severeCasualtyExposure = (float) (severeCasualty/(analysisEventHandler.getDemand(link.getId(), mode, hour))); // todo: no scale factor given that we model 100% bikePed , check ??
                    }else{
                        counterBikePed++;
                    }
                }

                //lightCasualtyExposureByTime.put(hour,lightCasualtyExposure);
                severeCasualtyExposureByTime.put(hour, severeCasualtyExposure);
            }
            //this.accidentsContext.getLinkId2info().get(link.getId()).getLightCasualityExposureByAccidentTypeByTime().put(accidentType,lightCasualtyExposureByTime);
            this.accidentsContext.getLinkId2info().get(link.getId()).getSevereFatalCasualityExposureByAccidentTypeByTime().put(accidentType, severeCasualtyExposureByTime);

        }
    }

    private void computeLinkCasualtyExposure(Link link) {
        for (AccidentType accidentType : AccidentType.values()){
            String mode;
            switch (accidentType){
                case CAR:
                    mode = "car";
                    break;
                case PED:
                    mode = "walk";
                    break;
                case BIKECAR:
                    mode = "bike";
                    break;
                case BIKEBIKE:
                    mode = "bike";
                    break;
                default:
                    mode = "null";
            }

            if("null".equals(mode)){
                throw new RuntimeException("Undefined accident type " + accidentType);
            }

            OpenIntFloatHashMap lightCasualtyExposureByTime = new OpenIntFloatHashMap();
            OpenIntFloatHashMap severeCasualtyExposureByTime = new OpenIntFloatHashMap();
            for(int hour = 0; hour < 24; hour++) {
                float lightCasualty = this.accidentsContext.getLinkId2info().get(link.getId()).getLightCasualityExposureByAccidentTypeByTime().get(accidentType).get(hour);
                float severeCasualty = this.accidentsContext.getLinkId2info().get(link.getId()).getSevereFatalCasualityExposureByAccidentTypeByTime().get(accidentType).get(hour);
                float lightCasualtyExposure =0.f;
                float severeCasualtyExposure = 0.f;
                if(mode.equals("car")){
                    if(analysisEventHandler.getDemand(link.getId(),mode,hour)!=0){
                        lightCasualtyExposure = (float) (lightCasualty/((analysisEventHandler.getDemand(link.getId(),mode,hour))*SCALEFACTOR*1.5));
                        severeCasualtyExposure = (float) (severeCasualty/((analysisEventHandler.getDemand(link.getId(),mode,hour))*SCALEFACTOR*1.5));
                    }else{
                        //log.warn(link.getId()+mode+hour);
                        counterCar++;
                    }
                }else{
                    if(analysisEventHandler.getDemand(link.getId(),mode,hour)!=0){
                        lightCasualtyExposure = (float) (lightCasualty/(analysisEventHandler.getDemand(link.getId(),mode,hour)*SCALEFACTOR));
                        severeCasualtyExposure = (float) (severeCasualty/(analysisEventHandler.getDemand(link.getId(),mode,hour)*SCALEFACTOR));
                    }else{
                        counterBikePed++;
                    }
                }

                lightCasualtyExposureByTime.put(hour,lightCasualtyExposure);
                severeCasualtyExposureByTime.put(hour,severeCasualtyExposure);
            }
            this.accidentsContext.getLinkId2info().get(link.getId()).getLightCasualityExposureByAccidentTypeByTime().put(accidentType,lightCasualtyExposureByTime);
            this.accidentsContext.getLinkId2info().get(link.getId()).getSevereFatalCasualityExposureByAccidentTypeByTime().put(accidentType,severeCasualtyExposureByTime);

        }
    }

    public void writeOut () throws FileNotFoundException {
        String outputRisk = scenario.getConfig().controller().getOutputDirectory() + "injuryRisk.csv";
        StringBuilder risk = new StringBuilder();

        //write header
        risk.append("personId,lightInjury,severeFatalInjury");
        risk.append('\n');

        //write data
        for (Id<Person> person : accidentsContext.getPersonId2info().keySet()){

            risk.append(person.toString());
            risk.append(',');
            risk.append(accidentsContext.getPersonId2info().get(person).getLightInjuryRisk());
            risk.append(',');
            risk.append(accidentsContext.getPersonId2info().get(person).getSevereInjuryRisk());
            risk.append('\n');

        }
        writeToFile(outputRisk,risk.toString());
    }

    public void writeOutCasualtyRate () throws FileNotFoundException {
        String outputRisk = scenario.getConfig().controller().getOutputDirectory() + "casualties.csv";
        StringBuilder risk = new StringBuilder();

        //write header
        risk.append("link,severity,accidentType,casualty");
        risk.append('\n');

        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            for(AccidentSeverity accidentSeverity : AccidentSeverity.values()){
                if(ACCIDENT_SEVERITY_MCR.contains(accidentSeverity)){
                    continue;
                }
                for(AccidentType accidentType : AccidentType.values()){
                    if (ACCIDENT_TYPE_MCR.contains(accidentType)){
                        continue;
                    }
                    double totalCasualty = 0.;
                    if(accidentsContext.getLinkId2info().get(link.getId()).getSevereFatalCasualityExposureByAccidentTypeByTime().get(accidentType) != null){
                        for(int hour = 0; hour < 24; hour++) {
                            totalCasualty += accidentsContext.getLinkId2info().get(link.getId()).getSevereFatalCasualityExposureByAccidentTypeByTime().get(accidentType).get(hour);
                        }
                        risk.append(link.getId());
                        risk.append(',');
                        risk.append(accidentSeverity.name());
                        risk.append(',');
                        risk.append(accidentType.name());
                        risk.append(',');
                        risk.append(totalCasualty);
                        risk.append('\n');
                    }
                }

            }
        }
        writeToFile(outputRisk,risk.toString());
    }

    public void writeOutCrashFrequency () throws FileNotFoundException {
        // todo:
        String outputRisk = scenario.getConfig().controller().getOutputDirectory() + "crashRate.csv";
        StringBuilder risk = new StringBuilder();

        //write header
        risk.append("link,severity,accidentType,crash");
        risk.append('\n');

        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            for(AccidentSeverity accidentSeverity : AccidentSeverity.values()){
                for(AccidentType accidentType : AccidentType.values()){
                    double totalCasualty = 0.;
                    for(int hour = 0; hour<=24; hour++) {
                        if(accidentSeverity.equals(AccidentSeverity.LIGHT)){
                            totalCasualty += accidentsContext.getLinkId2info().get(link.getId()).getLightCasualityExposureByAccidentTypeByTime().get(accidentType).get(hour);
                        }else{
                            totalCasualty += accidentsContext.getLinkId2info().get(link.getId()).getSevereFatalCasualityExposureByAccidentTypeByTime().get(accidentType).get(hour);
                        }
                    }
                    risk.append(link.getId());
                    risk.append(',');
                    risk.append(accidentSeverity.name());
                    risk.append(',');
                    risk.append(accidentType.name());
                    risk.append(',');
                    risk.append(totalCasualty);
                    risk.append('\n');
                }

            }
        }

        writeToFile(outputRisk,risk.toString());
    }

    public void writeOutExposure () throws FileNotFoundException {
        String outputRisk = scenario.getConfig().controller().getOutputDirectory() + "linkExposure.csv";
        StringBuilder risk = new StringBuilder();

        //write header
        risk.append("link,severity,accidentType,exposure");
        risk.append('\n');

        for (Link link : this.scenario.getNetwork().getLinks().values()) {
            for(AccidentSeverity accidentSeverity : AccidentSeverity.values()){
                for(AccidentType accidentType : AccidentType.values()){
                    double totalCasualty = 0.;
                    for(int hour = 0; hour<24; hour++) {
                        if(accidentSeverity.equals(AccidentSeverity.LIGHT)){
                            totalCasualty += accidentsContext.getLinkId2info().get(link.getId()).getLightCasualityExposureByAccidentTypeByTime().get(accidentType).get(hour);
                        }else{
                            totalCasualty += accidentsContext.getLinkId2info().get(link.getId()).getSevereFatalCasualityExposureByAccidentTypeByTime().get(accidentType).get(hour);
                        }
                    }
                    risk.append(link.getId());
                    risk.append(',');
                    risk.append(accidentSeverity.name());
                    risk.append(',');
                    risk.append(accidentType.name());
                    risk.append(',');
                    risk.append(totalCasualty);
                    risk.append('\n');
                }

            }
        }

        writeToFile(outputRisk,risk.toString());
    }

    public static void writeToFile(String path, String building) throws FileNotFoundException {
        PrintWriter bd = new PrintWriter(new FileOutputStream(path, true));
        bd.write(building);
        bd.close();
    }

    public AccidentsContext getAccidentsContext() {
        return accidentsContext;
    }
}
