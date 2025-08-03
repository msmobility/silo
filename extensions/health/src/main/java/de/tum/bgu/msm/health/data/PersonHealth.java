package de.tum.bgu.msm.health.data;

import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.health.disease.HealthExposures;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public interface PersonHealth extends Person {

    void updateWeeklyTravelSeconds(float seconds);

    float getWeeklyTravelSeconds();

    void updateWeeklyActivityMinutes(float minutes);

    float getWeeklyActivityMinutes();

    void setWeeklyHomeMinutes(float minutes);

    void updateWeeklyHomeMinutes(float minutes);

    float getWeeklyHomeMinutes();

    float getWeeklyMarginalMetHoursSport();

    float getWeeklyMarginalMetHours(Mode mode);

    void updateWeeklyMarginalMetHours(Mode mode, float mmetHours);

    Map<String, float[]> getWeeklyPollutionExposures();

    void updateWeeklyPollutionExposuresByHour(Map<String, float[]> newExposures);
   
    float getWeeklyExposureByPollutantNormalised(String pollutant);

    void setWeeklyExposureByPollutantNormalised(Map<String, Float> exposureMap);

    float getWeeklyAccidentRisk(String type);

    void updateWeeklyAccidentRisks(Map<String, Float> newRisks);

    float[] getWeeklyNoiseExposureByHour();

    void updateWeeklyNoiseExposuresByHour(float[] newExposure);

    float getWeeklyNoiseExposuresNormalised();

    void setWeeklyNoiseExposuresNormalised(float noiseExposureNormalised);

    void updateWeeklyGreenExposures(float newExposure);

    float getWeeklyGreenExposuresNormalised();

    void updateWeeklyTravelActivityHourOccupied(float[] hourOccupied);

    void setWeeklyGreenExposuresNormalised(float greenExposureNormalised);

    float[] getWeeklyTravelActivityHourOccupied();

    float getAllCauseRR();

    float getRelativeRiskByType(String type);

    EnumMap<HealthExposures, EnumMap<Diseases, Float>> getRelativeRisksByDisease();

    List<Diseases> getCurrentDisease();

    Map<Integer, List<String>> getHealthDiseaseTracker();

    Map<Diseases, Float> getCurrentDiseaseProb();

    void resetHealthData();

    //TODO: these are methods currently used for Munich health, need to adapt Munich health model
    void updateWeeklyPollutionExposures(Map<String, Float> exposureMap);

    void setRelativeRisks(Map<String, Float> relativeRisks);

    void setAllCauseRR(Float reduce);
}
