package de.tum.bgu.msm.data;

import static de.tum.bgu.msm.data.IncomeCategory.*;

/**
 * @author Rolf Moeckel, PB Albuquerque
 * Created on Dec 2, 2009
 *
 */
public enum HouseholdType {


    size1inc1(LOW),
    size2inc1(LOW),
    size3inc1(LOW),
    size4inc1(LOW),
    size1inc2(IncomeCategory.MEDIUM),
    size2inc2(IncomeCategory.MEDIUM),
    size3inc2(IncomeCategory.MEDIUM),
    size4inc2(IncomeCategory.MEDIUM),
    size1inc3(IncomeCategory.HIGH),
    size2inc3(IncomeCategory.HIGH),
    size3inc3(IncomeCategory.HIGH),
    size4inc3(IncomeCategory.HIGH),
    size1inc4(IncomeCategory.VERY_HIGH),
    size2inc4(IncomeCategory.VERY_HIGH),
    size3inc4(IncomeCategory.VERY_HIGH),
    size4inc4(IncomeCategory.VERY_HIGH);

    private final IncomeCategory incomeCategory;

    HouseholdType(IncomeCategory incomeCategory) {
        this.incomeCategory = incomeCategory;
    }

    public static HouseholdType defineHouseholdType (int hhSize, IncomeCategory incomeCategory) {
        // define household type based on size and income

        HouseholdType ht = null;
        if (hhSize == 1) {
            if (incomeCategory == LOW) ht = size1inc1;
            else if (incomeCategory == MEDIUM) ht = size1inc2;
            else if (incomeCategory == HIGH) ht = size1inc3;
            else ht = size1inc4;
        } else if (hhSize == 2) {
            if (incomeCategory == LOW) ht = size2inc1;
            else if (incomeCategory == MEDIUM) ht = size2inc2;
            else if (incomeCategory == HIGH) ht = size2inc3;
            else ht = size2inc4;
        } else if (hhSize == 3) {
            if (incomeCategory == LOW) ht = size3inc1;
            else if (incomeCategory == MEDIUM) ht = size3inc2;
            else if (incomeCategory == HIGH) ht = size3inc3;
            else ht = size3inc4;
        } else if (hhSize > 3) {
            if (incomeCategory == LOW) ht = size4inc1;
            else if (incomeCategory == MEDIUM) ht = size4inc2;
            else if (incomeCategory == HIGH) ht = size4inc3;
            else ht = size4inc4;
        }
        return ht;
    }

    public IncomeCategory getIncomeCategory() {
        return incomeCategory;
    }

    public static int convertHouseholdTypeToIncomeCategory (HouseholdType ht) {
        if (ht.equals(HouseholdType.size1inc1) || ht.equals(HouseholdType.size2inc1) ||
                ht.equals(HouseholdType.size3inc1) || ht.equals(HouseholdType.size4inc1)) {
            return 1;
        } else if (ht.equals(HouseholdType.size1inc2) || ht.equals(HouseholdType.size2inc2) ||
                ht.equals(HouseholdType.size3inc2) || ht.equals(HouseholdType.size4inc2)) {
            return 2;
        } else if (ht.equals(HouseholdType.size1inc3) || ht.equals(HouseholdType.size2inc3) ||
                ht.equals(HouseholdType.size3inc3) || ht.equals(HouseholdType.size4inc3)) {
            return 3;
        } else {
            return 4;
        }
    }
}


