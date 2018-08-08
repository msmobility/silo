package de.tum.bgu.msm.models.autoOwnership.munich;

import de.tum.bgu.msm.util.js.JavaScriptCalculator;

import java.io.Reader;

/**
 * Created by matthewokrah on 26/06/2018.
 */
public class SwitchToAutonomousVehicleJSCalculator extends JavaScriptCalculator<double[]>{
    public SwitchToAutonomousVehicleJSCalculator (Reader reader){
        super(reader);
    }

    public double[] calculate(int income, int year) {
        return super.calculate("calculateSwitchToAutonomousVehicleProbabilities", income, year);
    }
}
