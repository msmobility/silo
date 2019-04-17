package de.tum.bgu.msm.models;

import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

public class MarylandUpdateCarOwnershipJSCalculator extends JavaScriptCalculator<double[]> {

    protected MarylandUpdateCarOwnershipJSCalculator(Reader reader) {
        super(reader);
    }

    public double[] calculateCarOwnerShipProbabilities(int hhSize, int wrk, int inc, int transitAcc, int dens) {
        return super.calculate("calculateCarOwnerShipProbabilities", hhSize, wrk, inc,
                transitAcc, dens);
    }
}
