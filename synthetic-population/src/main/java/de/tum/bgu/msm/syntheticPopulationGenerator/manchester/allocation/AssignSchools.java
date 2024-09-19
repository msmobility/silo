package de.tum.bgu.msm.syntheticPopulationGenerator.manchester.allocation;

import com.google.common.collect.Table;
import de.tum.bgu.msm.common.matrix.Matrix;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.PersonMCR;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.schools.SchoolData;
import de.tum.bgu.msm.schools.SchoolUtils;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.util.*;

public class AssignSchools {

    private static final Logger logger = Logger.getLogger(AssignSchools.class);

    private final DataSetSynPop dataSetSynPop;
    private final DataContainerWithSchools dataContainer;

    private ArrayList<Person> studentArrayList;
    private int assignedStudents;

    private Matrix distanceImpedancePrimarySecondary;
    private Matrix distanceImpedanceTertiary;
    private Map<Integer, Map<Integer,Integer>> schoolCapacityMap;
    private Map<Integer, Integer> numberOfVacantPlacesByType;

    Map<Integer,Map<Integer,Map<Integer,Integer>>> schoolByZoneByType = new HashMap<>();


    public AssignSchools(DataContainerWithSchools dataContainer, DataSetSynPop dataSetSynPop){
        this.dataSetSynPop = dataSetSynPop;
        this.dataContainer = dataContainer;
    }

    public void run() {
        logger.info("   Running module: school allocation");
        calculateDistanceImpedance();
        initializeSchoolCapacity();
        readSchools();
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
            int schoolType = ((PersonMCR)p).getSchoolType();
            if (schoolType==3){
                schooltaz = selectTertiarySchool(hometaz);
            } else {
                schooltaz = selectPrimarySecondarySchool(hometaz, schoolType);
            }
            if (schooltaz > 0) {
                Map<Integer, Integer> weight = schoolByZoneByType.get(schooltaz).get(schoolType);
                int schoolId = SiloUtil.select(weight);
                ((PersonMCR)pp).setSchoolId(schoolId);
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

    private void readSchools() {
        SchoolData schoolData = dataContainer.getSchoolData();

        for (int row = 1; row <= PropertiesSynPop.get().main.schoolLocationlist.getRowCount(); row++) {

            int id = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"ID");
            int zone = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"oaID");
            float xCoordinate = PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"X");
            float yCoordinate = PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"Y");
            int schoolCapacity = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"capacity_imputed");
            int schoolType = (int) PropertiesSynPop.get().main.schoolLocationlist.getValueAt(row,"type");

            Coordinate coordinate = new Coordinate(xCoordinate,yCoordinate);
            schoolData.addSchool(SchoolUtils.getFactory().createSchool(id, schoolType, schoolCapacity,0,coordinate, zone));

            schoolByZoneByType.computeIfAbsent(zone, k -> new HashMap<>())
                    .computeIfAbsent(schoolType, k -> new HashMap<>())
                    .put(id, schoolCapacity);

        }
    }

}
