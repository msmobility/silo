package de.tum.bgu.msm.properties.modules;

public interface MainPropertiesModule {
    boolean isTrackTime();

    String getTrackTimeFile();

    int[] getScalingYears();

    boolean isReadSmallSynpop();

    boolean isWriteSmallSynpop();

    String getSpatialResultFileName();

    boolean isCreateMstmOutput();

    boolean isCreateHousingEnvironmentImpactFile();

    boolean isCreatePrestoSummary();

    boolean isRunSilo();

    String getScalingControlTotals();

    String getScenarioName();

    String getBaseDirectory();

    int getStartYear();

    int getEndYear();

    int getSimulationLength();

    int getGregorianIterator();

    int[] getIncomeBrackets();

    int getQualityLevels();

    int getRandomSeed();

    boolean isRunSynPop();

    int getSmallSynPopSize();
}
