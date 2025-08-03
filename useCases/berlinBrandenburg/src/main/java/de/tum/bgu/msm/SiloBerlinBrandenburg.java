package de.tum.bgu.msm;

import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.output.HouseholdSatisfactionMonitor;
import de.tum.bgu.msm.io.output.ModalSharesResultMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.io.MultiFileResultsMonitorBerlinBrandenburg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

/**
 * Implements SILO for the Berlin-Brandenburg Metropolitan Area
 *
 * @author Wei-Chieh Huang
 * Created on July 24, 2025 in Munich, Germany
 */
public class SiloBerlinBrandenburg {

    private final static Logger logger = LogManager.getLogger(SiloBerlinBrandenburg.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Berlin-Brandenburg Metropolitan Area");
        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForBerlinBrandenburg(properties, config);
        DataBuilder.read(properties, dataContainer);
        ModelContainer modelContainer = ModelBuilderBerlinBrandenburg.getModelContainerForMuc(dataContainer, properties, config);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(new MultiFileResultsMonitorBerlinBrandenburg(dataContainer, properties));
        model.addResultMonitor(new ModalSharesResultMonitor(dataContainer, properties));
        model.addResultMonitor(new HouseholdSatisfactionMonitor(dataContainer, properties, modelContainer));
        model.runModel();
        logger.info("Finished SILO.");
    }
}
