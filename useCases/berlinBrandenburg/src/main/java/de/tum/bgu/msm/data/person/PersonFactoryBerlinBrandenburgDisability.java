package de.tum.bgu.msm.data.person;

public class PersonFactoryBerlinBrandenburgDisability implements PersonFactory {

    @Override
    public PersonBerlinBrandenburgDisability createPerson(int id, int age,
                                                          Gender gender, Occupation occupation,
                                                          PersonRole role, int workplace,
                                                          int income) {
        return new PersonBerlinBrandenburgDisability(id, age, gender,
                occupation, role, workplace,
                income);
    }

    @Override
    public Person giveBirth(Person parent, int id, Gender gender) {
        PersonBerlinBrandenburgDisability pp = new PersonBerlinBrandenburgDisability(id, 0, gender, Occupation.TODDLER, PersonRole.CHILD, 0, 0);
        pp.setNationality(((PersonBerlinBrandenburgDisability) parent).getNationality());
        pp.setDisability(((PersonBerlinBrandenburgDisability) parent).getDisability());
        return pp;
    }

    //TODO duplicate as well school attributes
    @Override
    public PersonBerlinBrandenburgDisability duplicate(Person originalPerson, int id) {
        PersonBerlinBrandenburgDisability duplicate = new PersonBerlinBrandenburgDisability(id,
                originalPerson.getAge(),
                originalPerson.getGender(),
                originalPerson.getOccupation(),
                originalPerson.getRole(),
                -1,
                originalPerson.getAnnualIncome());
        duplicate.setDriverLicense(originalPerson.hasDriverLicense());
        duplicate.setNationality(((PersonBerlinBrandenburgDisability)originalPerson).getNationality());
        duplicate.setDisability(((PersonBerlinBrandenburgDisability) originalPerson).getDisability());
        return duplicate;
    }
}
