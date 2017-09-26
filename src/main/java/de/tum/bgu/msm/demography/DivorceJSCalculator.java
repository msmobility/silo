package de.tum.bgu.msm.demography;

import de.tum.bgu.msm.utils.javaScript.JavaScriptCalculator;

import java.io.Reader;

/**
 * Created by matthewokrah on 26/09/2017.
 */
public class DivorceJSCalculator extends JavaScriptCalculator <Double> {

    public DivorceJSCalculator (Reader reader, boolean log){
        super(reader, log);
    }

    public void setPersonType (int personType) {
        this.bindings.put("personType", personType);
    }
}
