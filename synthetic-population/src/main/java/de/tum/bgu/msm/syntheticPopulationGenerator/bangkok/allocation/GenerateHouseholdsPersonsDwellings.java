package de.tum.bgu.msm.syntheticPopulationGenerator.bangkok.allocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.run.data.dwelling.BangkokDwellingTypes;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.MicroDataManager;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GenerateHouseholdsPersonsDwellings {

    private static final Logger logger = Logger.getLogger(GenerateHouseholdsPersonsDwellings.class);

    private final DataContainer dataContainer;

    private final DataSetSynPop dataSetSynPop;
    private final MicroDataManager microDataManager;
    private int previousHouseholds;
    private int previousPersons;
    private int totalHouseholds;
    private Map<Integer, Float> probMicroData;
    private double[] probabilityId;
    private double sumProbabilities;
    private int[] ids;
    private int personCounter;
    private int householdCounter;

    private HashMap<Person, Integer> educationalLevel;

    private HouseholdDataManager householdData;


    public GenerateHouseholdsPersonsDwellings(DataContainer dataContainer, DataSetSynPop dataSetSynPop, HashMap<Person, Integer> educationalLevel){
        this.dataContainer = dataContainer;
        this.dataSetSynPop = dataSetSynPop;
        this.educationalLevel = educationalLevel;
        microDataManager = new MicroDataManager(dataSetSynPop);
    }

    public void run(){
        logger.info("   Running module: household, person and dwelling generation");
        previousHouseholds = 0;
        previousPersons = 0;
        householdData = dataContainer.getHouseholdDataManager();
        //for (int municipality = 1; municipality < 3; municipality++){
        for (int municipality : dataSetSynPop.getMunicipalities()){
            initializeMunicipalityData(municipality);
            double logging = 2;
            int it = 12;
            int[] hhSelection = selectMultipleHouseholds(totalHouseholds);
            for (int draw = 0; draw < totalHouseholds; draw++) {
                int hhSelected = hhSelection[draw];
                int tazSelected = municipality;
                Household household = generateHousehold();
                generatePersons(hhSelected, household);
                generateDwelling(hhSelected, household.getId(), tazSelected, municipality);
                if (draw == logging & draw > 2) {
                    logger.info("   Municipality " + municipality + ". Generated household " + draw);
                    it++;
                    logging = Math.pow(2, it);
                }
            }
        }
    }


    private Household generateHousehold(){

        HouseholdFactory factory = householdData.getHouseholdFactory();
        int id = householdData.getNextHouseholdId();
        Household household = factory.createHousehold(id,id,0);
                householdData.addHousehold(household);
        householdCounter++;
        return household;
    }


    private void generatePersons(int hhSelected, Household hh){

        int hhSize = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(hhSelected, "hhSize");
        PersonFactory factory = PersonUtils.getFactory();
        for (int person = 0; person < hhSize; person++) {
            int id = householdData.getNextPersonId();
            int personSelected = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(hhSelected, "id_firstPerson") + person;
            int age =(int) dataSetSynPop.getPersonDataSet().getValueAt(personSelected, "age");
            Gender gender = Gender.valueOf((int) dataSetSynPop.getPersonDataSet().getValueAt(personSelected, "gender"));
            Occupation occupation = Occupation.valueOf((int) dataSetSynPop.getPersonDataSet().getValueAt(personSelected, "employmentCode"));
            int income = 0;
            boolean license = MicroDataManager.obtainLicense(gender, age);
            int educationDegree = 1;
            PersonRole personRole = microDataManager.translatePersonRole((int)dataSetSynPop.getPersonDataSet().getValueAt(personSelected, "personRole"));
            int school = 1;
            Person pers = factory.createPerson(id, age, gender, occupation,personRole, 0, income); //(int id, int age, int gender, Race race, int occupation, int workplace, int income)
            //pers.setNationality(nationality1);
            pers.setDriverLicense(license);
           // pers.setSchoolType(school);
            householdData.addPerson(pers);
            householdData.addPersonToHousehold(pers, hh);
            educationalLevel.put(pers, educationDegree);
            personCounter++;
        }
    }

    private int guessIncome(){

        return 0;
    }
    private void generateDwelling(int hhSelected, int idHousehold, int tazSelected, int municipality){

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        int hhIncome = HouseholdUtil.getAnnualHhIncome(dataContainer.getHouseholdDataManager().getHouseholdFromId(idHousehold));
        int newDdId = realEstate.getNextDwellingId();
        int year = 0;
        int floorSpace = 0;
        int useInteger = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(hhSelected, "tenureCode");
        DwellingUsage usage = DwellingUsage.valueOf(useInteger);
        int ddType = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(hhSelected, "ddTypeCode");
        DwellingType type = guessDwellingType (ddType);
        int bedRooms = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(hhSelected, "bedrooms");
        Dwelling dwell = DwellingUtils.getFactory().createDwelling(newDdId, tazSelected, null, idHousehold, type , bedRooms, 1, 0, year);
        realEstate.addDwelling(dwell);
        dwell.setFloorSpace(floorSpace);
        dwell.setUsage(usage);
        guessPriceAndQuality(hhIncome, dwell);
    }


    private DwellingType guessDwellingType (int ddTypeCode){
        if (SiloUtil.getRandomNumberAsFloat() > 0.5){
            ddTypeCode = ddTypeCode + 1;
        }
        DwellingType type = BangkokDwellingTypes.DwellingTypeBangkok.valueOf(ddTypeCode);
        return type;
    }


    private void guessPriceAndQuality(int hhIncome, Dwelling dd) {
        int targetPrice =  (int)Math.round(hhIncome * 0.15 / 12);
        DwellingType ddType = dd.getType();
        int sizeDwelling = ((BangkokDwellingTypes.DwellingTypeBangkok)ddType).getsizeOfDwelling();
        int priceLowQuality = 80 * sizeDwelling;
        int priceMediumQuality = 160 * sizeDwelling;
        int priceHighQuality = 240 * sizeDwelling;
        int minDif = 10000000;
        int quality = 0;
        int price = 0;
        if (Math.abs(targetPrice - priceLowQuality) < minDif){
            quality = 1;
            price = priceLowQuality;
        } else if (Math.abs(targetPrice - priceMediumQuality) < minDif){
            quality = 2;
            price = priceMediumQuality;
        } else if (Math.abs(targetPrice - priceHighQuality) < minDif){
            quality = 3;
            price = priceHighQuality;
        }
        dd.setPrice(price);
        dd.setQuality(quality);

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




    private void initializeMunicipalityData(int municipality){

        logger.info("   Municipality " + municipality + ". Starting to generate households and persons");
        totalHouseholds = (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "households");

        probMicroData = new HashMap<>();
        probabilityId = new double[dataSetSynPop.getWeights().getRowCount()];
        ids = new int[probabilityId.length];
        sumProbabilities = 0;
        for (int id : dataSetSynPop.getWeights().getColumnAsInt("ID")){
            probMicroData.put(id, dataSetSynPop.getWeights().getValueAt(id, Integer.toString(municipality)));
        }
        for (int i = 0; i < probabilityId.length; i++){
            sumProbabilities = sumProbabilities + dataSetSynPop.getWeights().getValueAt(i+1, Integer.toString(municipality));
            probabilityId[i] = dataSetSynPop.getWeights().getValueAt(i+1, Integer.toString(municipality));
            ids[i] = (int) dataSetSynPop.getWeights().getValueAt(i+1, "ID");
        }

        personCounter = 0;
        householdCounter = 0;
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

}
