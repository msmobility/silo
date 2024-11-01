package de.tum.bgu.msm.health;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.ZoneMCR;
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
        logger.warn("Set up health model relative risk and disease state");
        if (properties.healthData.baseExposureFile == null) {
            initializeRelativeRisk();
        }else {
            calculateRelativeRisk();
        }
        initializeHealthDiseaseStates();
    }

    @Override
    public void prepareYear(int year) {

    }

    @Override
    public void endYear(int year) {
        logger.warn("Health disease model end year:" + year);
        if(properties.healthData.exposureModelYears.contains(year)) {
            calculateRelativeRisk();
        }
        updateDiseaseProbability(properties.healthData.adjustByRelativeRisk);
        updateHealthDiseaseStates(year);
    }

    @Override
    public void endSimulation() {
    }

    private void initializeRelativeRisk() {
        //set up base rr = 1. and assume everyone is healthy at beginning. year 0 is the warm-up
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            for (HealthExposures exposures : HealthExposures.values()){
                EnumMap<Diseases, Float> rr = new EnumMap<>(Diseases.class);

                for(Diseases diseases : ((DataContainerHealth)dataContainer).getDoseResponseData().get(exposures).keySet()){
                    rr.put(diseases, 1.f);
                }
                ((PersonHealth)person).getRelativeRisksByDisease().put(exposures, rr);
            }
        }
    }

    public void calculateRelativeRisk() {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            for(HealthExposures exposures : HealthExposures.values()){
                EnumMap<Diseases, Float> rr = null;
                switch (exposures){
                    case PHYSICAL_ACTIVITY:
                        rr = RelativeRisksDisease.calculateForPA((PersonHealth)person, (DataContainerHealth) dataContainer);
                        break;
                    case AIR_POLLUTION_PM25:
                        rr = RelativeRisksDisease.calculateForPM25((PersonHealth)person, (DataContainerHealth) dataContainer);
                        break;
                    case AIR_POLLUTION_NO2:
                        rr = RelativeRisksDisease.calculateForNO2((PersonHealth)person, (DataContainerHealth) dataContainer);
                        break;
                    default:
                        logger.error("Unknown exposures " + exposures);
                }
                ((PersonHealth)person).getRelativeRisksByDisease().put(exposures, rr);
            }
        }
    }

    public void updateDiseaseProbability(Boolean adjustByRelativeRisk) {
        for(Diseases diseases : Diseases.values()){
            if(((DataContainerHealth) dataContainer).getHealthTransitionData().get(diseases)==null){
                logger.warn("No health transition data for disease: " + diseases.name());
                continue;
            }

            for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {

                //age under 18 no disease prob applied
                if(person.getAge() < 18){
                    continue;
                }

                //TODO: more comprehensive validity check of disease for certain gender/age
                if(diseases.equals(Diseases.endometrial_cancer) & person.getGender().equals(Gender.MALE)){
                    continue;
                }

                int zoneId = dataContainer.getRealEstateDataManager().getDwelling(person.getHousehold().getDwellingId()).getZoneId();
                String location = ((ZoneMCR)dataContainer.getGeoData().getZones().get(zoneId)).getLsoaCode();
                String compositeKey = ((DataContainerHealth) dataContainer).createTransitionLookupIndex(Math.min(person.getAge(), 100), person.getGender(), location);
                if (((DataContainerHealth) dataContainer).getHealthTransitionData().get(diseases).get(compositeKey)==null){
                    logger.warn("No health transition data for disease: " + diseases + "| " + compositeKey);
                    continue;
                }

                //the age cap should be 100 for all diseases and all-cause-mortality
                double sickProb = ((DataContainerHealth) dataContainer).getHealthTransitionData().get(diseases).get(compositeKey);

                if(adjustByRelativeRisk){
                    for(HealthExposures exposures : HealthExposures.values()){
                        sickProb *= ((PersonHealth) person).getRelativeRisksByDisease().get(exposures).getOrDefault(diseases, 1.f);
                    }
                }

                ((PersonHealth)person).getCurrentDiseaseProb().put(diseases, (float) sickProb);
            }
        }
    }

    private void initializeHealthDiseaseStates() {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            //start year-1 as initial state
            ((PersonHealth) person).getHealthDiseaseTracker().put(Properties.get().main.startYear-1, Arrays.asList("healthy"));
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
                //for base year, year-1 is the initial state "healthy"
                personHealth.getHealthDiseaseTracker().put(year, personHealth.getHealthDiseaseTracker().get(year-1));
            }else {
                personHealth.getHealthDiseaseTracker().put(year, newDisease);
            }
        }
    }

}
