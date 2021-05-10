package de.tum.bgu.msm.data.dwelling;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.development.Development;
import de.tum.bgu.msm.data.development.DevelopmentImpl;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.HouseholdData;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.io.output.DefaultDwellingWriter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static de.tum.bgu.msm.data.dwelling.RealEstateUtils.RENT_CATEGORIES;

/**
 * Keeps data of dwellings and non-residential floorspace
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 7 January 2010 in Rhede
 **/
public class RealEstateDataManagerImpl implements RealEstateDataManager {

    private final static Logger logger = Logger.getLogger(RealEstateDataManagerImpl.class);

    private final DwellingData dwellingData;

    private final HouseholdData householdData;
    private final GeoData geoData;

    private final DwellingFactory dwellingFactory;
    private final Properties properties;

    public static int largestNoBedrooms;

    /**
     * Stores current shares of dwellings by quality levels. Updated once per year.
     */
    private final Map<Integer, Double> updatedQualityShares = new HashMap<>();
    /**
     * Stores initial shares of dwellings by quality levels in the base year.
     */
    private final Map<Integer, Double> initialQualityShares = new HashMap<>();

    private int highestDwellingIdInUse;
    private static final Map<IncomeCategory, Map<Integer, Float>> ddPriceByIncomeCategory = new EnumMap<>(IncomeCategory.class);

    private Map<Integer, List<Dwelling>> vacDwellingsByRegion = new LinkedHashMap<>();

    private double[] avePrice;
    private double[] aveVac;

    private final DwellingTypes dwellingTypes;

    public RealEstateDataManagerImpl(DwellingTypes dwellingTypes, DwellingData dwellingData,
                                     HouseholdData householdData, GeoData geoData,
                                     DwellingFactory dwellingFactory, Properties properties) {
        this.dwellingData = dwellingData;
        this.householdData = householdData;
        this.geoData = geoData;
        this.dwellingFactory = dwellingFactory;
        this.properties = properties;

        this.dwellingTypes = dwellingTypes;
    }

    @Override
    public DwellingFactory getDwellingFactory() {
        return dwellingFactory;
    }

    @Override
    public DwellingData getDwellingData() {
        return dwellingData;
    }

    @Override
    public void setup() {
        readDevelopmentData();
        calculateInitialDistributionOfDwellingQualityLevels();
        setHighestVariablesAndCalculateRentShareByIncome();
        identifyVacantDwellings();
    }

    @Override
    public void prepareYear(int year) {
        calculateRegionWidePriceAndVacancyByDwellingType();
        updatedQualityShares.clear();
        Map<Integer, List<Dwelling>> sortedDds = dwellingData.getDwellings().stream().collect(Collectors.groupingBy(Dwelling::getQuality));
        for(Map.Entry<Integer, List<Dwelling>> entry: sortedDds.entrySet()) {
            updatedQualityShares.put(entry.getKey(), ((double) entry.getValue().size()) / dwellingData.getDwellings().size());
        }
    }

    @Override
    public void endYear(int year) {
        if (!properties.realEstate.dwellingsIntermediatesFileName.equals("")) {
            final String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName + "/";
            String filedd = outputDirectory
                    + properties.realEstate.dwellingsIntermediatesFileName
                    + "_"
                    + year
                    + ".csv";
            new DefaultDwellingWriter(this.dwellingData.getDwellings()).writeDwellings(filedd);
        }
    }

    @Override
    public void endSimulation() {
        final String outputDirectory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName +"/";
        String filedd = outputDirectory
                + properties.realEstate.dwellingsFinalFileName
                + "_"
                + properties.main.endYear
                + ".csv";
        new DefaultDwellingWriter(this.dwellingData.getDwellings()).writeDwellings(filedd);
    }

    @Override
    public Map<Integer, Float> getRentPaymentsForIncomeGroup(IncomeCategory incomeCategory) {
        return ddPriceByIncomeCategory.get(incomeCategory);
    }

    @Override
    public int getNextDwellingId() {
        // increase highestDwellingIdInUse by 1 and return value
        return ++highestDwellingIdInUse;
    }

    @Override
    public Map<Integer, Double> getInitialQualShares() {
        return initialQualityShares;
    }

    @Override
    public Map<Integer, Double> getUpdatedQualityShares() {
        return updatedQualityShares;
    }

    /**
     * Return array with IDs of vacant dwellings in region
     * @param region
     * @return
     */
    @Override
    public List<Dwelling> getListOfVacantDwellingsInRegion(int region) {
        return Collections.unmodifiableList(vacDwellingsByRegion.getOrDefault(region,
                new ArrayList<>()));
    }

    @Override
    public int getNumberOfVacantDDinRegion(int region) {
        return vacDwellingsByRegion.getOrDefault(region, new ArrayList<>()).size();
    }

    @Override
    public DwellingTypes getDwellingTypes() {
        return dwellingTypes;
    }

    @Override
    public Dwelling getDwelling(int dwellingId) {
        return dwellingData.getDwelling(dwellingId);
    }

    @Override
    public Collection<Dwelling> getDwellings() {
        return Collections.unmodifiableCollection(dwellingData.getDwellings());
    }

    @Override
    public void removeDwelling(int id) {
        dwellingData.removeDwelling(id);
    }

    @Override
    public void addDwelling(Dwelling dwelling) {
        this.dwellingData.addDwelling(dwelling);
    }


    /**
     * Walk through all dwellings and identify vacant dwellings
     * (one-time task at beginning of model run only)
     */
    private void identifyVacantDwellings() {
        logger.info("  Identifying vacant dwellings");
        for (Dwelling dd : dwellingData.getDwellings()) {
            if (dd.getResidentId() == -1) {
                int dwellingId = dd.getId();
                //logger.info(dwellingId);
                int region = geoData.getZones().get(dd.getZoneId()).getRegion().getId();
                vacDwellingsByRegion.putIfAbsent(region, new ArrayList<>());
                vacDwellingsByRegion.get(region).add(dd);
                if (dwellingId == SiloUtil.trackDd) {
                    SiloUtil.trackWriter.println("Added dwelling " + dwellingId + " to list of vacant dwelling.");
                }
            }
        }
    }

    /**
     *  Count number of dwellings by quality and calculate average quality
     */
    private void calculateInitialDistributionOfDwellingQualityLevels() {
        Map<Integer, List<Dwelling>> sortedDds = dwellingData.getDwellings().stream().collect(Collectors.groupingBy(Dwelling::getQuality));
        for(Map.Entry<Integer, List<Dwelling>> entry: sortedDds.entrySet()) {
            initialQualityShares.put(entry.getKey(), ((double) entry.getValue().size()) / dwellingData.getDwellings().size());
        }
    }

    /**
     *
     * @return map of rent by region. Infinity value if no dwellings are present in that region
     */
    @Override
    public Map<Integer, Double> calculateRegionalPrices() {
        final Map<Integer, Zone> zones = geoData.getZones();
        final Map<Integer, List<Dwelling>> dwellingsByRegion =
                dwellingData.getDwellings().parallelStream().collect(Collectors.groupingByConcurrent(d ->
                        zones.get(d.getZoneId()).getRegion().getId()));
        //We set
        final Map<Integer, Double> rentsByRegion = geoData.getRegions().entrySet().parallelStream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> dwellingsByRegion.getOrDefault(e.getKey(),Collections.emptyList()).stream().mapToDouble(Dwelling::getPrice).average().orElse(Double.POSITIVE_INFINITY)));
        return rentsByRegion;
    }


    private void setHighestVariablesAndCalculateRentShareByIncome() {
        // identify highest dwelling ID in use and largest bedrooms, also calculate share of rent paid by each hh type
        // only done initially when model starts

        highestDwellingIdInUse = 0;
        largestNoBedrooms = 0;

        // identify how much rent (specified by 25 rent categories) is paid by households of each income category
        Map<IncomeCategory, Multiset<Integer>> countOfHouseholdsByIncomeAndRentCategory = new EnumMap<>(IncomeCategory.class);
        for (IncomeCategory incomeCat : IncomeCategory.values()) {
            countOfHouseholdsByIncomeAndRentCategory.put(incomeCat, HashMultiset.create());
        }
        for (Dwelling dd : dwellingData.getDwellings()) {
            highestDwellingIdInUse = Math.max(highestDwellingIdInUse, dd.getId());
            largestNoBedrooms = Math.max(largestNoBedrooms, dd.getBedrooms());
            int hhId = dd.getResidentId();
            if (hhId > 0) {
                int hhinc = HouseholdUtil.getAnnualHhIncome(householdData.getHousehold(hhId));
                IncomeCategory incomeCategory = HouseholdUtil.getIncomeCategoryForIncome(hhinc);
                int rentCategory = (int) ((dd.getPrice() * 1.) / 200.);  // rent category defined as <rent/200>
                rentCategory = Math.min(rentCategory, RENT_CATEGORIES);   // ensure that rent categories do not exceed max
                countOfHouseholdsByIncomeAndRentCategory.get(incomeCategory).add(rentCategory);
            }
        }
        IncomeCategory highestIncCat = IncomeCategory.values()[IncomeCategory.values().length - 1];
        countOfHouseholdsByIncomeAndRentCategory.get(highestIncCat).add(RENT_CATEGORIES);  // make sure that most expensive category can be afforded by richest households
        for (IncomeCategory incomeCategory : IncomeCategory.values()) {
            float sum = countOfHouseholdsByIncomeAndRentCategory.get(incomeCategory).size();
            Map<Integer, Float> shareOfRentsForThisIncCat = new HashMap<>();
            for (int rentCategory = 0; rentCategory <= RENT_CATEGORIES; rentCategory++) {
                int thisRentAndIncomeCat = countOfHouseholdsByIncomeAndRentCategory.get(incomeCategory).count(rentCategory);
                if (sum != 0) {
                    shareOfRentsForThisIncCat.put(rentCategory, thisRentAndIncomeCat / sum);
                } else {
                    //todo if there is not a househould of this rent and this category the shares should be zero?
                    shareOfRentsForThisIncCat.put(rentCategory, 0.f);
                }
            }
            ddPriceByIncomeCategory.put(incomeCategory, shareOfRentsForThisIncCat);
        }
    }


    /**
     *  Remove dwelling with ID ddId from list of vacant dwellings
     * @param ddId
     */
    @Override
    public void removeDwellingFromVacancyList(int ddId) {

        boolean found = false;

        Dwelling dwelling = dwellingData.getDwelling(ddId);
        int region = geoData.getZones().get(dwelling.getZoneId()).getRegion().getId();
        List<Dwelling> vacDwellings = vacDwellingsByRegion.get(region);
        if (vacDwellings != null) {
            found = vacDwellings.remove(dwelling);
            if (ddId == SiloUtil.trackDd) {
                SiloUtil.trackWriter.println("Removed dwelling " + ddId +
                        " from list of vacant dwellings.");
            }
        }

        if (!found) {
            logger.warn("Consistency error: Could not find vacant dwelling "
                    + ddId + " in vacDwellingsByRegion.");
        }
    }

    /**
     * Add dwelling to vacancy list
     * @param dd
     */
    @Override
    public void addDwellingToVacancyList(Dwelling dd) {

        int region = geoData.getZones().get(dd.getZoneId()).getRegion().getId();
        vacDwellingsByRegion.putIfAbsent(region, new ArrayList<>());
        vacDwellingsByRegion.get(region).add(dd);
        if (dd.getId() == SiloUtil.trackDd) {
            SiloUtil.trackWriter.println("Added dwelling " + dd.getId() +
                    " to list of vacant dwellings.");
        }
    }


    private void calculateRegionWidePriceAndVacancyByDwellingType() {
        // calculate region-wide average dwelling costs and vacancy by dwelling type
        logger.info("Updating region-wide average dwelling costs and vacancies:");

        int distinctDdTypes = dwellingTypes.getTypes().size();
        int[][] vacOcc = SiloUtil.setArrayToValue(new int[2][distinctDdTypes], 0);
        long[] price = SiloUtil.setArrayToValue(new long[distinctDdTypes], 0);

        for (Dwelling dd : dwellingData.getDwellings()) {
            int dto = dwellingTypes.getTypes().indexOf(dd.getType());
            price[dto] += dd.getPrice();

            if (dd.getResidentId() > 0) {
                vacOcc[1][dto]++;
            } else {
                vacOcc[0][dto]++;
            }
        }
        aveVac = new double[distinctDdTypes];
        avePrice = new double[distinctDdTypes];

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        for (DwellingType dt : dwellingTypes.getTypes()) {
            int dto = dwellingTypes.getTypes().indexOf(dt);

            if (vacOcc[0][dto] + vacOcc[1][dto] > 0) {
                aveVac[dto] = (double) vacOcc[0][dto] / (double) (vacOcc[0][dto] + vacOcc[1][dto]);
                avePrice[dto] = price[dto] / (double) (vacOcc[0][dto] + vacOcc[1][dto]);

            } else {
                aveVac[dto] = 0;
                avePrice[dto] = 0;
            }
            logger.info(dt + ": " + df.format(aveVac[dto]) + "% vacancy | " + df.format(avePrice[dto]) + " price");
        }
    }


    @Override
    public double[][] getVacancyRateByTypeAndRegion() {
        // calculate vacancy rate by region and dwelling type
        final int highestRegionId = geoData.getRegions().keySet().stream().mapToInt(Integer::intValue).max().getAsInt();
        int[][][] vacOcc = SiloUtil.setArrayToValue(new int[2][dwellingTypes.getTypes().size()][highestRegionId + 1], 0);

        for (Dwelling dd : dwellingData.getDwellings()) {
            int dto = dwellingTypes.getTypes().indexOf(dd.getType());
            if (dd.getResidentId() > 0) {
                vacOcc[1][dto][geoData.getZones().get(dd.getZoneId()).getRegion().getId()]++;
            } else {
                vacOcc[0][dto][geoData.getZones().get(dd.getZoneId()).getRegion().getId()]++;
            }
        }

        double[][] vacRate = new double[dwellingTypes.getTypes().size()][highestRegionId + 1];
        for (DwellingType dt : dwellingTypes.getTypes()) {
            int dto = dwellingTypes.getTypes().indexOf(dt);
            for (int region : geoData.getRegions().keySet()) {
                if ((vacOcc[0][dto][region] + vacOcc[1][dto][region]) > 0) {
                    vacRate[dto][region] = (double) vacOcc[0][dto][region] / (double) (vacOcc[0][dto][region] + vacOcc[1][dto][region]);
                } else {
                    vacRate[dto][region] = 0.;
                }
            }
        }
        return vacRate;
    }


    @Override
    public void setAvePriceByDwellingType(double[] newAvePrice) {
        avePrice = newAvePrice;
    }


    @Override
    public double[] getAveragePriceByDwellingType() {
        return avePrice;
    }


    @Override
    public double[] getAverageVacancyByDwellingType() {
        return aveVac;
    }


    @Override
    public int[][] getDwellingCountByTypeAndRegion() {
        // return number of dwellings by type and region
        final int highestRegionId = geoData.getRegions().keySet().stream().mapToInt(Integer::intValue).max().getAsInt();
        int[][] dwellingCount =
                SiloUtil.setArrayToValue(new int[dwellingTypes.getTypes().size()][highestRegionId + 1], 1);

        for (Dwelling dd : dwellingData.getDwellings()) {
            dwellingCount[dwellingTypes.getTypes().indexOf(dd.getType())][geoData.getZones().get(dd.getZoneId()).getRegion().getId()]++;
        }
        return dwellingCount;
    }


    @Override
    public double getAvailableCapacityForConstruction(int zone) {
        // return available land in developable land-use categories
        double sm;
        Development development = geoData.getZones().get(zone).getDevelopment();
        if (development.isUseDwellingCapacity()) {
            sm = development.getDwellingCapacity();
        } else {
            sm = development.getDevelopableArea();
        }
        return sm;
    }

    /**
     * Remove acres from developable land
     * @param zone
     * @param acres
     */
    @Override
    public void convertLand(int zone, float acres) {
        Development development = geoData.getZones().get(zone).getDevelopment();
        if (development.isUseDwellingCapacity()) {
            development.changeCapacityBy(-1);
        } else {
            development.changeAreaBy(-acres);
        }
    }

    private void readDevelopmentData() {
        String baseDirectory = Properties.get().main.baseDirectory;

        TableDataSet developmentTable = SiloUtil.readCSVfile(baseDirectory + Properties.get().geo.landUseAndDevelopmentFile);

        int[] zoneIdData = developmentTable.getColumnAsInt("Zone");
        Map<DwellingType, int[]> constraintData = new HashMap<>();
        for (DwellingType dwellingType : dwellingTypes.getTypes()) {
            constraintData.put(dwellingType, developmentTable.getColumnAsInt(dwellingType.toString()));
        }
        int[] dwellingCapacityData = developmentTable.getColumnAsInt("DevCapacity");
        double[] landUseData = developmentTable.getColumnAsDouble("DevLandUse");

        for (int i = 0; i < zoneIdData.length; i++) {
            if(geoData.getZones().containsKey(zoneIdData[i])) {

                Map<DwellingType, Boolean> constraints = new HashMap<>();
                for (DwellingType dwellingType : dwellingTypes.getTypes()) {
                    constraints.put(dwellingType, constraintData.get(dwellingType)[i] == 1);
                }
                int thisZoneCapacity = (int) (dwellingCapacityData[i] * properties.main.scaleFactor);
                double thisZoneLandUse = landUseData[i] * properties.main.scaleFactor;

                Development development = new DevelopmentImpl(thisZoneLandUse, thisZoneCapacity, constraints, Properties.get().geo.useCapacityForDwellings);
                geoData.getZones().get(zoneIdData[i]).setDevelopment(development);
            }
        }

    }

    /**
     * Vacates a dwelling by setting resident id to -1 and adding the dwelling to the vacancy list.
     *
     * @param idOldDD
     */
    @Override
    public void vacateDwelling(int idOldDD) {
        Dwelling dd = getDwelling(idOldDD);
        dd.setResidentID(-1);
        addDwellingToVacancyList(dd);
    }
}
