/*
 * Copyright  2005 PB Consult Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.vehicle.*;
import org.matsim.utils.objectattributes.attributable.Attributes;
import org.matsim.utils.objectattributes.attributable.AttributesImpl;

import java.util.*;


/**
 * @author Greg Erhardt
 * Created on Dec 2, 2009
 */
public class HouseholdImpl implements Household {

    // Note: if attributes are edited, remember to edit attributes for inmigrants in \relocation\ImOutMigration\setupInOutMigration.java and \relocation\ImOutMigration\inmigrateHh.java as well
    private final int hhId;
    private int dwellingId;

    private int autos;
    private HouseholdType type;

    private final Map<Integer, Person> persons;

    private final Attributes attributes = new AttributesImpl();

    private final List<Vehicle> vehicles = new ArrayList<>();

    public HouseholdImpl(int id, int dwellingID, int autos) {
        this.hhId = id;
        this.dwellingId = dwellingID;
        this.autos = autos;
        persons = new LinkedHashMap<>(10);
        for (int i = 0; i < autos; i++) {
            vehicles.add(new Car(i, CarType.CONVENTIONAL, VehicleUtil.getVehicleAgeInBaseYear()));
        }
    }

    @Override
    public int getId() {
        return hhId;
    }

    @Override
    public int getHhSize() {
        return persons.size();
    }

    @Override
    public int getDwellingId() {
        return dwellingId;
    }

    @Override
    public int getAutos() {
        return (int) this.vehicles.stream().filter(vv-> vv.getType().equals(VehicleType.CAR)).count();
    }

    @Override
    public Map<Integer, ? extends Person> getPersons() {
        return Collections.unmodifiableMap(persons);
    }

    @Override
    public HouseholdType getHouseholdType() {
        return type;
    }

    @Override
    public void updateHouseholdType() {
        this.type = HouseholdUtil.defineHouseholdType(this);
    }

    @Override
    public void setDwelling(int id) {
        this.dwellingId = id;
    }

    @Override
    public void addPerson(Person person) {
        if (person != null) {
            this.persons.put(person.getId(), person);
            updateHouseholdType();
        }
    }

    @Override
    public void removePerson(int personId) {
        this.persons.remove(personId);
        updateHouseholdType();
    }

    @Override
    public void setAutos(int autos) {
        vehicles.clear();
        for (int i = 0; i< autos; i++){
            vehicles.add(new Car(VehicleUtil.getHighestVehicleIdInHousehold(this), CarType.CONVENTIONAL, VehicleUtil.getVehicleAgeInBaseYear()));
        }
    }

    @Override
    public Optional<Object> getAttribute(String key) {
        return Optional.ofNullable(attributes.getAttribute(key));
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.putAttribute(key, value);
    }

    @Override
    public String toString() {
        return "Attributes of household " + hhId
                + "\nDwelling ID             " + dwellingId
                + "\nHousehold size          " + persons.size();
    }

    @Override
    public int hashCode() {
        return hhId;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Household && ((Household) o).getId() == this.hhId;
    }

    @Override
    public List<Vehicle> getVehicles() {
        return vehicles;
    }
}
