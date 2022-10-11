package de.tum.bgu.msm.scenarios.ev;

import de.tum.bgu.msm.*;
import de.tum.bgu.msm.container.*;
import de.tum.bgu.msm.data.vehicle.VehicleUtil;
import de.tum.bgu.msm.io.*;
import de.tum.bgu.msm.io.output.*;
import de.tum.bgu.msm.properties.*;
import de.tum.bgu.msm.schools.*;
import de.tum.bgu.msm.utils.*;
import org.apache.log4j.*;
import org.matsim.core.config.*;

/**
 * Implements SILO for the Munich Metropolitan Area
 *
 * @author Rolf Moeckel and Ana Moreno
 * Created on May 12, 2016 in Munich, Germany
 */
public class SiloMucEV {

    private final static Logger logger = Logger.getLogger(SiloMucEV.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0]);
        VehicleUtil.initializeVehicleUtils();

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Munich Metropolitan Area");
        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, config);
        DataBuilder.read(properties, dataContainer);
        ModelContainer modelContainer = ModelBuilderMucWithEVModel.getModelContainerForMuc(dataContainer, properties, config);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(new MultiFileResultsMonitorMuc(dataContainer, properties));
        model.addResultMonitor(new ModalSharesResultMonitor(dataContainer, properties));
        model.addResultMonitor(new HouseholdSatisfactionMonitor(dataContainer, properties, modelContainer));
        model.addResultMonitor(new EVResultMonitor(modelContainer, dataContainer, properties));
        model.runModel();
        logger.info("Finished SILO.");
    }
}
