package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.data.Day;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;

import java.util.List;
import java.util.Map;

public interface MatsimScenarioAssembler {

    Scenario assembleScenario(Config initialMatsimConfig, int year, TravelTimes travelTimes);
    Map<Day, Scenario> assembleMultiScenarios(Config initialMatsimConfig, int year, TravelTimes travelTimes);

}
