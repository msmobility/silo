package de.tum.bgu.msm.scenarios.healthMuc;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.ModelContainer;
//import de.tum.bgu.msm.io.ResultsMonitorMuc;
import de.tum.bgu.msm.io.output.DefaultResultsMonitor;
import de.tum.bgu.msm.io.output.HouseholdSatisfactionMonitor;
import de.tum.bgu.msm.io.output.ModalSharesResultMonitor;
import de.tum.bgu.msm.io.output.MultiFileResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

/**
 * Implements SILO for the Munich Metropolitan Area
 *
 * @author Qin Zhang*/


public class RunSiloMucHealth {

    private final static Logger logger = Logger.getLogger(RunSiloMucHealth.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Munich Metropolitan Area");
        HealthDataContainerImpl dataContainer = DataBuilderHealth.getModelDataForMuc(properties, config);
        DataBuilderHealth.read(properties, dataContainer);
        ModelContainer modelContainer = ModelBuilderMucHealth.getModelContainerForMuc(dataContainer, properties, config);
        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(new DefaultResultsMonitor(dataContainer, properties));
        model.addResultMonitor(new ModalSharesResultMonitor(dataContainer, properties));
        model.addResultMonitor(new MultiFileResultsMonitor(dataContainer, properties));
        model.addResultMonitor(new HouseholdSatisfactionMonitor(dataContainer, properties, modelContainer));
        model.runModel();
        logger.info("Finished SILO.");
    }
}
