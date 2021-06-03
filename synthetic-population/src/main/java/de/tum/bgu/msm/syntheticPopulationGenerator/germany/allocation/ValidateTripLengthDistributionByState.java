package de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.job.JobType;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import org.apache.commons.math.stat.Frequency;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ValidateTripLengthDistributionByState {

    private static final Logger logger = Logger.getLogger(ValidateTripLengthDistributionByState.class);

    private final DataSetSynPop dataSetSynPop;
    private final DataContainer dataContainer;
    private TableDataSet cellsMatrix;
    private TableDataSet municipalityODMatrix;
    private TableDataSet countyODMatrix;
    long totalTripLength =0 ;
    long numWorkers =0;
    int subPopulation;

    public ValidateTripLengthDistributionByState(DataContainer dataContainer, DataSetSynPop dataSetSynPop, int subPopulation){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
        this.subPopulation = subPopulation;
    }

    public void run(){
        logger.info("   Running module: read population");
        summarizeCommutersTripLength();
        System.out.println("total trip length: "+ (totalTripLength) );
        System.out.println("number of workers: "+ (numWorkers) );

    }


    private void summarizeCommutersTripLength(){
        Map<String, Frequency> frequencies = obtainWorkerFlows();
        summarizeFlows(frequencies, "microData/subPopulations/tripLengthDistributionWork.csv");
    }


    private Map<String, Frequency> obtainWorkerFlows(){
        Map<String, Frequency> frequencies = new HashMap<>();
        for (String jobType : JobType.getJobTypes()) {
            Frequency commuteDistance = new Frequency();
            frequencies.putIfAbsent(jobType, commuteDistance);
        }
        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        for (Person pp : dataContainer.getHouseholdDataManager().getPersons()){
            //TODO not part of the public person api anymore
            if (Integer.parseInt(String.valueOf(pp.getAttribute("workZone").get())) > 0){
                Household hh = pp.getHousehold();
                int origin = realEstate.getDwelling(hh.getDwellingId()).getZoneId();
                int destination = Integer.parseInt(pp.getAttribute("workZone").get().toString());
                int value = (int) dataSetSynPop.getDistanceTazToTaz().getValueAt(origin, destination);
                String jobType = pp.getAttribute("jobType").get().toString();
                Frequency distanceByType = frequencies.get(jobType);
                distanceByType.addValue(value);
                frequencies.put(jobType, distanceByType);
                totalTripLength = value + totalTripLength;
            }
        }
        return frequencies;
    }


    private void summarizeFlows(Map<String,Frequency> travelTimes, String fileName){
        //to obtain the trip length distribution
        int[] timeThresholds1 = new int[200];
        double[] frequencyTT1 = new double[200];
        Map<String, double[]> cumFrequency = new HashMap<>();
        for (String jobType : JobType.getJobTypes()) {
            for (int row = 0; row < timeThresholds1.length; row++) {
                timeThresholds1[row] = row + 1;
                frequencyTT1[row] = travelTimes.get(jobType).getCumPct(timeThresholds1[row]);
            }
            cumFrequency.putIfAbsent(jobType, frequencyTT1);
        }
        writeVectorToCSV(timeThresholds1, cumFrequency, fileName);

    }


    private void writeVectorToCSV(int[] thresholds, Map<String,double[]> frequencies, String outputFile){
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, true));
            if (subPopulation == 0) {
                pw.print("subPopulation,threshold");
                for (String jobType : JobType.getJobTypes()) {
                    pw.print(",freq_" + jobType);
                }
                pw.println();
            }
            for (int i = 0; i < thresholds.length; i++) {
                pw.print(subPopulation + "," + thresholds[i]);
                for (String jobType : JobType.getJobTypes()) {
                    pw.print("," + frequencies.get(jobType)[i]);
                }
                pw.println();
            }
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


   }