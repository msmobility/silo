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

import de.tum.bgu.msm.data.person.Nationality;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.Race;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author Greg Erhardt 
 * Created on Dec 2, 2009
 *
 */
public class HouseholdImpl implements Household {

    // Note: if attributes are edited, remember to edit attributes for inmigrants in \relocation\ImOutMigration\setupInOutMigration.java and \relocation\ImOutMigration\inmigrateHh.java as well
    private final int hhId;
    private int dwellingId;

    private Race race;
    private Nationality nationality;
    private int autos;
    private HouseholdType type;
    private int autonomous = 0;

    private final Map<Integer, Person> persons;

    public HouseholdImpl(int id, int dwellingID, int autos) {
        this.hhId = id;
        this.dwellingId = dwellingID;
        this.autos = autos;
        persons = new LinkedHashMap<>(10);
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
        return autos;
    }

    @Override
    public Map<Integer, ? extends Person> getPersons(){
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
    public Race getRace() {return race; }

    @Override
    public Nationality getNationality() {
        return nationality;
    }

    @Override
    public void setDwelling(int id) {
        this.dwellingId = id;
    }

    @Override
    public void addPerson(Person person) {
        if(person != null) {
            this.persons.put(person.getId(), person);
            update();
        }
    }

    @Override
    public void removePerson(Integer personId) {
        this.persons.remove(personId);
    }

    @Override
    public void setAutos(int autos) {
        this.autos = autos;
    }

    @Override
    public String toString() {
        return  "Attributes of household " + hhId
            +"\nDwelling ID             " + dwellingId
            +"\nHousehold size          " + persons.size();
    }

    @Override
    public void setAutonomous(int autonomous){
        this.autonomous = autonomous;
    }

    @Override
    public int getAutonomous(){
        return autonomous;
    }

    private void update() {
        this.race = HouseholdUtil.defineHouseholdRace(this);
        this.nationality = HouseholdUtil.defineHouseholdNationality(this);
        updateHouseholdType();
    }
}
