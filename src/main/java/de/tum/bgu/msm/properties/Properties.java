package de.tum.bgu.msm.properties;

import de.tum.bgu.msm.properties.modules.CblcmPropertiesModule;
import de.tum.bgu.msm.properties.modules.MainPropertiesModule;
import de.tum.bgu.msm.properties.modules.MainPropertiesModuleImpl;
import de.tum.bgu.msm.properties.modules.TransportModelPropertiesModule;

import java.util.ResourceBundle;

public final class Properties {

    private final MainPropertiesModule mainProperties;
    private final CblcmPropertiesModule cblcmProperties;
    private final TransportModelPropertiesModule transportModelProperties;

    public Properties(ResourceBundle bundle) {
        mainProperties = new MainPropertiesModuleImpl(bundle);
        cblcmProperties = new CblcmPropertiesModule(bundle);
        transportModelProperties = new TransportModelPropertiesModule(bundle);
    }

    public MainPropertiesModule getMainProperties() {
        return mainProperties;
    }

    public CblcmPropertiesModule getCblcmProperties() {
        return cblcmProperties;
    }

    public TransportModelPropertiesModule getTransportModelProperties() {
        return transportModelProperties;
    }
}