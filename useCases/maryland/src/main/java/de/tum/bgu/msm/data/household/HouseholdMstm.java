package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.data.person.PersonMstm;
import de.tum.bgu.msm.data.person.Race;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.vehicle.*;
import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class HouseholdMstm implements Household {

    private final HouseholdImpl delegate;
    private Race race;

    public HouseholdMstm(int id, int dwellingId, int autos) {
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
        return (int) this.delegate.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.CAR)).count();
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

    public Race getRace() {
        return race;
    }

    private void defineHouseholdRace() {
        Race householdRace = null;
        for (Person pp : getPersons().values()) {
            if (householdRace == null) {
                householdRace = ((PersonMstm) pp).getRace();
            } else if (((PersonMstm) pp).getRace() != householdRace) {
                race = Race.other;
            }
        }
        race = householdRace;
    }
}
