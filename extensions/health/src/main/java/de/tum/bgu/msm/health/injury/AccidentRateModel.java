package de.tum.bgu.msm.health.injury;

import cern.colt.map.tfloat.OpenIntFloatHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class AccidentRateModel {
    private static final Logger log = LogManager.getLogger( AccidentRateModel.class );
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

    public AccidentRateModel(Scenario scenario, float scalefactor) {
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
                AccidentRateCalculation calculator = new AccidentRateCalculation(SCALEFACTOR, accidentsContext, analysisEventHandler, accidentType, accidentSeverity, basePath);
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
                AccidentRateCalculation calculator = new AccidentRateCalculation(SCALEFACTOR, accidentsContext, analysisEventHandler, accidentType, accidentSeverity,basePath);
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
                AccidentRateCalculation calculator = new AccidentRateCalculation(SCALEFACTOR, accidentsContext, analysisEventHandler, accidentType, accidentSeverity,basePath);
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
        String outputRisk = scenario.getConfig().controller().getOutputDirectory() + "casualtyRate.csv";
        StringBuilder risk = new StringBuilder();

        //write header
        risk.append("link,severity,accidentType,casualty");
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

    public void writeOutCrashFrequency () throws FileNotFoundException {
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

    public static void writeToFile(String path, String building) throws FileNotFoundException {
        PrintWriter bd = new PrintWriter(new FileOutputStream(path, true));
        bd.write(building);
        bd.close();
    }

    public AccidentsContext getAccidentsContext() {
        return accidentsContext;
    }
}
