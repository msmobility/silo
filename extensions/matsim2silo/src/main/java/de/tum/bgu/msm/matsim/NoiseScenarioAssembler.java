package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.properties.Properties;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;

public class NoiseScenarioAssembler implements MatsimScenarioAssembler {

    private final SimpleMatsimScenarioAssembler delegate;
    private final DataContainer dataContainer;

    public NoiseScenarioAssembler(DataContainer dataContainer, Properties properties) {
        this.dataContainer = dataContainer;
        this.delegate = new SimpleMatsimScenarioAssembler(dataContainer, properties);
        this.noiseReceiverPoints = new NoiseReceiverPoints();
    }

    @Override
    public Scenario assembleScenario(Config initialMatsimConfig, int year) {
        return null;
    }
}
