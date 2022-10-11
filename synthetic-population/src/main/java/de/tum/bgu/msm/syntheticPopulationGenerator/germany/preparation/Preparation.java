package de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation;

import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;


public class Preparation extends ModuleSynPop {

    private static final Logger logger = Logger.getLogger(Preparation.class);



    public Preparation(DataSetSynPop dataSetSynPop){
        super(dataSetSynPop);
    }

    @Override
    public void run(){

        logger.info("   Started input data preparation.");
        readZonalData();
        if (PropertiesSynPop.get().main.runAllocation) {
            readMicroData();
            translatePersonMicroData();
            writeMicroData();
            if (PropertiesSynPop.get().main.runIPU) {
                createFrequencyMatrix();
            }
        }
        logger.info("   Completed input data preparation.");
    }


    private void readZonalData(){
        new ReadZonalData(dataSetSynPop).run();
    }


    private void readMicroData(){
        new ReadMicroData(dataSetSynPop).run();
    }


    private void translatePersonMicroData(){
        new TranslateMicroDataToCode(dataSetSynPop).run();
    }


    private void createFrequencyMatrix(){
        new PrepareFrequencyMatrix(dataSetSynPop).run();
    }


    private void writeMicroData(){
        SiloUtil.writeTableDataSet(dataSetSynPop.getPersonDataSet(), PropertiesSynPop.get().main.microPersonsFileName);
        SiloUtil.writeTableDataSet(dataSetSynPop.getHouseholdDataSet(), PropertiesSynPop.get().main.microHouseholdsFileName);
    }

}
