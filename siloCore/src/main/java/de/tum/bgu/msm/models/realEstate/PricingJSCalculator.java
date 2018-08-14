package de.tum.bgu.msm.models.realEstate;

import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public class PricingJSCalculator extends JavaScriptCalculator<Double> {


    public PricingJSCalculator(Reader reader) {
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
}
