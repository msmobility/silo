package de.tum.bgu.msm.scenarios.longCommutePenalty;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.output.DefaultResultsMonitor;
import de.tum.bgu.msm.io.output.MultiFileResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.run.DataBuilderTak;
import de.tum.bgu.msm.scenarios.oneCarPolicy.OneCarPolicyModelBuilderTak;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

public class RunLongCommutePenaltyTak {

    private static final Logger logger = Logger.getLogger(RunLongCommutePenaltyTak.class);

    public static void main(String[] args) {
        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for Takamatsu (Kagawa). Scenario: One car policy.");
        DataContainer dataContainer = DataBuilderTak.getTakModelData(properties, config);
        DataBuilderTak.read(properties, dataContainer);
        ModelContainer modelContainer = LongCommutePenaltyModelBuilderTak.getTakModels(
                dataContainer, properties, config);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(new DefaultResultsMonitor(dataContainer,properties));
        model.addResultMonitor(new MultiFileResultsMonitor(dataContainer, properties));
        model.runModel();
        logger.info("Finished SILO.");
    }


}
