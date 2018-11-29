package de.tum.bgu.msm.data;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Keeps data of dwellings and non-residential floorspace
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 7 January 2010 in Rhede
 **/

public class RealEstateDataManager {
    static Logger logger = Logger.getLogger(RealEstateDataManager.class);

    private final SiloDataContainer dataContainer;
    private final Map<Integer, Dwelling> dwellings = new HashMap<>();

    public static int largestNoBedrooms;
    public static int[] dwellingsByQuality;
    private static double[] initialQualityShares;
    private static int highestDwellingIdInUse;
    public static final int rentCategories = 25;
    private static final Map<IncomeCategory, Map<Integer, Float>> ddPriceByIncomeCategory = new EnumMap<>(IncomeCategory.class);

    private static int[] dwellingsByRegion;
    private static int[][] vacDwellingsByRegion;
    private static int[] vacDwellingsByRegionPos;
    private static int numberOfStoredVacantDD;
    private double[] avePrice;
    private double[] aveVac;
    private static float[] medianRent;

    private final List<DwellingType> dwellingTypes = new ArrayList<>();

    public List<DwellingType> getDwellingTypes() {
        return Collections.unmodifiableList(dwellingTypes);
    }

    public RealEstateDataManager(SiloDataContainer dataContainer, List<DwellingType> dwellingTypes) {
        this.dataContainer = dataContainer;
        this.dwellingTypes.addAll(dwellingTypes);
    }

    public Dwelling getDwelling(int dwellingId) {
        return dwellings.get(dwellingId);
    }

    public Collection<Dwelling> getDwellings() {
        return Collections.unmodifiableCollection(dwellings.values());
    }

    public void removeDwelling(int id) {
        dwellings.remove(id);
    }

    public void addDwelling(Dwelling dwelling) {
        this.dwellings.put(dwelling.getId(), dwelling);
    }

    public void identifyVacantDwellings() {
        // walk through all dwellings and identify vacant dwellings (one-time task at beginning of model run only)

        final GeoData geoData = dataContainer.getGeoData();
        int highestRegion = geoData.getRegions().keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        numberOfStoredVacantDD = Properties.get().realEstate.maxStorageOfVacantDwellings;
        dwellingsByRegion = new int[highestRegion + 1];
        vacDwellingsByRegion = new int[highestRegion + 1][numberOfStoredVacantDD + 1];
        vacDwellingsByRegion = SiloUtil.setArrayToValue(vacDwellingsByRegion, 0);
        vacDwellingsByRegionPos = new int[highestRegion + 1];
        vacDwellingsByRegionPos = SiloUtil.setArrayToValue(vacDwellingsByRegionPos, 0);

        logger.info("  Identifying vacant dwellings");
        for (Dwelling dd : dwellings.values()) {
            if (dd.getResidentId() == -1) {
                int dwellingId = dd.getId();
                int region = geoData.getZones().get(dd.getZoneId()).getRegion().getId();
                dwellingsByRegion[region]++;
                vacDwellingsByRegion[region][vacDwellingsByRegionPos[region]] = dwellingId;
                if (vacDwellingsByRegionPos[region] < numberOfStoredVacantDD) vacDwellingsByRegionPos[region]++;
                if (vacDwellingsByRegionPos[region] >= numberOfStoredVacantDD)
                    IssueCounter.countExcessOfVacantDwellings(region);
                if (dwellingId == SiloUtil.trackDd)
                    SiloUtil.trackWriter.println("Added dwelling " + dwellingId + " to list of vacant dwelling.");
            }
        }
    }

    public void fillQualityDistribution() {
        // count number of dwellings by quality and calculate average quality
        int numberOfQualityLevels = Properties.get().main.qualityLevels;
        dwellingsByQuality = new int[numberOfQualityLevels];
        initialQualityShares = new double[numberOfQualityLevels];
        for (Dwelling dd : getDwellings()) {
            dwellingsByQuality[dd.getQuality() - 1]++;
        }
        for (int qual = 1; qual <= numberOfQualityLevels; qual++) {
            initialQualityShares[qual - 1] = (double) dwellingsByQuality[qual - 1] /
                    (double) SiloUtil.getSum(dwellingsByQuality);
        }
    }


    public void setHighestVariablesAndCalculateRentShareByIncome() {
        // identify highest dwelling ID in use and largest bedrooms, also calculate share of rent paid by each hh type
        // only done initially when model starts

        highestDwellingIdInUse = 0;
        largestNoBedrooms = 0;

        // identify how much rent (specified by 25 rent categories) is paid by households of each income category
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        Map<IncomeCategory, Multiset<Integer>> countOfHouseholdsByIncomeAndRentCategory = new EnumMap<>(IncomeCategory.class);
        for (IncomeCategory incomeCat : IncomeCategory.values()) {
            countOfHouseholdsByIncomeAndRentCategory.put(incomeCat, HashMultiset.create());
        }
        for (Dwelling dd : dwellings.values()) {
            highestDwellingIdInUse = Math.max(highestDwellingIdInUse, dd.getId());
            largestNoBedrooms = Math.max(largestNoBedrooms, dd.getBedrooms());
            int hhId = dd.getResidentId();
            if (hhId > 0) {
                int hhinc = HouseholdUtil.getHhIncome(householdData.getHouseholdFromId(hhId));
                IncomeCategory incomeCategory = HouseholdDataManager.getIncomeCategoryForIncome(hhinc);
                int rentCategory = (int) ((dd.getPrice() * 1.) / 200.);  // rent category defined as <rent/200>
                rentCategory = Math.min(rentCategory, rentCategories);   // ensure that rent categories do not exceed max
                countOfHouseholdsByIncomeAndRentCategory.get(incomeCategory).add(rentCategory);
            }
        }
        IncomeCategory highestIncCat = IncomeCategory.values()[IncomeCategory.values().length - 1];
        countOfHouseholdsByIncomeAndRentCategory.get(highestIncCat).add(rentCategories);  // make sure that most expensive category can be afforded by richest households
        for (IncomeCategory incomeCategory : IncomeCategory.values()) {
            float sum = countOfHouseholdsByIncomeAndRentCategory.get(incomeCategory).size();
            Map<Integer, Float> shareOfRentsForThisIncCat = new HashMap<>();
            for (int rentCategory = 0; rentCategory <= rentCategories; rentCategory++) {
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


    public static Map<Integer, Float> getRentPaymentsForIncomeGroup(IncomeCategory incomeCategory) {
        return ddPriceByIncomeCategory.get(incomeCategory);
    }


    public static int getNextDwellingId() {
        // increase highestDwellingIdInUse by 1 and return value
        highestDwellingIdInUse++;
        return highestDwellingIdInUse;
    }


    public static double[] getInitialQualShares() {
        return initialQualityShares;
    }


    public static double[] getCurrentQualShares() {
        double[] currentQualityShares = new double[Properties.get().main.qualityLevels];
        for (int qual = 1; qual <= Properties.get().main.qualityLevels; qual++)
            currentQualityShares[qual - 1] =
                    (double) dwellingsByQuality[qual - 1] / (double) SiloUtil.getSum(dwellingsByQuality);
        return currentQualityShares;
    }


    public void calculateMedianRentByMSA() {

        final GeoData geoData = dataContainer.getGeoData();
        Map<Integer, ArrayList<Integer>> rentHashMap = new HashMap<>();
        for (Dwelling dd : dwellings.values()) {
            int dwellingMSA = geoData.getZones().get(dd.getZoneId()).getMsa();
            if (rentHashMap.containsKey(dwellingMSA)) {
                ArrayList<Integer> rents = rentHashMap.get(dwellingMSA);
                rents.add(dd.getPrice());
            } else {
                ArrayList<Integer> rents = new ArrayList<>();
                rents.add(dd.getPrice());
                rentHashMap.put(dwellingMSA, rents);
            }
        }
        medianRent = new float[99999];
        for (Integer thisMsa : rentHashMap.keySet()) {
            medianRent[thisMsa] = SiloUtil.getMedian(rentHashMap.get(thisMsa).stream().mapToInt(Integer::intValue).toArray());
        }
    }


    public static float getMedianRent(int msa) {
        return medianRent[msa];
    }


    public void summarizeDwellings() {
        // aggregate dwellings

        logger.info("****** Average Price is Written *******");

        SummarizeData.resultFile("QualityLevel,Dwellings");
        for (int qual = 1; qual <= Properties.get().main.qualityLevels; qual++) {
            String row = qual + "," + dwellingsByQuality[qual - 1];
            SummarizeData.resultFile(row);
        }

        Multiset<DwellingType> countsByDwellingType = HashMultiset.create(dwellingTypes.size());

        for (Dwelling dd : dwellings.values()) {
            countsByDwellingType.add(dd.getType());
        }
        for (DwellingType dt : dwellingTypes) {
            SummarizeData.resultFile("CountOfDD," + dt.toString() + "," + countsByDwellingType.count(dt));
        }
        for (DwellingType dt : dwellingTypes) {
            double avePrice = getAveragePriceByDwellingType()[dwellingTypes.indexOf(dt)];
            SummarizeData.resultFile("AveMonthlyPrice," + dt.toString() + "," + avePrice);
        }
        for (DwellingType dt : dwellingTypes) {
            double aveVac = getAverageVacancyByDwellingType()[dwellingTypes.indexOf(dt)];
            Formatter f = new Formatter();
            f.format("AveVacancy,%s,%f", dt.toString(), aveVac);
            SummarizeData.resultFile(f.toString());
        }
        // aggregate developable land
        SummarizeData.resultFile("Available land for construction by region");
        final GeoData geoData = dataContainer.getGeoData();
        final int highestId = geoData.getRegions().keySet()
                .stream().mapToInt(Integer::intValue).max().getAsInt();
        double[] availLand = new double[highestId + 1];
        for (int zone : geoData.getZones().keySet()) {
            availLand[geoData.getZones().get(zone).getRegion().getId()] +=
                    getAvailableCapacityForConstruction(zone);
        }
        for (int region : geoData.getRegions().keySet()) {
            Formatter f = new Formatter();
            f.format("%d,%f", region, availLand[region]);
            SummarizeData.resultFile(f.toString());
        }

        // summarize housing costs by income group
        SummarizeData.resultFile("Housing costs by income group");
        String header = "Income";
        for (int i = 0; i < 10; i++) header = header.concat(",rent_" + ((i + 1) * 250));
        header = header.concat(",averageRent");
        SummarizeData.resultFile(header);
        int[][] rentByIncome = new int[10][10];
        int[] rents = new int[10];
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        for (Household hh : householdData.getHouseholds()) {
            int hhInc = HouseholdUtil.getHhIncome(hh);
            int rent = dwellings.get(hh.getDwellingId()).getPrice();
            int incCat = Math.min((hhInc / 10000), 9);
            int rentCat = Math.min((rent / 250), 9);
            rentByIncome[incCat][rentCat]++;
            rents[incCat] += rent;
        }
        for (int i = 0; i < 10; i++) {
            String line = String.valueOf((i + 1) * 10000);
            int countThisIncome = 0;
            for (int r = 0; r < 10; r++) {
                line = line.concat("," + rentByIncome[i][r]);
                countThisIncome += rentByIncome[i][r];
            }
            if (countThisIncome != 0) { // new dz, avoid dividing by zero
                // TODO check what happens by leaving this out... the error is avoided
                line = line.concat("," + rents[i] / countThisIncome);
            }
            SummarizeData.resultFile(line);
        }
    }


    public static int getNumberOfDDinRegion(int region) {
        return dwellingsByRegion[region];
    }


    public static int[] getListOfVacantDwellingsInRegion(int region) {
        // return array with IDs of vacant dwellings in region

        int[] vacancies = new int[vacDwellingsByRegionPos[region]];
        System.arraycopy(vacDwellingsByRegion[region], 0, vacancies, 0, vacDwellingsByRegionPos[region]);
        return vacancies;
    }


    public static int getNumberOfVacantDDinRegion(int region) {
        return Math.max(vacDwellingsByRegionPos[region] - 1, 0);
    }


    public void removeDwellingFromVacancyList(int ddId) {
        // remove dwelling with ID ddId from list of vacant dwellings

        boolean found = false;

        // todo: when selecting a vacant dwelling, I should be able to store the index of this dwelling in the vacDwellingByRegion array, which should make it faster to remove the vacant dwelling from this array.
        int region = dataContainer.getGeoData().getZones()
                .get(dwellings.get(ddId).getZoneId()).getRegion().getId();
        for (int i = 0; i < vacDwellingsByRegionPos[region]; i++) {
            if (vacDwellingsByRegion[region][i] == ddId) {
                vacDwellingsByRegion[region][i] = vacDwellingsByRegion[region][vacDwellingsByRegionPos[region] - 1];
                vacDwellingsByRegion[region][vacDwellingsByRegionPos[region] - 1] = 0;
                vacDwellingsByRegionPos[region] -= 1;
                if (ddId == SiloUtil.trackDd) SiloUtil.trackWriter.println("Removed dwelling " + ddId +
                        " from list of vacant dwellings.");
                found = true;
                break;
            }
        }
        if (!found)
            logger.warn("Consistency error: Could not find vacant dwelling " + ddId + " in vacDwellingsByRegion.");
    }


    public void addDwellingToVacancyList(Dwelling dd) {
        // add dwelling to vacancy list

        int region = dataContainer.getGeoData().getZones().get(dd.getZoneId()).getRegion().getId();
        vacDwellingsByRegion[region][vacDwellingsByRegionPos[region]] = dd.getId();
        if (vacDwellingsByRegionPos[region] < numberOfStoredVacantDD) vacDwellingsByRegionPos[region]++;
        if (vacDwellingsByRegionPos[region] >= numberOfStoredVacantDD)
            IssueCounter.countExcessOfVacantDwellings(region);
        if (dd.getId() == SiloUtil.trackDd) SiloUtil.trackWriter.println("Added dwelling " + dd.getId() +
                " to list of vacant dwellings.");
    }


    public void calculateRegionWidePriceAndVacancyByDwellingType() {
        // calculate region-wide average dwelling costs and vacancy by dwelling type

        int distinctDdTypes = dwellingTypes.size();
        int[][] vacOcc = SiloUtil.setArrayToValue(new int[2][distinctDdTypes], 0);
        long[] price = SiloUtil.setArrayToValue(new long[distinctDdTypes], 0);

        for (Dwelling dd : dwellings.values()) {
            int dto = dwellingTypes.indexOf(dd.getType());
            price[dto] += dd.getPrice();

            if (dd.getResidentId() > 0) {
                vacOcc[1][dto]++;
            } else {
                vacOcc[0][dto]++;
            }
        }
        aveVac = new double[distinctDdTypes];
        avePrice = new double[distinctDdTypes];

        for (DwellingType dt : dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);

            if (vacOcc[0][dto] + vacOcc[1][dto] > 0) {
                aveVac[dto] = (double) vacOcc[0][dto] / (double) (vacOcc[0][dto] + vacOcc[1][dto]);
                avePrice[dto] = price[dto] / (double) (vacOcc[0][dto] + vacOcc[1][dto]);

            } else {
                aveVac[dto] = 0;
                avePrice[dto] = 0;
            }
        }
    }


    public double[][] getVacancyRateByTypeAndRegion() {
        // calculate vacancy rate by region and dwelling type

        final GeoData geoData = dataContainer.getGeoData();
        final int highestRegionId = geoData.getRegions().keySet().stream().mapToInt(Integer::intValue).max().getAsInt();
        int[][][] vacOcc = SiloUtil.setArrayToValue(new int[2][dwellingTypes.size()][highestRegionId + 1], 0);

        for (Dwelling dd : dwellings.values()) {
            int dto = dwellingTypes.indexOf(dd.getType());
            if (dd.getResidentId() > 0) {
                vacOcc[1][dto][geoData.getZones().get(dd.getZoneId()).getRegion().getId()]++;
            } else {
                vacOcc[0][dto][geoData.getZones().get(dd.getZoneId()).getRegion().getId()]++;
            }
        }

        double[][] vacRate = new double[dwellingTypes.size()][highestRegionId + 1];
        for (DwellingType dt : dwellingTypes) {
            int dto = dwellingTypes.indexOf(dt);
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


    public void setAvePriceByDwellingType(double[] newAvePrice) {
        avePrice = newAvePrice;
    }


    public double[] getAveragePriceByDwellingType() {
        return avePrice;
    }


    public double[] getAverageVacancyByDwellingType() {
        return aveVac;
    }


    public int[][] getDwellingCountByTypeAndRegion() {
        // return number of dwellings by type and region

        final GeoData geoData = dataContainer.getGeoData();
        final int highestRegionId = geoData.getRegions().keySet().stream().mapToInt(Integer::intValue).max().getAsInt();
        int[][] dwellingCount =
                SiloUtil.setArrayToValue(new int[dwellingTypes.size()][highestRegionId + 1], 1);

        for (Dwelling dd : dwellings.values()) {
            dwellingCount[dwellingTypes.indexOf(dd.getType())][geoData.getZones().get(dd.getZoneId()).getRegion().getId()]++;
        }
        return dwellingCount;
    }


    public double getAvailableCapacityForConstruction(int zone) {
        // return available land in developable land-use categories

        double sm;
        Development development = dataContainer.getGeoData().getZones().get(zone).getDevelopment();
        if (development.isUseDwellingCapacity()) {
            sm = development.getDwellingCapacity();
        } else {
            sm = development.getDevelopableArea();
        }
        return sm;
    }

    public void convertLand(int zone, float acres) {
        // remove acres from developable land
        Development development = dataContainer.getGeoData().getZones().get(zone).getDevelopment();
        if (development.isUseDwellingCapacity()) {
            development.changeCapacityBy(-1);
        } else {
            development.changeAreaBy(-acres);
        }
    }

    public void readDevelopmentData() {
        String baseDirectory = Properties.get().main.baseDirectory;

        TableDataSet developmentTable = SiloUtil.readCSVfile(baseDirectory + Properties.get().geo.landUseAndDevelopmentFile);

        int[] zoneIdData = developmentTable.getColumnAsInt("Zone");
        Map<DwellingType, int[]> constraintData = new HashMap<>();
        for (DwellingType dwellingType : dwellingTypes) {
            constraintData.put(dwellingType, developmentTable.getColumnAsInt(dwellingType.toString()));
        }
        int[] dwellingCapacityData = developmentTable.getColumnAsInt("DevCapacity");
        double[] landUseData = developmentTable.getColumnAsDouble("DevLandUse");

        for (int i = 0; i < zoneIdData.length; i++) {

            Map<DwellingType, Boolean> constraints = new HashMap<>();
            for (DwellingType dwellingType : dwellingTypes) {
                constraints.put(dwellingType, constraintData.get(dwellingType)[i] == 1);
            }

            Development development = new DevelopmentImpl(landUseData[i], dwellingCapacityData[i], constraints, Properties.get().geo.useCapacityForDwellings);
            dataContainer.getGeoData().getZones().get(zoneIdData[i]).setDevelopment(development);
        }

    }

}
