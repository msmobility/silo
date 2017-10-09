package de.tum.bgu.msm.relocation;

import de.tum.bgu.msm.utils.javaScript.JavaScriptCalculator;

import java.io.Reader;

public class SelectDwellingJSCalculator extends JavaScriptCalculator <Double>{

    public SelectDwellingJSCalculator(Reader reader, boolean log) {
        super(reader, log);
    }

    public void setDwellingUtility(double ddUtility) { this.bindings.put("ddUtility", ddUtility);}

}
