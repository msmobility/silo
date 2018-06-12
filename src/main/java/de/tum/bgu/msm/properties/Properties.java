package de.tum.bgu.msm.properties;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.properties.modules.*;

import java.util.ResourceBundle;

public final class Properties {

    private static Properties instance;

    public static Properties get() {
        if(instance == null) {
            throw new RuntimeException("Properties not initialized yet! Make sure to call initializeProperties Method first!");
        }
        return instance;
    }

    public static void initializeProperties(ResourceBundle bundle, Implementation implementation) {
        if(instance != null) {
            throw new RuntimeException("Already initialized properties!");
        }
        instance = new Properties(bundle, implementation);
    }

    public final MainProperties main;
    public final CblcmProperties cblcm;
    public final TransportModelPropertiesModule transportModel;
    public final GeoProperties geo;
    public final RealEstateProperties realEstate;
    public final HouseholdDataProperties householdData;
    public final JobDataProperties jobData;
    public final EventRulesProperties eventRules;
    public final DemographicsProperties demographics;
    public final AccessibilityProperties accessibility;
    public final MovesProperties moves;

    private Properties(ResourceBundle bundle, Implementation implementation) {
        main = new MainProperties(bundle, implementation);
        cblcm = new CblcmProperties(bundle);
        transportModel = new TransportModelPropertiesModule(bundle);
        geo = new GeoProperties(bundle, implementation);
        realEstate = new RealEstateProperties(bundle);
        householdData = new HouseholdDataProperties(bundle);
        jobData = new JobDataProperties(bundle);
        eventRules = new EventRulesProperties(bundle);
        demographics = new DemographicsProperties(bundle);
        accessibility = new AccessibilityProperties(bundle);
        moves = new MovesProperties(bundle);
    }
}