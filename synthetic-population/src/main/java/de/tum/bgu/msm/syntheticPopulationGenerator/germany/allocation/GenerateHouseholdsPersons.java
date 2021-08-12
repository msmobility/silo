package de.tum.bgu.msm.syntheticPopulationGenerator.germany.allocation;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation.MicroDataManager;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class GenerateHouseholdsPersons {

    private static final Logger logger = Logger.getLogger(GenerateHouseholdsPersons.class);

    private final DataContainer dataContainer;

    private final DataSetSynPop dataSetSynPop;
    private final de.tum.bgu.msm.syntheticPopulationGenerator.germany.preparation.MicroDataManager microDataManager;
    private int previousHouseholds;
    private int previousPersons;
    private int totalPersons;
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
    private Map<Integer, Map<String, Integer>> attributeCounterByMunicipality = new LinkedHashMap<>();
    private Map<Integer, Map<String, Integer>> attributeCounterByBorough = new LinkedHashMap<>();
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
        //int municipality = dataSetSynPop.getMunicipalities().get(0);
        for (int municipality : dataSetSynPop.getMunicipalities()){
            int municipalityTAZid = initializeMunicipalityData(municipality);
            initializeCounters(municipality);
            //List<Integer> hhSelection = selectMultipleHouseholds(totalHouseholds);
            String fileSelection = "C:/models/silo/germany/microData/" + PropertiesSynPop.get().main.state +
                    "/interimFiles/sampling_" + municipality + ".csv";
            TableDataSet hhSelection = SiloUtil.readCSVfile(fileSelection);
            String fileSelectionTAZ = "C:/models/silo/germany/microData/" + PropertiesSynPop.get().main.state +
                    "/interimFiles/samplingTAZ_" + municipality + ".csv";
            TableDataSet tazSelection = SiloUtil.readCSVfile(fileSelectionTAZ);
            int row = 1;
            int generatedPopulation = 0;
            while (generatedPopulation < totalPersons) {
                int hhSelected = (int) hhSelection.getValueAt(row,"x");
                int tazSelected = (int) tazSelection.getValueAt(row,"ID_cell");
                Household household = generateHousehold(tazSelected, municipality, tazSelection, row);
                generateDwelling(household.getId(), tazSelected, municipalityTAZid, tazSelection, row);
                generatePersons(hhSelected, household, tazSelected, municipality);
                row++;
                generatedPopulation = generatedPopulation + household.getHhSize();
            }

            logger.info("   Municipality " + municipality + ". Generated " + householdCounter  + " households.");
        }
        outputSummary();
    }


    private Household generateHousehold(int cell100by100, int municipality, TableDataSet tazSelection, int draw){

        HouseholdFactory factory = householdData.getHouseholdFactory();
        int id = householdData.getNextHouseholdId();
        Household household = factory.createHousehold(id,id,0);
                householdData.addHousehold(household);
        householdCounter++;
        int coordX = (int) tazSelection.getValueAt(draw,"x_mp_31468"); //int coordX = dataSetSynPop.getZoneCoordinates().get(cell100by100,"coordX");
        int coordY = (int) tazSelection.getValueAt(draw,"y_mp_31468"); //int coordY = dataSetSynPop.getZoneCoordinates().get(cell100by100,"coordY");

        //check whats in Zone features
        household.setAttribute("zone", municipality);
        household.setAttribute("idCity", Math.round(dataSetSynPop.getTazAttributes().get(cell100by100).get("idCity")));
        household.setAttribute("coordX", coordX);
        household.setAttribute("coordY", coordY);
        return household;
    }

    private void generateDwelling(int idHousehold, int cell100by100, int municipalityTAZid, TableDataSet tazSelection, int draw){

        RealEstateDataManager realEstate = dataContainer.getRealEstateDataManager();
        int newDdId = realEstate.getNextDwellingId();

        int coordX = (int) tazSelection.getValueAt(draw,"x_mp_31468"); //int coordX = dataSetSynPop.getZoneCoordinates().get(cell100by100,"coordX");
        int coordY = (int) tazSelection.getValueAt(draw,"y_mp_31468"); //int coordY = dataSetSynPop.getZoneCoordinates().get(cell100by100,"coordY");

        Dwelling dwell = DwellingUtils.getFactory().createDwelling(newDdId, municipalityTAZid, new Coordinate(coordX, coordY), idHousehold,
                DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234, 0, 4, 0, 0);
        realEstate.addDwelling(dwell);
    }


    private void generatePersons(int hhSelected, Household hh, int cell100by100, int municipality){

        int hhSize = dataSetSynPop.getHouseholdTable().get(hhSelected, "hhSize");
        PersonFactory factory = householdData.getPersonFactory();
        hh.setAttribute("hhRecord", dataSetSynPop.getHouseholdTable().get(hhSelected, "recordHh"));
        hh.setAttribute("hhSelected", hhSelected);
        //hh.setAttribute("hhWeight", probabilityId[hhSelected]);
        hh.setAttribute("taz", cell100by100);
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
            pers.setAttribute("copiedPerson", personSelected);
            householdData.addPerson(pers);
            householdData.addPersonToHousehold(pers, hh);
            personCounter++;
           updatePersonCounter(pers, municipality);
        }
        updateHouseholdCounters(hh, municipality);

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
            totalPersons = (int) PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "population");
        } else {
            totalPersons = (int) PropertiesSynPop.get().main.marginalsBorough.getIndexedValueAt(municipality, "borough_population");
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


    private void initializeCounters(int municipality){

        Map<String, Integer> attributeCounter = new LinkedHashMap<>();
        attributeCounter.putIfAbsent("female99", 0);
        attributeCounter.put("male99", 0);
        attributeCounter.put("female65", 0);
        attributeCounter.put("male65", 0);
        attributeCounter.put("female49", 0);
        attributeCounter.put("male49", 0);
        attributeCounter.put("female29", 0);
        attributeCounter.put("male29", 0);
        attributeCounter.put("female17", 0);
        attributeCounter.put("male17", 0);
        attributeCounter.put("females", 0);
        attributeCounter.put("males", 0);
        attributeCounter.put("population", 0);
        attributeCounter.put("hhTotal", 0);
        attributeCounter.put("hhSize1", 0);
        attributeCounter.put("hhSize2", 0);
        attributeCounter.put("hhSize3", 0);
        attributeCounter.put("hhSize4", 0);
        attributeCounter.put("hhSize5", 0);

        attributeCounterByMunicipality.putIfAbsent(municipality, attributeCounter);

        if (PropertiesSynPop.get().main.boroughIPU) {
            Map<String, Integer> attributesBorough = new LinkedHashMap<>();
            attributesBorough.putIfAbsent("borough_female99", 0);
            attributesBorough.put("borough_male99", 0);
            attributesBorough.put("borough_female65", 0);
            attributesBorough.put("borough_male65", 0);
            attributesBorough.put("borough_female49", 0);
            attributesBorough.put("borough_male49", 0);
            attributesBorough.put("borough_female29", 0);
            attributesBorough.put("borough_male29", 0);
            attributesBorough.put("borough_female17", 0);
            attributesBorough.put("borough_male17", 0);
            attributesBorough.put("borough_females", 0);
            attributesBorough.put("borough_males", 0);
            attributesBorough.put("borough_population", 0);
            attributesBorough.put("borough_hhTotal", 0);
            attributesBorough.put("borough_hhSize1", 0);
            attributesBorough.put("borough_hhSize2", 0);
            attributesBorough.put("borough_hhSize3", 0);
            attributesBorough.put("borough_hhSize4", 0);
            attributesBorough.put("borough_hhSize5", 0);
            attributeCounterByBorough.putIfAbsent(municipality, attributesBorough);
        }

    }

    private void updatePersonCounter(PersonMuc pp, int municipality){
        String ageGender = "";
        String gender = "";
        int row = 0;
        while (pp.getAge() > PropertiesSynPop.get().main.ageBracketsPerson[row]) {
            row++;
        }
        if (pp.getGender().equals(Gender.MALE)){
            ageGender = "male" + PropertiesSynPop.get().main.ageBracketsPerson[row];
            gender = "males";
        } else {
            ageGender = "female" + PropertiesSynPop.get().main.ageBracketsPerson[row];
            gender = "females";
        }
        attributeCounterByMunicipality.get(municipality).put(ageGender, attributeCounterByMunicipality.get(municipality).get(ageGender)+1);
        attributeCounterByMunicipality.get(municipality).put(gender, attributeCounterByMunicipality.get(municipality).get(gender)+1);
        attributeCounterByMunicipality.get(municipality).put("population", attributeCounterByMunicipality.get(municipality).get("population")+1);


    }

    private void updateHouseholdCounters(Household hh, int municipality){
        String hhSize = "";
        if (hh.getHhSize() < 5){
            hhSize = "hhSize" + hh.getHhSize();
        } else {
            hhSize = "hhSize5";
        }
        attributeCounterByMunicipality.get(municipality).put(hhSize, attributeCounterByMunicipality.get(municipality).get(hhSize)+1);
        attributeCounterByMunicipality.get(municipality).put("hhTotal", attributeCounterByMunicipality.get(municipality).get("hhTotal")+1);


    }


    private void outputSummary(){
        //PropertiesSynPop.get().main.cellsMatrix.appendColumn(incomeUpdate, "incomeScaler");
        String file = "microData/"
                + PropertiesSynPop.get().main.state
                + "/interimFiles/"
                + "allocationSummaryMunicipality"
                + ".csv";
        PrintWriter pw = SiloUtil.openFileForSequentialWriting(file, false);
        AtomicReference<String> header = new AtomicReference<>("zone");
        for (String key : attributeCounterByMunicipality.get(dataSetSynPop.getMunicipalities().get(0)).keySet()) {
            header.set(header + "," + key);
        }
        pw.println(header);
        AtomicReference<String> zoneStr = new AtomicReference<>("");
        for (int municipality : dataSetSynPop.getMunicipalities()) {
            //int municipality = 1;
            zoneStr.set(Integer.toString(municipality));
            for (Integer value : attributeCounterByMunicipality.get(municipality).values()) {
                zoneStr.set(zoneStr + "," + value);
            }
            pw.println(zoneStr);
        }
        pw.close();


    }


}
