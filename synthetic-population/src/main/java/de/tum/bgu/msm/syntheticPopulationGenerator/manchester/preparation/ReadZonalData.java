package de.tum.bgu.msm.syntheticPopulationGenerator.manchester.preparation;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.common.matrix.Matrix;
import de.tum.bgu.msm.data.dwelling.DwellingType;
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
        readZones();//LSOA = TAZs
        readDistanceMatrix();
        readCommuteFlow();
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
            int city = (int) PropertiesSynPop.get().main.selectedMunicipalities.getValueAt(row, "cityID");
            municipalities.add(city);
            if (PropertiesSynPop.get().main.twoGeographicalAreasIPU) {
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
            } else {
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
        Map<Integer, Map<DwellingType, Integer>> dwellingPriceByTypeAndZone = new HashMap<>();

        ArrayList<Integer> tazs = new ArrayList<>();
        ArrayList<Float> areas = new ArrayList<>();
        TableDataSet zoneAttributes = PropertiesSynPop.get().main.cellsMatrix;
        HashMap<Integer, HashMap<String, Float>> attributesZone = new HashMap<>();

        for (int i = 1; i <= zoneAttributes.getRowCount(); i++){
            int city = (int) zoneAttributes.getValueAt(i,"cityID");
            int taz = (int) zoneAttributes.getValueAt(i,"zoneID");
            float probability = zoneAttributes.getValueAt(i, "population");
            areas.add(zoneAttributes.getValueAt(i, "area"));
            //int capacitySchool = (int)zoneAttributes.getValueAt(i,"stu");
            float percentageVacantDwellings = zoneAttributes.getValueAt(i, "percentageVacantDwellings");
            //float averageIncome = zoneAttributes.getValueAt(i, "income");
            float households = zoneAttributes.getValueAt(i, "households");
            float primaryJobs = zoneAttributes.getValueAt(i, "pri");
            float secondaryJobs = zoneAttributes.getValueAt(i, "sec");
            float tertiaryJobs = zoneAttributes.getValueAt(i, "ter");
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
            /*Map<DwellingType, Integer> prices = new HashMap<>();
            prices.put(DefaultDwellingTypes.DefaultDwellingTypeImpl.SFA, priceSFA);
            prices.put(DefaultDwellingTypes.DefaultDwellingTypeImpl.SFD, priceSFD);
            prices.put(DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234, priceMF234);
            prices.put(DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus, priceMF5plus);
            dwellingPriceByTypeAndZone.put(taz,prices);
            *///schoolCapacity.put(taz,1,capacitySchool);

            HashMap<String, Float> Attributes = new HashMap<>();
            Attributes.put("percentageVacantDwelings", percentageVacantDwellings);
            //Attributes.put("income", averageIncome);
            Attributes.put("households", households);
            float totalJob = primaryJobs + secondaryJobs + tertiaryJobs;
            Attributes.put("pri", primaryJobs);
            Attributes.put("sec", secondaryJobs);
            Attributes.put("ter", tertiaryJobs);
            Attributes.put("tot",totalJob);
            attributesZone.put(taz, Attributes);
        }
        dataSetSynPop.setAreas(SiloUtil.convertArrayListToFloatArray(areas));
        dataSetSynPop.setProbabilityZone(probabilityZone);
        dataSetSynPop.setTazByMunicipality(cityTAZ);
        dataSetSynPop.setSchoolCapacity(schoolCapacity);
        dataSetSynPop.setTazs(tazs);
        dataSetSynPop.setTazIDs(tazs.stream().mapToInt(i -> i).toArray());
        dataSetSynPop.setTazAttributes(attributesZone);

    }

    private void readCommuteFlow(){
        //Read the skim matrix
        logger.info("   Starting to read CSV matrix");
        TableDataSet commuteFlow = SiloUtil.readCSVfile(PropertiesSynPop.get().main.commuteFlowFile);
        int[] externalNumbers = dataSetSynPop.getTazIDs();
        Matrix commuteFlowMatrix = new Matrix("mat1", "mat1", externalNumbers.length, externalNumbers.length);

        for (int row = 1; row <= commuteFlow.getRowCount(); row++){
            int origin = (int) commuteFlow.getValueAt(row, "homeId");
            int destination = (int) commuteFlow.getValueAt(row, "workId");
            float value = commuteFlow.getValueAt(row, "freq");
            commuteFlowMatrix.setValueAt(origin, destination, value);
        }

        commuteFlowMatrix.setExternalNumbersZeroBased(externalNumbers);
        dataSetSynPop.setCommuteFlowTazToTaz(commuteFlowMatrix);
        logger.info("   Read CSV matrix done");
    }

    private void readDistanceMatrix(){
        //Read the skim matrix
        logger.info("   Starting to read CSV matrix");
        TableDataSet distances = SiloUtil.readCSVfile(PropertiesSynPop.get().main.omxFileName);
        int[] externalNumbers = dataSetSynPop.getTazIDs();
        Matrix distanceMatrix = new Matrix("mat1", "mat1", externalNumbers.length, externalNumbers.length);

        for (int row = 1; row <= distances.getRowCount(); row++){
            int origin = (int) distances.getValueAt(row, "InputID");
            int destination = (int) distances.getValueAt(row, "TargetID");
            float value = distances.getValueAt(row, "Distance");
            distanceMatrix.setValueAt(origin, destination, value);
        }

        distanceMatrix.setExternalNumbersZeroBased(externalNumbers);
        dataSetSynPop.setDistanceTazToTaz(distanceMatrix);
        logger.info("   Read CSV matrix done");
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
            int posHBSchool = SiloUtil.findPositionInArray("HBSchool",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int length  = Integer.parseInt(lineElements[posId]);
                float hbw  = Float.parseFloat(lineElements[posHBW]);
                float hbschool  = Float.parseFloat(lineElements[posHBSchool]);
                frequencies.put(length,"HBW", hbw);
                frequencies.put(length, "HBSchool", hbschool);
            }


        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop job file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        dataSetSynPop.setTripLengthDistribution(frequencies);

    }
}
