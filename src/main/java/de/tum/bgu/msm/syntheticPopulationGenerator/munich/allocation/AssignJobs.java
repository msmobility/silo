package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.Job;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.MicroDataManager;
import org.apache.log4j.Logger;

import java.util.*;

public class AssignJobs {

    private static final Logger logger = Logger.getLogger(AssignJobs.class);

    private final DataSetSynPop dataSetSynPop;
    private final MicroDataManager microDataManager;
    private Matrix distanceImpedance;

    private HashMap<Integer, ArrayList<Integer>> vacantJobsByZoneAndType;
    private HashMap<Integer, ArrayList<Integer>> zonesWithVacantJobsType;
    private HashMap<Integer, Integer> numberVacantJobsType;
    private HashMap<String, Integer> jobIntTypes;

    String[] jobStringTypes;
    private ArrayList<Person> workerArrayList;
    private int assignedJobs;

    public AssignJobs(DataSetSynPop dataSetSynPop){
        microDataManager = new MicroDataManager(dataSetSynPop);
        this.dataSetSynPop = dataSetSynPop;
    }


    public void run() {

        calculateDistanceImpedance();
        identifyVacantJobsByZoneType();
        shuffleWorkers();
        double logging = 2;
        int it = 1;
        for (Person pp : workerArrayList){
            int selectedJobType = microDataManager.guessjobType(pp);
            int workplace = selectWorkplace(pp, selectedJobType);
            if (workplace > 0) {
                setWorkerAndJob(pp, workplace);
                updateMaps(selectedJobType);
            }
            if (assignedJobs == logging){
                logger.info("   Assigned " + assignedJobs + " jobs.");
                it++;
                logging = Math.pow(2, it);
            }
        }

    }


    private void calculateDistanceImpedance(){

        distanceImpedance = new Matrix(dataSetSynPop.getDistanceTazToTaz().getRowCount(), dataSetSynPop.getDistanceTazToTaz().getColumnCount());
        for (int i = 1; i <= dataSetSynPop.getDistanceTazToTaz().getRowCount(); i ++){
            for (int j = 1; j <= dataSetSynPop.getDistanceTazToTaz().getColumnCount(); j++){
                double value = Math.exp(PropertiesSynPop.get().main.alphaJob *
                        Math.exp(dataSetSynPop.getDistanceTazToTaz().getValueAt(i,j) * PropertiesSynPop.get().main.gammaJob));
                distanceImpedance.setValueAt(i,j,(float)value);
            }
        }
    }


    private void setWorkerAndJob(Person pp, int workplace){

        Job.getJobFromId(workplace).setWorkerID(pp.getId());
        int jobTAZ = Job.getJobFromId(workplace).getZone();
        pp.setJobTAZ(jobTAZ);
        pp.setWorkplace(workplace);
    }


    private int selectWorkplace(Person pp, int selectedJobType){

        int workplace;
        if (numberVacantJobsType.get(selectedJobType) != null) {
            Map<Integer, Float> probability = new HashMap<>();
            Iterator<Integer> iterator = zonesWithVacantJobsType.get(selectedJobType).iterator();
            while (iterator.hasNext()) {
                Integer zone = iterator.next();
                int key = zone * 100 + selectedJobType;
                float prob = distanceImpedance.getValueAt(pp.getHomeTaz(), zone) * vacantJobsByZoneAndType.get(key).size();
                probability.put(zone, prob);
            }
            int workTAZ = SiloUtil.select(probability);
            int key = workTAZ * 100 + selectedJobType;
            workplace = selectRandomlyJob(key);
        } else {
            workplace = -2;
        }
        return workplace;
    }


    private void shuffleWorkers(){

        Map<Integer, Person> personMap = Person.getPersonMap();
        workerArrayList = new ArrayList<>();
        for (Map.Entry<Integer,Person> pair : personMap.entrySet() ){
            if (pair.getValue().getOccupation() == 1){
                workerArrayList.add(pair.getValue());
            }
        }
        Collections.shuffle(workerArrayList);
        assignedJobs = 0;
    }


    private void identifyVacantJobsByZoneType() {

        logger.info("  Identifying vacant jobs by zone");
        Collection<Job> jobs = Job.getJobs();

        jobStringTypes = PropertiesSynPop.get().main.jobStringType;
        jobIntTypes = new HashMap<>();
        for (int i = 0; i < PropertiesSynPop.get().main.jobStringType.length; i++) {
            jobIntTypes.put(PropertiesSynPop.get().main.jobStringType[i], i);
        }
        vacantJobsByZoneAndType = new HashMap<>();
        zonesWithVacantJobsType = new HashMap<>();
        numberVacantJobsType = new HashMap<>();
        for (Job jj: jobs) {
            if (jj.getWorkerId() == -1) {
                int type = jobIntTypes.get(jj.getType());
                int zone = jj.getZone();
                int jobID = jj.getId();
                int typeZone = type + zone * 100;
                ArrayList<Integer> previousJobs = new ArrayList<>();
                if (vacantJobsByZoneAndType.get(typeZone) != null) {
                    previousJobs = vacantJobsByZoneAndType.get(typeZone);
                }
                previousJobs.add(jobID);
                vacantJobsByZoneAndType.put(typeZone, previousJobs);
                ArrayList<Integer> previousZones = new ArrayList<>();
                int previousVacantJobsByType = 1;
                if (zonesWithVacantJobsType.get(type) != null){
                    previousZones = zonesWithVacantJobsType.get(type);
                    previousVacantJobsByType = previousVacantJobsByType + numberVacantJobsType.get(type);
                }
                previousZones.add(zone);
                zonesWithVacantJobsType.put(type, previousZones);
                numberVacantJobsType.put(type, previousVacantJobsByType);
            }
        }
    }


    private void updateMaps(int selectedJobType){

        if (numberVacantJobsType.get(selectedJobType) <= 0){
            numberVacantJobsType.remove(selectedJobType);
        } else {
            numberVacantJobsType.put(selectedJobType, numberVacantJobsType.get(selectedJobType) - 1);
        }
        assignedJobs++;
    }


    private int selectRandomlyJob (int key) {

        ArrayList<Integer> ids = vacantJobsByZoneAndType.get(key);
        int tt = Math.round(SiloUtil.getRandomNumberAsFloat() * (ids.size() - 1));
        int selection = ids.get(tt);
        ids.remove(tt);
        if (ids.size() > 0){
            vacantJobsByZoneAndType.put(key, ids);
        } else {
            vacantJobsByZoneAndType.remove(key);
        }
        return selection;
    }
}
