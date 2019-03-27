package de.tum.bgu.msm.data;

import com.pb.common.datafile.TableDataSet;
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
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.person.Person;
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
public class SummarizeData {
    private static final String DEVELOPMENT_FILE = "development"; ;
    private final static Logger logger = Logger.getLogger(SummarizeData.class);


    private static PrintWriter resultWriter;
    private static PrintWriter spatialResultWriter;

    private static PrintWriter resultWriterFinal;
    private static PrintWriter spatialResultWriterFinal;

    public static Boolean resultWriterReplicate = false;

    private static TableDataSet scalingControlTotals;
    private static final String RESULT_FILE_SPATIAL = "resultFileSpatial";
    private static final String RESULT_FILE = "resultFile";

    public static void openResultFile(Properties properties) {
        // open summary file

        String directory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
        resultWriter = SiloUtil.openFileForSequentialWriting(directory + "/" + RESULT_FILE +
                ".csv", properties.main.startYear != properties.main.baseYear);
        resultWriterFinal = SiloUtil.openFileForSequentialWriting(directory + "/" + RESULT_FILE + "_" + properties.main.endYear + ".csv", false);
    }


    public static void readScalingYearControlTotals() {
        // read file with control totals to scale synthetic population to exogenous assumptions for selected output years

        String fileName = Properties.get().main.baseDirectory + Properties.get().main.scalingControlTotals;
        scalingControlTotals = SiloUtil.readCSVfile(fileName);
        scalingControlTotals.buildIndex(scalingControlTotals.getColumnPosition("Zone"));
    }


    public static void resultFile(String action) {
        // handle summary file
        resultFile(action, true);
    }

    public static void resultFile(String action, Boolean writeFinal) {
        // handle summary file
        switch (action) {
            case "close":
                resultWriter.close();
                resultWriterFinal.close();
                break;
            default:
                resultWriter.println(action);
                if (resultWriterReplicate && writeFinal) resultWriterFinal.println(action);
                break;
        }
    }

    public static void resultFileSpatial(String action) {
        resultFileSpatial(action, true);
    }

    public static void resultFileSpatial(String action, Boolean writeFinal) {
        // handle summary file
        switch (action) {
            case "open":
                String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
                spatialResultWriter = SiloUtil.openFileForSequentialWriting(directory + "/" + RESULT_FILE_SPATIAL +
                        ".csv", Properties.get().main.startYear != Properties.get().main.baseYear);
                spatialResultWriterFinal = SiloUtil.openFileForSequentialWriting(directory + "/" + RESULT_FILE_SPATIAL + "_" + Properties.get().main.endYear + ".csv", false);
                break;
            case "close":
                spatialResultWriter.close();
                spatialResultWriterFinal.close();
                break;
            default:
                spatialResultWriter.println(action);
                if (resultWriterReplicate && writeFinal) spatialResultWriterFinal.println(action);
                break;
        }
    }

    public static void summarizeSpatially(int year, DataContainer dataContainer) {
        // write out results by zone

        List<DwellingType> dwellingTypes = dataContainer.getRealEstateDataManager().getDwellingTypes();
        String hd = "Year" + year + ",autoAccessibility,transitAccessibility,population,households,hhInc_<" + Properties.get().main.incomeBrackets[0];
        for (int inc = 0; inc < Properties.get().main.incomeBrackets.length; inc++) {
            hd = hd.concat(",hhInc_>" + Properties.get().main.incomeBrackets[inc]);
        }
        for (DwellingType dwellingType : dwellingTypes){
            hd = hd.concat("dd_" + dwellingType.toString());
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
            dds[dataContainer.getRealEstateDataManager().getDwellingTypes().indexOf(dd.getType())][dd.getZoneId()]++;
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
            for (int inc = 0; inc <= Properties.get().main.incomeBrackets.length; inc++)
                txt = txt.concat("," + hhInc[inc][taz]);
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
            resultFileSpatial(txt);
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
            if (hhByZone.containsKey(zone)) hhs = hhByZone.get(zone).length;
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
                        pwh.println(hh.getAutos());
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
                            pwp.println(pp.getIncome());
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
                                pwh.println(hh.getAutos());
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
                                    pwp.println(pp.getIncome());
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
                            pwh.println(hh.getAutos());
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
                                pwp.println(pp.getIncome());
                            }
                        }
                    }
                }
            } else {
                if (scalingControlTotals.getIndexedValueAt(zone, ("HH" + year)) > 0)
                    logger.warn("SILO has no households in zone " +
                            zone + " that could be duplicated to match control total of " +
                            scalingControlTotals.getIndexedValueAt(zone, ("HH" + year)) + ".");
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
        pw.println("id,zone,type,size,occupied");
        for (Dwelling dd : dataContainer.getRealEstateDataManager().getDwellings()) {
            pw.print(dd.getId());
            pw.print(",");
            pw.print(dd.getZoneId());
            pw.print(",");
            pw.print(dd.getType());
            pw.print(",");
            pw.print(dd.getBedrooms());
            pw.print(",");
            pw.println((dd.getResidentId() == -1));
        }
        pw.close();
    }



//    public static void summarizeAutoOwnershipByCounty(Accessibility accessibility, DataContainer dataContainer) {
//        // This calibration function summarized households by auto-ownership and quits
//
//        PrintWriter pwa = SiloUtil.openFileForSequentialWriting("autoOwnershipA.csv", false);
//        pwa.println("hhSize,workers,income,transit,density,autos");
//        int[][] autos = new int[4][60000];
//        final RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
//        final GeoData geoData = dataContainer.getGeoData();
//        final JobDataManager jobDataManager = dataContainer.getJobDataManager();
//        final HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
//        for (Household hh : householdDataManager.getHouseholds()) {
//            int autoOwnership = hh.getAutos();
//            int zone = -1;
//            Dwelling dwelling = realEstate.getDwelling(hh.getDwellingId());
//            if (dwelling != null) {
//                zone = dwelling.getZoneId();
//            }
//            int county =  geoData.getZones().get(zone)).getCounty().getId();
//            autos[autoOwnership][county]++;
//            pwa.println(hh.getHhSize() + "," + HouseholdUtil.getNumberOfWorkers(hh) + "," + HouseholdUtil.getHhIncome(hh) + "," +
//                    accessibility.getTransitAccessibilityForZone(zone) + "," + jobDataManager.getJobDensityInZone(zone) + "," + hh.getAutos());
//        }
//        pwa.close();
//
//        PrintWriter pw = SiloUtil.openFileForSequentialWriting("autoOwnershipB.csv", false);
//        pw.println("County,0autos,1auto,2autos,3+autos");
//        for (int county = 0; county < 60000; county++) {
//            int sm = 0;
//            for (int a = 0; a < 4; a++) sm += autos[a][county];
//            if (sm > 0)
//                pw.println(county + "," + autos[0][county] + "," + autos[1][county] + "," + autos[2][county] + "," + autos[3][county]);
//        }
//        pw.close();
//        logger.info("Summarized auto ownership and quit.");
//        System.exit(0);
//    }


    public static void summarizeCarOwnershipByMunicipality(TableDataSet zonalData, DataContainer dataContainer) {
        // This calibration function summarizes household auto-ownership by municipality and quits

        SiloUtil.createDirectoryIfNotExistingYet("microData/interimFiles/");
        PrintWriter pwa = SiloUtil.openFileForSequentialWriting("microData/interimFiles/carOwnershipByHh.csv", false);
        pwa.println("license,workers,income,logDistanceToTransit,areaType,autos");
        int[][] autos = new int[4][10000000];
        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            int autoOwnership = hh.getAutos();
            int zone = -1;
            Dwelling dwelling = realEstate.getDwelling(hh.getDwellingId());
            if (dwelling != null) {
                zone = dwelling.getZoneId();
            }
            int municipality = (int) zonalData.getIndexedValueAt(zone, "ID_city");
            int distance = (int) Math.log(zonalData.getIndexedValueAt(zone, "distanceToTransit"));
            int area = (int) zonalData.getIndexedValueAt(zone, "BBSR");
            autos[autoOwnership][municipality]++;
            pwa.println(HouseholdUtil.getHHLicenseHolders(hh) + "," + HouseholdUtil.getNumberOfWorkers(hh) + "," + HouseholdUtil.getHhIncome(hh) + "," +
                    distance + "," + area + "," + hh.getAutos());
        }
        pwa.close();

        PrintWriter pw = SiloUtil.openFileForSequentialWriting("microData/interimFiles/carOwnershipByMunicipality.csv", false);
        pw.println("Municipality,0autos,1auto,2autos,3+autos");
        for (int municipality = 0; municipality < 10000000; municipality++) {
            int sm = 0;
            for (int a = 0; a < 4; a++) sm += autos[a][municipality];
            if (sm > 0)
                pw.println(municipality + "," + autos[0][municipality] + "," + autos[1][municipality] + "," + autos[2][municipality] + "," + autos[3][municipality]);
        }
        pw.close();

        logger.info("Summarized initial auto ownership");
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
        List<DwellingType> dwellingTypes = dataContainer.getRealEstateDataManager().getDwellingTypes();
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
