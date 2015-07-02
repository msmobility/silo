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
package edu.umd.ncsg.data;

import edu.umd.ncsg.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.io.Serializable;

import edu.umd.ncsg.SiloModel;
import edu.umd.ncsg.demography.BirthModel;

/**
 * @author Greg Erhardt 
 * Created on Dec 2, 2009
 *
 */
public class Household implements Serializable {

    static Logger logger = Logger.getLogger(Household.class);

    private static final Map<Integer, Household> householdMap = new HashMap<>();
    // Note: if attributes are edited, remember to edit attributes for inmigrants in \relocation\ImOutMigration\setupInOutMigration.java and \relocation\ImOutMigration\inmigrateHh.java as well
    private int hhId;
    private int dwellingId;
    private int hhSize;
    private Race race;
    private int autos;
    private int homeZone;
    private HouseholdType type;
    private Person[] persons;


    public Household(int id, int dwellingID, int homeZone, int hhSize, int autos) {
        this.hhId = id;
        this.dwellingId = dwellingID;
        this.homeZone = homeZone;
        this.hhSize = hhSize;
        this.autos = autos;
        persons = new Person[hhSize];
        householdMap.put(id,this);
    }


    public static Household[] getHouseholdArray() {
        return householdMap.values().toArray(new Household[householdMap.size()]);
    }


    public static Household getHouseholdFromId(int householdId) {
        return householdMap.get(householdId);
    }


    public static int getHouseholdCount() {
        return householdMap.size();
    }


    public static Collection<Household> getHouseholds() {
//        Collection<Household> households = null;
//        for (Household household: households) {
//        }
        return householdMap.values();
    }


    public static void saveHouseholds (Household[] hhs) {
        for (Household hh: hhs) householdMap.put(hh.getId(), hh);
    }


    public static void remove (int hhID) {
        householdMap.remove(hhID);
    }


    public void logAttributes () {
        logger.info("Attributes of household " + hhId);
        logger.info("Dwelling ID             " + dwellingId);
        logger.info("Household size          " + hhSize);
        logger.info("Home zone               " + homeZone);
        logger.info("Household race          " + race);
        for (Person pp: persons) logger.info("Member of hh is person  " + pp.getId());
    }

    public void logAttributes (PrintWriter pw) {
        pw.println ("Attributes of household " + hhId);
        pw.println ("Dwelling ID             " + dwellingId);
        pw.println ("Household size          " + hhSize);
        pw.println ("Home zone               " + homeZone);
        // cannot log person attributes or race, because when households are read (and logged) persons are not known yet
    }


    public int getId() {
        return hhId;
    }

    public int getHhSize() {
        return hhSize;
    }

    public int getDwellingId() {
        return dwellingId;
    }

    public int getAutos() {
        return autos;
    }

    public Person[] getPersons(){
        return persons;
    }

    public HouseholdType getHouseholdType() {
        return type;
    }

    public int getHhIncome () {
        // return annual household income
        int hhInc = 0;
        try {
            for (Person pp : persons) hhInc += pp.getIncome();
        } catch (Exception e) {
            logger.info("While attempting to calculate the income of household " + hhId + ", an error occurred.");
            logger.info("Error: " + e);
            System.exit(1);
        }
        return hhInc;
    }

    public int getHomeZone() {
        return homeZone;
    }

    public Race getRace() {return race; }

    public int getNumberOfWorkers () {
        int wrk = 0;
        for (Person pp: persons) if (pp.getOccupation() == 1) wrk++;
        return wrk;
    }

    public void setDwelling (int id) {
        this.dwellingId = id;
        setHomeZone(Dwelling.getDwellingFromId(id).getZone());
    }

    public void setHomeZone (int zone) {
        this.homeZone = zone;
    }


    public void setType() {
        int incCat = HouseholdDataManager.getIncomeCategoryForIncome(getHhIncome());
        this.type = HouseholdDataManager.defineHouseholdType(hhSize, incCat);
    }


    public void setHouseholdRace() {
        // define race of household
        Person[] pps = getPersons();
        Race householdRace = pps[0].getRace();
        if (getHhSize() > 1) {
            for (Person pp: pps) if (pp.getRace() != householdRace) householdRace = Race.other;
        }
        this.race = householdRace;
    }


    public void setAutos (int autos) {
        this.autos = autos;
    }


    public void addPersonForInitialSetup(Person per){
        // This method adds a person to the household without increasing the HH size. Only used for initial setup

        for (int i = 0; i < getHhSize(); i++) {
            if (persons[i] == null) {
                persons[i] = per;
                persons[i].setHhId(hhId);
                return;
            }
        }
        logger.fatal("Found more persons for household " + hhId + " than household size (" + getHhSize() + ") allows.");
    }


    public void removePerson (Person per) {
        // remove this person from household and reduce household size by one
        if (hhSize >= 2) {
            Person[] remainingPersons = new Person[persons.length - 1];
            int counter = 0;
            for (Person pers: persons) {
                if (pers.getId() != per.getId()) {
                    remainingPersons[counter] = pers;
                    counter++;
                }
            }
            persons = remainingPersons;
            hhSize -= 1;
            setType();
            setHouseholdRace();
        } else {
            HouseholdDataManager.removeHousehold(hhId);
        }
        if (hhId == SiloUtil.trackHh || per.getId() == SiloUtil.trackPp) SiloUtil.trackWriter.println("Person " +
                per.getId() + " was removed from household " + hhId + ".");
    }


    public void addAdultPerson(Person per) {
        // add existing person per (not a newborn child) to household

        Person[] newPersons = new Person[persons.length + 1];
        System.arraycopy(persons, 0, newPersons, 0, persons.length);
        newPersons[persons.length] = per;
        persons = newPersons;
        hhSize++;
        per.setHhId(hhId);
        setType();
        setHouseholdRace();
        if (per.getId() == SiloUtil.trackPp || hhId == SiloUtil.trackHh) SiloUtil.trackWriter.println("A person " +
                "(not a child) named " + per.getId() + " was added to household " + hhId + ".");
    }


    public void addNewbornPerson(Race race) {
        // create new Person for this household
        int id = HouseholdDataManager.getNextPersonId();
        int gender = 1;
        if (SiloModel.rand.nextDouble() <= BirthModel.getProbabilityForGirl()) gender = 2;
        Person per = new Person (id, hhId, 0, gender, race, 0, 0, 0);
        per.setRole(PersonRole.child);
        Person previousPers[] = getPersons();
        Person newPers[] = new Person[previousPers.length+1];
        System.arraycopy(previousPers, 0, newPers, 0, previousPers.length);
        newPers[previousPers.length] = per;
        persons = newPers;
        hhSize++;
        per.setHhId(hhId);
        setType();
        if (id == SiloUtil.trackPp || hhId == SiloUtil.trackHh) SiloUtil.trackWriter.println("A child named "
                + id + " was born and added to household " + hhId + ".");
    }


    public static int getTotalPopulation () {
        int tp = 0;
        for (Household hh: getHouseholdArray()) tp += hh.getHhSize();
        return tp;
    }


    public static float getAverageHouseholdSize () {
        float ahs = 0;
        int cnt = 0;
        for (Household hh: getHouseholdArray()) {
            ahs += hh.getHhSize();
            cnt++;
        }
        return ahs/(float) cnt;
    }
}
