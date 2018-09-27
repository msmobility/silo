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

import de.tum.bgu.msm.data.Location;

/**
 * Job interface
 * @author Nkuehnel
 **/
 public interface Job extends Location {

     int getId();

     int getWorkerId();

     String getType();

     void setWorkerID(int personID);

     void setJobWorkingTime(double startTimeInSeconds, double workingTimeInSeconds);

     double getStartTimeInSeconds();

     double getWorkingTimeInSeconds();
    
    

}
