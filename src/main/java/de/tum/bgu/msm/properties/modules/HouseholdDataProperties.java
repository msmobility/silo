package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

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

    public HouseholdDataProperties(ResourceBundle bundle) {
        meanIncomeChange = (float) ResourceUtil.getDoubleProperty(bundle, "mean.change.of.yearly.income");
        readBinaryPopulation = ResourceUtil.getBooleanProperty(bundle, "read.binary.pop.files", false);
        
        binaryPopulationFile = ResourceUtil.getProperty(bundle, "population.file.bin");
        
        summarizeMetro = ResourceUtil.getBooleanProperty(bundle, "summarize.hh.near.selected.metro.stp");
        selectedMetroStopsFile = ResourceUtil.getProperty(bundle, "selected.metro.stops");
        householdsNearMetroFile = ResourceUtil.getProperty(bundle, "hh.near.selected.metro.stops.summary");

        householdFileName = ResourceUtil.getProperty(bundle, "household.file.ascii");
        personFileName =  ResourceUtil.getProperty(bundle, "person.file.ascii");
        jobsFileName = ResourceUtil.getProperty(bundle, "job.file.ascii");
        dwellingsFileName = ResourceUtil.getProperty(bundle, "dwelling.file.ascii");
        writeBinPopFile = ResourceUtil.getBooleanProperty(bundle, "write.binary.pop.files");
        writeBinDwellingsFile = ResourceUtil.getBooleanProperty(bundle, "write.binary.dd.file");
        writeBinJobFile = ResourceUtil.getBooleanProperty(bundle, "write.binary.jj.file");
        binaryDwellingsFile = ResourceUtil.getProperty(bundle, "dwellings.file.bin");
        binaryJobFile = ResourceUtil.getProperty(bundle, "job.file.bin");
    }
}
