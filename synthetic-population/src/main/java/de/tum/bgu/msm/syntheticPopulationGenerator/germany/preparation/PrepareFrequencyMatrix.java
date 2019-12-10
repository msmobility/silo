package de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation;

import com.google.common.primitives.Ints;
import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.Map;

public class PrepareFrequencyMatrix {

    private static final Logger logger = Logger.getLogger(PrepareFrequencyMatrix.class);

    private DataSetSynPop dataSetSynPop;
    private TableDataSet frequencyMatrix;

    public PrepareFrequencyMatrix(DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
    }

    public void run() {
        //create the frequency matrix with all the attributes aggregated at the household level
        logger.info("   Starting to create the frequency matrix");

        initializeAttributesMunicipality();

        for (int i = 1; i <= frequencyMatrix.getRowCount(); i++){
            //checkContainsAndUpdate(attributesMunicipality[i],);
            frequencyMatrix.setValueAt(i,"hhTotal",1);
            int hhSize = dataSetSynPop.getHouseholdTable().get(i,"hhSize");
            updateHhSize(hhSize, i);
            for (int j = 0; j < hhSize; j++){
                int row = dataSetSynPop.getHouseholdTable().get(i,"personCount") + j;
                int age = dataSetSynPop.getPersonTable().get(row,"age");
                int gender = dataSetSynPop.getPersonTable().get(row,"gender");
                int occupation = dataSetSynPop.getPersonTable().get(row,"occupation");
                int sector = dataSetSynPop.getPersonTable().get(row,"sector");
                updateHhAgeGender(age, gender, i);
                updateHhWorkers(gender,occupation, sector, i);
            }
            frequencyMatrix.setValueAt(i,"population",hhSize);
            if (PropertiesSynPop.get().main.boroughIPU) {
                frequencyMatrix.setValueAt(i, "MUChhTotal", 1);
                frequencyMatrix.setValueAt(i, "MUCpopulation", hhSize);
            }
        }
        dataSetSynPop.setFrequencyMatrix(frequencyMatrix);
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


    private void updateHhWorkers(int gender, int occupation, int sector, int i) {
        if (occupation == 1){
            if (gender == 1){
                int value = 1 + (int) frequencyMatrix.getValueAt(i,"maleWorkers");
                frequencyMatrix.setValueAt(i,"maleWorkers",value);
            } else {
                int value = 1 + (int) frequencyMatrix.getValueAt(i,"femaleWorkers");
                frequencyMatrix.setValueAt(i,"femaleWorkers",value);
            }

            if (sector == 1){
                int value = 1 + (int) frequencyMatrix.getValueAt(i,"agrWorkers");
                frequencyMatrix.setValueAt(i,"agrWorkers",value);
            } else if (sector == 2){
                int value = 1 + (int) frequencyMatrix.getValueAt(i,"indWorkers");
                frequencyMatrix.setValueAt(i,"indWorkers",value);
            } else if (sector == 3){
                int value = 1 + (int) frequencyMatrix.getValueAt(i,"serWorkers");
                frequencyMatrix.setValueAt(i,"serWorkers",value);
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
            int value2 = 1 + (int) frequencyMatrix.getValueAt(i,"males");
            frequencyMatrix.setValueAt(i,"males",value2);
        } else {
            int value = 1 + (int) frequencyMatrix.getValueAt(i,"female" + PropertiesSynPop.get().main.ageBracketsPerson[row]);
            frequencyMatrix.setValueAt(i,"female" + PropertiesSynPop.get().main.ageBracketsPerson[row],value);
            int value2 = 1 + (int) frequencyMatrix.getValueAt(i,"females");
            frequencyMatrix.setValueAt(i,"females",value2);
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
        frequencyMatrix.appendColumn(Ints.toArray(dataSetSynPop.getHouseholdTable().rowKeySet()),"id");
        for (String attribute : PropertiesSynPop.get().main.attributesMunicipality){
            SiloUtil.addIntegerColumnToTableDataSet(frequencyMatrix, attribute);
        }
        if (PropertiesSynPop.get().main.twoGeographicalAreasIPU){
            for (String attribute : PropertiesSynPop.get().main.attributesCounty){
                SiloUtil.addIntegerColumnToTableDataSet(frequencyMatrix, attribute);
            }
        }
        if (PropertiesSynPop.get().main.boroughIPU) {
            for (String attribute : PropertiesSynPop.get().main.attributesBorough) {
                SiloUtil.addIntegerColumnToTableDataSet(frequencyMatrix, attribute);
            }
        }
    }

    private void checkContainsAndAdd(String key, int[] brackets, Map<String, Integer> map) {
        if (map.containsKey(key)){
            for (int i = 0; i < brackets.length; i++){
                String label = key + brackets[i];
                SiloUtil.addIntegerColumnToTableDataSet(frequencyMatrix,label);
            }
        }
    }

}
