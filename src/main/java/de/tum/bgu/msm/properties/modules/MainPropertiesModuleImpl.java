package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public class MainPropertiesModuleImpl implements MainPropertiesModule {

    private static final String RUN_SILO = "run.silo.model";

    private static final String TRACK_TIME = "track.time";
    private static final String TRACK_TIME_FILE = "track.time.file";

    private static final String SCALING_YEARS = "scaling.years";

    private static final String READ_SMALL_SYNPOP = "read.small.syn.pop";
    private static final String WRITE_SMALL_SYNPOP = "write.small.syn.pop";

    private static final String SPATIAL_RESULT_FILE_NAME = "spatial.result.file.name";

    private static final String CREATE_MSTM_OUTPUT_FILES = "create.mstm.socio.econ.files";
    private static final String CREATE_HOUSING_ENV_IMPACT_FILE = "create.housing.environm.impact.files";
    private static final String CREATE_PRESTO_SUMMARY_FILE     = "create.presto.summary.file";


    private final boolean runSilo;

    private final boolean trackTime;
    private final String trackTimeFile;

    private final int[] scalingYears;

    private final boolean readSmallSynpop;
    private final boolean writeSmallSynpop;

    private final String spatialResultFileName;

    private final boolean createMstmOutput;
    private final boolean createHousingEnvironmentImpactFile;
    private final boolean createPrestoSummary;

    public MainPropertiesModuleImpl(ResourceBundle bundle) {
        runSilo = ResourceUtil.getBooleanProperty(bundle, RUN_SILO, true);
        trackTime = ResourceUtil.getBooleanProperty(bundle, TRACK_TIME, false);
        trackTimeFile = bundle.getString(TRACK_TIME_FILE);
        scalingYears =  ResourceUtil.getIntegerArray(bundle, SCALING_YEARS);
        readSmallSynpop = ResourceUtil.getBooleanProperty(bundle, READ_SMALL_SYNPOP, false);
        writeSmallSynpop = ResourceUtil.getBooleanProperty(bundle, WRITE_SMALL_SYNPOP, false);
        spatialResultFileName = bundle.getString(SPATIAL_RESULT_FILE_NAME);
        createMstmOutput = ResourceUtil.getBooleanProperty(bundle, CREATE_MSTM_OUTPUT_FILES, false);
        createHousingEnvironmentImpactFile = ResourceUtil.getBooleanProperty(bundle, CREATE_HOUSING_ENV_IMPACT_FILE, false);
        createPrestoSummary = ResourceUtil.getBooleanProperty(bundle, CREATE_PRESTO_SUMMARY_FILE, false);
    }

    @Override
    public boolean isTrackTime() {
        return trackTime;
    }

    @Override
    public String getTrackTimeFile() {
        return trackTimeFile;
    }

    @Override
    public int[] getScalingYears() {
        return scalingYears;
    }

    @Override
    public boolean isReadSmallSynpop() {
        return readSmallSynpop;
    }

    @Override
    public boolean isWriteSmallSynpop() {
        return writeSmallSynpop;
    }

    @Override
    public String getSpatialResultFileName() {
        return spatialResultFileName;
    }

    @Override
    public boolean isCreateMstmOutput() {
        return createMstmOutput;
    }

    @Override
    public boolean isCreateHousingEnvironmentImpactFile() {
        return createHousingEnvironmentImpactFile;
    }

    @Override
    public boolean isCreatePrestoSummary() {
        return createPrestoSummary;
    }

    @Override
    public boolean isRunSilo() {
        return runSilo;
    }
}
