package de.tum.bgu.msm.syntheticPopulationGenerator.manchester.preparation;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.common.matrix.Matrix;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.manchester.DataSetSynPopMCR;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.ManchesterPropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import omx.OmxFile;
import omx.OmxLookup;
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
        //MSOA = county, LSOA = city
        readCities();
        //OA = TAZs
        readZones();
        readLsoaDistanceMatrix();
        readOADistanceMatrix();
        readTripLengthFrequencyDistribution();
        //Manchester job assignment approach: base year commute flows LSOA-LSOA
        readCommuteFlow();
        readCarOwnershipMatrix();
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

        HashMap<Integer, HashMap<String, Float>> attributesLSOA = new HashMap<>();

        for (int row = 1; row <= PropertiesSynPop.get().main.selectedMunicipalities.getRowCount(); row++) {
            int city = (int) PropertiesSynPop.get().main.selectedMunicipalities.getValueAt(row, "lsoaID");
            float primaryJobs = PropertiesSynPop.get().main.selectedMunicipalities.getValueAt(row, "pri");
            float secondaryJobs = PropertiesSynPop.get().main.selectedMunicipalities.getValueAt(row, "sec");
            float tertiaryJobs = PropertiesSynPop.get().main.selectedMunicipalities.getValueAt(row, "ter");
            float percentageVacantDwellings = PropertiesSynPop.get().main.selectedMunicipalities.getValueAt(row, "percentageVacantDwellings");

            HashMap<String, Float> attributes = new HashMap<>();
            float totalJob = primaryJobs + secondaryJobs + tertiaryJobs;
            attributes.put("tot", totalJob);
            attributes.put("percentageVacantDwellings", percentageVacantDwellings);

            attributesLSOA.put(city, attributes);

            municipalities.add(city);
            if (PropertiesSynPop.get().main.twoGeographicalAreasIPU) {
                int county = (int) PropertiesSynPop.get().main.selectedMunicipalities.getValueAt(row, "msoaID");
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

        ((DataSetSynPopMCR)dataSetSynPop).setLsoaAttributes(attributesLSOA);
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
        HashMap<Integer, Integer> tazCity = new HashMap<>();
        for (int i = 1; i <= zoneAttributes.getRowCount(); i++){
            int city = (int) zoneAttributes.getValueAt(i,"lsoaID");
            int taz = (int) zoneAttributes.getValueAt(i,"oaID");
            tazCity.put(taz,city);
            float probability = zoneAttributes.getValueAt(i, "population");

            int capacityPrimarySchool = (int)zoneAttributes.getValueAt(i,"primaryEdu");
            int capacitySecondarySchool = (int)zoneAttributes.getValueAt(i,"secondaryEdu");
            int capacityHigherEducation = (int)zoneAttributes.getValueAt(i,"higherEdu");

            float households = zoneAttributes.getValueAt(i, "households");
            float totEmpWeight = zoneAttributes.getValueAt(i, "totEmp_poiWeight");
            float wpPop = zoneAttributes.getValueAt(i, "wpPop_scaled2LsoaJob");

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
            schoolCapacity.put(taz,1,capacityPrimarySchool);
            schoolCapacity.put(taz,2,capacitySecondarySchool);
            schoolCapacity.put(taz,3,capacityHigherEducation);

            HashMap<String, Float> Attributes = new HashMap<>();
            Attributes.put("households", households);
            Attributes.put("tot",wpPop);
            attributesZone.put(taz, Attributes);
        }
        ((DataSetSynPopMCR)dataSetSynPop).setTazMunicipality(tazCity);
        dataSetSynPop.setAreas(SiloUtil.convertArrayListToFloatArray(areas));
        dataSetSynPop.setProbabilityZone(probabilityZone);
        dataSetSynPop.setTazByMunicipality(cityTAZ);
        dataSetSynPop.setSchoolCapacity(schoolCapacity);
        dataSetSynPop.setTazs(tazs);
        dataSetSynPop.setTazIDs(tazs.stream().mapToInt(i -> i).toArray());
        dataSetSynPop.setTazAttributes(attributesZone);

    }

    private void readCommuteFlow(){
        //Read the commuteFlow
        logger.info("   Starting to read CSV matrix for commute flow");
        TableDataSet commuteFlow = SiloUtil.readCSVfile(PropertiesSynPop.get().main.commuteFlowFile);
        int[] externalNumbers = dataSetSynPop.getTazIDs();
        Matrix commuteFlowMatrix = new Matrix("mat1", "mat1", externalNumbers.length, externalNumbers.length);

        for (int row = 1; row <= commuteFlow.getRowCount(); row++){
            int origin = (int) commuteFlow.getValueAt(row, "home_oaID");
            int destination = (int) commuteFlow.getValueAt(row, "work_oaID");
            float value = commuteFlow.getValueAt(row, "freq");
            commuteFlowMatrix.setValueAt(origin, destination, value);
        }

        commuteFlowMatrix.setExternalNumbersZeroBased(externalNumbers);
        dataSetSynPop.setCommuteFlowTazToTaz(commuteFlowMatrix);
        logger.info("   Read CSV matrix done");
    }

    private void readCarOwnershipMatrix(){
        //Read the commuteFlow
        logger.info("   Starting to read CSV matrix for car ownership");
        TableDataSet carOwnership = SiloUtil.readCSVfile(((ManchesterPropertiesSynPop)PropertiesSynPop.get().main).carOwnershipFile);
        Map<Integer, Map<Integer, Map<Integer,Float>>> probability = new HashMap<>();



        for (int row = 1; row <= carOwnership.getRowCount(); row++){
            int lsoaID = (int) carOwnership.getValueAt(row, "lsoaID");
            int hhsize = (int) carOwnership.getValueAt(row, "hhsizeCode");
            int car = (int) carOwnership.getValueAt(row, "carCode");
            float value = carOwnership.getValueAt(row, "Observation");

            probability.computeIfAbsent(lsoaID, k -> new HashMap<>())
                    .computeIfAbsent(hhsize, k -> new HashMap<>())
                    .put(car, value);
        }

        ((DataSetSynPopMCR)dataSetSynPop).setCarOwnershipProbabilityByHhsizeAndLSOA(probability);
        logger.info("   Read CSV matrix done");
    }

    private void readLsoaDistanceMatrix(){
        //Read the skim matrix
        logger.info("   Starting to read CSV matrix");
        TableDataSet distances = SiloUtil.readCSVfile(((ManchesterPropertiesSynPop)PropertiesSynPop.get().main).lsoaDistFileName);
        int[] externalNumbers = dataSetSynPop.getTazIDs();
        Matrix distanceMatrix = new Matrix("mat1", "mat1", externalNumbers.length, externalNumbers.length);

        for (int row = 1; row <= distances.getRowCount(); row++){
            int origin = (int) distances.getValueAt(row, "InputID");
            int destination = (int) distances.getValueAt(row, "TargetID");
            float value = distances.getValueAt(row, "Distance");
            distanceMatrix.setValueAt(origin, destination, value);
        }

        distanceMatrix.setExternalNumbersZeroBased(externalNumbers);
        ((DataSetSynPopMCR)dataSetSynPop).setDistanceLSOAToLSOA(distanceMatrix);
        logger.info("   Read CSV matrix done");
    }

    private void readOADistanceMatrix(){
        //Read the skim matrix
        logger.info("   Starting to read OMX matrix");
        OmxFile travelTimeOmx = new OmxFile(((ManchesterPropertiesSynPop)PropertiesSynPop.get().main).oaDistFileName);
        travelTimeOmx.openReadOnly();
        Matrix distanceMatrix = SiloUtil.convertOmxToMatrix(travelTimeOmx.getMatrix("dist"));
        OmxLookup omxLookUp = travelTimeOmx.getLookup("zone");
        int[] externalNumbers = (int[]) omxLookUp.getLookup();
        distanceMatrix.setExternalNumbersZeroBased(externalNumbers);
        for (int i = 1; i <= distanceMatrix.getRowCount(); i++){
            for (int j = 1; j <= distanceMatrix.getColumnCount(); j++){
                distanceMatrix.setValueAt(i,j, distanceMatrix.getValueAt(i,j)/1000);//m to km
            }
        }
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
            int posTertiary = SiloUtil.findPositionInArray("Tertiary",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int length  = Integer.parseInt(lineElements[posId]);
                float hbw  = Float.parseFloat(lineElements[posHBW]);
                float tertiary  = Float.parseFloat(lineElements[posTertiary]);
                frequencies.put(length,"HBW", hbw);
                frequencies.put(length, "Tertiary", tertiary);
            }


        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop job file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        dataSetSynPop.setTripLengthDistribution(frequencies);

    }
}
