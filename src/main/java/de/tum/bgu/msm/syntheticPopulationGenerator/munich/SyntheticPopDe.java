package de.tum.bgu.msm.syntheticPopulationGenerator.munich;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.SummarizeData;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.CreateCarOwnershipModel;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.SyntheticPopI;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation.Allocation;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.optimization.Optimization;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.Preparation;
import org.apache.log4j.Logger;

import java.util.ResourceBundle;


/**
 * Generates a synthetic population for a study area in Germany
 * @author Ana Moreno (TUM)
 * Created on May 12, 2016 in Munich
 *
 */
public class SyntheticPopDe implements SyntheticPopI {

    public static final Logger logger = Logger.getLogger(SyntheticPopDe.class);
    private final DataSetSynPop dataSetSynPop;

    private ResourceBundle rb;

    public SyntheticPopDe(DataSetSynPop dataSetSynPop) {
        this.rb = rb;
        this.dataSetSynPop = dataSetSynPop;
    }


    public void runSP(){
        //method to create the synthetic population at the base year
        if (!PropertiesSynPop.get().main.runSyntheticPopulation){
            return;
        }

        logger.info("   Starting to create the synthetic population.");
        createDirectoryForOutput();
        SiloDataContainer dataContainer = SiloDataContainer.createEmptySiloDataContainer(Implementation.MUNICH);
        long startTime = System.nanoTime();

        logger.info("Running Module: Reading inputs");
        new Preparation(dataSetSynPop).run();

        logger.info("Running Module: Optimization IPU");
        new Optimization(dataSetSynPop).run();

        logger.info("Running Module: Allocation");
        new Allocation(dataSetSynPop, dataContainer).run();

        logger.info("Running Module: Car ownership");
        new CreateCarOwnershipModel(dataContainer).run();

        logger.info("Summary of the synthetic population");
        SummarizeData.writeOutSyntheticPopulationDE(Properties.get().main.implementation.BASE_YEAR, dataContainer);

        long estimatedTime = System.nanoTime() - startTime;
        logger.info("   Finished creating the synthetic population. Elapsed time: " + estimatedTime);
    }


    private void createDirectoryForOutput() {
        SiloUtil.createDirectoryIfNotExistingYet("microData");
        SiloUtil.createDirectoryIfNotExistingYet("microData/interimFiles");
    }

}
