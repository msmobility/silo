package de.tum.bgu.msm.data.job;

import de.tum.bgu.msm.data.dwelling.DwellingFactory;

public class JobUtils {

    private final static JobFactory factory = new JobFactoryImpl();

    private JobUtils(){};

    public static JobFactory getFactory() {
        return factory;
    }
}
