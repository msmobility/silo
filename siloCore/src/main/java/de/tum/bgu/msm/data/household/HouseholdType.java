package de.tum.bgu.msm.data.household;

import static de.tum.bgu.msm.data.household.IncomeCategory.*;

/**
 * @author Rolf Moeckel, PB Albuquerque
 * Created on Dec 2, 2009
 *
 */
public enum HouseholdType {

    SIZE_1_INC_LOW(LOW),
    SIZE_2_INC_LOW(LOW),
    SIZE_3_INC_LOW(LOW),
    SIZE_4_INC_LOW(LOW),
    SIZE_1_INC_MEDIUM(MEDIUM),
    SIZE_2_INC_MEDIUM(MEDIUM),
    SIZE_3_INC_MEDIUM(MEDIUM),
    SIZE_4_INC_MEDIUM(MEDIUM),
    SIZE_1_INC_HIGH(HIGH),
    SIZE_2_INC_HIGH(HIGH),
    SIZE_3_INC_HIGH(HIGH),
    SIZE_4_INC_HIGH(HIGH),
    SIZE_1_INC_VERY_HIGH(VERY_HIGH),
    SIZE_2_INC_VERY_HIGH(VERY_HIGH),
    SIZE_3_INC_VERY_HIGH(VERY_HIGH),
    SIZE_4_INC_VERY_HIGH(VERY_HIGH);

    private final IncomeCategory incomeCategory;

    HouseholdType(IncomeCategory incomeCategory) {
        this.incomeCategory = incomeCategory;
    }


    public IncomeCategory getIncomeCategory() {
        return incomeCategory;
    }
}


