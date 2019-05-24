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
import de.tum.bgu.msm.utils.SampleException;
import de.tum.bgu.msm.utils.Sampler;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.TransportMode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;

public class MovesModelMuc extends AbstractMovesModelImpl {
    private final static Logger logger = Logger.getLogger(MovesModelMuc.class);

    private FileWriter writer;
    private int year;

    private final DwellingUtilityStrategy dwellingUtilityStrategy;
    private final DwellingProbabilityStrategy dwellingProbabilityStrategy;
    private final SelectRegionStrategy selectRegionStrategy;
    private EnumMap<IncomeCategory, EnumMap<Nationality, Sampler<Region>>> utilityByIncomeByNationalityByRegion = new EnumMap<>(IncomeCategory.class);

    private IndexedDoubleMatrix1D regionalShareForeigners;
    private IndexedDoubleMatrix1D hhByRegion;
    public static final String NORMALIZER = "powerOfPopulation";

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
//        try {
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
            EnumMap<Nationality, Sampler<Region>> utilityByNationalityByRegion = new EnumMap<>(Nationality.class);
            for (Nationality nationality : Nationality.values()) {
                Sampler<Region> regionSampler
                        = new Sampler<>(geoData.getRegions().size(), Region.class, SiloUtil.getRandomObject());
                for (Region region : geoData.getRegions().values()) {
                    final int averageRegionalRent = rentsByRegion.get(region.getId()).intValue();
                    final float regAcc = (float) convertAccessToUtility(accessibility.getRegionalAccessibility(region));
                    float priceUtil = (float) convertPriceToUtility(averageRegionalRent, incomeCategory);
                    regionSampler.incrementalAdd(region,
                            selectRegionStrategy.calculateSelectRegionProbability(incomeCategory,
                                    nationality, priceUtil, regAcc, (float) regionalShareForeigners.getIndexed(region.getId())));

                }
                utilityByNationalityByRegion.put(nationality, regionSampler);
            }
            utilityByIncomeByNationalityByRegion.put(incomeCategory, utilityByNationalityByRegion);
        }
    }

    @Override
    protected boolean isHouseholdEligibleToLiveHere(Household household, Dwelling dd) {
        return true;
    }

    private Sampler<Region> initializeByRegionSamplerForThisHousehold(Household household) {
        Set<Zone> workZones = new LinkedHashSet<>();
        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        for (Person pp : household.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                Zone workZone = geoData.getZones().get(jobDataManager.getJobFromId(pp.getJobId()).getZoneId());
                workZones.add(workZone);
            }
        }
        HouseholdType ht = HouseholdUtil.defineHouseholdType(household);
        Nationality nationality = ((HouseholdMuc) household).getNationality();

        Sampler<Region> sampler
                = utilityByIncomeByNationalityByRegion.get(ht.getIncomeCategory()).get(nationality).copy();

        sampler.updateProbabilities((region, oldValue) -> {
            double thisRegionFactor = 1;
            if (workZones != null) {
                for (Zone workZone : workZones) {
                    int timeFromZoneToRegion = (int) dataContainer.getTravelTimes().getTravelTimeToRegion(
                            workZone, region, properties.transportModel.peakHour_s, TransportMode.car);
                    thisRegionFactor = thisRegionFactor * commutingTimeProbability.getCommutingTimeProbability(timeFromZoneToRegion);
                }
            }
            return oldValue * thisRegionFactor;
        });

        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
        // todo: adjust probabilities to make that households tend to move shorter distances (dist to work is already represented)
        switch (NORMALIZER) {
            case ("shareVacDd"): {
                // use share of empty dwellings to calculate attractivity of region

                LongAdder totalVacantDd = new LongAdder();
                for (int region : geoData.getRegions().keySet()) {
                    totalVacantDd.add(realEstateDataManager.getNumberOfVacantDDinRegion(region));
                }
                sampler.updateProbabilities((region, oldValue) ->
                        oldValue * ((float) realEstateDataManager.getNumberOfVacantDDinRegion(region.getId()) / totalVacantDd.doubleValue()));
            }
            break;
            case ("vacDd"): {
                // Multiply utility of every region by number of vacant dwellings to steer households towards available dwellings
                // use number of vacant dwellings to calculate attractivity of region
                sampler.updateProbabilities((region, oldValue) -> oldValue * realEstateDataManager.getNumberOfVacantDDinRegion(region.getId()));
            }
            break;
            case ("dampenedVacRate"): {
                sampler.updateProbabilities((region, oldValue) -> {
                    int key = region.getId();
                    double x = (double) realEstateDataManager.getNumberOfVacantDDinRegion(key) /
                            (double) realEstateDataManager.getNumberOfVacantDDinRegion(key) * 100d;  // % vacancy
                    double y = 1.4186E-03 * Math.pow(x, 3) - 6.7846E-02 * Math.pow(x, 2) + 1.0292 * x + 4.5485E-03;
                    y = Math.min(5d, y);                                                // % vacancy assumed to be ready to move in
                    if (realEstateDataManager.getNumberOfVacantDDinRegion(key) < 1) {
                        return 0.;
                    }
                    return oldValue * (y / 100d * realEstateDataManager.getNumberOfVacantDDinRegion(key));
                });
            }
            break;
            case ("population"): {
                sampler.updateProbabilities((region, oldValue) -> oldValue * hhByRegion.getIndexed(region.getId()));
            }
            break;
            case ("powerOfPopulation"): {
                sampler.updateProbabilities((region, oldvalue) -> oldvalue * Math.pow(hhByRegion.getIndexed(region.getId()), 0.5));
            }
            break;
            default:
                //do nothing
        }

        return sampler;
    }

    @Override
    public int searchForNewDwelling(Household household) {

        // Step 1: select region
        Sampler<Region> regionSampler = initializeByRegionSamplerForThisHousehold(household);

        if (regionSampler.getCumulatedProbability() == 0.) {
            return -1;
        }
        Region selectedRegion;
        try {
            selectedRegion = regionSampler.sampleObject();
        } catch (SampleException e) {
            throw new RuntimeException(e);
        }

        // Step 2: select vacant dwelling in selected region
        List<Dwelling> vacantDwellings
                = dataContainer.getRealEstateDataManager().getListOfVacantDwellingsInRegion(selectedRegion.getId());
        if (vacantDwellings.isEmpty()) {
            return -1;
        }
        // No household will evaluate more than 20 dwellings
        int maxNumberOfDwellings = Math.min(20, vacantDwellings.size());

        Sampler<Dwelling> sampler = new Sampler<>(maxNumberOfDwellings, Dwelling.class, SiloUtil.getRandomObject());

        for (int i = 0; i < maxNumberOfDwellings; i++) {
            Dwelling dwelling = vacantDwellings.get(SiloUtil.getRandomObject().nextInt(vacantDwellings.size()));
            double util = calculateHousingUtility(household, dwelling, dataContainer.getTravelTimes());
            double probability = dwellingProbabilityStrategy.calculateSelectDwellingProbability(util);
            sampler.incrementalAdd(dwelling, probability);
        }
        try {
            return sampler.sampleObject().getId();
        } catch (SampleException e) {
            logger.warn(e.getMessage());
            return -1;
        }
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

        double workDistanceUtility = 1;
        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        for (Person pp : hh.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                JobMuc workLocation = Objects.requireNonNull((JobMuc) jobDataManager.getJobFromId(pp.getJobId()));
                int expectedCommuteTime = (int) travelTimes.getTravelTime(dd, workLocation, workLocation.getStartTimeInSeconds(), TransportMode.car);
                double factorForThisZone = commutingTimeProbability.getCommutingTimeProbability(Math.max(1, expectedCommuteTime));
                workDistanceUtility *= factorForThisZone;
            }
        }
//        double workdistanceUtilitySkim = 1;

//        Zone origin = geoData.getZones().get(dd.getZoneId());
//            Zone destination = geoData.getZones().get(workLocation.getZoneId());
        // TODO Think about how to apply this for other modes as well
//            int expectedCommuteTimePeak = (int) travelTimes.getTravelTime(dd, workLocation, Properties.get().transportModel.peakHour_s, TransportMode.car);
//            int expectedCommuteTimeZone = (int) travelTimes.getTravelTime(origin, destination, Properties.get().transportModel.peakHour_s, TransportMode.car);
//            int expectedCommuteTimeSkim = (int) travelTimes.getPeakSkim(TransportMode.car).getIndexed(dd.getZoneId(), workLocation.getZoneId());

        return dwellingUtilityStrategy.calculateSelectDwellingUtility(ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, workDistanceUtility);
    }
}
