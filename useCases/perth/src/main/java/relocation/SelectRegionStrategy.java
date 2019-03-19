package relocation;

import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.data.person.Nationality;

public interface SelectRegionStrategy {
    double calculateSelectRegionProbability(IncomeCategory incomeCategory, Nationality nationality, float priceUtil, float regAcc, float quick);
}
