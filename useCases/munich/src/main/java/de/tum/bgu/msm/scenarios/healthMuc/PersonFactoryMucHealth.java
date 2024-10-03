package de.tum.bgu.msm.scenarios.healthMuc;

import de.tum.bgu.msm.data.person.*;

public class PersonFactoryMucHealth implements PersonFactory {

    @Override
    public PersonHealthMuc createPerson(int id, int age,
                                  Gender gender, Occupation occupation,
                                  PersonRole role, int workplace,
                                  int income) {
        return new PersonHealthMuc(id, age, gender,
                occupation, role, workplace,
                income);
    }

    @Override
    public Person giveBirth(Person parent, int id, Gender gender) {
        PersonHealthMuc pp = new PersonHealthMuc(id, 0, gender, Occupation.TODDLER, PersonRole.CHILD, 0, 0);
        pp.setNationality(((PersonHealthMuc) parent).getNationality());
        return pp;
    }

    //TODO duplicate as well school attributes
    @Override
    public PersonHealthMuc duplicate(Person originalPerson, int id) {
        PersonHealthMuc duplicate = new PersonHealthMuc(id,
                originalPerson.getAge(),
                originalPerson.getGender(),
                originalPerson.getOccupation(),
                originalPerson.getRole(),
                -1,
                originalPerson.getAnnualIncome());
        duplicate.setDriverLicense(originalPerson.hasDriverLicense());
        duplicate.setNationality(((PersonHealthMuc)originalPerson).getNationality());
        duplicate.setSchoolId(((PersonHealthMuc) originalPerson).getSchoolId());
        return duplicate;
    }
}
