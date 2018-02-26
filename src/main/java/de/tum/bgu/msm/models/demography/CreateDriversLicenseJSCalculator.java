package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.data.PersonType;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

/**
 * Created by matthewokrah on 13/11/2017.
 */
public class CreateDriversLicenseJSCalculator extends JavaScriptCalculator<Double> {

    public CreateDriversLicenseJSCalculator (Reader reader) {
        super(reader);
    }

    public double calculateCreateDriversLicenseProbability(PersonType type) {
        return super.calculate("calculateCreateDriversLicenseProbability", type);
    }
}
