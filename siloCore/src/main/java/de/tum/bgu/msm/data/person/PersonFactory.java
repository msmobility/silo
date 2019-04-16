package de.tum.bgu.msm.data.person;

public interface PersonFactory {
    Person createPerson(int id, int age, Gender gender, Occupation occupation, PersonRole role,
                        int workplace, int income);

    Person giveBirth(Person parent, int id, Gender gender);

    Person duplicate(Person originalPerson, int nextPersonId);
}
