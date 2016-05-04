package edu.umd.ncsg.data;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.relocation.MovesModel;
import org.apache.log4j.Logger;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ResourceBundle;

import static edu.umd.ncsg.data.RealEstateDataManager.PROPERTIES_CAPACITY_FILE;
import static edu.umd.ncsg.data.RealEstateDataManager.PROPERTIES_LAND_USE_AREA;

/**
 * Methods to summarize model results
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 23 February 2012 in Albuquerque
 **/


public class summarizeData {
    static Logger logger = Logger.getLogger(summarizeData.class);

    protected static final String PROPERTIES_RESULT_FILE_NAME             = "result.file.name";
    protected static final String PROPERTIES_SPATIAL_RESULT_FILE_NAME     = "spatial.result.file.name";
    protected static final String PROPERTIES_SCALING_YEARS_CONTROL_TOTALS = "scaling.years.control.totals";
    protected static final String PROPERTIES_SCALED_MICRO_DATA_HH         = "scaled.micro.data.hh";
    protected static final String PROPERTIES_SCALED_MICRO_DATA_PP         = "scaled.micro.data.pp";
    protected static final String PROPERTIES_HOUSING_SUMMARY              = "housing.environment.impact.file.name";
    protected static final String PROPERTIES_BEM_YEARS                    = "bem.model.years";
    protected static final String PROPERTIES_FILENAME_HH_MICRODATA        = "household.file.ascii";
    protected static final String PROPERTIES_FILENAME_PP_MICRODATA        = "person.file.ascii";
    protected static final String PROPERTIES_FILENAME_DD_MICRODATA        = "dwelling.file.ascii";
    protected static final String PROPERTIES_FILENAME_JJ_MICRODATA        = "job.file.ascii";
    protected static final String PROPERTIES_WRITE_BIN_POP_FILES          = "write.binary.pop.files";
    protected static final String PROPERTIES_WRITE_BIN_DD_FILE            = "write.binary.dd.file";
    protected static final String PROPERTIES_WRITE_BIN_JJ_FILE            = "write.binary.jj.file";
    protected static final String PROPERTIES_PRESTO_REGION_DEFINITION     = "presto.regions";
    protected static final String PROPERTIES_PRESTO_SUMMARY_FILE          = "presto.summary.file";

    private static PrintWriter resultWriter;
    private static PrintWriter spatialResultWriter;

    private static PrintWriter resultWriterFinal;
    private static PrintWriter spatialResultWriterFinal;

    public static Boolean resultWriterReplicate = false;

    private static TableDataSet scalingControlTotals;
    private static int[] prestoRegionByTaz;


    public static void openResultFile(ResourceBundle rb) {
        // open summary file

        String directory = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName;
        SiloUtil.createDirectoryIfNotExistingYet(directory);
        String resultFileName = rb.getString(PROPERTIES_RESULT_FILE_NAME);
        resultWriter = SiloUtil.openFileForSequentialWriting(directory + "/" + resultFileName +
                SiloUtil.gregorianIterator + ".csv", SiloUtil.getStartYear() != SiloUtil.getBaseYear());
        resultWriterFinal = SiloUtil.openFileForSequentialWriting(directory + "/" + resultFileName + "_" + SiloUtil.getEndYear() + ".csv", false);
    }


    public static void readScalingYearControlTotals (ResourceBundle rb) {
        // read file with control totals to scale synthetic population to exogenous assumptions for selected output years

        String fileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_SCALING_YEARS_CONTROL_TOTALS);
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
                if(resultWriterReplicate && writeFinal)resultWriterFinal.println(action);
                break;
        }
    }

    public static void resultFileSpatial(ResourceBundle rb, String action) {
        resultFileSpatial(rb,action,true);
    }
        public static void resultFileSpatial(ResourceBundle rb, String action, Boolean writeFinal) {
        // handle summary file
        switch (action) {
            case "open":
                String directory = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName;
                SiloUtil.createDirectoryIfNotExistingYet(directory);
                String resultFileName = rb.getString(PROPERTIES_SPATIAL_RESULT_FILE_NAME);
                spatialResultWriter = SiloUtil.openFileForSequentialWriting(directory + "/" + resultFileName +
                        SiloUtil.gregorianIterator + ".csv", SiloUtil.getStartYear() != SiloUtil.getBaseYear());
                spatialResultWriterFinal = SiloUtil.openFileForSequentialWriting(directory + "/" + resultFileName +"_"+ SiloUtil.getEndYear() + ".csv", false);
                break;
            case "close":
                spatialResultWriter.close();
                spatialResultWriterFinal.close();
                break;
            default:
                spatialResultWriter.println(action);
                if(resultWriterReplicate && writeFinal )spatialResultWriterFinal.println(action);
                break;
        }
    }

    public static void summarizeSpatially (int year, MovesModel move, RealEstateDataManager realEstateData) {
        // write out results by zone

        String hd = "Year" + year + ",autoAccessibility,transitAccessibility,population,households,hhInc_<" + SiloUtil.incBrackets[0];
        for (int inc = 0; inc < SiloUtil.incBrackets.length; inc++) hd = hd.concat(",hhInc_>" + SiloUtil.incBrackets[inc]);
        resultFileSpatial(null, hd + ",dd_SFD,dd_SFA,dd_MF234,dd_MF5plus,dd_MH,availLand,avePrice,jobs,shWhite,shBlack,shHispanic,shOther");

        int[] zones = geoData.getZones();
        int[][] dds = new int[DwellingType.values().length + 1][geoData.getHighestZonalId() + 1];
        int[] prices = new int[geoData.getHighestZonalId() + 1];
        int[] jobs = new int[geoData.getHighestZonalId() + 1];
        int[] hhs = new int[geoData.getHighestZonalId() + 1];
        int[][] hhInc = new int[SiloUtil.incBrackets.length + 1][geoData.getHighestZonalId() + 1];
        int[] pop = getPopulationByZone();
        for (Household hh: Household.getHouseholdArray()) {
            int zone = Dwelling.getDwellingFromId(hh.getDwellingId()).getZone();
            int incGroup = HouseholdDataManager.getIncomeCategoryForIncome(hh.getHhIncome());
            hhInc[incGroup - 1][zone]++;
            hhs[zone] ++;
        }
        for (Dwelling dd: Dwelling.getDwellingArray()) {
            dds[dd.getType().ordinal()][dd.getZone()]++;
            prices[dd.getZone()] += dd.getPrice();
        }
        for (Job jj: Job.getJobArray()) {
            jobs[jj.getZone()]++;
        }


        for (int taz: zones) {
            float avePrice = -1;
            int ddThisZone = 0;
            for (DwellingType dt: DwellingType.values()) ddThisZone += dds[dt.ordinal()][taz];
            if (ddThisZone > 0) avePrice = prices[taz] / ddThisZone;
            double autoAcc = Accessibility.getAutoAccessibility(taz);
            double transitAcc = Accessibility.getTransitAccessibility(taz);
            double availLand = realEstateData.getAvailableLandForConstruction(taz);
//            Formatter f = new Formatter();
//            f.format("%d,%f,%f,%d,%d,%d,%f,%f,%d", taz, autoAcc, transitAcc, pop[taz], hhs[taz], dds[taz], availLand, avePrice, jobs[taz]);
            String txt = taz + "," + autoAcc + "," + transitAcc + "," + pop[taz] + "," + hhs[taz];
            for (int inc = 0; inc <= SiloUtil.incBrackets.length; inc++) txt = txt.concat("," + hhInc[inc][taz]);
            for (DwellingType dt: DwellingType.values()) txt = txt.concat("," + dds[dt.ordinal()][taz]);
            txt = txt.concat("," + availLand + "," + avePrice + "," + jobs[taz] + "," +
                    move.getZonalRacialShare(taz, Race.white) + "," +
                    move.getZonalRacialShare(taz, Race.black) + "," +
                    move.getZonalRacialShare(taz, Race.hispanic) + "," +
                    move.getZonalRacialShare(taz, Race.other));
//            String txt = f.toString();
            resultFileSpatial(null, txt);
        }
    }


    public static int[] getPopulationByZone () {
        // summarize population by zone

        int[] pp = new int[geoData.getHighestZonalId() + 1];
        for (Household hh: Household.getHouseholdArray()) {
            int zone = Dwelling.getDwellingFromId(hh.getDwellingId()).getZone();
            pp[zone] += hh.getHhSize();
        }
        return pp;
    }


    public static int[] getHouseholdsByZone () {
        // summarize households by zone

        int[] householdsByZone = new int[geoData.getHighestZonalId() + 1];
        for (Household hh: Household.getHouseholdArray()) {
            int zone = Dwelling.getDwellingFromId(hh.getDwellingId()).getZone();
            householdsByZone[zone]++;
        }
        return householdsByZone;
    }


    public static int[] getRetailEmploymentByZone() {
        // summarize retail employment by zone

        int[] retailEmplByZone = new int[geoData.getHighestZonalId() + 1];
        for (Job jj: Job.getJobArray()) {
            if (jj.getType().equals("RET")) retailEmplByZone[geoData.getZoneIndex(jj.getZone())]++;
        }
        return retailEmplByZone;
    }


    public static int[] getOtherEmploymentByZone() {
        // summarize other employment by zone

        int[] otherEmplByZone = new int[geoData.getHighestZonalId() + 1];
        for (Job jj: Job.getJobArray()) {
            if (jj.getType().equals("OTH")) otherEmplByZone[geoData.getZoneIndex(jj.getZone())]++;
        }
        return otherEmplByZone;
    }


    public static int[] getTotalEmploymentByZone() {
        // summarize retail employment by zone

        int[] totalEmplByZone = new int[geoData.getHighestZonalId() + 1];
        for (Job jj: Job.getJobArray()) {
            totalEmplByZone[geoData.getZoneIndex(jj.getZone())]++;
        }
        return totalEmplByZone;
    }


    public static void scaleMicroDataToExogenousForecast (ResourceBundle rb, int year, HouseholdDataManager householdData) {
        // scale synthetic population to exogenous forecast (for output only, scaled synthetic population is not used internally)

        if (!scalingControlTotals.containsColumn(("HH" + year))) {
            logger.warn("Could not find scaling targets to scale micro data to year " + year + ". No scaling completed.");
            return;
        }
        logger.info("Scaling synthetic population to exogenous forecast for year " + year + " (for output only, " +
                "scaled population is not used internally).");

        int artificialHhId = HouseholdDataManager.getHighestHouseholdIdInUse() + 1;
        int artificialPpId = HouseholdDataManager.getHighestPersonIdInUse() + 1;

        // calculate how many households need to be created or deleted in every zone
        int[] changeOfHh = new int[(geoData.getHighestZonalId() + 1)];
        HashMap<Integer, int[]> hhByZone = householdData.getHouseholdsByZone();
        for (int zone: geoData.getZones()) {
            int hhs = 0;
            if (hhByZone.containsKey(zone)) hhs = hhByZone.get(zone).length;
            changeOfHh[zone] =
                    (int) scalingControlTotals.getIndexedValueAt(zone, ("HH" + year)) - hhs;
        }

        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(rb.getString(PROPERTIES_SCALED_MICRO_DATA_HH) + year + ".csv", false);
        pwh.println("id,dwelling,zone,hhSize,autos");
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(rb.getString(PROPERTIES_SCALED_MICRO_DATA_PP) + year + ".csv", false);
        pwp.println("id,hhID,age,gender,race,occupation,driversLicense,workplace,income");
        for (int zone: geoData.getZones()) {
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
                Household hh = Household.getHouseholdFromId(hhInThisZone[i]);
                if (changeOfHh[zone] > 0) {
                    // write out original household
                    pwh.print(hh.getId());
                    pwh.print(",");
                    pwh.print(hh.getDwellingId());
                    pwh.print(",");
                    pwh.print(hh.getHomeZone());
                    pwh.print(",");
                    pwh.print(hh.getHhSize());
                    pwh.print(",");
                    pwh.println(hh.getAutos());
                    for (Person pp: hh.getPersons()) {
                        pwp.print(pp.getId());
                        pwp.print(",");
                        pwp.print(pp.getHhId());
                        pwp.print(",");
                        pwp.print(pp.getAge());
                        pwp.print(",");
                        pwp.print(pp.getGender());
                        pwp.print(",");
                        pwp.print(pp.getRace());
                        pwp.print(",");
                        pwp.print(pp.getOccupation());
                        pwp.print(",0,");
                        pwp.print(pp.getWorkplace());
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
                            pwh.print(hh.getHomeZone());
                            pwh.print(",");
                            pwh.print(hh.getHhSize());
                            pwh.print(",");
                            pwh.println(hh.getAutos());
                            for (Person pp: hh.getPersons()) {
                                pwp.print(artificialPpId);
                                pwp.print(",");
                                pwp.print(artificialHhId);
                                pwp.print(",");
                                pwp.print(pp.getAge());
                                pwp.print(",");
                                pwp.print(pp.getGender());
                                pwp.print(",");
                                pwp.print(pp.getRace());
                                pwp.print(",");
                                pwp.print(pp.getOccupation());
                                pwp.print(",0,");
                                pwp.print(pp.getWorkplace());
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
                        pwh.print(hh.getHomeZone());
                        pwh.print(",");
                        pwh.print(hh.getHhSize());
                        pwh.print(",");
                        pwh.println(hh.getAutos());
                        for (Person pp: hh.getPersons()) {
                            pwp.print(pp.getId());
                            pwp.print(",");
                            pwp.print(pp.getHhId());
                            pwp.print(",");
                            pwp.print(pp.getAge());
                            pwp.print(",");
                            pwp.print(pp.getGender());
                            pwp.print(",");
                            pwp.print(pp.getRace());
                            pwp.print(",");
                            pwp.print(pp.getOccupation());
                            pwp.print(",0,");
                            pwp.print(pp.getWorkplace());
                            pwp.print(",");
                            pwp.println(pp.getIncome());
                        }
                    }
                }
            }
            } else {
                if (scalingControlTotals.getIndexedValueAt(zone, ("HH" + year)) > 0) logger.warn("SILO has no households in zone " +
                        zone + " that could be duplicated to match control total of " +
                        scalingControlTotals.getIndexedValueAt(zone, ("HH" + year)) + ".");
            }
        }
        pwh.close();
        pwp.close();
    }


    public static void summarizeHousing (ResourceBundle rb, int year) {
        // summarize housing data for housing environmental impact calculations

        if (!SiloUtil.containsElement(ResourceUtil.getIntegerArray(rb, PROPERTIES_BEM_YEARS), year)) return;
        String directory = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/bem/";
        SiloUtil.createDirectoryIfNotExistingYet(directory);

        String fileName = (directory + rb.getString(PROPERTIES_HOUSING_SUMMARY) + "_" + year + "_" +
                SiloUtil.gregorianIterator + ".csv");

        PrintWriter pw = SiloUtil.openFileForSequentialWriting(fileName, false);
        pw.println("id,zone,type,size,yearBuilt,occupied");
        for (Dwelling dd: Dwelling.getDwellingArray()){
            pw.print(dd.getId());
            pw.print(",");
            pw.print(dd.getZone());
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


    public static void writeOutSyntheticPopulation (ResourceBundle rb) {
        // write out files with synthetic population

        logger.info("  Writing household file");
        String filehh = SiloUtil.baseDirectory + rb.getString(PROPERTIES_FILENAME_HH_MICRODATA) + "_" +
                SiloUtil.getEndYear() + ".csv";
        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(filehh, false);
        pwh.println("id,dwelling,zone,hhSize,autos");
        Household[] hhs = Household.getHouseholdArray();
        for (Household hh : hhs) {
            if (hh.getId() == SiloUtil.trackHh) {
                SiloUtil.trackingFile("Writing hh " + hh.getId() + " to micro data file.");
                hh.logAttributes(SiloUtil.trackWriter);
            }
            pwh.print(hh.getId());
            pwh.print(",");
            pwh.print(hh.getDwellingId());
            pwh.print(",");
            pwh.print(hh.getHomeZone());
            pwh.print(",");
            pwh.print(hh.getHhSize());
            pwh.print(",");
            pwh.println(hh.getAutos());
        }
        pwh.close();

        logger.info("  Writing person file");
        String filepp = SiloUtil.baseDirectory + rb.getString(PROPERTIES_FILENAME_PP_MICRODATA) + "_" +
                SiloUtil.getEndYear() + ".csv";
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(filepp, false);
        pwp.println("id,hhID,age,gender,relationShip,race,occupation,driversLicense,workplace,income");
        Person[] pps = Person.getPersonArray();
        for (Person pp : pps) {
            pwp.print(pp.getId());
            pwp.print(",");
            pwp.print(pp.getHhId());
            pwp.print(",");
            pwp.print(pp.getAge());
            pwp.print(",");
            pwp.print(pp.getGender());
            pwp.print(",\"");
            pwp.print(pp.getRole());
            pwp.print("\",\"");
            pwp.print(pp.getRace());
            pwp.print("\",");
            pwp.print(pp.getOccupation());
            pwp.print(",0,");
            pwp.print(pp.getWorkplace());
            pwp.print(",");
            pwp.println(pp.getIncome());
            if (pp.getId() == SiloUtil.trackPp) {
                SiloUtil.trackingFile("Writing pp " + pp.getId() + " to micro data file.");
                pp.logAttributes(SiloUtil.trackWriter);
            }
        }
        pwp.close();

        logger.info("  Writing dwelling file");
        String filedd = SiloUtil.baseDirectory + rb.getString(PROPERTIES_FILENAME_DD_MICRODATA) + "_" +
                SiloUtil.getEndYear() + ".csv";
        PrintWriter pwd = SiloUtil.openFileForSequentialWriting(filedd, false);
        pwd.println("id,zone,type,hhID,bedrooms,quality,monthlyCost,restriction,yearBuilt");
        Dwelling[] dds = Dwelling.getDwellingArray();
        for (Dwelling dd : dds) {
            pwd.print(dd.getId());
            pwd.print(",");
            pwd.print(dd.getZone());
            pwd.print(",\"");
            pwd.print(dd.getType());
            pwd.print("\",");
            pwd.print(dd.getResidentId());
            pwd.print(",");
            pwd.print(dd.getBedrooms());
            pwd.print(",");
            pwd.print(dd.getQuality());
            pwd.print(",");
            pwd.print(dd.getPrice());
            pwd.print(",");
            pwd.print(dd.getRestriction());
            pwd.print(",");
            pwd.println(dd.getYearBuilt());
            if (dd.getId() == SiloUtil.trackDd) {
                SiloUtil.trackingFile("Writing dd " + dd.getId() + " to micro data file.");
                dd.logAttributes(SiloUtil.trackWriter);
            }
        }
        pwd.close();

        logger.info("  Writing job file");
        String filejj = SiloUtil.baseDirectory + rb.getString(PROPERTIES_FILENAME_JJ_MICRODATA) + "_" +
                SiloUtil.getEndYear() + ".csv";
        PrintWriter pwj = SiloUtil.openFileForSequentialWriting(filejj, false);
        pwj.println("id,zone,personId,type");
        Job[] jjs = Job.getJobArray();
        for (Job jj : jjs) {
            pwj.print(jj.getId());
            pwj.print(",");
            pwj.print(jj.getZone());
            pwj.print(",");
            pwj.print(jj.getWorkerId());
            pwj.print(",\"");
            pwj.print(jj.getType());
            pwj.println("\"");
            if (jj.getId() == SiloUtil.trackJj) {
                SiloUtil.trackingFile("Writing jj " + jj.getId() + " to micro data file.");
                jj.logAttributes(SiloUtil.trackWriter);
            }
        }
        pwj.close();

        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_WRITE_BIN_POP_FILES))
            HouseholdDataManager.writeBinaryPopulationDataObjects(rb);
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_WRITE_BIN_DD_FILE))
            RealEstateDataManager.writeBinaryDwellingDataObjects(rb);
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_WRITE_BIN_JJ_FILE))
            JobDataManager.writeBinaryJobDataObjects(rb);
    }


    public static void summarizeAutoOwnershipByCounty() {
        // This calibration function summarized households by auto-ownership and quits

        PrintWriter pwa = SiloUtil.openFileForSequentialWriting("autoOwnershipA.csv", false);
        pwa.println("hhSize,workers,income,transit,density,autos");
        int[][] autos = new int[4][60000];
        for (Household hh: Household.getHouseholdArray()) {
            int autoOwnership = hh.getAutos();
            int zone = hh.getHomeZone();
            int county = geoData.getCountyOfZone(zone);
            autos[autoOwnership][county]++;
            pwa.println(hh.getHhSize()+","+hh.getNumberOfWorkers()+","+hh.getHhIncome()+","+
                    Accessibility.getTransitAccessibility(zone)+","+JobDataManager.getJobDensityInZone(zone)+","+hh.getAutos());
        }
        pwa.close();

        PrintWriter pw = SiloUtil.openFileForSequentialWriting("autoOwnershipB.csv", false);
        pw.println("County,0autos,1auto,2autos,3+autos");
        for (int county = 0; county < 60000; county++) {
            int sm = 0;
            for (int a = 0; a < 4; a++) sm += autos[a][county];
            if (sm > 0) pw.println(county+","+autos[0][county]+","+autos[1][county]+","+autos[2][county]+","+autos[3][county]);
        }
        pw.close();
        logger.info("Summarized auto ownership and quit.");
        System.exit(0);
    }


    public static void preparePrestoSummary (ResourceBundle rb) {
        // open PRESTO summary file

        String prestoZoneFile = SiloUtil.baseDirectory + rb.getString(PROPERTIES_PRESTO_REGION_DEFINITION);
        TableDataSet regionDefinition = SiloUtil.readCSVfile(prestoZoneFile);
        regionDefinition.buildIndex(regionDefinition.getColumnPosition("aggFips"));

        prestoRegionByTaz = SiloUtil.createArrayWithValue((geoData.getHighestZonalId() + 1), -1);
        for (int zone: geoData.getZones()) {
            try {
                prestoRegionByTaz[zone] =
                        (int) regionDefinition.getIndexedValueAt(geoData.getCountyOfZone(zone), "presto");
            } catch (Exception e) {
                prestoRegionByTaz[zone] = -1;
            }
        }
    }


    public static void summarizePrestoRegion (ResourceBundle rb, int year) {
        // summarize housing costs by income group in SILO region

        String fileName = (SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName + "/" +
                rb.getString(PROPERTIES_PRESTO_SUMMARY_FILE) + SiloUtil.gregorianIterator + ".csv");
        PrintWriter pw = SiloUtil.openFileForSequentialWriting(fileName, year != SiloUtil.getBaseYear());
        pw.println(year + ",Housing costs by income group");
        pw.print("Income");
        for (int i = 0; i < 10; i++) pw.print(",rent_" + ((i + 1) * 250));
        pw.println(",averageRent");
        int[][] rentByIncome = new int[10][10];
        int[] rents = new int[10];
        for (Household hh: Household.getHouseholdArray()) {
            if (prestoRegionByTaz[hh.getHomeZone()] > 0) {
                int hhInc = hh.getHhIncome();
                int rent = Dwelling.getDwellingFromId(hh.getDwellingId()).getPrice();
                int incCat = Math.min((hhInc / 10000), 9);
                int rentCat = Math.min((rent / 250), 9);
                rentByIncome[incCat][rentCat]++;
                rents[incCat] += rent;
            }
        }
        for (int i = 0; i < 10; i++) {
            pw.print(String.valueOf((i + 1) * 10000));
            int countThisIncome = 0;
            for (int r = 0; r < 10; r++) {
                pw.print("," + rentByIncome[i][r]);
                countThisIncome += rentByIncome[i][r];
            }
            pw.println("," + rents[i] / countThisIncome);
        }
    }


    public static void writeOutDevelopmentCapacityFile (ResourceBundle rb, RealEstateDataManager realEstateData) {
        // write out development capacity file to allow model run to be continued from this point later

        String capacityFileName = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName +
                ResourceUtil.getProperty(rb, PROPERTIES_CAPACITY_FILE) + "_" + SiloUtil.getEndYear() + ".csv";
        PrintWriter pwc = SiloUtil.openFileForSequentialWriting(capacityFileName, false);
        pwc.println("Zone,DevCapacity");
        for (int zone: geoData.getZones()) pwc.println(zone + "," + realEstateData.getDevelopmentCapacity(zone));
        pwc.close();
        String landUseFileName = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName +
                ResourceUtil.getProperty(rb, PROPERTIES_LAND_USE_AREA) + "_" + SiloUtil.getEndYear() + ".csv";
        PrintWriter pwl = SiloUtil.openFileForSequentialWriting(landUseFileName, false);
        pwl.println("Zone,lu41");
        for (int zone: geoData.getZones()) pwl.println(zone + "," + realEstateData.getDevelopableLand(zone));
        pwl.close();
    }
}
