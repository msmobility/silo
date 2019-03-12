package de.tum.bgu.msm.models;

import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.properties.Properties;

public class AbstractModel {

    protected final SiloDataContainer dataContainer;
    protected final Properties properties;

    public AbstractModel(SiloDataContainer dataContainer, Properties properties) {
        this.dataContainer = dataContainer;
        this.properties = properties;
    }
}
