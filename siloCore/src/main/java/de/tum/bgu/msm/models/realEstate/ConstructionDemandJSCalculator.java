package de.tum.bgu.msm.models.realEstate;

import de.tum.bgu.msm.data.DwellingType;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public class ConstructionDemandJSCalculator extends JavaScriptCalculator<Double> {

    public ConstructionDemandJSCalculator(Reader reader) {
        super(reader);
    }

    public double calculateConstructionDemand(double vacancyByRegion, DwellingType dwellingType) {
        return super.calculate("calculateConstructionDemand", vacancyByRegion, dwellingType);
    }

}
