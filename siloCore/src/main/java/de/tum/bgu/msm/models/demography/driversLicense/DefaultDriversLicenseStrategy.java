package de.tum.bgu.msm.models.demography.driversLicense;

import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.models.ScriptInputProvider;
import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by matthewokrah on 13/11/2017.
 */
public class DefaultDriversLicenseStrategy extends JavaScriptCalculator<Double> implements DriversLicenseStrategy{

    private final static Reader reader = new InputStreamReader(ScriptInputProvider.getDriversLicenseProbabilityScriptInput());

    public DefaultDriversLicenseStrategy() {
        super(reader);
    }


    @Override
    public double calculateChangeDriversLicenseProbability(Person person) {
        return super.calculate("calculateChangeDriversLicenseProbability", person.getType());
    }

    @Override
    public double calculateCreateDriversLicenseProbability(Person pp) {
        return super.calculate("calculateCreateDriversLicenseProbability", pp.getType());
    }
}
