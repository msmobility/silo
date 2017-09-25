package de.tum.bgu.msm.realEstate;

import de.tum.bgu.msm.utils.javaScript.JavaScriptCalculator;

import java.io.Reader;

public class RenovationJSCalculator extends JavaScriptCalculator <Double> {

    public RenovationJSCalculator (Reader reader, boolean log) {super(reader, log);}

    public void setQuality (int quality) {
        this.bindings.put("quality", quality);
    }

    public void setAlternative (int alt){
        this.bindings.put("alternative", alt);
    }
}
