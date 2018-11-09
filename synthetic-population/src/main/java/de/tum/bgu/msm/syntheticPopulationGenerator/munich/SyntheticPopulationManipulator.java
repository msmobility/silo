package de.tum.bgu.msm.syntheticPopulationGenerator.munich;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

public class SyntheticPopulationManipulator {

    static Logger logger = Logger.getLogger(SyntheticPopulationManipulator.class);

    public static void main(String[] args) {

        Properties properties = SiloUtil.siloInitialization(Implementation.MUNICH, args[0]);

        logger.info("Load silo data container");
        SiloDataContainer siloDataContainer = SiloDataContainer.loadSiloDataContainer(properties);

        //manipulate objects if needed

        logger.info("Write out synthetic population");
        SummarizeData.writeOutSyntheticPopulation(Properties.get().main.implementation.BASE_YEAR, siloDataContainer);


    }
}
