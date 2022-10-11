package de.tum.bgu.msm.models.realEstate.construction;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingFactory;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.vehicle.VehicleType;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.PrintWriter;
import java.util.*;

/**
 * This method allows to add dwellings as an overwrite. New dwellings are given exogenously and added in a given year,
 * regardless of current demand.
 *
 * Author: Rolf Moeckel, National Center for Smart Growth, University of Maryland
 * Created on 14 October 2014 in College Park
 **/
public class ConstructionOverwriteImpl extends AbstractModel implements ConstructionOverwrite {

    private final static Logger logger = Logger.getLogger(ConstructionOverwriteImpl.class);
    private final DwellingFactory factory;

    private boolean useOverwrite;
    private boolean traceOverwriteDwellings;

    private Map<Integer, List<Integer[]>> plannedDwellings;

    public ConstructionOverwriteImpl(DataContainer dataContainer, DwellingFactory factory, Properties properties, Random random) {
        super(dataContainer, properties, random);
        this.factory = factory;
        useOverwrite = properties.realEstate.constructionOverwriteDwelling;
        if (!useOverwrite) {
            return;
        }
        traceOverwriteDwellings = properties.realEstate.traceOverwriteDwellings;
        if (traceOverwriteDwellings) {
            String directory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
            SiloUtil.createDirectoryIfNotExistingYet(directory);
            String fileName = (directory + "/" + properties.realEstate.overWriteDwellingsTraceFile + ".csv");
            PrintWriter traceFile = SiloUtil.openFileForSequentialWriting(fileName, false);
            traceFile.println("dwellingID,zone,type,size,quality,initialPrice,restriction,yearBuilt");
            traceFile.close();
        }
        readOverwriteFile();
    }

    @Override
    public void setup() {}

    @Override
    public void prepareYear(int year) {
        addDwellings(year);
    }

    @Override
    public void endYear(int year) {}

    @Override
    public void endSimulation() {
        if (traceOverwriteDwellings) {
            finishOverwriteTracer();
        }
    }

    private void readOverwriteFile() {
        // read overwrite file

        logger.info("  Reading dwelling overwrite file");

        String fileName = properties.main.baseDirectory + properties.realEstate.constructionOverwriteDwellingFile;
        TableDataSet overwrite = SiloUtil.readCSVfile(fileName);
        plannedDwellings = new HashMap<>();

        for (int row = 1; row <= overwrite.getRowCount(); row++) {
            int year = (int) overwrite.getValueAt(row, "year");
            if (year > properties.main.endYear || year < 0) {
                continue;   // if year > endYear, this row is not relevant for current run
            }
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
            final List<DwellingType> types = dataContainer.getRealEstateDataManager().getDwellingTypes().getTypes();
            for (DwellingType dt: types) {
                if (dt.toString().equalsIgnoreCase(type)) {
                    data[1] = types.indexOf(dt);
                }
            }
            if (data[1] == -1) {
                logger.error("Invalid dwelling type in row " + row + " in file " + fileName + ".");
            }
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
                for (int i = 1; i <= quantity; i++) {
                    list.add(data);
                }
            } else {
                ArrayList<Integer[]> list = new ArrayList<>();
                for (int i = 1; i <= quantity; i++) {
                    list.add(data);
                }
                plannedDwellings.put(year, list);
            }
        }
    }


    private void addDwellings (int year) {
        // add overwrite dwellings for this year

        if (!useOverwrite) {
            return;
        }
        if (!plannedDwellings.containsKey(year)) {
            return;
        }
        logger.info("  Adding dwellings that are given exogenously as an overwrite for the year " + year);

        String directory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
        String fileName = (directory + "/" + properties.realEstate.overWriteDwellingsTraceFile + ".csv");
        PrintWriter traceFile = SiloUtil.openFileForSequentialWriting(fileName, true);
        List<Integer[]> list = plannedDwellings.get(year);
        for (Integer[] data: list) {
            int ddId = dataContainer.getRealEstateDataManager().getNextDwellingId();
            int zoneId = data[0];
            int dto = data[1];
            int size = data[2];
            int quality = data[3];
            int price = data[4];

            Dwelling dd = factory.createDwelling(ddId, zoneId, null, -1, dataContainer.getRealEstateDataManager().getDwellingTypes().getTypes().get(dto), size, quality, price, year);
            dataContainer.getRealEstateDataManager().addDwelling(dd);

            Coordinate coordinate = dataContainer.getGeoData().getZones().get(zoneId).getRandomCoordinate(random);
            dd.setCoordinate(coordinate);

            if (traceOverwriteDwellings) {
                traceFile.println(ddId + "," + zoneId + "," +  dataContainer.getRealEstateDataManager().getDwellingTypes().getTypes().get(dto).toString() + "," + size + "," +
                        quality + "," + price  + "," + year);
            }
            if (ddId == SiloUtil.trackDd) {
                SiloUtil.trackWriter.println("Dwelling " + ddId + " was constructed as an overwrite with these properties: ");
                SiloUtil.trackWriter.println(dd.toString());
            }
            dataContainer.getRealEstateDataManager().addDwellingToVacancyList(dd);
        }
        traceFile.close();
    }


    private void finishOverwriteTracer () {
        // Read Tracer File and write out current conditions at end of simulation

        if (!useOverwrite) {
            return;  // if overwrite is not used, now overwrite dwellings can be traced
        }
        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();
        String directory = properties.main.baseDirectory + "scenOutput/" + properties.main.scenarioName;
        String fileName = (directory + "/" + properties.realEstate.overWriteDwellingsTraceFile + ".csv");
        TableDataSet overwriteDwellings = SiloUtil.readCSVfile(fileName);
        int[] householdId   = SiloUtil.createArrayWithValue(overwriteDwellings.getRowCount(), 0);
        int[] householdSize = SiloUtil.createArrayWithValue(overwriteDwellings.getRowCount(), 0);
        int[] householdInc  = SiloUtil.createArrayWithValue(overwriteDwellings.getRowCount(), 0);
        int[] householdAuto = SiloUtil.createArrayWithValue(overwriteDwellings.getRowCount(), 0);
        int[] dwellingRent  = SiloUtil.createArrayWithValue(overwriteDwellings.getRowCount(), 0);
        for (int row = 1; row <= overwriteDwellings.getRowCount(); row++) {
            int ddId = (int) overwriteDwellings.getValueAt(row, "dwellingID");
            Dwelling dd = dataContainer.getRealEstateDataManager().getDwelling(ddId);
            if (dd == null) {
                overwriteDwellings.setStringValueAt(row, "type", "dwellingWasDemolished");
            }
            if (dd == null) {
                continue;
            }
            dwellingRent[row-1] = dd.getPrice();
            if (dd.getResidentId() > 0) {
                Household hh = householdDataManager.getHouseholdFromId(dd.getResidentId());
                householdId[row-1] = hh.getId();
                householdSize[row-1] = hh.getHhSize();
                householdInc[row-1] = HouseholdUtil.getAnnualHhIncome(hh);
                householdAuto[row-1] = (int) hh.getVehicles().stream().filter(vv -> vv.getType().equals(VehicleType.CAR)).count();
            }
        }
        int yr = properties.main.endYear;
        overwriteDwellings.appendColumn(householdId, ("resident_" + yr));
        overwriteDwellings.appendColumn(householdSize, ("hhSize_" + yr));
        overwriteDwellings.appendColumn(householdInc, ("hhIncome_" + yr));
        overwriteDwellings.appendColumn(householdAuto, ("hhAutos_" + yr));
        overwriteDwellings.appendColumn(dwellingRent, ("rent_" + yr));
        SiloUtil.writeTableDataSet(overwriteDwellings, fileName);
    }
}
