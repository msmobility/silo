package de.tum.bgu.msm.models;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.simulator.UpdateListener;
import org.apache.log4j.Logger;

import java.util.Random;

public class AbstractModel implements UpdateListener {

    private final static Logger logger = Logger.getLogger(AbstractModel.class);

    protected final DataContainer dataContainer;
    protected final Properties properties;
    protected final Random random;

    public AbstractModel(DataContainer dataContainer, Properties properties, Random random) {
        this.dataContainer = dataContainer;
        this.properties = properties;
        this.random = random;
    }

    @Override
    public void setup() {

    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void endYear(int year) {

    }

    @Override
    public void endSimulation() {

    }

    public void logCurrentRandomState() {
        logger.info(this.getClass().getSimpleName() + " | random: " + random.nextDouble());
    }
}
