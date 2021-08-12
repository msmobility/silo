package de.tum.bgu.msm.syntheticPopulationGenerator.properties;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import org.apache.commons.math.distribution.GammaDistributionImpl;

public abstract class AbstractPropertiesSynPop {
    public boolean runSyntheticPopulation;
    public String microDataFile;
    public boolean runIPU;
    public boolean runAllocation;
    public boolean runJobAllocation;
    public boolean twoGeographicalAreasIPU;
    public boolean runDisability;
    public String[] attributesMunicipality;
    public TableDataSet marginalsMunicipality;
    public TableDataSet jobsByTaz;
    public String[] attributesCounty;
    public TableDataSet marginalsCounty;
    public TableDataSet selectedMunicipalities;
    public TableDataSet cellsMatrix;
    public TableDataSet cellsMatrixBoroughs;
    public String zoneSystemFileName;
    public String omxFileName;
    public int[] ageBracketsPerson;
    public int[] ageBracketsPersonQuarter;
    public int[] householdSizes;
    public int[] yearBracketsDwelling;
    public int[] sizeBracketsDwelling;
    public int maxIterations;
    public double maxError;
    public double improvementError;
    public double iterationError;
    public double increaseError;
    public double initialError;
    public String weightsFileName;
    public String errorsMunicipalityFileName;
    public String errorsCountyFileName;
    public String[] jobStringType;
    public double alphaJob;
    public double gammaJob;
    public double alphaUniversity;
    public double gammaUniversity;
    public double[] incomeProbability;
    public String tripLengthDistributionFileName;
    public String householdsFileName;
    public String personsFileName;
    public String dwellingsFileName;
    public String jobsFileName;
    public int numberofQualityLevels;
    public int[] schoolTypes;
    public String errorsSummaryFileName;
    public String microPersonsFileName;
    public String microHouseholdsFileName;
    public String microDwellingsFileName;
    public String microJobsFileName;
    public int[] ageBracketsBorough;
    public String[] attributesBorough;
    public TableDataSet marginalsBorough;
    public boolean boroughIPU;
    public TableDataSet selectedBoroughs;
    public GammaDistributionImpl incomeGammaDistribution;
    public TableDataSet buildingLocationlist;
    public TableDataSet jobLocationlist;
    public TableDataSet schoolLocationlist;
    public boolean runMicrolocation;
    public TableDataSet zonalDataIPU;
    public String state;
    public String pathSyntheticPopulationFiles;
    public String householdsStateFileName;
    public String personsStateFileName;
    public String dwellingsStateFileName;
    public String jobsStateFileName;
    public String frequencyMatrixFileName;
    public String zoneFilename;
    public String zoneShapeFile;
    public String microDataHouseholds;
    public String microDataPersons;
    public TableDataSet incomeCoefficients;
    public double jobScaler;

    public int numberOfSubpopulations;
    public boolean populationSplitting;
    public String[] states;
    public boolean readMergeAndSplit;
    public boolean runBySubpopulation;
    public int vacantJobPercentage;
    public TableDataSet schoolsLocation;
    //public TableDataSet cellsMicrolocations;
    public int firstVacantJob;

}
