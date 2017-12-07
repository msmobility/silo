package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.Job;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.MicroDataManager;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
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
            setWorkerAndJob(pp, workplace);
            updateMaps( selectedJobType);
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
                distanceImpedance.setValueAt(i,j,(float) Math.exp(PropertiesSynPop.get().main.alphaJob *
                        Math.exp(dataSetSynPop.getDistanceTazToTaz().getValueAt(i,j) * PropertiesSynPop.get().main.gammaJob)));
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

        int workplace = 0;
        if (numberVacantJobsType.get(selectedJobType) > 0) {
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


    private int selectJobType(Person pp){
        double[] probabilities = new double[PropertiesSynPop.get().main.jobStringType.length];
        int[] jobTypes = new int[PropertiesSynPop.get().main.jobStringType.length];
        //Person and job type values
        String name = "";
        if (pp.getGender() == 1) {
            name = "maleEducation";
        } else {
            name = "femaleEducation";
        }
        name = name + pp.getEducationLevel();

        for (int job = 0; job < PropertiesSynPop.get().main.jobStringType.length; job++){
            jobTypes[job] = job + 1;
            probabilities[job] = PropertiesSynPop.get().main.probabilitiesJob.getStringIndexedValueAt(PropertiesSynPop.get().main.jobStringType[job],name);
        }

        return new EnumeratedIntegerDistribution(jobTypes, probabilities).sample();
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
        // populate HashMap with Jobs by zone and job type
        // adapted from SyntheticPopUS

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
                    previousVacantJobsByType += numberVacantJobsType.get(type);
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

    private static int[] select (double[] probabilities, int[] id) {
        // select item based on probabilities (for zero-based float array)
        double sumProb = 0;
        int[] results = new int[2];
        for (double val: probabilities) sumProb += val;
        double selPos = sumProb * SiloUtil.getRandomNumberAsFloat();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[probabilities.length - 1];
        results[1] = probabilities.length - 1;
        return results;
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
