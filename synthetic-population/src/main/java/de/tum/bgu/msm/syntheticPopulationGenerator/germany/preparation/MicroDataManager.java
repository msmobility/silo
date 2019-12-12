package de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation;


import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.DwellingUsage;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Nationality;
import de.tum.bgu.msm.data.person.PersonRole;
import de.tum.bgu.msm.data.person.Race;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.commons.math.MathException;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class MicroDataManager {

    private static final Logger logger = Logger.getLogger(MicroDataManager.class);

    private final DataSetSynPop dataSetSynPop;

    public MicroDataManager(DataSetSynPop dataSetSynPop){this.dataSetSynPop = dataSetSynPop;}


    public HashMap<String, String[]> attributesMicroData(){

        HashMap<String, String[]> attributesMicroData = new HashMap<>();
        String[] attributesPerson = {"age", "gender", "occupation", "income", "nationality", "school"};
        String[] attributesHousehold = {"workers", "hhSize"};
        attributesMicroData.put("person", attributesPerson);
        attributesMicroData.put("household", attributesHousehold);
        return attributesMicroData;
    }


    public Map<String, Map<String, Integer>> attributesPersonMicroData(){

        Map<String, Map<String, Integer>> attributesIPU = new HashMap<>();
        //IPU attributes
            Map<String, Integer> age = new HashMap<>();
                age.put("initial", 50);
                age.put("end", 52);
                attributesIPU.put("age", age);
            Map<String, Integer> gender = new HashMap<>();
                gender.put("initial", 54);
                gender.put("end", 55);
                attributesIPU.put("gender", gender);
            Map<String, Integer> occupation = new HashMap<>();
                occupation.put("initial", 32);
                occupation.put("end", 33);
                attributesIPU.put("occupation", occupation);
        //Additional attributes
            Map<String, Integer> income = new HashMap<>();
                income.put("initial", 471);
                income.put("end", 473);
                attributesIPU.put("income", income);
            Map<String, Integer> sector = new HashMap<>();
                sector.put("initial", 479);
                sector.put("end", 482);
                attributesIPU.put("sector", sector);
            Map<String, Integer> sectorComplete = new HashMap<>();
                sectorComplete.put("initial", 479);
                sectorComplete.put("end", 482);
                attributesIPU.put("sectorComplete", sectorComplete);
            Map<String, Integer> school = new HashMap<>();
                school.put("initial", 307);
                school.put("end", 309);
                attributesIPU.put("school", school);
            return attributesIPU;
    }


    public Map<String, Map<String, Integer>> attributesHouseholdMicroData(){

        Map<String, Map<String, Integer>> attributesIPU = new HashMap<>();
        //IPU attributes
            Map<String, Integer> hhSize = new HashMap<>();
                hhSize.put("initial", 26);
                hhSize.put("end", 28);
                attributesIPU.put("hhSize", hhSize);
        //Additional attributes
            Map<String, Integer> workers = new HashMap<>();
                workers.put("initial", 572);
                workers.put("end", 574);
                attributesIPU.put("workers", workers);
        return attributesIPU;
    }


    public Map<String, Map<String, Integer>> exceptionsMicroData(String stateString){

        Map<String, Map<String, Integer>> exceptionsMicroData = new HashMap<>();
        Map<String, Integer> LivingInQuarter = new HashMap<>();
        LivingInQuarter.put("initial", 34);
        LivingInQuarter.put("end", 35);
        LivingInQuarter.put("exceptionIf", 2);
        exceptionsMicroData.put("quarter", LivingInQuarter);
        Map<String, Integer> noIncome = new HashMap<>();
        noIncome.put("initial", 658);
        noIncome.put("end", 660);
        noIncome.put("exceptionIf", 99);
        exceptionsMicroData.put("noIncome", noIncome);
        int state = 1;
        if (stateString.length() == 2) {
            state = Integer.parseInt(stateString);
        } else {
            state = Integer.parseInt(stateString.substring(1,2));
        }
        for (int i = 1; i < 16; i++ ) {
            if (i!=state) {
                String nameState = "State" + i;
                Map<String, Integer> key = new HashMap<>();
                key.put("initial", 0);
                key.put("end", 2);
                key.put("exceptionIf", i);
                exceptionsMicroData.put(nameState, key);
            }
        }
        return exceptionsMicroData;
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


    public static boolean obtainLicense(Gender gender, int age){
        boolean license = false;
        int row = 1;
        int threshold = 0;
        if (age > 17) {
            if (age < 29) {
                if (gender == Gender.MALE) {
                    threshold = 86;
                } else {
                    threshold = 87;
                }
            } else if (age < 39) {
                if (gender == Gender.MALE) {
                    threshold = 95;
                } else {
                    threshold = 94;
                }
            } else if (age < 49) {
                if (gender == Gender.MALE) {
                    threshold = 97;
                } else {
                    threshold = 95;
                }
            } else if (age < 59) {
                if (gender == Gender.MALE) {
                    threshold = 96;
                } else {
                    threshold = 89;
                }
            } else if (age < 64) {
                if (gender == Gender.MALE) {
                    threshold = 95;
                } else {
                    threshold = 86;
                }
            } else if (age < 74) {
                if (gender == Gender.MALE) {
                    threshold = 95;
                } else {
                    threshold = 71;
                }
            } else {
                if (gender == Gender.MALE) {
                    threshold = 88;
                } else {
                    threshold = 44;
                }
            }
            if (SiloUtil.getRandomNumberAsDouble() * 100 < threshold) {
                license = true;
            }
        }
        return license;
    }



    public int guessjobType(int gender, int educationLevel){
        int jobType = 0;
        float[] cumProbability;
        switch (gender){
            case 1:
                switch (educationLevel) {
                    case 0:
                        cumProbability = new float[]{0.01853f,0.265805f,0.279451f,0.382040f,0.591423f,0.703214f,0.718372f,0.792528f,0.8353f,1.0f};
                        break;
                    case 1:
                        cumProbability = new float[]{0.01853f,0.265805f,0.279451f,0.382040f,0.591423f,0.703214f,0.718372f,0.792528f,0.8353f,1.0f};
                        break;
                    case 2:
                        cumProbability = new float[]{0.025005f,0.331942f,0.355182f,0.486795f,0.647928f,0.0748512f,0.779124f,0.838452f,0.900569f,1f};
                        break;
                    case 3:
                        cumProbability = new float[]{0.008533f,0.257497f,0.278324f,0.323668f,0.39151f,0.503092f,0.55153f,0.588502f,0.795734f,1f};
                        break;
                    case 4:
                        cumProbability = new float[]{0.004153f,0.154197f,0.16906f,0.19304f,0.246807f,0.347424f,0.387465f,0.418509f,0.4888415f,1f};
                        break;
                    default: cumProbability = new float[]{0.025005f,0.331942f,0.355182f,0.486795f,0.647928f,0.0748512f,0.779124f,0.838452f,0.900569f,1f};
                }
                break;
            case 2:
                switch (educationLevel) {
                    case 0:
                        cumProbability = new float[]{0.012755f,0.153795f,0.159108f,0.174501f,0.448059f,0.49758f,0.517082f,0.616346f,0.655318f,1f};
                        break;
                    case 1:
                        cumProbability = new float[]{0.012755f,0.153795f,0.159108f,0.174501f,0.448059f,0.49758f,0.517082f,0.616346f,0.655318f,1f};
                        break;
                    case 2:
                        cumProbability = new float[]{0.013754f,0.137855f,0.145129f,0.166915f,0.389282f,0.436095f,0.479727f,0.537868f,0.603158f,1f};
                        break;
                    case 3:
                        cumProbability = new float[]{0.005341f,0.098198f,0.109149f,0.125893f,0.203838f,0.261698f,0.314764f,0.366875f,0.611298f,1f};
                        break;
                    case 4:
                        cumProbability = new float[]{0.002848f,0.061701f,0.069044f,0.076051f,0.142332f,0.197382f,0.223946f,0.253676f,0.327454f,1f};
                        break;
                    default: cumProbability = new float[]{0.013754f,0.137855f,0.145129f,0.166915f,0.389282f,0.436095f,0.479727f,0.537868f,0.603158f,1f};
                }
                break;
                default: cumProbability = new float[]{0.025005f,0.331942f,0.355182f,0.486795f,0.647928f,0.0748512f,0.779124f,0.838452f,0.900569f,1f};
        }
        float threshold = SiloUtil.getRandomNumberAsFloat();
        for (int i = 0; i < cumProbability.length; i++) {
            if (cumProbability[i] > threshold) {
                return i;
            }
        }
        return cumProbability.length - 1;
    }

    public String translateJobType(int sector) {

        //According to the classification WZ08
        String sectorString = "";
        if (sector < 0){
            sectorString = "";
        } else if (sector < 100){
            sectorString = "Agri";
        } else if (sector < 350){
            sectorString = "Mnft";
        } else if (sector < 400){
            sectorString = "Util";
        } else if (sector < 450){
            sectorString = "Cons";
        } else if (sector < 490){
            sectorString = "Retl";
        } else if (sector < 550){
            sectorString = "Trns";
        } else if (sector < 580){
            sectorString = "Retl";
        } else if (sector < 640){
            sectorString = "Trns";
        } else if (sector < 663){
            sectorString = "Finc";
        } else if (sector < 830){
            sectorString = "Rlst";
        } else if (sector < 850){
            sectorString = "Admn";
        } else if (sector < 1000){
            sectorString = "Serv";
        }


        return sectorString;
    }

}
