package de.tum.bgu.msm.models.disability;

import de.tum.bgu.msm.data.person.Person;

public interface DisabilityStrategy {
    double calculateDisabilityProbability(Person person);
    double calculateDisabilityType(Person person);
    double calculateBaseYearDisabilityProbability(Person person);
}
