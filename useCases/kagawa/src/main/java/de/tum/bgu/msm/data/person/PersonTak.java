package de.tum.bgu.msm.data.person;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.schools.PersonWithSchool;

public final class PersonTak implements PersonWithSchool {

    private final Person delegate;

    private int schoolType = 0;
    private int schoolPlace = 0;
    private int schoolId = -1;

    public PersonTak(int id, int age, Gender gender, Occupation occupation,
                     PersonRole role, int jobId, int income) {
        delegate = new PersonImpl(id, age, gender, occupation, role, jobId, income);
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

    public static class PersonFactoryTak implements PersonFactory {

        @Override
        public PersonTak createPerson(int id, int age,
                                      Gender gender, Occupation occupation,
                                      PersonRole role, int workplace,
                                      int income) {
            return new PersonTak(id, age, gender,
                    occupation, role, workplace,
                    income);
        }

        @Override
        public Person giveBirth(Person parent, int id, Gender gender) {
            PersonTak pp = new PersonTak(id, 0, gender, Occupation.TODDLER, PersonRole.CHILD, 0, 0);
            return pp;
        }

        //TODO duplicate as well school attributes
        @Override
        public PersonTak duplicate(Person originalPerson, int id) {
            PersonTak duplicate = new PersonTak(id,
                    originalPerson.getAge(),
                    originalPerson.getGender(),
                    originalPerson.getOccupation(),
                    originalPerson.getRole(),
                    -1,
                    originalPerson.getIncome());
            duplicate.setDriverLicense(originalPerson.hasDriverLicense());
            duplicate.setSchoolId(((PersonTak) originalPerson).getSchoolId());
            return duplicate;
        }
    }
}
