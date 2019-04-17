package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.data.person.Person;

import java.util.Collection;

public interface HouseholdData {
    Household getHousehold(int householdId);

    Collection<Household> getHouseholds();

    Person getPerson(int id);

    void removePerson(int id);

    Collection<Person> getPersons();

    void removeHousehold(int householdId);

    void addPerson(Person person);

    void addHousehold(Household household);
}
