package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface MatsimScenarioAssembler {

    Scenario assembleScenario(Config initialMatsimConfig, int year, TravelTimes travelTimes);

    // used default identifier to allow for using following method in only one class that implements MatsimScenarioAssembler.
    default Scenario assembleScenarioWithPreviousPlans(
            Config initialMatsimConfig,
            int year,
            TravelTimes travelTimes,
            HashMap<Id, PreviousYearPlan> previousYearPlans) {

        throw new UnsupportedOperationException(
                "This MatsimScenarioAssembler does not support previous-year plans.");
    }
    Map<Day, Scenario> assembleMultiScenarios(Config initialMatsimConfig, int year, TravelTimes travelTimes);

}
