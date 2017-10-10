package de.tum.bgu.msm.autoOwnership;

import de.tum.bgu.msm.utils.javaScript.JavaScriptCalculator;

import java.io.Reader;

/**
 * Created by matthewokrah on 28/09/2017.
 */
public class CreateCarOwnershipJSCalculator extends JavaScriptCalculator<double[]> {

    public CreateCarOwnershipJSCalculator (Reader reader, boolean log){
        super(reader, log);
    }

    public void setLicense (int license) {
        this.bindings.put("license", license);
    }

    public void setWorkers (int workers) {
        this.bindings.put("workers", workers);
    }

    public void setIncome (int income) {
        this.bindings.put("income", income);
    }

    public void setLogDistanceToTransit (int logDistanceToTransit) {
        this.bindings.put("logDistanceToTransit", logDistanceToTransit);
    }

    public void setAreaType (int areaType) {
        this.bindings.put("areaType", areaType);
    }

    public void setAlternative (int alternative) {
        this.bindings.put("alternative", alternative);
    }

}
