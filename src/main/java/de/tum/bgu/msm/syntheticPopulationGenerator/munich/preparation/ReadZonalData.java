package de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.DwellingType;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import omx.OmxFile;
import omx.OmxLookup;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReadZonalData {

    private static final Logger logger = Logger.getLogger(ReadZonalData.class);

    private final DataSetSynPop dataSetSynPop;

    public ReadZonalData(DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;

    }

    public void run() {
        readCities();
        readZones();
        readDistanceMatrix();
    }

    private void readCities() {
        int[] cityID;
        int[] countyID;
        HashMap<Integer, ArrayList> municipalitiesByCounty;
        //List of municipalities and counties that are used for IPU and allocation
        ArrayList<Integer> municipalities = new ArrayList<>();
        ArrayList<Integer> counties = new ArrayList<>();
        municipalitiesByCounty = new HashMap<>();
        for (int row = 1; row <= PropertiesSynPop.get().main.selectedMunicipalities.getRowCount(); row++) {
            if (PropertiesSynPop.get().main.selectedMunicipalities.getValueAt(row, "Select") == 1f) {
                int city = (int) PropertiesSynPop.get().main.selectedMunicipalities.getValueAt(row, "ID_city");
                municipalities.add(city);
                int county = (int) PropertiesSynPop.get().main.selectedMunicipalities.getValueAt(row, "ID_county");
                if (!SiloUtil.containsElement(counties, county)) {
                    counties.add(county);
                }
                if (municipalitiesByCounty.containsKey(county)) {
                    ArrayList<Integer> citiesInThisCounty = municipalitiesByCounty.get(county);
                    citiesInThisCounty.add(city);
                    municipalitiesByCounty.put(county, citiesInThisCounty);
                } else {
                    ArrayList<Integer> citiesInThisCounty = new ArrayList<>();
                    citiesInThisCounty.add(city);
                    municipalitiesByCounty.put(county, citiesInThisCounty);
                }
            }
        }
        cityID = SiloUtil.convertArrayListToIntArray(municipalities);
        countyID = SiloUtil.convertArrayListToIntArray(counties);
        dataSetSynPop.setCityIDs(cityID);
        dataSetSynPop.setCountyIDs(countyID);
        dataSetSynPop.setMunicipalities(municipalities);
        dataSetSynPop.setCounties(counties);
        dataSetSynPop.setMunicipalitiesByCounty(municipalitiesByCounty);

        if (PropertiesSynPop.get().main.boroughIPU) {
            HashMap<Integer, ArrayList> boroughsByCity = new HashMap<>();
            ArrayList<Integer> boroughs = new ArrayList<>();
            for (int row = 1; row <= PropertiesSynPop.get().main.selectedBoroughs.getRowCount(); row++) {
                if (PropertiesSynPop.get().main.selectedBoroughs.getValueAt(row, "Select") == 1f) {
                    int borough = (int) PropertiesSynPop.get().main.selectedBoroughs.getValueAt(row, "ID_borough");
                    int city = (int) PropertiesSynPop.get().main.selectedBoroughs.getValueAt(row, "ID_city");
                    boroughs.add(borough);
                    if (boroughsByCity.containsKey(city)) {
                        ArrayList<Integer> boroughsInThisCity = boroughsByCity.get(city);
                        boroughsInThisCity.add(borough);
                        boroughsByCity.put(city, boroughsInThisCity);
                    } else {
                        ArrayList<Integer> boroughsInThisCity = new ArrayList<>();
                        boroughsInThisCity.add(borough);
                        boroughsByCity.put(city, boroughsInThisCity);
                    }
                }
            }
            dataSetSynPop.setBoroughsByCity(boroughsByCity);
            dataSetSynPop.setBoroughs(boroughs);
        }
    }


    private void readZones(){
        //TAZ attributes
        HashMap<Integer, int[]> cityTAZ = new HashMap<>();
        Map<Integer, Map<Integer, Float>> probabilityZone = new HashMap<>();
        Map<Integer, Map<DwellingType, Integer>> dwellingPriceByTypeAndZone = new HashMap<>();
        Table<Integer, Integer, Integer> schoolCapacity = HashBasedTable.create();
        for (int i = 1; i <= PropertiesSynPop.get().main.cellsMatrix.getRowCount(); i++){
            int city = (int) PropertiesSynPop.get().main.cellsMatrix.getValueAt(i,"ID_city");
            int taz = (int) PropertiesSynPop.get().main.cellsMatrix.getValueAt(i,"ID_cell");
            float probability = PropertiesSynPop.get().main.cellsMatrix.getValueAt(i, "population");
            int priceSFA = (int) PropertiesSynPop.get().main.cellsMatrix.getValueAt(i,"SFA");
            int priceSFD = (int) PropertiesSynPop.get().main.cellsMatrix.getValueAt(i,"SFD");
            int priceMF234 = (int) PropertiesSynPop.get().main.cellsMatrix.getValueAt(i,"MF234");
            int priceMF5plus = (int) PropertiesSynPop.get().main.cellsMatrix.getValueAt(i,"MF5plus");
            int capacityPrimary = (int)PropertiesSynPop.get().main.cellsMatrix.getValueAt(i,"capacityPrimary");
            int capacitySecondary = (int)PropertiesSynPop.get().main.cellsMatrix.getValueAt(i,"capacitySecondary");
            int capacityTertiary = (int)PropertiesSynPop.get().main.cellsMatrix.getValueAt(i,"capacityTertiary");
            if (cityTAZ.containsKey(city)){
                int[] previousTaz = cityTAZ.get(city);
                previousTaz = SiloUtil.expandArrayByOneElement(previousTaz, taz);
                cityTAZ.put(city, previousTaz);
                Map<Integer, Float> probabilities = probabilityZone.get(city);
                probabilities.put(taz, probability);
            } else {
                int[] previousTaz = {taz};
                cityTAZ.put(city,previousTaz);
                Map<Integer, Float> probabilities = new HashMap<>();
                probabilities.put(taz, probability);
                probabilityZone.put(city, probabilities);
            }
            Map<DwellingType, Integer> prices = new HashMap<>();
            prices.put(DwellingType.SFA, priceSFA);
            prices.put(DwellingType.SFD, priceSFD);
            prices.put(DwellingType.MF234, priceMF234);
            prices.put(DwellingType.MF5plus, priceMF5plus);
            dwellingPriceByTypeAndZone.put(taz,prices);
            schoolCapacity.put(taz,1,capacityPrimary);
            schoolCapacity.put(taz, 2, capacitySecondary);
            schoolCapacity.put(taz, 3, capacityTertiary);
        }
        dataSetSynPop.setProbabilityZone(probabilityZone);
        dataSetSynPop.setDwellingPriceByTypeAndZone(dwellingPriceByTypeAndZone);
        dataSetSynPop.setTazByMunicipality(cityTAZ);
        dataSetSynPop.setSchoolCapacity(schoolCapacity);
    }

    private void readDistanceMatrix(){
        //Read the skim matrix
        logger.info("   Starting to read OMX matrix");
        OmxFile travelTimeOmx = new OmxFile(PropertiesSynPop.get().main.omxFileName);
        travelTimeOmx.openReadOnly();
        Matrix distanceMatrix = SiloUtil.convertOmxToMatrix(travelTimeOmx.getMatrix("mat1"));
        OmxLookup omxLookUp = travelTimeOmx.getLookup("lookup1");
        int[] externalNumbers = (int[]) omxLookUp.getLookup();
        distanceMatrix.setExternalNumbersZeroBased(externalNumbers);
        for (int i = 1; i <= distanceMatrix.getRowCount(); i++){
            for (int j = 1; j <= distanceMatrix.getColumnCount(); j++){
                distanceMatrix.setValueAt(i,j, distanceMatrix.getValueAt(i,j)/1000);
            }
        }
        dataSetSynPop.setDistanceTazToTaz(distanceMatrix);
        logger.info("   Read OMX matrix");
    }
}
