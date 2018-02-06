package de.tum.bgu.msm.properties.modulesSynPop;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import org.apache.commons.math.distribution.GammaDistributionImpl;

import java.util.ResourceBundle;

public class MainPropertiesSynPop {

    public final boolean runSyntheticPopulation;
    public final String microDataFile;
    public final boolean runIPU;
    public final boolean runAllocation;
    public final boolean runJobAllocation;
    public final boolean twoGeographicalAreasIPU;
    public final String[] attributesMunicipality;
    public final TableDataSet marginalsMunicipality;
    public final String[] attributesCounty;
    public final TableDataSet marginalsCounty;
    public final TableDataSet selectedMunicipalities;
    public final TableDataSet cellsMatrix;
    public final TableDataSet cellsMatrixBoroughs;
    public final String omxFileName;
    public final int[] ageBracketsPerson;
    public final int[] ageBracketsPersonQuarter;
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
    public final double[] incomeProbability;
    public final String tripLengthDistributionFileName;
    public final String householdsFileName;
    public final String personsFileName;
    public final String dwellingsFileName;
    public final String jobsFileName;
    public final int numberofQualityLevels;
    public final int[] schoolTypes;
    public final String errorsSummaryFileName;
    public final String microPersonsFileName;
    public final String microHouseholdsFileName;
    public final String microDwellingsFileName;
    public final int[] ageBracketsBorough;
    public final String[] attributesBorough;
    public final TableDataSet marginalsBorough;
    public final boolean boroughIPU;
    public final TableDataSet selectedBoroughs;
    public final GammaDistributionImpl incomeGammaDistribution;

    public MainPropertiesSynPop(ResourceBundle bundle) {

        runSyntheticPopulation = ResourceUtil.getBooleanProperty(bundle, "run.synth.pop.generator", false);
        microDataFile = ResourceUtil.getProperty(bundle, "micro.data");
        runIPU = ResourceUtil.getBooleanProperty(bundle, "run.ipu.synthetic.pop");
        runAllocation = ResourceUtil.getBooleanProperty(bundle, "run.population.allocation", false);
        runJobAllocation = ResourceUtil.getBooleanProperty(bundle, "run.job.allocation", false);
        twoGeographicalAreasIPU = ResourceUtil.getBooleanProperty(bundle, "run.ipu.city.and.county");

        attributesMunicipality = ResourceUtil.getArray(bundle, "attributes.municipality");
        marginalsMunicipality = SiloUtil.readCSVfile(bundle.getString("marginals.municipality"));
        marginalsMunicipality.buildIndex(marginalsMunicipality.getColumnPosition("ID_city"));

        attributesCounty = ResourceUtil.getArray(bundle, "attributes.county"); //attributes are decided on the properties file
        marginalsCounty = SiloUtil.readCSVfile(bundle.getString("marginals.county")); //all the marginals from the region
        marginalsCounty.buildIndex(marginalsCounty.getColumnPosition("ID_county"));

        selectedMunicipalities = SiloUtil.readCSVfile(bundle.getString("municipalities.list"));
        selectedMunicipalities.buildIndex(selectedMunicipalities.getColumnPosition("ID_city"));

        cellsMatrix = SiloUtil.readCSVfile(bundle.getString("taz.definition"));
        cellsMatrix.buildIndex(cellsMatrix.getColumnPosition("ID_borough"));

        omxFileName = ResourceUtil.getProperty(bundle,"distanceODmatrix");

        ageBracketsPerson = ResourceUtil.getIntegerArray(bundle, "age.brackets");
        ageBracketsPersonQuarter = ResourceUtil.getIntegerArray(bundle, "age.brackets.quarter");

        jobStringType = ResourceUtil.getArray(bundle, "employment.types");
        alphaJob = ResourceUtil.getDoubleProperty(bundle, "employment.choice.alpha", 50);
        gammaJob = ResourceUtil.getDoubleProperty(bundle, "employment.choice.gamma", -0.003);
        tripLengthDistributionFileName = ResourceUtil.getProperty(bundle, "trip.length.distribution");

        schoolTypes = ResourceUtil.getIntegerArray(bundle, "school.types");
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

        double incomeShape = ResourceUtil.getDoubleProperty(bundle, "income.gamma.shape", 1.0737036186);
        double incomeRate = ResourceUtil.getDoubleProperty(bundle, "income.gamma.rate", 0.0006869439);
        incomeProbability = ResourceUtil.getDoubleArray(bundle, "income.probability");
        incomeGammaDistribution = new GammaDistributionImpl(incomeShape,1 / incomeRate);

        weightsFileName = ResourceUtil.getProperty(bundle,"weights.matrix");
        errorsMunicipalityFileName = ResourceUtil.getProperty(bundle, "errors.IPU.municipality.matrix");
        errorsCountyFileName = ResourceUtil.getProperty(bundle, "errors.IPU.county.matrix");
        errorsSummaryFileName = ResourceUtil.getProperty(bundle, "errors.IPU.summary.matrix");

        householdsFileName = ResourceUtil.getProperty(bundle,"household.file.ascii");
        personsFileName = ResourceUtil.getProperty(bundle,"person.file.ascii");
        dwellingsFileName = ResourceUtil.getProperty(bundle,"dwelling.file.ascii");
        jobsFileName = ResourceUtil.getProperty(bundle,"job.file.ascii");

        microPersonsFileName = ResourceUtil.getProperty(bundle,"micro.persons");
        microHouseholdsFileName = ResourceUtil.getProperty(bundle,"micro.households");
        microDwellingsFileName = ResourceUtil.getProperty(bundle,"micro.dwellings");

        boroughIPU = ResourceUtil.getBooleanProperty(bundle,"run.three.areas", false);
        if (boroughIPU){
            selectedBoroughs = SiloUtil.readCSVfile(bundle.getString("municipalities.list.borough"));
            attributesBorough = ResourceUtil.getArray(bundle, "attributes.borough");
            marginalsBorough = SiloUtil.readCSVfile(bundle.getString("marginals.borough"));
            marginalsBorough.buildIndex(marginalsBorough.getColumnPosition("ID_borough"));
            ageBracketsBorough = ResourceUtil.getIntegerArray(bundle, "age.brackets.borough");
            cellsMatrixBoroughs = SiloUtil.readCSVfile(bundle.getString("taz.definition.borough"));
            cellsMatrixBoroughs.buildIndex(cellsMatrixBoroughs.getColumnPosition("ID_borough"));
        } else {
            selectedBoroughs = null;
            attributesBorough = null;
            marginalsBorough = null;
            ageBracketsBorough = null;
            cellsMatrixBoroughs = null;
        }

    }

    public void additionalProperties(){



    }
}
