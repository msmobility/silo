package de.tum.bgu.msm.scenarios.health;

import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.Purpose;
import de.tum.bgu.msm.data.person.PersonMuc;

// Calculates physical activity exposure for a given mode, distance, and time
public class PhysicalActivity {

    // Cycle-specific assumptions/parameters
    private static final double GRADIENT = .015;
    private static final double GRAVITY_M_PER_S2 = 9.8;
    private static final double GROUND_RESISTANCE_COEFFICIENT = 0.007;
    private static final double WIND_RESISTANCE_COEFFICIENT = 0.5;
    private static final double PERSON_KG = 76.9;
    private static final double BIKE_AND_BAG_KG = 16.;
    private static final double FRONTAL_AREA_M2 = 0.8;
    private static final double AIR_DENSITY_KG_PER_M2 = 1.225;
    private static final double STATIONARY_TIME_PROPORTION = 0.2;
    private static final double CYCLE_EFFICIENCY = 0.93;

    public static double getMet(Mode mode, double metres, double seconds) {
        if(mode.equals(Mode.autoDriver) || mode.equals(Mode.autoPassenger)) {
            return 1.28;
        } else if(mode.equals(Mode.bus)) {
            return 1.67;
        } else if(mode.equals(Mode.walk)) {
            return getWalkMet(metres, seconds);
        } else if(mode.equals(Mode.bicycle)) {
            return getCycleMet(metres, seconds);
        } else {
            return 0.;
        }
    }

    public static double getMMet(Mode mode, double metres, double seconds) {
        return getMet(mode, metres, seconds) - 1.;
    }


    private static double getWalkMet(double metres, double seconds) {
        // From ACSM's Guidelines for Exercise Testing and Prescription
        double speed = metres / seconds;
        return (6. * speed + 54. * speed * GRADIENT + 3.5) / 3.5;
    }

    private static double getCycleMet(double metres, double seconds) {
        // From Propensity to Cycle Tool
        double speed = metres / seconds;
        double speedMoving = speed / (1-STATIONARY_TIME_PROPORTION);
        double speedAir = speedMoving;
        double powerRoadResistance = GROUND_RESISTANCE_COEFFICIENT * (PERSON_KG + BIKE_AND_BAG_KG) * speedMoving;
        double powerWindResistance = WIND_RESISTANCE_COEFFICIENT * FRONTAL_AREA_M2 * AIR_DENSITY_KG_PER_M2 * Math.pow(speedAir,2) * speedMoving;
        double powerGravity = GRAVITY_M_PER_S2 * GRADIENT *  (PERSON_KG + BIKE_AND_BAG_KG) * speedMoving;
        double power = (powerRoadResistance + powerWindResistance + powerGravity) / CYCLE_EFFICIENCY;
        double oxygenLitersPerMin = 0.01141 * power + 0.435;
        double kCalPerMin = oxygenLitersPerMin * 5;

        return 0.88586 * kCalPerMin * 60 / (PERSON_KG * (1-STATIONARY_TIME_PROPORTION));
    }

}
