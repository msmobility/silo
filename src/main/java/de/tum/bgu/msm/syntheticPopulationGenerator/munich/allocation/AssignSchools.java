package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import com.google.common.collect.Table;
import com.pb.common.matrix.Matrix;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.RealEstateDataManager;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import org.apache.log4j.Logger;

import java.util.*;

public class AssignSchools {

    private static final Logger logger = Logger.getLogger(AssignSchools.class);

    private final DataSetSynPop dataSetSynPop;
    private final SiloDataContainer dataContainer;

    private ArrayList<Person> studentArrayList;
    private int assignedStudents;

    private Matrix distanceImpedancePrimarySecondary;
    private Matrix distanceImpedanceTertiary;
    private Map<Integer, Map<Integer,Integer>> schoolCapacityMap;
    private Map<Integer, Integer> numberOfVacantPlacesByType;

    public AssignSchools(SiloDataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }

    public void run() {
        logger.info("   Running module: school allocation");
        calculateDistanceImpedance();
        initializeSchoolCapacity();
        shuffleStudents();
        double logging = 2;
        int it = 12;
        RealEstateDataManager realEstate = dataContainer.getRealEstateData();
        for (Person pp : studentArrayList){
            int schooltaz;
            int hometaz = realEstate.getDwelling(pp.getHh().getDwellingId()).getZone();
            if (pp.getSchoolType() == 3){
                schooltaz = selectTertiarySchool(hometaz);
            } else {
                schooltaz = selectPrimarySecondarySchool(hometaz, pp.getSchoolType());
            }
            if (schooltaz > 0) {
                pp.setSchoolPlace(schooltaz);
                pp.setJobTAZ(schooltaz);
            } else {
                pp.setJobTAZ(-2);
            }
            if (assignedStudents == logging){
                logger.info("   Assigned " + assignedStudents + " schools.");
                it++;
                logging = Math.pow(2, it);
            }
        }

    }


    private void calculateDistanceImpedance(){

        distanceImpedanceTertiary = new Matrix(dataSetSynPop.getDistanceTazToTaz().getRowCount(), dataSetSynPop.getDistanceTazToTaz().getColumnCount());
        distanceImpedancePrimarySecondary = new Matrix(dataSetSynPop.getDistanceTazToTaz().getRowCount(), dataSetSynPop.getDistanceTazToTaz().getColumnCount());
        Map<Integer, Float> utilityMapTertiary = dataSetSynPop.getTripLengthDistribution().column("Tertiary");
        for (int i = 1; i <= dataSetSynPop.getDistanceTazToTaz().getRowCount(); i ++){
            for (int j = 1; j <= dataSetSynPop.getDistanceTazToTaz().getColumnCount(); j++){
                int distance = (int) dataSetSynPop.getDistanceTazToTaz().getValueAt(i,j);
                float utilityTertiary = 0.00000001f;
                if (distance < 200){
                    utilityTertiary = utilityMapTertiary.get(distance);
                }
                distanceImpedanceTertiary.setValueAt(i,j,utilityTertiary);
                distanceImpedancePrimarySecondary.setValueAt(i,j, distance);
            }
        }
    }


    private int selectTertiarySchool(int hometaz){

        int schooltaz = -2;
        if (numberOfVacantPlacesByType.get(3) > 0) {
            Map<Integer, Float> probability = new HashMap<>();
            Iterator<Integer> iterator = schoolCapacityMap.get(3).keySet().iterator();
            while (iterator.hasNext()) {
                Integer zone = iterator.next();
                float prob = distanceImpedanceTertiary.getValueAt(hometaz, zone) * schoolCapacityMap.get(3).get(zone);
                probability.put(zone, prob);
            }
            schooltaz = SiloUtil.select(probability);
            int remainingCapacity = schoolCapacityMap.get(3).get(schooltaz);
            if (remainingCapacity > 0) {
                schoolCapacityMap.get(3).put(schooltaz, remainingCapacity - 1);
            } else {
                schoolCapacityMap.get(3).remove(schooltaz);
            }
            numberOfVacantPlacesByType.put(3, numberOfVacantPlacesByType.get(3) - 1);
        }
        assignedStudents++;
        return schooltaz;
    }


    private int selectPrimarySecondarySchool(int hometaz, int schoolType){

        int schooltaz = -2;
        if (numberOfVacantPlacesByType.get(schoolType) > 0) {
            float minDistance = 100000;
            Iterator<Integer> iterator = schoolCapacityMap.get(schoolType).keySet().iterator();
            while (iterator.hasNext()) {
                Integer zone = iterator.next();
                if (distanceImpedancePrimarySecondary.getValueAt(hometaz, zone) < minDistance) {
                    schooltaz = zone;
                    minDistance = distanceImpedancePrimarySecondary.getValueAt(hometaz, zone);
                }
            }
            int remainingCapacity = schoolCapacityMap.get(schoolType).get(schooltaz);
            if (remainingCapacity > 0) {
                schoolCapacityMap.get(schoolType).put(schooltaz, remainingCapacity - 1);
            } else {
                schoolCapacityMap.get(schoolType).remove(schooltaz);
            }
            numberOfVacantPlacesByType.put(schoolType, numberOfVacantPlacesByType.get(schoolType) - 1);
        }
        assignedStudents++;
        return schooltaz;
    }


    private void shuffleStudents(){

        Map<Integer, Person> personMap = (Map<Integer, Person>) dataContainer.getHouseholdData().getPersons();
        studentArrayList = new ArrayList<>();
        for (Map.Entry<Integer,Person> pair : personMap.entrySet() ){
            if (pair.getValue().getOccupation() == 3){
                studentArrayList.add(pair.getValue());
                pair.getValue().setSchoolPlace(-1);
                pair.getValue().setJobTAZ(-1);
            }
        }
        Collections.shuffle(studentArrayList);
        assignedStudents = 0;
    }


    private void initializeSchoolCapacity(){

        schoolCapacityMap = new HashMap<>();
        numberOfVacantPlacesByType = new HashMap<>();
        Table<Integer, Integer, Integer> schoolCapacity = dataSetSynPop.getSchoolCapacity();
        Iterator<Integer> iteratorRow = schoolCapacity.rowKeySet().iterator();
        while (iteratorRow.hasNext()){
            int zone = iteratorRow.next();
            Iterator<Integer> iteratorCol = schoolCapacity.columnKeySet().iterator();
            while (iteratorCol.hasNext()){
                int schoolType = iteratorCol.next();
                int places = schoolCapacity.get(zone, schoolType);
                if (places > 0) {
                    Map<Integer, Integer> prevPlaces = new HashMap<>();
                    if (schoolCapacityMap.get(schoolType)!= null) {
                        prevPlaces = schoolCapacityMap.get(schoolType);
                    }
                    prevPlaces.put(zone, places);
                    schoolCapacityMap.put(schoolType, prevPlaces);
                    int previousPlaces = 0;
                    if (numberOfVacantPlacesByType.get(schoolType)!= null){
                        previousPlaces = numberOfVacantPlacesByType.get(schoolType);
                    }
                    numberOfVacantPlacesByType.put(schoolType, previousPlaces + places);
                }
            }
        }
    }

}
