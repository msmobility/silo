package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.data.Nationality;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public class SelectRegionJSCalculator extends JavaScriptCalculator<Double> {

    public SelectRegionJSCalculator(Reader reader) {
        super(reader);
    }

    public double calculateSelectRegionProbability(int group, Nationality nationality, float price, float accessibility, float share) {
        return super.calculate("calculateSelectRegionProbability", group, nationality.ordinal(), price, accessibility, share);
    }
}
