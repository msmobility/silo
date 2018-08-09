package de.tum.bgu.msm.models;

import de.tum.bgu.msm.container.SiloDataContainer;

public class AbstractModel {

    protected final SiloDataContainer dataContainer;

    public AbstractModel(SiloDataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }
}
