package de.tum.bgu.msm.health.noise;

public class NoiseMetrics {

    private static final int PENALTY_DAY = 0;
    private static final int PENALTY_EVENING = 5;
    private static final int PENALTY_NIGHT = 10;

    public static double getHourlyNoiseLevel(int hour, double noiseRp) {

        if(hour > 19  && hour <= 23 ){
            return Math.pow(10, (noiseRp + PENALTY_EVENING) / 10);
        }else if (hour <= 7  || hour > 23 ){
            return Math.pow(10, (noiseRp + PENALTY_NIGHT) / 10);
        }else{
            return Math.pow(10, (noiseRp + PENALTY_DAY) / 10);
        }
    }

    public static double getHighAnnoyedPercentage(float Lden) {
        //noise %HA high annoyed (Guski et al., 2017)
        return (78.9270 - 3.1162 * Lden + 0.0342 * Lden * Lden);
    }

    public static double getHighSleepDisturbancePercentage(float Lnight) {

        //%HSD high sleep disturbance (Basner & McGuire, 2018)
        return (19.4312 - 0.9336 * Lnight + 0.0126 * Lnight * Lnight);
    }
}
