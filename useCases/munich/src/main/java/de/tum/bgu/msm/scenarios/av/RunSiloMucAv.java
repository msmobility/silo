package de.tum.bgu.msm.scenarios.av;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.SiloMuc;
import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.io.MultiFileResultsMonitorMuc;
import de.tum.bgu.msm.io.output.HouseholdSatisfactionMonitor;
import de.tum.bgu.msm.io.output.ModalSharesResultMonitor;
import de.tum.bgu.msm.io.output.MultiFileResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

public class RunSiloMucAv {
    private final static Logger logger = Logger.getLogger(SiloMuc.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Munich Metropolitan Area");
        DataContainerWithSchools dataContainer = DataBuilderForAV.getModelDataForMuc(properties, config);
        DataBuilderForAV.read(properties, dataContainer);
        boolean useAv = Boolean.parseBoolean(args[2]);
        String avSwitchCalculator = args[3];
        ModelContainer modelContainer = ModelBuilderMucAv.getModelContainerAvForMuc(dataContainer, properties, config, useAv, avSwitchCalculator);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(new MultiFileResultsMonitorMuc(dataContainer, properties));
        //model.addResultMonitor(new MultiFileResultsMonitor(dataContainer, properties));
        model.addResultMonitor(new HouseholdSatisfactionMonitor(dataContainer, properties, modelContainer));
        if (useAv){
            model.addResultMonitor(new AVOwnershipResultsMonitor(modelContainer, dataContainer, properties));
        }
        model.addResultMonitor(new ModeChoiceResultsMonitor(dataContainer, properties));
        model.addResultMonitor(new ModalSharesResultMonitor(dataContainer, properties));
        model.runModel();
        logger.info("Finished SILO.");
    }
}