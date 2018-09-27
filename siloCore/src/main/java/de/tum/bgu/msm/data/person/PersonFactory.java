package de.tum.bgu.msm.data.person;

import de.tum.bgu.msm.data.Occupation;

public interface PersonFactory {
    Person createPerson(int id, int age, Gender gender, Race race, Occupation occupation, int workplace, int income);
}
