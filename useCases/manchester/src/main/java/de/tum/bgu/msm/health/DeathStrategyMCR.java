package de.tum.bgu.msm.health;

import de.tum.bgu.msm.data.ZoneMCR;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.health.data.DataContainerHealth;
import de.tum.bgu.msm.health.data.PersonHealth;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.health.disease.HealthExposures;
import de.tum.bgu.msm.models.demography.death.DeathStrategy;

import java.util.*;

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

        if (personAge < 0){
            throw new RuntimeException("Undefined negative person age !" + personAge);
        }

        //cap age at 100, over 100 all cause mortality prob = 1
        if (personAge >= 100){
            return 1.;
        }

        // check killed by injury
        Set<Diseases> killedInAccident = Set.of(
                Diseases.killed_car,
                Diseases.killed_bike,
                Diseases.killed_walk
        );

        if (!Collections.disjoint(((PersonHealth) person).getCurrentDisease(), killedInAccident)) {
            return 1.;
        }

        int zoneId = dataContainer.getRealEstateDataManager().getDwelling(person.getHousehold().getDwellingId()).getZoneId();
        String location = ((ZoneMCR)dataContainer.getGeoData().getZones().get(zoneId)).getLsoaCode();
        String compositeKey = ((DataContainerHealth) dataContainer).createTransitionLookupIndex(Math.min(person.getAge(), 100), person.getGender(), location);

        double alpha = dataContainer.getHealthTransitionData().get(Diseases.all_cause_mortality).get(compositeKey);

        //no rr adjustment for age under 18
        if(personAge < 18){
            return alpha;
        }

        if(adjustByRelativeRisk){
            for(HealthExposures healthExposures : ((PersonHealth)person).getRelativeRisksByDisease().keySet()){
                alpha *=  ((PersonHealth)person).getRelativeRisksByDisease().get(healthExposures).get(Diseases.all_cause_mortality);
            }
        }

        // risk factors
        Set<Diseases> currentDiseases = new HashSet<>(((PersonHealth) person).getCurrentDisease());
        Set<Diseases> cancers = Set.of(
                Diseases.breast_cancer,
                Diseases.endometrial_cancer,
                Diseases.colon_cancer,
                Diseases.bladder_cancer,
                Diseases.esophageal_cancer,
                Diseases.gastric_cardia_cancer,
                Diseases.head_neck_cancer,
                Diseases.liver_cancer,
                Diseases.lung_cancer,
                Diseases.rectum_cancer
        );
        Set<Diseases> injuries = Set.of(
                Diseases.severely_injured_car,
                Diseases.severely_injured_bike,
                Diseases.severely_injured_walk
        );

        // Risk factors
        // todo: apply only for people more than 40
        if(personAge > 40){
            if (currentDiseases.contains(Diseases.all_cause_dementia)) {
                alpha *= 8.42;
            }
            if (currentDiseases.contains(Diseases.parkinson)) {
                alpha *= 4.6;
            }
            if (currentDiseases.contains(Diseases.copd)) {
                alpha *= 2.58;
            }
            if (currentDiseases.contains(Diseases.stroke)) {
                alpha *= 1.85;
            }
            if (!Collections.disjoint(currentDiseases, cancers)) {
                alpha *= 1.99;
            }
            if (currentDiseases.contains(Diseases.diabetes)) {
                alpha *= 1.93;
            }
            if (currentDiseases.contains(Diseases.coronary_heart_disease)) {
                alpha *= 1.72;
            }
            if (currentDiseases.contains(Diseases.depression)) {
                alpha *= 1.4;
            }
        }


        // todo: what happens with people < 18
        if (!Collections.disjoint(currentDiseases, injuries)) {
            if (person.getGender().equals(Gender.MALE)) {
                alpha *= 1.71;
            } else {
                alpha *= 1.74;
            }
        }
        return alpha;
    }
}