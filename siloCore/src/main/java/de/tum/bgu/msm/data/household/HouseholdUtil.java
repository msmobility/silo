package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.Race;

import static de.tum.bgu.msm.data.household.HouseholdType.*;
import static de.tum.bgu.msm.data.household.IncomeCategory.*;

public class HouseholdUtil {

    private static HouseholdFactory factory = new HouseholdFactoryImpl();

    private HouseholdUtil() {
    }


    public static HouseholdFactory getFactory() {
        return factory;
    }

    public static int getNumberOfWorkers(Household household) {
        return (int) household.getPersons().values().stream().filter(p -> p.getOccupation() == Occupation.EMPLOYED).count();
    }

    public static int getHHLicenseHolders(Household household) {
        return (int) household.getPersons().values().stream().filter(Person::hasDriverLicense).count();
    }

    public static int getHhIncome(Household household) {
        int hhInc = 0;
        for (Person i : household.getPersons().values()) {
            hhInc += i.getIncome();
        }
        return hhInc;
    }

    public static boolean checkIfOnlyChildrenRemaining(Household household) {
        if (household.getPersons().isEmpty()) {
            return false;
        }
        for (Person pp : household.getPersons().values()) {
            if (pp.getAge() >= 16) {
                return false;
            }
        }
        return true;
    }

    public static HouseholdType defineHouseholdType(Household household) {
        // define household type based on size and income

        int hhSize = household.getHhSize();
        IncomeCategory incomeCategory = HouseholdDataManager.getIncomeCategoryForIncome(HouseholdUtil.getHhIncome(household));

        HouseholdType ht = null;
        if (hhSize == 1) {
            if (incomeCategory == LOW) ht = SIZE_1_INC_LOW;
            else if (incomeCategory == MEDIUM) ht = SIZE_1_INC_MEDIUM;
            else if (incomeCategory == HIGH) ht = SIZE_1_INC_HIGH;
            else ht = SIZE_1_INC_VERY_HIGH;
        } else if (hhSize == 2) {
            if (incomeCategory == LOW) ht = SIZE_2_INC_LOW;
            else if (incomeCategory == MEDIUM) ht = SIZE_2_INC_MEDIUM;
            else if (incomeCategory == HIGH) ht = SIZE_2_INC_HIGH;
            else ht = SIZE_2_INC_VERY_HIGH;
        } else if (hhSize == 3) {
            if (incomeCategory == LOW) ht = SIZE_3_INC_LOW;
            else if (incomeCategory == MEDIUM) ht = SIZE_3_INC_MEDIUM;
            else if (incomeCategory == HIGH) ht = SIZE_3_INC_HIGH;
            else ht = SIZE_3_INC_VERY_HIGH;
        } else if (hhSize > 3) {
            if (incomeCategory == LOW) ht = SIZE_4_INC_LOW;
            else if (incomeCategory == MEDIUM) ht = SIZE_4_INC_MEDIUM;
            else if (incomeCategory == HIGH) ht = SIZE_4_INC_HIGH;
            else ht = SIZE_4_INC_VERY_HIGH;
        }
        return ht;
    }

    public static Race defineHouseholdRace(Household household) {
        Race householdRace = null;
        for (Person pp : household.getPersons().values()) {
            if (householdRace == null) {
                householdRace = pp.getRace();
            } else if (pp.getRace() != householdRace) {
                return Race.other;
            }
        }
        return householdRace;
    }
}
