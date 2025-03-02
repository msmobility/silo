package de.tum.bgu.msm.syntheticPopulationGenerator.manchester.microlocation;

import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.api.feature.simple.SimpleFeature;
import org.matsim.core.utils.gis.ShapeFileReader;

import java.util.HashMap;
import java.util.Map;

public class Microlocation extends ModuleSynPop {
    private static final Logger logger = LogManager.getLogger(Microlocation.class);
    private final DataContainerWithSchools dataContainer;

    public Microlocation(DataSetSynPop dataSetSynPop, DataContainerWithSchools dataContainer){
        super(dataSetSynPop);
        this.dataContainer = dataContainer;
    }

    @Override
    public void run(){
        logger.info("   Started microlocation model.");

        if (PropertiesSynPop.get().main.runMicrolocation) {
            generateDwellingMicrolocation();
            //TODO: job microlocation for Manchester
            //generateJobMicrolocation();
        }

        logger.info("   Completed microlocation model.");

    }


    private void generateJobMicrolocation() {
        new GenerateJobMicrolocation(dataContainer, dataSetSynPop).run();

    }

    private void generateDwellingMicrolocation() {
        new GenerateDwellingMicrolocation(dataContainer,dataSetSynPop).run();

    }

}
