package de.tum.bgu.msm.models.relocation.munich;

/*
 * Implementation of the MovesModelI Interface for the Munich implementation
 * @author Rolf Moeckel
 * Date: 20 May 2017, near Greenland in an altitude of 35,000 feet
*/

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.models.relocation.AbstractDefaultMovesModel;
import de.tum.bgu.msm.models.relocation.SelectDwellingJSCalculator;
import de.tum.bgu.msm.models.relocation.SelectRegionJSCalculator;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.Matrices;
import org.matsim.api.core.v01.TransportMode;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class MovesModelMuc extends AbstractDefaultMovesModel {

    private SelectRegionJSCalculator regionCalculator;
    private EnumMap<IncomeCategory, EnumMap<Nationality, Map<Integer, Double>>> utilityByIncomeNationalityAndRegion = new EnumMap<>(IncomeCategory.class) ;

    private SelectDwellingJSCalculator dwellingCalculator;
    private final DoubleMatrix1D regionalShareForeigners;
    private final DoubleMatrix1D hhByRegion;

    public MovesModelMuc(SiloDataContainer dataContainer, Accessibility accessibility) {
        super(dataContainer, accessibility);
        regionalShareForeigners = Matrices.doubleMatrix1D(geoData.getRegions().values());
        hhByRegion = Matrices.doubleMatrix1D(geoData.getRegions().values());
    }

    private void calculateShareOfForeignersByZoneAndRegion() {


        final DoubleMatrix1D hhByZone = Matrices.doubleMatrix1D(geoData.getZones().values());
        regionalShareForeigners.assign(0);
        hhByRegion.assign(0);
        for (Household hh: dataContainer.getHouseholdData().getHouseholds()) {
            int zone = -1;
            Dwelling dwelling = dataContainer.getRealEstateData().getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                zone = dwelling.getZoneId();
            }
            final int region = geoData.getZones().get(zone).getRegion().getId();
            hhByZone.setQuick(zone, hhByZone.getQuick(zone) + 1);
            hhByRegion.setQuick(region, hhByRegion.getQuick(region) + 1);
            if (hh.getNationality() != Nationality.GERMAN) {
                regionalShareForeigners.setQuick(region, regionalShareForeigners.getQuick(region)+1);
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
    protected void setupSelectRegionModel() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("SelectRegionCalc"));
        regionCalculator = new SelectRegionJSCalculator(reader);
    }



    @Override
    public void calculateRegionalUtilities() {
        LOGGER.info("Calculating regional utilities");
        calculateShareOfForeignersByZoneAndRegion();
        final Map<Integer, Double> rentsByRegion = calculateRegionalPrices();
        for (IncomeCategory incomeCategory: IncomeCategory.values()) {
            EnumMap<Nationality, Map<Integer, Double>> utilitiesByNationalityRegionForThisIncome = new EnumMap<>(Nationality.class);
            for (Nationality nationality: Nationality.values()) {
                Map<Integer, Double> utilitiesByRegionForThisNationalityAndIncome = new HashMap<>();
                for (Region region : geoData.getRegions().values()){
                    final int averageRegionalRent = rentsByRegion.get(region.getId()).intValue();
                    final float regAcc = (float) convertAccessToUtility(accessibility.getRegionalAccessibility(region.getId()));
                    float priceUtil = (float) convertPriceToUtility(averageRegionalRent, incomeCategory);
                    utilitiesByRegionForThisNationalityAndIncome.put(region.getId(),
                            regionCalculator.calculateSelectRegionProbability(incomeCategory,
                                    nationality, priceUtil, regAcc, (float) regionalShareForeigners.getQuick(region.getId())));

                }
                utilitiesByNationalityRegionForThisIncome.put(nationality, utilitiesByRegionForThisNationalityAndIncome);
            }
            utilityByIncomeNationalityAndRegion.put(incomeCategory, utilitiesByNationalityRegionForThisIncome);
        }

    }

    private Map<Integer, Double> getUtilitiesByRegionForThisHouesehold(HouseholdType ht, Nationality nationality, Collection<Zone> workZones){
        Map<Integer, Double> utilitiesForThisHousheold
                = new HashMap<>(utilityByIncomeNationalityAndRegion.get(ht.getIncomeCategory()).get(nationality));

        for(Region region : geoData.getRegions().values()){
            double thisRegionFactor = 1;
            if (workZones != null) {
                for (Zone workZone : workZones) {
                    int timeFromZoneToRegion = (int) dataContainer.getTravelTimes().getTravelTimeToRegion(
                    		workZone, region, Properties.get().main.peakHour, TransportMode.car);
                    thisRegionFactor = thisRegionFactor * accessibility.getCommutingTimeProbability(timeFromZoneToRegion);
                }
            }
            utilitiesForThisHousheold.put(region.getId(),utilitiesForThisHousheold.get(region.getId())*thisRegionFactor);
        }
        return utilitiesForThisHousheold;
    }

    @Override
    protected void setupSelectDwellingModel() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("SelectDwellingCalc"));
        dwellingCalculator = new SelectDwellingJSCalculator(reader);
    }


    @Override
    public int searchForNewDwelling(List<Person> persons) {
        // search alternative dwellings

        // data preparation
        int householdIncome = 0;
        Nationality nationality = persons.get(0).getNationality();
        Map<Person, Zone> workerZonesForThisHousehold = new HashMap<>();
        JobDataManager jobData = dataContainer.getJobData();
        for (Person pp: persons) {
        	// Are we sure that workplace must only not be -2? How about workplace = -1? nk/dz, july'18
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getWorkplace() != -2) {
            	Zone workZone = geoData.getZones().get(jobData.getJobFromId(pp.getWorkplace()).getZoneId());
                workerZonesForThisHousehold.put(pp, workZone);
                householdIncome += pp.getIncome();
            }
            if (pp.getNationality() != nationality) nationality = Nationality.OTHER;
        }

        IncomeCategory incomeCategory = HouseholdDataManager.getIncomeCategoryForIncome(householdIncome);
        HouseholdType ht = HouseholdType.defineHouseholdType(persons.size(), incomeCategory);

        // Step 1: select region
        Map<Integer, Double> regionUtilitiesForThisHousehold  = new HashMap<>();
        regionUtilitiesForThisHousehold.putAll(getUtilitiesByRegionForThisHouesehold(ht,nationality,workerZonesForThisHousehold.values()));

        // todo: adjust probabilities to make that households tend to move shorter distances (dist to work is already represented)
        String normalizer = "powerOfPopulation";
        int totalVacantDd = 0;
        for (int region: geoData.getRegions().keySet()) {
            totalVacantDd += RealEstateDataManager.getNumberOfVacantDDinRegion(region);
        }
        for (int region : regionUtilitiesForThisHousehold.keySet()){
            switch (normalizer) {
                case ("vacDd"): {
                    // Multiply utility of every region by number of vacant dwellings to steer households towards available dwellings
                    // use number of vacant dwellings to calculate attractivity of region
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * (float) RealEstateDataManager.getNumberOfVacantDDinRegion(region));
                } case ("shareVacDd"): {
                    // use share of empty dwellings to calculate attractivity of region
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * ((float) RealEstateDataManager.getNumberOfVacantDDinRegion(region) / (float) totalVacantDd));
                } case ("dampenedVacRate"): {
                    double x = (double) RealEstateDataManager.getNumberOfVacantDDinRegion(region) /
                            (double) RealEstateDataManager.getNumberOfDDinRegion(region) * 100d;  // % vacancy
                    double y = 1.4186E-03 * Math.pow(x, 3) - 6.7846E-02 * Math.pow(x, 2) + 1.0292 * x + 4.5485E-03;
                    y = Math.min(5d, y);                                                // % vacancy assumed to be ready to move in
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * (y / 100d * RealEstateDataManager.getNumberOfDDinRegion(region)));
                    if (RealEstateDataManager.getNumberOfVacantDDinRegion(region) < 1) {
                        regionUtilitiesForThisHousehold.put(region, 0D);
                    }
                } case ("population"): {
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * hhByRegion.getQuick(region));
                } case ("noNormalization"): {
                    // do nothing
                }case ("powerOfPopulation"): {
                    regionUtilitiesForThisHousehold.put(region, regionUtilitiesForThisHousehold.get(region) * Math.pow(hhByRegion.getQuick(region),0.5));
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
//                    dataContainer.getJobData().getJobFromId(worker.getWorkplace()).getZone() + "," +
//                    selectedRegionId  + "," +
//                    accessibility.getMinTravelTimeFromZoneToRegion(dataContainer.getJobData().getJobFromId(worker.getWorkplace()).getZone(), selectedRegionId));
//        }



        // Step 2: select vacant dwelling in selected region
        int[] vacantDwellings = RealEstateDataManager.getListOfVacantDwellingsInRegion(selectedRegionId);
        double[] expProbs = SiloUtil.createArrayWithValue(vacantDwellings.length, 0d);
        double sumProbs = 0.;
        int maxNumberOfDwellings = Math.min(20, vacantDwellings.length);  // No household will evaluate more than 20 dwellings
        float factor = ((float) maxNumberOfDwellings / (float) vacantDwellings.length);
        for (int i = 0; i < vacantDwellings.length; i++) {
            if (SiloUtil.getRandomNumberAsFloat() > factor) continue;
            Dwelling dd = dataContainer.getRealEstateData().getDwelling(vacantDwellings[i]);
            double util = calculateDwellingUtilityForHouseholdType(ht, dd);
            util = personalizeDwellingUtilityForThisHousehold(persons, dd, householdIncome, util);
            expProbs[i] = dwellingCalculator.calculateSelectDwellingProbability(util);
            sumProbs =+ expProbs[i];
        }
        if (sumProbs == 0) return -1;    // could not find dwelling that fits restrictions
        int selected = SiloUtil.select(expProbs, sumProbs);
        return vacantDwellings[selected];
    }

    @Override
    protected double calculateDwellingUtilityForHouseholdType(HouseholdType ht, Dwelling dd) {
        double ddQualityUtility = convertQualityToUtility(dd.getQuality());
        double ddSizeUtility = convertAreaToUtility(dd.getBedrooms());
        double ddAutoAccessibilityUtility = convertAccessToUtility(accessibility.getAutoAccessibilityForZone(dd.getZoneId()));
        double transitAccessibilityUtility = convertAccessToUtility(accessibility.getTransitAccessibilityForZone(dd.getZoneId()));
        double ddPriceUtility = convertPriceToUtility(dd.getPrice(), ht);
        return dwellingUtilityJSCalculator.calculateSelectDwellingUtility(ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility);
    }

    @Override
    protected double personalizeDwellingUtilityForThisHousehold(List<Person> persons, Dwelling dd, int income, double genericUtility) {
        //currently this is re-filtering persons to find workers (it was done previously in select region)
        // This way looks more flexible to account for other trips, such as education, though.
        IncomeCategory incomeCategory = HouseholdDataManager.getIncomeCategoryForIncome(income);
        HouseholdType ht = HouseholdType.defineHouseholdType(persons.size(), incomeCategory);

        double travelCostUtility = 1; //do not have effect at the moment;

        Map<Person, Location> workerZonesForThisHousehold = new HashMap<>();
        JobDataManager jobData = dataContainer.getJobData();
        for (Person pp: persons) {
            if (pp.getOccupation() == Occupation.EMPLOYED && pp.getWorkplace() != -2) {
            	Location workLocation = Objects.requireNonNull(jobData.getJobFromId(pp.getWorkplace()));
                workerZonesForThisHousehold.put(pp, workLocation);
            }
        }
        double workDistanceUtility = 1;
        for (Location workLocation : workerZonesForThisHousehold.values()){
        	double factorForThisZone = accessibility.getCommutingTimeProbability(Math.max(1,(int) dataContainer.getTravelTimes().getTravelTime(
                    dd, workLocation, Properties.get().main.peakHour, TransportMode.car)));
            workDistanceUtility *= factorForThisZone;
        }
        return dwellingUtilityJSCalculator.personalizeUtility(ht, genericUtility, workDistanceUtility, travelCostUtility);
    }

    @Override
    public void finishYear(int year) {
    }


}
