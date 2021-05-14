package de.tum.bgu.msm.syntheticPopulationGenerator.properties;

import de.tum.bgu.msm.common.util.ResourceUtil;
import de.tum.bgu.msm.properties.PropertiesUtil;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.commons.math.distribution.GammaDistributionImpl;

import java.util.ResourceBundle;


public class KagawaPropertiesSynPop extends AbstractPropertiesSynPop{

        public KagawaPropertiesSynPop (ResourceBundle bundle) {

        PropertiesUtil.newPropertySubmodule("SP: main properties");

        runSyntheticPopulation = PropertiesUtil.getBooleanProperty(bundle, "run.synth.pop.generator", false);
        microDataFile = PropertiesUtil.getStringProperty(bundle, "micro.data", "PATH_TO_MICRO_DATA");
        runIPU = PropertiesUtil.getBooleanProperty(bundle, "run.ipu.synthetic.pop", false);
        runAllocation = PropertiesUtil.getBooleanProperty(bundle, "run.population.allocation", false);
        runJobAllocation = PropertiesUtil.getBooleanProperty(bundle, "run.job.allocation", false);
        twoGeographicalAreasIPU = PropertiesUtil.getBooleanProperty(bundle, "run.ipu.city.and.county", false);
        runMicrolocation = PropertiesUtil.getBooleanProperty(bundle, "run.sp.microlocation", false);
        zonalDataIPU = SiloUtil.readCSVfile2(PropertiesUtil.getStringProperty(bundle,"zonal.data.ipu","input/testing/ZoneCode_KGW.csv"));
        zonalDataIPU.buildIndex(zonalDataIPU.getColumnPosition("V1"));

        //todo I would read these attributes from a file, probable, the same as read in the next property
        attributesMunicipality = PropertiesUtil.getStringPropertyArray(bundle, "attributes.municipality", new String[]{"H_Own","H_Rent","ddT_Detached","ddT_Apart","ddT_Multi",
                "M_9","M_14","M_19","M_24","M_29","M_34","M_39","M_44","M_49","M_54","M_59","M_64","M_69","M_74","M_79","M_84","M_120",
                "F_9","F_14","F_19","F_24","F_29","F_34","F_39","F_44","F_49","F_54","F_59","F_64","F_69","F_74","F_79","F_84","F_120",
                "nHH_5","nHH_4","nHH_3","nHH_2","nHH_1","Population","hhTotal"});
        marginalsMunicipality = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "marginals.municipality", "input/testing/ZoneData01.csv"));

        // todo this table is not a property but a data container, "ID_city" might be a property? (if this is applciable to other implementations)

        marginalsMunicipality.buildIndex(marginalsMunicipality.getColumnPosition("CODE_Z01"));


        //todo same as municipalities
        attributesCounty = null; //attributes are decided on the properties file
        marginalsCounty = null; //all the marginals from the region

        selectedMunicipalities = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "municipalities.list", "input/testing/ZoneCode_KGW.csv"));
        selectedMunicipalities.buildIndex(selectedMunicipalities.getColumnPosition("V1"));

        cellsMatrix = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "raster.cells.definition", "input/testing/zoneAttributes.csv"));
        cellsMatrix.buildIndex(cellsMatrix.getColumnPosition("ID_cell"));

        //todo this cannot be the final name of the matrix
        omxFileName = PropertiesUtil.getStringProperty(bundle, "distanceODmatrix", "input/syntheticPopulation/tdTest.omx");

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
        numberofQualityLevels = PropertiesUtil.getIntProperty(bundle, "dwelling.quality.levels.distinguished", 4);
        yearBracketsDwelling = PropertiesUtil.getIntPropertyArray(bundle, "dd.year.brackets", new int[]{1, 2, 3, 4, 5});
        sizeBracketsDwelling = PropertiesUtil.getIntPropertyArray(bundle, "dd.size.brackets", new int[]{60, 80, 100, 120, 2000});

        maxIterations = PropertiesUtil.getIntProperty(bundle, "max.iterations.ipu", 1000);
        maxError = PropertiesUtil.getDoubleProperty(bundle, "max.error.ipu", 0.0001);
        improvementError = PropertiesUtil.getDoubleProperty(bundle, "min.improvement.error.ipu", 0.001);
        iterationError = PropertiesUtil.getDoubleProperty(bundle, "iterations.improvement.ipu", 2);
        increaseError = PropertiesUtil.getDoubleProperty(bundle, "increase.error.ipu", 1.05);
        initialError = PropertiesUtil.getDoubleProperty(bundle, "ini.error.ipu", 1000);

        double incomeShape = PropertiesUtil.getDoubleProperty(bundle, "income.gamma.shape", 1.0737036186);
        double incomeRate = PropertiesUtil.getDoubleProperty(bundle, "income.gamma.rate", 0.0006869439);
        //todo consider to read it from another source e.g. a JS calculator or CSV file
        incomeProbability = PropertiesUtil.getDoublePropertyArray(bundle, "income.probability", new double[]{0.07998391,
                0.15981282,0.25837521,0.34694010,0.42580696,0.49569720,0.55744375,0.61188119,0.65980123,0.72104215,0.77143538,
                0.81284178,0.84682585,0.87469331,0.90418202,0.92677087,0.94770566,0.96267752,0.97337602,0.98101572,0.99313092,
                0.99874378,0.99999464});
        //this is not a property but a variable?
        incomeGammaDistribution = new GammaDistributionImpl(incomeShape, 1 / incomeRate);

        weightsFileName = PropertiesUtil.getStringProperty(bundle, "weights.matrix", "microData/interimFiles/weigthsMatrix.csv");
        errorsMunicipalityFileName = PropertiesUtil.getStringProperty(bundle, "errors.IPU.municipality.matrix", "microData/interimFiles/errorsIPUmunicipality.csv");
        errorsCountyFileName = PropertiesUtil.getStringProperty(bundle, "errors.IPU.county.matrix", "microData/interimFiles/errorsIPUcounty.csv");
        errorsSummaryFileName = PropertiesUtil.getStringProperty(bundle, "errors.IPU.summary.matrix", "microData/interimFiles/errorsIPUsummary.csv");

        //todo this properties will be doubled with silo model run properties
        householdsFileName = PropertiesUtil.getStringProperty(bundle, "household.file.ascii", "microData/hh");
        personsFileName = PropertiesUtil.getStringProperty(bundle, "person.file.ascii", "microData/pp");
        dwellingsFileName = PropertiesUtil.getStringProperty(bundle, "dwelling.file.ascii", "microData/dd");
        jobsFileName = PropertiesUtil.getStringProperty(bundle, "job.file.ascii", "microData/jj");

        microPersonsFileName = PropertiesUtil.getStringProperty(bundle, "micro.persons", "microData/interimFiles/microPersons.csv");
        microHouseholdsFileName = PropertiesUtil.getStringProperty(bundle, "micro.households", "microData/interimFiles/microHouseholds.csv");
        microDwellingsFileName = PropertiesUtil.getStringProperty(bundle, "micro.dwellings", "microData/interimFiles/microDwellings.csv");

        boroughIPU = ResourceUtil.getBooleanProperty(bundle, "run.three.areas", false);
        selectedBoroughs = null;
        attributesBorough = null;
        marginalsBorough = null;
        ageBracketsBorough = null;
        cellsMatrixBoroughs = null;

        buildingLocationlist = null;
        jobLocationlist = null;
        schoolLocationlist = null;
    }
}
