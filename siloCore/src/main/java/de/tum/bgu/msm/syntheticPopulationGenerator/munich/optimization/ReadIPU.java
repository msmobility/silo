package de.tum.bgu.msm.syntheticPopulationGenerator.munich.optimization;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import org.apache.log4j.Logger;

public class ReadIPU {

    private static final Logger logger = Logger.getLogger(ReadIPU.class);

    private final DataSetSynPop dataSetSynPop;

    public ReadIPU(DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
    }

    public void run(){

        logger.info("   Reading the weights matrix");

        dataSetSynPop.setWeights(SiloUtil.readCSVfile2(PropertiesSynPop.get().main.weightsFileName));
        dataSetSynPop.getWeights().buildIndex(dataSetSynPop.getWeights().getColumnPosition("ID"));

        logger.info("   Finishing reading the results from the IPU");

    }
}
