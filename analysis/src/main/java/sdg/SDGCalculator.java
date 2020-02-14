package sdg;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.Mode;
import de.tum.bgu.msm.data.Purpose;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingUsage;
import de.tum.bgu.msm.data.dwelling.RealEstateDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.util.MitoUtil;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import sdg.data.AnalyzedPerson;
import sdg.data.DataContainerSdg;
import sdg.data.Trip;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;


public class SDGCalculator {

    private static Logger logger = Logger.getLogger(SDGCalculator.class);
    public static List<Trip> tripList = new ArrayList<>();
    private Collection<Household> households = new ArrayList<>();
    private Collection<Dwelling> dwellings = new ArrayList<>();
    private Collection<Person> persons = new ArrayList<>();
    private RealEstateDataManager realEstateDataManager;
    private Map<Integer, List<Household>> hhByZone = new HashMap<>();
    private Map<Integer, List<Person>> ppByZone = new HashMap<>();
    private Map<Integer, List<Dwelling>> ddByZone = new HashMap<>();
    private Map< Id<org.matsim.api.core.v01.population.Person>, AnalyzedPerson> matsimPerson = new HashMap<>();
    private Map<Integer, Person> matsimIdSiloPerson = new HashMap<>();
    private Map<Integer, List<Trip>> commutingTripsByZone = new HashMap<>();
    private Map<Integer, List<Trip>> schoolTripsByZone = new HashMap<>();
    private Map<Integer, Map<Mode, List<Trip>>> ttByModeByZone = new HashMap<>();

    public void setMatsimPerson(Map<Id<org.matsim.api.core.v01.population.Person>, AnalyzedPerson> matsimPerson) {
        this.matsimPerson = matsimPerson;
    }

    void calculateSdgIndicators(DataContainerSdg dataContainer, String outputPath, int year) {
        loadDataSet(dataContainer);
        calculateNonSpatialSdgIndicators(dataContainer,outputPath,year);
        calculateSpatialSdgIndicators(dataContainer,outputPath,year);
    }

    private void loadDataSet(DataContainer dataContainer) {
        households = dataContainer.getHouseholdDataManager().getHouseholds();
        dwellings = dataContainer.getRealEstateDataManager().getDwellings();
        persons = dataContainer.getHouseholdDataManager().getPersons();
        realEstateDataManager = dataContainer.getRealEstateDataManager();
        hhByZone = households.stream().collect(Collectors.groupingBy(hh -> HouseholdUtil.getHhZone(hh, dataContainer)));
        ppByZone = persons.stream().collect(Collectors.groupingBy(pp -> HouseholdUtil.getHhZone(pp.getHousehold(), dataContainer)));
        ddByZone = dwellings.stream().collect(Collectors.groupingBy(dd -> dd.getZoneId()));
        commutingTripsByZone = tripList.stream().filter(tt -> tt.getPurpose().equals(Purpose.HBW)).collect(Collectors.groupingBy(tt -> realEstateDataManager.getDwelling(tt.getPerson().getHousehold().getDwellingId()).getZoneId()));
        schoolTripsByZone = tripList.stream().filter(tt -> tt.getPurpose().equals(Purpose.HBE)).collect(Collectors.groupingBy(tt -> realEstateDataManager.getDwelling(tt.getPerson().getHousehold().getDwellingId()).getZoneId()));
        ttByModeByZone = tripList.stream().collect(Collectors.groupingBy(tt -> realEstateDataManager.getDwelling(tt.getPerson().getHousehold().getDwellingId()).getZoneId(), Collectors.groupingBy(tt -> tt.getMode())));

        for (Trip trip : tripList){
            if (matsimPerson.get(trip.getId())!=null){
                matsimIdSiloPerson.put(trip.getId(),trip.getPerson());
            }
        }
    }

    private void calculateSpatialSdgIndicators(DataContainerSdg dataContainer, String outputPath, int year) {
        //Spatial indicators
        String fileSpatialSDGIndicator = outputPath + "/spatialSDGIndicators.csv";
        PrintWriter spatialSDGIndicator = MitoUtil.openFileForSequentialWriting(fileSpatialSDGIndicator, false);

        //write header
        spatialSDGIndicator.println("year,zoneId,shareOfLowIncomeHH,avgRentIncomeRatio,avgFloorAreaPerPerson,shareOfAuto0,shareOfAuto1,shareOfAuto2,shareOfAuto3,shareOfddQuality1,shareOfddQuality2,shareOfddQuality3,vacantDwellingShare,populationDensity,avgCommutingTime,avgSchoolDistance,autoDriver,autoPassenger,bicycle,bus,train,tramOrMetro,walk");

        for (Zone zone : dataContainer.getGeoData().getZones().values()) {
            spatialSDGIndicator.print(year);
            spatialSDGIndicator.print(",");
            spatialSDGIndicator.print(zone.getZoneId());
            spatialSDGIndicator.print(",");
            if(hhByZone.get(zone.getZoneId())!= null) {
                spatialSDGIndicator.print(hhByZone.get(zone.getZoneId()).stream().filter(hh -> HouseholdUtil.getIncomeCategoryForIncome(HouseholdUtil.getHhIncome(hh)).equals(IncomeCategory.LOW)).count() / (double)hhByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(hhByZone.get(zone.getZoneId()).stream().mapToDouble(hh -> realEstateDataManager.getDwelling(hh.getDwellingId()).getPrice() / (double)(HouseholdUtil.getHhIncome(hh) + 1)).average().getAsDouble());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(hhByZone.get(zone.getZoneId()).stream().mapToDouble(hh -> realEstateDataManager.getDwelling(hh.getDwellingId()).getFloorSpace() / (double)hh.getHhSize()).average().getAsDouble());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print((double)hhByZone.get(zone.getZoneId()).stream().filter(hh -> hh.getAutos() == 0).count()/(double)hhByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print((double)hhByZone.get(zone.getZoneId()).stream().filter(hh -> hh.getAutos() == 1).count()/(double)hhByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print((double)hhByZone.get(zone.getZoneId()).stream().filter(hh -> hh.getAutos() == 2).count()/(double)hhByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print((double)hhByZone.get(zone.getZoneId()).stream().filter(hh -> hh.getAutos() > 2).count()/(double)hhByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
            }else{
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
            }

            if(ddByZone.get(zone.getZoneId())!= null) {
                spatialSDGIndicator.print(ddByZone.get(zone.getZoneId()).stream().filter(dd -> dd.getQuality() == 1).count() / (double)ddByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(ddByZone.get(zone.getZoneId()).stream().filter(dd -> dd.getQuality() == 2).count() / (double)ddByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(ddByZone.get(zone.getZoneId()).stream().filter(dd -> dd.getQuality() == 3).count() / (double)ddByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(ddByZone.get(zone.getZoneId()).stream().filter(dd -> dd.getUsage().equals(DwellingUsage.VACANT)).count()/(double)ddByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
            }else{
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
            }
//            spatialSDGIndicator.print(realEstateDataManager.getAvailableCapacityForConstruction(zone.getZoneId()));
//            spatialSDGIndicator.print(",");
            if(ppByZone.get(zone.getZoneId())!= null) {
                spatialSDGIndicator.print(ppByZone.get(zone.getZoneId()).size());
                spatialSDGIndicator.print(",");
            }else{
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
            }

            if(commutingTripsByZone.get(zone.getZoneId())!= null) {
                spatialSDGIndicator.print(commutingTripsByZone.get(zone.getZoneId()).stream().mapToDouble(tt -> tt.getTripTravelTime()).average().getAsDouble());
                spatialSDGIndicator.print(",");
            }else{
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
            }

            if(schoolTripsByZone.get(zone.getZoneId())!= null) {
                spatialSDGIndicator.print(schoolTripsByZone.get(zone.getZoneId()).stream().mapToDouble(tt -> tt.getTripTravelTime()).average().getAsDouble());
            }else{
                spatialSDGIndicator.print(0);
            }

            if(ttByModeByZone.get(zone.getZoneId())!= null) {
                ttByModeByZone.get(zone.getZoneId()).entrySet().forEach(entry -> spatialSDGIndicator.print("," + entry.getValue().size()));
            }else{
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
                spatialSDGIndicator.print(",");
                spatialSDGIndicator.print(0);
            }
            spatialSDGIndicator.println();
        }

        spatialSDGIndicator.close();

    }

    private void calculateNonSpatialSdgIndicators(DataContainerSdg dataContainer, String outputPath, int year) {

        String fileNonSpatialSDGIndicator = outputPath + "/nonSpatialSDGIndicators.csv";
        PrintWriter nonSpatialSDGIndicator = MitoUtil.openFileForSequentialWriting(fileNonSpatialSDGIndicator, false);

        //write header
        logger.info("avg commuting time");
        nonSpatialSDGIndicator.println("year,incomeCategory,avgCommutingTime");
        Map<IncomeCategory, List<Trip>> commutingTripByIncome = tripList.stream().filter(tt -> tt.getPurpose().equals(Purpose.HBW)).collect(Collectors.groupingBy(tt -> HouseholdUtil.getIncomeCategoryForIncome(HouseholdUtil.getHhIncome(tt.getPerson().getHousehold()))));
        commutingTripByIncome.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + "," + entry.getKey() +  "," + entry.getValue().stream().mapToDouble(tt -> tt.getTripTravelTime()).average().getAsDouble()));
        nonSpatialSDGIndicator.println("year,mode,avgCommutingTime");
        Map<Mode, List<Trip>> commutingTripByMode = tripList.stream().filter(tt -> tt.getPurpose().equals(Purpose.HBW)).collect(Collectors.groupingBy(tt -> tt.getMode()));
        commutingTripByMode.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + "," + entry.getKey() +  "," + entry.getValue().stream().mapToDouble(tt -> tt.getTripTravelTime()).average().getAsDouble()));
        nonSpatialSDGIndicator.println(year + ",all," + tripList.stream().filter(tt -> tt.getPurpose().equals(Purpose.HBW)).mapToDouble(tt->tt.getTripTravelTime()).average().getAsDouble());

        logger.info("mode share");
        nonSpatialSDGIndicator.println("year,mode,share");
        Map<Mode, List<Trip>> ttByMode = tripList.stream().collect(Collectors.groupingBy(tt -> tt.getMode()));
        ttByMode.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + "," + entry.getKey() +  "," + entry.getValue().size()/tripList.size()));

        logger.info("rentIncomeRatio");
        nonSpatialSDGIndicator.println("year,rentIncomeRatio,value");
        nonSpatialSDGIndicator.println(year + ",all," + households.stream().mapToDouble(hh->(double) (realEstateDataManager.getDwelling(hh.getDwellingId()).getPrice() / (HouseholdUtil.getHhIncome(hh)+1))).average().getAsDouble());

        logger.info("dwelling quality");
        nonSpatialSDGIndicator.println("year,incomeCategory,dwellingQuality,value");
        Map<IncomeCategory, Map<Integer, List<Household>>> hhByIncomeByDdQuality = households.stream().collect(Collectors.groupingBy(hh -> HouseholdUtil.getIncomeCategoryForIncome(HouseholdUtil.getHhIncome(hh)), Collectors.groupingBy(hh -> dataContainer.getRealEstateDataManager().getDwelling(hh.getDwellingId()).getQuality())));
        hhByIncomeByDdQuality.entrySet().forEach(entry -> entry.getValue().entrySet().forEach(entryDwellingQuality -> nonSpatialSDGIndicator.println(year + "," + entry.getKey() + "," + entryDwellingQuality.getKey() + "," + entryDwellingQuality.getValue().size())));
        nonSpatialSDGIndicator.println("year,dwellingQuality,share");
        Map<Integer, List<Dwelling>> ddByQuality = dwellings.stream().collect(Collectors.groupingBy(dd -> dd.getQuality()));
        ddByQuality.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + "," + entry.getKey() + "," + entry.getValue().size()/dwellings.size()));


        nonSpatialSDGIndicator.println("year,incomeCategory,avgfloorSpace");
        Map<IncomeCategory, List<Household>> hhByIncome = households.stream().collect(Collectors.groupingBy(hh -> HouseholdUtil.getIncomeCategoryForIncome(HouseholdUtil.getHhIncome(hh))));
        hhByIncome.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + "," + entry.getKey() + "," + entry.getValue().stream().mapToDouble(hh -> realEstateDataManager.getDwelling(hh.getDwellingId()).getFloorSpace() / hh.getHhSize()).average().getAsDouble()));
        nonSpatialSDGIndicator.println(year + ",all," + households.stream().mapToDouble(hh -> realEstateDataManager.getDwelling(hh.getDwellingId()).getFloorSpace() / hh.getHhSize()).average().getAsDouble());

        nonSpatialSDGIndicator.println("year,incomeCategory,carOwnership,value");
        Map<IncomeCategory, Map<Integer, List<Household>>> hhByIncomeByAuto = households.stream().collect(Collectors.groupingBy(hh -> HouseholdUtil.getIncomeCategoryForIncome(HouseholdUtil.getHhIncome(hh)),Collectors.groupingBy(hh -> hh.getAutos())));
        hhByIncomeByAuto.entrySet().forEach(entry -> entry.getValue().entrySet().forEach(entryIncome -> nonSpatialSDGIndicator.println(year + "," + entry.getKey() + "," + entryIncome.getKey() + "," + entryIncome.getValue().size())));
        nonSpatialSDGIndicator.println(year + ",all," + households.stream().mapToDouble(hh -> hh.getAutos()).average().getAsDouble());

        nonSpatialSDGIndicator.println(year + ",vacantDwellingRatio," + dwellings.stream().filter(dd -> dd.getUsage().equals(DwellingUsage.VACANT)).count()/dwellings.size());

        nonSpatialSDGIndicator.println("year,incomeCategory,avgDelayTime");
        Map<IncomeCategory, List<AnalyzedPerson>> analyzedPPByIncome = matsimPerson.values().stream().collect(Collectors.groupingBy(pp -> HouseholdUtil.getIncomeCategoryForIncome(HouseholdUtil.getHhIncome(matsimIdSiloPerson.get(pp.getId()).getHousehold()))));
        analyzedPPByIncome.entrySet().forEach(entry -> nonSpatialSDGIndicator.println(year + "," + entry.getKey() + "," + entry.getValue().stream().mapToDouble(pp -> pp.getCongestedTime()-pp.getFreeFlowTime()).average().getAsDouble()));
        nonSpatialSDGIndicator.println(year + ",all," + matsimPerson.values().stream().mapToDouble(pp -> pp.getCongestedTime()- pp.getFreeFlowTime()).average().getAsDouble());

        nonSpatialSDGIndicator.close();
    }


}