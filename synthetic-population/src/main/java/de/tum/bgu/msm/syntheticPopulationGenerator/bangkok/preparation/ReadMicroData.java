package de.tum.bgu.msm.syntheticPopulationGenerator.bangkok.preparation;


import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

public class ReadMicroData {

    private static final Logger logger = Logger.getLogger(ReadMicroData.class);

    private final DataSetSynPop dataSetSynPop;

    private TableDataSet personDataSet;
    private TableDataSet householdDataSet;


    public ReadMicroData(DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
    }

    public void run(){
        logger.info("   Starting to read the micro data");
        personDataSet = SiloUtil.readCSVfile(PropertiesSynPop.get().main.microDataPersons);
        householdDataSet = SiloUtil.readCSVfile(PropertiesSynPop.get().main.microDataHouseholds);
        dataSetSynPop.setPersonDataSet(personDataSet);
        dataSetSynPop.setHouseholdDataSet(householdDataSet);
    }


}
