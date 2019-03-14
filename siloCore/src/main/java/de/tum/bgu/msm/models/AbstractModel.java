package de.tum.bgu.msm.models;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.properties.Properties;

public class AbstractModel {

    protected final DataContainer dataContainer;
    protected final Properties properties;

    public AbstractModel(DataContainer dataContainer, Properties properties) {
        this.dataContainer = dataContainer;
        this.properties = properties;
    }
}
