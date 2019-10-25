package run;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.output.DefaultResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

/**
 * Implements SILO for for the Maryland Statewide Transportation Model
 *
 * @author Rolf Moeckel
 */
public class SiloPerth {

    private final static Logger logger = Logger.getLogger(SiloPerth.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Starting SILO land use model for Perth");
        DataContainer dataContainer = DataBuilder.buildDataContainer(properties, config);
        DataBuilder.readInput(properties, dataContainer);

        ModelContainer modelContainer = ModelBuilderPerth.getModelContainerForMstm(dataContainer, properties, config);
        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(new DefaultResultsMonitor(dataContainer, properties));
        model.runModel();
        logger.info("Finished SILO.");
    }
}
