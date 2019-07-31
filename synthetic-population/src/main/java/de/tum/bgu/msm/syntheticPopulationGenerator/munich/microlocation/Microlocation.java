package de.tum.bgu.msm.syntheticPopulationGenerator.munich.microlocation;

import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.DataContainerWithSchoolsImpl;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.ModuleSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import org.apache.log4j.Logger;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.util.HashMap;
import java.util.Map;

public class Microlocation extends ModuleSynPop {
    private static final Logger logger = Logger.getLogger(Microlocation.class);
    private final DataContainerWithSchools dataContainer;

    public Microlocation(DataSetSynPop dataSetSynPop, DataContainerWithSchools dataContainer){
        super(dataSetSynPop);
        this.dataContainer = dataContainer;
    }

    @Override
    public void run(){
        logger.info("   Started microlocation model.");

        String zoneShapeFile = Properties.get().geo.zoneShapeFile;
        Map<Integer, SimpleFeature> zoneFeatureMap = new HashMap<>();
        for (SimpleFeature feature: ShapeFileReader.getAllFeatures(zoneShapeFile)) {
            int zoneId = Integer.parseInt(feature.getAttribute("id").toString());
            zoneFeatureMap.put(zoneId,feature);
        }
        dataSetSynPop.setZoneFeatureMap(zoneFeatureMap);

        if (PropertiesSynPop.get().main.runMicrolocation) {
            generateDwellingMicrolocation();
            generateJobMicrolocation();
            generateSchoolMicrolocation();
        }

        logger.info("   Completed microlocation model.");

    }


    private void generateJobMicrolocation() {
        new GenerateJobMicrolocation(dataContainer, dataSetSynPop).run();

    }

    private void generateDwellingMicrolocation() {
        new GenerateDwellingMicrolocation(dataContainer,dataSetSynPop).run();

    }

    private void generateSchoolMicrolocation() {
        new GenerateSchoolMicrolocation(dataContainer,dataSetSynPop).run();

    }
}
