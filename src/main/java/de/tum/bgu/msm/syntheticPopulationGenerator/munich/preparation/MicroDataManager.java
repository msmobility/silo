package de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation;


import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
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
        String[] attributesPerson = {"age", "gender", "occupation", "income", "nationality", "telework", "ppPrivate", "educationDegree", "personStatus", "spouseInHousehold", "marriage", "relationship", "school"};
        String[] attributesHousehold = {"workers", "hhSize"};
        String[] attributesDwelling = {"ddUse", "ddYear", "ddFloor", "ddSize", "ddHeatingEnergy", "ddHeatingType", "ddAdHeating"};
        attributesMicroData.put("person", attributesPerson);
        attributesMicroData.put("household", attributesHousehold);
        attributesMicroData.put("dwelling", attributesDwelling);
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
            Map<String, Integer> nationality = new HashMap<>();
                nationality.put("initial", 370);
                nationality.put("end", 372);
                attributesIPU.put("nationality", nationality);
        //Additional attributes
            Map<String, Integer> income = new HashMap<>();
                income.put("initial", 471);
                income.put("end", 473);
                attributesIPU.put("income", income);
            Map<String, Integer> telework = new HashMap<>();
                telework.put("initial", 198);
                telework.put("end", 200);
                attributesIPU.put("telework", telework);
            Map<String, Integer> ppPrivate = new HashMap<>();
                ppPrivate.put("initial", 34);
                ppPrivate.put("end", 35);
                attributesIPU.put("ppPrivate", ppPrivate);
            Map<String, Integer> educationDegree = new HashMap<>();
                educationDegree.put("initial", 323);
                educationDegree.put("end", 325);
                attributesIPU.put("educationDegree", educationDegree);
            Map<String, Integer> status = new HashMap<>();
                status.put("initial", 40);
                status.put("end", 42);
                attributesIPU.put("personStatus", status);
            Map<String, Integer> spouse = new HashMap<>();
                spouse.put("initial", 36);
                spouse.put("end", 38);
                attributesIPU.put("spouseInHousehold", spouse);
            Map<String, Integer> marriage = new HashMap<>();
                marriage.put("initial", 59);
                marriage.put("end", 60);
                attributesIPU.put("marriage", marriage);
            Map<String, Integer> relationship = new HashMap<>();
                relationship.put("initial", 566);
                relationship.put("end", 568);
                attributesIPU.put("relationship", relationship);
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


    public Map<String, Map<String, Integer>> attributesDwellingMicroData(){

        Map<String, Map<String, Integer>> attributesIPU = new HashMap<>();
        //IPU attributes
            Map<String, Integer> ddUse = new HashMap<>();
                ddUse.put("initial", 493);
                ddUse.put("end", 495);
                attributesIPU.put("ddUse", ddUse);
            Map<String, Integer> ddYear = new HashMap<>();
                ddYear.put("initial", 500);
                ddYear.put("end", 502);
                attributesIPU.put("ddYear", ddYear);
            Map<String, Integer> ddFloor = new HashMap<>();
                ddFloor.put("initial", 495);
                ddFloor.put("end", 498);
                attributesIPU.put("ddFloor", ddFloor);
            Map<String, Integer> ddSize = new HashMap<>();
                ddSize.put("initial", 491);
                ddSize.put("end", 493);
                attributesIPU.put("ddSize", ddSize);
        //Additional attributes
            Map<String, Integer> ddHeatingEnergy = new HashMap<>();
                ddHeatingEnergy.put("initial", 506);
                ddHeatingEnergy.put("end", 508);
                attributesIPU.put("ddHeatingEnergy", ddHeatingEnergy);
            Map<String, Integer> ddHeatingType = new HashMap<>();
                ddHeatingType.put("initial", 504);
                ddHeatingType.put("end", 506);
                attributesIPU.put("ddHeatingType", ddHeatingType);
            Map<String, Integer> ddAdHeating = new HashMap<>();
                ddAdHeating.put("initial", 1017);
                ddAdHeating.put("end", 1019);
                attributesIPU.put("ddAdHeating", ddAdHeating);
        return attributesIPU;
    }

    public Map<String, Map<String, Integer>> exceptionsMicroData(){

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
            Map<String, Integer> movingOutInFiveYears = new HashMap<>();
                movingOutInFiveYears.put("initial", 491);
                movingOutInFiveYears.put("end", 493);
                movingOutInFiveYears.put("exceptionIf", -5);
                exceptionsMicroData.put("movedOut", movingOutInFiveYears);
            Map<String, Integer> noBuildingSize = new HashMap<>();
                noBuildingSize.put("initial", 491);
                noBuildingSize.put("end", 493);
                noBuildingSize.put("exceptionIf", 9);
                exceptionsMicroData.put("noSize", noBuildingSize);
            Map<String, Integer> noDwellingUsage = new HashMap<>();
                noDwellingUsage.put("initial", 493);
                noDwellingUsage.put("end", 495);
                noDwellingUsage.put("exceptionIf", 9);
                exceptionsMicroData.put("noUsage", noDwellingUsage);
            Map<String, Integer> noBuildingYear = new HashMap<>();
                noBuildingYear.put("initial", 500);
                noBuildingYear.put("end", 502);
                noBuildingYear.put("exceptionIf", 99);
                exceptionsMicroData.put("noYear", noBuildingYear);
            Map<String, Integer> SchleswigHolstein = new HashMap<>();
                SchleswigHolstein.put("initial", 0);
                SchleswigHolstein.put("end", 2);
                SchleswigHolstein.put("exceptionIf", 1);
                exceptionsMicroData.put("out1", SchleswigHolstein);
            Map<String, Integer> Hamburg = new HashMap<>();
                Hamburg.put("initial", 0);
                Hamburg.put("end", 2);
                Hamburg.put("exceptionIf", 2);
                exceptionsMicroData.put("out2", Hamburg);
            Map<String, Integer> Niedersachsen = new HashMap<>();
                Niedersachsen.put("initial", 0);
                Niedersachsen.put("end", 2);
                Niedersachsen.put("exceptionIf", 3);
                exceptionsMicroData.put("out3", Niedersachsen);
            Map<String, Integer> Bremen = new HashMap<>();
                Bremen.put("initial", 0);
                Bremen.put("end", 2);
                Bremen.put("exceptionIf", 4);
                exceptionsMicroData.put("out4", Bremen);
            Map<String, Integer> NordrheinWestfalen = new HashMap<>();
                NordrheinWestfalen.put("initial", 0);
                NordrheinWestfalen.put("end", 2);
                NordrheinWestfalen.put("exceptionIf", 5);
                exceptionsMicroData.put("out5", NordrheinWestfalen);
            Map<String, Integer> Hessen = new HashMap<>();
                Hessen.put("initial", 0);
                Hessen.put("end", 2);
                Hessen.put("exceptionIf", 6);
                exceptionsMicroData.put("out6", Hessen);
            Map<String, Integer> RheinlandPfalz = new HashMap<>();
                RheinlandPfalz.put("initial", 0);
                RheinlandPfalz.put("end", 2);
                RheinlandPfalz.put("exceptionIf", 7);
                exceptionsMicroData.put("out7", RheinlandPfalz);
            Map<String, Integer> BadenWuerttemberg = new HashMap<>();
                BadenWuerttemberg.put("initial", 0);
                BadenWuerttemberg.put("end", 2);
                BadenWuerttemberg.put("exceptionIf", 8);
                exceptionsMicroData.put("out8", BadenWuerttemberg);
            Map<String, Integer> Saarland = new HashMap<>();
                Saarland.put("initial", 0);
                Saarland.put("end", 2);
                Saarland.put("exceptionIf", 10);
                exceptionsMicroData.put("out10", Saarland);
            Map<String, Integer> Berlin = new HashMap<>();
                Berlin.put("initial", 0);
                Berlin.put("end", 2);
                Berlin.put("exceptionIf", 11);
                exceptionsMicroData.put("out11", Berlin);
            Map<String, Integer> Brandenburg = new HashMap<>();
                Brandenburg.put("initial", 0);
                Brandenburg.put("end", 2);
                Brandenburg.put("exceptionIf", 12);
                exceptionsMicroData.put("out12", Brandenburg);
            Map<String, Integer> MecklenburgVorpommern = new HashMap<>();
                MecklenburgVorpommern.put("initial", 0);
                MecklenburgVorpommern.put("end", 2);
                MecklenburgVorpommern.put("exceptionIf", 13);
                exceptionsMicroData.put("out13", MecklenburgVorpommern);
            Map<String, Integer> Sachsen = new HashMap<>();
                Sachsen.put("initial", 0);
                Sachsen.put("end", 2);
                Sachsen.put("exceptionIf", 14);
                exceptionsMicroData.put("out14", Sachsen);
            Map<String, Integer> SachsenAnhalt = new HashMap<>();
                SachsenAnhalt.put("initial", 0);
                SachsenAnhalt.put("end", 2);
                SachsenAnhalt.put("exceptionIf", 15);
                exceptionsMicroData.put("out15", SachsenAnhalt);
            Map<String, Integer> Thueringen = new HashMap<>();
                Thueringen.put("initial", 0);
                Thueringen.put("end", 2);
                Thueringen.put("exceptionIf", 16);
                exceptionsMicroData.put("out16", Thueringen);
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


    public Race translateRace(int nationality){
        Race race = Race.white;
        if (nationality == 8){
            race = Race.black;
        }
        return race;
    }


    public Nationality translateNationality (int nationality){
        Nationality nationality1 = Nationality.german;
        if (nationality == 8){
            nationality1 = Nationality.other;
        }
        return nationality1;
    }


    public PersonRole translatePersonRole (int role){
        PersonRole personRole = PersonRole.single;
        if (role == 2) {
            personRole = PersonRole.married;
        } else if (role == 3) {
            personRole = PersonRole.child;
        }
        return personRole;
    }

    public boolean obtainLicense(int gender, int age){
        boolean license = false;
        int row = 1;
        int threshold = 0;
        if (age > 17) {
            if (age < 29) {
                if (gender == 0) {
                    threshold = 86;
                } else {
                    threshold = 87;
                }
            } else if (age < 39) {
                if (gender == 1) {
                    threshold = 95;
                } else {
                    threshold = 94;
                }
            } else if (age < 49) {
                if (gender == 1) {
                    threshold = 97;
                } else {
                    threshold = 95;
                }
            } else if (age < 59) {
                if (gender == 1) {
                    threshold = 96;
                } else {
                    threshold = 89;
                }
            } else if (age < 64) {
                if (gender == 1) {
                    threshold = 95;
                } else {
                    threshold = 86;
                }
            } else if (age < 74) {
                if (gender == 1) {
                    threshold = 95;
                } else {
                    threshold = 71;
                }
            } else {
                if (gender == 1) {
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


    public int guessDwellingQuality(int heatingType, int heatingEnergy, int additionalHeating, int yearBuilt){
        //guess quality of dwelling based on construction year and heating characteristics.
        //kitchen and bathroom quality are not coded on the micro data
        int quality = PropertiesSynPop.get().main.numberofQualityLevels;
        if (heatingType > 2) quality--; //reduce quality if not central or district heating
        if (heatingEnergy > 4) quality--; //reduce quality if energy is not gas, electricity or heating oil (i.e. coal, wood, biomass, solar energy)
        if (additionalHeating == 0) quality++; //increase quality if there is additional heating in the house (regardless the used energy)
        if (yearBuilt > 0){
            //Ages - 1: before 1919, 2: 1919-1948, 3: 1949-1978, 4: 1979 - 1986; 5: 1987 - 1990; 6: 1991 - 2000; 7: 2001 - 2004; 8: 2005 - 2008, 9: 2009 or later,
            float[] deteriorationProbability = {0.9f, 0.8f, 0.6f, 0.3f, 0.12f, 0.08f, 0.05f, 0.04f, 0.04f};
            float prob = deteriorationProbability[yearBuilt - 1];
            //attempt to drop quality by age two times (to get some spreading of quality levels)
            quality = quality - SiloUtil.select(new double[]{1 - prob ,prob});
            quality = quality - SiloUtil.select(new double[]{1 - prob, prob});
        }
        quality = Math.max(quality, 1);      // ensure that quality never drops below 1
        quality = Math.min(quality, PropertiesSynPop.get().main.numberofQualityLevels);      // ensure that quality never excess the number of quality levels
        return quality;
    }


    public DwellingType translateDwellingType (int buildingSize, float ddType1Prob, float ddType3Prob){
        DwellingType type = DwellingType.MF234;
        if (buildingSize < 3){
            if (SiloUtil.getRandomNumberAsFloat() < ddType1Prob){
                type = DwellingType.SFD;
            } else {
                type = DwellingType.SFA;
            }
        } else {
            if (SiloUtil.getRandomNumberAsFloat() < ddType3Prob){
                type = DwellingType.MF5plus;
            }
        }

        return type;
    }


    public int guessBedrooms (int floorSpace){
        int bedrooms = 0;
        if (floorSpace < 40){
            bedrooms = 0;
        } else if (floorSpace < 60){
            bedrooms = 1;
        } else if (floorSpace < 80){
            bedrooms = 2;
        } else if (floorSpace < 100){
            bedrooms = 3;
        } else if (floorSpace < 120){
            bedrooms = 4;
        } else {
            bedrooms = 5;
        }

        return bedrooms;
    }


    public int guessPrice(float brw, int quality, int size, int use) {

        //coefficient by quality of the dwelling
        float qualityReduction = 1;
        if (quality == 1){
            qualityReduction = 0.7f;
        } else if (quality == 2){
            qualityReduction = 0.9f;
        } else if (quality == 4){
            qualityReduction = 1.1f;
        }
        //conversion from land price to the monthly rent
        float convertToMonth = 0.0057f;
        //increase price for rented dwellings
        float rentedIncrease = 1; //by default, the price is not reduced/increased
        if (use == 2){
            rentedIncrease = 1.2f; //rented dwelling
        } else if (use == 3){
            rentedIncrease = 1; //vacant dwelling
        }
        //extra costs for power, water, etc (Nebenkosten)
        int nebenKost = 150;

        float price = brw * size * qualityReduction * convertToMonth * rentedIncrease + nebenKost;
        return (int) price;
    }


    public int guessFloorSpace(int floorSpace){
        //provide the size of the building
        int floorSpaceDwelling = 0;
        switch (floorSpace){
            case 60:
                floorSpaceDwelling = (int) (30 + SiloUtil.getRandomNumberAsFloat() * 40);
                break;
            case 80:
                floorSpaceDwelling = (int) (60 + SiloUtil.getRandomNumberAsFloat() * 20);
                break;
            case 100:
                floorSpaceDwelling = (int) (80 + SiloUtil.getRandomNumberAsFloat() * 20);
                break;
            case 120:
                floorSpaceDwelling = (int) (100 + SiloUtil.getRandomNumberAsFloat() * 20);
                break;
            case 2000:
                floorSpaceDwelling = (int) (120 + SiloUtil.getRandomNumberAsFloat() * 50);
                break;
        }
        return floorSpaceDwelling;
    }


    public int dwellingYearBracket(int year){

        int yearBracket = 0;
        if (year < 1949){
            yearBracket = 2;
        } else if (year < 1991){
            yearBracket = 5;
        } else if (year < 2001){
            yearBracket = 6;
        } else {
            yearBracket = 9;
        }

        return yearBracket;
    }


    public int guessjobType(Person pp){
        int jobType = 0;
        double[] probability;
        switch (pp.getGender()){
            case 1:
                switch ((pp.getEducationLevel())) {
                    case 0:
                        probability = new double[]{0.018525903,0.247279377,0.013645804,0.102589096,0.209383272,0.111790907,0.015157988,0.074155564,0.042772461,0.164699627};
                        break;
                    case 1:
                        probability = new double[]{0.018525903,0.247279377,0.013645804,0.102589096,0.209383272,0.111790907,0.015157988,0.074155564,0.042772461,0.164699627};
                        break;
                    case 2:
                        probability = new double[]{0.025005413,0.306936861,0.023240014,0.131612602,0.161132838,0.100584686,0.030611236,0.059327951,0.062117484,0.099430916};
                        break;
                    case 3:
                        probability = new double[]{0.008533151,0.249164089,0.020626395,0.04534431,0.067841835,0.111582213,0.048438001,0.036971695,0.207231818,0.204266493};
                        break;
                    case 4:
                        probability = new double[]{0.004152555,0.150044116,0.014863438,0.023979707,0.053767274,0.100617138,0.040040495,0.031044385,0.069906374,0.511584519};
                        break;
                    default: probability = new double[]{0.005341122,0.092856972,0.01095121,0.016743423,0.077945575,0.057859952,0.053065854,0.052110739,0.244423023,0.388702129};
                }
                break;
            case 2:
                switch ((pp.getEducationLevel())) {
                    case 0:
                        probability = new double[]{0.012755482,0.141039813,0.005312882,0.015392634,0.273558614,0.049521016,0.019501401,0.099264435,0.038971953,0.34468177};
                        break;
                    case 1:
                        probability = new double[]{0.012755482,0.141039813,0.005312882,0.015392634,0.273558614,0.049521016,0.019501401,0.099264435,0.038971953,0.34468177};
                        break;
                    case 2:
                        probability = new double[]{0.013754169,0.124100786,0.00727449,0.02178542,0.222367369,0.046812291,0.043632331,0.058141381,0.065290076,0.396841686};
                        break;
                    case 3:
                        probability = new double[]{0.005341122,0.092856972,0.01095121,0.016743423,0.077945575,0.057859952,0.053065854,0.052110739,0.244423023,0.388702129};
                        break;
                    case 4:
                        probability = new double[]{0.002848241,0.058853003,0.007342596,0.007007503,0.066280334,0.05505006,0.026564225,0.029730278,0.073777412,0.672546348};
                        break;
                    default: probability = new double[]{0.005341122,0.092856972,0.01095121,0.016743423,0.077945575,0.057859952,0.053065854,0.052110739,0.244423023,0.388702129};
                }
                break;
                default: probability = new double[]{0.005341122,0.092856972,0.01095121,0.016743423,0.077945575,0.057859952,0.053065854,0.052110739,0.244423023,0.388702129};
        }
        jobType = SiloUtil.select(probability, 1);

        return jobType;
    }
}
