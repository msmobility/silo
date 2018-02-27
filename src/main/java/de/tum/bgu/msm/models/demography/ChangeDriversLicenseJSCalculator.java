package de.tum.bgu.msm.models.demography;

import java.io.Reader;

import de.tum.bgu.msm.data.PersonType;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

/**
 * Created by matthewokrah on 13/11/2017.
 */
public class ChangeDriversLicenseJSCalculator extends JavaScriptCalculator<Double> {

    public ChangeDriversLicenseJSCalculator (Reader reader) {
        super(reader);
    }

    public double calculateChangeDriversLicenseProbability(PersonType type) {
        return super.calculate("calculateChangeDriversLicenseProbability", type);
    }
}
