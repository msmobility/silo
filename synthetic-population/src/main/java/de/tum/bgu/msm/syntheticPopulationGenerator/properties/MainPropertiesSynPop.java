package de.tum.bgu.msm.syntheticPopulationGenerator.properties;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.properties.PropertiesUtil;
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

    public final TableDataSet buildingLocationlist;
    public final TableDataSet jobLocationlist;
    public final TableDataSet schoolLocationlist;

    public MainPropertiesSynPop(ResourceBundle bundle) {

        runSyntheticPopulation = PropertiesUtil.getBooleanProperty(bundle, "run.synth.pop.generator", false);
        microDataFile = PropertiesUtil.getStringProperty(bundle, "micro.data", "PATH_TO_MICRO_DATA");
        runIPU = PropertiesUtil.getBooleanProperty(bundle, "run.ipu.synthetic.pop", false);
        runAllocation = PropertiesUtil.getBooleanProperty(bundle, "run.population.allocation", false);
        runJobAllocation = PropertiesUtil.getBooleanProperty(bundle, "run.job.allocation", false);
        twoGeographicalAreasIPU = PropertiesUtil.getBooleanProperty(bundle, "run.ipu.city.and.county", true);

        //todo I would read these attributes from a file, probable, the same as read in the next property
        attributesMunicipality = PropertiesUtil.getStringPropertyArray(bundle, "attributes.municipality", new String[]{"smallDwellings","mediumDwellings",
                "ddOwned","ddRented","foreigners","male4","female4","male9","female9","male14","female14","male19","female19","male24","female24","male29",
                "female29","male34","female34","male39","female39","male44","female44","male49","female49","male54","female54","male59","female59","male64",
                "female64","male69","female69","male74","female74","male79","female79","male99","female99","hhSize5","hhSize4","hhSize3","hhSize2","hhSize1",
                "maleWorkers","femaleWorkers","population","hhTotal"});
        marginalsMunicipality = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "marginals.municipality", "input/syntheticPopulation/marginalsMunicipality.csv" ));

        // todo this table is not a property but a data container, "ID_city" might be a property? (if this is applciable to other implementations)
        marginalsMunicipality.buildIndex(marginalsMunicipality.getColumnPosition("ID_city"));

        //todo same as municipalities
        attributesCounty = PropertiesUtil.getStringPropertyArray(bundle, "attributes.county", new String[]{"ddFloor60","ddFloor80",
                        "ddFloor100","ddFloor120","ddFloor2000","smallDwellings2","smallDwellings5","smallDwellings6",
                        "smallDwellings9","mediumDwellings2","mediumDwellings5","mediumDwellings6","mediumDwellings9"}); //attributes are decided on the properties file
        marginalsCounty = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "marginals.county", "input/syntheticPopulation/marginalsCounty.csv")); //all the marginals from the region
        marginalsCounty.buildIndex(marginalsCounty.getColumnPosition("ID_county"));

        selectedMunicipalities = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "municipalities.list", "input/syntheticPopulation/municipalitiesList.csv"));
        selectedMunicipalities.buildIndex(selectedMunicipalities.getColumnPosition("ID_city"));

        cellsMatrix = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "taz.definition", "input/syntheticPopulation/zoneAttributes.csv"));
        cellsMatrix.buildIndex(cellsMatrix.getColumnPosition("ID_cell"));

        //todo this cannot be the final name of the matrix
        omxFileName = PropertiesUtil.getStringProperty(bundle,"distanceODmatrix", "input/syntheticPopulation/tdTest.omx");

        ageBracketsPerson = PropertiesUtil.getIntPropertyArray(bundle, "age.brackets", new int[]{4,9,14,19,24,29,34,39,44,49,54,59,64,69,74,79,99});
        ageBracketsPersonQuarter = PropertiesUtil.getIntPropertyArray(bundle, "age.brackets.quarter", new int[]{64,120});

        jobStringType = PropertiesUtil.getStringPropertyArray(bundle, "employment.types", new String[]{"Agri","Mnft","Util","Cons","Retl","Trns","Finc",
                "Rlst","Admn","Serv"});
        alphaJob = PropertiesUtil.getDoubleProperty(bundle, "employment.choice.alpha", 50);
        gammaJob = PropertiesUtil.getDoubleProperty(bundle, "employment.choice.gamma", -0.003);
        tripLengthDistributionFileName = PropertiesUtil.getStringProperty(bundle, "trip.length.distribution", "input/syntheticPopulation/tripLengthDistribution.csv");

        schoolTypes = PropertiesUtil.getIntPropertyArray(bundle, "school.types", new int[]{1,2,3});
        alphaUniversity = PropertiesUtil.getDoubleProperty(bundle, "university.choice.alpha", 50);
        gammaUniversity = PropertiesUtil.getDoubleProperty(bundle, "university.choice.gamma", -0.003);

        householdSizes = PropertiesUtil.getIntPropertyArray(bundle, "household.size.brackets", new int[]{1,2,3,4,5});
        numberofQualityLevels = PropertiesUtil.getIntProperty(bundle, "dwelling.quality.levels.distinguished", 4);
        yearBracketsDwelling = PropertiesUtil.getIntPropertyArray(bundle, "dd.year.brackets", new int[]{2,5,6,9});
        sizeBracketsDwelling = PropertiesUtil.getIntPropertyArray(bundle, "dd.size.brackets", new int[]{60,80,100,120,2000});

        maxIterations = PropertiesUtil.getIntProperty(bundle, "max.iterations.ipu",1000);
        maxError = PropertiesUtil.getDoubleProperty(bundle, "max.error.ipu", 0.0001);
        improvementError = PropertiesUtil.getDoubleProperty(bundle, "min.improvement.error.ipu", 0.001);
        iterationError = PropertiesUtil.getDoubleProperty(bundle, "iterations.improvement.ipu",2);
        increaseError = PropertiesUtil.getDoubleProperty(bundle, "increase.error.ipu",1.05);
        initialError = PropertiesUtil.getDoubleProperty(bundle, "ini.error.ipu", 1000);

        double incomeShape = PropertiesUtil.getDoubleProperty(bundle, "income.gamma.shape", 1.0737036186);
        double incomeRate = PropertiesUtil.getDoubleProperty(bundle, "income.gamma.rate", 0.0006869439);
        //todo consider to read it from another source e.g. a JS calculator or CSV file
        incomeProbability = PropertiesUtil.getDoublePropertyArray(bundle, "income.probability", new double[]{0.07998391,0.15981282,
                0.25837521,0.34694010,0.42580696,0.49569720,0.55744375,0.61188119,0.65980123,
                0.72104215,0.77143538,0.81284178,0.84682585,0.87469331,0.90418202,0.92677087,
                0.94770566,0.96267752,0.97337602,0.98101572,0.99313092,0.99874378,0.99999464});
        //this is not a property but a variable?
        incomeGammaDistribution = new GammaDistributionImpl(incomeShape,1 / incomeRate);

        weightsFileName = PropertiesUtil.getStringProperty(bundle,"weights.matrix", "microData/interimFiles/weigthsMatrix.csv");
        errorsMunicipalityFileName = PropertiesUtil.getStringProperty(bundle, "errors.IPU.municipality.matrix", "microData/interimFiles/errorsIPUmunicipality.csv");
        errorsCountyFileName = PropertiesUtil.getStringProperty(bundle, "errors.IPU.county.matrix", "microData/interimFiles/errorsIPUcounty.csv");
        errorsSummaryFileName = PropertiesUtil.getStringProperty(bundle, "errors.IPU.summary.matrix", "microData/interimFiles/errorsIPUsummary.csv");

        //todo this properties will be doubled with silo model run properties
        householdsFileName = PropertiesUtil.getStringProperty(bundle,"household.file.ascii",  "microData/hh");
        personsFileName = PropertiesUtil.getStringProperty(bundle,"person.file.ascii",  "microData/pp");
        dwellingsFileName = PropertiesUtil.getStringProperty(bundle,"dwelling.file.ascii", "microData/dd");
        jobsFileName = PropertiesUtil.getStringProperty(bundle,"job.file.ascii", "microData/jj");

        microPersonsFileName = PropertiesUtil.getStringProperty(bundle,"micro.persons", "microData/interimFiles/microPersons.csv");
        microHouseholdsFileName = PropertiesUtil.getStringProperty(bundle,"micro.households", "microData/interimFiles/microHouseholds.csv");
        microDwellingsFileName = PropertiesUtil.getStringProperty(bundle,"micro.dwellings", "microData/interimFiles/microDwellings.csv");

        boroughIPU = ResourceUtil.getBooleanProperty(bundle,"run.three.areas", false);
        if (boroughIPU){
            selectedBoroughs = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "municipalities.list.borough", "input/syntheticPopulation/municipalitiesListBorough.csv"));
            attributesBorough = PropertiesUtil.getStringPropertyArray(bundle, "attributes.borough", new String[]{"MUCforeigners",
                    "MUCage5", "MUCage17", "MUCage64", "MUCage99", "MUChhWithChildren", "MUChhSize1", "MUCfemaleWorkers", "MUCmaleWorkers",
                    "MUCfemale", "MUCpopulation", "MUChhTotal"});
            marginalsBorough = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "marginals.borough", "input/syntheticPopulation/marginalsBorough.csv"));
            marginalsBorough.buildIndex(marginalsBorough.getColumnPosition("ID_borough"));
            ageBracketsBorough = PropertiesUtil.getIntPropertyArray(bundle, "age.brackets.borough", new int[]{5,17,64,99});
            cellsMatrixBoroughs = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "taz.definition.borough", "input/syntheticPopulation/zoneAttributesBorough.csv"));
            cellsMatrixBoroughs.buildIndex(cellsMatrixBoroughs.getColumnPosition("ID_borough"));
        } else {
            selectedBoroughs = null;
            attributesBorough = null;
            marginalsBorough = null;
            ageBracketsBorough = null;
            cellsMatrixBoroughs = null;
        }

        //todo do not need to ride always?
        buildingLocationlist = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "buildingLocation.list", "input/syntheticPopulation/buildingLocation.csv"));
        jobLocationlist = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "jobLocation.list", "input/syntheticPopulation/jobLocation.csv"));
        schoolLocationlist = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "schoolLocation.list", "input/syntheticPopulation/schoolLocation.csv"));

    }

    public void additionalProperties(){



    }
}
