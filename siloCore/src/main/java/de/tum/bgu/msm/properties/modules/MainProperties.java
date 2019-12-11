package de.tum.bgu.msm.properties.modules;


import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.properties.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class MainProperties {

    private final static Logger logger = Logger.getLogger(MainProperties.class);

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
     * Base year of SILO input data.
     */
    public final int baseYear;

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
     * Print out the synthetic population nd the development capacity file at the final year
     */
    public final boolean printOutFinalSyntheticPopulation;
    
    /**
     * Returns the number of logical threads available at runtime.
     */
    public final int numberOfThreads;

    /**
     * Returns the sub-sample of the population to be simulated in SILO
     */
    public final double scaleFactor;

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

    public MainProperties(String propertiesBasePath, ResourceBundle bundle) {

        PropertiesUtil.newPropertySubmodule("Main properties");
        scenarioName = PropertiesUtil.getStringProperty(bundle, "scenario.name");
        logger.info("Scenario name: " + scenarioName);
        //by omitting base directory one has to set up a working folder in intellij etc. which represents "." in the next line
        //add working directory as default value?
        try {
            baseDirectory = new File(PropertiesUtil.getStringProperty(bundle, "base.directory", propertiesBasePath)).getCanonicalPath()+ "/";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        baseYear = PropertiesUtil.getIntProperty(bundle, "base.year");
        startYear = PropertiesUtil.getIntProperty(bundle, "start.year", baseYear);
        endYear = PropertiesUtil.getIntProperty(bundle, "end.year");
        randomSeed = PropertiesUtil.getIntProperty(bundle, "random.seed", -1);
        printOutFinalSyntheticPopulation = PropertiesUtil.getBooleanProperty(bundle, "print.out.sp.final", false);

        PropertiesUtil.newPropertySubmodule("Main - runtime tracking");
        trackTime = PropertiesUtil.getBooleanProperty(bundle, "track.time", true);

        PropertiesUtil.newPropertySubmodule("Main - dwelling and income input data");
        incomeBrackets = PropertiesUtil.getIntPropertyArray(bundle,"income.brackets.hh.types", new int[]{20000,40000,60000}); //munich implementation
        qualityLevels = PropertiesUtil.getIntProperty(bundle, "dwelling.quality.levels.distinguished", 4);

        PropertiesUtil.newPropertySubmodule("Main microlocation");

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

        numberOfThreads = PropertiesUtil.getIntProperty(bundle, "number.of.threads", Runtime.getRuntime().availableProcessors());

        scaleFactor = PropertiesUtil.getDoubleProperty(bundle, "scale.factor", 1.);
    }
}
