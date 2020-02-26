package de.tum.bgu.msm.data.person;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.schools.PersonWithSchool;

import java.util.Optional;

public class PersonMucDisability implements PersonWithSchool {

    private final PersonMuc delegate;

    private Disability disability;

    PersonMucDisability(int id, int age,
                        Gender gender, Occupation occupation,
                        PersonRole role, int jobId,
                        int income)  {
        delegate = new PersonMuc(id, age, gender, occupation, role, jobId, income);
    }

    @Override
    public void setSchoolType(int schoolType) {delegate.setSchoolType(schoolType);}

    @Override
    public int getSchoolType() {return delegate.getSchoolType();}

    @Override
    public void setSchoolPlace(int schoolPlace) {
        delegate.setSchoolPlace(schoolPlace);
    }

    @Override
    public int getSchoolPlace() {return delegate.getSchoolPlace();}

    @Override
    public int getSchoolId() {
        return delegate.getSchoolId();
    }

    @Override
    public void setSchoolId(int schoolId) {
        delegate.setSchoolId(schoolId);
    }

    public void setNationality(Nationality nationality) {
         delegate.setNationality(nationality);
    }

    public Nationality getNationality() {
        return delegate.getNationality();
    }


    public Disability getDisability() {
        return disability;
    }

    public void setDisability(Disability disability) {
        this.disability = disability;
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
    public Optional<Object> getAttribute(String key) {
        return delegate.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        delegate.setAttribute(key, value);
    }
}
