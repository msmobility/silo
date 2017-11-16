package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public class MainPropertiesImpl implements MainProperties {

    private static final String RUN_SILO = "run.silo.model";
    private static final String SCENARIO_NAME = "scenario.name";
    protected static final String BASE_DIRECTORY = "base.directory";
    protected static final String RANDOM_SEED = "random.seed";
    protected static final String TRACKING_FILE_NAME = "track.file.name";

    private static final String TRACK_TIME = "track.time";
    private static final String TRACK_TIME_FILE = "track.time.file";

    private static final String SCALING_YEARS = "scaling.years";
    private static final String SCALING_YEARS_CONTROL_TOTALS = "scaling.years.control.totals";

    private static final String RUN_SYN_POP = "run.synth.pop.generator";
    private static final String SIZE_SMALL_SYNPOP = "size.small.syn.pop";
    private static final String READ_SMALL_SYNPOP = "read.small.syn.pop";
    private static final String WRITE_SMALL_SYNPOP = "write.small.syn.pop";

    private static final String SPATIAL_RESULT_FILE_NAME = "spatial.result.file.name";

    private static final String CREATE_MSTM_OUTPUT_FILES = "create.mstm.socio.econ.files";
    private static final String CREATE_HOUSING_ENV_IMPACT_FILE = "create.housing.environm.impact.files";
    private static final String CREATE_PRESTO_SUMMARY_FILE = "create.presto.summary.file";

    public static final String START_YEAR = "start.year";
    public static final String SIMULATION_PERIOD_LENGTH = "simulation.period.length";
    public static final String END_YEAR = "end.year";
    public static final String GREGORIAN_ITERATOR = "this.gregorian.iterator";

    public static final String INCOME_BRACKETS = "income.brackets.hh.types";
    public static final String NUMBER_OF_DWELLING_QUALITY_LEVELS = "dwelling.quality.levels.distinguished";

    private final boolean runSilo;
    private final String scenarioName;

    private final boolean trackTime;
    private final String trackTimeFile;

    private final int[] scalingYears;

    private final boolean readSmallSynpop;
    private final boolean writeSmallSynpop;

    private final String spatialResultFileName;

    private final boolean createMstmOutput;
    private final boolean createHousingEnvironmentImpactFile;
    private final boolean createPrestoSummary;
    private final String scalingControlTotals;
    private final String baseDirectory;
    private final int startYear;
    private final int endYear;
    private final int simulationLength;
    private final int gregorianIterator;
    private final int[] incomeBrackets;
    private final int qualityLevels;
    private final int randomSeed;
    private final boolean runSynPop;
    private final int smallSynPopSize;

    public MainPropertiesImpl(ResourceBundle bundle) {
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
        scalingControlTotals = ResourceUtil.getProperty(bundle, SCALING_YEARS_CONTROL_TOTALS);
        scenarioName = ResourceUtil.getProperty(bundle, SCENARIO_NAME);
        baseDirectory = ResourceUtil.getProperty(bundle, BASE_DIRECTORY);
        startYear = ResourceUtil.getIntegerProperty(bundle, START_YEAR);
        endYear = ResourceUtil.getIntegerProperty(bundle, END_YEAR);
        simulationLength = ResourceUtil.getIntegerProperty(bundle, SIMULATION_PERIOD_LENGTH);
        gregorianIterator = ResourceUtil.getIntegerProperty(bundle, GREGORIAN_ITERATOR);
        incomeBrackets = ResourceUtil.getIntegerArray(bundle, INCOME_BRACKETS);
        qualityLevels = ResourceUtil.getIntegerProperty(bundle, NUMBER_OF_DWELLING_QUALITY_LEVELS);
        randomSeed = ResourceUtil.getIntegerProperty(bundle, RANDOM_SEED, 42);
        runSynPop = ResourceUtil.getBooleanProperty(bundle, RUN_SYN_POP, false);
        smallSynPopSize = ResourceUtil.getIntegerProperty(bundle, SIZE_SMALL_SYNPOP, 0);
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

    @Override
    public String getScalingControlTotals() {
        return scalingControlTotals;
    }

    @Override
    public String getScenarioName() {
        return scenarioName;
    }

    @Override
    public String getBaseDirectory() {
        return baseDirectory;
    }

    @Override
    public int getStartYear() {
        return startYear;
    }

    @Override
    public int getEndYear() {
        return endYear;
    }

    @Override
    public int getSimulationLength() {
        return simulationLength;
    }

    @Override
    public int getGregorianIterator() {
        return gregorianIterator;
    }

    @Override
    public int[] getIncomeBrackets() {
        return incomeBrackets;
    }

    @Override
    public int getQualityLevels() {
        return qualityLevels;
    }

    @Override
    public int getRandomSeed() {
        return randomSeed;
    }

    @Override
    public boolean isRunSynPop() {
        return runSynPop;
    }

    @Override
    public int getSmallSynPopSize() {
        return smallSynPopSize;
    }
}
