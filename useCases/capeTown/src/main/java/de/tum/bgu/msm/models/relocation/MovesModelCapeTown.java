package de.tum.bgu.msm.models.relocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonCapeTown;
import de.tum.bgu.msm.data.person.RaceCapeTown;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.models.relocation.moves.AbstractMovesModelImpl;
import de.tum.bgu.msm.models.relocation.moves.DwellingProbabilityStrategy;
import de.tum.bgu.msm.models.relocation.moves.MovesStrategy;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix1D;
import de.tum.bgu.msm.utils.SiloUtil;
import org.matsim.api.core.v01.TransportMode;

import java.util.*;

public class MovesModelCapeTown extends AbstractMovesModelImpl {

    private final DwellingUtilityStrategyCapeTown ddUtilityStrategy;
    private final SelectRegionStrategyCapeTown regionStrategy;
    private final DwellingProbabilityStrategy ddProbabilityStrategy;

    private final Map<Integer, Map<RaceCapeTown, Double>> personShareByRaceByRegion = new HashMap<>();
    private final Map<Integer, Map<RaceCapeTown, Double>> personShareByRaceByZone = new HashMap<>();

    private IndexedDoubleMatrix1D ppByRegion;
    private IndexedDoubleMatrix1D ppByZone;

    private final Map<IncomeCategory, Map<RaceCapeTown, Map<Integer, Double>>> utilityByRegionByRaceByIncome = new EnumMap<>(IncomeCategory.class);

    public MovesModelCapeTown(DataContainer dataContainer, Properties properties,
                              MovesStrategy movesStrategy, DwellingUtilityStrategyCapeTown ddUtilityStrategy,
                              SelectRegionStrategyCapeTown regionStrategy, DwellingProbabilityStrategy ddProbabilityStrategy) {
        super(dataContainer, properties, movesStrategy);
        this.ddUtilityStrategy = ddUtilityStrategy;
        this.regionStrategy = regionStrategy;
        this.ddProbabilityStrategy = ddProbabilityStrategy;
    }

    @Override
    public void setup() {
        ppByRegion = new IndexedDoubleMatrix1D(geoData.getRegions().values());
        ppByZone = new IndexedDoubleMatrix1D(geoData.getZones().values());
        super.setup();
    }

    @Override
    protected double calculateHousingUtility(Household hh, Dwelling dd, TravelTimes travelTimes) {
        double ddQualityUtility = convertQualityToUtility(dd.getQuality());
        double ddSizeUtility = convertAreaToUtility(dd.getBedrooms());
        Zone zone = geoData.getZones().get(dd.getZoneId());
        double ddAutoAccessibilityUtility = convertAccessToUtility(accessibility.getAutoAccessibilityForZone(zone));
        double transitAccessibilityUtility = convertAccessToUtility(accessibility.getTransitAccessibilityForZone(zone));
        HouseholdType ht = hh.getHouseholdType();
        double ddPriceUtility = convertPriceToUtility(dd.getPrice(), ht);

        //currently this is re-filtering persons to find workers (it was done previously in select region)
        // This way looks more flexible to account for other trips, such as education, though.

        double travelCostUtility = 1; //do not have effect at the moment;

        Map<Person, Job> jobsForThisHousehold = new HashMap<>();
        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        for (Person pp : hh.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                Job workLocation = Objects.requireNonNull(jobDataManager.getJobFromId(pp.getJobId()));
                jobsForThisHousehold.put(pp, workLocation);
            }
        }
        double workDistanceUtility = 1;
        for (Job workLocation : jobsForThisHousehold.values()) {
            double travelTime_s = travelTimes.getTravelTime(dd, workLocation, properties.transportModel.peakHour_s, TransportMode.car);
            double factorForThisZone = dataContainer.getCommutingTimeProbability().getCommutingTimeProbability(Math.max(1, (int) (travelTime_s / 60.)));
            workDistanceUtility *= factorForThisZone;
        }
        return ddUtilityStrategy.calculateSelectDwellingUtility(ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, workDistanceUtility);
    }

    @Override
    protected void calculateRegionalUtilities() {
        logger.info("Calculating regional utilities");
        utilityByRegionByRaceByIncome.clear();
        calculateRacialSharesByZoneAndRegion();
        final Map<Integer, Double> rentsByRegion = calculateRegionalPrices();
        for (IncomeCategory incomeCategory : IncomeCategory.values()) {
            EnumMap<RaceCapeTown, Map<Integer, Double>> utilityByRegionByRace = new EnumMap<>(RaceCapeTown.class);
            for (RaceCapeTown race : RaceCapeTown.values()) {
                Map<Integer, Double> utilityByRegion = new HashMap<>();
                for (Region region : geoData.getRegions().values()) {
                    if (!rentsByRegion.containsKey(region.getId())) {
                        continue;
                    }
                    final int averageRegionalRent = rentsByRegion.get(region.getId()).intValue();
                    final float regAcc = (float) convertAccessToUtility(accessibility.getRegionalAccessibility(region));
                    float priceUtil = (float) convertPriceToUtility(averageRegionalRent, incomeCategory);
                    utilityByRegion.put(region.getId(),
                            regionStrategy.calculateSelectRegionProbability(incomeCategory,
                                    race, priceUtil, regAcc, personShareByRaceByRegion.get(region.getId()).get(race)));
                }
                utilityByRegionByRace.put(race, utilityByRegion);
            }
            utilityByRegionByRaceByIncome.put(incomeCategory, utilityByRegionByRace);
        }
    }

    private void calculateRacialSharesByZoneAndRegion() {
        ppByRegion.assign(0);
        ppByZone.assign(0);

        for (Region region : geoData.getRegions().values()) {
            EnumMap<RaceCapeTown, Double> regionalRacialShare = new EnumMap<>(RaceCapeTown.class);
            for (RaceCapeTown race : RaceCapeTown.values()) {
                regionalRacialShare.put(race, 0.);
            }
            personShareByRaceByRegion.put(region.getId(), regionalRacialShare);
            for (Zone zone : region.getZones()) {
                EnumMap<RaceCapeTown, Double> zonalRacialShare = new EnumMap<>(RaceCapeTown.class);
                for (RaceCapeTown race : RaceCapeTown.values()) {
                    zonalRacialShare.put(race, 0.);
                }
                personShareByRaceByZone.put(zone.getZoneId(), zonalRacialShare);
            }
        }

        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            int zone = -1;
            Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                zone = dwelling.getZoneId();
            }
            final int region = geoData.getZones().get(zone).getRegion().getId();
            for (Person person : hh.getPersons().values()) {
                RaceCapeTown race = ((PersonCapeTown) person).getRace();

                personShareByRaceByRegion.get(region).merge(race, 1., (oldValue, newValue) -> oldValue + newValue);
                personShareByRaceByZone.get(zone).merge(race, 1., (oldValue, newValue) -> oldValue + newValue);

                ppByZone.setIndexed(zone, ppByZone.getIndexed(zone) + 1);
                ppByRegion.setIndexed(region, ppByRegion.getIndexed(region) + 1);
            }
        }

        for (Region region : geoData.getRegions().values()) {
            personShareByRaceByRegion.get(region.getId()).replaceAll((raceCapeTown, count) -> {
                if (ppByRegion.getIndexed(region.getId()) == 0.) {
                    return 0.;
                } else {
                    return count / ppByRegion.getIndexed(region.getId());
                }
            });
            for (Zone zone : region.getZones()) {
                personShareByRaceByZone.get(zone.getZoneId()).replaceAll((raceCapeTown, count) -> {
                    if (ppByZone.getIndexed(zone.getZoneId()) == 0.) {
                        return 0.;
                    } else {
                        return count / ppByZone.getIndexed(zone.getZoneId());
                    }
                });
            }
        }
    }

    @Override
    protected boolean isHouseholdEligibleToLiveHere(Household household, Dwelling dd) {
        return true;
    }

    @Override
    public int searchForNewDwelling(Household household) {
        // search alternative dwellings

        // data preparation
        int householdIncome = 0;
        Map<Person, Zone> workerZonesForThisHousehold = new HashMap<>();
        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                Zone workZone = geoData.getZones().get(jobDataManager.getJobFromId(pp.getJobId()).getZoneId());
                workerZonesForThisHousehold.put(pp, workZone);
                householdIncome += pp.getIncome();
            }
        }

        HouseholdType ht = HouseholdUtil.defineHouseholdType(household);
        RaceCapeTown race = ((HouseholdCapeTown) household).getRace();

        // Step 1: select region
        Map<Integer, Double> regionUtilitiesForThisHousehold = new HashMap<>();
        regionUtilitiesForThisHousehold.putAll(getUtilitiesByRegionForThisHousehold(ht, race, workerZonesForThisHousehold.values()));

        // todo: adjust probabilities to make that households tend to move shorter distances (dist to work is already represented)
        String normalizer = "powerOfPopulation";
        int totalVacantDd = 0;
        for (int region : geoData.getRegions().keySet()) {
            totalVacantDd += realEstateDataManager.getNumberOfVacantDDinRegion(region);
        }
        for (int region : regionUtilitiesForThisHousehold.keySet()) {
            switch (normalizer) {
                case ("vacDd"): {
                    // Multiply utility of every region by number of vacant dwellings to steer households towards available dwellings
                    // use number of vacant dwellings to calculate attractivity of region
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * (float) realEstateDataManager.getNumberOfVacantDDinRegion(region));
                }
                case ("shareVacDd"): {
                    // use share of empty dwellings to calculate attractivity of region
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * ((float) realEstateDataManager.getNumberOfVacantDDinRegion(region) / (float) totalVacantDd));
                }
                case ("dampenedVacRate"): {
                    double x = (double) realEstateDataManager.getNumberOfVacantDDinRegion(region) /
                            (double) realEstateDataManager.getNumberOfVacantDDinRegion(region) * 100d;  // % vacancy
                    double y = 1.4186E-03 * Math.pow(x, 3) - 6.7846E-02 * Math.pow(x, 2) + 1.0292 * x + 4.5485E-03;
                    y = Math.min(5d, y);                                                // % vacancy assumed to be ready to move in
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * (y / 100d * realEstateDataManager.getNumberOfVacantDDinRegion(region)));
                    if (realEstateDataManager.getNumberOfVacantDDinRegion(region) < 1) {
                        regionUtilitiesForThisHousehold.put(region, 0D);
                    }
                }
                case ("population"): {
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * ppByRegion.getIndexed(region));
                }
                case ("noNormalization"): {
                    // do nothing
                }
                case ("powerOfPopulation"): {
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * Math.pow(ppByRegion.getIndexed(region), 0.5));
                }
            }
        }

        int selectedRegionId;
        if (regionUtilitiesForThisHousehold.values().stream().mapToDouble(i -> i).sum() == 0) {
            return -1;
        } else {
            selectedRegionId = SiloUtil.select(regionUtilitiesForThisHousehold);
        }


        // Step 2: select vacant dwelling in selected region
        List<Dwelling> vacantDwellings = realEstateDataManager.getListOfVacantDwellingsInRegion(selectedRegionId);
        double[] dwellingProbs = new double[vacantDwellings.size()];

        // No household will evaluate more than 20 dwellings
        int maxNumberOfDwellings = Math.min(20, vacantDwellings.size());
        double sum = 0;
        for(int i = 0; i < maxNumberOfDwellings; i++) {
            Dwelling dwelling = vacantDwellings.get(SiloUtil.getRandomObject().nextInt(vacantDwellings.size()));
            double util = calculateHousingUtility(household, dwelling, dataContainer.getTravelTimes());
            double prob = ddProbabilityStrategy.calculateSelectDwellingProbability(util);
            dwellingProbs[i] = prob;
            sum += prob;
        }
        if (sum == 0) {
            // could not find dwelling that fits restrictions
            return -1;
        }
        final Dwelling select = vacantDwellings.get(SiloUtil.select(dwellingProbs, sum));
        return select.getId();
    }

    private Map<Integer, Double> getUtilitiesByRegionForThisHousehold(HouseholdType ht, RaceCapeTown race, Collection<Zone> workZones) {
        Map<Integer, Double> utilitiesForThisHousehold
                = new HashMap<>(utilityByRegionByRaceByIncome.get(ht.getIncomeCategory()).get(race));

        for (Region region : geoData.getRegions().values()) {
            if(!utilitiesForThisHousehold.containsKey(region.getId())) {
                continue;
            }
            double thisRegionFactor = 1;
            if (workZones != null) {
                for (Zone workZone : workZones) {
                    int timeFromZoneToRegion = (int) dataContainer.getTravelTimes().getTravelTimeToRegion(
                            workZone, region, properties.transportModel.peakHour_s, TransportMode.car);
                    thisRegionFactor = thisRegionFactor * dataContainer.getCommutingTimeProbability().getCommutingTimeProbability(timeFromZoneToRegion);
                }
            }
            utilitiesForThisHousehold.put(region.getId(), utilitiesForThisHousehold.get(region.getId()) * thisRegionFactor);
        }
        return utilitiesForThisHousehold;
    }

    @Override
    public void endSimulation() {

    }
}
