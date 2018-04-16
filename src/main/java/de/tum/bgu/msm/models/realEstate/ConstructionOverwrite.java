package de.tum.bgu.msm.models.realEstate;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This method allows to add dwellings as an overwrite. New dwellings are given exogenously and added in a given year,
 * regardless of current demand.
 *
 * Author: Rolf Moeckel, National Center for Smart Growth, University of Maryland
 * Created on 14 October 2014 in College Park
 **/

public class ConstructionOverwrite extends AbstractModel {

    static Logger logger = Logger.getLogger(ConstructionModel.class);

    private boolean useOverwrite;
    private boolean traceOverwriteDwellings;
    private HashMap<Integer, List<Integer[]>> plannedDwellings;

    public ConstructionOverwrite(SiloDataContainer dataContainer) {
        super(dataContainer);
        useOverwrite = Properties.get().realEstate.constructionOverwriteDwelling;
        if (!useOverwrite) return;
        traceOverwriteDwellings = Properties.get().realEstate.traceOverwriteDwellings;
        if (traceOverwriteDwellings) {
            String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
            SiloUtil.createDirectoryIfNotExistingYet(directory);
            String fileName = (directory + "/" + Properties.get().realEstate.overWriteDwellingsTraceFile + "_" +
                    Properties.get().main.gregorianIterator + ".csv");
            PrintWriter traceFile = SiloUtil.openFileForSequentialWriting(fileName, false);
            traceFile.println("dwellingID,zone,type,size,quality,initialPrice,restriction,yearBuilt");
            traceFile.close();
        }
        readOverwriteFile();
    }


    private void readOverwriteFile() {
        // read overwrite file

        logger.info("  Reading dwelling overwrite file");

        String fileName = Properties.get().main.baseDirectory + Properties.get().realEstate.constructionOverwriteDwellingFile;
        TableDataSet overwrite = SiloUtil.readCSVfile(fileName);
        plannedDwellings = new HashMap<>();

        for (int row = 1; row <= overwrite.getRowCount(); row++) {
            int year = (int) overwrite.getValueAt(row, "year");
            if (year > Properties.get().main.endYear || year < 0) continue;   // if year > endYear, this row is not relevant for current run
            Integer[] data = new Integer[6];
            int zone = (int) overwrite.getValueAt(row, "zone");
            String type = overwrite.getStringValueAt(row, "type");
            int bedrooms = (int) overwrite.getValueAt(row, "bedrooms");
            int quality = (int) overwrite.getValueAt(row, "quality");
            int costs = (int) overwrite.getValueAt(row, "monthlyCosts");
            float restrictions = overwrite.getValueAt(row, "restriction");
            int quantity = (int) overwrite.getValueAt(row, "quantity");
            data[0] = zone;
            data[1] = -1;
            for (DwellingType dt: DwellingType.values()) if (dt.toString().equalsIgnoreCase(type)) data[1] = dt.ordinal();
            if (data[1] == -1) logger.error("Invalid dwelling type in row " + row + " in file " + fileName + ".");
            data[2] = bedrooms;
            data[3] = quality;
            if (restrictions == 0) {
                data[4] = costs; // no affordable-housing restrictions on this dwelling
            } else {
                data[4] = 0; // dwelling rent will be calculated in the year of construction in relation to current median income
            }
            data[5] = (int) (restrictions * 100);
            if (plannedDwellings.containsKey(year)) {
                List<Integer[]> list = plannedDwellings.get(year);
                for (int i = 1; i <= quantity; i++) list.add(data);
            } else {
                ArrayList<Integer[]> list = new ArrayList<>();
                for (int i = 1; i <= quantity; i++) list.add(data);
                plannedDwellings.put(year, list);
            }
        }
    }


    public void addDwellings (int year) {
        // add overwrite dwellings for this year

        if (!useOverwrite) return;
        if (!plannedDwellings.containsKey(year)) return;
        logger.info("  Adding dwellings that are given exogenously as an overwrite for the year " + year);

        String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
        String fileName = (directory + "/" + Properties.get().realEstate.overWriteDwellingsTraceFile + "_" +
                Properties.get().main.gregorianIterator + ".csv");
        PrintWriter traceFile = SiloUtil.openFileForSequentialWriting(fileName, true);
        List<Integer[]> list = plannedDwellings.get(year);
        for (Integer[] data: list) {
            int ddId = RealEstateDataManager.getNextDwellingId();
            int zoneId = data[0];
            int dto = data[1];
            int size = data[2];
            int quality = data[3];
            int price = data[4];
            float restriction = data[5] / 100f;
            if (restriction != 0) {
                // rent-controlled, multiply restriction (usually 0.3, 0.5 or 0.8) with median income with 30% housing budget
                // correction: in the PUMS data set, households with the about-median income of 58,000 pay 18% of their income in rent...
                int msa = dataContainer.getGeoData().getZones().get(zoneId).getMsa();
                price = (int) (Math.abs(restriction) * HouseholdDataManager.getMedianIncome(msa) / 12 * 0.18 + 0.5);
            }
            Dwelling dd = dataContainer.getRealEstateData().createDwelling(ddId, zoneId, -1, DwellingType.values()[dto], size, quality, price, restriction, year);
            if (traceOverwriteDwellings) traceFile.println(ddId + "," + zoneId + "," + DwellingType.values()[dto] + "," + size + "," +
                    quality + "," + price + "," + restriction + "," + year);
            if (ddId == SiloUtil.trackDd) {
                SiloUtil.trackWriter.println("Dwelling " + ddId + " was constructed as an overwrite with these properties: ");
                SiloUtil.trackWriter.println(dd.toString());
            }
            dataContainer.getRealEstateData().addDwellingToVacancyList(dd);
        }
        traceFile.close();
    }


    public boolean traceOverwriteDwellings() {
        return traceOverwriteDwellings;
    }


    public void finishOverwriteTracer () {
        // Read Tracer File and write out current conditions at end of simulation

        if (!useOverwrite) {
            return;  // if overwrite is not used, now overwrite dwellings can be traced
        }
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        String directory = Properties.get().main.baseDirectory + "scenOutput/" + Properties.get().main.scenarioName;
        String fileName = (directory + "/" + Properties.get().realEstate.overWriteDwellingsTraceFile + "_" +
                Properties.get().main.gregorianIterator + ".csv");
        TableDataSet overwriteDwellings = SiloUtil.readCSVfile(fileName);
        int[] householdId   = SiloUtil.createArrayWithValue(overwriteDwellings.getRowCount(), 0);
        int[] householdSize = SiloUtil.createArrayWithValue(overwriteDwellings.getRowCount(), 0);
        int[] householdInc  = SiloUtil.createArrayWithValue(overwriteDwellings.getRowCount(), 0);
        int[] householdAuto = SiloUtil.createArrayWithValue(overwriteDwellings.getRowCount(), 0);
        int[] dwellingRent  = SiloUtil.createArrayWithValue(overwriteDwellings.getRowCount(), 0);
        for (int row = 1; row <= overwriteDwellings.getRowCount(); row++) {
            int ddId = (int) overwriteDwellings.getValueAt(row, "dwellingID");
            Dwelling dd = dataContainer.getRealEstateData().getDwelling(ddId);
            if (dd == null) overwriteDwellings.setStringValueAt(row, "type", "dwellingWasDemolished");
            if (dd == null) continue;
            dwellingRent[row-1] = dd.getPrice();
            if (dd.getResidentId() > 0) {
                Household hh = householdData.getHouseholdFromId(dd.getResidentId());
                householdId[row-1] = hh.getId();
                householdSize[row-1] = hh.getHhSize();
                householdInc[row-1] = hh.getHhIncome();
                householdAuto[row-1] = hh.getAutos();
            }
        }
        int yr = Properties.get().main.endYear;
        overwriteDwellings.appendColumn(householdId, ("resident_" + yr));
        overwriteDwellings.appendColumn(householdSize, ("hhSize_" + yr));
        overwriteDwellings.appendColumn(householdInc, ("hhIncome_" + yr));
        overwriteDwellings.appendColumn(householdAuto, ("hhAutos_" + yr));
        overwriteDwellings.appendColumn(dwellingRent, ("rent_" + yr));
        SiloUtil.writeTableDataSet(overwriteDwellings, fileName);
    }
}
