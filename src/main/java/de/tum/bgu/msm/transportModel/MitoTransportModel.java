package de.tum.bgu.msm.transportModel;

import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.MitoModel;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.MitoHousehold;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.io.input.InputFeed;
import org.apache.log4j.Logger;

import java.util.Map;
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
        mito.setRandomNumberGenerator(SiloUtil.getRandomObject());
        setBaseDirectory(baseDirectory);
    }

    @Override
    public void feedData(Map<Integer, Zone> zones, Matrix hwySkim, Matrix transitSkim, Map<Integer, MitoHousehold> households) {
        logger.info("  SILO data being sent to MITO");
        InputFeed feed = new InputFeed(zones, hwySkim, transitSkim, households);
        mito.feedData(feed);
    }


    private void setBaseDirectory (String baseDirectory) {
        mito.setBaseDirectory(baseDirectory);
    }


    @Override
    public void runTransportModel(int year) {
	    MitoModel.setScenarioName (SiloUtil.scenarioName);

        logger.info("  Running travel demand model MITO for the year " + year);
        mito.runModel();
    }

}
