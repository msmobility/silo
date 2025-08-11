package de.tum.bgu.msm.models.demography.death;

import de.tum.bgu.msm.data.person.Person;

public interface DeathStrategy {
    double calculateDeathProbability(Person person);
}
