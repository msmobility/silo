package de.tum.bgu.msm.models;

import de.tum.bgu.msm.container.SiloDataContainerImpl;
import de.tum.bgu.msm.properties.Properties;

public class AbstractModel {

    protected final SiloDataContainerImpl dataContainer;
    protected final Properties properties;

    public AbstractModel(SiloDataContainerImpl dataContainer, Properties properties) {
        this.dataContainer = dataContainer;
        this.properties = properties;
    }
}
