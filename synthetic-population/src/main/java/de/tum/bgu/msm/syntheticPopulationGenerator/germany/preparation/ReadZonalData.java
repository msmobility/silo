package de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.common.matrix.Matrix;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import omx.OmxFile;
import omx.OmxLookup;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        readTripLengthFrequencyDistribution();
        readTripLengthMatrix();
    }

    private void readCities() {
        int[] cityID;
        int[] countyID;
        HashMap<Integer, ArrayList> municipalitiesByCounty;
        //List of municipalities and counties that are used for IPU and allocation
        ArrayList<Integer> municipalities = new ArrayList<>();
        ArrayList<Integer> counties = new ArrayList<>();
        municipalitiesByCounty = new HashMap<>();
        ArrayList<Integer> municipalitiesWithZero = new ArrayList<>();
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
        dataSetSynPop.setMunicipalitiesWithZeroPopulation(municipalitiesWithZero);

        if (PropertiesSynPop.get().main.boroughIPU) {
            HashMap<Integer, ArrayList> boroughsByCounty = new HashMap<>();
            ArrayList<Integer> boroughs = new ArrayList<>();
            ArrayList<Integer> countieswithBoroughs = new ArrayList<>();
            for (int row = 1; row <= PropertiesSynPop.get().main.selectedBoroughs.getRowCount(); row++) {
                if (PropertiesSynPop.get().main.selectedBoroughs.getValueAt(row, "Select") == 1f) {
                    int borough = (int) PropertiesSynPop.get().main.selectedBoroughs.getValueAt(row, "ID_borough");
                    int county = (int) PropertiesSynPop.get().main.selectedBoroughs.getValueAt(row, "ID_county");
                    boroughs.add(borough);
                    if (boroughsByCounty.containsKey(county)) {
                        ArrayList<Integer> boroughsInThisCity = boroughsByCounty.get(county);
                        boroughsInThisCity.add(borough);
                        boroughsByCounty.put(county, boroughsInThisCity);
                    } else {
                        ArrayList<Integer> boroughsInThisCity = new ArrayList<>();
                        boroughsInThisCity.add(borough);
                        boroughsByCounty.put(county, boroughsInThisCity);
                        countieswithBoroughs.add(county);
                    }
                }
            }
            dataSetSynPop.setBoroughsByCounty(boroughsByCounty);
            dataSetSynPop.setBoroughs(boroughs);
        }
    }


    private void readZones(){
        //TAZ attributes
        logger.info("   Started to read TAZ 100 by 100 m");
        HashMap<Integer, int[]> cityTAZ = new HashMap<>();
        Map<Integer, Map<Integer, Float>> probabilityZone = new HashMap<>();
        Table<Integer, Integer, Integer> schoolCapacity = HashBasedTable.create();
        Table<Integer, String, Integer> zoneCoordinates = HashBasedTable.create();
        Map<Integer, Integer> universityByZone = new HashMap<>();
        ArrayList<Integer> tazs = new ArrayList<>();
        TableDataSet zoneAttributes;
        if (!PropertiesSynPop.get().main.boroughIPU){
            zoneAttributes = PropertiesSynPop.get().main.cellsMatrix;
        } else {
            zoneAttributes = PropertiesSynPop.get().main.cellsMatrixBoroughs;
        }
        for (int i = 1; i <= zoneAttributes.getRowCount(); i++){
            int city = (int) zoneAttributes.getValueAt(i,"ID_borough");
            int taz = (int) zoneAttributes.getValueAt(i,"ID_cell");
            float probability = zoneAttributes.getValueAt(i, "population");
            int capacityPrimary = (int)zoneAttributes.getValueAt(i,"capacityPrimary");
            int capacitySecondary = (int)zoneAttributes.getValueAt(i,"capacitySecondary");
            int capacityTertiary = (int)zoneAttributes.getValueAt(i,"capacityTertiary");
            int coordX = (int)zoneAttributes.getValueAt(i,"coordX");
            int coordY = (int)zoneAttributes.getValueAt(i,"coordY");
            if (!tazs.contains(taz)) {
                tazs.add(taz);
            }
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
            schoolCapacity.put(taz,1,capacityPrimary);
            schoolCapacity.put(taz, 2, capacitySecondary);
            schoolCapacity.put(taz, 3, capacityTertiary);
            if (!universityByZone.isEmpty()){
                if (universityByZone.containsKey(taz)){
                    int capacity = capacityTertiary + universityByZone.get(taz);
                    universityByZone.put(taz, capacity);
                } else {
                    universityByZone.put(taz, capacityTertiary);
                }
            } else {
                universityByZone.putIfAbsent(taz, capacityTertiary);
            }
            zoneCoordinates.put(taz,"coordX",coordX);
            zoneCoordinates.put(taz,"coordY",coordY);
            if (isPowerOfFour(i)) {
                logger.info("   Read " + i + " TAZ 100 by 100 m");
            }
        }
        dataSetSynPop.setProbabilityZone(probabilityZone);
        dataSetSynPop.setTazByMunicipality(cityTAZ);
        dataSetSynPop.setSchoolCapacity(schoolCapacity);
        dataSetSynPop.setTazs(tazs);
        dataSetSynPop.setTazIDs(tazs.stream().mapToInt(i -> i).toArray());
        dataSetSynPop.setZoneCoordinates(zoneCoordinates);
        logger.info("   Finished to read TAZ 100 by 100 m");
    }

    private void readTripLengthMatrix(){
        //Read the skim matrix
        logger.info("   Starting to read OMX matrix");
        OmxFile tripLengthOmx = new OmxFile(PropertiesSynPop.get().main.omxFileName);
        tripLengthOmx.openReadOnly();
        Matrix tripLengthMatrix = SiloUtil.convertOmxToMatrix(tripLengthOmx.getMatrix("mat1"));
        OmxLookup omxLookUp = tripLengthOmx.getLookup("lookup1");
        int[] externalNumbers = (int[]) omxLookUp.getLookup();
        tripLengthMatrix.setExternalNumbersZeroBased(externalNumbers);
        Matrix tripLengthProbabilityMatrix = new Matrix(externalNumbers.length, externalNumbers.length);
        tripLengthProbabilityMatrix.setExternalNumbersZeroBased(externalNumbers);
        for (int i = 1; i <= tripLengthMatrix.getRowCount(); i++){
            for (int j = 1; j <= tripLengthMatrix.getColumnCount(); j++){
                //UNIT:kilometers
                double distanceInKm = tripLengthMatrix.getValueAt(i,j)/1000;
                tripLengthMatrix.setValueAt(i,j, (float) distanceInKm);
                float utility = 0;
                if (distanceInKm < 200){
                    utility = dataSetSynPop.getTripLengthDistribution().get((int) distanceInKm, "HBW");
                }
                tripLengthProbabilityMatrix.setValueAt(i,j,utility);
            }
        }
        dataSetSynPop.setDistanceTazToTaz(tripLengthMatrix);
        dataSetSynPop.setDistanceUtility(tripLengthProbabilityMatrix);
        logger.info("Read OMX matrix");
    }


    private void readTripLengthFrequencyDistribution(){
        logger.info("   Starting to read trip length frequency distributions");
        String fileName = PropertiesSynPop.get().main.tripLengthDistributionFileName;
        String recString = "";
        Table<Integer, String, Float> frequencies = HashBasedTable.create();
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("min", header);
            int posHBW = SiloUtil.findPositionInArray("HBW",header);
            int posPrimarySecondary = SiloUtil.findPositionInArray("Primary",header);
            int posTertiary = SiloUtil.findPositionInArray("Tertiary",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int length  = Integer.parseInt(lineElements[posId]);
                float hbw  = Float.parseFloat(lineElements[posHBW]);
                float primary  = Float.parseFloat(lineElements[posPrimarySecondary]);
                float tertiary  = Float.parseFloat(lineElements[posTertiary]);
                frequencies.put(length,"HBW", hbw);
                frequencies.put(length, "Primary", primary);
                frequencies.put(length, "Tertiary", tertiary);
            }


        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop job file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        dataSetSynPop.setTripLengthDistribution(frequencies);

    }

    private static boolean isPowerOfFour(int number){
        double pow = Math.pow(number, 0.25);
        if (pow - Math.floor(pow) == 0){
            return  true;
        } else {
            return false;
        }

    }
}
