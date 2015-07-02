package edu.umd.ncsg.data;

import com.pb.common.util.ResourceUtil;

import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Job types that are distinguished in the model
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 22 February 2013 in Santa Fe
 **/

public class JobType {

    protected static final String PROPERTIES_EMPLOYMENT_TYPE                  = "employment.types";
    private static String[] jobTypes;
    private static int nJobTypes;
    private static HashMap<String,Integer> ordinal;


    public JobType(ResourceBundle rb) {
        jobTypes = ResourceUtil.getArray(rb, PROPERTIES_EMPLOYMENT_TYPE);
        ordinal = new HashMap<>();
        for (int i = 0; i < jobTypes.length; i++) {
            ordinal.put(jobTypes[i], i);
        }
        nJobTypes = jobTypes.length;
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
        return nJobTypes;
    }
}
