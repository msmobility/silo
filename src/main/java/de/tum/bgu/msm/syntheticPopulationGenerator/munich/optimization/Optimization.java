package de.tum.bgu.msm.syntheticPopulationGenerator.munich.optimization;


import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import org.apache.log4j.Logger;

public class Optimization extends ModuleSynPop{

    private static final Logger logger = Logger.getLogger(Optimization.class);

    public Optimization(DataSetSynPop dataSetSynPop){
        super(dataSetSynPop);
    }

    @Override
    public void run(){
        logger.info("   Started optimization model.");
        if (PropertiesSynPop.get().main.runIPU){
            if (PropertiesSynPop.get().main.twoGeographicalAreasIPU) {
                applyIPUbyCountyAndCity();
            } else {
                applyIPUbyCity();
            }
        } else {
            readIPUresults();
        }
        calculateErrors();
        logger.info("   Completed optimization model.");

    }

    private void applyIPUbyCountyAndCity(){
        IPUbyCountyAndCity ipu = new IPUbyCountyAndCity(dataSetSynPop);
        ipu.run();
    }

    private void applyIPUbyCity(){
        IPUbyCity ipu = new IPUbyCity(dataSetSynPop);
        ipu.run();
    }

    private void readIPUresults(){
        ReadIPU ipu = new ReadIPU(dataSetSynPop);
        ipu.run();
    }

    private void calculateErrors(){
        CalculateIPUerrors errors = new CalculateIPUerrors(dataSetSynPop);
        errors.run();
    }
}
