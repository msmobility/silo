package de.tum.bgu.msm.syntheticPopulationGenerator.bangkok.preparation;


import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CheckHouseholdRelationship {

    private static final Logger logger = Logger.getLogger(CheckHouseholdRelationship.class);

    private final DataSetSynPop dataSetSynPop;
    private TableDataSet microDataHousehold;
    private TableDataSet microDataPerson;

    private HashMap<Integer, Integer> childrenInHousehold;
    private HashMap<String, HashMap<Integer, Integer>> possibleMarried;
    private HashMap<String, HashMap<Integer, Integer>> noClass;
    private HashMap<String, HashMap<Integer, Integer>> singles;
    private HashMap<String, HashMap<Integer, Integer>> married;
    private HashMap<String, HashMap<Integer, Integer>> headCouple;


    public CheckHouseholdRelationship(DataSetSynPop dataSetSynPop){this.dataSetSynPop = dataSetSynPop;}

    public void run(){
        initialize();
        for (int household : microDataHousehold.getColumnAsInt("hhThaiId")){
            int hhSize = (int) microDataHousehold.getValueAt(household, "hhSize");
            int firstMember = (int) microDataHousehold.getValueAt(household, "id_firstPerson");
            if (hhSize == 1){
                setPersonAsSingle(firstMember);
            } else {
                obtainRolesInHousehold(firstMember, hhSize);
                checkHouseholdRelationship();
                checkCohabitation();
                setRoles(singles, married, childrenInHousehold, noClass);
            }
            resetMaps();
        }
        dataSetSynPop.setPersonDataSet(microDataPerson);
        String hhFileName = ("microData/interimFiles/microHouseholds2.csv");
        SiloUtil.writeTableDataSet(microDataHousehold, hhFileName);

        String ppFileName = ("microData/interimFiles/microPerson2.csv");
        SiloUtil.writeTableDataSet(microDataPerson, ppFileName);
    }


    private void obtainRolesInHousehold(int firstMember, int hhSize){

            for (int j = 0; j < hhSize; j++) {
                int row = firstMember + j;
                int relationToHead = (int) microDataPerson.getValueAt(row, "relationshipCode");
                int age = (int) microDataPerson.getValueAt(row, "age");
                int gender =(int) microDataPerson.getValueAt(row, "gender");

                if (relationToHead == 3) {
                    childrenInHousehold.put(row, age); //children -> children
                } else if (age < 15) {
                    childrenInHousehold.put(row, age); //children -> children
                } else if (relationToHead == 1) { //household head. If there is a spouse, get them married
                    headCouple = updateInnerMap(headCouple, gender, age, row);
                } else if (relationToHead == 2) { //household head spouse. If there is a head, get them married
                    headCouple = updateInnerMap(headCouple, gender, age, row);
                } else if (relationToHead == 6){
                    singles = updateInnerMap(singles, gender, age, row); //household worker -> single
                } else {
                    noClass = updateInnerMap(noClass, gender, age, row); //need further classification at the household level. Look for cohabitation
                }

        }
    }



    private void checkHouseholdRelationship(){
        //method to check how household members are related

                //check marriage household head
                if (headCouple.get("male")!=null ){
                    //male
                    int ageMale = 0;
                    int rowMale = 0;
                    for (Map.Entry<Integer, Integer> pair : headCouple.get("male").entrySet()) {
                        ageMale = pair.getValue();
                        rowMale = pair.getKey();
                    }
                    if (headCouple.get("female")!=null ){
                        int ageFemale= 0;
                        int rowFemale = 0;
                        for (Map.Entry<Integer, Integer> pair : headCouple.get("female").entrySet()) {
                            ageFemale = pair.getValue();
                            rowFemale = pair.getKey();
                        }
                        married = updateInnerMap(married, 1, ageMale, rowMale);
                        married = updateInnerMap(married, 2, ageFemale, rowFemale);
                    } else {
                        singles = updateInnerMap(singles, 1, ageMale, rowMale);
                    }
                } else {
                    if (headCouple.get("female")!=null ){
                        int ageFemale= 0;
                        int rowFemale = 0;
                        for (Map.Entry<Integer, Integer> pair : headCouple.get("female").entrySet()) {
                            ageFemale = pair.getValue();
                            rowFemale = pair.getKey();
                        }
                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                    }
                }
    }

        private void checkCohabitation(){
                //check cohabitation
                int[] countNotClassified = new int[2];
                int checkForCohabitation = 0;
                HashMap<Integer, Integer> notClassifiedMales = noClass.get("male");
                if (notClassifiedMales != null){
                    countNotClassified[0] = notClassifiedMales.size();
                    checkForCohabitation = 1;
                } else {
                    countNotClassified[0] = 0;
                }
                HashMap<Integer, Integer> notClassifiedFemales = noClass.get("female");
                if (notClassifiedFemales != null){
                    countNotClassified[1] = notClassifiedFemales.size();
                    checkForCohabitation = 1;
                } else {
                    countNotClassified[1] = 0;
                }
                if (checkForCohabitation == 1) {
                    //look for cohabitation
                    if (countNotClassified[0] == countNotClassified[1]) { //one male and one female were not classified
                        for (int possibleMales = 1; possibleMales <= countNotClassified[0]; possibleMales++) {
                            //check for a possible single male to get married (408 households with)
                            int rowFemale = (int) notClassifiedFemales.keySet().toArray()[possibleMales - 1];
                            int ageFemale = notClassifiedFemales.get(rowFemale);
                            HashMap<Integer, Integer> singleMale = singles.get("male");
                            if (singleMale == null & notClassifiedMales == null) { //no possible single male to marry -> set as single
                                singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                            } else if (singleMale != null) { //check for marriage with the male with the lowest age difference
                                int minDiff = 20;
                                int rowMarried = 0;
                                int[] rowSingles = new int[singleMale.size()];
                                for (Map.Entry<Integer, Integer> pair : singleMale.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageFemale - age) < minDiff) {
                                        minDiff = Math.abs(ageFemale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, minDiff, rowMarried);
                                        married = updateInnerMap(married, 2, ageFemale, rowFemale);
                                    } else {
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                }

                            } else {
                                int minDiff = 20;
                                int rowMarried = 0;
                                for (Map.Entry<Integer, Integer> pair : notClassifiedMales.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageFemale - age) < minDiff) {
                                        minDiff = Math.abs(ageFemale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, minDiff, rowMarried);
                                        married = updateInnerMap(married, 2, ageFemale, rowFemale);
                                    } else {
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                        singles = updateInnerMap(singles, 1, minDiff, rowMarried);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    for (int i = 0; i < notClassifiedMales.keySet().toArray().length; i++) {
                                        int rowMale = (int) notClassifiedMales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 1, ageFemale, rowMale);
                                    }
                                }
                                if (notClassifiedFemales.keySet().toArray().length > 1) {
                                    for (int i = 1; i < notClassifiedFemales.keySet().toArray().length; i++) {
                                        rowFemale = (int) notClassifiedFemales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    }
                                }
                            }
                        }
                    } else if (countNotClassified[0] == 0 & countNotClassified[1] > 0) { //only females were not classified
                        //set all of them as single
                        for (Map.Entry<Integer, Integer> pair : notClassifiedFemales.entrySet()) {
                            int newRow = pair.getKey();
                            int newAge = pair.getValue();
                            singles = updateInnerMap(singles, 2, newAge, newRow);
                        }
                    } else if (countNotClassified[1] == 0 & countNotClassified[0] > 0) { //only males were not classified
                        // set all of them as single
                        for (Map.Entry<Integer, Integer> pair : notClassifiedMales.entrySet()) {
                            int newRow = pair.getKey();
                            int newAge = pair.getValue();
                            singles = updateInnerMap(singles, 1, newAge, newRow);
                        }
                    } else if (countNotClassified[1] > countNotClassified[0]) {  //more females are not classified
                        for (int possibleMales = 1; possibleMales <= countNotClassified[0]; possibleMales++) {
                            //check for a possible single male to get married (408 households with)
                            int rowFemale = (int) notClassifiedFemales.keySet().toArray()[possibleMales - 1];
                            int ageFemale = notClassifiedFemales.get(rowFemale);
                            HashMap<Integer, Integer> singleMale = singles.get("male");
                            if (singleMale == null & notClassifiedMales == null) { //no possible single male to marry -> set as single
                                singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                            } else if (singleMale != null) { //check for marriage with the male with the lowest age difference
                                int minDiff = 20;
                                int rowMarried = 0;
                                int[] rowSingles = new int[singleMale.size()];
                                for (Map.Entry<Integer, Integer> pair : singleMale.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageFemale - age) < minDiff) {
                                        minDiff = Math.abs(ageFemale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, minDiff, rowMarried);
                                        married = updateInnerMap(married, 2, ageFemale, rowFemale);
                                    } else {
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                }

                            } else {
                                int minDiff = 20;
                                int rowMarried = 0;
                                for (Map.Entry<Integer, Integer> pair : notClassifiedMales.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageFemale - age) < minDiff) {
                                        minDiff = Math.abs(ageFemale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, minDiff, rowMarried);
                                        married = updateInnerMap(married, 2, ageFemale, rowFemale);
                                    } else {
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                        singles = updateInnerMap(singles, 1, minDiff, rowMarried);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    for (int i = 0; i < notClassifiedMales.keySet().toArray().length; i++) {
                                        int rowMale = (int) notClassifiedMales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 1, ageFemale, rowMale);
                                    }
                                }
                                if (notClassifiedFemales.keySet().toArray().length > 1) {
                                    for (int i = 1; i < notClassifiedFemales.keySet().toArray().length; i++) {
                                        rowFemale = (int) notClassifiedFemales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                                    }
                                }
                            }
                        }
                    } else if (countNotClassified[0] > countNotClassified[1]) { //check for a possible single female to get married (94 households)
                        for (int possibleFemales = 1; possibleFemales <= countNotClassified[1]; possibleFemales++) {
                            int rowMale = (int) notClassifiedMales.keySet().toArray()[possibleFemales -1];
                            int ageMale = notClassifiedMales.get(rowMale);
                            HashMap<Integer, Integer> singleFemale = singles.get("female");
                            if (singleFemale == null & notClassifiedFemales == null) { //no possible single female to marry -> set as single
                                singles = updateInnerMap(singles, 1, ageMale, rowMale);
                            } else if (singleFemale != null) { //check for marriage with the female with the lowest age difference
                                int minDiff = 20;
                                int rowMarried = 0;
                                for (Map.Entry<Integer, Integer> pair : singleFemale.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageMale - age) < minDiff) {
                                        minDiff = Math.abs(ageMale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, ageMale, rowMale);
                                        married = updateInnerMap(married, 2, minDiff, rowMarried);
                                    } else {
                                        singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                }
                            } else {
                                int minDiff = 20;
                                int rowMarried = 0;
                                for (Map.Entry<Integer, Integer> pair : notClassifiedFemales.entrySet()) {
                                    int age = pair.getValue();
                                    if (Math.abs(ageMale - age) < minDiff) {
                                        minDiff = Math.abs(ageMale - age);
                                        rowMarried = pair.getKey();
                                    }
                                }
                                if (rowMarried > 0) {
                                    double threshold = (20 - minDiff) / 10;
                                    if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                                        married = updateInnerMap(married, 1, ageMale, rowMale);
                                        married = updateInnerMap(married, 2, minDiff, rowMarried);
                                    } else {
                                        singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                        singles = updateInnerMap(singles, 2, minDiff, rowMarried);
                                    }
                                } else {
                                    singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                    for (int i = 0; i < notClassifiedFemales.keySet().toArray().length; i++) {
                                        rowMale = (int) notClassifiedFemales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 2, ageMale, rowMale);
                                    }
                                }
                                if (notClassifiedMales.keySet().toArray().length > 1) {
                                    for (int i = 1; i < notClassifiedMales.keySet().toArray().length; i++) {
                                        rowMale = (int) notClassifiedMales.keySet().toArray()[i];
                                        singles = updateInnerMap(singles, 1, ageMale, rowMale);
                                    }
                                }
                            }
                        }
                    } else {
                        logger.info("   Case without treatment. Please check this household " );
                    }
                }

                //check for only one married person in the household

        }

/*        //double checking for persons that are not classified
        for (int i = 1; i <= microDataPerson.getRowCount(); i++){
            if (microDataPerson.getValueAt(i,"personRole") == -1){
                microDataPerson.setValueAt(i, "personRole", 1);
            }
        }

        String hhFileName = ("microData/interimFiles/microHouseholds2.csv");
        SiloUtil.writeTableDataSet(microDataHousehold, hhFileName);

        String ppFileName = ("microData/interimFiles/microPerson2.csv");
        SiloUtil.writeTableDataSet(microDataPerson, ppFileName);*/



    private void setRoles(HashMap<String, HashMap<Integer, Integer>> singles, HashMap<String, HashMap<Integer, Integer>> married,
                          HashMap<Integer, Integer> childrenInHousehold, HashMap<String, HashMap<Integer, Integer>> noClass) {

        //set children in the household
        if (childrenInHousehold != null){
            for (Map.Entry<Integer, Integer> pair : childrenInHousehold.entrySet()){
                int row = pair.getKey();
                microDataPerson.setValueAt(row, "personRole", 3);
            }
        }
        //set singles and married in the household
        String[] keys = {"male", "female"};
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            HashMap<Integer, Integer> inner = singles.get(key);
            if (inner != null) {
                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
                    int row = pair.getKey();
                    microDataPerson.setValueAt(row, "personRole", 1);
                }
            }
            inner = married.get(key);
            if (inner != null) {
                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
                    int row = pair.getKey();
                    microDataPerson.setValueAt(row, "personRole", 2);
                }
            }
            inner = noClass.get(key);
            if (inner != null) {
                for (Map.Entry<Integer, Integer> pair : inner.entrySet()) {
                    int row = pair.getKey();
                    microDataPerson.setValueAt(row, "rearrangedRole", 1);
                }
            }
        }
    }


    private void setPersonAsSingle(int ppID){
        microDataPerson.setValueAt(ppID, "personRole", 1);
        /*dataSetSynPop.getPersons().get(ppID).put("personRole", 1);
        dataSetSynPop.getPersonTable().put(ppID, "personRole", 1);*/
    }


    private void checkPossibleMarriage(HashMap<Integer, Integer> notClassifiedMales, HashMap<Integer, Integer> notClassifiedFemales){
        int rowMale = (int) notClassifiedMales.keySet().toArray()[0];
        int rowFemale = (int) notClassifiedFemales.keySet().toArray()[0];
        int ageMale = notClassifiedMales.get(rowMale);
        int ageFemale = notClassifiedFemales.get(rowFemale);
        int diffAge = Math.abs(ageFemale - ageMale);
        double threshold = (20 - diffAge) / 10;
        if (SiloUtil.getRandomNumberAsDouble() < threshold) {
            married = updateInnerMap(married, 1, ageMale, rowMale);
            married = updateInnerMap(married, 2, ageFemale, rowFemale);
        } else {
            singles = updateInnerMap(singles, 1, ageMale, rowMale);
            singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
        }
    }


    private void setAsSingle(HashMap<Integer, Integer> notClassified, String gender) {
        int genderInt = 1;
        if (gender.equals("female")){
            genderInt = 2;
        }
        for (Map.Entry<Integer, Integer> pair : notClassified.entrySet()) {
            int newRow = pair.getKey();
            int newAge = pair.getValue();
            singles = updateInnerMap(singles, genderInt, newAge, newRow);
        }
    }


    private void checkMostLikelyMaleForPossibleMarriage(HashMap<Integer, Integer> notClassifiedMales, HashMap<Integer, Integer> notClassifiedFemales){
        int rowFemale = (int) notClassifiedFemales.keySet().toArray()[0];
        int ageFemale = notClassifiedFemales.get(rowFemale);
        HashMap<Integer, Integer> singleMale = singles.get("male");
        if (singleMale == null & notClassifiedMales == null) { //no possible single male to marry -> set as single
            singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
        } else if (singleMale != null) { //check for marriage with the male with the lowest age difference
            int minDiff = 20;
            int rowMarried = 0;
            int[] rowSingles = new int[singleMale.size()];
            for (Map.Entry<Integer, Integer> pair : singleMale.entrySet()) {
                int age = pair.getValue();
                if (Math.abs(ageFemale - age) < minDiff) {
                    minDiff = Math.abs(ageFemale - age);
                    rowMarried = pair.getKey();
                }
            }
            if (rowMarried > 0) {
                double threshold = (20 - minDiff) / 10;
                if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                    married = updateInnerMap(married, 1, minDiff, rowMarried);
                    married = updateInnerMap(married, 2, ageFemale, rowFemale);
                } else {
                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                }
            } else {
                singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
            }
        } else {
            int minDiff = 20;
            int rowMarried = 0;
            for (Map.Entry<Integer, Integer> pair : notClassifiedMales.entrySet()) {
                int age = pair.getValue();
                if (Math.abs(ageFemale - age) < minDiff) {
                    minDiff = Math.abs(ageFemale - age);
                    rowMarried = pair.getKey();
                }
            }
            if (rowMarried > 0) {
                double threshold = (20 - minDiff) / 10;
                if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                    married = updateInnerMap(married, 1, minDiff, rowMarried);
                    married = updateInnerMap(married, 2, ageFemale, rowFemale);
                } else {
                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                    singles = updateInnerMap(singles, 1, minDiff, rowMarried);
                }
            } else {
                singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                for (int i = 0; i < notClassifiedMales.keySet().toArray().length; i++){
                    int rowMale = (int) notClassifiedMales.keySet().toArray()[i];
                    singles = updateInnerMap(singles, 1, ageFemale, rowMale);
                }
            }
            if (notClassifiedFemales.keySet().toArray().length > 1){
                for (int i = 1; i < notClassifiedFemales.keySet().toArray().length; i++){
                    rowFemale = (int) notClassifiedFemales.keySet().toArray()[i];
                    singles = updateInnerMap(singles, 2, ageFemale, rowFemale);
                }
            }
        }
    }


    private void checkMostLikelyFemaleForPossibleMarriage(HashMap<Integer, Integer> notClassifiedMales, HashMap<Integer, Integer> notClassifiedFemales) {
        int rowMale = (int) notClassifiedMales.keySet().toArray()[0];
        int ageMale = notClassifiedMales.get(rowMale);
        HashMap<Integer, Integer> singleFemale = singles.get("female");
        if (singleFemale == null & notClassifiedFemales == null) { //no possible single female to marry -> set as single
            singles = updateInnerMap(singles, 1, ageMale, rowMale);
        } else if (singleFemale != null){ //check for marriage with the female with the lowest age difference
            int minDiff = 20;
            int rowMarried = 0;
            for (Map.Entry<Integer, Integer> pair : singleFemale.entrySet()) {
                int age = pair.getValue();
                if (Math.abs(ageMale - age) < minDiff) {
                    minDiff = Math.abs(ageMale - age);
                    rowMarried = pair.getKey();
                }
            }
            if (rowMarried > 0) {
                double threshold = (20 - minDiff) / 10;
                if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                    married = updateInnerMap(married, 1, ageMale, rowMale);
                    married = updateInnerMap(married, 2, minDiff, rowMarried);
                } else {
                    singles = updateInnerMap(singles, 1, ageMale, rowMale);
                }
            } else {
                singles = updateInnerMap(singles, 1, ageMale, rowMale);
            }
        } else {
            int minDiff = 20;
            int rowMarried = 0;
            for (Map.Entry<Integer, Integer> pair : notClassifiedFemales.entrySet()) {
                int age = pair.getValue();
                if (Math.abs(ageMale - age) < minDiff) {
                    minDiff = Math.abs(ageMale - age);
                    rowMarried = pair.getKey();
                }
            }
            if (rowMarried > 0) {
                double threshold = (20 - minDiff) / 10;
                if (SiloUtil.getRandomNumberAsDouble() < threshold) {
                    married = updateInnerMap(married, 1, ageMale, rowMale);
                    married = updateInnerMap(married, 2, minDiff, rowMarried);
                } else {
                    singles = updateInnerMap(singles, 1, ageMale, rowMale);
                    singles = updateInnerMap(singles, 2, minDiff, rowMarried);
                }
            } else {
                singles = updateInnerMap(singles, 1, ageMale, rowMale);
                for (int i = 0; i < notClassifiedFemales.keySet().toArray().length; i++){
                    rowMale = (int) notClassifiedFemales.keySet().toArray()[i];
                    singles = updateInnerMap(singles, 2, ageMale, rowMale);
                }
            }
            if (notClassifiedMales.keySet().toArray().length > 1){
                for (int i = 1; i < notClassifiedMales.keySet().toArray().length; i++){
                    rowMale = (int) notClassifiedMales.keySet().toArray()[i];
                    singles = updateInnerMap(singles, 1, ageMale, rowMale);
                }
            }
        }
    }


    private void initialize(){
        microDataPerson = dataSetSynPop.getPersonDataSet();
        microDataHousehold = dataSetSynPop.getHouseholdDataSet();
        SiloUtil.addIntegerColumnToTableDataSet(microDataPerson,"personRole");
        SiloUtil.addIntegerColumnToTableDataSet(microDataPerson, "rearrangedRole");
        SiloUtil.addIntegerColumnToTableDataSet(microDataHousehold,"nonClassifiedMales");
        SiloUtil.addIntegerColumnToTableDataSet(microDataHousehold, "nonClassifiedFemales");
        childrenInHousehold = new HashMap<>();
        noClass = new HashMap<>();
        singles = new HashMap<>();
        married = new HashMap<>();
        headCouple = new HashMap<>();
    }


    private void resetMaps(){
        childrenInHousehold.clear();
        noClass.clear();
        singles.clear();
        married.clear();
        headCouple.clear();
    }


    private HashMap<String,HashMap<Integer,Integer>> updateInnerMap(HashMap<String, HashMap<Integer, Integer>> outer, int gender, int age, int row) {
        String key = "male";
        if (gender == 2) {
            key = "female";
        }
        HashMap<Integer, Integer> inner = outer.get(key);
        if (inner == null){
            inner = new HashMap<Integer, Integer>();
            outer.put(key, inner);
        }
        inner.put(row, age);
        return outer;
    }

}
