package de.tum.bgu.msm.scenarios.health;

import de.tum.bgu.msm.data.Mode;

// Calculates physical activity exposure for a given mode, distance, and time
public class PhysicalActivity {

    // Cycle-specific assumptions/parameters
    private static final double GROUND_RESISTANCE_COEFFICIENT = 0.007;
    private static final double WIND_RESISTANCE_COEFFICIENT = 0.5;
    private static final double PERSON_KG = 76.9;
    private static final double BIKE_AND_BAG_KG = 16.;
    private static final double FRONTAL_AREA_M2 = 0.8;
    private static final double AIR_DENSITY_KG_PER_M2 = 1.225;
    private static final double STATIONARY_TIME_PROPORTION = 0.2;
    private static final double CYCLE_EFFICIENCY = 0.93;

    public static double calculate(Mode mode, double metres, double hours) {
        if(mode.equals(Mode.walk)) {
            return calculateWalk(metres, hours);
        } else if(mode.equals(Mode.bicycle)) {
            return calculateCycle(metres, hours);
        } else {
            return 0.;
        }
    }

    private static double calculateWalk(double metres, double hours) {
        double speed = metres / (hours * 3600.);
        double walkMMET = 1.45*Math.exp(0.684*speed) - 1;
        return walkMMET * hours;
    }

    private static double calculateCycle(double metres, double hours) {
        double speed = metres / (hours * 3600.);

        double speedMoving = speed / (1-STATIONARY_TIME_PROPORTION);
        double speedAir = speedMoving;
        double powerRoadResistance = GROUND_RESISTANCE_COEFFICIENT * (PERSON_KG + BIKE_AND_BAG_KG) * speedMoving;
        double powerWindResistance = WIND_RESISTANCE_COEFFICIENT * FRONTAL_AREA_M2 * AIR_DENSITY_KG_PER_M2 * Math.pow(speedAir,2) * speedMoving;
        double power = (powerRoadResistance + powerWindResistance) / CYCLE_EFFICIENCY;
        double oxygenLitersPerMin = 0.01141 * power + 0.435;
        double kCalPerMin = oxygenLitersPerMin * 5;
        double cycleMMET = kCalPerMin * 60 / (PERSON_KG * (1-STATIONARY_TIME_PROPORTION)) - 1;

        return cycleMMET * hours;
    }

}
