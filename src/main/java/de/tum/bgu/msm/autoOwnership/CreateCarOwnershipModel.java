package de.tum.bgu.msm.autoOwnership;

import com.pb.common.calculator.UtilityExpressionCalculator;
import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.*;
import omx.OmxFile;
import omx.OmxLookup;
import org.apache.log4j.Logger;

import javax.script.ScriptException;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ResourceBundle;

/**
 * Implements car ownership of initial synthetic population (base year) for the Munich Metropolitan Area
 *
 * @author Matthew Okrah
 *         Created on 28/04/2017 in Munich, Germany.
 */

public class CreateCarOwnershipModel {

    static Logger logger = Logger.getLogger(CreateCarOwnershipModel.class);
    static Logger traceLogger = Logger.getLogger("trace");
    private ResourceBundle rb;

    protected static final String PROPERTIES_CarOwnership_UEC_FILE = "CarOwnership.UEC.FileName";
    protected static final String PROPERTIES_CarOwnership_UEC_DATA_SHEET = "CarOwnership.UEC.DataSheetNumber";
    protected static final String PROPERTIES_CarOwnership_UEC_CREATE_UTILITY = "CarOwnership.UEC.Create.Utility";
    protected static final String PROPERTIES_LOG_UTILITY_CALCULATION_CONSTRUCTION = "log.util.carOwnership";
    protected static final String PROPERTIES_ZONAL_DATA = "raster.cells.definition";
    protected static final String PROPERTIES_TRANSIT_ACCEESS_TIME = "transit.access.time";

    private String uecFileName;
    private int dataSheetNumber;
    int numAltsCarOwnership;
    private double[][][][][][] carOwnerShipUtil;   // [three probabilities][license][workers][income][logdistToTransit][areaType]
    private TableDataSet zonalData;

    private Reader reader;
    private CreateCarOwnershipJSCalculator calculator;


    public CreateCarOwnershipModel(ResourceBundle rb) {
        // Constructor
        logger.info(" Setting up probabilities for car ownership model");
        this.rb = rb;
        setupCarOwnershipModel();
        readZonalData();
    }

    private void setupCarOwnershipModel() {

        reader = new InputStreamReader(this.getClass().getResourceAsStream("CreateCarOwnershipCalc"));
        calculator = new CreateCarOwnershipJSCalculator(reader, false);

    }

    public void run(boolean flagSkipCreationOfSPforDebugging) {
        // main run method

        for (Household hh : Household.getHouseholdArray()) {
            simulateCarOwnership(hh);
        }
        summarizeData.summarizeCarOwnershipByMunicipality(zonalData);
        if (flagSkipCreationOfSPforDebugging) {
            logger.info("Finished car ownership model");
            System.exit(0);
        }
    }

    public void simulateCarOwnership(Household hh) {
        // simulate number of autos for household hh
        // Note: This method can only be executed after all households have been generated and allocated to zones,
        // as distance to transit and areaType is dependent on where households are living
        int license = Math.min(hh.getHHLicenseHolders(), 7);
        int workers = Math.min(hh.getNumberOfWorkers(), 7);
        int income = Math.min(hh.getHhIncome(), 13504);
        int logDistanceToTransit = Math.min((int) Math.log(zonalData.getIndexedValueAt(hh.getHomeZone(), "distanceToTransit")), 10);
        int areaType = (int) zonalData.getIndexedValueAt(hh.getHomeZone(), "BBSR");

        double[] prob = calculateCarOwnershipProb(license, workers, income, logDistanceToTransit, areaType - 1);
        hh.setAutos(SiloUtil.select(prob));
    }

    private double[] calculateCarOwnershipProb(int license, int workers, int income, int logDistanceToTransit, int areaType) {
        calculator.setLicense(license);
        calculator.setWorkers(workers);
        calculator.setIncome(income);
        calculator.setLogDistanceToTransit(logDistanceToTransit);
        calculator.setAreaType(areaType);

        double[] result = new double[4];
        try {
            result = calculator.calculate();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return result;
    }


    public void readZonalData() {
        //method to read the zonal data not using geoData

        zonalData = SiloUtil.readCSVfile(rb.getString(PROPERTIES_ZONAL_DATA));
        zonalData.buildIndex(zonalData.getColumnPosition("ID_cell"));

        //get minimum distance to transit from OMX matrix and append to zonal data
        float[] minDistance = SiloUtil.createArrayWithValue(zonalData.getRowCount(), 0f);
        zonalData.appendColumn(minDistance, "distanceToTransit");

        String omxFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_TRANSIT_ACCEESS_TIME);
        OmxFile travelTimeOmx = new OmxFile(omxFileName);
        travelTimeOmx.openReadOnly();
        Matrix accessDistanceMatrix = SiloUtil.convertOmxToMatrix(travelTimeOmx.getMatrix("mat1"));
        OmxLookup omxLookUp = travelTimeOmx.getLookup("lookup1");

        for (int i = 1; i <= zonalData.getRowCount(); i++) {
            float minDist = 9999;
            int origin = (int) zonalData.getValueAt(i, "ID_cell");
            for (int j = 1; j <= zonalData.getRowCount(); j++) {
                int dest = (int) zonalData.getValueAt(j, "ID_cell");
                float distance = accessDistanceMatrix.getValueAt(origin, dest) * 83.33f;
                if (distance > 0 & distance < minDist) {
                    minDist = distance;
                }
            }
            minDist = minDist + 1;
            zonalData.setValueAt(i, "distanceToTransit", minDist);
        }
    }
}


