package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.data.person.Person;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HouseholdDataImpl implements HouseholdData {

    private final Map<Integer, Person> persons = new ConcurrentHashMap<>();
    private final Map<Integer, Household> households = new ConcurrentHashMap<>();


    @Override
    public Household getHousehold(int householdId) {
        return households.get(householdId);
    }

    @Override
    public Collection<Household> getHouseholds() {
        return households.values();
    }

    @Override
    public Person getPerson(int id) {
        return persons.get(id);
    }

    @Override
    public void removePerson(int id) {
        persons.remove(id);
    }

    @Override
    public Collection<Person> getPersons() {
        return persons.values();
    }

    @Override
    public void removeHousehold(int householdId) {
        households.remove(householdId);
    }

    @Override
    public void addPerson(Person person) {
        persons.put(person.getId(), person);
    }

    @Override
    public void addHousehold(Household household) {
        households.put(household.getId(), household);
    }
}
