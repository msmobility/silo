package de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.properties.PropertiesSynPop;
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
    private TableDataSet microHouseholds;
    private TableDataSet microPersons;
    private TableDataSet microDwellings;
    private HashMap<String, String[]> attributesMicroData = new HashMap<>();
    private Map<String, Map<String, Integer>> attributesPersonMicroData = new HashMap<>();
    private Map<String, Map<String, Integer>> attributesHouseholdMicroData = new HashMap<>();
    private Map<String, Map<String, Integer>> attributesDwellingMicroData = new HashMap<>();
    private Map<Integer, Map<String, Integer>> households = new HashMap<>();
    private Map<Integer, Map<String, Integer>> persons = new HashMap<>();
    private Table<Integer, String, Integer> personTable = HashBasedTable.create();
    private Table<Integer, String, Integer> householdTable = HashBasedTable.create();
    private Table<Integer, String, Integer> dwellingTable = HashBasedTable.create();
    private Map<Integer, Map<String, Integer>> dwellings = new HashMap<>();

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
        String pumsFileName = de.tum.bgu.msm.properties.Properties.get().main.baseDirectory + PropertiesSynPop.get().main.microDataFile;
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


        initializeMicroData(hhCount, personCount);

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
/*        dataSetSynPop.setMicroDataPersons(microPersons);
        dataSetSynPop.setMicroDataHouseholds(microHouseholds);
        dataSetSynPop.setMicroDataDwellings(microDwellings);

        dataSetSynPop.setHouseholds(households);
        dataSetSynPop.setPersons(persons);
        dataSetSynPop.setDwellings(dwellings);*/

        dataSetSynPop.setPersonTable(personTable);
        dataSetSynPop.setHouseholdTable(householdTable);
        dataSetSynPop.setDwellingTable(dwellingTable);
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


    private void initializeMicroData(int hhCountTotal, int personCountTotal){
        //method to initialize the TableDataSets from households, persons and dwellings with the labels from the variables to be read

        microHouseholds = new TableDataSet();
        microPersons = new TableDataSet();
        microDwellings = new TableDataSet();
        SiloUtil.addIntegerColumnToTableDataSet(microHouseholds,"id", hhCountTotal);
        SiloUtil.addIntegerColumnToTableDataSet(microHouseholds,"recordHh");
        SiloUtil.addIntegerColumnToTableDataSet(microHouseholds,"personCount");
        SiloUtil.addIntegerColumnToTableDataSet(microPersons,"id", personCountTotal);
        SiloUtil.addIntegerColumnToTableDataSet(microPersons,"idHh");
        SiloUtil.addIntegerColumnToTableDataSet(microPersons,"recordHh");
        SiloUtil.addIntegerColumnToTableDataSet(microPersons,"recordPp");
        SiloUtil.addIntegerColumnToTableDataSet(microDwellings,"id",hhCountTotal);
        for (String attribute : attributesMicroData.get("person")){
            SiloUtil.addIntegerColumnToTableDataSet(microPersons, attribute);
        }
        for (String attribute : attributesMicroData.get("household")){
            SiloUtil.addIntegerColumnToTableDataSet(microHouseholds, attribute);
        }
        for (String attribute : attributesMicroData.get("dwelling")){
            SiloUtil.addIntegerColumnToTableDataSet(microDwellings, attribute);
        }
        microHouseholds.buildIndex(microHouseholds.getColumnPosition("id"));
        microPersons.buildIndex(microPersons.getColumnPosition("id"));
        microDwellings.buildIndex(microDwellings.getColumnPosition("id"));
    }


    private void updateMicroPersons(int personCount, int hhCount, int householdNumber, String recString){
        //method to update the values of the TDS of persons

        microPersons.setValueAt(personCount,"id",personCount);
        microPersons.setValueAt(personCount,"idHh",hhCount);
        microPersons.setValueAt(personCount,"recordHh",householdNumber);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesPersonMicroData.entrySet()){
            int start = pair.getValue().get("initial");
            int finish = pair.getValue().get("end");
            microPersons.setValueAt(personCount,pair.getKey(),convertToInteger(recString.substring(start,finish)));
        }

        Map<String, Integer> attributes = new HashMap<>();
        attributes.put("id", personCount);
        attributes.put("idHh",hhCount);
        attributes.put("recordHh",householdNumber);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesPersonMicroData.entrySet()){
            int start = pair.getValue().get("initial");
            int finish = pair.getValue().get("end");
            attributes.put(pair.getKey(),convertToInteger(recString.substring(start,finish)));
        }
        persons.put(personCount, attributes);

        personTable.put(personCount, "id", personCount);
        personTable.put(personCount,"idHh",hhCount);
        personTable.put(personCount,"recordHh",householdNumber);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesPersonMicroData.entrySet()){
            int start = pair.getValue().get("initial");
            int finish = pair.getValue().get("end");
            personTable.put(personCount, pair.getKey(),convertToInteger(recString.substring(start,finish)));
        }
    }


    private void updateMicroHouseholds(int hhCount, int householdNumber, int personCount,  String recString){
        //method to update the values of the TDS of households

        microHouseholds.setValueAt(hhCount,"id",hhCount);
        microHouseholds.setValueAt(hhCount,"recordHh",householdNumber);
        microHouseholds.setValueAt(hhCount,"personCount",personCount);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesHouseholdMicroData.entrySet()){
            int start = pair.getValue().get("initial");
            int finish = pair.getValue().get("end");
            microHouseholds.setValueAt(hhCount,pair.getKey(),convertToInteger(recString.substring(start,finish)));
        }

        Map<String, Integer> attributes = new HashMap<>();
        attributes.put("id", hhCount);
        attributes.put("recordHh", householdNumber);
        attributes.put("personCount", personCount);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesHouseholdMicroData.entrySet()){
            int start = pair.getValue().get("initial");
            int finish = pair.getValue().get("end");
            attributes.put(pair.getKey(),convertToInteger(recString.substring(start,finish)));
            //microHouseholds.setValueAt(hhCount,pair.getKey(),convertToInteger(recString.substring(start,finish)));
        }
        households.put(hhCount, attributes);

        householdTable.put(hhCount,"id", hhCount);
        householdTable.put(hhCount, "recordHh", householdNumber);
        householdTable.put(hhCount,"personCount", personCount);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesHouseholdMicroData.entrySet()){
            int start = pair.getValue().get("initial");
            int finish = pair.getValue().get("end");
            householdTable.put(hhCount, pair.getKey(),convertToInteger(recString.substring(start,finish)));
            //microHouseholds.setValueAt(hhCount,pair.getKey(),convertToInteger(recString.substring(start,finish)));
        }
    }


    private void updateMicroDwellings(int hhCount, String recString){
        //method to update the values of the TDS of dwellings

        microDwellings.setValueAt(hhCount,"id",hhCount);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesDwellingMicroData.entrySet()){
            int start = pair.getValue().get("initial");
            int finish = pair.getValue().get("end");
            microDwellings.setValueAt(hhCount,pair.getKey(),convertToInteger(recString.substring(start,finish)));
        }
        Map<String, Integer> attributes = new HashMap<>();
        attributes.put("id", hhCount);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesDwellingMicroData.entrySet()){
            int start = pair.getValue().get("initial");
            int finish = pair.getValue().get("end");
            attributes.put(pair.getKey(),convertToInteger(recString.substring(start,finish)));
        }
        dwellings.put(hhCount, attributes);

        dwellingTable.put(hhCount, "id", hhCount);
        for (Map.Entry<String, Map<String, Integer>> pair : attributesDwellingMicroData.entrySet()){
            int start = pair.getValue().get("initial");
            int finish = pair.getValue().get("end");
            dwellingTable.put(hhCount, pair.getKey(),convertToInteger(recString.substring(start,finish)));
        }
    }


    private int convertToInteger(String s) {
        // converts s to an integer value, one or two leading spaces are allowed

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


}
