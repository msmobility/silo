package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.data.Id;
import de.tum.bgu.msm.data.person.Nationality;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.Race;

import java.util.Map;

public interface Household extends Id {

    int getHhSize();

    int getDwellingId();

    int getAutos();

    Map<Integer, ? extends Person> getPersons();

    HouseholdType getHouseholdType();

    void updateHouseholdType();

    Race getRace();

    Nationality getNationality();

    void setDwelling(int id);

    void addPerson(Person person);

    void removePerson(Integer personId);

    void setAutos(int autos);

    void setAutonomous(int autonomous);

    int getAutonomous();
}
