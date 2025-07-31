import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MatsimPlansAnalyzer {
    public static void main(String[] args){

        Config config = ConfigUtils.createConfig();
        config.plans().setInputFile("/media/admin/EXTERNAL_USB1/simulation_results_for_paper/base/matsim/2021/thursday/car/2021.output_plans.xml.gz");

        Scenario scenario = ScenarioUtils.loadScenario(config);
        //Population pop = PopulationUtils.readPopulation("/media/admin/EXTERNAL_USB1/simulation_results_for_paper/base/matsim/2021/thursday/car/2021.output_plans.xml.gz");

        checkForDuplicatePersonIds(scenario.getPopulation());

        System.out.println(scenario.getPopulation().getPersons().size());

    }

    public static void checkForDuplicatePersonIds(Population population) {
        Map<Id<Person>, Integer> idCountMap = new HashMap<>();

        // First pass: count occurrences of each ID
        for (Person person : population.getPersons().values()) {
            Id<Person> personId = person.getId();
            idCountMap.put(personId, idCountMap.getOrDefault(personId, 0) + 1);
        }

        // Second pass: identify duplicates
        boolean duplicatesFound = false;
        for (Map.Entry<Id<Person>, Integer> entry : idCountMap.entrySet()) {
            if (entry.getValue() > 1) {
                duplicatesFound = true;
                System.err.println("Duplicate ID found: " + entry.getKey() +
                        " (appears " + entry.getValue() + " times)");
            }
        }

        if (!duplicatesFound) {
            System.out.println("No duplicate person IDs found in the population.");
        } else {
            System.err.println("WARNING: Duplicate person IDs detected!");
        }
    }
}
