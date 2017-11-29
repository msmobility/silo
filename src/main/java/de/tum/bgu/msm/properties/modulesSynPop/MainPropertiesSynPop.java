package de.tum.bgu.msm.properties.modulesSynPop;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;

import java.util.ResourceBundle;

public class MainPropertiesSynPop {

    public final boolean runSyntheticPopulation;
    public final String microDataFile;
    public final int yearMicroData;
    public final boolean runIPU;
    public final boolean twoGeographicalAreasIPU;
    public final String[] attributesMunicipality;
    public final TableDataSet marginalsMunicipality;
    public final String[] attributesCounty;
    public final TableDataSet marginalsCounty;
    public final TableDataSet selectedMunicipalities;
    public final TableDataSet cellsMatrix;
    public final String omxFileName;
    public final int[] ageBracketsPerson;
    public final int[] ageBracketsPersonQuarter;
    public final TableDataSet jobsTable;
    public final TableDataSet educationDegreeTable;
    public final TableDataSet schoolLevelTable;
    public final int[] householdSizes;
    public final int[] yearBracketsDwelling;
    public final int[] sizeBracketsDwelling;
    public final int maxIterations;
    public final double maxError;
    public final double improvementError;
    public final double iterationError;
    public final double increaseError;
    public final double initialError;
    public final String weightsFileName;
    public final String errorsMunicipalityFileName;
    public final String errorsCountyFileName;
    public final String[] jobStringType;
    public final double alphaJob;
    public final double gammaJob;
    public final double alphaUniversity;
    public final double gammaUniversity;
    public final double incomeShape;
    public final double incomeRate;
    public final double[] incomeProbability;
    public final TableDataSet probabilitiesJob;
    public final String householdsFileName;
    public final String personsFileName;
    public final String dwellingsFileName;
    public final String jobsFileName;
    public final int numberofQualityLevels;
    public final int[] schoolTypes;
    public final String attributesPersonFileName;
    public final String attributesHouseholdFileName;
    public final boolean extractMicroData;


    public MainPropertiesSynPop(ResourceBundle bundle) {

        runSyntheticPopulation = ResourceUtil.getBooleanProperty(bundle, "run.synth.pop.generator", false);
        microDataFile = ResourceUtil.getProperty(bundle, "micro.data.2010");
        yearMicroData = ResourceUtil.getIntegerProperty(bundle, "year.micro.data");
        runIPU = ResourceUtil.getBooleanProperty(bundle, "run.ipu.synthetic.pop");
        twoGeographicalAreasIPU = ResourceUtil.getBooleanProperty(bundle, "run.ipu.city.and.county");
        extractMicroData = ResourceUtil.getBooleanProperty(bundle, "only.extract.microdata", false);

        attributesMunicipality = ResourceUtil.getArray(bundle, "attributes.household");
        marginalsMunicipality = SiloUtil.readCSVfile(bundle.getString("marginals.municipality"));
        marginalsMunicipality.buildIndex(marginalsMunicipality.getColumnPosition("ID_city"));

        attributesCounty = ResourceUtil.getArray(bundle, "attributes.region"); //attributes are decided on the properties file
        marginalsCounty = SiloUtil.readCSVfile(bundle.getString("marginals.county")); //all the marginals from the region
        marginalsCounty.buildIndex(marginalsCounty.getColumnPosition("ID_county"));

        attributesPersonFileName = ResourceUtil.getProperty(bundle, "read.attributes.pp","");
        attributesHouseholdFileName = ResourceUtil.getProperty(bundle, "read.attributes.hh","");

        selectedMunicipalities = SiloUtil.readCSVfile(bundle.getString("municipalities.list"));
        selectedMunicipalities.buildIndex(selectedMunicipalities.getColumnPosition("ID_city"));

        cellsMatrix = SiloUtil.readCSVfile(bundle.getString("raster.cells.definition"));
        cellsMatrix.buildIndex(cellsMatrix.getColumnPosition("ID_cell"));

        omxFileName = ResourceUtil.getProperty(bundle,"distanceODmatrix");

        ageBracketsPerson = ResourceUtil.getIntegerArray(bundle, "age.brackets");
        ageBracketsPersonQuarter = ResourceUtil.getIntegerArray(bundle, "age.brackets.quarter");

        jobStringType = ResourceUtil.getArray(bundle, "employment.types");
        alphaJob = ResourceUtil.getDoubleProperty(bundle, "employment.choice.alpha", 50);
        gammaJob = ResourceUtil.getDoubleProperty(bundle, "employment.choice.gamma", -0.003);
        jobsTable = SiloUtil.readCSVfile(bundle.getString("jobs.dictionary"));
        probabilitiesJob = SiloUtil.readCSVfile(bundle.getString("employment.probability"));
        probabilitiesJob.buildStringIndex(1);

        schoolTypes = ResourceUtil.getIntegerArray(bundle, "school.types");
        educationDegreeTable = SiloUtil.readCSVfile(bundle.getString("education.dictionary"));
        schoolLevelTable = SiloUtil.readCSVfile(bundle.getString("school.dictionary"));
        alphaUniversity = ResourceUtil.getDoubleProperty(bundle, "university.choice.alpha", 50);
        gammaUniversity = ResourceUtil.getDoubleProperty(bundle, "university.choice.gamma", -0.003);

        householdSizes = ResourceUtil.getIntegerArray(bundle, "household.size.brackets");
        numberofQualityLevels = ResourceUtil.getIntegerProperty(bundle, "dwelling.quality.levels.distinguished");
        yearBracketsDwelling = ResourceUtil.getIntegerArray(bundle, "dd.year.brackets");
        sizeBracketsDwelling = ResourceUtil.getIntegerArray(bundle, "dd.size.brackets");

        maxIterations = ResourceUtil.getIntegerProperty(bundle, "max.iterations.ipu",1000);
        maxError = ResourceUtil.getDoubleProperty(bundle, "max.error.ipu", 0.0001);
        improvementError = ResourceUtil.getDoubleProperty(bundle, "min.improvement.error.ipu", 0.001);
        iterationError = ResourceUtil.getDoubleProperty(bundle, "iterations.improvement.ipu",2);
        increaseError = ResourceUtil.getDoubleProperty(bundle, "increase.error.ipu",1.05);
        initialError = ResourceUtil.getDoubleProperty(bundle, "ini.error.ipu", 1000);

        incomeShape = ResourceUtil.getDoubleProperty(bundle, "income.gamma.shape", 1.0737036186);
        incomeRate = ResourceUtil.getDoubleProperty(bundle, "income.gamma.rate", 0.0006869439);
        incomeProbability = ResourceUtil.getDoubleArray(bundle, "income.probability");

        weightsFileName = ResourceUtil.getProperty(bundle,"weights.matrix");
        errorsMunicipalityFileName = ResourceUtil.getProperty(bundle, "errors.IPU.Municipality.matrix");
        errorsCountyFileName = ResourceUtil.getProperty(bundle, "errors.IPU.County.matrix");

        householdsFileName = ResourceUtil.getProperty(bundle,"household.file.ascii");
        personsFileName = ResourceUtil.getProperty(bundle,"person.file.ascii");
        dwellingsFileName = ResourceUtil.getProperty(bundle,"dwelling.file.ascii");
        jobsFileName = ResourceUtil.getProperty(bundle,"job.file.ascii");


    }

    public void additionalProperties(){



    }
}
