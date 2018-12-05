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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.school.School;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.matsim.core.utils.collections.QuadTree;
import org.osgeo.proj4j.CoordinateTransformFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Keeps data of schools
 * Author: Qin Zhang
 **/

public class SchoolDataManager {
    static Logger logger = Logger.getLogger(SchoolDataManager.class);

    private final GeoData geoData;
    private final SiloDataContainer data;
    private final Map<Integer, School> schools = new ConcurrentHashMap<>();
    private QuadTree<School> primarySearchTree;
    private QuadTree<School> secondarySearchTree;
    private QuadTree<School> tertiarySearchTree;

    public SchoolDataManager(SiloDataContainer data) {
        this.data = data;
        this.geoData = data.getGeoData();
    }

    public void setSchoolSearchTree(Properties properties) {
        Envelope bounds = loadEnvelope(properties);
        double minX = bounds.getMinX();
        double minY = bounds.getMinY();
        double maxX = bounds.getMaxX();
        double maxY = bounds.getMaxY();
        this.primarySearchTree = new QuadTree<>(minX,minY,maxX,maxY);
        this.secondarySearchTree = new QuadTree<>(minX,minY,maxX,maxY);
        this.tertiarySearchTree = new QuadTree<>(minX,minY,maxX,maxY);
    }


    private Envelope loadEnvelope(Properties properties) {
        //TODO: Remove minX,minY,maxX,maxY when implementing study area shapefile in Geodata 09 Oct QZ'
        File schoolsShapeFile = new File(properties.main.baseDirectory + properties.schoolData.schoolsShapeFile);

        try {
            FileDataStore dataStore = FileDataStoreFinder.getDataStore(schoolsShapeFile);
            return dataStore.getFeatureSource().getBounds();
        } catch (IOException e) {
            logger.error("Error reading file " + schoolsShapeFile);
            throw new RuntimeException(e);
        }
    }

    public void addSchool(School ss) {
        this.schools.put(ss.getId(), ss);
    }

    public School getSchoolFromId(int id) {
        return schools.get(id);
    }

    public Collection<School> getSchools() {
        return schools.values();
    }

    public School getClosestSchool(Person person) {
        Dwelling dwelling = data.getRealEstateData().getDwelling(person.getHousehold().getDwellingId());

        Coordinate coordinate = null;
        if (Properties.get().main.useMicrolocation) {
            coordinate = ((MicroLocation) dwelling).getCoordinate();
        } else{
            coordinate = geoData.getZones().get(dwelling.getZoneId()).getRandomCoordinate();
        }
        switch (person.getSchoolType()+1){
            case 1:
                return primarySearchTree.getClosest(coordinate.x,coordinate.y);
            case 2:
                return secondarySearchTree.getClosest(coordinate.x,coordinate.y);
            case 3:
                return tertiarySearchTree.getClosest(coordinate.x,coordinate.y);
            default:
                throw new IllegalArgumentException(String.format("schoolType %d not valid.", person.getSchoolType()+1));
        }
    }

    public void addSchoolToSearchTree(School school) {

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
}


