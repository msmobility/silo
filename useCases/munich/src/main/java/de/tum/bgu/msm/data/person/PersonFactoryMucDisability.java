package de.tum.bgu.msm.data.person;

public class PersonFactoryMucDisability implements PersonFactory {

    @Override
    public PersonMucDisability createPerson(int id, int age,
                                  Gender gender, Occupation occupation,
                                  PersonRole role, int workplace,
                                  int income) {
        return new PersonMucDisability(id, age, gender,
                occupation, role, workplace,
                income);
    }

    @Override
    public Person giveBirth(Person parent, int id, Gender gender) {
        PersonMucDisability pp = new PersonMucDisability(id, 0, gender, Occupation.TODDLER, PersonRole.CHILD, 0, 0);
        pp.setNationality(((PersonMucDisability) parent).getNationality());
        pp.setDisability(((PersonMucDisability) parent).getDisability());
        return pp;
    }

    //TODO duplicate as well school attributes
    @Override
    public PersonMucDisability duplicate(Person originalPerson, int id) {
        PersonMucDisability duplicate = new PersonMucDisability(id,
                originalPerson.getAge(),
                originalPerson.getGender(),
                originalPerson.getOccupation(),
                originalPerson.getRole(),
                -1,
                originalPerson.getAnnualIncome());
        duplicate.setDriverLicense(originalPerson.hasDriverLicense());
        duplicate.setNationality(((PersonMucDisability)originalPerson).getNationality());
        duplicate.setDisability(((PersonMucDisability) originalPerson).getDisability());
        return duplicate;
    }
}
