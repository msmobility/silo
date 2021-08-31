package de.tum.bgu.msm.syntheticPopulationGenerator.bangkok.preparation;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.common.matrix.Matrix;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        readTripLengthFrequencyDistribution();
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
                counties.add(city);
                ArrayList<Integer> citiesInThisCounty = new ArrayList<>();
                citiesInThisCounty.add(city);
                municipalitiesByCounty.put(city, citiesInThisCounty);
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

    }


    private void readZones(){
        //TAZ attributes
        HashMap<Integer, int[]> cityTAZ = new HashMap<>();
        Map<Integer, Map<Integer, Float>> probabilityZone = new HashMap<>();
        Table<Integer, Integer, Integer> schoolCapacity = HashBasedTable.create();
        ArrayList<Integer> tazs = new ArrayList<>();
        ArrayList<Float> areas = new ArrayList<>();
        TableDataSet zoneAttributes = PropertiesSynPop.get().main.cellsMatrix;
        HashMap<Integer, HashMap<String, Float>> attributesZone = new HashMap<>();

        for (int i = 1; i <= zoneAttributes.getRowCount(); i++){
            int city = (int) zoneAttributes.getValueAt(i,"ID_city");
            int taz = (int) zoneAttributes.getValueAt(i,"ID_cell");
            float probability = zoneAttributes.getValueAt(i, "Population");
            areas.add(zoneAttributes.getValueAt(i, "area"));
            int capacitySchool = (int)zoneAttributes.getValueAt(i,"stu");
            float percentageVacantDwellings = zoneAttributes.getValueAt(i, "percentageVacantDwellings");
            float averageIncome = zoneAttributes.getValueAt(i, "income");
            float households = zoneAttributes.getValueAt(i, "households");
            float primaryJobs = zoneAttributes.getValueAt(i, "pri");
            float secondaryJobs = zoneAttributes.getValueAt(i, "sec");
            float tertiaryJobs = zoneAttributes.getValueAt(i, "ter");
            float percentVacantJobs = zoneAttributes.getValueAt(i, "percentVacantJobs");
            float ratioJobs = 1 + percentVacantJobs / 100;
            tazs.add(taz);
            int[] previousTaz = {taz};
            cityTAZ.put(city,previousTaz);
            Map<Integer, Float> probabilities = new HashMap<>();
            probabilities.put(taz, probability);
            probabilityZone.put(city, probabilities);
            schoolCapacity.put(taz,1, (int) (capacitySchool * ratioJobs));
            HashMap<String, Float> Attributes = new HashMap<>();
            Attributes.put("percentageVacantDwelings", percentageVacantDwellings);
            Attributes.put("income", averageIncome);
            Attributes.put("households", households);
            Attributes.put("pri", primaryJobs * ratioJobs);
            Attributes.put("sec", secondaryJobs * ratioJobs);
            Attributes.put("ter", tertiaryJobs * ratioJobs);
            attributesZone.put(city, Attributes);
        }
        dataSetSynPop.setAreas(SiloUtil.convertArrayListToFloatArray(areas));
        dataSetSynPop.setProbabilityZone(probabilityZone);
        dataSetSynPop.setTazByMunicipality(cityTAZ);
        dataSetSynPop.setSchoolCapacity(schoolCapacity);
        dataSetSynPop.setTazs(tazs);
        dataSetSynPop.setTazIDs(tazs.stream().mapToInt(i -> i).toArray());
        dataSetSynPop.setTazAttributes(attributesZone);

    }

    private void readDistanceMatrix(){
        //Read the skim matrix
        logger.info("   Starting to read OMX matrix");
        TableDataSet distances = SiloUtil.readCSVfile(PropertiesSynPop.get().main.omxFileName);
        int[] externalNumbers = dataSetSynPop.getCityIDs();
        Matrix distanceMatrix = new Matrix("mat1", "mat1", externalNumbers.length, externalNumbers.length);

        for (int row = 1; row <= distances.getRowCount(); row++){
            int origin = (int) distances.getValueAt(row, "InputID") - 1;
            int destination = (int) distances.getValueAt(row, "TargetID") - 1;
            float value = distances.getValueAt(row, "distance") / 1000;
            distanceMatrix.setValueAt(origin, destination, value);
        }

        float[] areas = dataSetSynPop.getAreas();
        for (int zone : externalNumbers){
            float intrazonal = (float) (Math.sqrt(areas[zone-1] / Math.PI) * 0.6);
            distanceMatrix.setValueAt(zone, zone, intrazonal);
        }
        distanceMatrix.setExternalNumbersZeroBased(externalNumbers);
        dataSetSynPop.setDistanceTazToTaz(distanceMatrix);
        logger.info("   Read OMX matrix");
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
            int posId = SiloUtil.findPositionInArray("km", header);
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
}
