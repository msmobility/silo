package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.data.IncomeCategory;
import de.tum.bgu.msm.data.Nationality;
import de.tum.bgu.msm.data.PersonDiscrimination;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public class SelectRegionJSCalculator extends JavaScriptCalculator<Double> {

    public SelectRegionJSCalculator(Reader reader) {
        super(reader);
    }

    //person discrimination can be race or nationality or any other attribute
    // that implements PersonDiscrimination interface - this will need to be defined in the javascript
    public double calculateSelectRegionProbability(IncomeCategory incomeCategory, PersonDiscrimination personDiscrimination, float price, float accessibility, float share) {
        return super.calculate("calculateSelectRegionProbability", incomeCategory, personDiscrimination, price, accessibility, share);
    }
}
