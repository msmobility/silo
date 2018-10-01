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
package de.tum.bgu.msm.data.person;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.data.*;

/**
 * @author Greg Erhardt 
 * Created on Dec 2, 2009
 *
 */
public interface Person extends Id {

    void setHousehold(Household household);

    Household getHh();

    void setRole(PersonRole pr);

    void birthday();

    void setIncome (int newIncome);

    void setWorkplace(int newWorkplace);

    void setOccupation(Occupation newOccupation);

    int getId();
	
	int getAge();

    Gender getGender();

    Race getRace();

    Occupation getOccupation();

    int getIncome();

    PersonType getType();

    PersonRole getRole();

    int getWorkplace();

    void setEducationLevel(int educationLevel);

    int getEducationLevel();

    void setTelework(int telework);

    int getTelework();

    void setNationality(Nationality nationality);

    Nationality getNationality();

    void setTravelTime(float travelTime);

    float getTravelTime();

    void setJobTAZ(int jobTAZ);

    int getJobTAZ();

    void setDriverLicense(boolean driverLicense);

    boolean hasDriverLicense();

    void setSchoolType(int schoolType);

    int getSchoolType();

    void setSchoolPlace(int schoolPlace);

    int getSchoolPlace();

    Coordinate getSchoolLocation();

    int getSchoolZoneId();

    void setSchoolCoordinate(Coordinate schoolLocation, int schoolZoneId);
}
