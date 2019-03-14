package de.tum.bgu.msm.models.realEstate.construction;

import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public class DefaultConstructionDemandStrategy extends JavaScriptCalculator<Double> implements ConstructionDemandStrategy {

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getConstructionDemandScriptInput());

    public DefaultConstructionDemandStrategy() {
        super(reader);
    }

    public double calculateConstructionDemand(double vacancyByRegion, DwellingType dwellingType) {
        return super.calculate("calculateConstructionDemand", vacancyByRegion, dwellingType);
    }
}
