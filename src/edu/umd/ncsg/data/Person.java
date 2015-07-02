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

import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.*;
import java.io.Serializable;

/**
 * @author Greg Erhardt 
 * Created on Dec 2, 2009
 *
 */
public class Person implements Serializable {

    static Logger logger = Logger.getLogger(Person.class);

    private static final Map<Integer,Person> personMap = new HashMap<>();
    // Note: if attributes are edited, remember to edit attributes for inmigrants in \relocation\InOutMigration\setupInOutMigration.java and \relocation\InOutMigration\inmigrateHh.java as well
    int id;
	int hhid;
	int age;
	int gender;
	Race race;
	int occupation;
    int workplace;
	int income;
	Household hh;
	PersonType type;
    PersonRole role;
//    private Lock lock = new ReentrantLock();



    public Person(int id, int hhid, int age, int gender, Race race, int occupation, int workplace, int income) {
		this.id = id;
		this.hhid = hhid; 
		this.age = age; 
		this.gender = gender; 
		this.race = race;
		this.occupation = occupation;
        this.workplace = workplace;
		this.income = income; 
        this.hh = Household.getHouseholdFromId(hhid);
		setType(age, gender);
        personMap.put(id,this);
    }
    
    public static Person getPersonFromId(int id) {
        return personMap.get(id);
    }

    public static void removePerson(int id) {
        personMap.remove(id);
    }

    public static int getPersonCount() {
        return personMap.size();
    }


    public static Collection<Person> getPersons() {
//        Collection<Person> persons = null;
//        for (Person person : persons) {
//        }
//
        return personMap.values();
    }


    public static void savePersons (Person[] pps) {
        for (Person pp: pps) personMap.put(pp.getId(), pp);
    }


    public static Person[] getPersonArray() {
        return personMap.values().toArray(new Person[personMap.size()]);
    }


     public void setType (int age, int gender) {
        if (gender == 1) {
        if (age==0) type = PersonType.menAge0;
        else if (age<=4) type = PersonType.menAge1to4;
        else if (age<=9) type = PersonType.menAge5to9;
		else if (age<=14) type = PersonType.menAge10to14;
        else if (age<=19) type = PersonType.menAge15to19;
        else if (age<=24) type = PersonType.menAge20to24;
        else if (age<=29) type = PersonType.menAge25to29;
        else if (age<=34) type = PersonType.menAge30to34;
        else if (age<=39) type = PersonType.menAge35to39;
        else if (age<=44) type = PersonType.menAge40to44;
        else if (age<=49) type = PersonType.menAge45to49;
        else if (age<=54) type = PersonType.menAge50to54;
        else if (age<=59) type = PersonType.menAge55to59;
        else if (age<=64) type = PersonType.menAge60to64;
        else if (age<=69) type = PersonType.menAge65to69;
        else if (age<=74) type = PersonType.menAge70to74;
        else if (age<=79) type = PersonType.menAge75to79;
        else if (age<=84) type = PersonType.menAge80to84;
        else if (age<=89) type = PersonType.menAge85to89;
        else if (age<=94) type = PersonType.menAge90to94;
        else if (age<=99) type = PersonType.menAge95to99;
        else type = PersonType.menAge100plus;
        } else {
            if (age==0) type = PersonType.womenAge0;
            else if (age<=4) type = PersonType.womenAge1to4;
            else if (age<=9) type = PersonType.womenAge5to9;
            else if (age<=14) type = PersonType.womenAge10to14;
            else if (age<=19) type = PersonType.womenAge15to19;
            else if (age<=24) type = PersonType.womenAge20to24;
            else if (age<=29) type = PersonType.womenAge25to29;
            else if (age<=34) type = PersonType.womenAge30to34;
            else if (age<=39) type = PersonType.womenAge35to39;
            else if (age<=44) type = PersonType.womenAge40to44;
            else if (age<=49) type = PersonType.womenAge45to49;
            else if (age<=54) type = PersonType.womenAge50to54;
            else if (age<=59) type = PersonType.womenAge55to59;
            else if (age<=64) type = PersonType.womenAge60to64;
            else if (age<=69) type = PersonType.womenAge65to69;
            else if (age<=74) type = PersonType.womenAge70to74;
            else if (age<=79) type = PersonType.womenAge75to79;
            else if (age<=84) type = PersonType.womenAge80to84;
            else if (age<=89) type = PersonType.womenAge85to89;
            else if (age<=94) type = PersonType.womenAge90to94;
            else if (age<=99) type = PersonType.womenAge95to99;
            else type = PersonType.womenAge100plus;
        }
    }


    public void setHhId(int hhId) {
        this.hhid = hhId;
        this.hh = Household.getHouseholdFromId(hhId);
    }

    public void setRole(PersonRole pr) {
        this.role = pr;
    }

    public void setAge(int newAge) {
        this.age = newAge;
    }

    public void setIncome (int newIncome) {
        this.income = newIncome;
        Household.getHouseholdFromId(hhid).setType();
    }

    public void setWorkplace(int newWorkplace) {
        this.workplace = newWorkplace;
    }

    public void setOccupation(int newOccupation) {
        this.occupation = newOccupation;
    }

//    public Lock getLock () {
//        return lock;
//    }


    public void logAttributes () {
        logger.info("Attributes of person " + id);
        logger.info("Household id         " + hhid);
        logger.info("Age                  " + age);
        logger.info("Gender (1 m, 2 f)    " + gender);
        logger.info("Role in household    " + role);
        logger.info("Race                 " + race);
        logger.info("Occupation           " + occupation);
        logger.info("Workplace ID         " + workplace);
        logger.info("Income               " + income);
        logger.info("Person type          " + type.toString());
        logger.info("Person role          " + role.toString());
    }


    public void logAttributes (PrintWriter pw) {
        pw.println ("Attributes of person " + id);
        pw.println ("Household id         " + hhid);
        pw.println ("Age                  " + age);
        pw.println ("Gender (1 m, 2 f)    " + gender);
        pw.println ("Role in household    " + role);
        pw.println ("Race                 " + race);
        pw.println ("Occupation           " + occupation);
        pw.println ("Workplace ID         " + workplace);
        pw.println ("Income               " + income);
        pw.println ("Person type          " + type.toString());
        // cannot log person role here because when persons are read, the role is not defined yet.
    }


    public int getId() {
		return id; 
	}
	
	public int getHomeTaz() {
		return Household.getHouseholdFromId(hhid).getHomeZone();
	}
	
	public int getAge() {
		return age; 
	}

    public int getGender() {
        return gender;
    }

    public int getHhId() {
        return hhid;
    }

    public Race getRace() {
        return race;
    }

    public int getOccupation() {
        return occupation;
    }

    public int getIncome() {
        return income;
    }

    public PersonType getType() {
		return type;
	}

    public PersonRole getRole() {
		return role;
	}

    public int getWorkplace() {
        return workplace;
    }

    public void quitJob (boolean makeJobAvailableToOthers) {
        // Person quits job and the job is added to the vacantJobList
        // <makeJobAvailableToOthers> is false if this job disappears from the job market
        Job jb = Job.getJobFromId(workplace);
        if (makeJobAvailableToOthers) JobDataManager.addJobToVacancyList(jb.getZone(), workplace);
        jb.setWorkerID(-1);
        workplace = -1;
        occupation = 2;
        income = (int) (income * 0.6 + 0.5);  //  todo: think about smarter retirement/social welfare algorithm to adjust income after employee leaves work.
    }

}
