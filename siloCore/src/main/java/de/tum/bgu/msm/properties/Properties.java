package de.tum.bgu.msm.properties;

import de.tum.bgu.msm.properties.modules.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public final class Properties {

    private static Properties instance;


    public static Properties get() {
        if(instance == null) {
            throw new RuntimeException("Properties not initialized yet! Make sure to call initializeProperties Method first!");
        }
        return instance;
    }

    public static Properties initializeProperties(String path) {

        if(instance != null) {
            throw new RuntimeException("Already initialized properties!");
        }
        instance = new Properties(path);
        return instance;
    }


    public final MainProperties main;
    public final TransportModelPropertiesModule transportModel;
    public final GeoProperties geo;
    public final RealEstateProperties realEstate;
    public final HouseholdDataProperties householdData;
    public final JobDataProperties jobData;
    public final EventRulesProperties eventRules;
    public final DemographicsProperties demographics;
    public final AccessibilityProperties accessibility;
    public final MovesProperties moves;
    public final TrackProperties track;
    public final SchoolDataProperties schoolData;
    private final String path;

    private Properties(String path) {
        this.path = path;
        File propFile = null;
        try {
            propFile = new File(this.path).getCanonicalFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ResourceBundle bundle = null;
        try {
            bundle = new PropertyResourceBundle(new FileReader(propFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        main = new MainProperties(propFile.getParent(), bundle);
        transportModel = new TransportModelPropertiesModule(bundle);
        geo = new GeoProperties(bundle);
        realEstate = new RealEstateProperties(bundle);
        householdData = new HouseholdDataProperties(bundle);
        jobData = new JobDataProperties(bundle);
        eventRules = new EventRulesProperties(bundle);
        demographics = new DemographicsProperties(bundle);
        accessibility = new AccessibilityProperties(bundle, main.startYear);
        moves = new MovesProperties(bundle);
        track = new TrackProperties(bundle);
        schoolData = new SchoolDataProperties(bundle);



        // copy properties file into scenarios directory


//        copyFile(baseDirectory + resourceBundleNames[0], baseDirectory + "scenOutput/" + scenarioName + "/" + prop[prop.length-1]);
        // I don't see how this can work.  resourceBundleNames[0] is already the full path name, so if you prepend "baseDirectory"
        // and it is not empty, the command cannot possibly work.  It may have worked by accident in the past if everybody
        // had the resourceBundle directly at the JVM file system root.  kai (and possibly already changed by dz before), aug'16


    }
}