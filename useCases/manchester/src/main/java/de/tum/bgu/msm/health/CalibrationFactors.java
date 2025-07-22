package de.tum.bgu.msm.health;

import java.util.HashMap;
import java.util.Map;

public class CalibrationFactors {
    private static final Map<String, Map<String, Double>> calibrationFactors = new HashMap<>();

    static {
        // Initialize scenarios
        String[] scenarios = {"base", "both", "green", "safeStreet", "goDutch"};
        String[] modes = {"Car", "Bike", "Walk"};

        // Populate the map
        for (String scenario : scenarios) {
            Map<String, Double> modeFactors = new HashMap<>();
            for (String mode : modes) {
                // Set base scenario values
                if (scenario.equals("base")) {
                    switch (mode) {
                        case "Bike":
                            modeFactors.put("Cycle", 2.314814815);
                            break;
                        case "Car":
                            modeFactors.put("Driver", 1.266666667);
                            break;
                        case "Walk":
                            modeFactors.put("Walk", 0.741854637);
                            break;
                    }
                } else {
                    // Set other scenarios to 0
                    modeFactors.put(mode, 0.0);
                }
            }
            calibrationFactors.put(scenario, modeFactors);
        }
    }

    // Method to get calibration factor by scenario and mode
    public double getCalibrationFactor(String scenario, String mode) {
        return calibrationFactors.getOrDefault(scenario, new HashMap<>()).getOrDefault(mode, 0.0);
    }
}
