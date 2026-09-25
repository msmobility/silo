package run;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.output.DefaultResultsMonitor;
import de.tum.bgu.msm.io.output.HouseholdSatisfactionMonitor;
import de.tum.bgu.msm.io.output.MultiFileResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.RoutingConfigGroup;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class RunFabilandAutofrei {

    enum AutofreiScenario {
        BASE,
        SMALL,
        LARGE
    }

    enum PtScenario {
//        NONE,
        NES,
        RINGSX
    }


    //    static String scenario = "base";
    static AutofreiScenario autofreiScenario = AutofreiScenario.LARGE;
    static PtScenario ptScenario = PtScenario.NES; // nes / ringsX

    private final static Logger logger = LogManager.getLogger(RunFabilandAutofrei.class);

    public static void main(String[] args) throws IOException {



        // Create a temporary SILO config, wherein we overwrite the scenario name:
        String prefix = "2026-09-15b__";
        String scenario = prefix + "autofrei-" + autofreiScenario + "__pt-" + ptScenario;
        Path baseSiloPropertiesFile = Path.of("useCases/fabiland/scenario/25r_ae.properties");
        Path tempSiloPropertiesFile = Files.createTempFile(baseSiloPropertiesFile.toAbsolutePath().getParent(), "tmp-", ".properties");
        Files.writeString(tempSiloPropertiesFile,
                Files.readString(baseSiloPropertiesFile) + "\nscenario.name = " + scenario + "\n");
        tempSiloPropertiesFile.toFile().deleteOnExit();


        // load Silo properties
        Properties siloConfig = SiloUtil.siloInitialization(tempSiloPropertiesFile.toString());

        // load MATSim config & modify certain options
        Config matsimConfig = ConfigUtils.loadConfig("useCases/fabiland/scenario/config_cap30_1-l_nes_smc.xml");


        logger.info("Started SILO Fabiland sandbox model");

        // The following is obviously just a dirty quickfix until access/egress is default in MATSim
        if (siloConfig.transportModel.includeAccessEgress) {
            matsimConfig.routing().setAccessEgressType(RoutingConfigGroup.AccessEgressType.accessEgressModeToLink); // in matsim-13-w37
        }
//		config.routing().setAccessEgressType( RoutingConfigGroup.AccessEgressType.none );
        // yyyyyy Silo uses a re-implementation of a lot of matsim infrastructure, and that is outside injection.  The more advanced access/egress types are not implemented there.
        // kai, apr'26

        matsimConfig.controller().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.overwriteExistingFiles );
//        matsimConfig.controller().setLastIteration(50);


//        if (ptScenario.equals(PtScenario.NONE)) {
//            matsimConfig.network().setInputFile("matsimInput/nw_cap30.xml");
//            matsimConfig.transit().setTransitScheduleFile(null);
//        } else
        if (ptScenario.equals(PtScenario.NES)) {
            // do nothing
        } else if (ptScenario.equals(PtScenario.RINGSX)) {
            matsimConfig.network().setInputFile("matsimInput/nw_cap30_rings_x.xml");
            matsimConfig.transit().setTransitScheduleFile("matsimInput/ts_rings_x.xml");
//            matsimConfig.transit().setVehiclesFile("matsimInput/tv_rings_x.xml");
        } else {
            throw new RuntimeException();
        }
        

        if (autofreiScenario.equals(AutofreiScenario.BASE)) {
            // do nothing
        } else if (autofreiScenario.equals(AutofreiScenario.SMALL)) {
            Network network = NetworkUtils.createNetwork();
            new MatsimNetworkReader(network).readFile("useCases/fabiland/scenario/" + matsimConfig.network().getInputFile());

            // Carfree
            Node node13 = network.getNodes().get(Id.createNodeId(13));
            Set<Link> carfreeLinks = new HashSet<>(node13.getInLinks().values());
            carfreeLinks.addAll(node13.getOutLinks().values());

            for (Link carfreeLink : carfreeLinks) {
                carfreeLink.setFreespeed(carfreeLink.getFreespeed() / 100);
            }


            new NetworkWriter(network).write("useCases/fabiland/scenario/matsimInput/_nw_jr_tmp.xml");
            matsimConfig.network().setInputFile("matsimInput/_nw_jr_tmp.xml");
        } else if (autofreiScenario.equals(AutofreiScenario.LARGE)) {
            Network network = NetworkUtils.createNetwork();
            new MatsimNetworkReader(network).readFile("useCases/fabiland/scenario/" + matsimConfig.network().getInputFile());

            // all links must have a centroid within +-5000m
            Set<Link> carfreeLinks = network.getLinks().values().stream().filter(link -> Math.abs(link.getCoord().getX()) <= 5000 & Math.abs(link.getCoord().getY()) <= 5000).collect(Collectors.toSet());
            for (Link carfreeLink : carfreeLinks) {
                carfreeLink.setFreespeed(carfreeLink.getFreespeed() / 100);
            }

            new NetworkWriter(network).write("useCases/fabiland/scenario/matsimInput/_nw_jr_tmp.xml");
            matsimConfig.network().setInputFile("matsimInput/_nw_jr_tmp.xml");

        } else {
            throw new RuntimeException();
        }

//        RoutingConfigGroup.TeleportedModeParams walkParams = matsimConfig.routing().getTeleportedModeParams().get(TransportMode.walk);
//        walkParams.setTeleportedModeSpeed(walkParams.getTeleportedModeSpeed() * 10);
//
//        RoutingConfigGroup.TeleportedModeParams nNwalkParams = matsimConfig.routing().getTeleportedModeParams().get(TransportMode.non_network_walk);
//        nNwalkParams.setTeleportedModeSpeed(nNwalkParams.getTeleportedModeSpeed() * 10);
        // Somehow, some version matsim is starting again for the accessibility computation, and that wipes the directory after the main run.
        // --> did not help

        logger.warn("Constructing data container ...");
        DataContainer dataContainer = DataBuilderFabiland.buildDataContainer(siloConfig, matsimConfig);
        DataBuilderFabiland.readInput(siloConfig, dataContainer);
        logger.warn("... done with constructing data container.");


        logger.warn("Constructing model container ...");
        ModelContainer modelContainer = ModelBuilderFabiland.getModelContainer(dataContainer, siloConfig, matsimConfig);

//        ModelContainer modelContainer = ModelBuilderFabilandSimplified.getModelContainer(dataContainer, siloConfig, matsimConfig);
        logger.warn("... done with constructing model container.");

        logger.warn("Constructing silo model ...");
        SiloModel model = new SiloModel(siloConfig, dataContainer, modelContainer);

        model.addResultMonitor( new DefaultResultsMonitor(dataContainer, siloConfig) );
        model.addResultMonitor( new MultiFileResultsMonitor(dataContainer, siloConfig) );
        model.addResultMonitor( new HouseholdSatisfactionMonitor(dataContainer, siloConfig, modelContainer) );
        logger.warn("... done with constructing silo model.");

        logger.warn("Running silo model ...");
        model.runModel();
        logger.warn("Finished SILO.");
    }
}
