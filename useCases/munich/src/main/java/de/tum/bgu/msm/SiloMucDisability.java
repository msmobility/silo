package de.tum.bgu.msm;

import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.DataContainerMuc;
import de.tum.bgu.msm.events.DisabilityEvent;
import de.tum.bgu.msm.io.ResultsMonitorMuc;
import de.tum.bgu.msm.io.output.ResultsMonitor;
import de.tum.bgu.msm.models.demography.death.DefaultDeathStrategy;
import de.tum.bgu.msm.models.disability.DefaultDisabilityStrategy;
import de.tum.bgu.msm.models.disability.DisabilityImpl;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

/**
 * Implements SILO for the Munich Metropolitan Area
 *
 * @author Rolf Moeckel and Ana Moreno
 * Created on May 12, 2016 in Munich, Germany
 */
public class SiloMucDisability {

    private final static Logger logger = Logger.getLogger(SiloMucDisability.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Starting SILO land use model for the Munich Metropolitan Area");
        DataContainerMuc dataContainer = DataBuilderDisability.getModelDataForMuc(properties);
        DataBuilderDisability.read(properties, dataContainer);
        ModelContainer modelContainer = ModelBuilder.getModelContainerForMuc(dataContainer, properties, config);
        modelContainer.registerEventModel(DisabilityEvent.class, new DisabilityImpl(dataContainer, properties,new DefaultDisabilityStrategy()));
        ResultsMonitor resultsMonitor = new ResultsMonitorMuc(dataContainer, properties);
        SiloModel model = new SiloModel(properties, dataContainer, modelContainer, resultsMonitor);
        model.runModel();
        logger.info("Finished SILO.");
    }
}
