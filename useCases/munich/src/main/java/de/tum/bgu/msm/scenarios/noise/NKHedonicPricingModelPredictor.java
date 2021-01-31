package de.tum.bgu.msm.scenarios.noise;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.matsim.noise.NoiseDwelling;

import java.util.function.Supplier;

public class NKHedonicPricingModelPredictor implements HedonicPricingModelPredictor {

    private static double INTERCEPT = 2.2390;

    private static double BETA_LOG_AREA = 0.7706;

    private static double BETA_LOW_NOISE = 0;
    private static double BETA_MEDIUM_NOISE = -0.0360;
    private static double BETA_LOUD_NOISE = -0.0583;
    private static double BETA_VERY_LOUD_NOISE = -0.1005;

    private static double BETA_ACCESSIBILITY = 0.2165;
    private static double BETA_PARKING_AVAILABLE = 0.0180;

    private static double BETA_LUXURY_QUALITY = 0.1678;
    private static double BETA_SUPERIOR_QUALITY = 0;
    private static double BETA_AVERAGE_QUALITY = -0.1430;

    private static double BETA_FIRST_TIME_USE_STATE = 0;
    private static double BETA_NEW_BUILDING_STATE = -0.0298;
    private static double BETA_FIRST_TIME_USE_AFTER_RESTORATION_STATE = -0.0815;
    private static double BETA_RESTORED_STATE = -0.1020;
    private static double BETA_MODERNIZED_STATE = -0.1171;
    private static double BETA_WELL_KEPT_STATE = -0.1580;
    private static double BETA_RENOVATED_STATE = -0.1697;

    ////////////////////////////////

    private final static double INTERCEPT_AREA = 8.1063;
    private final static double BETA_ROOMS_AREA = 26.0538;

    @Override
    public double predictPrice(Dwelling dwelling) {

        double logPrice = INTERCEPT;

        logPrice += BETA_LOG_AREA * estimateArea(dwelling);

        final double noiseImmission = ((NoiseDwelling) dwelling).getNoiseImmission();
        if(noiseImmission < 55) {
            logPrice += BETA_LOW_NOISE;
        } else if (noiseImmission < 65){
            logPrice += BETA_MEDIUM_NOISE;
        } else if(noiseImmission < 75) {
            logPrice += BETA_LOUD_NOISE;
        } else {
            logPrice += BETA_VERY_LOUD_NOISE;
        }

        try {
            logPrice += BETA_ACCESSIBILITY * dwelling.getAttribute("matsim_accessibility").map(o ->(Double) o).orElseThrow((Supplier<Throwable>) RuntimeException::new);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        logPrice += BETA_PARKING_AVAILABLE * dwelling.getAttribute("parking_available").map(o -> (Integer) o).orElse(0);
        logPrice += convertQualityToRentCoefficient(dwelling.getQuality());
        logPrice += getStateImpact(dwelling);

        return Math.exp(logPrice);
    }

    private double getStateImpact(Dwelling dwelling) {
        final NKHedonicPricingModelState state = (NKHedonicPricingModelState) dwelling.getAttribute("state").get();
        switch (state) {
            case FIRST_TIME_USE_STATE:
                return BETA_FIRST_TIME_USE_STATE;
            case NEW_BUILDING_STATE:
                return BETA_NEW_BUILDING_STATE;
            case FIRST_TIME_USE_AFTER_RESTORATION_STATE:
                return BETA_FIRST_TIME_USE_AFTER_RESTORATION_STATE;
            case RESTORED_STATE:
                return BETA_RESTORED_STATE;
            case MODERNIZED_STATE:
                return BETA_MODERNIZED_STATE;
            case WELL_KEPT_STATE:
                return BETA_WELL_KEPT_STATE;
            case RENOVATED_STATE:
                return BETA_RENOVATED_STATE;
            default:
                throw new RuntimeException();
        }
    }

    private double convertQualityToRentCoefficient(int qualityLevel) {
        if(qualityLevel < 3) {
            return BETA_AVERAGE_QUALITY;
        } else if(qualityLevel < 4) {
            return BETA_SUPERIOR_QUALITY;
        } else {
            return BETA_LUXURY_QUALITY;
        }
    }

    private double estimateArea(Dwelling dwelling) {
        double area = INTERCEPT_AREA + BETA_ROOMS_AREA * dwelling.getBedrooms();
        return area;
    }
}
