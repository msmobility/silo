package run;

import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobFactory;
import de.tum.bgu.msm.data.job.JobUtils;
import de.tum.bgu.msm.io.input.DefaultJobReader;
import de.tum.bgu.msm.io.input.JobReader;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JobReaderPerth implements JobReader {

    private final static Logger logger = Logger.getLogger(DefaultJobReader.class);
    private final JobDataManager jobDataManager;
    private final GeoData geoData;

    public JobReaderPerth(JobDataManager jobDataManager, GeoData geoData) {
        this.jobDataManager = jobDataManager;
        this.geoData = geoData;
    }

    @Override
    public void readData(String fileName) {

        logger.info("Reading job micro data from ascii file");
        JobFactory factory = JobUtils.getFactory();
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posZone = SiloUtil.findPositionInArray("zone", header);
            int posWorker = SiloUtil.findPositionInArray("personId", header);
            int posType = SiloUtil.findPositionInArray("type", header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id = Integer.parseInt(lineElements[posId]);
                int zoneId = Integer.parseInt(lineElements[posZone]);
                int worker = Integer.parseInt(lineElements[posWorker]);
                String type = lineElements[posType].replace("\"", "");

                Job jj = factory.createJob(id, zoneId, geoData.getZones().get(zoneId).getRandomCoordinate(SiloUtil.getRandomObject()), worker, type);


                jobDataManager.addJob(jj);
                if (id == SiloUtil.trackJj) {
                    SiloUtil.trackWriter.println("Read job with following attributes from " + fileName);
                    SiloUtil.trackWriter.println(jj.toString());
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop job file: " + fileName, new RuntimeException());
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">", new RuntimeException());
        }
        logger.info("Finished reading " + recCount + " jobs.");
    }
}
