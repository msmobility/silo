package de.tum.bgu.msm.data.person;

public class PersonFactoryImpl implements PersonFactory {

    @Override
    public PersonImpl createPerson(int id, int age, Gender gender, Race race, Occupation occupation, PersonRole role,
                               int workplace, int income) {
        return new PersonImpl(id, age, gender, race, occupation, role, workplace, income);
    }

    @Override
    public PersonImpl duplicate(Person originalPerson, int id) {
        PersonImpl duplicate = new PersonImpl(id,
                originalPerson.getAge(),
                originalPerson.getGender(),
                originalPerson.getRace(),
                originalPerson.getOccupation(),
                originalPerson.getRole(),
                -1,
                originalPerson.getIncome());
        duplicate.setDriverLicense(originalPerson.hasDriverLicense());
        duplicate.setEducationLevel(originalPerson.getEducationLevel());
        duplicate.setNationality(originalPerson.getNationality());
        return duplicate;
    }
}
