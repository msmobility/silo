package de.tum.bgu.msm.models.bikeOwnership;

import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

/**
 * Repurposed from CarOwnership by Kieran on 25/01/2024.
 */
public class CreateBikeOwnershipJSCalculatorMuc extends JavaScriptCalculator<double[]> {

    public CreateBikeOwnershipJSCalculatorMuc(Reader reader){
        super(reader);
    }

    public double[] calculate(int license, int workers, int income, double logDistanceToTransit, int areaType) {
        return super.calculate("calculate", license, workers, income, logDistanceToTransit, areaType);
    }
}
