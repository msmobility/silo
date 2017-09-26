package de.tum.bgu.msm.realEstate;

import de.tum.bgu.msm.data.DwellingType;
import de.tum.bgu.msm.utils.javaScript.JavaScriptCalculator;

import java.io.Reader;

public class ConstructionDemandJSCalculator extends JavaScriptCalculator<Double> {


    public ConstructionDemandJSCalculator(Reader reader, boolean log) {
        super(reader, log);
    }


    public void setDwellingType (DwellingType dwellingType) {
        this.bindings.put("dwellingType", dwellingType.toString());
    }

    public void setVacancyByRegion(double vacancyByRegion) {
        this.bindings.put("vacancyByRegion", vacancyByRegion);
    }


}
