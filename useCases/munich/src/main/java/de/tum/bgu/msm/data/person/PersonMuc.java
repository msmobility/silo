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
    private double weeklyHomeHours = 0.;

    //for health model
    private Map<Mode, Double> weeklyPhysicalActivityMmetHours = new HashMap<>();
    private double weeklyLightInjuryRisk = 0.;
    private double weeklySevereInjuryRisk = 0.;
    private double weeklyFatalityInjuryRisk = 0.;
    private Map<String, Double> weeklyExposureByPollutant = new HashMap<>();
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

    public void updateWeeklyPhysicalActivityMmetHours(Mode mode, double mmetHours) {
        if(weeklyPhysicalActivityMmetHours.get(mode) == null) {
            weeklyPhysicalActivityMmetHours.put(mode,mmetHours);
        } else {
            weeklyPhysicalActivityMmetHours.put(mode, weeklyPhysicalActivityMmetHours.get(mode) + mmetHours);
        }
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

    public void setWeeklyHomeHours(double hours) { this.weeklyHomeHours = hours; }

    public double getWeeklyHomeHours() { return weeklyHomeHours; }

    public double getWeeklyPhysicalActivityMmetHours(Mode mode) {
        return weeklyPhysicalActivityMmetHours.getOrDefault(mode,0.);
    }

    public double getWeeklyLightInjuryRisk() {
        return weeklyLightInjuryRisk;
    }

    public void updateWeeklyLightInjuryRisk(double weeklyLightInjuryRisk) {
        this.weeklyLightInjuryRisk = 1 - ((1 - this.weeklyLightInjuryRisk) * (1 - weeklyLightInjuryRisk));
    }

    public double getWeeklySevereInjuryRisk() {
        return weeklySevereInjuryRisk;
    }

    public void updateWeeklySevereInjuryRisk(double weeklySevereInjuryRisk) {
        this.weeklySevereInjuryRisk = 1 - ((1 - this.weeklySevereInjuryRisk) * (1 - weeklySevereInjuryRisk));
    }

    public Double getWeeklyExposureByPollutant(String pollutant) {
        return weeklyExposureByPollutant.get(pollutant);
    }

    public void updateWeeklyPollutionExposures(Map<String, Double> newExposures) {
        newExposures.forEach((k, v) -> weeklyExposureByPollutant.merge(k, v, Double::sum));
    }

    public double getAllCauseRR() {return allCauseRR;}

    public void setAllCauseRR(double RR) {this.allCauseRR = RR;}

    public double getWeeklyFatalityInjuryRisk() {
        return weeklyFatalityInjuryRisk;
    }

    public void updateWeeklyFatalityInjuryRisk(double weeklyFatalityInjuryRisk) {
        this.weeklyFatalityInjuryRisk = 1 - ((1 - this.weeklyFatalityInjuryRisk) * (1 - weeklyFatalityInjuryRisk));
    }
}
