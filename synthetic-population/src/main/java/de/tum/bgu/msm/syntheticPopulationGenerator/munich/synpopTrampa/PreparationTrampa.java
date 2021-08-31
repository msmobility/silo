package de.tum.bgu.msm.syntheticPopulationGenerator.munich.synpopTrampa;

import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.Preparation;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.ReadZonalData;
import org.apache.log4j.Logger;

public class PreparationTrampa extends ModuleSynPop {

    private static final Logger logger = Logger.getLogger(Preparation.class);

    public PreparationTrampa(DataSetSynPop dataSetSynPop) {
        super(dataSetSynPop);
    }

    @Override
    public void run() {

        logger.info("   Started input data preparation.");
        readZonalData();
        createFrequencyMatrix();
        logger.info("   Completed input data preparation.");
    }

    private void readZonalData() {
        new ReadZonalData(dataSetSynPop).run();
    }

    private void createFrequencyMatrix() {
        new PrepareFrequencyMatrixTrampa(dataSetSynPop).run();
    }
}

