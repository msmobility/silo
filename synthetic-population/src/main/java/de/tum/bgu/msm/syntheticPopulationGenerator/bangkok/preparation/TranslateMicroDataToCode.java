package de.tum.bgu.msm.syntheticPopulationGenerator.bangkok.preparation;


import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.commons.math.MathException;
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
        for (int personCount : dataSetSynPop.getPersonDataSet().getColumnAsInt("ppThaiId")){
            translateOccupation(personCount);
            translateRelationshipToHouseholdHead(personCount);
            translateMarriage(personCount);
        }
        //convert one by one the records from microDwellings
        for (int hhCount : dataSetSynPop.getHouseholdDataSet().getColumnAsInt("hhThaiId")){
            translateDwellingUsage(hhCount);
            translateDwellingType(hhCount);
        }
        logger.info("   Finished translating the micro data");
    }

    private void initializeNewVariables(){
        appendNewColumnToTDS(dataSetSynPop.getPersonDataSet(), "employmentCode");
        appendNewColumnToTDS(dataSetSynPop.getPersonDataSet(),"relationshipCode");
        appendNewColumnToTDS(dataSetSynPop.getPersonDataSet(),"maritalCode");
        appendNewColumnToTDS(dataSetSynPop.getHouseholdDataSet(),"tenureCode");
        appendNewColumnToTDS(dataSetSynPop.getHouseholdDataSet(),"ddTypeCode");
    }

    private void translateMarriage(int personCount){
        int valueCode = 0;
        int valueMicroData = (int) dataSetSynPop.getPersonDataSet().getValueAt(personCount,"maritalStatus");
        switch (valueMicroData){
            case 1: //Single
                valueCode = 1;
                break;
            case 2: //Married
                valueCode = 2;
                break;
            case 3: //Widow
                valueCode = 1;
                break;
            case 4: //divorced
                valueCode = 1;
                break;
            case 5: //separated
                valueCode = 1;
                break;
            case 6: //even married but unknown status
                valueCode = 1;
                break;
            case 9: //unkown
                valueCode = 3;
                break;
        }
        dataSetSynPop.getPersonDataSet().setValueAt(personCount,"maritalCode", valueCode);
    }

    private void translateOccupation(int personCount) {
        if ((int) dataSetSynPop.getPersonDataSet().getValueAt(personCount,"age") < 16){
            dataSetSynPop.getPersonDataSet().setValueAt(personCount,"employmentCode", 4);
        } else {
            int occupation = (int) dataSetSynPop.getPersonDataSet().getValueAt(personCount, "employmentStatus");
            if (occupation == 0) {
                dataSetSynPop.getPersonDataSet().setValueAt(personCount, "employmentCode", 3);
            } else if (occupation == 9) {
                dataSetSynPop.getPersonDataSet().setValueAt(personCount, "employmentCode", 3);
            } else if (occupation == 3) {
                dataSetSynPop.getPersonDataSet().setValueAt(personCount, "employmentCode", 2);
            } else {
                dataSetSynPop.getPersonDataSet().setValueAt(personCount, "employmentCode", 1);
            }
        }
        // 1 = Employer
        // 2 = A private business operator without an employee
        // 3 = Household business assistant without being paid
        // 4 = Government employees
        // 5 = State Enterprise Employees
        // 6 = Private Employee
        // 7 = Members of bundles
        // 9 = Unknown
        // blank = not eligible to ask

        //in our internal code:
        // 1 = employed with pay
        // 2 = employed without pay
        // 3 = unemployed or retiree
        // 4 = student
    }


    private void translateRelationshipToHouseholdHead(int personCount){
        int valueCode = 0;
        int valueMicroData = (int) dataSetSynPop.getPersonDataSet().getValueAt(personCount,"role");
        switch (valueMicroData){
            case 1: //Household head (hHH)
                valueCode = 1;
                break;
            case 2: //Partner of hHH
                valueCode = 2;
                break;
            case 3: //unmarried kid of hHH
                valueCode = 3;
                break;
            case 4: //married kid of hHH
                valueCode = 4;
                break;
            case 5: //son in law
                valueCode = 4;
                break;
            case 6: //grandchild,
                valueCode = 3;
                break;
            case 7: //parent of hHH
                valueCode = 5;
                break;
            case 8: //workers
                valueCode = 6;
                break;
            case 9: //not related with hHH
                valueCode = 6;
                break;
        }
        dataSetSynPop.getPersonDataSet().setValueAt(personCount,"relationshipCode", valueCode);
    }


    private void translateHighestEducationalDegree(int personCount){
        int valueCode = 0;
        int valueMicroData = (int) dataSetSynPop.getPersonDataSet().getValueAt(personCount,"highestEducationDegree");
        switch (valueMicroData) {
            case -3: //Younger than 15 years old
                valueCode = 1;
                break;
            case -5: //No educational degree
                valueCode = 1;
                break;
            case 1: //Anlernausbildung oder berufliches Praktikum
                valueCode = 2;
                break;
            case 2: //Berufsvorbereitungsjahr
                valueCode = 2;
                break;
            case 3: //Abschluss einer Lehre/Berufsausbildung im dualen System
                valueCode = 2;
                break;
            case 4: //Berufsqualifizierender Abschluss
                valueCode = 2;
                break;
            case 5: //Abschluss einer Meister-/Technikerausbildung
                valueCode = 2;
                break;
            case 6: //Abschluss der Fachschule der DDR
                valueCode = 2;
                break;
            case 7: //Abschluss einer Verwaltungsfachhochschule
                valueCode = 3;
                break;
            case 8: //Fachhochschulabschluss
                valueCode = 3;
                break;
            case 9: //Abschluss einer Universität, wissenschaftlichen Hochschule, Kunsthochschule
                valueCode = 4;
                break;
            case 10: //Promotion
                valueCode = 4;
                break;
            case 11: //Vorbereitungsdienst für den mittleren Dienst in der öffentlichen Verwaltung
                valueCode = 4;
                break;
            case 12: //Abschluss einer Berufsakademie
                valueCode = 4;
                break;
            case 99: //Keine Angabe - Only 128 out of 489630
                valueCode = 0;
                break;
        }
        dataSetSynPop.getPersonTable().put(personCount,"educationDegree", valueCode);
    }

    private void translateDwellingUsage(int ddCount){
        int valueCode = 0;
        int valueMicroData = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(ddCount,"tenure");
        switch (valueMicroData){
            case 1:
                valueCode = 1;
                break;
            case 2:
                valueCode = 1;
                break;
            case 3: //
                valueCode = 2;
                break;
            case 4: //
                valueCode = 2;
                break;
            case 5: //
                valueCode = 2;
                break;
            case 6: //
                valueCode = 2;
                break;
            case 7: //
                valueCode = 2;
                break;
            case 9: //
                valueCode = 2;
                break;
        }
        // 1 = Owned with installment obligations
        // 2 = Ownership, no installment obligations
        // 3 = Hire Purchase
        // 4 = Rent
        // 5 = Live without rent because of the part.
        //       of wages.
        // 6 = Live without rent because you are naked.
        // 7 = Other
        // 9 = Unknown
        // blank = not eligible to ask
        dataSetSynPop.getHouseholdDataSet().setValueAt(ddCount,"tenureCode",valueCode);
    }

    private void translateDwellingType(int ddCount){
        int valueCode = 0;
        int valueMicroData = (int) dataSetSynPop.getHouseholdDataSet().getValueAt(ddCount,"dwellingType");
        switch (valueMicroData){
            case 1:
                valueCode = 5;
                break;
            case 2:
                valueCode = 5;
                break;
            case 3: //
                valueCode = 1;
                break;
            case 4: //
                valueCode = 3;
                break;
            case 5: //
                valueCode = 5;
                break;
            case 6: //
                valueCode = 3;
                break;
            case 7: //
                valueCode = 3;
                break;
            case 8: //
                valueCode = 3;
                break;
            case 9: //
                valueCode = 3;
                break;
            case 0: //
                valueCode = 3;
                break;
        }
        // 1 = Single house
        // 2 = Townhouse/Semi-detached house
        // 3 = Condominium
        // 4 = Flat/apartment/dormitory - low rise condo
        // 5 = Row/rowhouse
        // 6 = Home room
        // 7 = Office room
        // 8 = Boad/raft/car
        // 9 = Unknown
        // 0 = Other
        // blank = not eligible to ask
        dataSetSynPop.getHouseholdDataSet().setValueAt(ddCount,"ddTypeCode",valueCode);
    }



    public int translateIncome(int valueMicroData){
        int valueCode = 0;
        double low = 0;
        double high = 1;
        double income = 0;
        switch (valueMicroData){
            case 90: // kein Einkommen
                valueCode = 0;
                break;
            case 50: //Selbständige/r Landwirt/in in der Haupttätigkeit
                low = 0; //give them a random income following the distribution
                high = 1;
                break;
            case 99: ///keine Angabe
                low = 0; //give them a random income following the distribution
                high = 1;
                break;
            case 1: //income class
                low = 0;
                high = 0.07998391;
                break;
            case 2: //income class
                low = 0.07998391;
                high = 0.15981282;
                break;
            case 3: //income class
                low = 0.15981282;
                high = 0.25837521;
                break;
            case 4: //income class
                low = 0.25837521;
                high = 0.34694010;
                break;
            case 5: //income class
                low = 0.34694010;
                high = 0.42580696;
                break;
            case 6: //income class
                low = 0.42580696;
                high = 0.49569720;
                break;
            case 7: //income class
                low = 0.49569720;
                high = 0.55744375;
                break;
            case 8: //income class
                low = 0.55744375;
                high = 0.61188119;
                break;
            case 9: //income class
                low = 0.61188119;
                high = 0.65980123;
                break;
            case 10: //income class
                low = 0.65980123;
                high = 0.72104215;
                break;
            case 11: //income class
                low = 0.72104215;
                high = 0.77143538;
                break;
            case 12: //income class
                low = 0.77143538;
                high = 0.81284178;
                break;
            case 13: //income class
                low = 0.81284178;
                high = 0.84682585;
                break;
            case 14: //income class
                low = 0.84682585;
                high = 0.87469331;
                break;
            case 15: //income class
                low = 0.87469331;
                high = 0.90418202;
                break;
            case 16: //income class
                low = 0.90418202;
                high = 0.92677087;
                break;
            case 17: //income class
                low = 0.92677087;
                high = 0.94770566;
                break;
            case 18: //income class
                low = 0.94770566;
                high = 0.96267752;
                break;
            case 19: //income class
                low = 0.96267752;
                high = 0.97337602;
                break;
            case 20: //income class
                low = 0.97337602;
                high = 0.98101572;
                break;
            case 21: //income class
                low = 0.98101572;
                high = 0.99313092;
                break;
            case 22: //income class
                low = 0.99313092;
                high = 0.99874378;
                break;
            case 23: //income class
                low = 0.99874378;
                high = 0.99999464;
                break;
            case 24: //income class
                low = 0.99999464;
                high = 1;
                break;
        }
        double cummulativeProb = SiloUtil.getRandomNumberAsDouble()*(high - low) + low;
        try {
            income = PropertiesSynPop.get().main.incomeGammaDistribution.inverseCumulativeProbability(cummulativeProb);
            valueCode = (int) income;
        } catch (MathException e) {
            e.printStackTrace();
        }
        return valueCode;
    }

    private void appendNewColumnToTDS(TableDataSet tableDataSet, String columnName){
        int length = tableDataSet.getRowCount();
        int[] dummy = SiloUtil.createArrayWithValue(length, 0);
        tableDataSet.appendColumn(dummy, columnName);
    }
}
