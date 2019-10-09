package de.tum.bgu.msm.data.person;

public class PersonFactoryImpl implements PersonFactory {

    @Override
    public PersonImpl createPerson(int id, int age, Gender gender, Occupation occupation, PersonRole role,
                               int workplace, int income) {
        return new PersonImpl(id, age, gender, occupation, role, workplace, income);
    }

    @Override
    public Person giveBirth(Person parent, int id, Gender gender) {
        return new PersonImpl(id, 0, gender, Occupation.TODDLER, PersonRole.CHILD, 0, 0);
    }

    @Override
    public PersonImpl duplicate(Person originalPerson, int id) {
        PersonImpl duplicate = new PersonImpl(id,
                originalPerson.getAge(),
                originalPerson.getGender(),
                originalPerson.getOccupation(),
                originalPerson.getRole(),
                -1,
                originalPerson.getAnnualIncome());
        duplicate.setDriverLicense(originalPerson.hasDriverLicense());
        return duplicate;
    }
}
