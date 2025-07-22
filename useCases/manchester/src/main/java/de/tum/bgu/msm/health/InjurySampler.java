package de.tum.bgu.msm.health;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.health.data.PersonHealth;
import de.tum.bgu.msm.health.disease.Diseases;
import de.tum.bgu.msm.properties.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class InjurySampler {

    private static final Logger logger = LogManager.getLogger(HealthExposureModelMCR.class);
    private static final Random random = new Random();
    private CalibrationFactors calibrationFactors;
    private Properties properties;

    InjurySampler(Properties properties, CalibrationFactors calibrationFactors) {
        this.calibrationFactors = calibrationFactors;
        this.properties = properties;
    }

    /**
     * Samples injuries for a single mode and returns a list of injured person IDs.
     * @param targetInjured Number of target injuries for the mode.
     * @param dataContainer Data container with person data.
     * @param mode Transport mode (e.g., "Car", "Bike", "Walk").
     * @return List of injured person IDs.
     */
    public List<Integer> sampleInjuries(int targetInjured, DataContainer dataContainer, String mode) {
        List<Integer> personIds = new ArrayList<>();
        List<Double> probabilities = new ArrayList<>();
        double totalRisk = 0.0;

        // Collect injury risks for the mode
        for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            double injuryRisk = ((PersonHealth) person).getWeeklyAccidentRisk("severeFatalInjury" + mode);
            if (injuryRisk > 0) {
                int personId = person.getId();
                //injuryRiskMap.put(personId, injuryRisk);
                personIds.add(personId);
                probabilities.add(injuryRisk);
                totalRisk += injuryRisk;
                logger.info("Injury risk " + injuryRisk + " for person " + personId + " in mode " + mode);
            }
        }

        // Normalize probabilities
        List<Double> normalizedProbabilities = new ArrayList<>();
        for (Double prob : probabilities) {
            normalizedProbabilities.add(totalRisk > 0 ? prob / totalRisk : 0.0);
        }

        // Sample without replacement
        List<Integer> injuredPersons = new ArrayList<>();
        int maxPossibleInjuries = Math.min(targetInjured, personIds.size());
        int injuredCount = 0;

        while (injuredCount < maxPossibleInjuries) {
            double rand = random.nextDouble();
            double cumulativeProb = 0.0;

            for (int i = 0; i < normalizedProbabilities.size(); i++) {
                cumulativeProb += normalizedProbabilities.get(i);
                if (rand <= cumulativeProb) {
                    int selectedPersonId = personIds.get(i);
                    injuredPersons.add(selectedPersonId);
                    injuredCount++;

                    // Remove person from sampling pool (sampling without replacement)
                    personIds.remove(i);
                    probabilities.remove(i);
                    normalizedProbabilities.remove(i);

                    // Renormalize remaining probabilities
                    if (!probabilities.isEmpty()) {
                        totalRisk = probabilities.stream().mapToDouble(Double::doubleValue).sum();
                        normalizedProbabilities.clear();
                        for (Double prob : probabilities) {
                            normalizedProbabilities.add(totalRisk > 0 ? prob / totalRisk : 0.0);
                        }
                    }
                    break;
                }
            }
        }

        // Log results
        for (int personId : injuredPersons) {
            logger.info("Person " + personId + " was injured in mode " + mode);
        }
        logger.info("Total injured: " + injuredCount + " out of target " + targetInjured + " for mode " + mode);

        return injuredPersons;
    }

    public List<Integer> sampleInjuries2(DataContainer dataContainer, String mode) {
        List<Integer> injuredPersons = new ArrayList<>();

        // Collect injury risks for the mode
        for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            double injuryRisk = ((PersonHealth) person).getWeeklyAccidentRisk("severeFatalInjury" + mode);

            // adjust injury risk by applying age/gender relative risks + finalCalibration to fit link based stats
            injuryRisk = injuryRisk * getCasualtyRR_byAge_Gender(person.getGender(), person.getAge(), mode, (HealthDataContainerImpl) dataContainer) * calibrationFactors.getCalibrationFactor(properties.main.scenarioName, mode) ;

            if (random.nextDouble() < injuryRisk) {
                injuredPersons.add(person.getId());
            }
        }

        // Log results ??
        return injuredPersons;
    }

    double getCasualtyRR_byAge_Gender(Gender gender, int age, String mode, HealthDataContainerImpl dataContainer) {
        // Parameter validation
        if (gender == null || mode == null) {
            throw new IllegalArgumentException("Gender and mode cannot be null");
        }

        final int MIN_AGE = 0;
        final int MAX_AGE = 100;

        // Clamp age value
        age = Math.max(MIN_AGE, Math.min(age, MAX_AGE));

        // Determine mode string
        String modeStr;
        switch (mode) {
            case "Car":
                modeStr = "Driver";
                break;
            case "Bike":
                modeStr = "Cyclist";
                break;
            case "Walk":
                modeStr = "Pedestrian";
                break;
            default:
                logger.warn("Impossible to compute injury relative risk for mode " + mode);
                return 1.0; // Consider if this is the appropriate default
        }

        // Safely retrieve the value
        try {
            return ((HealthDataContainerImpl) dataContainer)
                    .getHealthInjuryRRdata()
                    .get(modeStr)
                    .get(gender)
                    .get(age);
        } catch (NullPointerException e) {
            logger.error("Missing data for mode: " + modeStr + ", gender: " + gender + ", age: " + age, e);
            return 1.0; // Or consider throwing an exception
        }
    }

    /**
     * Processes injuries for multiple modes and updates disease probabilities.
     * @param dataContainer Data container with person data.
     * @param targetInjured Map of mode to target number of injuries.
     * @param fatalityProbabilities Map of mode to person ID to fatality probability.
     */

    public void processInjuries(DataContainer dataContainer, Map<String, Integer> targetInjured, Map<String, Map<String, Double>> fatalityProbabilities) {
        List<String> modes = Arrays.asList("Car", "Bike", "Walk");
        //Map<String, Map<Integer, Double>> injuryRiskMaps = new HashMap<>();
        Map<String, List<Integer>> injuredPersonsByMode = new HashMap<>();

        // Sample injuries for each mode
        for (String mode : modes) {
            //Map<Integer, Double> injuryRiskMap = new HashMap<>();
            List<Integer> injuredPersons = sampleInjuries(targetInjured.getOrDefault(mode, 0), dataContainer, mode);
            //injuryRiskMaps.put(mode, injuryRiskMap);
            injuredPersonsByMode.put(mode, injuredPersons);
        }

        // Process each person for all modes
        // todo: there is a theoretical probability that an agent is injured by multiple modes during the sample sampling iteration

        for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            int personId = person.getId();
            PersonHealth personHealth = (PersonHealth) person;

            for (String mode : modes) {
                String modeAlias = getModeAlias(mode);
                Diseases severeInjury = getSevereInjuryDisease(mode);
                Diseases fatalInjury = getFatalInjuryDisease(mode);

                if (injuredPersonsByMode.get(mode).contains(personId)) {
                    // Process injury for this mode
                    processInjuryRisk(person, modeAlias, fatalInjury, severeInjury, fatalityProbabilities);
                } else {
                    // Set zero probability for non-injured persons
                    personHealth.getCurrentDiseaseProb().putIfAbsent(severeInjury, 0.0f);
                    personHealth.getCurrentDiseaseProb().putIfAbsent(fatalInjury, 0.0f);
                }
            }
        }
    }

    public void processInjuries2(DataContainer dataContainer, Map<String, Map<String, Double>> fatalityProbabilities) {
        List<String> modes = Arrays.asList("Car", "Bike", "Walk");
        Map<String, List<Integer>> injuredPersonsByMode = new HashMap<>();

        // Sample injuries for each mode
        for (String mode : modes) {
            List<Integer> injuredPersons = sampleInjuries2(dataContainer, mode);
            injuredPersonsByMode.put(mode, injuredPersons);
        }

        // Process each person for all modes
        // todo: there is a theoretical probability that an agent is injured by multiple modes during the sample sampling iteration

        for (Person person : dataContainer.getHouseholdDataManager().getPersons()) {
            int personId = person.getId();
            PersonHealth personHealth = (PersonHealth) person;

            for (String mode : modes) {
                String modeAlias = getModeAlias(mode);
                Diseases severeInjury = getSevereInjuryDisease(mode);
                Diseases fatalInjury = getFatalInjuryDisease(mode);

                if (injuredPersonsByMode.get(mode).contains(personId)) {
                    // Process injury for this mode
                    processInjuryRisk(person, modeAlias, fatalInjury, severeInjury, fatalityProbabilities);
                } else {
                    // Set zero probability for non-injured persons
                    personHealth.getCurrentDiseaseProb().putIfAbsent(severeInjury, 0.0f);
                    personHealth.getCurrentDiseaseProb().putIfAbsent(fatalInjury, 0.0f);
                }
            }
        }
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

    private String getAgeGroup(int age) {
        if (age < 18) return "<18";
        else if (age <= 65) return "18-65";
        else return "65+";
    }

    private String getModeAlias(String mode) {
        switch (mode) {
            case "Car":
                return "Driver";
            case "Bike":
                return "Cyclist";
            case "Walk":
                return "Pedestrian";
            default:
                throw new IllegalArgumentException("Unknown mode: " + mode);
        }
    }

    private Diseases getSevereInjuryDisease(String mode) {
        switch (mode) {
            case "Car":
                return Diseases.severely_injured_car;
            case "Bike":
                return Diseases.severely_injured_bike;
            case "Walk":
                return Diseases.severely_injured_walk;
            default:
                throw new IllegalArgumentException("Unknown mode: " + mode);
        }
    }

    private Diseases getFatalInjuryDisease(String mode) {
        switch (mode) {
            case "Car":
                return Diseases.killed_car;
            case "Bike":
                return Diseases.killed_bike;
            case "Walk":
                return Diseases.killed_walk;
            default:
                throw new IllegalArgumentException("Unknown mode: " + mode);
        }
    }
}