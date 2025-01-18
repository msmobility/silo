package de.tum.bgu.msm.health.data;

import de.tum.bgu.msm.data.Mode;
import org.matsim.api.core.v01.network.Link;
import routing.components.Gradient;

// Calculates physical activity exposure for a given mode, distance, and time
public class PhysicalActivity {

    // Cycle-specific assumptions/parameters
    private static final double DEFAULT_GRADIENT = 0.005;
    private static final double DEFAULT_GROUND_RESISTANCE = 0.007;
    private static final double GRAVITY_M_PER_S2 = 9.8;
    private static final double WIND_RESISTANCE_COEFFICIENT = 0.5;
    private static final double PERSON_KG = 76.9;
    private static final double BIKE_AND_BAG_KG = 16.;
    private static final double FRONTAL_AREA_M2 = 0.8;
    private static final double AIR_DENSITY_KG_PER_M2 = 1.225;
    private static final double STATIONARY_TIME_PROPORTION = 0.2;
    private static final double CYCLE_EFFICIENCY = 0.93;

    public static double getMMet(Mode mode, double metres, double seconds, Link link) {
        if(mode.equals(Mode.autoDriver) || mode.equals(Mode.autoPassenger)) {
            return 0.28;    // From Costa et al., (2015)
        } else if(mode.equals(Mode.bus)) {
            return 0.67;    // From Costa et al., (2015)
        } else if(mode.equals(Mode.walk)) {
            return getWalkMMet(metres, seconds, link);
        } else if(mode.equals(Mode.bicycle)) {
            return getCycleMMet(metres, seconds, link);
        } else {
            return 0.;
        }
    }


    // Based on ACSM's Guidelines for Exercise Testing and Prescription (Ross, n.d.)
    private static double getWalkMMet(double metres, double seconds, Link link) {
        double gradient = Math.max(0.,Math.min(0.4,getGradient(link)));
        double speed = metres / seconds;
        return (6. * speed + 108. * speed *  gradient) / 3.5;
    }

    // Adapted from cycling intensity calculations in the Propensity to Cycle Tool (Woodcock et al., 2021)
    private static double getCycleMMet(double metres, double seconds, Link link) {

        double groundResistance = DEFAULT_GROUND_RESISTANCE / getSurfaceFactor(link);
        double gradient = Math.max(-0.1,Math.min(0.1,getGradient(link)));
        double speed = metres / seconds;
        double speedMoving = speed / (1 - STATIONARY_TIME_PROPORTION);
        double speedAir = speedMoving;
        double powerRoadResistance = groundResistance * (PERSON_KG + BIKE_AND_BAG_KG) * speedMoving;
        double powerWindResistance = WIND_RESISTANCE_COEFFICIENT * FRONTAL_AREA_M2 * AIR_DENSITY_KG_PER_M2 * Math.pow(speedAir,2) * speedMoving;
        double powerGravity = GRAVITY_M_PER_S2 * gradient *  (PERSON_KG + BIKE_AND_BAG_KG) * speedMoving;
        double power = (powerRoadResistance + powerWindResistance + powerGravity) / CYCLE_EFFICIENCY;
        double oxygenLitersPerMin = 0.01141 * power + 0.435;
        double kCalPerMin = oxygenLitersPerMin * 5;
        double met = kCalPerMin * 60 / PERSON_KG;
        if(gradient > 0.) {
            met /= (1 - STATIONARY_TIME_PROPORTION);
        }
        return Math.max(0.,met - 1);
    }

    private static double getGradient(Link link) {
        if(link == null) {
            return DEFAULT_GRADIENT;
        } else {
            return Gradient.getGradient(link);
        }
    }

    // Surface factor from MATSim Bicycle Extension (Ziemke et al., 2017)
    private static double getSurfaceFactor(Link link) {
        if(link == null) {
            return 1.;
        } else {
            String surface = (String) link.getAttributes().getAttribute("surface");
            if (surface == null) {
                return 1.0;
            } else {
                return switch (surface) {
                    case "paved", "asphalt" -> 1.0;
                    case "cobblestone (bad)", "grass" -> 0.4;
                    case "cobblestone;flattened", "cobblestone:flattened", "sett", "earth" -> 0.6;
                    case "concrete", "asphalt;paving_stones:35", "compacted" -> 0.9;
                    case "concrete:lanes", "concrete_plates", "concrete:plates", "paving_stones:3" -> 0.8;
                    case "paving_stones", "paving_stones:35", "paving_stones:30", "compressed", "bricks", "stone", "pebblestone", "fine_gravel", "gravel", "ground" ->
                            0.7;
                    case "sand" -> 0.2;
                    default -> 0.5;
                };
            }
        }
    }
}
