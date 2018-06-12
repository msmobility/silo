package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.Implementation;

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

    public MainProperties(ResourceBundle bundle, Implementation implementation) {
        this.implementation = implementation;
        runSilo = ResourceUtil.getBooleanProperty(bundle, "run.silo.model", true);
        trackTime = ResourceUtil.getBooleanProperty(bundle, "track.time", false);
        trackTimeFile = bundle.getString("track.time.file");
        scalingYears = Arrays.stream(ResourceUtil.getIntegerArray(bundle, "scaling.years"))
                .boxed().filter(i -> i > 0).collect(Collectors.toSet());
        readSmallSynpop = ResourceUtil.getBooleanProperty(bundle, "read.small.syn.pop", false);
        writeSmallSynpop = ResourceUtil.getBooleanProperty(bundle, "write.small.syn.pop", false);
        spatialResultFileName = bundle.getString("spatial.result.file.name");
        createMstmOutput = ResourceUtil.getBooleanProperty(bundle, "create.mstm.socio.econ.files", false);
        createHousingEnvironmentImpactFile = ResourceUtil.getBooleanProperty(bundle, "create.housing.environm.impact.files", false);
        createPrestoSummary = ResourceUtil.getBooleanProperty(bundle, "create.presto.summary.file", false);
        scalingControlTotals = ResourceUtil.getProperty(bundle, "scaling.years.control.totals");
        scenarioName = ResourceUtil.getProperty(bundle, "scenario.name");
        baseDirectory = ResourceUtil.getProperty(bundle, "base.directory");
        startYear = ResourceUtil.getIntegerProperty(bundle, "start.year");
        endYear = ResourceUtil.getIntegerProperty(bundle, "end.year");
        gregorianIterator = ResourceUtil.getIntegerProperty(bundle, "this.gregorian.iterator");
        incomeBrackets = ResourceUtil.getIntegerArray(bundle, "income.brackets.hh.types");
        qualityLevels = ResourceUtil.getIntegerProperty(bundle, "dwelling.quality.levels.distinguished");
        randomSeed = ResourceUtil.getIntegerProperty(bundle, "random.seed", 42);
        runSynPop = ResourceUtil.getBooleanProperty(bundle, "run.synth.pop.generator", false);
        smallSynPopSize = ResourceUtil.getIntegerProperty(bundle, "size.small.syn.pop", 0);
        prestoZoneFile = bundle.getString("presto.regions");
        scaledMicroDataHh = bundle.getString("scaled.micro.data.hh");
        scaledMicroDataPp = bundle.getString("scaled.micro.data.pp");
        bemModelYears = ResourceUtil.getIntegerArray(bundle, "bem.model.years");
        housingEnvironmentImpactFile = ResourceUtil.getProperty(bundle, "housing.environment.impact.file.name");
        prestoSummaryFile = ResourceUtil.getProperty(bundle, "presto.summary.file");
    }
}
