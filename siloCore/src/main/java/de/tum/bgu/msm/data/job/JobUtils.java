package de.tum.bgu.msm.data.job;

public class JobUtils {

    private final static JobFactory factory = new JobFactoryImpl();

    private JobUtils(){};

    public static JobFactory getFactory() {
        return factory;
    }
}
