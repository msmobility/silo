package de.tum.bgu.msm.models.realEstate.pricing;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DefaultPricingStrategy extends JavaScriptCalculator<Double> implements PricingStrategy{

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getPricingScriptInput());


    public DefaultPricingStrategy() {
        super(reader);
    }

    public double getLowInflectionPoint() {
        return super.calculate("getLowInflectionPoint");
    }

    public double getHighInflectionPoint() {
        return super.calculate("getHighInflectionPoint");
    }

    public double getLowerSlope() {
        return super.calculate("getLowerSlope");
    }

    public double getMainSlope() {
        return super.calculate("getMainSlope");
    }

    public double getHighSlope() {
        return super.calculate("getHighSlope");
    }

    public double getMaximumChange() {
        return super.calculate("getMaximumChange");
    }

    @Override
    public boolean shouldUpdatePrice(Dwelling dd) {
        return true;
    }

    @Override
    public double getMaxVacancyRateForPriceChange() {
        return super.calculate("getMaxVacancyRateForPriceChange");
    }
}
