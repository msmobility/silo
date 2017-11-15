package de.tum.bgu.msm.properties;

import de.tum.bgu.msm.properties.modules.*;

import java.util.ResourceBundle;

public final class Properties {

    private final MainPropertiesModule mainProperties;
    private final CblcmPropertiesModule cblcmProperties;
    private final TransportModelPropertiesModule transportModelProperties;
    private final GeoProperties geoProperties;
    private final RealEstateProperties realEstatePrperties;
    private final HouseholdDataProperties householdDataProperties;
    private final JobDataProperties jobDataProperties;

    public Properties(ResourceBundle bundle) {
        mainProperties = new MainPropertiesModuleImpl(bundle);
        cblcmProperties = new CblcmPropertiesModule(bundle);
        transportModelProperties = new TransportModelPropertiesModule(bundle);
        geoProperties = new GeoProperties(bundle);
        realEstatePrperties = new RealEstateProperties(bundle);
        householdDataProperties = new HouseholdDataProperties(bundle);
        jobDataProperties = new JobDataProperties(bundle);
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

    public GeoProperties getGeoProperties() {
        return geoProperties;
    }

    public RealEstateProperties getRealEstateProperties() {
        return realEstatePrperties;
    }

    public HouseholdDataProperties getHouseholdDataProperties() {
        return householdDataProperties;
    }

    public JobDataProperties getJobDataProperties() {
        return jobDataProperties;
    }
}