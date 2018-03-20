package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.RealEstateDataManager;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import org.apache.commons.math.stat.Frequency;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class ValidateTripLengthDistribution {

    private static final Logger logger = Logger.getLogger(ValidateTripLengthDistribution.class);

    private final DataSetSynPop dataSetSynPop;
    private final SiloDataContainer dataContainer;

    public ValidateTripLengthDistribution(SiloDataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }

    public void run(){
        logger.info("   Running module: read population");
        summarizeCommutersTripLength();
        summarizeStudentsTripLength();
    }


    private void summarizeCommutersTripLength(){
        ArrayList<Person> workerArrayList = obtainWorkers();
        Frequency travelTimes = obtainFlows(workerArrayList);
        summarizeFlows(travelTimes, "microData/interimFiles/tripLengthDistributionWork.csv");
    }


    private void summarizeStudentsTripLength(){
        for (int school = 1; school <= 3 ; school++){
            ArrayList<Person> studentArrayList = obtainStudents(school);
            Frequency travelTimes = obtainFlows(studentArrayList);
            summarizeFlows(travelTimes, "microData/interimFiles/tripLengthDistributionSchool" + school + ".csv");
        }
    }


    private ArrayList<Person> obtainWorkers(){
        Map<Integer, Person> personMap = (Map<Integer, Person>) dataContainer.getHouseholdData().getPersons();
        ArrayList<Person> workerArrayList = new ArrayList<>();
        for (Map.Entry<Integer, Person> pair : personMap.entrySet()){
            if (pair.getValue().getOccupation() == 1){
                workerArrayList.add(pair.getValue());
            }
        }
        return workerArrayList;
    }


    private Frequency obtainFlows(ArrayList<Person> personArrayList){
        Frequency commuteDistance = new Frequency();
        RealEstateDataManager realEstate = dataContainer.getRealEstateData();
        for (Person pp : personArrayList){
            if (pp.getJobTAZ() > 0){
                int origin = realEstate.getDwelling(pp.getHh().getDwellingId()).getZone();
                int value = (int) dataSetSynPop.getDistanceTazToTaz().getValueAt(origin, pp.getJobTAZ());
                commuteDistance.addValue(value);
            }
        }
        return commuteDistance;
    }


    private void summarizeFlows(Frequency travelTimes, String fileName){
        //to obtain the trip length distribution
        int[] timeThresholds1 = new int[79];
        double[] frequencyTT1 = new double[79];
        for (int row = 0; row < timeThresholds1.length; row++) {
            timeThresholds1[row] = row + 1;
            frequencyTT1[row] = travelTimes.getCumPct(timeThresholds1[row]);
        }
        writeVectorToCSV(timeThresholds1, frequencyTT1, fileName);

    }


    private void writeVectorToCSV(int[] thresholds, double[] frequencies, String outputFile){
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, true));
            pw.println("threshold,frequency");
            for (int i = 0; i< thresholds.length; i++) {
                pw.println(thresholds[i] + "," + frequencies[i]);
            }
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private ArrayList<Person> obtainStudents (int school){
        Map<Integer, Person> personMap = (Map<Integer, Person>) dataContainer.getHouseholdData().getPersons();
        ArrayList<Person> workerArrayList = new ArrayList<>();
        for (Map.Entry<Integer, Person> pair : personMap.entrySet()) {
            if (pair.getValue().getOccupation() == 3 & pair.getValue().getSchoolType() == school) {
                workerArrayList.add(pair.getValue());
            }
        }
        return workerArrayList;
    }

}
