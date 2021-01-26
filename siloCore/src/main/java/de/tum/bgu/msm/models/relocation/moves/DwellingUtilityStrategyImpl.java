package de.tum.bgu.msm.models.relocation.moves;

import de.tum.bgu.msm.data.household.HouseholdType;

public class DwellingUtilityStrategyImpl implements DwellingUtilityStrategy {

    @Override
    public double calculateSelectDwellingUtility(HouseholdType ht, double ddSizeUtility, double ddPriceUtility,
                                                 double ddQualityUtility, double ddAutoAccessibilityUtility,
                                                 double transitAccessibilityUtility, double ddWorkDistanceUtility) {

        double alpha;
        double beta;
        double gamma;
        double delta;
        double epsilon;

        switch (ht) {
            case SIZE_1_INC_LOW:
                //old hhtype 0
                alpha = 0.12;
                beta = 0.25;
                gamma = 0.3;
                delta = 0.2;
                epsilon = 0.35;
                break;
            case SIZE_2_INC_LOW:
                //old hhtype 1
                alpha = 0.14;
                beta = 0.25;
                gamma = 0.3;
                delta = 0.2;
                epsilon = 0.35;
                break;
            case SIZE_3_INC_LOW:
                //old hhtype 2
                alpha = 0.16;
                beta = 0.25;
                gamma = 0.3;
                delta = 0.2;
                epsilon = 0.35;
                break;
            case SIZE_4_INC_LOW:
                //old hhtype 3
                alpha = 0.18;
                beta = 0.25;
                gamma = 0.3;
                delta = 0.2;
                epsilon = 0.35;
                break;
            case SIZE_1_INC_MEDIUM:
                //old hhtype 4
                alpha = 0.17;
                beta = 0.2;
                gamma = 0.25;
                delta = 0.35;
                epsilon = 0.3;
                break;
            case SIZE_2_INC_MEDIUM:
                //old hhtype 5
                alpha = 0.19;
                beta = 0.2;
                gamma = 0.25;
                delta = 0.35;
                epsilon = 0.3;
                break;
            case SIZE_3_INC_MEDIUM:
                //old hhtype 6
                alpha = 0.21;
                beta = 0.2;
                gamma = 0.25;
                delta = 0.35;
                epsilon = 0.3;
                break;
            case SIZE_4_INC_MEDIUM:
                //old hhtype 7
                alpha = 0.23;
                beta = 0.2;
                gamma = 0.25;
                delta = 0.35;
                epsilon = 0.3;
                break;
            case SIZE_1_INC_HIGH:
                //old hhtype 8
                alpha = 0.22;
                beta = 0.15;
                gamma = 0.15;
                delta = 0.5;
                epsilon = 0.1;
                break;
            case SIZE_2_INC_HIGH:
                //old hhtype 9
                alpha = 0.24;
                beta = 0.15;
                gamma = 0.15;
                delta = 0.5;
                epsilon = 0.1;
                break;
            case SIZE_3_INC_HIGH:
                //old hhtype 10
                alpha = 0.26;
                beta = 0.15;
                gamma = 0.15;
                delta = 0.5;
                epsilon = 0.1;
                break;
            case SIZE_4_INC_HIGH:
                //old hhtype 11
                alpha = 0.28;
                beta = 0.15;
                gamma = 0.15;
                delta = 0.5;
                epsilon = 0.1;
                break;
            case SIZE_1_INC_VERY_HIGH:
                //old hhtype 12
                alpha = 0.27;
                beta = 0.12;
                gamma = 0.08;
                delta = 0.6;
                epsilon = 0.05;
                break;
            case SIZE_2_INC_VERY_HIGH:
                //old hhtype 13
                alpha = 0.29;
                beta = 0.12;
                gamma = 0.08;
                delta = 0.6;
                epsilon = 0.05;
                break;
            case SIZE_3_INC_VERY_HIGH:
                //old hhtype 14
                alpha = 0.31;
                beta = 0.12;
                gamma = 0.08;
                delta = 0.6;
                epsilon = 0.05;
                break;
            case SIZE_4_INC_VERY_HIGH:
                //old hhtype 15
                alpha = 0.33;
                beta = 0.12;
                gamma = 0.08;
                delta = 0.6;
                epsilon = 0.05;
                break;
            default:
                throw new Error("The household type is not defined!");
        }
        double optFactors = alpha * ddSizeUtility + beta * ddAutoAccessibilityUtility
                + gamma * transitAccessibilityUtility + (1.0 - alpha - beta - gamma) * ddQualityUtility;
        return Math.pow(optFactors, delta)
                * Math.pow(ddPriceUtility, epsilon)
                * Math.pow(ddWorkDistanceUtility, (1 - delta - epsilon));
    }
}