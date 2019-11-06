package de.tum.bgu.msm.run;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.output.DefaultResultsMonitor;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;

/**
 * @author dziemke
 */
public final class SiloMatsim {
    private final static Logger logger = Logger.getLogger(SiloMatsim.class);

    private final Properties properties;
    private final Config matsimConfig;// = ConfigUtils.createConfig(); // SILO-MATSim integration-specific

    /**
     * Option to set the matsim config directly, at this point meant for tests.
     */
    public SiloMatsim(String args, Config config) {
        properties = SiloUtil.siloInitialization(args);
        matsimConfig = config;
    }

    public final void run() {
        logger.info("Starting SILO program for MATSim");
        DataContainer dataContainer = DataBuilder.buildDataContainer(properties, matsimConfig);
        DataBuilder.readInput(properties, dataContainer);
        ModelContainer modelContainer = ModelBuilderMstm.getModelContainerForMstm(dataContainer, properties, matsimConfig);
        ResultsMonitor resultsMonitor = new DefaultResultsMonitor(dataContainer, properties);
        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(resultsMonitor);
        model.runModel();
        logger.info("Finished SILO.");

    }
}