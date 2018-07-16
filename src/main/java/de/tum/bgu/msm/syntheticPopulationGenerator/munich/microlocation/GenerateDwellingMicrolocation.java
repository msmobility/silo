package de.tum.bgu.msm.syntheticPopulationGenerator.munich.microlocation;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.data.munich.MunichZone;
import de.tum.bgu.msm.models.transportModel.matsim.SiloMatsimUtils;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;

import java.io.PrintWriter;
import java.util.*;

public class GenerateDwellingMicrolocation {

    private static final Logger logger = Logger.getLogger(GenerateDwellingMicrolocation.class);
    private static final double PENALTY = 0.5;
    private final SiloDataContainer dataContainer;
    private final DataSetSynPop dataSetSynPop;
    private HashMap<Integer, Float> buildingX = new HashMap<>();
    private HashMap<Integer, Float> buildingY = new HashMap<>();
    private HashMap<Integer, HashMap<Integer,Double>> zoneBuildingMap = new HashMap<>();
    Map<Integer, Integer> buildingZone = new HashMap<Integer, Integer>();
    Map<Integer, Double> buildingArea = new HashMap<>();
    Map<Integer, Float> zoneDensity = new HashMap<>();
    Map<Integer, Integer> dwellingsInTAZ = new HashMap<Integer, Integer>();

    public GenerateDwellingMicrolocation(SiloDataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }

    public void run() {
        logger.info("   Running module: dwelling microlocation");
        logger.info("   Start parsing buildings information to hashmap");
        readBuidlingFile();
        calculateDensity();
        logger.info("   Start Selecting the building to allocate the dwelling");
        //Select the building to allocate the dwelling
        int errorBuilding = 0;
        for (Dwelling dd: dataContainer.getRealEstateData().getDwellings()) {
            int zoneID = dd.getZone();
            if (zoneBuildingMap.get(zoneID) == null){
                dd.setLocation(SiloMatsimUtils.getRandomCoordinateInGeometry(dataSetSynPop.getZoneFeatureMap().get(zoneID)));
                errorBuilding++;
                continue;
            }
            int selectedBuildingID = SiloUtil.select(zoneBuildingMap.get(zoneID));
            double remainingCapacity = (zoneBuildingMap.get(zoneID).get(selectedBuildingID))*PENALTY;//-zoneDensity.get(zoneID);
            if (remainingCapacity > 0) {
                zoneBuildingMap.get(zoneID).put(selectedBuildingID, remainingCapacity);
            } else {
                zoneBuildingMap.get(zoneID).put(selectedBuildingID, 0.0);
            }
            dd.setLocation(new Coord(buildingX.get(selectedBuildingID),buildingY.get(selectedBuildingID)));
        }

        logger.warn( errorBuilding +"   Dwellings cannot find specific building location. Their coordinates are assigned randomly in TAZ" );
        logger.info("   Finished dwelling microlocation.");
    }


    private void readBuidlingFile() {
        //parse buildings information to hashmap
        for (int row = 1; row <= PropertiesSynPop.get().main.buildingLocationlist.getRowCount(); row++) {
            //parse building id, area, x and y coordinate
            int id = (int) PropertiesSynPop.get().main.buildingLocationlist.getValueAt(row,"buildingID");
            int zone = (int) PropertiesSynPop.get().main.buildingLocationlist.getValueAt(row,"zoneID");
            double area = PropertiesSynPop.get().main.buildingLocationlist.getValueAt(row,"Area");
            float xCoordinate = PropertiesSynPop.get().main.buildingLocationlist.getValueAt(row,"X");
            float yCoordinate = PropertiesSynPop.get().main.buildingLocationlist.getValueAt(row,"Y");
            buildingX.put(id,xCoordinate);
            buildingY.put(id,yCoordinate);
            buildingArea.put(id,area);
            buildingZone.put(id,zone);
            //put all buildings with the same zoneID into one building list
            if (zoneBuildingMap.get(zone) == null){
                HashMap<Integer, Double> buildingAreaList = new HashMap<Integer, Double>();
                zoneBuildingMap.put(zone,buildingAreaList);
            }
            zoneBuildingMap.get(zone).put(id,area);
        }
    }

    private void calculateDensity() {
        for (Dwelling dd: dataContainer.getRealEstateData().getDwellings()) {
            int zoneID = dd.getZone();

            if (dwellingsInTAZ.get(zoneID) == null) {
                dwellingsInTAZ.put(zoneID, 0);
            }

            int numberOfDwellings = dwellingsInTAZ.get(zoneID);
            dwellingsInTAZ.put(zoneID,numberOfDwellings+1);

        }

        for (int zone: zoneBuildingMap.keySet()){
            if ((zoneBuildingMap.get(zone) != null)&( dwellingsInTAZ.get(zone) != null)) {
                float density = getSum(zoneBuildingMap.get(zone).values())/dwellingsInTAZ.get(zone);
                zoneDensity.put(zone, density);
            }
        }
    }


    private static float getSum(Collection<? extends Number> values) {
        float sm = 0.f;
        for (Number value : values) {
            sm += value.doubleValue();
        }
        return sm;
    }

}
