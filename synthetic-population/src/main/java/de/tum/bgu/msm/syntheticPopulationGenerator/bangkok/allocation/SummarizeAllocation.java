package de.tum.bgu.msm.syntheticPopulationGenerator.bangkok.allocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.run.data.dwelling.BangkokDwellingTypes;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.munich.preparation.MicroDataManager;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SummarizeAllocation {

    private static final Logger logger = Logger.getLogger(SummarizeAllocation.class);

    private final DataContainer dataContainer;

    private final DataSetSynPop dataSetSynPop;
    private final MicroDataManager microDataManager;
    private int previousHouseholds;
    private int previousPersons;
    private int totalHouseholds;

    private int firstHouseholdMunicipality;
    private HouseholdDataManager householdData;
    private RealEstateDataManager realEstate;
    private Map<Integer, Map<String, Double>> zonalSummary = new LinkedHashMap<>();
    private Map<Integer, Map<String, Double>> allocationErrors = new LinkedHashMap<>();

    public SummarizeAllocation(DataContainer dataContainer, DataSetSynPop dataSetSynPop){
        this.dataContainer = dataContainer;
        this.dataSetSynPop = dataSetSynPop;
        microDataManager = new MicroDataManager(dataSetSynPop);
    }

    public void run(){
        logger.info("   Running module: summarize data");
        previousHouseholds = 0;
        previousPersons = 0;
        householdData = dataContainer.getHouseholdDataManager();
        realEstate = dataContainer.getRealEstateDataManager();
        firstHouseholdMunicipality = 1;
        for (int municipality : dataSetSynPop.getMunicipalities()){
            logger.info("   Starting to summarize municipality " + municipality);
            List<Dwelling> dwellingMap = realEstate.getDwellings().stream().filter(x->x.getZoneId() == municipality).collect(Collectors.toList());
            List<Household> householdMap = householdData.getHouseholds().stream().filter(x -> dwellingMap.contains(x.getDwellingId())).collect(Collectors.toList());
            List<Person> personMap = householdData.getPersons().stream().filter(x -> householdMap.contains(x.getHousehold())).collect(Collectors.toList());
            zonalSummary.putIfAbsent(municipality, new LinkedHashMap<>());
            allocationErrors.putIfAbsent(municipality, new LinkedHashMap<>());
            summarizeByZone(municipality, householdMap, personMap, dwellingMap);
            allocationErrorsByZone(municipality);
        }
        outputSummaryByZone();
    }

    private void summarizeByZone(int municipality, List<Household> householdMap, List< Person> personMap, List< Dwelling> dwellingMap) {
        zonalSummary.get(municipality).put("hhTotal", (double) householdMap.stream().mapToInt(x -> x.getId()).count());
        zonalSummary.get(municipality).put("hhSize1", (double) householdMap.stream().filter(x ->x.getHhSize() == 1).count());
        zonalSummary.get(municipality).put("hhSize2", (double) householdMap.stream().filter(x ->x.getHhSize() == 2).count());
        zonalSummary.get(municipality).put("hhSize3", (double) householdMap.stream().filter(x ->x.getHhSize() == 3).count());
        zonalSummary.get(municipality).put("hhSize4", (double) householdMap.stream().filter(x ->x.getHhSize() == 4).count());
        zonalSummary.get(municipality).put("hhSize5+", (double) householdMap.stream().filter(x ->x.getHhSize() > 4).count());
        zonalSummary.get(municipality).put("detached_120", (double) dwellingMap.stream().filter(x ->x.getType().equals(BangkokDwellingTypes.DwellingTypeBangkok.DETATCHED_HOUSE_120)).count());
        zonalSummary.get(municipality).put("detached_200", (double) dwellingMap.stream().filter(x ->x.getType().equals(BangkokDwellingTypes.DwellingTypeBangkok.DETATCHED_HOUSE_200)).count());
        zonalSummary.get(municipality).put("high_rise50", (double) dwellingMap.stream().filter(x ->x.getType().equals(BangkokDwellingTypes.DwellingTypeBangkok.HIGH_RISE_CONDOMINIUM_50)).count());
        zonalSummary.get(municipality).put("high_rise30", (double) dwellingMap.stream().filter(x ->x.getType().equals(BangkokDwellingTypes.DwellingTypeBangkok.HIGH_RISE_CONDOMINIUM_30)).count());
        zonalSummary.get(municipality).put("low_rise50", (double) dwellingMap.stream().filter(x ->x.getType().equals(BangkokDwellingTypes.DwellingTypeBangkok.LOW_RISE_CONDOMINIUM_50)).count());
        zonalSummary.get(municipality).put("low_rise30", (double) dwellingMap.stream().filter(x ->x.getType().equals(BangkokDwellingTypes.DwellingTypeBangkok.LOW_RISE_CONDOMINIUM_30)).count());
        zonalSummary.get(municipality).put("quality1", (double) dwellingMap.stream().filter(x ->x.getQuality() == 1).count());
        zonalSummary.get(municipality).put("quality2", (double) dwellingMap.stream().filter(x ->x.getQuality() == 2).count());
        zonalSummary.get(municipality).put("quality3", (double) dwellingMap.stream().filter(x ->x.getQuality() == 3).count());
        zonalSummary.get(municipality).put("quality4", (double) dwellingMap.stream().filter(x ->x.getQuality() == 4).count());
        if(dwellingMap.size() > 1) {
            zonalSummary.get(municipality).put("rent", dwellingMap.stream().mapToInt(x -> x.getPrice()).average().getAsDouble());
        } else {
            zonalSummary.get(municipality).put("rent", 0.0);
        }
        if (personMap.size() > 1) {
            zonalSummary.get(municipality).put("income", personMap.stream().mapToInt(x ->x.getAnnualIncome()).average().getAsDouble());
        } else {
            zonalSummary.get(municipality).put("income", 0.0);
        }
        zonalSummary.get(municipality).put("population", (double) personMap.stream().mapToInt(x -> x.getId()).count());
        zonalSummary.get(municipality).put("males", (double) personMap.stream().filter(x ->x.getGender().equals(Gender.MALE)).count());
        zonalSummary.get(municipality).put("females", (double) personMap.stream().filter(x ->x.getGender().equals(Gender.FEMALE)).count());
        zonalSummary.get(municipality).put("workers", (double) personMap.stream().filter(x ->x.getOccupation().equals(Occupation.EMPLOYED)).count());

        int[] ageBracketsGender = new int[]{9,19,29,39,49,59,69,79,89,109};
        double countAgeMale = 0;
        double countAgeFemale = 0;
        for (int age : ageBracketsGender){
            double maleYoungerThan = (double) personMap.stream().filter(x->x.getGender().equals(Gender.MALE)).filter(x->x.getAge() <= age).count();
            zonalSummary.get(municipality).put("males"+age, maleYoungerThan - countAgeMale);
            countAgeMale = maleYoungerThan;
            double femaleYoungerThan = (double) personMap.stream().filter(x->x.getGender().equals(Gender.FEMALE)).filter(x->x.getAge() <= age).count();
            zonalSummary.get(municipality).put("females"+age, femaleYoungerThan - countAgeFemale);
            countAgeFemale = femaleYoungerThan;
        }
        int[] ageBrackets = new int[]{18,35,65,109};
        double countAge = 0;
        for (int age : ageBrackets){
            double youngerThan = (double) personMap.stream().filter(x->x.getGender().equals(Gender.MALE)).filter(x->x.getAge() <= age).count();
            zonalSummary.get(municipality).put("person"+age, youngerThan - countAge);
            countAge = youngerThan;
        }
    }

    private void allocationErrorsByZone(int municipality) {

        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("fem109", "females109");
        attributes.put("male109", "males109");
        attributes.put("fem89", "females89");
        attributes.put("male89", "males89");
        attributes.put("fem79", "females79");
        attributes.put("male79", "males79");
        attributes.put("fem9", "females9");
        attributes.put("male9", "males9");
        attributes.put("fem69", "females69");
        attributes.put("male69", "males69");
        attributes.put("fem19", "females19");
        attributes.put("male19", "males19");
        attributes.put("fem29", "females29");
        attributes.put("male29", "males29");
        attributes.put("fem59", "females59");
        attributes.put("male59", "males59");
        attributes.put("fem39", "females39");
        attributes.put("male39", "males39");
        attributes.put("fem49", "females49");
        attributes.put("male49", "males49");
        attributes.put("females", "females");
        attributes.put("males", "males");
        attributes.put("population", "population");
        attributes.put("households", "hhTotal");
        for (String attributeMarginals : attributes.keySet()) {
            allocationErrors.get(municipality).put(attributes.get(attributeMarginals), Math.abs((zonalSummary.get(municipality).get(attributes.get(attributeMarginals)) -
                    PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, attributeMarginals) /
                            PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, attributeMarginals))));
        }
        allocationErrors.get(municipality).put("condo", Math.abs((zonalSummary.get(municipality).get("high_rise50") +zonalSummary.get(municipality).get("high_rise30")-
                PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "condo") /
                        PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "condo"))));
        allocationErrors.get(municipality).put("apartment", Math.abs((zonalSummary.get(municipality).get("low_rise50") +zonalSummary.get(municipality).get("low_rise30")-
                PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "apartment") /
                        PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "apartment"))));
        allocationErrors.get(municipality).put("house", Math.abs((zonalSummary.get(municipality).get("detached_120") +zonalSummary.get(municipality).get("detached_200")-
                PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "house") /
                        PropertiesSynPop.get().main.marginalsMunicipality.getIndexedValueAt(municipality, "house"))));
    }

    private void outputSummaryByZone(){
        //PropertiesSynPop.get().main.cellsMatrix.appendColumn(incomeUpdate, "incomeScaler");

        PrintWriter pw = SiloUtil.openFileForSequentialWriting("microData/interimFiles/zonalDataSummary.csv", false);
        AtomicReference<String> header = new AtomicReference<>("zone");
        for (String key : zonalSummary.get(1).keySet()) {
            header.set(header + "," + key);
        }
        pw.println(header);
        AtomicReference<String> zoneStr = new AtomicReference<>("");
        for (int municipality : dataSetSynPop.getMunicipalities()) {
            zoneStr.set(Integer.toString(municipality));
            for (Double value : zonalSummary.get(municipality).values()) {
                zoneStr.set(zoneStr + "," + value);
            }
            pw.println(zoneStr);
        }
        pw.close();

        PrintWriter pw1 = SiloUtil.openFileForSequentialWriting("microData/interimFiles/allocationErrors.csv", false);
        AtomicReference<String> header1 = new AtomicReference<>("zone");
        for (String key : allocationErrors.get(1).keySet()) {
            header1.set(header1 + "," + key);
        }
        pw1.println(header1);
        AtomicReference<String> zoneStr1 = new AtomicReference<>("");
        for (int municipality : dataSetSynPop.getMunicipalities()) {
            zoneStr1.set(Integer.toString(municipality));
            for (Double value : allocationErrors.get(municipality).values()) {
                zoneStr1.set(zoneStr1 + "," + value);
            }
            pw1.println(zoneStr1);
        }
        pw1.close();
    }



}
