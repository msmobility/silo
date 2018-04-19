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
package de.tum.bgu.msm.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Greg Erhardt 
 * Created on Dec 2, 2009
 *
 */
public final class Household {

    // Note: if attributes are edited, remember to edit attributes for inmigrants in \relocation\ImOutMigration\setupInOutMigration.java and \relocation\ImOutMigration\inmigrateHh.java as well
    private final int hhId;
    private int dwellingId;
    private Race race;
    private Nationality nationality;
    private int autos;
    private HouseholdType type;
    private final List<Person> persons;

    Household(int id, int dwellingID, int autos) {
        this.hhId = id;
        this.dwellingId = dwellingID;
        this.autos = autos;
        persons = new ArrayList<>();
    }

    public int getId() {
        return hhId;
    }

    public int getHhSize() {
        return persons.size();
    }

    public int getDwellingId() {
        return dwellingId;
    }

    public int getAutos() {
        return autos;
    }

    public List<Person> getPersons(){
        return Collections.unmodifiableList(persons);
    }

    public HouseholdType getHouseholdType() {
        return type;
    }

    public int getHhIncome () {
        int hhInc = 0;
        for (Person pp : persons) {
            hhInc += pp.getIncome();
        }
        return hhInc;
    }

    public Race getRace() {return race; }

    public Nationality getNationality() {
        return nationality;
    }

    public int getNumberOfWorkers () {
        int wrk = 0;
        for (Person pp: persons) if (pp.getOccupation() == 1) wrk++;
        return wrk;
    }

    public int getHHLicenseHolders () {
        int lic = 0;
        for (Person pp: persons) if (pp.hasDriverLicense()) lic++;
        return lic;
    }

    public void setDwelling (int id) {
        this.dwellingId = id;
    }

    void addPerson(Person person) {
        this.persons.add(person);
    }

    void removePerson(Person person) {
        this.persons.remove(person);
    }

    public void setType() {
        int incCat = HouseholdDataManager.getIncomeCategoryForIncome(getHhIncome());
        this.type = HouseholdDataManager.defineHouseholdType(persons.size(), incCat);
    }

    public void determineHouseholdRace() {
        Race householdRace = null;
        for (Person pp: persons) {
            if(householdRace == null) {
                householdRace = pp.getRace();
            } else if (pp.getRace() != householdRace) {
                this.race = Race.other;
                return;
            }
        }
        this.race = householdRace;
    }

    public void setAutos (int autos) {
        this.autos = autos;
    }

    public boolean checkIfOnlyChildrenRemaining() {
        if(persons.isEmpty()) {
            return false;
        }
        for (Person person: persons) {
           if(person.getAge() >= 16) {
               return false;
           }
        }
        return true;
    }

    @Override
    public String toString() {
        return  "Attributes of household " + hhId
            +"\nDwelling ID             " + dwellingId
            +"\nHousehold size          " + persons.size();
    }
}
