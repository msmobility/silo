package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.data.person.Nationality;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.vehicle.Vehicle;
import de.tum.bgu.msm.data.vehicle.VehicleType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HouseholdMuc implements Household {

    private final HouseholdImpl delegate;
    private Nationality nationality;
    private int autonomous = 0;

    public HouseholdMuc(int id, int dwellingId, int autos) {
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
    }

    @Override
    public void setDwelling(int id) {
        delegate.setDwelling(id);
    }

    @Override
    public void addPerson(Person person) {
        delegate.addPerson(person);
        defineHouseholdNationality();
    }

    @Override
    public void removePerson(int personId) {
        delegate.removePerson(personId);
        defineHouseholdNationality();
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

//    public void setAutonomous(int autonomous){
//        this.autonomous = autonomous;
//    }

//    public int getAutonomous(){
//        return autonomous;
//    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    public Nationality getNationality() {
        return nationality;
    }

    private void defineHouseholdNationality() {
        Nationality householdNationaliy = Nationality.OTHER;
        //for (Person pp : getPersons().values()) {
        //    if (householdNationaliy == null) {
         //       householdNationaliy = ((PersonMuc)pp).getNationality();
        //    } else if ((p).getNationality() != householdNationaliy) {
        //        nationality = Nationality.OTHER;
        //        return;
         //   }
        //}
        nationality = Nationality.OTHER;
    }

}
