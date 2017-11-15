package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public final class HouseholdDataProperties {

    private static final String HH_FILE_ASCII     = "household.file.ascii";
    private static final String PP_FILE_ASCII     = "person.file.ascii";
    private static final String DD_FILE_ASCII     = "dwelling.file.ascii";
    private static final String JJ_FILE_ASCII     = "job.file.ascii";
    private static final String READ_BIN_FILE     = "read.binary.pop.files";
    private static final String POP_FILE_BIN      = "population.file.bin";
    private static final String INCOME_CHANGE     = "mean.change.of.yearly.income";
    private static final String SUMMARIZE_METRO   = "summarize.hh.near.selected.metro.stp";
    private static final String SELECTED_METRO    = "selected.metro.stops";
    private static final String HH_NEAR_METRO     = "hh.near.selected.metro.stops.summary";
    
    private final float meanIncomeChange;
    private final boolean readBinaryPopulation;
    private final String householdFileName;
    private final String binaryPopulationFile;
    private final String personFileName;
    private final boolean summarizeMetro;
    private final String selectedMetroStopsFile;
    private final String householdsNearMetroFile;
    private final String jobsFileName;
    private final String dwellingsFileName;

    public HouseholdDataProperties(ResourceBundle bundle) {
        meanIncomeChange = (float) ResourceUtil.getDoubleProperty(bundle, INCOME_CHANGE);
        readBinaryPopulation = ResourceUtil.getBooleanProperty(bundle, READ_BIN_FILE, false);
        
        binaryPopulationFile = ResourceUtil.getProperty(bundle, POP_FILE_BIN);
        
        summarizeMetro = ResourceUtil.getBooleanProperty(bundle, SUMMARIZE_METRO);
        selectedMetroStopsFile = ResourceUtil.getProperty(bundle,SELECTED_METRO);
        householdsNearMetroFile = ResourceUtil.getProperty(bundle,HH_NEAR_METRO);

        householdFileName = ResourceUtil.getProperty(bundle, HH_FILE_ASCII);
        personFileName =  ResourceUtil.getProperty(bundle, PP_FILE_ASCII);
        jobsFileName = ResourceUtil.getProperty(bundle, JJ_FILE_ASCII);
        dwellingsFileName = ResourceUtil.getProperty(bundle, DD_FILE_ASCII);
    }

    public float getMeanIncomeChange() {
        return meanIncomeChange;
    }

    public boolean isReadBinaryPopulation() {
        return readBinaryPopulation;
    }

    public String getHouseholdFileName() {
        return householdFileName;
    }

    public String getBinaryPopulationFile() {
        return binaryPopulationFile;
    }

    public String getPersonFileName() {
        return personFileName;
    }

    public boolean isSummarizeMetro() {
        return summarizeMetro;
    }

    public String getSelectedMetroStopsFile() {
        return selectedMetroStopsFile;
    }

    public String getHouseholdsNearMetroFile() {
        return householdsNearMetroFile;
    }

    public String getJobsFileName() {
        return jobsFileName;
    }

    public String getDwellingsFileName() {
        return dwellingsFileName;
    }
}
