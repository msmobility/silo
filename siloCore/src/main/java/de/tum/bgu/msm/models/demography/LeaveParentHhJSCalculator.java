package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.data.PersonType;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public class LeaveParentHhJSCalculator extends JavaScriptCalculator<Double> {

    public LeaveParentHhJSCalculator (Reader reader) {
        super(reader);
    }

    public double calculateLeaveParentsProbability(PersonType type) {
        return super.calculate("calculateLeaveParentsProbability", type);
    }
}
