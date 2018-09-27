package de.tum.bgu.msm.data.job;


import java.util.HashMap;

/**
 * Job types that are distinguished in the model
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 22 February 2013 in Santa Fe
 **/

public class JobType {


    private static String[] jobTypes;
    private static HashMap<String,Integer> ordinal;


    public JobType(String[] jobTypesArg) {
        jobTypes = jobTypesArg;
        ordinal = new HashMap<>();
        for (int i = 0; i < jobTypes.length; i++) {
            ordinal.put(jobTypes[i], i);
        }
    }


    public static String getJobType (int i) {
        return jobTypes[i];
    }


    public static int getOrdinal(String jobType) {
        // return 0-based index of jobType
        return ordinal.get(jobType);
    }


    public static String[] getJobTypes() {
        return jobTypes;
    }


    public static int getNumberOfJobTypes() {
        return jobTypes.length;
    }
}
