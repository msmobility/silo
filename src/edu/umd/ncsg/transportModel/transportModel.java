package edu.umd.ncsg.transportModel;

import com.pb.common.datafile.TableDataSet;
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
    protected static final String PROPERTIES_MSTM_EXECUTABLE          = "transport.executable";
    protected static final String PROPERTIES_MSTM_SETTINGS            = "transport.settings";
    protected static final String PROPERTIES_SCHOOL_ENROLLMENT_DATA   = "household.distribution";
    protected static final String PROPERTIES_MSTM_SE_DATA_FILE        = "mstm.socio.economic.data.file";
    protected static final String PROPERTIES_MSTM_DIRECTORY           = "transport.directory";
    protected static final String PROPERTIES_MSTM_SCENARIO            = "transport.scenario";
    protected static final String PROPERTIES_AUTO_PEAK_SKIM           = "auto.peak.sov.skim.";
    protected static final String PROPERTIES_TRANSIT_PEAK_SKIM        = "transit.peak.time.";

    private ResourceBundle rb;
    private String mstmDirectory;
    private String mstmScenario;

    public transportModel(ResourceBundle rb) {
        // constructor
        this.rb = rb;
    }

    public void runMstm (int year) {
        // run transportation model MSTM in CUBE

        mstmDirectory = rb.getString(PROPERTIES_MSTM_DIRECTORY);
        mstmScenario = rb.getString(PROPERTIES_MSTM_SCENARIO + "." + year);
        writeSocioEconomicDataFileForMstm(year);
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


    public void writeSocioEconomicDataFileForMstm(int year) {
        // write out file with socio-economic data for MSTM transportation model

        String fileName = (mstmDirectory + "/" + mstmScenario + "/" + rb.getString(PROPERTIES_MSTM_SE_DATA_FILE));
        logger.info("  Summarizing socio-economic data for MSTM to file " + fileName);
        // summarize micro data
        int[] hhs = new int[geoData.getZones().length];
        int[] ind = new int[geoData.getZones().length];
        int[] ret = new int[geoData.getZones().length];
        int[] off = new int[geoData.getZones().length];
        int[] oth = new int[geoData.getZones().length];

        for (Household hh: Household.getHouseholdArray()) hhs[geoData.getZoneIndex(hh.getHomeZone())]++;
        String[] jobTypes = JobType.getJobTypes();
        for (Job jj: Job.getJobArray()) {
            if (jj.getType().equalsIgnoreCase(jobTypes[0])) ret[geoData.getZoneIndex(jj.getZone())]++;
            else if (jj.getType().equalsIgnoreCase(jobTypes[1])) off[geoData.getZoneIndex(jj.getZone())]++;
            else if (jj.getType().equalsIgnoreCase(jobTypes[2])) ind[geoData.getZoneIndex(jj.getZone())]++;
            else if (jj.getType().equalsIgnoreCase(jobTypes[3])) oth[geoData.getZoneIndex(jj.getZone())]++;
        }
        TableDataSet enrollment = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SCHOOL_ENROLLMENT_DATA));
        enrollment.buildIndex(enrollment.getColumnPosition(";SMZ_N"));

        // write file for MSTM
        PrintWriter pw = SiloUtil.openFileForSequentialWriting(fileName, false);
        pw.println(";SMZ_N,ACRES,HH" + year + ",ENR,RE" + year + ",OFF" + year + ",OTH" + year + ",TOT" + year);
        for (int zone: geoData.getZones()) {
            int zoneId = geoData.getZoneIndex(zone);
            int totalEmployment = ind[zoneId] + ret[zoneId] + off[zoneId] + oth[zoneId];
            pw.println(zone + "," + geoData.getSizeOfZoneInAcres(zone) + "," + hhs[zoneId] + "," +
                    enrollment.getIndexedValueAt(zone, "ENR") + "," + ret[zoneId] + "," +
                    off[zoneId] + "," + oth[zoneId] + "," + totalEmployment);
        }
        pw.close();
    }
}
