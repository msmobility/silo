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
import java.util.stream.Collectors;

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
        } else {
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
        if (year == properties.main.startYear || properties.healthData.exposureModelYears.contains(year)) {
            calculateRelativeRisk();
        }

        // transition probs and health statuses
        updateDiseaseProbability(properties.healthData.adjustByRelativeRisk);
        updateHealthDiseaseStates(year);
    }

    @Override
    public void endSimulation() {
    }

    private void initializeRelativeRisk() {
        //set up base rr = 1. and assume everyone is healthy at beginning. year 0 is the warm-up
        for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            for (HealthExposures exposures : HealthExposures.values()) {
                EnumMap<Diseases, Float> rr = new EnumMap<>(Diseases.class);

                for (Diseases diseases : ((DataContainerHealth) dataContainer).getDoseResponseData().get(exposures).keySet()) {
                    if (diseases.equals(Diseases.killed_bike) || diseases.equals(Diseases.killed_walk) || diseases.equals(Diseases.killed_car) ||
                            diseases.equals(Diseases.severely_injured_car) || diseases.equals(Diseases.severely_injured_walk) || diseases.equals(Diseases.severely_injured_bike)) {
                        continue;
                    }
                    rr.put(diseases, 1.f);
                }
                ((PersonHealth) person).getRelativeRisksByDisease().put(exposures, rr);
            }
        }
    }

    public void calculateRelativeRisk() {
        for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            for (HealthExposures exposures : HealthExposures.values()) {
                EnumMap<Diseases, Float> rr = null;
                switch (exposures) {
                    case PHYSICAL_ACTIVITY:
                        rr = RelativeRisksDisease.calculateForPA((PersonHealth) person, (DataContainerHealth) dataContainer);
                        break;
                    case AIR_POLLUTION_PM25:
                        rr = RelativeRisksDisease.calculateForPM25((PersonHealth) person, (DataContainerHealth) dataContainer);
                        break;
                    case AIR_POLLUTION_NO2:
                        rr = RelativeRisksDisease.calculateForNO2((PersonHealth) person, (DataContainerHealth) dataContainer);
                        break;
                    case NOISE:
                        rr = RelativeRisksDisease.calculateForNoise((PersonHealth) person, (DataContainerHealth) dataContainer);
                        break;
                    case NDVI:
                        rr = RelativeRisksDisease.calculateForNDVI((PersonHealth) person, (DataContainerHealth) dataContainer);
                        break;
                    default:
                        logger.error("Unknown exposures " + exposures);
                }
                ((PersonHealth) person).getRelativeRisksByDisease().put(exposures, rr);
            }
        }
    }

    public void updateDiseaseProbability(Boolean adjustByRelativeRisk) {
        for (Diseases diseases : Diseases.values()) {
            if (((DataContainerHealth) dataContainer).getHealthTransitionData().get(diseases) == null) {
                logger.warn("No health transition data for disease: " + diseases.name());
                continue;
            }

            for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {

                //age under 18 no disease prob applied
                if (person.getAge() < 18) {
                    continue;
                }

                //TODO: more comprehensive validity check of disease for certain gender/age
                if (diseases.equals(Diseases.endometrial_cancer) & person.getGender().equals(Gender.MALE)) {
                    continue;
                }

                if (diseases.equals(Diseases.breast_cancer) & person.getGender().equals(Gender.MALE)) {
                    continue;
                }

                int zoneId = dataContainer.getRealEstateDataManager().getDwelling(person.getHousehold().getDwellingId()).getZoneId();
                String location = ((ZoneMCR) dataContainer.getGeoData().getZones().get(zoneId)).getLsoaCode();
                String compositeKey = ((DataContainerHealth) dataContainer).createTransitionLookupIndex(Math.min(person.getAge(), 100), person.getGender(), location);
                if (((DataContainerHealth) dataContainer).getHealthTransitionData().get(diseases).get(compositeKey) == null) {
                    logger.warn("No health transition data for disease: " + diseases + "| " + compositeKey);
                    continue;
                }

                //the age cap should be 100 for all diseases and all-cause-mortality
                double sickRate = ((DataContainerHealth) dataContainer).getHealthTransitionData().get(diseases).get(compositeKey);

                // Effects of exposures
                double sickProb = 0;
                if (adjustByRelativeRisk) {
                    for (HealthExposures exposures : HealthExposures.values()) {
                        sickRate *= ((PersonHealth) person).getRelativeRisksByDisease().get(exposures).getOrDefault(diseases, 1.f);
                    }
                }

                // Disease interactions effects (diabetes on coronary heart disease/stroke)
                if (((PersonHealth) person).getCurrentDisease().contains(Diseases.diabetes)) {
                    // increase risk to have coronary heart disease
                    if (diseases.equals(Diseases.coronary_heart_disease)) {
                        if (((PersonHealth) person).getGender().equals(Gender.MALE)) {
                            sickRate *= 2.16;
                        } else {
                            sickRate *= 2.82;
                        }
                    }

                    // increase risk to have stroke
                    if (diseases.equals(Diseases.stroke)) {
                        if (((PersonHealth) person).getGender().equals(Gender.MALE)) {
                            sickRate *= 1.83;
                        } else {
                            sickRate *= 2.28;
                        }
                    }
                }

                //
                sickProb = 1 - Math.exp(-sickRate);
                ((PersonHealth) person).getCurrentDiseaseProb().put(diseases, (float) sickProb);
            }
        }

        // Injuries
        boolean activateInjuries = true; // activate injuries here

        if(activateInjuries){
            // Prepare fatalities map by mode and age (gender ??)
            Map<String, Map<String, Double>> FatalityProbabilities = createProbabilityMap();
            CalibrationFactors calibrationFactors = new CalibrationFactors();

            // Set up the injury sampler and process the injured people
            InjurySampler injSampler = new InjurySampler(properties, calibrationFactors);

            // The target come from the accidentModel, they should be add to the HealthDataContainer
            /*
            Map<String, Integer> CasualtiesByMode = new HashMap<>();
            CasualtiesByMode.put("Car", 200);
            CasualtiesByMode.put("Walk", 300);
            CasualtiesByMode.put("Bike", 100);
             */

            // Process injury transitions
            injSampler.processInjuries2(dataContainer, FatalityProbabilities);
        }
    }

    private void initializeHealthDiseaseStates() {
        Map<Integer, List<Diseases>> prevData = ((HealthDataContainerImpl) dataContainer).getPrevalenceData();
        for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            if (prevData.keySet().contains(person.getId()) && (!prevData.get(person.getId()).contains(null))) {
                List<String> diseaseList = prevData.get(person.getId())  // Returns List<Diseases>
                        .stream()                                      // Convert List to Stream
                        .map(Enum::name)                               // Map each enum to its name
                        .collect(Collectors.toList());                 // Collect into List<String>
                ((PersonHealth) person).getHealthDiseaseTracker().put(Properties.get().main.startYear - 1, diseaseList);
            } else {
                //start year-1 as initial state
                ((PersonHealth) person).getHealthDiseaseTracker().put(Properties.get().main.startYear - 1, Arrays.asList("healthy"));
            }
        }
    }


    public void updateHealthDiseaseStates(int year) {
        for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            PersonHealth personHealth = (PersonHealth) person;
            List<String> newDisease = new ArrayList<>();

            for (Diseases diseases : Diseases.values()) {
                if (diseases.equals(Diseases.all_cause_mortality)) {
                    continue;
                }

                //if(diseases.equals(Diseases.killed_bike) || diseases.equals(Diseases.killed_walk) || diseases.equals(Diseases.killed_car)){
                //    continue;
                //}

                // diseases.equals(Diseases.severely_injured_car) || diseases.equals(Diseases.severely_injured_walk) || diseases.equals(Diseases.severely_injured_bike) ||

                if (personHealth.getCurrentDiseaseProb().get(diseases) == null) {
                    continue;
                }

                //TODO: control random number? survival equation
                if (random.nextFloat() < (personHealth.getCurrentDiseaseProb().get(diseases))) {
                    if (!personHealth.getCurrentDisease().contains(diseases)) {
                        personHealth.getCurrentDisease().add(diseases);
                        newDisease.add(diseases.toString());
                    }
                }
            }

            //update Disease track map
            if (newDisease.isEmpty()) {
                if (personHealth.getHealthDiseaseTracker().get(year - 1) == null) { // todo: min(keysetTracker) == currentYear ??
                    // newborns
                    personHealth.getHealthDiseaseTracker().put(year, Arrays.asList("healthy")); // todo: check if redundant ?
                } else {
                    //for base year, year-1 is the initial state "healthy"
                    personHealth.getHealthDiseaseTracker().put(year, personHealth.getHealthDiseaseTracker().get(year - 1));
                }
            } else {
                List<String> fullDisease = new ArrayList<>();
                if (personHealth.getHealthDiseaseTracker().get(year - 1) != null) {
                    fullDisease.addAll(personHealth.getHealthDiseaseTracker().get(year - 1)); // get old diseases
                }
                fullDisease.addAll(newDisease); // add new diseases
                fullDisease.remove("healthy");
                personHealth.getHealthDiseaseTracker().put(year, fullDisease);
            }
        }
    }

    public Map<String, Map<String, Double>> createProbabilityMap() {
        Map<String, Map<String, Double>> fatalityProbabilities = new HashMap<>();

        // Cyclist probabilities
        Map<String, Double> cyclistProbs = new HashMap<>();
        cyclistProbs.put("<18", 0.0588);   // 6 / (6 + 96)
        cyclistProbs.put("18-65", 0.0220); // 11 / (11 + 489)
        cyclistProbs.put("65+", 0.0833);   // 2 / (2 + 22)
        fatalityProbabilities.put("Cyclist", cyclistProbs);

        // Driver probabilities
        Map<String, Double> driverProbs = new HashMap<>();
        driverProbs.put("<18", 0.0847);    // 10 / (10 + 108)
        driverProbs.put("18-65", 0.0760);  // 66 / (66 + 802)
        driverProbs.put("65+", 0.1090);    // 17 / (17 + 139)
        fatalityProbabilities.put("Driver", driverProbs);

        // Pedestrian probabilities
        Map<String, Double> pedestrianProbs = new HashMap<>();
        pedestrianProbs.put("<18", 0.0220); // 11 / (11 + 488)
        pedestrianProbs.put("18-65", 0.1000); // 73 / (73 + 657)
        pedestrianProbs.put("65+", 0.2170);   // 59 / (59 + 213)
        fatalityProbabilities.put("Pedestrian", pedestrianProbs);

        return fatalityProbabilities;
    }

    /*
    public String getAgeGroup(int age) {
        if (age < 18) return "<18";
        else if (age <= 65) return "18-65";
        else return "65+";
    }

    private void processInjuryRisk(Person person, String probabilityKey, Diseases fatalDisease, Diseases injuryDisease, Map<String, Map<String, Double>> FatalityTable) {
        PersonHealth personHealth = (PersonHealth) person;

        double pFatal = FatalityTable.get(probabilityKey).get(getAgeGroup(person.getAge()));

        if (random.nextDouble() < pFatal) {
            personHealth.getCurrentDiseaseProb().put(fatalDisease, 1.0f);
            personHealth.getCurrentDiseaseProb().put(injuryDisease, 0.0f);
        } else {
            personHealth.getCurrentDiseaseProb().put(fatalDisease, 0.0f);
            personHealth.getCurrentDiseaseProb().put(injuryDisease, 1.0f);
        }
    }
     */
}
