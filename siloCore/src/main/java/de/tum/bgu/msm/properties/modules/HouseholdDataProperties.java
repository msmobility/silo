package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.ResourceBundle;

public final class HouseholdDataProperties {

    public final float meanIncomeChange;
    public final String householdFileName;
    public final String personFileName;
    public final boolean summarizeMetro;
    public final String selectedMetroStopsFile;
    public final String householdsNearMetroFile;

    public final String householdIntermediatesFileName;
    public final String householdFinalFileName;
    public final String personIntermediatesFileName;
    public final String personFinalFileName;

    public HouseholdDataProperties(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Synthetic persons and households for the base year");
        householdFileName = PropertiesUtil.getStringProperty(bundle, "household.file.ascii", "microData/hh");
        personFileName =  PropertiesUtil.getStringProperty(bundle, "person.file.ascii", "microData/pp");
        PropertiesUtil.newPropertySubmodule("Synthetic persons and households output of the final year");
        householdIntermediatesFileName = PropertiesUtil.getStringProperty(bundle, "household.intermediates.file.ascii", "microData/hh");
        householdFinalFileName = PropertiesUtil.getStringProperty(bundle, "household.final.file.ascii", "microData/hh");
        personIntermediatesFileName = PropertiesUtil.getStringProperty(bundle, "person.intermediates.file.ascii", "microData/pp");
        personFinalFileName = PropertiesUtil.getStringProperty(bundle, "person.final.file.ascii", "microData/pp");

        PropertiesUtil.newPropertySubmodule("Household data properties - additional models");
        meanIncomeChange = (float) PropertiesUtil.getDoubleProperty(bundle, "mean.change.of.yearly.income", 2000);
        summarizeMetro = PropertiesUtil.getBooleanProperty(bundle, "summarize.hh.near.selected.metro.stp", false);
        selectedMetroStopsFile = PropertiesUtil.getStringProperty(bundle, "selected.metro.stops","input/housingNearMetroTracer.csv" );
        householdsNearMetroFile = PropertiesUtil.getStringProperty(bundle, "hh.near.selected.metro.stops.summary", "householdNearSelectedMetroStops");
    }
}
