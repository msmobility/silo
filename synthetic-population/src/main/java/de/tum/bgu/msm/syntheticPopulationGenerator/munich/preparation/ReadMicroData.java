package de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReadMicroData {

    private static final Logger logger = Logger.getLogger(ReadMicroData.class);

    private final DataSetSynPop dataSetSynPop;
    private final MicroDataManager microDataManager;
    private Map<String, Map<String, Integer>> exceptionsMicroData = new HashMap<>();
    private HashMap<String, String[]> attributesMicroData = new HashMap<>();
    private Map<String, Map<String, Integer>> attributesPersonMicroData = new HashMap<>();
    private Map<String, Map<String, Integer>> attributesHouseholdMicroData = new HashMap<>();
    private Map<String, Map<String, Integer>> attributesDwellingMicroData = new HashMap<>();
    private Table<Integer, String, Integer> personTable = HashBasedTable.create();
    private Table<Integer, String, Integer> householdTable = HashBasedTable.create();
    private Table<Integer, String, Integer> dwellingTable = HashBasedTable.create();

    private TableDataSet personDataSet;
    private TableDataSet householdDataSet;
    private TableDataSet dwellingDataSet;

    public ReadMicroData(DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        microDataManager = new MicroDataManager(dataSetSynPop);
    }

    public void run(){

        logger.info("   Starting to read the micro data");

        exceptionsMicroData = microDataManager.exceptionsMicroData();
        attributesMicroData = microDataManager.attributesMicroData();
        attributesPersonMicroData = microDataManager.attributesPersonMicroData();
        attributesHouseholdMicroData = microDataManager.attributesHouseholdMicroData();
        attributesDwellingMicroData = microDataManager.attributesDwellingMicroData();


        //Scanning the file to obtain the number of households and persons in Bavaria
        String pumsFileName = PropertiesSynPop.get().main.microDataFile;
        String recString = "";
        int recCount = 0;
        int hhCount = 0;
        int personCount = 0;
        int hhOutCountTotal = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                int householdNumber = convertToInteger(recString.substring(2,8)) * 1000 + convertToInteger(recString.substring(8,11));
                int restriction = checkRestrictions(recString);
                if (householdNumber != previousHouseholdNumber & restriction == 1) {
                    hhCount++;
                    personCount++;
                    previousHouseholdNumber = householdNumber; // Update the household number
                } else if (householdNumber == previousHouseholdNumber & restriction == 1) {
                    personCount++;
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        generateTableDataSet(hhCount, personCount);

        //read the micro data and assign the characteristics
        hhCount = 0;
        personCount = 0;
        recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(pumsFileName));
            int previousHouseholdNumber = -1;
            while ((recString = in.readLine()) != null) {
                recCount++;
                int householdNumber = convertToInteger(recString.substring(2,8)) * 1000 + convertToInteger(recString.substring(8,11));
                int restriction = checkRestrictions(recString);
                if (householdNumber != previousHouseholdNumber & restriction == 1) {
                    hhCount++;
                    personCount++;
                    updateMicroHouseholds(hhCount,householdNumber,personCount,recString);
                    updateMicroDwellings(hhCount,recString);
                    updateMicroPersons(personCount,hhCount,householdNumber,recString);
                    previousHouseholdNumber = householdNumber; // Update the household number
                } else if (householdNumber == previousHouseholdNumber & restriction == 1) {
                    personCount++;
                    updateMicroPersons(personCount,hhCount,householdNumber,recString);
                }
            }
            logger.info("  Read " + (personCount) + " person records in " +
                    (hhCount) + " private households from file: " + pumsFileName);
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + pumsFileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }

        logger.info("   Finished reading the micro data");
        dataSetSynPop.setPersonTable(personTable);
        dataSetSynPop.setHouseholdTable(householdTable);
        dataSetSynPop.setDwellingTable(dwellingTable);
        dataSetSynPop.setPersonDataSet(personDataSet);
        dataSetSynPop.setHouseholdDataSet(householdDataSet);
        dataSetSynPop.setDwellingDataSet(dwellingDataSet);
        SiloUtil.writeTableDataSet(personDataSet,"microData/interimFiles/persons.csv");
        SiloUtil.writeTableDataSet(householdDataSet,"microData/interimFiles/households.csv");
        SiloUtil.writeTableDataSet(dwellingDataSet,"microData/interimFiles/dwellings.csv");
    }


    private int checkRestrictions(String recString){
        int restriction = 1;
        for (String exception : exceptionsMicroData.keySet()){
            int start = exceptionsMicroData.get(exception).get("initial");
            int finish = exceptionsMicroData.get(exception).get("end");
            int threshold = exceptionsMicroData.get(exception).get("exceptionIf");
            int value = convertToInteger(recString.substring(start,finish));
            if (threshold == value){
                restriction = 0;
            }
        }
        return restriction;
    }


    private void updateMicroPersons(int personCount, int hhCount, int householdNumber, String recString){
        personTable.put(personCount, "id", personCount);
        personTable.put(personCount,"idHh",hhCount);
        personTable.put(personCount,"recordHh",householdNumber);
        personDataSet.setValueAt(personCount, "id", personCount);
        personDataSet.setValueAt(personCount, "idHh", hhCount);
        personDataSet.setValueAt(personCount, "recordHh", householdNumber);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesPersonMicroData.entrySet()){
            int start = pair.getValue().get("initial");
            int finish = pair.getValue().get("end");
            personTable.put(personCount, pair.getKey(),convertToInteger(recString.substring(start,finish)));
            personDataSet.setValueAt(personCount, pair.getKey(), convertToInteger(recString.substring(start,finish)));
        }
    }


    private void updateMicroHouseholds(int hhCount, int householdNumber, int personCount,  String recString){
        householdTable.put(hhCount,"id", hhCount);
        householdTable.put(hhCount, "recordHh", householdNumber);
        householdTable.put(hhCount,"personCount", personCount);
        householdDataSet.setValueAt(hhCount, "id", hhCount);
        householdDataSet.setValueAt(hhCount, "recordHh", householdNumber);
        householdDataSet.setValueAt(hhCount, "personCount", personCount);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesHouseholdMicroData.entrySet()){
            int start = pair.getValue().get("initial");
            int finish = pair.getValue().get("end");
            householdTable.put(hhCount, pair.getKey(),convertToInteger(recString.substring(start,finish)));
            householdDataSet.setValueAt(hhCount, pair.getKey(), convertToInteger(recString.substring(start,finish)));
        }
    }


    private void updateMicroDwellings(int hhCount, String recString){
        dwellingTable.put(hhCount, "id", hhCount);
        dwellingDataSet.setValueAt(hhCount, "id", hhCount);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesDwellingMicroData.entrySet()){
            int start = pair.getValue().get("initial");
            int finish = pair.getValue().get("end");
            dwellingTable.put(hhCount, pair.getKey(),convertToInteger(recString.substring(start,finish)));
            dwellingDataSet.setValueAt(hhCount, pair.getKey(), convertToInteger(recString.substring(start,finish)));
        }
    }

    private void generateTableDataSet(int hhCount, int ppCount){

        householdDataSet = new TableDataSet();
        appendNewColumnToTDS(householdDataSet, "id", hhCount);
        appendNewColumnToTDS(householdDataSet, "recordHh", hhCount);
        appendNewColumnToTDS(householdDataSet, "personCount", hhCount);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesHouseholdMicroData.entrySet()){
            String variableName = pair.getKey();
            appendNewColumnToTDS(householdDataSet, variableName, hhCount);
        }
        personDataSet = new TableDataSet();
        appendNewColumnToTDS(personDataSet, "id", ppCount);
        appendNewColumnToTDS(personDataSet, "idHh", ppCount);
        appendNewColumnToTDS(personDataSet, "recordHh", ppCount);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesPersonMicroData.entrySet()){
            String variableName = pair.getKey();
            appendNewColumnToTDS(personDataSet, variableName, ppCount);
        }
        dwellingDataSet = new TableDataSet();
        appendNewColumnToTDS(dwellingDataSet, "id", hhCount);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesDwellingMicroData.entrySet()){
            String variableName = pair.getKey();
            appendNewColumnToTDS(dwellingDataSet, variableName, hhCount);
        }
    }


    private void appendNewColumnToTDS(TableDataSet tableDataSet, String columnName, int length){

        int[] dummy = SiloUtil.createArrayWithValue(length, 0);
        tableDataSet.appendColumn(dummy, columnName);
    }


    private int convertToInteger(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            boolean spacesOnly = true;
            for (int pos = 0; pos < s.length(); pos++) {
                if (!s.substring(pos, pos+1).equals(" ")) spacesOnly = false;
            }
            if (spacesOnly) return -999;
            else {
                logger.fatal("String " + s + " cannot be converted into an integer.");
                return 0;
            }
        }
    }

    private float convertToFloat(String s){
        try {
            return Float.parseFloat(s.trim());
        } catch (Exception e) {
            boolean spacesOnly = true;
            for (int pos = 0; pos < s.length(); pos++) {
                if (!s.substring(pos, pos+1).equals(" ")) spacesOnly = false;
            }
            if (spacesOnly) return -999;
            else {
                logger.fatal("String " + s + " cannot be converted into an integer.");
                return 0;
            }
        }
    }


}
