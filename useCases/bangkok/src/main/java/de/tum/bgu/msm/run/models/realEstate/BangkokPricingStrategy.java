package de.tum.bgu.msm.run.models.realEstate;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.models.realEstate.pricing.PricingStrategy;

public class BangkokPricingStrategy implements PricingStrategy {

    private double inflectionLow;
    private double inflectionHigh;
    private double slopeLow;
    private double slopeMain;
    private double slopeHigh;
    private double maxDelta;
    private double maxVacancyRateForPriceChange;

    public BangkokPricingStrategy() {
        inflectionLow = getLowInflectionPoint();
        inflectionHigh = getHighInflectionPoint();
        slopeLow = getLowerSlope();
        slopeMain = getMainSlope();
        slopeHigh = getHighSlope();
        maxDelta = getMaximumChange();
        maxVacancyRateForPriceChange = getMaxVacancyRateForPriceChange();
    }

    private double getMaxVacancyRateForPriceChange() {
        // This value defines the vacancy rate (not multiplied by structural vacancies) from which the price will not change at all.
        // For vacancies rates above 10% (if set to 0.1) the price will not decrease any more.
        // This value applies for all dwelling types.
        return 0.1;
    }

    public boolean isPriceUpdateAllowed(Dwelling dd) {
        return true;
    }

    @Override
    public double getPriceChangeRate(double vacancyRateAtThisRegion, double structuralVacancyRate) {
        double changeRate;
        float structuralVacLow = (float) (structuralVacancyRate * inflectionLow);
        float structuralVacHigh = (float) (structuralVacancyRate * inflectionHigh);
        if (vacancyRateAtThisRegion > maxVacancyRateForPriceChange){
            //vacancy is higher than the maximum value to change price. Do not change price.
            changeRate = 1 - structuralVacHigh * slopeHigh +
                    (-structuralVacancyRate * slopeMain + structuralVacHigh * slopeMain) +
                    slopeHigh * maxVacancyRateForPriceChange;
        } else if (vacancyRateAtThisRegion < structuralVacLow) {
            // vacancy is particularly low, prices need to rise steeply
            changeRate = 1 - structuralVacLow * slopeLow +
                    (-structuralVacancyRate * slopeMain + structuralVacLow * slopeMain) +
                    slopeLow * vacancyRateAtThisRegion;
        } else if (vacancyRateAtThisRegion < structuralVacHigh) {
            // vacancy is within a normal range, prices change gradually
            changeRate = 1 - structuralVacancyRate * slopeMain + slopeMain * vacancyRateAtThisRegion;
        } else {
            // vacancy is very high but under the maximum value to change price, prices do not change much
            changeRate = 1 - structuralVacHigh * slopeHigh +
                    (-structuralVacancyRate * slopeMain + structuralVacHigh * slopeMain) +
                    slopeHigh * vacancyRateAtThisRegion;
        }
        changeRate = Math.min(changeRate, 1f + maxDelta);
        changeRate = Math.max(changeRate, 1f - maxDelta);

        return changeRate;
    }

    private double getLowInflectionPoint() {
        // The value below is multiplied with the structural vacancy of a given dwelling type and defines the tipping point from where prices increase steeply
        // If this value is set to 1., it means that vacancies below the structural vacancy will lead to a steep price increase
        // If this value is set to 0.5, it means that vacancies below half the structural vacancy will lead to a steep price incrase
        return 0.9;
    }

    private double getHighInflectionPoint() {
        // The value below is multiplied with the structural vacancy of a given dwelling type and defines the tipping point from which prices decrease slowly
        // If this value is set to 2., it means that vacancy rates above twice the structural vacancy will lead to very slow price increases
        return 2.;
    }

    private double getLowerSlope() {
        // describes the steep slope that is used for vacancies below the lower inflection point
        return -10.;
    }

    private double getMainSlope() {
        // describes the medium slope that is used for vacancies between the lower and the higher inflection point
        return -1.;
    }

    private double getHighSlope() {
        // describes the flat slope that is used for vacancies above the higher inflection point
        return -0.1;
    }

    private double getMaximumChange() {
        // describes the maximum change in percent by which the housing may change within one year
        return 0.1;
    }

}
