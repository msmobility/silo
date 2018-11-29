package run;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.io.ParametersReader;
import de.tum.bgu.msm.io.PopulationReader;
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
    private static Map<Integer, Household> householdMap;
    private static Map<Integer, Person> personMap;

    public static void main(String[] args) {

        ParametersReader reader = new ParametersReader();
        completeParametersMap = reader.readData(args[1]);
        PopulationReader popReader = new PopulationReader();
        householdMap = popReader.readHouseholdFile(args[2]);
        personMap = popReader.readPersonFile(args[3],householdMap);

        for (int i = 1; i <= completeParametersMap.keySet().size(); i++) {

            Properties properties = SiloUtil.siloInitialization(Implementation.MUNICH, args[0], i);
            Config config = null;
            Map<String, Double> parametersMap = completeParametersMap.get(i);

            logger.info("Starting SILO. Combination of parameters: " + i);
            SiloModel model = new SiloModel(config, properties, parametersMap, i, householdMap, personMap);
            model.runModel();
            logger.info("Finished SILO. Combination of parameters: " + i);
        }
    }
}
