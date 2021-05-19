package de.tum.bgu.msm.syntheticPopulation;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.models.realEstate.construction.DefaultConstructionDemandStrategy;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

public class ipuTest {

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
    private HashMap<Integer, int[]> municipalitiesByCounty;
    static Logger logger = Logger.getLogger(ipuTest.class);

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
        System.arraycopy(attributesMunicipality1, 1, attributesMunicipality, 0, attributesMunicipality.length);
        System.arraycopy(attributesCounty1, 1, attributesCounty, 0, attributesCounty.length);
        ArrayList<Integer> municipalities = new ArrayList<>();
        ArrayList<Integer> counties = new ArrayList<>();
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
                    int[] citiesInThisCounty = municipalitiesByCounty.get(county);
                    int[] expandedCityList = SiloUtil.expandArrayByOneElement(citiesInThisCounty, city);
                    municipalitiesByCounty.put(county, expandedCityList);
                } else {
                    municipalitiesByCounty.put(county, new int[]{city});
                }
            }
        }
        cityID = SiloUtil.convertArrayListToIntArray(municipalities);
        countyID = SiloUtil.convertArrayListToIntArray(counties);

    }

    @Ignore("not provided input data")@Test
    public void testModelOne() throws ScriptException {

        long startTime = System.nanoTime();

        int[] microDataIds = frequencyMatrix.getColumnAsInt("ID");
        int[] nonZeroIds = frequencyMatrix.getColumnAsInt("ID");
        frequencyMatrix.buildIndex(frequencyMatrix.getColumnPosition("ID"));

        //Create the collapsed version of the frequency matrix(common for all)
        TableDataSet nonZero = new TableDataSet();
        TableDataSet nonZeroSize = new TableDataSet();
        nonZero.appendColumn(nonZeroIds,"IDnonZero");
        int[] dummy0 = {0,0};
        nonZeroSize.appendColumn(dummy0,"IDnonZero");
        for (int attribute = 0; attribute < attributesCounty.length; attribute++) {
            int[] nonZeroVector = new int[microDataIds.length];
            int[] sumNonZero = {0, 0};
            for (int row = 1; row < microDataIds.length + 1; row++) {
                if (frequencyMatrix.getValueAt(row, attributesCounty[attribute]) != 0) {
                    nonZeroVector[sumNonZero[0]] = row;
                    sumNonZero[0] = sumNonZero[0] + 1;
                }
            }
            nonZero.appendColumn(nonZeroVector, attributesCounty[attribute]);
            nonZeroSize.appendColumn(sumNonZero, attributesCounty[attribute]);
        }
        for(int attribute = 0; attribute < attributesMunicipality.length; attribute++) {
            int[] nonZeroVector = new int[microDataIds.length];
            int[] sumNonZero = {0, 0};
            for (int row = 1; row < microDataIds.length + 1; row++) {
                if (frequencyMatrix.getValueAt(row, attributesMunicipality[attribute]) != 0) {
                    nonZeroVector[sumNonZero[0]] = row;
                    sumNonZero[0] = sumNonZero[0] + 1;
                }
            }
            nonZero.appendColumn(nonZeroVector, attributesMunicipality[attribute]);
            nonZeroSize.appendColumn(sumNonZero, attributesMunicipality[attribute]);
        }
        nonZero.buildIndex(nonZero.getColumnPosition("IDnonZero"));
        nonZeroSize.buildIndex(nonZeroSize.getColumnPosition("IDnonZero"));


        //Create the weights table (for all the municipalities)
        TableDataSet weightsMatrix = new TableDataSet();
        weightsMatrix.appendColumn(microDataIds,"ID");


        //Create the errors table (for all the municipalities, by attribute)
        TableDataSet errorsMatrix = new TableDataSet();
        errorsMatrix.appendColumn(cityID,"ID_city");
        for (int attribute = 0; attribute < attributesMunicipality.length; attribute++){
            double[] dummy2 = SiloUtil.createArrayWithValue(cityID.length,1.0);
            errorsMatrix.appendColumn(dummy2, attributesMunicipality[attribute]);
        }
        TableDataSet errorsMatrixRegion = new TableDataSet();
        errorsMatrixRegion.appendColumn(countyID,"ID_county");
        for (int attribute = 0; attribute < attributesCounty.length; attribute++){
            double[] dummy2 = SiloUtil.createArrayWithValue(countyID.length,1.0);
            errorsMatrixRegion.appendColumn(dummy2, attributesCounty[attribute]);
        }
        errorsMatrixRegion.buildIndex(errorsMatrixRegion.getColumnPosition("ID_county"));
        errorsMatrix.buildIndex(errorsMatrix.getColumnPosition("ID_city"));

        long estimatedTime = System.nanoTime() - startTime;
        logger.info("Prep time: " + estimatedTime);


        int regionID = 199053;
        int[] municipalitiesID = municipalitiesByCounty.get(regionID);
        float factor = 0f;
        int position = 0;

        String[] attributesHouseholdList = attributesMunicipality; //List of attributes at the household level (Gemeinden).
        String[] attributesRegionList = attributesCounty; //List of attributes at the region level (Landkreise).
        TableDataSet microDataMatrix = new TableDataSet(); //Frequency matrix obtained from the micro data.
        microDataMatrix = frequencyMatrix;
        TableDataSet collapsedMicroData = new TableDataSet(); //List of values different than zero, per attribute, from microdata
        collapsedMicroData = nonZero;
        TableDataSet lengthMicroData = new TableDataSet(); //Number of values different than zero, per attribute, from microdata
        lengthMicroData = nonZeroSize;

        //weights: TableDataSet with (one + number of municipalities) columns, the ID of the household from microData and the weights for the municipalities of the county
        TableDataSet weights = new TableDataSet();
        weights.appendColumn(microDataIds,"ID");
        for (int municipality = 0; municipality < municipalitiesID.length; municipality++){
            double[] dummy20 = SiloUtil.createArrayWithValue(microDataMatrix.getRowCount(),1.0);
            weights.appendColumn(dummy20, Integer.toString(municipalitiesID[municipality])); //the column label is the municipality cityID
        }
        weights.buildIndex(weights.getColumnPosition("ID"));
        TableDataSet minWeights = new TableDataSet();
        minWeights.appendColumn(microDataIds,"ID");
        for (int municipality = 0; municipality < municipalitiesID.length; municipality++){
            double[] dummy20 = SiloUtil.createArrayWithValue(microDataMatrix.getRowCount(),1.0);
            minWeights.appendColumn(dummy20, Integer.toString(municipalitiesID[municipality])); //the column label is the municipality cityID
        }
        minWeights.buildIndex(weights.getColumnPosition("ID"));


        //marginalsRegion: TableDataSet that contains in each column the marginal of a region attribute at the county level. Only one "real"" row
        TableDataSet marginalsRegion = new TableDataSet();
        int[] dummyw10 = {regionID,0};
        marginalsRegion.appendColumn(dummyw10,"ID_county");
        for(int attribute = 0; attribute < attributesRegionList.length; attribute++){
            float[] dummyw11 = {controlTotalsCount.getValueAt(
                    controlTotalsCount.getIndexedRowNumber(regionID),attributesRegionList[attribute]),0};
            marginalsRegion.appendColumn(dummyw11,attributesRegionList[attribute]);
        }
        marginalsRegion.buildIndex(marginalsRegion.getColumnPosition("ID_county"));


        //weighted sum and errors Region: TableDataSet that contains in each column the error of a region attribute at the county (landkreise) level
        TableDataSet errorsRegion = new TableDataSet();
        int[] dummy01 = {regionID,0};
        errorsRegion.appendColumn(dummy01,"ID_county");
        for (int attribute = 0; attribute < attributesRegionList.length; attribute++){
            float[] dummyQ2 = {0,0};
            errorsRegion.appendColumn(dummyQ2,attributesRegionList[attribute]);
        }
        errorsRegion.buildIndex(errorsRegion.getColumnPosition("ID_county"));

        //For each attribute at the region level (landkreise)
        for (int attribute = 0; attribute < attributesRegionList.length; attribute++) {
            float weighted_sum = 0f;
            for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                weighted_sum = weighted_sum +
                        SiloUtil.getWeightedSum(weights.getColumnAsDouble(Integer.toString(municipalitiesID[municipality])),
                                microDataMatrix.getColumnAsFloat(attributesRegionList[attribute]),
                                collapsedMicroData.getColumnAsInt(attributesRegionList[attribute]),
                                (int) lengthMicroData.getValueAt(1, attributesRegionList[attribute]));
            }
            factor = marginalsRegion.getIndexedValueAt(regionID, attributesRegionList[attribute]) /
                    weighted_sum;
            for (int municipality = 0; municipality < municipalitiesID.length; municipality++) {
                for (int row = 0; row < lengthMicroData.getValueAt(1, attributesRegionList[attribute]); row++) {
                    position = (int) collapsedMicroData.getIndexedValueAt(nonZeroIds[row], attributesRegionList[attribute]);
                    float previous_weight = weights.getValueAt(position, Integer.toString(municipalitiesID[municipality]));
                    weights.setValueAt(position, Integer.toString(municipalitiesID[municipality]), factor * previous_weight);
                }
            }
        }

        Long estimatedTime1 = System.nanoTime() - estimatedTime;

        logger.info("   Calculation time " + estimatedTime1);


    }

}
