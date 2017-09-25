package de.tum.bgu.msm.relocation;

import de.tum.bgu.msm.data.Nationality;
import de.tum.bgu.msm.utils.javaScript.JavaScriptCalculator;

import java.io.Reader;

public class SelectRegionJSCalculator extends JavaScriptCalculator<Double> {

    public SelectRegionJSCalculator(Reader reader, boolean log) {
        super(reader, log);
    }

    public void setIncomeGroup(int group) {
        this.bindings.put("incomeGroup", group);
    }

    public void setNationality(Nationality nationality) {
        this.bindings.put("nationality", nationality.ordinal());
    }

    public void setMedianPrice(float price) {
        this.bindings.put("price", price);
    }

    public void setAccessibility(float accessibility) {
        this.bindings.put("accessibility", accessibility);
    }

    public void setForeignersShare(float share) {
        this.bindings.put("share", share);
    }
}
