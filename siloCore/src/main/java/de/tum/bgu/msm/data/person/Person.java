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

import de.tum.bgu.msm.data.Id;
import de.tum.bgu.msm.data.household.Household;

import java.util.Optional;

/**
 * @author Greg Erhardt 
 * Created on Dec 2, 2009
 *
 */
public interface Person extends Id {

    void setHousehold(Household householdId);

    Household getHousehold();

    void setRole(PersonRole pr);

    void birthday();

    void setIncome (int newIncome);

    void setWorkplace(int newWorkplace);

    void setOccupation(Occupation newOccupation);
	
	int getAge();

    Gender getGender();

    Occupation getOccupation();

    int getAnnualIncome();

    PersonType getType();

    PersonRole getRole();

    int getJobId();

    void setDriverLicense(boolean driverLicense);

    boolean hasDriverLicense();

    Optional<Object> getAttribute(String key);

    void setAttribute(String key, Object value);
}
