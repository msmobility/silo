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

    private double weeklyTravelSeconds = 0.;
    private double weeklyActivityMinutes = 0.;
    private double weeklyHomeMinutes = 0.;

    //for health model
    private Map<Mode, Double> weeklyMarginalMetHours = new HashMap<>();
    private Map<String, Double> weeklyAccidentRisks = new HashMap<>();
    private Map<String, Double> weeklyExposureByPollutant = new HashMap<>();
    private Map<String, Double> relativeRisks;
    private double allCauseRR;




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
        return delegate.toString()
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

    public double getWeeklyMarginalMetHours(Mode mode) {
        return weeklyMarginalMetHours.getOrDefault(mode, 0.);
    }

    public void updateWeeklyMarginalMetHours(Mode mode, double mmetHours) {
        weeklyMarginalMetHours.put(mode, weeklyMarginalMetHours.getOrDefault(mode, 0.) + mmetHours);
    }

    public void updateWeeklyTravelSeconds(double seconds) {
        weeklyTravelSeconds += seconds;
    }
    public void updateWeeklyActivityMinutes(double minutes) {
        weeklyActivityMinutes += minutes; }

    public double getWeeklyTravelSeconds() {
        return weeklyTravelSeconds;
    }
    public double getWeeklyActivityMinutes() { return weeklyActivityMinutes; }

    public void setWeeklyHomeMinutes(double hours) { this.weeklyHomeMinutes = hours; }

    public double getWeeklyHomeMinutes() { return weeklyHomeMinutes; }

    public Double getWeeklyExposureByPollutant(String pollutant) {
        return weeklyExposureByPollutant.get(pollutant);
    }

    // todo: make not hardcoded...
    public double getWeeklyExposureByPollutantNormalised(String pollutant) {
        if(pollutant.equals("pm2.5")) {
            return weeklyExposureByPollutant.get(pollutant) * 0.008511726 - 6.018974;
        } else if(pollutant.equals("no2")) {
            return weeklyExposureByPollutant.get(pollutant) * 0.008599 - 12.07159;
        } else return 0;
    }

    public double getWeeklyAccidentRisk(String type) {
        return weeklyAccidentRisks.getOrDefault(type, 0.);
    }

    public void updateWeeklyAccidentRisks(Map<String, Double> newRisks) {
        newRisks.forEach((k, v) -> weeklyAccidentRisks.merge(k, v, (v1, v2) -> v1 + v2 - v1*v2));
    }

    public void updateWeeklyPollutionExposures(Map<String, Double> newExposures) {
        newExposures.forEach((k, v) -> weeklyExposureByPollutant.merge(k, v, Double::sum));
    }

    public double getAllCauseRR() {return allCauseRR;}

    public void setAllCauseRR(double rr) {this.allCauseRR = rr;}

    public Double getRelativeRiskByType(String type) {
        return relativeRisks.get(type);
    }

    public void setRelativeRisks(Map<String, Double> relativeRisks) {
        this.relativeRisks = relativeRisks;
    }
}
