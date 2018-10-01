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

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public School createSchool(int id, int type, int capacity, Location location) {
        School school = new School(id, type, capacity, location);
        this.schools.put(id, school);
        return school;
    }

    public void readSchools(Properties properties) {
        logger.info("Reading school micro data from ascii file");

        int year = Properties.get().main.startYear;
        String fileName = properties.main.baseDirectory + properties.schoolData.schoolsFileName;
        fileName += "_" + year + ".csv";

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posZone = SiloUtil.findPositionInArray("zone", header);
            int posCapacity = SiloUtil.findPositionInArray("capacity", header);
            int posOccupancy = SiloUtil.findPositionInArray("occupancy", header);
            int posType = SiloUtil.findPositionInArray("type", header);

            int posCoordX = -1;
            int posCoordY = -1;
            if(Properties.get().main.implementation == Implementation.MUNICH) {
                posCoordX = SiloUtil.findPositionInArray("CoordX", header);
                posCoordY = SiloUtil.findPositionInArray("CoordY", header);
            }


            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                int zoneId = Integer.parseInt(lineElements[posZone]);
                Location location;
                Zone zone = geoData.getZones().get(zoneId);
                int type = Integer.parseInt(lineElements[posType]);
                int capacity = Integer.parseInt(lineElements[posOccupancy]);
                int occupancy = Integer.parseInt(lineElements[posCapacity]);

                //TODO: remove it when we implement interface
                if (Properties.get().main.implementation == Implementation.MUNICH) {
                    location = new MicroLocation(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]), zone);
                } else {
                    location = zone;
                }
                School ss = createSchool(id, type, capacity, location);
                ss.setCurrentOccupancy(occupancy);

//Qin ???
//                if (id == SiloUtil.trackSs) {
//                    SiloUtil.trackWriter.println("Read school with following attributes from " + fileName);
//                    SiloUtil.trackWriter.println(schools.get(id).toString());
//                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop school file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " schools.");
    }
}
