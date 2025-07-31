package de.tum.bgu.msm.health;

import de.tum.bgu.msm.data.Ethnic;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.health.data.PersonHealth;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.health.disease.HealthExposures;
import de.tum.bgu.msm.schools.PersonWithSchool;

import java.util.*;

public class PersonHealthMCR implements PersonWithSchool, PersonHealth {

    private final Person delegate;

    private int schoolType = 0;
    private int schoolPlace = 0;
    private int schoolId = -1;
    private Ethnic ethnic = null;

    private float weeklyTravelSeconds = 0.f;
    private float weeklyActivityMinutes = 0.f;
    private float weeklyHomeMinutes = 0.f;
    private float[] weeklyTravelActivityHourOccupied = new float[24*7];

    //for exposure model
    private Map<Mode, Float> weeklyMarginalMetHours = new HashMap<>();
    private float weeklyMarginalMetHoursSport = 0.f;
    private Map<String, Double> weeklyAccidentRisks = new HashMap<>();
    private Map<String, float[]> weeklyExposureByPollutantByHour = new HashMap<>();
    private Map<String, Float> weeklyExposureByPollutantNormalised;

    private float[] weeklyNoiseExposureByHour = new float[24*7];
    private float weeklyNoiseExposureNormalised;
    private float noiseHighAnnoyedPercentage = 0.f;
    private float noiseHighSleepDisturbancePercentage = 0.f;
    private float weeklyNdviExposure = 0.f;
    private float weeklyNdviExposureNormalised = 0.f;
    private List<VisitedLink> visitedLinks = new ArrayList<>();


    //for disease model
    private EnumMap<HealthExposures, EnumMap<Diseases, Float>> relativeRisksByDisease = new EnumMap<>(HealthExposures.class);
    private Map<Integer, List<String>> healthDiseaseTracker = new HashMap<>();
    private List<Diseases> currentDisease = new ArrayList<>();
    private Map<Diseases, Float> currentDiseaseProb = new HashMap<>();

    // Injuries
    // private InjuryStatus injuryStatus = InjuryStatus.NO_INJURY;

    public PersonHealthMCR(int id, int age,
                           Gender gender, Occupation occupation,
                           PersonRole role, int jobId,
                           int income)  {
        delegate = new PersonImpl(id, age, gender, occupation, role, jobId, income);
    }

    @Override
    public void setSchoolType(int schoolType) {this.schoolType = schoolType; }

    @Override
    public int getSchoolType() {return schoolType;}

    @Override
    public void setSchoolPlace(int schoolPlace) {
        this.schoolPlace = schoolPlace;
    }

    @Override
    public int getSchoolPlace() {return schoolPlace;}

    @Override
    public int getSchoolId() {
        return schoolId;
    }

    @Override
    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    @Override
    public void setHousehold(Household householdId) {
        delegate.setHousehold(householdId);
    }

    @Override
    public Household getHousehold() {
        return delegate.getHousehold();
    }

    @Override
    public void setRole(PersonRole pr) {
        delegate.setRole(pr);
    }

    @Override
    public void birthday() {
        delegate.birthday();
    }

    @Override
    public void setIncome(int newIncome) {
        delegate.setIncome(newIncome);
    }

    @Override
    public void setWorkplace(int newWorkplace) {
        delegate.setWorkplace(newWorkplace);
    }

    @Override
    public void setOccupation(Occupation newOccupation) {
        delegate.setOccupation(newOccupation);
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public int getAge() {
        return delegate.getAge();
    }

    @Override
    public Gender getGender() {
        return delegate.getGender();
    }

    @Override
    public Occupation getOccupation() {
        return delegate.getOccupation();
    }

    @Override
    public int getAnnualIncome() {
        return delegate.getAnnualIncome();
    }

    @Override
    public PersonType getType() {
        return delegate.getType();
    }

    @Override
    public PersonRole getRole() {
        return delegate.getRole();
    }

    @Override
    public int getJobId() {
        return delegate.getJobId();
    }

    @Override
    public void setDriverLicense(boolean driverLicense) {
        delegate.setDriverLicense(driverLicense);
    }

    @Override
    public boolean hasDriverLicense() {
        return delegate.hasDriverLicense();
    }

    public Ethnic getEthnic() {
        return ethnic;
    }

    public void setEthnic(Ethnic ethnic) {
        this.ethnic = ethnic;
    }

    @Override
    public String toString() {
        return delegate
                +"\nSchool type               " + schoolType
                +"\nSchool place               " + schoolPlace
                +"\nSchool id    " + schoolId;
    }

    @Override
    public Optional<Object> getAttribute(String key) {
        return delegate.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        delegate.setAttribute(key, value);
    }

    @Override
    public void updateWeeklyTravelSeconds(float seconds) {
        weeklyTravelSeconds += seconds;
    }

    @Override
    public float getWeeklyTravelSeconds() {
        return weeklyTravelSeconds;
    }

    @Override
    public void updateWeeklyActivityMinutes(float minutes) {
        weeklyActivityMinutes += minutes; }

    @Override
    public float getWeeklyActivityMinutes() { return weeklyActivityMinutes; }

    @Override
    public void setWeeklyHomeMinutes(float minutes) { this.weeklyHomeMinutes = minutes; }

    @Override
    public void updateWeeklyHomeMinutes(float minutes) { this.weeklyHomeMinutes += minutes; }

    @Override
    public float getWeeklyHomeMinutes() { return weeklyHomeMinutes; }

    @Override
    public float getWeeklyMarginalMetHours(Mode mode) {
        return weeklyMarginalMetHours.getOrDefault(mode, 0.f);
    }

    @Override
    public void updateWeeklyMarginalMetHours(Mode mode, float mmetHours) {
        weeklyMarginalMetHours.put(mode, weeklyMarginalMetHours.getOrDefault(mode, 0.f) + mmetHours);
    }

    @Override
    public float getWeeklyMarginalMetHoursSport() {
        return weeklyMarginalMetHoursSport;
    }

    public void setWeeklyMarginalMetHoursSport(float weeklyMarginalMetHoursSport) {
        this.weeklyMarginalMetHoursSport = weeklyMarginalMetHoursSport;
    }

    @Override
    public Map<String, float[]> getWeeklyPollutionExposures() {
        return weeklyExposureByPollutantByHour;
    }

    @Override
    public void updateWeeklyPollutionExposuresByHour(Map<String, float[]> newExposures) {
        for (String pollutant : newExposures.keySet()) {
            if (!weeklyExposureByPollutantByHour.containsKey(pollutant)) {
                weeklyExposureByPollutantByHour.put(pollutant, newExposures.get(pollutant));
            }else {
                for(int i = 0; i< weeklyExposureByPollutantByHour.get(pollutant).length; i++) {
                    this.weeklyExposureByPollutantByHour.get(pollutant)[i] += newExposures.get(pollutant)[i];
                }
            }
        }
    }

    @Override
    public float getWeeklyExposureByPollutantNormalised(String pollutant) {
        return weeklyExposureByPollutantNormalised.get(pollutant);
    }

    @Override
    public void setWeeklyExposureByPollutantNormalised(Map<String, Float> exposureMap) {
        this.weeklyExposureByPollutantNormalised = exposureMap;
    }

    @Override
    public double getWeeklyAccidentRisk(String type) {
        return weeklyAccidentRisks.getOrDefault(type, 0.0);
    }

    @Override
    public void updateWeeklyAccidentRisks(Map<String, Double> newRisks) {
        //newRisks.forEach((k, v) -> weeklyAccidentRisks.merge(k, v, (v1, v2) -> v1 + v2 - v1*v2));
        newRisks.forEach((k, v) -> weeklyAccidentRisks.merge(k, v, (v1, v2) -> v1 + v2));
    }

    public float[] getWeeklyNoiseExposureByHour() {
        return weeklyNoiseExposureByHour;
    }

    @Override
    public void updateWeeklyNoiseExposuresByHour(float[] newExposure) {
        for(int i=0; i<newExposure.length; i++) {
            this.weeklyNoiseExposureByHour[i] += newExposure[i];
        }
    }

    @Override
    public float getWeeklyNoiseExposuresNormalised() {
        return this.weeklyNoiseExposureNormalised;
    }

    @Override
    public void setWeeklyNoiseExposuresNormalised(float noiseExposureNormalised) {
        this.weeklyNoiseExposureNormalised = noiseExposureNormalised ;
    }

    public float getWeeklyNdviExposure() {
        return weeklyNdviExposure;
    }

    @Override
    public void updateWeeklyGreenExposures(float greenExposure) {
        this.weeklyNdviExposure += greenExposure;
    }

    @Override
    public float getWeeklyGreenExposuresNormalised() {
        return this.weeklyNdviExposureNormalised;
    }

    @Override
    public void setWeeklyGreenExposuresNormalised(float greenExposureNormalised) {
        this.weeklyNdviExposureNormalised = greenExposureNormalised;
    }

    @Override
    public float[] getWeeklyTravelActivityHourOccupied() {
        return weeklyTravelActivityHourOccupied;
    }

    @Override
    public void updateWeeklyTravelActivityHourOccupied(float[] travelActivityHourOccupied) {
        for(int i=0; i<travelActivityHourOccupied.length; i++) {
            this.weeklyTravelActivityHourOccupied[i] += travelActivityHourOccupied[i];
        }
    }

    @Override
    public EnumMap<HealthExposures, EnumMap<Diseases, Float>> getRelativeRisksByDisease() {
        return relativeRisksByDisease;
    }

    public void setRelativeRisksByDisease(EnumMap<HealthExposures, EnumMap<Diseases, Float>> relativeRisksByDisease) {
        this.relativeRisksByDisease = relativeRisksByDisease;
    }

    public Map<Diseases, Float> getCurrentDiseaseProb() {
        return currentDiseaseProb;
    }

    public List<Diseases> getCurrentDisease() {
        return currentDisease;
    }

    public Map<Integer, List<String>> getHealthDiseaseTracker() {
        return healthDiseaseTracker;
    }

    public void resetHealthData(){
        weeklyTravelSeconds = 0.f;
        weeklyActivityMinutes = 0.f;
        weeklyMarginalMetHours.clear();
        weeklyAccidentRisks.clear();
        weeklyExposureByPollutantByHour.clear();
        Arrays.fill(weeklyNoiseExposureByHour,0.f);
        weeklyNdviExposure = 0.f;
        Arrays.fill(weeklyTravelActivityHourOccupied,0.f);
    }

    public float getNoiseHighAnnoyedPercentage() {
        return noiseHighAnnoyedPercentage;
    }

    public void setNoiseHighAnnoyedPercentage(float noiseHighAnnoyedPercentage) {
        this.noiseHighAnnoyedPercentage = noiseHighAnnoyedPercentage;
    }

    public float getNoiseHighSleepDisturbancePercentage() {
        return noiseHighSleepDisturbancePercentage;
    }

    public void setNoiseHighSleepDisturbancePercentage(float noiseHighSleepDisturbancePercentage) {
        this.noiseHighSleepDisturbancePercentage = noiseHighSleepDisturbancePercentage;
    }

    // For injuries Manchester
    List<VisitedLink> getVisitedLinks(){
        return visitedLinks;
    }

    void addVisitedLinks(List<VisitedLink> visitedLink){
        this.visitedLinks.addAll(visitedLink);
    }

    //For Munich
    @Override
    public void updateWeeklyPollutionExposures(Map<String, Double> exposureMap) {}

    @Override
    public void setRelativeRisks(Map<String, Float> relativeRisks) {}

    @Override
    public void setAllCauseRR(Float reduce) {}

    @Override
    public float getAllCauseRR() {
        return 0;
    }

    @Override
    public float getRelativeRiskByType(String type) {
        return 0;
    }



}
