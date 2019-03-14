package de.tum.bgu.msm.models.demography.leaveParentalHousehold;

import de.tum.bgu.msm.data.person.Person;

public interface LeaveParentalHouseholdStrategy {
    double calculateLeaveParentsProbability(Person person);
}
