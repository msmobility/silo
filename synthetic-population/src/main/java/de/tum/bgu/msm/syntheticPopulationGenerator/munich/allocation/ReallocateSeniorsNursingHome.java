package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.MicroDataManager;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ReallocateSeniorsNursingHome {

    private static final Logger logger = Logger.getLogger(ReallocateSeniorsNursingHome.class);

    private final DataContainer dataContainer;

    private final DataSetSynPop dataSetSynPop;
    private final MicroDataManager microDataManager;

    private HouseholdDataManager householdData;
    private RealEstateDataManager realEstate;
    private Map<Integer, Map<Integer, Map<Integer, Integer>>> seniorMalesinHouseholdsByCounty;
    private Map<Integer, Map<Integer, Map<Integer, Integer>>> seniorFemalesinHouseholdsByCounty;
    private Map<Integer, Map<Integer, Map<Integer, Integer>>> seniorFemalesinFemaleHouseholdsByCounty;
    private Map<Integer, ArrayList<Integer>> nursingHomesByCounty = new HashMap<>();

    private Map<Integer, Map<Integer, Person>> residentsByNursingHome = new HashMap<>();
    private Map<Integer, Integer> nursingHomeTAZ = new HashMap<>();

    public ReallocateSeniorsNursingHome(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
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
        initializeData();
        for (int county : dataSetSynPop.getCounties()){
            logger.info("               County  "+ county );
            double logging = 2;
            int it = 1;
            int nursingHomeResidents = 0;
            Map<Integer, Integer> femalesSelected = new HashMap<>();
            Map<Integer, Integer> malesSelected = new HashMap<>();
            for (int age : PropertiesSynPop.get().main.ageBracketsPersonQuarter) {
                femalesSelected.put(age, 0);
                malesSelected.put(age, 0);
            }
            //select senior households based on males
            for (int age : PropertiesSynPop.get().main.ageBracketsPersonQuarter){
                String columnName = "male"+age;
                int count = (int) PropertiesSynPop.get().main.nursingHomeResidents.getIndexedValueAt(county, columnName);
                count = count - malesSelected.get(age);
                if (nursingHomesByCounty.get(county).size() > 0) {
                    Map<Integer, Integer> hhSelection = selectMicroHouseholdWithReplacement(count,
                            seniorMalesinHouseholdsByCounty.get(county).get(age));
                    int[] nursingHomeSelection = selectMultipleObjectsEqualProbability(count,
                            nursingHomesByCounty.get(county).toArray(new Integer[0]));
                    int selected = 0;
                    for (int draw = 1; draw <= hhSelection.size(); draw++) {
                        if (selected <= count) {
                        Household hhSelected = householdData.getHouseholdFromId(hhSelection.get(draw));
                            if (hhSelected.getDwellingId() != -1) {
                                realEstate.getDwelling(hhSelected.getDwellingId()).setResidentID(-1);
                            }
                        hhSelected.setDwelling(-1);
                        hhSelected.setAttribute("Nursing_home", "yes");
                        hhSelected.setAttribute("Nursing_home_id", nursingHomeSelection[draw - 1]);
                        hhSelected.setAttribute("Nursing_home_zone", nursingHomeTAZ.get(nursingHomeSelection[draw - 1]));
                        for (Person pp : hhSelected.getPersons().values()) {
                            residentsByNursingHome.get(nursingHomeSelection[draw - 1]).put(pp.getId(), pp);
                            int row = 0;
                            while (pp.getAge() > PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]) {
                                row++;
                            }
                            if (pp.getGender().equals(Gender.FEMALE)) {
                                int previous = femalesSelected.get(PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]);
                                femalesSelected.put(PropertiesSynPop.get().main.ageBracketsPersonQuarter[row], previous + 1);
                            } else {
                                if (age == PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]){
                                    selected = selected + 1;
                                }
                                int previous = malesSelected.get(PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]);
                                malesSelected.put(PropertiesSynPop.get().main.ageBracketsPersonQuarter[row], previous + 1);
                            }
                            nursingHomeResidents++;
                        }
/*                        for (int ageFemales : PropertiesSynPop.get().main.ageBracketsPersonQuarter) {
                            if (seniorFemalesinHouseholdsByCounty.get(county).get(age).containsKey(hhSelected.getId())) {
                                int previousFemales = femalesSelected.get(ageFemales);
                                femalesSelected.put(ageFemales, seniorFemalesinHouseholdsByCounty.get(county).get(age).get(hhSelected.getId()) + previousFemales);
                            }
                        }*/

                        }
                    }
                }
            }
            //for remaining females
            for (int age : PropertiesSynPop.get().main.ageBracketsPersonQuarter) {
                //if (age == 120 && county == 9163) {
                    //some issue with this age bracket and county. Skip for now and analyze individually
                //}else {
                    String columnName = "female" + age;
                    int count = (int) PropertiesSynPop.get().main.nursingHomeResidents.getIndexedValueAt(county, columnName);
                    count = count - femalesSelected.get(age);
                    if (count > 0) {
                        if (nursingHomesByCounty.get(county).size() > 0) {
                            Map<Integer, Integer> hhSelection = selectMicroHouseholdWithReplacement(count,
                                    seniorFemalesinFemaleHouseholdsByCounty.get(county).get(age));
                            int[] nursingHomeSelection = selectMultipleObjectsEqualProbability(count,
                                    nursingHomesByCounty.get(county).toArray(new Integer[0]));
                            int selected = 0;
                            for (int draw = 1; draw <= hhSelection.size(); draw++) {
                                if (selected <= hhSelection.size()){
                                Household hhSelected = householdData.getHouseholdFromId(hhSelection.get(draw));
                                if (hhSelected.getDwellingId() != -1) {
                                    realEstate.getDwelling(hhSelected.getDwellingId()).setResidentID(-1);
                                }
                                hhSelected.setDwelling(-1);
                                hhSelected.setAttribute("Nursing_home", true);
                                hhSelected.setAttribute("Nursing_home_id", nursingHomeSelection[draw - 1]);
                                hhSelected.setAttribute("Nursing_home_zone", nursingHomeTAZ.get(nursingHomeSelection[draw - 1]));
                                for (Person pp : hhSelected.getPersons().values()) {
                                    residentsByNursingHome.get(nursingHomeSelection[draw - 1]).put(pp.getId(), pp);
                                    //check if there is another female that is older, and, in that case, remove that household from the list of "selectable"
                                    int row = 0;
                                    while (pp.getAge() > PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]) {
                                        row++;
                                    }
                                    int previous = femalesSelected.get(PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]);
                                    femalesSelected.put(PropertiesSynPop.get().main.ageBracketsPersonQuarter[row], previous + 1);
                                    if (age == PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]){
                                        selected = selected + 1;
                                    } else {
                                        if (seniorFemalesinFemaleHouseholdsByCounty.get(county).get(age).containsKey(hhSelected.getId())){
                                            seniorFemalesinFemaleHouseholdsByCounty.get(county).get(age).remove(hhSelected.getId());
                                        }
                                    }
                                    nursingHomeResidents++;
                                }
/*                                if (seniorFemalesinFemaleHouseholdsByCounty.get(county).get(age).containsKey(hhSelected.getId())) {
                                    selected = selected + seniorFemalesinFemaleHouseholdsByCounty.get(county).get(age).get(hhSelected.getId());
                                }*/
/*                                for (int ageFemales : PropertiesSynPop.get().main.ageBracketsPersonQuarter) {
                                    if (seniorFemalesinFemaleHouseholdsByCounty.get(county).get(age).containsKey(hhSelected.getId())) {
                                        int previousFemales = femalesSelected.get(ageFemales);
                                        femalesSelected.put(ageFemales, seniorFemalesinFemaleHouseholdsByCounty.get(county).get(age).get(hhSelected.getId()) + previousFemales);
                                    }
                                }*/
                            }
                        }
                    }
                }
            }
            logger.info(" County " + county + ". Nursing home persons " + nursingHomeResidents);
        }

        dataSetSynPop.setResidentsByNursingHome(residentsByNursingHome);
    }


    private void initializeData(){
        seniorMalesinHouseholdsByCounty = new HashMap<>(); //Map<Integer, Map<Integer, Map<Integer, Integer>>>
        seniorFemalesinHouseholdsByCounty = new HashMap<>();
        seniorFemalesinFemaleHouseholdsByCounty = new HashMap<>(); //Map<Integer, Map<Integer, Map<Integer, Integer>>>
        for (int counties : dataSetSynPop.getCounties()){
            seniorMalesinHouseholdsByCounty.putIfAbsent(counties,new HashMap<>());
            seniorFemalesinHouseholdsByCounty.putIfAbsent(counties,new HashMap<>());
            seniorFemalesinFemaleHouseholdsByCounty.putIfAbsent(counties,new HashMap<>());
            nursingHomesByCounty.putIfAbsent(counties, new ArrayList<>());
            for (int age : PropertiesSynPop.get().main.ageBracketsPersonQuarter){
                seniorMalesinHouseholdsByCounty.get(counties).put(age, new HashMap<>());
                seniorFemalesinHouseholdsByCounty.get(counties).put(age, new HashMap<>());
                seniorFemalesinFemaleHouseholdsByCounty.get(counties).put(age, new HashMap<>());
            }
        }
        for (Household hh : householdData.getHouseholds()){
            hh.setAttribute("Nursing_home", "no");
            hh.setAttribute("Nursing_home_id", -1);
            boolean allSenior = verifyIfAllSenior(hh);
            if (allSenior){
                int taz = realEstate.getDwelling(hh.getDwellingId()).getZoneId();
                int county = (int) PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(taz,"ID_county");
                HashMap<Integer, Integer> males = new HashMap<>();
                HashMap<Integer, Integer> females = new HashMap<>();
                HashMap<Integer, Integer> femalesOnly = new HashMap<>();
                for (int age : PropertiesSynPop.get().main.ageBracketsPersonQuarter){
                    males.put(age, 0);
                    females.put(age, 0);
                    femalesOnly.put(age, 0);
                }
                for (Person pp : hh.getPersons().values()){
                    int age = pp.getAge();
                    int row = 0;
                    while (age > PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]) {
                        row++;
                    }
                    if(pp.getGender().equals(Gender.MALE)){
                        int countMale = males.get(PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]);
                        males.put(PropertiesSynPop.get().main.ageBracketsPersonQuarter[row], countMale+1);

                    } else {
                        int countfeMale = females.get(PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]);
                        females.put(PropertiesSynPop.get().main.ageBracketsPersonQuarter[row], countfeMale+1);
                        boolean onlyFemales = verifyIfAllSeniorFemales(hh);
                        if (onlyFemales) {
                            int countfeMaleonly = femalesOnly.get(PropertiesSynPop.get().main.ageBracketsPersonQuarter[row]);
                            femalesOnly.put(PropertiesSynPop.get().main.ageBracketsPersonQuarter[row], countfeMaleonly+1);
                        }
                    }
                }
                for (int age : PropertiesSynPop.get().main.ageBracketsPersonQuarter){
                    if (males.get(age) > 0){
                        seniorMalesinHouseholdsByCounty.get(county).get(age).put(hh.getId(), males.get(age));
                    };
                    if (females.get(age) > 0){
                        seniorFemalesinHouseholdsByCounty.get(county).get(age).put(hh.getId(), females.get(age));
                    };
                    if (femalesOnly.get(age) > 0){
                        seniorFemalesinFemaleHouseholdsByCounty.get(county).get(age).put(hh.getId(), femalesOnly.get(age));
                    };
                }
            }
        }
        for (int id : PropertiesSynPop.get().main.nursingHomes.getColumnAsInt("n_home_id")) {
            int zone = (int) PropertiesSynPop.get().main.nursingHomes.getIndexedValueAt(id, "ID_cell");
            int county = (int) PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(zone,"ID_county");
            nursingHomesByCounty.get(county).add(id);
            residentsByNursingHome.put(id, new HashMap<>());
            nursingHomeTAZ.put(id, zone);
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

}
