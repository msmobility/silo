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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.util.*;

public class DiseaseModelMCR extends AbstractModel implements ModelUpdateListener {
    private static final Logger logger = LogManager.getLogger(DiseaseModelMCR.class);

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
        if(year == properties.main.startYear || properties.healthData.exposureModelYears.contains(year)) {
            calculateRelativeRisk();
        }

        // check injuries here
        //checkTransportInjuries(year);

        // transition probs and health statuses
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
                    if(diseases.equals(Diseases.severely_injured_car) ||
                            diseases.equals(Diseases.severely_injured_walk) ||
                            diseases.equals(Diseases.severely_injured_bike) ||
                            diseases.equals(Diseases.killed_bike) ||
                            diseases.equals(Diseases.killed_walk) ||
                            diseases.equals(Diseases.killed_car)){
                        continue;

                    }

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
                    case NOISE:
                        rr = RelativeRisksDisease.calculateForNoise((PersonHealth)person, (DataContainerHealth) dataContainer);
                        break;
                    case NDVI:
                        rr = RelativeRisksDisease.calculateForNDVI((PersonHealth)person, (DataContainerHealth) dataContainer);
                        break;
                    default:
                        logger.error("Unknown exposures " + exposures);
                }
                ((PersonHealth)person).getRelativeRisksByDisease().put(exposures, rr);
            }
        }
    }

    private void checkInjuries(int year) {
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()){
            // ((PersonHealthMCR) person).getCurrentDisease().contains()
        }
    }

    private void checkTransportInjuries(int year) {
        // todo: iterate over persons here
        for(Person person : dataContainer.getHouseholdDataManager().getPersons()){
            if (((PersonHealthMCR) person).getInjuryStatus().name().startsWith("KILLED_")) return;

            for (String mode : Set.of("Car", "Walk", "Bike")) {
                // Step 1: Check if ANY injury occurs (fatal or serious)
                String anyInjuryKey = "severeFatalInjury" + mode;  // e.g., "severeInjuryCar"
                float anyInjuryProb = ((PersonHealthMCR) person).getWeeklyAccidentRisk(anyInjuryKey);
                float annualAnyInjuryProb = anyInjuryProb;

                //float annualAnyInjuryProb = 1 - (float) Math.pow(1 - anyInjuryProb, 52);

                if (random.nextFloat() < annualAnyInjuryProb) {
                    // todo: read-in prob table
                    double pFatal = 0.01;  // P(killed | injured; age, gender, mode)

                    if (random.nextFloat() < pFatal) {
                        // Killed
                        // todo:
                        switch (mode.toLowerCase()) {  // Case-insensitive comparison
                            case "car":
                                ((PersonHealthMCR) person).setInjuryStatus(InjuryStatus.KILLED_CAR);
                                break;
                            case "bike":
                                ((PersonHealthMCR) person).setInjuryStatus(InjuryStatus.KILLED_BIKE);
                                break;
                            case "walk":
                                ((PersonHealthMCR) person).setInjuryStatus(InjuryStatus.KILLED_WALK);
                                break;
                            default:
                                throw new IllegalArgumentException("Unknown transport mode: " + mode);
                        }
                    }

                    ((PersonHealthMCR) person).getHealthDiseaseTracker().put(year,
                            List.of("dead", "killed_by_" + mode.toLowerCase()));
                    return;  // Exit early
                } else {
                    // Seriously injured (log but don't change state)
                    List<String> status = new ArrayList<>(((PersonHealthMCR) person).getHealthDiseaseTracker().getOrDefault(year,
                            ((PersonHealthMCR) person).getHealthDiseaseTracker().get(year - 1)));
                    status.add("seriously_injured_by_" + mode.toLowerCase());
                    ((PersonHealthMCR) person).getHealthDiseaseTracker().put(year, status);
                }
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

                if(diseases.equals(Diseases.breast_cancer) & person.getGender().equals(Gender.MALE)){
                    continue;
                }

                int zoneId = dataContainer.getRealEstateDataManager().getDwelling(person.getHousehold().getDwellingId()).getZoneId();
                String location = ((ZoneMCR) dataContainer.getGeoData().getZones().get(zoneId)).getLsoaCode();
                String compositeKey = ((DataContainerHealth) dataContainer).createTransitionLookupIndex(Math.min(person.getAge(), 100), person.getGender(), location);
                if (((DataContainerHealth) dataContainer).getHealthTransitionData().get(diseases).get(compositeKey)==null){
                    logger.warn("No health transition data for disease: " + diseases + "| " + compositeKey);
                    continue;
                }

                //the age cap should be 100 for all diseases and all-cause-mortality
                double sickRate = ((DataContainerHealth) dataContainer).getHealthTransitionData().get(diseases).get(compositeKey);

                double sickProb = 0;
                if(adjustByRelativeRisk){
                    for(HealthExposures exposures : HealthExposures.values()){
                        sickRate *= ((PersonHealth) person).getRelativeRisksByDisease().get(exposures).getOrDefault(diseases, 1.f);
                    }
                }


                // disease interactions effects here (diabetes and coronary heart/stroke)
                // todo: logic is if person has diabetes, if desease is coronary heart/stroke, then set probRiskFactor.
                double probRiskFactor = 1.;
                sickRate *= probRiskFactor;


                //
                sickProb = 1- Math.exp(-sickRate);
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

                //TODO: control random number? survival equation
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
                List<String> fullDisease = new ArrayList<>(personHealth.getHealthDiseaseTracker().get(year-1));
                fullDisease.addAll(newDisease);
                fullDisease.remove("healthy");
                personHealth.getHealthDiseaseTracker().put(year, fullDisease);
            }
        }
    }
}
