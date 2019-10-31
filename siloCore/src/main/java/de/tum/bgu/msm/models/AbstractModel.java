package de.tum.bgu.msm.models;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.simulator.UpdateListener;
import org.apache.log4j.Logger;

import java.util.Random;

public abstract class AbstractModel implements UpdateListener {

    private final static Logger logger = Logger.getLogger(AbstractModel.class);

    protected final DataContainer dataContainer;
    protected final Properties properties;
    protected final Random random;

    public AbstractModel(DataContainer dataContainer, Properties properties, Random random) {
        this.dataContainer = dataContainer;
        this.properties = properties;
        this.random = random;
    }

    public void logCurrentRandomState() {
        logger.info(this.getClass().getSimpleName() + " | random: " + random.nextDouble());
    }
}
