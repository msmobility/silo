package de.tum.bgu.msm.syntheticPopulationGenerator;

import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

/**
 * Created by matthewokrah on 28/09/2017.
 */
public class CreateCarOwnershipJSCalculator extends JavaScriptCalculator<double[]> {

    public CreateCarOwnershipJSCalculator (Reader reader){
        super(reader);
    }

    public double[] calculate(int license, int workers, int income, double logDistanceToTransit, int areaType) {
        return super.calculate("calculate", license, workers, income, logDistanceToTransit, areaType);
    }
}
