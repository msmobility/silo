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
package de.tum.bgu.msm.schools;

import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingData;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.matsim.core.utils.collections.QuadTree;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Keeps data of schools
 * Author: Qin Zhang
 **/
public class SchoolDataImpl implements SchoolData {

    private final static Logger logger = LogManager.getLogger(SchoolDataImpl.class);
    private static final int MIN_SECONDARY_AGE = 10;
    private static final int MIN_TERTIARY_AGE = 18;

    private final GeoData geoData;
    private final DwellingData dwellingData;
    private final Properties properties;
    private final Map<Integer, School> schools = new ConcurrentHashMap<>();

    boolean setup = false;

    private QuadTree<School> primarySearchTree;
    private QuadTree<School> secondarySearchTree;
    private QuadTree<School> tertiarySearchTree;

    public SchoolDataImpl(GeoData geoData, DwellingData dwellingData, Properties properties) {
        this.geoData = geoData;
        this.dwellingData = dwellingData;
        this.properties = properties;
    }

    public static int guessSchoolType(PersonWithSchool person) {
        if (person.getAge() < MIN_SECONDARY_AGE) {
            person.setSchoolType(1);
            return 1;
        }else if (person.getAge() < MIN_TERTIARY_AGE) {
            person.setSchoolType(2);
            return 2;
        }else {
            person.setSchoolType(3);
            return 3;
        }
    }

    private void setSchoolSearchTree(double minx, double miny, double maxx, double maxy) {
        double minX = minx-1;
        double minY = miny-1;
        double maxX = maxx+1;
        double maxY = maxy+1;
        this.primarySearchTree = new QuadTree<>(minX,minY,maxX,maxY);
        this.secondarySearchTree = new QuadTree<>(minX,minY,maxX,maxY);
        this.tertiarySearchTree = new QuadTree<>(minX,minY,maxX,maxY);
    }

    @Override
    public void addSchool(School ss) {
        this.schools.put(ss.getId(), ss);
        if(setup) {
            addSchoolToSearchTree(ss);
        }
    }

    @Override
    public School getSchoolFromId(int id) {
        return schools.get(id);
    }

    @Override
    public Collection<School> getSchools() {
        return schools.values();
    }

    @Override
    public School getClosestSchool(Person person, int schoolType) {
        Dwelling dwelling = dwellingData.getDwelling(person.getHousehold().getDwellingId());

        Coordinate coordinate;
        if (dwelling != null) {
            coordinate = dwelling.getCoordinate();
        } else{
            coordinate = geoData.getZones().get(dwelling.getZoneId()).getRandomCoordinate(SiloUtil.getRandomObject());
        }
        switch (schoolType){
            case 1:
                return primarySearchTree.getClosest(coordinate.x,coordinate.y);
            case 2:
                return secondarySearchTree.getClosest(coordinate.x,coordinate.y);
            case 3:
                return tertiarySearchTree.getClosest(coordinate.x,coordinate.y);
            default:
                throw new IllegalArgumentException(String.format("schoolType %d not valid.", schoolType));
        }
    }

    @Override
    public void removeSchool(int id) {

        final School remove = this.schools.remove(id);
        int type = remove.getType();
        final Coordinate coordinate = ((MicroLocation) remove).getCoordinate();
        switch (type) {
            case 1:
                primarySearchTree.remove(coordinate.x, coordinate.y, remove);
                break;
            case 2:
                secondarySearchTree.remove(coordinate.x, coordinate.y, remove);
                break;
            case 3:
                tertiarySearchTree.remove(coordinate.x, coordinate.y, remove);
        }
    }

    private void addSchoolToSearchTree(School school) {
        int schoolType = school.getType();
        switch (schoolType){
            case 1:
                primarySearchTree.put(((MicroLocation)school).getCoordinate().x,((MicroLocation)school).getCoordinate().y,school);
                break;
            case 2:
                secondarySearchTree.put(((MicroLocation)school).getCoordinate().x,((MicroLocation)school).getCoordinate().y,school);
                break;
            case 3:
                tertiarySearchTree.put(((MicroLocation)school).getCoordinate().x,((MicroLocation)school).getCoordinate().y,school);
        }
    }

    @Override
    public void setup() {
        logger.info("Setup of schools.");
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (School school : schools.values()) {
            Coordinate coord = ((MicroLocation) school).getCoordinate();
            if (coord.x < minX) minX = coord.x;
            if (coord.x > maxX) maxX = coord.x;
            if (coord.y < minY) minY = coord.y;
            if (coord.y > maxY) maxY = coord.y;
        }
        setSchoolSearchTree(minX, minY, maxX, maxY);
        for(School school: schools.values()) {
            addSchoolToSearchTree(school);
        }
    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void endYear(int year) {

    }

    @Override
    public void endSimulation() {

    }
}


