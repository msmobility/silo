package de.tum.bgu.msm.data.school;


public class SchoolUtils {

    private final static SchoolFactory factory = new SchoolFactoryImpl();

    private SchoolUtils(){};

    public static SchoolFactory getFactory() {
        return factory;
    }
}
