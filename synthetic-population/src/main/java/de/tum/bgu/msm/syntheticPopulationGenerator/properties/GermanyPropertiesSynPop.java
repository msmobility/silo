package de.tum.bgu.msm.syntheticPopulationGenerator.properties;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.properties.PropertiesUtil;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.commons.math.distribution.GammaDistributionImpl;

import java.util.ResourceBundle;

public class GermanyPropertiesSynPop extends AbstractPropertiesSynPop {

    public GermanyPropertiesSynPop(ResourceBundle bundle) {

        PropertiesUtil.newPropertySubmodule("SP: main properties");

        state = PropertiesUtil.getStringProperty(bundle, "state.synPop", "01");
        states = PropertiesUtil.getStringPropertyArray(bundle, "states.synPop.splitting", new String[]{"01","10"});
        numberOfSubpopulations = PropertiesUtil.getIntProperty(bundle, "micro.data.subpopulations", 1);
        populationSplitting = PropertiesUtil.getBooleanProperty(bundle, "micro.data.splitting", false);
        runBySubpopulation = PropertiesUtil.getBooleanProperty(bundle,"run.by.subpopulation",false);
        readMergeAndSplit = PropertiesUtil.getBooleanProperty(bundle, "micro.data.read.splitting", false);
        runSyntheticPopulation = PropertiesUtil.getBooleanProperty(bundle, "run.synth.pop.generator", false);
        runIPU = PropertiesUtil.getBooleanProperty(bundle, "run.ipu.synthetic.pop", false);
        runAllocation = PropertiesUtil.getBooleanProperty(bundle, "run.population.allocation", false);
        //runJobAllocation = PropertiesUtil.getBooleanProperty(bundle, "run.job.de.tum.bgu.msm.syntheticPopulationGenerator.germany.disability ", true); // was there initially. was true - than can not find Agri
        runJobAllocation = PropertiesUtil.getBooleanProperty(bundle, "run.job.allocation", true);
        twoGeographicalAreasIPU = PropertiesUtil.getBooleanProperty(bundle, "run.ipu.city.and.county", false);
        boroughIPU = PropertiesUtil.getBooleanProperty(bundle,"run.three.areas",false);
        runDisability = PropertiesUtil.getBooleanProperty(bundle, "run.disability", false);
        runMicrolocation = PropertiesUtil.getBooleanProperty(bundle, "run.sp.microlocation", false);

        microDataFile = PropertiesUtil.getStringProperty(bundle, "micro.data", "C:/Users/Wei/Documents/germanyModel/topSecretData/suf2010v1.dat");
        // todo this table is not a property but a data container, "ID_city" might be a property? (if this is applciable to other implementations)
        marginalsMunicipality = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle,"marginals.municipality","input/syntheticPopulation/" + state + "/marginalsMunicipality.csv"));
        marginalsMunicipality.buildIndex(marginalsMunicipality.getColumnPosition("ID_city"));

        jobsByTaz = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle,"jobs.by.taz","input/syntheticPopulation" + "/jobsByTaz.csv"));
        jobsByTaz.buildIndex(jobsByTaz.getColumnPosition("taz"));

        //todo same as municipalities
        marginalsCounty = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle,"marginals.county","input/syntheticPopulation/" + state + "/marginalsCounty.csv"));
        marginalsCounty.buildIndex(marginalsCounty.getColumnPosition("ID_county"));

        selectedMunicipalities = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle,"municipalities.list","input/syntheticPopulation/" + state + "/municipalitiesList.csv"));
        selectedMunicipalities.buildIndex(selectedMunicipalities.getColumnPosition("ID_city"));

        cellsMatrix = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle,"taz.definition","input/syntheticPopulation/" + state + "/zoneAttributeswithTAZ.csv"));
        cellsMatrix.buildIndex(cellsMatrix.getColumnPosition("ID_cell"));

        zoneSystemFileName = PropertiesUtil.getStringProperty(bundle,"taz.definition ","input/syntheticPopulation/" + state + "/zoneAttributeswithTAZ.csv");

        //todo this cannot be the final name of the matrix
        omxFileName = PropertiesUtil.getStringProperty(bundle, "distanceODmatrix", "input/syntheticPopulation/tdTest_distance.omx");

        attributesMunicipality = PropertiesUtil.getStringPropertyArray(bundle, "attributes.municipality", new String[]{"Agr", "Ind", "Srv"});
        attributesCounty = PropertiesUtil.getStringPropertyArray(bundle, "attributes.county", new String[]{"Agr", "Ind", "Srv"});

        ageBracketsPerson = PropertiesUtil.getIntPropertyArray(bundle, "age.brackets", new int[]{9, 14, 19, 24, 29, 34, 39, 44, 49, 54, 59, 64, 69, 74, 79, 84, 120});
        ageBracketsPersonQuarter = null;

        jobStringType = PropertiesUtil.getStringPropertyArray(bundle, "employment.types", new String[]{"Agr", "Ind", "Srv"});
        alphaJob = PropertiesUtil.getDoubleProperty(bundle, "employment.choice.alpha", 50);
        gammaJob = PropertiesUtil.getDoubleProperty(bundle, "employment.choice.gamma", -0.003);
        tripLengthDistributionFileName = PropertiesUtil.getStringProperty(bundle, "trip.length.distribution", "input/syntheticPopulation/tripLengthDistribution.csv");

        schoolTypes = PropertiesUtil.getIntPropertyArray(bundle, "school.types", new int[]{1, 2, 3});
        alphaUniversity = PropertiesUtil.getDoubleProperty(bundle, "university.choice.alpha", 50);
        gammaUniversity = PropertiesUtil.getDoubleProperty(bundle, "university.choice.gamma", -0.003);

        householdSizes = PropertiesUtil.getIntPropertyArray(bundle, "household.size.brackets", new int[]{1, 2, 3, 4, 5});

        maxIterations = PropertiesUtil.getIntProperty(bundle, "max.iterations.ipu", 1000);
        maxError = PropertiesUtil.getDoubleProperty(bundle, "max.error.ipu", 0.0001);
        improvementError = PropertiesUtil.getDoubleProperty(bundle, "min.improvement.error.ipu", 0.001);
        iterationError = PropertiesUtil.getDoubleProperty(bundle, "iterations.improvement.ipu", 2);
        increaseError = PropertiesUtil.getDoubleProperty(bundle, "increase.error.ipu", 1.05);
        initialError = PropertiesUtil.getDoubleProperty(bundle, "ini.error.ipu", 1000);

        double incomeShape = PropertiesUtil.getDoubleProperty(bundle, "income.gamma.shape", 1.0737036186);
        double incomeRate = PropertiesUtil.getDoubleProperty(bundle, "income.gamma.rate", 0.0006869439);
        //todo consider to read it from another source e.g. a JS calculator or CSV file
        //this is not a property but a variable?
        incomeGammaDistribution = new GammaDistributionImpl(incomeShape, 1 / incomeRate);

        //todo this properties will be doubled with silo model run properties
        weightsFileName = PropertiesUtil.getStringProperty(bundle, "weights.matrix", "microData/" + state +  "/interimFiles/weigthsMatrix.csv");
        errorsMunicipalityFileName = PropertiesUtil.getStringProperty(bundle, "errors.IPU.municipality.matrix", "microData/" + state +  "/interimFiles/errorsIPUmunicipality.csv");
        errorsCountyFileName = PropertiesUtil.getStringProperty(bundle, "errors.IPU.county.matrix", "microData/" + state + "/interimFiles/errorsIPUcounty.csv");
        errorsSummaryFileName = PropertiesUtil.getStringProperty(bundle, "errors.IPU.summary.matrix", "microData/" + state + "/interimFiles/errorsIPUsummary.csv");
        frequencyMatrixFileName = PropertiesUtil.getStringProperty(bundle,"frequency.matrix.file","microData/" + state +  "/interimFiles/quencyMatrix.csv");

        buildingLocationlist = null;
        jobLocationlist = null;
        schoolLocationlist = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle,"school.location", "input/syntheticPopulation/08_schoolLocation.csv"));

        firstVacantJob = PropertiesUtil.getIntProperty(bundle,"first.vacant.job.id", 45000000);
        /*cellsMicrolocations = SiloUtil.readCSVfile2("C:/models/silo/germany/input/syntheticPopulation/all_raster_100_updated.csv");
        cellsMicrolocations.buildStringIndex(cellsMicrolocations.getColumnPosition("id"));*/

        microPersonsFileName = PropertiesUtil.getStringProperty(bundle, "micro.persons", "microData/" + state +  "/interimFiles/microPersons.csv");
        microHouseholdsFileName = PropertiesUtil.getStringProperty(bundle, "micro.households", "microData/" + state +  "/interimFiles/microHouseholds.csv");
        zonalDataIPU = null;

        householdsFileName = PropertiesUtil.getStringProperty(bundle, "household.file.ascii", "microData/hh");
        personsFileName = PropertiesUtil.getStringProperty(bundle, "person.file.ascii", "microData/pp");
        dwellingsFileName = PropertiesUtil.getStringProperty(bundle, "dwelling.file.ascii", "microData/dd");
        jobsFileName = PropertiesUtil.getStringProperty(bundle, "job.file.ascii", "microData/jj");

        pathSyntheticPopulationFiles = PropertiesUtil.getStringProperty(bundle, "path.synthetic.ascii", "microData/");
        householdsStateFileName = PropertiesUtil.getStringProperty(bundle, "household.file.ascii.sp", "microData/" + state +  "/hh");
        personsStateFileName = PropertiesUtil.getStringProperty(bundle, "person.file.ascii.sp", "microData/" + state +  "/pp");
        dwellingsStateFileName = PropertiesUtil.getStringProperty(bundle, "dwelling.file.ascii.sp", "microData/" + state +  "/dd");
        jobsStateFileName = PropertiesUtil.getStringProperty(bundle, "job.file.ascii.sp", "microData/" + state +  "/jj");
        counters = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle,"counters.synthetic.population","microData/subPopulations/countersByState.csv"));
        counters.buildStringIndex(counters.getColumnPosition("state"));
        vacantJobPercentage = PropertiesUtil.getIntProperty(bundle,"jobs.vacant.percentage", 25);

        if (boroughIPU) {
            attributesBorough = PropertiesUtil.getStringPropertyArray(bundle, "attributes.borough", new String[]{"Agr", "Ind", "Srv"});
            marginalsBorough = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "marginals.borough", "input/syntheticPopulation/" + state + "/marginalsBorough.csv"));
            marginalsBorough.buildIndex(marginalsBorough.getColumnPosition("ID_borough"));
            selectedBoroughs = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle,"municipalities.list.borough","input/syntheticPopulation/" + state + "/municipalitiesListBorough.csv"));
            selectedBoroughs.buildIndex(selectedBoroughs.getColumnPosition("ID_borough"));
            cellsMatrixBoroughs = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle,"taz.definition","input/syntheticPopulation/" + state + "/zoneAttributesBoroughwithTAZ.csv"));
            cellsMatrixBoroughs.buildIndex(cellsMatrixBoroughs.getColumnPosition("ID_cell"));
        }
    }

}
