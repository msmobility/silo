package de.tum.bgu.msm.demography;

import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

/**
 * Created by matthewokrah on 26/09/2017.
 */
public class DivorceJSCalculator extends JavaScriptCalculator<Double> {

    public DivorceJSCalculator (Reader reader){
        super(reader);
    }

    public double calculateDivorceProbability (int personType) {
        return super.calculate("calculateDivorceProbability", personType);
    }
}
