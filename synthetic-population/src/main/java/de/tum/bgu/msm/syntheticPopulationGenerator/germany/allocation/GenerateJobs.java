package de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobUtils;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GenerateJobs {

    private static final Logger logger = Logger.getLogger(GenerateJobs.class);

    private final DataSetSynPop dataSetSynPop;
    private Map<Integer, Float> jobsByTaz;
    private final DataContainer dataContainer;
    private final Random random;


    public GenerateJobs(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
        random = new Random(Properties.get().main.randomSeed);
    }

    public void run(){
        logger.info("   Running module: job generation");
        // read a new file taz and jobs by type

        TableDataSet jobsByTaz = PropertiesSynPop.get().main.jobsByTaz;

        for (int taz : jobsByTaz.getColumnAsInt("taz")){
            logger.info("   Municipality " + taz + ". Starting to generate jobs");
            for (String jobType : PropertiesSynPop.get().main.jobStringType) {
                //read instead of marginals Municipality another file with all the job totals by skim matrix zone for a certain state
                //no need to loop for probabilities because this is the highest resolution - do a for loop to generate each job (keep the next three lines for the future, when we have jobs bz PLZ)
//                if (PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(taz, jobType) > 0.1) {
//                    initializeTAZprobability(taz, jobType);
//                    generateJobsByTypeAtMunicipalityWithReplacement(taz, jobType);
//                }
                int numberOfJobs = (int) jobsByTaz.getIndexedValueAt(taz, jobType);
                generateJobsInTAZ(taz, jobType, numberOfJobs);
            }
        }
    }

    private void generateJobsInTAZ(int taz, String jobType, int numberOfJobs) {
        JobDataManager jobData = dataContainer.getJobDataManager();
        for (int job = 0; job < numberOfJobs; job++){
            int id = jobData.getNextJobId();
            jobData.addJob(JobUtils.getFactory().createJob(id, taz, null, -1, jobType));
        }
    }


    private void generateJobsByTypeAtMunicipalityWithReplacement(int municipality, String jobType){
        JobDataManager jobData = dataContainer.getJobDataManager();
            int totalJobs = (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, jobType);
            for (int job = 0; job < totalJobs; job++){
                int id = jobData.getNextJobId();
                int tazSelected = SiloUtil.select(jobsByTaz);
                if (jobsByTaz.get(tazSelected) > 1){
                    jobsByTaz.put(tazSelected, jobsByTaz.get(tazSelected) - 1);
                } else {
                    jobsByTaz.remove(tazSelected);
                }
                jobData.addJob(JobUtils.getFactory().createJob(id, tazSelected, null, -1, jobType));
            }

    }


    private void initializeTAZprobability(int municipality, String jobType){
        jobsByTaz = new HashMap<>();
        jobsByTaz.clear();
        for (int taz : dataSetSynPop.getTazByMunicipality().get(municipality)){
            jobsByTaz.put(taz, PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(taz, jobType));
        }
    }
}
