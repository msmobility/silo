package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public final class JobDataProperties {

    public final String[] jobTypes;
    public final String jobsFileName;
    public final String jobsIntermediatesFileName;
    public final String jobsFinalFileName;
    public final JobForecastMethod jobForecastMethod;
    public final String jobControlTotalsFileName;
    public final String employmentForeCastFile;
    public final Map<String,Double> growthRateInPercentByJobType = new HashMap<>();
    public final String jobStartTimeDistributionFile;
    public final String jobDurationDistributionFile;

    public enum JobForecastMethod {
        INTERPOLATION, RATE;
    }


    public JobDataProperties(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Job data properties");
        jobTypes = PropertiesUtil.getStringPropertyArray(bundle, "employment.types", new String[]{"Agri","Mnft","Util","Cons","Retl","Trns","Finc","Rlst","Admn","Serv"});

        PropertiesUtil.newPropertySubmodule("Job - forecasts");
        jobForecastMethod = JobForecastMethod.
                valueOf(PropertiesUtil.getStringProperty(bundle, "job.forecast.method", "rate").toUpperCase());
        jobControlTotalsFileName = PropertiesUtil.getStringProperty(bundle, "job.control.total", "input/assumptions/employmentForecast.csv");
        employmentForeCastFile = PropertiesUtil.getStringProperty(bundle, "interpol.empl.forecast", "interpolatedEmploymentForecast");
        //todo prepared for separate growth rates by industry
        if (jobForecastMethod.equals(JobForecastMethod.RATE)) {
            double overallJobGrowthRate = PropertiesUtil.getDoubleProperty(bundle, "job.growth.rate", 0.);
            for (String jobType : jobTypes) {
                //currently assign a unique
                growthRateInPercentByJobType.put(jobType, overallJobGrowthRate);
            }
        }

        PropertiesUtil.newPropertySubmodule("Job - synthetic jobs input");
        jobsFileName = PropertiesUtil.getStringProperty(bundle, "job.file.ascii", "microData/jj");
        jobsIntermediatesFileName = PropertiesUtil.getStringProperty(bundle, "job.intermediates.file.ascii", "microData/jj");
        jobsFinalFileName = PropertiesUtil.getStringProperty(bundle, "job.final.file.ascii", "microData/jj");

        PropertiesUtil.newPropertySubmodule("Job - job time input");
        jobStartTimeDistributionFile = PropertiesUtil.getStringProperty(bundle, "job.start.distribution.file", "input/jobStartTimeDistributions.csv");
        jobDurationDistributionFile = PropertiesUtil.getStringProperty(bundle, "job.duration.distribution.file", "input/jobDurationDistributions.csv");
    }
}
