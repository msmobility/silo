package de.tum.bgu.msm.models.realEstate.construction;

import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

public final class DefaultConstructionLocationStrategy extends JavaScriptCalculator<Double> implements ConstructionLocationStrategy{

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getConstructionLocationScriptInput());

    public DefaultConstructionLocationStrategy() {
        super(reader);
    }

    @Override
    public double calculateConstructionProbability(DwellingType dwellingType, double price, double accessibility) {
        return super.calculate("calculateConstructionUtility", dwellingType, price, accessibility);
    }
}
