package de.tum.bgu.msm.health;

import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.models.demography.death.DeathStrategy;

public class DeathStrategyHealth implements DeathStrategy {

    // Lifetable filenames todo: define somewhere in properties
    private static final String LIFETABLE_FILENAME_MALE = "input/health/lifetable_male.csv";
    private static final String LIFETABLE_FILENAME_FEMALE = "input/health/lifetable_female.csv";

    final double[] mortalityRatesMale;
    final double[] mortalityRatesFemale;

    public DeathStrategyHealth(String baseDirectory) {
        this.mortalityRatesMale = LifeTableReader.readData(baseDirectory + LIFETABLE_FILENAME_MALE);
        this.mortalityRatesFemale = LifeTableReader.readData(baseDirectory + LIFETABLE_FILENAME_FEMALE);
    }

    @Override
    public double calculateDeathProbability(Person person) {
        final int personAge = Math.min(person.getAge(), 100);
        Gender personSex = person.getGender();
        double allCauseRR = ((PersonHealth)person).getAllCauseRR();
        double fatalAccidentRisk = ((PersonHealth)person).getWeeklyAccidentRisk("fatality");

        var alpha = 0.;

        if (personAge < 0){
            throw new RuntimeException("Undefined negative person age!"+personAge);
        }

        if ("MALE".equals(personSex.name())) {
            alpha = mortalityRatesMale[personAge];
        } else if (personSex.name().equals("FEMALE")) {
           alpha = mortalityRatesFemale[personAge];
        }

        double adjustedAlpha = alpha * allCauseRR;
        return adjustedAlpha + fatalAccidentRisk - adjustedAlpha * fatalAccidentRisk;
    }
}