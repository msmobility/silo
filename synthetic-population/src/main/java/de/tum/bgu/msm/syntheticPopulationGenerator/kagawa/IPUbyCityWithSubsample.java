package de.tum.bgu.msm.syntheticPopulationGenerator.kagawa;

import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.optimizationIPU.optimization.IPUbyCity;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class IPUbyCityWithSubsample {



    private static final Logger logger = Logger.getLogger(IPUbyCity.class);

    private final DataSetSynPop dataSetSynPop;

    private Map<Integer, double[]> weightsByMun;
    private Map<Integer, double[]> minWeightsByMun;
    private Map<String, int[]> valuesByHousehold;
    private Map<Integer, Map<String, Integer>> totalMunicipality;
    private Map<String, Double> errorsByMunicipality;

    private HashMap<Integer, Integer> hhs;

    private double initialError;
    private double minError;
    private long startTime;
    private int finish;
    private int iteration;
    private double averageError;

    public IPUbyCityWithSubsample(DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
    }

    public void run(){

        for (int municipality : dataSetSynPop.getCityIDs()){
            if (!dataSetSynPop.getMunicipalitiesWithZeroPopulation().contains(municipality)) {
                logger.info("   Municipality " + municipality + ". IPU starts");
                initializeErrorsandTotals(municipality);
                while (finish == 0 & iteration < PropertiesSynPop.get().main.maxIterations) {
                    calculateWeights(municipality);
                    averageError = calculateErrors(municipality);
                    finish = checkStoppingCriteria(municipality, averageError, iteration);
                    iteration++;
                }
                summarizeErrorsAndWeights(municipality, iteration);
                logger.info("   IPU finished after : " + iteration + " iterations with a minimum average error of: " + minError * 100 + " %.");
                resetErrorsandTotals();
            }
        }

    }



    public void calculateWeights(int municipality){

        //For each municipality, obtain the weight matching each attribute
        final ExecutorService service = Executors.newCachedThreadPool();
        List<Callable<Void>> tasks = new ArrayList<>();
        tasks.add(() -> {
            for (String attribute : PropertiesSynPop.get().main.attributesMunicipality) {
                double weightedSumMunicipality = SiloUtil.sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attribute));
                if (weightedSumMunicipality > 0.001) {
                    double updatingFactor = totalMunicipality.get(municipality).get(attribute) / weightedSumMunicipality;
                    double[] previousWeights = weightsByMun.get(municipality);
                    int[] values = valuesByHousehold.get(attribute);
                    double[] updatedWeights = new double[previousWeights.length];
                    IntStream.range(0, previousWeights.length).parallel().forEach(id -> updatedWeights[id] = multiplyIfNotZero(previousWeights[id], values[id], updatingFactor));
                    weightsByMun.put(municipality, updatedWeights);
                }
            }
            return null;
        });
        try {
            service.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public double calculateErrors(int municipality){

        int counter = 0;
        //obtain the errors by municipality
        final ExecutorService service = Executors.newCachedThreadPool();
        List<Callable<Void>> tasks = new ArrayList<>();
        tasks.add(() ->{
            for (String attribute : PropertiesSynPop.get().main.attributesMunicipality){
                double weightedSumMunicipality = SiloUtil.sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attribute));
                double errorByAttributeAndMunicipality = 0;
                if (totalMunicipality.get(municipality).get(attribute) > 0){
                    errorByAttributeAndMunicipality = Math.abs((weightedSumMunicipality - totalMunicipality.get(municipality).get(attribute)) / totalMunicipality.get(municipality).get(attribute));
                    errorsByMunicipality.put(attribute, errorByAttributeAndMunicipality);
                }
            }
            return null;
        });
        try {
            service.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double averageErrorIteration = errorsByMunicipality.values().stream().mapToDouble(Number::doubleValue).sum();
        counter = counter + errorsByMunicipality.entrySet().size();


        averageErrorIteration = averageErrorIteration / counter;
        return averageErrorIteration;
    }


    public int checkStoppingCriteria(int municipality, double averageErrorIteration, int iteration){

        //Stopping criteria: exceeds the maximum number of iterations or the maximum error is lower than the threshold
        int finish = 0;
        if (averageErrorIteration < PropertiesSynPop.get().main.maxError) {
            finish = 1;
            logger.info("   IPU finished after :" + iteration + " iterations with a minimum average error of: " + minError * 100 + " %.");
        } else if ((iteration / PropertiesSynPop.get().main.iterationError) % 1 == 0) {
            if (Math.abs((initialError - averageErrorIteration) / initialError) < PropertiesSynPop.get().main.improvementError) {
                finish = 1;
                logger.info("   IPU finished after " + iteration + " iterations because the error does not improve. The minimum average error is: " + minError * 100 + " %.");
            } else if (averageErrorIteration == 0) {
                finish = 1;
                logger.info("   IPU finished after " + iteration + " iterations because the error starts increasing. The minimum average error is: " + minError * 100 + " %.");
            } else {
                initialError = averageErrorIteration;
            }
        } else if (iteration == PropertiesSynPop.get().main.maxIterations) {
            finish = 1;
            logger.info("   IPU finished after the total number of iterations. The minimum average error is: " + minError * 100 + " %.");
        } else {

        }

        if (averageErrorIteration < minError) {
            double[] minW = weightsByMun.get(municipality);
            minWeightsByMun.put(municipality, minW);
            minError = averageErrorIteration;
        }
        return finish;
    }


    public void summarizeErrorsAndWeights(int municipality, int iteration){

        //Write the weights after finishing IPU for each municipality (saved each time over the previous version)
        long estimatedTime = (System.nanoTime() - startTime) / 1000000000;
        dataSetSynPop.getErrorsSummary().setIndexedValueAt(municipality, "error", (float) minError);
        dataSetSynPop.getErrorsSummary().setIndexedValueAt(municipality, "iterations", iteration);
        dataSetSynPop.getErrorsSummary().setIndexedValueAt(municipality, "time", estimatedTime);

        double[] values = new double[dataSetSynPop.getFrequencyMatrix().getRowCount()];
        int row = 0;
        int rowId = 0;
        for (int i : dataSetSynPop.getFrequencyMatrix().getColumnAsInt("id")) {
            if (hhs.containsKey(i)) {
                values[row] = minWeightsByMun.get(municipality)[rowId];
                rowId++;
            } else {
                values[row] = 0;
            }
            row++;
        }
        dataSetSynPop.getWeights().appendColumn(values, Integer.toString(municipality));
        for (String attribute : PropertiesSynPop.get().main.attributesMunicipality){
            float value = errorsByMunicipality.get(attribute).floatValue();
            dataSetSynPop.getErrorsMunicipality().setIndexedValueAt(municipality, attribute, value);
        }

        SiloUtil.writeTableDataSet(dataSetSynPop.getWeights(), PropertiesSynPop.get().main.weightsFileName);
        SiloUtil.writeTableDataSet(dataSetSynPop.getErrorsMunicipality(), PropertiesSynPop.get().main.errorsMunicipalityFileName);
        SiloUtil.writeTableDataSet(dataSetSynPop.getErrorsSummary(), PropertiesSynPop.get().main.errorsSummaryFileName);
    }


    public double multiplyIfNotZero(double x, double y, double f){
        if (y == 0){
            return x;
        } else {
            return x * f;
        }
    }


    public void initializeErrorsandTotals(int municipality){

        startTime = System.nanoTime();

        //weights, values, control totals
        weightsByMun = Collections.synchronizedMap(new HashMap<>());
        minWeightsByMun = Collections.synchronizedMap(new HashMap<>());
        valuesByHousehold = Collections.synchronizedMap(new HashMap<>());
        totalMunicipality = Collections.synchronizedMap(new HashMap<>());
        errorsByMunicipality = Collections.synchronizedMap(new HashMap<>());


        finish = 0;
        iteration = 0;
        initialError = PropertiesSynPop.get().main.initialError;
        minError = PropertiesSynPop.get().main.initialError;
        double weightedSum0 = 0f;

        //obtain the subsample of microhouseholds that live in V2
        hhs = dataSetSynPop.getHouseholdsForFrequencyMatrix().get(municipality);


        //initialize errors
        //initialize errors, considering the first weight (equal to 1)
        for (String attribute : PropertiesSynPop.get().main.attributesMunicipality) {
            int[] values = new int[hhs.size()];
            int row = 0;
            for (int i : dataSetSynPop.getFrequencyMatrix().getColumnAsInt("id")) {
                if (hhs.containsKey(i)) {
                    values[row] = (int) dataSetSynPop.getFrequencyMatrix().getIndexedValueAt(i, attribute);
                    row++;
                }
            }
            valuesByHousehold.put(attribute, values);
            double[] dummy = SiloUtil.createArrayWithValue(hhs.size(), 1.);
            weightsByMun.put(municipality, dummy);
            double[] dummy1 = SiloUtil.createArrayWithValue(hhs.size(), 1.);
            minWeightsByMun.put(municipality, dummy1);
            if (totalMunicipality.containsKey(municipality)) {
                Map<String, Integer> innerMap = totalMunicipality.get(municipality);
                innerMap.put(attribute, (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, attribute));
                totalMunicipality.put(municipality, innerMap);
                errorsByMunicipality.put(attribute, 0.);

            } else {
                HashMap<String, Integer> inner = new HashMap<>();
                inner.put(attribute, (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, attribute));
                totalMunicipality.put(municipality, inner);
                errorsByMunicipality.put(attribute, 0.);
            }
        }
    }

    public void resetErrorsandTotals(){
        weightsByMun.clear();
        minWeightsByMun.clear();
        valuesByHousehold.clear();
        totalMunicipality.clear();
        errorsByMunicipality.clear();

    }



}
