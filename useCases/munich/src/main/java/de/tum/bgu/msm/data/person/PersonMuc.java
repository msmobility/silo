package de.tum.bgu.msm.data.person;

import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.schools.PersonWithSchool;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PersonMuc implements PersonWithSchool {

    private final Person delegate;

    private Nationality nationality;

    private int schoolType = 0;
    private int schoolPlace = 0;
    private int schoolId = -1;

    private float weeklyTravelSeconds = 0.f;
    private float weeklyActivityMinutes = 0.f;
    private float weeklyHomeMinutes = 0.f;

    //for health model
    private final Map<Mode, Float> weeklyMarginalMetHours = new HashMap<>();
    private final Map<String, Float> weeklyAccidentRisks = new HashMap<>();
    private final Map<String, Float> weeklyExposureByPollutant = new HashMap<>();
    private Map<String, Float> relativeRisks;
    private float allCauseRR;




    public PersonMuc(int id, int age,
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

    public void setNationality(Nationality nationality) {
        this.nationality = nationality;
    }

    public Nationality getNationality() {
        return nationality;
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

    @Override
    public String toString() {
        return delegate
                +"\nNationality                  " + nationality
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

    public float getWeeklyMarginalMetHours(Mode mode) {
        return weeklyMarginalMetHours.getOrDefault(mode, 0.f);
    }

    public void updateWeeklyMarginalMetHours(Mode mode, float mmetHours) {
        weeklyMarginalMetHours.put(mode, weeklyMarginalMetHours.getOrDefault(mode, 0.f) + mmetHours);
    }

    public void updateWeeklyTravelSeconds(float seconds) {
        weeklyTravelSeconds += seconds;
    }
    public void updateWeeklyActivityMinutes(float minutes) {
        weeklyActivityMinutes += minutes; }

    public float getWeeklyTravelSeconds() {
        return weeklyTravelSeconds;
    }
    public float getWeeklyActivityMinutes() { return weeklyActivityMinutes; }

    public void setWeeklyHomeMinutes(float hours) { this.weeklyHomeMinutes = hours; }

    public float getWeeklyHomeMinutes() { return weeklyHomeMinutes; }

    public float getWeeklyExposureByPollutant(String pollutant) {
        return weeklyExposureByPollutant.get(pollutant);
    }

    // The "minimum" weekly ventilation over the week (56hr sleep + 112hr rest per week)
    public float getWeeklyExposureByPollutantNormalised(String pollutant) {
        return (float) (weeklyExposureByPollutant.get(pollutant) / (56*0.27 + 112*0.61));
    }

    public float getWeeklyAccidentRisk(String type) {
        return weeklyAccidentRisks.getOrDefault(type, 0.f);
    }

    public void updateWeeklyAccidentRisks(Map<String, Float> newRisks) {
        newRisks.forEach((k, v) -> weeklyAccidentRisks.merge(k, v, (v1, v2) -> v1 + v2 - v1*v2));
    }

    public void updateWeeklyPollutionExposures(Map<String, Float> newExposures) {
        newExposures.forEach((k, v) -> weeklyExposureByPollutant.merge(k, v, Float::sum));
    }

    public float getAllCauseRR() {return allCauseRR;}

    public void setAllCauseRR(float rr) {this.allCauseRR = rr;}

    public float getRelativeRiskByType(String type) {
        return relativeRisks.get(type);
    }

    public void setRelativeRisks(Map<String, Float> relativeRisks) {
        this.relativeRisks = relativeRisks;
    }
}
