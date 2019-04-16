package de.tum.bgu.msm.models.carOwnership;

import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

/**
 * Created by matthewokrah on 29/09/2017.
 */
public class CarOwnershipJSCalculatorMuc extends JavaScriptCalculator<double[]> {

    public CarOwnershipJSCalculatorMuc(Reader reader){
        super(reader);
    }

    public double[] calculateCarOwnerShipProbabilities(int previousCars, int hhSizePlus, int hhSizeMinus,
                                                       int hhIncomePlus, int hhIncomeMinus, int licensePlus,
                                                       int changeResidence) {
        return super.calculate("calculateCarOwnerShipProbabilities", previousCars, hhSizePlus, hhSizeMinus,
                hhIncomePlus, hhIncomeMinus, licensePlus, changeResidence);
    }

}
