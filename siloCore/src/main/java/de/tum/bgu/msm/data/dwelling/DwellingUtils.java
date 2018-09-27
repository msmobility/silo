package de.tum.bgu.msm.data.dwelling;

public class DwellingUtils {

    private static DwellingFactory factory = new DwellingFactoryImpl();

    private DwellingUtils(){};

    public static DwellingFactory getFactory() {
        return factory;
    }
}
