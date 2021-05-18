package de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation;

import com.google.common.primitives.Ints;
import de.tum.bgu.msm.common.datafile.TableDataSet;
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
            updateHouseholdsBorough(hhSize, i);
            for (int j = 0; j < hhSize; j++){
                int row = dataSetSynPop.getHouseholdTable().get(i,"personCount") + j;
                int age = dataSetSynPop.getPersonTable().get(row,"age");
                int gender = dataSetSynPop.getPersonTable().get(row,"gender");
                int occupation = dataSetSynPop.getPersonTable().get(row,"occupation");
                int sector = dataSetSynPop.getPersonTable().get(row,"sector");
                updateHhAgeGender(age, gender, i);
                updateHhWorkers(gender,occupation, sector, i);
                updatePersonsBorough(age,gender,occupation,i);
            }
            frequencyMatrix.setValueAt(i,"population",hhSize);
        }
        dataSetSynPop.setFrequencyMatrix(frequencyMatrix);
        SiloUtil.writeTableDataSet(frequencyMatrix,PropertiesSynPop.get().main.frequencyMatrixFileName);
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
    }


    private void updateHhSize(int hhSize, int i) {
        //Method to update the frequency matrix depending on hhSize
        if (hhSize > PropertiesSynPop.get().main.householdSizes[PropertiesSynPop.get().main.householdSizes.length - 1]){
            hhSize = PropertiesSynPop.get().main.householdSizes[PropertiesSynPop.get().main.householdSizes.length - 1];
        }
        frequencyMatrix.setValueAt(i,"hhSize"+ hhSize, 1);
    }

    private void updateHouseholdsBorough(int hhSize, int i) {
        if (PropertiesSynPop.get().main.boroughIPU) {
            frequencyMatrix.setValueAt(i, "borough_hhTotal", 1);
            frequencyMatrix.setValueAt(i, "borough_population", hhSize);
            //attributes specific for each city implemented
            switch (PropertiesSynPop.get().main.state){
                case "02_Hamburg":
                    if (hhSize == 1) {
                        frequencyMatrix.setValueAt(i, "borough_hhSize1", 1);
                    } else if (hhSize == 2){
                        frequencyMatrix.setValueAt(i, "borough_hhSize2", 1);
                    } else if (hhSize == 3){
                        frequencyMatrix.setValueAt(i, "borough_hhSize3", 1);
                    } else if (hhSize == 4){
                        frequencyMatrix.setValueAt(i, "borough_hhSize4", 1);
                    } else if (hhSize > 4){
                        frequencyMatrix.setValueAt(i, "borough_hhSize5", 1);
                    }
                    break;
                case "03_Hannover":
                    if (hhSize == 1) {
                        frequencyMatrix.setValueAt(i, "borough_hhSize1", 1);
                    }
                    break;
                case "04_Bremen":
                    if (hhSize == 1) {
                        frequencyMatrix.setValueAt(i, "borough_hhSize1", 1);
                    } else if (hhSize == 2){
                        frequencyMatrix.setValueAt(i, "borough_hhSize2", 1);
                    } else if (hhSize == 3){
                        frequencyMatrix.setValueAt(i, "borough_hhSize3", 1);
                    } else if (hhSize == 4){
                        frequencyMatrix.setValueAt(i, "borough_hhSize4", 1);
                    } else if (hhSize > 4){
                        frequencyMatrix.setValueAt(i, "borough_hhSize5", 1);
                    }
                    break;
                case "05_Koeln":
                    if (hhSize == 1) {
                        frequencyMatrix.setValueAt(i, "borough_hhSize1", 1);
                    } else if (hhSize == 2){
                        frequencyMatrix.setValueAt(i, "borough_hhSize2", 1);
                    } else if (hhSize == 3){
                        frequencyMatrix.setValueAt(i, "borough_hhSize3", 1);
                    } else if (hhSize == 4){
                        frequencyMatrix.setValueAt(i, "borough_hhSize4", 1);
                    } else if (hhSize > 4){
                        frequencyMatrix.setValueAt(i, "borough_hhSize5", 1);
                    }
                    break;
                case "05_Essen":
                    if (hhSize == 1) {
                        frequencyMatrix.setValueAt(i, "borough_hhSize1", 1);
                    }
                    break;
                case "05_Duesseldorf":
                    if (hhSize == 1) {
                        frequencyMatrix.setValueAt(i, "borough_hhSize1", 1);
                    }
                    break;
                case "05_Dortmund":
                    if (hhSize == 1) {
                        frequencyMatrix.setValueAt(i, "borough_hhSize1", 1);
                    }
                    break;
                case "06_Frankfurt":
                    if (hhSize == 1) {
                        frequencyMatrix.setValueAt(i, "borough_hhSize1", 1);
                    } else if (hhSize == 2){
                        frequencyMatrix.setValueAt(i, "borough_hhSize2", 1);
                    } else if (hhSize == 3){
                        frequencyMatrix.setValueAt(i, "borough_hhSize3", 1);
                    } else if (hhSize == 4){
                        frequencyMatrix.setValueAt(i, "borough_hhSize4", 1);
                    } else if (hhSize > 4){
                        frequencyMatrix.setValueAt(i, "borough_hhSize5", 1);
                    }
                    break;
                case "08_Stuttgart":
                    if (hhSize == 1) {
                        frequencyMatrix.setValueAt(i, "borough_hhSize1", 1);
                    }
                    break;
                case "09_Muenchen":
                    if (hhSize == 1) {
                        frequencyMatrix.setValueAt(i, "borough_hhSize1", 1);
                    }
                    break;
                case "05_Duisburg":
                    if (hhSize == 1) {
                        frequencyMatrix.setValueAt(i, "borough_hhSize1", 1);
                    } else if (hhSize == 2){
                        frequencyMatrix.setValueAt(i, "borough_hhSize2", 1);
                    } else if (hhSize == 3){
                        frequencyMatrix.setValueAt(i, "borough_hhSize3", 1);
                    } else if (hhSize == 4){
                        frequencyMatrix.setValueAt(i, "borough_hhSize4", 1);
                    } else if (hhSize > 4){
                        frequencyMatrix.setValueAt(i, "borough_hhSize5", 1);
                    }
                    break;
                case "11_Berlin":
                    break;
                case "14_Dresden":
                    if (hhSize == 1) {
                        frequencyMatrix.setValueAt(i, "borough_hhSize1", 1);
                    } else if (hhSize == 2){
                        frequencyMatrix.setValueAt(i, "borough_hhSize2", 1);
                    } else if (hhSize == 3){
                        frequencyMatrix.setValueAt(i, "borough_hhSize3", 1);
                    } else if (hhSize == 4){
                        frequencyMatrix.setValueAt(i, "borough_hhSize4", 1);
                    } else if (hhSize > 4){
                        frequencyMatrix.setValueAt(i, "borough_hhSize5", 1);
                    }
                    break;
                case "14_Leipzig":
                    break;
                default:
            }
        }
    }


    private void updatePersonsBorough(int age, int gender, int occupation, int i) {
        if (PropertiesSynPop.get().main.boroughIPU) {
            switch (PropertiesSynPop.get().main.state) {
                case "02_Hamburg":
                    if (gender == 2) {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females");
                        frequencyMatrix.setValueAt(i, "borough_females", value1);
                    } else {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_males");
                        frequencyMatrix.setValueAt(i, "borough_males", value1);
                    }
                    if (age < 19){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age18");
                        frequencyMatrix.setValueAt(i, "borough_age18", value1);
                    } else if (age < 30){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age29");
                        frequencyMatrix.setValueAt(i, "borough_age29", value1);
                    } else if (age < 50){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age49");
                        frequencyMatrix.setValueAt(i, "borough_age49", value1);
                    } else if (age < 65){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age64");
                        frequencyMatrix.setValueAt(i, "borough_age64", value1);
                    } else {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age99");
                        frequencyMatrix.setValueAt(i, "borough_age99", value1);
                    }
                    break;

                case "03_Hannover":
                    if (age < 19){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age18");
                        frequencyMatrix.setValueAt(i, "borough_age18", value1);
                        frequencyMatrix.setValueAt(i, "borough_hhWithChildren", 1);
                    } else if (age < 60){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age59");
                        frequencyMatrix.setValueAt(i, "borough_age59", value1);
                    } else {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age99");
                        frequencyMatrix.setValueAt(i, "borough_age99", value1);
                    }
                    break;

                    case "04_Bremen":
                    if (gender == 2) {
                        if (age < 19){
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_female18");
                            frequencyMatrix.setValueAt(i, "borough_female18", value1);
                        } else if (age < 36){
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_female35");
                            frequencyMatrix.setValueAt(i, "borough_female35", value1);
                        } else if (age < 66){
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_female65");
                            frequencyMatrix.setValueAt(i, "borough_female65", value1);
                        } else {
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_female99");
                            frequencyMatrix.setValueAt(i, "borough_female99", value1);
                        }
                    } else {
                        if (age < 19){
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_male18");
                            frequencyMatrix.setValueAt(i, "borough_male18", value1);
                        } else if (age < 36){
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_male35");
                            frequencyMatrix.setValueAt(i, "borough_male35", value1);
                        } else if (age < 66){
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_male65");
                            frequencyMatrix.setValueAt(i, "borough_male65", value1);
                        } else {
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_male99");
                            frequencyMatrix.setValueAt(i, "borough_male99", value1);
                        }
                    }
                    if (age < 19) {
                        frequencyMatrix.setValueAt(i, "borough_hhWithChildren", 1);
                    }
                    break;

                    case "05_Koeln":
                    if (age < 19) {
                        frequencyMatrix.setValueAt(i, "borough_hhWithChildren", 1);
                    }
                    if (gender == 2) {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females");
                        frequencyMatrix.setValueAt(i, "borough_females", value1);
                        if (age > 18){
                            int value11 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females18_more");
                            frequencyMatrix.setValueAt(i, "borough_females18_more", value11);
                        }
                    }
                    break;

                case "05_Duesseldorf":
                    if (age < 19){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age18");
                        frequencyMatrix.setValueAt(i, "borough_age18", value1);
                    } else if (age < 31){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age30");
                        frequencyMatrix.setValueAt(i, "borough_age30", value1);
                    } else if (age < 51){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age50");
                        frequencyMatrix.setValueAt(i, "borough_age50", value1);
                    } else if (age < 66){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age65");
                        frequencyMatrix.setValueAt(i, "borough_age65", value1);
                    } else {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age99");
                        frequencyMatrix.setValueAt(i, "borough_age99", value1);
                    }
                    break;

                case "05_Essen":
                    if (gender == 2) {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females");
                        frequencyMatrix.setValueAt(i, "borough_females", value1);
                    } else {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_males");
                        frequencyMatrix.setValueAt(i, "borough_males", value1);
                    }
                    if (age < 19){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age18");
                        frequencyMatrix.setValueAt(i, "borough_age18", value1);
                    } else if (age < 26){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age25");
                        frequencyMatrix.setValueAt(i, "borough_age25", value1);
                    } else if (age < 46){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age45");
                        frequencyMatrix.setValueAt(i, "borough_age45", value1);
                    } else if (age < 66){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age65");
                        frequencyMatrix.setValueAt(i, "borough_age65", value1);
                    } else {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age99");
                        frequencyMatrix.setValueAt(i, "borough_age99", value1);
                    }
                    break;

                case "05_Dortmund":
                    if (gender == 2) {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females");
                        frequencyMatrix.setValueAt(i, "borough_females", value1);
                    } else {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_males");
                        frequencyMatrix.setValueAt(i, "borough_males", value1);
                    }
                    break;

                case "06_Frankfurt":
                    if (gender == 2) {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females");
                        frequencyMatrix.setValueAt(i, "borough_females", value1);
                    }
                    if (age < 18){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age17");
                        frequencyMatrix.setValueAt(i, "borough_age17", value1);
                    } else if (age < 30){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age29");
                        frequencyMatrix.setValueAt(i, "borough_age29", value1);
                    } else if (age < 65){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age64");
                        frequencyMatrix.setValueAt(i, "borough_age64", value1);
                    } else {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age99");
                        frequencyMatrix.setValueAt(i, "borough_age99", value1);
                    }
                    break;

                case "08_Stuttgart":
                    if (gender == 2) {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females");
                        frequencyMatrix.setValueAt(i, "borough_females", value1);
                    }
                    if (age < 19){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age18");
                        frequencyMatrix.setValueAt(i, "borough_age18", value1);
                    } else if (age < 66){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age65");
                        frequencyMatrix.setValueAt(i, "borough_age65", value1);
                    } else {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age99");
                        frequencyMatrix.setValueAt(i, "borough_age99", value1);
                    }
                    break;

                case "09_Muenchen":
                    if (age < 19) {
                        frequencyMatrix.setValueAt(i, "borough_hhWithChildren", 1);
                    }
                    if (gender == 2) {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females");
                        frequencyMatrix.setValueAt(i, "borough_females", value1);
                    }
/*                    if (age < 6){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age5");
                        frequencyMatrix.setValueAt(i, "borough_age5", value1);
                    }*/ if (age < 18){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age17");
                        frequencyMatrix.setValueAt(i, "borough_age17", value1);
                    } else if (age < 65){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age64");
                        frequencyMatrix.setValueAt(i, "borough_age64", value1);
                    } else {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age99");
                        frequencyMatrix.setValueAt(i, "borough_age99", value1);
                    }
                    if (gender == 2 && occupation==1) {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_femaleWorkers");
                        frequencyMatrix.setValueAt(i, "borough_femaleWorkers", value1);
                    }
                    if (gender == 1 && occupation==1) {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_maleWorkers");
                        frequencyMatrix.setValueAt(i, "borough_maleWorkers", value1);
                    }
                    break;

                case "05_Duisburg":
                    if (gender == 2) {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females");
                        frequencyMatrix.setValueAt(i, "borough_females", value1);
                    } else {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_males");
                        frequencyMatrix.setValueAt(i, "borough_males", value1);
                    }
                    if (age < 19){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age18");
                        frequencyMatrix.setValueAt(i, "borough_age18", value1);
                    } else if (age < 31){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age30");
                        frequencyMatrix.setValueAt(i, "borough_age30", value1);
                    } else if (age < 51){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age50");
                        frequencyMatrix.setValueAt(i, "borough_age50", value1);
                    } else if (age < 66){
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age65");
                        frequencyMatrix.setValueAt(i, "borough_age65", value1);
                    } else {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age99");
                        frequencyMatrix.setValueAt(i, "borough_age99", value1);
                    }
                    break;

                case "11_Berlin":
                    if (gender == 2) {
                        int value2 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females");
                        frequencyMatrix.setValueAt(i, "borough_females", value2);
                        if (age < 19){
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females_18");
                            frequencyMatrix.setValueAt(i, "borough_females_18", value1);
                        } else if (age < 36){
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females_35");
                            frequencyMatrix.setValueAt(i, "borough_females_35", value1);
                        } else if (age < 66){
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females_65");
                            frequencyMatrix.setValueAt(i, "borough_females_65", value1);
                        } else {
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females_99");
                            frequencyMatrix.setValueAt(i, "borough_females_99", value1);
                        }
                    } else {
                        int value2 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_males");
                        frequencyMatrix.setValueAt(i, "borough_males", value2);
                        if (age < 19){
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_males_18");
                            frequencyMatrix.setValueAt(i, "borough_males_18", value1);
                        } else if (age < 36){
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_males_35");
                            frequencyMatrix.setValueAt(i, "borough_males_35", value1);
                        } else if (age < 66){
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_males_65");
                            frequencyMatrix.setValueAt(i, "borough_males_65", value1);
                        } else {
                            int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_males_99");
                            frequencyMatrix.setValueAt(i, "borough_males_99", value1);
                        }
                    }
                    break;

                case "14_Dresden":
                    if (age < 19) {
                        frequencyMatrix.setValueAt(i, "borough_hhWithChildren", 1);
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_age18");
                        frequencyMatrix.setValueAt(i, "borough_age18", value1);
                    }
                    break;

                case "14_Leipzig":
                    if (gender == 2) {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_females");
                        frequencyMatrix.setValueAt(i, "borough_females", value1);
                    } else {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(i, "borough_males");
                        frequencyMatrix.setValueAt(i, "borough_males", value1);
                    }
                    break;

                default:
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
