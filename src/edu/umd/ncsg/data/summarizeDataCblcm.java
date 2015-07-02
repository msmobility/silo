package edu.umd.ncsg.data;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloUtil;

import java.io.PrintWriter;
import java.util.ResourceBundle;

/**
 * Summarizes SILO model output for the Chesapeake Bay Land Change Model
 * @author Rolf Moeckel
 * Created on 3 July 2014 in College Park, MD
 *
 */

public class summarizeDataCblcm {

    protected static final String PROPERTIES_CBLCM_YEARS                = "cblcm.years";
    protected static final String PROPERTIES_CBLCM_POPULATION_FILE      = "cblcm.population.file.name";
    protected static final String PROPERTIES_CBLCM_EMPLOYMENT_FILE      = "cblcm.employment.file.name";
    protected static final String PROPERTIES_CBLCM_DWELLING_FILE        = "cblcm.dwellings.file.name";
    protected static final String PROPERTIES_CBLCM_ACCESSIBILITIES_FILE = "cblcm.accessibilities.file.name";


    public static void createCblcmSummaries(ResourceBundle rb, int year) {
        // create summary files for Chesapeake Bay Land Change Model

        if (!SiloUtil.containsElement(ResourceUtil.getIntegerArray(rb, PROPERTIES_CBLCM_YEARS), year)) return;
        String directory = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/cblcm";
        SiloUtil.createDirectoryIfNotExistingYet(directory);
        summarizePopulation(rb, year);
        summarizeEmployment(rb, year);
        summarizeDwellings(rb, year);
        summarizeAccessibilities(rb, year);
    }


    private static void summarizePopulation (ResourceBundle rb, int year) {
        // summarize households by type and zone for selected years

        String directory = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName;
        String popFileName = (directory + "/cblcm/" + rb.getString(PROPERTIES_CBLCM_POPULATION_FILE) +
                SiloUtil.gregorianIterator + ".csv");
        int[][] households = new int[geoData.getZones().length][SiloUtil.incBrackets.length + 1];
        for (Household hh : Household.getHouseholdArray()) {
            int hhIncomeGroup = HouseholdDataManager.getIncomeCategoryForIncome(hh.getHhIncome());
            households[geoData.getZoneIndex(hh.getHomeZone())][hhIncomeGroup - 1]++;
        }

        if (SiloUtil.checkIfFileExists(popFileName) && year != SiloUtil.getBaseYear()) {
            TableDataSet pop = SiloUtil.readCSVfile(popFileName);
            for (int income = 0; income <= SiloUtil.incBrackets.length; income++) {
                int[] hh = new int[geoData.getZones().length];
                for (int i = 0; i < geoData.getZones().length; i++) hh[i] = households[i][income];
                String columnName;
                if (income == 0) {
                    columnName = "hh" + year + "_i_0-" + SiloUtil.incBrackets[income];
                } else if (income == SiloUtil.incBrackets.length) {
                    columnName = "hh" + year + "_i_above" + SiloUtil.incBrackets[income - 1];
                } else {
                    columnName = "hh" + year + "_i_" + SiloUtil.incBrackets[income - 1] + "-" + SiloUtil.incBrackets[income];
                }
                pop.appendColumn(hh, columnName);
            }
            SiloUtil.writeTableDataSet(pop, popFileName);
        } else {
            PrintWriter pw = SiloUtil.openFileForSequentialWriting(popFileName, false);
            pw.print("zone");
            pw.print(",hh" + year + "_i_0-" + SiloUtil.incBrackets[0]);
            for (int i = 1; i < SiloUtil.incBrackets.length; i++)
                pw.print(",hh" + year + "_i_" + SiloUtil.incBrackets[i - 1] +
                        "-" + SiloUtil.incBrackets[i]);
            pw.print(",hh" + year + "_i_above" + SiloUtil.incBrackets[SiloUtil.incBrackets.length-1]);
            pw.println();
            int[] zones = geoData.getZones();
            for (int zn = 0; zn < zones.length; zn++) {
                pw.print(zones[zn]);
                for (int i = 0; i <= SiloUtil.incBrackets.length; i++) pw.print("," + households[zn][i]);
                pw.println();
            }
            pw.close();
        }
    }


    private static void summarizeEmployment (ResourceBundle rb, int year) {
        // summarize employment by type for selected years

        String directory = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName;
        String emplFileName = (directory + "/cblcm/" + rb.getString(PROPERTIES_CBLCM_EMPLOYMENT_FILE) +
                SiloUtil.gregorianIterator + ".csv");
        int[][] jobs = new int[geoData.getZones().length][JobType.getNumberOfJobTypes()];
        for (Job jj : Job.getJobArray()) {
            int jobType = JobType.getOrdinal(jj.getType());
            jobs[geoData.getZoneIndex(jj.getZone())][jobType]++;
        }

        if (SiloUtil.checkIfFileExists(emplFileName) && year != SiloUtil.getBaseYear()) {
            TableDataSet empl = SiloUtil.readCSVfile(emplFileName);
            for (int emplType = 0; emplType < JobType.getNumberOfJobTypes(); emplType++) {
                int[] jobOfThisType = new int[geoData.getZones().length];
                for (int i = 0; i < geoData.getZones().length; i++) jobOfThisType[i] = jobs[i][emplType];
                String columnName = "empl_" + year + "_" + JobType.getJobType(emplType);
                empl.appendColumn(jobOfThisType, columnName);
            }
            SiloUtil.writeTableDataSet (empl, emplFileName);
        } else {
            PrintWriter pw = SiloUtil.openFileForSequentialWriting(emplFileName, false);
            pw.print("zone");
            for (int emplType = 0; emplType < JobType.getNumberOfJobTypes(); emplType++)
                pw.print(",empl_" + year + "_" + JobType.getJobType(emplType));
            pw.println();
            int[] zones = geoData.getZones();
            for (int zn = 0; zn < zones.length; zn++) {
                pw.print(zones[zn]);
                for (int empType = 0; empType < JobType.getNumberOfJobTypes(); empType++) {
                    pw.print("," + jobs[zn][empType]);
                }
                pw.println();
            }
            pw.close();
        }
    }


    private static void summarizeDwellings (ResourceBundle rb, int year) {
        // summarize dwellings by type and zone for selected years

        String directory = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName;
        String ddFileName = (directory + "/cblcm/" + rb.getString(PROPERTIES_CBLCM_DWELLING_FILE) +
                SiloUtil.gregorianIterator + ".csv");
        int[][] dwellings = new int[geoData.getZones().length][DwellingType.values().length];
        for (Dwelling dd : Dwelling.getDwellingArray()) {
            int ddType = dd.getType().ordinal();
            dwellings[geoData.getZoneIndex(dd.getZone())][ddType]++;
        }

        if (SiloUtil.checkIfFileExists(ddFileName) && year != SiloUtil.getBaseYear()) {
            TableDataSet ddTable = SiloUtil.readCSVfile(ddFileName);
            for (DwellingType ddType: DwellingType.values()) {
                int[] dd = new int[geoData.getZones().length];
                int type = ddType.ordinal();
                for (int i = 0; i < geoData.getZones().length; i++) dd[i] = dwellings[i][type];
                String columnName = "dd_" + year + "_" + ddType.toString();
                ddTable.appendColumn(dd, columnName);
            }
            SiloUtil.writeTableDataSet(ddTable, ddFileName);
        } else {
            PrintWriter pw = SiloUtil.openFileForSequentialWriting(ddFileName, false);
            pw.print("zone");
            for (DwellingType ddType: DwellingType.values())
                pw.print(",dd_" + year + "_" + ddType.toString());
            pw.println();
            int[] zones = geoData.getZones();
            for (int zn = 0; zn < zones.length; zn++) {
                pw.print(zones[zn]);
                for (DwellingType ddType: DwellingType.values()) pw.print("," + dwellings[zn][ddType.ordinal()]);
                pw.println();
            }
            pw.close();
        }
    }


    private static void summarizeAccessibilities (ResourceBundle rb, int year) {
        // summarize accessibilities by type (transit/highway) and zone for selected years

        String directory = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName;
        String accFileName = (directory + "/cblcm/" + rb.getString(PROPERTIES_CBLCM_ACCESSIBILITIES_FILE) +
                SiloUtil.gregorianIterator + ".csv");

        if (SiloUtil.checkIfFileExists(accFileName) && year != SiloUtil.getBaseYear()) {
            TableDataSet accTable = SiloUtil.readCSVfile(accFileName);
            double[] accHwy = new double[geoData.getZones().length];
            double[] accTrn = new double[geoData.getZones().length];
            int[] zones = geoData.getZones();
            for (int i = 0; i < zones.length; i++) {
                accHwy[i] = Accessibility.getAutoAccessibility(zones[i]);
                accTrn[i] = Accessibility.getTransitAccessibility(zones[i]);
            }
            accTable.appendColumn(accHwy, "acc_auto_" + year);
            accTable.appendColumn(accTrn, "acc_transit_" + year);
            SiloUtil.writeTableDataSet(accTable, accFileName);
        } else {
            PrintWriter pw = SiloUtil.openFileForSequentialWriting(accFileName,false);
            pw.println("zone,acc_auto_" + year + ",acc_transit_" + year);
            int[] zones = geoData.getZones();
            for (int zone : zones) pw.println(zone + "," + Accessibility.getAutoAccessibility(zone) + "," +
                    Accessibility.getTransitAccessibility(zone));
            pw.close();
        }
    }
}


