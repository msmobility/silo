package de.tum.bgu.msm.data.person;

import de.tum.bgu.msm.data.household.Household;

public class PersonMuc implements Person {

    private final Person delegate;

    private Nationality nationality;

    private int schoolType = 0;
    private int schoolPlace = 0;
    private int schoolId = -1;

    public PersonMuc(int id, int age,
                     Gender gender, Occupation occupation,
                     PersonRole role, int jobId,
                     int income)  {
        delegate = new PersonImpl(id, age, gender, occupation, role, jobId, income);
    }

    public void setSchoolType(int schoolType) {this.schoolType = schoolType; }

    public int getSchoolType() {return schoolType;}

    public void setSchoolPlace(int schoolPlace) {
        this.schoolPlace = schoolPlace;
    }

    public int getSchoolPlace() {return schoolPlace;}

    public int getSchoolId() {
        return schoolId;
    }

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
    public int getIncome() {
        return delegate.getIncome();
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
}
