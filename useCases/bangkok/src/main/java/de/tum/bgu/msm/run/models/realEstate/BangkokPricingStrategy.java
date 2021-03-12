package de.tum.bgu.msm.run.models.realEstate;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.models.realEstate.pricing.PricingStrategy;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class BangkokPricingStrategy extends JavaScriptCalculator<Double> implements PricingStrategy {

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getPricingScriptInput());

    private double inflectionLow;
    private double inflectionHigh;
    private double slopeLow;
    private double slopeMain;
    private double slopeHigh;
    private double maxDelta;
    private double maxVacancyRateForPriceChange;

    public BangkokPricingStrategy() {
        super(reader);
        inflectionLow = getLowInflectionPoint();
        inflectionHigh = getHighInflectionPoint();
        slopeLow = getLowerSlope();
        slopeMain = getMainSlope();
        slopeHigh = getHighSlope();
        maxDelta = getMaximumChange();
        maxVacancyRateForPriceChange = getMaxVacancyRateForPriceChange();
    }

    private double getMaxVacancyRateForPriceChange() {
        return super.calculate("getMaxVacancyRateForPriceChange");
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
        return super.calculate("getLowInflectionPoint");
    }

    private double getHighInflectionPoint() {
        return super.calculate("getHighInflectionPoint");
    }

    private double getLowerSlope() {
        return super.calculate("getLowerSlope");
    }

    private double getMainSlope() {
        return super.calculate("getMainSlope");
    }

    private double getHighSlope() {
        return super.calculate("getHighSlope");
    }

    private double getMaximumChange() {
        return super.calculate("getMaximumChange");
    }


}
