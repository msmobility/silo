package de.tum.bgu.msm.health.data;

import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.properties.Properties;

public class PollutionExposure {

    // Background Rates
    private static final double BACKGROUND_PM25 = Properties.get().healthData.BACKGROUND_PM25;
    private static final double BACKGROUND_NO2 = Properties.get().healthData.BACKGROUND_NO2;

    // Other Constants
    private static final double BASE_LEVEL_INHALATION_RATE = 1.;  // from METAHIT
    private static final double SLEEP_VENTILATION_RATE = 0.27; // from Tainio et al., 2016
    private static final double REST_VENTILATION_RATE = 0.61; // from Tainio et al., 2016
    private static final double LIGHTACTIVITY_VENTILATION_RATE = 1.125; // using 2.25 MET ((1+ 1.25)/2), see the transport MET to ventilation rate function
    private static final double OTHERSPORTS_VENTILATION_RATE = 2; // 5mMET average intensity of physical activity
    private static final int SLEEP_START = 24; //aligned with noise night penalty
    private static final int SLEEP_END = 7;//aligned with noise night penalty

    public static double getHomeExposurePm25(double minutesAtHome, int dayHour ,double locationIncremental) {
        double minutesResting = 0.;
        double minutesSleeping = 0.;

        if(dayHour >= SLEEP_START || dayHour <= SLEEP_END){
            minutesSleeping = minutesAtHome;
        } else {
            minutesResting = minutesAtHome;
        }
        return (BACKGROUND_PM25 + locationIncremental) * (REST_VENTILATION_RATE * minutesResting + SLEEP_VENTILATION_RATE * minutesSleeping) / 60.;
    }

    public static double getHomeExposureNo2(double minutesAtHome, int dayHour, double locationIncremental) {
        double minutesResting = 0.;
        double minutesSleeping = 0.;

        if(dayHour >= SLEEP_START || dayHour <= SLEEP_END){
            minutesSleeping = minutesAtHome;
        } else {
            minutesResting = minutesAtHome;
        }

        return (BACKGROUND_NO2 + locationIncremental) * (REST_VENTILATION_RATE * minutesResting + SLEEP_VENTILATION_RATE * minutesSleeping) / 60.;
    }

    public static double getActivityExposurePm25(double activityMinutes, double locationIncremental) {
        return (BACKGROUND_PM25 + locationIncremental) * REST_VENTILATION_RATE * activityMinutes / 60.;
    }

    public static double getActivityExposureNo2(double activityMinutes,double locationIncremental) {
        return (BACKGROUND_NO2 + locationIncremental)  * REST_VENTILATION_RATE * activityMinutes / 60.;
    }

    // Link Exposures
    public static double getLinkExposurePm25(Mode mode, double linkPm25, double linkSeconds, double linkMarginalMet) {

        double modeExposureFactor = 0.;
        switch(mode) {
            case autoDriver:
            case autoPassenger:
                modeExposureFactor = 2.5;
                break;
            case walk:
            case bus:
                modeExposureFactor = 1.9;
                break;
            case bicycle:
                modeExposureFactor = 2.;
                break;
        }

        double ventilationRate = BASE_LEVEL_INHALATION_RATE + linkMarginalMet / 2.;

        return (BACKGROUND_PM25 + linkPm25) * modeExposureFactor * ventilationRate * linkSeconds / 3600.;
    }

    public static double getLinkExposureNo2(Mode mode, double linkNo2, double linkSeconds, double linkMarginalMet) {

        double modeExposureFactor = 0.;
        switch(mode) {
            case autoDriver:
            case autoPassenger:
                modeExposureFactor = 8.6;
                break;
            case bus:
            case bicycle:
                modeExposureFactor = 4.5;
                break;
            case walk:
                modeExposureFactor = 3.0;
                break;
        }

        double ventilationRate = BASE_LEVEL_INHALATION_RATE + linkMarginalMet / 2.;

        return (BACKGROUND_NO2 + linkNo2) * modeExposureFactor * ventilationRate * linkSeconds / 3600.;
    }

    // For Munich
    public static double getHomeExposurePm25(double minutesAtHome) {
        double minutesResting = Math.max(0,minutesAtHome - 3360);
        double minutesSleeping = minutesAtHome - minutesResting;
        return BACKGROUND_PM25 * (REST_VENTILATION_RATE * minutesResting + SLEEP_VENTILATION_RATE * minutesSleeping) / 60.;
    }

    public static double getHomeExposureNo2(double minutesAtHome) {
        double minutesResting = Math.max(0,minutesAtHome - 3360);
        double minutesSleeping = minutesAtHome - minutesResting;
        return BACKGROUND_NO2 * (REST_VENTILATION_RATE * minutesResting + SLEEP_VENTILATION_RATE * minutesSleeping) / 60.;
    }

    // Activity Exposures
    public static double getActivityExposurePm25(double activityMinutes) {
        return BACKGROUND_PM25 * REST_VENTILATION_RATE * activityMinutes / 60.;
    }

    public static double getActivityExposureNo2(double activityMinutes) {
        return BACKGROUND_NO2 * REST_VENTILATION_RATE * activityMinutes / 60.;
    }

        // new ventilation rate
    public static double getHomeExposurePm25_newvent(double minutesAtHome, int dayHour ,  double locationIncremental) {
        double minutesResting = 0.;
        double minutesSleeping = 0.;

        if(dayHour >= SLEEP_START || dayHour <= SLEEP_END){
            minutesSleeping = minutesAtHome;
        } else {
            minutesResting = minutesAtHome;
        }
        return (BACKGROUND_PM25 + locationIncremental) * (REST_VENTILATION_RATE * minutesResting * (1-0.328)+ LIGHTACTIVITY_VENTILATION_RATE * minutesResting* 0.328 + SLEEP_VENTILATION_RATE * minutesSleeping) / 60.;
    }

    public static double getHomeExposureNo2_newvent(double minutesAtHome, int dayHour, double locationIncremental) {
        double minutesResting = 0.;
        double minutesSleeping = 0.;

        if(dayHour >= SLEEP_START || dayHour <= SLEEP_END){
            minutesSleeping = minutesAtHome;
        } else {
            minutesResting = minutesAtHome;
        }
        return (BACKGROUND_NO2 + locationIncremental) * (REST_VENTILATION_RATE * minutesResting * (1-0.328)+ LIGHTACTIVITY_VENTILATION_RATE * minutesResting* 0.328 + SLEEP_VENTILATION_RATE * minutesSleeping) / 60.;
    }

    public static double getActivityExposurePm25_newvent(double activityMinutes,  double otherSportsMETh, double locationIncremental) {
        double otherSportsdailymin = ((otherSportsMETh/3.)/7.)*60.;
        double othersportsfraction = otherSportsdailymin/194.; // 194 min is the average time spent on activities.
        if (othersportsfraction>1) {
            othersportsfraction = 1;
        }
        return (BACKGROUND_PM25 + locationIncremental) * ((OTHERSPORTS_VENTILATION_RATE * activityMinutes * othersportsfraction) + (LIGHTACTIVITY_VENTILATION_RATE *  activityMinutes * (1-othersportsfraction )))/ 60.;
    }

    public static double getActivityExposureNo2_newvent(double activityMinutes, double otherSportsMETh, double locationIncremental) {
        double otherSportsdailymin = ((otherSportsMETh/3.)/7.)*60.;
        double othersportsfraction = otherSportsdailymin/194.; // 194 min is the average time spent on activities.
        if (othersportsfraction>1) {
            othersportsfraction = 1;
        }
        return (BACKGROUND_NO2 + locationIncremental)  * ((OTHERSPORTS_VENTILATION_RATE * activityMinutes * othersportsfraction) + (LIGHTACTIVITY_VENTILATION_RATE *  activityMinutes * (1-othersportsfraction )))/ 60.;
    }

}
