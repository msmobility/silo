package de.tum.bgu.msm.syntheticPopulation;

import com.google.common.primitives.Ints;
import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.realEstate.ConstructionDemandJSCalculator;
import org.junit.Before;
import org.junit.Test;

import javax.measure.unit.SI;
import javax.script.ScriptException;
import java.io.Reader;
import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;

import static de.tum.bgu.msm.SiloUtil.getWeightedSum;

public class ipuTestOpt {


    private Reader reader;
    private ConstructionDemandJSCalculator calculator;

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

    @Test
    public void testModelOne() throws ScriptException {

        long startTime = System.nanoTime();

        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        int[] nonZeroIds = frequencyMatrix.getColumnAsInt("ID");
        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));


        //Create the weights table (for all the municipalities)
        TableDataSet weightsMatrix = new TableDataSet();
        weightsMatrix.appendColumn(microDataIds,"ID");


        long estimatedTime = System.nanoTime() - startTime;
        logger.info("Prep time: " + estimatedTime);

        //For my implementation---------------------------------------------
        //------------------------------------------------------------------
        int county = 199053;
        float factor = 0f;
        int position = 0;

        //weights, values, control totals
        HashMap<Integer, double[]> weightsByMun = new HashMap<>();
        HashMap<Integer, double[]> minWeightsByMun = new HashMap<>();
        HashMap<String, int[]> valuesByHousehold = new HashMap<>();
        HashMap<String, Integer> totalCounty = new HashMap<>();
        HashMap<Integer, HashMap<String, Integer>> totalMunicipality = new HashMap<>();
        HashMap<Integer, HashMap<String, Double>> errorByMun = new HashMap<>();
        HashMap<String, Double> errorByRegion = new HashMap<>();
        float weightedSum0 = 0f;

        for (int k = 0; k < attributesCounty.length; k++){
            String attribute = attributesCounty[k];
            int[] values = new int[frequencyMatrix.getRowCount()];
            for (int i = 1; i <= frequencyMatrix.getRowCount(); i++){
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
        for (int k = 0; k < attributesMunicipality.length; k++){
            String attribute = attributesMunicipality[k];
            int[] values = new int[frequencyMatrix.getRowCount()];
            for (int i = 1; i <= frequencyMatrix.getRowCount(); i++){
                values[i - 1] = (int) frequencyMatrix.getValueAt(i, attribute);
            }
            valuesByHousehold.put(attribute, values);
            Iterator<Integer> iterator = municipalities.iterator();
            while(iterator.hasNext()){
                Integer municipality = iterator.next();
                double[] dummy = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(), 1.);
                weightsByMun.put(municipality, dummy);
                double[] dummy1 = SiloUtil.createArrayWithValue(frequencyMatrix.getRowCount(), 1.);
                minWeightsByMun.put(municipality,dummy1);
                if (totalMunicipality.containsKey(municipality)){
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


        //For each attribute at the region level (landkreise)
        for (String attribute : attributesCounty){
            factor = totalCounty.get(attribute) / weightedSum0;
            Iterator<Integer> iterator = municipalities.iterator();
            weightedSum0 = 0;
            while(iterator.hasNext()) {
                Integer municipality = iterator.next();
                double factor1 = factor;


                double[] m1 = weightsByMun.get(municipality);
                int[] m2 = valuesByHousehold.get(attribute);
                double[] m3 = new double[m1.length];
                //IntStream.range(0, m1.length).parallel().forEach(id -> m3[id] = m1[id] * m2[id]);


                IntStream.range(0, m1.length).forEach(id -> m3[id] = multiplyIfNotZero(m1[id], m2[id], factor1));

              /*  double[] result = Arrays.stream(m1).map(r -> IntStream.range(0, m2.length).mapToDouble(j -> r[j] * m2[j]).toArray()).toArray(double[]::new);

                double[] we = Arrays.stream(m1).map(r -> IntStream.range(0,m1.length).mapToDouble(i -> IntStream.range(0, m2.length).mapToDouble(j -> r[j] * m2[i]).toArray()).toArray(double[]::new));


                //double[] result = Arrays.stream(previous).map(r -> IntStream.range(0, previous.length).mapToDouble(k -> r[k]));

                double[] updated = DoubleStream.of(weightsByMun.get(municipality)).map(d->d*factor1).toArray();*/

                //double[] newW = DoubleStream.of(weightsByMun.get(municipality)).map()


                weightsByMun.put(municipality, m3);
                weightedSum0 = weightedSum0 + SiloUtil.getWeightedSum(weightsByMun.get(municipality), valuesByHousehold.get(attribute));
            }
        }


        Long estimatedTime1 = System.nanoTime() - estimatedTime;

        logger.info("   Calculation time " + estimatedTime1);

    }

    public double multiplyIfNotZero(double x, double y, double f){
        if (y==0){
            return x;
        } else {
            return x*f;
        }
    }
}
