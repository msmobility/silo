package de.tum.bgu.msm.data;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.development.Development;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingData;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdData;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.IndexedDoubleMatrix1D;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Methods to summarize model results
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 23 February 2012 in Albuquerque
 **/
public final class SummarizeData {
    private static final String DEVELOPMENT_FILE = "development"; ;
    private final static Logger logger = Logger.getLogger(SummarizeData.class);

    private static PrintWriter spatialResultWriter;
    private static PrintWriter spatialResultWriter_2;

    private static TableDataSet scalingControlTotals;
    private static final String RESULT_FILE_SPATIAL = "resultFileSpatial";

    private SummarizeData(){}

    public static void readScalingYearControlTotals() {
        // read file with control totals to scale synthetic population to exogenous assumptions for selected output years

        String fileName = Properties.get().main.baseDirectory + Properties.get().main.scalingControlTotals;
        scalingControlTotals = SiloUtil.readCSVfile(fileName);
        scalingControlTotals.buildIndex(scalingControlTotals.getColumnPosition("Zone"));
    }

    public static void resultFileSpatial(String action) {
        // handle summary file
        switch (action) {
            case "open":
                String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
                spatialResultWriter = SiloUtil.openFileForSequentialWriting(directory + "/" + RESULT_FILE_SPATIAL +
                        ".csv", Properties.get().main.startYear != Properties.get().main.baseYear);
                break;
            case "close":
                spatialResultWriter.close();
                break;
            default:
                spatialResultWriter.println(action);
                break;
        }
    }

    public static void resultFileSpatial_2(String action) {
        // handle summary file
        switch (action) {
            case "open":
                String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
                spatialResultWriter_2 = SiloUtil.openFileForSequentialWriting(directory + "/siloResults/" + RESULT_FILE_SPATIAL +
                        ".csv", Properties.get().main.startYear != Properties.get().main.baseYear);
                break;
            case "close":
                spatialResultWriter_2.close();
                break;
            default:
                spatialResultWriter_2.println(action);
                break;
        }
    }

    public static void summarizeSpatially(int year, DataContainer dataContainer) {
        // write out results by zone

        List<DwellingType> dwellingTypes = dataContainer.getRealEstateDataManager().getDwellingTypes().getTypes();
        String hd = "Year" + year + ",autoAccessibility,transitAccessibility,population,households,hhInc_<" + Properties.get().main.incomeBrackets[0];
        for (int inc = 0; inc < Properties.get().main.incomeBrackets.length; inc++) {
            hd = hd.concat(",hhInc_>" + Properties.get().main.incomeBrackets[inc]);
        }
        for (DwellingType dwellingType : dwellingTypes){
            hd = hd.concat(",dd_" + dwellingType.toString());
        }

        if(year == Properties.get().main.baseYear) {
            String hd_2 = "year,zone,autoAccessibility,transitAccessibility,population,households,hhInc_<" + Properties.get().main.incomeBrackets[0];
            for (int inc = 0; inc < Properties.get().main.incomeBrackets.length; inc++) {
                hd_2 = hd_2.concat(",hhInc_>" + Properties.get().main.incomeBrackets[inc]);
            }
            for (DwellingType dwellingType : dwellingTypes) {
                hd_2 = hd_2.concat(",dd_" + dwellingType.toString());
            }
            hd_2 = hd_2.concat(",availLand,avePrice,jobs,shWhite,shBlack,shHispanic,shOther");
            resultFileSpatial_2(hd_2);
        }


        hd = hd.concat(",availLand,avePrice,jobs,shWhite,shBlack,shHispanic,shOther");
        resultFileSpatial(hd);

        final int highestZonalId = dataContainer.getGeoData().getZones().keySet()
                .stream().mapToInt(Integer::intValue).max().getAsInt();
        int[][] dds = new int[dwellingTypes.size()][highestZonalId + 1];
        int[] prices = new int[highestZonalId + 1];
        int[] jobs = new int[highestZonalId + 1];
        int[] hhs = new int[highestZonalId + 1];
        int[][] hhInc = new int[Properties.get().main.incomeBrackets.length + 1][highestZonalId + 1];
        IndexedDoubleMatrix1D pop = getPopulationByZone(dataContainer);
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            int zone = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId()).getZoneId();
            int incGroup = hh.getHouseholdType().getIncomeCategory().ordinal();
            hhInc[incGroup][zone]++;
            hhs[zone]++;
        }
        for (Dwelling dd : dataContainer.getRealEstateDataManager().getDwellings()) {
            dds[dwellingTypes.indexOf(dd.getType())][dd.getZoneId()]++;
            prices[dd.getZoneId()] += dd.getPrice();
        }
        for (Job jj : dataContainer.getJobDataManager().getJobs()) {
            jobs[jj.getZoneId()]++;
        }


        for (Zone zone : dataContainer.getGeoData().getZones().values()) {
        	int taz = zone.getId();
            float avePrice = -1;
            int ddThisZone = 0;
            for (DwellingType dt : dwellingTypes) {
                ddThisZone += dds[dwellingTypes.indexOf(dt)][taz];
            }
            if (ddThisZone > 0) {
                avePrice = ((float) prices[taz]) / ddThisZone;
            }
            double autoAcc = dataContainer.getAccessibility().getAutoAccessibilityForZone(zone);
            double transitAcc = dataContainer.getAccessibility().getTransitAccessibilityForZone(zone);
            double availLand = dataContainer.getRealEstateDataManager().getAvailableCapacityForConstruction(taz);
//            Formatter f = new Formatter();
//            f.format("%d,%f,%f,%d,%d,%d,%f,%f,%d", taz, autoAcc, transitAcc, pop[taz], hhs[taz], dds[taz], availLand, avePrice, jobs[taz]);
            String txt = taz + "," + autoAcc + "," + transitAcc + "," + pop.getIndexed(taz) + "," + hhs[taz];
            for (int inc = 0; inc <= Properties.get().main.incomeBrackets.length; inc++) {
                txt = txt.concat("," + hhInc[inc][taz]);
            }
            for (DwellingType dt : dwellingTypes){
                txt = txt.concat("," + dds[dwellingTypes.indexOf(dt)][taz]);
            }
            txt = txt.concat("," + availLand + "," + avePrice + "," + jobs[taz] + "," +
                    // todo: make the summary application specific, Munich does not work with these race categories
                    "0,0,0,0");
//                    modelContainer.getMove().getZonalRacialShare(taz, Race.white) + "," +
//                    modelContainer.getMove().getZonalRacialShare(taz, Race.black) + "," +
//                    modelContainer.getMove().getZonalRacialShare(taz, Race.hispanic) + "," +
//                    modelContainer.getMove().getZonalRacialShare(taz, Race.other));
//            String txt = f.toString();
            String txt_2 = String.valueOf(year).concat(",").concat(txt);
            resultFileSpatial(txt);
            resultFileSpatial_2(txt_2);
        }
    }


    public static void scaleMicroDataToExogenousForecast(int year, DataContainer dataContainer) {
        //TODO Will fail for new zones with 0 households and a projected growth. Could be an issue when modeling for Zones with transient existence.
        // scale synthetic population to exogenous forecast (for output only, scaled synthetic population is not used internally)

        if (!scalingControlTotals.containsColumn(("HH" + year))) {
            logger.warn("Could not find scaling targets to scale micro data to year " + year + ". No scaling completed.");
            return;
        }
        logger.info("Scaling synthetic population to exogenous forecast for year " + year + " (for output only, " +
                "scaled population is not used internally).");

        int artificialHhId = dataContainer.getHouseholdDataManager().getHighestHouseholdIdInUse() + 1;
        int artificialPpId = dataContainer.getHouseholdDataManager().getHighestPersonIdInUse() + 1;

        // calculate how many households need to be created or deleted in every zone
        final int highestId = dataContainer.getGeoData().getZones().keySet()
                .stream().mapToInt(Integer::intValue).max().getAsInt();
        int[] changeOfHh = new int[highestId + 1];


        Map<Integer, int[]> hhByZone = new HashMap<>();
        RealEstateDataManager realEstateDataManager = dataContainer.getRealEstateDataManager();
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            int zone = -1;
            Dwelling dwelling = realEstateDataManager.getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                zone = dwelling.getZoneId();
            }
            if (hhByZone.containsKey(zone)) {
                int[] oldList = hhByZone.get(zone);
                int[] newList = SiloUtil.expandArrayByOneElement(oldList, hh.getId());
                hhByZone.put(zone, newList);
            } else {
                hhByZone.put(zone, new int[]{hh.getId()});
            }
        }

        for (int zone : dataContainer.getGeoData().getZones().keySet()) {
            int hhs = 0;
            if (hhByZone.containsKey(zone)) {
                hhs = hhByZone.get(zone).length;
            }
            changeOfHh[zone] =
                    (int) scalingControlTotals.getIndexedValueAt(zone, ("HH" + year)) - hhs;
        }

        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(Properties.get().main.scaledMicroDataHh + year + ".csv", false);
        pwh.println("id,dwelling,zone,hhSize,autos");
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(Properties.get().main.scaledMicroDataPp + year + ".csv", false);
        pwp.println("id,hhID,age,gender,occupation,driversLicense,workplace,income");

        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        for (int zone : dataContainer.getGeoData().getZones().keySet()) {
            if (hhByZone.containsKey(zone)) {
                int[] hhInThisZone = hhByZone.get(zone);
                int[] selectedHH = new int[hhInThisZone.length];
                if (changeOfHh[zone] > 0) {          // select households to duplicate (draw with replacement)
                    for (int i = 0; i < changeOfHh[zone]; i++) {
                        int selected = SiloUtil.select(hhInThisZone.length) - 1;
                        selectedHH[selected]++;
                    }
                } else if (changeOfHh[zone] < 0) {   // select households to delete (draw without replacement)
                    float[] prob = new float[hhInThisZone.length];
                    SiloUtil.setArrayToValue(prob, 1);
                    for (int i = 0; i < Math.abs(changeOfHh[zone]); i++) {
                        int selected = SiloUtil.select(prob);
                        selectedHH[selected] = 1;
                        prob[selected] = 0;
                    }
                }

                // write out households and duplicate (if changeOfHh > 0) or delete (if changeOfHh < 0) selected households
                for (int i = 0; i < hhInThisZone.length; i++) {
                    Household hh = householdDataManager.getHouseholdFromId(hhInThisZone[i]);
                    if (changeOfHh[zone] > 0) {
                        // write out original household
                        pwh.print(hh.getId());
                        pwh.print(",");
                        pwh.print(hh.getDwellingId());
                        pwh.print(",");
                        pwh.print(zone);
                        pwh.print(",");
                        pwh.print(hh.getHhSize());
                        pwh.print(",");
                        pwh.println((int) hh.getVehicles().stream().filter(v-> v.getType().equals(VehicleType.CAR)).count());
                        for (Person pp : hh.getPersons().values()) {
                            pwp.print(pp.getId());
                            pwp.print(",");
                            pwp.print(pp.getHousehold().getId());
                            pwp.print(",");
                            pwp.print(pp.getAge());
                            pwp.print(",");
                            pwp.print(pp.getGender().getCode());
                            pwp.print(",");
                            pwp.print(pp.getOccupation());
                            pwp.print(",0,");
                            pwp.print(pp.getJobId());
                            pwp.print(",");
                            pwp.println(pp.getAnnualIncome());
                        }
                        // duplicate household if selected
                        if (selectedHH[i] > 0) {    // household to be repeated for this output file
                            for (int repeat = 0; repeat < selectedHH[i]; repeat++) {
                                pwh.print(artificialHhId);
                                pwh.print(",");
                                pwh.print(hh.getDwellingId());
                                pwh.print(",");
                                pwh.print(zone);
                                pwh.print(",");
                                pwh.print(hh.getHhSize());
                                pwh.print(",");
                                pwh.println((int) hh.getVehicles().stream().filter(v-> v.getType().equals(VehicleType.CAR)).count());
                                for (Person pp : hh.getPersons().values()) {
                                    pwp.print(artificialPpId);
                                    pwp.print(",");
                                    pwp.print(artificialHhId);
                                    pwp.print(",");
                                    pwp.print(pp.getAge());
                                    pwp.print(",");
                                    pwp.print(pp.getGender().getCode());
                                    pwp.print(",");
                                    pwp.print(pp.getOccupation());
                                    pwp.print(",0,");
                                    pwp.print(pp.getJobId());
                                    pwp.print(",");
                                    pwp.println(pp.getAnnualIncome());
                                    artificialPpId++;
                                }
                                artificialHhId++;
                            }
                        }
                    } else if (changeOfHh[zone] < 0) {
                        if (selectedHH[i] == 0) {    // household to be kept (selectedHH[i] == 1 for households to be deleted)
                            pwh.print(hh.getId());
                            pwh.print(",");
                            pwh.print(hh.getDwellingId());
                            pwh.print(",");
                            pwh.print(zone);
                            pwh.print(",");
                            pwh.print(hh.getHhSize());
                            pwh.print(",");
                            pwh.println((int) hh.getVehicles().stream().filter(v-> v.getType().equals(VehicleType.CAR)).count());
                            for (Person pp : hh.getPersons().values()) {
                                pwp.print(pp.getId());
                                pwp.print(",");
                                pwp.print(pp.getHousehold().getId());
                                pwp.print(",");
                                pwp.print(pp.getAge());
                                pwp.print(",");
                                pwp.print(pp.getGender().getCode());
                                pwp.print(",");
                                pwp.print(pp.getOccupation());
                                pwp.print(",0,");
                                pwp.print(pp.getJobId());
                                pwp.print(",");
                                pwp.println(pp.getAnnualIncome());
                            }
                        }
                    }
                }
            } else {
                if (scalingControlTotals.getIndexedValueAt(zone, ("HH" + year)) > 0) {
                    logger.warn("SILO has no households in zone " +
                            zone + " that could be duplicated to match control total of " +
                            scalingControlTotals.getIndexedValueAt(zone, ("HH" + year)) + ".");
                }
            }
        }
        pwh.close();
        pwp.close();
    }


    public static void summarizeHousing(int year, DataContainer dataContainer) {
        // summarize housing data for housing environmental impact calculations

        if (!SiloUtil.containsElement(Properties.get().main.bemModelYears, year)) return;
        String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName + "/bem/";
        SiloUtil.createDirectoryIfNotExistingYet(directory);

        String fileName = (directory + Properties.get().main.housingEnvironmentImpactFile + "_" + year + ".csv");

        PrintWriter pw = SiloUtil.openFileForSequentialWriting(fileName, false);
        pw.println("id,zone,type,size,yearBuilt,occupied");
        for (Dwelling dd : dataContainer.getRealEstateDataManager().getDwellings()) {
            pw.print(dd.getId());
            pw.print(",");
            pw.print(dd.getZoneId());
            pw.print(",");
            pw.print(dd.getType());
            pw.print(",");
            pw.print(dd.getBedrooms());
            pw.print(",");
            pw.print(dd.getYearBuilt());
            pw.print(",");
            pw.println((dd.getResidentId() == -1));
        }
        pw.close();
    }





    public static void writeOutDevelopmentFile(DataContainer dataContainer) {
        // write out development capacity file to allow model run to be continued from this point later


        String baseDirectory = Properties.get().main.baseDirectory;
        String scenarioName = Properties.get().main.scenarioName;
        int endYear = Properties.get().main.endYear;

        String capacityFileName = baseDirectory + "scenOutput/" + scenarioName + "/" +
                DEVELOPMENT_FILE + "_" + endYear + ".csv";

        PrintWriter pw = SiloUtil.openFileForSequentialWriting(capacityFileName, false);
        StringBuilder builder = new StringBuilder();
        builder.append("Zone,");
        List<DwellingType> dwellingTypes = dataContainer.getRealEstateDataManager().getDwellingTypes().getTypes();
        for (DwellingType dwellingType : dwellingTypes) {
            builder.append(dwellingType.toString()).append(",");
        }
        builder.append("DevCapacity,DevLandUse");
        pw.println(builder);

        for (Zone zone : dataContainer.getGeoData().getZones().values()) {
            builder = new StringBuilder();
            builder.append(zone.getId()).append(",");
            Development development = zone.getDevelopment();
            for (DwellingType dwellingType : dwellingTypes) {
                builder.append(development.isThisDwellingTypeAllowed(dwellingType)?1:0).append(",");
            }
            builder.append(development.getDwellingCapacity()).append(",").append(development.getDevelopableArea());
            pw.println(builder);
        }
        pw.close();

    }

    public static IndexedDoubleMatrix1D getPopulationByZone(HouseholdData householdData, GeoData geoData, DwellingData dwellingData) {
        IndexedDoubleMatrix1D popByZone = new IndexedDoubleMatrix1D(geoData.getZones().values());
        for (Household hh : householdData.getHouseholds()) {
            final int zone = dwellingData.getDwelling(hh.getDwellingId()).getZoneId();
            popByZone.setIndexed(zone, popByZone.getIndexed(zone) + hh.getHhSize());
        }
        return popByZone;
    }

    @Deprecated
    public static IndexedDoubleMatrix1D getPopulationByZone(DataContainer dataContainer) {
        IndexedDoubleMatrix1D popByZone = new IndexedDoubleMatrix1D(dataContainer.getGeoData().getZones().values());
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            final int zone = dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId()).getZoneId();
            popByZone.setIndexed(zone, popByZone.getIndexed(zone) + hh.getHhSize());
        }
        return popByZone;
    }
}
