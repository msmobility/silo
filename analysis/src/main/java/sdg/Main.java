package sdg;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.properties.Properties;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(Implementation.MUNICH, args[0]);


        Map<Integer, Household> householdMap = new HashMap<>();
        Map<Integer, Person> personMap = new HashMap<>();
        SiloDataContainer siloDataContainer = SiloDataContainer.loadSiloDataContainer(properties, householdMap, personMap);

        SDGCalculator.calculateSdgIndicators(siloDataContainer, Properties.get().main.baseDirectory + "/scenOutput/" + Properties.get().main.scenarioName,
                Properties.get().main.startYear);

    }

}
