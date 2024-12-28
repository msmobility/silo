package de.tum.bgu.msm.syntheticPopulationGenerator.manchester.allocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.ManchesterDwellingUsage;
import de.tum.bgu.msm.data.PersonFactoryMCR;
import de.tum.bgu.msm.data.PersonMCR;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.data.ManchesterDwellingTypes;
import de.tum.bgu.msm.run.data.dwelling.BangkokDwellingTypes;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.manchester.DataSetSynPopMCR;
import de.tum.bgu.msm.syntheticPopulationGenerator.manchester.preparation.MicroDataManager;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class GenerateHouseholdsPersonsDwellings {

    private static final Logger logger = LogManager.getLogger(GenerateHouseholdsPersonsDwellings.class);

    private final DataContainer dataContainer;

    private final DataSetSynPop dataSetSynPop;
    private final MicroDataManager microDataManager;
    private int previousHouseholds;
    private int previousPersons;
    private int totalHouseholds;
    private Map<Integer, Float> probMicroData;
    private double[] probabilityId;
    private double[] probabilityTAZ;
    private double sumProbabilities;
    private int[] ids;
    private double sumTAZs;
    private int[] idTAZs;
    private int personCounter;
    private int householdCounter;
    private int firstHouseholdMunicipality;
    private RealEstateDataManager realEstate;
    private Map<Integer, Map<String, Double>> zonalSummary = new LinkedHashMap<>();
    private Map<Integer, Map<String, Double>> allocationErrors = new LinkedHashMap<>();
    private double incomeByMunicipality;
    private int personCounterByMunicipality;

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
        realEstate = dataContainer.getRealEstateDataManager();
        firstHouseholdMunicipality = 1;
        //for (int municipality = 1; municipality < 3; municipality++){
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
                generatePersons(hhSelected, household);
                generateDwelling(hhSelected, household.getId(), tazSelected, municipality);
                incomeByMunicipality = incomeByMunicipality + HouseholdUtil.getAnnualHhIncome(household);
                if (draw == logging & draw > 2) {
                    logger.info("   Municipality " + municipality + ". Generated household " + draw);
                    it++;
                    logging = Math.pow(2, it);
                }
            }

            summaryByCity(municipality);
            allocationErrorsByZone(municipality);
        }
        outputSummaryByZone();
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
        PersonFactoryMCR factory = new PersonFactoryMCR();
        for (int person = 0; person < hhSize; person++) {
            int id = householdData.getNextPersonId();
            int personSelected = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(hhSelected, "id_firstPerson") + person;
            int age =(int) dataSetSynPop.getPersonDataSet().getValueAt(personSelected, "age");
            Gender gender = Gender.valueOf((int) dataSetSynPop.getPersonDataSet().getValueAt(personSelected, "gender"));
            Occupation occupation = Occupation.valueOf((int) dataSetSynPop.getPersonDataSet().getValueAt(personSelected, "employmentCode"));
            int monthlyIncome = microDataManager.translateIncome((int) dataSetSynPop.getPersonDataSet().getValueAt(personSelected, "income"));
            boolean license = microDataManager.obtainLicense(gender, age);
            //TODO: education degree
            int educationDegree = 1;
            PersonRole personRole = microDataManager.translatePersonRole((int)dataSetSynPop.getPersonDataSet().getValueAt(personSelected, "personRole"));
            int school = microDataManager.guessSchoolType(occupation, age);
            PersonMCR pers = factory.createPerson(id, age, gender, occupation,personRole, 0, monthlyIncome*12); //(int id, int age, int gender, Race race, int occupation, int workplace, int income)
            pers.setDriverLicense(license);
            pers.setSchoolType(school);
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
        DwellingUsage usage = microDataManager.translateDwellingUsage(useInteger);
        int ddType = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(hhSelected, "ddTypeCode");
        DwellingType type = ManchesterDwellingTypes.DwellingTypeManchester.valueOf(ddType);
        int bedRooms = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(hhSelected, "bedroom");
        Dwelling dwell = DwellingUtils.getFactory().createDwelling(newDdId, tazSelected, null, idHousehold, type , bedRooms, 1, 0, year);
        realEstate.addDwelling(dwell);
        dwell.setFloorSpace(floorSpace);
        dwell.setUsage(usage);
        priceRegression(dwell);
        guessDwellingQuality(dwell);
        //guessPriceAndQuality(hhIncome, dwell);
    }

    private void priceRegression(Dwelling dwell) {
        //TODO: price regression at LSOA level
        double logPrice = 6.418 + 0.289 * dwell.getBedrooms();

        switch ((ManchesterDwellingTypes.DwellingTypeManchester)dwell.getType()){
            case SFD:
                logPrice += 0;
                break;
            case SFA:
                logPrice += -0.194;
                break;
            case MF234:
            case MF5plus:
                logPrice += -0.107;
                break;
            default:
                logger.warn("Dwelling type doesn't exist!");
        }


        //dist to center TAZ (OA 21code E00175845 zone ID 936)
        logPrice += -0.022 * dataSetSynPop.getDistanceTazToTaz().getValueAt(dwell.getZoneId(),936);

        //job density
        int lsoaID = ((DataSetSynPopMCR)dataSetSynPop).getTazMunicipality().get(dwell.getZoneId());
        if(((DataSetSynPopMCR)dataSetSynPop).getLsoaAttributes().get(lsoaID).get("tot")>0){
            logPrice += 0.017 * Math.log(((DataSetSynPopMCR)dataSetSynPop).getLsoaAttributes().get(lsoaID).get("tot"));
        }

        double ddPrice = Math.exp(logPrice);
        dwell.setPrice((int) ddPrice);
    }

    public int guessDwellingQuality(int heatingType, int heatingEnergy, int additionalHeating, int yearBuilt){
        //guess quality of dwelling based on construction year and heating characteristics.
        //kitchen and bathroom quality are not coded on the micro data
        int quality = PropertiesSynPop.get().main.numberofQualityLevels;
        if (heatingType > 2) quality--; //reduce quality if not central or district heating
        if (heatingEnergy > 4) quality--; //reduce quality if energy is not gas, electricity or heating oil (i.e. coal, wood, biomass, solar energy)
        if (additionalHeating == 0) quality++; //increase quality if there is additional heating in the house (regardless the used energy)
        if (yearBuilt > 0){
            //Ages - 1: before 1919, 2: 1919-1948, 3: 1949-1978, 4: 1979 - 1986; 5: 1987 - 1990; 6: 1991 - 2000; 7: 2001 - 2004; 8: 2005 - 2008, 9: 2009 or later,
            float[] deteriorationProbability = {0.9f, 0.8f, 0.6f, 0.3f, 0.12f, 0.08f, 0.05f, 0.04f, 0.04f};
            float prob = deteriorationProbability[yearBuilt - 1];
            //attempt to drop quality by age two times (to get some spreading of quality levels)
            quality = quality - SiloUtil.select(new double[]{1 - prob ,prob});
            quality = quality - SiloUtil.select(new double[]{1 - prob, prob});
        }
        quality = Math.max(quality, 1);      // ensure that quality never drops below 1
        quality = Math.min(quality, PropertiesSynPop.get().main.numberofQualityLevels);      // ensure that quality never excess the number of quality levels
        return quality;
    }

    public void guessDwellingQuality(Dwelling dd){
        double unitPrice = dd.getPrice()/(double)dd.getBedrooms();
        if (unitPrice<=240){
            dd.setQuality(1);
        }else if(unitPrice<=320){
            dd.setQuality(2);
        }else if(unitPrice<=400){
            dd.setQuality(3);
        }else {
            dd.setQuality(4);
        }
    }

    private void guessPriceAndQuality(int hhIncome, Dwelling dd) {
        int targetPrice =  (int)Math.round(hhIncome * 0.15 / 12);
        DwellingType ddType = dd.getType();
        int sizeDwelling = ((ManchesterDwellingTypes.DwellingTypeManchester)ddType).getsizeOfDwelling();
        int priceLowQuality = 8 * sizeDwelling;
        int priceMediumQuality = 16 * sizeDwelling;
        int priceHighQuality = 24 * sizeDwelling;
        int quality = 0;
        int price = 0;
        if (targetPrice >= priceHighQuality){
            quality = 3;
            price = priceHighQuality;
        } else if (targetPrice >= priceMediumQuality){
            quality = 2;
            price = priceMediumQuality;
        } else {
            quality = 1;
            price = priceLowQuality;
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
        totalHouseholds = (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "hh");

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

        probabilityTAZ = new double[dataSetSynPop.getProbabilityZone().get(municipality).keySet().size()];
        sumTAZs = 0;
        probabilityTAZ = dataSetSynPop.getProbabilityZone().get(municipality).values().stream().mapToDouble(Number::doubleValue).toArray();
        double sum = 0.;
        for (int i = 0; i < probabilityTAZ.length; i++){
            sum += probabilityTAZ[i];
        }

        for (int i = 0; i < probabilityTAZ.length; i++){
            probabilityTAZ[i] = probabilityTAZ[i]/sum;
        }

        idTAZs = dataSetSynPop.getProbabilityZone().get(municipality).keySet().stream().mapToInt(Number::intValue).toArray();
        sumTAZs = dataSetSynPop.getProbabilityZone().get(municipality).values().stream().mapToDouble(Number::doubleValue).sum();


        personCounter = 0;
        householdCounter = 0;
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
                if (probabilityId[p] > 0.00001) {
                    selected[completed] = ids[p];
                    completed++;
                }
            }
        }
        return selected;

    }


    private void summarizeByZone(int municipality, Map<Integer, Household> householdMap, Map<Integer, Person> personMap, Map<Integer, Dwelling> dwellingMap) {
        zonalSummary.get(municipality).put("hh", (double) householdMap.values().stream().mapToInt(x -> x.getId()).count());
        zonalSummary.get(municipality).put("hh1", (double) householdMap.values().stream().filter(x ->x.getHhSize() == 1).count());
        zonalSummary.get(municipality).put("hh2", (double) householdMap.values().stream().filter(x ->x.getHhSize() == 2).count());
        zonalSummary.get(municipality).put("hh3", (double) householdMap.values().stream().filter(x ->x.getHhSize() == 3).count());
        zonalSummary.get(municipality).put("hh4+", (double) householdMap.values().stream().filter(x ->x.getHhSize() >= 4).count());
        zonalSummary.get(municipality).put("detached", (double) dwellingMap.values().stream().filter(x ->x.getType().equals(ManchesterDwellingTypes.DwellingTypeManchester.SFD)).count());
        zonalSummary.get(municipality).put("attached", (double) dwellingMap.values().stream().filter(x ->x.getType().equals(ManchesterDwellingTypes.DwellingTypeManchester.SFA)).count());
        zonalSummary.get(municipality).put("flat", (double) dwellingMap.values().stream().filter(x ->x.getType().equals(ManchesterDwellingTypes.DwellingTypeManchester.MF234)).count());
        zonalSummary.get(municipality).put("mobileHome", (double) dwellingMap.values().stream().filter(x ->x.getType().equals(ManchesterDwellingTypes.DwellingTypeManchester.MF5plus)).count());

        zonalSummary.get(municipality).put("own", (double) dwellingMap.values().stream().filter(x ->x.getUsage().equals(DwellingUsage.OWNED)).count());
        zonalSummary.get(municipality).put("rent", (double) dwellingMap.values().stream().filter(x ->x.getUsage().equals(DwellingUsage.RENTED)).count());

        zonalSummary.get(municipality).put("pp", (double) personMap.values().stream().mapToInt(x -> x.getId()).count());
        //zonalSummary.get(municipality).put("MaleEmp", (double) personMap.values().stream().filter(x ->x.getGender().equals(Gender.MALE)&x.getOccupation().equals(Occupation.EMPLOYED)).count());
        //zonalSummary.get(municipality).put("FemEmp", (double) personMap.values().stream().filter(x ->x.getGender().equals(Gender.FEMALE)&x.getOccupation().equals(Occupation.EMPLOYED)).count());

        int[] ageBracketsGender = new int[]{4,9,15,19,24,34,49,64,74};
        double countAgeMale = 0;
        double countAgeFemale = 0;
        for (int age : ageBracketsGender){
            double maleYoungerThan = (double) personMap.values().stream().filter(x->x.getGender().equals(Gender.MALE)).filter(x->x.getAge() <= age).count();
            zonalSummary.get(municipality).put(age+"Male", maleYoungerThan - countAgeMale);
            countAgeMale = maleYoungerThan;
            double femaleYoungerThan = (double) personMap.values().stream().filter(x->x.getGender().equals(Gender.FEMALE)).filter(x->x.getAge() <= age).count();
            zonalSummary.get(municipality).put(age+"Fem", femaleYoungerThan - countAgeFemale);
            countAgeFemale = femaleYoungerThan;
        }

        zonalSummary.get(municipality).put("75Male", (double) personMap.values().stream().filter(x->x.getGender().equals(Gender.MALE)).filter(x->x.getAge() >= 75).count());
        zonalSummary.get(municipality).put("75Fem", (double) personMap.values().stream().filter(x->x.getGender().equals(Gender.FEMALE)).filter(x->x.getAge() >= 75).count());

    }

    private void allocationErrorsByZone(int municipality) {
        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("mobileHome", "mobileHome");
        attributes.put("flat", "flat");
        attributes.put("attached", "attached");
        attributes.put("detached", "detached");
        attributes.put("own", "own");
        attributes.put("rent", "rent");
        attributes.put("4Male", "4Male");
        attributes.put("4Fem", "4Fem");
        attributes.put("9Male", "9Male");
        attributes.put("9Fem", "9Fem");
        attributes.put("15Male", "15Male");
        attributes.put("15Fem", "15Fem");
        attributes.put("19Male", "19Male");
        attributes.put("19Fem", "19Fem");
        attributes.put("24Male", "24Male");
        attributes.put("24Fem", "24Fem");
        attributes.put("34Male", "34Male");
        attributes.put("34Fem", "34Fem");
        attributes.put("49Male", "49Male");
        attributes.put("49Fem", "49Fem");
        attributes.put("64Male", "64Male");
        attributes.put("64Fem", "64Fem");
        attributes.put("74Male", "74Male");
        attributes.put("74Fem", "74Fem");
        attributes.put("75Male", "75Male");
        attributes.put("75Fem", "75Fem");
        attributes.put("hh4+", "hh4+");
        attributes.put("hh3", "hh3");
        attributes.put("hh2", "hh2");
        attributes.put("hh1", "hh1");
        attributes.put("hh", "hh");
        attributes.put("pp", "pp");
        allocationErrors.putIfAbsent(municipality, new LinkedHashMap<>());
        for (String attributeMarginals : attributes.keySet()) {
            float refValue;
            if(attributeMarginals.equals("rent")) {
                refValue = PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "rentP") +
                        PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "rentS");
            }else{
                refValue = PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, attributeMarginals);
            }

            if (refValue==0){
                allocationErrors.get(municipality).put(attributes.get(attributeMarginals),0.);
            }else {
                allocationErrors.get(municipality).put(attributes.get(attributeMarginals), Math.abs((zonalSummary.get(municipality).get(attributes.get(attributeMarginals)) -
                        refValue))/refValue);
            }
        }
    }


    private void summaryByCity(int municipality) {
        Map<Integer, Household> householdMap = new HashMap<>();
        Map<Integer, Dwelling> dwellingMap = new HashMap<>();
        Map<Integer, Person> personMap = new HashMap<>();
        int ppNumber = 0;
        for (int hhNumber = 0; hhNumber < totalHouseholds; hhNumber++){
            Household hh = householdData.getHouseholdFromId(hhNumber + firstHouseholdMunicipality);
            householdMap.put(hhNumber, hh);
            dwellingMap.put(hhNumber, realEstate.getDwelling(hh.getId()));
            for (Person pp : hh.getPersons().values()){
                personMap.put(ppNumber, pp);
                ppNumber++;
            }
        }
        zonalSummary.putIfAbsent(municipality, new LinkedHashMap<>());
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
