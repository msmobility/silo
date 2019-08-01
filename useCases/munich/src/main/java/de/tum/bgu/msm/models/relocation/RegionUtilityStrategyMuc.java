package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.data.person.Nationality;

public interface RegionUtilityStrategyMuc {
    double calculateRegionUtility(IncomeCategory incomeCategory, Nationality nationality, float priceUtil, float regAcc, float quick);
}
