package de.tum.bgu.msm.scenarios.health;

import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.io.LifeTableReader;
import de.tum.bgu.msm.models.demography.death.DeathStrategy;

public class DeathStrategyHealth implements DeathStrategy {

    // Lifetable filenames todo: define somewhere in properties
    private static final String LIFETABLE_FILENAME_MALE = "input/health/lifetable_male";
    private static final String LIFETABLE_FILENAME_FEMALE = "input/health/lifetable_female";

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
        double allCauseRR = ((PersonMuc)person).getAllCauseRR();
        double weeklyFatalAccidentRisk = ((PersonMuc)person).getWeeklyAccidentRisk("fatality");
        double yearlyFatalAccidentRisk = weeklyFatalAccidentRisk * 52.1429;

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
        return adjustedAlpha + yearlyFatalAccidentRisk - adjustedAlpha * yearlyFatalAccidentRisk;
    }
}