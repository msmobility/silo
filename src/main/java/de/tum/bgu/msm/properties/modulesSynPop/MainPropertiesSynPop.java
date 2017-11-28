package de.tum.bgu.msm.properties.modulesSynPop;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public class MainPropertiesSynPop {

    public final boolean runSyntheticPopulation;
    public final int yearMicroData;
    public final boolean runIPU;
    public final boolean twoGeographicalAreasIPU;


    public MainPropertiesSynPop(ResourceBundle bundle) {

        runSyntheticPopulation = ResourceUtil.getBooleanProperty(bundle, "run.synth.pop.generator", false);
        yearMicroData = ResourceUtil.getIntegerProperty(bundle, "year.micro.data");
        runIPU = ResourceUtil.getBooleanProperty(bundle, "run.ipu.synthetic.pop");
        twoGeographicalAreasIPU = ResourceUtil.getBooleanProperty(bundle, "run.ipu.city.and.county");

    }
}
