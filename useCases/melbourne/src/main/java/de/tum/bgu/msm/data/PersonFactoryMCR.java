package de.tum.bgu.msm.data;

import de.tum.bgu.msm.data.person.*;

public class PersonFactoryMCR implements PersonFactory {

    @Override
    public PersonMCR createPerson(int id, int age,
                                  Gender gender, Occupation occupation,
                                  PersonRole role, int workplace,
                                  int income) {
        return new PersonMCR(id, age, gender,
                occupation, role, workplace,
                income);
    }

    @Override
    public Person giveBirth(Person parent, int id, Gender gender) {
        PersonMCR pp = new PersonMCR(id, 0, gender, Occupation.TODDLER, PersonRole.CHILD, 0, 0);
        return pp;
    }

    //TODO duplicate as well school attributes
    @Override
    public PersonMCR duplicate(Person originalPerson, int id) {
        PersonMCR duplicate = new PersonMCR(id,
                originalPerson.getAge(),
                originalPerson.getGender(),
                originalPerson.getOccupation(),
                originalPerson.getRole(),
                -1,
                originalPerson.getAnnualIncome());
        duplicate.setDriverLicense(originalPerson.hasDriverLicense());
        duplicate.setSchoolId(((PersonMCR) originalPerson).getSchoolId());
        return duplicate;
    }
}
