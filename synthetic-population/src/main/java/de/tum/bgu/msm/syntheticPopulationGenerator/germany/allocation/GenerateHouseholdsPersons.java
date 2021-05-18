package de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdMuc;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation.MicroDataManager;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.disability.DisabilityBase;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class GenerateHouseholdsPersons {

    private static final Logger logger = Logger.getLogger(GenerateHouseholdsPersons.class);

    private final DataContainer dataContainer;

    private final DataSetSynPop dataSetSynPop;
    private final de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation.MicroDataManager microDataManager;
    private int previousHouseholds;
    private int previousPersons;
    private int totalHouseholds;
    private Map<Integer, Float> probTAZ;
    private Map<Integer, Float> probMicroData;
    private double[] probabilityId;
    private double sumProbabilities;
    private double[] probabilityTAZ;
    private double sumTAZs;
    private int[] ids;
    private int[] idTAZs;
    private int personCounter;
    private int householdCounter;

    private HouseholdDataManager householdData;


    public GenerateHouseholdsPersons(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataContainer = dataContainer;
        this.dataSetSynPop = dataSetSynPop;
        microDataManager = new de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation.MicroDataManager(dataSetSynPop);
    }

    public void run(){
        logger.info("   Running module: household, person and dwelling generation");
        previousHouseholds = 0;
        previousPersons = 0;
        householdData = dataContainer.getHouseholdDataManager();
        for (int municipality : dataSetSynPop.getMunicipalities()){
            int municipalityTAZid = initializeMunicipalityData(municipality);
            List<Integer> hhSelection = selectMultipleHouseholds(totalHouseholds);
            List<Integer> tazSelection = selectMultipleTAZ(totalHouseholds);
            for (int draw = 0; draw < totalHouseholds; draw++) {
                int hhSelected = hhSelection.get(draw);
                int tazSelected = tazSelection.get(draw);
                Household household = generateHousehold(tazSelected, municipalityTAZid);
                generateDwelling(household.getId(), tazSelected, municipalityTAZid);
                generatePersons(hhSelected, household, tazSelected);
            }
            logger.info("   Municipality " + municipality + ". Generated " + householdCounter  + " households.");
        }
    }


    private Household generateHousehold(int cell100by100, int municipalityTAZid){

        HouseholdFactory factory = householdData.getHouseholdFactory();
        int id = householdData.getNextHouseholdId();
        Household household = factory.createHousehold(id,id,0);
                householdData.addHousehold(household);
        householdCounter++;
        int coordX = dataSetSynPop.getZoneCoordinates().get(cell100by100,"coordX");
        int coordY = dataSetSynPop.getZoneCoordinates().get(cell100by100,"coordY");
        //check whats in Zone features
        household.setAttribute("zone", municipalityTAZid);
        household.setAttribute("coordX", coordX);
        household.setAttribute("coordY", coordY);
        return household;
    }

    private void generateDwelling(int idHousehold, int cell100by100, int municipalityTAZid){

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        int newDdId = realEstate.getNextDwellingId();

        int coordX = dataSetSynPop.getZoneCoordinates().get(cell100by100,"coordX");
        int coordY = dataSetSynPop.getZoneCoordinates().get(cell100by100,"coordY");

        Dwelling dwell = DwellingUtils.getFactory().createDwelling(newDdId, municipalityTAZid, new Coordinate(coordX, coordY), idHousehold,
                DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234, 0, 4, 0, 0);
        realEstate.addDwelling(dwell);
    }


    private void generatePersons(int hhSelected, Household hh, int cell100by100){

        int hhSize = dataSetSynPop.getHouseholdTable().get(hhSelected, "hhSize");
        PersonFactory factory = householdData.getPersonFactory();
       for (int person = 0; person < hhSize; person++) {
            int id = householdData.getNextPersonId();
            int personSelected = dataSetSynPop.getHouseholdTable().get(hhSelected, "personCount") + person;
            int age = dataSetSynPop.getPersonTable().get(personSelected, "age");

            float BBSR = PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(cell100by100,"BBSR_Type");

            Gender gender = Gender.valueOf(dataSetSynPop.getPersonTable().get(personSelected, "gender"));
            Occupation occupation = Occupation.valueOf(dataSetSynPop.getPersonTable().get(personSelected, "occupation"));
            int income = microDataManager.translateIncome(dataSetSynPop.getPersonTable().get(personSelected, "income"));

            boolean license = MicroDataManager.obtainLicense(gender, age, BBSR);


            String jobtype = microDataManager.translateJobType10sectors(dataSetSynPop.getPersonTable().get(personSelected, "sectorComplete"));
            int school = dataSetSynPop.getPersonTable().get(personSelected, "school");
            PersonMuc pers = (PersonMuc) factory.createPerson(id, age, gender, occupation,PersonRole.SINGLE, 0, income); //(int id, int age, int gender, Race race, int occupation, int workplace, int income)
            pers.setDriverLicense(license);
            pers.setAttribute("jobType", jobtype);
            pers.setAttribute("schoolType", school);
            pers.setAttribute("disability",Disability.WITHOUT);
            householdData.addPerson(pers);
            householdData.addPersonToHousehold(pers, hh);
            personCounter++;
        }
    }


    private int selectMicroHouseholdWithReplacement() {

        int hhSelected = SiloUtil.select(probMicroData);
        if (probMicroData.get(hhSelected) > 1){
            probMicroData.put(hhSelected, probMicroData.get(hhSelected) - 1);
        } else {
            probMicroData.remove(hhSelected);
        }
        return hhSelected;
    }


    private int selectTAZwithoutReplacement(int hhSelected){

        int taz = SiloUtil.select(probTAZ);
        return taz;
    }


    private int initializeMunicipalityData(int municipality){

        logger.info("   Municipality " + municipality + ". Starting to generate households and persons");
        if (!PropertiesSynPop.get().main.boroughIPU) {
            totalHouseholds = (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "hhTotal");
        } else {
            totalHouseholds = (int) PropertiesSynPop.get().main.marginalsBorough.getIndexedValueAt(municipality, "borough_hhTotal");
        }
        probTAZ = dataSetSynPop.getProbabilityZone().get(municipality);
        probMicroData = new HashMap<>();
        probabilityId = new double[dataSetSynPop.getWeights().getRowCount()];
        ids = new int[probabilityId.length];
        sumProbabilities = 0;
        for (int id : dataSetSynPop.getWeights().getColumnAsInt("ID")) {
            probMicroData.put(id, dataSetSynPop.getWeights().getValueAt(id, Integer.toString(municipality)));
        }
        for (int i = 0; i < probabilityId.length; i++) {
            sumProbabilities = sumProbabilities + dataSetSynPop.getWeights().getValueAt(i + 1, Integer.toString(municipality));
            probabilityId[i] = dataSetSynPop.getWeights().getValueAt(i + 1, Integer.toString(municipality));
            ids[i] = (int) dataSetSynPop.getWeights().getValueAt(i + 1, "ID");
        }
        probabilityTAZ = new double[dataSetSynPop.getProbabilityZone().get(municipality).keySet().size()];
        sumTAZs = 0;
        probabilityTAZ = dataSetSynPop.getProbabilityZone().get(municipality).values().stream().mapToDouble(Number::doubleValue).toArray();
        for (int i = 1; i < probabilityTAZ.length; i++) {
            probabilityTAZ[i] = probabilityTAZ[i] + probabilityTAZ[i - 1];
        }
        idTAZs = dataSetSynPop.getProbabilityZone().get(municipality).keySet().stream().mapToInt(Number::intValue).toArray();
        sumTAZs = dataSetSynPop.getProbabilityZone().get(municipality).values().stream().mapToDouble(Number::doubleValue).sum();
        personCounter = 0;
        householdCounter = 0;
        return (int) PropertiesSynPop.get().main.cellsMatrix.getIndexedValueAt(idTAZs[0],"TAZ");
    }


    public List<Integer> selectMultipleHouseholds(int selections) {

        Integer[] selected;
        selected = new Integer[selections];
        int completed = 0;
        for (int iteration = 0; iteration < 100; iteration++){
            int m = selections - completed;
            double[] randomChoices = new double[m];
            for (int k = 0; k < randomChoices.length; k++) {
                randomChoices[k] = SiloUtil.getRandomNumberAsDouble()*selections;
            }
            Arrays.sort(randomChoices);

            //look up for the n travellers
            int p = 0;
            double cumulative = probabilityId[p];
            for (double randomNumber : randomChoices){
                while (randomNumber > cumulative && p < probabilityId.length - 1) {
                    p++;
                    cumulative += probabilityId[p];
                }
                if (probabilityId[p] > 0) {
                    selected[completed] = ids[p];
                    completed++;
                }
            }
        }
        List<Integer> hhSelectionShuffle = Arrays.asList(selected);
        Collections.shuffle(hhSelectionShuffle);
        return hhSelectionShuffle;

    }

    private List<Integer> selectMultipleTAZ(int selections){

        Integer[] selected;
        selected = new Integer[selections];
        int completed = 0;
        for (int iteration = 0; iteration < 100; iteration++){
            int m = selections - completed;
            //double[] randomChoice = new double[(int)(numberOfTrips*1.1) ];
            double[] randomChoices = new double[m];
            for (int k = 0; k < randomChoices.length; k++) {
                randomChoices[k] = SiloUtil.getRandomNumberAsDouble()*selections;
            }
            Arrays.sort(randomChoices);

            //look up for the n travellers
            int p = 0;
            double cumulative = probabilityTAZ[p];
            for (double randomNumber : randomChoices){
                while (randomNumber > cumulative && p < probabilityTAZ.length - 1) {
                    p++;
                    cumulative += probabilityTAZ[p];
                }
                if (probabilityTAZ[p] > 0) {
                    selected[completed] = idTAZs[p];
                    completed++;
                }
            }
        }
        List<Integer> tazSelectionShuffle = Arrays.asList(selected);
        Collections.shuffle(tazSelectionShuffle);
        return tazSelectionShuffle;
    }

}
