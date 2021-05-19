package de.tum.bgu.msm.syntheticPopulationGenerator.bangkok.preparation;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

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
            frequencyMatrix.setValueAt(i,"households",1);
            int hhSize = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(i,"hhSize");
            for (int j = 0; j < hhSize; j++){
                int row = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(i,"id_firstPerson") + j;
                int age =(int) dataSetSynPop.getPersonDataSet().getValueAt(row,"age");
                int gender = (int) dataSetSynPop.getPersonDataSet().getValueAt(row,"gender");
                //int nationality =(int) dataSetSynPop.getPersonDataSet().getValueAt(row,"nationality");
                updateHhGender(gender, i);
                updateHhAgeGender(age, gender, i);
                //updateHhNationalityGender(nationality, gender, i);
            }
            int ddType = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(i,"ddTypeCode");
            updateDdType(ddType, i);
            frequencyMatrix.setValueAt(i,"population",hhSize);
        }
        dataSetSynPop.setFrequencyMatrix(frequencyMatrix);
        logger.info("   Finished creating the frequency matrix");

    }

    private void updateDdType(int ddType, int i){
        if (ddType == 1) {
            frequencyMatrix.setValueAt(i, "apartment", 1);
        } else if (ddType == 3){
            frequencyMatrix.setValueAt(i, "condo", 1);
        } else {
            frequencyMatrix.setValueAt(i, "house", 1);
        }
    }


    private void updateHhNationalityGender(int nationality, int gender, int i) {
        if (nationality > 2){
            if (gender == 1){
                int value = 1 + (int) frequencyMatrix.getValueAt(i,"males_foreigner");
                frequencyMatrix.setValueAt(i,"males_foreigner", value);
            } else {
                int value = 1 + (int) frequencyMatrix.getValueAt(i,"females_foreigner");
                frequencyMatrix.setValueAt(i,"females_foreigner",value);
            }
        }
    }



    private void updateHhGender(int gender, int i) {
        if (gender == 1){
            int valueGender = 1 + (int) frequencyMatrix.getValueAt(i,"males");
            frequencyMatrix.setValueAt(i,"males",valueGender);
        } else {
            int valueGender = 1 + (int) frequencyMatrix.getValueAt(i,"females");
            frequencyMatrix.setValueAt(i,"females",valueGender);
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
            int value = 1 + (int) frequencyMatrix.getValueAt(i,"fem" + PropertiesSynPop.get().main.ageBracketsPerson[row]);
            frequencyMatrix.setValueAt(i,"fem" + PropertiesSynPop.get().main.ageBracketsPerson[row],value);
        }

    }



    private void initializeAttributesMunicipality() {
        //Method to create the list of attributes given the generic names and the brackets

        frequencyMatrix = new TableDataSet();
        frequencyMatrix.appendColumn(dataSetSynPop.getHouseholdDataSet().getColumnAsInt("hhThaiId"), "ID");
        for (String attribute : PropertiesSynPop.get().main.attributesMunicipality){
            SiloUtil.addIntegerColumnToTableDataSet(frequencyMatrix, attribute);
        }
        SiloUtil.addIntegerColumnToTableDataSet(frequencyMatrix, "condo");
        SiloUtil.addIntegerColumnToTableDataSet(frequencyMatrix, "apartment");
        SiloUtil.addIntegerColumnToTableDataSet(frequencyMatrix, "house");
    }

}
