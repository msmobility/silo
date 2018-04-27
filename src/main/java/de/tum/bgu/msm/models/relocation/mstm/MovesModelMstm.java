package de.tum.bgu.msm.models.relocation.mstm;

/*
* @author Rolf Moeckel (PB Albuquerque)
* Created on Apr 4, 2011 in Albuquerque, NM
* Revised on Apr 24, 2014 in College Park, MD
*/

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import com.pb.common.calculator.UtilityExpressionCalculator;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.maryland.MstmRegion;
import de.tum.bgu.msm.data.maryland.MstmZone;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.models.relocation.AbstractDefaultMovesModel;
import de.tum.bgu.msm.models.relocation.MovesDMU;
import de.tum.bgu.msm.util.matrices.Matrices;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MovesModelMstm extends AbstractDefaultMovesModel {

    private int numAltsSelReg;

    private double parameter_SelectDD;

    private UtilityExpressionCalculator selectRegionModel;
    private MovesDMU selectRegionDmu;
    private DoubleMatrix2D zonalRacialComposition;
    private DoubleMatrix2D regionalRacialComposition;
    private DoubleMatrix1D hhByRegion;
    private double selectDwellingRaceRelevance;
    private boolean provideRentSubsidyToLowIncomeHh;


    public MovesModelMstm(SiloDataContainer dataContainer, Accessibility accessibility) {
        super(dataContainer, accessibility);
        selectDwellingRaceRelevance = Properties.get().moves.racialRelevanceInZone;
        provideRentSubsidyToLowIncomeHh = Properties.get().moves.provideLowIncomeSubsidy;
        if (provideRentSubsidyToLowIncomeHh) {
            dataContainer.getRealEstateData().calculateMedianRentByMSA();
        }
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
                zone = dwelling.getZone();
            }
            final int region = geoData.getZones().get(zone).getRegion().getId();

            zonalRacialComposition.setQuick(zone, hh.getRace().getId(),
                    zonalRacialComposition.getQuick(zone, hh.getRace().getId()) + 1);
            regionalRacialComposition.setQuick(region, hh.getRace().getId(),
                    regionalRacialComposition.getQuick(region, hh.getRace().getId()) + 1);

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

        int selRegModelSheetNumber = Properties.get().moves.selectRegionModelSheet;
        // initialize UEC
        selectRegionModel = new UtilityExpressionCalculator(new File(uecFileName),
                selRegModelSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                MovesDMU.class);
        selectRegionDmu = new MovesDMU();
        numAltsSelReg = selectRegionModel.getNumberOfAlternatives();
    }

    @Override
    public void calculateRegionalUtilities() {
        // everything is available

        calculateRacialCompositionByZoneAndRegion();
        int[] selRegAvail = new int[numAltsSelReg + 1];
        for (int i = 1; i < selRegAvail.length; i++) selRegAvail[i] = 1;

        int highestRegion = geoData.getRegions().keySet().stream().reduce(Integer::max).get().intValue();
        int[] regPrice = new int[highestRegion + 1];
        float[] regAcc = new float[highestRegion + 1];
        float[] regSchQu = new float[highestRegion + 1];
        float[] regCrime = new float[highestRegion + 1];
        Map<Integer, Double> rentsByRegion = calculateRegionalPrices();
        for (Region region: geoData.getRegions().values()) {
            final int id = region.getId();
            int price;
            if(rentsByRegion.containsKey(id)) {
                price = rentsByRegion.get(id).intValue();
            } else {
                price = 0;
            }
            regPrice[id] = price;
            regAcc[id] = (float) convertAccessToUtility(accessibility.getRegionalAccessibility(id));
            regSchQu[id] = (float) ((MstmRegion) region).getSchoolQuality();
            regCrime[id] = (float) (1f - ((MstmRegion) region).getCrimeRate());  // invert utility, as lower crime rate has higher utility
        }
        selectRegionDmu.setRegionalAccessibility(regAcc);
        selectRegionDmu.setRegionalSchoolQuality(regSchQu);
        selectRegionDmu.setRegionalCrimeRate(regCrime);
        for (Race race: Race.values()) {
            float[] regionalRacialShare = new float[highestRegion + 1];
            for (int regionId: geoData.getRegions().keySet()) {
                regionalRacialShare[regionId] = (float) regionalRacialComposition.getQuick(regionId, race.getId());
            }
            selectRegionDmu.setRegionalRace(race, regionalRacialShare);
        }
        utilityRegion = new double[Properties.get().main.incomeBrackets.length + 1][Race.values().length][numAltsSelReg];
        for (int income = 1; income <= Properties.get().main.incomeBrackets.length + 1; income++) {
            // set DMU attributes
            float[] priceUtil = new float[highestRegion + 1];
            for (int regionId: geoData.getRegions().keySet()) {
                priceUtil[regionId] = (float) convertPriceToUtility(regPrice[regionId], income);
            }
            selectRegionDmu.setMedianRegionPrice(priceUtil);
            selectRegionDmu.setIncomeGroup(income - 1);
            for (Race race: Race.values()) {
                selectRegionDmu.setRace(race);
                double util[] = selectRegionModel.solve(selectRegionDmu.getDmuIndexValues(), selectRegionDmu, selRegAvail);
                for (int alternative = 0; alternative < numAltsSelReg; alternative++) {
                    utilityRegion[income - 1][race.getId()][alternative] = util[alternative];
                }
                if (logCalculationRegion)
                    selectRegionModel.logAnswersArray(traceLogger, "Select-Region Model for HH of income group " +
                            income + " with race " + race);
            }
        }
    }


    @Override
    protected void setupSelectDwellingModel() {
        // set up model for choice of dwelling

        int selectDwellingSheetNumber = Properties.get().moves.selectDwellingSheet;
        // initialize UEC
        UtilityExpressionCalculator selectDwellingModel = new UtilityExpressionCalculator(new File(uecFileName),
                selectDwellingSheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                MovesDMU.class);
        MovesDMU selectDwellingDmu = new MovesDMU();
        // everything is available
        numAltsMoveOrNot = selectDwellingModel.getNumberOfAlternatives();
        int[] selectDwellingAvail = new int[numAltsMoveOrNot + 1];
        for (int i = 1; i < selectDwellingAvail.length; i++) selectDwellingAvail[i] = 1;
        // set DMU attributes
        selectDwellingModel.solve(selectDwellingDmu.getDmuIndexValues(), selectDwellingDmu, selectDwellingAvail);
        // todo: looks wrong, parameter should be read from UEC file, not from properties file
        parameter_SelectDD = Properties.get().moves.selectDwellingParameter;
        if (logCalculationDwelling) {
            // log UEC values for each household type
            selectDwellingModel.logAnswersArray(traceLogger, "Select-Dwelling Model");
        }
    }


    private double[] getRegionUtilities (HouseholdType ht, Race race, int[] workZones) {
        // return utility of regions based on household type and based on work location of workers in household

        int[] regions = geoData.getRegionIdsArray();
        double[] util = new double[numAltsSelReg];
        double[] workDistanceFactor = new double[numAltsSelReg];
        for (int i = 0; i < numAltsSelReg; i++) {
            workDistanceFactor[i] = 1;
            if (workZones != null) {  // for inmigrating household, work places are selected after household found a home
                for (int workZone : workZones) {
                    int smallestDistInMin = (int) accessibility.getMinTravelTimeFromZoneToRegion(workZone, regions[i]);
                    workDistanceFactor[i] = workDistanceFactor[i] * accessibility.getCommutingTimeProbability(smallestDistInMin);
                }
            }
        }
        int incomeCat = HouseholdType.convertHouseholdTypeToIncomeCategory(ht);
        for (int i = 0; i < numAltsSelReg; i++) {
            util[i] = utilityRegion[incomeCat - 1][race.getId()][i] * workDistanceFactor[i];
        }
        return util;
    }

    @Override
    public int searchForNewDwelling(List<Person> persons) {
        // search alternative dwellings

        // data preparation
        int wrkCount = 0;
        for (Person pp: persons) {
            if (pp.getOccupation() == 1 && pp.getWorkplace() != -2) wrkCount++;
        }
        int pos = 0;
        int householdIncome = 0;
        int[] workZones = new int[wrkCount];
        Race householdRace = persons.get(0).getRace();
        JobDataManager jobData = dataContainer.getJobData();
        for (Person pp: persons) {
            if (pp.getOccupation() == 1 && pp.getWorkplace() != -2) {
                workZones[pos] = jobData.getJobFromId(pp.getWorkplace()).getZone();
                pos++;
                householdIncome += pp.getIncome();
                if (pp.getRace() != householdRace) householdRace = Race.other;
            }
        }
        int incomeBracket = HouseholdDataManager.getIncomeCategoryForIncome(householdIncome);
        HouseholdType ht = HouseholdDataManager.defineHouseholdType(persons.size(), incomeBracket);

        // Step 1: select region
        int[] regions = geoData.getRegionIdsArray();
        double[] regionUtilities = getRegionUtilities(ht, householdRace, workZones);
        // todo: adjust probabilities to make that households tend to move shorter distances (dist to work is already represented)
        String normalizer = "population";
        int totalVacantDd = 0;
        for (int region: geoData.getRegions().keySet()) {
            totalVacantDd += RealEstateDataManager.getNumberOfVacantDDinRegion(region);
        }
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
                    regionUtilities[i] = regionUtilities[i] * hhByRegion.getQuick(regions[i]);
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
        int maxNumberOfDwellings = Math.min(20, vacantDwellings.length);  // No household will evaluate more than 20 dwellings
        float factor = ((float) maxNumberOfDwellings / (float) vacantDwellings.length);
        for (int i = 0; i < vacantDwellings.length; i++) {
            if (SiloUtil.getRandomNumberAsFloat() > factor) continue;
            Dwelling dd = dataContainer.getRealEstateData().getDwelling(vacantDwellings[i]);
            int msa = geoData.getZones().get(dd.getZone()).getMsa();
            if (dd.getRestriction() > 0 &&    // dwelling is restricted to households with certain income
                    householdIncome > (HouseholdDataManager.getMedianIncome(msa) * dd.getRestriction())) continue;
            double racialShare = 1;
            if (householdRace != Race.other) {
                racialShare = getZonalRacialShare(geoData.getZones().get(dd.getZone()).getId(), householdRace);
            }
            // multiply by racial share to make zones with higher own racial share more attractive
            double adjProb;
            if (householdQualifiesForSubsidy(householdIncome, geoData.getZones().get(dd.getZone()).getId(), dd.getPrice())) {
                adjProb = Math.pow(calculateDwellingUtilityOfHousehold(ht, householdIncome, dd), (1 - selectDwellingRaceRelevance)) *
                        Math.pow(racialShare, selectDwellingRaceRelevance);
            } else {
                adjProb = Math.pow(dd.getUtilByHhType()[ht.ordinal()], (1 - selectDwellingRaceRelevance)) *
                        Math.pow(racialShare, selectDwellingRaceRelevance);
            }
            expProbs[i] = Math.exp(parameter_SelectDD * adjProb);
        }
        if (SiloUtil.getSum(expProbs) == 0) return -1;    // could not find dwelling that fits restrictions
        int selected = SiloUtil.select(expProbs);
        return vacantDwellings[selected];
    }



    @Override
    protected double calculateDwellingUtilityOfHousehold(HouseholdType ht, int income, Dwelling dd) {
//        evaluateDwellingDmu.setUtilityDwellingQuality(convertQualityToUtility(dd.getQuality()));
//        evaluateDwellingDmu.setUtilityDwellingSize(convertAreaToUtility(dd.getBedrooms()));
//        evaluateDwellingDmu.setUtilityDwellingAutoAccessibility(convertAccessToUtility(accessibility.getAutoAccessibilityForZone(dd.getZone())));
//        evaluateDwellingDmu.setUtilityDwellingTransitAccessibility(convertAccessToUtility(accessibility.getTransitAccessibilityForZone(dd.getZone())));
//        evaluateDwellingDmu.setUtilityDwellingSchoolQuality(((MstmZone) geoData.getZones().get(dd.getZone())).getSchoolQuality());
//        evaluateDwellingDmu.setUtilityDwellingCrimeRate(((MstmZone) geoData.getZones().get(dd.getZone())).getCounty().getCrimeRate());

        double ddQualityUtility = convertQualityToUtility(dd.getQuality());
        double ddSizeUtility = convertAreaToUtility(dd.getBedrooms());
        double ddAutoAccessibilityUtility = convertAccessToUtility(accessibility.getAutoAccessibilityForZone(dd.getZone()));
        double transitAccessibilityUtility = convertAccessToUtility(accessibility.getTransitAccessibilityForZone(dd.getZone()));

        int price = dd.getPrice();
        if (provideRentSubsidyToLowIncomeHh && income > 0) {     // income equals -1 if dwelling is vacant right now
            // housing subsidy program in place
            int msa = geoData.getZones().get(dd.getZone()).getMsa();
            if (income < (0.5f * HouseholdDataManager.getMedianIncome(msa)) && price < (0.4f * income / 12f)) {
                float housingBudget = (income / 12f * 0.18f);  // technically, the housing budget is 30%, but in PUMS data households pay 18% on the average
                float subsidy = RealEstateDataManager.getMedianRent(msa) - housingBudget;
                price = Math.max(0, price - (int) (subsidy + 0.5));
            }
        }

        double ddPriceUtility = convertPriceToUtility(price, ht);

        double ddUtility = dwellingUtilityJSCalculator.calculateSelectDwellingUtility(ht, ddSizeUtility, ddPriceUtility,
                ddQualityUtility, ddAutoAccessibilityUtility,
                transitAccessibilityUtility, 0.0, 0.0);

//        evaluateDwellingDmu.setUtilityDwellingPrice(convertPriceToUtility(price, ht));
//        evaluateDwellingDmu.setType(ht);
//        double util[] = ddUtilityModel.solve(evaluateDwellingDmu.getDmuIndexValues(), evaluateDwellingDmu, evalDwellingAvail);
//         log UEC values for each household type
//        if (logCalculationDwelling)
//            ddUtilityModel.logAnswersArray(traceLogger, "Quality of dwelling " + dd.getId());
        return ddUtility;
    }


    private boolean householdQualifiesForSubsidy(int income, int zone, int price) {
        int assumedIncome = Math.max(income, 15000);  // households with less than that must receive some welfare
        return provideRentSubsidyToLowIncomeHh &&
                income <= (0.5f * HouseholdDataManager.getMedianIncome(geoData.getZones().get(zone).getMsa())) &&
                price <= (0.4f * assumedIncome);
    }
}
