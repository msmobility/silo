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

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.models.demography.BirthModel;

import java.util.*;

/**
 * @author Greg Erhardt 
 * Created on Dec 2, 2009
 *
 */
public final class Household {

    private static final Map<Integer, Household> householdMap = new HashMap<>();
    // Note: if attributes are edited, remember to edit attributes for inmigrants in \relocation\ImOutMigration\setupInOutMigration.java and \relocation\ImOutMigration\inmigrateHh.java as well
    private final int hhId;
    private int dwellingId;
    private Race race;
    private Nationality nationality;
    private int autos;
    private HouseholdType type;
    private final List<Person> persons;

    public Household(int id, int dwellingID, int autos) {
        this.hhId = id;
        this.dwellingId = dwellingID;
        this.autos = autos;
        persons = new ArrayList<>();
        householdMap.put(id,this);
    }

    public static Household getHouseholdFromId(int householdId) {
        return householdMap.get(householdId);
    }

    public static int getHouseholdCount() {
        return householdMap.size();
    }

    public static Collection<Household> getHouseholds() {
        return Collections.unmodifiableCollection(householdMap.values());
    }

    public static void saveHouseholds (Household[] hhs) {
        for (Household hh: hhs) householdMap.put(hh.getId(), hh);
    }

    public static void remove (int hhID) {
        householdMap.remove(hhID);
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
        return persons;
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

    public int getHomeZone() {
        Dwelling dwelling = Dwelling.getDwellingFromId(this.dwellingId);
        if(dwelling != null) {
            return dwelling.getZone();
        } else {
            return -1;
        }
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

    public void removePerson (Person person, SiloDataContainer dataContainer) {
        persons.remove(person);
        if(!persons.isEmpty()) {
            setType();
            determineHouseholdRace();
        } else {
            dataContainer.getHouseholdData().removeHousehold(hhId);
        }
        if (hhId == SiloUtil.trackHh || person.getId() == SiloUtil.trackPp) {
            SiloUtil.trackWriter.println("Person " +
                    person.getId() + " was removed from household " + hhId + ".");
        }
    }

    public void addPerson(Person person) {
        // add existing person per (not a newborn child) to household
        if(persons.contains(person)) {
            throw new IllegalArgumentException("Person " + person.getId() + " was already added to household " + this.getId());
        }
        persons.add(person);
        person.setHousehold(this);
        setType();
        determineHouseholdRace();
        if (person.getId() == SiloUtil.trackPp || hhId == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("A person " +
                    "(not a child) named " + person.getId() + " was added to household " + hhId + ".");
        }
    }

    public void addNewbornPerson(Race race) {
        // create new Person for this household
        int id = HouseholdDataManager.getNextPersonId();
        int gender = 1;
        if (SiloUtil.getRandomNumberAsDouble() <= BirthModel.getProbabilityForGirl()) {
            gender = 2;
        }
        Person person = new Person (id, 0, gender, race, 0, 0, 0);
        person.setRole(PersonRole.CHILD);
        persons.add(person);
        person.setHousehold(this);
        setType();
        if (id == SiloUtil.trackPp || hhId == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("For unto us a child was born... A child named "
                    + id + " was born and added to household " + hhId + ".");
        }
    }

    public static int getTotalPopulation () {
        int tp = 0;
        for (Household hh: getHouseholds()) {
            tp += hh.getHhSize();
        }
        return tp;
    }

    public static float getAverageHouseholdSize () {
        float ahs = 0;
        int cnt = 0;
        for (Household hh: getHouseholds()) {
            ahs += hh.getHhSize();
            cnt++;
        }
        return ahs/(float) cnt;
    }

    public MitoHousehold convertToMitoHh(MitoZone zone) {
        return new MitoHousehold(hhId, getHhIncome(), autos, zone);
    }

    public static Map<Integer, MitoHousehold> convertHhs(Map<Integer, MitoZone> zones) {
        Map<Integer, MitoHousehold> thhs = new HashMap<>();
        for (Household siloHousehold : getHouseholds()) {
            MitoZone zone = zones.get(Dwelling.getDwellingFromId(siloHousehold.getDwellingId()).getZone());
            MitoHousehold household = siloHousehold.convertToMitoHh(zone);
            thhs.put(household.getId(), household);
        }
        return thhs;
    }

    public boolean checkIfOnlyChildrenRemaining() {
        for (Person person: persons) {
           if(person.getAge() >= 16) {
               return false;
           }
        }
        return true;
    }

    @Override
    public String toString() {
        int homeZone = -1;
        final Dwelling dwelling = Dwelling.getDwellingFromId(dwellingId);
        if(dwelling != null) {
            homeZone = dwelling.getZone();
        }
        return  "Attributes of household " + hhId
            +"\nDwelling ID             " + dwellingId
            +"\nHousehold size          " + persons.size()
            +"\nHome zone               " + homeZone ;
    }
}
