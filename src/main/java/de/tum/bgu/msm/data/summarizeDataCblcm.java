package de.tum.bgu.msm.data;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.maryland.GeoDataMstm;
import de.tum.bgu.msm.data.maryland.MstmZone;
import de.tum.bgu.msm.properties.Properties;

import java.io.PrintWriter;

/**
 * Summarizes SILO model output for the Chesapeake Bay Land Change Model
 * @author Rolf Moeckel
 * Created on 3 July 2014 in College Park, MD
 *
 */

public class summarizeDataCblcm {


    public static void createCblcmSummaries(int year, SiloModelContainer modelContainer,
                                            SiloDataContainer dataContainer) {
        // create summary files for Chesapeake Bay Land Change Model

        if (!SiloUtil.containsElement(Properties.get().cblcm.years, year)) return;
        String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName + "/cblcm";
        SiloUtil.createDirectoryIfNotExistingYet(directory);
        summarizePopulation(year, dataContainer);
        summarizeEmployment(year, dataContainer);
        summarizeDwellings(year, dataContainer);
        summarizeAccessibilities(year, modelContainer, dataContainer);
        summarizeByCounty(year, dataContainer);
    }


    private static void summarizePopulation (int year, SiloDataContainer dataContainer) {
        // summarize households by type and zone for selected years

        String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
        String popFileName = (directory + "/cblcm/" + Properties.get().cblcm.populationFile +
                Properties.get().main.gregorianIterator + ".csv");
        int[][] households = new int[dataContainer.getGeoData().getZones().size()][Properties.get().main.incomeBrackets.length + 1];
        RealEstateDataManager realEstate = dataContainer.getRealEstateData();
        for (Household hh : dataContainer.getHouseholdData().getHouseholds()) {
            int hhIncomeGroup = HouseholdDataManager.getIncomeCategoryForIncome(hh.getHhIncome());
            int zone = -1;
            Dwelling dwelling = realEstate.getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                zone = dwelling.getZone();
            }
            households[dataContainer.getGeoData().getZoneIndex(zone)][hhIncomeGroup - 1]++;
        }

        if (SiloUtil.checkIfFileExists(popFileName) && year != Properties.get().main.implementation.BASE_YEAR) {
            TableDataSet pop = SiloUtil.readCSVfile(popFileName);
            for (int income = 0; income <= Properties.get().main.incomeBrackets.length; income++) {
                int[] hh = new int[dataContainer.getGeoData().getZones().size()];
                for (int i = 0; i < dataContainer.getGeoData().getZones().size(); i++) hh[i] = households[i][income];
                String columnName;
                if (income == 0) {
                    columnName = "hh" + year + "_i_0-" + Properties.get().main.incomeBrackets[income];
                } else if (income == Properties.get().main.incomeBrackets.length) {
                    columnName = "hh" + year + "_i_above" + Properties.get().main.incomeBrackets[income - 1];
                } else {
                    columnName = "hh" + year + "_i_" + Properties.get().main.incomeBrackets[income - 1] + "-" + Properties.get().main.incomeBrackets[income];
                }
                pop.appendColumn(hh, columnName);
            }
            SiloUtil.writeTableDataSet(pop, popFileName);
        } else {
            PrintWriter pw = SiloUtil.openFileForSequentialWriting(popFileName, false);
            pw.print("zone");
            pw.print(",hh" + year + "_i_0-" + Properties.get().main.incomeBrackets[0]);
            for (int i = 1; i < Properties.get().main.incomeBrackets.length; i++)
                pw.print(",hh" + year + "_i_" + Properties.get().main.incomeBrackets[i - 1] +
                        "-" + Properties.get().main.incomeBrackets[i]);
            pw.print(",hh" + year + "_i_above" + Properties.get().main.incomeBrackets[Properties.get().main.incomeBrackets.length-1]);
            pw.println();

            for (int zn: dataContainer.getGeoData().getZones().keySet()) {
                pw.print(zn);
                for (int i = 0; i <= Properties.get().main.incomeBrackets.length; i++) {
                    pw.print("," + households[zn][i]);
                }
                pw.println();
            }
            pw.close();
        }
    }


    private static void summarizeEmployment (int year, SiloDataContainer dataContainer) {
        // summarize employment by type for selected years

        String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
        String emplFileName = (directory + "/cblcm/" + Properties.get().cblcm.employmentFile +
                Properties.get().main.gregorianIterator + ".csv");
        int[][] jobs = new int[dataContainer.getGeoData().getZoneIdsArray().length][JobType.getNumberOfJobTypes()];
        for (Job jj : dataContainer.getJobData().getJobs()) {
            int jobType = JobType.getOrdinal(jj.getType());
            jobs[dataContainer.getGeoData().getZoneIndex(jj.getZone())][jobType]++;
        }

        if (SiloUtil.checkIfFileExists(emplFileName) && year != Properties.get().main.implementation.BASE_YEAR) {
            TableDataSet empl = SiloUtil.readCSVfile(emplFileName);
            for (int emplType = 0; emplType < JobType.getNumberOfJobTypes(); emplType++) {
                int[] jobOfThisType = new int[dataContainer.getGeoData().getZoneIdsArray().length];
                for (int i = 0; i < dataContainer.getGeoData().getZoneIdsArray().length; i++) jobOfThisType[i] = jobs[i][emplType];
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
            int[] zones = dataContainer.getGeoData().getZoneIdsArray();
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


    private static void summarizeDwellings (int year, SiloDataContainer dataContainer) {
        // summarize dwellings by type and zone for selected years

        String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
        String ddFileName = (directory + "/cblcm/" + Properties.get().cblcm.dwellingsFile +
                Properties.get().main.gregorianIterator + ".csv");
        int[][] dwellings = new int[dataContainer.getGeoData().getZoneIdsArray().length][DwellingType.values().length];
        for (Dwelling dd : dataContainer.getRealEstateData().getDwellings()) {
            int ddType = dd.getType().ordinal();
            dwellings[dataContainer.getGeoData().getZoneIndex(dd.getZone())][ddType]++;
        }

        if (SiloUtil.checkIfFileExists(ddFileName) && year != Properties.get().main.implementation.BASE_YEAR) {
            TableDataSet ddTable = SiloUtil.readCSVfile(ddFileName);
            for (DwellingType ddType: DwellingType.values()) {
                int[] dd = new int[dataContainer.getGeoData().getZoneIdsArray().length];
                int type = ddType.ordinal();
                for (int i = 0; i < dataContainer.getGeoData().getZoneIdsArray().length; i++) dd[i] = dwellings[i][type];
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
            int[] zones = dataContainer.getGeoData().getZoneIdsArray();
            for (int zn = 0; zn < zones.length; zn++) {
                pw.print(zones[zn]);
                for (DwellingType ddType: DwellingType.values()) pw.print("," + dwellings[zn][ddType.ordinal()]);
                pw.println();
            }
            pw.close();
        }
    }


    private static void summarizeAccessibilities (int year, SiloModelContainer modelContainer,
                                                  SiloDataContainer dataContainer) {
        // summarize accessibilities by type (transit/highway) and zone for selected years

        String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
        String accFileName = (directory + "/cblcm/" + Properties.get().cblcm.accessibilityFile +
                Properties.get().main.gregorianIterator + ".csv");

        if (SiloUtil.checkIfFileExists(accFileName) && year != Properties.get().main.implementation.BASE_YEAR) {
            TableDataSet accTable = SiloUtil.readCSVfile(accFileName);
            double[] accHwy = new double[dataContainer.getGeoData().getZoneIdsArray().length];
            double[] accTrn = new double[dataContainer.getGeoData().getZoneIdsArray().length];
            int[] zones = dataContainer.getGeoData().getZoneIdsArray();
            for (int i = 0; i < zones.length; i++) {
                accHwy[i] = modelContainer.getAcc().getAutoAccessibilityForZone(zones[i]);
                accTrn[i] = modelContainer.getAcc().getTransitAccessibilityForZone(zones[i]);
            }
            accTable.appendColumn(accHwy, "acc_auto_" + year);
            accTable.appendColumn(accTrn, "acc_transit_" + year);
            SiloUtil.writeTableDataSet(accTable, accFileName);
        } else {
            PrintWriter pw = SiloUtil.openFileForSequentialWriting(accFileName,false);
            pw.println("zone,acc_auto_" + year + ",acc_transit_" + year);
            int[] zones = dataContainer.getGeoData().getZoneIdsArray();
            for (int zone : zones) pw.println(zone + "," + modelContainer.getAcc().getAutoAccessibilityForZone(zone) + "," +
                    modelContainer.getAcc().getTransitAccessibilityForZone(zone));
            pw.close();
        }
    }


    private static void summarizeByCounty (int year, SiloDataContainer dataContainer) {
        // summarize population and employment data by county

        String countyOrderFile = Properties.get().cblcm.countyOrderFile;
        int[] countyOrder = SiloUtil.readCSVfile(countyOrderFile).getColumnAsInt("fips");
        int[] countyOrderIndex = SiloUtil.createIndexArray(countyOrder);
        String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
        String hhFileName = (directory + "/cblcm/" + Properties.get().cblcm.countyPopulationFile +
                Properties.get().main.gregorianIterator + ".txt");
        String jobFileName = (directory + "/cblcm/" + Properties.get().cblcm.countyEmployMentFile +
                Properties.get().main.gregorianIterator + ".txt");

        int[] hhByCounty = new int[countyOrder.length];
        int[] jobsByCounty = new int[countyOrder.length];
        RealEstateDataManager realEstate = dataContainer.getRealEstateData();
        GeoDataMstm geoData = (GeoDataMstm) dataContainer.getGeoData();
        for (Household hh: dataContainer.getHouseholdData().getHouseholds()) {
            int zoneId = -1;
            Dwelling dwelling = realEstate.getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                zoneId = dwelling.getZone();
            }
            MstmZone zone = (MstmZone) geoData.getZones().get(zoneId);
            int homeFips = zone.getCounty().getId();
            if (SiloUtil.containsElement(countyOrder, homeFips)) {
                hhByCounty[countyOrderIndex[homeFips]]++;
            }
        }
        for (Job jj: dataContainer.getJobData().getJobs()) {
            int jobFips = ((MstmZone) geoData.getZones().get(jj.getZone())).getCounty().getId();
            if (SiloUtil.containsElement(countyOrder, jobFips)) {
                jobsByCounty[countyOrderIndex[jobFips]]++;
            }
        }

        PrintWriter pwp;
        PrintWriter pwj;
        if (!SiloUtil.checkIfFileExists(hhFileName) || year == Properties.get().main.implementation.BASE_YEAR) {
            pwp = SiloUtil.openFileForSequentialWriting(hhFileName, false);
            pwp.print("Housing");
            for (int fips: countyOrder) pwp.print(" " + fips);
            pwp.println();
            pwj = SiloUtil.openFileForSequentialWriting(jobFileName, false);
            pwj.print("JOBS");
            for (int fips: countyOrder) pwj.print(" " + fips);
            pwj.println();
        } else {
            pwp = SiloUtil.openFileForSequentialWriting(hhFileName, true);
            pwj = SiloUtil.openFileForSequentialWriting(jobFileName, true);
        }
        pwp.print(year);
        for (int fips: countyOrder) pwp.print(" " + hhByCounty[countyOrderIndex[fips]]);
        pwp.println();
        pwp.close();
        pwj.print(year);
        for (int fips: countyOrder) pwj.print(" " + jobsByCounty[countyOrderIndex[fips]]);
        pwj.println();
        pwj.close();
    }
}


