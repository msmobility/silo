package de.tum.bgu.msm.syntheticPopulationGenerator.bangkok.allocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.MicroDataManager;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.*;

public class GenerateVacantDwellings {

    private static final Logger logger = Logger.getLogger(GenerateVacantDwellings.class);

    private final DataSetSynPop dataSetSynPop;
    private final MicroDataManager microDataManager;
    private Map<Integer, Map<Integer, Float>> ddQuality;
    private float ddTypeProbOfSFAorSFD;
    private float ddTypeProbOfMF234orMF5plus;
    private Map<Integer, Float> probVacantBuildingSize;
    private Map<Integer, Float> probVacantFloor;
    private double[] probabilityTAZ;
    private double sumTAZs;
    private int[] ids;
    private int[] idTAZs;
    private int highestDwellingIdInUse;
    private final DataContainer dataContainer;
    private RealEstateDataManager realEstateData;
    private Map<Integer, List<Dwelling>> occupiedDwellings = new HashMap<>();


    public GenerateVacantDwellings(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataContainer = dataContainer;
        this.dataSetSynPop = dataSetSynPop;
        microDataManager = new MicroDataManager(dataSetSynPop);
    }

    public void run(){
        logger.info("   Running module: household, person and dwelling generation");
        initializeQualityAndIncomeDistributions();
        generateVacantDwellings();
    }


    private void initializeQualityAndIncomeDistributions(){

        realEstateData = dataContainer.getRealEstateDataManager();
        for (Dwelling dd: realEstateData.getDwellings()){
            int municipality = (int) PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(dd.getZoneId(),"ID_city");
            updateQualityMap(municipality, dd.getYearBuilt(), dd.getQuality());
        }
        highestDwellingIdInUse = 0;

        for (Dwelling dd: realEstateData.getDwellings()) {
            highestDwellingIdInUse = Math.max(highestDwellingIdInUse, dd.getId());
            int zone = dd.getZoneId();
            if (occupiedDwellings.get(zone) != null){
                occupiedDwellings.get(zone).add(dd);
            } else {
                List<Dwelling> dwe = new ArrayList<>();
                dwe.add(dd);
                occupiedDwellings.put(zone, dwe);
            }
        }


    }

    private void generateVacantDwellings(){

        //for (int municipality = 1; municipality < 3; municipality++){
        for (int municipality : dataSetSynPop.getMunicipalities()){
            float percentageVacantDwellings = dataSetSynPop.getTazAttributes().get(municipality).get("percentageVacantDwelings");
            int vacantDwellings = (int) (percentageVacantDwellings * dataSetSynPop.getTazAttributes().get(municipality).get("households") / 100);
            List<Dwelling> dwellingForCopy = occupiedDwellings.get(municipality);
            int vacantCounter = 0;
            if (dwellingForCopy != null){
                Collections.shuffle(dwellingForCopy);
                for (int draw = 0; draw < vacantDwellings; draw++){
                    int tazSelected = municipality;
                    int newDdId = highestDwellingIdInUse++;
                    Dwelling idDwellingToCopy = dwellingForCopy.get(draw);
                    int floorSpace = idDwellingToCopy.getFloorSpace();
                    int year = idDwellingToCopy.getYearBuilt();
                    DwellingType type = idDwellingToCopy.getType();
                    int bedRooms = idDwellingToCopy.getBedrooms();
                    int quality = idDwellingToCopy.getQuality();
                    int price = idDwellingToCopy.getPrice();
                    Dwelling dwell = DwellingUtils.getFactory().createDwelling(newDdId, tazSelected, null, -1, type, bedRooms, quality, price, year); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year
                    realEstateData.addDwelling(dwell);
                    dwell.setUsage(DwellingUsage.VACANT);
                    dwell.setFloorSpace(floorSpace);
                    vacantCounter++;
                }
            }
            logger.info("Municipality " + municipality + ". Generated vacant dwellings: " + vacantCounter);
        }
    }


    private void initializeVacantDwellingData(int municipality){

        probVacantFloor = new HashMap<>();
        for (int floor : PropertiesSynPop.get().main.sizeBracketsDwelling) {
            probVacantFloor.put(floor, PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "vacantDwellings" + floor));
        }
        probVacantBuildingSize = new HashMap<>();
        for (int year : PropertiesSynPop.get().main.yearBracketsDwelling){
            int sizeYear = year;
            String label = "vacantSmallDwellings" + year;
            probVacantBuildingSize.put(sizeYear, PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, label));
            sizeYear = year + 10;
            label = "vacantMediumDwellings" + year;
            probVacantBuildingSize.put(sizeYear, PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, label));

        }
        ddTypeProbOfSFAorSFD = PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality,"ddProbSFAorSFD");
        ddTypeProbOfMF234orMF5plus = PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality,"ddProbMF234orMF5plus");
        probabilityTAZ = new double[dataSetSynPop.getProbabilityZone().get(municipality).keySet().size()];
        sumTAZs = 0;
        probabilityTAZ = dataSetSynPop.getProbabilityZone().get(municipality).values().stream().mapToDouble(Number::doubleValue).toArray();
        for (int i = 1; i < probabilityTAZ.length; i++){
            probabilityTAZ[i] = probabilityTAZ[i] + probabilityTAZ[i-1];
        }
        idTAZs = dataSetSynPop.getProbabilityZone().get(municipality).keySet().stream().mapToInt(Number::intValue).toArray();
    }


    private void updateQualityMap(int municipality, int year, int quality){

        int yearBracket = microDataManager.dwellingYearBracket(year);
        int key = yearBracket * 10000000 + municipality;
        if (ddQuality != null) {
            if (ddQuality.get(key) != null) {
                Map<Integer, Float> qualities = ddQuality.get(key);
                if (qualities.get(quality) != null) {
                    float prev = 1 + qualities.get(quality);
                    qualities.put(quality, prev);
                } else {
                    qualities.put(quality, 1f);
                }
                ddQuality.put(key, qualities);
            } else {
                Map<Integer, Float> qualities = new HashMap<>();
                qualities.put(quality, 1f);
                ddQuality.put(key, qualities);
            }
        } else {
            ddQuality = new HashMap<>();
            Map<Integer, Float> qualities = new HashMap<>();
            qualities.put(quality, 1f);
            ddQuality.put(key, qualities);
        }
    }


    private int[] selectMultipleTAZ(int selections){

        int[] selected;
        selected = new int[selections];
        int completed = 0;
        for (int iteration = 0; iteration < 100; iteration++){
            int m = selections - completed;
            double[] randomChoices = new double[m];
            for (int k = 0; k < randomChoices.length; k++) {
                randomChoices[k] = SiloUtil.getRandomNumberAsDouble();
            }
            Arrays.sort(randomChoices);

            int p = 0;
            double cumulative = probabilityTAZ[p];
            for (double randomNumber : randomChoices){
                while (randomNumber > cumulative && p < probabilityTAZ.length - 1) {
                    p++;
                    cumulative += probabilityTAZ[p];
                }
                if (probabilityTAZ[p] > 0) {
                    selected[completed] = idTAZs[p];
                    completed++;
                }
            }
        }
        return selected;
    }


    private int extractYear(int buildingYear){

        int year = 0;
        if (buildingYear < 10){
            year = buildingYear;
        } else {
            year = buildingYear - 10;
        }
        return year;
    }


    private DwellingType extractDwellingType (int buildingYear, float ddType1Prob, float ddType3Prob){

        DwellingType type = DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234;

        if (buildingYear < 10){
            if (SiloUtil.getRandomNumberAsFloat() < ddType1Prob){
                type = DefaultDwellingTypes.DefaultDwellingTypeImpl.SFD;
            } else {
                type = DefaultDwellingTypes.DefaultDwellingTypeImpl.SFA;
            }
        } else {
            if (SiloUtil.getRandomNumberAsFloat() < ddType3Prob){
                type = DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus;
            }
        }


        return type;
    }


    private int selectQualityVacant(int municipality, int year){
        int result = 0;
        if (ddQuality.get(year * 10000000 + municipality) == null) {
            HashMap<Integer, Float> qualities = new HashMap<>();
            for (int quality = 1; quality <= PropertiesSynPop.get().main.numberofQualityLevels; quality++){
                qualities.put(quality, 1f);
            }
            ddQuality.put(year * 10000000 + municipality, qualities);
        }
        result = SiloUtil.select(ddQuality.get(year * 10000000 + municipality));
        return result;
    }


}
