package de.tum.bgu.msm.data.person;

public class PersonfactoryMstm implements PersonFactory {

    @Override
    public MarylandPerson createPerson(int id, int age,
                                       Gender gender, Occupation occupation,
                                       PersonRole role, int workplace,
                                       int income) {
        return new MarylandPerson(id, age, gender,
                occupation, role,
                workplace, income);
    }

    @Override
    public Person giveBirth(Person parent, int id, Gender gender) {
        MarylandPerson pp = new MarylandPerson(id, 0, gender, Occupation.TODDLER, PersonRole.CHILD, 0, 0);
        pp.setRace(((MarylandPerson) parent).getRace());
        return pp;
    }

    @Override
    public MarylandPerson duplicate(Person originalPerson, int nextPersonId) {
        MarylandPerson duplicate = new MarylandPerson(originalPerson.getId(), originalPerson.getAge(),
                originalPerson.getGender(), originalPerson.getOccupation(),
                originalPerson.getRole(), originalPerson.getJobId(), originalPerson.getIncome());
        duplicate.setRace(((MarylandPerson) originalPerson).getRace());
        duplicate.setDriverLicense(originalPerson.hasDriverLicense());
        return duplicate;
    }
}
