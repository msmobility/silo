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
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.models.relocation.AbstractDefaultMovesModel;
import de.tum.bgu.msm.models.relocation.SelectDwellingJSCalculator;
import de.tum.bgu.msm.models.relocation.SelectRegionJSCalculator;
import de.tum.bgu.msm.util.matrices.Matrices;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public class MovesModelMuc extends AbstractDefaultMovesModel {

    private SelectRegionJSCalculator regionCalculator;
    private SelectDwellingJSCalculator dwellingCalculator;
    private final DoubleMatrix1D regionalShareForeigners;
    private final DoubleMatrix1D hhByRegion;

    public MovesModelMuc(SiloDataContainer dataContainer, Accessibility accessibility) {
        super(dataContainer, accessibility);
        regionalShareForeigners = Matrices.doubleMatrix1D(geoData.getRegions().values());
        hhByRegion = Matrices.doubleMatrix1D(geoData.getRegions().values());
    }

    private void calculateShareOfForeignersByZoneAndRegion() {

        final DoubleMatrix1D zonalShare = Matrices.doubleMatrix1D(geoData.getZones().values());
        zonalShare.assign(0);
        final DoubleMatrix1D hhByZone = zonalShare.copy();

        regionalShareForeigners.assign(0);
        hhByRegion.assign(0);

        for (Household hh : dataContainer.getHouseholdData().getHouseholds()) {
            int zone = -1;
            Dwelling dwelling = dataContainer.getRealEstateData().getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                zone = dwelling.getZone();
            }
            final int region = geoData.getZones().get(zone).getRegion().getId();
            hhByZone.setQuick(zone, hhByZone.getQuick(zone) + 1);
            hhByRegion.setQuick(region, hhByRegion.getQuick(region) + 1);
            if (hh.getNationality() != Nationality.german) {
                zonalShare.setQuick(zone, zonalShare.getQuick(zone) + 1);
                regionalShareForeigners.setQuick(region, zonalShare.getQuick(region) + 1);
            }
        }

        zonalShare.assign(hhByZone, (foreignerShare, numberOfHouseholds) -> {
            if (numberOfHouseholds > 0) {
                return foreignerShare / numberOfHouseholds;
            } else {
                return 0;
            }
        });

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
        final int highestRegion = geoData.getRegions().keySet().stream().mapToInt(Integer::intValue).max().getAsInt();
        utilityRegion = new double[Properties.get().main.incomeBrackets.length + 1][Nationality.values().length][highestRegion + 1];

        final Map<Integer, Double> rentsByRegion = calculateRegionalPrices();

        for (int region : geoData.getRegions().keySet()) {
            final int averageRegionalRent = rentsByRegion.get(region).intValue();
            final float regAcc = (float) convertAccessToUtility(accessibility.getRegionalAccessibility(region));
            for (int income = 1; income <= Properties.get().main.incomeBrackets.length + 1; income++) {
                float priceUtil = (float) convertPriceToUtility(averageRegionalRent, income);
                for (Nationality nationality : Nationality.values()) {
                    utilityRegion[income - 1][nationality.ordinal()][region - 1] = regionCalculator.calculateSelectRegionProbability(income - 1,
                            nationality, priceUtil, regAcc, (float) regionalShareForeigners.getQuick(region));
                }
            }
        }
    }

    @Override
    protected void setupSelectDwellingModel() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("SelectDwellingCalc"));
        dwellingCalculator = new SelectDwellingJSCalculator(reader);
    }


    private double[] getRegionUtilities(HouseholdType ht, Race race, int[] workZones) {
        // return utility of regions based on household type and based on work location of workers in household

        int[] regions = geoData.getRegionIdsArray();
        double[] util = new double[regions.length];
        double[] workDistanceFactor = new double[regions.length];
        for (int i = 0; i < regions.length; i++) {
            workDistanceFactor[i] = 1;
            if (workZones != null) {  // for inmigrating household, work places are selected after household found a home
                for (int workZone : workZones) {
                    int smallestDistInMin = (int) accessibility.getMinTravelTimeFromZoneToRegion(workZone, regions[i]);
                    workDistanceFactor[i] = workDistanceFactor[i] * accessibility.getCommutingTimeProbability(smallestDistInMin);
                }
            }
        }
        int incomeCat = HouseholdType.convertHouseholdTypeToIncomeCategory(ht);
        for (int i = 0; i < regions.length; i++) {
            util[i] = utilityRegion[incomeCat - 1][race.ordinal()][i] * workDistanceFactor[i];
        }
        return util;
    }


    @Override
    public int searchForNewDwelling(List<Person> persons) {
        // search alternative dwellings

        // data preparation
        int wrkCount = 0;
        for (Person pp : persons) {
            if (pp.getOccupation() == 1 && pp.getWorkplace() != -2) {
                wrkCount++;
            }
        }
        int pos = 0;
        int householdIncome = 0;
        int[] workZones = new int[wrkCount];
        Race householdRace = persons.get(0).getRace();
        JobDataManager jobData = dataContainer.getJobData();
        for (Person pp : persons) {
            if (pp.getOccupation() == 1 && pp.getWorkplace() != -2) {
                workZones[pos] = jobData.getJobFromId(pp.getWorkplace()).getZone();
                pos++;
                householdIncome += pp.getIncome();
                if (pp.getRace() != householdRace) {
                    householdRace = Race.black; //changed this so race is a proxy of nationality
                }
            }
        }
        if (householdRace == Race.other) {
            householdRace = Race.black;
        } else if (householdRace == Race.hispanic) {
            householdRace = Race.black;
        }
        int incomeBracket = HouseholdDataManager.getIncomeCategoryForIncome(householdIncome);
        HouseholdType ht = HouseholdDataManager.defineHouseholdType(persons.size(), incomeBracket);

        // Step 1: select region
        int[] regions = geoData.getRegionIdsArray();
        double[] regionUtilities = getRegionUtilities(ht, householdRace, workZones);
        // todo: adjust probabilities to make that households tend to move shorter distances (dist to work is already represented)
        String normalizer = "population";
        int totalVacantDd = 0;
        for (int region : geoData.getRegionIdsArray()) {
            totalVacantDd += RealEstateDataManager.getNumberOfVacantDDinRegion(region);
        }
        for (int i = 0; i < regionUtilities.length; i++) {
            switch (normalizer) {
                case ("vacDd"): {
                    // Multiply utility of every region by number of vacant dwellings to steer households towards available dwellings
                    // use number of vacant dwellings to calculate attractivity of region
                    regionUtilities[i] = regionUtilities[i] * (float) RealEstateDataManager.getNumberOfVacantDDinRegion(regions[i]);
                }
                case ("shareVacDd"): {
                    // use share of empty dwellings to calculate attractivity of region
                    regionUtilities[i] = regionUtilities[i] * ((float) RealEstateDataManager.getNumberOfVacantDDinRegion(regions[i]) / (float) totalVacantDd);
                }
                case ("dampenedVacRate"): {
                    double x = (double) RealEstateDataManager.getNumberOfVacantDDinRegion(regions[i]) /
                            (double) RealEstateDataManager.getNumberOfDDinRegion(regions[i]) * 100d;  // % vacancy
                    double y = 1.4186E-03 * Math.pow(x, 3) - 6.7846E-02 * Math.pow(x, 2) + 1.0292 * x + 4.5485E-03;
                    y = Math.min(5d, y);                                                // % vacancy assumed to be ready to move in
                    regionUtilities[i] = regionUtilities[i] * (y / 100d * RealEstateDataManager.getNumberOfDDinRegion(regions[i]));
                    if (RealEstateDataManager.getNumberOfVacantDDinRegion(regions[i]) < 1) regionUtilities[i] = 0d;
                }
                case ("population"): {
                    regionUtilities[i] = regionUtilities[i] * hhByRegion.getQuick(regions[i]);
                }
                case ("noNormalization"): {
                    // do nothing
                }
            }
        }
        if (SiloUtil.getSum(regionUtilities) == 0) return -1;
        int selectedRegion = SiloUtil.select(regionUtilities);

        // Step 2: select vacant dwelling in selected region
        int[] vacantDwellings = RealEstateDataManager.getListOfVacantDwellingsInRegion(regions[selectedRegion]);
        double[] expProbs = SiloUtil.createArrayWithValue(vacantDwellings.length, 0d);
        double sumProbs = 0.;
        int maxNumberOfDwellings = Math.min(20, vacantDwellings.length);  // No household will evaluate more than 20 dwellings
        float factor = ((float) maxNumberOfDwellings / (float) vacantDwellings.length);
        for (int i = 0; i < vacantDwellings.length; i++) {
            if (SiloUtil.getRandomNumberAsFloat() > factor) continue;
            Dwelling dd = dataContainer.getRealEstateData().getDwelling(vacantDwellings[i]);
            double util = calculateDwellingUtilityOfHousehold(ht, householdIncome, dd);
            expProbs[i] = dwellingCalculator.calculateSelectDwellingProbability(util);
            sumProbs = +expProbs[i];
        }
        if (sumProbs == 0) return -1;    // could not find dwelling that fits restrictions
        int selected = SiloUtil.select(expProbs, sumProbs);
        return vacantDwellings[selected];
    }

    @Override
    protected double calculateDwellingUtilityOfHousehold(HouseholdType ht, int income, Dwelling dd) {
        //evaluateDwellingDmu.setUtilityDwellingQuality(convertQualityToUtility(dd.getQuality()));
        //evaluateDwellingDmu.setUtilityDwellingSize(convertAreaToUtility(dd.getBedrooms()));
        //evaluateDwellingDmu.setUtilityDwellingAutoAccessibility(convertAccessToUtility(accessibility.getAutoAccessibilityForZone(dd.getZone())));
        //evaluateDwellingDmu.setUtilityDwellingTransitAccessibility(convertAccessToUtility(accessibility.getTransitAccessibilityForZone(dd.getZone())));
        //int price = dd.getPrice();
        //evaluateDwellingDmu.setUtilityDwellingPrice(convertPriceToUtility(price, ht));
        //evaluateDwellingDmu.setType(ht);

        double ddQualityUtility = convertQualityToUtility(dd.getQuality());
        double ddSizeUtility = convertAreaToUtility(dd.getBedrooms());
        double ddAutoAccessibilityUtility = convertAccessToUtility(accessibility.getAutoAccessibilityForZone(dd.getZone()));
        double transitAccessibilityUtility = convertAccessToUtility(accessibility.getTransitAccessibilityForZone(dd.getZone()));
        double ddPriceUtility = convertPriceToUtility(dd.getPrice(), ht);

        //double util[] = ddUtilityModel.solve(evaluateDwellingDmu.getDmuIndexValues(), evaluateDwellingDmu, evalDwellingAvail);
        double ddUtility = dwellingUtilityJSCalculator.calculateSelectDwellingUtility(ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, 0.0, 0.0);


//        if (logCalculationDwelling)
//        ddUtilityModel.logAnswersArray(traceLogger, "Quality of dwelling " + dd.getId());

        return ddUtility;
    }

}
