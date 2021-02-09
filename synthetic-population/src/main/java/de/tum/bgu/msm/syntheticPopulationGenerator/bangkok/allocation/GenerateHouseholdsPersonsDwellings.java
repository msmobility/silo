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

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
    private int personCounterByMunicipality;
    private int householdCounterByMunicipality;
    private double incomeByMunicipality;
    private HashMap<Person, Integer> educationalLevel;
    private int firstHouseholdMunicipality;
    private HouseholdDataManager householdData;
    private RealEstateDataManager realEstate;
    private Map<Integer, Map<String, Double>> zonalSummary = new LinkedHashMap<>();
    private Map<Integer, Map<String, Double>> allocationErrors = new LinkedHashMap<>();

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
        realEstate = dataContainer.getRealEstateDataManager();
        firstHouseholdMunicipality = 1;
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
                incomeByMunicipality = incomeByMunicipality + HouseholdUtil.getAnnualHhIncome(household);
                if (draw == logging & draw > 2) {
                    logger.info("   Municipality " + municipality + ". Generated household " + draw);
                    it++;
                    logging = Math.pow(2, it);
                }
            }
            recalculateIncomeAndDwellingPrice(municipality);
            allocationErrorsByZone(municipality);
        }
        outputSummaryByZone();
    }

    private void summarizeByZone(int municipality, Map<Integer, Household> householdMap, Map<Integer, Person> personMap, Map<Integer, Dwelling> dwellingMap) {
        zonalSummary.get(municipality).put("hhTotal", (double) householdMap.values().stream().mapToInt(x -> x.getId()).count());
        zonalSummary.get(municipality).put("hhSize1", (double) householdMap.values().stream().filter(x ->x.getHhSize() == 1).count());
        zonalSummary.get(municipality).put("hhSize2", (double) householdMap.values().stream().filter(x ->x.getHhSize() == 2).count());
        zonalSummary.get(municipality).put("hhSize3", (double) householdMap.values().stream().filter(x ->x.getHhSize() == 3).count());
        zonalSummary.get(municipality).put("hhSize4", (double) householdMap.values().stream().filter(x ->x.getHhSize() == 4).count());
        zonalSummary.get(municipality).put("hhSize5+", (double) householdMap.values().stream().filter(x ->x.getHhSize() > 4).count());
        zonalSummary.get(municipality).put("detached_120", (double) dwellingMap.values().stream().filter(x ->x.getType().equals(BangkokDwellingTypes.DwellingTypeBangkok.DETATCHED_HOUSE_120)).count());
        zonalSummary.get(municipality).put("detached_200", (double) dwellingMap.values().stream().filter(x ->x.getType().equals(BangkokDwellingTypes.DwellingTypeBangkok.DETATCHED_HOUSE_200)).count());
        zonalSummary.get(municipality).put("high_rise50", (double) dwellingMap.values().stream().filter(x ->x.getType().equals(BangkokDwellingTypes.DwellingTypeBangkok.HIGH_RISE_CONDOMINIUM_50)).count());
        zonalSummary.get(municipality).put("high_rise30", (double) dwellingMap.values().stream().filter(x ->x.getType().equals(BangkokDwellingTypes.DwellingTypeBangkok.HIGH_RISE_CONDOMINIUM_30)).count());
        zonalSummary.get(municipality).put("low_rise50", (double) dwellingMap.values().stream().filter(x ->x.getType().equals(BangkokDwellingTypes.DwellingTypeBangkok.LOW_RISE_CONDOMINIUM_50)).count());
        zonalSummary.get(municipality).put("low_rise30", (double) dwellingMap.values().stream().filter(x ->x.getType().equals(BangkokDwellingTypes.DwellingTypeBangkok.LOW_RISE_CONDOMINIUM_30)).count());
        zonalSummary.get(municipality).put("quality1", (double) dwellingMap.values().stream().filter(x ->x.getQuality() == 1).count());
        zonalSummary.get(municipality).put("quality2", (double) dwellingMap.values().stream().filter(x ->x.getQuality() == 2).count());
        zonalSummary.get(municipality).put("quality3", (double) dwellingMap.values().stream().filter(x ->x.getQuality() == 3).count());
        zonalSummary.get(municipality).put("quality4", (double) dwellingMap.values().stream().filter(x ->x.getQuality() == 4).count());
        if(dwellingMap.keySet().size() > 1) {
            zonalSummary.get(municipality).put("rent", dwellingMap.values().stream().mapToInt(x -> x.getPrice()).average().getAsDouble());
            zonalSummary.get(municipality).put("income", personMap.values().stream().mapToInt(x ->x.getAnnualIncome()).average().getAsDouble());
        } else {
            zonalSummary.get(municipality).put("rent", 0.0);
            zonalSummary.get(municipality).put("income", 0.0);
        }
        zonalSummary.get(municipality).put("population", (double) personMap.values().stream().mapToInt(x -> x.getId()).count());
        zonalSummary.get(municipality).put("males", (double) personMap.values().stream().filter(x ->x.getGender().equals(Gender.MALE)).count());
        zonalSummary.get(municipality).put("females", (double) personMap.values().stream().filter(x ->x.getGender().equals(Gender.FEMALE)).count());
        zonalSummary.get(municipality).put("workers", (double) personMap.values().stream().filter(x ->x.getOccupation().equals(Occupation.EMPLOYED)).count());

        int[] ageBracketsGender = new int[]{9,19,29,39,49,59,69,79,89,109};
        double countAgeMale = 0;
        double countAgeFemale = 0;
        for (int age : ageBracketsGender){
            double maleYoungerThan = (double) personMap.values().stream().filter(x->x.getGender().equals(Gender.MALE)).filter(x->x.getAge() <= age).count();
            zonalSummary.get(municipality).put("males"+age, maleYoungerThan - countAgeMale);
            countAgeMale = maleYoungerThan;
            double femaleYoungerThan = (double) personMap.values().stream().filter(x->x.getGender().equals(Gender.FEMALE)).filter(x->x.getAge() <= age).count();
            zonalSummary.get(municipality).put("females"+age, femaleYoungerThan - countAgeFemale);
            countAgeFemale = femaleYoungerThan;
        }
        int[] ageBrackets = new int[]{18,35,65,109};
        double countAge = 0;
        for (int age : ageBrackets){
            double youngerThan = (double) personMap.values().stream().filter(x->x.getGender().equals(Gender.MALE)).filter(x->x.getAge() <= age).count();
            zonalSummary.get(municipality).put("person"+age, youngerThan - countAge);
            countAge = youngerThan;
        }
    }

    private void allocationErrorsByZone(int municipality) {

        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("fem109", "females109");
        attributes.put("male109", "males109");
        attributes.put("fem89", "females89");
        attributes.put("male89", "males89");
        attributes.put("fem79", "females79");
        attributes.put("male79", "males79");
        attributes.put("fem9", "females9");
        attributes.put("male9", "males9");
        attributes.put("fem69", "females69");
        attributes.put("male69", "males69");
        attributes.put("fem19", "females19");
        attributes.put("male19", "males19");
        attributes.put("fem29", "females29");
        attributes.put("male29", "males29");
        attributes.put("fem59", "females59");
        attributes.put("male59", "males59");
        attributes.put("fem39", "females39");
        attributes.put("male39", "males39");
        attributes.put("fem49", "females49");
        attributes.put("male49", "males49");
        attributes.put("females", "females");
        attributes.put("males", "males");
        attributes.put("population", "population");
        attributes.put("households", "hhTotal");
        allocationErrors.putIfAbsent(municipality, new LinkedHashMap<>());
        for (String attributeMarginals : attributes.keySet()) {
            allocationErrors.get(municipality).put(attributes.get(attributeMarginals), Math.abs((zonalSummary.get(municipality).get(attributes.get(attributeMarginals)) -
                    PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, attributeMarginals)) /
                            PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, attributeMarginals)));
        }
        allocationErrors.get(municipality).put("condo", Math.abs((zonalSummary.get(municipality).get("high_rise50") +zonalSummary.get(municipality).get("high_rise30")-
                PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "condo") )/
                        PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "condo")));
        allocationErrors.get(municipality).put("apartment", Math.abs((zonalSummary.get(municipality).get("low_rise50") +zonalSummary.get(municipality).get("low_rise30")-
                PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "apartment")) /
                        PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "apartment")));
        allocationErrors.get(municipality).put("house", Math.abs((zonalSummary.get(municipality).get("detached_120") +zonalSummary.get(municipality).get("detached_200")-
                PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "house")) /
                        PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "house")));
    }


    private void recalculateIncomeAndDwellingPrice(int municipality) {
        double averageIncomeByZoneCensus = PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(municipality, "income");
        double averageIncomeZone = incomeByMunicipality / personCounterByMunicipality;
        double incomeMultiplier = averageIncomeByZoneCensus / averageIncomeZone;
        Map<Integer, Household> householdMap = new HashMap<>();
        Map<Integer, Dwelling> dwellingMap = new HashMap<>();
        Map<Integer, Person> personMap = new HashMap<>();
        int ppHumber = 0;
        for (int hhNumber = 0; hhNumber < totalHouseholds; hhNumber++){
            Household hh = householdData.getHouseholdFromId(hhNumber + firstHouseholdMunicipality);
            householdMap.put(hhNumber, hh);
            dwellingMap.put(hhNumber, realEstate.getDwelling(hh.getId()));
            for (Person pp : hh.getPersons().values()){
                int newIncome =  (int)( pp.getAnnualIncome() * incomeMultiplier);
                pp.setIncome(newIncome);
                personMap.put(ppHumber, pp);
                ppHumber++;
            }
            int hhIncome = HouseholdUtil.getAnnualHhIncome(hh);
            guessAndSetPriceAndQuality(hhIncome, realEstate.getDwelling(hh.getDwellingId()));
        }
        zonalSummary.putIfAbsent(municipality, new LinkedHashMap<>());
        zonalSummary.get(municipality).put("incomeScaler", incomeMultiplier);
        summarizeByZone(municipality, householdMap, personMap, dwellingMap);
    }


    private void outputSummaryByZone(){
        //PropertiesSynPop.get().main.cellsMatrix.appendColumn(incomeUpdate, "incomeScaler");

        PrintWriter pw = SiloUtil.openFileForSequentialWriting("microData/interimFiles/zonalDataSummary.csv", false);
        AtomicReference<String> header = new AtomicReference<>("zone");
        for (String key : zonalSummary.get(1).keySet()) {
            header.set(header + "," + key);
        }
        pw.println(header);
        AtomicReference<String> zoneStr = new AtomicReference<>("");
        for (int municipality : dataSetSynPop.getMunicipalities()) {
            zoneStr.set(Integer.toString(municipality));
            for (Double value : zonalSummary.get(municipality).values()) {
                zoneStr.set(zoneStr + "," + value);
            }
            pw.println(zoneStr);
        }
        pw.close();

        PrintWriter pw1 = SiloUtil.openFileForSequentialWriting("microData/interimFiles/allocationErrors.csv", false);
        AtomicReference<String> header1 = new AtomicReference<>("zone");
        for (String key : allocationErrors.get(1).keySet()) {
            header1.set(header1 + "," + key);
        }
        pw1.println(header1);
        AtomicReference<String> zoneStr1 = new AtomicReference<>("");
        for (int municipality : dataSetSynPop.getMunicipalities()) {
            zoneStr1.set(Integer.toString(municipality));
            for (Double value : allocationErrors.get(municipality).values()) {
                zoneStr1.set(zoneStr1 + "," + value);
            }
            pw1.println(zoneStr1);
        }
        pw1.close();
    }

    private Household generateHousehold(){

        HouseholdFactory factory = householdData.getHouseholdFactory();
        int id = householdData.getNextHouseholdId();
        Household household = factory.createHousehold(id,id,0);
                householdData.addHousehold(household);
        householdCounterByMunicipality++;
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
            int occupationCode = (int) dataSetSynPop.getPersonDataSet().getValueAt(personSelected, "employmentCode");
            Occupation occupation = translateOccupation(occupationCode);
            int income = guessIncome(age, occupationCode, occupation, gender);
            boolean license = MicroDataManager.obtainLicense(gender, age);
            int educationDegree = 1;
            PersonRole personRole = translatePersonRole((int)dataSetSynPop.getPersonDataSet().getValueAt(personSelected, "personRole"));
            int school = 1;
            Person pers = factory.createPerson(id, age, gender, occupation,personRole, 0, income); //(int id, int age, int gender, Race race, int occupation, int workplace, int income)
            //pers.setNationality(nationality1);
            pers.setDriverLicense(license);
           // pers.setSchoolType(school);
            householdData.addPerson(pers);
            householdData.addPersonToHousehold(pers, hh);
            educationalLevel.put(pers, educationDegree);
            personCounterByMunicipality++;
        }
    }

    private int guessIncome(int age, int occupationCode, Occupation occupation, Gender gender){

        if (occupationCode == 2){
            return 0; //employed but not paid
        } else if (occupationCode == 4){
            return 0; //student
        } else {

            Map<Integer, Double> expUtilities = new HashMap<>();
            expUtilities.put(1, 1.0);
            for (int incomeCat = 2; incomeCat < 10; incomeCat++){
                expUtilities.put(incomeCat, Math.exp(calculateUtilityForIncome(incomeCat, age, occupation, gender)));
            }
            double denominator = expUtilities.values().stream().mapToDouble(Double::doubleValue).sum();
            Map<Integer, Double> probabilities = new HashMap<>();
            expUtilities.forEach((x,y)-> probabilities.put(x,y/denominator));
            int incomeCat = SiloUtil.select(probabilities);
            int income = (int) PropertiesSynPop.get().main.incomeCoefficients.getValueAt(incomeCat, "averageIncome");
            return income;
        }
    }

    public Occupation translateOccupation(int valueCode){
        if (valueCode == 1){
         return Occupation.EMPLOYED;
        } else if (valueCode == 2){
            return Occupation.EMPLOYED;
        } else if ( valueCode == 3){
            return Occupation.UNEMPLOYED;
        } else {
            return Occupation.STUDENT;
        }

    }

    public double calculateUtilityForIncome(int category, int age, Occupation occupation, Gender gender  ){
        double utility;

        //coefficients
        double b_age = PropertiesSynPop.get().main.incomeCoefficients.getValueAt(category, "age");
        double b_occu2 = PropertiesSynPop.get().main.incomeCoefficients.getValueAt(category, "occu2");
        double b_occu3 = PropertiesSynPop.get().main.incomeCoefficients.getValueAt(category, "occu3");
        double b_gender = PropertiesSynPop.get().main.incomeCoefficients.getValueAt(category, "sex");

        utility = b_age * age +
                b_occu2 * Boolean.compare(occupation.equals(Occupation.EMPLOYED), false) +
                b_occu3 * Boolean.compare(occupation.equals(Occupation.UNEMPLOYED), false) +
                b_gender * Boolean.compare(gender.equals(Gender.MALE), false);

        return utility;
    }

    private PersonRole translatePersonRole (int role){
        PersonRole personRole = PersonRole.SINGLE;
        if (role == 2) {
            personRole = PersonRole.MARRIED;
        } else if (role == 3) {
            personRole = PersonRole.CHILD;
        }
        return personRole;
    }

    private void generateDwelling(int hhSelected, int idHousehold, int tazSelected, int municipality){

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
    }


    private DwellingType guessDwellingType (int ddTypeCode){
        if (SiloUtil.getRandomNumberAsFloat() > 0.5){
            ddTypeCode = ddTypeCode + 1;
        }
        DwellingType type = BangkokDwellingTypes.DwellingTypeBangkok.valueOf(ddTypeCode);
        return type;
    }

    private String dwellingTypeString (DwellingType ddType){
        if (ddType.equals(BangkokDwellingTypes.DwellingTypeBangkok.HIGH_RISE_CONDOMINIUM_30)){
            return "high_rise";
        } else if (ddType.equals(BangkokDwellingTypes.DwellingTypeBangkok.HIGH_RISE_CONDOMINIUM_50)){
            return "high_rise";
        } else if (ddType.equals(BangkokDwellingTypes.DwellingTypeBangkok.LOW_RISE_CONDOMINIUM_30)){
            return"low_rise";
        } else if (ddType.equals(BangkokDwellingTypes.DwellingTypeBangkok.LOW_RISE_CONDOMINIUM_50)) {
            return "low_rise";
        } else if (ddType.equals(BangkokDwellingTypes.DwellingTypeBangkok.DETATCHED_HOUSE_120)) {
            return "detached_house";
        } else {
            return "detached_house";
        }

    }


    private void guessAndSetPriceAndQuality(int hhIncome, Dwelling dd) {
        int targetPrice =  (int)Math.round(hhIncome * 0.15);
        DwellingType ddType = dd.getType();
        int sizeDwelling = ((BangkokDwellingTypes.DwellingTypeBangkok)ddType).getsizeOfDwelling();
        String ddTypeStr = dwellingTypeString(ddType);
        float priceSqmLowQuality = PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(dd.getZoneId(), ddTypeStr + ".1");
        float priceLowQuality = priceSqmLowQuality * sizeDwelling;
        float priceSqmMediumQuality = PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(dd.getZoneId(), ddTypeStr + ".2");
        float priceMediumQuality = priceSqmMediumQuality * sizeDwelling;
        float priceSqmHighQuality = PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(dd.getZoneId(), ddTypeStr + ".3");
        float priceHighQuality = priceSqmHighQuality * sizeDwelling;
        float minDif = Math.abs(targetPrice - priceLowQuality);
        int quality = 1;
        float price = priceLowQuality;
        if (Math.abs(targetPrice - priceMediumQuality) < minDif){
            quality = 2;
            price = priceMediumQuality;
            minDif = Math.abs(targetPrice - priceMediumQuality);
            if ((Math.abs(targetPrice - priceHighQuality) < minDif)){
                quality = 3;
                price = priceHighQuality;
            }
        } else {
            if ((Math.abs(targetPrice - priceHighQuality) < minDif)){
                quality = 3;
                price = priceHighQuality;
            }
        }
        dd.setPrice( (int) price);
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

        personCounterByMunicipality = 0;
        householdCounterByMunicipality = 0;
        incomeByMunicipality = 0;
        firstHouseholdMunicipality = Math.max(householdData.getHighestHouseholdIdInUse(), 1); //the
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
