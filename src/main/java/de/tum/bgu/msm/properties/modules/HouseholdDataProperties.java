package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.ResourceBundle;

public final class HouseholdDataProperties {

    public final float meanIncomeChange;
    public final boolean readBinaryPopulation;
    public final String householdFileName;
    public final String binaryPopulationFile;
    public final String personFileName;
    public final boolean summarizeMetro;
    public final String selectedMetroStopsFile;
    public final String householdsNearMetroFile;
    public final String jobsFileName;
    public final String dwellingsFileName;
    public final boolean writeBinPopFile;
    public final boolean writeBinDwellingsFile;
    public final boolean writeBinJobFile;
    public final String binaryDwellingsFile;
    public final String binaryJobFile;
    public final String householdFinalFileName;
    public final String personFinalFileName;
    public final String jobsFinalFileName;
    public final String dwellingsFinalFileName;



    public HouseholdDataProperties(ResourceBundle bundle) {

        PropertiesUtil.printOutModuleTitle("Synthetic population input files");
        PropertiesUtil.printOutModuleTitle("Synthetic popualtion for the base year");
        householdFileName = PropertiesUtil.getStringProperty(bundle, "household.file.ascii", "microData/hh");
        personFileName =  PropertiesUtil.getStringProperty(bundle, "person.file.ascii", "microData/pp");
        jobsFileName = PropertiesUtil.getStringProperty(bundle, "job.file.ascii", "microData/jj");
        //todo this property is doubled in Real State Properties
        dwellingsFileName = PropertiesUtil.getStringProperty(bundle, "dwelling.file.ascii","microData/dd");

        PropertiesUtil.printOutModuleTitle("Synthetic popualtion output of the final year");
        householdFinalFileName = PropertiesUtil.getStringProperty(bundle, "household.final.file.ascii", "microData/futureYears/hh");
        personFinalFileName = PropertiesUtil.getStringProperty(bundle, "person.final.file.ascii", "microData/futureYears/pp");
        jobsFinalFileName = PropertiesUtil.getStringProperty(bundle, "job.final.file.ascii", "microData/futureYears/jj");
        dwellingsFinalFileName = PropertiesUtil.getStringProperty(bundle, "dwelling.final.file.ascii", "microData/futureYears/dd");


        PropertiesUtil.printOutModuleTitle("Synthetic popualtion for the base year - binary files options");
        readBinaryPopulation = PropertiesUtil.getBooleanProperty(bundle, "read.binary.pop.files", false);
        writeBinPopFile = PropertiesUtil.getBooleanProperty(bundle, "write.binary.pop.files", false);
        writeBinDwellingsFile = PropertiesUtil.getBooleanProperty(bundle, "write.binary.dd.file", false);
        writeBinJobFile = PropertiesUtil.getBooleanProperty(bundle, "write.binary.jj.file", false);
        binaryPopulationFile = PropertiesUtil.getStringProperty(bundle, "population.file.bin", "microData/popData.bin");
        binaryDwellingsFile = PropertiesUtil.getStringProperty(bundle, "dwellings.file.bin","microData/ddData.bin" );
        binaryJobFile = PropertiesUtil.getStringProperty(bundle, "job.file.bin", "microData/jjData.bin");

        PropertiesUtil.printOutModuleTitle("Household data properties - additional models");
        meanIncomeChange = (float) PropertiesUtil.getDoubleProperty(bundle, "mean.change.of.yearly.income", 2000);
        summarizeMetro = PropertiesUtil.getBooleanProperty(bundle, "summarize.hh.near.selected.metro.stp", false);
        selectedMetroStopsFile = PropertiesUtil.getStringProperty(bundle, "selected.metro.stops","input/housingNearMetroTracer.csv" );
        householdsNearMetroFile = PropertiesUtil.getStringProperty(bundle, "hh.near.selected.metro.stops.summary", "householdNearSelectedMetroStops");






    }
}
