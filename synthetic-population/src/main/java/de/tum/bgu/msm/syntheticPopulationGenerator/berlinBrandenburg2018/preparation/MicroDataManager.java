package de.tum.bgu.msm.syntheticPopulationGenerator.berlinBrandenburg2018.preparation;

import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes;
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

        String[] attributesPerson = {"employmentStatus", "age", "gender", "maritalStatus", "partnerInHh",
                "relationToHhHead", "personStatus", "secondaryEmploymentStatus", "partTimeEmployment",
                "occupation", "positionInOccupation", "employeesInFirm", "fullTimeOrPartTime", "weeklyWorkingHours",
                "workSector", "saturdayWorkInLastFourWeeks", "sundayWorkInLastFourWeeks", "sixPmToElevenPmWorkInLastFourWeeks",
                "elevenPmToSixAmWorkInLastFourWeeks", "workFromHomeInLastFourWeeks", "flexibleOvertime",
                "ifWorkplaceAndResidenceInSameMunicipality", "workingHoursPerWeekInSecondaryEmployment", "ifSchoolAttendedLastYear",
                "typeOfSchoolAttended", "highestSchoolLeavingQualification", "highestProfessionalQualification",
                "yearOfHighestProfessionalQualification", "nationality", "netIncomeLastMonth", "netIncomeAverage", "disabilityStatus",
                "personRoleInFamily", "disabilityGrade", "sharedAccommodationType", "statusOfHeadInSharedAccommodation",
                "statusOfAccommodation", "flexibleWorkingPossibilityForCare", "internetUsage"};

        String[] attributesHousehold = {"hhSize", "numberOfFamiliesInHh", "statusOfResidence", "privateQuarter",
                "statusOfResidenceEurostat", "presenceOfOtherHousingInGermany", "childrenInHh", "netHhIncome", "nonFamilyMembersInHh",
                "hhFamilyFormat", "netHhIncomeLastMonth", "defaultGrossingFactor", "grossingFactorForDisabled",
                "internetAccess", "internetSpeed"};

        String[] attributesDwelling = {"ddType", "ddBuildingSize", "ddOwnership", "ddArea", "ddConstructionYear",
                "ddMoveInYear", "ddHeatingEnergyType", "ddGrossRent", "ddColdNetRent", "ddBuildingSizeClass", "ddPersonsInApartment",
                "ddColdGrossRentPerSqm", "ddColdNetRentPerSqm", "ddRentBurdenRatio", "ddUse", "ddRentalCharges",
                "ddNumberOfHeatingTypes", "ddSituation", "ddDistrictHeating", "ddBlockCentralHeating", "ddFloorHeating",
                "ddStoveHeating", "ddHeatingTypeUnknown", "ddTypeOfResidentialBuilding", "ddNumberOfRoomsInApartment",
                "ddBuildingOwnership", "ddSeamlessAccess", "ddSufficientFrontDoorPassage", "ddSufficientCorridorPassage",
                "ddInaccessible", "ddEvenFloors", "ddWideCorridors", "ddSpaceNearKitchenette", "ddSpaceInBathroom", "ddShowerAccess",
                "ddOwnerHomeLoans", "ddMunicipalitySize"};

        attributesMicroData.put("person", attributesPerson);
        attributesMicroData.put("household", attributesHousehold);
        attributesMicroData.put("dwelling", attributesDwelling);

        return attributesMicroData;
    }

    public Map<String, Map<String, Integer>> attributesPersonMicroData(){

        Map<String, Map<String, Integer>> attributesIPU = new HashMap<>();
        // IPU attributes
            Map<String, Integer> age = new HashMap<>();
                age.put("initial", 54);
                age.put("end", 56);
                attributesIPU.put("age", age);
            Map<String, Integer> gender = new HashMap<>();
                gender.put("initial", 58);
                gender.put("end", 60);
                attributesIPU.put("gender", gender);
            Map<String, Integer> occupation = new HashMap<>();
                occupation.put("initial", 147);
                occupation.put("end", 151);
                attributesIPU.put("occupation", occupation);
            Map<String, Integer> nationality = new HashMap<>();
                nationality.put("initial", 480);
                nationality.put("end", 482);
                attributesIPU.put("nationality", nationality);

        // Additional attributes
            Map<String, Integer> employmentStatus = new HashMap<>();
                employmentStatus.put("initial", 32);
                employmentStatus.put("end", 34);
                attributesIPU.put("employmentStatus", employmentStatus);
            Map<String, Integer> maritalStatus = new HashMap<>();
                maritalStatus.put("initial", 64);
                maritalStatus.put("end", 66);
                attributesIPU.put("maritalStatus", maritalStatus);
            Map<String, Integer> partnerInHh = new HashMap<>();
                partnerInHh.put("initial", 66);
                partnerInHh.put("end", 68);
                attributesIPU.put("partnerInHh", partnerInHh);
            Map<String, Integer> relationToHhHead = new HashMap<>();
                relationToHhHead.put("initial", 82);
                relationToHhHead.put("end", 84);
                attributesIPU.put("relationToHhHead", relationToHhHead);
            Map<String, Integer> personStatus = new HashMap<>();
                personStatus.put("initial", 88);
                personStatus.put("end", 90);
                attributesIPU.put("personStatus", personStatus);
            Map<String, Integer> secondaryEmploymentStatus = new HashMap<>();
                secondaryEmploymentStatus.put("initial", 90);
                secondaryEmploymentStatus.put("end", 92);
                attributesIPU.put("secondaryEmploymentStatus", secondaryEmploymentStatus);
            Map<String, Integer> partTimeEmployment = new HashMap<>();
                partTimeEmployment.put("initial", 100);
                partTimeEmployment.put("end", 102);
                attributesIPU.put("partTimeEmployment", partTimeEmployment);
            Map<String, Integer> positionInOccupation = new HashMap<>();
                positionInOccupation.put("initial", 173);
                positionInOccupation.put("end", 175);
                attributesIPU.put("positionInOccupation", positionInOccupation);
            Map<String, Integer> employeesInFirm = new HashMap<>();
                employeesInFirm.put("initial", 183);
                employeesInFirm.put("end", 185);
                attributesIPU.put("employeesInFirm", employeesInFirm);
            Map<String, Integer> fullTimeOrPartTime = new HashMap<>();
                fullTimeOrPartTime.put("initial", 199);
                fullTimeOrPartTime.put("end", 201);
                attributesIPU.put("fullTimeOrPartTime", fullTimeOrPartTime);
            Map<String, Integer> weeklyWorkingHours = new HashMap<>();
                weeklyWorkingHours.put("initial", 203);
                weeklyWorkingHours.put("end", 205);
                attributesIPU.put("weeklyWorkingHours", weeklyWorkingHours);
            Map<String, Integer> workSector = new HashMap<>();
                workSector.put("initial", 213);
                workSector.put("end", 216);
                attributesIPU.put("workSector", workSector);
            Map<String, Integer> saturdayWorkInLastFourWeeks = new HashMap<>();
                saturdayWorkInLastFourWeeks.put("initial", 228);
                saturdayWorkInLastFourWeeks.put("end", 230);
                attributesIPU.put("saturdayWorkInLastFourWeeks", saturdayWorkInLastFourWeeks);
            Map<String, Integer> sundayWorkInLastFourWeeks = new HashMap<>();
                sundayWorkInLastFourWeeks.put("initial", 230);
                sundayWorkInLastFourWeeks.put("end", 232);
                attributesIPU.put("sundayWorkInLastFourWeeks", sundayWorkInLastFourWeeks);
            Map<String, Integer> sixPmToElevenPmWorkInLastFourWeeks = new HashMap<>();
                sixPmToElevenPmWorkInLastFourWeeks.put("initial", 232);
                sixPmToElevenPmWorkInLastFourWeeks.put("end", 234);
                attributesIPU.put("sixPmToElevenPmWorkInLastFourWeeks", sixPmToElevenPmWorkInLastFourWeeks);
            Map<String, Integer> elevenPmToSixAmWorkInLastFourWeeks = new HashMap<>();
                elevenPmToSixAmWorkInLastFourWeeks.put("initial", 234);
                elevenPmToSixAmWorkInLastFourWeeks.put("end", 236);
                attributesIPU.put("elevenPmToSixAmWorkInLastFourWeeks", elevenPmToSixAmWorkInLastFourWeeks);
            Map<String, Integer> workFromHomeInLastFourWeeks = new HashMap<>();
                workFromHomeInLastFourWeeks.put("initial", 254);
                workFromHomeInLastFourWeeks.put("end", 256);
                attributesIPU.put("workFromHomeInLastFourWeeks", workFromHomeInLastFourWeeks);
            Map<String, Integer> flexibleOvertime = new HashMap<>();
                flexibleOvertime.put("initial", 246);
                flexibleOvertime.put("end", 248);
                attributesIPU.put("flexibleOvertime", flexibleOvertime);
            Map<String, Integer> ifWorkplaceAndResidenceInSameMunicipality = new HashMap<>();
                ifWorkplaceAndResidenceInSameMunicipality.put("initial", 277);
                ifWorkplaceAndResidenceInSameMunicipality.put("end", 279);
                attributesIPU.put("ifWorkplaceAndResidenceInSameMunicipality", ifWorkplaceAndResidenceInSameMunicipality);
            Map<String, Integer> workingHoursPerWeekInSecondaryEmployment = new HashMap<>();
                workingHoursPerWeekInSecondaryEmployment.put("initial", 297);
                workingHoursPerWeekInSecondaryEmployment.put("end", 299);
                attributesIPU.put("workingHoursPerWeekInSecondaryEmployment", workingHoursPerWeekInSecondaryEmployment);
            Map<String, Integer> ifSchoolAttendedLastYear = new HashMap<>();
                ifSchoolAttendedLastYear.put("initial", 375);
                ifSchoolAttendedLastYear.put("end", 377);
                attributesIPU.put("ifSchoolAttendedLastYear", ifSchoolAttendedLastYear);
            Map<String, Integer> typeOfSchoolAttended = new HashMap<>();
                typeOfSchoolAttended.put("initial", 377);
                typeOfSchoolAttended.put("end", 379);
                attributesIPU.put("typeOfSchoolAttended", typeOfSchoolAttended);
            Map<String, Integer> highestSchoolLeavingQualification = new HashMap<>();
                highestSchoolLeavingQualification.put("initial", 389);
                highestSchoolLeavingQualification.put("end", 391);
                attributesIPU.put("highestSchoolLeavingQualification", highestSchoolLeavingQualification);
            Map<String, Integer> highestProfessionalQualification = new HashMap<>();
                highestProfessionalQualification.put("initial", 393);
                highestProfessionalQualification.put("end", 395);
                attributesIPU.put("highestProfessionalQualification", highestProfessionalQualification);
            Map<String, Integer> yearOfHighestProfessionalQualification = new HashMap<>();
                yearOfHighestProfessionalQualification.put("initial", 403);
                yearOfHighestProfessionalQualification.put("end", 407);
                attributesIPU.put("yearOfHighestProfessionalQualification", yearOfHighestProfessionalQualification);
            Map<String, Integer> netIncomeLastMonth = new HashMap<>();
                netIncomeLastMonth.put("initial", 581);
                netIncomeLastMonth.put("end", 583);
                attributesIPU.put("netIncomeLastMonth", netIncomeLastMonth);
            Map<String, Integer> netIncomeAverage = new HashMap<>();
                netIncomeAverage.put("initial", 594);
                netIncomeAverage.put("end", 596);
                attributesIPU.put("netIncomeAverage", netIncomeAverage);
            Map<String, Integer> disabilityStatus = new HashMap<>();
                disabilityStatus.put("initial", 948);
                disabilityStatus.put("end", 950);
                attributesIPU.put("disabilityStatus", disabilityStatus);
            Map<String, Integer> personRoleInFamily = new HashMap<>();
                personRoleInFamily.put("initial", 780);
                personRoleInFamily.put("end", 782);
                attributesIPU.put("personRoleInFamily", personRoleInFamily);
            Map<String, Integer> disabilityGrade = new HashMap<>();
                disabilityGrade.put("initial", 950);
                disabilityGrade.put("end", 952);
                attributesIPU.put("disabilityGrade", disabilityGrade);
            Map<String, Integer> sharedAccommodationType = new HashMap<>();
                sharedAccommodationType.put("initial", 952);
                sharedAccommodationType.put("end", 954);
                attributesIPU.put("sharedAccommodationType", sharedAccommodationType);
            Map<String, Integer> statusOfHeadInSharedAccommodation = new HashMap<>();
                statusOfHeadInSharedAccommodation.put("initial", 954);
                statusOfHeadInSharedAccommodation.put("end", 956);
                attributesIPU.put("statusOfHeadInSharedAccommodation", statusOfHeadInSharedAccommodation);
            Map<String, Integer> statusOfAccommodation = new HashMap<>();
                statusOfAccommodation.put("initial", 994);
                statusOfAccommodation.put("end", 996);
                attributesIPU.put("statusOfAccommodation", statusOfAccommodation);
            Map<String, Integer> flexibleWorkingPossibilityForCare = new HashMap<>();
                flexibleWorkingPossibilityForCare.put("initial", 1008);
                flexibleWorkingPossibilityForCare.put("end", 1010);
                attributesIPU.put("flexibleWorkingPossibilityForCare", flexibleWorkingPossibilityForCare);
            Map<String, Integer> internetUsage = new HashMap<>();
                internetUsage.put("initial", 1032);
                internetUsage.put("end", 1034);
                attributesIPU.put("internetUsage", internetUsage);

            return attributesIPU;
    }

    public Map<String, Map<String, Integer>> attributesHouseholdMicroData(){

        Map<String, Map<String, Integer>> attributesIPU = new HashMap<>();
        // IPU attributes
            Map<String, Integer> hhSize = new HashMap<>();
                hhSize.put("initial", 26);
                hhSize.put("end", 28);
                attributesIPU.put("hhSize", hhSize);

        // Additional attributes
            Map<String, Integer> numberOfFamiliesInHh = new HashMap<>();
                numberOfFamiliesInHh.put("initial", 28);
                numberOfFamiliesInHh.put("end", 30);
                attributesIPU.put("numberOfFamiliesInHh", numberOfFamiliesInHh);
            Map<String, Integer> statusOfResidence = new HashMap<>();
                statusOfResidence.put("initial", 34);
                statusOfResidence.put("end", 36);
                attributesIPU.put("statusOfResidence", statusOfResidence);
            Map<String, Integer> privateQuarter = new HashMap<>();
                privateQuarter.put("initial", 36);
                privateQuarter.put("end", 38);
                attributesIPU.put("privateQuarter", privateQuarter);
            Map<String, Integer> statusOfResidenceEurostat = new HashMap<>();
                statusOfResidenceEurostat.put("initial", 38);
                statusOfResidenceEurostat.put("end", 40);
                attributesIPU.put("statusOfResidenceEurostat", statusOfResidenceEurostat);
            Map<String, Integer> presenceOfOtherHousingInGermany = new HashMap<>();
                presenceOfOtherHousingInGermany.put("initial", 92);
                presenceOfOtherHousingInGermany.put("end", 94);
                attributesIPU.put("presenceOfOtherHousingInGermany", presenceOfOtherHousingInGermany);
            Map<String, Integer> childrenInHh = new HashMap<>();
                childrenInHh.put("initial", 712);
                childrenInHh.put("end", 714);
                attributesIPU.put("childrenInHh", childrenInHh);
            Map<String, Integer> netHhIncome = new HashMap<>();
                netHhIncome.put("initial", 724);
                netHhIncome.put("end", 726);
                attributesIPU.put("netHhIncome", netHhIncome);
            Map<String, Integer> nonFamilyMembersInHh = new HashMap<>();
                nonFamilyMembersInHh.put("initial", 745);
                nonFamilyMembersInHh.put("end", 747);
                attributesIPU.put("nonFamilyMembersInHh", nonFamilyMembersInHh);
            Map<String, Integer> hhFamilyFormat = new HashMap<>();
                hhFamilyFormat.put("initial", 755);
                hhFamilyFormat.put("end", 757);
                attributesIPU.put("hhFamilyFormat", hhFamilyFormat);
            Map<String, Integer> netHhIncomeLastMonth = new HashMap<>();
                netHhIncomeLastMonth.put("initial", 763);
                netHhIncomeLastMonth.put("end", 765);
                attributesIPU.put("netHhIncomeLastMonth", netHhIncomeLastMonth);
            Map<String, Integer> defaultGrossingFactor = new HashMap<>();
                defaultGrossingFactor.put("initial", 793);
                defaultGrossingFactor.put("end", 802);
                attributesIPU.put("defaultGrossingFactor", defaultGrossingFactor);
            Map<String, Integer> grossingFactorForDisabled = new HashMap<>();
                grossingFactorForDisabled.put("initial", 811);
                grossingFactorForDisabled.put("end", 820);
                attributesIPU.put("grossingFactorForDisabled", grossingFactorForDisabled);
            Map<String, Integer> internetAccess = new HashMap<>();
                internetAccess.put("initial", 1028);
                internetAccess.put("end", 1030);
                attributesIPU.put("internetAccess", internetAccess);
            Map<String, Integer> internetSpeed = new HashMap<>();
                internetSpeed.put("initial", 1030);
                internetSpeed.put("end", 1032);
                attributesIPU.put("internetSpeed", internetSpeed);

        return attributesIPU;
    }

    public Map<String, Map<String, Integer>> attributesDwellingMicroData(){

        Map<String, Map<String, Integer>> attributesIPU = new HashMap<>();
        // IPU attributes
            Map<String, Integer> ddArea = new HashMap<>();
                ddArea.put("initial", 616);
                ddArea.put("end", 619);
                attributesIPU.put("ddArea", ddArea);
            Map<String, Integer> ddUse = new HashMap<>();
                ddUse.put("initial", 700);
                ddUse.put("end", 702);
                attributesIPU.put("ddUse", ddUse);
            // ddOwnership used in place of ddUse, as ddUse - in Munich SILO implementation - actually refers to ownership
            Map<String, Integer> ddOwnership = new HashMap<>();
                ddOwnership.put("initial", 614);
                ddOwnership.put("end", 616);
                attributesIPU.put("ddOwnership", ddOwnership);
            Map<String, Integer> ddConstructionYear = new HashMap<>();
                ddConstructionYear.put("initial", 619);
                ddConstructionYear.put("end", 621);
                attributesIPU.put("ddConstructionYear", ddConstructionYear);

        // Additional attributes
            Map<String, Integer> ddType = new HashMap<>();
                ddType.put("initial", 610);
                ddType.put("end", 612);
                attributesIPU.put("ddType", ddType);
            Map<String, Integer> ddBuildingSize= new HashMap<>();
                ddBuildingSize.put("initial", 612);
                ddBuildingSize.put("end", 614);
                attributesIPU.put("ddBuildingSize", ddBuildingSize);
            Map<String, Integer> ddMoveInYear = new HashMap<>();
                ddMoveInYear.put("initial", 621);
                ddMoveInYear.put("end", 625);
                attributesIPU.put("ddMoveInYear", ddMoveInYear);
            Map<String, Integer> ddHeatingEnergyType = new HashMap<>();
                ddHeatingEnergyType.put("initial", 625);
                ddHeatingEnergyType.put("end", 627);
                attributesIPU.put("ddHeatingEnergyType", ddHeatingEnergyType);
            Map<String, Integer> ddGrossRent = new HashMap<>();
                ddGrossRent.put("initial", 627);
                ddGrossRent.put("end", 631);
                attributesIPU.put("ddGrossRent", ddGrossRent);
            Map<String, Integer> ddColdNetRent = new HashMap<>();
                ddColdNetRent.put("initial", 639);
                ddColdNetRent.put("end", 643);
                attributesIPU.put("ddColdNetRent", ddColdNetRent);
            Map<String, Integer> ddBuildingSizeClass = new HashMap<>();
                ddBuildingSizeClass.put("initial", 1138);
                ddBuildingSizeClass.put("end", 1140);
                attributesIPU.put("ddBuildingSizeClass", ddBuildingSizeClass);
            Map<String, Integer> ddPersonsInApartment = new HashMap<>();
                ddPersonsInApartment.put("initial", 694);
                ddPersonsInApartment.put("end", 696);
                attributesIPU.put("ddPersonsInApartment", ddPersonsInApartment);
            Map<String, Integer> ddColdGrossRentPerSqm = new HashMap<>();
                ddColdGrossRentPerSqm.put("initial", 696);
                ddColdGrossRentPerSqm.put("end", 698);
                attributesIPU.put("ddColdGrossRentPerSqm", ddColdGrossRentPerSqm);
            Map<String, Integer> ddColdNetRentPerSqm = new HashMap<>();
                ddColdNetRentPerSqm.put("initial", 829);
                ddColdNetRentPerSqm.put("end", 831);
                attributesIPU.put("ddColdNetRentPerSqm", ddColdNetRentPerSqm);
            Map<String, Integer> ddRentBurdenRatio = new HashMap<>();
                ddRentBurdenRatio.put("initial", 698);
                ddRentBurdenRatio.put("end", 700);
                attributesIPU.put("ddRentBurdenRatio", ddRentBurdenRatio);
            Map<String, Integer> ddRentalCharges = new HashMap<>();
                ddRentalCharges.put("initial", 708);
                ddRentalCharges.put("end", 710);
                attributesIPU.put("ddRentalCharges", ddRentalCharges);
            Map<String, Integer> ddNumberOfHeatingTypes = new HashMap<>();
                ddNumberOfHeatingTypes.put("initial", 714);
                ddNumberOfHeatingTypes.put("end", 716);
                attributesIPU.put("ddNumberOfHeatingTypes", ddNumberOfHeatingTypes);
            Map<String, Integer> ddSituation = new HashMap<>();
                ddSituation.put("initial", 893);
                ddSituation.put("end", 895);
                attributesIPU.put("ddSituation", ddSituation);
            Map<String, Integer> ddDistrictHeating = new HashMap<>();
                ddDistrictHeating.put("initial", 913);
                ddDistrictHeating.put("end", 915);
                attributesIPU.put("ddDistrictHeating", ddDistrictHeating);
            Map<String, Integer> ddBlockCentralHeating = new HashMap<>();
                ddBlockCentralHeating.put("initial", 915);
                ddBlockCentralHeating.put("end", 917);
                attributesIPU.put("ddBlockCentralHeating", ddBlockCentralHeating);
            Map<String, Integer> ddFloorHeating = new HashMap<>();
                ddFloorHeating.put("initial", 917);
                ddFloorHeating.put("end", 919);
                attributesIPU.put("ddFloorHeating", ddFloorHeating);
            Map<String, Integer> ddStoveHeating = new HashMap<>();
                ddStoveHeating.put("initial", 920);
                ddStoveHeating.put("end", 921);
                attributesIPU.put("ddStoveHeating", ddStoveHeating);
            Map<String, Integer> ddHeatingTypeUnknown = new HashMap<>();
                ddHeatingTypeUnknown.put("initial", 921);
                ddHeatingTypeUnknown.put("end", 923);
                attributesIPU.put("ddHeatingTypeUnknown", ddHeatingTypeUnknown);
            Map<String, Integer> ddTypeOfResidentialBuilding = new HashMap<>();
                ddTypeOfResidentialBuilding.put("initial", 1034);
                ddTypeOfResidentialBuilding.put("end", 1036);
                attributesIPU.put("ddTypeOfResidentialBuilding", ddTypeOfResidentialBuilding);
            Map<String, Integer> ddNumberOfRoomsInApartment = new HashMap<>();
                ddNumberOfRoomsInApartment.put("initial", 1036);
                ddNumberOfRoomsInApartment.put("end", 1038);
                attributesIPU.put("ddNumberOfRoomsInApartment", ddNumberOfRoomsInApartment);
            Map<String, Integer> ddBuildingOwnership = new HashMap<>();
                ddBuildingOwnership.put("initial", 1038);
                ddBuildingOwnership.put("end", 1040);
                attributesIPU.put("ddBuildingOwnership", ddBuildingOwnership);
            Map<String, Integer> ddSeamlessAccess = new HashMap<>();
                ddSeamlessAccess.put("initial", 1040);
                ddSeamlessAccess.put("end", 1042);
                attributesIPU.put("ddSeamlessAccess", ddSeamlessAccess);
            Map<String, Integer> ddSufficientFrontDoorPassage = new HashMap<>();
                ddSufficientFrontDoorPassage.put("initial", 1042);
                ddSufficientFrontDoorPassage.put("end", 1044);
                attributesIPU.put("ddSufficientFrontDoorPassage", ddSufficientFrontDoorPassage);
            Map<String, Integer> ddSufficientCorridorPassage = new HashMap<>();
                ddSufficientCorridorPassage.put("initial", 1044);
                ddSufficientCorridorPassage.put("end", 1046);
                attributesIPU.put("ddSufficientCorridorPassage", ddSufficientCorridorPassage);
            Map<String, Integer> ddInaccessible = new HashMap<>();
                ddInaccessible.put("initial", 1046);
                ddInaccessible.put("end", 1048);
                attributesIPU.put("ddInaccessible", ddInaccessible);
            Map<String, Integer> ddEvenFloors = new HashMap<>();
                ddEvenFloors.put("initial", 1050);
                ddEvenFloors.put("end", 1052);
                attributesIPU.put("ddEvenFloors", ddEvenFloors);
            Map<String, Integer> ddWideCorridors = new HashMap<>();
                ddWideCorridors.put("initial", 1058);
                ddWideCorridors.put("end", 1060);
                attributesIPU.put("ddWideCorridors", ddWideCorridors);
            Map<String, Integer> ddSpaceNearKitchenette = new HashMap<>();
                ddSpaceNearKitchenette.put("initial", 1060);
                ddSpaceNearKitchenette.put("end", 1062);
                attributesIPU.put("ddSpaceNearKitchenette", ddSpaceNearKitchenette);
            Map<String, Integer> ddSpaceInBathroom = new HashMap<>();
                ddSpaceInBathroom.put("initial", 1062);
                ddSpaceInBathroom.put("end", 1064);
                attributesIPU.put("ddSpaceInBathroom", ddSpaceInBathroom);
            Map<String, Integer> ddShowerAccess = new HashMap<>();
                ddShowerAccess.put("initial", 1064);
                ddShowerAccess.put("end", 1066);
                attributesIPU.put("ddShowerAccess", ddShowerAccess);
            Map<String, Integer> ddOwnerHomeLoans = new HashMap<>();
                ddOwnerHomeLoans.put("initial", 1070);
                ddOwnerHomeLoans.put("end", 1072);
                attributesIPU.put("ddOwnerHomeLoans", ddOwnerHomeLoans);
            Map<String, Integer> ddMunicipalitySize = new HashMap<>();
                ddMunicipalitySize.put("initial", 682);
                ddMunicipalitySize.put("end", 683);
                attributesIPU.put("ddMunicipalitySize", ddMunicipalitySize);

        return attributesIPU;
    }

    public Map<String, Map<String, Integer>> exceptionsMicroData(){

        Map<String, Map<String, Integer>> exceptionsMicroData = new HashMap<>();
            Map<String, Integer> LivingInQuarter = new HashMap<>();
                LivingInQuarter.put("initial", 36);     // Person lives in shared accommodation
                LivingInQuarter.put("end", 38);
                LivingInQuarter.put("exceptionIf", 2);
                exceptionsMicroData.put("quarter", LivingInQuarter);
            Map<String, Integer> noIncome = new HashMap<>();    // Person did not respond to the "average" income question in census
                noIncome.put("initial", 594);
                noIncome.put("end", 596);
                noIncome.put("exceptionIf", 99);
                exceptionsMicroData.put("noIncome", noIncome);
            Map<String, Integer> movingOutInFiveYears = new HashMap<>();    // Did you leave the country in the last five years? Use EF383 from key file.
                movingOutInFiveYears.put("initial", 505);
                movingOutInFiveYears.put("end", 507);
                movingOutInFiveYears.put("exceptionIf", 1);
                exceptionsMicroData.put("movedOut", movingOutInFiveYears);
            Map<String, Integer> noBuildingSize = new HashMap<>();      // Person did not respond to the "building size (number of apartments)" question in census
                noBuildingSize.put("initial", 612);
                noBuildingSize.put("end", 614);
                noBuildingSize.put("exceptionIf", -8);
                exceptionsMicroData.put("noSize", noBuildingSize);
            Map<String, Integer> noDwellingUsage = new HashMap<>();     // Person did not respond to the "dwelling use (ownership)" question in census
                noDwellingUsage.put("initial", 614);
                noDwellingUsage.put("end", 616);
                noDwellingUsage.put("exceptionIf", -8);
                exceptionsMicroData.put("noUsage", noDwellingUsage);
            Map<String, Integer> noBuildingYear = new HashMap<>();    // Person did not respond to the "building year" question in census
                noBuildingYear.put("initial", 619);
                noBuildingYear.put("end", 621);
                noBuildingYear.put("exceptionIf", 9);
                exceptionsMicroData.put("noYear", noBuildingYear);
            Map<String, Integer> SchleswigHolstein = new HashMap<>();   // Exceptions for persons living outside Berlin and Brandenburg
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
            Map<String, Integer> Bayern = new HashMap<>();
                Bayern.put("initial", 0);
                Bayern.put("end", 2);
                Bayern.put("exceptionIf", 9);
                exceptionsMicroData.put("out9", Bayern);
            Map<String, Integer> Saarland = new HashMap<>();
                Saarland.put("initial", 0);
                Saarland.put("end", 2);
                Saarland.put("exceptionIf", 10);
                exceptionsMicroData.put("out10", Saarland);
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

    public int translateIncome(int valueMicroData) {
        int valueCode = 0;
        double low = 0;
        double high = 1;
        double income = 0;
        switch (valueMicroData) {
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

        double cummulativeProb = SiloUtil.getRandomNumberAsDouble() * (high - low) + low;
        try {
            income = PropertiesSynPop.get().main.incomeGammaDistribution.inverseCumulativeProbability(cummulativeProb);
            valueCode = (int) income;
        } catch (MathException e) {
            e.printStackTrace();
        }

        return valueCode;
    }

    public Race translateRace(int nationality) {     // Potentially redundant for the Berlin-Brandenburg implementation
        Race race = Race.white;
        if (nationality == 8) {
            double raceRandom = SiloUtil.getRandomNumberAsDouble();
            if (raceRandom < 0.33) {
                race = Race.black;
            } else if ((raceRandom >= 0.33) && (raceRandom < 0.67)) {
                race = Race.hispanic;
            } else {
                race = Race.other;
            }
        }

        return race;
    }

    public Nationality translateNationality (int nationality){
        Nationality nationality1 = Nationality.GERMAN;
        if (nationality == 8) {
            nationality1 = Nationality.OTHER;
        }

        return nationality1;
    }

    public PersonRole translatePersonRole (int role){
        PersonRole personRole = PersonRole.SINGLE;
        if (role == 2) {
            personRole = PersonRole.MARRIED;
        } else if (role == 3) {
            personRole = PersonRole.CHILD;
        }

        return personRole;
    }

    public static boolean obtainLicense(Gender gender, int age) {
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

    public int guessDwellingQuality(int districtHeatingType, int blockCentralHeatingType, int heatingEnergyType, int numberOfHeatingTypes, int yearBuilt) {
        // guess quality of dwelling based on construction year and heating characteristics.
        // kitchen and bathroom quality are not coded on the microdata
        int quality = PropertiesSynPop.get().main.numberofQualityLevels;
        if ((districtHeatingType == 0) && (blockCentralHeatingType == 0)) quality--; // reduce quality if not central or district heating
        if (heatingEnergyType > 4) quality--; //reduce quality if energy is not gas, electricity or heating oil (i.e. coal, wood, biomass, solar energy)
        if (numberOfHeatingTypes > 1) quality++; //increase quality if there is additional heating in the house (regardless the used energy)
        if (yearBuilt > 0) {
            // Ages - 1: before 1919, 2: 1919-1948, 3: 1949-1978, 4: 1979-1990; 5: 1991-2000; 6: 2001-2010; 7: 2011-2015; 8: 2016 or later; 9: No response
            float[] deteriorationProbability = {0.9f, 0.8f, 0.6f, 0.3f, 0.12f, 0.08f, 0.05f, 0.04f, 0.04f};
            float prob = deteriorationProbability[yearBuilt - 1];
            //attempt to drop quality by age two times (to get some spreading of quality levels)
            quality = quality - SiloUtil.select(new double[]{1 - prob ,prob});
            quality = quality - SiloUtil.select(new double[]{1 - prob, prob});
        }

        quality = Math.max(quality, 1);      // ensure that quality never drops below 1
        quality = Math.min(quality, PropertiesSynPop.get().main.numberofQualityLevels);      // ensure that quality never exceeds the number of quality levels
        return quality;
    }


    public DwellingType translateDwellingType (int buildingSize, float ddType1Prob, float ddType3Prob){
        DefaultDwellingTypes.DefaultDwellingTypeImpl type = DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234;
        if (buildingSize < 3) {
            if (SiloUtil.getRandomNumberAsFloat() < ddType1Prob){
                type = DefaultDwellingTypes.DefaultDwellingTypeImpl.SFD;
            } else {
                type = DefaultDwellingTypes.DefaultDwellingTypeImpl.SFA;
            }
        } else {
            if (SiloUtil.getRandomNumberAsFloat() < ddType3Prob){
                type = DefaultDwellingTypes.DefaultDwellingTypeImpl.MF5plus;
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

    public DwellingUsage translateDwellingUsage(int ownership) {   // Use ddOwnership and not ddUse from the attributes map when (and if) calling this method
        DwellingUsage usage;

        if ((ownership == 1) || (ownership == 2)) {
            usage = DwellingUsage.OWNED;
        } else if ((ownership == 3) || (ownership == 4)) {
            usage = DwellingUsage.RENTED;
        } else {
            usage = DwellingUsage.VACANT;
        }
        return usage;
    }

    public int guessPrice(float brw, int quality, int size, DwellingUsage use) {
        //coefficient by quality of the dwelling
        float qualityReduction = 1;

        if (quality == 1) {
            qualityReduction = 0.7f;
        } else if (quality == 2) {
            qualityReduction = 0.9f;
        } else if (quality == 4) {
            qualityReduction = 1.1f;
        }
        //conversion from land price to the monthly rent
        float convertToMonth = 0.0057f;
        //increase price for rented dwellings
        float rentedIncrease = 1; //by default, the price is not reduced/increased
        if (use.equals(DwellingUsage.RENTED)){
            rentedIncrease = 1.2f; //rented dwelling
        } else if (use.equals(DwellingUsage.VACANT)){
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
                floorSpaceDwelling = (int) (30 + SiloUtil.getRandomNumberAsFloat() * 50);
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

    public int dwellingYearBracket(int year) {

        int yearBracket = 0;

        if (year < 1919) {
            yearBracket = 1;
        } else if (year < 1948) {
            yearBracket = 2;
        } else if (year < 1978) {
            yearBracket = 3;
        } else if (year < 1990) {
            yearBracket = 4;
        } else if (year < 2000) {
            yearBracket = 5;
        } else if (year < 2010) {
            yearBracket = 6;
        } else if (year < 2015) {
            yearBracket = 7;
        } else {
            yearBracket = 8;
        }

        return yearBracket;
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

    public int dwellingYearfromBracket(int yearBracket){

        int year = 0;

        if (yearBracket < 2) {
            year = (int) (1900 + SiloUtil.getRandomNumberAsFloat() * 18);
        } else if (yearBracket < 3) {
            year = (int) (1919 + SiloUtil.getRandomNumberAsFloat() * 29);
        } else if (yearBracket < 4) {
            year = (int) (1949 + SiloUtil.getRandomNumberAsFloat() * 29);
        } else if (yearBracket < 5) {
            year = (int) (1979 + SiloUtil.getRandomNumberAsFloat() * 11);
        } else if (yearBracket < 6) {
            year = (int) (1991 + SiloUtil.getRandomNumberAsFloat() * 9);
        } else if (yearBracket < 7) {
            year = (int) (2001 + SiloUtil.getRandomNumberAsFloat() * 9);
        } else if (yearBracket < 8){
            year = (int) (2011 + SiloUtil.getRandomNumberAsFloat() * 4);
        } else {
            year = (int) (2016 + SiloUtil.getRandomNumberAsFloat() * 2);
        }

        return year;
    }
}