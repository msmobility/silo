package de.tum.bgu.msm.data;

import de.tum.bgu.msm.data.person.*;

public class PersonFactoryMEL implements PersonFactory {

    @Override
    public PersonMEL createPerson(int id, int age,
                                  Gender gender, Occupation occupation,
                                  PersonRole role, int workplace,
                                  int income) {
        return new PersonMEL(id, age, gender,
                occupation, role, workplace,
                income);
    }

    @Override
    public Person giveBirth(Person parent, int id, Gender gender) {
        PersonMEL pp = new PersonMEL(id, 0, gender, Occupation.TODDLER, PersonRole.CHILD, 0, 0);
        return pp;
    }

    //TODO duplicate as well school attributes
    @Override
    public PersonMEL duplicate(Person originalPerson, int id) {
        PersonMEL duplicate = new PersonMEL(id,
                originalPerson.getAge(),
                originalPerson.getGender(),
                originalPerson.getOccupation(),
                originalPerson.getRole(),
                -1,
                originalPerson.getAnnualIncome());
        duplicate.setDriverLicense(originalPerson.hasDriverLicense());
        duplicate.setSchoolId(((PersonMEL) originalPerson).getSchoolId());
        return duplicate;
    }
}
