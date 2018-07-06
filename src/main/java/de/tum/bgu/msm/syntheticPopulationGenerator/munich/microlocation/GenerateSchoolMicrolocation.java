package de.tum.bgu.msm.syntheticPopulationGenerator.munich.microlocation;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Job;
import de.tum.bgu.msm.data.Occupation;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.munich.MunichZone;
import de.tum.bgu.msm.models.transportModel.matsim.SiloMatsimUtils;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;

import java.io.PrintWriter;
import java.util.*;

public class GenerateSchoolMicrolocation {

    private static final Logger logger = Logger.getLogger(GenerateSchoolMicrolocation.class);

    private final SiloDataContainer dataContainer;
    private final DataSetSynPop dataSetSynPop;
    private Map<Integer, Float> schoolX = new HashMap<>();
    private Map<Integer, Float> schoolY = new HashMap<>();
    Map<Integer, Integer> schoolZone = new HashMap<>();
    Map<Integer, Map<Integer,Map<Integer,Integer>>> zoneSchoolTypeSchoolLocationCapacity = new HashMap<>();


    public GenerateSchoolMicrolocation(SiloDataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }

    public void run() {
        logger.info("   Running module: school microlocation");
        logger.info("   Start parsing schools information to hashmap");
        readSchoolFile();
        logger.info("   Start Selecting the school to allocate the student");
        //Select the school to allocate the student
        int errorSchool = 0;
        for (Person pp : dataContainer.getHouseholdData().getPersons()) {
            if (pp.getOccupation() == 3) {
                int zoneID = pp.getSchoolPlace();
                int schoolType = pp.getSchoolType();

                if (zoneSchoolTypeSchoolLocationCapacity.get(zoneID).get(schoolType)==null){
                    pp.setSchoolCoord(SiloMatsimUtils.getRandomCoordinateInGeometry(dataSetSynPop.getZoneFeatureMap().get(zoneID)));
                    errorSchool++;
                    continue;
                }
                int selectedSchoolID = SiloUtil.select(zoneSchoolTypeSchoolLocationCapacity.get(zoneID).get(schoolType));
                int remainingCapacity = zoneSchoolTypeSchoolLocationCapacity.get(zoneID).get(schoolType).get(selectedSchoolID) - 1;
                zoneSchoolTypeSchoolLocationCapacity.get(zoneID).get(schoolType).put(selectedSchoolID, remainingCapacity);
                pp.setSchoolCoord(new Coord(schoolY.get(selectedSchoolID), schoolY.get(selectedSchoolID)));
            }
        }
        logger.warn( errorSchool +"   Dwellings cannot find specific building location. Their coordinates are assigned randomly in TAZ" );
        logger.info("   Finished school microlocation.");
    }



    private void readSchoolFile() {

        for (int zone : dataSetSynPop.getTazs()){
            Map<Integer,Map<Integer,Integer>> schoolLocationListForThisSchoolType = new HashMap<>();
            for (int type = 1 ; type <= 3; type++){
                Map<Integer,Integer> schoolCapacity = new HashMap<>();
                schoolLocationListForThisSchoolType.put(type,schoolCapacity);
            }
            zoneSchoolTypeSchoolLocationCapacity.put(zone,schoolLocationListForThisSchoolType);
        }
        
        for (int row = 1; row <= PropertiesSynPop.get().main.schoolLocationlist.getRowCount(); row++) {

            int id = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"OBJECTID");
            int zone = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"zoneID");
            float xCoordinate = PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"x");
            float yCoordinate = PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"y");
            int schoolCapacity = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"schoolCapacity");
            int schoolType = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"schoolType");

            schoolZone.put(id,zone);
            schoolX.put(id,xCoordinate);
            schoolY.put(id,yCoordinate);

            if (zoneSchoolTypeSchoolLocationCapacity.get(zone) != null){
                zoneSchoolTypeSchoolLocationCapacity.get(zone).get(schoolType).put(id,schoolCapacity);
            }else{
                logger.info("Error zoneID" + zone);
            }

        }
    }
}
