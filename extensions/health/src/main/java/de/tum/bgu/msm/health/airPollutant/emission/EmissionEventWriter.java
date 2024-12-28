package de.tum.bgu.msm.health.airPollutant.emission;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.emissions.EmissionModule;
import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.EventWriterXML;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.FileNotFoundException;

import static org.matsim.core.controler.Injector.createInjector;

public class EmissionEventWriter {

    private static final Logger logger = LogManager.getLogger(EmissionEventWriter.class);


    public static void main(String[] args) {

        //final String outputDirectoryRoot = "H:\\FDisk\\models\\mitoMunich\\scenOutput/mito2.0_originalSP_0.1_1\\2011\\trafficAssignment";
        final String outputDirectoryRoot = "F:\\models\\healthModel\\muc\\scenOutput\\healthModelWithTrucks\\matsim\\2011\\weekday/car";


        String configFile = "F:/models/healthModel/muc/configHealth.xml";
        Config config = ConfigUtils.loadConfig(configFile);
        Scenario scenario = ScenarioUtils.createMutableScenario(config);
        scenario.getConfig().controller().setOutputDirectory(outputDirectoryRoot);
        prepareConfig(scenario);

        String eventFileWithEmissions = scenario.getConfig().controller().getOutputDirectory() + "/" + "2011.output_events_emission.xml.gz";
        String vehicleFileWithEmissionType = scenario.getConfig().controller().getOutputDirectory() + "/" + "2011.vehicles_emission.xml.gz";
        String networkFile = scenario.getConfig().controller().getOutputDirectory()+"/2011.output_network.xml.gz";
        String populationFile = scenario.getConfig().controller().getOutputDirectory()+"/2011.output_plans.xml.gz";

        String linkWarmEmissionFile = scenario.getConfig().controller().getOutputDirectory() + "/linkWarmEmissionFile.csv";
        String linkColdEmissionFile = scenario.getConfig().controller().getOutputDirectory() + "/linkColdEmissionFile.csv";
        String vehicleWarmEmissionFile = scenario.getConfig().controller().getOutputDirectory() + "/vehicleWarmEmissionFile.csv";
        String vehicleColdEmissionFile = scenario.getConfig().controller().getOutputDirectory() + "/vehicleColdEmissionFile.csv";



        EmissionEventAnalysis emissionEventsAnalysis = new EmissionEventAnalysis();
        try {
            emissionEventsAnalysis.run(configFile,
                    outputDirectoryRoot,
                    eventFileWithEmissions,
                    vehicleFileWithEmissionType,
                    populationFile,
                    networkFile,
                    linkWarmEmissionFile,
                    linkColdEmissionFile,
                    vehicleWarmEmissionFile,
                    vehicleColdEmissionFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    private static void createEmissionEventsOffline(Scenario scenario, String eventsFileWithoutEmissions, String eventsFileWithEmission) {
        EventsManager eventsManager = EventsUtils.createEventsManager();

        AbstractModule module = new AbstractModule(){
            @Override
            public void install(){
                bind( Scenario.class ).toInstance( scenario );
                bind( EventsManager.class ).toInstance( eventsManager );
                bind( EmissionModule.class ) ;
            }
        };


        com.google.inject.Injector injector = createInjector(scenario.getConfig(), module);

        EmissionModule emissionModule = injector.getInstance(EmissionModule.class);

        EventWriterXML emissionEventWriter = new EventWriterXML(eventsFileWithEmission);
        emissionModule.getEmissionEventsManager().addHandler(emissionEventWriter);

        MatsimEventsReader matsimEventsReader = new MatsimEventsReader(eventsManager);
        matsimEventsReader.readFile(eventsFileWithoutEmissions);

        emissionEventWriter.closeFile();
        logger.warn("Created Emission events file.");
    }

    public static Config prepareConfig(Scenario scenario) {
        scenario.getConfig().travelTimeCalculator().setTraveltimeBinSize(3600);
        EmissionsConfigGroup emissionsConfig= new EmissionsConfigGroup();
        emissionsConfig.setDetailedVsAverageLookupBehavior(EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
        emissionsConfig.setAverageColdEmissionFactorsFile(scenario.getConfig().controller().getOutputDirectory()+ "/EFA_ColdStart_Vehcat_matching.txt");
        emissionsConfig.setAverageWarmEmissionFactorsFile(scenario.getConfig().controller().getOutputDirectory()+ "/EFA_HOT_Vehcat_matching.txt");
        emissionsConfig.setNonScenarioVehicles(EmissionsConfigGroup.NonScenarioVehicles.ignore);
        //emissionsConfig.setHbefaRoadTypeSource(EmissionsConfigGroup.HbefaRoadTypeSource.fromLinkAttributes);
        emissionsConfig.setHbefaVehicleDescriptionSource(EmissionsConfigGroup.HbefaVehicleDescriptionSource.fromVehicleTypeDescription);
        scenario.getConfig().addModule(emissionsConfig);

        return scenario.getConfig();
    }

}
