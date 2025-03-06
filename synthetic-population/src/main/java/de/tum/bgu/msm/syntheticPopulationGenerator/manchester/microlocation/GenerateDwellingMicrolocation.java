package de.tum.bgu.msm.syntheticPopulationGenerator.manchester.microlocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.ManchesterDwellingTypes;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.DwellingType;
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
import java.util.stream.Collectors;

public class GenerateDwellingMicrolocation {

    private static final Logger logger = LogManager.getLogger(GenerateDwellingMicrolocation.class);
    private final DataContainer dataContainer;
    private final DataSetSynPop dataSetSynPop;
    private final Map<Long, Coordinate> buildingCoord = new HashMap<>();
    private final Map<Integer, Map<String, List<Long>>> zone2ddType2buildingIdMap = new HashMap<>();
    Map<Long, Integer> buildingZone = new HashMap<>();

    public GenerateDwellingMicrolocation(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataContainer = dataContainer;
        this.dataSetSynPop = dataSetSynPop;
    }

    public void run() {
        logger.info("   Running module: dwelling microlocation");
        logger.info("   Start parsing buildings information to hashmap");
        readBuidlingFile(PropertiesSynPop.get().main.microDwellingsFileName);
        logger.info("   Start Selecting the building to allocate the dwelling");
        //Select the building to allocate the dwelling
        int missingType = 0;
        int errorBuilding = 0;

        Map<Integer, Map<DwellingType, List<NoiseDwellingMCR>>> groupedDwellings = dataContainer.getRealEstateDataManager()
                .getDwellings()
                .stream()
                .filter(d -> d instanceof NoiseDwellingMCR) // Ensure only NoiseDwellingMCR instances
                .map(d -> (NoiseDwellingMCR) d) // Cast to NoiseDwellingMCR
                .collect(Collectors.groupingBy(
                        NoiseDwellingMCR::getZoneId,
                        Collectors.groupingBy(NoiseDwellingMCR::getType)
                ));

        for (int zoneID: groupedDwellings.keySet()) {
            for(DwellingType ddtype: groupedDwellings.get(zoneID).keySet()) {
                List<NoiseDwellingMCR> dwellings = groupedDwellings.get(zoneID).get(ddtype);

                Map<String, List<Long>> buildingMap = zone2ddType2buildingIdMap.getOrDefault(zoneID, Collections.emptyMap());
                List<Long> buildings = getBuildingsForType(buildingMap, ddtype);

                if (buildings == null || buildings.isEmpty()) {
                    logger.warn("Zone " + zoneID + " has no buildings for type " + ddtype);
                    missingType++;
                    for (NoiseDwellingMCR dd : dwellings){
                        float coordX = dataSetSynPop.getTazAttributes().get(zoneID).get("popCentroid_x");
                        float coordY = dataSetSynPop.getTazAttributes().get(zoneID).get("popCentroid_y");
                        dd.setCoordinate(new Coordinate(coordX, coordY));
                        errorBuilding++;
                    }
                    continue;
                }

                List<Long> selectedBuildingIDs = selectBuildings(buildings, dwellings.size());

                for (int index = 0; index < dwellings.size(); index++) {
                    NoiseDwellingMCR dd = dwellings.get(index);
                    long selectedBuildingID = selectedBuildingIDs.get(index);
                    dd.setCoordinate(buildingCoord.get(selectedBuildingID));
                }
            }

            logger.info("Zone " + zoneID +  " dwelling microlocation assigned.");
        }

        logger.warn( errorBuilding +"   Dwellings cannot find specific building location. Their coordinates are assigned randomly in TAZ" );
        logger.warn( missingType +"   Dwellings cannot find specific building type. Their location are selected from general 'Dwelling' type." );

        logger.info("   Finished dwelling microlocation.");
    }

    private List<Long> getBuildingsForType(Map<String, List<Long>> buildingMap, DwellingType ddType) {
        List<Long> buildings = Optional.ofNullable(buildingMap.get(ddType.toString()))
                .orElse(Collections.emptyList());

        if (!buildings.isEmpty()) return buildings;

        // Fallback logic based on ManchesterDwellingTypes
        if (ddType instanceof ManchesterDwellingTypes.DwellingTypeManchester) {
            return switch ((ManchesterDwellingTypes.DwellingTypeManchester) ddType) {
                case SFD -> Optional.ofNullable(buildingMap.get("SFA"))
                        .orElseGet(() -> buildingMap.getOrDefault("Dwelling", Collections.emptyList()));
                case SFA -> Optional.ofNullable(buildingMap.get("SFD"))
                        .orElseGet(() -> buildingMap.getOrDefault("Dwelling", Collections.emptyList()));
                case FLAT -> buildingMap.getOrDefault("Dwelling", Collections.emptyList());
                case MH -> Optional.ofNullable(buildingMap.get("Dwelling"))
                        .orElseGet(() -> buildingMap.getOrDefault("FLAT", Collections.emptyList()));
            };
        }
        return Collections.emptyList();
    }


    public List<Long> selectBuildings(List<Long> buildings, int n) {
        Random random = new Random();
        List<Long> selectedBuildings = new ArrayList<>();

        if (buildings.size() >= n) {
            // Select `n` unique buildings
            Collections.shuffle(buildings); // Shuffle to get randomness
            selectedBuildings = buildings.subList(0, n);
        } else {
            // Select all buildings in random order
            List<Long> shuffledBuildings = new ArrayList<>(buildings);
            Collections.shuffle(shuffledBuildings);
            selectedBuildings.addAll(shuffledBuildings);

            // Fill up with random duplicates
            while (selectedBuildings.size() < n) {
                selectedBuildings.add(buildings.get(random.nextInt(buildings.size())));
            }
        }

        return selectedBuildings;
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
