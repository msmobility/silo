package de.tum.bgu.msm.demography;

import java.io.Reader;

import de.tum.bgu.msm.data.PersonType;
import  de.tum.bgu.msm.utils.javaScript.JavaScriptCalculator;

/**
 * Created by matthewokrah on 13/11/2017.
 */
public class ChangeDriversLicenseJSCalculator extends JavaScriptCalculator <Double>{

    public ChangeDriversLicenseJSCalculator (Reader reader, boolean log) {
        super(reader, log);
    }

    public void setPersonType (PersonType type) {
        this.bindings.put("personType", type);
    }


}
