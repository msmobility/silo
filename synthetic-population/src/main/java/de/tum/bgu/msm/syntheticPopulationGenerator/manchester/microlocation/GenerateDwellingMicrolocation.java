package de.tum.bgu.msm.syntheticPopulationGenerator.manchester.microlocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.ManchesterDwellingTypes;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobFactory;
import de.tum.bgu.msm.health.NoiseDwellingMCR;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GenerateDwellingMicrolocation {

    private static final Logger logger = LogManager.getLogger(GenerateDwellingMicrolocation.class);
    private final DataContainer dataContainer;
    private Map<Long, Coordinate> buildingCoord = new HashMap<>();
    private Map<Integer, Map<String, List<Long>>> zone2ddType2buildingIdMap = new HashMap<>();
    Map<Long, Integer> buildingZone = new HashMap<>();

    public GenerateDwellingMicrolocation(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataContainer = dataContainer;
    }

    public void run() {
        logger.info("   Running module: dwelling microlocation");
        logger.info("   Start parsing buildings information to hashmap");
        readBuidlingFile(PropertiesSynPop.get().main.microDwellingsFileName);
        logger.info("   Start Selecting the building to allocate the dwelling");
        //Select the building to allocate the dwelling
        int missingType = 0;
        int errorBuilding = 0;
        for (Dwelling dd: dataContainer.getRealEstateDataManager().getDwellings()) {
            int zoneID = dd.getZoneId();
            String ddtype = dd.getType().toString();
            Zone zone = dataContainer.getGeoData().getZones().get(zoneID);
            if (zone2ddType2buildingIdMap.get(zoneID) == null){
                dd.setCoordinate(zone.getRandomCoordinate(SiloUtil.getRandomObject()));
                errorBuilding++;
                continue;
            }

            long selectedBuildingID;
            List<Long> buildingIds = zone2ddType2buildingIdMap.get(zoneID).get(ddtype);

            if (buildingIds == null || buildingIds.isEmpty()) {
                missingType++;
                ddtype = "Dwelling";
                buildingIds = zone2ddType2buildingIdMap.get(zoneID).get(ddtype);
                if (buildingIds == null || buildingIds.isEmpty()) {
                   errorBuilding++;
                   logger.warn("No dwelling type: " + ddtype + " found in zone: " + zoneID);
                   continue;
                }

            }

            selectedBuildingID = buildingIds.get(SiloUtil.getRandomObject().nextInt(buildingIds.size()));
            zone2ddType2buildingIdMap.get(zoneID).get(ddtype).remove(selectedBuildingID);

            dd.setCoordinate(buildingCoord.get(selectedBuildingID));
            ((NoiseDwellingMCR) dd).setMicroBuildingId(selectedBuildingID);
        }

        logger.warn( errorBuilding +"   Dwellings cannot find specific building location. Their coordinates are assigned randomly in TAZ" );
        logger.warn( missingType +"   Dwellings cannot find specific building type. Their location are selected from general 'Dwelling' type." );

        logger.info("   Finished dwelling microlocation.");
    }


    private void readBuidlingFile(String fileName) {
        //parse buildings information to hashmap
        logger.info("Reading building micro data from csv file");
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("ref_no", header);
            int posZone = SiloUtil.findPositionInArray("oaID", header);
            int posDDType = SiloUtil.findPositionInArray("ddType", header);

            int posCoordX = -1;
            int posCoordY = -1;
            try {
                posCoordX = SiloUtil.findPositionInArray("X", header);
                posCoordY = SiloUtil.findPositionInArray("Y", header);
            } catch (Exception e) {
                logger.warn("No coords given in dwelling input file. Models using microlocations will not work.");
            }

            int noCoordCounter = 0;


            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                long id = Long.parseLong(lineElements[posId]);
                int zone = Integer.parseInt(lineElements[posZone]);
                String ddType = lineElements[posDDType];

                Coordinate coordinate = null;
                if (posCoordX >= 0 && posCoordY >= 0) {
                    try {
                        coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                    } catch (Exception e) {
                        noCoordCounter++;
                    }
                }

               buildingCoord.put(id,coordinate);
                buildingZone.put(id,zone);
                //put all buildings with the same zoneID into one building list
                zone2ddType2buildingIdMap.computeIfAbsent(zone, k -> {
                    Map<String, List<Long>> typeMap = new HashMap<>();
                    for (ManchesterDwellingTypes.DwellingTypeManchester type : ManchesterDwellingTypes.DwellingTypeManchester.values()) {
                        typeMap.put(type.toString(), new ArrayList<>());
                    }
                    return typeMap;
                });
                zone2ddType2buildingIdMap.get(zone).computeIfAbsent(ddType, k -> new ArrayList<>()).add(id);



            }
            if(noCoordCounter > 0) {
                logger.warn("There were " + noCoordCounter + " micro dwelling without coordinates.");
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading micro dwelling file: " + fileName, new RuntimeException());
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">", new RuntimeException());
        }
        logger.info("Finished reading " + recCount + " jobs.");

    }

}
