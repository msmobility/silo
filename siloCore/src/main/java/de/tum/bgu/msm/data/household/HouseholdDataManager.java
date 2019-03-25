package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonFactory;
import de.tum.bgu.msm.models.ModelUpdateListener;

import java.util.Collection;

public interface HouseholdDataManager extends ModelUpdateListener {

    float getAverageIncome(Gender gender, int age, Occupation occupation);

    Household getHouseholdFromId(int householdId);

    Collection<Household> getHouseholds();

    Person getPersonFromId(int id);

    void removePerson(int id);

    Collection<Person> getPersons();

    void removePersonFromHousehold(Person person);

    void addPersonToHousehold(Person person, Household household);

    void removeHousehold(int householdId);

    int getNextHouseholdId();

    int getNextPersonId();

    int getHighestHouseholdIdInUse();

    int getHighestPersonIdInUse();

    /**
     * Creates and saves a memento for the given household by duplicating its current state. A household will
     * only be saved once per year. This implies that the memento of a household will, at the end of the year,
     * contain the state of the household before the first call to this method in the given year. See also
     * https://www.tutorialspoint.com/design_pattern/memento_pattern.htm
     *
     * @param hh
     */
    void saveHouseholdMemento(Household hh);

    /**
     * Returns the memento states of all households for the current year up to this point.
     * @return
     */
    Collection<Household> getHouseholdMementos();

    void addPerson(Person person);

    void addHousehold(Household household);

    PersonFactory getPersonFactory();

    HouseholdFactory getHouseholdFactory();

    Household duplicateHousehold(Household original);
}
