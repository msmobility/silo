package run;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.output.DefaultResultsMonitor;
import de.tum.bgu.msm.io.output.MultiFileResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

public class RunFabiland {

    private final static Logger logger = Logger.getLogger(RunFabiland.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO Fabiland sandbox model");

        // The following is obviously just a dirty quickfix until access/egress is default in MATSim
        if (properties.transportModel.includeAccessEgress) {
            config.plansCalcRoute().setInsertingAccessEgressWalk(true);
        }

        DataContainer dataContainer = DataBuilderFabiland.buildDataContainer(properties, config);
        DataBuilderFabiland.readInput(properties, dataContainer);

        ModelContainer modelContainer = ModelBuilderFabiland.getModelContainer(dataContainer, properties, config);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(new DefaultResultsMonitor(dataContainer, properties)); //the old monitor
        model.addResultMonitor(new MultiFileResultsMonitor(dataContainer, properties)); //the new, multi file monitor
        model.runModel();
        logger.info("Finished SILO.");
    }
}