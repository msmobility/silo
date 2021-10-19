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
package de.tum.bgu.msm.data.job;

import de.tum.bgu.msm.data.Id;
import de.tum.bgu.msm.data.MicroLocation;
import de.tum.bgu.msm.data.Zone;
import org.locationtech.jts.geom.Coordinate;

import java.util.Optional;

/**
 * Job interface
 *
 * @author Nkuehnel
 **/
public interface Job extends MicroLocation, Id {

    int getWorkerId();

    String getType();

    void setWorkerID(int personID);

    Optional<Integer> getStartTimeInSeconds();

    Optional<Integer> getWorkingTimeInSeconds();

    Optional<Object> getAttribute(String key);

    void setAttribute(String key, Object value);

     void relocateJob(Zone newZone, Coordinate newCoordinate);

}
