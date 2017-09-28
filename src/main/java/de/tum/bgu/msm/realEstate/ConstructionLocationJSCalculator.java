package de.tum.bgu.msm.realEstate;

import de.tum.bgu.msm.data.DwellingType;
import de.tum.bgu.msm.utils.javaScript.JavaScriptCalculator;

import java.io.Reader;

public final class ConstructionLocationJSCalculator extends JavaScriptCalculator<Double>{

    public ConstructionLocationJSCalculator(Reader reader, boolean log) {
        super(reader, log);
    }

    public void setDwellingType(DwellingType dwellingType) {
        this.bindings.put("dwellingType", dwellingType);
    }

    public void setPrice(float price) {
        this.bindings.put("price", price);
    }

    public void setAccessibility(double accessibility) {
        this.bindings.put("accessibility", accessibility);
    }
}
