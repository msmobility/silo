package de.tum.bgu.msm.processMD_GrowthModel;

import com.pb.common.datafile.DBFFileReader;
import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.util.ResourceBundle;

/**
 * Reads dbf files with available land for construction and summarized data by TAZ
 * Author: Rolf Moeckel, University of Maryland
 * Data: 5 August 2014 (College Park, MD)
 */

public class processMD_GrowthModel {

    static Logger logger = Logger.getLogger(processMD_GrowthModel.class);
    private ResourceBundle rb;
    private String baseDirectory;
    private int[] zones;
    private float devCap[];


    public static void main (String[] arguments) {
        // Main run method
        processMD_GrowthModel pm = new processMD_GrowthModel(arguments);
        pm.processData();
    }


    public processMD_GrowthModel(String[] args) {
        // constructor
        String rbName = args[0];
        File propFile = new File(rbName);
        rb = ResourceUtil.getPropertyBundle(propFile);
        baseDirectory = ResourceUtil.getProperty(rb, "base.directory");
    }


    public void processData () {
        //
        logger.info("Started process to summarize MD Growth Model development capacities");
        long startTime = System.currentTimeMillis();
        readZones();
        readData();
        writeSummary();
        float endTime = SiloUtil.rounder(((System.currentTimeMillis() - startTime) / 60000), 1);
        int hours = (int) (endTime / 60);
        int min = (int) (endTime - 60 * hours);
        logger.info("Runtime: " + hours + " hours and " + min + " minutes.");
        logger.info("Completed process to summarize MD Growth Model development capacities");
    }


    private void readZones () {
        // read zone system

        String fileName = baseDirectory + rb.getString("zonal.data.file");
        TableDataSet zonalData = SiloUtil.readCSVfile(fileName);
        zones = zonalData.getColumnAsInt("SMZRMZ");
        devCap = new float[SiloUtil.getHighestVal(zones) + 1];
    }


    private void readData () {
        // read dbf files
        String path = rb.getString("growth.model.location");
        String[] fileNames = ResourceUtil.getArray(rb, "dbf.county.files");
        for (String txt: fileNames) {
            String fileName = txt.concat("_adjusted_SpatialJoin.dbf");
            try {
                logger.info("  Reading file " + fileName);
                DBFFileReader dbf = new DBFFileReader();
                TableDataSet data = dbf.readFile(new File(baseDirectory + path + fileName));
                summarizeThisCounty(data);
            } catch (Exception e) {
                logger.error("Could not read " + fileName);
            }
        }
    }


    private void summarizeThisCounty (TableDataSet data) {
        // aggregate development potential by TAZ

        for (int row = 1; row <= data.getRowCount(); row++) {
            int zone = (int) data.getValueAt(row, "CENT");
            devCap[zone] += data.getValueAt(row, "NHC_ADJ");
        }
    }


    private void writeSummary () {
        // write out development capacity

        PrintWriter pw = SiloUtil.openFileForSequentialWriting(rb.getString("zonal.development.capcty"), false);
        pw.println("Zone,devCapacity");
        for (int zone: zones) {
            if (devCap[zone] > 0) pw.println(zone + "," + devCap[zone]);
        }
        pw.close();
    }
}
