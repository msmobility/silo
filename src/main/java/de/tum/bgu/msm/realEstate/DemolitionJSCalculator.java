package de.tum.bgu.msm.realEstate;

import de.tum.bgu.msm.utils.javaScript.JavaScriptCalculator;

import java.io.Reader;

public final class DemolitionJSCalculator extends JavaScriptCalculator<Double> {

    public DemolitionJSCalculator(Reader reader, boolean log) {
        super(reader, log);
    }

    public void setOccupied(boolean occupied) {
        this.bindings.put("occupied", occupied);
    }

    public void setDwellingQuality(int quality) {
        this.bindings.put("quality", quality);
    }
}
