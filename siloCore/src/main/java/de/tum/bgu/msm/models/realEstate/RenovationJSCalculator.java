package de.tum.bgu.msm.models.realEstate;


import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public class RenovationJSCalculator extends JavaScriptCalculator<Double> {

    public RenovationJSCalculator (Reader reader) {super(reader);}

    public double calculateRenovationProbability(int quality, int alternative) {
        return super.calculate("calculateRenovationProbability", quality, alternative);
    }
}
