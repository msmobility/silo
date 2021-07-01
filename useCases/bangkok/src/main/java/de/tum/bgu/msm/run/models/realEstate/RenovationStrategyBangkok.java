package de.tum.bgu.msm.run.models.realEstate;

import de.tum.bgu.msm.models.realEstate.renovation.RenovationStrategy;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class RenovationStrategyBangkok extends JavaScriptCalculator<Double> implements RenovationStrategy {


    private final static Reader reader = new InputStreamReader(RenovationStrategyBangkok.class.getResourceAsStream("RenovationCalcBangkok"));

    public RenovationStrategyBangkok() {super(reader);}


    @Override
    public double calculateRenovationProbability(int oldQuality, int newQuality) {
        return super.calculate("calculateRenovationProbability", oldQuality, newQuality);
    }
}
