package de.tum.bgu.msm.data.person;

public class PersonFactoryTak implements PersonFactory {

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
                originalPerson.getAnnualIncome());
        duplicate.setDriverLicense(originalPerson.hasDriverLicense());
        duplicate.setSchoolId(((PersonTak) originalPerson).getSchoolId());
        return duplicate;
    }
}
