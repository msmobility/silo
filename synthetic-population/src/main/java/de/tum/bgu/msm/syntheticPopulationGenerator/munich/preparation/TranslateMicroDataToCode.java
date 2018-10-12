package de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation;


import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
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
            translateHighestEducationalDegree(personCount);
            translateOccupation(personCount, attendingSchool);
            translateRelationshipToHouseholdHead(personCount);
        }
        //convert one by one the records from microDwellings
        for (int ddCount = 1; ddCount <= dataSetSynPop.getDwellingTable().rowKeySet().size(); ddCount++){
            translateDwellingUsage(ddCount);
            translateDwellingHeatingEnergy(ddCount);
            translateDwellingHeatingType(ddCount);
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


    private void translateRelationshipToHouseholdHead(int personCount){
        int valueCode = 0;
        int valueMicroData = (int) dataSetSynPop.getPersonTable().get(personCount,"relationship");
        switch (valueMicroData){
            case 1: //Household head (hHH)
                valueCode = 1;
                break;
            case 2: //Partner of hHH
                valueCode = 2;
                break;
            case 3: //kid of hHH
                valueCode = 3;
                break;
            case 4: //grandchild of hHH
                valueCode = 3;
                break;
            case 5: //mother or father of hHH
                valueCode = 4;
                break;
            case 6: //grandfather or grandmother of hHH,
                valueCode = 4;
                break;
            case 7: //sibling of hHH
                valueCode = 4;
                break;
            case 8: //other relationship with hHH
                if ((int) dataSetSynPop.getPersonTable().get(personCount, "age") < 16) {
                    valueCode = 3;
                } else {
                    valueCode = 4;
                }
                break;
            case 9: //not related with hHH
                if ((int) dataSetSynPop.getPersonTable().get(personCount, "age") < 16) {
                    valueCode = 3;
                } else {
                    valueCode = 4;
                }
                break;
        }
        dataSetSynPop.getPersonTable().put(personCount,"relationship", valueCode);
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


    private void translateHighestEducationalDegree(int personCount){
        int valueCode = 0;
        int valueMicroData = (int) dataSetSynPop.getPersonTable().get(personCount,"educationDegree");
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
        int valueMicroData = (int) dataSetSynPop.getDwellingTable().get(ddCount,"ddUse");
        switch (valueMicroData){
            case -1: //Group quarter
                valueCode = 0;
                break;
            case -5: //Moved out last year
                valueCode = 0;
                break;
            case 1: //Owner of building
                valueCode = 1;
                break;
            case 2: //Owner of dwelling
                valueCode = 1;
                break;
            case 3: //Renter (principal)
                valueCode = 2;
                break;
            case 4: //Sub-renter
                valueCode = 2;
                break;
            case 9: //No data
                valueCode = 0;
                break;
        }
        dataSetSynPop.getDwellingTable().put(ddCount,"ddUse",valueCode);
    }


    private void translateDwellingHeatingEnergy(int ddCount){
        int valueCode = 0;
        int valueMicroData = (int) dataSetSynPop.getDwellingTable().get(ddCount,"ddHeatingEnergy");
        switch (valueMicroData){
            case -1: //Group quarter
                valueCode = 0;
                break;
            case -5: //Moved out last year
                valueCode = 0;
                break;
            case 1: //Fernwärme (bei Fernheizung)
                valueCode = 1;
                break;
            case 2: //Gas
                valueCode = 1;
                break;
            case 3: //Elektrizität, Strom (ohne Wärmepumpe)
                valueCode = 1;
                break;
            case 4: //Heizöl
                valueCode = 1;
                break;
            case 5: //Briketts, Braunkohle
                valueCode = 0;
                break;
            case 6: //Koks, Steinkohle
                valueCode = 0;
                break;
            case 7: //Holz, Holzpellets
                valueCode = 0;
                break;
            case 8: //Biomasse (außer Holz), Biogas
                valueCode = 0;
                break;
            case 9: //Sonnenenergie (Solarkollektoren)
                valueCode = 1;
                break;
            case 10: //Erd- und andere Umweltwärme, Abluftwärme (Wärmepumpen, -tauscher)
                valueCode = 1;
                break;
            case 99: //No data
                valueCode = 0;
                break;
        }
        dataSetSynPop.getDwellingTable().put(ddCount,"ddHeatingEnergy",valueCode);
    }


    private void translateDwellingHeatingType(int ddCount){
        int valueCode = 0;
        int valueMicroData = (int) dataSetSynPop.getDwellingTable().get(ddCount,"ddHeatingType");
        switch (valueMicroData){
            case -1: //Group quarter
                valueCode = 0;
                break;
            case -5: //Moved out last year
                valueCode = 0;
                break;
            case 1: //Fernheizung
                valueCode = 1;
                break;
            case 2: //Blockheizung, Zentralheizung
                valueCode = 1;
                break;
            case 3: //Etagenheizung
                valueCode = 0;
                break;
            case 4: //Einzel- oder Mehrraumöfen (auch Elektrospeicher)
                valueCode = 0;
                break;
            case 9: //No data
                valueCode = 0;
                break;
        }
        dataSetSynPop.getDwellingTable().put(ddCount,"ddHeatingType",valueCode);
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
