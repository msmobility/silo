package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.ResourceBundle;

public final class JobDataProperties {

    public final int maxStorageOfvacantJobs;
    public final String[] jobTypes;
    public final boolean readBinaryJobFile;
    public final boolean writeBinJobFile;
    public final String jobsFileName;
    public final String jobsFinalFileName;
    public final String binaryJobsFileName;
    public final String jobControlTotalsFileName;
    public final String employmentForeCastFile;
    public final String jobStartTimeDistributionFile;
    public final String jobDurationDistributionFile;


    public JobDataProperties(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Job data properties");
        maxStorageOfvacantJobs = PropertiesUtil.getIntProperty(bundle, "vacant.job.by.reg.array", 100000);
        jobTypes = PropertiesUtil.getStringPropertyArray(bundle, "employment.types", new String[]{"Agri","Mnft","Util","Cons","Retl","Trns","Finc","Rlst","Admn","Serv"});

        PropertiesUtil.newPropertySubmodule("Job - forecasts");
        jobControlTotalsFileName = PropertiesUtil.getStringProperty(bundle, "job.control.total", "input/assumptions/employmentForecast.csv");
        employmentForeCastFile = PropertiesUtil.getStringProperty(bundle, "interpol.empl.forecast", "interpolatedEmploymentForecast");

        PropertiesUtil.newPropertySubmodule("Job - synthetic jobs input");
        jobsFileName = PropertiesUtil.getStringProperty(bundle, "job.file.ascii", "microData/jj");
        jobsFinalFileName = PropertiesUtil.getStringProperty(bundle, "job.final.file.ascii", "microData/futureYears/jj");
        readBinaryJobFile = PropertiesUtil.getBooleanProperty(bundle, "read.binary.jj.file", false);
        writeBinJobFile = PropertiesUtil.getBooleanProperty(bundle, "write.binary.jj.file", false);
        binaryJobsFileName = PropertiesUtil.getStringProperty(bundle, "job.file.bin", "microData/jjData.bin");

        PropertiesUtil.newPropertySubmodule("Job - job time input");
        jobStartTimeDistributionFile = PropertiesUtil.getStringProperty(bundle, "job.start.distribution.file", "input/jobStartTimeDistributions.csv");
        jobDurationDistributionFile = PropertiesUtil.getStringProperty(bundle, "job.duration.distribution.file", "input/jobDurationDistributions.csv");

    }
}
