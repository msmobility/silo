package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.MicroDataManager;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ReallocateSeniorsNursingHomev2 {

    private static final Logger logger = Logger.getLogger(ReallocateSeniorsNursingHomev2.class);

    private final DataContainer dataContainer;

    private final DataSetSynPop dataSetSynPop;
    private final MicroDataManager microDataManager;

    private HouseholdDataManager householdData;
    private RealEstateDataManager realEstate;
    private Map<Integer, Household> seniorHouseholds;
    private Map<Integer, Map<Integer, Integer>> seniorMalesinHouseholdsByCounty;
    private Map<Integer, Map<Integer, Integer>> seniorFemalesinHouseholdsByCounty;
    private Map<Integer, Map<Integer, Integer>> seniorFemalesinFemaleHouseholdsByCounty;
    private Map<Integer, ArrayList<Integer>> nursingHomesByCounty = new HashMap<>();
    private Map<Integer, Map<Integer, Integer>> nursingHomesByCountyMap = new HashMap<>();

    private Map<Integer, Map<Integer, Person>> residentsByNursingHome = new HashMap<>();
    private Map<Integer, Integer> nursingHomeTAZ = new HashMap<>();

    public ReallocateSeniorsNursingHomev2(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataContainer = dataContainer;
        this.dataSetSynPop = dataSetSynPop;
        microDataManager = new MicroDataManager(dataSetSynPop);
    }

    //TODO. Modify writers to output nursing home related information
    //TODO. Generate some statistics on how many residents are sent to nursing homes

    public void run(){
        logger.info("   Running module: reallocation of seniors");
        householdData = dataContainer.getHouseholdDataManager();
        realEstate = dataContainer.getRealEstateDataManager();
        seniorHouseholds = new HashMap<>();
        initializeNursingHomesAndSeniors();
        for (int age : PropertiesSynPop.get().main.ageBracketsPersonQuarter){
            logger.info("               Age  "+ age);
            Map<Integer, Map<String, Integer>> personsAlreadySelected = initializeData(age);
            int seniorsOfAge = 0;
            for (int county : dataSetSynPop.getCounties()){
                //logger.info("               County  "+ county );
                int nursingHomeResidents = 0;
                int femalesSelected = 0;
                int malesSelected = 0;
                //select senior households based on males
                String columnName = "male"+age;
                int count = (int) PropertiesSynPop.get().main.nursingHomeResidents.getIndexedValueAt(county, columnName);
                count = count - personsAlreadySelected.get(county).get("male");
                if (nursingHomesByCountyMap.get(county).size() > 0) {
                    Map<Integer, Integer> hhSelection = selectMicroHouseholdWithReplacement(count,
                            seniorMalesinHouseholdsByCounty.get(county));
                    Map<Integer, Integer> nursingHomeSelection = selectNursingHomesWithoutReplacement(count,
                            nursingHomesByCountyMap.get(county));
                    for (int draw = 1; draw <= hhSelection.size(); draw++) {
                        if (malesSelected < count) {
                            Household hhSelected = householdData.getHouseholdFromId(hhSelection.get(draw));
                            //if (hhSelected.getDwellingId() != -1) {
                                realEstate.getDwelling(hhSelected.getDwellingId()).setResidentID(-1);
                            //}
                            hhSelected.setDwelling(-1);
                            hhSelected.setAttribute("Nursing_home", "yes");
                            hhSelected.setAttribute("Nursing_home_id", nursingHomeSelection.get(draw));
                            hhSelected.setAttribute("Nursing_home_zone", nursingHomeTAZ.get(nursingHomeSelection.get(draw)));

                            for (Person pp : hhSelected.getPersons().values()) {
                                residentsByNursingHome.get(nursingHomeSelection.get(draw)).put(pp.getId(), pp);
                                int row = 0;
                                while (pp.getAge() > PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]) {
                                    row++;
                                }
                                if (age == PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]){
                                    if (pp.getGender().equals(Gender.FEMALE)) {
                                        femalesSelected++;
                                    } else {
                                        malesSelected++;
                                    }
                                }
                                nursingHomeResidents++;
                                seniorsOfAge++;
                            }
                        }
                    }
                }

                //for remaining females
                String columnName1 = "female" + age;
                int count2 = (int) PropertiesSynPop.get().main.nursingHomeResidents.getIndexedValueAt(county, columnName1);
                count2 = count2 - femalesSelected - personsAlreadySelected.get(county).get("female");
                if (count2 > 0) {
                    if (nursingHomesByCountyMap.get(county).size() > 0) {
                        Map<Integer, Integer> hhSelection = selectMicroHouseholdWithReplacement(count2,
                                seniorFemalesinFemaleHouseholdsByCounty.get(county));
                        Map<Integer, Integer> nursingHomeSelection = selectNursingHomesWithoutReplacement(count2,
                                nursingHomesByCountyMap.get(county));
                        int selected = 0;
                        for (int draw = 1; draw <= hhSelection.size(); draw++) {
                            if (selected < count2){
/*                                if (county == 9163){
                                    logger.info("   Age "  + age + " draw "+ draw);
                                }*/
                                Household hhSelected = householdData.getHouseholdFromId(hhSelection.get(draw));
                                //if (hhSelected.getDwellingId() != -1) {
                                    realEstate.getDwelling(hhSelected.getDwellingId()).setResidentID(-1);
                                //}
                                hhSelected.setDwelling(-1);
                                hhSelected.setAttribute("Nursing_home", "yes");
                                hhSelected.setAttribute("Nursing_home_id", nursingHomeSelection.get(draw));
                                hhSelected.setAttribute("Nursing_home_zone", nursingHomeTAZ.get(nursingHomeSelection.get(draw)));
                                for (Person pp : hhSelected.getPersons().values()) {
                                    residentsByNursingHome.get(nursingHomeSelection.get(draw)).put(pp.getId(), pp);
                                    //check if there is another female that is older, and, in that case, remove that household from the list of "selectable"
                                    int row = 0;
                                    while (pp.getAge() > PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]) {
                                        row++;
                                    }
                                    if (age == PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]){
                                        selected++;
                                    }
                                    nursingHomeResidents++;
                                    seniorsOfAge++;
                                }
                            }
                        }
                    }
                }
                logger.info(" County " + county + ". Nursing home persons " + nursingHomeResidents);
            }
            logger.info(" Age " + age + ". Nursing home persons " + seniorsOfAge);
        }

        dataSetSynPop.setResidentsByNursingHome(residentsByNursingHome);
    }


    private Map<Integer, Map<String, Integer>> initializeData(int age){

        Map<Integer, Map<String, Integer>> personsAlreadySelected = new HashMap<>();
        seniorMalesinHouseholdsByCounty = new HashMap<>(); //Map<Integer for county, Map<Integer for hhId, Integer>>
        seniorFemalesinHouseholdsByCounty = new HashMap<>();
        seniorFemalesinFemaleHouseholdsByCounty = new HashMap<>(); //Map<Integer, Map<Integer, Map<Integer, Integer>>>
        for (int counties : dataSetSynPop.getCounties()){
            seniorMalesinHouseholdsByCounty.putIfAbsent(counties,new HashMap<>());
            seniorFemalesinHouseholdsByCounty.putIfAbsent(counties,new HashMap<>());
            seniorFemalesinFemaleHouseholdsByCounty.putIfAbsent(counties,new HashMap<>());
            personsAlreadySelected.putIfAbsent(counties, new HashMap<>());
            personsAlreadySelected.get(counties).putIfAbsent("male", 0);
            personsAlreadySelected.get(counties).put("female", 0);
            personsAlreadySelected.get(counties).put("femaleOnly", 0);
        }
        for (Household hh : seniorHouseholds.values()){
            int taz = 0;
            if (hh.getAttribute("Nursing_home").get().toString().equals("no")) {
                taz = realEstate.getDwelling(hh.getDwellingId()).getZoneId();

            } else {
                taz = Integer.parseInt(hh.getAttribute("Nursing_home_zone").get().toString());
            }
            int county = (int) PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(taz, "ID_county");
            int malesofAgeinHousehold = 0;
            int femalesofAgeinHousehold = 0;
            boolean onlyFemales = verifyIfAllSeniorFemales(hh);
            int femalesofAgeinHouseholdOnlyFemales = 0;
            for (Person pp : hh.getPersons().values()){
                int agePerson = pp.getAge();
                int row = 0;
                while (agePerson > PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]) {
                    row++;
                }
               if (age == PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]){
                   if(pp.getGender().equals(Gender.MALE)){
                       malesofAgeinHousehold++;
                   } else {
                       femalesofAgeinHousehold++;
                       if (onlyFemales) {
                           femalesofAgeinHouseholdOnlyFemales++;
                       }
                   }
               }
                if (hh.getAttribute("Nursing_home").get().toString().equals("no")){
                    if (malesofAgeinHousehold > 0) {
                        seniorMalesinHouseholdsByCounty.get(county).put(hh.getId(), malesofAgeinHousehold);
                    }
                    if (femalesofAgeinHousehold > 0) {
                        seniorFemalesinHouseholdsByCounty.get(county).put(hh.getId(), femalesofAgeinHousehold);
                    }
                    if (femalesofAgeinHouseholdOnlyFemales > 0) {
                        seniorFemalesinFemaleHouseholdsByCounty.get(county).put(hh.getId(), femalesofAgeinHouseholdOnlyFemales);
                    }
                } else {
                    personsAlreadySelected.get(county).put("male",personsAlreadySelected.get(county).get("male") + malesofAgeinHousehold);
                    personsAlreadySelected.get(county).put("female",personsAlreadySelected.get(county).get("female") + femalesofAgeinHousehold);
                    personsAlreadySelected.get(county).put("femaleOnly",personsAlreadySelected.get(county).get("femaleOnly") + femalesofAgeinHouseholdOnlyFemales);
                }
            }

        }

        return personsAlreadySelected;
    }


    private void initializeNursingHomesAndSeniors(){

        for (int counties : dataSetSynPop.getCounties()) {
            nursingHomesByCounty.putIfAbsent(counties, new ArrayList<>());
            nursingHomesByCountyMap.putIfAbsent(counties, new HashMap<>());
        }
        for (int id : PropertiesSynPop.get().main.nursingHomes.getColumnAsInt("n_home_id")) {
            int zone = (int) PropertiesSynPop.get().main.nursingHomes.getIndexedValueAt(id, "ID_cell");
            int county = (int) PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(zone,"ID_county");
            nursingHomesByCounty.get(county).add(id);
            nursingHomesByCountyMap.get(county).put(id, 1);
            residentsByNursingHome.put(id, new HashMap<>());
            nursingHomeTAZ.put(id, zone);
        }
        for (Household hh : householdData.getHouseholds()) {
            hh.setAttribute("Nursing_home", "no");
            hh.setAttribute("Nursing_home_id", -1);
            hh.setAttribute("Nursing_home_zone", realEstate.getDwelling(hh.getDwellingId()).getZoneId());
            boolean allSenior = verifyIfAllSenior(hh);
            if (allSenior) {
                seniorHouseholds.putIfAbsent(hh.getId(), hh);
            }
        }
    }
    private boolean verifyIfAllSenior(Household hh){
        boolean condition = true;
        for (Person pp : hh.getPersons().values()){
            if (pp.getAge()<= 65){
                condition = false;
                break;
            }
        }
        return condition;
    }


    private boolean verifyIfAllSeniorFemales(Household hh){
        boolean condition = true;
        for (Person pp : hh.getPersons().values()){
            if (pp.getGender().equals(Gender.MALE)){
                condition = false;
                break;
            }
        }
        return condition;
    }


    public int[] selectMultipleObjectsWithProbability(int selections, Integer[] ids, Integer[] probabilityId) {

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

    public int[] selectMultipleObjectsEqualProbability(int selections, Integer[] ids) {

        int[] selected;
        double probabilityOfEach = 1;
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
            double cumulative = 1;
            for (double randomNumber : randomChoices){
                while (randomNumber > cumulative && p < ids.length - 1) {
                    p++;
                    cumulative += probabilityOfEach;
                }
                if (probabilityOfEach > 0) {
                    selected[completed] = ids[p];
                    completed++;
                }
            }
        }
        return selected;

    }

    private Map<Integer, Integer> selectMicroHouseholdWithReplacement(int selections, Map<Integer, Integer> households) {

        Map<Integer, Integer> selectedHouseholds = new HashMap<>();
        for (int i = 1; i <= selections; i++) {
            Integer hhSelected = SiloUtil.select(households);
            selectedHouseholds.putIfAbsent(i, hhSelected);
            households.remove(hhSelected);
            if (households.size() < 1){
                break;
            }
        }

        return selectedHouseholds;
    }

    private Map<Integer, Integer> selectNursingHomesWithoutReplacement(int selections, Map<Integer, Integer> households) {

        Map<Integer, Integer> selectedHouseholds = new HashMap<>();
        for (int i = 1; i <= selections; i++) {
            Integer hhSelected = SiloUtil.select(households);
            selectedHouseholds.putIfAbsent(i, hhSelected);
        }

        return selectedHouseholds;
    }

}
