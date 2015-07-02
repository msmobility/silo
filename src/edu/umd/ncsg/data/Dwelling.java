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
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

/**
 * @author Greg Erhardt 
 * Created on Dec 2, 2009
 *
 */
public class Dwelling {

    static Logger logger = Logger.getLogger(Dwelling.class);
    private static final Map<Integer, Dwelling> dwellingMap = new HashMap<>();
	int id;
    int zone;
    int hhId;
    DwellingType type;
    int bedrooms;
    int quality;
    int price;
    int yearBuilt;
    float restriction;
    double utilOfResident;
    double[] utilByHhType;


    public Dwelling (int id, int zone, int hhId, DwellingType type, int bedrooms, int quality, int price, float restriction,
                     int year) {
        // Create new dwelling object
        this.id = id;
        this.zone = zone;
        this.hhId = hhId;
        this.type = type;
        this.bedrooms = bedrooms;
        this.quality = quality;
        this.price = price;
        this.restriction = restriction;
        this.yearBuilt = year;
        this.utilOfResident = 0.;
        this.utilByHhType = new double[HouseholdType.values().length];
        dwellingMap.put(id, this);
    }


    public static void saveDwellings (Dwelling[] dds) {
        for (Dwelling dd: dds) dwellingMap.put(dd.getId(), dd);
    }


    public static Dwelling getDwellingFromId(int dwellingId) {
        return dwellingMap.get(dwellingId);
    }


    public static int getDwellingCount() {
        return dwellingMap.size();
    }

    public static Collection<Dwelling> getDwellings() {
        // return collection of dwellings
            return dwellingMap.values();
    }

    public static Dwelling[] getDwellingArray() {
        return dwellingMap.values().toArray(new Dwelling[dwellingMap.size()]);
    }


    public static void removeDwelling(int id) {
        dwellingMap.remove(id);
    }


    public void logAttributes () {
        logger.info("Attributes of dwelling  " + id);
        logger.info("Located in zone         " + zone);
        logger.info("Occupied by household   " + hhId);
        logger.info("Dwelling type           " + type.toString());
        logger.info("Number of bedroom       " + bedrooms);
        logger.info("Quality (1 low, 4 high) " + quality);
        logger.info("Monthly price in US$    " + price);
        logger.info("Affordable housing      " + restriction);
        logger.info("Year dwelling was built " + yearBuilt);
    }

    public void logAttributes (PrintWriter pw) {
        pw.println ("Attributes of dwelling  " + id);
        pw.println ("Located in zone         " + zone);
        pw.println ("Occupied by household   " + hhId);
        pw.println ("Dwelling type           " + type.toString());
        pw.println ("Number of bedrooms      " + bedrooms);
        pw.println ("Quality (1 low, 4 high) " + quality);
        pw.println ("Monthly price in US$    " + price);
        pw.println ("Affordable housing      " + restriction);
        pw.println ("Year dwelling was built " + yearBuilt);
    }


    public int getId () {
        return id;
    }

    public int getQuality () {
        return quality;
    }

    public int getResidentId () {
        return hhId;
    }

    public int getZone() {
        return zone;
    }

    public int getPrice() {
        return price;
    }

    public DwellingType getType() {
        return type;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public int getYearBuilt() {
        return yearBuilt;
    }

    public float getRestriction() {
        // 0: no restriction, negative value: rent-controlled, positive value: rent-controlled and maximum income of renter
        return restriction;
    }

    public double[] getUtilByHhType() {
        return utilByHhType;
    }

    public double getUtilOfResident() {
        return utilOfResident;
    }

    public void setResidentID(int residentID) {
        this.hhId = residentID;
    }
    
    public void setQuality (int quality) {
        this.quality = quality;
    }  

    public void setPrice (int price) {
        this.price = price;
    }

    public void setRestriction (float restriction) {
        // 0: no restriction, negative value: rent-controlled, positive value: rent-controlled and maximum income of renter
        this.restriction = restriction;
    }

    public void setUtilitiesOfVacantDwelling(double[] utils) {
        this.utilByHhType = utils;
    }

    public void setUtilOfResident(double utilOfResident) {
        this.utilOfResident = utilOfResident;
    }

 }
