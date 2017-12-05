package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.pb.common.matrix.Matrix;
import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.Job;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.properties.PropertiesSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.log4j.Logger;

import java.util.*;

public class AssignJobs {

    private static final Logger logger = Logger.getLogger(AssignJobs.class);

    private final DataSetSynPop dataSetSynPop;
    private Matrix distanceImpedance;

    private HashMap<Integer, ArrayList<Integer>> vacantJobsZoneType;
    private HashMap<Integer, ArrayList<Integer>> zonesWithVacantJobsType;
    private HashMap<Integer, Integer> numberVacantJobsType;
    private HashMap<String, Integer> jobIntTypes;

    String[] jobStringTypes;
    private ArrayList<Person> workerArrayList;
    private int assignedJobs;

    public AssignJobs(DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
    }

    public void run() {

        calculateDistanceImpedance();
        identifyVacantJobsByZoneType();
        shuffleWorkers();
        double logging = 2;
        int it = 1;
        for (Person pp : workerArrayList){
            int selectedJobType = selectJobType(pp);
            int workplace = selectWorkplace(pp, selectedJobType);
            int jobtaz = setWorkerAndJob(pp, workplace);
            updateMaps(workplace, jobtaz, selectedJobType);
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


    private int setWorkerAndJob(Person pp, int workplace){

        Job.getJobFromId(workplace).setWorkerID(pp.getId());
        int jobTAZ = Job.getJobFromId(workplace).getZone();
        pp.setJobTAZ(jobTAZ);
        pp.setWorkplace(workplace);
        return jobTAZ;
    }


    private int selectWorkplace(Person pp, int selectedJobType){

        int workplace = 0;
        // if there are still TAZ with vacant jobs in the region, select one of them. If not, assign them outside the area

        Map<Integer, Float> probability = new HashMap<>();
        Iterator<Integer> iterator = zonesWithVacantJobsType.get(selectedJobType).iterator();
        while (iterator.hasNext()){
            Integer zone = iterator.next();
            float prob = distanceImpedance.getValueAt(pp.getHomeTaz(), Math.round(zone / 100));
            probability.put(zone, prob);
        }
        int workTAZ = SiloUtil.select(probability);
        workplace = selectRandomlyJob(vacantJobsZoneType.get(workTAZ));
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
        /*idVacantJobsByZoneType = new HashMap<>();
        numberVacantJobsByType = new HashMap<>();
        idZonesVacantJobsByType = new HashMap<>();
        numberZonesByType = new HashMap<>();
        numberVacantJobsByZoneByType = new HashMap<>();
        jobIntTypes = new HashMap<>();
        for (int i = 0; i < PropertiesSynPop.get().main.jobStringType.length; i++) {
            jobIntTypes.put(PropertiesSynPop.get().main.jobStringType[i], i);
        }
        int[] cellsID = PropertiesSynPop.get().main.cellsMatrix.getColumnAsInt("ID_cell");

        //create the counter hashmaps
        for (int i = 0; i < PropertiesSynPop.get().main.jobStringType.length; i++){
            int type = jobIntTypes.get(PropertiesSynPop.get().main.jobStringType[i]);
            numberZonesByType.put(type,0);
            numberVacantJobsByType.put(type,0);
            for (int j = 0; j < cellsID.length; j++){
                numberVacantJobsByZoneByType.put(type + cellsID[j] * 100, 0);
            }
        }
        //get the totals
        for (Job jj: jobs) {
            if (jj.getWorkerId() == -1) {
                int type = jobIntTypes.get(jj.getType());
                int typeZone = type + jj.getZone() * 100;
                //update the set of zones that have ID
                if (numberVacantJobsByZoneByType.get(typeZone) == 0){
                    numberZonesByType.put(type, numberZonesByType.get(type) + 1);
                }
                //update the number of vacant jobs per job type
                numberVacantJobsByType.put(type, numberVacantJobsByType.get(type) + 1);
                numberVacantJobsByZoneByType.put(typeZone, numberVacantJobsByZoneByType.get(typeZone) + 1);
            }
        }
        //create the IDs Hashmaps and reset the counters
        for (String jobType : PropertiesSynPop.get().main.jobStringType){
            int type = jobIntTypes.get(jobType);
            int[] dummy = SiloUtil.createArrayWithValue(numberZonesByType.get(type),0);
            idZonesVacantJobsByType.put(type,dummy);
            numberZonesByType.put(type,0);
            for (int j = 0; j < cellsID.length; j++){
                int typeZone = type + cellsID[j] * 100;
                int[] dummy2 = SiloUtil.createArrayWithValue(numberVacantJobsByZoneByType.get(typeZone), 0);
                idVacantJobsByZoneType.put(typeZone, dummy2);
                numberVacantJobsByZoneByType.put(typeZone, 0);
            }
        }
        //fill the Hashmaps with IDs
        for (Job jj: jobs) {
            if (jj.getWorkerId() == -1) {
                int type = jobIntTypes.get(jj.getType());
                int typeZone = jobIntTypes.get(jj.getType()) + jj.getZone() * 100;
                //update the list of job IDs per zone and job type
                int [] previousJobIDs = idVacantJobsByZoneType.get(typeZone);
                previousJobIDs[numberVacantJobsByZoneByType.get(typeZone)] = jj.getId();
                idVacantJobsByZoneType.put(typeZone,previousJobIDs);
                //update the set of zones that have ID
                if (numberVacantJobsByZoneByType.get(typeZone) == 0){
                    int[] previousZones = idZonesVacantJobsByType.get(type);
                    previousZones[numberZonesByType.get(type)] = typeZone;
                    idZonesVacantJobsByType.put(type,previousZones);
                    numberZonesByType.put(type, numberZonesByType.get(type) + 1);
                }
                //update the number of vacant jobs per job type
                numberVacantJobsByZoneByType.put(typeZone, numberVacantJobsByZoneByType.get(typeZone) + 1);
            }
        }*/
        jobIntTypes = new HashMap<>();
        for (int i = 0; i < PropertiesSynPop.get().main.jobStringType.length; i++) {
            jobIntTypes.put(PropertiesSynPop.get().main.jobStringType[i], i);
        }
        vacantJobsZoneType = new HashMap<>();
        zonesWithVacantJobsType = new HashMap<>();
        numberVacantJobsType = new HashMap<>();
        for (Job jj: jobs) {
            if (jj.getWorkerId() == -1) {
                int type = jobIntTypes.get(jj.getType());
                int typeZone = jobIntTypes.get(jj.getType()) + jj.getZone() * 100;
                //update the list of job IDs per zone and job type
                if (vacantJobsZoneType.get(typeZone) != null){
                    ArrayList<Integer> previousJobs = vacantJobsZoneType.get(typeZone);
                    previousJobs.add(jj.getId());
                } else {
                    ArrayList<Integer> previousJobs = new ArrayList<>();
                    previousJobs.add(jj.getId());
                    vacantJobsZoneType.put(typeZone, previousJobs);
                }
                if (zonesWithVacantJobsType.get(type) != null){
                    ArrayList<Integer> previousZones = zonesWithVacantJobsType.get(type);
                    previousZones.add(typeZone);
                    numberVacantJobsType.put(type, 1 + numberVacantJobsType.get(type));
                } else {
                    ArrayList<Integer> previousZones = new ArrayList<>();
                    previousZones.add(typeZone);
                    zonesWithVacantJobsType.put(type, previousZones);
                    numberVacantJobsType.put(type, 1);
                }
            }
        }
    }


    private void updateMaps(int jobID, int jobTAZ, int selectedJobType){

        if (numberVacantJobsType.get(selectedJobType) <= 0){
            numberVacantJobsType.remove(selectedJobType);
        } else {
            numberVacantJobsType.put(selectedJobType, numberVacantJobsType.get(selectedJobType) - 1);
        }
        int key = jobTAZ * 100 + selectedJobType;
        ArrayList<Integer> jobs = vacantJobsZoneType.get(key);
        jobs.remove(jobID);
        if (vacantJobsZoneType.get(key) == null){
            vacantJobsZoneType.remove(key);
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


    private int selectRandomlyJob (ArrayList<Integer> ids){

        int tt = Math.round(SiloUtil.getRandomNumberAsFloat() * ids.size());
        int selection = ids.get(tt);
        return selection;
    }
}
