package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public class TransportModelPropertiesModule {

    private static final String MODEL_YEARS = "transport.model.years";
    private static final String SKIM_YEARS = "skim.years";

    private static final String RUN_TRAVEL_DEMAND_MODEL = "mito.run.travel.model";
    private static final String FILE_DEMAND_MODEL = "mito.properties.file";

    private static final String RUN_TRAVEL_MODEL_MATSIM = "matsim.run.travel.model";

    private final int[] modelYears;
    private final int[] skimYears;

    private final boolean runTravelDemandModel;
    private final String demandModelPropertiesPath;

    private final boolean runMatsim;

    public TransportModelPropertiesModule(ResourceBundle bundle) {
        modelYears = ResourceUtil.getIntegerArray(bundle, MODEL_YEARS);
        skimYears = ResourceUtil.getIntegerArray(bundle, SKIM_YEARS);
        runTravelDemandModel = ResourceUtil.getBooleanProperty(bundle, RUN_TRAVEL_DEMAND_MODEL, false);
        demandModelPropertiesPath = ResourceUtil.getProperty(bundle, FILE_DEMAND_MODEL);
        runMatsim = ResourceUtil.getBooleanProperty(bundle, RUN_TRAVEL_MODEL_MATSIM, false);
    }

    public int[] getModelYears() {
        return modelYears;
    }

    public int[] getSkimYears() {
        return skimYears;
    }

    public boolean isRunTravelDemandModel() {
        return runTravelDemandModel;
    }

    public String getDemandModelPropertiesPath() {
        return demandModelPropertiesPath;
    }

    public boolean isRunMatsim() {
        return runMatsim;
    }
}
