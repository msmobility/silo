package de.tum.bgu.msm.models.relocation.moves;

import de.tum.bgu.msm.data.household.IncomeCategory;

public class RegionUtilityStrategyImpl implements RegionUtilityStrategy {

    @Override
    public double calculateSelectRegionProbability(IncomeCategory incomeCategory,
                                                   float price, float accessibility, float share) {
        double alpha;

        switch (incomeCategory) {
            case LOW:
                alpha = 0.04;
                break;
            case MEDIUM:
                alpha = 0.08;
                break;
            case HIGH:
                alpha = 0.120;
                break;
            case VERY_HIGH:
                alpha = 0.16;
                break;
            default:
                throw new Error("Undefined income group: " + incomeCategory);
        }

        return (1 - alpha) * price + alpha * accessibility;
    }
}
