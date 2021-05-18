package de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation;

import com.google.common.math.LongMath;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.common.matrix.Matrix;
import de.tum.bgu.msm.common.matrix.RowVector;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.job.Job;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonMuc;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class AssignJobs {

    private static final Logger logger = Logger.getLogger(AssignJobs.class);

    private final DataSetSynPop dataSetSynPop;
    private final DataContainer dataContainer;
    private Matrix tripLengthImpedance;

    private HashMap<String, Integer> jobIntTypes;
    protected HashMap<Integer, int[]> idVacantJobsByZoneType;
    protected HashMap<Integer, Integer> numberVacantJobsByType;
    protected HashMap<Integer, int[]> idZonesVacantJobsByType;
    protected HashMap<Integer, Integer> numberVacantJobsByZoneByType;
    protected HashMap<Integer, Integer> numberTotalJobsByZoneByType;
    protected HashMap<Integer, Integer> numberZonesByType;

    private String[] jobStringTypes;
    private ArrayList<Person> workerArrayList;
    private int assignedJobs;
    private int[] tazIds;


    public AssignJobs(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }


    public void run() {
        logger.info("   Running module: job de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation.AssignJobs");
        calculateTripLengthImpedance();
        identifyVacantJobsByZoneType();
        shuffleWorkers();
        logger.info("Number of workers " + workerArrayList.size());
        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        HouseholdDataManager households = dataContainer.getHouseholdDataManager();
        //TableDataSet cellsMatrix = PropertiesSynPop.get().main.cellsMatrix;

        for (Person pp : workerArrayList){
            String selectedJobTypeAsString = (String) pp.getAttribute("jobType").get();
            ///todo found some empty job types
            if (selectedJobTypeAsString.equals("")){
               pp.setAttribute("jobType", "Serv");
                selectedJobTypeAsString="Serv";
            }

            int selectedJobType = jobIntTypes.get(selectedJobTypeAsString);

            Household hh = pp.getHousehold();

            int origin = realEstate.getDwelling(hh.getDwellingId()).getZoneId();

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
        logger.info("   Finished job de.tum.bgu.msm.syntheticPopulationGenerator.germany.disability. Assigned " + assignedJobs + " jobs.");

    }



   private void calculateTripLengthImpedance(){


       int rowCount = dataSetSynPop.getDistanceTazToTaz().getRowCount();
       int columnCount = dataSetSynPop.getDistanceTazToTaz().getColumnCount();
       tripLengthImpedance = new Matrix(rowCount, columnCount);
        for (int i = 1; i <= rowCount; i ++){
            for (int j = 1; j <= columnCount; j++){
                int tripLength = (int) dataSetSynPop.getDistanceTazToTaz().getValueAt(i,j);
                tripLengthImpedance.setValueAt(i, j, tripLength);

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
            double alpha = 0.2700;  // 0.6500;  0.6000; 0.5500
            double gamma =-0.0200;  //-0.0300; -0.0200;-0.0050
            int[] ids = idZonesVacantJobsByType.get(selectedJobType);
            RowVector triplength = tripLengthImpedance.getRow(homeTaz);
            IntStream.range(0, probs.length).parallel().forEach(id -> probs[id] = Math.exp(Math.exp(triplength.getValueAt((ids[id]-selectedJobType)/100 )*gamma) * Math.pow(numberTotalJobsByZoneByType.get(ids[id]),alpha) )); //100%: *0.2; Bayern*1.2; Bayern_noType *1.2*0.05; Bayern_oneType_moreVacant
            //IntStream.range(0, probs.length).parallel().forEach(id -> probs[id] = Math.exp(Math.exp(triplength.getValueAt((ids[id]-selectedJobType)/100 )*gamma)));
            workplace = randomSelect(triplength, probs, ids,selectedJobType);
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
        TableDataSet jobsByTaz = PropertiesSynPop.get().main.jobsByTaz;

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
        numberTotalJobsByZoneByType = new HashMap<>();

        //create the counter hashmaps
        for (int i = 0; i < PropertiesSynPop.get().main.jobStringType.length; i++){
            int type = jobIntTypes.get(PropertiesSynPop.get().main.jobStringType[i]);
            numberZonesByType.put(type,0);
            numberVacantJobsByType.put(type,0);
            for (int taz : jobsByTaz.getColumnAsInt("taz")){
                numberVacantJobsByZoneByType.put(type + taz * 100, 0);
                numberTotalJobsByZoneByType.put(type + taz * 100, 0);
            }
        }

        //get the totals
        int count = 0;
        for (Job jj: jobs) {
            //set all jobs vacant to allocate them
            jj.setWorkerID(-1);
            int type = jobIntTypes.get(jj.getType());
            int typeZone = type + jj.getZoneId() * 100;
            //update the set of zones that have ID
            if (numberVacantJobsByZoneByType.get(typeZone) == 0){
                numberZonesByType.put(type, numberZonesByType.get(type) + 1);
            }
            //update the number of vacant jobs per job type
            numberVacantJobsByType.put(type, numberVacantJobsByType.get(type) + 1);
            numberVacantJobsByZoneByType.put(typeZone, numberVacantJobsByZoneByType.get(typeZone) + 1);
            numberTotalJobsByZoneByType.put(typeZone, numberTotalJobsByZoneByType.get(typeZone) + 1);
            count++;

        }
        logger.info("Number of vacant jobs " + count);

        //create the IDs Hashmaps and reset the counters
        for (String jobType : PropertiesSynPop.get().main.jobStringType){
            int type = jobIntTypes.get(jobType);
            int[] dummy = SiloUtil.createArrayWithValue(numberZonesByType.get(type),0);
            idZonesVacantJobsByType.put(type,dummy);
            numberZonesByType.put(type,0);
            for (int taz : jobsByTaz.getColumnAsInt("taz")){
                int typeZone = type + taz * 100;
                int[] dummy2 = SiloUtil.createArrayWithValue(numberVacantJobsByZoneByType.get(typeZone), 0);
                idVacantJobsByZoneType.put(typeZone, dummy2);
                numberVacantJobsByZoneByType.put(typeZone, 0);
                numberTotalJobsByZoneByType.put(typeZone, 0);
            }
        }
        //fill the Hashmaps with IDs
        for (Job jj: jobs) {
            //all jobs are vacant in this step of the synthetic population
            int type = jobIntTypes.get(jj.getType());
            int typeZone = jobIntTypes.get(jj.getType()) + jj.getZoneId() * 100;
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
            numberTotalJobsByZoneByType.put(typeZone, numberTotalJobsByZoneByType.get(typeZone) + 1);

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

    public static int[] randomSelect(RowVector triplength, double[] probabilities, int[] id, int selectedJobType) {
        // select item based on probabilities (for zero-based float array)
        double sumProb = Arrays.stream(probabilities).sum();

        int[] results = new int[2];
        double selPos = sumProb * SiloUtil.getRandomNumberAsFloat();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {

            sum += probabilities[i];

            if (sum > selPos) {
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[probabilities.length - 1];
        results[1] = probabilities.length - 1;
        return results;
    }

    public static int[] limitedRansomSelect(RowVector triplength, double[] probabilities, int[] id, int selectedJobType) {
        // select item based on probabilities (for zero-based float array)
        Vector<Float> seqDistance = new Vector<>();

        for (int k=0; k < probabilities.length; k++){
            seqDistance.add(triplength.getValueAt((id[k]-selectedJobType)/100));
        }
        Collections.sort(seqDistance);

        Float threshold;

        if(seqDistance.size()>=100){
            threshold = seqDistance.get(100);
        }else{
            threshold = seqDistance.get(seqDistance.size());
        }

        double sumProb = 0;
        for (int j=0; j < probabilities.length; j++){
            if (triplength.getValueAt((id[j]-selectedJobType)/100)<=threshold){
                sumProb += probabilities[j];
            }else{
                sumProb += 0;
            }
        }

        int[] results = new int[2];
        double selPos = sumProb * SiloUtil.getRandomNumberAsFloat();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {

            if (triplength.getValueAt((id[i]-selectedJobType)/100)<=100){
                sum += probabilities[i];
            }

            if (sum > selPos) {
                results[0] = id[i];
                results[1] = i;
                return results;
            }
        }
        results[0] = id[probabilities.length - 1];
        results[1] = probabilities.length - 1;
        return results;
    }

    public static int[] maxUtilitySelect(RowVector triplength, double[] probabilities, int[] id, int selectedJobType) {
        // select item based on probabilities (for zero-based float array)
        double maxProb = Arrays.stream(probabilities).max().getAsDouble();

        int[] results = new int[2];

        for (int i = 0; i < probabilities.length; i++) {

            if (probabilities[i] == maxProb) {
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
