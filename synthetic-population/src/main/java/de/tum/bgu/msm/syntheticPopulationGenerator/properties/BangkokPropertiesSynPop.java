package de.tum.bgu.msm.syntheticPopulationGenerator.properties;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import de.tum.bgu.msm.properties.PropertiesUtil;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.commons.math.distribution.GammaDistributionImpl;

import java.util.ResourceBundle;

public class BangkokPropertiesSynPop extends AbstractPropertiesSynPop {

    public BangkokPropertiesSynPop(ResourceBundle bundle) {

        PropertiesUtil.newPropertySubmodule("SP: main properties");

        runIPU = PropertiesUtil.getBooleanProperty(bundle, "run.ipu.synthetic.pop", false);
        runMicrolocation = PropertiesUtil.getBooleanProperty(bundle, "run.sp.microlocation", false);
        runAllocation = PropertiesUtil.getBooleanProperty(bundle, "run.population.allocation", false);
        runJobAllocation = PropertiesUtil.getBooleanProperty(bundle, "run.job.allocation", false);
        twoGeographicalAreasIPU = PropertiesUtil.getBooleanProperty(bundle, "run.ipu.city.and.county", false);

        //todo I would read these attributes from a file, probable, the same as read in the next property


        attributesMunicipality = PropertiesUtil.getStringPropertyArray(bundle, "attributes.municipality");

        // todo this table is not a property but a data container, "ID_city" might be a property? (if this is applciable to other implementations)
        marginalsMunicipality = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle,"marginals.municipality","input/syntheticPopulation/marginalsMunicipality.csv"));
        marginalsMunicipality.buildIndex(marginalsMunicipality.getColumnPosition("ID_city"));


        //todo same as municipalities
        marginalsCounty = null;

        selectedMunicipalities = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle,"municipalities.list","input/syntheticPopulation/municipalitiesList.csv"));
        selectedMunicipalities.buildIndex(selectedMunicipalities.getColumnPosition("ID_city"));

        cellsMatrix = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle,"taz.definition ","input/syntheticPopulation/zoneAttributes.csv"));
        cellsMatrix.buildIndex(cellsMatrix.getColumnPosition("ID_cell"));

        //todo this cannot be the final name of the matrix
        omxFileName = PropertiesUtil.getStringProperty(bundle, "distanceODmatrix", "input/syntheticPopulation/distanceMatrixQGIS.csv");

        ageBracketsPerson = PropertiesUtil.getIntPropertyArray(bundle, "age.brackets", new int[]{9, 14, 19, 24, 29, 34, 39, 44, 49, 54, 59, 64, 69, 74, 79, 84, 120});
        ageBracketsPersonQuarter = null;

        jobStringType = PropertiesUtil.getStringPropertyArray(bundle, "employment.types", new String[]{"pri", "sec", "ter"});
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
        //this is not a property but a variable?
        incomeGammaDistribution = new GammaDistributionImpl(incomeShape, 1 / incomeRate);

        //todo this properties will be doubled with silo model run properties
        weightsFileName = PropertiesUtil.getStringProperty(bundle, "weights.matrix", "microData/interimFiles/weigthsMatrix.csv");
        errorsMunicipalityFileName = PropertiesUtil.getStringProperty(bundle, "errors.IPU.municipality.matrix", "microData/interimFiles/errorsIPUmunicipality.csv");
        errorsCountyFileName = PropertiesUtil.getStringProperty(bundle, "errors.IPU.county.matrix", "microData/interimFiles/errorsIPUcounty.csv");
        errorsSummaryFileName = PropertiesUtil.getStringProperty(bundle, "errors.IPU.summary.matrix", "microData/interimFiles/errorsIPUsummary.csv");
        //todo do not need to ride always?
        if (runMicrolocation) {
            buildingLocationlist = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "buildingLocation.list", "input/syntheticPopulation/buildingLocation.csv"));
            jobLocationlist = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "jobLocation.list", "input/syntheticPopulation/jobLocation.csv"));
            schoolLocationlist = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "schoolLocation.list", "input/syntheticPopulation/schoolLocation.csv"));
        } else {
            buildingLocationlist = null;
            jobLocationlist = null;
            schoolLocationlist = null;
        }
        zonalDataIPU = null;
        runDisability = PropertiesUtil.getBooleanProperty(bundle, "run.disability", false);

        zoneShapeFile = PropertiesUtil.getStringProperty(bundle, "zones.SP.shape", "input/zonesShapefile/TAZ_wgs5.shp");
        zoneFilename = PropertiesUtil.getStringProperty(bundle, "zones.SP.file", "input/zoneSystem.csv");

        microDataHouseholds = PropertiesUtil.getStringProperty(bundle, "micro.data.households", "input/syntheticPopulation/hhThaiSilo.csv");
        microDataPersons = PropertiesUtil.getStringProperty(bundle, "micro.data.persons", "input/syntheticPopulation/ppThaiSilo.csv");

        microPersonsFileName = PropertiesUtil.getStringProperty(bundle, "micro.persons", "microData/interimFiles/microPersons.csv");
        microHouseholdsFileName = PropertiesUtil.getStringProperty(bundle, "micro.households", "microData/interimFiles/microHouseholds.csv");

        incomeCoefficients = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "income.coefficients","input/syntheticPopulation/incomeCoefficients.csv"));
        jobScaler = PropertiesUtil.getDoubleProperty(bundle,"job.scaler",1.0);
    }

}
