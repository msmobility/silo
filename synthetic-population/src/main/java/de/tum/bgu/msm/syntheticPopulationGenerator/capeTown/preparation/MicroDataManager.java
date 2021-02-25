package de.tum.bgu.msm.syntheticPopulationGenerator.capeTown.preparation;

import de.tum.bgu.msm.data.dwelling.CapeTownDwellingTypes;
import de.tum.bgu.msm.data.dwelling.DwellingUsage;
import de.tum.bgu.msm.data.person.*;

public class MicroDataManager {

    public MicroDataManager(){}


    public int translateIncome(String incomeStr){
        int income = 0;
        if (incomeStr.equals("No income")){
            income = 0;
        } else if (incomeStr.equals("R 1 - R 4800")){
            income = 50;
        } else if (incomeStr.equals("R 4801 - R 9600")){
            income = 112;
        } else if (incomeStr.equals("R 9601 - R 19200")){
            income = 211;
        } else if (incomeStr.equals("R 19201 - R 38400")){
            income = 423;
        } else if (incomeStr.equals("R 38401 - R 76800")){
            income = 846;
        } else if (incomeStr.equals("R 76801 - R 153600")){
            income = 1693;
        } else if (incomeStr.equals("R 153601 - R 307200")){
            income = 3385;
        } else if (incomeStr.equals("R 307201 - R 614400")){
            income = 6771;
        } else if (incomeStr.equals("R 614401- R 1228800")){
            income = 13542;
        } else if (incomeStr.equals("R 1228801 - R 2457600")){
            income = 27085;
        } else if (incomeStr.equals("R2457601 or more")){
            income = 76608;
        } else if (incomeStr.equals("Unspecified")){
            income = 50;
        }
        return income;
    }


    public RaceCapeTown translateRace(String raceStr){
        RaceCapeTown race = RaceCapeTown.BLACK; //default value
        //used for category: "Black African"
        if (raceStr.equals("Coloured")){
            race = RaceCapeTown.COLOURED;
        } else if (raceStr.equals("Indian or Asian")){
            race = RaceCapeTown.OTHER;
        } else if (raceStr.equals("Other")){
            race = RaceCapeTown.OTHER;
        } else if (raceStr.equals("White")){
            race = RaceCapeTown.WHITE;
        }
        return race;
    }


    public Occupation translateOccupation(String occupationStr, String schoolStr){
        Occupation occupation = Occupation.EMPLOYED; //default value
        //used for category: "Employed"
        if (schoolStr.equals("Yes")){
            occupation = Occupation.STUDENT;
        } else {
            if (occupationStr.equals("Age less than 15 years")) {
                occupation = Occupation.TODDLER;
            } else if (occupationStr.equals("Unemployed")) {
                occupation = Occupation.UNEMPLOYED;
            } else if (occupationStr.equals("Discouraged work-seeker")) {
                occupation = Occupation.RETIREE;
            } else if (occupationStr.equals("NoData")) {
                occupation = Occupation.UNEMPLOYED;
            }
        }
        return occupation;
    }


    public Gender translateGender(String genderStr){
        Gender gender = Gender.FEMALE; //default value
        //used for category: "Female"
        if (genderStr.equals("Male")){
            gender = Gender.MALE;
        }
        return gender;
    }


    public PersonRole translateRole(int roleStr, int age){
        PersonRole role = PersonRole.SINGLE; //default value
        //used for categories: "Never married","Separated", "Widower/widow", "Divorced"
        if (age < 15){
            return PersonRole.CHILD;
        } else {
            if (roleStr == 2) {
                role = PersonRole.MARRIED;
            }
        }
        return role;
    }

    public Nationality translateNationality(String nationalityStr){
        Nationality nationality = Nationality.GERMAN; //default value
        //used for category: "Yes" (to citizenship)
        if (nationalityStr.equals("No")){
            nationality = Nationality.OTHER;
        } else if (nationalityStr.equals("Unspecified")){
            nationality = Nationality.OTHER;
        }
        return nationality;
    }


    public CapeTownDwellingTypes.DwellingTypeCapeTown translateDwellingType(String ddTypeStr){
        CapeTownDwellingTypes.DwellingTypeCapeTown dwellingType = null; //default value
        //used for categories: House or brick/concrete block structure on a separate stand or yard,
        //Semi-detached house, Town house (semi-detached house in complex)
        if (ddTypeStr.equals("House or brick/concrete block structure on a separate stand or yard")) {
            dwellingType = CapeTownDwellingTypes.DwellingTypeCapeTown.FORMAL;
        } else if (ddTypeStr.equals("Traditional dwelling/hut/structure made of traditional materials")){
            dwellingType = CapeTownDwellingTypes.DwellingTypeCapeTown.INFORMAL;
        } else if (ddTypeStr.equals("Flat or apartment in a block of flats")){
            dwellingType = CapeTownDwellingTypes.DwellingTypeCapeTown.MULTIFAMILY;
        } else if (ddTypeStr.equals("Cluster house in complex")){
            dwellingType = CapeTownDwellingTypes.DwellingTypeCapeTown.MULTIFAMILY;
        } else if (ddTypeStr.equals("Town house (semi-detached house in complex)")){
            dwellingType = CapeTownDwellingTypes.DwellingTypeCapeTown.SEMIDETACHED;
        } else if (ddTypeStr.equals("Semi-detached house")){
            dwellingType = CapeTownDwellingTypes.DwellingTypeCapeTown.SEMIDETACHED;
        } else if (ddTypeStr.equals("House/flat/room in back yard")){
            dwellingType = CapeTownDwellingTypes.DwellingTypeCapeTown.FORMAL;
        } else if (ddTypeStr.equals("Informal dwelling/shack in back yard")){
            dwellingType = CapeTownDwellingTypes.DwellingTypeCapeTown.BACKYARD_INFORMAL;
        } else if (ddTypeStr.equals("Room/flatlet on a property or a larger dwelling/servants'quarters/granny flat")){
            dwellingType = CapeTownDwellingTypes.DwellingTypeCapeTown.INFORMAL;
        } else if (ddTypeStr.equals("Informal dwelling/shack NOT in back yard e.g. in an informal/squatter settlement or on farm")){
            dwellingType = CapeTownDwellingTypes.DwellingTypeCapeTown.INFORMAL;
        } else if (ddTypeStr.equals("Caravan/tent")){
            dwellingType = CapeTownDwellingTypes.DwellingTypeCapeTown.INFORMAL;
        } else if (ddTypeStr.equals("Other")){
            dwellingType = CapeTownDwellingTypes.DwellingTypeCapeTown.FORMAL;
        } else if (ddTypeStr.equals("Unspecified")){
            dwellingType = CapeTownDwellingTypes.DwellingTypeCapeTown.FORMAL;
        }
        return dwellingType;
    }


    public int guessQuality(String waterPiped, String toilet, int numberofQualityLevels){
        int quality = 4;
        if (!waterPiped.equals("Piped (tap) water inside the dwelling")){// if there is no piped water inside dwelling and/or yard
            if (!waterPiped.equals("Piped (tap) water inside the yard")) {
                quality--;
            }
        }
        if (waterPiped.equals("No access to piped (tap) water")){ //if there is no access to piped water, reduce quality one level more
            quality--;
        }
        if (!toilet.equals("Flush toilet (connected to sewerage system)")){//if the toilet is not a flushed toilet
            if (!toilet.equals("Flush toilet (with septic tank)")) {
                quality--;
            }
        }
        if (toilet.equals("Bucket latrine")){// if the toilet is a bucket, reduce quality one level more
            quality--;
        }
        // ensure that quality never excess the number of quality levels
        return quality = Math.max(quality, 1);
    }

    public DwellingUsage translateDwellingUsage(String tenure){
        DwellingUsage dwellingUsage = DwellingUsage.OWNED; //default value
        //used for categories: "Owned and fully paid off", "Owned but not yet paid off"
        if (tenure.equals("Rented")){
            dwellingUsage = DwellingUsage.RENTED;
        } else if (tenure.equals("Occupied rent-free")){
            dwellingUsage = DwellingUsage.RENTED;
        } else if (tenure.equals("Other")){
            dwellingUsage = DwellingUsage.RENTED;
        }
        return dwellingUsage;
    }

    public int translateCars(String carStr){
        int cars = 0;
        if (carStr.equals("Yes")){
            cars = 1;
        }
        return cars;
    }

    public String translateRelation(String strRelation){
        String relation = "noChild";

        if(strRelation.equals("Son/daughter")){
            relation = "child";
        } else if (strRelation.equals("Adopted son/daughter")){
            relation = "child";
        } else if (strRelation.equals("Stepchild")){
            relation = "child";
        } else if (strRelation.equals("Grand/greatgrand child")){
            relation = "child";
        } else if (strRelation.equals("Son/Daughter-in-law")){
            relation = "child";
        }
        return relation;
    }


    public int translateGenderToInt(String strRelation){
        int genderInt = 1;

        if(strRelation.equals("Female")){
            genderInt = 2;
        }
        return genderInt;
    }

    public int guessDwellingPrice(int hhIncome){
        int price = 0;

        if (hhIncome > 25000){
            price = Math.round(hhIncome / 10);
        } else if (hhIncome > 10000) {
            float housingCosts = 25 - hhIncome * 15 / 20000;
            price = Math.round(hhIncome * housingCosts / 100);
        } else {
            float housingCosts = 50 - hhIncome * 25 / 10000;
            price = Math.round(hhIncome * housingCosts / 100);
        }
        return Math.max(price, 100);
    }
}