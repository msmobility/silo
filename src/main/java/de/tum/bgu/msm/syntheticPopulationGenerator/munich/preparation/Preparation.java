package de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
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
        readMicroData();
        translatePersonMicroData();
        checkHouseholdRelationships();
        writeMicroData();
        createFrequencyMatrix();
        logger.info("   Completed input data preparation.");
    }


    private void readZonalData(){
        ReadZonalData read = new ReadZonalData(dataSetSynPop);
        read.run();
    }


    private void readMicroData(){
        ReadMicroData read = new ReadMicroData(dataSetSynPop);
        read.run();
    }


    private void checkHouseholdRelationships(){
        CheckHouseholdRelationship check = new CheckHouseholdRelationship(dataSetSynPop);
        check.run();
    }


    private void translatePersonMicroData(){
        TranslateMicroDataToCode translate = new TranslateMicroDataToCode(dataSetSynPop);
        translate.run();
    }


    private void createFrequencyMatrix(){
        PrepareFrequencyMatrix prepareFrequencyMatrix = new PrepareFrequencyMatrix(dataSetSynPop);
        prepareFrequencyMatrix.run();
    }


    private void writeMicroData(){
        SiloUtil.writeTableDataSet(dataSetSynPop.getPersonDataSet(), PropertiesSynPop.get().main.microPersonsFileName);
        SiloUtil.writeTableDataSet(dataSetSynPop.getHouseholdDataSet(), PropertiesSynPop.get().main.microHouseholdsFileName);
        SiloUtil.writeTableDataSet(dataSetSynPop.getDwellingDataSet(), PropertiesSynPop.get().main.microDwellingsFileName);
    }

}
