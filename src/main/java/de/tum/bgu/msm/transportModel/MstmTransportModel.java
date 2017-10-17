package de.tum.bgu.msm.transportModel;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.data.MitoHousehold;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.SiloMuc;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.transportModel.tripGeneration.TripGenerationData;
import de.tum.bgu.msm.transportModel.tripGeneration.tripPurposes;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static de.tum.bgu.msm.SiloModel.PROPERTIES_FILE_DEMAND_MODEL;

/**
 * Controls transportation model
 * Author: Rolf Moeckel, Technical University of Munich
 * Created on 19 September 2014 in Wheaton, MD
 * Revised on 6 May 2016 in Munich, Germany
 **/

public class MstmTransportModel implements TransportModelI {

    private static Logger logger = Logger.getLogger(MstmTransportModel.class);
    private static final String PROPERTIES_RUN_SILO               = "run.silo.model";
    private static final String PROPERTIES_RUN_SP                 = "run.synth.pop.generator";
    private static final String PROPERTIES_TRANSPORT_MODEL_YEARS  = "transport.model.years";
    private static final String PROPERTIES_SCHOOL_ENROLLMENT_DATA = "household.distribution";
    private static final String PROPERTIES_MSTM_SE_DATA_FILE      = "mstm.socio.economic.data.file";
    private static final String PROPERTIES_MSTM_HH_WRK_DATA_FILE  = "mstm.households.by.workers.file";
    private static final String PROPERTIES_MSTM_HH_SIZE_DATA_FILE = "mstm.households.by.size.file";
    private static final String PROPERTIES_MSTM_INCOME_BRACKETS   = "mstm.income.brackets";

    private ResourceBundle rbLandUse;
    private ResourceBundle rbTravel;
    private GeoData geoData;
    private TripGenerationData tgData;


    public MstmTransportModel(ResourceBundle rbLandUse, GeoData geoData) {
        // constructor
        this.rbLandUse = rbLandUse;
        this.geoData = geoData;
        String fileName = ResourceUtil.getProperty(rbLandUse, PROPERTIES_FILE_DEMAND_MODEL);
        rbTravel = ResourceUtil.getPropertyBundle(new File(fileName));

    }



    @Override
    public void runTransportModel (int year) {
        // run travel demand model

        // if transport model is run by itself, year is not specified by SILO; then, needs to read the first year from properties file
        if (year == -1) {
            year = ResourceUtil.getIntegerArray(rbLandUse, PROPERTIES_TRANSPORT_MODEL_YEARS)[0];
        }
        logger.info("Running travel demand model for the year " + year);
        tripGeneration();
        logger.info("Completed travel demand model for the year " + year);

        if (ResourceUtil.getBooleanProperty(rbLandUse, SiloModel.PROPERTIES_CREATE_MSTM_OUTPUT_FILES, true))
		this.writeOutSocioEconomicDataForMstm(year + 1);
    }


    
    private void tripGeneration () {
        // run trip generation

        tgData = new TripGenerationData(rbTravel);
        tgData.readHouseholdTravelSurvey("all");

        TravelDemandData tdd = new TravelDemandData(rbTravel);
        tdd.readData();
        if (!ResourceUtil.getBooleanProperty(rbLandUse, SiloMuc.PROPERTIES_RUN_SILO) &&
                !ResourceUtil.getBooleanProperty(rbLandUse, SiloMuc.PROPERTIES_RUN_SYNTHETIC_POPULATION)) {
            RealEstateDataManager realEstateData = new RealEstateDataManager(rbLandUse, geoData);
            HouseholdDataManager householdData = new HouseholdDataManager(rbLandUse, realEstateData);
            householdData.readPopulation(false, 0);
            householdData.connectPersonsToHouseholds();
            householdData.setTypeOfAllHouseholds();
        }

        logger.info("  Running trip generation");
        // generate trips for every household

        for (int purp = 0; purp < tripPurposes.values().length; purp++) {
                String strPurp = tripPurposes.values()[purp].toString();
                TableDataSet hhTypeDef = createHHTypeDefinition(strPurp);
                int[] hhTypeArray = tgData.defineHouseholdTypeOfEachSurveyRecords(selectAutoMode(strPurp), hhTypeDef);
                HashMap<String, Integer[]> tripsByHhTypeAndPurpose = tgData.collectTripFrequencyDistribution(hhTypeArray);
                // Generate trips for each household
                for (Household hh: Household.getHouseholdArray()) {
                    int region = tdd.getRegionOfZone(hh.getHomeZone());
                    int incCategory = tdd.translateIncomeIntoCategory (hh.getHhIncome());
                    int hhType = tgData.getHhType(selectAutoMode(strPurp), hhTypeDef, hh.getHhSize(), hh.getNumberOfWorkers(),
                            incCategory, hh.getAutos(), region);
                    String token = hhType + "_" + strPurp;
                    Integer[] tripFrequencies = tripsByHhTypeAndPurpose.get(token);
                    if (tripFrequencies == null) {
                        logger.error("Could not find trip frequencies for this hhType/Purpose: " + token);
                    }
//                    if (SiloUtil.getSum(tripFrequencies) == 0) continue;
//                    int numTrips = selectNumberOfTrips(tripFrequencies);
//                    int mstmIncCat = HouseholdDataManager.getIncomeCategoryForIncome(hh.getHhIncome());
//                    tripProd[hh.getHomeZone()][purp][mstmIncCat] += numTrips;
//                }
//            }
//            logger.info("  Generated " + SiloUtil.customFormat("###,###", SiloUtil.getSum(tripProd)) + " raw trips.");
        }
        }
    }


    private TableDataSet createHHTypeDefinition (String purpose) {
        // create household type definition file
        String[] hhDefToken = ResourceUtil.getArray(rbTravel, ("hh.type." + purpose));
        //        int categoryID = Integer.parseInt(hhDefToken[0]);
        int numCategories = Integer.parseInt(hhDefToken[1]);
        String sizeToken = hhDefToken[2];
        String[] sizePortions = sizeToken.split("\\.");
        String workerToken = hhDefToken[3];
        String[] workerPortions = workerToken.split("\\.");
        String incomeToken = hhDefToken[4];
        String[] incomePortions = incomeToken.split("\\.");
        String autoToken = hhDefToken[5];
        String[] autoPortions = autoToken.split("\\.");
        String regionToken = hhDefToken[6];
        String[] regionPortions = regionToken.split("\\.");
        TableDataSet hhTypeDef = tgData.createHouseholdTypeTableDataSet(numCategories, sizePortions, workerPortions,
                incomePortions, autoPortions, regionPortions);
        int[] hhCounter = tgData.defineHouseholdTypeOfEachSurveyRecords(selectAutoMode(purpose), hhTypeDef);
        HashMap<Integer, Integer> numHhByType = new HashMap<>();
        for (int hhType: hhCounter) {
            if (numHhByType.containsKey(hhType)) {
                int oldNum = numHhByType.get(hhType);
                numHhByType.put(hhType, (oldNum + 1));
            } else {
                numHhByType.put(hhType, 1);
            }
        }
        hhTypeDef.appendColumn(new float[hhTypeDef.getRowCount()], "counter");
        hhTypeDef.buildIndex(hhTypeDef.getColumnPosition("hhType"));
        for (int type: numHhByType.keySet()) {
            if (type == 0) continue;
            hhTypeDef.setIndexedValueAt(type, "counter", numHhByType.get(type));
        }
//        mstmUtilities.writeTable(hhTypeDef, "temp_" + purpose + ".csv");
        return hhTypeDef;
    }


    private String selectAutoMode (String purpose) {
        // return autos or autoSufficiency depending on mode chosen

        String autoMode = "autos";
        if (purpose.equalsIgnoreCase("HBW") || purpose.equalsIgnoreCase("NHBW")) autoMode = "autoSufficiency";
        return autoMode;
    }


    private int selectNumberOfTrips (Integer[] tripFrequencies) {
        // select number of trips
        double[] probabilities = new double[tripFrequencies.length];
        for (int i = 0; i < tripFrequencies.length; i++) probabilities[i] = (double) tripFrequencies[i];
        return SiloUtil.select(probabilities);
    }


    private void writeOutSocioEconomicDataForMstm(int year) {
        // write out file with socio-economic data for MSTM transportation model

        String fileName = (SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/" +
                rbLandUse.getString(PROPERTIES_MSTM_SE_DATA_FILE) + "_" + year + ".csv");
        logger.info("  Summarizing socio-economic data for MSTM to file " + fileName);
        // summarize micro data
        int[] hhs = new int[geoData.getZones().length];
        int[] ind = new int[geoData.getZones().length];
        int[] ret = new int[geoData.getZones().length];
        int[] off = new int[geoData.getZones().length];
        int[] oth = new int[geoData.getZones().length];

        for (Household hh : Household.getHouseholdArray()) hhs[geoData.getZoneIndex(hh.getHomeZone())]++;
        String[] jobTypes = JobType.getJobTypes();
        for (Job jj : Job.getJobArray()) {
            if (jj.getType().equalsIgnoreCase(jobTypes[0])) ret[geoData.getZoneIndex(jj.getZone())]++;
            else if (jj.getType().equalsIgnoreCase(jobTypes[1])) off[geoData.getZoneIndex(jj.getZone())]++;
            else if (jj.getType().equalsIgnoreCase(jobTypes[2])) ind[geoData.getZoneIndex(jj.getZone())]++;
            else if (jj.getType().equalsIgnoreCase(jobTypes[3])) oth[geoData.getZoneIndex(jj.getZone())]++;
        }
        TableDataSet enrollment = SiloUtil.readCSVfile(rbLandUse.getString(PROPERTIES_SCHOOL_ENROLLMENT_DATA));
        enrollment.buildIndex(enrollment.getColumnPosition(";SMZ_N"));

        // write file for MSTM
        PrintWriter pw = SiloUtil.openFileForSequentialWriting(fileName, false);
        if (pw == null) return;
        pw.println(";SMZ_N,ACRES,HH" + year + ",ENR" + year + ",RE" + year + ",OFF" + year + ",OTH" + year + ",TOT" + year);
        // SMZ_N: zone (int)
        // ACRES: size of zone in acres
        // HH: number of households in zone
        // ENR: school enrollment in zone (at school location)
        // RE: Retail employment in zone
        // OFF: Office employment in zone
        // OTH: Other employment in zone
        // TOT: Total employment in zone
        for (int zone : geoData.getZones()) {
            int zoneId = geoData.getZoneIndex(zone);
            int totalEmployment = ind[zoneId] + ret[zoneId] + off[zoneId] + oth[zoneId];
            pw.println(zone + "," + geoData.getSizeOfZoneInAcres(zone) + "," + hhs[zoneId] + "," +
                    enrollment.getIndexedValueAt(zone, "ENR") + "," + ret[zoneId] + "," +
                    off[zoneId] + "," + oth[zoneId] + "," + totalEmployment);
        }
        pw.close();

        String fileNameWrk = (SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/" +
                rbLandUse.getString(PROPERTIES_MSTM_HH_WRK_DATA_FILE) + "_" + year + ".csv");
        logger.info("  Summarizing households by number of workers for MSTM to file " + fileNameWrk);
        int[] mstmIncCategories = ResourceUtil.getIntegerArray(rbLandUse, PROPERTIES_MSTM_INCOME_BRACKETS);

        PrintWriter pwWrk = SiloUtil.openFileForSequentialWriting(fileNameWrk, false);
        if (pwWrk == null) return;
        int[][][] hhByWorkersAndInc = new int[geoData.getZones().length][4][5];
        for (Household hh : Household.getHouseholdArray()) {
            int inc = HouseholdDataManager.getSpecifiedIncomeCategoryForIncome(mstmIncCategories, hh.getHhIncome());
            int wrk = Math.min(HouseholdDataManager.getNumberOfWorkersInHousehold(hh), 3);
            int zone = hh.getHomeZone();
            hhByWorkersAndInc[geoData.getZoneIndex(zone)][wrk][inc - 1]++;
        }
        pwWrk.println("SMZ,WKR0_IQ1,WKR0_IQ2,WKR0_IQ3,WKR0_IQ4,WKR0_IQ5,WKR1_IQ1,WKR1_IQ2,WKR1_IQ3,WKR1_IQ4,WKR1_IQ5," +
                "WKR2_IQ1,WKR2_IQ2,WKR2_IQ3,WKR2_IQ4,WKR2_IQ5,WKR3_IQ1,WKR3_IQ2,WKR3_IQ3,WKR3_IQ4,WKR3_IQ5,Total");
        // SMZ: zone (int)
        // WKR0_IQ1: number of households with zero workers in income group 1 in zone
        // WKR0_IQ2: number of households with zero workers in income group 2 in zone
        // Etc.
        // Total: Total number of households in zone
        for (int zone : geoData.getZones()) {
            pwWrk.print(zone);
            int total = 0;
            for (int wrk = 0; wrk <= 3; wrk++) {
                for (int inc = 1; inc <= 5; inc++) {
                    pwWrk.print("," + hhByWorkersAndInc[geoData.getZoneIndex(zone)][wrk][inc-1]);
                    total += hhByWorkersAndInc[geoData.getZoneIndex(zone)][wrk][inc-1];
                }
            }
            pwWrk.println("," + total);
        }
        pwWrk.close();

        String fileNameSize = (SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/" +
                rbLandUse.getString(PROPERTIES_MSTM_HH_SIZE_DATA_FILE) + "_" + year + ".csv");
        logger.info("  Summarizing households by size for MSTM to file " + fileNameSize);

        PrintWriter pwSize = SiloUtil.openFileForSequentialWriting(fileNameSize, false);
        if (pwSize == null) return;
        int[][][] hhBySizeAndInc = new int[geoData.getZones().length][5][5];
        for (Household hh : Household.getHouseholdArray()) {
            int inc = HouseholdDataManager.getSpecifiedIncomeCategoryForIncome(mstmIncCategories, hh.getHhIncome());
            int size = Math.min(hh.getHhSize(), 5);
            int zone = hh.getHomeZone();
            hhBySizeAndInc[geoData.getZoneIndex(zone)][size - 1][inc - 1]++;
        }
        pwSize.println("SMZ,SIZ1_IQ1,SIZ1_IQ2,SIZ1_IQ3,SIZ1_IQ4,SIZ1_IQ5,SIZ2_IQ1,SIZ2_IQ2,SIZ2_IQ3,SIZ2_IQ4,SIZ2_IQ5," +
                "SIZ3_IQ1,SIZ3_IQ2,SIZ3_IQ3,SIZ3_IQ4,SIZ3_IQ5,SIZ4_IQ1,SIZ4_IQ2,SIZ4_IQ3,SIZ4_IQ4,SIZ4_IQ5,SIZ5_IQ1," +
                "SIZ5_IQ2,SIZ5_IQ3,SIZ5_IQ4,SIZ5_IQ5,Total");
        // SMZ: zone (int)
        // SIZ1_IQ1: number of households with household size 1 in income group 1 in zone
        // SIZ1_IQ2: number of households with household size 1 in income group 2 in zone
        // Etc.
        // Total: Total number of households in zone
        for (int zone : geoData.getZones()) {
            pwSize.print(zone);
            int total = 0;
            for (int size = 1; size <= 5; size++) {
                for (int inc = 1; inc <= 5; inc++) {
                    pwSize.print("," + hhBySizeAndInc[geoData.getZoneIndex(zone)][size - 1][inc - 1]);
                    total += hhBySizeAndInc[geoData.getZoneIndex(zone)][size - 1][inc - 1];
                }
            }
            pwSize.println("," + total);
        }
        pwSize.close();

    }

}
