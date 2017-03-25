package de.tum.bgu.msm.transportModel;

import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.MitoHousehold;
import de.tum.bgu.msm.MitoModel;
import org.apache.log4j.Logger;

import java.util.ResourceBundle;

/**
 * Implementation of Transport Model Interface for MITO
 * @author Rolf Moeckel
 * Created on February 18, 2017 in Munich, Germany
 *
 */

public class MitoTransportModel implements TransportModelI {
    private static final Logger logger = Logger.getLogger( MitoTransportModel.class );
    private MitoModel mito;


    public MitoTransportModel(ResourceBundle rb, String baseDirectory) {
        this.mito = new MitoModel(rb);
        setBaseDirectory(baseDirectory);
    }


    public void feedData(int[] zones, Matrix autoTravelTimes, Matrix transitTravelTimes, MitoHousehold[] mitoHouseholds,
                         int[] retailEmplByZone, int[] officeEmplByZone, int[] otherEmplByZone, int[] totalEmplByZone,
                         float[] sizeOfZonesInAcre) {
        logger.info("  SILO data being sent to MITO");
        mito.feedData(zones, autoTravelTimes, transitTravelTimes, mitoHouseholds, retailEmplByZone,
                officeEmplByZone, otherEmplByZone, totalEmplByZone, sizeOfZonesInAcre);
    }


    public void setScenarioName (String scenarioName) {
        mito.setScenarioName (scenarioName);
    }


    private void setBaseDirectory (String baseDirectory) {
        mito.setBaseDirectory(baseDirectory);
    }


    @Override
    public void runTransportModel(int year) {

        logger.info("  Running travel demand model MITO for the year " + year);
        mito.runModel();
    }

    @Override
    public void writeOutSocioEconomicDataForMstm(int year) {
        // not doing anything.
    }
    @Override
    public void tripGeneration() {
        // not doing anything.
    }

}
