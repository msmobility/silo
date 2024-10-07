package health;

import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.health.data.PersonHealth;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.health.disease.HealthExposures;
import de.tum.bgu.msm.models.demography.death.DeathStrategy;

public class DeathStrategyMCR implements DeathStrategy {

    private final HealthDataContainerImpl dataContainer;
    private final Boolean adjustByRelativeRisk;

    public DeathStrategyMCR(HealthDataContainerImpl dataContainer, Boolean adjustByRelativeRisk) {
        this.dataContainer = dataContainer;
        this.adjustByRelativeRisk = adjustByRelativeRisk;
    }

    @Override
    public double calculateDeathProbability(Person person) {
        final int personAge = Math.min(person.getAge(), 100);
        Gender personSex = person.getGender();

        //TODO: check with Ali
        double allCauseRR = 1. ;

        for(HealthExposures healthExposures : ((PersonHealth)person).getRelativeRisksByDisease().keySet()){
            allCauseRR = allCauseRR * ((PersonHealth)person).getRelativeRisksByDisease().get(healthExposures).get(Diseases.all_cause_mortality);
        };

        //TODO: how to integrate injury into all cause mortality
        //double fatalAccidentRisk = ((PersonHealth)person).getWeeklyAccidentRisk("fatality");

        if (personAge < 0){
            throw new RuntimeException("Undefined negative person age!"+personAge);
        }

        double alpha = dataContainer.getHealthTransitionData().get(Diseases.all_cause_mortality).get(personSex).get(personAge);

        if(adjustByRelativeRisk){
            alpha = alpha * allCauseRR;
        }

        return alpha;
    }
}