package edu.umd.ncsg.transportModel.tripGeneration;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import edu.umd.ncsg.SiloUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.ResourceBundle;

//import java.util.ArrayList;

/**
 * This class stores data needed for the microscopic trip generation module
 *
 * @author Rolf Moeckel
 * @version 1.0, Jul 2nd, 2014 (College Park, MD)
 * Created by IntelliJ IDEA.
 */


public class TripGenerationData {

    private static Logger logger = Logger.getLogger(TripGenerationData.class);
    private ResourceBundle rb;
    private TableDataSet htsHH;
    private TableDataSet htsTR;
    private static String autoMode;
    private int minNumberOfRecords;


    public TripGenerationData(ResourceBundle rb) {
        // Constructor
        this.rb = rb;
        autoMode = rb.getString("anal.autos.or.autosufficiency");
        minNumberOfRecords = ResourceUtil.getIntegerProperty(rb, "min.no.of.records.by.hh.type");
    }


    public void readHouseholdTravelSurvey(String tripPurpose) {
        // read household travel survey

        logger.info("  Reading household travel survey");
        htsHH = SiloUtil.readCSVfile(rb.getString("household.travel.survey.hh"));
        if (tripPurpose.equalsIgnoreCase("all")) {
            htsTR = SiloUtil.readCSVfile(rb.getString("household.travel.survey.trips"));
        } else {
            htsTR = condenseSurveyToPurpose(tripPurpose);
        }
    }


    private TableDataSet condenseSurveyToPurpose (String tripPurpose) {
        // Condence trip file of household travel survey to single trip purpose

        TableDataSet fullTable = SiloUtil.readCSVfile(rb.getString("household.travel.survey.trips"));
        int counter = 0;
        for (int row = 1; row <= fullTable.getRowCount(); row++) {
            if (fullTable.getStringValueAt(row, "mainPurpose").equalsIgnoreCase(tripPurpose)) counter++;
        }
        int[] sampleId = new int[counter];
        String[] purpose = new String[counter];
        int pos = 0;
        for (int row = 1; row < fullTable.getRowCount(); row++) {
            if (fullTable.getStringValueAt(row, "mainPurpose").equalsIgnoreCase(tripPurpose)) {
                sampleId[pos] = (int) fullTable.getValueAt(row, "sampn");
                purpose[pos] = tripPurpose;
                pos++;
            }
        }
        TableDataSet tableForThisPurpose = new TableDataSet();
        tableForThisPurpose.appendColumn(sampleId, "sampn");
        tableForThisPurpose.appendColumn(purpose, "mainPurpose");
        return tableForThisPurpose;
    }


    public int getNumberOfHouseholdRecords () {
        return htsHH.getRowCount();
    }


//    public TableDataSet getHtsHH () {
//        return htsHH;
//    }


//    public TableDataSet getHtsTR() {
//        return htsTR;
//    }


    public TableDataSet createHouseholdTypeTableDataSet (int numCategories, String[] sizePortions, String[] workerPortions,
                                                         String[] incomePortions, String[] autoPortions, String[] regionPortions) {
        // create household type TableDataSet

        int[] hhType = new int[numCategories];
        int[] size_l = new int[numCategories];
        int[] size_h = new int[numCategories];
        int[] workers_l = new int[numCategories];
        int[] workers_h = new int[numCategories];
        int[] income_l = new int[numCategories];
        int[] income_h = new int[numCategories];
        int[] autos_l = new int[numCategories];
        int[] autos_h = new int[numCategories];
        int[] region_l = new int[numCategories];
        int[] region_h = new int[numCategories];

        int typeCounter = 0;
        for (String sizeToken: sizePortions) {
            String[] sizeParts = sizeToken.split("-");
            for (String workerToken : workerPortions) {
                String[] workerParts = workerToken.split("-");
                for (String incomeToken : incomePortions) {
                    String[] incomeParts = incomeToken.split("-");
                    for (String autoToken : autoPortions) {
                        String[] autoParts = autoToken.split("-");
                        for (String regionToken : regionPortions) {
                            String[] regionParts = regionToken.split("-");
                            hhType[typeCounter] = typeCounter + 1;
                            size_l[typeCounter] = Integer.parseInt(sizeParts[0]);
                            size_h[typeCounter] = Integer.parseInt(sizeParts[1]);
                            workers_l[typeCounter] = Integer.parseInt(workerParts[0]) - 1;
                            workers_h[typeCounter] = Integer.parseInt(workerParts[1]) - 1;
                            income_l[typeCounter] = Integer.parseInt(incomeParts[0]);
                            income_h[typeCounter] = Integer.parseInt(incomeParts[1]);
                            autos_l[typeCounter] = Integer.parseInt(autoParts[0]) - 1;
                            autos_h[typeCounter] = Integer.parseInt(autoParts[1]) - 1;
                            region_l[typeCounter] = Integer.parseInt(regionParts[0]);
                            region_h[typeCounter] = Integer.parseInt(regionParts[1]);
                            typeCounter++;
                        }
                    }
                }
            }
        }

        TableDataSet hhTypeDef = new TableDataSet();
        hhTypeDef.appendColumn(hhType, "hhType");
        hhTypeDef.appendColumn(size_l, "size_l");
        hhTypeDef.appendColumn(size_h, "size_h");
        hhTypeDef.appendColumn(workers_l, "workers_l");
        hhTypeDef.appendColumn(workers_h, "workers_h");
        hhTypeDef.appendColumn(income_l, "income_l");
        hhTypeDef.appendColumn(income_h, "income_h");
        hhTypeDef.appendColumn(autos_l, "autos_l");
        hhTypeDef.appendColumn(autos_h, "autos_h");
        hhTypeDef.appendColumn(region_l, "region_l");
        hhTypeDef.appendColumn(region_h, "region_h");
        hhTypeDef.buildIndex(hhTypeDef.getColumnPosition("hhType"));
        return hhTypeDef;
    }


    public int[] defineHouseholdTypeOfEachSurveyRecords(String autoDef, TableDataSet hhTypeDef) {
        // Count number of household records per predefined typ

        int[] hhTypeCounter = new int[SiloUtil.getHighestVal(hhTypeDef.getColumnAsInt("hhType")) + 1];
        int[] hhTypeArray = new int[htsHH.getRowCount() + 1];

        for (int row = 1; row <= htsHH.getRowCount(); row++) {
            int hhSze = (int) htsHH.getValueAt(row, "hhsiz");
            hhSze = Math.min(hhSze, 7);    // hhsiz 8 has only 19 records, aggregate with hhsiz 7
            int hhWrk = (int) htsHH.getValueAt(row, "hhwrk");
            hhWrk = Math.min(hhWrk, 4);    // hhwrk 6 has 1 and hhwrk 5 has 7 records, aggregate with hhwrk 4
            int hhInc = (int) htsHH.getValueAt(row, "incom");
            int hhVeh = (int) htsHH.getValueAt(row, "hhveh");
            hhVeh = Math.min (hhVeh, 3);   // Auto-ownership model will generate groups 0, 1, 2, 3+ only.
            int region = (int) htsHH.getValueAt(row, "urbanSuburbanRural");

            int hhTypeId = getHhType(autoDef, hhTypeDef, hhSze, hhWrk, hhInc, hhVeh, region);
            hhTypeArray[row] = hhTypeId;
            hhTypeCounter[hhTypeId]++;
        }
        // analyze if every household type has a sufficient number of records
        for (int hht = 1; hht < hhTypeCounter.length; hht++) {
            if (hhTypeCounter[hht] < minNumberOfRecords) hhTypeArray[0] = -1;  // marker that this hhTypeDef is not worth analyzing
        }
        return hhTypeArray;
    }


    public int getHhType (String autoDef, TableDataSet hhTypeDef, int hhSze, int hhWrk, int hhInc, int hhVeh, int hhReg) {
        // Define household type

        hhSze = Math.min (hhSze, 7);
        hhWrk = Math.min (hhWrk, 4);
        int hhAut;
        if (autoDef.equalsIgnoreCase("autos")) {
            hhAut = Math.min(hhVeh, 3);
        } else {
            if (hhVeh < hhWrk) hhAut = 0;        // fewer autos than workers
            else if (hhVeh == hhWrk) hhAut = 1;  // equal number of autos and workers
            else hhAut = 2;                      // more autos than workers
        }
        for (int hhType = 1; hhType <= hhTypeDef.getRowCount(); hhType++) {
            if (hhSze >= hhTypeDef.getIndexedValueAt(hhType, "size_l") &&          // Household size
                    hhSze <= hhTypeDef.getIndexedValueAt(hhType, "size_h") &&
                    hhWrk >= hhTypeDef.getIndexedValueAt(hhType, "workers_l") &&   // Number of workers
                    hhWrk <= hhTypeDef.getIndexedValueAt(hhType, "workers_h") &&
                    hhInc >= hhTypeDef.getIndexedValueAt(hhType, "income_l") &&    // Household income
                    hhInc <= hhTypeDef.getIndexedValueAt(hhType, "income_h") &&
                    hhAut >= hhTypeDef.getIndexedValueAt(hhType, "autos_l") &&     // Number of vehicles
                    hhAut <= hhTypeDef.getIndexedValueAt(hhType, "autos_h") &&
                    hhReg >= hhTypeDef.getIndexedValueAt(hhType, "region_l") &&    // Region (urban, suburban, rural)
                    hhReg <= hhTypeDef.getIndexedValueAt(hhType, "region_h")) {
                return (int) hhTypeDef.getIndexedValueAt(hhType, "hhType");
            }
        }
        logger.error ("Could not define household type: " + hhSze + " " + hhWrk + " " + hhInc + " " + hhVeh + " " + hhReg);
        for (int hhType = 1; hhType <= hhTypeDef.getRowCount(); hhType++) {
            System.out.println(hhType+": "+hhTypeDef.getIndexedValueAt(hhType, "size_l")+"-"+hhTypeDef.getIndexedValueAt(hhType, "size_h")
                    +","+hhTypeDef.getIndexedValueAt(hhType, "workers_l")+"-"+hhTypeDef.getIndexedValueAt(hhType, "workers_h")
                    +","+hhTypeDef.getIndexedValueAt(hhType, "income_l")+"-"+hhTypeDef.getIndexedValueAt(hhType, "income_h")
                    +","+hhTypeDef.getIndexedValueAt(hhType, "autos_l")+"-"+hhTypeDef.getIndexedValueAt(hhType, "autos_h")
                    +","+hhTypeDef.getIndexedValueAt(hhType, "region_l")+"-"+hhTypeDef.getIndexedValueAt(hhType, "region_h"));
        }
        return -1;
    }


//    public String[] defineTripPurposes (TableDataSet hhTypeDef, String tripPurp) {
//        // Define trip purposes (six purposes given exogenously, of which some are split by income group, and all are split by auto availability)
//
//        ArrayList<String> incomeClassCategories = countCategoriesInHhTypeDefinition(hhTypeDef, "i", "income_l", "income_h");
//        ArrayList<String> autoAvailabilityCategories = countCategoriesInHhTypeDefinition(hhTypeDef, "a", "autos_l", "autos_h");
//        String[] tripPurposes;
//        String[] basicTripTypesByIncomeAndAutos = {"HBW", "HBO", "HBS"};
//        String[] basicTripTypesAllIncome = {"HBE", "NHBW", "NHBO"};
//        if (tripPurp.equalsIgnoreCase("all")) {
//            tripPurposes = new String[basicTripTypesByIncomeAndAutos.length * incomeClassCategories.size() * autoAvailabilityCategories.size()
//                    + basicTripTypesAllIncome.length];
//            int pos = 0;
//            for (String tt : basicTripTypesByIncomeAndAutos) {
//                for (String inc : incomeClassCategories) {
//                    for (String auto : autoAvailabilityCategories) {
//                        tripPurposes[pos] = tt + "_" + inc + "_" + auto;
//                        pos++;
//                    }
//                }
//            }
//            for (String tt : basicTripTypesAllIncome) {
//                tripPurposes[pos] = tt;
//                pos++;
//            }
//        } else {   // household type exploration mode, only one trip purpose is processed
//            if (mstmUtilities.containsElement(basicTripTypesByIncomeAndAutos, tripPurp)) {
//                tripPurposes = new String[1 * incomeClassCategories.size() * autoAvailabilityCategories.size()];
//                int pos = 0;
//                for (String inc : incomeClassCategories) {
//                    for (String auto : autoAvailabilityCategories) {
//                        tripPurposes[pos] = tripPurp + "_" + inc + "_" + auto;
//                        pos++;
//                    }
//                }
//
//            } else {
//                tripPurposes = new String[]{tripPurp};
//            }
//        }
////        for (String p: tripPurposes) System.out.println(p);
//        return tripPurposes;
//    }


//    private ArrayList<String> countCategoriesInHhTypeDefinition(TableDataSet hhTypeDef, String prefix, String low, String high) {
//        // Count number of income groups defined in household type definition file
//
//        ArrayList<String> al = new ArrayList<>();
//        for (int hhType = 1; hhType <= hhTypeDef.getRowCount(); hhType++) {
//            String token = prefix + (int) hhTypeDef.getIndexedValueAt(hhType, low) + "-" + (int) hhTypeDef.getIndexedValueAt(hhType, high);
//            if (!al.contains(token)) al.add(token);
//        }
//        return al;
//    }


    public String getAutoMode () {
        return autoMode;
    }


    public int getMinNumberOfRecords () {
        return minNumberOfRecords;
    }


    public HashMap<String, Integer[]> collectTripFrequencyDistribution (int[] hhTypeArray) {
        // Summarize frequency of number of trips for each household type by each trip purpose
        //
        // Storage Structure
        //   HashMap<String, Integer> tripsByHhTypeAndPurpose: Token is hhType_TripPurpose
        //   |
        //   contains -> Integer[] tripFrequencyList: Frequency of 0, 1, 2, 3, ... trips

        HashMap<String, Integer[]> tripsByHhTypeAndPurpose = new HashMap<>();  // contains trips by hhtype and purpose

        for (int hhType = 1; hhType < hhTypeArray.length; hhType++) {
            for (tripPurposes purp: tripPurposes.values()) {
                String token = String.valueOf(hhType) + "_" + purp.toString();
                // fill Storage structure from bottom       0                  10                  20                  30
                Integer[] tripFrequencyList = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};  // space for up to 30 trips
                tripsByHhTypeAndPurpose.put(token, tripFrequencyList);
            }
        }

        // Read through household file fo HTS
        int pos = 1;
        for (int hhRow = 1; hhRow <= getNumberOfHouseholdRecords(); hhRow++) {
            int sampleId = (int) htsHH.getValueAt(hhRow, "sampn");
            int hhType = hhTypeArray[hhRow];
            int[] tripsOfThisHouseholdByPurposes = new int[tripPurposes.values().length];
            // Ready through trip file of HTS
            for (int trRow = pos; trRow <= htsTR.getRowCount(); trRow++) {
                if ((int) htsTR.getValueAt(trRow, "sampn") == sampleId) {

                    // add this trip to this household
                    pos++;
                    String htsTripPurpose = htsTR.getStringValueAt(trRow, "mainPurpose");
                    tripsOfThisHouseholdByPurposes[tripPurposes.valueOf(htsTripPurpose).ordinal()]++;
                } else {
                    // This trip record does not belong to this household
                    break;
                }
            }
            for (int p = 0; p < tripPurposes.values().length; p++) {
                String token = String.valueOf(hhType) + "_" + tripPurposes.values()[p].toString();
                Integer[] tripsOfThisHouseholdType = tripsByHhTypeAndPurpose.get(token);
                int count = tripsOfThisHouseholdByPurposes[p];
                tripsOfThisHouseholdType[count]++;
                tripsByHhTypeAndPurpose.put(token, tripsOfThisHouseholdType);
            }
        }
        return tripsByHhTypeAndPurpose;
    }

}
