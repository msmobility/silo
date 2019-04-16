package relocation;

import de.tum.bgu.msm.data.household.IncomeCategory;

public interface SelectRegionStrategy {
    double calculateSelectRegionProbability(IncomeCategory incomeCategory,float priceUtil, float regAcc, float quick);
}
