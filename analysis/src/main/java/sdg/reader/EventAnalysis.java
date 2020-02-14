package sdg.reader;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.scenario.ScenarioUtils;
import sdg.data.AnalyzedPerson;

import java.util.Map;

public class EventAnalysis {


    public Map<Id<Person>, AnalyzedPerson> runEventAnalysis(String networkFileName, String eventFileName){

        EventsManager eventsManager = EventsUtils.createEventsManager();
        Config config = ConfigUtils.createConfig();
        config.network().setInputFile(networkFileName);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        CongestionEventHandler congestionEventHandler = new CongestionEventHandler(scenario.getNetwork());
        eventsManager.addHandler(congestionEventHandler);
        new MatsimEventsReader(eventsManager).readFile(eventFileName);
        return congestionEventHandler.getPersons();
    }
}
