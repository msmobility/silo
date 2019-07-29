package de.tum.bgu.msm.models.transportModel.matsim;

import de.tum.bgu.msm.container.DataContainer;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;

/**
 * @author dziemke, nkuehnel
 */
public interface MatsimPopulationGenerator {

    Population generatePopulation(Config config, DataContainer dataContainer, double populationScalingFactor);
}