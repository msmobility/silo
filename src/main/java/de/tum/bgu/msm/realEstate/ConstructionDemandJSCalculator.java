package de.tum.bgu.msm.realEstate;

import de.tum.bgu.msm.data.DwellingType;
import de.tum.bgu.msm.utils.javaScript.JavaScriptCalculator;

import java.io.Reader;

public class ConstructionDemandJSCalculator extends JavaScriptCalculator<Float> {


    public ConstructionDemandJSCalculator(Reader reader, boolean log) {
        super(reader, log);
    }


    public void setDwellingType (DwellingType dwellingType) {
        this.bindings.put("dwellingType", dwellingType);
    }
}
