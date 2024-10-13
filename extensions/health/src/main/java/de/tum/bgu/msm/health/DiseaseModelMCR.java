package de.tum.bgu.msm.health;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.health.data.*;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.health.disease.HealthExposures;
import de.tum.bgu.msm.health.disease.RelativeRisksDisease;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.ModelUpdateListener;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.util.*;

public class DiseaseModelMCR extends AbstractModel implements ModelUpdateListener {
    private static final Logger logger = Logger.getLogger(DiseaseModelMCR.class);

    public DiseaseModelMCR(DataContainer dataContainer, Properties properties, Random random) {
        super(dataContainer, properties, random);
    }

    @Override
    public void setup() {
        //set up base rr = 1. and assume everyone is healthy at beginning. year 0 is the warm-up
        logger.warn("Set up health model relative risk and disease state");
        initializeRelativeRisk();
        initializeHealthDiseaseStates();
    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void endYear(int year) {
        logger.warn("Health disease model end year:" + year);
        calculateRelativeRisk();
        updateDiseaseProbability(Boolean.FALSE);
        updateHealthDiseaseStates(year);
    }

    @Override
    public void endSimulation() {
    }

    private void initializeRelativeRisk() {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            EnumMap<Diseases, Float> rr_PA = new EnumMap<>(Diseases.class);

            for(Diseases diseases : ((DataContainerHealth)dataContainer).getDoseResponseData().get(HealthExposures.PHYSICAL_ACTIVITY).keySet()){
                rr_PA.put(diseases, 1.f);
            }
            ((PersonHealth)person).getRelativeRisksByDisease().put(HealthExposures.PHYSICAL_ACTIVITY, rr_PA);

            //EnumMap<Diseases, Float> rr_AP = RelativeRisksDisease.calculateForAP(personMRC, (DataContainerHealth) dataContainer);
            //personMRC.getRelativeRisksByDisease().put(HealthExposures.AIR_POLLUTION, rr_AP);
        }
    }

    public void calculateRelativeRisk() {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            EnumMap<Diseases, Float> rr_PA = RelativeRisksDisease.calculateForPA((PersonHealth)person, (DataContainerHealth) dataContainer);
            ((PersonHealth)person).getRelativeRisksByDisease().put(HealthExposures.PHYSICAL_ACTIVITY, rr_PA);

            //EnumMap<Diseases, Float> rr_AP = RelativeRisksDisease.calculateForAP(personMRC, (DataContainerHealth) dataContainer);
            //personMRC.getRelativeRisksByDisease().put(HealthExposures.AIR_POLLUTION, rr_AP);
        }
    }

    public void updateDiseaseProbability(Boolean adjustByRelativeRisk) {
        for(Diseases diseases : Diseases.values()){
            if(((DataContainerHealth) dataContainer).getHealthTransitionData().get(diseases)==null){
                logger.warn("No health transition data for disease: " + diseases.name());
                continue;
            }

            for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {

                //TODO: more comprehensive validity check of disease for certain gender/age
                if(diseases.equals(Diseases.breast_cancer) & person.getGender().equals(Gender.MALE)){
                    continue;
                }

                if(diseases.equals(Diseases.endometrial_cancer) & person.getGender().equals(Gender.MALE)){
                    continue;
                }

                double sickProb = 0.;
                if(adjustByRelativeRisk){

                }else{
                    if (((DataContainerHealth) dataContainer).getHealthTransitionData().get(diseases).get(person.getGender())==null){
                        logger.warn("No health transition data for disease: " + diseases.name() + "for gender " + person.getGender().name());
                    }
                    //TODO: check with Belen, age cap 95 or 100?
                    //the age cap should be 100 for all diseases and all-cause-mortality
                    sickProb = ((DataContainerHealth) dataContainer).getHealthTransitionData().get(diseases).get(person.getGender()).getOrDefault(Math.min(person.getAge(), 95), 0.);
                }
                ((PersonHealth)person).getCurrentDiseaseProb().put(diseases, (float) sickProb);
            }
        }
    }

    private void initializeHealthDiseaseStates() {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            ((PersonHealth) person).getHealthDiseaseTracker().put(Properties.get().main.startYear, Arrays.asList("health"));
        }
    }


    public void updateHealthDiseaseStates(int year) {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            PersonHealth personHealth = (PersonHealth) person;
            List<String> newDisease = new ArrayList<>();

            for(Diseases diseases : Diseases.values()){
                if(diseases.equals(Diseases.all_cause_mortality)){
                    continue;
                }

                if(personHealth.getCurrentDiseaseProb().get(diseases)==null){
                    continue;
                }

                if(random.nextFloat()<(personHealth.getCurrentDiseaseProb().get(diseases))){
                    if(!personHealth.getCurrentDisease().contains(diseases)){
                        personHealth.getCurrentDisease().add(diseases);
                        newDisease.add(diseases.toString());
                    }
                }
            }

            //update Disease track map
            if(newDisease.isEmpty()){
                if(year == Properties.get().main.baseYear){
                    personHealth.getHealthDiseaseTracker().put(year, Arrays.asList("health"));
                }else {
                    personHealth.getHealthDiseaseTracker().put(year, personHealth.getHealthDiseaseTracker().get(year-1));
                }
            }else {
                personHealth.getHealthDiseaseTracker().put(year, newDisease);
            }
        }
    }

}
