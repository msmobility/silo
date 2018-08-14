package de.tum.bgu.msm.data;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.maryland.MstmZone;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.data.maryland.GeoDataMstm;
import org.apache.commons.lang3.ArrayUtils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//        int[][] households = new int[dataContainer.getGeoData().getZones().size()][IncomeCategory.values().length];
        Map<Integer, EnumMultiset<IncomeCategory>> householdsByZoneAndIncome = new HashMap<>();
        RealEstateDataManager realEstate = dataContainer.getRealEstateData();
        for(int zoneId: dataContainer.getGeoData().getZones().keySet()) {
            householdsByZoneAndIncome.put(zoneId, EnumMultiset.create(IncomeCategory.class));
        }

        //for households not living anywhere at the moment
        householdsByZoneAndIncome.put(-1, EnumMultiset.create(IncomeCategory.class));

        for (Household hh : dataContainer.getHouseholdData().getHouseholds()) {
            IncomeCategory hhIncomeGroup = hh.getHouseholdType().getIncomeCategory();
            int zone = -1;
            Dwelling dwelling = realEstate.getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                zone = dwelling.determineZoneId();
            }
            householdsByZoneAndIncome.get(zone).add(hhIncomeGroup);
        }

        if (SiloUtil.checkIfFileExists(popFileName) && year != Properties.get().main.implementation.BASE_YEAR) {
            TableDataSet pop = SiloUtil.readCSVfile(popFileName);
            for (IncomeCategory income: IncomeCategory.values()) {
                List<Integer> householdCounts = new ArrayList<>();
                for (int i = 0; i < dataContainer.getGeoData().getZones().size(); i++) {
                    householdCounts.add(householdsByZoneAndIncome.get(i).count(income));
                }
                String columnName;
                if (income == IncomeCategory.LOW) {
                    columnName = "hh" + year + "_i_0-" + Properties.get().main.incomeBrackets[income.ordinal()];
                } else if (income == IncomeCategory.VERY_HIGH) {
                    columnName = "hh" + year + "_i_above" + Properties.get().main.incomeBrackets[income.ordinal() - 1];
                } else {
                    columnName = "hh" + year + "_i_" + Properties.get().main.incomeBrackets[income.ordinal() - 1] + "-" + Properties.get().main.incomeBrackets[income.ordinal()];
                }
                pop.appendColumn(ArrayUtils.toPrimitive(householdCounts.toArray(new Integer[0])), columnName);
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
                for (IncomeCategory i: IncomeCategory.values()) {
                    pw.print("," + householdsByZoneAndIncome.get(zn).count(i));
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
        final Map<Integer, Multiset<String>> jobsByZoneAndType = new HashMap<>();
        for(int zoneId: dataContainer.getGeoData().getZones().keySet()) {
            jobsByZoneAndType.put(zoneId, HashMultiset.create());
        }
        for (Job jj : dataContainer.getJobData().getJobs()) {
            jobsByZoneAndType.get(jj.getLocation()).add(jj.getType());
        }

        if (SiloUtil.checkIfFileExists(emplFileName) && year != Properties.get().main.implementation.BASE_YEAR) {
            TableDataSet empl = SiloUtil.readCSVfile(emplFileName);
            for (String emplType: JobType.getJobTypes()) {
                List<Integer> jobCounts = new ArrayList<>();
                for (int i: dataContainer.getGeoData().getZones().keySet()) {
                    jobCounts.add(jobsByZoneAndType.get(i).count(emplType));
                }
                String columnName = "empl_" + year + "_" + emplType;
                empl.appendColumn(ArrayUtils.toPrimitive(jobCounts.toArray(new Integer[0])), columnName);
            }
            SiloUtil.writeTableDataSet (empl, emplFileName);
        } else {
            PrintWriter pw = SiloUtil.openFileForSequentialWriting(emplFileName, false);
            pw.print("zone");
            for (int emplType = 0; emplType < JobType.getNumberOfJobTypes(); emplType++)
                pw.print(",empl_" + year + "_" + JobType.getJobType(emplType));
            pw.println();
            for (int zn: dataContainer.getGeoData().getZones().keySet()) {
                pw.print(zn);
                for (String empType: JobType.getJobTypes()) {
                    pw.print("," + jobsByZoneAndType.get(zn).count(empType));
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
        Map<Integer, EnumMultiset<DwellingType>> dwellingsByZoneAndType = new HashMap<>();
        for(int zone: dataContainer.getGeoData().getZones().keySet()) {
            dwellingsByZoneAndType.put(zone, EnumMultiset.create(DwellingType.class));
        }
        for (Dwelling dd : dataContainer.getRealEstateData().getDwellings()) {
            dwellingsByZoneAndType.get(dd.getLocation()).add(dd.getType());
        }

        if (SiloUtil.checkIfFileExists(ddFileName) && year != Properties.get().main.implementation.BASE_YEAR) {
            TableDataSet ddTable = SiloUtil.readCSVfile(ddFileName);
            for (DwellingType ddType: DwellingType.values()) {
                List<Integer> dwellingCounts = new ArrayList<>();
                for (int i: dataContainer.getGeoData().getZones().keySet()) {
                    dwellingCounts.add(dwellingsByZoneAndType.get(i).count(ddType));
                }
                String columnName = "dd_" + year + "_" + ddType.toString();
                ddTable.appendColumn(ArrayUtils.toPrimitive(dwellingCounts.toArray(new Integer[0])), columnName);
            }
            SiloUtil.writeTableDataSet(ddTable, ddFileName);
        } else {
            PrintWriter pw = SiloUtil.openFileForSequentialWriting(ddFileName, false);
            pw.print("zone");
            for (DwellingType ddType: DwellingType.values())
                pw.print(",dd_" + year + "_" + ddType.toString());
            pw.println();

            for (int zn: dataContainer.getGeoData().getZones().keySet()) {
                pw.print(zn);
                for (DwellingType ddType: DwellingType.values()) {
                    pw.print("," + dwellingsByZoneAndType.get(zn).count(ddType.ordinal()));
                }
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
            List<Double> highwayAccessibilities = new ArrayList<>();
            List<Double> transitAccessibilities = new ArrayList<>();
            for (int zone: dataContainer.getGeoData().getZones().keySet()) {
                highwayAccessibilities.add(modelContainer.getAcc().getAutoAccessibilityForZone(zone));
                transitAccessibilities.add(modelContainer.getAcc().getTransitAccessibilityForZone(zone));
            }
            accTable.appendColumn(ArrayUtils.toPrimitive(highwayAccessibilities.toArray(new Double[0])), "acc_auto_" + year);
            accTable.appendColumn(ArrayUtils.toPrimitive(transitAccessibilities.toArray(new Double[0])), "acc_transit_" + year);
            SiloUtil.writeTableDataSet(accTable, accFileName);
        } else {
            PrintWriter pw = SiloUtil.openFileForSequentialWriting(accFileName,false);
            pw.println("zone,acc_auto_" + year + ",acc_transit_" + year);
            for (int zone : dataContainer.getGeoData().getZones().keySet()) {
                pw.println(zone + "," + modelContainer.getAcc().getAutoAccessibilityForZone(zone) + "," +
                        modelContainer.getAcc().getTransitAccessibilityForZone(zone));
            }
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
                zoneId = dwelling.determineZoneId();
            }
            MstmZone zone = (MstmZone) geoData.getZones().get(zoneId);
            int homeFips = zone.getCounty().getId();
            if (SiloUtil.containsElement(countyOrder, homeFips)) {
                hhByCounty[countyOrderIndex[homeFips]]++;
            }
        }
        for (Job jj: dataContainer.getJobData().getJobs()) {
            int jobFips = ((MstmZone) geoData.getZones().get(jj.determineZoneId())).getCounty().getId();
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


