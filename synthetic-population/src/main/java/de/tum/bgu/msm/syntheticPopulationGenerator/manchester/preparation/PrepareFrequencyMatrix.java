package de.tum.bgu.msm.syntheticPopulationGenerator.manchester.preparation;

import com.pb.common.datafile.TableDataSet;
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
            frequencyMatrix.setValueAt(i,"hh",1);
            int hhSize = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(i,"hhSize");

            for (int j = 0; j < hhSize; j++){
                int row = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(i,"id_firstPerson") + j;
                int age =(int) dataSetSynPop.getPersonDataSet().getValueAt(row,"ageCode");
                int gender = (int) dataSetSynPop.getPersonDataSet().getValueAt(row,"gender");
                int occupation =(int) dataSetSynPop.getPersonDataSet().getValueAt(row,"employmentCode");

                updateHhAgeGender(age, gender, i);
                updateHhOccupactionGender(occupation, gender, i);
            }
            int ddType = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(i,"ddTypeCode");
            updateDdType(ddType, i);
            updateHhSize(hhSize,i);
            frequencyMatrix.setValueAt(i,"pp",hhSize);
        }
        dataSetSynPop.setFrequencyMatrix(frequencyMatrix);
        logger.info("   Finished creating the frequency matrix");

    }

    private void updateDdType(int ddType, int i){
        if (ddType == 1) {
            frequencyMatrix.setValueAt(i, "dd1", 1);
        } else if (ddType == 2){
            frequencyMatrix.setValueAt(i, "dd2", 1);
        } else if (ddType == 3){
            frequencyMatrix.setValueAt(i, "dd3", 1);
        } else {
            frequencyMatrix.setValueAt(i, "dd4", 1);
        }
    }

    private void updateHhSize(int hhSize, int i){
        if (hhSize == 1) {
            frequencyMatrix.setValueAt(i, "hh1", 1);
        } else if (hhSize == 2){
            frequencyMatrix.setValueAt(i, "hh2", 1);
        } else if (hhSize == 3){
            frequencyMatrix.setValueAt(i, "hh3", 1);
        }else {
            frequencyMatrix.setValueAt(i, "hh4+", 1);
        }
    }


    private void updateHhOccupactionGender(int occupation, int gender, int i) {
        if (occupation == 1){
            if (gender == 1){
                int value = 1 + (int) frequencyMatrix.getValueAt(i,"MaleEmp");
                frequencyMatrix.setValueAt(i,"MaleEmp", value);
            } else {
                int value = 1 + (int) frequencyMatrix.getValueAt(i,"FemEmp");
                frequencyMatrix.setValueAt(i,"FemEmp",value);
            }
        }
    }


    private void updateHhAgeGender(int age, int gender, int i) {
        int rowAgeBracket = age - 1;

        if (gender == 1){
            int value = 1 + (int) frequencyMatrix.getValueAt(i,PropertiesSynPop.get().main.ageBracketsPerson[rowAgeBracket] + "Male" );
            frequencyMatrix.setValueAt(i, PropertiesSynPop.get().main.ageBracketsPerson[rowAgeBracket]+ "Male",value);
        } else {
            int value = 1 + (int) frequencyMatrix.getValueAt(i,PropertiesSynPop.get().main.ageBracketsPerson[rowAgeBracket]+ "Fem");
            frequencyMatrix.setValueAt(i,PropertiesSynPop.get().main.ageBracketsPerson[rowAgeBracket]+ "Fem",value);
        }

    }



    private void initializeAttributesMunicipality() {
        //Method to create the list of attributes given the generic names and the brackets

        frequencyMatrix = new TableDataSet();
        frequencyMatrix.appendColumn(dataSetSynPop.getHouseholdDataSet().getColumnAsInt("HouseholdID"), "ID");
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

}
