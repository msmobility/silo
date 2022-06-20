package de.tum.bgu.msm.scenarios.decisionTreeRelocation;

import de.tum.bgu.msm.DataBuilder;
import de.tum.bgu.msm.ModelBuilderMuc;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.SiloMuc;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.MultiFileResultsMonitorMuc;
import de.tum.bgu.msm.io.output.HouseholdSatisfactionMonitor;
import de.tum.bgu.msm.io.output.ModalSharesResultMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

public class RunSiloDecissionTrees {

    private final static Logger logger = Logger.getLogger(SiloMuc.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Munich Metropolitan Area");
        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, config);
        DataBuilder.read(properties, dataContainer);
        ModelContainer modelContainer = ModelBuilderMucWithDecissionTrees.getModelContainerForMuc(dataContainer, properties, config);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(new MultiFileResultsMonitorMuc(dataContainer, properties));
        model.addResultMonitor(new ModalSharesResultMonitor(dataContainer, properties));
        model.addResultMonitor(new HouseholdSatisfactionMonitor(dataContainer, properties, modelContainer));
        model.runModel();
        logger.info("Finished SILO.");
    }
}
