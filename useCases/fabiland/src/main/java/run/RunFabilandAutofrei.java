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
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.RoutingConfigGroup;
import org.matsim.core.controler.OutputDirectoryHierarchy;

import java.util.Arrays;

public class RunFabilandAutofrei {

    private final static Logger logger = LogManager.getLogger(RunFabilandAutofrei.class);

    public static void main(String[] args) {
        // see regression test

        // args: SILO config, MATSim config
        // e.g., "useCases/fabiland/scenario/1r_ae.properties useCases/fabiland/scenario/config_cap30_1-l_nes_smc.xml"
        // or, to match regression test ...
        // "useCases/fabiland/scenario/test.properties useCases/fabiland/scenario/config_cap30_1-l_nes_smc.xml --config:controller.lastIteration 1 "

        Properties siloConfig = SiloUtil.siloInitialization("useCases/fabiland/scenario/25r_ae.properties");

//        String[] matsimArgs = Arrays.copyOfRange( args, 1, args.length );

        Config matsimConfig = null;
//        if (args.length > 1 && args[1] != null) {
        matsimConfig = ConfigUtils.loadConfig("useCases/fabiland/scenario/config_cap30_1-l_nes_smc.xml");
//        }
        logger.info("Started SILO Fabiland sandbox model");

        // The following is obviously just a dirty quickfix until access/egress is default in MATSim
        if (siloConfig.transportModel.includeAccessEgress) {
////            config.plansCalcRoute().setInsertingAccessEgressWalk(true); // in matsim-12
            matsimConfig.routing().setAccessEgressType(RoutingConfigGroup.AccessEgressType.accessEgressModeToLink); // in matsim-13-w37
        }
//		config.routing().setAccessEgressType( RoutingConfigGroup.AccessEgressType.none );
        // yyyyyy Silo uses a re-implementation of a lot of matsim infrastructure, and that is outside injection.  The more advanced access/egress types are not implemented there.
        // kai, apr'26

        matsimConfig.controller().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.overwriteExistingFiles );
        matsimConfig.controller().setLastIteration(1);


        matsimConfig.network().setInputFile("matsimInput/autofrei.xml");

        RoutingConfigGroup.TeleportedModeParams walkParams = matsimConfig.routing().getTeleportedModeParams().get(TransportMode.walk);
        walkParams.setTeleportedModeSpeed(walkParams.getTeleportedModeSpeed() * 10);


        RoutingConfigGroup.TeleportedModeParams nNwalkParams = matsimConfig.routing().getTeleportedModeParams().get(TransportMode.non_network_walk);
        nNwalkParams.setTeleportedModeSpeed(nNwalkParams.getTeleportedModeSpeed() * 10);
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
