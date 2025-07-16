package de.tum.bgu.msm.models.demography.death;

import de.tum.bgu.msm.data.person.Person;

import java.util.Random;

public interface DeathStrategy {
    double calculateDeathProbability(Person person, Random random);
}
