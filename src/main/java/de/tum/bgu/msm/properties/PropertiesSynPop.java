package de.tum.bgu.msm.properties;

import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.properties.modules.MainProperties;
import de.tum.bgu.msm.properties.modulesSynPop.MainPropertiesSynPop;

import java.util.ResourceBundle;

public final class PropertiesSynPop {

    private static PropertiesSynPop instance;
    public static PropertiesSynPop get(){
        if (instance == null){
            throw new RuntimeException("Properties not initialized yet!");
        }
        return instance;
    }

    public static void initializePropertiesSynPop(ResourceBundle bundle, SiloModel.Implementation implementation){
        instance = new PropertiesSynPop(bundle, implementation);
    }

    public final MainPropertiesSynPop main;




    private PropertiesSynPop(ResourceBundle bundle, SiloModel.Implementation implementation){
        main = new MainPropertiesSynPop(bundle);


    }

}
