package de.tum.bgu.msm.health.data;

import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.person.*;

import java.util.HashMap;
import java.util.Map;

public interface PersonHealth extends Person {


    float getWeeklyMarginalMetHours(Mode mode);

    void updateWeeklyMarginalMetHours(Mode mode, float mmetHours);

    void updateWeeklyTravelSeconds(float seconds);
    void updateWeeklyActivityMinutes(float minutes);

    float getWeeklyTravelSeconds();
    float getWeeklyActivityMinutes();

    void setWeeklyHomeMinutes(float hours);

    float getWeeklyHomeMinutes();

    float getWeeklyExposureByPollutant(String pollutant);

    // todo: make not hardcoded...
    // 1.49/3 is the "minimum" weekly ventilation rate (8hr sleep + 16hr rest per day)
    float getWeeklyExposureByPollutantNormalised(String pollutant);

    float getWeeklyAccidentRisk(String type);

    void updateWeeklyAccidentRisks(Map<String, Float> newRisks);

    void updateWeeklyPollutionExposures(Map<String, Float> newExposures);

    float getAllCauseRR();

    void setAllCauseRR(float rr);

    float getRelativeRiskByType(String type);

    public void setRelativeRisks(Map<String, Float> relativeRisks);


}
