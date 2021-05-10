package de.tum.bgu.msm.syntheticPopulationGenerator.bangkok.allocation;

import com.google.common.collect.Table;
import de.tum.bgu.msm.common.matrix.Matrix;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.*;

public class AssignSchools {

    private static final Logger logger = Logger.getLogger(AssignSchools.class);

    private final DataSetSynPop dataSetSynPop;
    private final DataContainer dataContainer;

    private ArrayList<Person> studentArrayList;
    private int assignedStudents;

    private Matrix distanceImpedancePrimarySecondary;
    private Matrix distanceImpedanceTertiary;
    private Map<Integer, Map<Integer,Integer>> schoolCapacityMap;
    private Map<Integer, Integer> numberOfVacantPlacesByType;

    public AssignSchools(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }

    public void run() {
        logger.info("   Running module: school allocation");
        calculateDistanceImpedance();
        initializeSchoolCapacity();
        shuffleStudents();

        for (Person pp : dataContainer.getHouseholdDataManager().getPersons()){
            pp.setDriverLicense(obtainLicense(pp.getGender(),pp.getAge()));
        }


        double logging = 2;
        int it = 12;
        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        for (Person p : studentArrayList){
            Person pp = p;
            int schooltaz;
            Household household = pp.getHousehold();
            int hometaz = realEstate.getDwelling(household.getDwellingId()).getZoneId();
            if (pp.getAge() > 18 ){
                schooltaz = selectTertiarySchool(hometaz);
            } else {
                schooltaz = selectPrimarySecondarySchool(hometaz, 1);
            }
            if (schooltaz > 0) {
                pp.setWorkplace(schooltaz);
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
            int remainingCapacity = schoolCapacityMap.get(3).get(schooltaz) - 1;
            if (remainingCapacity > 0) {
                schoolCapacityMap.get(3).put(schooltaz, remainingCapacity);
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
            int remainingCapacity = schoolCapacityMap.get(schoolType).get(schooltaz) - 1;
            if (remainingCapacity > 0) {
                schoolCapacityMap.get(schoolType).put(schooltaz, remainingCapacity);
            } else {
                schoolCapacityMap.get(schoolType).remove(schooltaz);
            }
            numberOfVacantPlacesByType.put(schoolType, numberOfVacantPlacesByType.get(schoolType) - 1);
        }
        assignedStudents++;
        return schooltaz;
    }


    private void shuffleStudents(){

        studentArrayList = new ArrayList<>();
        for (Person p : dataContainer.getHouseholdDataManager().getPersons()){
            Person pp = p;
            if (pp.getOccupation() == Occupation.STUDENT){
                studentArrayList.add(pp);
                pp.setWorkplace(-1);
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

    public boolean obtainLicense(Gender gender, int age){
        boolean license = false;
        int row = 1;
        int threshold = 0;
        if (age > 17) {
            if (age < 29) {
                if (gender == Gender.MALE) {
                    threshold = 86;
                } else {
                    threshold = 87;
                }
            } else if (age < 39) {
                if (gender == Gender.MALE) {
                    threshold = 95;
                } else {
                    threshold = 94;
                }
            } else if (age < 49) {
                if (gender == Gender.MALE) {
                    threshold = 97;
                } else {
                    threshold = 95;
                }
            } else if (age < 59) {
                if (gender == Gender.MALE) {
                    threshold = 96;
                } else {
                    threshold = 89;
                }
            } else if (age < 64) {
                if (gender == Gender.MALE) {
                    threshold = 95;
                } else {
                    threshold = 86;
                }
            } else if (age < 74) {
                if (gender == Gender.MALE) {
                    threshold = 95;
                } else {
                    threshold = 71;
                }
            } else {
                if (gender == Gender.MALE) {
                    threshold = 88;
                } else {
                    threshold = 44;
                }
            }
            if (SiloUtil.getRandomNumberAsDouble() * 100 < threshold) {
                license = true;
            }
        }
        return license;
    }

}
