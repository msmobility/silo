package de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static de.tum.bgu.msm.SiloUtil.addIntegerColumnToTableDataSet;

public class PrepareFrequencyMatrix {

    private static final Logger logger = Logger.getLogger(PrepareFrequencyMatrix.class);

    private DataSetSynPop dataSetSynPop;
    private TableDataSet frequencyMatrix;
    private TableDataSet microHouseholds;
    private TableDataSet microPersons;
    private TableDataSet microDwellings;

    public PrepareFrequencyMatrix(DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
    }

    public void run() {
        //create the frequency matrix with all the attributes aggregated at the household level
        logger.info("   Starting to create the frequency matrix");

        //Read the attributes to match and initialize frequency matrix
        initializeAttributesMunicipality();

        //Update the frequency matrix with the microdata
        for (int i = 1; i <= frequencyMatrix.getRowCount(); i++){
            //checkContainsAndUpdate(attributesMunicipality[i],);
            frequencyMatrix.setValueAt(i,"hhTotal",1);
            int hhSize = (int) microHouseholds.getValueAt(i,"hhSize");
            updateHhSize(hhSize, i);
            updateDdUse((int) microDwellings.getValueAt(i,"ddUse"), i);
            updateDdYear((int) microDwellings.getValueAt(i,"ddYear"), (int) microDwellings.getValueAt(i,"ddSize"), i);
            updateDdFloor((int) microDwellings.getValueAt(i,"ddFloor"), i);
            for (int j = 0; j < hhSize; j++){
                int row = (int) microHouseholds.getValueAt(i,"personCount") + j;
                int age = (int) microPersons.getValueAt(row,"age");
                int gender = (int) microPersons.getValueAt(row,"gender");
                int occupation = (int) microPersons.getValueAt(row,"occupation");
                int nationality = (int) microPersons.getValueAt(row,"nationality");
                updateHhAgeGender(age, gender, i);
                updateHhWorkers(gender,occupation, i);
                updateHhForeigners(nationality, i);
            }
            frequencyMatrix.setValueAt(i,"population",hhSize);
            if (PropertiesSynPop.get().main.boroughIPU) {
                frequencyMatrix.setValueAt(i, "MUChhTotal", 1);
                frequencyMatrix.setValueAt(i, "MUCpopulation", hhSize);
            }
        }

        dataSetSynPop.setFrequencyMatrix(frequencyMatrix);
        //SiloUtil.writeTableDataSet(frequencyMatrix,PropertiesSynPop.get().main.fil);

        logger.info("   Finished creating the frequency matrix");

    }

    private void updateHhForeigners(int nationality, int i) {
        if (nationality > 2){
            int value = 1 + (int) frequencyMatrix.getValueAt(i,"foreigners");
            frequencyMatrix.setValueAt(i,"foreigners",value);
            if (PropertiesSynPop.get().main.boroughIPU) {
                frequencyMatrix.setValueAt(i, "MUCforeigners", value);
            }
        }
    }


    private void updateHhWorkers(int gender, int occupation, int i) {
        if (occupation == 1){
            if (gender == 1){
                int value = 1 + (int) frequencyMatrix.getValueAt(i,"maleWorkers");
                frequencyMatrix.setValueAt(i,"maleWorkers",value);
            } else {
                int value = 1 + (int) frequencyMatrix.getValueAt(i,"femaleWorkers");
                frequencyMatrix.setValueAt(i,"femaleWorkers",value);
            }
            if (PropertiesSynPop.get().main.boroughIPU) {
                if (gender == 1) {
                    int value = 1 + (int) frequencyMatrix.getValueAt(i, "MUCmaleWorkers");
                    frequencyMatrix.setValueAt(i, "MUCmaleWorkers", value);
                } else {
                    int value = 1 + (int) frequencyMatrix.getValueAt(i, "MUCfemaleWorkers");
                    frequencyMatrix.setValueAt(i, "MUCfemaleWorkers", value);
                }
            }
        }
    }


    private void updateHhAgeGender(int age, int gender, int i) {
        int row = 0;
        while (age > PropertiesSynPop.get().main.ageBracketsPerson[row]) {
            row++;
        }
        if (gender == 1){
            int value = 1 + (int) frequencyMatrix.getValueAt(i,"male" + PropertiesSynPop.get().main.ageBracketsPerson[row]);
            frequencyMatrix.setValueAt(i,"male" + PropertiesSynPop.get().main.ageBracketsPerson[row],value);
        } else {
            int value = 1 + (int) frequencyMatrix.getValueAt(i,"female" + PropertiesSynPop.get().main.ageBracketsPerson[row]);
            frequencyMatrix.setValueAt(i,"female" + PropertiesSynPop.get().main.ageBracketsPerson[row],value);
        }
        if (PropertiesSynPop.get().main.boroughIPU) {
            int row1 = 0;
            if (age < 18) {
                frequencyMatrix.setValueAt(i, "MUChhWithChildren", 1);
            }
            if (gender == 2) {
                int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "MUCfemale");
                frequencyMatrix.setValueAt(i, "MUCfemale", value1);
            }
            while (age > PropertiesSynPop.get().main.ageBracketsBorough[row1]){
                row1++;
            }
            int value = 1 + (int) frequencyMatrix.getValueAt(i, "MUCage" + PropertiesSynPop.get().main.ageBracketsBorough[row1]);
            frequencyMatrix.setValueAt(i, "MUCage" + PropertiesSynPop.get().main.ageBracketsBorough[row1], value);
        }
    }


    private void updateDdYear(int ddYear, int ddSize, int i) {
        int row = 0;
        while (ddYear > PropertiesSynPop.get().main.yearBracketsDwelling[row]) {
            row++;
        }
        if (ddSize == 1) { //1 or 2 dwellings inside the building
            frequencyMatrix.setValueAt(i, "smallDwellings" + PropertiesSynPop.get().main.yearBracketsDwelling[row], 1);
            frequencyMatrix.setValueAt(i, "smallDwellings", 1);
        } else { //3 or more dwellings inside the building
            frequencyMatrix.setValueAt(i, "mediumDwellings" + PropertiesSynPop.get().main.yearBracketsDwelling[row], 1);
            frequencyMatrix.setValueAt(i, "mediumDwellings", 1);
        }
    }


    private void updateDdFloor(int ddFloor, int i) {
        int row = 0;
        if (ddFloor >PropertiesSynPop.get().main. sizeBracketsDwelling[PropertiesSynPop.get().main.sizeBracketsDwelling.length-1]){
            row = PropertiesSynPop.get().main.sizeBracketsDwelling.length - 1;
        } else {
            while (ddFloor > PropertiesSynPop.get().main.sizeBracketsDwelling[row]) {
                row++;
            }
        }
        frequencyMatrix.setValueAt(i,"ddFloor" + PropertiesSynPop.get().main.sizeBracketsDwelling[row],1);
    }


    private void updateDdUse(int ddUse, int i) {
        //Method to update the dwelling use
        if (ddUse == 1){
            frequencyMatrix.setValueAt(i,"ddOwned", 1);
        } else {
            frequencyMatrix.setValueAt(i,"ddRented" , 1);
        }
    }

    private void updateHhSize(int hhSize, int i) {
        //Method to update the frequency matrix depending on hhSize
        if (hhSize > PropertiesSynPop.get().main.householdSizes[PropertiesSynPop.get().main.householdSizes.length - 1]){
            hhSize = PropertiesSynPop.get().main.householdSizes[PropertiesSynPop.get().main.householdSizes.length - 1];
        }
        frequencyMatrix.setValueAt(i,"hhSize"+ hhSize, 1);
        if (PropertiesSynPop.get().main.boroughIPU) {
            if (hhSize == 1) {
                frequencyMatrix.setValueAt(i, "MUChhSize1", 1);
            }
        }
    }


    private void initializeAttributesMunicipality() {
        //Method to create the list of attributes given the generic names and the brackets

        frequencyMatrix = new TableDataSet();
        frequencyMatrix.appendColumn(dataSetSynPop.getMicroDataHouseholds().getColumnAsInt("id"),"id");
        for (String attribute : PropertiesSynPop.get().main.attributesMunicipality){
            addIntegerColumnToTableDataSet(frequencyMatrix, attribute);
        }
        if (PropertiesSynPop.get().main.twoGeographicalAreasIPU){
            for (String attribute : PropertiesSynPop.get().main.attributesCounty){
                addIntegerColumnToTableDataSet(frequencyMatrix, attribute);
            }
        }
        if (PropertiesSynPop.get().main.boroughIPU) {
            for (String attribute : PropertiesSynPop.get().main.attributesBorough) {
                addIntegerColumnToTableDataSet(frequencyMatrix, attribute);
            }
        }
        microHouseholds = dataSetSynPop.getMicroDataHouseholds();
        microPersons = dataSetSynPop.getMicroDataPersons();
        microDwellings = dataSetSynPop.getMicroDataDwellings();
    }

    private void checkContainsAndAdd(String key, int[] brackets, Map<String, Integer> map) {
        if (map.containsKey(key)){
            for (int i = 0; i < brackets.length; i++){
                String label = key + brackets[i];
                addIntegerColumnToTableDataSet(frequencyMatrix,label);
            }
        }
    }

}
