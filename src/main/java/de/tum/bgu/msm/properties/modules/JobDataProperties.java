package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

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
        PropertiesUtil.printOutModuleTitle("Job data properties");
        maxStorageOfvacantJobs = PropertiesUtil.getIntProperty(bundle, "vacant.job.by.reg.array", 100000);
        jobTypes = PropertiesUtil.getStringPropertyArray(bundle, "employment.types", new String[]{"Agri","Mnft","Util","Cons","Retl","Trns","Finc","Rlst","Admn","Serv"});

        PropertiesUtil.printOutModuleTitle("Job - forecasts");
        hasControlYears = bundle.containsKey("job.control.total.years");
        if(hasControlYears) {
            //todo this values are read as 11 and 50 instead of 2011 and 2050!!!
            controlYears = PropertiesUtil.getIntPropertyArray(bundle, "job.control.total.years", new int[]{11,50});
        } else {
            controlYears = new int[]{};
        }
        jobControlTotalsFileName = PropertiesUtil.getStringProperty(bundle, "job.control.total", "input/assumptions/employmentForecast.csv");
        //todo the following two properties are equal
        employmentForeCastFile = PropertiesUtil.getStringProperty(bundle, "interpol.empl.forecast", "interpolatedEmploymentForecast");
        interpolatedEmploymentForecast = PropertiesUtil.getStringProperty(bundle, "interpol.empl.forecast", "interpolatedEmploymentForecast");

        PropertiesUtil.printOutModuleTitle("Job - synthetic jobs input");
        //todo this properties are doubled in household data properties
        readBinaryJobFile = PropertiesUtil.getBooleanProperty(bundle, "read.binary.jj.file", false);
        jobsFileName = PropertiesUtil.getStringProperty(bundle, "job.file.ascii", "microData/jj");
        binaryJobsFileName = PropertiesUtil.getStringProperty(bundle, "job.file.bin", "microData/jjData.bin");
    }
}
