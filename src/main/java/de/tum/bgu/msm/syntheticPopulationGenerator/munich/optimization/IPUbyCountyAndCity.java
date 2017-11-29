package de.tum.bgu.msm.syntheticPopulationGenerator.munich.optimization;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.utils.concurrent.ConcurrentFunctionExecutor;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.IntStream;

public class IPUbyCountyAndCity {

    private static final Logger logger = Logger.getLogger(IPUbyCountyAndCity.class);

    private final DataSetSynPop dataSetSynPop;

    private TableDataSet errorsCounty;
    private TableDataSet errorsMunicipality;
    private TableDataSet errorsSummary;

    private Map<Integer, double[]> weightsByMun;
    private Map<Integer, double[]> minWeightsByMun;
    private Map<String, int[]> valuesByHousehold;
    private Map<String, Integer> totalCounty;
    private Map<Integer, Map<String, Integer>> totalMunicipality;
    private Map<Integer, Map<String, Double>> errorByMun;
    private Map<String, Double> errorByRegion;

    public IPUbyCountyAndCity(DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
    }

    public void run(){
        createWeights();
        createErrors();
        for (int county : dataSetSynPop.getCountyIDs()){
            initializeErrorsandTotals(county);
            int finish = 0;
            int iteration = 0;
            double initialError = PropertiesSynPop.get().main.initialError;
            double minError = initialError;
            while (finish == 0 & iteration < PropertiesSynPop.get().main.maxIterations) {
                calculateWeights(county);
                averageError = calculateErrors(county);
                logger.info("   County " + county + ". Iteration " + iteration + ". Average error: " + averageErrorIteration * 100 + " %.");

            }
            resetErrorsandTotals();
        }
    }


    public void calculateWeights(int county){

        ArrayList<Integer> mun = dataSetSynPop.getMunicipalitiesByCounty().get(county);
        int[] municipalities = mun.stream().mapToInt(i->i).toArray();
        //for each iteration
        int finish = 0;
        int iteration = 0;
        double initialError = PropertiesSynPop.get().main.initialError;
        double minError = initialError;
        while (finish == 0 & iteration < PropertiesSynPop.get().main.maxIterations) {

            //For each attribute at the region level (landkreise), we obtain the weights
            double weightedSumRegion = 0;
            for (String attribute : PropertiesSynPop.get().main.attributesCounty) {
                Iterator<Integer> iterator1 = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
                while (iterator1.hasNext()) {
                    Integer municipality = iterator1.next();
                    weightedSumRegion = weightedSumRegion + SiloUtil.sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attribute));
                }
                if (weightedSumRegion > 0.001) {
                    double updatingFactor = totalCounty.get(attribute) / weightedSumRegion;
                    Iterator<Integer> iterator2 = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
                    while (iterator2.hasNext()) {
                        Integer municipality = iterator2.next();
                        double[] previousWeights = weightsByMun.get(municipality);
                        int[] values = valuesByHousehold.get(attribute);
                        double[] updatedWeights = new double[previousWeights.length];
                        IntStream.range(0, previousWeights.length).parallel().forEach(id -> updatedWeights[id] = multiplyIfNotZero(previousWeights[id], values[id], updatingFactor));
                        weightsByMun.put(municipality, updatedWeights);
                    }
                }
                //logger.info("Attribute " + attribute + ": sum is " + weightedSumRegion);
                weightedSumRegion = 0;
            }


            //For each municipality, obtain the weight matching each attribute
            ConcurrentFunctionExecutor executor = new ConcurrentFunctionExecutor();
            Iterator<Integer> iterator = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
            while (iterator.hasNext()) {
                Integer municipality = iterator.next();
                executor.addFunction(() -> {
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
                });
            }
            executor.execute();



        }

        //Write the weights after finishing IPU for each municipality (saved each time over the previous version)
        for (int municipality : municipalities) {
            dataSetSynPop.getWeights().appendColumn(minWeightsByMun.get(municipality), Integer.toString(municipality));
        }

        SiloUtil.writeTableDataSet(dataSetSynPop.getWeights(), "input/syntheticPopulation/weights1.csv");


    }


    public double calculateErrors(int county){

        ArrayList<Integer> mun = dataSetSynPop.getMunicipalitiesByCounty().get(county);
        int[] municipalities = mun.stream().mapToInt(i->i).toArray();
        double averageErrorIteration = 0.;
        int counter = 0;
        //obtain errors by county
        for (String attributeC : PropertiesSynPop.get().main.attributesCounty) {
            double errorByCounty = 0.;
            double weightedSumCounty = 0.;
            if (totalCounty.get(attributeC) > 0) {
                Iterator<Integer> iterator3 = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
                while (iterator3.hasNext()) {
                    Integer municipality = iterator3.next();
                    double weightedSum = SiloUtil.sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attributeC));
                    weightedSumCounty += weightedSum;
                }
                errorByCounty = errorByCounty + Math.abs((weightedSumCounty - totalCounty.get(attributeC)) / totalCounty.get(attributeC));
                errorByRegion.put(attributeC, errorByCounty);
                averageErrorIteration += errorByCounty;
                counter++;
            }
        }

        //obtain the errors by municipality
        ConcurrentFunctionExecutor executor1 = new ConcurrentFunctionExecutor();
        Iterator<Integer> iterator1 = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
        while (iterator1.hasNext()){
            Integer municipality = iterator1.next();
            Map<String, Double> errorsByMunicipality = Collections.synchronizedMap(new HashMap<>());
            executor1.addFunction(() ->{
                for (String attribute : PropertiesSynPop.get().main.attributesMunicipality){
                    double weightedSumMunicipality = SiloUtil.sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attribute));
                    double errorByAttributeAndMunicipality = 0;
                    if (totalMunicipality.get(municipality).get(attribute) > 0){
                        errorByAttributeAndMunicipality = Math.abs((weightedSumMunicipality - totalMunicipality.get(municipality).get(attribute)) / totalMunicipality.get(municipality).get(attribute));
                        errorsByMunicipality.put(attribute, errorByAttributeAndMunicipality);
                    }
                }
            });
            errorByMun.put(municipality, errorsByMunicipality);
        }
        executor1.execute();
        for (int municipality : municipalities) {
            averageErrorIteration = averageErrorIteration + errorByMun.get(municipality).values().stream().mapToDouble(Number::doubleValue).sum();
            counter = counter + errorByMun.get(municipality).entrySet().size();
        }

        averageErrorIteration = averageErrorIteration / counter;
        return averageErrorIteration;
    }


    public void stoppingCriteria(){
        //Stopping criteria: exceeds the maximum number of iterations or the maximum error is lower than the threshold
        if (averageErrorIteration < PropertiesSynPop.get().main.maxError) {
            finish = 1;
            logger.info("   IPU finished after :" + iteration + " iterations with a minimum average error of: " + minError * 100 + " %.");
            iteration =PropertiesSynPop.get().main. maxIterations + 1;
        } else if ((iteration / PropertiesSynPop.get().main.iterationError) % 1 == 0) {
            if (Math.abs((initialError - averageErrorIteration) / initialError) < PropertiesSynPop.get().main.improvementError) {
                finish = 1;
                logger.info("   IPU finished after " + iteration + " iterations because the error does not improve. The minimum average error is: " + minError * 100 + " %.");
            } else if (averageErrorIteration == 0) {
                finish = 1;
                logger.info("   IPU finished after " + iteration + " iterations because the error starts increasing. The minimum average error is: " + minError * 100 + " %.");
            } else {
                initialError = averageErrorIteration;
                iteration = iteration + 1;
            }
        } else if (iteration == PropertiesSynPop.get().main.maxIterations) {
            finish = 1;
            logger.info("   IPU finished after the total number of iterations. The minimum average error is: " + minError * 100 + " %.");
        } else {
            iteration = iteration + 1;
        }

        if (averageErrorIteration < minError) {
            for (int municipality : municipalities) {
                double[] minW = weightsByMun.get(municipality);
                minWeightsByMun.put(municipality, minW);
            }
            minError = averageErrorIteration;
        }
        //long estimatedTime = (System.nanoTime() - startTime) / 1000000000;
        errorsSummary.setIndexedValueAt(county, "error", (float) minError);
        errorsSummary.setIndexedValueAt(county, "iterations", iteration);
        //errorsSummary.setIndexedValueAt(county, "time", estimatedTime);
    }

    public double multiplyIfNotZero(double x, double y, double f){
        if (y == 0){
            return x;
        } else {
            return x * f;
        }
    }


    public void createWeights(){
        int[] microDataIds = dataSetSynPop.getFrequencyMatrix().getColumnAsInt("ID");
        dataSetSynPop.getFrequencyMatrix().buildIndex(dataSetSynPop.getFrequencyMatrix().getColumnPosition("ID"));
        dataSetSynPop.setWeights(new TableDataSet());
        dataSetSynPop.getWeights().appendColumn(microDataIds, "ID");
    }


    public void createErrors(){
        errorsCounty = new TableDataSet();
        errorsMunicipality = new TableDataSet();
        errorsSummary = new TableDataSet();
        String[] labels = new String[]{"error", "iterations","time"};
        errorsCounty = SiloUtil.initializeTableDataSet(errorsCounty,
                PropertiesSynPop.get().main.attributesCounty, dataSetSynPop.getCountyIDs());
        errorsMunicipality =  SiloUtil.initializeTableDataSet(errorsMunicipality,
                PropertiesSynPop.get().main.attributesMunicipality, dataSetSynPop.getCityIDs());
        errorsSummary =  SiloUtil.initializeTableDataSet(errorsSummary, labels, dataSetSynPop.getCountyIDs());
    }

    public void initializeErrorsandTotals(int county){

        //weights, values, control totals
        weightsByMun = Collections.synchronizedMap(new HashMap<>());
        minWeightsByMun = Collections.synchronizedMap(new HashMap<>());
        valuesByHousehold = Collections.synchronizedMap(new HashMap<>());
        totalCounty = Collections.synchronizedMap(new HashMap<>());
        totalMunicipality = Collections.synchronizedMap(new HashMap<>());
        errorByMun = Collections.synchronizedMap(new HashMap<>());
        errorByRegion = Collections.synchronizedMap(new HashMap<>());

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
        for (String attribute : PropertiesSynPop.get().main.attributesMunicipality) {
            int[] values = new int[dataSetSynPop.getFrequencyMatrix().getRowCount()];
            for (int i = 1; i <= dataSetSynPop.getFrequencyMatrix().getRowCount(); i++) {
                values[i - 1] = (int) dataSetSynPop.getFrequencyMatrix().getValueAt(i, attribute);
            }
            valuesByHousehold.put(attribute, values);
            Iterator<Integer> iterator = dataSetSynPop.getMunicipalitiesByCounty().get(county).iterator();
            while (iterator.hasNext()) {
                Integer municipality = iterator.next();
                double[] dummy = SiloUtil.createArrayWithValue(dataSetSynPop.getFrequencyMatrix().getRowCount(), 1.);
                weightsByMun.put(municipality, dummy);
                double[] dummy1 = SiloUtil.createArrayWithValue(dataSetSynPop.getFrequencyMatrix().getRowCount(), 1.);
                minWeightsByMun.put(municipality, dummy1);
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
    }

    public void resetErrorsandTotals(){
        weightsByMun.clear();
        minWeightsByMun.clear();
        valuesByHousehold.clear();
        totalCounty.clear();
        totalMunicipality.clear();
        errorByMun.clear();
        errorByRegion.clear();
    }
}
