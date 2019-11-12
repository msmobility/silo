package de.tum.bgu.msm.models.pricing;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.models.realEstate.pricing.PricingStrategy;

/**
 * Pricing settings for Takamatsu study area
 * @author Nico
 */
public class PricingStrategyTak implements PricingStrategy {

    /**
     * This value is multiplied with the structural vacancy of a given dwelling type
     * and defines the tipping point from where prices increase steeply
     * If this value is set to 1., it means that vacancies below the structural vacancy will lead
     * to a steep price increase
     * If this value is set to 0.5, it means that vacancies below half the structural vacancy will lead
     * to a steep price increase
     */
    private static final double INFLECTION_LOW = 0.9;

    /**
     * This value is multiplied with the structural vacancy of a given dwelling type
     * and defines the tipping point from which prices decrease slowly
     * If this value is set to 2., it means that vacancy rates above twice the structural vacancy will lead
     * to very slow price increases
     */
    private static final double INFLECTION_HIGH = 2.;

    /**
     * This value defines the vacancy rate (not multiplied by structural vacancies) from which the price will not change at all.
     * For vacancies rates above 10% (if set to 0.1) the price will not decrease any more.
     * This value applies for all dwelling types.
     */
    private static final double MAX_VACANCY_RATE_FOR_PRICE_CHANGE = 0.1;

    /**
     * Describes the steep slope that is used for vacancies below the lower inflection point
     */
    private static final double SLOPE_LOW = -10.;

    /**
     * Describes the medium slope that is used for vacancies between the lower and the higher inflection point
     */
    private static final double SLOPE_MAIN = -0.6;

    /**
     * Describes the flat slope that is used for vacancies above the higher inflection point
     */
    private static final double SLOPE_HIGH = -0.03;

    /**
     * Describes the maximum change in percent by which the housing may change within one year
     */
    private static final double MAX_DELTA = 0.1;

    @Override
    public boolean isPriceUpdateAllowed(Dwelling dd) {
        return true;
    }

    @Override
    public double getPriceChangeRate(double vacancyRateAtThisRegion, double structuralVacancyRate) {

        double changeRate;
        double inflectionPointLow = structuralVacancyRate * INFLECTION_LOW;
        double inflectionPointHigh = structuralVacancyRate * INFLECTION_HIGH;

        if (vacancyRateAtThisRegion >= MAX_VACANCY_RATE_FOR_PRICE_CHANGE) {
            //vacancy is higher than the maximum value to change price. Do not change price.
            return  1.;
        } else if (vacancyRateAtThisRegion < inflectionPointLow) {
            // vacancy is particularly low, prices need to rise steeply
            changeRate = 1 + SLOPE_MAIN * (inflectionPointLow - structuralVacancyRate)
            + SLOPE_LOW * (vacancyRateAtThisRegion - inflectionPointLow) ;
        } else if (vacancyRateAtThisRegion < inflectionPointHigh) {
            // vacancy is within a normal range, prices change gradually
            changeRate = 1 + SLOPE_MAIN * (vacancyRateAtThisRegion - structuralVacancyRate);
        } else {
            // vacancy is very high but under the maximum value to change price, prices do not change much
            changeRate = 1 + SLOPE_MAIN * (inflectionPointHigh - structuralVacancyRate)
                    + (vacancyRateAtThisRegion - inflectionPointHigh) * SLOPE_HIGH;
        }
        changeRate = Math.max(Math.min(changeRate, 1. + MAX_DELTA), 1. - MAX_DELTA);
        return changeRate;
    }
}
