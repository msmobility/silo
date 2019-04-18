package de.tum.bgu.msm.models.relocation;

/*
 * Implementation of the MovesModel Interface for the Munich implementation
 * @author Rolf Moeckel
 * Date: 20 May 2017, near Greenland in an altitude of 35,000 feet
 */

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.data.person.Nationality;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.travelTimes.TravelTimes;
import de.tum.bgu.msm.models.relocation.moves.AbstractMovesModelImpl;
import de.tum.bgu.msm.models.relocation.moves.DwellingProbabilityStrategy;
import de.tum.bgu.msm.models.relocation.moves.MovesStrategy;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix1D;
import de.tum.bgu.msm.utils.SiloUtil;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MovesModelMuc extends AbstractMovesModelImpl {
    private final static Logger logger = Logger.getLogger(MovesModelMuc.class);

    int requestsDwellingSearch = 0;

    private FileWriter writer;
    private int year;


    private final DwellingUtilityStrategy dwellingUtilityStrategy;
    private final DwellingProbabilityStrategy dwellingProbabilityStrategy;
    private final SelectRegionStrategy selectRegionStrategy;
    private EnumMap<IncomeCategory, EnumMap<Nationality, Map<Integer, Double>>> utilityByIncomeByNationalityByRegion = new EnumMap<>(IncomeCategory.class);

    private IndexedDoubleMatrix1D regionalShareForeigners;
    private IndexedDoubleMatrix1D hhByRegion;

    public MovesModelMuc(DataContainer dataContainer, Properties properties, MovesStrategy movesStrategy,
                         DwellingUtilityStrategy dwellingUtilityStrategy,
                         DwellingProbabilityStrategy dwellingProbabilityStrategy,
                         SelectRegionStrategy selectRegionStrategy) {
        super(dataContainer, properties, movesStrategy);
        this.dwellingUtilityStrategy = dwellingUtilityStrategy;
        this.dwellingProbabilityStrategy = dwellingProbabilityStrategy;
        this.selectRegionStrategy = selectRegionStrategy;
    }

    @Override
    public void setup() {
        regionalShareForeigners = new IndexedDoubleMatrix1D(geoData.getRegions().values());
        hhByRegion = new IndexedDoubleMatrix1D(geoData.getRegions().values());
        super.setup();
    }

    @Override
    public void prepareYear(int year) {
        super.prepareYear(year);
        this.year = year;
    }

    @Override
    public void endYear(int year) {
    }

    @Override
    public void endSimulation() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void calculateShareOfForeignersByZoneAndRegion() {
        final IndexedDoubleMatrix1D hhByZone = new IndexedDoubleMatrix1D(geoData.getZones().values());
        regionalShareForeigners.assign(0);
        hhByRegion.assign(0);
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            int zone = -1;
            Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                zone = dwelling.getZoneId();
            }
            final int region = geoData.getZones().get(zone).getRegion().getId();
            hhByZone.setIndexed(zone, hhByZone.getIndexed(zone) + 1);
            hhByRegion.setIndexed(region, hhByRegion.getIndexed(region) + 1);

            if (((HouseholdMuc) hh).getNationality() != Nationality.GERMAN) {
                regionalShareForeigners.setIndexed(region, regionalShareForeigners.getIndexed(region) + 1);
            }
        }

        regionalShareForeigners.assign(hhByRegion, (foreignerShare, numberOfHouseholds) -> {
            if (numberOfHouseholds > 0) {
                return foreignerShare / numberOfHouseholds;
            } else {
                return 0;
            }
        });
    }

//    private double convertDistToWorkToUtil (Household hh, int homeZone) {
//        // convert distance to work and school to utility
//        double util = 1;
//        for (Person p: hh.getPersons()) {
//            if (p.getOccupation() == 1 && p.getWorkplace() != -2) {
//                int workZone = Job.getJobFromId(p.getWorkplace()).getZone();
//                int travelTime = (int) SiloUtil.rounder(siloModelContainer.getAcc().getAutoTravelTime(homeZone, workZone),0);
//                util = util * siloModelContainer.getAcc().getCommutingTimeProbability(travelTime);
//            }
//        }
//        return util;
//    }


//    private double convertTravelCostsToUtility (Household hh, int homeZone) {
//        // convert travel costs to utility
//        double util = 1;
//        float workTravelCostsGasoline = 0;
//        for (Person p: hh.getPersons()) if (p.getOccupation() == 1 && p.getWorkplace() != -2) {
//            int workZone = Job.getJobFromId(p.getWorkplace()).getZone();
//            // yearly commute costs with 251 work days over 12 months, doubled to account for return trip
//            workTravelCostsGasoline += siloModelContainer.getAcc().getTravelCosts(homeZone, workZone) * 251f * 2f;
//        }
//        // todo: Create more plausible utilities
//        // Assumptions: Transportation costs are 5.9-times higher than expenditures for gasoline (https://www.census.gov/compendia/statab/2012/tables/12s0688.xls)
//        // Households spend 19% of their income on transportation, and 70% thereof is not
//        // work-related (but HBS, HBO, NHB, etc. trips)
//        float travelCosts = workTravelCostsGasoline * 5.9f + (hh.getHhIncome() * 0.19f * 0.7f);
//        if (travelCosts > (hh.getHhIncome() * 0.19f)) util = 0.5;
//        if (travelCosts > (hh.getHhIncome() * 0.25f)) util = 0.4;
//        if (travelCosts > (hh.getHhIncome() * 0.40f)) util = 0.2;
//        if (travelCosts > (hh.getHhIncome() * 0.50f)) util = 0.0;
//        return util;
//    }


    @Override
    public void calculateRegionalUtilities() {
        logger.info("Calculating regional utilities");
        calculateShareOfForeignersByZoneAndRegion();
        final Map<Integer, Double> rentsByRegion = calculateRegionalPrices();
        for (IncomeCategory incomeCategory : IncomeCategory.values()) {
            EnumMap<Nationality, Map<Integer, Double>> utilityByNationalityByRegion = new EnumMap<>(Nationality.class);
            for (Nationality nationality : Nationality.values()) {
                Map<Integer, Double> utilityByRegion = new LinkedHashMap<>();
                for (Region region : geoData.getRegions().values()) {
                    final int averageRegionalRent = rentsByRegion.get(region.getId()).intValue();
                    final float regAcc = (float) convertAccessToUtility(accessibility.getRegionalAccessibility(region));
                    float priceUtil = (float) convertPriceToUtility(averageRegionalRent, incomeCategory);
                    utilityByRegion.put(region.getId(),
                            selectRegionStrategy.calculateSelectRegionProbability(incomeCategory,
                                    nationality, priceUtil, regAcc, (float) regionalShareForeigners.getIndexed(region.getId())));

                }
                utilityByNationalityByRegion.put(nationality, utilityByRegion);
            }
            utilityByIncomeByNationalityByRegion.put(incomeCategory, utilityByNationalityByRegion);
        }

    }

    @Override
    protected boolean isHouseholdEligibleToLiveHere(Household household, Dwelling dd) {
        return true;
    }

    private Map<Integer, Double> getUtilitiesByRegionForThisHousehold(HouseholdType ht, Nationality nationality, Collection<Zone> workZones) {
        Map<Integer, Double> utilitiesForThisHousheold
                = new LinkedHashMap<>(utilityByIncomeByNationalityByRegion.get(ht.getIncomeCategory()).get(nationality));

        for (Region region : geoData.getRegions().values()) {
            double thisRegionFactor = 1;
            if (workZones != null) {
                for (Zone workZone : workZones) {
                    int timeFromZoneToRegion = (int) dataContainer.getTravelTimes().getTravelTimeToRegion(
                            workZone, region, properties.transportModel.peakHour_s, TransportMode.car);
                    thisRegionFactor = thisRegionFactor * commutingTimeProbability.getCommutingTimeProbability(timeFromZoneToRegion);
                }
            }
            utilitiesForThisHousheold.put(region.getId(), utilitiesForThisHousheold.get(region.getId()) * thisRegionFactor);
        }
        return utilitiesForThisHousheold;
    }

    @Override
    public int searchForNewDwelling(Household household) {
        requestsDwellingSearch++;
        if (requestsDwellingSearch % 5000 == 0) {
            logger.info("Number of searches for dwelling so far: " + requestsDwellingSearch);
        }
        // search alternative dwellings

        // data preparation
        int householdIncome = 0;
        Nationality nationality = ((HouseholdMuc) household).getNationality();
        Set<Zone> workerZonesForThisHousehold = new LinkedHashSet<>();
        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                Zone workZone = geoData.getZones().get(jobDataManager.getJobFromId(pp.getJobId()).getZoneId());
                workerZonesForThisHousehold.add(workZone);
                householdIncome += pp.getIncome();
            }
        }

        HouseholdType ht = HouseholdUtil.defineHouseholdType(household);

        // Step 1: select region
        Map<Integer, Double> regionUtilitiesForThisHousehold = new LinkedHashMap<>();
        regionUtilitiesForThisHousehold.putAll(getUtilitiesByRegionForThisHousehold(ht, nationality, workerZonesForThisHousehold));

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
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * hhByRegion.getIndexed(region));
                }
                case ("noNormalization"): {
                    // do nothing
                }
                case ("powerOfPopulation"): {
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * Math.pow(hhByRegion.getIndexed(region), 0.5));
                }
            }
        }


        int selectedRegionId;
        if (regionUtilitiesForThisHousehold.values().stream().mapToDouble(i -> i).sum() == 0) {
            return -1;
        } else {
            selectedRegionId = SiloUtil.select(regionUtilitiesForThisHousehold);
        }

        //todo debugging
//        for(Person worker : workerZonesForThisHousehold.keySet()){
//            pw.println(year + "," +
//                    worker.getHh().getZoneId() + "," +
//                    worker.getZoneId() + "," +
//                    dataContainer.getJobDataManager().getJobFromId(worker.getWorkplace()).getZone() + "," +
//                    selectedRegionId  + "," +
//                    accessibility.getMinTravelTimeFromZoneToRegion(dataContainer.getJobDataManager().getJobFromId(worker.getWorkplace()).getZone(), selectedRegionId));
//        }


        // Step 2: select vacant dwelling in selected region
        int[] vacantDwellings = realEstateDataManager.getListOfVacantDwellingsInRegion(selectedRegionId);
        double[] expProbs = SiloUtil.createArrayWithValue(vacantDwellings.length, 0d);
        double sumProbs = 0.;
        int maxNumberOfDwellings = Math.min(20, vacantDwellings.length);  // No household will evaluate more than 20 dwellings
        float factor = ((float) maxNumberOfDwellings / (float) vacantDwellings.length);
        for (int i = 0; i < vacantDwellings.length; i++) {
            if (SiloUtil.getRandomNumberAsFloat() > factor) continue;
            Dwelling dd = realEstateDataManager.getDwelling(vacantDwellings[i]);
            //
            double util = calculateHousingUtility(household, dd, dataContainer.getTravelTimes()); // interesting part!
            //
            expProbs[i] = dwellingProbabilityStrategy.calculateSelectDwellingProbability(util);
            sumProbs = +expProbs[i];
        }
        if (sumProbs == 0) {
            // could not find dwelling that fits restrictions
            return -1;
        }
        int selected = SiloUtil.select(expProbs, sumProbs);
        return vacantDwellings[selected];
    }

    @Override
    protected double calculateHousingUtility(Household hh, Dwelling dd, TravelTimes travelTimes) {
        double ddQualityUtility = convertQualityToUtility(dd.getQuality());
        double ddSizeUtility = convertAreaToUtility(dd.getBedrooms());
        double ddAutoAccessibilityUtility = convertAccessToUtility(accessibility.getAutoAccessibilityForZone(geoData.getZones().get(dd.getZoneId())));
        double transitAccessibilityUtility = convertAccessToUtility(accessibility.getTransitAccessibilityForZone(geoData.getZones().get(dd.getZoneId())));
        HouseholdType ht = hh.getHouseholdType();
        double ddPriceUtility = convertPriceToUtility(dd.getPrice(), ht);


        //currently this is re-filtering persons to find workers (it was done previously in select region)
        // This way looks more flexible to account for other trips, such as education, though.

        double travelCostUtility = 1; //do not have effect at the moment;

        Map<Person, JobMuc> jobsForThisHousehold = new LinkedHashMap<>();
        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        for (Person pp : hh.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                JobMuc workLocation = Objects.requireNonNull((JobMuc) jobDataManager.getJobFromId(pp.getJobId()));
                jobsForThisHousehold.put(pp, workLocation);
            }
        }
        double workDistanceUtility = 1;
        for (JobMuc workLocation : jobsForThisHousehold.values()) {
            // TODO Think about how to apply this for other modes as well
            int expectedCommuteTime = (int) travelTimes.getTravelTime(dd, workLocation, workLocation.getStartTimeInSeconds(), TransportMode.car);

            double factorForThisZone = commutingTimeProbability.getCommutingTimeProbability(Math.max(1, expectedCommuteTime));
            workDistanceUtility *= factorForThisZone;
        }

        return dwellingUtilityStrategy.calculateSelectDwellingUtility(ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, workDistanceUtility);
    }
}
