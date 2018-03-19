package de.tum.bgu.msm.data;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.maryland.GeoDataMstm;
import de.tum.bgu.msm.data.maryland.MstmZone;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.util.matrices.Matrices;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Methods to summarize model results
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 23 February 2012 in Albuquerque
 **/


public class SummarizeData {
    static Logger logger = Logger.getLogger(SummarizeData.class);

    protected static final String PROPERTIES_RESULT_FILE_NAME             = "result.file.name";

    protected static final String PROPERTIES_USE_CAPACITY   = "use.growth.capacity.data";

    private static PrintWriter resultWriter;
    private static PrintWriter spatialResultWriter;

    private static PrintWriter resultWriterFinal;
    private static PrintWriter spatialResultWriterFinal;

    public static Boolean resultWriterReplicate = false;

    private static TableDataSet scalingControlTotals;
    private static int[] prestoRegionByTaz;

    public static void openResultFile(ResourceBundle rb) {
        // open summary file

        String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
        SiloUtil.createDirectoryIfNotExistingYet(directory);
        String resultFileName = rb.getString(PROPERTIES_RESULT_FILE_NAME);
        resultWriter = SiloUtil.openFileForSequentialWriting(directory + "/" + resultFileName +
                Properties.get().main.gregorianIterator + ".csv", Properties.get().main.startYear != Properties.get().main.implementation.BASE_YEAR);
        resultWriterFinal = SiloUtil.openFileForSequentialWriting(directory + "/" + resultFileName + "_" + Properties.get().main.endYear + ".csv", false);
    }


    public static void readScalingYearControlTotals () {
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
                if(resultWriterReplicate && writeFinal)resultWriterFinal.println(action);
                break;
        }
    }

    public static void resultFileSpatial(String action) {
        resultFileSpatial(action,true);
    }
    public static void resultFileSpatial(String action, Boolean writeFinal) {
        // handle summary file
        switch (action) {
            case "open":
                String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
                SiloUtil.createDirectoryIfNotExistingYet(directory);
                String resultFileName = Properties.get().main.spatialResultFileName;
                spatialResultWriter = SiloUtil.openFileForSequentialWriting(directory + "/" + resultFileName +
                        Properties.get().main.gregorianIterator + ".csv", Properties.get().main.startYear != Properties.get().main.implementation.BASE_YEAR);
                spatialResultWriterFinal = SiloUtil.openFileForSequentialWriting(directory + "/" + resultFileName +"_"+ Properties.get().main.endYear + ".csv", false);
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

    public static void summarizeSpatially (int year, SiloModelContainer modelContainer, SiloDataContainer dataContainer) {
        // write out results by zone

        String hd = "Year" + year + ",autoAccessibility,transitAccessibility,population,households,hhInc_<" + Properties.get().main.incomeBrackets[0];
        for (int inc = 0; inc < Properties.get().main.incomeBrackets.length; inc++) hd = hd.concat(",hhInc_>" + Properties.get().main.incomeBrackets[inc]);
        resultFileSpatial(hd + ",dd_SFD,dd_SFA,dd_MF234,dd_MF5plus,dd_MH,availLand,avePrice,jobs,shWhite,shBlack,shHispanic,shOther");

        final int highestZonalId = dataContainer.getGeoData().getZones().keySet()
                .stream().mapToInt(Integer::intValue).max().getAsInt();
        int[][] dds = new int[DwellingType.values().length + 1][highestZonalId + 1];
        int[] prices = new int[highestZonalId + 1];
        int[] jobs = new int[highestZonalId + 1];
        int[] hhs = new int[highestZonalId + 1];
        int[][] hhInc = new int[Properties.get().main.incomeBrackets.length + 1][highestZonalId + 1];
        DoubleMatrix1D pop = getPopulationByZone(dataContainer);
        for (Household hh: dataContainer.getHouseholdData().getHouseholds()) {
            int zone = dataContainer.getRealEstateData().getDwelling(hh.getDwellingId()).getZone();
            int incGroup = HouseholdDataManager.getIncomeCategoryForIncome(hh.getHhIncome());
            hhInc[incGroup - 1][zone]++;
            hhs[zone] ++;
        }
        for (Dwelling dd: dataContainer.getRealEstateData().getDwellings()) {
            dds[dd.getType().ordinal()][dd.getZone()]++;
            prices[dd.getZone()] += dd.getPrice();
        }
        for (Job jj: dataContainer.getJobData().getJobs()) {
            jobs[jj.getZone()]++;
        }


        for (int taz: dataContainer.getGeoData().getZones().keySet()) {
            float avePrice = -1;
            int ddThisZone = 0;
            for (DwellingType dt: DwellingType.values()) ddThisZone += dds[dt.ordinal()][taz];
            if (ddThisZone > 0) avePrice = prices[taz] / ddThisZone;
            double autoAcc = modelContainer.getAcc().getAutoAccessibilityForZone(taz);
            double transitAcc = modelContainer.getAcc().getTransitAccessibilityForZone(taz);
            double availLand = dataContainer.getRealEstateData().getAvailableLandForConstruction(taz);
//            Formatter f = new Formatter();
//            f.format("%d,%f,%f,%d,%d,%d,%f,%f,%d", taz, autoAcc, transitAcc, pop[taz], hhs[taz], dds[taz], availLand, avePrice, jobs[taz]);
            String txt = taz + "," + autoAcc + "," + transitAcc + "," + pop.getQuick(taz) + "," + hhs[taz];
            for (int inc = 0; inc <= Properties.get().main.incomeBrackets.length; inc++) txt = txt.concat("," + hhInc[inc][taz]);
            for (DwellingType dt: DwellingType.values()) txt = txt.concat("," + dds[dt.ordinal()][taz]);
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

    public static DoubleMatrix1D getPopulationByZone (SiloDataContainer dataContainer) {
        DoubleMatrix1D popByZone = Matrices.doubleMatrix1D(dataContainer.getGeoData().getZones().values());
        for (Household hh: dataContainer.getHouseholdData().getHouseholds()) {
            final int zone = dataContainer.getRealEstateData().getDwelling(hh.getDwellingId()).getZone();
            popByZone.setQuick(zone, popByZone.getQuick(zone) + hh.getHhSize());
        }
        return popByZone;
    }


    public static void scaleMicroDataToExogenousForecast (int year, SiloDataContainer dataContainer) {
        //TODO Will fail for new zones with 0 households and a projected growth. Could be an issue when modeling for Zones with transient existence.
        // scale synthetic population to exogenous forecast (for output only, scaled synthetic population is not used internally)

        if (!scalingControlTotals.containsColumn(("HH" + year))) {
            logger.warn("Could not find scaling targets to scale micro data to year " + year + ". No scaling completed.");
            return;
        }
        logger.info("Scaling synthetic population to exogenous forecast for year " + year + " (for output only, " +
                "scaled population is not used internally).");

        int artificialHhId = dataContainer.getHouseholdData().getHighestHouseholdIdInUse() + 1;
        int artificialPpId = dataContainer.getHouseholdData().getHighestPersonIdInUse() + 1;

        // calculate how many households need to be created or deleted in every zone
        final int highestId = dataContainer.getGeoData().getZones().keySet()
                .stream().mapToInt(Integer::intValue).max().getAsInt();
        int[] changeOfHh = new int[highestId + 1];
        HashMap<Integer, int[]> hhByZone = dataContainer.getHouseholdData().getHouseholdsByZone();
        for (int zone: dataContainer.getGeoData().getZones().keySet()) {
            int hhs = 0;
            if (hhByZone.containsKey(zone)) hhs = hhByZone.get(zone).length;
            changeOfHh[zone] =
                    (int) scalingControlTotals.getIndexedValueAt(zone, ("HH" + year)) - hhs;
        }

        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(Properties.get().main.scaledMicroDataHh + year + ".csv", false);
        pwh.println("id,dwelling,zone,hhSize,autos");
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(Properties.get().main.scaledMicroDataPp + year + ".csv", false);
        pwp.println("id,hhID,age,gender,race,occupation,driversLicense,workplace,income");

        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        for (int zone: dataContainer.getGeoData().getZones().keySet()) {
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
                    Household hh = householdData.getHouseholdFromId(hhInThisZone[i]);
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
                        for (Person pp: hh.getPersons()) {
                            pwp.print(pp.getId());
                            pwp.print(",");
                            pwp.print(pp.getHh().getId());
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
                                pwh.print(zone);
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
                            pwh.print(zone);
                            pwh.print(",");
                            pwh.print(hh.getHhSize());
                            pwh.print(",");
                            pwh.println(hh.getAutos());
                            for (Person pp: hh.getPersons()) {
                                pwp.print(pp.getId());
                                pwp.print(",");
                                pwp.print(pp.getHh().getId());
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


    public static void summarizeHousing (int year, SiloDataContainer dataContainer) {
        // summarize housing data for housing environmental impact calculations

        if (!SiloUtil.containsElement(Properties.get().main.bemModelYears, year)) return;
        String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName + "/bem/";
        SiloUtil.createDirectoryIfNotExistingYet(directory);

        String fileName = (directory + Properties.get().main.housingEnvironmentImpactFile + "_" + year + "_" +
                Properties.get().main.gregorianIterator + ".csv");

        PrintWriter pw = SiloUtil.openFileForSequentialWriting(fileName, false);
        pw.println("id,zone,type,size,yearBuilt,occupied");
        for (Dwelling dd: dataContainer.getRealEstateData().getDwellings()){
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


    public static void writeOutSyntheticPopulation (int year, SiloDataContainer dataContainer) {
        // write out files with synthetic population

        logger.info("  Writing household file");
        String filehh = Properties.get().main.baseDirectory + Properties.get().householdData.householdFileName + "_" +
                year + ".csv";
        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(filehh, false);
        pwh.println("id,dwelling,zone,hhSize,autos");
        for (Household hh : dataContainer.getHouseholdData().getHouseholds()) {
            if (hh.getId() == SiloUtil.trackHh) {
                SiloUtil.trackingFile("Writing hh " + hh.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(hh.toString());
            }
            pwh.print(hh.getId());
            pwh.print(",");
            pwh.print(hh.getDwellingId());
            pwh.print(",");
            int zone = -1;
            Dwelling dwelling = dataContainer.getRealEstateData().getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                zone = dwelling.getZone();
            }
            pwh.print(zone);
            pwh.print(",");
            pwh.print(hh.getHhSize());
            pwh.print(",");
            pwh.println(hh.getAutos());
        }
        pwh.close();

        logger.info("  Writing person file");
        String filepp = Properties.get().main.baseDirectory + Properties.get().householdData.personFileName + "_" +
                year + ".csv";
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(filepp, false);
        pwp.println("id,hhID,age,gender,relationShip,race,occupation,driversLicense,workplace,income");
        for (Person pp : dataContainer.getHouseholdData().getPersons()) {
            pwp.print(pp.getId());
            pwp.print(",");
            pwp.print(pp.getHh().getId());
            pwp.print(",");
            pwp.print(pp.getAge());
            pwp.print(",");
            pwp.print(pp.getGender());
            pwp.print(",\"");
            String role = pp.getRole().toString();
            pwp.print(role);
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
                SiloUtil.trackWriter.println(pp.toString());
            }
        }
        pwp.close();

        logger.info("  Writing dwelling file");
        String filedd = Properties.get().main.baseDirectory + Properties.get().householdData.dwellingsFileName + "_" +
                year + ".csv";
        PrintWriter pwd = SiloUtil.openFileForSequentialWriting(filedd, false);
        pwd.println("id,zone,type,hhID,bedrooms,quality,monthlyCost,restriction,yearBuilt");
        for (Dwelling dd : dataContainer.getRealEstateData().getDwellings()) {
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
                SiloUtil.trackWriter.println(dd.toString());
            }
        }
        pwd.close();

/*        logger.info ("  Reading dwelling file that was written (for debugging only");
        String recString = "";
        int recCount = 0;
        try {
            File file = new File(filedd);
            if (file.exists()) {
                BufferedReader in = new BufferedReader(new FileReader(file));
                while ((recString = in.readLine()) != null) {
                    recCount++;
                    System.out.println(recCount+" <"+recString+">");
                }
            } else {
                System.out.println("Did not find file " + filedd);
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop dwelling file: " + filedd);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }*/



        logger.info("  Writing job file");
        String filejj = Properties.get().main.baseDirectory + Properties.get().householdData.jobsFileName + "_" +
                year + ".csv";
        PrintWriter pwj = SiloUtil.openFileForSequentialWriting(filejj, false);
        pwj.println("id,zone,personId,type");
        for (Job jj : dataContainer.getJobData().getJobs()) {
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
                SiloUtil.trackWriter.println(jj.toString());
            }
        }
        pwj.close();

        if (Properties.get().householdData.writeBinPopFile) {
            dataContainer.getHouseholdData().writeBinaryPopulationDataObjects();
        }
        if (Properties.get().householdData.writeBinDwellingsFile) {
            dataContainer.getRealEstateData().writeBinaryDwellingDataObjects();
        }
        if (Properties.get().householdData.writeBinJobFile) {
            dataContainer.getJobData().writeBinaryJobDataObjects();
        }
    }


    public static void writeOutSyntheticPopulationDE (int year, SiloDataContainer dataContainer) {
        // write out files with synthetic population

        logger.info("  Writing household file");
        String filehh = Properties.get().main.baseDirectory + Properties.get().householdData.householdFileName + "_" +
                year + ".csv";
        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(filehh, false);
        pwh.println("id,dwelling,zone,hhSize,autos");
        RealEstateDataManager realEstate = dataContainer.getRealEstateData();
        for (Household hh : dataContainer.getHouseholdData().getHouseholds()) {
            if (hh.getId() == SiloUtil.trackHh) {
                SiloUtil.trackingFile("Writing hh " + hh.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(hh.toString());
            }
            pwh.print(hh.getId());
            pwh.print(",");
            pwh.print(hh.getDwellingId());
            pwh.print(",");
            int zone = -1;
            Dwelling dwelling = realEstate.getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                zone = dwelling.getZone();
            }
            pwh.print(zone);
            pwh.print(",");
            pwh.print(hh.getHhSize());
            pwh.print(",");
            pwh.println(hh.getAutos());

        }
        pwh.close();

        logger.info("  Writing person file");
        String filepp = Properties.get().main.baseDirectory + Properties.get().householdData.personFileName + "_" +
                year + ".csv";
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(filepp, false);
        pwp.println("id,hhid,age,gender,relationShip,race,occupation,workplace,income,nationality,education,homeZone,workZone,driversLicense,schoolDE,schoolTAZ");
        for (Person pp : dataContainer.getHouseholdData().getPersons()) {
            pwp.print(pp.getId());
            pwp.print(",");
            pwp.print(pp.getHh().getId());
            pwp.print(",");
            pwp.print(pp.getAge());
            pwp.print(",");
            pwp.print(pp.getGender());
            pwp.print(",\"");
            pwp.print(pp.getRole().toString());
            pwp.print("\",\"");
            pwp.print(pp.getRace());
            pwp.print("\",");
            pwp.print(pp.getOccupation());
            pwp.print(",");
            pwp.print(pp.getWorkplace());
            pwp.print(",");
            pwp.print(pp.getIncome());
            pwp.print(",");
            pwp.print(pp.getNationality());
            pwp.print(",");
            pwp.print(pp.getEducationLevel());
            pwp.print(",");
            int zone = -1;
            Dwelling dwelling = realEstate.getDwelling(pp.getHh().getDwellingId());
            if(dwelling != null) {
                zone = dwelling.getZone();
            }
            pwp.print(zone);
            pwp.print(",");
            pwp.print(pp.getJobTAZ());
            pwp.print(",");
            pwp.print(pp.hasDriverLicense());
            pwp.print(",");
            pwp.print(pp.getSchoolType());
            pwp.print(",");
            pwp.println(pp.getSchoolPlace());
            if (pp.getId() == SiloUtil.trackPp) {
                SiloUtil.trackingFile("Writing pp " + pp.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(pp.toString());
            }
        }
        pwp.close();

        logger.info("  Writing dwelling file");
        String filedd = Properties.get().main.baseDirectory + Properties.get().householdData.dwellingsFileName + "_" +
                year + ".csv";
        PrintWriter pwd = SiloUtil.openFileForSequentialWriting(filedd, false);
        pwd.println("id,zone,type,hhID,bedrooms,quality,monthlyCost,restriction,yearBuilt,floor,building,year,usage");
        for (Dwelling dd : realEstate.getDwellings()) {
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
            pwd.print(dd.getYearBuilt());
            pwd.print(",");
            pwd.print(dd.getFloorSpace());
            pwd.print(",");
            pwd.print(dd.getBuildingSize());
            pwd.print(",");
            pwd.print(dd.getYearConstructionDE());
            pwd.print(",");
            int use = 1;
            if (dd.getUsage().equals(Dwelling.Usage.RENTED)){
                use = 2;
            } else if (dd.getUsage().equals(Dwelling.Usage.VACANT)){
                use = 3;
            }
            pwd.println(use);
            if (dd.getId() == SiloUtil.trackDd) {
                SiloUtil.trackingFile("Writing dd " + dd.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(dd.toString());
            }
        }
        pwd.close();

        logger.info("  Writing job file");
        String filejj = Properties.get().main.baseDirectory + Properties.get().householdData.jobsFileName + "_" +
                year + ".csv";
        PrintWriter pwj = SiloUtil.openFileForSequentialWriting(filejj, false);
        pwj.println("id,zone,personId,type");
        for (Job jj : dataContainer.getJobData().getJobs()) {
            pwj.print(jj.getId());
            pwj.print(",");
            pwj.print(jj.getZone());
            pwj.print(",");
            pwj.print(jj.getWorkerId());
            pwj.print(",");
            pwj.println(jj.getType());
            if (jj.getId() == SiloUtil.trackJj) {
                SiloUtil.trackingFile("Writing jj " + jj.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(jj.toString());
            }
        }
        pwj.close();

/*
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_WRITE_BIN_POP_FILES))
            HouseholdDataManager.writeBinaryPopulationDataObjects(rb);
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_WRITE_BIN_DD_FILE))
            RealEstateDataManager.writeBinaryDwellingDataObjects(rb);
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_WRITE_BIN_JJ_FILE))
            JobDataManager.writeBinaryJobDataObjects(rb);
*/

    }



    public static void writeOutSyntheticPopulationDE (int year, String file, SiloDataContainer dataContainer) {
        // write out files with synthetic population

        logger.info("  Writing household file");
        String filehh = Properties.get().main.baseDirectory + Properties.get().householdData.householdFileName + file +
                year + ".csv";
        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(filehh, false);
        pwh.println("id,dwelling,zone,hhSize,autos");
        RealEstateDataManager realEstate = dataContainer.getRealEstateData();
        for (Household hh : dataContainer.getHouseholdData().getHouseholds()) {
            if (hh.getId() == SiloUtil.trackHh) {
                SiloUtil.trackingFile("Writing hh " + hh.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(hh.toString());
            }
            pwh.print(hh.getId());
            pwh.print(",");
            pwh.print(hh.getDwellingId());
            pwh.print(",");
            int zone = -1;
            Dwelling dwelling = realEstate.getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                zone = dwelling.getZone();
            }
            pwh.print(zone);
            pwh.print(",");
            pwh.print(hh.getHhSize());
            pwh.print(",");
            pwh.println(hh.getAutos());

        }
        pwh.close();

        logger.info("  Writing person file");
        String filepp = Properties.get().main.baseDirectory + Properties.get().householdData.personFileName + file +
                year + ".csv";
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(filepp, false);
        pwp.println("id,hhid,age,gender,relationShip,race,occupation,workplace,income,nationality,education,homeZone,workZone,driversLicense,schoolDE,schoolplace,autos,trips");
        for (Person pp : dataContainer.getHouseholdData().getPersons()) {
            pwp.print(pp.getId());
            pwp.print(",");
            pwp.print(pp.getHh().getId());
            pwp.print(",");
            pwp.print(pp.getAge());
            pwp.print(",");
            pwp.print(pp.getGender());
            pwp.print(",\"");
            pwp.print(pp.getRole().toString());
            pwp.print("\",\"");
            pwp.print(pp.getRace());
            pwp.print("\",");
            pwp.print(pp.getOccupation());
            pwp.print(",");
            pwp.print(pp.getWorkplace());
            pwp.print(",");
            pwp.print(pp.getIncome());
            pwp.print(",");
            pwp.print(pp.getNationality());
            pwp.print(",");
            pwp.print(pp.getEducationLevel());
            pwp.print(",");
            int zone = -1;
            Dwelling dwelling = realEstate.getDwelling(pp.getHh().getDwellingId());
            if(dwelling != null) {
                zone = dwelling.getZone();
            }
            pwp.print(zone);
            pwp.print(",");
            pwp.print(pp.getJobTAZ());
            pwp.print(",");
            pwp.print(pp.hasDriverLicense());
            pwp.print(",");
            pwp.print(pp.getSchoolType());
            pwp.print(",");
            pwp.print(pp.getSchoolPlace());
            pwp.print(",");
            pwp.print(pp.getHh().getAutos());
            pwp.print(",");
            pwp.println(pp.getTelework());
            if (pp.getId() == SiloUtil.trackPp) {
                SiloUtil.trackingFile("Writing pp " + pp.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(pp.toString());
            }
        }
        pwp.close();

        logger.info("  Writing dwelling file");
        String filedd = Properties.get().main.baseDirectory + Properties.get().householdData.dwellingsFileName + file +
                year + ".csv";
        PrintWriter pwd = SiloUtil.openFileForSequentialWriting(filedd, false);
        pwd.println("id,zone,type,hhID,bedrooms,quality,monthlyCost,restriction,yearBuilt,floor,building,year,usage");
        for (Dwelling dd : realEstate.getDwellings()) {
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
            pwd.print(dd.getYearBuilt());
            pwd.print(",");
            pwd.print(dd.getFloorSpace());
            pwd.print(",");
            pwd.print(dd.getBuildingSize());
            pwd.print(",");
            pwd.print(dd.getYearConstructionDE());
            pwd.print(",");
            pwd.println(dd.getUsage());
            if (dd.getId() == SiloUtil.trackDd) {
                SiloUtil.trackingFile("Writing dd " + dd.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(dd.toString());
            }
        }
        pwd.close();

        logger.info("  Writing job file");
        String filejj = Properties.get().main.baseDirectory + Properties.get().householdData.jobsFileName + file +
                year + ".csv";
        PrintWriter pwj = SiloUtil.openFileForSequentialWriting(filejj, false);
        pwj.println("id,zone,personId,type");
        for (Job jj : dataContainer.getJobData().getJobs()) {
            pwj.print(jj.getId());
            pwj.print(",");
            pwj.print(jj.getZone());
            pwj.print(",");
            pwj.print(jj.getWorkerId());
            pwj.print(",");
            pwj.println(jj.getType());
            if (jj.getId() == SiloUtil.trackJj) {
                SiloUtil.trackingFile("Writing jj " + jj.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(jj.toString());
            }
        }
        pwj.close();

/*
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_WRITE_BIN_POP_FILES))
            HouseholdDataManager.writeBinaryPopulationDataObjects(rb);
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_WRITE_BIN_DD_FILE))
            RealEstateDataManager.writeBinaryDwellingDataObjects(rb);
        if (ResourceUtil.getBooleanProperty(rb, PROPERTIES_WRITE_BIN_JJ_FILE))
            JobDataManager.writeBinaryJobDataObjects(rb);
*/

    }

    public static void writeOutSyntheticPopulationDEShort (int year, int step, SiloDataContainer dataContainer) {
        // write out files with synthetic population

        String fileEnding = "_" + step + "k_" + year + ".csv";

        logger.info("  Writing household file");
        String filehh = Properties.get().main.baseDirectory + Properties.get().householdData.householdFileName + fileEnding;
        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(filehh, false);
        pwh.println("id,dwelling,zone,hhSize,autos");
        String filepp = Properties.get().main.baseDirectory + Properties.get().householdData.personFileName + fileEnding;
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(filepp, false);
        pwp.println("id,hhid,age,gender,relationShip,race,occupation,workplace,income,nationality,education,homeZone,workZone,license,schoolDE");
        Household[] households = dataContainer.getHouseholdData().getHouseholds().toArray(new Household[0]);
        RealEstateDataManager realEstate = dataContainer.getRealEstateData();
        for (int i = 0; i < households.length; i = i + step) {
            Household hh = households[i];
            if (hh.getId() == SiloUtil.trackHh) {
                SiloUtil.trackingFile("Writing hh " + hh.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(hh.toString());
            }
            pwh.print(hh.getId());
            pwh.print(",");
            pwh.print(hh.getDwellingId());
            pwh.print(",");
            int zone = -1;
            Dwelling dwelling = realEstate.getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                zone = dwelling.getZone();
            }
            pwh.print(zone);
            pwh.print(",");
            pwh.print(hh.getHhSize());
            pwh.print(",");
            pwh.println(hh.getAutos());
            for (Person pp: hh.getPersons()){
                pwp.print(pp.getId());
                pwp.print(",");
                pwp.print(pp.getHh().getId());
                pwp.print(",");
                pwp.print(pp.getAge());
                pwp.print(",");
                pwp.print(pp.getGender());
                pwp.print(",\"");
                pwp.print(pp.getRole().toString());
                pwp.print("\",\"");
                pwp.print(pp.getRace());
                pwp.print("\",");
                pwp.print(pp.getOccupation());
                pwp.print(",");
                pwp.print(pp.getWorkplace());
                pwp.print(",");
                pwp.print(pp.getIncome());
                pwp.print(",");
                pwp.print(pp.getNationality());
                pwp.print(",");
                pwp.print(pp.getEducationLevel());
                pwp.print(",");
                pwp.print(zone);
                pwp.print(",");
                pwp.print(pp.getJobTAZ());
                pwp.print(",");
                pwp.print(pp.hasDriverLicense());
                pwp.print(",");
                pwp.println(pp.getSchoolType());
                if (pp.getId() == SiloUtil.trackPp) {
                    SiloUtil.trackingFile("Writing pp " + pp.getId() + " to micro data file.");
                    SiloUtil.trackWriter.println(pp.toString());
                }
            }
        }
        pwh.close();
        pwp.close();

        logger.info("  Writing dwelling file");
        String filedd = Properties.get().main.baseDirectory + Properties.get().householdData.dwellingsFileName + fileEnding;
        PrintWriter pwd = SiloUtil.openFileForSequentialWriting(filedd, false);
        pwd.println("id,zone,type,hhID,bedrooms,quality,monthlyCost,restriction,yearBuilt,floor,building,year,usage");
        for (Dwelling dd: realEstate.getDwellings()) {
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
            pwd.print(dd.getYearBuilt());
            pwd.print(",");
            pwd.print(dd.getFloorSpace());
            pwd.print(",");
            pwd.print(dd.getBuildingSize());
            pwd.print(",");
            pwd.print(dd.getYearConstructionDE());
            pwd.print(",");
            pwd.println(dd.getUsage());
            if (dd.getId() == SiloUtil.trackDd) {
                SiloUtil.trackingFile("Writing dd " + dd.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(dd.toString());
            }
        }
        pwd.close();

        logger.info("  Writing job file");
        String filejj = Properties.get().main.baseDirectory + Properties.get().householdData.jobsFileName + fileEnding;
        PrintWriter pwj = SiloUtil.openFileForSequentialWriting(filejj, false);
        pwj.println("id,zone,personId,type");
        for (Job jj: dataContainer.getJobData().getJobs()) {
            pwj.print(jj.getId());
            pwj.print(",");
            pwj.print(jj.getZone());
            pwj.print(",");
            pwj.print(jj.getWorkerId());
            pwj.print(",");
            pwj.println(jj.getType());
            if (jj.getId() == SiloUtil.trackJj) {
                SiloUtil.trackingFile("Writing jj " + jj.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(jj);
            }
        }
        pwj.close();
    }

    public static void summarizeAutoOwnershipByCounty(Accessibility accessibility, SiloDataContainer dataContainer) {
        // This calibration function summarized households by auto-ownership and quits

        PrintWriter pwa = SiloUtil.openFileForSequentialWriting("autoOwnershipA.csv", false);
        pwa.println("hhSize,workers,income,transit,density,autos");
        int[][] autos = new int[4][60000];
        final RealEstateDataManager realEstate= dataContainer.getRealEstateData();
        final GeoData geoData = dataContainer.getGeoData();
        final JobDataManager jobData = dataContainer.getJobData();
        final HouseholdDataManager householdData = dataContainer.getHouseholdData();
        for (Household hh: householdData.getHouseholds()) {
            int autoOwnership = hh.getAutos();
            int zone = -1;
            Dwelling dwelling = realEstate.getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                zone = dwelling.getZone();
            }
            int county = ((MstmZone)geoData.getZones().get(zone)).getCounty().getId();
            autos[autoOwnership][county]++;
            pwa.println(hh.getHhSize()+","+hh.getNumberOfWorkers()+","+hh.getHhIncome()+","+
                    accessibility.getTransitAccessibilityForZone(zone)+","+jobData.getJobDensityInZone(zone)+","+hh.getAutos());
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


    public static void preparePrestoSummary (GeoData geoData) {

        String prestoZoneFile = Properties.get().main.baseDirectory + Properties.get().main.prestoZoneFile;
        TableDataSet regionDefinition = SiloUtil.readCSVfile(prestoZoneFile);
        regionDefinition.buildIndex(regionDefinition.getColumnPosition("aggFips"));

        final int highestId = geoData.getZones().keySet().stream().mapToInt(Integer::intValue).max().getAsInt();
        prestoRegionByTaz = new int[highestId+1];
        Arrays.fill(prestoRegionByTaz, -1);
        for (Zone zone: geoData.getZones().values()) {
            try {
                prestoRegionByTaz[zone.getId()] =
                        (int) regionDefinition.getIndexedValueAt(((MstmZone) zone).getCounty().getId(), "presto");
            } catch (Exception e) {
                prestoRegionByTaz[zone.getId()] = -1;
            }
        }
    }


    public static void summarizePrestoRegion (int year, SiloDataContainer dataContainer) {
        // summarize housing costs by income group in SILO region

        String fileName = (Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName + "/" +
                Properties.get().main.prestoSummaryFile + Properties.get().main.gregorianIterator + ".csv");
        PrintWriter pw = SiloUtil.openFileForSequentialWriting(fileName, year != Properties.get().main.implementation.BASE_YEAR);
        pw.println(year + ",Housing costs by income group");
        pw.print("Income");
        for (int i = 0; i < 10; i++) pw.print(",rent_" + ((i + 1) * 250));
        pw.println(",averageRent");
        int[][] rentByIncome = new int[10][10];
        int[] rents = new int[10];
        RealEstateDataManager realEstate = dataContainer.getRealEstateData();
        for (Household hh: dataContainer.getHouseholdData().getHouseholds()) {
            int zone = -1;
            Dwelling dwelling = realEstate.getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                zone = dwelling.getZone();
            }
            if (prestoRegionByTaz[zone] > 0) {
                int hhInc = hh.getHhIncome();
                int rent = realEstate.getDwelling(hh.getDwellingId()).getPrice();
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


    public static void writeOutSyntheticPopulationDe (ResourceBundle rb, int year, SiloDataContainer dataContainer) {
        // write out files with synthetic population

        logger.info("  Writing household file");
        String filehh = Properties.get().main.baseDirectory + Properties.get().householdData.householdFileName + "_" +
                year + ".csv";
        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(filehh, false);
        pwh.println("id,dwelling,zone,hhSize,autos");
        RealEstateDataManager realEstate = dataContainer.getRealEstateData();
        for (Household hh : dataContainer.getHouseholdData().getHouseholds()) {
            if (hh.getId() == SiloUtil.trackHh) {
                SiloUtil.trackingFile("Writing hh " + hh.getId() + " to micro data file.");
                SiloUtil.trackWriter.println(hh.toString());
            }
            pwh.print(hh.getId());
            pwh.print(",");
            pwh.print(hh.getDwellingId());
            pwh.print(",");
            int zone = -1;
            Dwelling dwelling = realEstate.getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                zone = dwelling.getZone();
            }
            pwh.print(zone);
            pwh.print(",");
            pwh.print(hh.getHhSize());
            pwh.print(",");
            pwh.println(hh.getAutos());
        }
        pwh.close();

        logger.info("  Writing person file");
        String filepp = Properties.get().main.baseDirectory + Properties.get().householdData.personFileName + "_" +
                year + ".csv";
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(filepp, false);
        pwp.println("id,hhID,age,gender,relationShip,race,occupation,driversLicense,workplace,income");
        for (Person pp : dataContainer.getHouseholdData().getPersons()) {
            pwp.print(pp.getId());
            pwp.print(",");
            pwp.print(pp.getHh().getId());
            pwp.print(",");
            pwp.print(pp.getAge());
            pwp.print(",");
            pwp.print(pp.getGender());
            pwp.print(",\"");
            pwp.print(pp.getRole().toString());
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
                SiloUtil.trackWriter.println(pp.toString());
            }
        }
        pwp.close();

        if (ResourceUtil.getBooleanProperty(rb, "write.binary.pop.files")) {
            dataContainer.getHouseholdData().writeBinaryPopulationDataObjects();
        }
        if (ResourceUtil.getBooleanProperty(rb, "write.binary.dd.file")) {
            realEstate.writeBinaryDwellingDataObjects();
        }
        if (ResourceUtil.getBooleanProperty(rb, "write.binary.jj.file")) {
            dataContainer.getJobData().writeBinaryJobDataObjects();
        }
    }

    public static void summarizeCarOwnershipByMunicipality(TableDataSet zonalData, SiloDataContainer dataContainer) {
        // This calibration function summarizes household auto-ownership by municipality and quits

        SiloUtil.createDirectoryIfNotExistingYet("microData/interimFiles/");
        PrintWriter pwa = SiloUtil.openFileForSequentialWriting("microData/interimFiles/carOwnershipByHh.csv", false);
        pwa.println("license,workers,income,logDistanceToTransit,areaType,autos");
        int[][] autos = new int[4][10000000];
        RealEstateDataManager realEstate = dataContainer.getRealEstateData();
        for (Household hh: dataContainer.getHouseholdData().getHouseholds()) {
            int autoOwnership = hh.getAutos();
            int zone = -1;
            Dwelling dwelling = realEstate.getDwelling(hh.getDwellingId());
            if(dwelling != null) {
                zone = dwelling.getZone();
            }
            int municipality = (int) zonalData.getIndexedValueAt(zone, "ID_city");
            int distance = (int) Math.log(zonalData.getIndexedValueAt(zone, "distanceToTransit"));
            int area = (int) zonalData.getIndexedValueAt(zone,"BBSR");
            autos[autoOwnership][municipality]++;
            pwa.println(hh.getHHLicenseHolders()+ "," + hh.getNumberOfWorkers() + "," + hh.getHhIncome() + "," +
                    distance + "," + area + "," + hh.getAutos());
        }
        pwa.close();

        PrintWriter pw = SiloUtil.openFileForSequentialWriting("microData/interimFiles/carOwnershipByMunicipality.csv", false);
        pw.println("Municipality,0autos,1auto,2autos,3+autos");
        for (int municipality = 0; municipality < 10000000; municipality++) {
            int sm = 0;
            for (int a = 0; a < 4; a++) sm += autos[a][municipality];
            if (sm > 0) pw.println(municipality+","+autos[0][municipality]+","+autos[1][municipality]+","+autos[2][municipality]+","+autos[3][municipality]);
        }
        pw.close();

        logger.info("Summarized initial auto ownership");

    }


}
