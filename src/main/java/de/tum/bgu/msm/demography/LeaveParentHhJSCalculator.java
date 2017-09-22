package de.tum.bgu.msm.demography;

import  de.tum.bgu.msm.utils.javaScript.JavaScriptCalculator;

import java.io.Reader;

public class LeaveParentHhJSCalculator extends JavaScriptCalculator <Double> {

    public LeaveParentHhJSCalculator (Reader reader, boolean log) {
        super(reader, log);
    }

    public void setPersonType (int personType) {
        this.bindings.put("personType", personType);
    }

}
