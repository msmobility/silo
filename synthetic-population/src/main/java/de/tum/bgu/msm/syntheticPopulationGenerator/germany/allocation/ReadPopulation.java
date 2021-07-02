package de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobFactoryMuc;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.io.DwellingReaderMucMito;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.io.HouseholdReaderMucMito;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.io.PersonReaderMucMito;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadPopulation {

    private static final Logger logger = Logger.getLogger(ReadPopulation.class);
    private final DataContainer dataContainer;

    ReadPopulation(DataContainer dataContainer){
        this.dataContainer = dataContainer;
    }

    public void run(){
        logger.info("   Running module: read population");
        readHouseholdData(Properties.get().main.startYear);
        readPersonData(Properties.get().main.startYear);
        readDwellingData(Properties.get().main.startYear);
        readJobData(Properties.get().main.startYear);
    }


    private void readHouseholdData(int year) {
        logger.info("Reading household micro data from ascii file");

        HouseholdDataManager householdData = dataContainer.getHouseholdDataManager();
        HouseholdFactory householdFactory = householdData.getHouseholdFactory();
        String fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.householdsStateFileName + "_" + year + ".csv";
        HouseholdReaderMucMito hhReader = new HouseholdReaderMucMito(householdData, (HouseholdFactoryMuc) householdFactory);
        hhReader.readData(fileName);
    }


    private void readPersonData(int year) {
        logger.info("Reading person micro data from ascii file");

        HouseholdDataManager householdData = dataContainer.getHouseholdDataManager();
        String fileName = Properties.get().main.baseDirectory +  PropertiesSynPop.get().main.personsStateFileName + "_" + year + ".csv";
        PersonReaderMucMito ppReader = new PersonReaderMucMito(householdData);
        ppReader.readData(fileName);
    }


    private void readDwellingData(int year) {
        logger.info("Reading dwelling micro data from ascii file");

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        String fileName = Properties.get().main.baseDirectory + PropertiesSynPop.get().main.dwellingsStateFileName + "_" + year + ".csv";
        DwellingReaderMucMito ddReader = new DwellingReaderMucMito(realEstate);
        ddReader.readData(fileName);
    }


    private void readJobData(int year) {
        logger.info("Reading job micro data from ascii file");

        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        JobFactoryMuc jobFactory = (JobFactoryMuc) dataContainer.getJobDataManager().getFactory();
        String fileName = Properties.get().main.baseDirectory + Properties.get().jobData.jobsFileName;
        fileName += "_" + year + ".csv";

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posZone = SiloUtil.findPositionInArray("zone",header);
            int posWorker = SiloUtil.findPositionInArray("personId",header);
            int posType = SiloUtil.findPositionInArray("type",header);
            int posCoordX = SiloUtil.findPositionInArray("CoordX", header);
            int posCoordY = SiloUtil.findPositionInArray("CoordY", header);
            int posStartTime = SiloUtil.findPositionInArray("startTime", header);
            int posDuration = SiloUtil.findPositionInArray("duration", header);


            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id      = Integer.parseInt(lineElements[posId]);
                int zoneId    = Integer.parseInt(lineElements[posZone]);
                int worker  = Integer.parseInt(lineElements[posWorker]);
                String type = lineElements[posType].replace("\"", "");
                Coordinate coordinate = null;
                if (posCoordX >= 0 && posCoordY >= 0) {
                    try {
                        coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                    } catch (Exception e) {
                    }
                }
                JobMuc jj = jobFactory.createJob(id, zoneId, coordinate, worker, type);
                int startTime = Integer.parseInt(lineElements[posStartTime]);
                int duration = Integer.parseInt(lineElements[posDuration]);
                jj.setJobWorkingTime(startTime, duration);
                jobDataManager.addJob(jj);
                if (id == SiloUtil.trackJj) {
                    SiloUtil.trackWriter.println("Read job with following attributes from " + fileName);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop job file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " jobs.");
    }

}
