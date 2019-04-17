package de.tum.bgu.msm.models.demography.divorce;

import de.tum.bgu.msm.data.person.Person;

public interface DivorceStrategy {
    double calculateDivorceProbability(Person per);
}
