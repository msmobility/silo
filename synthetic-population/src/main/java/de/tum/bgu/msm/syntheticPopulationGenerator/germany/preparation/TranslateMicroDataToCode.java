package de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation;


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

        //convert one by one the records from microPersons
        for (int personCount = 1; personCount <= dataSetSynPop.getPersonTable().rowKeySet().size(); personCount++){
            boolean attendingSchool = translateSchoolAttendance(personCount);
            translateOccupation(personCount, attendingSchool);
            translateSector(personCount);
        }
        logger.info("   Finished translating the micro data");
    }


    private void translateOccupation(int personCount, boolean attendingSchool) {
        int occupation = (int) dataSetSynPop.getPersonTable().get(personCount,"occupation");
        if (occupation > 1){
            if (attendingSchool){
                dataSetSynPop.getPersonTable().put(personCount,"occupation",3);

            } else {
                dataSetSynPop.getPersonTable().put(personCount,"occupation",2);
            }
        }
    }


    private void translateSector(int personCount){
        int sector = (int)dataSetSynPop.getPersonTable().get(personCount,"sector");
        if (sector < 1) {
            dataSetSynPop.getPersonTable().put(personCount, "sector", 0);
        } else if (sector < 50){
            dataSetSynPop.getPersonTable().put(personCount,"sector",1);
        } else if (sector < 450){
            dataSetSynPop.getPersonTable().put(personCount,"sector",2);
        } else if (sector < 1000){
            dataSetSynPop.getPersonTable().put(personCount,"sector",3);
        } else {
            dataSetSynPop.getPersonTable().put(personCount,"sector",0);
        }
    }


    private boolean translateSchoolAttendance(int personCount){
        int valueCode = 0;
        int valueMicroData = (int) dataSetSynPop.getPersonTable().get(personCount,"school");
        switch (valueMicroData){
            case -5:
                valueCode = 0;
                break;
            case 1: //Grundschule
                valueCode = 1;
                break;
            case 2: //Hauptschule
                valueCode = 2;
                break;
            case 3: //Realschule
                valueCode = 2;
                break;
            case 4: //Schulartunabhängige Orientierungsstufe
                valueCode = 2;
                break;
            case 5: //Schularten mit mehreren Bildungsgängen
                valueCode = 2;
                break;
            case 6: //Gesamtschule, Waldorfschule
                valueCode = 2;
                break;
            case 7: //Gymnasium
                valueCode = 2;
                break;
            case 8: //Sonderschule (Förderschule)
                valueCode = 2;
                break;
            case 9: //Berufsvorbereitungsjahr
                valueCode = 0;
                break;
            case 10: //Berufliche Schule
                valueCode = 0;
                break;
            case 11: //Berufsgrundbildungsjahr
                valueCode = 0;
                break;
            case 12: //Berufliche Schule
                valueCode = 0;
                break;
            case 13: //Berufsschule
                valueCode = 0;
                break;
            case 14: //Berufsfachschule,
                valueCode = 0;
                break;
            case 15: //Fachschule
                valueCode = 3;
                break;
            case 16: //Fach-/Berufsakademie
                valueCode = 3;
                break;
            case 17: //2- oder 3-jährige Schule des Gesundheitswesens
                valueCode = 3;
                break;
            case 18: //Verwaltungsfachhochschule
                valueCode = 3;
                break;
            case 19: //Fachhochschule
                valueCode = 3;
                break;
            case 20: //Universität
                valueCode = 3;
                break;
            case 21: //Promotionsstudium
                valueCode = 3;
                break;
            case 99: //Keine Angabe - Only 128 out of 489630
                valueCode = 0;
                break;
        }
        dataSetSynPop.getPersonTable().put(personCount,"school", valueCode);
        boolean schoolAttendance = false;
        if (valueCode > 0){
            schoolAttendance = true;
        }
        return schoolAttendance;
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

}
