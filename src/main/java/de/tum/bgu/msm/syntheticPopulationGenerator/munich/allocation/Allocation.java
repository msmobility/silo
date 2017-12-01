package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;


import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import org.apache.log4j.Logger;

public class Allocation extends ModuleSynPop{

    private static final Logger logger = Logger.getLogger(Allocation.class);

    public Allocation(DataSetSynPop dataSetSynPop){
        super(dataSetSynPop);
    }

    @Override
    public void run(){
        logger.info("   Started allocation model.");
        generateHouseholdsPersonsDwellings();
        generateJobs();
        assignJobs();
        assignSchools();
        logger.info("   Completed allocation model.");

    }

    public void generateHouseholdsPersonsDwellings(){
        GenerateHouseholdsPersonsDwellings generate = new GenerateHouseholdsPersonsDwellings(dataSetSynPop);
        generate.run();
    }

    public void generateJobs(){

    }

    public void assignJobs(){

    }

    public void assignSchools(){

    }

}
