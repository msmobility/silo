package de.tum.bgu.msm.models.relocation.mstm;

/*
* @author Rolf Moeckel (PB Albuquerque)
* Created on Apr 4, 2011 in Albuquerque, NM
* Revised on Apr 24, 2014 in College Park, MD
*/

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.JobData;
import de.tum.bgu.msm.data.RealEstateData;
import de.tum.bgu.msm.data.Region;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdType;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.maryland.MstmRegion;
import de.tum.bgu.msm.data.maryland.MstmZone;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.Race;
import de.tum.bgu.msm.models.relocation.AbstractDefaultMovesModel;
import de.tum.bgu.msm.models.relocation.SelectDwellingJSCalculator;
import de.tum.bgu.msm.models.relocation.SelectRegionJSCalculator;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.Matrices;
import de.tum.bgu.msm.utils.SiloUtil;
import org.matsim.api.core.v01.TransportMode;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

//import com.pb.common.calculator.UtilityExpressionCalculator;
//import de.tum.bgu.msm.models.relocation.MovesDMU;

public class MovesModelMstm extends AbstractDefaultMovesModel {

    private SelectRegionJSCalculator regionCalculator;
    private EnumMap<IncomeCategory, EnumMap<Race, Map<Integer, Double>>> utilityByIncomeRaceRegion = new EnumMap<>(IncomeCategory.class) ;

    private DoubleMatrix2D zonalRacialComposition;
    private DoubleMatrix2D regionalRacialComposition;
    private DoubleMatrix1D hhByRegion;
    private double selectDwellingRaceRelevance;
    private boolean provideRentSubsidyToLowIncomeHh;

    private SelectDwellingJSCalculator dwellingCalculator;
    private MstmDwellingUtilityJSCalculator mstmDwellingUtilityJSCalculator;

    private final Reader selectDwellingReader;
    private final Reader ddUtilityReader;

    public MovesModelMstm(DataContainer dataContainer, Properties properties, InputStream movesOrNotInputStream, InputStream selectDwellingInputStream, InputStream ddUtilityInputStream) {
        super(dataContainer, properties, movesOrNotInputStream);
        this.selectDwellingReader = new InputStreamReader(selectDwellingInputStream);
        this.ddUtilityReader = new InputStreamReader(ddUtilityInputStream);
    }

    @Override
    public void setup() {
        super.setup();
        setupUtilityCalculators();
        selectDwellingRaceRelevance = properties.moves.racialRelevanceInZone;
        provideRentSubsidyToLowIncomeHh = properties.moves.provideLowIncomeSubsidy;
        if (provideRentSubsidyToLowIncomeHh) {
            dataContainer.getRealEstateData().calculateMedianRentByMSA();
        }
    }

    @Override
    public void endSimulation() {

    }

    private void calculateRacialCompositionByZoneAndRegion() {
        resetMatrices();
        updateHouseholdInventar();
        scaleMatrices();
    }

    private void resetMatrices() {
        zonalRacialComposition = Matrices.doubleMatrix2D(geoData.getZones().values(), Arrays.asList(Race.values()));
        regionalRacialComposition = Matrices.doubleMatrix2D(geoData.getRegions().values(), Arrays.asList(Race.values()));
        hhByRegion = Matrices.doubleMatrix1D(geoData.getZones().values());
        regionalRacialComposition.assign(0);
        zonalRacialComposition.assign(0);
        hhByRegion.assign(0);
    }

    private void updateHouseholdInventar() {
        for (Household hh: dataContainer.getHouseholdData().getHouseholds()) {
            int zone = -1;
            Dwelling dwelling = dataContainer.getRealEstateData().getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                zone = dwelling.getZoneId();
            }
            final int region = geoData.getZones().get(zone).getRegion().getId();

            zonalRacialComposition.setQuick(zone, hh.getRace().getId(),
                    zonalRacialComposition.getQuick(zone, hh.getRace().getId()) + 1);
            regionalRacialComposition.setQuick(region, hh.getRace().getId(),
                    regionalRacialComposition.getQuick(region, hh.getRace().getId()));

            hhByRegion.setQuick(region, hhByRegion.getQuick(region) + 1);
        }
    }

    private void scaleMatrices() {
        for (int zone: geoData.getZones().keySet()) {
            final double zonalSum = zonalRacialComposition.viewRow(zone).zSum();
            if (zonalSum > 0) {
                zonalRacialComposition.viewRow(zone).assign(share -> share / zonalSum);
            }
        }

        for (int region: geoData.getRegions().keySet()) {
            final double regSum = regionalRacialComposition.viewRow(region).zSum();
            if (regSum > 0) {
                regionalRacialComposition.viewRow(region).assign(share -> share / regSum);
            }
        }
    }

    private double getZonalRacialShare(int zone, Race race) {
        return zonalRacialComposition.getQuick(zone, race.getId());
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
    protected void setupSelectRegionModel() {
        // set up model for selection of region
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("SelectRegionCalcMstm"));
        regionCalculator = new SelectRegionJSCalculator(reader);

    }

    @Override
    public void calculateRegionalUtilities() {
        // this method calculates generic utilities by household type, race and region and stores them in utilityRegion

        calculateRacialCompositionByZoneAndRegion();
        Map<Integer, Double> averagePriceByRegion = calculateRegionalPrices();
        Map<Integer, Integer> priceByRegion = new HashMap<>();
        Map<Integer, Float> accessibilityByRegion = new  HashMap<>();
        Map<Integer, Float> schoolQualityByRegion = new  HashMap<>();
        Map<Integer, Float> crimeRateByRegion = new  HashMap<>();

        for (Region region: geoData.getRegions().values()) {
            final int id = region.getId();
            int price;
            if(averagePriceByRegion.containsKey(id)) {
                price = averagePriceByRegion.get(id).intValue();
            } else {
                price = 0;
            }
            priceByRegion.put(id, price);
            accessibilityByRegion.put(id, (float) convertAccessToUtility(accessibility.getRegionalAccessibility(id)));
            schoolQualityByRegion.put(id,  (float) ((MstmRegion) region).getSchoolQuality());
            crimeRateByRegion.put(id , (float) (1f - ((MstmRegion) region).getCrimeRate()));  // invert utility, as lower crime rate has higher utility
        }

        for (IncomeCategory incomeCategory: IncomeCategory.values()) {
            EnumMap<Race, Map<Integer, Double>> utilitiesByRaceRegionForThisIncome = new EnumMap(Race.class);
            Map<Integer, Float> priceUtilitiesByRegion = new HashMap<>();
            for (Race race: Race.values()) {
                Map<Integer, Double> utilitiesByRegionForThisRaceIncome = new HashMap<>();
                for (Region region : geoData.getRegions().values()){
                    priceUtilitiesByRegion.put(region.getId(), (float) convertPriceToUtility(priceByRegion.get(region.getId()), incomeCategory));
                    utilitiesByRegionForThisRaceIncome.put(region.getId(),
                            regionCalculator.calculateSelectRegionProbabilityMstm(incomeCategory,
                                    race, priceUtilitiesByRegion.get(region.getId()), accessibilityByRegion.get(region.getId()),
                                    (float) regionalRacialComposition.get(region.getId(), race.getId()), schoolQualityByRegion.get(region.getId()),
                                    crimeRateByRegion.get(region.getId())));

                }
                utilitiesByRaceRegionForThisIncome.put(race, utilitiesByRegionForThisRaceIncome);
            }
            utilityByIncomeRaceRegion.put(incomeCategory, utilitiesByRaceRegionForThisIncome);
        }

    }

    private Map<Integer, Double> getUtilitiesByRegionForThisHouesehold(HouseholdType ht, Race race, Collection<Zone> workZones){
        Map<Integer, Double> utilitiesForThisHousheold = new HashMap<>();
        utilitiesForThisHousheold.putAll(utilityByIncomeRaceRegion.get(ht.getIncomeCategory()).get(race));

        for(Region region : geoData.getRegions().values()){
            double thisRegionFactor = 1;
            if (workZones != null) {
                for (Zone workZone : workZones) {
                    int timeFromZoneToRegion = (int) dataContainer.getTravelTimes().getTravelTimeToRegion(
                    		workZone, region, properties.transportModel.peakHour_s, TransportMode.car);
                    thisRegionFactor = thisRegionFactor * accessibility.getCommutingTimeProbability(timeFromZoneToRegion);
                }
            }
            utilitiesForThisHousheold.put(region.getId(),utilitiesForThisHousheold.get(region.getId())*thisRegionFactor);
        }
        return utilitiesForThisHousheold;
    }


    protected void setupUtilityCalculators() {
        // set up model for choice of dwelling
//        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("SelectDwellingCalc"));
        dwellingCalculator = new SelectDwellingJSCalculator(selectDwellingReader);
//        Reader ddUtilityReader = new InputStreamReader(this.getClass().getResourceAsStream("DwellingUtilityCalc"));
        mstmDwellingUtilityJSCalculator = new MstmDwellingUtilityJSCalculator(ddUtilityReader);

    }


    @Override
    public int searchForNewDwelling(Household household) {
        // search alternative dwellings for the list of persons in the household

        // data preparation -- > count workers, store working zones, define income, define race
        int householdIncome = 0;
        Race householdRace = null;
        Map<Person, Zone> workerZonesForThisHousehold = new HashMap<>();
        JobData jobData = dataContainer.getJobData();
        RealEstateData realEstateData = dataContainer.getRealEstateData();
        for (Person pp: household.getPersons().values()) {
        	// Are we sure that workplace must only not be -2? How about workplace = -1? nk/dz, july'18
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
            	Zone workZone = geoData.getZones().get(jobData.getJobFromId(pp.getJobId()).getZoneId());
                workerZonesForThisHousehold.put(pp, workZone);
                householdIncome += pp.getIncome();
            }
            if(householdRace == null) {
                householdRace = pp.getRace();
            } else if (pp.getRace() != householdRace) {
                householdRace = Race.other;
            }
        }

        HouseholdType ht = HouseholdUtil.defineHouseholdType(household);

        // Step 1: select region
        Map<Integer, Double> regionUtilitiesForThisHousehold  = new HashMap<>();
        regionUtilitiesForThisHousehold.putAll(getUtilitiesByRegionForThisHouesehold(ht, householdRace, workerZonesForThisHousehold.values()));

        // todo: adjust probabilities to make that households tend to move shorter distances (dist to work is already represented)
        String normalizer = "population";
        int totalVacantDd = 0;
        for (int region: geoData.getRegions().keySet()) {
            totalVacantDd += realEstateData.getNumberOfVacantDDinRegion(region);
        }
        for (int region : regionUtilitiesForThisHousehold.keySet()){
            switch (normalizer) {
                case ("vacDd"): {
                    // Multiply utility of every region by number of vacant dwellings to steer households towards available dwellings
                    // use number of vacant dwellings to calculate attractivity of region
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * (float) realEstateData.getNumberOfVacantDDinRegion(region));
                } case ("shareVacDd"): {
                    // use share of empty dwellings to calculate attractivity of region
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * ((float) realEstateData.getNumberOfVacantDDinRegion(region) / (float) totalVacantDd));
                } case ("dampenedVacRate"): {
                    double x = (double) realEstateData.getNumberOfVacantDDinRegion(region) /
                            (double) realEstateData.getNumberOfDDinRegion(region) * 100d;  // % vacancy
                    double y = 1.4186E-03 * Math.pow(x, 3) - 6.7846E-02 * Math.pow(x, 2) + 1.0292 * x + 4.5485E-03;
                    y = Math.min(5d, y);                                                // % vacancy assumed to be ready to move in
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * (y / 100d * realEstateData.getNumberOfDDinRegion(region)));
                    if (realEstateData.getNumberOfVacantDDinRegion(region) < 1) {
                        regionUtilitiesForThisHousehold.put(region, 0D);
                    }
                } case ("population"): {
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * hhByRegion.getQuick(region));
                } case ("noNormalization"): {
                    // do nothing
                }
            }
        }
        int selectedRegionId;
        if (regionUtilitiesForThisHousehold.values().stream().mapToDouble(i -> i).sum() == 0) {
            return -1; //cannot find a region with some utility //todo why not to look for for another region??
        } else {
            selectedRegionId = SiloUtil.select(regionUtilitiesForThisHousehold);
        }

        // Step 2: select vacant dwelling in selected region
        int[] vacantDwellings = realEstateData.getListOfVacantDwellingsInRegion(selectedRegionId);
        double[] expProbs = SiloUtil.createArrayWithValue(vacantDwellings.length, 0d);
        int maxNumberOfDwellings = Math.min(20, vacantDwellings.length);  // No household will evaluate more than 20 dwellings
        float factor = ((float) maxNumberOfDwellings / (float) vacantDwellings.length);
        for (int i = 0; i < vacantDwellings.length; i++) {
            if (SiloUtil.getRandomNumberAsFloat() > factor) continue;
            Dwelling dd = dataContainer.getRealEstateData().getDwelling(vacantDwellings[i]);
            int msa = geoData.getZones().get(dd.getZoneId()).getMsa();
            if (dd.getRestriction() > 0 &&    // dwelling is restricted to households with certain income
                    householdIncome > (dataContainer.getHouseholdData().getMedianIncome(msa) * dd.getRestriction())) continue;
            double racialShare = 1;
            if (householdRace != Race.other) {
                racialShare = getZonalRacialShare(geoData.getZones().get(dd.getZoneId()).getZoneId(), householdRace);
            }
            // multiply by racial share to make zones with higher own racial share more attractive

            double utility = calculateHousingUtility(household, dd);

            double adjustedUtility = Math.pow(utility, (1 - selectDwellingRaceRelevance)) *
                    Math.pow(racialShare, selectDwellingRaceRelevance);

            //adjProbability is the adjusted dwelling utility
            expProbs[i] = dwellingCalculator.calculateSelectDwellingProbability(adjustedUtility);

        }
        if (SiloUtil.getSum(expProbs) == 0) {
            return -1;    // could not find dwelling that fits restrictions
        }
        int selected = SiloUtil.select(expProbs);
        return vacantDwellings[selected];
    }



    @Override
    protected double calculateHousingUtility(Household hh, Dwelling dd) {

        double ddQualityUtility = convertQualityToUtility(dd.getQuality());
        double ddSizeUtility = convertAreaToUtility(dd.getBedrooms());
        double ddAutoAccessibilityUtility = convertAccessToUtility(accessibility.getAutoAccessibilityForZone(dd.getZoneId()));
        double transitAccessibilityUtility = convertAccessToUtility(accessibility.getTransitAccessibilityForZone(dd.getZoneId()));
        HouseholdType ht = hh.getHouseholdType();
        double ddPriceUtility = convertPriceToUtility(dd.getPrice(), ht);

        double crimeUtility = 1. - ((MstmZone) geoData.getZones().get(dd.getZoneId())).getCounty().getCrimeRate();
        double schoolQualityUtility = ((MstmRegion) geoData.getZones().get(dd.getZoneId()).getRegion()).getSchoolQuality();

        int income = HouseholdUtil.getHhIncome(hh);
        if (householdQualifiesForSubsidy(income, geoData.getZones().get(dd.getZoneId()).getZoneId(), dd.getPrice())) {
            //need to recalculate the generic utility

            int price = dd.getPrice();
            if (provideRentSubsidyToLowIncomeHh && income > 0) {     // income equals -1 if dwelling is vacant right now
                // housing subsidy program in place
                int msa = geoData.getZones().get(dd.getZoneId()).getMsa();
                if (income < (0.5f * dataContainer.getHouseholdData().getMedianIncome(msa)) && price < (0.4f * income / 12f)) {
                    float housingBudget = (income / 12f * 0.18f);  // technically, the housing budget is 30%, but in PUMS data households pay 18% on the average
                    float subsidy = dataContainer.getRealEstateData().getMedianRent(msa) - housingBudget;
                    price = Math.max(0, price - (int) (subsidy + 0.5));
                }
            }
            ddPriceUtility = convertPriceToUtility(price, ht);
        }

        Map<Person, Zone> jobsForThisHousehold = new HashMap<>();
        JobData jobData = dataContainer.getJobData();
        for (Person pp: hh.getPersons().values()) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getJobId() != -2) {
                Job workLocation = Objects.requireNonNull(jobData.getJobFromId(pp.getJobId()));
                jobsForThisHousehold.put(pp, geoData.getZones().get(workLocation.getZoneId()));
            }
        }
        double workDistanceUtility = 1;
        Zone originZone = geoData.getZones().get(dd.getZoneId());
        for (Zone workLocation : jobsForThisHousehold.values()){
            double factorForThisZone = accessibility.getCommutingTimeProbability(Math.max(1,(int) dataContainer.getTravelTimes().getTravelTime(
                    originZone, workLocation, 0, TransportMode.car)));
            workDistanceUtility *= factorForThisZone;
        }

        return mstmDwellingUtilityJSCalculator.calculateSelectDwellingUtility(ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, schoolQualityUtility, crimeUtility, workDistanceUtility);

    }

    private boolean householdQualifiesForSubsidy(int income, int zone, int price) {
        int assumedIncome = Math.max(income, 15000);  // households with less than that must receive some welfare
        return provideRentSubsidyToLowIncomeHh &&
                income <= (0.5f * dataContainer.getHouseholdData().getMedianIncome(geoData.getZones().get(zone).getMsa())) &&
                price <= (0.4f * assumedIncome);
    }
}
