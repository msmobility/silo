package de.tum.bgu.msm.models.relocation.moves;

import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;

public class RegionProbabilityStrategyImpl extends JavaScriptCalculator<Double> implements RegionProbabilityStrategy {

    public RegionProbabilityStrategyImpl() {
        super(new InputStreamReader(ScriptInputProvider.getRegionProbabilityScriptInput()));
    }

    @Override
    public double calculateSelectRegionProbability(double util) {
        return super.calculate("calculateSelectRegionProbability", util);
    }
}
