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

import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.school.School;
import org.apache.log4j.Logger;

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


    public SchoolDataManager(SiloDataContainer data) {
        this.data = data;
        this.geoData = data.getGeoData();
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
}


