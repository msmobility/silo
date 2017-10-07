package de.tum.bgu.msm.autoOwnership;

import de.tum.bgu.msm.utils.javaScript.JavaScriptCalculator;

import java.io.Reader;

/**
 * Created by matthewokrah on 29/09/2017.
 */
public class UpdateCarOwnershipJSCalculator extends JavaScriptCalculator<double[]> {

    public UpdateCarOwnershipJSCalculator (Reader reader, boolean log){
        super(reader, log);
    }

    public void setPreviousCars (int previousCars) {
        this.bindings.put("previousCars", previousCars);
    }

    public void setHHSizePlus (int hhSizePlus) {
        this.bindings.put("hhSizePlus", hhSizePlus);
    }

    public void setHHSizeMinus (int hhSizeMinus) {
        this.bindings.put("hhSizeMinus", hhSizeMinus);
    }

    public void setHHIncomePlus (int hhIncomePlus) {
        this.bindings.put("hhIncomePlus", hhIncomePlus);
    }

    public void setHHIncomeMinus (int hhIncomeMinus) {
        this.bindings.put("hhIncomeMinus", hhIncomeMinus);
    }

    public void setLicensePlus (int licensePlus) {
        this.bindings.put("licensePlus", licensePlus);
    }

    public void setChangeResidence (int changeResidence) {
        this.bindings.put("changeResidence", changeResidence);
    }

    public void setAlternative (int alternative) {
        this.bindings.put("alternative", alternative);
    }

}
