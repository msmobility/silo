package de.tum.bgu.msm.properties.modules;


import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.properties.PropertiesUtil;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class MainProperties {

    public final boolean runSilo;
    public final String scenarioName;

    public final boolean trackTime;
    public final String trackTimeFile;

    public final Set<Integer> scalingYears;

    public final boolean readSmallSynpop;
    public final boolean writeSmallSynpop;

    public final String resultFileName;
    public final String spatialResultFileName;

    public final boolean createMstmOutput;
    public final boolean createHousingEnvironmentImpactFile;
    public final boolean createPrestoSummary;
    public final String scalingControlTotals;
    public final String baseDirectory;
    public final int startYear;
    public final int endYear;
    public final int gregorianIterator;
    public final int[] incomeBrackets;
    public final int qualityLevels;
    public final int randomSeed;
    public final boolean runSynPop;
    public final int smallSynPopSize;
    public final String prestoZoneFile;
    public final String scaledMicroDataHh;
    public final String scaledMicroDataPp;
    public final int[] bemModelYears;
    public final String housingEnvironmentImpactFile;
    public final String prestoSummaryFile;

    public final Implementation implementation;

    public final boolean runDwellingMicrolocation;
    public final boolean runJobMicrolocation;
    public final boolean runSchoolMicrolocation;

    public MainProperties(ResourceBundle bundle, Implementation implementation) {

        this.implementation = implementation;

        PropertiesUtil.printOutModuleTitle("Main properties");
        runSilo = PropertiesUtil.getBooleanProperty(bundle, "run.silo.model", true);
        scenarioName = PropertiesUtil.getStringProperty(bundle, "scenario.name");
        //by omitting base directory one has to set up a working folder in intellij etc. which represents "." in the next line
        baseDirectory = PropertiesUtil.getStringProperty(bundle, "base.directory", "./");
        startYear = PropertiesUtil.getIntProperty(bundle, "start.year");
        endYear = PropertiesUtil.getIntProperty(bundle, "end.year");
        randomSeed = PropertiesUtil.getIntProperty(bundle, "random.seed", -1);

        PropertiesUtil.printOutModuleTitle("Main - runtime tracking");
        trackTime = PropertiesUtil.getBooleanProperty(bundle, "track.time", true);
        trackTimeFile = PropertiesUtil.getStringProperty(bundle, "track.time.file","timeTracker.csv");

        PropertiesUtil.printOutModuleTitle("Main - result files");
        resultFileName = PropertiesUtil.getStringProperty(bundle, "result.file.name", "resultFile");
        spatialResultFileName =  PropertiesUtil.getStringProperty(bundle,"spatial.result.file.name", "resultFileSpatial");

        PropertiesUtil.printOutModuleTitle("Main - dwelling and income input data");
        incomeBrackets = PropertiesUtil.getIntPropertyArray(bundle,"income.brackets.hh.types", new int[]{20000,40000,60000});
        qualityLevels = PropertiesUtil.getIntProperty(bundle, "dwelling.quality.levels.distinguished", 4);

        PropertiesUtil.printOutModuleTitle("Main synthetic population");
        runSynPop = PropertiesUtil.getBooleanProperty(bundle, "run.synth.pop.generator", false);
        smallSynPopSize = PropertiesUtil.getIntProperty(bundle, "size.small.syn.pop", 0);
        readSmallSynpop = PropertiesUtil.getBooleanProperty(bundle, "read.small.syn.pop", false);
        writeSmallSynpop = PropertiesUtil.getBooleanProperty(bundle, "write.small.syn.pop", false);

        PropertiesUtil.printOutModuleTitle("Main microlocation");
        runDwellingMicrolocation = PropertiesUtil.getBooleanProperty(bundle, "run.dwelling.microlocation", false);
        runJobMicrolocation = PropertiesUtil.getBooleanProperty(bundle, "run.job.microlocation", false);
        runSchoolMicrolocation = PropertiesUtil.getBooleanProperty(bundle, "run.school.microlocation", false);

        PropertiesUtil.printOutModuleTitle("Main = connection with other models and specific scenarios");
        createMstmOutput = PropertiesUtil.getBooleanProperty(bundle, "create.mstm.socio.econ.files", false);
        createHousingEnvironmentImpactFile = PropertiesUtil.getBooleanProperty(bundle, "create.housing.environm.impact.files", false);
        scalingYears = Arrays.stream(PropertiesUtil.getIntPropertyArray(bundle, "scaling.years", new int[] {-1}))
                .boxed().filter(i -> i > 0).collect(Collectors.toSet());
        scalingControlTotals = PropertiesUtil.getStringProperty(bundle, "scaling.years.control.totals", "input/assumptions/scalingYearsControlTotals.csv");
        scaledMicroDataHh = PropertiesUtil.getStringProperty(bundle, "scaled.micro.data.hh", "microdata/scaled/hh_");
        scaledMicroDataPp = PropertiesUtil.getStringProperty(bundle, "scaled.micro.data.pp", "microdata/scaled/pp_");
        createPrestoSummary = PropertiesUtil.getBooleanProperty(bundle, "create.presto.summary.file", false);
        prestoZoneFile = PropertiesUtil.getStringProperty(bundle,"presto.regions", "input/prestoRegionDefinition");
        prestoSummaryFile = PropertiesUtil.getStringProperty(bundle, "presto.summary.file", "prestoSummary");
        bemModelYears = PropertiesUtil.getIntPropertyArray(bundle, "bem.model.years", new int[]{2000,2040});
        housingEnvironmentImpactFile = PropertiesUtil.getStringProperty(bundle, "housing.environment.impact.file.name", "bemHousing");

        PropertiesUtil.printOutModuleTitle("Main - gregorian iterator");
        gregorianIterator = PropertiesUtil.getIntProperty(bundle, "this.gregorian.iterator", 1);
    }
}
