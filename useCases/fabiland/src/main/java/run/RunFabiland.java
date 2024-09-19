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
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;

import java.util.Arrays;

public class RunFabiland {

    private final static Logger logger = Logger.getLogger(RunFabiland.class);

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
//            config.plansCalcRoute().setInsertingAccessEgressWalk(true); // in matsim-12
            config.plansCalcRoute().setAccessEgressType(PlansCalcRouteConfigGroup.AccessEgressType.accessEgressModeToLink); // in matsim-13-w37
        }

        DataContainer dataContainer = DataBuilderFabiland.buildDataContainer(properties, config);
        DataBuilderFabiland.readInput(properties, dataContainer);

        ModelContainer modelContainer = ModelBuilderFabiland.getModelContainer(dataContainer, properties, config);

        ResultsMonitor resultsMonitor = new DefaultResultsMonitor(dataContainer, properties);
        MultiFileResultsMonitor multiFileResultsMonitor = new MultiFileResultsMonitor(dataContainer, properties);
        HouseholdSatisfactionMonitor householdSatisfactionMonitor = new HouseholdSatisfactionMonitor(dataContainer, properties, modelContainer);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(resultsMonitor);
        model.addResultMonitor(multiFileResultsMonitor);
        model.addResultMonitor(householdSatisfactionMonitor);
        model.runModel();
        logger.info("Finished SILO.");
    }
}
