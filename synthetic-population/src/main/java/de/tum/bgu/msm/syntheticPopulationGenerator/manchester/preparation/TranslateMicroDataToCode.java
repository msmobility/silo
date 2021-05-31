package de.tum.bgu.msm.syntheticPopulationGenerator.manchester.preparation;


import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

public class TranslateMicroDataToCode {

    private static final Logger logger = Logger.getLogger(TranslateMicroDataToCode.class);

    private DataSetSynPop dataSetSynPop;


    public TranslateMicroDataToCode(DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
    }


    public void run(){

        //method to translate the categories from the initial micro data to the categories from SILO
        logger.info("   Starting to translate the micro data");

       initializeNewVariables();

       //convert one by one the records from microPersons
        for (int personCount : dataSetSynPop.getPersonDataSet().getColumnAsInt("IndividualID")){
            translateOccupation(personCount);
            translateAge(personCount);
        }
        //convert one by one the records from microHouseholds
        for (int hhCount : dataSetSynPop.getHouseholdDataSet().getColumnAsInt("HouseholdID")){
            translateDwellingType(hhCount);
        }
        logger.info("   Finished translating the micro data");
    }

    private void initializeNewVariables(){
        appendNewColumnToTDS(dataSetSynPop.getPersonDataSet(), "employmentCode");
        appendNewColumnToTDS(dataSetSynPop.getPersonDataSet(),"ageCode");
        appendNewColumnToTDS(dataSetSynPop.getHouseholdDataSet(),"ddTypeCode");
    }

    private void translateOccupation(int personCount) {
        int occupation = (int) dataSetSynPop.getPersonDataSet().getValueAt(personCount,"employed");
        int age = (int) dataSetSynPop.getPersonDataSet().getValueAt(personCount,"age");
        switch(occupation) {
            case 1:
                dataSetSynPop.getPersonDataSet().setValueAt(personCount,"employmentCode", 1);
                break;
            case 2:
                dataSetSynPop.getPersonDataSet().setValueAt(personCount,"employmentCode", 2);
                break;
            case 3:
                dataSetSynPop.getPersonDataSet().setValueAt(personCount,"employmentCode", 3);
                break;
            case 4:
                dataSetSynPop.getPersonDataSet().setValueAt(personCount,"employmentCode", 4);
                break;
            case 5:
                int guessOccupation = guessOccupation(age);
                dataSetSynPop.getPersonDataSet().setValueAt(personCount,"employmentCode", guessOccupation);
                break;
            default:
                throw new IllegalArgumentException(String.format("Code %d not valid.", occupation));
        }

//        Value = 1.0	Label = Employed (EconFull, EconPart, EconGovT)
//        Value = 2.0	Label = Unemployed (EconSick, EconRgUn, EconSkng, EconNSkg)
//        Value = 3.0	Label = Student (EconStdt)
//        Value = 4.0	Label = Retired (EconRtrd)
//        Value = 5.0	Label = Other


    }

    private int guessOccupation(int age) {
        if(age <= 6){
            return 0;
        }else if(age <=18){
            return 3;
        }else if(age >=60){
            return 4;
        }else if(SiloUtil.getRandomNumberAsDouble()<=0.045){
            return 2;
        }else {
            return 1;
        }
    }

    private void translateAge(int personCount) {
        int age = (int) dataSetSynPop.getPersonDataSet().getValueAt(personCount,"age");

        int valueCode;

        if(age<=4){
            valueCode=1;
        }else if(age<=10){
            valueCode=2;
        }else if(age<=16){
            valueCode=3;
        }else if(age<=20){
            valueCode=4;
        }else if(age<=29){
            valueCode=5;
        }else if(age<=39){
            valueCode=6;
        }else if(age<=49){
            valueCode=7;
        }else if(age<=59){
            valueCode=8;
        }else {
            valueCode=9;
        }


        dataSetSynPop.getPersonDataSet().setValueAt(personCount,"ageCode",valueCode);

//                1 Value = 1.0	Label = Less than 1 year
//                1 Value = 2.0	Label = 1 - 2 years
//                1 Value = 3.0	Label = 3 - 4 years
//                2 Value = 4.0	Label = 5 - 10 years
//                3 Value = 5.0	Label = 11 - 15 years
//                3 Value = 6.0	Label = 16 years
//                4 Value = 7.0	Label = 17 years
//                4 Value = 8.0	Label = 18 years
//                4 Value = 9.0	Label = 19 years
//                4 Value = 10.0	Label = 20 years
//                5 Value = 11.0	Label = 21 - 25 years
//                5 Value = 12.0	Label = 26 - 29 years
//                6 Value = 13.0	Label = 30 - 39 years
//                7 Value = 14.0	Label = 40 - 49 years
//                8 Value = 15.0	Label = 50 - 59 years
//                9 Value = 16.0	Label = 60 - 64 years
//                9 Value = 17.0	Label = 65 - 69 years
//                9 Value = 18.0	Label = 70 - 74 years
//                9 Value = 19.0	Label = 75 - 79 years
//                9 Value = 20.0	Label = 80 - 84 years
//                9 Value = 21.0	Label = 85 years +

    }

    private void translateDwellingType(int hhCount){
        int valueCode = 0;
        int ddType = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(hhCount,"ddType");
        switch (ddType){
            case 1:
                valueCode = 1;
                break;
            case 2:
                valueCode = 2;
                break;
            case 3: //
                valueCode = 3;
                break;
            case 4:
                valueCode = 4;
                break;
        }
//         Value = 1.0	Label = House / bungalow (detached)
//         Value = 2.0	Label = House / bungalow (semi-detached)
//         Value = 3.0	Label = House / bungalow (terrace / end terrace)
//         Value = 4.0	Label = flat, other


        dataSetSynPop.getHouseholdDataSet().setValueAt(hhCount,"ddTypeCode",valueCode);
    }


    private void appendNewColumnToTDS(TableDataSet tableDataSet, String columnName){
        int length = tableDataSet.getRowCount();
        int[] dummy = SiloUtil.createArrayWithValue(length, 0);
        tableDataSet.appendColumn(dummy, columnName);
    }
}
