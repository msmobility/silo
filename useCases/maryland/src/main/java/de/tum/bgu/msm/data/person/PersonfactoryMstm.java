package de.tum.bgu.msm.data.person;

public class PersonfactoryMstm implements PersonFactory {

    @Override
    public PersonMstm createPerson(int id, int age,
                                   Gender gender, Occupation occupation,
                                   PersonRole role, int workplace,
                                   int income) {
        return new PersonMstm(id, age, gender,
                occupation, role,
                workplace, income);
    }

    @Override
    public Person giveBirth(Person parent, int id, Gender gender) {
        PersonMstm pp = new PersonMstm(id, 0, gender, Occupation.TODDLER, PersonRole.CHILD, 0, 0);
        pp.setRace(((PersonMstm) parent).getRace());
        return pp;
    }

    @Override
    public PersonMstm duplicate(Person originalPerson, int nextPersonId) {
        PersonMstm duplicate = new PersonMstm(nextPersonId, originalPerson.getAge(),
                originalPerson.getGender(), originalPerson.getOccupation(),
                originalPerson.getRole(), originalPerson.getJobId(), originalPerson.getAnnualIncome());
        duplicate.setRace(((PersonMstm) originalPerson).getRace());
        duplicate.setDriverLicense(originalPerson.hasDriverLicense());
        return duplicate;
    }
}
