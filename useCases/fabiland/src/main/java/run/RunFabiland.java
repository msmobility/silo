package run;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.output.DefaultResultsMonitor;
import de.tum.bgu.msm.io.output.HouseholdSatisfactionMonitor;
import de.tum.bgu.msm.io.output.MultiFileResultsMonitor;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.RoutingConfigGroup;
import org.matsim.core.controler.OutputDirectoryHierarchy;

import java.util.Arrays;

public class RunFabiland {

    private final static Logger logger = LogManager.getLogger(RunFabiland.class);

    public static void main(String[] args) {
        // yyyy This does not run out of the box.  Presumably, it needs an argument.  Could you please add a comment that explains to make this here run?  Thanks ...  kai, jun'23
        // yyyyyy Also, there should be a regression test running this method.  kai, jun'23

        Properties properties = SiloUtil.siloInitialization(args[0]);

        String[] matsimArgs = Arrays.copyOfRange( args, 1, args.length );

        Config config = null;
//        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(matsimArgs);
//        }
        logger.info("Started SILO Fabiland sandbox model");

        // The following is obviously just a dirty quickfix until access/egress is default in MATSim
        if (properties.transportModel.includeAccessEgress) {
////            config.plansCalcRoute().setInsertingAccessEgressWalk(true); // in matsim-12
            config.routing().setAccessEgressType(RoutingConfigGroup.AccessEgressType.accessEgressModeToLink); // in matsim-13-w37
        }
//		config.routing().setAccessEgressType( RoutingConfigGroup.AccessEgressType.none );
		// yyyyyy Silo uses a re-implementation of a lot of matsim infrastructure, and that is outside injection.  The more advanced access/egress types are not implemented there.
		// kai, apr'26

		config.controller().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.overwriteExistingFiles );
		// Somehow, some version matsim is starting again for the accessibility computation, and that wipes the directory after the main run.
		// --> did not help

        logger.warn("Constructing data container ...");
        DataContainer dataContainer = DataBuilderFabiland.buildDataContainer(properties, config);
        DataBuilderFabiland.readInput(properties, dataContainer);
        logger.warn("... done with constructing data container.");

        logger.warn("Constructing model container ...");
        ModelContainer modelContainer = ModelBuilderFabiland.getModelContainer(dataContainer, properties, config);
        logger.warn("... done with constructing model container.");

        logger.warn("Constructing silo model ...");
		SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor( new DefaultResultsMonitor(dataContainer, properties) );
        model.addResultMonitor( new MultiFileResultsMonitor(dataContainer, properties) );
        model.addResultMonitor( new HouseholdSatisfactionMonitor(dataContainer, properties, modelContainer) );
        logger.warn("... done with constructing silo model.");

        logger.warn("Running silo model ...");
        model.runModel();
        logger.warn("Finished SILO.");
    }
}
