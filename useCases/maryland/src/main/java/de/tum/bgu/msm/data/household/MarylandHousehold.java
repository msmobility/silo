package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.data.person.MarylandPerson;
import de.tum.bgu.msm.data.person.Race;
import de.tum.bgu.msm.data.person.Person;

import java.util.Map;


public class MarylandHousehold implements Household {

    private final HouseholdImpl delegate;
    private Race race;

    public MarylandHousehold(int id, int dwellingId, int autos) {
        delegate = new HouseholdImpl(id, dwellingId, autos);
    }

    @Override
    public int getHhSize() {
        return delegate.getHhSize();
    }

    @Override
    public int getDwellingId() {
        return delegate.getDwellingId();
    }

    @Override
    public int getAutos() {
        return delegate.getAutos();
    }

    @Override
    public Map<Integer, ? extends Person> getPersons() {
        return delegate.getPersons();
    }

    @Override
    public HouseholdType getHouseholdType() {
        return delegate.getHouseholdType();
    }

    @Override
    public void updateHouseholdType() {
        delegate.updateHouseholdType();
        defineHouseholdRace();
    }

    @Override
    public void setDwelling(int id) {
        delegate.setDwelling(id);
    }

    @Override
    public void addPerson(Person person) {
        delegate.addPerson(person);
        defineHouseholdRace();
    }

    @Override
    public void removePerson(int personId) {
        delegate.removePerson(personId);
        defineHouseholdRace();
    }

    @Override
    public void setAutos(int autos) {
        delegate.setAutos(autos);
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    public Race getRace() {
        return race;
    }

    private void defineHouseholdRace() {
        Race householdRace = null;
        for (Person pp : getPersons().values()) {
            if (householdRace == null) {
                householdRace = ((MarylandPerson) pp).getRace();
            } else if (((MarylandPerson) pp).getRace() != householdRace) {
                race = Race.other;
            }
        }
        race = householdRace;
    }
}
