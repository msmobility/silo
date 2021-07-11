package de.tum.bgu.msm.scenarios.health;

import de.tum.bgu.msm.data.Mode;
import org.matsim.contrib.emissions.Pollutant;

public class PollutionExposure {

    // Background Rates
    private static final double BACKGROUND_PM25 = 9.;
    private static final double BACKGROUND_NO2 = 17.6;

    // Other Constants
    private static final double BASE_LEVEL_INHALATION_RATE = 1.;
    private static final double CLOSED_WINDOW_PM_RATIO = 0.5;
    private static final double CLOSED_WINDOW_RATIO = 0.5;
    private static final double ROAD_RATIO_MAX = 3.216;
    private static final double ROAD_RATIO_SLOPE = 0.379;

    public static double[] getActivityExposures(double activityMinutes) {
        double ventilationRate = BASE_LEVEL_INHALATION_RATE + 0.167;

        double exposurePm25 = BACKGROUND_PM25 * ventilationRate * activityMinutes / 60.;
        double exposureNo2 = BACKGROUND_NO2 * ventilationRate * activityMinutes / 60.;
        return new double[] {exposurePm25, exposureNo2};
    }

    public static double[] getLinkExposures(Mode mode, double linkPm25, double linkNo2, double linkSeconds, double linkMarginalMet) {
        double concentrationPm25 = BACKGROUND_PM25 + linkPm25;
        double concentrationNo2 = BACKGROUND_NO2 + linkNo2;

        // Ventilation rates
        double ventilationRate = BASE_LEVEL_INHALATION_RATE + linkMarginalMet / 2.;

        // On road off road ratios
        double onRoadOffRoadRatioPm25 = ROAD_RATIO_MAX - ROAD_RATIO_SLOPE * Math.log(concentrationPm25);
        double onRoadOffRoadRatioNo2 = ROAD_RATIO_MAX - ROAD_RATIO_SLOPE * Math.log(concentrationNo2);

        // Exposure ratios
        double exposureRatioPm25;
        double exposureRatioNo2;
        if(mode.equals(Mode.walk) || mode.equals(Mode.bicycle)) {
            exposureRatioPm25 = onRoadOffRoadRatioPm25;
            exposureRatioNo2 = onRoadOffRoadRatioNo2;
        } else {
            exposureRatioPm25 = (1 - CLOSED_WINDOW_RATIO) * onRoadOffRoadRatioPm25 + CLOSED_WINDOW_RATIO * CLOSED_WINDOW_PM_RATIO;
            exposureRatioNo2 = (1 - CLOSED_WINDOW_RATIO) * onRoadOffRoadRatioNo2 + CLOSED_WINDOW_RATIO * CLOSED_WINDOW_PM_RATIO;
        }

        double exposurePm25 = concentrationPm25 * exposureRatioPm25 * ventilationRate * linkSeconds / 3600.;
        double exposureNo2 = concentrationNo2 * exposureRatioNo2 * ventilationRate * linkSeconds / 3600.;

        return new double[] {exposurePm25, exposureNo2};
    }

}
