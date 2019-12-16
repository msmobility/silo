package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.SiloMuc;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.ResultsMonitorMuc;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

public class SiloMucNoiseInsensitive {

    private final static Logger logger = Logger.getLogger(SiloMuc.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Munich Metropolitan Area");
        NoiseDataContainerImpl dataContainer = DataBuilderNoise.getModelDataForMuc(properties, config);
        DataBuilderNoise.read(properties, dataContainer);
        ModelContainer modelContainer = ModelBuilderMucNoiseInsensitive.getModelContainerForMuc(dataContainer, properties, config);
//        modelContainer.registerModelUpdateListener(new NoiseModel(dataContainer, properties, SiloUtil.provideNewRandom()));
        ResultsMonitor resultsMonitor = new ResultsMonitorMuc(dataContainer, properties);
        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(resultsMonitor);
        model.runModel();
        logger.info("Finished SILO.");
    }
}
