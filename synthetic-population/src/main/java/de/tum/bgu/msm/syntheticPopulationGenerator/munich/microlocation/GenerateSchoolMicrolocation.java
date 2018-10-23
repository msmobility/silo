package de.tum.bgu.msm.syntheticPopulationGenerator.munich.microlocation;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.*;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.school.School;
import de.tum.bgu.msm.data.school.SchoolUtils;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class GenerateSchoolMicrolocation {

    private static final Logger logger = Logger.getLogger(GenerateSchoolMicrolocation.class);

    private final SiloDataContainer dataContainer;
    private final DataSetSynPop dataSetSynPop;
    Map<Integer, Map<Integer,Map<Integer,Integer>>> zoneSchoolTypeSchoolLocationCapacity = new HashMap<>();


    public GenerateSchoolMicrolocation(SiloDataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }

    public void run() {
        logger.info("   Running module: school microlocation");
        logger.info("   Start creating school objects from school location list");
        createSchools();
        logger.info("   Start Selecting the school to allocate the student");
        //Select the school to allocate the student
        int errorSchool = 0;
        for (Person pp : dataContainer.getHouseholdData().getPersons()) {
            if (pp.getOccupation() == Occupation.STUDENT) {
                int zoneID = pp.getSchoolPlace();
                int schoolType = pp.getSchoolType();
                Zone zone = dataContainer.getGeoData().getZones().get(zoneID);

                if (zoneSchoolTypeSchoolLocationCapacity.get(zoneID).get(schoolType) == null){
                	pp.setSchoolCoordinate(zone.getRandomCoordinate(), zoneID);
                    errorSchool++;
                    continue;
                }
                int selectedSchoolID = SiloUtil.select(zoneSchoolTypeSchoolLocationCapacity.get(zoneID).get(schoolType));
                School school = dataContainer.getSchoolData().getSchoolFromId(selectedSchoolID);
                int remainingCapacity = zoneSchoolTypeSchoolLocationCapacity.get(zoneID).get(schoolType).get(selectedSchoolID) - 1;
                zoneSchoolTypeSchoolLocationCapacity.get(zoneID).get(schoolType).put(selectedSchoolID, remainingCapacity);
                pp.setSchoolId(school.getId());
                pp.setSchoolCoordinate(((MicroLocation)school).getCoordinate(), school.getZoneId());
            }
        }

        for (School ss : dataContainer.getSchoolData().getSchools()){
            int finalRemainingCapacity = zoneSchoolTypeSchoolLocationCapacity.get(ss.getZoneId()).get(ss.getType()).get(ss.getId());
            ss.setOccupancy(ss.getCapacity()-finalRemainingCapacity);
        }

        logger.warn( errorSchool +"   Students cannot find specific school location. Their coordinates are assigned randomly in TAZ" );
        logger.info("   Finished school microlocation.");
    }



    private void createSchools() {

        for (int zone : dataSetSynPop.getTazs()){
            Map<Integer,Map<Integer,Integer>> schoolLocationListForThisSchoolType = new HashMap<>();
            for (int type = 1 ; type <= 3; type++){
                Map<Integer,Integer> schoolCapacity = new HashMap<>();
                schoolLocationListForThisSchoolType.put(type,schoolCapacity);
            }
            zoneSchoolTypeSchoolLocationCapacity.put(zone,schoolLocationListForThisSchoolType);
        }

        SchoolDataManager schoolDataManager = dataContainer.getSchoolData();

        for (int row = 1; row <= PropertiesSynPop.get().main.schoolLocationlist.getRowCount(); row++) {

            int id = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"OBJECTID");
            int zone = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"zoneID");
            float xCoordinate = PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"x");
            float yCoordinate = PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"y");
            int schoolCapacity = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"schoolCapacity");
            int schoolType = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"schoolType");

            Coordinate coordinate = new Coordinate(xCoordinate,yCoordinate);
            schoolDataManager.addSchool(SchoolUtils.getFactory().createSchool(id, schoolType, schoolCapacity,0,coordinate, zone));

            if (zoneSchoolTypeSchoolLocationCapacity.get(zone) != null){
                zoneSchoolTypeSchoolLocationCapacity.get(zone).get(schoolType).put(id,schoolCapacity);
            }else{
                logger.info("Error zoneID" + zone);
            }

        }
    }
}
