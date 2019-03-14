package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.container.Data;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonFactory;

import java.util.Collection;
import java.util.List;

public interface HouseholdDataManager extends Data {

    float getMedianIncome(int msa);

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

    void addHouseholdAboutToChange(Household hh);

    List<Household> getUpdatedHouseholds();

    void addPerson(Person person);

    void addHousehold(Household household);

    PersonFactory getPersonFactory();

    HouseholdFactory getHouseholdFactory();

    Household duplicateHousehold(Household original);
}
