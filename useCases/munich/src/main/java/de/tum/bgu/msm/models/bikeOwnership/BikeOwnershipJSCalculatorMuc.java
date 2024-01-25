package de.tum.bgu.msm.models.bikeOwnership;

import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

/**
 * Repurposed from CarOwnership by Kieran on 25/01/2024.
 */
public class BikeOwnershipJSCalculatorMuc extends JavaScriptCalculator<double[]> {

    public BikeOwnershipJSCalculatorMuc(Reader reader){
        super(reader);
    }

    public double[] calculateBikeOwnerShipProbabilities(int previousBikes, ppAge,
                                                       int changeResidence) {
        return super.calculate("calculateBikeOwnerShipProbabilities", previousBikes, ppAge, changeResidence);
    }

}
