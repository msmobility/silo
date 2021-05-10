package de.tum.bgu.msm.syntheticPopulation;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.models.realEstate.construction.DefaultConstructionDemandStrategy;
import de.tum.bgu.msm.util.concurrent.ConcurrentExecutor;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.Reader;
import java.util.*;
import java.util.stream.IntStream;

public class ipuTestOpt {


    private Reader reader;
    private DefaultConstructionDemandStrategy calculator;

    private TableDataSet controlTotalsMun;
    private TableDataSet controlTotalsCount;
    private TableDataSet frequencyMatrix;
    private TableDataSet municipalityList;
    private String[] attributesMunicipality;
    private String[] attributesCounty;
    private int[] cityID;
    private int[] countyID;
    private HashMap<Integer, ArrayList> municipalitiesByCounty;
    static Logger logger = Logger.getLogger(ipuTestOpt.class);
    private ArrayList<Integer> municipalities;
    private ArrayList<Integer> counties;

    @Before
    public void setup() {

        controlTotalsMun = SiloUtil.readCSVfile2("C:/models/silo/capeTown/input/syntheticPopulation/controlTotalsMunIPU.csv");
        controlTotalsCount = SiloUtil.readCSVfile2("C:/models/silo/capeTown/input/syntheticPopulation/controlTotalsCountIPU.csv");
        controlTotalsMun.buildIndex(controlTotalsMun.getColumnPosition("ID_city"));
        controlTotalsCount.buildIndex(controlTotalsCount.getColumnPosition("ID_county"));
        frequencyMatrix = SiloUtil.readCSVfile2("C:/models/silo/capeTown/input/syntheticPopulation/frequencyMatrix.csv");
        TableDataSet selectedMunicipalities = SiloUtil.readCSVfile2("C:/models/silo/capeTown/input/syntheticPopulation/municipalitiesList.csv");
        String[] attributesMunicipality1 = controlTotalsMun.getColumnLabels();
        String[] attributesCounty1 = controlTotalsCount.getColumnLabels();
        attributesMunicipality = new String[attributesMunicipality1.length - 1];
        attributesCounty = new String[attributesCounty1.length - 1];
        for (int i = 0; i < attributesMunicipality.length; i++){attributesMunicipality[i] = attributesMunicipality1[i+1];}
        for (int i = 0; i < attributesCounty.length; i++){attributesCounty[i] = attributesCounty1[i+1];}
        municipalities = new ArrayList<>();
        counties = new ArrayList<>();
        municipalitiesByCounty = new HashMap<>();
        for (int row = 1; row <= selectedMunicipalities.getRowCount(); row++){
            if (selectedMunicipalities.getValueAt(row,"Select") == 1f){
                int city = (int) selectedMunicipalities.getValueAt(row,"ID_city");
                municipalities.add(city);
                int county = (int) selectedMunicipalities.getValueAt(row,"ID_county");
                if (!SiloUtil.containsElement(counties, county)) {
                    counties.add(county);
                }
                if (municipalitiesByCounty.containsKey(county)) {
                    ArrayList<Integer> citiesInThisCounty = municipalitiesByCounty.get(county);
                    citiesInThisCounty.add(city);
                    municipalitiesByCounty.put(county, citiesInThisCounty);
                } else {
                    ArrayList<Integer> arrayList = new ArrayList<>();
                    arrayList.add(city);
                    municipalitiesByCounty.put(county, arrayList);
                }
            }
        }

    }


    @Ignore("not provided input data")@Test
    public void testModelOne() throws ScriptException {

        long startTime = System.nanoTime();

        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));


        //Create the weights table (for all the municipalities)
        TableDataSet weightsMatrix = new TableDataSet();
        weightsMatrix.appendColumn(microDataIds,"ID");


        long estimatedTime = System.nanoTime() - startTime;
        logger.info("Prep time: " + estimatedTime);

        //For my implementation---------------------------------------------
        //------------------------------------------------------------------
        int county = 199053;
        //float factor = 0f;
        //int position = 0;

        //weights, values, control totals
        Map<Integer, double[]> weightsByMun = Collections.synchronizedMap(new HashMap<>());
        Map<Integer, double[]> minWeightsByMun =Collections.synchronizedMap(new HashMap<>());
        Map<String, int[]> valuesByHousehold = Collections.synchronizedMap(new HashMap<>());
        Map<String, Integer> totalCounty = Collections.synchronizedMap(new HashMap<>());
        Map<Integer, HashMap<String, Integer>> totalMunicipality = Collections.synchronizedMap(new HashMap<>());
        Map<Integer, HashMap<String, Double>> errorByMun = Collections.synchronizedMap(new HashMap<>());
        Map<String, Double> errorByRegion = Collections.synchronizedMap(new HashMap<>());
        double weightedSum0 = 0f;

        int finish = 0;
        int iteration = 0;
        double maxError = 0.00001;
        int maxIterations = 500;
        double minError = 0.001;
        double initialError = 10000;
        double improvementError = 0.001;
        double iterationError = 2;
        double increaseError = 1.05;



            for (int k = 0; k < attributesCounty.length; k++) {
                String attribute = attributesCounty[k];
                int[] values = new int[frequencyMatrix.getRowCount()];
                for (int i = 1; i <= frequencyMatrix.getRowCount(); i++) {
                    values[i - 1] = (int) frequencyMatrix.getValueAt(i, attribute);
                    if (k == 0) {
                        weightedSum0 = weightedSum0 + values[i - 1] * municipalities.size();
                    }
                }
                valuesByHousehold.put(attribute, values);
                int total = (int) controlTotalsCount.getIndexedValueAt(county, attribute);
                totalCounty.put(attribute, total);
                errorByRegion.put(attribute, 0.);
            }
            for (int k = 0; k < attributesMunicipality.length; k++) {
                String attribute = attributesMunicipality[k];
                int[] values = new int[frequencyMatrix.getRowCount()];
                for (int i = 1; i <= frequencyMatrix.getRowCount(); i++) {
                    values[i - 1] = (int) frequencyMatrix.getValueAt(i, attribute);
                }
                valuesByHousehold.put(attribute, values);
                Iterator<Integer> iterator = municipalities.iterator();
                while (iterator.hasNext()) {
                    Integer municipality = iterator.next();
                    double[] dummy = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(), 1.);
                    weightsByMun.put(municipality, dummy);
                    double[] dummy1 = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(), 1.);
                    minWeightsByMun.put(municipality, dummy1);
                    if (totalMunicipality.containsKey(municipality)) {
                        HashMap<String, Integer> inner = totalMunicipality.get(municipality);
                        inner.put(attribute, (int) controlTotalsMun.getIndexedValueAt(municipality, attribute));
                        totalMunicipality.put(municipality, inner);
                        HashMap<String, Double> inner1 = errorByMun.get(municipality);
                        inner1.put(attribute, 0.);
                        errorByMun.put(municipality, inner1);
                    } else {
                        HashMap<String, Integer> inner = new HashMap<>();
                        inner.put(attribute, (int) controlTotalsMun.getIndexedValueAt(municipality, attribute));
                        totalMunicipality.put(municipality, inner);
                        HashMap<String, Double> inner1 = new HashMap<>();
                        inner1.put(attribute, 0.);
                        errorByMun.put(municipality, inner1);
                    }
                }
            }

        while (finish == 0) {

            //For each municipality, obtain the weight matching each attribute
            ConcurrentExecutor executor = ConcurrentExecutor.cachedService();
            Iterator<Integer> iterator = municipalities.iterator();
            while (iterator.hasNext()) {
                Integer municipality = iterator.next();
                executor.addTaskToQueue(() -> {
                    for (String attribute : attributesMunicipality) {
                        double weightedSumMunicipality = sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attribute));
                        if (weightedSumMunicipality > 0.000001) {
                            double factor = totalMunicipality.get(municipality).get(attribute) / weightedSumMunicipality;
                            double[] m1 = weightsByMun.get(municipality);
                            int[] m2 = valuesByHousehold.get(attribute);
                            double[] m3 = new double[m1.length];
                            IntStream.range(0, m1.length).parallel().forEach(id -> m3[id] = multiplyIfNotZero(m1[id], m2[id], factor));
                            weightsByMun.put(municipality, m3);
                        }
                    }
                    return null;
                });
            }
            executor.execute();


            //For each attribute at the region level (landkreise)
            double weightedSumRegion = 0;
            for (String attribute : attributesCounty) {
                Iterator<Integer> iterator1 = municipalities.iterator();
                while (iterator1.hasNext()) {
                    Integer municipality = iterator1.next();
                    weightedSumRegion = weightedSumRegion + sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attribute));
                }
                double factor = totalCounty.get(attribute) / weightedSumRegion;
                Iterator<Integer> iterator2 = municipalities.iterator();
                while (iterator2.hasNext()) {
                    Integer municipality = iterator2.next();
                    double[] m1 = weightsByMun.get(municipality);
                    int[] m2 = valuesByHousehold.get(attribute);
                    double[] m3 = new double[m1.length];
                    IntStream.range(0, m1.length).parallel().forEach(id -> m3[id] = multiplyIfNotZero(m1[id], m2[id], factor));
                    weightsByMun.put(municipality, m3);
                }
                //logger.info("Attribute " + attribute + ": sum is " + weightedSumRegion);
                weightedSumRegion = 0;
            }


            //obtain errors by municipality and region for the final weight at the iteration
            //update the weighted sums and errors of the region attributes, given the new weights
            double averageErrorIteration = 0.;
            int counter = 0;
            for (String attributeC : attributesCounty) {
                double errorC = 0.;
                Iterator<Integer> iterator3 = municipalities.iterator();
                while (iterator3.hasNext()) {
                    Integer municipality = iterator3.next();
                    for (String attribute : attributesMunicipality) {
                        double weightedSumMunicipality = sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attribute));
                        double error = 0;
                        if (totalMunicipality.get(municipality).get(attribute) > 0) {
                            error = Math.abs((weightedSumMunicipality - totalMunicipality.get(municipality).get(attribute)) / totalMunicipality.get(municipality).get(attribute));
                            averageErrorIteration += error;
                            counter++;
                        }
                        HashMap<String, Double> inner = errorByMun.get(municipality);
                        if (inner == null) {
                            inner = new HashMap<String, Double>();
                            errorByMun.put(municipality, inner);
                        }
                        inner.put(attribute, error);
                    }
                    errorC = errorC + sumProduct(weightsByMun.get(municipality), valuesByHousehold.get(attributeC));
                }
                errorByRegion.put(attributeC, errorC);
                averageErrorIteration += errorC;
                counter++;
            }

            averageErrorIteration = averageErrorIteration / counter;
            logger.info("   County " + county + ". Iteration " + 0 + ". Average error: " + averageErrorIteration * 100 + " %.");

            Long estimatedTime1 = System.nanoTime() - estimatedTime;

            logger.info("   Calculation time " + estimatedTime1);

            //Stopping criteria: exceeds the maximum number of iterations or the maximum error is lower than the threshold
            if (averageErrorIteration < maxError) {
                finish = 1;
                logger.info("   IPU finished after :" + iteration + " iterations with a minimum average error of: " + minError * 100 + " %.");
                iteration = maxIterations + 1;
            } else if ((iteration / iterationError) % 1 == 0) {
                if (Math.abs((initialError - averageErrorIteration) / initialError) < improvementError) {
                    finish = 1;
                    logger.info("   IPU finished after " + iteration + " iterations because the error does not improve. The minimum average error is: " + minError * 100 + " %.");
                } else if (averageErrorIteration  == 0) {
                    finish = 1;
                    logger.info("   IPU finished after " + iteration + " iterations because the error starts increasing. The minimum average error is: " + minError * 100 + " %.");
                } else {
                    initialError = averageErrorIteration;
                    iteration = iteration + 1;
                }
            } else if (iteration == maxIterations) {
                finish = 1;
                logger.info("   IPU finished after the total number of iterations. The minimum average error is: " + minError * 100 + " %.");
            } else {
                iteration = iteration + 1;
            }
        }

    }

    public double multiplyIfNotZero(double x, double y, double f){
        if (y == 0){
            return x;
        } else {
            return x * f;
        }
    }

    public double sumProduct(double[] a, int[] b){
        double sum = 0;
        for (int i = 0; i < a.length; i++){
            sum = sum + a[i]*b[i];
        }


        return sum;
    }


    public double maximum (double[] a){
        double max = 0;
        for (int i = 0; i < a.length; i++){
            if (a[i] > max){
                max = a[i];
            }
        }
        return  max;
    }
}
