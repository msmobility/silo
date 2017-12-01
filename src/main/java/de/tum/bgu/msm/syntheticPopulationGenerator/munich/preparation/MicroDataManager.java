package de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation;


import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
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


}
