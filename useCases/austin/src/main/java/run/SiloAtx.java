package run;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.output.DefaultResultsMonitor;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.run.DataBuilder;
import de.tum.bgu.msm.run.ModelBuilderMstm;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

/**
 * Implements SILO for the Maryland Statewide Transportation Model
 *
 * @author Rolf Moeckel
 * Created on Nov 22, 2013 in Wheaton, MD
 */

public class SiloAtx {

    private final static Logger logger = Logger.getLogger(de.tum.bgu.msm.run.SiloMstm.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Starting SILO land use model for Austin");
        DataContainer dataContainer = DataBuilder.buildDataContainer(properties, config);
        DataBuilder.readInput(properties, dataContainer);
        ModelContainer modelContainer = ModelBuilderMstm.getModelContainerForMstm(dataContainer, properties, config);
        ResultsMonitor resultsMonitor = new DefaultResultsMonitor(dataContainer, properties);
        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(resultsMonitor);
        model.runModel();
        logger.info("Finished SILO.");
    }
}
