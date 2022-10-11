package de.tum.bgu.msm.data.person.household;

import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdImpl;
import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonTak;
import de.tum.bgu.msm.data.vehicle.Vehicle;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HouseholdTak implements Household {

    private final HouseholdImpl delegate;
    private int autonomous = 0;

    public HouseholdTak(int id, int dwellingId, int autos) {
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
    }

    @Override
    public void setDwelling(int id) {
        delegate.setDwelling(id);
    }

    @Override
    public void addPerson(Person person) {
        delegate.addPerson(person);
    }

    @Override
    public void removePerson(int personId) {
        delegate.removePerson(personId);
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

    public void setAutonomous(int autonomous){
        this.autonomous = autonomous;
    }

    public int getAutonomous(){
        return autonomous;
    }

    @Override
    public int getId() {
        return delegate.getId();
    }


}
