package run;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

/**
 * Implements SILO for the Maryland Statewide Transportation Model
 *
 * @author Rolf Moeckel
 * Created on Nov 22, 2013 in Wheaton, MD
 */

public class SiloMstm {

    private static Logger logger = Logger.getLogger(SiloMstm.class);


    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(Implementation.MARYLAND, args[0]);
        logger.info("Starting SILO program for MSTM");
        SiloModel model = new SiloModel(properties);
        model.runModel();
        logger.info("Finished SILO.");

    }
}
