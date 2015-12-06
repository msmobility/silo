package edu.umd.ncsg.transportModel;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.data.*;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.ResourceBundle;

/**
 * Controls transportation model
 * Author: Rolf Moeckel, University of Maryland
 * Created on 19 September 2014 in Wheaton, MD
 **/

public class transportModel {

    static Logger logger = Logger.getLogger(transportModel.class);
    //    protected static final String PROPERTIES_MSTM_EXECUTABLE          = "transport.executable";
//    protected static final String PROPERTIES_MSTM_SETTINGS            = "transport.settings";
    protected static final String PROPERTIES_SCHOOL_ENROLLMENT_DATA = "household.distribution";
    protected static final String PROPERTIES_MSTM_SE_DATA_FILE      = "mstm.socio.economic.data.file";
    protected static final String PROPERTIES_MSTM_HH_WRK_DATA_FILE  = "mstm.households.by.workers.file";
    protected static final String PROPERTIES_MSTM_HH_SIZE_DATA_FILE = "mstm.households.by.size.file";
    protected static final String PROPERTIES_MSTM_INCOME_BRACKETS   = "mstm.income.brackets";
//    protected static final String PROPERTIES_MSTM_DIRECTORY           = "transport.directory";
//    protected static final String PROPERTIES_MSTM_SCENARIO            = "transport.scenario";
//    protected static final String PROPERTIES_AUTO_PEAK_SKIM           = "auto.peak.sov.skim.";
//    protected static final String PROPERTIES_TRANSIT_PEAK_SKIM        = "transit.peak.time.";

    private ResourceBundle rb;
//    private String mstmDirectory;
//    private String mstmScenario;

    public transportModel(ResourceBundle rb) {
        // constructor
        this.rb = rb;
    }

    public void runMstm(int year) {
        // run transportation model MSTM in CUBE

//        mstmDirectory = rb.getString(PROPERTIES_MSTM_DIRECTORY);
//        mstmScenario = rb.getString(PROPERTIES_MSTM_SCENARIO + "." + year);
        writeSocioEconomicDataFilesForMstm(year);
//        logger.info("***  Started MSTM for year " + year + ". Waiting for its completion...");
//        try {
//            String exeFile = mstmDirectory + "/" + rb.getString(PROPERTIES_MSTM_EXECUTABLE);
//            int[] mstmSet = ResourceUtil.getIntegerArray(rb, PROPERTIES_MSTM_SETTINGS);
//            String[] runParameters = {exeFile, mstmScenario, String.valueOf(mstmSet[0]), String.valueOf(mstmSet[1]),
//                    String.valueOf(mstmSet[2])};
//            final ProcessBuilder mstm = new ProcessBuilder(runParameters);
//            mstm.directory(new File(mstmDirectory));
//            final Process tm = mstm.start();
//            BufferedReader r = new BufferedReader(new InputStreamReader(tm.getInputStream()));
//            String output;
//            while ((output = r.readLine()) != null) System.out.println(output);
//            tm.waitFor();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        logger.info("***  MSTM finished. Continuing with SILO.");
    }


    public void writeSocioEconomicDataFilesForMstm(int year) {
        // write out file with socio-economic data for MSTM transportation model

        String fileName = (SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/" +
                rb.getString(PROPERTIES_MSTM_SE_DATA_FILE) + "_" + year + ".csv");
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
        TableDataSet enrollment = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SCHOOL_ENROLLMENT_DATA));
        enrollment.buildIndex(enrollment.getColumnPosition(";SMZ_N"));

        // write file for MSTM
        PrintWriter pw = SiloUtil.openFileForSequentialWriting(fileName, false);
        if (pw == null) return;
        pw.println(";SMZ_N,ACRES,HH" + year + ",ENR" + year + ",RE" + year + ",OFF" + year + ",OTH" + year + ",TOT" + year);
        // I am guessing:
        // SMZ_N: zone (int)
        // ACRES: size of zone in acres
        // HH: number of households in zone
        // ENR: school enrollment in zone (unclear is this is origin or destination data)
        // RE: number of retired? people in zone
        // OFF: number of office workers in zone (unclear if this is origin or destination data)
        // "ind": presumably would be industrial, but is not written out
        // OTH: number of other? workers in zone (unclear if origin or destination data)
        // TOT: total number of workers in zone (unclear if origin or destination data)
        // kai, dec'15
        for (int zone : geoData.getZones()) {
            int zoneId = geoData.getZoneIndex(zone);
            int totalEmployment = ind[zoneId] + ret[zoneId] + off[zoneId] + oth[zoneId];
            pw.println(zone + "," + geoData.getSizeOfZoneInAcres(zone) + "," + hhs[zoneId] + "," +
                    enrollment.getIndexedValueAt(zone, "ENR") + "," + ret[zoneId] + "," +
                    off[zoneId] + "," + oth[zoneId] + "," + totalEmployment);
        }
        pw.close();

        String fileNameWrk = (SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/" +
                rb.getString(PROPERTIES_MSTM_HH_WRK_DATA_FILE) + "_" + year + ".csv");
        logger.info("  Summarizing households by number of workers for MSTM to file " + fileNameWrk);
        int[] mstmIncCategories = ResourceUtil.getIntegerArray(rb, PROPERTIES_MSTM_INCOME_BRACKETS);

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
        // I am guessing:
        // SMZ: zone (int)
        // WKR0_IQ1: number of households with zero workers in income quantile 1
        // Etc.
        // kai, dec'15
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
                rb.getString(PROPERTIES_MSTM_HH_SIZE_DATA_FILE) + "_" + year + ".csv");
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
