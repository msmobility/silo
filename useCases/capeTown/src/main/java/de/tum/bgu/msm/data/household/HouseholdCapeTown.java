package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonCapeTown;
import de.tum.bgu.msm.data.person.RaceCapeTown;
import de.tum.bgu.msm.data.vehicle.Vehicle;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HouseholdCapeTown implements Household {

    private final HouseholdImpl delegate;
    private RaceCapeTown race;

    public HouseholdCapeTown(int id, int dwellingId, int autos) {
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
    public Optional<Object> getAttribute(String key) {
        return delegate.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        delegate.setAttribute(key, value);
    }

    @Override
    public List<Vehicle> getVehicles() {
        return delegate.getVehicles();
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    public RaceCapeTown getRace() {
        return race;
    }

    private void defineHouseholdRace() {
        RaceCapeTown householdRace = null;
        for (Person pp : getPersons().values()) {
            if (householdRace == null) {
                householdRace = ((PersonCapeTown) pp).getRace();
            } else if (((PersonCapeTown) pp).getRace() != householdRace) {
                race = RaceCapeTown.OTHER;
            }
        }
        race = householdRace;
    }
}
