package de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.common.matrix.Matrix;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.SchoolData;
import de.tum.bgu.msm.schools.SchoolUtils;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import omx.OmxFile;
import omx.OmxLookup;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GenerateSchoolMicrolocations {

    private static final Logger logger = Logger.getLogger(GenerateSchoolMicrolocations.class);

    private final DataSetSynPop dataSetSynPop;
    private final DataContainerWithSchools dataContainer;


    public GenerateSchoolMicrolocations(DataContainerWithSchools dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }

    public void run() {
        createSchools();
    }


    private void createSchools() {

        Map<Integer, Map<Integer,Map<Integer,Integer>>> zoneSchoolTypeSchoolLocationCapacity = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> zoneSchoolTypeSchoolLocationVacancy = new HashMap<>();
        for (int type = 1 ; type <= 3; type++){
            Map<Integer,Map<Integer,Integer>> schoolLocationListForThisSchoolType = new HashMap<>();
            Map<Integer, Integer> schoolLocationListForThisSchoolTypeVacancy = new HashMap<>();
            for (int zone : dataSetSynPop.getTazs()){
                Map<Integer,Integer> schoolCapacity = new HashMap<>();
                schoolLocationListForThisSchoolType.put(zone,schoolCapacity);
                schoolLocationListForThisSchoolTypeVacancy.put(zone,0);
            }
            zoneSchoolTypeSchoolLocationCapacity.put(type,schoolLocationListForThisSchoolType);
            zoneSchoolTypeSchoolLocationVacancy.put(type,schoolLocationListForThisSchoolTypeVacancy);
        }

        SchoolData schoolData = dataContainer.getSchoolData();

        for (int row = 1; row <= PropertiesSynPop.get().main.schoolLocationlist.getRowCount(); row++) {

            int id = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"OBJECTID");
            int zone = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"zoneID");
            float xCoordinate = PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"x");
            float yCoordinate = PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"y");
            int schoolCapacity = (int) ((PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"schoolCapacity") + 2) * 1.2);
            int schoolType = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"schoolType");

            Coordinate coordinate = new Coordinate(xCoordinate,yCoordinate);
            schoolData.addSchool(SchoolUtils.getFactory().createSchool(id, schoolType, schoolCapacity,0,coordinate, zone));

            zoneSchoolTypeSchoolLocationCapacity.get(schoolType).get(zone).put(id,schoolCapacity);
            int previousCapacity = zoneSchoolTypeSchoolLocationVacancy.get(schoolType).get(zone);
            zoneSchoolTypeSchoolLocationVacancy.get(schoolType).put(zone, schoolCapacity + previousCapacity);

        }
        dataSetSynPop.setZoneSchoolTypeSchoolLocationCapacity(zoneSchoolTypeSchoolLocationCapacity);
        dataSetSynPop.setZoneSchoolTypeSchoolLocationVacancy(zoneSchoolTypeSchoolLocationVacancy);
    }
}
