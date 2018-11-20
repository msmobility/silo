package de.tum.bgu.msm.properties.modules;


import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class MainProperties {

    /**
     * Name of the scenario and of the output folder.
     */
    public final String scenarioName;

    /**
     * Track model runtime (true or false)
     */
    public final boolean trackTime;

    /**
     * Path of the base directory. Set to the properties path by default.
     */
    public final String baseDirectory;

    /**
     * Start year of SILO run.
     */
    public final int startYear;

    /**
     * End year of SILO run.
     */
    public final int endYear;

    /**
     * Income thresholds to define {@link IncomeCategory} low, medium, high, and very high.
     */
    public final int[] incomeBrackets;

    /**
     * Number of dwelling quality categories.
     */
    public final int qualityLevels;

    /**
     * Model seed. By default it is equal to -1 and generates a random result.
     */
    public final int randomSeed;

    /**
     * Value of {@link Implementation}.
     */
    public final Implementation implementation;

    /**
     * Use microlocation (XY coordinates) of dwellings, jobs and schools.
     */
    public final boolean useMicrolocation;

    /**
     * Print out the synthetic population nd the development capacity file at the final year
     */
    public final boolean printOutFinalSyntheticPopulation;

    @Deprecated
    public final String prestoZoneFile;
    @Deprecated
    public final String scaledMicroDataHh;
    @Deprecated
    public final String scaledMicroDataPp;
    @Deprecated
    public final int[] bemModelYears;
    @Deprecated
    public final String housingEnvironmentImpactFile;
    @Deprecated
    public final String prestoSummaryFile;
    @Deprecated
    public final Set<Integer> scalingYears;
    @Deprecated
    public final boolean createMstmOutput;
    @Deprecated
    public final boolean createHousingEnvironmentImpactFile;
    @Deprecated
    public final boolean createPrestoSummary;
    @Deprecated
    public final String scalingControlTotals;

    public MainProperties(String propertiesBasePath, ResourceBundle bundle, Implementation implementation) {

        this.implementation = implementation;

        PropertiesUtil.newPropertySubmodule("Main properties");
        scenarioName = PropertiesUtil.getStringProperty(bundle, "scenario.name");
        //by omitting base directory one has to set up a working folder in intellij etc. which represents "." in the next line
        //add working directory as default value?
        baseDirectory = PropertiesUtil.getStringProperty(bundle, "base.directory", propertiesBasePath + "/");
        startYear = PropertiesUtil.getIntProperty(bundle, "start.year");
        endYear = PropertiesUtil.getIntProperty(bundle, "end.year");
        randomSeed = PropertiesUtil.getIntProperty(bundle, "random.seed", -1);
        printOutFinalSyntheticPopulation = PropertiesUtil.getBooleanProperty(bundle, "print.out.sp.final", true);

        PropertiesUtil.newPropertySubmodule("Main - runtime tracking");
        trackTime = PropertiesUtil.getBooleanProperty(bundle, "track.time", true);

        PropertiesUtil.newPropertySubmodule("Main - dwelling and income input data");
        incomeBrackets = PropertiesUtil.getIntPropertyArray(bundle,"income.brackets.hh.types", new int[]{20000,40000,60000}); //munich implementation
        qualityLevels = PropertiesUtil.getIntProperty(bundle, "dwelling.quality.levels.distinguished", 4);

        PropertiesUtil.newPropertySubmodule("Main microlocation");
        useMicrolocation = PropertiesUtil.getBooleanProperty(bundle, "use.microlocation", false);

        PropertiesUtil.newPropertySubmodule("Main = connection with other models and specific scenarios");
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

    }
}
