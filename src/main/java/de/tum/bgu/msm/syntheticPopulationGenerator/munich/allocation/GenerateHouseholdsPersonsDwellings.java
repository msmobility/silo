package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.MicroDataManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GenerateHouseholdsPersonsDwellings {

    private static final Logger logger = Logger.getLogger(GenerateHouseholdsPersonsDwellings.class);

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
    private int personCounter;
    private int householdCounter;


    public GenerateHouseholdsPersonsDwellings(DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        microDataManager = new MicroDataManager(dataSetSynPop);
    }

    public void run(){

        previousHouseholds = 0;
        previousPersons = 0;
        //initializeQualityAndIncomeDistributions();
        for (int municipality : dataSetSynPop.getMunicipalities()){
            initializeMunicipalityData(municipality);
            double logging = 2;
            int it = 1;
            for (int draw = 0; draw < totalHouseholds; draw++){
                int hhSelected = selectMicroHouseholdWithReplacement();
                int tazSelected = selectTAZwithoutReplacement(hhSelected);
                int idHousehold = generateHousehold(hhSelected, tazSelected);
                generatePersons(hhSelected, idHousehold);
                generateDwelling(hhSelected, idHousehold, tazSelected, municipality);
                if (draw == logging){
                    logger.info("   Municipality " + municipality + ". Generated household " + draw);
                    it++;
                    logging = Math.pow(2, it);
                }
            }
        }
        generateVacantDwellings();
    }


    private int generateHousehold(int hhSelected, int tazSelected){

        int hhSize = dataSetSynPop.getHouseholds().get(hhSelected).get("hhSize");
        int id = HouseholdDataManager.getNextHouseholdId();
        Household household = new Household(id, id, tazSelected, hhSize, 0); //(int id, int dwellingID, int homeZone, int hhSize, int autos)
        householdCounter++;
        return id;
    }


    private void generatePersons(int hhSelected, int idHousehold){

        for (int person = 0; person < Household.getHouseholdFromId(idHousehold).getHhSize(); person++) {
            int id = HouseholdDataManager.getNextPersonId();
            int personSelected = dataSetSynPop.getHouseholds().get(hhSelected).get("personCount") + person;
            int age = dataSetSynPop.getPersons().get(personSelected).get("age");
            int gender = dataSetSynPop.getPersons().get(personSelected).get("gender");
            int occupation = dataSetSynPop.getPersons().get(personSelected).get("occupation");
            Race race = microDataManager.translateRace(dataSetSynPop.getPersons().get(personSelected).get("nationality"));
            Nationality nationality1 = microDataManager.translateNationality(dataSetSynPop.getPersons().get(personSelected).get("nationality"));
            int income = microDataManager.translateIncome(dataSetSynPop.getPersons().get(personSelected).get("income"));
            boolean license = microDataManager.obtainLicense(gender, age);
            int telework = dataSetSynPop.getPersons().get(personSelected).get("telework");
            int educationDegree = dataSetSynPop.getPersons().get(personSelected).get("educationDegree");
            PersonRole personRole = microDataManager.translatePersonRole(dataSetSynPop.getPersons().get(personSelected).get("personRole"));
            int school = dataSetSynPop.getPersons().get(personSelected).get("school");
            Person pers = new Person(id, idHousehold, age, gender, race, occupation, 0, income); //(int id, int hhid, int age, int gender, Race race, int occupation, int workplace, int income)
            Household.getHouseholdFromId(idHousehold).addPersonForInitialSetup(pers);
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
        int year = dataSetSynPop.getDwellings().get(hhSelected).get("ddYear");
        int floorSpace = dataSetSynPop.getDwellings().get(hhSelected).get("ddFloor");
        int usage = dataSetSynPop.getDwellings().get(hhSelected).get("ddUse");
        int buildingSize = dataSetSynPop.getDwellings().get(hhSelected).get("ddSize");
        int ddHeatingEnergy = dataSetSynPop.getDwellings().get(hhSelected).get("ddHeatingEnergy");
        int ddHeatingType = dataSetSynPop.getDwellings().get(hhSelected).get("ddHeatingType");
        int ddAdHeating = dataSetSynPop.getDwellings().get(hhSelected).get("ddAdHeating");
        int quality = microDataManager.guessDwellingQuality(ddHeatingType, ddHeatingEnergy, ddAdHeating, year);
        DwellingType type = microDataManager.translateDwellingType(buildingSize, ddTypeProbOfSFAorSFD, ddTypeProbOfMF234orMF5plus);
        int bedRooms = microDataManager.guessBedrooms(floorSpace);
        int groundPrice = dataSetSynPop.getDwellingPriceByTypeAndZone().get(tazSelected).get(type);
        int price = microDataManager.guessPrice(groundPrice, quality, floorSpace, usage);
        Dwelling dwell = new Dwelling(newDdId, tazSelected, idHousehold, type , bedRooms, quality, price, 0, year); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year
        dwell.setFloorSpace(floorSpace);
        dwell.setUsage(usage);
        dwell.setBuildingSize(buildingSize);
        updateQualityMap(municipality, year, quality);
    }


    private void generateVacantDwellings(){

        for (int municipality : dataSetSynPop.getMunicipalities()){

            int vacantDwellings =(int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "totalDwellingsVacant");
            initializeVacantDwellingData(municipality);
            int vacantCounter = 0;

            for (int draw = 0; draw < vacantDwellings; draw++){

                int tazSelected = selectTAZwithoutReplacement(0);
                int newDdId = RealEstateDataManager.getNextDwellingId();
                int floorSpace = microDataManager.guessFloorSpace(SiloUtil.select(probVacantFloor));
                int buildingYear = SiloUtil.select(probVacantBuildingSize);
                int year = extractYear(buildingYear);
                DwellingType type = extractDwellingType(buildingYear, ddTypeProbOfSFAorSFD, ddTypeProbOfMF234orMF5plus);
                int bedRooms = microDataManager.guessBedrooms(floorSpace);
                int quality = selectQualityVacant(municipality, year);
                int groundPrice = dataSetSynPop.getDwellingPriceByTypeAndZone().get(tazSelected).get(type);
                int price = microDataManager.guessPrice(groundPrice, quality, floorSpace, 3);
                Dwelling dwell = new Dwelling(newDdId, tazSelected, -1, DwellingType.MF234, bedRooms, quality, price, 0, year); //newDwellingId, raster cell, HH Id, ddType, bedRooms, quality, price, restriction, construction year
                dwell.setUsage(3); //vacant dwelling = 3; and hhID is equal to -1
                dwell.setFloorSpace(floorSpace);
                vacantCounter++;
            }
            logger.info("Municipality " + municipality + ". Generated vacant dwellings: " + vacantCounter);
        }
    }


    private int selectMicroHouseholdWithReplacement() {

        int hhSelected = SiloUtil.select(probMicroData);
        if (probMicroData.get(hhSelected) > 0){
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
        for (int id : dataSetSynPop.getWeights().getColumnAsInt("ID")){
            probMicroData.put(id, dataSetSynPop.getWeights().getValueAt(id, Integer.toString(municipality)));
        }
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
}
