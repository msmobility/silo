package de.tum.bgu.msm.syntheticPopulationGenerator;


import com.pb.common.datafile.TableDataSet;
import com.pb.common.matrix.Matrix;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.properties.Properties;
import omx.OmxFile;
import omx.OmxLookup;
import org.apache.log4j.Logger;

import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
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


    private TableDataSet zonalData;

    private CreateCarOwnershipJSCalculator calculator;


    public CreateCarOwnershipModel() {
        // Constructor
        logger.info(" Setting up probabilities for car ownership model");
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("CreateCarOwnershipCalc"));
        calculator = new CreateCarOwnershipJSCalculator(reader, false);
        readZonalData();
    }

    public void run() {
        // main run method

        for (Household hh : Household.getHouseholdArray()) {
            simulateCarOwnership(hh);
        }
        boolean tokenForTestingCarOwnership = false;
        if (tokenForTestingCarOwnership) {
            SummarizeData.summarizeCarOwnershipByMunicipality(zonalData);
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
        // add 1 to the value of distance to transit before taking log to avoid situations of log 0
        double logDistanceToTransit = Math.log(zonalData.getIndexedValueAt(hh.getHomeZone(), "distanceToTransit") + 1);
        int areaType = (int) zonalData.getIndexedValueAt(hh.getHomeZone(), "BBSR");

        double[] prob = calculateCarOwnershipProb(license, workers, income, logDistanceToTransit, areaType);
        hh.setAutos(SiloUtil.select(prob));
        //logger.info("Finished car ownership model");
        //logger.info(hh.getAutos()+"cars " + hh.getNumberOfWorkers()+"workers " + hh.getHHLicenseHolders()+"license " + hh.getHhIncome()+"income ");
    }

    private double[] calculateCarOwnershipProb(int license, int workers, int income, double logDistanceToTransit, int areaType) {
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

    public void readZonalData() {
        //method to read the zonal data not using geoData

        zonalData = SiloUtil.readCSVfile(Properties.get().geo.zonalAttributesFile);
        zonalData.buildIndex(zonalData.getColumnPosition("ID_cell"));

        //add a column to store distance to transit pre-populated with 0s
        float[] minDistance = SiloUtil.createArrayWithValue(zonalData.getRowCount(), 0f);
        zonalData.appendColumn(minDistance, "distanceToTransit");

        //convert transit access time matrix from omx to java matrix
        String omxFileName = Properties.get().main.baseDirectory + Properties.get().geo.transitAccessTime;
        OmxFile travelTimeOmx = new OmxFile(omxFileName);
        travelTimeOmx.openReadOnly();
        Matrix accessTimeMatrix = SiloUtil.convertOmxToMatrix(travelTimeOmx.getMatrix("mat1"));

        //get minimum time to transit, convert to distance and append to zonal data
        for (int i = 1; i <= zonalData.getRowCount(); i++) {
            float minDist = 9999; // upper limit for distance (in meters) to transit, default for places with no access to transit
            int origin = (int) zonalData.getValueAt(i, "ID_cell");
            for (int j = 1; j <= zonalData.getRowCount(); j++) {
                int dest = (int) zonalData.getValueAt(j, "ID_cell");
                // convert time in minutes to distance in meters using a speed of 5 km/h
                float distance = accessTimeMatrix.getValueAt(origin, dest) * 83.33f;
                if (distance > 0 & distance < minDist) {
                    minDist = distance;
                }
            }
            zonalData.setValueAt(i, "distanceToTransit", minDist);
        }
    }
}


