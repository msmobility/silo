package de.tum.bgu.msm.syntheticPopulationGenerator.munich.synpopTrampa;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobFactoryMuc;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation.GenerateJobs;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class GenerateJobsTrampa {

    private static final Logger logger = Logger.getLogger(GenerateJobs.class);

    private final DataSetSynPop dataSetSynPop;
    private Map<Integer, Float> jobsByTaz;
    private final DataContainer dataContainer;


    public GenerateJobsTrampa(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }

    public void run(){
        logger.info("   Running module: job generation");
        for (int municipality : dataSetSynPop.getMunicipalities()){
            logger.info("   Municipality " + municipality + ". Starting to generate jobs");
            for (String jobType : PropertiesSynPop.get().main.jobStringType) {
                if (PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, jobType) > 0.1) {
                    initializeTAZprobability(municipality, jobType);
                    generateJobsByTypeAtMunicipalityWithReplacement(municipality, jobType);
                }
            }
        }
    }


    private void generateJobsByTypeAtMunicipalityWithReplacement(int municipality, String jobType){
        JobDataManager jobData = dataContainer.getJobDataManager();
        final JobFactoryMuc jobFactoryMuc = new JobFactoryMuc();
        jobFactoryMuc.readWorkingTimeDistributions(Properties.get());
        int totalJobs = (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, jobType);
        for (int job = 0; job < totalJobs; job++){
            int id = jobData.getNextJobId();
            int tazSelected = SiloUtil.select(jobsByTaz);
            if (jobsByTaz.get(tazSelected) > 1){
                jobsByTaz.put(tazSelected, jobsByTaz.get(tazSelected) - 1);
            } else {
                jobsByTaz.remove(tazSelected);
            }
            jobData.addJob(jobFactoryMuc.createJob(id, tazSelected, null, -1, jobType));
        }

    }


    private void initializeTAZprobability(int municipality, String jobType){
        jobsByTaz = new HashMap<>();
        for (int taz : dataSetSynPop.getTazByMunicipality().get(municipality)){
            jobsByTaz.put(taz, PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(taz, jobType));
        }
    }
}
