package de.tum.bgu.msm.syntheticPopulationGenerator.bangkok.allocation;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.job.JobDataManager;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.commons.math.stat.Frequency;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ValidateTripLengthDistribution {

    private static final Logger logger = Logger.getLogger(ValidateTripLengthDistribution.class);

    private final DataSetSynPop dataSetSynPop;
    private final DataContainer dataContainer;
    private TableDataSet cellsMatrix;
    private TableDataSet municipalityODMatrix;
    private TableDataSet countyODMatrix;

    public ValidateTripLengthDistribution(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }

    public void run(){
        logger.info("   Running module: read population");
        initializeODmatrices();
        summarizeCommutersTripLength();
        summarizeStudentsTripLength();
    }


    private void summarizeCommutersTripLength(){
        ArrayList<Person> workerArrayList = obtainWorkers();
        Frequency travelTimes = obtainFlows(workerArrayList);
        summarizeFlows(travelTimes, "microData/interimFiles/tripLengthDistributionWork.csv");
        SiloUtil.writeTableDataSet(municipalityODMatrix, "microData/interimFiles/odMatrixMunicipalityFinal.csv");
        SiloUtil.writeTableDataSet(countyODMatrix, "microData/interimFiles/odMatrixCountyFinal.csv");
    }


    private void summarizeStudentsTripLength(){
        for (int school = 1; school <= 3 ; school++){
            ArrayList<Person> studentArrayList = obtainStudents(school);
            Frequency travelTimes = obtainFlows(studentArrayList);
            summarizeFlows(travelTimes, "microData/interimFiles/tripLengthDistributionSchool" + school + ".csv");
        }
    }


    private ArrayList<Person> obtainWorkers(){
        ArrayList<Person> workerArrayList = new ArrayList<>();
        for (Person pp : dataContainer.getHouseholdDataManager().getPersons()){
            if (pp.getOccupation() == Occupation.EMPLOYED){
                workerArrayList.add(pp);
            }
        }
        return workerArrayList;
    }


    private Frequency obtainFlows(ArrayList<Person> personArrayList){
        Frequency commuteDistance = new Frequency();
        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        JobDataManager jobDataManager = dataContainer.getJobDataManager();
        for (Person pp : personArrayList){
            //TODO not part of the public person api anymore
            if (pp.getJobId() > 0){
                Household hh = pp.getHousehold();
                int origin = realEstate.getDwelling(hh.getDwellingId()).getZoneId();
                int destination = jobDataManager.getJobFromId(pp.getJobId()).getZoneId();
                int value = (int) dataSetSynPop.getDistanceTazToTaz().getValueAt(origin, destination);
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
        ArrayList<Person> workerArrayList = new ArrayList<>();
        for (Person pp : dataContainer.getHouseholdDataManager().getPersons()) {
            if (pp.getOccupation() == Occupation.STUDENT) {
                workerArrayList.add(pp);
            }
        }
        return workerArrayList;
    }


    private void initializeODmatrices(){
        cellsMatrix = PropertiesSynPop.get().main.cellsMatrix;
        cellsMatrix.buildIndex(cellsMatrix.getColumnPosition("ID_cell"));
        municipalityODMatrix = new TableDataSet();
        municipalityODMatrix.appendColumn(dataSetSynPop.getCityIDs(),"id");
        for (int municipality : dataSetSynPop.getMunicipalities()){
            SiloUtil.addIntegerColumnToTableDataSet(municipalityODMatrix, Integer.toString(municipality));
        }
        municipalityODMatrix.buildIndex(municipalityODMatrix.getColumnPosition("id"));
        countyODMatrix = new TableDataSet();
        countyODMatrix.appendColumn(dataSetSynPop.getCountyIDs(), "id");
        for (int county : dataSetSynPop.getCounties()){
            SiloUtil.addIntegerColumnToTableDataSet(countyODMatrix, Integer.toString(county));
        }
        countyODMatrix.buildIndex(countyODMatrix.getColumnPosition("id"));
    }

}
