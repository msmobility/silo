package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public final class JobDataProperties {

    public final int maxStorageOfvacantJobs;
    public final String[] jobTypes;
    public final boolean readBinaryJobFile;
    public final String jobsFileName;
    public final String binaryJobsFileName;
    public final boolean hasControlYears;
    public final int[] controlYears;
    public final String jobControlTotalsFileName;
    public final String employmentForeCastFile;
    public final String interpolatedEmploymentForecast;

    public JobDataProperties(ResourceBundle bundle) {
        maxStorageOfvacantJobs = ResourceUtil.getIntegerProperty(bundle, "vacant.job.by.reg.array");
        jobTypes = ResourceUtil.getArray(bundle, "employment.types");
        readBinaryJobFile = ResourceUtil.getBooleanProperty(bundle, "read.binary.jj.file", false);
        jobsFileName = ResourceUtil.getProperty(bundle, "job.file.ascii");
        binaryJobsFileName = ResourceUtil.getProperty(bundle, "job.file.bin");
        hasControlYears = bundle.containsKey("job.control.total.years");
        if(hasControlYears) {
            controlYears = ResourceUtil.getIntegerArray(bundle, "job.control.total.years");
        } else {
            controlYears = new int[]{};
        }
        jobControlTotalsFileName = ResourceUtil.getProperty(bundle, "job.control.total");
        employmentForeCastFile = ResourceUtil.getProperty(bundle, "interpol.empl.forecast");
        interpolatedEmploymentForecast = ResourceUtil.getProperty(bundle, "interpol.empl.forecast");
    }
}
