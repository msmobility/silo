package de.tum.bgu.msm.syntheticPopulationGenerator.munich.synpopTrampa;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingUtils;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.MicroDataManager;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GenerateHouseholdsPersonsDwellingsTrampa {

    private static final Logger logger = Logger.getLogger(GenerateHouseholdsPersonsDwellingsTrampa.class);

    private final DataContainer dataContainer;

    private final DataSetSynPop dataSetSynPop;
    private int totalHouseholds;
    private Map<Integer, Float> probMicroData;
    private double[] probabilityId;
    private double[] probabilityTAZ;
    private int[] ids;
    private int[] idTAZs;

    private final HashMap<Person, Integer> educationalLevel;

    private HouseholdDataManager householdData;


    public GenerateHouseholdsPersonsDwellingsTrampa(DataContainer dataContainer, DataSetSynPop dataSetSynPop, HashMap<Person, Integer> educationalLevel){
        this.dataContainer = dataContainer;
        this.dataSetSynPop = dataSetSynPop;
        this.educationalLevel = educationalLevel;
    }

    public void run(){
        logger.info("   Running module: household, person and dwelling generation");
        householdData = dataContainer.getHouseholdDataManager();
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
    }


    private Household generateHousehold(){

        HouseholdFactory factory = householdData.getHouseholdFactory();
        int id = householdData.getNextHouseholdId();
        Household household = factory.createHousehold(id,id,0);
        householdData.addHousehold(household);
        return household;
    }


    private void generatePersons(int hhSelected, Household hh){

        int hhSize = dataSetSynPop.getHouseholdTable().get(hhSelected, "hhSize");
        PersonFactory factory = new PersonFactoryMuc();
        for (int person = 0; person < hhSize; person++) {
            int id = householdData.getNextPersonId();
            int personSelected = dataSetSynPop.getHouseholdTable().get(hhSelected, "personCount") + person;
            int age = dataSetSynPop.getPersonTable().get(personSelected, "age");
            int income = 0;
            if(person == 0) {
                income = dataSetSynPop.getHouseholdTable().get(hhSelected, "income");
            }
            Gender gender = Gender.valueOf(dataSetSynPop.getPersonTable().get(personSelected, "gender"));
            Occupation occupation = Occupation.valueOf(dataSetSynPop.getPersonTable().get(personSelected, "occupation"));
            Nationality nationality1 = Nationality.GERMAN;

            boolean license = MicroDataManager.obtainLicense(gender, age);
            int educationDegree =0;
            PersonRole personRole = PersonRole.SINGLE;
            PersonMuc pers = (PersonMuc) factory.createPerson(id, age, gender, occupation,personRole, 0, income); //(int id, int age, int gender, Race race, int occupation, int workplace, int income)
            pers.setNationality(nationality1);
            pers.setDriverLicense(license);
            householdData.addPerson(pers);
            householdData.addPersonToHousehold(pers, hh);

            final Random random = SiloUtil.getRandomObject();
            //https://www.statistik.bayern.de/mam/produkte/veroffentlichungen/statistische_berichte/a1310c_201100_36422.pdf
            if(age<18) {
                if(random.nextDouble() < 0.35) {
                    pers.setOccupation(Occupation.TODDLER);
                } else if(random.nextDouble() < 0.215) {
                    pers.setSchoolType(1);
                } else {
                    pers.setSchoolType(2);
                }
            }

            if(age == 25) {
                if(occupation == Occupation.STUDENT) {
                    pers.setSchoolType(3);
                }
            }

            educationalLevel.put(pers, educationDegree);
        }
    }


    private void generateDwelling(int hhSelected, int idHousehold, int tazSelected, int municipality){

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        int newDdId = realEstate.getNextDwellingId();
        Dwelling dwell = DwellingUtils.getFactory().createDwelling(newDdId, tazSelected, null, idHousehold, DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus,
                2, 3, 1000, 1985);
        realEstate.addDwelling(dwell);
    }


    private void initializeMunicipalityData(int municipality){

        logger.info("   Municipality " + municipality + ". Starting to generate households and persons");
        totalHouseholds = (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "hhTotal");
        probMicroData = new HashMap<>();
        probabilityId = new double[dataSetSynPop.getWeights().getRowCount()];
        ids = new int[probabilityId.length];
        for (int id : dataSetSynPop.getWeights().getColumnAsInt("ID")){
            probMicroData.put(id, dataSetSynPop.getWeights().getValueAt(id, Integer.toString(municipality)));
        }
        for (int i = 0; i < probabilityId.length; i++){
            probabilityId[i] = dataSetSynPop.getWeights().getValueAt(i+1, Integer.toString(municipality));
            ids[i] = (int) dataSetSynPop.getWeights().getValueAt(i+1, "ID");
        }
        probabilityTAZ = new double[dataSetSynPop.getProbabilityZone().get(municipality).keySet().size()];
        probabilityTAZ = dataSetSynPop.getProbabilityZone().get(municipality).values().stream().mapToDouble(Number::doubleValue).toArray();
        final double sum = Arrays.stream(probabilityTAZ).sum();
        probabilityTAZ = Arrays.stream(probabilityTAZ).map(d -> d/sum).toArray();
        for (int i = 1; i < probabilityTAZ.length; i++){
            probabilityTAZ[i] = probabilityTAZ[i] + probabilityTAZ[i-1];
        }
        idTAZs = dataSetSynPop.getProbabilityZone().get(municipality).keySet().stream().mapToInt(Number::intValue).toArray();
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

