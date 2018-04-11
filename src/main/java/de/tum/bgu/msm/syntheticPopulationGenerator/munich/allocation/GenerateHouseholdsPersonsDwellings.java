package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.MicroDataManager;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GenerateHouseholdsPersonsDwellings {

    private static final Logger logger = Logger.getLogger(GenerateHouseholdsPersonsDwellings.class);

    private final SiloDataContainer dataContainer;

    private final DataSetSynPop dataSetSynPop;
    private final MicroDataManager microDataManager;
    private int previousHouseholds;
    private int previousPersons;
    private Map<Integer, Map<Integer, Float>> ddQuality;
    private int totalHouseholds;
    private float ddTypeProbOfSFAorSFD;
    private float ddTypeProbOfMF234orMF5plus;
    private Map<Integer, Float> probTAZ;
    private Map<Integer, Float> probMicroData;
    private Map<Integer, Float> probVacantBuildingSize;
    private Map<Integer, Float> probVacantFloor;
    private double[] probabilityId;
    private double sumProbabilities;
    private double[] probabilityTAZ;
    private double sumTAZs;
    private int[] ids;
    private int[] idTAZs;
    private int personCounter;
    private int householdCounter;

    private HouseholdDataManager householdDataManager;


    public GenerateHouseholdsPersonsDwellings(SiloDataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataContainer = dataContainer;
        this.dataSetSynPop = dataSetSynPop;
        microDataManager = new MicroDataManager(dataSetSynPop);
    }

    public void run(){
        logger.info("   Running module: household, person and dwelling generation");
        previousHouseholds = 0;
        previousPersons = 0;
        //initializeQualityAndIncomeDistributions();
        householdDataManager = dataContainer.getHouseholdData();
        for (int municipality : dataSetSynPop.getMunicipalities()){
            initializeMunicipalityData(municipality);
            double logging = 2;
            int it = 12;
            int[] hhSelection = selectMultipleHouseholds(totalHouseholds);
            int[] tazSelection = selectMultipleTAZ(totalHouseholds);
            for (int draw = 0; draw < totalHouseholds; draw++) {
                int hhSelected = hhSelection[draw];
                int tazSelected = tazSelection[draw];
                Household household = generateHousehold();
                generateDwelling(hhSelected, household.getId(), tazSelected, municipality);
                generatePersons(hhSelected, household);
                if (draw == logging & draw > 2) {
                    logger.info("   Municipality " + municipality + ". Generated household " + draw);
                    it++;
                    logging = Math.pow(2, it);
                }
            }
        }
        generateVacantDwellings();
    }


    private Household generateHousehold(){

        int id = householdDataManager.getNextHouseholdId();
        Household household = householdDataManager.createHousehold(id, id, 0);
        householdCounter++;
        return household;
    }


    private void generatePersons(int hhSelected, Household hh){

        int hhSize = dataSetSynPop.getHouseholdTable().get(hhSelected, "hhSize");
        for (int person = 0; person < hhSize; person++) {
            int id = householdDataManager.getNextPersonId();
            int personSelected = dataSetSynPop.getHouseholdTable().get(hhSelected, "personCount") + person;
            int age = dataSetSynPop.getPersonTable().get(personSelected, "age");
            int gender = dataSetSynPop.getPersonTable().get(personSelected, "gender");
            int occupation = dataSetSynPop.getPersonTable().get(personSelected, "occupation");
            Race race = microDataManager.translateRace(dataSetSynPop.getPersonTable().get(personSelected, "nationality"));
            Nationality nationality1 = microDataManager.translateNationality(dataSetSynPop.getPersonTable().get(personSelected, "nationality"));
            int income = microDataManager.translateIncome(dataSetSynPop.getPersonTable().get(personSelected, "income"));
            boolean license = microDataManager.obtainLicense(gender, age);
            int telework = dataSetSynPop.getPersonTable().get(personSelected, "telework");
            int educationDegree = dataSetSynPop.getPersonTable().get(personSelected, "educationDegree");
            PersonRole personRole = microDataManager.translatePersonRole(dataSetSynPop.getPersonTable().get(personSelected, "personRole"));
            int school = dataSetSynPop.getPersonTable().get(personSelected, "school");
            Person pers = householdDataManager.createPerson(id, age, gender, race, occupation, 0, income); //(int id, int age, int gender, Race race, int occupation, int workplace, int income)
            householdDataManager.addPersonToHousehold(pers, hh);
            pers.setRole(personRole);
            pers.setNationality(nationality1);
            pers.setDriverLicense(license);
            pers.setEducationLevel(educationDegree);
            pers.setSchoolType(school);
            pers.setTelework(telework);
            personCounter++;
        }
    }


    private void generateDwelling(int hhSelected, int idHousehold, int tazSelected, int municipality){

        int newDdId = RealEstateDataManager.getNextDwellingId();
        int year = dataSetSynPop.getDwellingTable().get(hhSelected, "ddYear");
        int floorSpace = dataSetSynPop.getDwellingTable().get(hhSelected, "ddFloor");
        int useInteger = dataSetSynPop.getDwellingTable().get(hhSelected, "ddUse");
        Dwelling.Usage usage = Dwelling.Usage.valueOf(useInteger);
        int buildingSize = dataSetSynPop.getDwellingTable().get(hhSelected, "ddSize");
        int ddHeatingEnergy = dataSetSynPop.getDwellingTable().get(hhSelected, "ddHeatingEnergy");
        int ddHeatingType = dataSetSynPop.getDwellingTable().get(hhSelected, "ddHeatingType");
        int ddAdHeating = dataSetSynPop.getDwellingTable().get(hhSelected, "ddAdHeating");
        int quality = microDataManager.guessDwellingQuality(ddHeatingType, ddHeatingEnergy, ddAdHeating, year);
        DwellingType type = microDataManager.translateDwellingType(buildingSize, ddTypeProbOfSFAorSFD, ddTypeProbOfMF234orMF5plus);
        int bedRooms = microDataManager.guessBedrooms(floorSpace);
        int groundPrice = dataSetSynPop.getDwellingPriceByTypeAndZone().get(tazSelected).get(type);
        int price = microDataManager.guessPrice(groundPrice, quality, floorSpace, usage);
        Dwelling dwell = dataContainer.getRealEstateData().createDwelling(newDdId, tazSelected, idHousehold, type , bedRooms, quality, price, 0, year);
        dwell.setFloorSpace(floorSpace);
        dwell.setUsage(usage);
        dwell.setBuildingSize(buildingSize);
        updateQualityMap(municipality, year, quality);
    }


    private void generateVacantDwellings(){
        RealEstateDataManager realEstate = dataContainer.getRealEstateData();
        for (int municipality : dataSetSynPop.getMunicipalities()){
            int vacantDwellings =(int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "totalDwellingsVacant");
            initializeVacantDwellingData(municipality);
            int vacantCounter = 0;
            int[] tazSelection = selectMultipleTAZ(vacantDwellings);

            for (int draw = 0; draw < vacantDwellings; draw++){
                int tazSelected = tazSelection[draw];
                int newDdId = RealEstateDataManager.getNextDwellingId();
                int floorSpace = microDataManager.guessFloorSpace(SiloUtil.select(probVacantFloor));
                int buildingYear = SiloUtil.select(probVacantBuildingSize);
                int year = extractYear(buildingYear);
                DwellingType type = extractDwellingType(buildingYear, ddTypeProbOfSFAorSFD, ddTypeProbOfMF234orMF5plus);
                int bedRooms = microDataManager.guessBedrooms(floorSpace);
                int quality = selectQualityVacant(municipality, year);
                int groundPrice = dataSetSynPop.getDwellingPriceByTypeAndZone().get(tazSelected).get(type);
                int price = microDataManager.guessPrice(groundPrice, quality, floorSpace, Dwelling.Usage.VACANT);
                Dwelling dwell = realEstate.createDwelling(newDdId, tazSelected, -1, DwellingType.MF234, bedRooms, quality, price, 0, year); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year
                dwell.setUsage(Dwelling.Usage.VACANT); //vacant dwelling = 3; and hhID is equal to -1
                dwell.setFloorSpace(floorSpace);
                vacantCounter++;
            }
            logger.info("Municipality " + municipality + ". Generated vacant dwellings: " + vacantCounter);
        }
    }


    private int selectMicroHouseholdWithReplacement() {

        int hhSelected = SiloUtil.select(probMicroData);
        if (probMicroData.get(hhSelected) > 1){
            probMicroData.put(hhSelected, probMicroData.get(hhSelected) - 1);
        } else {
            probMicroData.remove(hhSelected);
        }
        return hhSelected;
    }


    private int selectTAZwithoutReplacement(int hhSelected){

        int taz = SiloUtil.select(probTAZ);
        return taz;
    }


    private void initializeMunicipalityData(int municipality){

        logger.info("   Municipality " + municipality + ". Starting to generate households and persons");
        totalHouseholds = (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "hhTotal");
        ddTypeProbOfSFAorSFD = PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality,"ddProbSFAorSFD");
        ddTypeProbOfMF234orMF5plus = PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality,"ddProbMF234orMF5plus");
        probTAZ = dataSetSynPop.getProbabilityZone().get(municipality);
        probMicroData = new HashMap<>();
        probabilityId = new double[dataSetSynPop.getWeights().getRowCount()];
        ids = new int[probabilityId.length];
        sumProbabilities = 0;
        for (int id : dataSetSynPop.getWeights().getColumnAsInt("ID")){
            probMicroData.put(id, dataSetSynPop.getWeights().getValueAt(id, Integer.toString(municipality)));
        }
        for (int i = 0; i < probabilityId.length; i++){
/*            sumProbabilities = sumProbabilities + dataSetSynPop.getWeights().getValueAt(i+1, Integer.toString(municipality));
            probabilityId[i] = sumProbabilities;*/
            sumProbabilities = sumProbabilities + dataSetSynPop.getWeights().getValueAt(i+1, Integer.toString(municipality));
            probabilityId[i] = dataSetSynPop.getWeights().getValueAt(i+1, Integer.toString(municipality));
            ids[i] = (int) dataSetSynPop.getWeights().getValueAt(i+1, "ID");
        }
        probabilityTAZ = new double[dataSetSynPop.getProbabilityZone().get(municipality).keySet().size()];
        sumTAZs = 0;
        probabilityTAZ = dataSetSynPop.getProbabilityZone().get(municipality).values().stream().mapToDouble(Number::doubleValue).toArray();
        for (int i = 1; i < probabilityTAZ.length; i++){
            probabilityTAZ[i] = probabilityTAZ[i] + probabilityTAZ[i-1];
        }
        idTAZs = dataSetSynPop.getProbabilityZone().get(municipality).keySet().stream().mapToInt(Number::intValue).toArray();
        sumTAZs = dataSetSynPop.getProbabilityZone().get(municipality).values().stream().mapToDouble(Number::doubleValue).sum();
        personCounter = 0;
        householdCounter = 0;
    }


    private void initializeQualityAndIncomeDistributions(){

        previousHouseholds = 0;
        previousPersons = 0;

        ddQuality = new HashMap<>();
        for (int year : PropertiesSynPop.get().main.yearBracketsDwelling){
            Iterator<Integer> iterator = dataSetSynPop.getMunicipalities().iterator();
            while (iterator.hasNext()) {
                Integer municipality = iterator.next();
                HashMap<Integer, Float> qualities = new HashMap<>();
                for (int quality = 1; quality <= PropertiesSynPop.get().main.numberofQualityLevels; quality++){
                    qualities.put(quality, 0f);
                }
                int key = year * 10000000 + municipality;
                ddQuality.put(key, qualities);
            }
        }
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

        DwellingType type = DwellingType.SFA;

        if (buildingYear < 10){
            if (SiloUtil.getRandomNumberAsFloat() < ddType1Prob){
                type = DwellingType.SFD;
            } else {
                type = DwellingType.SFA;
            }
        } else {
            if (SiloUtil.getRandomNumberAsFloat() < ddType3Prob){
                type = DwellingType.MF5plus;
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


    public int[] selectMultipleHouseholds(int selections) {

        int[] selected;
        selected = new int[selections];
        int completed = 0;
        for (int iteration = 0; iteration < 100; iteration++){
            int m = selections - completed;
            double[] randomChoices = new double[m];
            for (int k = 0; k < randomChoices.length; k++) {
                randomChoices[k] = SiloUtil.getRandomNumberAsDouble()*selections;
            }
            Arrays.sort(randomChoices);

            //look up for the n travellers
            int p = 0;
            double cumulative = probabilityId[p];
            for (double randomNumber : randomChoices){
                while (randomNumber > cumulative && p < probabilityId.length - 1) {
                    p++;
                    cumulative += probabilityId[p];
                }
                if (probabilityId[p] > 0) {
                    selected[completed] = ids[p];
                    completed++;
                }
            }
        }
        return selected;

    }

    private int[] selectMultipleTAZ(int selections){

        int[] selected;
        selected = new int[selections];
        int completed = 0;
        for (int iteration = 0; iteration < 100; iteration++){
            int m = selections - completed;
            //double[] randomChoice = new double[(int)(numberOfTrips*1.1) ];
            double[] randomChoices = new double[m];
            for (int k = 0; k < randomChoices.length; k++) {
                randomChoices[k] = SiloUtil.getRandomNumberAsDouble();
            }
            Arrays.sort(randomChoices);

            //look up for the n travellers
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
}
