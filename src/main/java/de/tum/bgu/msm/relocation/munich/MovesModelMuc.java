package de.tum.bgu.msm.relocation.munich;

/*
 * Implementation of the MovesModelI Interface for the Munich implementation
 * @author Rolf Moeckel
 * Date: 20 May 2017, near Greenland in an altitude of 35,000 feet
*/

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.events.EventManager;
import de.tum.bgu.msm.events.EventRules;
import de.tum.bgu.msm.events.EventTypes;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.relocation.AbstractDefaultMovesModel;
import de.tum.bgu.msm.relocation.SelectDwellingJSCalculator;
import de.tum.bgu.msm.relocation.SelectRegionJSCalculator;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class MovesModelMuc extends AbstractDefaultMovesModel {

    private float[] regionalShareForeigners;
    private SelectRegionJSCalculator regionCalculator;
    private SelectDwellingJSCalculator dwellingCalculator;

    public MovesModelMuc(GeoData geoData) {
        super(geoData);
    }

    private void calculateShareOfForeignersByZoneAndRegion() {

        float[] zonalShareForeigners = new float[geoData.getZones().length];
        regionalShareForeigners = new float[geoData.getRegionList().length];
        SiloUtil.setArrayToValue(zonalShareForeigners, 0f);
        for (Household hh: Household.getHouseholdArray()) {
            int region = geoData.getRegionOfZone(hh.getHomeZone());
            if (hh.getNationality() != Nationality.german) {
                zonalShareForeigners[geoData.getZoneIndex(hh.getHomeZone())]++;
                regionalShareForeigners[geoData.getRegionIndex(region)]++;
            }
        }
        int[] hhByZone = HouseholdDataManager.getNumberOfHouseholdsByZone(geoData);
        for (int zone: geoData.getZones()) {
            if (hhByZone[geoData.getZoneIndex(zone)] > 0) {
                zonalShareForeigners[geoData.getZoneIndex(zone)] =
                        zonalShareForeigners[geoData.getZoneIndex(zone)] / hhByZone[geoData.getZoneIndex(zone)];
            } else {
                zonalShareForeigners[geoData.getZoneIndex(zone)] = 0;  // should not be necessary, but implemented for safety
            }
        }
        int[] hhByRegion = HouseholdDataManager.getNumberOfHouseholdsByRegion(geoData);
        for (int region: geoData.getRegionList()) {
            if (hhByRegion[geoData.getRegionIndex(region)] > 0) {
                regionalShareForeigners[geoData.getRegionIndex(region)] =
                        regionalShareForeigners[geoData.getRegionIndex(region)] / hhByRegion[geoData.getRegionIndex(region)];
            } else {
                regionalShareForeigners[geoData.getRegionIndex(region)] = 0;  // should not be necessary, but implemented for safety
            }
        }
    }



//    private double convertDistToWorkToUtil (Household hh, int homeZone) {
//        // convert distance to work and school to utility
//        double util = 1;
//        for (Person p: hh.getPersons()) {
//            if (p.getOccupation() == 1 && p.getWorkplace() != -2) {
//                int workZone = Job.getJobFromId(p.getWorkplace()).getZone();
//                int travelTime = (int) SiloUtil.rounder(siloModelContainer.getAcc().getAutoTravelTime(homeZone, workZone),0);
//                util = util * siloModelContainer.getAcc().getWorkTLFD(travelTime);
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
        regionCalculator = new SelectRegionJSCalculator(reader, false);
    }

    @Override
    public void calculateRegionalUtilities(SiloModelContainer siloModelContainer) {
        // everything is available

        int[] regions = geoData.getRegionList();
        calculateShareOfForeignersByZoneAndRegion();

        int highestRegion = SiloUtil.getHighestVal(regions);
        int[] regPrice = new int[highestRegion + 1];
        float[] regAcc = new float[highestRegion + 1];
        for (int region: regions) {
            regPrice[geoData.getRegionIndex(region)] = calculateRegPrice(region);
            regAcc[geoData.getRegionIndex(region)] = (float) convertAccessToUtility(siloModelContainer.getAcc().getRegionalAccessibility(region));
        }

        utilityRegion = new double[Properties.get().main.incomeBrackets.length + 1][Nationality.values().length][regions.length];
        for (int income = 1; income <= Properties.get().main.incomeBrackets.length + 1; income++) {

            float[] priceUtil = new float[highestRegion + 1];

            for (int region: regions) {
                priceUtil[region] = (float) convertPriceToUtility(regPrice[region], income);
            }

            for (Nationality nationality: Nationality.values()) {
                for (int region: regions) {
                    regionCalculator.setIncomeGroup(income - 1);
                    regionCalculator.setNationality(nationality);
                    regionCalculator.setMedianPrice(priceUtil[region]);
                    regionCalculator.setForeignersShare(regionalShareForeigners[geoData.getRegionIndex(region)]);
                    regionCalculator.setAccessibility(regAcc[region]);
                    double utility = 0;
                    try {
                        utility = regionCalculator.calculate();
                        utilityRegion[income - 1][nationality.ordinal()][region-1] = utility;
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        householdsByRegion = HouseholdDataManager.getNumberOfHouseholdsByRegion(geoData);
    }

    @Override
    protected void setupSelectDwellingModel() {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("SelectDwellingCalc"));
        dwellingCalculator = new SelectDwellingJSCalculator(reader, false);
    }


    private double[] getRegionUtilities (HouseholdType ht, Race race, int[] workZones, SiloModelContainer siloModelContainer) {
        // return utility of regions based on household type and based on work location of workers in household

        int[] regions = geoData.getRegionList();
        double[] util = new double[regions.length];
        double[] workDistanceFactor = new double[regions.length];
        for (int i = 0; i < regions.length; i++) {
            workDistanceFactor[i] = 1;
            if (workZones != null) {  // for inmigrating household, work places are selected after household found a home
                for (int workZone : workZones) {
                    int smallestDistInMin = (int) siloModelContainer.getAcc().getMinDistanceFromZoneToRegion(workZone, regions[i]);
                    workDistanceFactor[i] = workDistanceFactor[i] * siloModelContainer.getAcc().getWorkTLFD(smallestDistInMin);
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
    public int searchForNewDwelling(Person[] persons, SiloModelContainer modelContainer) {
        // search alternative dwellings

        // data preparation
        int wrkCount = 0;
        for (Person pp: persons) if (pp.getOccupation() == 1 && pp.getWorkplace() != -2) wrkCount++;
        int pos = 0;
        int householdIncome = 0;
        int[] workZones = new int[wrkCount];
        Race householdRace = persons[0].getRace();
        for (Person pp: persons) if (pp.getOccupation() == 1 && pp.getWorkplace() != -2) {
            workZones[pos] = Job.getJobFromId(pp.getWorkplace()).getZone();
            pos++;
            householdIncome += pp.getIncome();
            if (pp.getRace() != householdRace) householdRace = Race.black; //changed this so race is a proxy of nationality
        }
        if (householdRace == Race.other){
            householdRace = Race.black;
        } else if (householdRace == Race.hispanic){
            householdRace = Race.black;
        }
        int incomeBracket = HouseholdDataManager.getIncomeCategoryForIncome(householdIncome);
        HouseholdType ht = HouseholdDataManager.defineHouseholdType(persons.length, incomeBracket);

        // Step 1: select region
        int[] regions = geoData.getRegionList();
        double[] regionUtilities = getRegionUtilities(ht, householdRace, workZones, modelContainer);
        // todo: adjust probabilities to make that households tend to move shorter distances (dist to work is already represented)
        String normalizer = "population";
        int totalVacantDd = 0;
        for (int region: geoData.getRegionList()) totalVacantDd += RealEstateDataManager.getNumberOfVacantDDinRegion(region);
        for (int i = 0; i < regionUtilities.length; i++) {
            switch (normalizer) {
                case ("vacDd"): {
                    // Multiply utility of every region by number of vacant dwellings to steer households towards available dwellings
                    // use number of vacant dwellings to calculate attractivity of region
                    regionUtilities[i] = regionUtilities[i] * (float) RealEstateDataManager.getNumberOfVacantDDinRegion(regions[i]);
                } case ("shareVacDd"): {
                    // use share of empty dwellings to calculate attractivity of region
                    regionUtilities[i] = regionUtilities[i] * ((float) RealEstateDataManager.getNumberOfVacantDDinRegion(regions[i]) / (float) totalVacantDd);
                } case ("dampenedVacRate"): {
                    double x = (double) RealEstateDataManager.getNumberOfVacantDDinRegion(regions[i]) /
                            (double) RealEstateDataManager.getNumberOfDDinRegion(regions[i]) * 100d;  // % vacancy
                    double y = 1.4186E-03 * Math.pow(x, 3) - 6.7846E-02 * Math.pow(x, 2) + 1.0292 * x + 4.5485E-03;
                    y = Math.min(5d, y);                                                // % vacancy assumed to be ready to move in
                    regionUtilities[i] = regionUtilities[i] * (y / 100d * RealEstateDataManager.getNumberOfDDinRegion(regions[i]));
                    if (RealEstateDataManager.getNumberOfVacantDDinRegion(regions[i]) < 1) regionUtilities[i] = 0d;
                } case ("population"): {
                    regionUtilities[i] = regionUtilities[i] * householdsByRegion[i];
                } case ("noNormalization"): {
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
            Dwelling dd = Dwelling.getDwellingFromId(vacantDwellings[i]);
            double util = calculateDwellingUtilityOfHousehold(ht, householdIncome, dd, modelContainer);
            dwellingCalculator.setDwellingUtility(util);
            try {
                expProbs[i] = dwellingCalculator.calculate();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            sumProbs =+ expProbs[i];
        }
        if (sumProbs == 0) return -1;    // could not find dwelling that fits restrictions
        int selected = SiloUtil.select(expProbs, sumProbs);
        return vacantDwellings[selected];
    }

    @Override
    protected double calculateDwellingUtilityOfHousehold(HouseholdType ht, int income, Dwelling dd, SiloModelContainer modelContainer) {
        evaluateDwellingDmu.setUtilityDwellingQuality(convertQualityToUtility(dd.getQuality()));
        evaluateDwellingDmu.setUtilityDwellingSize(convertAreaToUtility(dd.getBedrooms()));
        evaluateDwellingDmu.setUtilityDwellingAutoAccessibility(convertAccessToUtility(modelContainer.getAcc().getAutoAccessibility(dd.getZone())));
        evaluateDwellingDmu.setUtilityDwellingTransitAccessibility(convertAccessToUtility(modelContainer.getAcc().getTransitAccessibility(dd.getZone())));

        int price = dd.getPrice();
        evaluateDwellingDmu.setUtilityDwellingPrice(convertPriceToUtility(price, ht));
        evaluateDwellingDmu.setType(ht);
        double util[] = ddUtilityModel.solve(evaluateDwellingDmu.getDmuIndexValues(), evaluateDwellingDmu, evalDwellingAvail);
        if (logCalculationDwelling)
            ddUtilityModel.logAnswersArray(traceLogger, "Quality of dwelling " + dd.getId());
        return util[0];
    }

}
