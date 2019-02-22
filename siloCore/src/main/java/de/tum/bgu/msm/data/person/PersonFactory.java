package de.tum.bgu.msm.data.person;

public interface PersonFactory {
    Person createPerson(int id, int age, Gender gender, Race race, Occupation occupation, PersonRole role,
                        int workplace, int income);

    Person duplicate(Person originalPerson, int nextPersonId);
}
