package de.tum.bgu.msm.data.person;

public class PersonFactoryBerlinBrandenburg implements PersonFactory {

    @Override
    public PersonBerlinBrandenburg createPerson(int id, int age,
                                                Gender gender, Occupation occupation,
                                                PersonRole role, int workplace,
                                                int income) {
        return new PersonBerlinBrandenburg(id, age, gender,
                occupation, role, workplace,
                income);
    }

    @Override
    public Person giveBirth(Person parent, int id, Gender gender) {
        PersonBerlinBrandenburg pp = new PersonBerlinBrandenburg(id, 0, gender, Occupation.TODDLER, PersonRole.CHILD, 0, 0);
        pp.setNationality(((PersonBerlinBrandenburg) parent).getNationality());
        return pp;
    }

    //TODO duplicate as well school attributes
    @Override
    public PersonBerlinBrandenburg duplicate(Person originalPerson, int id) {
        PersonBerlinBrandenburg duplicate = new PersonBerlinBrandenburg(id,
                originalPerson.getAge(),
                originalPerson.getGender(),
                originalPerson.getOccupation(),
                originalPerson.getRole(),
                -1,
                originalPerson.getAnnualIncome());
        duplicate.setDriverLicense(originalPerson.hasDriverLicense());
        duplicate.setNationality(((PersonBerlinBrandenburg)originalPerson).getNationality());
        duplicate.setSchoolId(((PersonBerlinBrandenburg) originalPerson).getSchoolId());
        return duplicate;
    }
}
