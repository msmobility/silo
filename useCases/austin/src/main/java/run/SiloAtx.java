package run;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloModel;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.properties.Properties;
import org.apache.log4j.Logger;

/**
 * Implements SILO for the Austin Texas Area
 *
 * @author Ty Wellik
 * Created on Jan 5, 2019 in Austin, TX
 */

public class SiloAtx {

    private static Logger logger = Logger.getLogger(SiloAtx.class);


    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(Implementation.AUSTIN, args[0]);
        logger.info("Starting SILO program for Austin Area");
        SiloModel model = new SiloModel(properties);
        model.runModel();
        logger.info("Finished SILO.");

    }
}
