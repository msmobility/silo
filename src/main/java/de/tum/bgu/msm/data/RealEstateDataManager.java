package de.tum.bgu.msm.data;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.*;
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
    public static int rentCategories;
    private static HashMap<Integer, float[]> ddPriceByHhType;
    private static int[] dwellingsByRegion;
    private static int[][] vacDwellingsByRegion;
    private static int[] vacDwellingsByRegionPos;
    private static int numberOfStoredVacantDD;
    private double[] avePrice;
    private double[] aveVac;
    private static float[] medianRent;
    private HashMap<DwellingType, Float> acresByDwellingType;

    public RealEstateDataManager(SiloDataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    public void saveDwellings (Dwelling[] dds) {
        for (Dwelling dd: dds) {
            dwellings.put(dd.getId(), dd);
        }
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

    public void readDwellings (Properties properties) {
        boolean readBin = Properties.get().realEstate.readBinaryDwellingFile;
        if (readBin) {
            readBinaryDwellingDataObjects();
        } else {
            readDwellingData(properties);
        }
        readAcresNeededByDwellingType();
    }

    public Dwelling createDwelling(int id, int zone, int hhId, DwellingType type, int bedrooms, int quality, int price, float restriction,
                                   int year) {
        Dwelling dwelling = new Dwelling(id, zone, hhId, type, bedrooms, quality, price, restriction, year);
        this.dwellings.put(id, dwelling);
        return dwelling;
    }


    private void readDwellingData(Properties properties) {
        // read dwelling micro data from ascii file

        logger.info("Reading dwelling micro data from ascii file");
        int year = Properties.get().main.startYear;
        String fileName = properties.main.baseDirectory + properties.realEstate.dwellingsFile;
        fileName += "_" + year + ".csv";

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId      = SiloUtil.findPositionInArray("id", header);
            int posZone    = SiloUtil.findPositionInArray("zone",header);
            int posHh      = SiloUtil.findPositionInArray("hhId",header);
            int posType    = SiloUtil.findPositionInArray("type",header);
            int posRooms   = SiloUtil.findPositionInArray("bedrooms",header);
            int posQuality = SiloUtil.findPositionInArray("quality",header);
            int posCosts   = SiloUtil.findPositionInArray("monthlyCost",header);
            int posRestr   = SiloUtil.findPositionInArray("restriction",header);
            int posYear    = SiloUtil.findPositionInArray("yearBuilt",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id        = Integer.parseInt(lineElements[posId]);
                int zoneId      = Integer.parseInt(lineElements[posZone]);
                int hhId      = Integer.parseInt(lineElements[posHh]);
                String tp     = lineElements[posType].replace("\"", "");
                DwellingType type = DwellingType.valueOf(tp);
                int price     = Integer.parseInt(lineElements[posCosts]);
                int area      = Integer.parseInt(lineElements[posRooms]);
                int quality   = Integer.parseInt(lineElements[posQuality]);
                float restrict  = Float.parseFloat(lineElements[posRestr]);
                int yearBuilt = Integer.parseInt(lineElements[posYear]);
                createDwelling(id, zoneId, hhId, type, area, quality, price, restrict, yearBuilt);   // this automatically puts it in id->dwelling map in Dwelling class
                if (id == SiloUtil.trackDd) {
                    SiloUtil.trackWriter.println("Read dwelling with following attributes from " + fileName);
                    SiloUtil.trackWriter.println(dwellings.get(id).toString());
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop dwelling file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " dwellings.");
    }


    private void readAcresNeededByDwellingType () {
        // read in the area needed to build a dwelling

        String fileNameAcres = Properties.get().main.baseDirectory + Properties.get().realEstate.dwellingTypeAcresFile;
        TableDataSet tblAcresByDwellingType =  SiloUtil.readCSVfile(fileNameAcres);
        acresByDwellingType = new HashMap<>();
        for (int row = 1; row <= tblAcresByDwellingType.getRowCount(); row++) {
            String type = tblAcresByDwellingType.getStringValueAt(row, "DwellingType");
            float acres = tblAcresByDwellingType.getValueAt(row, "acres");
            boolean notFound = true;
            for (DwellingType dt: DwellingType.values()) {
                if (dt.toString().equals(type)) {
                    acresByDwellingType.put(dt, acres);
                    notFound = false;
                }
            }
            if (notFound) logger.error("Could not reference type " + type + " of " + fileNameAcres + " with DwellingType.");
        }

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
                int region = geoData.getZones().get(dd.getZone()).getRegion().getId();
                dwellingsByRegion[region]++;
                vacDwellingsByRegion[region][vacDwellingsByRegionPos[region]] = dwellingId;
                if (vacDwellingsByRegionPos[region] < numberOfStoredVacantDD) vacDwellingsByRegionPos[region]++;
                if (vacDwellingsByRegionPos[region] >= numberOfStoredVacantDD) IssueCounter.countExcessOfVacantDwellings(region);
                if (dwellingId == SiloUtil.trackDd)
                    SiloUtil.trackWriter.println("Added dwelling " + dwellingId + " to list of vacant dwelling.");
            }
        }
    }


    public void writeBinaryDwellingDataObjects() {

        String fileName = Properties.get().main.baseDirectory + Properties.get().householdData.binaryDwellingsFile;
        logger.info("  Writing dwelling data to binary file.");
        Object[] data = dwellings.values().toArray(new Dwelling[0]);
        try {
            File fl = new File(fileName);
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fl));
            out.writeObject(data);
            out.close();
        } catch (Exception e) {
            logger.error ("Error saving to binary file " + fileName + ". Object not saved.\n" + e);
        }
    }


    private void readBinaryDwellingDataObjects() {

        String fileName = Properties.get().main.baseDirectory + Properties.get().realEstate.binaryDwellingsFile;
        logger.info("  Reading dwelling data from binary file.");
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(fileName)));
            Object data = in.readObject();
            saveDwellings((Dwelling[]) data);
        } catch (Exception e) {
            logger.error ("Error reading from binary file " + fileName + ". Object not read.\n" + e);
        }
        logger.info("  Finished reading " + dwellings.size() + " dwellings.");
    }


    public float getAcresNeededForOneDwelling(DwellingType dt) {
        return acresByDwellingType.get(dt);
    }


    public void fillQualityDistribution () {
        // count number of dwellings by quality and calculate average quality
        int numberOfQualityLevels = Properties.get().main.qualityLevels;
        dwellingsByQuality = new int[numberOfQualityLevels];
        initialQualityShares = new double[numberOfQualityLevels];
        for (Dwelling dd: getDwellings()) {
            dwellingsByQuality[dd.getQuality() - 1]++;
        }
        for (int qual = 1; qual <= numberOfQualityLevels; qual++) {
            initialQualityShares[qual - 1] = (double) dwellingsByQuality[qual - 1] /
                    (double) SiloUtil.getSum(dwellingsByQuality);
        }
    }


    public void setHighestVariables () {
        // identify highest dwelling ID in use and largest bedrooms, also calculate share of rent paid by each hh type
        // only done initially when model starts

        highestDwellingIdInUse = 0;
        largestNoBedrooms = 0;

        // identify how much rent (specified by 25 rent categories) is paid by households of each income category
        rentCategories = 25;
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        int[] incBrackets = Properties.get().main.incomeBrackets;
        float[][] priceByIncome = new float[incBrackets.length + 1][rentCategories + 1];
        for (Dwelling dd: dwellings.values()) {
            highestDwellingIdInUse = Math.max(highestDwellingIdInUse, dd.getId());
            largestNoBedrooms = Math.max(largestNoBedrooms, dd.getBedrooms());
            int hhId = dd.getResidentId();
            if (hhId > 0) {
                int hhinc = householdData.getHouseholdFromId(hhId).getHhIncome();
                int incomeCategory = HouseholdDataManager.getIncomeCategoryForIncome(hhinc);
                int rentCategory = (int) ((dd.getPrice() * 1.) / 200.);  // rent category defined as <rent/200>
                rentCategory = Math.min(rentCategory, rentCategories);   // ensure that rent categories do not exceed max
                priceByIncome[incomeCategory - 1][rentCategory]++;
            }
        }
        priceByIncome[incBrackets.length][rentCategories]++;  // make sure that most expensive category can be afforded by richest households
        ddPriceByHhType = new HashMap<>();
        for (int incomeCategory = 1; incomeCategory <= incBrackets.length + 1; incomeCategory++) {
            float[] vector = new float[rentCategories + 1];
            System.arraycopy(priceByIncome[incomeCategory - 1], 0, vector, 0, vector.length);
            float sum = SiloUtil.getSum(vector);
            for (int i = 0; i < vector.length; i++) vector[i] = vector[i] / sum;
            ddPriceByHhType.put(incomeCategory, vector);
        }
    }


    public static float[] getRentPaymentsForIncomeGroup (int incomeCategory) {
        return ddPriceByHhType.get(incomeCategory);
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
        for (int qual = 1; qual <= Properties.get().main.qualityLevels; qual++) currentQualityShares[qual - 1] =
                (double) dwellingsByQuality[qual - 1] / (double) SiloUtil.getSum(dwellingsByQuality);
        return currentQualityShares;
    }


    public void calculateMedianRentByMSA() {

        final GeoData geoData = dataContainer.getGeoData();
        Map<Integer, ArrayList<Integer>> rentHashMap = new HashMap<>();
        for (Dwelling dd: dwellings.values()) {
            int dwellingMSA = geoData.getZones().get(dd.getZone()).getMsa();
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
        for (Integer thisMsa: rentHashMap.keySet()) {
            medianRent[thisMsa] = SiloUtil.getMedian(rentHashMap.get(thisMsa).stream().mapToInt(Integer::intValue).toArray());
        }
    }


    public static float getMedianRent (int msa) {
        return medianRent[msa];
    }


    public void summarizeDwellings () {
        // aggregate dwellings

        SummarizeData.resultFile("QualityLevel,Dwellings");
        for (int qual = 1; qual <= Properties.get().main.qualityLevels; qual++) {
            String row = qual + "," + dwellingsByQuality[qual - 1];
            SummarizeData.resultFile(row);
        }
        int[] ddByType = new int[DwellingType.values().length];
        for (Dwelling dd: dwellings.values()) ddByType[dd.getType().ordinal()]++;
        for (DwellingType dt: DwellingType.values()) {
            SummarizeData.resultFile("CountOfDD,"+dt.toString()+","+ddByType[dt.ordinal()]);
        }
        for (DwellingType dt: DwellingType.values()) {
            double avePrice = getAveragePriceByDwellingType()[dt.ordinal()];
            SummarizeData.resultFile("AveMonthlyPrice,"+dt.toString()+","+avePrice);
        }
        for (DwellingType dt: DwellingType.values()) {
            double aveVac = getAverageVacancyByDwellingType()[dt.ordinal()];
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
        for (int zone: geoData.getZones().keySet()) {
            availLand[geoData.getZones().get(zone).getRegion().getId()] +=
                    getAvailableLandForConstruction(zone);
        }
        for (int region: geoData.getRegions().keySet()) {
            Formatter f = new Formatter();
            f.format("%d,%f", region, availLand[region]);
            SummarizeData.resultFile(f.toString());
        }

        // summarize housing costs by income group
        SummarizeData.resultFile("Housing costs by income group");
        String header = "Income";
        for (int i = 0; i < 10; i++) header = header.concat(",rent_" + ((i+1) * 250));
        header = header.concat(",averageRent");
        SummarizeData.resultFile(header);
        int[][] rentByIncome = new int[10][10];
        int[] rents = new int[10];
        for (Household hh: dataContainer.getHouseholdData().getHouseholds()) {
            int hhInc = hh.getHhIncome();
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


    public static int getNumberOfDDinRegion (int region) {
        return dwellingsByRegion[region];
    }


    public static int[] getListOfVacantDwellingsInRegion (int region) {
        // return array with IDs of vacant dwellings in region

        int[] vacancies = new int[vacDwellingsByRegionPos[region]];
        System.arraycopy(vacDwellingsByRegion[region], 0, vacancies, 0, vacDwellingsByRegionPos[region]);
        return vacancies;
    }


    public static int getNumberOfVacantDDinRegion (int region) {
        return Math.max(vacDwellingsByRegionPos[region] - 1, 0);
    }


    public void removeDwellingFromVacancyList (int ddId) {
        // remove dwelling with ID ddId from list of vacant dwellings

        boolean found = false;

        // todo: when selecting a vacant dwelling, I should be able to store the index of this dwelling in the vacDwellingByRegion array, which should make it faster to remove the vacant dwelling from this array.
        int region = dataContainer.getGeoData().getZones()
                .get(dwellings.get(ddId).getZone()).getRegion().getId();
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
        if (!found) logger.warn("Consistency error: Could not find vacant dwelling " + ddId + " in vacDwellingsByRegion.");
    }


    public void addDwellingToVacancyList (Dwelling dd) {
        // add dwelling to vacancy list

        int region = dataContainer.getGeoData().getZones().get(dd.getZone()).getRegion().getId();
        vacDwellingsByRegion[region][vacDwellingsByRegionPos[region]] = dd.getId();
        if (vacDwellingsByRegionPos[region] < numberOfStoredVacantDD) vacDwellingsByRegionPos[region]++;
        if (vacDwellingsByRegionPos[region] >= numberOfStoredVacantDD) IssueCounter.countExcessOfVacantDwellings(region);
        if (dd.getId() == SiloUtil.trackDd) SiloUtil.trackWriter.println("Added dwelling " + dd.getId() +
                " to list of vacant dwellings.");
    }


    public void calculateRegionWidePriceAndVacancyByDwellingType() {
        // calculate region-wide average dwelling costs and vacancy by dwelling type

        int[][] vacOcc = SiloUtil.setArrayToValue(new int[2][DwellingType.values().length], 0);
        long[] price = SiloUtil.setArrayToValue(new long[DwellingType.values().length], 0);

        for (Dwelling dd: dwellings.values()) {
            int dto = dd.getType().ordinal();
            price[dto] += dd.getPrice();

            if (dd.getResidentId() > 0) {
                vacOcc[1][dto] ++;
            } else {
                vacOcc[0][dto]++;
            }
        }
        aveVac = new double[DwellingType.values().length];
        avePrice = new double[DwellingType.values().length];

        for (DwellingType dt: DwellingType.values()) {
            int dto = dt.ordinal();

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
        int[][][] vacOcc = SiloUtil.setArrayToValue(new int[2][DwellingType.values().length][highestRegionId + 1], 0);

        for (Dwelling dd: dwellings.values()) {
            int dto = dd.getType().ordinal();
            if (dd.getResidentId() > 0) {
                vacOcc[1][dto][geoData.getZones().get(dd.getZone()).getRegion().getId()]++;
            } else {
                vacOcc[0][dto][geoData.getZones().get(dd.getZone()).getRegion().getId()]++;
            }
        }

        double[][] vacRate = new double[DwellingType.values().length][highestRegionId + 1];
        for (DwellingType dt: DwellingType.values()) {
            int dto = dt.ordinal();
            for (int region: geoData.getRegions().keySet()) {
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


    public double[] getAverageVacancyByDwellingType () {
        return aveVac;
    }


    public int[][] getDwellingCountByTypeAndRegion() {
        // return number of dwellings by type and region

        final GeoData geoData = dataContainer.getGeoData();
        final int highestRegionId = geoData.getRegions().keySet().stream().mapToInt(Integer::intValue).max().getAsInt();
        int[][] dwellingCount =
                SiloUtil.setArrayToValue(new int[DwellingType.values().length][highestRegionId + 1], 1);

        for (Dwelling dd: dwellings.values()) {
            dwellingCount[dd.getType().ordinal()][geoData.getZones().get(dd.getZone()).getRegion().getId()] ++;
        }
        return dwellingCount;
    }


    public double getAvailableLandForConstruction (int zone) {
        // return available land in developable land-use categories

        double sm;
        if (useDwellingCapacityForThisZone(zone)) {         // use absolute number of dwellings as capacity constraint
            sm = SiloUtil.rounder(dataContainer.getGeoData().getDevelopmentCapacity(zone),0);  // some capacity values are not integer numbers, not sure why
        } else {
            sm = getDevelopableLand(zone);                            // use land use data
        }
        return sm;
    }


    public boolean useDwellingCapacityForThisZone (int zone) {
        // return true if capacity for number of dwellings is used in this zone, otherwise return false
        final GeoData geoData = dataContainer.getGeoData();
        if (!geoData.useNumberOfDwellingsAsCapacity()) {
            return false;
        }
        try {
            geoData.getDevelopmentCapacity(zone);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public double getDevelopableLand(int zone) {
        // return number of acres of available land for development
        final GeoData geoData = dataContainer.getGeoData();
        double sm = 0;
        for (int type : geoData.getDevelopableLandUseTypes()) {
            String landUseType = "LU" + type;
            sm += geoData.getAreaOfLandUse(landUseType, zone);
        }
        return sm;
    }


    public void convertLand(int zone, float acres) {
        // remove acres from developable land
        final GeoData geoData = dataContainer.getGeoData();
        if (useDwellingCapacityForThisZone(zone)) {
            geoData.reduceDevelopmentCapacityByOneDwelling(zone);
        } else {
            geoData.reduceDevelopmentCapacityByDevelopableAcres(zone, acres);
        }
    }
}
