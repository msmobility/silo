package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import de.tum.bgu.msm.common.matrix.Matrix;
import de.tum.bgu.msm.common.matrix.RowVector;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobFactoryMuc;
import de.tum.bgu.msm.data.job.JobMuc;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.MicroDataManager;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class ReallocateWorkersNursingHomev2 {

    private static final Logger logger = Logger.getLogger(ReallocateWorkersNursingHomev2.class);

    private final DataContainer dataContainer;

    private final DataSetSynPop dataSetSynPop;
    private final MicroDataManager microDataManager;

    private HouseholdDataManager householdData;
    private RealEstateDataManager realEstate;

    private Map<Integer, Coordinate> nursingHomesCoordinates = new HashMap<>();
    private Map<Integer, Map<Integer, Person>> residentsByNursingHome = new HashMap<>();
    private Map<Integer, Integer> totalResidentsByNursingHome = new HashMap<>();
    private Map<Integer, Integer> nursingHomeTAZ = new HashMap<>();
    private Map<Integer, Map<Person, Float>> workerImpedanceByNursingHome = new HashMap<>();

    private Map<Integer, Map<Person, Integer>> possibleWorkersAtZone = new HashMap<>();
    private Matrix distanceImpedance;

    private JobFactoryMuc jobFactory;
    private JobDataManager jobData;

    public ReallocateWorkersNursingHomev2(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataContainer = dataContainer;
        this.dataSetSynPop = dataSetSynPop;
        microDataManager = new MicroDataManager(dataSetSynPop);
    }

    public void run(){
        logger.info("   Running module: reallocation of workers to nursing homes");
        householdData = dataContainer.getHouseholdDataManager();
        realEstate = dataContainer.getRealEstateDataManager();
        jobData = dataContainer.getJobDataManager();
        jobFactory = (JobFactoryMuc) jobData.getFactory();

        initializeNursingHomesAndSeniors();
        for (int nursingHome : totalResidentsByNursingHome.keySet()){
            int workTAZ = nursingHomeTAZ.get(nursingHome);
            int workers = Math.toIntExact(Math.round(totalResidentsByNursingHome.get(nursingHome) * 0.75));
            int assignedWorkers = 0;
           /* Map<Integer, Double > probabilityByTaz = new HashMap<>();
            for (int zone : possibleWorkersAtZone.keySet()){
                double prob = distanceImpedance.getValueAt()

            }
            RowVector distances = distanceImpedance.getRow(nursingHomeTAZ.get(nursingHome));
            Math.exp(distances.getValueAt(ids[id] / 100) * Math.pow(possibleWorkersAtZone.get(ids[id]).size(), 0.45)));*/
            for (int i = 1; i <= workers; i++){

                int homeTaz = selectHomeTAZofWorker(nursingHomeTAZ.get(nursingHome));
                Person worker = SiloUtil.select(possibleWorkersAtZone.get(homeTaz));
                //int randomWorkerInhomeTaz = (int) (possibleWorkersAtZone.get(homeTaz).size() * SiloUtil.getRandomNumberAsFloat());
                //Person worker = possibleWorkersAtZone.get(homeTaz).get(randomWorkerInhomeTaz);

                assignWorkerToNursingHome(worker, nursingHome, homeTaz);
                assignedWorkers++;
            }
            /*if (workers > workerImpedanceByNursingHome.size()){
                //all workers in the map are assigned to this nursing home
                int missingWorkers = workers - workerImpedanceByNursingHome.size();
                logger.info("   Nursing home " + nursingHome + " does not have enough workers. Needed extra " + missingWorkers);
                for (Person worker : workerImpedanceByNursingHome.get(nursingHome).keySet()){
                    assignWorkerToNursingHome(worker, nursingHome);
                    assignedWorkers++;
                }
            } else {
                Map<Integer, Person> selectedWorkers = selectWorkerWithReplacement(workers, workerImpedanceByNursingHome.get(nursingHome));
                for (Person worker : selectedWorkers.values()){
                    assignWorkerToNursingHome(worker, nursingHome);
                    assignedWorkers++;
                }
            }*/
            logger.info("   Nursing home " + nursingHome + " assigned " + assignedWorkers + " workers for " + totalResidentsByNursingHome.get(
                    nursingHome) + " residents.");
        }

    }


   private void assignWorkerToNursingHome(Person worker, int nursingHome){
       int previousJob = worker.getJobId();
       jobData.getJobFromId(previousJob).setWorkerID(-1);
       int id = jobData.getNextJobId();
       JobMuc jj = jobFactory.createJob(id, nursingHomeTAZ.get(nursingHome), nursingHomesCoordinates.get(nursingHome), worker.getId(), "NursingHome");
       jobData.addJob(jj);
       worker.setWorkplace(jj.getId());
   }

    private void assignWorkerToNursingHome(Person worker, int nursingHome, int homeTaz){
        int previousJob = worker.getJobId();
        jobData.getJobFromId(previousJob).setWorkerID(-1);
        int id = jobData.getJobs().size() + 1;
        JobMuc jj = jobFactory.createJob(id, nursingHomeTAZ.get(nursingHome), nursingHomesCoordinates.get(nursingHome), worker.getId(), "NursingHome");
        jobData.addJob(jj);
        worker.setWorkplace(jj.getId());
        possibleWorkersAtZone.get(homeTaz).remove(worker);
        double distance = dataSetSynPop.getDistanceTazToTaz().getValueAt(homeTaz, nursingHomeTAZ.get(nursingHome));
        jj.setAttribute("distance", distance);
        if (possibleWorkersAtZone.get(homeTaz).isEmpty()){
            possibleWorkersAtZone.remove(homeTaz);
        }
    }

    private int selectHomeTAZofWorker(int workTAZ){

        int homeTAZ = -1;
        //int[] ids = dataSetSynPop.getTazIDs();
        Integer[] ids = possibleWorkersAtZone.keySet().toArray(new Integer[0]);
        double[] probs = new double[ids.length];
            RowVector distances = distanceImpedance.getRow(workTAZ);
            IntStream.range(0, probs.length).parallel().forEach(id ->
                    probs[id] = Math.exp(distanceImpedance.getValueAt(ids[id], workTAZ) * Math.pow(possibleWorkersAtZone.get(ids[id]).size(), 0.45)));
            homeTAZ = select(probs, ids);
        return homeTAZ;
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

    public static int select (double[] probabilities, Integer[] id) {
        // select item based on probabilities (for zero-based float array)
        double sumProb = Arrays.stream(probabilities).sum();
        int[] results = new int[2];
        double selPos = sumProb * SiloUtil.getRandomNumberAsFloat();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] == 1){probabilities[i] = 0;}
            sum += probabilities[i];
            if (sum > selPos) {
                //return i;
                //results[0] = id[i];
                //results[1] = i;
                return id[i];
            }
        }
        //results[0] = id[probabilities.length - 1];
        //results[1] = probabilities.length - 1;
        return id[probabilities.length - 1];
    }


    private void initializeNursingHomesAndSeniors(){

        for (int id : PropertiesSynPop.get().main.nursingHomes.getColumnAsInt("n_home_id")) {
            int zone = (int) PropertiesSynPop.get().main.nursingHomes.getIndexedValueAt(id, "ID_cell");
            double coordX = PropertiesSynPop.get().main.nursingHomes.getIndexedValueAt(id,"CENTROID_X");
            double coordY = PropertiesSynPop.get().main.nursingHomes.getIndexedValueAt(id,"CENTROID_Y");
            nursingHomesCoordinates.put(id, new Coordinate(coordX, coordY));
            totalResidentsByNursingHome.put(id,0);
            nursingHomeTAZ.put(id, zone);
            workerImpedanceByNursingHome.put(id, new HashMap<>());
        }
        calculateDistanceImpedance();
        Map<Integer, Float> utilityHBW = dataSetSynPop.getTripLengthDistribution().column("HBW");

        for (int zones : dataSetSynPop.getTazs()){
            possibleWorkersAtZone.putIfAbsent(zones, new HashMap<>());
        }

        for (Household hh : householdData.getHouseholds()) {
            int nursingHome = Integer.parseInt(hh.getAttribute("Nursing_home_id").get().toString());
            if (nursingHome > -1) {
                int previousResidents = totalResidentsByNursingHome.get(nursingHome);
                int updatedResidents = previousResidents + hh.getHhSize();
                totalResidentsByNursingHome.put(nursingHome, updatedResidents);
            } else {
                for (Person pp : hh.getPersons().values()) {
                    if (pp.getOccupation().equals(Occupation.EMPLOYED)){
                        String type = jobData.getJobFromId(pp.getJobId()).getType();

                        int homeTAZ = Integer.parseInt(hh.getAttribute("zone").get().toString());
                            /*for (int possibleNursingHome : totalResidentsByNursingHome.keySet()) {
                                int distance = (int) dataSetSynPop.getDistanceTazToTaz().getValueAt(homeTAZ, nursingHomeTAZ.get(possibleNursingHome));
                                if (distance < 150){
                                    float utility = utilityHBW.get(distance);
                                    workerImpedanceByNursingHome.get(possibleNursingHome).putIfAbsent(pp, utility);
                                }
                            }*/
                        double distance = dataSetSynPop.getDistanceTazToTaz().getValueAt(homeTAZ, jobData.getJobFromId(pp.getJobId()).getZoneId());
                        jobData.getJobFromId(pp.getJobId()).setAttribute("distance", distance);
                        double threshold = 0.95;
                        if (!type.equals("Agri") && !type.equals("Mnft") && !type.equals("Util") && !type.equals("Cons")) {
                            threshold = 0.60;
                        }
                        if (SiloUtil.getRandomNumberAsDouble() > threshold){
                            int id = 1;
                            if (!possibleWorkersAtZone.get(homeTAZ).isEmpty()){
                                id = possibleWorkersAtZone.get(homeTAZ).size() + 1;
                            }
                            possibleWorkersAtZone.get(homeTAZ).putIfAbsent(pp, 1);
                        }
                    }
                }
            }

        }
        for (int zones : dataSetSynPop.getTazs()){
            if (possibleWorkersAtZone.get(zones).isEmpty()){
                possibleWorkersAtZone.remove(zones);
            }

        }
    }

    private Map<Integer, Person> selectWorkerWithReplacement(int selections, Map<Person, Float> workers) {

        Map<Integer, Person> selectedHouseholds = new HashMap<>();
        for (int i = 1; i <= selections; i++) {
            Person hhSelected = SiloUtil.select(workers);
            selectedHouseholds.putIfAbsent(i, hhSelected);
            workers.remove(hhSelected);
            if (workers.size() < 1){
                break;
            }
        }

        return selectedHouseholds;
    }



}
