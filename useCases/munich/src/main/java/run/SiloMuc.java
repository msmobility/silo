package run;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.io.ParametersReader;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;

import java.util.Map;

/**
 * Implements SILO for the Munich Metropolitan Area
 *
 * @author Rolf Moeckel and Ana Moreno
 * Created on May 12, 2016 in Munich, Germany
 */
public class SiloMuc {

    private static Logger logger = Logger.getLogger(SiloMuc.class);
    private static Map<Integer, Map<String, Double>> completeParametersMap;

    public static void main(String[] args) {

        ParametersReader reader = new ParametersReader();
        completeParametersMap = reader.readData(args[1]);

        for (int i = 1; i <= completeParametersMap.keySet().size(); i++) {

            Properties properties = SiloUtil.siloInitialization(Implementation.MUNICH, args[0]);
            Config config = null;
            Map<String, Double> parametersMap = completeParametersMap.get(i);

            logger.info("Starting SILO land use model for the Munich Metropolitan Area");
            SiloModel model = new SiloModel(config, properties, parametersMap);
            model.runModel();
            logger.info("Finished SILO.");
        }
    }
}
