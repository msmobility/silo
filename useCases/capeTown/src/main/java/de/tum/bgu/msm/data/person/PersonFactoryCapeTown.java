package de.tum.bgu.msm.data.person;

public class PersonFactoryCapeTown implements PersonFactory {

    @Override
    public PersonCapeTown createPerson(int id, int age, Gender gender, Occupation occupation, PersonRole role, int workplace, int income) {
        return new PersonCapeTown(id, age, gender, occupation, role, workplace, income);
    }

    @Override
    public Person giveBirth(Person parent, int id, Gender gender) {
        PersonCapeTown personCapeTown = new PersonCapeTown(id, 0, gender, Occupation.TODDLER, PersonRole.CHILD, -1, 0);
        personCapeTown.setRace(((PersonCapeTown)parent).getRace());
        return personCapeTown;
    }

    @Override
    public Person duplicate(Person originalPerson, int nextPersonId) {
        PersonCapeTown duplicate = new PersonCapeTown(nextPersonId, originalPerson.getAge(),
                originalPerson.getGender(), originalPerson.getOccupation(),
                originalPerson.getRole(), originalPerson.getJobId(), originalPerson.getAnnualIncome());
        duplicate.setRace(((PersonCapeTown) originalPerson).getRace());
        duplicate.setDriverLicense(originalPerson.hasDriverLicense());
        return duplicate;
    }
}
