package de.tum.bgu.msm.models.demography.marriage;

import de.tum.bgu.msm.data.person.Person;

public interface MarriageStrategy {
    double calculateMarriageProbability(Person pp);
}
