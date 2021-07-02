package de.tum.bgu.msm.syntheticPopulationGenerator.properties;

public final class PropertiesSynPop {

    private static PropertiesSynPop instance;
    public static PropertiesSynPop get(){
        if (instance == null){
            throw new RuntimeException("Properties not initialized yet!");
        }
        return instance;
    }

    public static PropertiesSynPop initializePropertiesSynPop(AbstractPropertiesSynPop propertiesSynPop){
        instance = new PropertiesSynPop(propertiesSynPop);
        return instance;
    }

    public final AbstractPropertiesSynPop main;

    private PropertiesSynPop(AbstractPropertiesSynPop propertiesSynPop){
        main = propertiesSynPop;
    }

}
