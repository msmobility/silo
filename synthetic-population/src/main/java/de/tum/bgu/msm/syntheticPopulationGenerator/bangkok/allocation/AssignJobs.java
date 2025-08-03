package de.tum.bgu.msm.syntheticPopulationGenerator.bangkok.allocation;

import com.google.common.math.LongMath;
import de.tum.bgu.msm.common.matrix.Matrix;
import de.tum.bgu.msm.common.matrix.RowVector;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.IntStream;

public class AssignJobs {

    private static final Logger logger = LogManager.getLogger(AssignJobs.class);

    private final DataSetSynPop dataSetSynPop;
    private final DataContainer dataContainer;
    private Matrix distanceImpedance;

    private HashMap<String, Integer> jobIntTypes;
    protected HashMap<Integer, int[]> idVacantJobsByZoneType;
    protected HashMap<Integer, Integer> numberVacantJobsByType;
    protected HashMap<Integer, int[]> idZonesVacantJobsByType;
    protected HashMap<Integer, Integer> numberVacantJobsByZoneByType;
    protected HashMap<Integer, Integer> numberZonesByType;

    private String[] jobStringTypes;
    private ArrayList<Person> workerArrayList;
    private int assignedJobs;
    private int[] tazIds;

    private HashMap<Person, Integer> educationalLevel;

    public AssignJobs(DataContainer dataContainer, DataSetSynPop dataSetSynPop, HashMap<Person, Integer> educationalLevel){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
        this.educationalLevel = educationalLevel;
    }


    public void run() {
        logger.info("   Running module: job allocation");
        calculateDistanceImpedance();
        identifyVacantJobsByZoneType();
        shuffleWorkers();
        logger.info("Number of workers " + workerArrayList.size());
        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        HouseholdDataManager households = dataContainer.getHouseholdDataManager();
        for (Person pp : workerArrayList){

            Household hh = pp.getHousehold();
            int origin = realEstate.getDwelling(hh.getDwellingId()).getZoneId();
            int selectedJobType = 0; //guessjobType(origin);
            int[] workplace = selectWorkplace(origin, selectedJobType);
            if (workplace[0] > 0) {
                int jobID = idVacantJobsByZoneType.get(workplace[0])[numberVacantJobsByZoneByType.get(workplace[0]) - 1];
                setWorkerAndJob(pp, jobID);
                updateMaps(selectedJobType, workplace);
            }
            if (LongMath.isPowerOfTwo(assignedJobs)){
                logger.info("   Assigned " + assignedJobs + " jobs.");
            }
            assignedJobs++;
        }
        logger.info("   Finished job allocation. Assigned " + assignedJobs + " jobs.");
    }


   private void calculateDistanceImpedance(){

        distanceImpedance = new Matrix(dataSetSynPop.getDistanceTazToTaz().getRowCount(), dataSetSynPop.getDistanceTazToTaz().getColumnCount());
        Map<Integer, Float> utilityHBW = dataSetSynPop.getTripLengthDistribution().column("HBW");
        for (int i = 1; i <= dataSetSynPop.getDistanceTazToTaz().getRowCount(); i ++){
            for (int j = 1; j <= dataSetSynPop.getDistanceTazToTaz().getColumnCount(); j++){
                int distance = (int) dataSetSynPop.getDistanceTazToTaz().getValueAt(i,j);
                float utility = 0.00000001f;
                if (distance < 200){
                    utility = utilityHBW.get(distance);
                }
                distanceImpedance.setValueAt(i, j, utility);
            }
        }
    }


    private void setWorkerAndJob(Person pp, int jobID){

        dataContainer.getJobDataManager().getJobFromId(jobID).setWorkerID(pp.getId());
        int jobTAZ = dataContainer.getJobDataManager().getJobFromId(jobID).getZoneId();
        pp.setWorkplace(jobID);
    }


    private int[] selectWorkplace(int homeTaz, int selectedJobType){

        int[] workplace = new int[2];
        if (numberZonesByType.get(selectedJobType) > 0) {
            double[] probs = new double[numberZonesByType.get(selectedJobType)];
            int[] ids = idZonesVacantJobsByType.get(selectedJobType);
            RowVector distances = distanceImpedance.getRow(homeTaz);
            IntStream.range(0, probs.length).parallel().forEach(id -> probs[id] = Math.exp(distances.getValueAt(ids[id] / 100) * Math.pow(numberVacantJobsByZoneByType.get(ids[id]), 0.45)));
            workplace = select(probs, ids);
        } else {
            workplace[0] = -2;
        }
        return workplace;
    }


    private void shuffleWorkers(){

        workerArrayList = new ArrayList<>();
        //All employed persons look for employment, regardless they have already assigned one. That's why also workplace and jobTAZ are set to -1
        for (Person pp : dataContainer.getHouseholdDataManager().getPersons()){
            if (pp.getOccupation() == Occupation.EMPLOYED){
                workerArrayList.add(pp);
                pp.setWorkplace(-1);
            }
        }
        Collections.shuffle(workerArrayList);
        assignedJobs = 0;
    }


    private void identifyVacantJobsByZoneType() {

        logger.info("  Identifying vacant jobs by zone");
        Collection<Job> jobs = dataContainer.getJobDataManager().getJobs();

        jobStringTypes = PropertiesSynPop.get().main.jobStringType;
        jobIntTypes = new HashMap<>();
        for (int i = 0; i < PropertiesSynPop.get().main.jobStringType.length; i++) {
            jobIntTypes.put(PropertiesSynPop.get().main.jobStringType[i], i);
        }
        tazIds = dataSetSynPop.getTazs().stream().mapToInt(i -> i).toArray();

        idVacantJobsByZoneType = new HashMap<>();
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
        int count = 0;
        for (Job jj: jobs) {
            //set all jobs vacant to allocate them
            jj.setWorkerID(-1);
            int type = 0;//jobIntTypes.get(jj.getType());
            int typeZone = type + jj.getZoneId() * 100;
            //update the set of zones that have ID
            if (numberVacantJobsByZoneByType.get(typeZone) == 0){
                numberZonesByType.put(type, numberZonesByType.get(type) + 1);
            }
            //update the number of vacant jobs per job type
            numberVacantJobsByType.put(type, numberVacantJobsByType.get(type) + 1);
            numberVacantJobsByZoneByType.put(typeZone, numberVacantJobsByZoneByType.get(typeZone) + 1);
            count++;

        }
        logger.info("Number of vacant jobs " + count);

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
            //all jobs are vacant in this step of the synthetic population
            int type = 0;//jobIntTypes.get(jj.getType());
            int typeZone = 0 + jj.getZoneId() * 100;
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
    }


    private void updateMaps(int selectedJobType, int[] zoneType){

       numberVacantJobsByZoneByType.put(zoneType[0], numberVacantJobsByZoneByType.get(zoneType[0]) - 1);
        numberVacantJobsByType.put(selectedJobType, numberVacantJobsByType.get(selectedJobType) - 1);
        if (numberVacantJobsByZoneByType.get(zoneType[0]) < 1) {
            idZonesVacantJobsByType.get(selectedJobType)[zoneType[1]] = idZonesVacantJobsByType.get(selectedJobType)[numberZonesByType.get(selectedJobType) - 1];
            idZonesVacantJobsByType.put(selectedJobType, idZonesVacantJobsByType.get(selectedJobType));
            numberZonesByType.put(selectedJobType, numberZonesByType.get(selectedJobType) - 1);
            if (numberZonesByType.get(selectedJobType) < 1) {
                int w = 0;
                while (w < PropertiesSynPop.get().main.jobStringType.length & selectedJobType > jobIntTypes.get(jobStringTypes[w])) {
                    w++;
                }
                jobIntTypes.remove(jobStringTypes[w]);
                jobStringTypes[w] = jobStringTypes[jobStringTypes.length - 1];
                jobStringTypes = SiloUtil.removeOneElementFromZeroBasedArray(jobStringTypes, jobStringTypes.length - 1);
            }
        }
    }


    public int guessjobType(int zone){

        float[] cumProbability = new float[3];
        cumProbability[0] = dataSetSynPop.getTazAttributes().get(zone).get("pri");
        cumProbability[1] = dataSetSynPop.getTazAttributes().get(zone).get("sec");
        cumProbability[2] = dataSetSynPop.getTazAttributes().get(zone).get("ter");
        float threshold = SiloUtil.getRandomNumberAsFloat();
        for (int i = 0; i < cumProbability.length; i++) {
            if (cumProbability[i] > threshold) {
                return i;
            }
        }
        return cumProbability.length - 1;

    }


    public static int[] select (double[] probabilities, int[] id) {
        // select item based on probabilities (for zero-based float array)
        double sumProb = Arrays.stream(probabilities).sum();
        int[] results = new int[2];
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
}
