package de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdFactoryMuc;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobFactoryMuc;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.io.DwellingReaderMucMito;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.io.HouseholdReaderMucMito;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.io.PersonReaderMucMito;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Read2011JobsForMicrolocation {

    private static final Logger logger = Logger.getLogger(Read2011JobsForMicrolocation.class);
    private final DataContainer dataContainer;
    private int subPopulation;
    private DataSetSynPop dataSetSynPop;

    public Read2011JobsForMicrolocation(DataContainer dataContainer, DataSetSynPop dataSetSynPop, int subPopulation){
        this.dataContainer = dataContainer;
        this.subPopulation = subPopulation;
        this.dataSetSynPop = dataSetSynPop;
    }

    public void run(){
        readJobData();
    }


    private void readJobData() {
        logger.info("Reading job micro data from ascii file");

        String fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.jobsStateFileName;
        fileName += "_" + 2011 + ".csv";

        Map<String, Map<Integer, Map<Integer, Coordinate>>> microlocationsJobsByTypeAndZone = new LinkedHashMap<>();
        for (String jobType : JobType.getJobTypes()) {
            microlocationsJobsByTypeAndZone.putIfAbsent(jobType, new HashMap<>());
            for (int taz : PropertiesSynPop.get().main.jobsByTaz.getColumnAsInt("taz")) {
                Map<Integer, Coordinate> centroidCoordinates = new HashMap<>();
                int coordX = dataSetSynPop.getZoneCoordinates().get(taz,"coordX");
                int coordY = dataSetSynPop.getZoneCoordinates().get(taz,"coordY");
                microlocationsJobsByTypeAndZone.get(jobType).putIfAbsent(taz, new HashMap<>());
                microlocationsJobsByTypeAndZone.get(jobType).get(taz).put(0, new Coordinate(coordX, coordY));
            }
        }


        String recString = "";
        int recCount = 0;
        int count = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.replace("\"","").split(",");
            int posZone = SiloUtil.findPositionInArray("zone", header);
            int posType = SiloUtil.findPositionInArray("type", header);
            int posCoordX = SiloUtil.findPositionInArray("CoordX", header);
            int posCoordY = SiloUtil.findPositionInArray("CoordY", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                if (recCount % (PropertiesSynPop.get().main.numberOfSubpopulations) == subPopulation) {
                    count++;
                    String[] lineElements = recString.split(",");
                    int zoneId = Integer.parseInt(lineElements[posZone]);
                    String jobType = lineElements[posType].replace("\"", "");
                    Coordinate coordinate =  new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                    int countByZoneAndJobType = microlocationsJobsByTypeAndZone.get(jobType).get(zoneId).entrySet().size();
                    microlocationsJobsByTypeAndZone.get(jobType).get(zoneId).putIfAbsent(countByZoneAndJobType, coordinate);
                }
            }
        dataSetSynPop.setMicrolocationsJobsByTypeAndZone(microlocationsJobsByTypeAndZone);
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop job file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " jobs.");
    }

}
