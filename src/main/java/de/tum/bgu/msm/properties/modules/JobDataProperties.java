package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public final class JobDataProperties {

    private static final String JJ_FILE_BIN = "job.file.bin";
    private static final String JJ_FILE_ASCII = "job.file.ascii";
    private static final String READ_BIN_FILE = "read.binary.jj.file";
    private static final String MAX_NUM_VAC_JOB = "vacant.job.by.reg.array";
    private static final String EMPLOYMENT_FORECAST = "interpol.empl.forecast";
    private static final String JOB_CONTROL_TOTAL = "job.control.total";
    private static final String JOB_CONTROL_YEARS = "job.control.total.years";
    private static final String EMPLOYMENT_TYPE = "employment.types";
    
    private final int maxStorageOfvacantJobs;
    private final String[] jobTypes;
    private final boolean readBinaryJobFile;
    private final String jobsFileName;
    private final String binaryJobsFileName;
    private final boolean hasControlYears;
    private final int[] controlYears;
    private final String jobControlTotalsFileName;
    private final String employmentForeCastFile;

    public JobDataProperties(ResourceBundle bundle) {
        maxStorageOfvacantJobs = ResourceUtil.getIntegerProperty(bundle, MAX_NUM_VAC_JOB);
        jobTypes = ResourceUtil.getArray(bundle, EMPLOYMENT_TYPE);
        readBinaryJobFile = ResourceUtil.getBooleanProperty(bundle, READ_BIN_FILE, false);
        jobsFileName = ResourceUtil.getProperty(bundle, JJ_FILE_ASCII);
        binaryJobsFileName = ResourceUtil.getProperty(bundle, JJ_FILE_BIN);
        hasControlYears = bundle.containsKey(JOB_CONTROL_YEARS);
        if(hasControlYears) {
            controlYears = ResourceUtil.getIntegerArray(bundle, JOB_CONTROL_YEARS);
        } else {
            controlYears = new int[]{};
        }
        jobControlTotalsFileName = ResourceUtil.getProperty(bundle, JOB_CONTROL_TOTAL);
        employmentForeCastFile = ResourceUtil.getProperty(bundle, EMPLOYMENT_FORECAST);
    }

    public int getMaxStorageOfvacantJobs() {
        return maxStorageOfvacantJobs;
    }

    public String[] getJobTypes() {
        return jobTypes;
    }

    public boolean isReadBinaryJobFile() {
        return readBinaryJobFile;
    }

    public String getJobsFileName() {
        return jobsFileName;
    }

    public String getBinaryJobsFileName() {
        return binaryJobsFileName;
    }

    public boolean hasControlYears() {
        return hasControlYears;
    }

    public int[] getControlYears() {
        return controlYears;
    }

    public String getJobControlTotalsFileName() {
        return jobControlTotalsFileName;
    }

    public String getEmploymentForeCastFile() {
        return employmentForeCastFile;
    }
}
