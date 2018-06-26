package de.tum.bgu.msm.syntheticPopulationGenerator.munich.microlocation;

import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import org.apache.log4j.Logger;

public class Microlocation extends ModuleSynPop {
    private static final Logger logger = Logger.getLogger(Microlocation.class);
    private final SiloDataContainer dataContainer;

    public Microlocation(DataSetSynPop dataSetSynPop, SiloDataContainer dataContainer){
        super(dataSetSynPop);
        this.dataContainer = dataContainer;
    }

    @Override
    public void run(){
        logger.info("   Started microlocation model.");
        if (PropertiesSynPop.get().main.runDwellingMicrolocation) {
            generateDwellingMicrolocation();
        }

        if (PropertiesSynPop.get().main.runJobMicrolocation) {
            generateJobMicrolocation();
        }

        if (PropertiesSynPop.get().main.runSchoolMicrolocation) {
            generateSchoolMicrolocation();
        }

        logger.info("   Completed microlocation model.");

    }


    private void generateJobMicrolocation() {
        new GenerateJobMicrolocation(dataContainer, dataSetSynPop).run();

    }

    private void generateDwellingMicrolocation() {
        new GenerateDwellingMicrolocation(dataContainer).run();

    }

    private void generateSchoolMicrolocation() {
        new GenerateSchoolMicrolocation(dataContainer,dataSetSynPop).run();

    }
}
