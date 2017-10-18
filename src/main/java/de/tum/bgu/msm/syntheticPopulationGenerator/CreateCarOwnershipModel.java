package de.tum.bgu.msm.syntheticPopulationGenerator;


import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.*;
import omx.OmxFile;
import omx.OmxLookup;
import org.apache.log4j.Logger;

import javax.script.ScriptException;
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

    protected static final String PROPERTIES_LOG_UTILITY_CALCULATION_CONSTRUCTION = "log.util.carOwnership";
    protected static final String PROPERTIES_ZONAL_DATA = "raster.cells.definition";
    protected static final String PROPERTIES_TRANSIT_ACCEESS_TIME = "transit.access.time";

    private TableDataSet zonalData;

    private Reader reader;
    private CreateCarOwnershipJSCalculator calculator;


    public CreateCarOwnershipModel(ResourceBundle rb) {
        // Constructor
        logger.info(" Setting up probabilities for car ownership model");
        this.rb = rb;
        reader = new InputStreamReader(this.getClass().getResourceAsStream("CreateCarOwnershipCalc"));
        calculator = new CreateCarOwnershipJSCalculator(reader, false);
        readZonalData();
    }

    private double[] calculateCarOwnershipProb(int license, int workers, int income, int logDistanceToTransit, int areaType) {
        // setup to calculate the car ownership probabilities for an individual household from the javascript calculator
        calculator.setLicense(license);
        calculator.setWorkers(workers);
        calculator.setIncome(income);
        calculator.setLogDistanceToTransit(logDistanceToTransit);
        calculator.setAreaType(areaType);

        double[] result = new double[4];  // probabilities for 0, 1, 2 and 3+ cars
        try {
            result = calculator.calculate();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return result;
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
        int license = hh.getHHLicenseHolders();
        int workers = hh.getNumberOfWorkers();
        int income = hh.getHhIncome()/12;  // convert yearly into monthly income
        int logDistanceToTransit = (int) Math.log(zonalData.getIndexedValueAt(hh.getHomeZone(), "distanceToTransit"));
        int areaType = (int) zonalData.getIndexedValueAt(hh.getHomeZone(), "BBSR");

        double[] prob = calculateCarOwnershipProb(license, workers, income, logDistanceToTransit, areaType);
        hh.setAutos(SiloUtil.select(prob));
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
            zonalData.setIndexedValueAt(i, "distanceToTransit", minDist);
        }
    }
}


