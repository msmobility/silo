package de.tum.bgu.msm.syntheticPopulationGenerator.bangkok.allocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobUtils;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class GenerateJobs {

    private static final Logger logger = Logger.getLogger(GenerateJobs.class);

    private final DataSetSynPop dataSetSynPop;
    private Map<Integer, Float> jobsByTaz;
    private final DataContainer dataContainer;


    public GenerateJobs(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }

    public void run(){
        logger.info("   Running module: job generation");
        for (int municipality : dataSetSynPop.getMunicipalities()){
            for (String jobType : PropertiesSynPop.get().main.jobStringType) {
                if (PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(municipality, jobType) > 0.1) {
                    initializeTAZprobability(municipality, jobType);
                    generateJobsByTypeAtMunicipalityWithReplacement(municipality, jobType);
                }
            }
        }
    }


    private void generateJobsByTypeAtMunicipalityWithReplacement(int municipality, String jobType){
        JobDataManager jobData = dataContainer.getJobDataManager();
            int totalJobs = (int) PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(municipality, jobType);
            totalJobs = (int) Math.round(totalJobs * PropertiesSynPop.get().main.jobScaler);
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
            jobsByTaz.put(taz, (float) Math.round(PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(taz, jobType) * PropertiesSynPop.get().main.jobScaler));
        }
    }
}
