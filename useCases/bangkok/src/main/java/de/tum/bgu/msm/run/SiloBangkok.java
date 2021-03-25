package de.tum.bgu.msm.run;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.container.DefaultDataContainer;
import de.tum.bgu.msm.container.ModelContainer;
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
 * @author Rolf Moeckel and Ana Moreno
 * Created on May 12, 2016 in Munich, Germany
 */
public class SiloBangkok {

    private final static Logger logger = Logger.getLogger(SiloBangkok.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Bangkok Metropolitan Area");
        DefaultDataContainer dataContainer = DataBuilderBangkok.getModelDataForBangkok(properties, config);
        DataBuilderBangkok.read(properties, dataContainer);
        ModelContainer modelContainer = ModelBuilderBangkok.getModelContainerForBangkok(dataContainer, properties, config);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        //model.addResultMonitor(new ResultsMonitorMuc(dataContainer, properties));
        model.addResultMonitor(new MultiFileResultsMonitor(dataContainer, properties));
        model.addResultMonitor(new HouseholdSatisfactionMonitor(dataContainer, properties, modelContainer));
        model.addResultMonitor(new ModalSharesResultMonitor(dataContainer, properties));
        model.runModel();
        logger.info("Finished SILO.");
    }
}
