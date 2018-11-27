package de.tum.bgu.msm.syntheticPopulationGenerator.optimizationIPU.optimization;

import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.IntStream;

public class IPUbyCountyCityAndBorough {
    private static final Logger logger = Logger.getLogger(IPUbyCountyCityAndBorough.class);

    private final DataSetSynPop dataSetSynPop;
    private Map<Integer, double[]> weightsByBorough;
    private Map<Integer, double[]> minWeightsByBorough;
    private Map<String, int[]> valuesByHousehold;
    private Map<String, Integer> totalCounty;
    private Map<Integer, Map<String, Integer>> totalMunicipality;
    private Map<Integer, Map<String, Double>> errorByMun;
    private Map<String, Double> errorByRegion;
    private Map<Integer, Map<String, Integer>>  totalBorough;
    private Map<Integer, Map<String, Double>> errorsByBorough;

    private double initialError;
    private double minError;
    private long startTime;
    private int finish;
    private int iteration;
    private double averageError;


    public IPUbyCountyCityAndBorough(DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
    }

    public void run(int county){
        initializeErrorsandTotals(county);
        while (finish == 0 & iteration < PropertiesSynPop.get().main.maxIterations) {
            calculateWeights(county);
            averageError = calculateErrors(county);
            finish = checkStoppingCriteria(county, averageError, iteration);
            iteration++;
        }
        summarizeErrorsAndWeights(county, iteration);
        resetErrorsandTotals();
    }


    public void calculateWeights(int county){

        //For each attribute at the region level (landkreise), we obtain the weights
        double weightedSumRegion = 0;
        for (String attribute : PropertiesSynPop.get().main.attributesCounty) {
            Iterator<Integer> iterator1 = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
            while (iterator1.hasNext()) {
                Integer municipality = iterator1.next();
                Iterator<Integer> iterator4 = dataSetSynPop.getBoroughsByCounty().get(county).iterator();
                while (iterator4.hasNext()) {
                    Integer borough = iterator4.next();
                    weightedSumRegion = weightedSumRegion + SiloUtil.sumProduct(weightsByBorough.get(borough), valuesByHousehold.get(attribute));
                }
            }
            if (weightedSumRegion > 0.001) {
                double updatingFactor = totalCounty.get(attribute) / weightedSumRegion;
                Iterator<Integer> iterator2 = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
                while (iterator2.hasNext()) {
                    Integer municipality = iterator2.next();
                    Iterator<Integer> iterator4 = dataSetSynPop.getBoroughsByCounty().get(county).iterator();
                    while (iterator4.hasNext()) {
                        Integer borough = iterator4.next();
                        double[] previousWeights = weightsByBorough.get(borough);
                        int[] values = valuesByHousehold.get(attribute);
                        double[] updatedWeights = new double[previousWeights.length];
                        IntStream.range(0, previousWeights.length).parallel().forEach(id -> updatedWeights[id] = multiplyIfNotZero(previousWeights[id], values[id], updatingFactor));
                        weightsByBorough.put(borough, updatedWeights);
                    }
                }
            }
            //logger.info("Attribute " + attribute + ": sum is " + weightedSumRegion);
            weightedSumRegion = 0;
        }


        //For each municipality, obtain the weight matching each attribute
        double weightedSumMunicipality = 0;
        for (String attribute : PropertiesSynPop.get().main.attributesMunicipality) {
            Iterator<Integer> iterator1 = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
            while (iterator1.hasNext()) {
                Integer municipality = iterator1.next();
                Iterator<Integer> iterator4 = dataSetSynPop.getBoroughsByCounty().get(county).iterator();
                while (iterator4.hasNext()) {
                    Integer borough = iterator4.next();
                    weightedSumMunicipality = weightedSumMunicipality + SiloUtil.sumProduct(weightsByBorough.get(borough), valuesByHousehold.get(attribute));
                }
                if (weightedSumMunicipality > 0.001) {
                    double updatingFactor = totalMunicipality.get(municipality).get(attribute) / weightedSumMunicipality;
                    Iterator<Integer> iterator5 = dataSetSynPop.getBoroughsByCounty().get(county).iterator();
                    while (iterator5.hasNext()) {
                        Integer borough = iterator5.next();
                        double[] previousWeights = weightsByBorough.get(borough);
                        int[] values = valuesByHousehold.get(attribute);
                        double[] updatedWeights = new double[previousWeights.length];
                        IntStream.range(0, previousWeights.length).parallel().forEach(id -> updatedWeights[id] = multiplyIfNotZero(previousWeights[id], values[id], updatingFactor));
                        weightsByBorough.put(borough, updatedWeights);
                    }
                }
                //logger.info("Attribute " + attribute + ": sum is " + weightedSumRegion);
            }
            weightedSumMunicipality = 0;
        }

        //For each borough, obtain the weight matching each attribute
        ConcurrentExecutor executor = ConcurrentExecutor.cachedService();
        Iterator<Integer> iterator = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
        while (iterator.hasNext()) {
            Integer municipality = iterator.next();
            Iterator<Integer> iterator5 = dataSetSynPop.getBoroughsByCounty().get(county).iterator();
            while (iterator5.hasNext()) {
                Integer borough = iterator5.next();
                executor.addTaskToQueue(() -> {
                    for (String attribute : PropertiesSynPop.get().main.attributesBorough) {
                        double weightedSumBorough = SiloUtil.sumProduct(weightsByBorough.get(borough), valuesByHousehold.get(attribute));
                        if (weightedSumBorough > 0.001) {
                            double updatingFactor = totalBorough.get(borough).get(attribute) / weightedSumBorough;
                            double[] previousWeights = weightsByBorough.get(borough);
                            int[] values = valuesByHousehold.get(attribute);
                            double[] updatedWeights = new double[previousWeights.length];
                            IntStream.range(0, previousWeights.length).parallel().forEach(id -> updatedWeights[id] = multiplyIfNotZero(previousWeights[id], values[id], updatingFactor));
                            weightsByBorough.put(borough, updatedWeights);
                        }
                    }
                    return null;
                });
            }
        }
        executor.execute();

    }


    public double calculateErrors(int county){

        double averageErrorIteration = 0.;
        int counter = 0;
        //obtain errors by county
        for (String attributeC : PropertiesSynPop.get().main.attributesCounty) {
            double errorByCounty = 0.;
            double weightedSumCounty = 0.;
            if (totalCounty.get(attributeC) > 0) {
                Iterator<Integer> iterator1 = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
                while (iterator1.hasNext()) {
                    Integer municipality = iterator1.next();
                    Iterator<Integer> iterator4 = dataSetSynPop.getBoroughsByCounty().get(county).iterator();
                    while (iterator4.hasNext()) {
                        Integer borough = iterator4.next();
                        double weightedSum = SiloUtil.sumProduct(weightsByBorough.get(borough), valuesByHousehold.get(attributeC));
                        weightedSumCounty += weightedSum;
                    }
                }
                errorByCounty = errorByCounty + Math.abs((weightedSumCounty - totalCounty.get(attributeC)) / totalCounty.get(attributeC));
                errorByRegion.put(attributeC, errorByCounty);
                averageErrorIteration += errorByCounty;
                counter++;
            }
        }

        //obtain errors by county
        Iterator<Integer> iterator2 = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
        while (iterator2.hasNext()) {
            Integer municipality = iterator2.next();
            Map<String, Double> errorMunicipality = Collections.synchronizedMap(new HashMap<>());
            for (String attributeC : PropertiesSynPop.get().main.attributesMunicipality) {
                double errorByCounty = 0.;
                double weightedSumCounty = 0.;
                if (totalMunicipality.get(municipality).get(attributeC) > 0) {
                    Iterator<Integer> iterator1 = dataSetSynPop.getBoroughsByCounty().get(county).iterator();
                    while (iterator1.hasNext()) {
                        Integer borough = iterator1.next();
                        double weightedSum = SiloUtil.sumProduct(weightsByBorough.get(borough), valuesByHousehold.get(attributeC));
                        weightedSumCounty += weightedSum;
                    }
                    errorByCounty = errorByCounty + Math.abs((weightedSumCounty - totalMunicipality.get(municipality).get(attributeC)) / totalMunicipality.get(municipality).get(attributeC));
                    averageErrorIteration += errorByCounty;
                    counter++;
                }
                errorMunicipality.put(attributeC, errorByCounty);
                errorByMun.put(municipality, errorMunicipality);
            }
        }

        //obtain the errors by borough
        ConcurrentExecutor executor1 = ConcurrentExecutor.cachedService();
        Iterator<Integer> iterator6 = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
        while (iterator6.hasNext()){
            Integer municipality = iterator6.next();
            Iterator<Integer> iterator1 = dataSetSynPop.getBoroughsByCounty().get(county).iterator();
            while (iterator1.hasNext()) {
                Integer borough = iterator1.next();
                Map<String, Double> errorsByBorough1 = Collections.synchronizedMap(new HashMap<>());
                executor1.addTaskToQueue(() -> {
                    for (String attribute : PropertiesSynPop.get().main.attributesBorough) {
                        double weightedSumMunicipality = SiloUtil.sumProduct(weightsByBorough.get(borough), valuesByHousehold.get(attribute));
                        double errorByAttributeAndMunicipality = 0;
                        if (totalBorough.get(borough).get(attribute) > 0) {
                            errorByAttributeAndMunicipality = Math.abs((weightedSumMunicipality - totalBorough.get(borough).get(attribute)) / totalBorough.get(borough).get(attribute));
                            errorsByBorough1.put(attribute, errorByAttributeAndMunicipality);
                        }
                    }
                    return null;
                });
                errorsByBorough.put(borough, errorsByBorough1);
            }
        }
        executor1.execute();


        Iterator<Integer> iterator5 = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
        while (iterator5.hasNext()){
            Integer municipality = iterator5.next();
            averageErrorIteration = averageErrorIteration + errorByMun.get(municipality).values().stream().mapToDouble(Number::doubleValue).sum();
            counter = counter + errorByMun.get(municipality).entrySet().size();
            Iterator<Integer> iterator4 = dataSetSynPop.getBoroughsByCounty().get(county).iterator();
            while (iterator4.hasNext()) {
                Integer borough = iterator4.next();
                averageErrorIteration = averageErrorIteration + errorsByBorough.get(borough).values().stream().mapToDouble(Number::doubleValue).sum();
                counter = counter + errorsByBorough.get(borough).entrySet().size();
            }
        }

        averageErrorIteration = averageErrorIteration / counter;
        //logger.info("   County " + county + ". Iteration " + iteration + ". Average error: " + averageErrorIteration * 100 + " %.");
        return averageErrorIteration;
    }


    public int checkStoppingCriteria(int county, double averageErrorIteration, int iteration){

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
            Iterator<Integer> iterator = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
            while (iterator.hasNext()){
                Integer municipality = iterator.next();
                Iterator<Integer> iterator4 = dataSetSynPop.getBoroughsByCounty().get(county).iterator();
                while (iterator4.hasNext()) {
                    Integer borough = iterator4.next();
                    double[] minW = weightsByBorough.get(borough);
                    minWeightsByBorough.put(borough, minW);
                }
            }
            minError = averageErrorIteration;
        }
        return finish;
    }


    public void summarizeErrorsAndWeights(int county, int iteration){

        //Write the weights after finishing IPU for each municipality (saved each time over the previous version)
        long estimatedTime = (System.nanoTime() - startTime) / 1000000000;
        dataSetSynPop.getErrorsSummary().setIndexedValueAt(county, "error", (float) minError);
        dataSetSynPop.getErrorsSummary().setIndexedValueAt(county, "iterations", iteration);
        dataSetSynPop.getErrorsSummary().setIndexedValueAt(county, "time", estimatedTime);

        Iterator<Integer> iterator = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
        while (iterator.hasNext()){
            Integer municipality = iterator.next();
            for (String attribute : PropertiesSynPop.get().main.attributesMunicipality){
                float value = errorByMun.get(municipality).get(attribute).floatValue();
                dataSetSynPop.getErrorsMunicipality().setIndexedValueAt(municipality, attribute, value);
            }
            Iterator<Integer> iterator4 = dataSetSynPop.getBoroughsByCounty().get(county).iterator();
            while (iterator4.hasNext()) {
                Integer borough = iterator4.next();
                dataSetSynPop.getWeights().appendColumn(minWeightsByBorough.get(borough), Integer.toString(borough));
                for (String attribute : PropertiesSynPop.get().main.attributesBorough){
                    float value = errorsByBorough.get(borough).get(attribute).floatValue();
                    dataSetSynPop.getErrorsBorough().setIndexedValueAt(borough, attribute, value);
                }
            }
        }
        for (String attribute : PropertiesSynPop.get().main.attributesCounty){
            float value = errorByRegion.get(attribute).floatValue();
            dataSetSynPop.getErrorsCounty().setIndexedValueAt(county, attribute, value);
        }

        SiloUtil.writeTableDataSet(dataSetSynPop.getWeights(), PropertiesSynPop.get().main.weightsFileName);
        SiloUtil.writeTableDataSet(dataSetSynPop.getErrorsMunicipality(), PropertiesSynPop.get().main.errorsMunicipalityFileName);
        SiloUtil.writeTableDataSet(dataSetSynPop.getErrorsCounty(), PropertiesSynPop.get().main.errorsCountyFileName);
        SiloUtil.writeTableDataSet(dataSetSynPop.getErrorsSummary(), PropertiesSynPop.get().main.errorsSummaryFileName);
    }


    public double multiplyIfNotZero(double x, double y, double f){
        if (y == 0){
            return x;
        } else {
            return x * f;
        }
    }


    public void initializeErrorsandTotals(int county){

        startTime = System.nanoTime();

        //weights, values, control totals
        weightsByBorough = Collections.synchronizedMap(new HashMap<>());
        minWeightsByBorough = Collections.synchronizedMap(new HashMap<>());
        valuesByHousehold = Collections.synchronizedMap(new HashMap<>());
        totalCounty = Collections.synchronizedMap(new HashMap<>());
        totalMunicipality = Collections.synchronizedMap(new HashMap<>());
        totalBorough = Collections.synchronizedMap(new HashMap<>());
        errorByMun = Collections.synchronizedMap(new HashMap<>());
        errorByRegion = Collections.synchronizedMap(new HashMap<>());
        errorsByBorough = Collections.synchronizedMap(new HashMap<>());


        finish = 0;
        iteration = 0;
        initialError = PropertiesSynPop.get().main.initialError;
        minError = PropertiesSynPop.get().main.initialError;
        double weightedSum0 = 0f;

        //initialize errors
        //initialize errors, considering the first weight (equal to 1)
        for (String attribute : PropertiesSynPop.get().main.attributesCounty) {
            int[] values = new int[dataSetSynPop.getFrequencyMatrix().getRowCount()];
            for (int i = 1; i <= dataSetSynPop.getFrequencyMatrix().getRowCount(); i++) {
                values[i - 1] = (int) dataSetSynPop.getFrequencyMatrix().getValueAt(i, attribute);
                if (attribute.equals(PropertiesSynPop.get().main.attributesCounty[0])) {
                    weightedSum0 = weightedSum0 + values[i - 1] * dataSetSynPop.getMunicipalitiesByCounty().get(county).size();
                }
            }
            valuesByHousehold.put(attribute, values);
            int total = (int) PropertiesSynPop.get().main.marginalsCounty.getIndexedValueAt(county, attribute);
            totalCounty.put(attribute, total);
            errorByRegion.put(attribute, 0.);
        }

        //Errors by borough and city
        for (String attribute : PropertiesSynPop.get().main.attributesMunicipality) {
            int[] values = new int[dataSetSynPop.getFrequencyMatrix().getRowCount()];
            for (int i = 1; i <= dataSetSynPop.getFrequencyMatrix().getRowCount(); i++) {
                values[i - 1] = (int) dataSetSynPop.getFrequencyMatrix().getValueAt(i, attribute);
            }
            valuesByHousehold.put(attribute, values);
            Iterator<Integer> iterator = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
            while (iterator.hasNext()) {
                Integer municipality = iterator.next();
                if (totalMunicipality.containsKey(municipality)) {
                    Map<String, Integer> innerMap = totalMunicipality.get(municipality);
                    innerMap.put(attribute, (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, attribute));
                    totalMunicipality.put(municipality, innerMap);
                    Map<String, Double> inner1 = errorByMun.get(municipality);
                    inner1.put(attribute, 0.);
                    errorByMun.put(municipality, inner1);
                } else {
                    HashMap<String, Integer> inner = new HashMap<>();
                    inner.put(attribute, (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, attribute));
                    totalMunicipality.put(municipality, inner);
                    HashMap<String, Double> inner1 = new HashMap<>();
                    inner1.put(attribute, 0.);
                    errorByMun.put(municipality, inner1);
                }
            }
        }
        //Errors by borough and city
        for (String attribute : PropertiesSynPop.get().main.attributesBorough) {
            int[] values = new int[dataSetSynPop.getFrequencyMatrix().getRowCount()];
            for (int i = 1; i <= dataSetSynPop.getFrequencyMatrix().getRowCount(); i++) {
                values[i - 1] = (int) dataSetSynPop.getFrequencyMatrix().getValueAt(i, attribute);
            }
            valuesByHousehold.put(attribute, values);
            Iterator<Integer> iterator = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
            while (iterator.hasNext()) {
                Integer municipality = iterator.next();
                Iterator<Integer> iterator4 = dataSetSynPop.getBoroughsByCounty().get(county).iterator();
                while (iterator4.hasNext()) {
                    Integer borough = iterator4.next();
                    double[] dummy = SiloUtil.createArrayWithValue(dataSetSynPop.getFrequencyMatrix().getRowCount(), 1.);
                    weightsByBorough.put(borough, dummy);
                    double[] dummy1 = SiloUtil.createArrayWithValue(dataSetSynPop.getFrequencyMatrix().getRowCount(), 1.);
                    minWeightsByBorough.put(borough, dummy1);
                    if (totalBorough.containsKey(borough)) {
                        Map<String, Integer> innerMap = totalBorough.get(borough);
                        innerMap.put(attribute, (int) PropertiesSynPop.get().main.marginalsBorough.getIndexedValueAt(borough, attribute));
                        totalBorough.put(borough, innerMap);
                        Map<String, Double> inner1 = errorsByBorough.get(borough);
                        inner1.put(attribute, 0.);
                        errorsByBorough.put(borough, inner1);
                    } else {
                        HashMap<String, Integer> inner = new HashMap<>();
                        inner.put(attribute, (int) PropertiesSynPop.get().main.marginalsBorough.getIndexedValueAt(borough, attribute));
                        totalBorough.put(borough, inner);
                        HashMap<String, Double> inner1 = new HashMap<>();
                        inner1.put(attribute, 0.);
                        errorsByBorough.put(borough, inner1);
                    }
                }
            }
        }
        dataSetSynPop.setValuesByHousehold(valuesByHousehold);
    }

    public void resetErrorsandTotals(){
        weightsByBorough.clear();
        minWeightsByBorough.clear();
        valuesByHousehold.clear();
        totalCounty.clear();
        totalMunicipality.clear();
        errorByMun.clear();
        errorByRegion.clear();
        errorsByBorough.clear();
    }

}
