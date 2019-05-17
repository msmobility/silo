package run;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.io.ParametersReader;
import de.tum.bgu.msm.io.PopulationReader;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.matsim.core.config.Config;

import java.io.File;
import java.util.Map;

import static de.tum.bgu.msm.utils.SiloUtil.initializeRandomNumber;

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

        Properties properties = SiloUtil.siloInitialization(Implementation.MUNICH, args[0]);
        completeParametersMap = new ParametersReader().readData(args[1]);
        householdMap = new PopulationReader().readHouseholdFile(args[2]);
        personMap = new PopulationReader().readPersonFile(args[3],householdMap);

        SummarizeData.openResultFile(properties);
        SummarizeData.resultFile("combinationId" + "," + "year" + "," + "variable" + "," + "count");

        for (int i = 1; i <= completeParametersMap.keySet().size(); i++) {
            Config config = null;
            Map<String, Double> parametersMap = completeParametersMap.get(i);
            initializeRandomNumber(parametersMap.get("RandomSeed").intValue());
            logger.info("Starting SILO. Combination of parameters: " + i);
            SiloModel model = new SiloModel(config, properties, parametersMap, i, householdMap, personMap);
            model.runModel();
            logger.info("Finished SILO. Combination of parameters: " + i);
        }
        SummarizeData.resultFile("close", false);
    }

}
