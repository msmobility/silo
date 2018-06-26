package de.tum.bgu.msm.syntheticPopulationGenerator.munich.microlocation;

import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Dwelling;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;

import java.io.PrintWriter;
import java.util.*;

public class GenerateDwellingMicrolocation {

    private static final Logger logger = Logger.getLogger(GenerateDwellingMicrolocation.class);
    private static final double PENALTY = 0.5;
    private final SiloDataContainer dataContainer;
    private HashMap<Integer, Float> buildingX = new HashMap<>();
    private HashMap<Integer, Float> buildingY = new HashMap<>();
    private HashMap<Integer, HashMap<Integer,Double>> zoneBuildingMap = new HashMap<>();
    Map<Integer, Integer> dwellingCount = new HashMap<Integer, Integer>();
    Map<Integer, Integer> buildingZone = new HashMap<Integer, Integer>();
    Map<Integer, Double> buildingArea = new HashMap<>();
    Map<Integer, Float> zoneDensity = new HashMap<>();
    Map<Integer, Integer> dwellingsInTAZ = new HashMap<Integer, Integer>();

    public GenerateDwellingMicrolocation(SiloDataContainer dataContainer){
        this.dataContainer = dataContainer;
    }

    public void run() {
        logger.info("   Running module: dwelling microlocation");
        logger.info("Start parsing buildings information to hashmap");
        readBuidlingFile();
        densityCalculation();
        logger.info("Start Selecting the building to allocate the dwelling");
        //Select the building to allocate the dwelling
        int errorBuilding = 0;

        for (Dwelling dd: dataContainer.getRealEstateData().getDwellings()) {
            int zoneID = dd.getZone();
            if (zoneBuildingMap.get(zoneID) == null){
                dd.setCoord(new Coord(0.0,0.0));
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
            dd.setCoord(new Coord(buildingX.get(selectedBuildingID),buildingY.get(selectedBuildingID)));

            //for test
            if (dwellingCount.get(selectedBuildingID) ==null){
                dwellingCount.put(selectedBuildingID,0);
            }

            int count = dwellingCount.get(selectedBuildingID);
            dwellingCount.put(selectedBuildingID,(count + 1));
            //for test

        }

        logger.info("Number of errorBuilding:" + errorBuilding);


        //for test
        String filetest = "C:/Users/Qin/Desktop/dwellingMicrolocation.csv";
        PrintWriter pwt = SiloUtil.openFileForSequentialWriting(filetest, false);
        pwt.println("dwellingid,area,zone,x,y,count");
        for (int id: buildingX.keySet()){
            pwt.print(id);
            pwt.print(",");
            pwt.print(buildingArea.get(id));
            pwt.print(",");
            pwt.print(buildingZone.get(id));
            pwt.print(",");
            pwt.print(buildingX.get(id));
            pwt.print(",");
            pwt.print(buildingY.get(id));
            pwt.print(",");
            if(dwellingCount.get(id) ==null){
                pwt.println(0);
            }else{
                pwt.println(dwellingCount.get(id));
            }

        }
        pwt.close();
        //for test

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

    private void densityCalculation() {
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
