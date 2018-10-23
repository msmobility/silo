package de.tum.bgu.msm.syntheticPopulationGenerator;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.capeTown.SyntheticPopCT;
import de.tum.bgu.msm.syntheticPopulationGenerator.maryland.SyntheticPopUs;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.SyntheticPopDe;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.ResourceBundle;

public class SyntheticPopulationManipulator {

    static Logger logger = Logger.getLogger(SyntheticPopulationManipulator.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(args[0], Implementation.MUNICH);

        logger.info("Load silo data container");
        SiloDataContainer siloDataContainer = SiloDataContainer.loadSiloDataContainer(properties);

        //manipulate objects if needed

        logger.info("Write out synthetic population");
        SummarizeData.writeOutSyntheticPopulation(Properties.get().main.implementation.BASE_YEAR, siloDataContainer);


    }
}
