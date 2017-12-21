package de.tum.bgu.msm.syntheticPopulationGenerator.munich;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.CreateCarOwnershipModel;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation.Allocation;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.optimization.Optimization;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.Preparation;
import omx.OmxFile;
import omx.OmxLookup;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.commons.math.stat.Frequency;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;


/**
 * Generates a simple synthetic population for a study area in Germany
 * @author Ana Moreno (TUM)
 * Created on May 12, 2016 in Munich
 *
 */
public class SyntheticPopDe implements SyntheticPopI {

    protected TableDataSet microDataHousehold;
    protected TableDataSet microDataPerson;
    protected TableDataSet microDataDwelling;
    protected TableDataSet frequencyMatrix;

    protected int[] cityID;
    protected int[] countyID;
    protected HashMap<Integer, ArrayList> municipalitiesByCounty;
    protected HashMap<Integer, int[]> cityTAZ;

    protected TableDataSet counterMunicipality;
    protected TableDataSet errorMunicipality;

    protected TableDataSet weightsTable;


    HashMap<String, Integer> jobIntTypes;

    protected HashMap<Integer, int[]> idVacantJobsByZoneType;
    protected HashMap<Integer, Integer> numberVacantJobsByType;
    protected HashMap<Integer, int[]> idZonesVacantJobsByType;
    protected HashMap<Integer, Integer> numberVacantJobsByZoneByType;
    protected HashMap<Integer, Integer> numberZonesByType;

    protected HashMap<Integer, Integer> numberVacantSchoolsByZoneByType;
    protected HashMap<Integer, int[]> idZonesVacantSchoolsByType;
    protected HashMap<Integer, Integer> numberZonesWithVacantSchoolsByType;
    protected HashMap<Integer, Integer> schoolCapacityByType;

    protected Matrix distanceMatrix;
    protected Matrix distanceImpedance;
    protected TableDataSet odMunicipalityFlow;
    protected TableDataSet odCountyFlow;

    public static final Logger logger = Logger.getLogger(SyntheticPopDe.class);
    private final DataSetSynPop dataSetSynPop;

    private ResourceBundle rb;

    public SyntheticPopDe(DataSetSynPop dataSetSynPop) {
        this.rb = rb;
        this.dataSetSynPop = dataSetSynPop;
    }


    public void runSP(){
        //method to create the synthetic population
        if (!PropertiesSynPop.get().main.runSyntheticPopulation) return;
        logger.info("   Starting to create the synthetic population.");
        createDirectoryForOutput();
        long startTime = System.nanoTime();

        logger.info("Running Module: Reading inputs");
        Preparation preparation = new Preparation(dataSetSynPop);
        preparation.run();

        logger.info("Running Module: Optimization IPU");
        Optimization optimization = new Optimization(dataSetSynPop);
        optimization.run();

        logger.info("Running Module: Allocation");
        Allocation allocation = new Allocation(dataSetSynPop);
        allocation.run();

        //addCars(false);
        SummarizeData.writeOutSyntheticPopulationDE(SiloUtil.getBaseYear());

        long estimatedTime = System.nanoTime() - startTime;
        logger.info("   Finished creating the synthetic population. Elapsed time: " + estimatedTime);
    }


    private void createDirectoryForOutput() {
        // create output directories
        SiloUtil.createDirectoryIfNotExistingYet("microData");
        SiloUtil.createDirectoryIfNotExistingYet("microData/interimFiles");
    }



    private void validationCommutersFlow(){

        //For checking
        //OD matrix from the commuters data, for validation


        int[] allCounties = PropertiesSynPop.get().main.selectedMunicipalities.getColumnAsInt("smallCenter");
        TableDataSet observedODFlow = SiloUtil.readCSVfile("input/syntheticPopulation/odMatrixCommuters.csv");
        observedODFlow.buildIndex(observedODFlow.getColumnPosition("ID_city"));
        //OD matrix for the core cities, obtained from the commuters data
        TableDataSet observedCoreODFlow = new TableDataSet();
        int [] selectedCounties = SiloUtil.idendifyUniqueValues(allCounties);
        observedCoreODFlow.appendColumn(selectedCounties,"smallCenter");
        for (int i = 0; i < selectedCounties.length; i++){
            int[] dummy = SiloUtil.createArrayWithValue(selectedCounties.length,0);
            observedCoreODFlow.appendColumn(dummy,Integer.toString(selectedCounties[i]));
        }
        observedCoreODFlow.buildIndex(observedCoreODFlow.getColumnPosition("smallCenter"));
        int ini = 0;
        int end = 0;
        // We decided to read this file here again, as this method is likely to be removed later, which is why we did not
        // want to create a global variable for TableDataSet selectedMunicipalities (Ana and Rolf, 29 Mar 2017)

        int[] citySmallID = PropertiesSynPop.get().main.selectedMunicipalities.getColumnAsInt("smallID");
        for (int i = 0; i < cityID.length; i++){
            ini = (int) PropertiesSynPop.get().main.selectedMunicipalities.getIndexedValueAt(cityID[i],"smallCenter");
            for (int j = 0; j < cityID.length; j++){
                end = (int) PropertiesSynPop.get().main.selectedMunicipalities.getIndexedValueAt(cityID[j],"smallCenter");
                observedCoreODFlow.setIndexedValueAt(ini,Integer.toString(end),
                        observedCoreODFlow.getIndexedValueAt(ini,Integer.toString(end)) + observedODFlow.getIndexedValueAt(cityID[i],Integer.toString(cityID[j])));
            }
        }
        //OD flows at the municipality level in one TableDataSet, to facilitate visualization of the deviation between the observed data and the estimated data
        odMunicipalityFlow = new TableDataSet();
        int[] cityKeys = new int[citySmallID.length * citySmallID.length];
        int[] odData = new int[citySmallID.length * citySmallID.length];
        int k = 0;
        for (int row = 0; row < citySmallID.length; row++){
            for (int col = 0; col < citySmallID.length; col++){
                cityKeys[k] = citySmallID[row] * 1000 + citySmallID[col];
                odData[k] = (int) observedODFlow.getIndexedValueAt(cityID[row],Integer.toString(cityID[col]));
                k++;
            }
        }
        int[] initial = SiloUtil.createArrayWithValue(cityKeys.length, 0);
        odMunicipalityFlow.appendColumn(cityKeys,"ID_od");
        odMunicipalityFlow.appendColumn(odData,"ObservedFlow");
        odMunicipalityFlow.appendColumn(initial,"SimulatedFlow");
        odMunicipalityFlow.buildIndex(odMunicipalityFlow.getColumnPosition("ID_od"));

        //OD flows at the regional level (5 core cities)
        odCountyFlow = new TableDataSet();
        int[] regionKeys = new int[selectedCounties.length * selectedCounties.length];
        int[] regionalFlows = new int[selectedCounties.length * selectedCounties.length];
        k = 0;
        for (int row = 0; row < selectedCounties.length; row++){
            for (int col = 0; col < selectedCounties.length; col++){
                regionKeys[k] = selectedCounties[row] * 1000 + selectedCounties[col];
                regionalFlows[k] = (int) observedCoreODFlow.getIndexedValueAt(selectedCounties[row],Integer.toString(selectedCounties[col]));
                k++;
            }
        }
        int[] initialFlow = SiloUtil.createArrayWithValue(regionKeys.length, 0);
        odCountyFlow.appendColumn(regionKeys,"ID_od");
        odCountyFlow.appendColumn(regionalFlows,"ObservedFlow");
        odCountyFlow.appendColumn(initialFlow,"SimulatedFlow");
        odCountyFlow.buildIndex(odCountyFlow.getColumnPosition("ID_od"));
    }


    public void writeVectorToCSV(int[] thresholds, double[] frequencies, String outputFile, double a, double g){
        try {

            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, true));
            pw.println("alpha,gamma,threshold,frequency,iteration");

            for (int i = 0; i< thresholds.length; i++) {
                pw.println(a + "," + g + "," + thresholds[i] + "," + frequencies[i]);
            }
            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMatrixToCSV(String outputFile, TableDataSet matrix, Double alpha, Double gamma, int count){
        try {

            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, true));

            for (int i = 1; i<= matrix.getRowCount(); i++) {
                String line = Integer.toString((int) matrix.getValueAt(i,1));
                for  (int j = 2; j <= matrix.getColumnCount(); j++){
                    line = line + "," + Integer.toString((int) matrix.getValueAt(i,j));
                }
                line = line + "," + alpha + "," + gamma + "," + count;
                pw.println(line);
            }
            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void checkTripLengthDistribution (Frequency travelTimes, double alpha, double gamma, String fileName, double step){
        //to obtain the trip length distribution
        int[] timeThresholds1 = new int[79];
        double[] frequencyTT1 = new double[79];
        for (int row = 0; row < timeThresholds1.length; row++) {
            timeThresholds1[row] = row + 1;
            frequencyTT1[row] = travelTimes.getCumPct(timeThresholds1[row]);
            //logger.info("Time: " + timeThresholds1[row] + ", cummulated frequency:  " + frequencyTT1[row]);
        }
        writeVectorToCSV(timeThresholds1, frequencyTT1, fileName, alpha, gamma);

    }

    public void checkodMatrix (TableDataSet odMatrix, double a, double g, int it, String fileName){
        //to obtain the square difference between the observed and estimated OD flows
        double dif = 0;
        double ind = 0;
        int count = 0;
        for (int row = 1; row <= odMatrix.getRowCount(); row++){
            ind = odMatrix.getValueAt(row,Integer.toString(it)) - odMatrix.getValueAt(row,"ObservedFlow");
            dif = dif + ind * ind;
            count++;
        }
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(fileName, true));
            pw.println(a + "," + g + "," + dif + "," + dif / count + "," + it);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void addCars(boolean flagSkipCreationOfSPforDebugging) {
        //method to estimate the number of cars per household
        //it must be run after generating the population
        CreateCarOwnershipModel createCarOwnershipModel = new CreateCarOwnershipModel(rb);
        createCarOwnershipModel.run(flagSkipCreationOfSPforDebugging);
    }


}
