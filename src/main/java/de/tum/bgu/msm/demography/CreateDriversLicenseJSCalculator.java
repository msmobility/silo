package de.tum.bgu.msm.demography;

import de.tum.bgu.msm.data.PersonType;
import de.tum.bgu.msm.utils.javaScript.JavaScriptCalculator;

import java.io.Reader;

/**
 * Created by matthewokrah on 13/11/2017.
 */
public class CreateDriversLicenseJSCalculator extends JavaScriptCalculator<Double> {

    public CreateDriversLicenseJSCalculator (Reader reader, boolean log) {
        super(reader, log);
    }

    public void setPersonType (PersonType type) {
        this.bindings.put("personType", type);
    }

}
