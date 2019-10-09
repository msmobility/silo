package de.tum.bgu.msm.data.person;

public class PersonFactoryMuc implements PersonFactory {

    @Override
    public PersonMuc createPerson(int id, int age,
                                  Gender gender, Occupation occupation,
                                  PersonRole role, int workplace,
                                  int income) {
        return new PersonMuc(id, age, gender,
                occupation, role, workplace,
                income);
    }

    @Override
    public Person giveBirth(Person parent, int id, Gender gender) {
        PersonMuc pp = new PersonMuc(id, 0, gender, Occupation.TODDLER, PersonRole.CHILD, 0, 0);
        pp.setNationality(((PersonMuc) parent).getNationality());
        return pp;
    }

    //TODO duplicate as well school attributes
    @Override
    public PersonMuc duplicate(Person originalPerson, int id) {
        PersonMuc duplicate = new PersonMuc(id,
                originalPerson.getAge(),
                originalPerson.getGender(),
                originalPerson.getOccupation(),
                originalPerson.getRole(),
                -1,
                originalPerson.getAnnualIncome());
        duplicate.setDriverLicense(originalPerson.hasDriverLicense());
        duplicate.setNationality(((PersonMuc)originalPerson).getNationality());
        duplicate.setSchoolId(((PersonMuc) originalPerson).getSchoolId());
        return duplicate;
    }
}
